package com.tianji.promotion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianji.promotion.pojo.entity.Coupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 优惠券的规则信息 Mapper 接口
 * </p>
 *
 * @author 天哥
 */
public interface CouponMapper extends BaseMapper<Coupon> {

    @Update("UPDATE coupon SET issue_num = issue_num + 1 WHERE id = #{couponId} " +
            "and issue_num < total_num") //乐观锁，update时检查库存
    int incrIssueNum(@Param("couponId") Long couponId);
}
