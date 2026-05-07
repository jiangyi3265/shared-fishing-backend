package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishCouponTemplate;
import com.ruoyi.fishing.domain.FishUserCoupon;
import com.ruoyi.fishing.mapper.FishCouponTemplateMapper;
import com.ruoyi.fishing.mapper.FishUserCouponMapper;
import com.ruoyi.fishing.service.IFishCouponService;

@Service
public class FishCouponServiceImpl implements IFishCouponService
{
    @Autowired
    private FishCouponTemplateMapper templateMapper;

    @Autowired
    private FishUserCouponMapper userCouponMapper;

    @Override
    public FishCouponTemplate selectFishCouponTemplateByTemplateId(Long templateId) {
        return templateMapper.selectFishCouponTemplateByTemplateId(templateId);
    }

    @Override
    public List<FishCouponTemplate> selectFishCouponTemplateList(FishCouponTemplate template) {
        return templateMapper.selectFishCouponTemplateList(template);
    }

    @Override
    public int insertFishCouponTemplate(FishCouponTemplate template) {
        template.setCreateBy(safeUser());
        template.setCreateTime(DateUtils.getNowDate());
        return templateMapper.insertFishCouponTemplate(template);
    }

    @Override
    public int updateFishCouponTemplate(FishCouponTemplate template) {
        template.setUpdateBy(safeUser());
        template.setUpdateTime(DateUtils.getNowDate());
        return templateMapper.updateFishCouponTemplate(template);
    }

    @Override
    public int deleteFishCouponTemplateByTemplateIds(Long[] templateIds) {
        return templateMapper.deleteFishCouponTemplateByTemplateIds(templateIds);
    }

    @Override
    public List<FishUserCoupon> selectUserCouponList(FishUserCoupon query) {
        return userCouponMapper.selectFishUserCouponList(query);
    }

    @Override
    public List<FishUserCoupon> selectMyCoupons(Long userId) {
        return userCouponMapper.selectMyCoupons(userId);
    }

    @Override
    public List<FishUserCoupon> selectAvailableCoupons(Long userId) {
        return userCouponMapper.selectAvailableCoupons(userId);
    }

    @Override
    public FishUserCoupon grantCoupon(Long userId, Long templateId, String source) {
        FishCouponTemplate t = templateMapper.selectFishCouponTemplateByTemplateId(templateId);
        if (t == null) throw new ServiceException("优惠券模板不存在");
        if ("1".equals(t.getStatus())) throw new ServiceException("该优惠券已停用");
        if (t.getTotalStock() != null && t.getTotalStock() > 0
                && t.getIssuedCount() != null && t.getIssuedCount() >= t.getTotalStock()) {
            throw new ServiceException("优惠券已领完");
        }
        FishUserCoupon c = new FishUserCoupon();
        c.setTemplateId(templateId);
        c.setUserId(userId);
        c.setTitle(t.getTitle());
        c.setCouponType(t.getCouponType());
        c.setCouponValue(t.getCouponValue());
        c.setMinAmountCents(t.getMinAmountCents() == null ? 0 : t.getMinAmountCents());
        int validDays = t.getValidDays() == null || t.getValidDays() <= 0 ? 30 : t.getValidDays();
        c.setExpireTime(new Date(System.currentTimeMillis() + (long) validDays * 86400000L));
        c.setUsed(0);
        c.setSource(source == null ? "" : source);
        userCouponMapper.insertFishUserCoupon(c);

        t.setIssuedCount((t.getIssuedCount() == null ? 0 : t.getIssuedCount()) + 1);
        templateMapper.updateFishCouponTemplate(t);
        return c;
    }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
