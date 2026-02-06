package com.tianji.promotion.service;

import com.tianji.promotion.pojo.entity.Coupon;
import com.tianji.promotion.pojo.entity.ExchangeCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 兑换码 服务类
 * </p>
 *
 * @author 天哥
 */
public interface IExchangeCodeService extends IService<ExchangeCode> {

    void asyncGenerateCode(Coupon coupon);
}
