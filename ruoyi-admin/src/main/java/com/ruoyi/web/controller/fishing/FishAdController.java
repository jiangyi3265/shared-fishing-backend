package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishAd;
import com.ruoyi.fishing.service.IFishAdService;

/**
 * 轮播图/活动
 */
@RestController
@RequestMapping("/fishing/ad")
public class FishAdController extends BaseController
{
    @Autowired
    private IFishAdService adService;

    @PreAuthorize("@ss.hasPermi('fishing:ad:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishAd ad)
    {
        startPage();
        return getDataTable(adService.selectFishAdList(ad));
    }

    @PreAuthorize("@ss.hasPermi('fishing:ad:query')")
    @GetMapping("/{adId}")
    public AjaxResult getInfo(@PathVariable Long adId)
    {
        return success(adService.selectFishAdByAdId(adId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:ad:add')")
    @Log(title = "轮播图管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishAd ad)
    {
        return toAjax(adService.insertFishAd(ad));
    }

    @PreAuthorize("@ss.hasPermi('fishing:ad:edit')")
    @Log(title = "轮播图管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishAd ad)
    {
        return toAjax(adService.updateFishAd(ad));
    }

    @PreAuthorize("@ss.hasPermi('fishing:ad:remove')")
    @Log(title = "轮播图管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{adIds}")
    public AjaxResult remove(@PathVariable Long[] adIds)
    {
        return toAjax(adService.deleteFishAdByAdIds(adIds));
    }
}
