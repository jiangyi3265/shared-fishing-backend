package com.ruoyi.web.controller.fishing;

import java.util.HashMap;
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
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.domain.FishRechargeOrder;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.service.IFishBalanceService;

@RestController
@RequestMapping("/fishing/rechargeOrder")
public class FishRechargeOrderController extends BaseController
{
    @Autowired
    private IFishBalanceService balanceService;

    @PreAuthorize("@ss.hasPermi('fishing:rechargeOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishRechargeOrder o)
    {
        startPage();
        return getDataTable(balanceService.listRechargeOrders(o));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rechargeOrder:query')")
    @GetMapping("/balance/{userId}")
    public AjaxResult balance(@PathVariable Long userId)
    {
        FishUserBalance b = balanceService.getBalance(userId);
        List<FishBalanceLog> logs = balanceService.recentLogs(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("balance", b);
        data.put("logs", logs);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('fishing:rechargeOrder:adjust')")
    @Log(title = "余额调整", businessType = BusinessType.UPDATE)
    @PostMapping("/adjust")
    public AjaxResult adjust(@RequestBody Map<String, Object> body)
    {
        Long userId = parseLong(body.get("userId"));
        Integer delta = parseInt(body.get("deltaCents"));
        String remark = body.get("remark") == null ? "" : body.get("remark").toString();
        if (userId == null || delta == null || delta == 0) return AjaxResult.error("参数错误");
        return success(balanceService.adminAdjust(userId, delta, remark, SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('fishing:rechargeOrder:export')")
    @Log(title = "充值订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishRechargeOrder o)
    {
        List<FishRechargeOrder> list = balanceService.listRechargeOrders(o);
        new ExcelUtil<>(FishRechargeOrder.class).exportExcel(response, list, "充值订单");
    }

    private Long parseLong(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }
    private Integer parseInt(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return null; }
    }
}
