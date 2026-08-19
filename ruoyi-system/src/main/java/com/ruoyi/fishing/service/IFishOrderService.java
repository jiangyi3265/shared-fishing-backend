package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishOrder;

/* mall order ids 通过 fish_order.mallOrderIds 字段串联 */

public interface IFishOrderService
{
    /** 当前生效的《垂钓安全须知及责任确认书》版本。 */
    String SAFETY_AGREEMENT_VERSION = "2026-08-16";
    public FishOrder selectFishOrderByOrderId(Long orderId);
    public List<FishOrder> selectFishOrderList(FishOrder order);
    public List<FishOrder> selectOrdersByUser(Long userId);
    public FishOrder selectRunningOrder(Long userId);
    public FishOrder selectPendingOrder(Long userId);

    /** 开始计时 */
    public FishOrder startOrder(Long userId, Long venueId, Long spotId,
            String safetyAgreementVersion, boolean safetyAgreed);

    /** 结束计时并计算费用 */
    public FishOrder finishOrder(Long userId);

    /** 撤销未支付的收竿结算，保留原下竿时间并恢复计时 */
    public FishOrder resumeOrder(Long userId, Long orderId);

    /** 支付（mock 模式直接结算）；mallOrderIds 可选，传入则一并合并支付 */
    public FishOrder pay(Long userId, Long orderId, Long couponId, List<Long> mallOrderIds);

    /** 支付（带余额抵扣）：useBalance=true 时优先扣余额，不足部分微信支付 */
    public FishOrder pay(Long userId, Long orderId, Long couponId, List<Long> mallOrderIds, boolean useBalance);

    /** 应用优惠券并计算实付金额（不改变状态，用于微信预支付）；mallOrderIds 可选 */
    public FishOrder preparePayment(Long userId, Long orderId, Long couponId, List<Long> mallOrderIds);

    /** 应用优惠券 + 余额，计算微信实付金额（不改变状态） */
    public FishOrder preparePayment(Long userId, Long orderId, Long couponId, List<Long> mallOrderIds, boolean useBalance);

    /** 微信异步通知回调后置为已支付（幂等） */
    public FishOrder markPaid(String orderNo, String tradeNo);

    /** 微信异步通知回调后置为已支付，并核验实际到账金额 */
    public FishOrder markPaid(String orderNo, String tradeNo, Integer paidAmountCents);

    /** 获取运行中订单预估金额 */
    public FishOrder estimateRunning(Long userId);

    /** 管理员人工结束 */
    public FishOrder adminFinish(Long orderId);

    /** 管理员取消订单 */
    public int adminCancel(Long orderId, String reason);

    /** 扫描超过阈值小时仍计时中的订单，按当前规则封顶结算为待支付，返回处理数量 */
    public int autoSettleTimeoutOrders(int timeoutHours);

    public int deleteFishOrderByOrderIds(Long[] orderIds);
}
