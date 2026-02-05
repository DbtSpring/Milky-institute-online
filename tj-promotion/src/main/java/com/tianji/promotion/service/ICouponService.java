package com.tianji.promotion.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.pojo.dto.CouponFormDTO;
import com.tianji.promotion.pojo.dto.CouponIssueFormDTO;
import com.tianji.promotion.pojo.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.promotion.pojo.query.CouponQuery;
import com.tianji.promotion.pojo.vo.CouponPageVO;

import javax.validation.Valid;

/**
 * <p>
 * 优惠券的规则信息 服务类
 * </p>
 *
 * @author 天哥
 */
public interface ICouponService extends IService<Coupon> {

    void saveCoupon(CouponFormDTO dto);

    PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query);

    void beginIssue(@Valid CouponIssueFormDTO dto);
}
