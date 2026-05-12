package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishRentalGoods;
import com.ruoyi.fishing.domain.FishRentalOrder;
import com.ruoyi.fishing.service.IFishRentalService;

@RestController
@RequestMapping("/fishing/rental")
public class FishRentalController extends BaseController
{
    @Autowired private IFishRentalService rentalService;

    @PreAuthorize("@ss.hasPermi('fishing:rental:list')")
    @GetMapping("/goods/list")
    public TableDataInfo goodsList(FishRentalGoods q) { startPage(); return getDataTable(rentalService.selectGoodsList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:rental:query')")
    @GetMapping("/goods/{id}")
    public AjaxResult goodsInfo(@PathVariable Long id) { return success(rentalService.selectGoodsById(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:rental:add')")
    @Log(title = "租赁装备", businessType = BusinessType.INSERT)
    @PostMapping("/goods")
    public AjaxResult addGoods(@RequestBody FishRentalGoods g) { return toAjax(rentalService.insertGoods(g)); }

    @PreAuthorize("@ss.hasPermi('fishing:rental:edit')")
    @Log(title = "租赁装备", businessType = BusinessType.UPDATE)
    @PutMapping("/goods")
    public AjaxResult editGoods(@RequestBody FishRentalGoods g) { return toAjax(rentalService.updateGoods(g)); }

    @PreAuthorize("@ss.hasPermi('fishing:rental:remove')")
    @Log(title = "租赁装备", businessType = BusinessType.DELETE)
    @DeleteMapping("/goods/{ids}")
    public AjaxResult removeGoods(@PathVariable Long[] ids) { return toAjax(rentalService.deleteGoodsByIds(ids)); }

    @PreAuthorize("@ss.hasPermi('fishing:rentalOrder:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(FishRentalOrder q) { startPage(); return getDataTable(rentalService.selectOrderList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:rentalOrder:return')")
    @Log(title = "确认归还", businessType = BusinessType.UPDATE)
    @PutMapping("/order/return/{id}")
    public AjaxResult confirmReturn(@PathVariable Long id) { return toAjax(rentalService.confirmReturn(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:rentalOrder:forfeit')")
    @Log(title = "扣除押金", businessType = BusinessType.UPDATE)
    @PutMapping("/order/forfeit/{id}")
    public AjaxResult forfeit(@PathVariable Long id, @RequestParam(required = false) String remark) { return toAjax(rentalService.forfeitDeposit(id, remark)); }
}
