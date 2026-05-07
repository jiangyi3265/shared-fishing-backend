package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishCouponTemplate;
import com.ruoyi.fishing.domain.FishUserCoupon;

public interface IFishCouponService
{
    public FishCouponTemplate selectFishCouponTemplateByTemplateId(Long templateId);
    public List<FishCouponTemplate> selectFishCouponTemplateList(FishCouponTemplate template);
    public int insertFishCouponTemplate(FishCouponTemplate template);
    public int updateFishCouponTemplate(FishCouponTemplate template);
    public int deleteFishCouponTemplateByTemplateIds(Long[] templateIds);

    public List<FishUserCoupon> selectUserCouponList(FishUserCoupon query);
    public List<FishUserCoupon> selectMyCoupons(Long userId);
    public List<FishUserCoupon> selectAvailableCoupons(Long userId);
    public FishUserCoupon grantCoupon(Long userId, Long templateId, String source);
}
