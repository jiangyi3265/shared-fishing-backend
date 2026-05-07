package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishBillingRule;
import com.ruoyi.fishing.service.IFishBillingRuleService;

/**
 * 计费规则
 */
@RestController
@RequestMapping("/fishing/rule")
public class FishBillingRuleController extends BaseController
{
    @Autowired
    private IFishBillingRuleService ruleService;

    @PreAuthorize("@ss.hasPermi('fishing:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishBillingRule rule)
    {
        startPage();
        return getDataTable(ruleService.selectFishBillingRuleList(rule));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rule:query')")
    @GetMapping("/{ruleId}")
    public AjaxResult getInfo(@PathVariable Long ruleId)
    {
        return success(ruleService.selectFishBillingRuleByRuleId(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rule:add')")
    @Log(title = "计费规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishBillingRule rule)
    {
        return toAjax(ruleService.insertFishBillingRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rule:edit')")
    @Log(title = "计费规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishBillingRule rule)
    {
        return toAjax(ruleService.updateFishBillingRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rule:remove')")
    @Log(title = "计费规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(ruleService.deleteFishBillingRuleByRuleIds(ruleIds));
    }
}
