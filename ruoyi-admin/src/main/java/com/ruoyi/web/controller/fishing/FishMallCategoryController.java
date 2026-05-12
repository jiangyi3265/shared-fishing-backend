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
import com.ruoyi.fishing.domain.FishMallCategory;
import com.ruoyi.fishing.service.IFishMallService;

@RestController
@RequestMapping("/fishing/mallCategory")
public class FishMallCategoryController extends BaseController
{
    @Autowired
    private IFishMallService mallService;

    @PreAuthorize("@ss.hasPermi('fishing:mallCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishMallCategory cat)
    {
        startPage();
        return getDataTable(mallService.listCategory(cat));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallCategory:query')")
    @GetMapping("/{catId}")
    public AjaxResult get(@PathVariable Long catId) { return success(mallService.getCategory(catId)); }

    @PreAuthorize("@ss.hasPermi('fishing:mallCategory:add')")
    @Log(title = "商城分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishMallCategory cat)
    {
        cat.setCreateBy(SecurityUtils.getUsername());
        return toAjax(mallService.saveCategory(cat));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallCategory:edit')")
    @Log(title = "商城分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishMallCategory cat)
    {
        cat.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(mallService.updateCategory(cat));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallCategory:remove')")
    @Log(title = "商城分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{catIds}")
    public AjaxResult remove(@PathVariable Long[] catIds) { return toAjax(mallService.deleteCategories(catIds)); }
}
