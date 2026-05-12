package com.ruoyi.web.controller.fishing;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.fishing.domain.FishMallGoods;
import com.ruoyi.fishing.service.IFishMallService;

@RestController
@RequestMapping("/fishing/mallGoods")
public class FishMallGoodsController extends BaseController
{
    @Autowired
    private IFishMallService mallService;

    @PreAuthorize("@ss.hasPermi('fishing:mallGoods:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishMallGoods g)
    {
        startPage();
        return getDataTable(mallService.listGoods(g));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallGoods:query')")
    @GetMapping("/{goodsId}")
    public AjaxResult get(@PathVariable Long goodsId) { return success(mallService.getGoods(goodsId)); }

    @PreAuthorize("@ss.hasPermi('fishing:mallGoods:add')")
    @Log(title = "商城商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishMallGoods g)
    {
        g.setCreateBy(SecurityUtils.getUsername());
        return toAjax(mallService.saveGoods(g));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallGoods:edit')")
    @Log(title = "商城商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishMallGoods g)
    {
        g.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(mallService.updateGoods(g));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallGoods:toggle')")
    @Log(title = "商品上下架", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{goodsId}")
    public AjaxResult toggle(@PathVariable Long goodsId, @RequestBody Map<String, String> body)
    {
        return toAjax(mallService.toggleGoodsStatus(goodsId, body.getOrDefault("status", "0")));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallGoods:remove')")
    @Log(title = "商城商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds) { return toAjax(mallService.deleteGoods(goodsIds)); }
}
