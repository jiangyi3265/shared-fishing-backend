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
import com.ruoyi.fishing.domain.FishRefund;
import com.ruoyi.fishing.service.IFishRefundService;

/**
 * 退款审批
 */
@RestController
@RequestMapping("/fishing/refund")
public class FishRefundController extends BaseController
{
    @Autowired
    private IFishRefundService refundService;

    @PreAuthorize("@ss.hasPermi('fishing:refund:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishRefund refund)
    {
        startPage();
        return getDataTable(refundService.selectFishRefundList(refund));
    }

    @PreAuthorize("@ss.hasPermi('fishing:refund:export')")
    @Log(title = "退款", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishRefund refund)
    {
        List<FishRefund> list = refundService.selectFishRefundList(refund);
        new ExcelUtil<>(FishRefund.class).exportExcel(response, list, "退款数据");
    }

    @PreAuthorize("@ss.hasPermi('fishing:refund:query')")
    @GetMapping("/{refundId}")
    public AjaxResult getInfo(@PathVariable Long refundId)
    {
        return success(refundService.selectFishRefundByRefundId(refundId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:refund:audit')")
    @Log(title = "通过退款", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{refundId}")
    public AjaxResult approve(@PathVariable Long refundId, @RequestBody(required = false) Map<String, Object> body)
    {
        Integer amount = null;
        String remark = "";
        if (body != null)
        {
            Object a = body.get("refundAmountCents");
            if (a instanceof Number) amount = ((Number) a).intValue();
            else if (a != null) try { amount = Integer.parseInt(a.toString()); } catch (Exception ignore) {}
            Object r = body.get("auditRemark");
            if (r != null) remark = r.toString();
        }
        return success(refundService.approve(refundId, amount, remark, SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('fishing:refund:audit')")
    @Log(title = "驳回退款", businessType = BusinessType.UPDATE)
    @PutMapping("/reject/{refundId}")
    public AjaxResult reject(@PathVariable Long refundId, @RequestBody(required = false) Map<String, String> body)
    {
        String remark = body == null ? "" : body.getOrDefault("auditRemark", "");
        return success(refundService.reject(refundId, remark, SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('fishing:refund:audit')")
    @Log(title = "退款", businessType = BusinessType.DELETE)
    @DeleteMapping("/{refundIds}")
    public AjaxResult remove(@PathVariable Long[] refundIds)
    {
        return toAjax(refundService.deleteFishRefundByRefundIds(refundIds));
    }
}
