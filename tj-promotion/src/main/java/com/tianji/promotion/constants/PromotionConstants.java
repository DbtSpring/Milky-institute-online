package com.tianji.promotion.constants;

public class PromotionConstants {
    //（Redis key1：唯一目的就是获取全局唯一的自增序列号(String)）
    public static final String COUPON_CODE_SERIAL_KEY = "coupon:code_serial_key";

    //（Redis key2：目的是为了由score范围查member，由已解析出的兑换码得SerialNum查couponId）
    // 写入Redis缓存，    member：couponId，      score（权重）：兑换码的最大序列号
    //  ZSet 存的是 「member-score」键（field）值（value）对 ，score 为数字（权重），会按 score从小到大自动排序；
    // ZSet其实是加了Score得Set，算是Map
    public static final String COUPON_RANGE_KEY = "coupon:range_key";
}
