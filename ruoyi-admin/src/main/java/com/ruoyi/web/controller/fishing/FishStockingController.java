package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishStockingRecord;
import com.ruoyi.fishing.service.IFishStockingService;

/**
 * 放鱼记录
 */
@RestController
@RequestMapping("/fishing/stocking")
public class FishStockingController extends BaseController
{
    @Autowired
    private IFishStockingService stockingService;

    @PreAuthorize("@ss.hasPermi('fishing:stocking:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishStockingRecord q)
    {
        startPage();
        return getDataTable(stockingService.selectList(q));
    }

    @PreAuthorize("@ss.hasPermi('fishing:stocking:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId)
    {
        return success(stockingService.selectByRecordId(recordId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:stocking:add')")
    @Log(title = "放鱼记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishStockingRecord r)
    {
        return toAjax(stockingService.insertRecord(r));
    }

    @PreAuthorize("@ss.hasPermi('fishing:stocking:edit')")
    @Log(title = "放鱼记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishStockingRecord r)
    {
        return toAjax(stockingService.updateRecord(r));
    }

    @PreAuthorize("@ss.hasPermi('fishing:stocking:remove')")
    @Log(title = "放鱼记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(stockingService.deleteByRecordIds(recordIds));
    }
}
