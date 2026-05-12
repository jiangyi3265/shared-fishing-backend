package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.domain.FishRechargeOrder;
import com.ruoyi.fishing.domain.FishRechargePlan;
import com.ruoyi.fishing.domain.FishUserBalance;

/**
 * 储值余额服务：余额查询/变更 + 充值套餐 + 充值订单
 */
public interface IFishBalanceService
{
    // 余额
    FishUserBalance getBalance(Long userId);
    List<FishBalanceLog> recentLogs(Long userId);
    List<FishBalanceLog> listLogs(FishBalanceLog q);

    /**
     * 通用扣减（消费/退款返还都走这里）。delta < 0 扣余额，delta > 0 入账。
     * 返回流水（含 balanceAfterCents），余额不足抛 ServiceException。
     */
    FishBalanceLog applyDelta(Long userId, int delta, String type, String relatedOrderNo, String remark, String operator);

    /** 后台手工调整余额 */
    FishBalanceLog adminAdjust(Long userId, int delta, String remark, String operator);

    // 充值套餐
    List<FishRechargePlan> listPlans(FishRechargePlan q);
    List<FishRechargePlan> listActivePlans();
    FishRechargePlan getPlan(Long planId);
    int savePlan(FishRechargePlan p);
    int updatePlan(FishRechargePlan p);
    int deletePlans(Long[] ids);

    // 充值订单
    /** 创建充值订单（status=0 待支付），可基于套餐 planId 或自定义 amountCents */
    FishRechargeOrder createRechargeOrder(Long userId, Long planId, Integer customAmountCents);

    /** 微信回调：标记已支付并入账（含赠送金额） */
    FishRechargeOrder markRechargePaid(String rechargeNo, String tradeNo);

    List<FishRechargeOrder> listRechargeOrders(FishRechargeOrder q);
    List<FishRechargeOrder> listMyRechargeOrders(Long userId);
}
