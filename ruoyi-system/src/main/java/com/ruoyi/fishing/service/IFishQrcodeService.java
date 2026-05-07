package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishQrcode;

public interface IFishQrcodeService
{
    public FishQrcode selectFishQrcodeByQrId(Long qrId);
    public List<FishQrcode> selectFishQrcodeList(FishQrcode qr);
    public int insertFishQrcode(FishQrcode qr);
    public int updateFishQrcode(FishQrcode qr);
    public int deleteFishQrcodeByQrId(Long qrId);
}
