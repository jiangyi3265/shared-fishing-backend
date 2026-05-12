package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishMemberLevel;
import com.ruoyi.fishing.service.IFishMemberLevelService;

@RestController
@RequestMapping("/fishing/memberLevel")
public class FishMemberLevelController extends BaseController
{
    @Autowired
    private IFishMemberLevelService levelService;

    @PreAuthorize("@ss.hasPermi('fishing:memberLevel:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishMemberLevel q) { startPage(); return getDataTable(levelService.selectList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:memberLevel:query')")
    @GetMapping("/{levelId}")
    public AjaxResult getInfo(@PathVariable Long levelId) { return success(levelService.selectById(levelId)); }

    @PreAuthorize("@ss.hasPermi('fishing:memberLevel:add')")
    @Log(title = "会员等级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishMemberLevel l) { return toAjax(levelService.insert(l)); }

    @PreAuthorize("@ss.hasPermi('fishing:memberLevel:edit')")
    @Log(title = "会员等级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishMemberLevel l) { return toAjax(levelService.update(l)); }

    @PreAuthorize("@ss.hasPermi('fishing:memberLevel:remove')")
    @Log(title = "会员等级", businessType = BusinessType.DELETE)
    @DeleteMapping("/{levelIds}")
    public AjaxResult remove(@PathVariable Long[] levelIds) { return toAjax(levelService.deleteByIds(levelIds)); }
}
