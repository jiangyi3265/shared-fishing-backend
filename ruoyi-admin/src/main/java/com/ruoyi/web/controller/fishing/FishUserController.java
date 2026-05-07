package com.ruoyi.web.controller.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
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
}
