package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishGroupFishing;
import com.ruoyi.fishing.service.IFishGroupService;

@RestController
@RequestMapping("/fishing/group")
public class FishGroupController extends BaseController
{
    @Autowired private IFishGroupService groupService;

    @PreAuthorize("@ss.hasPermi('fishing:group:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishGroupFishing q) { startPage(); return getDataTable(groupService.selectList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:group:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) { return success(groupService.selectById(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:group:cancel')")
    @Log(title = "取消拼场", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{id}")
    public AjaxResult cancel(@PathVariable Long id) { return toAjax(groupService.cancel(id, null)); }
}
