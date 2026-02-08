package com.tianji.promotion.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.promotion.pojo.dto.UserCouponDTO;
import com.tianji.promotion.pojo.entity.Coupon;
import com.tianji.promotion.pojo.entity.UserCoupon;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务类
 * </p>
 *
 * @author 天哥
 */
public interface IUserCouponService extends IService<UserCoupon> {


    void receiveCoupon(Long couponId);

    void exchangeCoupon(String code);

    void checkAndCreateUserCoupon(Coupon coupon, Long userId, Long serialNum);

    void checkAndCreateUserCoupon(UserCouponDTO uc);
}
