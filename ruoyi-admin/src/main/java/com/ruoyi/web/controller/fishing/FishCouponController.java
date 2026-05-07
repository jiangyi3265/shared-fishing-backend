package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishCouponTemplate;
import com.ruoyi.fishing.domain.FishUserCoupon;
import com.ruoyi.fishing.service.IFishCouponService;

/**
 * 优惠券模板
 */
@RestController
@RequestMapping("/fishing/coupon")
public class FishCouponController extends BaseController
{
    @Autowired
    private IFishCouponService couponService;

    @PreAuthorize("@ss.hasPermi('fishing:coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishCouponTemplate template)
    {
        startPage();
        return getDataTable(couponService.selectFishCouponTemplateList(template));
    }

    @PreAuthorize("@ss.hasPermi('fishing:coupon:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable Long templateId)
    {
        return success(couponService.selectFishCouponTemplateByTemplateId(templateId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:coupon:add')")
    @Log(title = "优惠券模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishCouponTemplate template)
    {
        return toAjax(couponService.insertFishCouponTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('fishing:coupon:edit')")
    @Log(title = "优惠券模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishCouponTemplate template)
    {
        return toAjax(couponService.updateFishCouponTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('fishing:coupon:remove')")
    @Log(title = "优惠券模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(couponService.deleteFishCouponTemplateByTemplateIds(templateIds));
    }

    @PreAuthorize("@ss.hasPermi('fishing:coupon:list')")
    @GetMapping("/user/list")
    public TableDataInfo userCouponList(FishUserCoupon query)
    {
        startPage();
        return getDataTable(couponService.selectUserCouponList(query));
    }
}
