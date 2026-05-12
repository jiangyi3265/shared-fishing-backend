package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishReservation;
import com.ruoyi.fishing.service.IFishSpotService;

@RestController
@RequestMapping("/fishing/reservation")
public class FishReservationController extends BaseController
{
    @Autowired
    private IFishSpotService spotService;

    @PreAuthorize("@ss.hasPermi('fishing:reservation:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishReservation q) { startPage(); return getDataTable(spotService.selectReservationList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:reservation:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) { return success(spotService.selectReservationById(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:reservation:confirm')")
    @Log(title = "预订确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{id}")
    public AjaxResult confirm(@PathVariable Long id) { return toAjax(spotService.confirmReservation(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:reservation:cancel')")
    @Log(title = "预订取消", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{id}")
    public AjaxResult cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return toAjax(spotService.cancelReservation(id, null, reason));
    }
}
