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
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.service.IFishVenueService;

/**
 * 钓场管理
 */
@RestController
@RequestMapping("/fishing/venue")
public class FishVenueController extends BaseController
{
    @Autowired
    private IFishVenueService venueService;

    @PreAuthorize("@ss.hasPermi('fishing:venue:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishVenue venue)
    {
        startPage();
        return getDataTable(venueService.selectFishVenueList(venue));
    }

    @PreAuthorize("@ss.hasPermi('fishing:venue:export')")
    @Log(title = "钓场", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishVenue venue)
    {
        List<FishVenue> list = venueService.selectFishVenueList(venue);
        ExcelUtil<FishVenue> util = new ExcelUtil<>(FishVenue.class);
        util.exportExcel(response, list, "钓场数据");
    }

    @PreAuthorize("@ss.hasPermi('fishing:venue:query')")
    @GetMapping("/{venueId}")
    public AjaxResult getInfo(@PathVariable Long venueId)
    {
        return success(venueService.selectFishVenueByVenueId(venueId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:venue:add')")
    @Log(title = "钓场", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishVenue venue)
    {
        return toAjax(venueService.insertFishVenue(venue));
    }

    @PreAuthorize("@ss.hasPermi('fishing:venue:edit')")
    @Log(title = "钓场", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishVenue venue)
    {
        return toAjax(venueService.updateFishVenue(venue));
    }

    @PreAuthorize("@ss.hasPermi('fishing:venue:remove')")
    @Log(title = "钓场", businessType = BusinessType.DELETE)
    @DeleteMapping("/{venueIds}")
    public AjaxResult remove(@PathVariable Long[] venueIds)
    {
        return toAjax(venueService.deleteFishVenueByVenueIds(venueIds));
    }
}
