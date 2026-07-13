package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishQrcode;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishQrcodeMapper;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.service.IFishQrcodeService;

@Service
public class FishQrcodeServiceImpl implements IFishQrcodeService
{
    @Autowired
    private FishQrcodeMapper mapper;

    @Autowired
    private FishVenueMapper venueMapper;

    @Override
    public FishQrcode selectFishQrcodeByQrId(Long qrId) { return mapper.selectFishQrcodeByQrId(qrId); }

    @Override
    public List<FishQrcode> selectFishQrcodeList(FishQrcode qr) { return mapper.selectFishQrcodeList(qr); }

    @Override
    public int insertFishQrcode(FishQrcode qr)
    {
        validate(qr, true);
        qr.setCreateBy(safeUser());
        qr.setCreateTime(DateUtils.getNowDate());
        if (qr.getStatus() == null) qr.setStatus("0");
        return mapper.insertFishQrcode(qr);
    }

    @Override
    public int updateFishQrcode(FishQrcode qr)
    {
        validate(qr, false);
        qr.setUpdateBy(safeUser());
        qr.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateFishQrcode(qr);
    }

    @Override
    public int deleteFishQrcodeByQrId(Long qrId) { return mapper.deleteFishQrcodeByQrId(qrId); }

    private String safeUser() {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return "system"; }
    }

    private void validate(FishQrcode qr, boolean creating)
    {
        if (qr == null) throw new ServiceException("二维码信息不能为空");
        if (!creating && qr.getQrId() == null) throw new ServiceException("二维码ID不能为空");
        if (creating && qr.getVenueId() == null) throw new ServiceException("请选择所属钓场");
        if (qr.getVenueId() != null)
        {
            FishVenue venue = venueMapper.selectFishVenueByVenueId(qr.getVenueId());
            if (venue == null) throw new ServiceException("所属钓场不存在");
            if ("1".equals(venue.getStatus())) throw new ServiceException("所属钓场已停用");
        }
        if (creating && (qr.getQrType() == null || qr.getQrType().trim().isEmpty()))
        {
            qr.setQrType(FishQrcode.TYPE_COMMON);
        }
        if (qr.getQrType() != null
                && !FishQrcode.TYPE_START.equals(qr.getQrType())
                && !FishQrcode.TYPE_END.equals(qr.getQrType())
                && !FishQrcode.TYPE_COMMON.equals(qr.getQrType()))
        {
            throw new ServiceException("二维码类型不支持");
        }
    }
}
