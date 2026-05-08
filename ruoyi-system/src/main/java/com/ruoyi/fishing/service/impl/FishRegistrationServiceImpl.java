package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishAd;
import com.ruoyi.fishing.domain.FishRegistration;
import com.ruoyi.fishing.mapper.FishAdMapper;
import com.ruoyi.fishing.mapper.FishRegistrationMapper;
import com.ruoyi.fishing.service.IFishRegistrationService;

@Service
public class FishRegistrationServiceImpl implements IFishRegistrationService
{
    @Autowired
    private FishRegistrationMapper regMapper;

    @Autowired
    private FishAdMapper adMapper;

    @Override
    public FishRegistration selectFishRegistrationByRegId(Long regId) { return regMapper.selectFishRegistrationByRegId(regId); }

    @Override
    public List<FishRegistration> selectFishRegistrationList(FishRegistration reg) { return regMapper.selectFishRegistrationList(reg); }

    @Override
    public List<FishRegistration> selectByUserId(Long userId) { return regMapper.selectByUserId(userId); }

    @Override
    @Transactional
    public FishRegistration submit(Long adId, Long userId, String name, String phone, String remark)
    {
        FishAd ad = adMapper.selectFishAdByAdId(adId);
        if (ad == null || !"activity".equals(ad.getAdType())) throw new ServiceException("活动不存在");
        if ("1".equals(ad.getStatus())) throw new ServiceException("活动已停用");

        FishRegistration existing = regMapper.selectByAdAndUser(adId, userId);
        if (existing != null) return existing;

        if (ad.getActivitySlots() != null && ad.getActivitySlots() > 0)
        {
            FishRegistration query = new FishRegistration();
            query.setAdId(adId);
            int count = regMapper.selectFishRegistrationList(query).size();
            if (count >= ad.getActivitySlots()) throw new ServiceException("报名人数已满");
        }

        FishRegistration r = new FishRegistration();
        r.setAdId(adId);
        r.setUserId(userId);
        r.setName(name);
        r.setPhone(phone);
        r.setRemark2(remark == null ? "" : remark);
        r.setFeeCents(ad.getActivityFeeCents() == null ? 0 : ad.getActivityFeeCents());
        r.setPaid(0);
        r.setStatus(0);
        regMapper.insertFishRegistration(r);
        return r;
    }

    @Override
    public FishRegistration pay(Long regId)
    {
        FishRegistration r = regMapper.selectFishRegistrationByRegId(regId);
        if (r == null) throw new ServiceException("报名不存在");
        r.setPaid(1);
        r.setPaidTime(new Date());
        regMapper.updateFishRegistration(r);
        return r;
    }

    @Override
    public int updateFishRegistration(FishRegistration reg) { return regMapper.updateFishRegistration(reg); }

    @Override
    public int deleteFishRegistrationByRegIds(Long[] regIds) { return regMapper.deleteFishRegistrationByRegIds(regIds); }
}
