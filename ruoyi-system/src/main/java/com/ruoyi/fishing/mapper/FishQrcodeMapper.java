package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishQrcode;

public interface FishQrcodeMapper
{
    public FishQrcode selectFishQrcodeByQrId(Long qrId);
    public FishQrcode selectByVenueAndType(@Param("venueId") Long venueId, @Param("qrType") String qrType);
    public List<FishQrcode> selectFishQrcodeList(FishQrcode qr);
    public int insertFishQrcode(FishQrcode qr);
    public int updateFishQrcode(FishQrcode qr);
    public int deleteFishQrcodeByQrId(Long qrId);
}
