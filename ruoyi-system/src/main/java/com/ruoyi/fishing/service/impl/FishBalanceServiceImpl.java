package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.domain.FishRechargeOrder;
import com.ruoyi.fishing.domain.FishRechargePlan;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.mapper.FishBalanceLogMapper;
import com.ruoyi.fishing.mapper.FishRechargeOrderMapper;
import com.ruoyi.fishing.mapper.FishRechargePlanMapper;
import com.ruoyi.fishing.mapper.FishUserBalanceMapper;
import com.ruoyi.fishing.service.IFishBalanceService;

@Service
public class FishBalanceServiceImpl implements IFishBalanceService
{
    @Autowired private FishUserBalanceMapper balanceMapper;
    @Autowired private FishBalanceLogMapper logMapper;
    @Autowired private FishRechargePlanMapper planMapper;
    @Autowired private FishRechargeOrderMapper rechargeMapper;

    // ===== 余额 =====

    @Override
    public FishUserBalance getBalance(Long userId)
    {
        balanceMapper.insertIfAbsent(userId);
        return balanceMapper.selectByUserId(userId);
    }

    @Override
    public List<FishBalanceLog> recentLogs(Long userId) { return logMapper.selectByUser(userId); }

    @Override
    public List<FishBalanceLog> listLogs(FishBalanceLog q) { return logMapper.selectList(q); }

    @Override
    @Transactional
    public FishBalanceLog applyDelta(Long userId, int delta, String type, String relatedOrderNo, String remark, String operator)
    {
        if (delta == 0) throw new ServiceException("变动金额不可为 0");
        balanceMapper.insertIfAbsent(userId);
        boolean recharge = FishBalanceLog.TYPE_RECHARGE.equals(type);
        int updated = balanceMapper.applyDelta(userId, delta, recharge);
        if (updated == 0) throw new ServiceException(delta < 0 ? "余额不足" : "余额更新失败");
        FishUserBalance after = balanceMapper.selectByUserId(userId);

        FishBalanceLog log = new FishBalanceLog();
        log.setUserId(userId);
        log.setDeltaCents(delta);
        log.setBalanceAfterCents(after.getBalanceCents());
        log.setType(type);
        log.setRelatedOrderNo(relatedOrderNo == null ? "" : relatedOrderNo);
        log.setRemark(remark == null ? "" : remark);
        log.setOperator(operator == null ? "" : operator);
        logMapper.insert(log);
        return log;
    }

    @Override
    public FishBalanceLog adminAdjust(Long userId, int delta, String remark, String operator)
    {
        return applyDelta(userId, delta, FishBalanceLog.TYPE_ADMIN_ADJUST, "", remark, operator);
    }

    // ===== 套餐 =====

    @Override public List<FishRechargePlan> listPlans(FishRechargePlan q) { return planMapper.selectList(q); }

    @Override
    public List<FishRechargePlan> listActivePlans()
    {
        FishRechargePlan q = new FishRechargePlan();
        q.setStatus("0");
        return planMapper.selectList(q);
    }

    @Override public FishRechargePlan getPlan(Long planId) { return planMapper.selectById(planId); }
    @Override public int savePlan(FishRechargePlan p)
    {
        if (p.getStatus() == null) p.setStatus("0");
        if (p.getSort() == null) p.setSort(0);
        if (p.getBonusCents() == null) p.setBonusCents(0);
        return planMapper.insert(p);
    }
    @Override public int updatePlan(FishRechargePlan p) { return planMapper.update(p); }
    @Override public int deletePlans(Long[] ids) { return planMapper.deleteByIds(ids); }

    // ===== 充值订单 =====

    @Override
    @Transactional
    public FishRechargeOrder createRechargeOrder(Long userId, Long planId, Integer customAmountCents)
    {
        int amount;
        int bonus = 0;
        FishRechargePlan plan = null;
        if (planId != null)
        {
            plan = planMapper.selectById(planId);
            if (plan == null) throw new ServiceException("套餐不存在");
            if (!"0".equals(plan.getStatus())) throw new ServiceException("套餐已下架");
            amount = plan.getAmountCents();
            bonus  = plan.getBonusCents() == null ? 0 : plan.getBonusCents();
        }
        else
        {
            if (customAmountCents == null || customAmountCents < 100)
                throw new ServiceException("自定义充值金额最少 1 元");
            amount = customAmountCents;
        }

        long now = System.currentTimeMillis();
        FishRechargeOrder o = new FishRechargeOrder();
        o.setRechargeNo("R" + now + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        o.setUserId(userId);
        o.setPlanId(plan != null ? plan.getPlanId() : null);
        o.setAmountCents(amount);
        o.setBonusCents(bonus);
        o.setTotalCreditCents(amount + bonus);
        o.setStatus(0);
        rechargeMapper.insert(o);
        return o;
    }

    @Override
    @Transactional
    public FishRechargeOrder markRechargePaid(String rechargeNo, String tradeNo)
    {
        FishRechargeOrder o = rechargeMapper.selectByRechargeNo(rechargeNo);
        if (o == null) return null;
        if (o.getStatus() != null && o.getStatus() == 1) return o; // 幂等
        if (o.getStatus() == null || o.getStatus() != 0) return o;

        int g = rechargeMapper.updateStatusWithGuard(o.getRechargeId(), 0, 1);
        if (g == 0) return rechargeMapper.selectById(o.getRechargeId());

        o.setStatus(1);
        o.setPayTradeNo(tradeNo == null ? "" : tradeNo);
        o.setPaidTime(new Date());
        rechargeMapper.update(o);

        // 入账：本金按 recharge，赠送按 gift（不计入累计充值）
        applyDelta(o.getUserId(), o.getAmountCents(), FishBalanceLog.TYPE_RECHARGE,
                   o.getRechargeNo(), "充值入账", "system");
        if (o.getBonusCents() != null && o.getBonusCents() > 0)
        {
            applyDelta(o.getUserId(), o.getBonusCents(), FishBalanceLog.TYPE_GIFT,
                       o.getRechargeNo(), "充值赠送", "system");
        }
        return o;
    }

    @Override public List<FishRechargeOrder> listRechargeOrders(FishRechargeOrder q) { return rechargeMapper.selectList(q); }
    @Override public List<FishRechargeOrder> listMyRechargeOrders(Long userId) { return rechargeMapper.selectByUser(userId); }
}
