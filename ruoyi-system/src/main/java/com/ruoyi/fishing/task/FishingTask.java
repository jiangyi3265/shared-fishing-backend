package com.ruoyi.fishing.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.fishing.service.IFishMallService;
import com.ruoyi.fishing.service.IFishOrderService;
import com.ruoyi.fishing.service.IFishSpotService;

/**
 * 钓场定时任务，由若依 quartz 通过 bean 名调用：
 *   fishingTask.autoSettleTimeoutOrders(24)
 *   fishingTask.autoCancelTimeoutMallOrders(30)
 */
@Component("fishingTask")
public class FishingTask
{
    private static final Logger log = LoggerFactory.getLogger(FishingTask.class);

    @Autowired
    private IFishOrderService orderService;

    @Autowired
    private IFishMallService mallService;

    @Autowired
    private IFishSpotService spotService;

    /** 自动结算超过 timeoutHours 小时仍计时中的订单，置为待支付 */
    public void autoSettleTimeoutOrders(Integer timeoutHours)
    {
        int hours = timeoutHours == null ? 24 : timeoutHours;
        int n = orderService.autoSettleTimeoutOrders(hours);
        if (n > 0) log.info("[fishing] auto-settled {} timeout orders (>{}h)", n, hours);
    }

    /** 自动取消超过 timeoutMinutes 分钟仍待支付的商城订单，回滚库存 */
    public void autoCancelTimeoutMallOrders(Integer timeoutMinutes)
    {
        int minutes = timeoutMinutes == null ? 30 : timeoutMinutes;
        int n = mallService.autoCancelTimeoutOrders(minutes);
        if (n > 0) log.info("[fishing] auto-canceled {} timeout mall orders (>{}min)", n, minutes);
    }

    /** 自动释放超时未到场的预订 */
    public void autoExpireReservations()
    {
        int n = spotService.expireOverdue();
        if (n > 0) log.info("[fishing] auto-expired {} overdue reservations", n);
    }
}
