package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishRechargePlan;
import com.ruoyi.fishing.service.IFishBalanceService;

@RestController
@RequestMapping("/fishing/rechargePlan")
public class FishRechargePlanController extends BaseController
{
    @Autowired
    private IFishBalanceService balanceService;

    @PreAuthorize("@ss.hasPermi('fishing:rechargePlan:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishRechargePlan p)
    {
        startPage();
        return getDataTable(balanceService.listPlans(p));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rechargePlan:query')")
    @GetMapping("/{planId}")
    public AjaxResult get(@PathVariable Long planId) { return success(balanceService.getPlan(planId)); }

    @PreAuthorize("@ss.hasPermi('fishing:rechargePlan:add')")
    @Log(title = "充值套餐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishRechargePlan p)
    {
        p.setCreateBy(SecurityUtils.getUsername());
        return toAjax(balanceService.savePlan(p));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rechargePlan:edit')")
    @Log(title = "充值套餐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishRechargePlan p)
    {
        p.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(balanceService.updatePlan(p));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rechargePlan:remove')")
    @Log(title = "充值套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) { return toAjax(balanceService.deletePlans(ids)); }
}
