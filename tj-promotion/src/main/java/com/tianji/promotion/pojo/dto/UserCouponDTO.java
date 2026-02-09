package com.tianji.promotion.pojo.dto;

import lombok.Data;

/**
 * 用于用户领券时，用redis校验资格过后，传给mq发放的DTO
 */
@Data
public class UserCouponDTO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 优惠券id
     */
    private Long couponId;
}
