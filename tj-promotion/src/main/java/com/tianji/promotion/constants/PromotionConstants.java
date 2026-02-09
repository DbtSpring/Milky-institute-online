package com.tianji.promotion.constants;

public class PromotionConstants {
    //（Redis key1：唯一目的就是获取全局唯一的自增序列号(String)）
    public static final String COUPON_CODE_SERIAL_KEY = "coupon:code:serial";

    //（Redis key2：目的是为了由score范围查member，由已解析出的兑换码得SerialNum查couponId）
    // 写入Redis缓存，    member：couponId，      score（权重）：兑换码的最大序列号
    //  ZSet 存的是 「member-score」键（field）值（value）对 ，score 为数字（权重），会按 score从小到大自动排序；
    // ZSet其实是加了Score得Set，算是Map
    public static final String COUPON_RANGE_KEY = "coupon:range";

    //Redis的bitMap（用来记录兑换码是否已兑换 0/1）
    public static final String COUPON_CODE_MAP_KEY = "coupon:code:map";


    //Redis key1（配合校验完后发mq）:用于领券时候校验优惠券是否在有效期内、库存、每人限领数量
    public static final String COUPON_CACHE_KEY_PREFIX = "prs:coupon:";
    //Redis key2（配合校验完后发mq）：用于领券时候校验每人限领数量
    public static final String USER_COUPON_CACHE_KEY_PREFIX = "prs:user:coupon:";

}
