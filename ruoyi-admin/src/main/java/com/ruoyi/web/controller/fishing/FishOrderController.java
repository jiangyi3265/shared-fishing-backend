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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.service.IFishOrderService;

/**
 * 订单管理
 */
@RestController
@RequestMapping("/fishing/order")
public class FishOrderController extends BaseController
{
    @Autowired
    private IFishOrderService orderService;

    @PreAuthorize("@ss.hasPermi('fishing:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishOrder order)
    {
        startPage();
        return getDataTable(orderService.selectFishOrderList(order));
    }

    @PreAuthorize("@ss.hasPermi('fishing:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishOrder order)
    {
        List<FishOrder> list = orderService.selectFishOrderList(order);
        ExcelUtil<FishOrder> util = new ExcelUtil<>(FishOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    @PreAuthorize("@ss.hasPermi('fishing:order:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(orderService.selectFishOrderByOrderId(orderId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:order:finish')")
    @Log(title = "人工结束订单", businessType = BusinessType.UPDATE)
    @PutMapping("/finish/{orderId}")
    public AjaxResult adminFinish(@PathVariable Long orderId)
    {
        return success(orderService.adminFinish(orderId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:order:cancel')")
    @Log(title = "取消订单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{orderId}")
    public AjaxResult adminCancel(@PathVariable Long orderId, @RequestBody(required = false) Map<String, String> body)
    {
        String reason = body == null ? "" : body.getOrDefault("reason", "");
        return toAjax(orderService.adminCancel(orderId, reason));
    }
}
