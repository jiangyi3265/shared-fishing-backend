package com.ruoyi.web.controller.fishing;

import java.util.List;
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
import com.ruoyi.fishing.domain.FishRegistration;
import com.ruoyi.fishing.service.IFishRegistrationService;

/**
 * 活动报名管理
 */
@RestController
@RequestMapping("/fishing/registration")
public class FishRegistrationController extends BaseController
{
    @Autowired
    private IFishRegistrationService regService;

    @PreAuthorize("@ss.hasPermi('fishing:registration:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishRegistration reg)
    {
        startPage();
        return getDataTable(regService.selectFishRegistrationList(reg));
    }

    @PreAuthorize("@ss.hasPermi('fishing:registration:export')")
    @Log(title = "活动报名", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishRegistration reg)
    {
        List<FishRegistration> list = regService.selectFishRegistrationList(reg);
        ExcelUtil<FishRegistration> util = new ExcelUtil<>(FishRegistration.class);
        util.exportExcel(response, list, "活动报名");
    }

    @PreAuthorize("@ss.hasPermi('fishing:registration:query')")
    @GetMapping("/{regId}")
    public AjaxResult getInfo(@PathVariable Long regId)
    {
        return success(regService.selectFishRegistrationByRegId(regId));
    }
}
