package com.ruoyi.web.controller.fishing;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.service.IFishMallService;

@RestController
@RequestMapping("/fishing/mallOrder")
public class FishMallOrderController extends BaseController
{
    @Autowired
    private IFishMallService mallService;

    @PreAuthorize("@ss.hasPermi('fishing:mallOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishMallOrder o)
    {
        startPage();
        return getDataTable(mallService.listOrder(o));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallOrder:query')")
    @GetMapping("/{mallOrderId}")
    public AjaxResult get(@PathVariable Long mallOrderId) { return success(mallService.getOrder(mallOrderId)); }

    /** 确认领取：传订单号（兼容历史凭证） */
    @PreAuthorize("@ss.hasPermi('fishing:mallOrder:redeem')")
    @Log(title = "商城订单确认领取", businessType = BusinessType.UPDATE)
    @PostMapping("/redeem")
    public AjaxResult redeem(@RequestBody Map<String, String> body)
    {
        return success(mallService.redeem(body.getOrDefault("code", ""), SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('fishing:mallOrder:redeem')")
    @Log(title = "商城订单取消", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{mallOrderId}")
    public AjaxResult cancel(@PathVariable Long mallOrderId) { return success(mallService.cancel(mallOrderId)); }

    @PreAuthorize("@ss.hasPermi('fishing:mallOrder:export')")
    @Log(title = "商城订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishMallOrder o)
    {
        List<FishMallOrder> list = mallService.listOrder(o);
        new ExcelUtil<>(FishMallOrder.class).exportExcel(response, list, "商城订单");
    }
}
