package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishSpot;
import com.ruoyi.fishing.service.IFishSpotService;

@RestController
@RequestMapping("/fishing/spot")
public class FishSpotController extends BaseController
{
    @Autowired
    private IFishSpotService spotService;

    @PreAuthorize("@ss.hasPermi('fishing:spot:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishSpot q) { startPage(); return getDataTable(spotService.selectList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:spot:query')")
    @GetMapping("/{spotId}")
    public AjaxResult getInfo(@PathVariable Long spotId) { return success(spotService.selectById(spotId)); }

    @PreAuthorize("@ss.hasPermi('fishing:spot:add')")
    @Log(title = "钓位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishSpot s) { return toAjax(spotService.insertSpot(s)); }

    @PreAuthorize("@ss.hasPermi('fishing:spot:edit')")
    @Log(title = "钓位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishSpot s) { return toAjax(spotService.updateSpot(s)); }

    @PreAuthorize("@ss.hasPermi('fishing:spot:remove')")
    @Log(title = "钓位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{spotIds}")
    public AjaxResult remove(@PathVariable Long[] spotIds) { return toAjax(spotService.deleteByIds(spotIds)); }
}
