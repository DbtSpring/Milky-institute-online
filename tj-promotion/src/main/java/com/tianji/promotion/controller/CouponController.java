package com.tianji.promotion.controller;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.pojo.dto.CouponFormDTO;
import com.tianji.promotion.pojo.dto.CouponIssueFormDTO;
import com.tianji.promotion.pojo.query.CouponQuery;
import com.tianji.promotion.pojo.vo.CouponPageVO;
import com.tianji.promotion.pojo.vo.CouponVO;
import com.tianji.promotion.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 控制器
 * </p>
 *
 * @author 天哥
 */
@Api(tags = "Coupon管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final ICouponService couponService;

    @ApiOperation("新增优惠券接口")
    @PostMapping
    public void saveCoupon(@RequestBody @Valid CouponFormDTO dto){
        couponService.saveCoupon(dto);
    }

    @ApiOperation("分页查询优惠券接口")
    @GetMapping("/page")
    public PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query){
        return couponService.queryCouponByPage(query);
    }

    @ApiOperation("发放优惠券接口")
    @PutMapping("/{id}/issue")
    public void beginIssue(@RequestBody @Valid CouponIssueFormDTO dto) {
        couponService.beginIssue(dto);
    }

    @ApiOperation("修改优惠券接口")
    @PutMapping
    public void updateCoupon(@RequestBody @Valid CouponFormDTO dto){
        couponService.updateCoupon(dto);
    }

    @ApiOperation("删除优惠券接口")
    @DeleteMapping("/{id}")
    public void delCouponByCouponId(@PathVariable Long id){
        couponService.delCouponById(id);
    }

    @ApiOperation("查询发放中的优惠券列表")
    @GetMapping("/list")
    public List<CouponVO> queryIssuingCoupons(){
        return couponService.queryIssuingCoupons();
    }

}
