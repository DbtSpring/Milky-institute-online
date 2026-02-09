package com.tianji.promotion.service.impl;

import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.common.autoconfigure.mq.RabbitMqHelper;
import com.tianji.common.autoconfigure.redisson.annotations.Lock;
import com.tianji.common.constants.MqConstants;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.promotion.constants.PromotionConstants;
import com.tianji.promotion.enums.ExchangeCodeStatus;
import com.tianji.promotion.mapper.CouponMapper;
import com.tianji.promotion.mapper.UserCouponMapper;
import com.tianji.promotion.pojo.dto.UserCouponDTO;
import com.tianji.promotion.pojo.entity.Coupon;
import com.tianji.promotion.pojo.entity.ExchangeCode;
import com.tianji.promotion.pojo.entity.UserCoupon;
import com.tianji.promotion.service.IExchangeCodeService;
import com.tianji.promotion.service.IUserCouponService;
import com.tianji.promotion.utils.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务实现类
 * </p>
 *
 * @author 天哥
 */
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {

    private final CouponMapper couponMapper;
    private final IExchangeCodeService codeService;
    private final StringRedisTemplate redisTemplate;
    private final RabbitMqHelper mqHelper;

    /**
     * 用户在用户端领取优惠券（查mysql，然后领取）
     * 改造后：查redis资格校验，然后异步通知mq领取
     * @param couponId
     */
    @Override
    @Transactional
    @Lock(name = "lock:coupon:#{couponId}") //比Transactional的aop优先执行
    public void receiveCoupon(Long couponId) {
        // 1.查询优惠券（用Redis一种key）
//        Coupon coupon = couponMapper.selectById(couponId);
        Coupon coupon = queryCouponByCache(couponId);
        if (coupon == null) {
            throw new BadRequestException("优惠券不存在");
        }
        // 2.校验发放时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueBeginTime()) || now.isAfter(coupon.getIssueEndTime())) {
            throw new BadRequestException("优惠券发放已经结束或尚未开始");
        }
        // 3.校验库存
        if (coupon.getIssueNum() >= coupon.getTotalNum()) {
            throw new BadRequestException("优惠券库存不足");
        }
        Long userId = UserContext.getUser();
        // 4.校验每人限领数量（用Redis里另一种key）
        // 4.1.统计当前用户对当前优惠券的已经领取的数量
//        Integer count = lambdaQuery()
//                .eq(UserCoupon::getUserId, userId)
//                .eq(UserCoupon::getCouponId, couponId)
//                .count();
        String key = PromotionConstants.USER_COUPON_CACHE_KEY_PREFIX + couponId;
        Long count = redisTemplate.opsForHash().increment(key, userId.toString(), 1);

        // 4.2.校验限领数量
        if (count >= coupon.getUserLimit()) {
            throw new BadRequestException("超出领取数量");
        }
//        // 5.更新优惠券的已经发放的数量 + 1
//        couponMapper.incrIssueNum(coupon.getId());
//        // 6.新增一个用户券
//        saveUserCoupon(coupon, userId);

        // 5.扣减优惠券库存（redis操作一）
        redisTemplate.opsForHash().increment(
                PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId, "totalNum", -1);

        // 6.发送MQ消息（redis这边都校验完了，扣完了，排队去吧）
        UserCouponDTO uc = new UserCouponDTO();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        mqHelper.send(MqConstants.Exchange.PROMOTION_EXCHANGE, MqConstants.Key.COUPON_RECEIVE, uc);
    }

    private Coupon queryCouponByCache(Long couponId) {
        // 1.准备KEY
        String key = PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId;
        // 2.查询
        Map<Object, Object> objMap = redisTemplate.opsForHash().entries(key);
        if (objMap.isEmpty()) {
            return null;
        }
        // 3.数据反序列化
        return BeanUtils.mapToBean(objMap, Coupon.class, false, CopyOptions.create());
    }

    @Override
