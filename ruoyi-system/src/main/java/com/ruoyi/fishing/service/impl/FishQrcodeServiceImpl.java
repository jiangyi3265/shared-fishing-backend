package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishQrcode;
import com.ruoyi.fishing.mapper.FishQrcodeMapper;
import com.ruoyi.fishing.service.IFishQrcodeService;

@Service
public class FishQrcodeServiceImpl implements IFishQrcodeService
{
    @Autowired
    private FishQrcodeMapper mapper;

    @Override
    public FishQrcode selectFishQrcodeByQrId(Long qrId) { return mapper.selectFishQrcodeByQrId(qrId); }

    @Override
    public List<FishQrcode> selectFishQrcodeList(FishQrcode qr) { return mapper.selectFishQrcodeList(qr); }

    @Override
    public int insertFishQrcode(FishQrcode qr)
    {
        qr.setCreateBy(safeUser());
        qr.setCreateTime(DateUtils.getNowDate());
        if (qr.getStatus() == null) qr.setStatus("0");
        return mapper.insertFishQrcode(qr);
    }

    @Override
    public int updateFishQrcode(FishQrcode qr)
    {
        qr.setUpdateBy(safeUser());
        qr.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateFishQrcode(qr);
    }

    @Override
    public int deleteFishQrcodeByQrId(Long qrId) { return mapper.deleteFishQrcodeByQrId(qrId); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }
}
