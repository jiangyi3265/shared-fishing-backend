package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.fishing.domain.FishUser;
import com.ruoyi.fishing.service.IFishUserService;

/**
 * 小程序用户
 */
@RestController
@RequestMapping("/fishing/user")
public class FishUserController extends BaseController
{
    @Autowired
    private IFishUserService userService;

    @PreAuthorize("@ss.hasPermi('fishing:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishUser user)
    {
        startPage();
        return getDataTable(userService.selectFishUserList(user));
    }

    @PreAuthorize("@ss.hasPermi('fishing:user:query')")
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId)
    {
        return success(userService.selectFishUserByUserId(userId));
    }

    @PreAuthorize("@ss.hasPermi('fishing:user:blacklist')")
    @Log(title = "用户黑名单", businessType = BusinessType.UPDATE)
    @PutMapping("/blacklist")
    public AjaxResult setBlacklist(@RequestBody java.util.Map<String, Object> body)
    {
        if (body.get("userId") == null) return AjaxResult.error("缺少userId");
        Long userId = Long.valueOf(body.get("userId").toString());
        boolean blacklist = Boolean.parseBoolean(body.getOrDefault("blacklist", "true").toString());
        String reason = (String) body.getOrDefault("reason", "");
        return toAjax(userService.setBlacklist(userId, blacklist, reason));
    }
}
