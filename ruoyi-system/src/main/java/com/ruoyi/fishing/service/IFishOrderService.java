package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishOrder;

public interface IFishOrderService
{
    public FishOrder selectFishOrderByOrderId(Long orderId);
    public List<FishOrder> selectFishOrderList(FishOrder order);
    public List<FishOrder> selectOrdersByUser(Long userId);
    public FishOrder selectRunningOrder(Long userId);
    public FishOrder selectPendingOrder(Long userId);

    /** 开始计时 */
    public FishOrder startOrder(Long userId, Long venueId);

    /** 结束计时并计算费用 */
    public FishOrder finishOrder(Long userId);

    /** 支付 */
    public FishOrder pay(Long userId, Long orderId, Long couponId);

    /** 应用优惠券并计算实付金额（不改变订单状态，用于微信预支付） */
    public FishOrder preparePayment(Long userId, Long orderId, Long couponId);

    /** 微信异步通知回调后置为已支付（幂等） */
    public FishOrder markPaid(String orderNo, String tradeNo);

    /** 获取运行中订单预估金额 */
    public FishOrder estimateRunning(Long userId);

    /** 管理员人工结束 */
    public FishOrder adminFinish(Long orderId);

    /** 管理员取消订单 */
    public int adminCancel(Long orderId, String reason);

    public int deleteFishOrderByOrderIds(Long[] orderIds);
}
