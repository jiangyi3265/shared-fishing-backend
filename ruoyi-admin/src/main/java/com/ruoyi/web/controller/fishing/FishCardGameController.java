package com.ruoyi.web.controller.fishing;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishCatchRecord;
import com.ruoyi.fishing.service.IFishCatchService;
import com.ruoyi.fishing.service.IFishCardGameService;

@RestController
@RequestMapping("/fishing/fish-card")
public class FishCardGameController extends BaseController
{
    @Autowired private IFishCardGameService service;
    @Autowired private IFishCatchService catchService;

    /**
     * 鱼鉴认证审核列表。把客户上传记录直接放到鱼鉴后台，避免运营人员去“钓获打卡”里查找。
     */
    @PreAuthorize("@ss.hasPermi('fishing:fishCard:list')")
    @GetMapping("/review/list")
    public TableDataInfo reviewList(@RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String nickname,
                                    @RequestParam(required = false) String fishSpecies)
    {
        FishCatchRecord query = new FishCatchRecord();
        query.setCardOnly(true);
        query.setStatus(status);
        query.setNickname(nickname);
        query.setFishSpecies(fishSpecies);
        startPage();
        return getDataTable(catchService.selectList(query));
    }

    @PreAuthorize("@ss.hasAnyPermi('fishing:fishCard:audit,fishing:catch:audit')")
    @Log(title = "鱼鉴认证审核", businessType = BusinessType.UPDATE)
    @PutMapping("/review/audit")
    public AjaxResult auditReview(@RequestBody Map<String, Object> body)
    {
        if (body == null || body.get("catchId") == null || body.get("status") == null)
        {
            return AjaxResult.error("参数缺失");
        }
        Long catchId = Long.valueOf(String.valueOf(body.get("catchId")));
        int status = Integer.parseInt(String.valueOf(body.get("status")));
        String reason = body.get("rejectReason") == null ? "" : String.valueOf(body.get("rejectReason"));
        return toAjax(catchService.audit(catchId, status, reason));
    }

    @PreAuthorize("@ss.hasPermi('fishing:fishCard:list')")
    @GetMapping("/round/list")
    public TableDataInfo list(@RequestParam(required = false) Long campaignId,
                              @RequestParam(required = false) Integer rewardStatus,
                              @RequestParam(required = false) String nickname)
    {
        Map<String, Object> query = new HashMap<>();
        query.put("campaignId", campaignId);
        query.put("rewardStatus", rewardStatus);
        query.put("nickname", nickname);
        startPage();
        return getDataTable(service.selectAdminRounds(query));
    }

    @PreAuthorize("@ss.hasPermi('fishing:fishCard:pay')")
    @Log(title = "鱼鉴奖励发放", businessType = BusinessType.UPDATE)
    @PostMapping("/reward/{roundId}/paid")
    public AjaxResult markPaid(@PathVariable Long roundId)
    {
        return toAjax(service.markRewardPaid(roundId, getUsername()));
    }
}
