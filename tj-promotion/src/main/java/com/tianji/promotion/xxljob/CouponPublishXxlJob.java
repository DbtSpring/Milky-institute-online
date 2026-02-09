package com.tianji.promotion.xxljob;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.tianji.promotion.mapper.CouponMapper;
import com.tianji.promotion.pojo.entity.Coupon;

import java.time.LocalDateTime;

/**
 * XXL-JOB优惠券定时发放任务：更新未开始优惠券为进行中
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponPublishXxlJob {

    // 注入优惠券Mapper（用Service层也可以，保持项目统一即可）
    private final CouponMapper couponMapper;

    // 任务名称：xxl-job控制台配置时要和这个value一致
    @XxlJob("couponPublish")
    public void couponPublish() throws Exception {
        // ===================== 1. XXL-JOB分片参数获取 =====================
        // 分片索引：当前执行器的分片序号（从0开始，如0、1、2）
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数：总共有多少个执行器/分片（如3个执行器则为3）
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info("优惠券定时发放任务开始执行，分片索引：{}，分片总数：{}，当前时间：{}",
                shardIndex, shardTotal, LocalDateTime.now());

        // 若分片总数为0，直接结束（无分片配置时）
        if (shardTotal == 0) {
            log.warn("优惠券定时发放任务未配置分片，直接结束");
            XxlJobHelper.handleSuccess("未配置分片，任务结束");
            return;
        }

        // ===================== 2. 构建更新条件 + 分片过滤 =====================
        LambdaUpdateWrapper<Coupon> updateWrapper = new LambdaUpdateWrapper<>();
        // 核心条件1：状态=未开始（按你实际枚举值改，这里假设0=未开始）
        updateWrapper.eq(Coupon::getStatus, 0)
                // 核心条件2：发放时间 ≤ 当前时间（publishTime为你实体的发放开始时间字段，改字段名）
                .le(Coupon::getTermBeginTime, LocalDateTime.now())
                // 分片核心：按主键ID取模分片，避免重复处理
                // 原理：id % 分片总数 = 分片索引 → 每个执行器只处理自己分片的数
                .apply("id % {0} = {1}", shardTotal, shardIndex)
                // 批量更新：设置状态为进行中（假设1=进行中，按你实际枚举值改）
                .set(Coupon::getStatus, 1);

        // ===================== 3. 执行批量更新 + 结果返回 =====================
        // MP的update方法：更新符合条件的所有数据，返回更新成功的条数
        int updateCount = couponMapper.update(null, updateWrapper);
        log.info("优惠券定时发放任务执行完成，分片索引：{}，分片总数：{}，成功更新状态条数：{}",
                shardIndex, shardTotal, updateCount);

        // 告诉XXL-JOB控制台任务执行成功，返回日志
        XxlJobHelper.handleSuccess(String.format("分片%s执行完成，成功更新%d张优惠券为进行中", shardIndex, updateCount));
    }
}