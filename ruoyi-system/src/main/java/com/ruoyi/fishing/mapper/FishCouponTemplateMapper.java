package com.ruoyi.fishing.mapper;

import java.util.List;
import com.ruoyi.fishing.domain.FishCouponTemplate;

public interface FishCouponTemplateMapper
{
    public FishCouponTemplate selectFishCouponTemplateByTemplateId(Long templateId);
    public List<FishCouponTemplate> selectFishCouponTemplateList(FishCouponTemplate template);
    public int insertFishCouponTemplate(FishCouponTemplate template);
    public int updateFishCouponTemplate(FishCouponTemplate template);
    public int deleteFishCouponTemplateByTemplateId(Long templateId);
    public int deleteFishCouponTemplateByTemplateIds(Long[] templateIds);
}
