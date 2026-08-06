package com.ruoyi.web.controller.fishing;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.service.IFishCardGameService;

@RestController
@RequestMapping("/fishing/fish-card")
public class FishCardGameController extends BaseController
{
    @Autowired private IFishCardGameService service;

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
