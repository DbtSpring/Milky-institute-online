package com.tianji.promotion.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.promotion.mapper.ExchangeCodeMapper;
import com.tianji.promotion.pojo.entity.Coupon;
import com.tianji.promotion.pojo.entity.ExchangeCode;
import com.tianji.promotion.service.IExchangeCodeService;
import com.tianji.promotion.utils.CodeUtil;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tianji.promotion.constants.PromotionConstants.*;

/**
 * <p>
 * 兑换码 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode> implements IExchangeCodeService {

    private final StringRedisTemplate redisTemplate;
    //BoundValueOperations：Redis的String操作工具类，可以一次绑定key终生使用
    //原生麻烦写法：redisTemplate.opsForValue().increment(COUPON_CODE_SERIAL_KEY, totalNum);
    //新写法：serialOps.increment(totalNum)
    private final BoundValueOperations<String, String> serialOps;

    public ExchangeCodeServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.serialOps = redisTemplate.boundValueOps(COUPON_CODE_SERIAL_KEY);
    }

    @Override
    //用自定义线程池异步完成
    @Async("generateExchangeCodeExecutor")
    //这个方法和Redis关系很大
    public void asyncGenerateCode(Coupon coupon) {
        // 此优惠券发放的兑换码数量
        Integer totalNum = coupon.getTotalNum();
        // 1.获取Redis自增序列号（Redis key1：唯一目的就是获取全局唯一的自增序列号）
        //result是最大“32位原始序列号”
        Long result = serialOps.increment(totalNum);
        if (result == null) {
            return;
        }
        //maxSerialNum是最大“32位原始序列号”
        int maxSerialNum = result.intValue();
        List<ExchangeCode> list = new ArrayList<>(totalNum);
        //serialNum是“32位原始序列号”
        for (int serialNum = maxSerialNum - totalNum + 1; serialNum <= maxSerialNum; serialNum++) {
            // 2.生成50位兑换码
            String code = CodeUtil.generateCode(serialNum, coupon.getId());
            ExchangeCode e = new ExchangeCode();
            e.setCode(code);
            e.setId(serialNum);
            e.setExchangeTargetId(coupon.getId());
            e.setExpiredTime(coupon.getIssueEndTime());
            list.add(e);
        }
        // 3.保存数据库
        saveBatch(list);

        // 4.（Redis key2：目的是为了由score范围查member，由已解析出的兑换码得SerialNum查couponId）
        // 写入Redis缓存，    member：couponId，      score（权重）：兑换码的最大序列号
        //ZSet 存的是 「member-score」键（field）值（value）对 ，score 为数字（权重），会按 score从小到大自动排序；
        //ZSet其实是加了Score得Set，算是Map
        redisTemplate.opsForZSet().add(COUPON_RANGE_KEY, coupon.getId().toString(), maxSerialNum);
    }
}