package com.tianji.promotion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tianji.promotion.service.IPromotionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 促销活动，形式多种多样，例如：优惠券 控制器
 * </p>
 *
 * @author 天哥
 */
@Api(tags = "Promotion管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {

    private final IPromotionService promotionService;


}
