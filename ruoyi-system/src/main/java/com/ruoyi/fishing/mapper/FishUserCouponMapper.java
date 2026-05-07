package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishUserCoupon;

public interface FishUserCouponMapper
{
    public FishUserCoupon selectFishUserCouponByCouponId(Long couponId);
    public List<FishUserCoupon> selectFishUserCouponList(FishUserCoupon coupon);
    public List<FishUserCoupon> selectMyCoupons(@Param("userId") Long userId);
    public List<FishUserCoupon> selectAvailableCoupons(@Param("userId") Long userId);
    public int insertFishUserCoupon(FishUserCoupon coupon);
    public int updateFishUserCoupon(FishUserCoupon coupon);
    public int useCoupon(@Param("couponId") Long couponId, @Param("orderId") Long orderId);
}
