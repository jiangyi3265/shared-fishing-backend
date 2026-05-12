package com.ruoyi.web.controller.fishing;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishCompetition;
import com.ruoyi.fishing.service.IFishCompetitionService;

@RestController
@RequestMapping("/fishing/competition")
public class FishCompetitionController extends BaseController
{
    @Autowired private IFishCompetitionService compService;

    @PreAuthorize("@ss.hasPermi('fishing:competition:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishCompetition q) { startPage(); return getDataTable(compService.selectList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:competition:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) { return success(compService.selectById(id)); }

    @PreAuthorize("@ss.hasPermi('fishing:competition:add')")
    @Log(title = "比赛", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishCompetition c) { return toAjax(compService.insert(c)); }

    @PreAuthorize("@ss.hasPermi('fishing:competition:edit')")
    @Log(title = "比赛", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishCompetition c) { return toAjax(compService.update(c)); }

    @PreAuthorize("@ss.hasPermi('fishing:competition:edit')")
    @PutMapping("/status/{id}/{status}")
    public AjaxResult changeStatus(@PathVariable Long id, @PathVariable int status) { return toAjax(compService.updateStatus(id, status)); }

    @PreAuthorize("@ss.hasPermi('fishing:competition:weigh')")
    @Log(title = "称重", businessType = BusinessType.UPDATE)
    @PutMapping("/weigh")
    public AjaxResult weigh(@RequestBody Map<String, Object> body) {
        if (body.get("entryId") == null || body.get("weightGram") == null) return AjaxResult.error("参数缺失");
        Long entryId = Long.valueOf(body.get("entryId").toString());
        int weightGram = Integer.parseInt(body.get("weightGram").toString());
        int fishCount = body.containsKey("fishCount") ? Integer.parseInt(body.get("fishCount").toString()) : 0;
        String weighBy = body.getOrDefault("weighBy", "").toString();
        String weighImage = body.getOrDefault("weighImage", "").toString();
        return toAjax(compService.weigh(entryId, weightGram, fishCount, weighBy, weighImage));
    }

    @PreAuthorize("@ss.hasPermi('fishing:competition:settle')")
    @Log(title = "结算发奖", businessType = BusinessType.UPDATE)
    @PostMapping("/settle/{id}")
    public AjaxResult settle(@PathVariable Long id) { return success(compService.settle(id)); }
}
