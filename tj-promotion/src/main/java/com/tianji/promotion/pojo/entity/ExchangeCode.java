package com.tianji.promotion.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tianji.promotion.enums.ExchangeCodeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 兑换码
 * </p>
 *
 * @author 天哥
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("exchange_code")
@ApiModel(value="ExchangeCode对象", description="兑换码")
public class ExchangeCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "兑换码id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty(value = "兑换码")
    private String code;

    @ApiModelProperty(value = "兑换码状态， 1：待兑换，2：已兑换，3：兑换活动已结束")
    private ExchangeCodeStatus status;

    @ApiModelProperty(value = "兑换人")
    private Long userId;

    @ApiModelProperty(value = "兑换类型，1：优惠券，以后再添加其它类型")
    private Integer type;

    @ApiModelProperty(value = "兑换码目标id，例如兑换优惠券，该id则是优惠券的配置id")
    private Long exchangeTargetId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "兑换码过期时间")
    private LocalDateTime expiredTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
