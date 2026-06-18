package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishBalanceLog;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.domain.FishWeighOrder;
import com.ruoyi.fishing.mapper.FishVenueMapper;
import com.ruoyi.fishing.mapper.FishWeighOrderMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishWeighService;

/**
 * 称鱼结算 Service 实现
 */
@Service
public class FishWeighServiceImpl implements IFishWeighService
{
    @Autowired private FishWeighOrderMapper weighMapper;
    @Autowired private FishVenueMapper venueMapper;
    @Autowired private IFishBalanceService balanceService;

    /** 1 斤 = 500 克 */
    private static final int GRAMS_PER_JIN = 500;

    @Override
    public int[] getPrices(Long venueId)
    {
        int normal = DEFAULT_PRICE_CENTS;
        int member = DEFAULT_MEMBER_PRICE_CENTS;
        if (venueId != null)
        {
            FishVenue v = venueMapper.selectFishVenueByVenueId(venueId);
            if (v != null)
            {
                if (v.getFishPriceCents() != null && v.getFishPriceCents() > 0) normal = v.getFishPriceCents();
                if (v.getFishMemberPriceCents() != null && v.getFishMemberPriceCents() > 0) member = v.getFishMemberPriceCents();
            }
        }
        return new int[] { normal, member };
    }

    @Override
    @Transactional
    public FishWeighOrder createOrder(Long userId, Long venueId, int weightGrams, boolean isMember)
    {
        if (userId == null) throw new ServiceException("用户不存在");
        if (weightGrams <= 0) throw new ServiceException("请输入有效的鱼获重量");

        int[] prices = getPrices(venueId);
        int priceCents = isMember ? prices[1] : prices[0];
        // 服务端重算金额：金额(分) = 重量(克) × 单价(分/斤) ÷ 500，四舍五入
        int amountCents = (int) Math.round((double) weightGrams * priceCents / GRAMS_PER_JIN);
        if (amountCents <= 0) throw new ServiceException("结算金额异常");

        long now = System.currentTimeMillis();
        FishWeighOrder o = new FishWeighOrder();
        o.setWeighNo("W" + now + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        o.setUserId(userId);
        o.setVenueId(venueId);
        o.setWeightGrams(weightGrams);
        o.setPriceCents(priceCents);
        o.setIsMember(isMember ? 1 : 0);
        o.setAmountCents(amountCents);
        o.setStatus(0);
        weighMapper.insert(o);
        return o;
    }

    @Override
    public FishWeighOrder selectById(Long fishWeighId) { return weighMapper.selectById(fishWeighId); }

    @Override
    public FishWeighOrder selectByWeighNo(String weighNo) { return weighMapper.selectByWeighNo(weighNo); }

    @Override
    @Transactional
    public FishWeighOrder markPaid(String weighNo, String tradeNo)
    {
        FishWeighOrder o = weighMapper.selectByWeighNo(weighNo);
        if (o == null) return null;
        if (o.getStatus() != null && o.getStatus() == 1) return o; // 幂等
        if (o.getStatus() == null || o.getStatus() != 0) return o;

        int g = weighMapper.updateStatusWithGuard(o.getFishWeighId(), 0, 1);
        if (g == 0) return weighMapper.selectById(o.getFishWeighId());

        o.setStatus(1);
        o.setPayTradeNo(tradeNo == null ? "" : tradeNo);
        o.setPaidTime(new Date());
        weighMapper.update(o);
        return o;
    }

    @Override
    @Transactional
    public FishWeighOrder payByBalance(Long userId, Long fishWeighId)
    {
        FishWeighOrder o = weighMapper.selectById(fishWeighId);
        if (o == null) throw new ServiceException("称鱼订单不存在");
        if (!userId.equals(o.getUserId())) throw new ServiceException("无权操作该订单");
        if (o.getStatus() != null && o.getStatus() == 1) return o; // 已支付，幂等

        FishUserBalance balance = balanceService.getBalance(userId);
        int balanceCents = balance == null || balance.getBalanceCents() == null ? 0 : balance.getBalanceCents();
        int amount = o.getAmountCents() == null ? 0 : o.getAmountCents();
        if (balanceCents < amount) throw new ServiceException("余额不足");

        int g = weighMapper.updateStatusWithGuard(o.getFishWeighId(), 0, 1);
        if (g == 0) return weighMapper.selectById(o.getFishWeighId());

        balanceService.applyDelta(userId, -amount, FishBalanceLog.TYPE_CONSUME_FISHING,
                o.getWeighNo(), "称鱼结算", "system");

        o.setStatus(1);
        o.setPayTradeNo("BALANCE");
        o.setPaidTime(new Date());
        weighMapper.update(o);
        return o;
    }

    @Override
    public List<FishWeighOrder> selectByUser(Long userId) { return weighMapper.selectByUser(userId); }

    @Override
    public List<FishWeighOrder> selectList(FishWeighOrder q) { return weighMapper.selectList(q); }
}
