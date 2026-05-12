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
import com.ruoyi.fishing.domain.FishCatchRecord;
import com.ruoyi.fishing.service.IFishCatchService;

@RestController
@RequestMapping("/fishing/catch")
public class FishCatchController extends BaseController
{
    @Autowired
    private IFishCatchService catchService;

    @PreAuthorize("@ss.hasPermi('fishing:catch:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishCatchRecord q) { startPage(); return getDataTable(catchService.selectList(q)); }

    @PreAuthorize("@ss.hasPermi('fishing:catch:query')")
    @GetMapping("/{catchId}")
    public AjaxResult getInfo(@PathVariable Long catchId) { return success(catchService.selectById(catchId)); }

    @PreAuthorize("@ss.hasPermi('fishing:catch:audit')")
    @Log(title = "钓获审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody Map<String, Object> body) {
        if (body.get("catchId") == null || body.get("status") == null) return AjaxResult.error("参数缺失");
        Long catchId = Long.valueOf(body.get("catchId").toString());
        int status = Integer.parseInt(body.get("status").toString());
        String reason = body.getOrDefault("rejectReason", "").toString();
        return toAjax(catchService.audit(catchId, status, reason));
    }

    @PreAuthorize("@ss.hasPermi('fishing:catch:feature')")
    @Log(title = "钓获精选", businessType = BusinessType.UPDATE)
    @PutMapping("/feature")
    public AjaxResult feature(@RequestBody Map<String, Object> body) {
        if (body.get("catchId") == null) return AjaxResult.error("参数缺失");
        Long catchId = Long.valueOf(body.get("catchId").toString());
        boolean featured = Boolean.parseBoolean(body.getOrDefault("featured", "true").toString());
        return toAjax(catchService.setFeatured(catchId, featured));
    }

    @PreAuthorize("@ss.hasPermi('fishing:catch:remove')")
    @Log(title = "钓获删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{catchIds}")
    public AjaxResult remove(@PathVariable Long[] catchIds) { return toAjax(catchService.deleteByIds(catchIds)); }
}
