package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishPointsGoods;
import com.ruoyi.fishing.domain.FishPointsExchange;
import com.ruoyi.fishing.service.IFishPointsService;

@RestController
@RequestMapping("/fishing/points")
public class FishPointsController extends BaseController
{
    @Autowired private IFishPointsService pointsService;

    @PreAuthorize("@ss.hasPermi('fishing:pointsGoods:list')")
    @GetMapping("/goods/list")
    public TableDataInfo goodsList(FishPointsGoods q) { startPage(); return getDataTable(pointsService.selectGoodsList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:pointsGoods:query')")
    @GetMapping("/goods/{id}")
    public AjaxResult goodsInfo(@PathVariable Long id) { return success(pointsService.selectGoodsById(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:pointsGoods:add')")
    @Log(title = "积分商品", businessType = BusinessType.INSERT)
    @PostMapping("/goods")
    public AjaxResult addGoods(@RequestBody FishPointsGoods g) { return toAjax(pointsService.insertGoods(g)); }

    @PreAuthorize("@ss.hasPermi('fishing:pointsGoods:edit')")
    @Log(title = "积分商品", businessType = BusinessType.UPDATE)
    @PutMapping("/goods")
    public AjaxResult editGoods(@RequestBody FishPointsGoods g) { return toAjax(pointsService.updateGoods(g)); }

    @PreAuthorize("@ss.hasPermi('fishing:pointsGoods:remove')")
    @Log(title = "积分商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/goods/{ids}")
    public AjaxResult removeGoods(@PathVariable Long[] ids) { return toAjax(pointsService.deleteGoodsByIds(ids)); }

    @PreAuthorize("@ss.hasPermi('fishing:pointsExchange:list')")
    @GetMapping("/exchange/list")
    public TableDataInfo exchangeList(FishPointsExchange q) { startPage(); return getDataTable(pointsService.selectExchangeList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:pointsExchange:deliver')")
    @Log(title = "兑换发放", businessType = BusinessType.UPDATE)
    @PutMapping("/exchange/deliver/{id}")
    public AjaxResult deliver(@PathVariable Long id) { return toAjax(pointsService.deliverExchange(id)); }
}
