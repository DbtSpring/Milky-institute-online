package com.tianji.promotion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tianji.promotion.service.IExchangeCodeService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 兑换码 控制器
 * </p>
 *
 * @author 天哥
 */
@Api(tags = "ExchangeCode管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/exchangeCode")
public class ExchangeCodeController {

    private final IExchangeCodeService exchangeCodeService;


}
