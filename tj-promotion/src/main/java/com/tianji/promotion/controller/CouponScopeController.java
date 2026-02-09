package com.tianji.promotion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tianji.promotion.service.ICouponScopeService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 优惠券作用范围(优惠券-课程） 控制器
 * </p>
 *
 * @author 天哥
 */
@Api(tags = "CouponScope管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/couponScope")
public class CouponScopeController {

    private final ICouponScopeService couponScopeService;


}
