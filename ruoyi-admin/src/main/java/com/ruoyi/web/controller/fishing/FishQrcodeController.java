package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishQrcode;
import com.ruoyi.fishing.service.IFishQrcodeService;
import com.ruoyi.fishing.service.IWxMiniappCodeService;

@RestController
@RequestMapping("/fishing/qrcode")
public class FishQrcodeController extends BaseController
{
    @Autowired
    private IFishQrcodeService qrcodeService;
    @Autowired
    private IWxMiniappCodeService wxMiniappCodeService;

    @PreAuthorize("@ss.hasPermi('fishing:qrcode:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishQrcode qr)
    {
        startPage();
        return getDataTable(qrcodeService.selectFishQrcodeList(qr));
    }

    @PreAuthorize("@ss.hasPermi('fishing:qrcode:query')")
    @GetMapping("/{qrId}")
    public AjaxResult getInfo(@PathVariable Long qrId)
    {
        return success(qrcodeService.selectFishQrcodeByQrId(qrId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:qrcode:add')")
    @Log(title = "二维码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishQrcode qr) { return toAjax(qrcodeService.insertFishQrcode(qr)); }

    @PreAuthorize("@ss.hasPermi('fishing:qrcode:query')")
    @GetMapping(value = "/wxacode/{qrId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> wxacode(@PathVariable Long qrId,
                                          @RequestParam(required = false, defaultValue = "release") String envVersion,
                                          @RequestParam(required = false, defaultValue = "430") Integer width)
    {
        FishQrcode qr = qrcodeService.selectFishQrcodeByQrId(qrId);
        if (qr == null) throw new ServiceException("二维码不存在");
        if ("1".equals(qr.getStatus())) throw new ServiceException("二维码已停用");
        byte[] image = wxMiniappCodeService.createUnlimitedCode("qrId=" + qrId, "pages/index/index", width, envVersion);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-Disposition", "inline; filename=wxacode-" + qrId + ".jpg")
                .body(image);
    }

    @PreAuthorize("@ss.hasPermi('fishing:qrcode:edit')")
    @Log(title = "二维码", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishQrcode qr) { return toAjax(qrcodeService.updateFishQrcode(qr)); }

    @PreAuthorize("@ss.hasPermi('fishing:qrcode:remove')")
    @Log(title = "二维码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{qrId}")
    public AjaxResult remove(@PathVariable Long qrId) { return toAjax(qrcodeService.deleteFishQrcodeByQrId(qrId)); }
}
