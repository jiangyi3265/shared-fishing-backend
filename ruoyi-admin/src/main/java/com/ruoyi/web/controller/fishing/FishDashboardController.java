package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.fishing.service.IDashboardService;

@RestController
@RequestMapping("/fishing/dashboard")
public class FishDashboardController extends BaseController
{
    @Autowired
    private IDashboardService dashboardService;

    @PreAuthorize("@ss.hasPermi('fishing:order:list')")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return success(dashboardService.summary());
    }

    @PreAuthorize("@ss.hasPermi('fishing:order:list')")
    @GetMapping("/order-trend")
    public AjaxResult orderTrend(@RequestParam(defaultValue = "7") int days)
    {
        return success(dashboardService.orderTrend(days));
    }

    @PreAuthorize("@ss.hasPermi('fishing:order:list')")
    @GetMapping("/revenue-trend")
    public AjaxResult revenueTrend(@RequestParam(defaultValue = "7") int days)
    {
        return success(dashboardService.revenueTrend(days));
    }
}