//    @Transactional
    public void exchangeCoupon(String code) {
        // 1.校验并解析兑换码
        long serialNum = CodeUtil.parseCode(code);
        // 2.校验是否已经兑换 SETBIT KEY 4 1 ，这里直接执行setbit，通过返回值来判断是否兑换过
        boolean exchanged = codeService.updateExchangeMark(serialNum, true);
        if (exchanged) {
            throw new BizIllegalException("兑换码已经被兑换过了");
        }
        try {
            // 3.查询兑换码对应的优惠券id
            ExchangeCode exchangeCode = codeService.getById(serialNum);
            if (exchangeCode == null) {
                throw new BizIllegalException("兑换码不存在！");
            }
            // 4.是否过期
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(exchangeCode.getExpiredTime())) {
                throw new BizIllegalException("兑换码已经过期");
            }
            // 5.校验并生成用户券
            // 5.1.查询优惠券
            Coupon coupon = couponMapper.selectById(exchangeCode.getExchangeTargetId());
            // 5.2.查询用户
            Long userId = UserContext.getUser();
//            悲观锁，解决每个用户超发问题
//            且锁要在事务外面（事务边界问题）

//            方案一：用jvm锁
//            synchronized(userId.toString().intern()) {
//                // 5.3.校验并生成用户券，更新兑换码状态
//                //AopContext.currentProxy()：获取当前方法所在的代理对象。用于解决事务失效问题
//                IUserCouponService userCouponService = (IUserCouponService)AopContext.currentProxy();
//                userCouponService.checkAndCreateUserCoupon(coupon, userId, serialNum);
//            }

//            //方案二：悲观锁不要用jvm锁了，改用分布式锁（redis的ET key value NX来实现的锁获取和释放，不稳健）
//            String key = "lock:coupon:uid:" + userId;
//            RedisLock redisLock = new RedisLock(key, redisTemplate);
//            Boolean success = redisLock.tryLock(20, TimeUnit.SECONDS);
//            if(!success){
//                throw new BizIllegalException("请求太频繁");
//            }
//            try{
//                //5.3.校验并生成用户券，更新兑换码状态
//                //AopContext.currentProxy()：获取当前方法所在的代理对象。用于解决事务失效问题
//                IUserCouponService userCouponService = (IUserCouponService) AopContext.currentProxy();
//                userCouponService.checkAndCreateUserCoupon(coupon, userId, serialNum);
//            }finally{
//                redisLock.unlock();
//            }

//            方案三：用Redisson的锁，且用自定义抽取aop，在checkAndCreateUserCoupon方法上注解来加锁
              IUserCouponService userCouponService = (IUserCouponService) AopContext.currentProxy();
              userCouponService.checkAndCreateUserCoupon(coupon, userId, serialNum);

        } catch (Exception e) {
            // 重置兑换的标记 0
            codeService.updateExchangeMark(serialNum, false);
            throw e;
        }
    }


    private void saveUserCoupon(Coupon coupon, Long userId) {
        // 1.基本信息
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(coupon.getId());
        // 2.有效期信息
        LocalDateTime termBeginTime = coupon.getTermBeginTime();
        LocalDateTime termEndTime = coupon.getTermEndTime();
        if (termBeginTime == null) {
            termBeginTime = LocalDateTime.now();
            termEndTime = termBeginTime.plusDays(coupon.getTermDays());
        }
        uc.setTermBeginTime(termBeginTime);
        uc.setTermEndTime(termEndTime);
        // 3.保存
        save(uc);
    }


    /**
     * 这个子方法是为了母方法exchangeCoupon准备的
     * @param coupon
     * @param userId
     * @param serialNum
     */
    //事务边界关系必须是：事务外部包裹锁
    //这里要保证aop顺序：锁要先于事务执行
    @Transactional
    //！！！这是import com.tianji.common.autoconfigure.redisson.annotations.Lock中的
    //里面定义了redisson详细的aop、枚举、还有工厂模式/策略模式/spel表达式获取类型
    @Lock(name="lock:coupon:#{T(com.tianji.common.utils.UserContext).getUser()}")
    @Override
    public void checkAndCreateUserCoupon(Coupon coupon, Long userId, Long serialNum) {

        // 1.校验每人限领数量
        // 1.1.统计当前用户对当前优惠券的已经领取的数量
        Integer count = lambdaQuery()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, coupon.getId())
                .count();
        // 1.2.校验限领数量
        if (count != null && count >= coupon.getUserLimit()) {
            throw new BadRequestException("超出领取数量");
        }
        // 2.更新优惠券的已经发放的数量 + 1
        int r = couponMapper.incrIssueNum(coupon.getId());
        //！解决超发问题（用乐观锁CAS）
        if (r == 0) {
            throw new BadRequestException("优惠券库存不足");
        }

        // 3.新增一个用户券
        saveUserCoupon(coupon, userId);
        // 4.更新兑换码状态
        if (serialNum != null) {
            codeService.lambdaUpdate()
                    .set(ExchangeCode::getUserId, userId)
                    .set(ExchangeCode::getStatus, ExchangeCodeStatus.USED)
                    .eq(ExchangeCode::getId, serialNum)
                    .update();
        }

    }

    //
    /**
     * 这个子方法是为了母方法receiveCoupon准备的
     */
    //事务边界关系必须是：事务外部包裹锁
    //这里要保证aop顺序：锁要先于事务执行
    @Transactional
    //！！！这是import com.tianji.common.autoconfigure.redisson.annotations.Lock中的
    //里面定义了redisson详细的aop、枚举、还有工厂模式/策略模式/spel表达式获取类型
    //    改变：由于redis校验+mq通知的改造，这里不用加锁了
//    @Lock(name="lock:coupon:#{T(com.tianji.common.utils.UserContext).getUser()}")
    @Override
    public void checkAndCreateUserCoupon(UserCouponDTO uc) {
        // 1.查询优惠券
        Coupon coupon = couponMapper.selectById(uc.getCouponId());
        if (coupon == null) {
            throw new BizIllegalException("优惠券不存在！");
        }
        // 2.更新优惠券的已经发放的数量 + 1
        int r = couponMapper.incrIssueNum(coupon.getId());
        if (r == 0) {
            throw new BizIllegalException("优惠券库存不足！");
        }
        // 3.新增一个用户券
        saveUserCoupon(coupon, uc.getUserId());
    }

}
