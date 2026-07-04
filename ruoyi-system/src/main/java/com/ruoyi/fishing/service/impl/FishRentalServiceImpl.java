package com.ruoyi.fishing.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishRentalGoods;
import com.ruoyi.fishing.domain.FishRentalOrder;
import com.ruoyi.fishing.domain.FishUserBalance;
import com.ruoyi.fishing.mapper.FishRentalMapper;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishRentalService;
import com.ruoyi.fishing.service.IFishUserService;

@Service
public class FishRentalServiceImpl implements IFishRentalService
{
    @Autowired private FishRentalMapper mapper;
    @Autowired private IFishUserService userService;
    @Autowired private IFishBalanceService balanceService;

    @Override public FishRentalGoods selectGoodsById(Long id) { return mapper.selectGoodsById(id); }
    @Override public List<FishRentalGoods> selectGoodsList(FishRentalGoods q) { return mapper.selectGoodsList(q); }
    @Override public List<FishRentalGoods> selectGoodsAvailable() { return mapper.selectGoodsAvailable(); }
    @Override public int insertGoods(FishRentalGoods g) { if(g.getStatus()==null)g.setStatus("0"); return mapper.insertGoods(g); }
    @Override public int updateGoods(FishRentalGoods g) { return mapper.updateGoods(g); }
    @Override public int deleteGoodsByIds(Long[] ids) { return mapper.deleteGoodsByIds(ids); }

    @Override public List<FishRentalOrder> selectOrderList(FishRentalOrder q) { return mapper.selectOrderList(q); }
    @Override public List<FishRentalOrder> selectOrdersByUser(Long userId) { return mapper.selectOrdersByUser(userId); }

    @Override
    @Transactional
    public FishRentalOrder rent(Long userId, Long goodsId) {
        userService.assertNotBlacklisted(userId);
        FishRentalGoods g = mapper.selectGoodsById(goodsId);
        if (g == null || !"0".equals(g.getStatus())) throw new ServiceException("装备不存在或已下架");
        int totalCharge = (g.getDepositCents() == null ? 0 : g.getDepositCents())
                        + (g.getRentCents() == null ? 0 : g.getRentCents());
        if (totalCharge > 0) {
            FishUserBalance balance = balanceService.getBalance(userId);
            int available = balance == null || balance.getBalanceCents() == null ? 0 : balance.getBalanceCents();
            if (available < totalCharge) {
                int missing = totalCharge - available;
                throw new ServiceException("储值余额不足：租借「" + g.getName() + "」需 ¥"
                        + formatCents(totalCharge) + "，当前余额 ¥" + formatCents(available)
                        + "，还差 ¥" + formatCents(missing) + "。请先充值后再租借。");
            }
        }
        int dec = mapper.decreaseStock(goodsId);
        if (dec == 0) throw new ServiceException("库存不足");

        FishRentalOrder o = new FishRentalOrder();
        o.setOrderNo("L" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        o.setUserId(userId);
        o.setGoodsId(goodsId);
        o.setGoodsName(g.getName());
        o.setDepositCents(g.getDepositCents());
        o.setRentCents(g.getRentCents());
        o.setRentTime(new Date());
        mapper.insertOrder(o);

        // 从余额扣取押金+租金
        if (totalCharge > 0) {
            balanceService.applyDelta(userId, -totalCharge, "consume_fishing",
                    o.getOrderNo(), "装备租赁(" + g.getName() + ")", "system");
        }
        return o;
    }

    @Override
    @Transactional
    public int confirmReturn(Long orderId) {
        FishRentalOrder order = mapper.selectOrderById(orderId);
        if (order == null) return 0;
        mapper.updateOrderReturn(orderId);
        mapper.increaseStock(order.getGoodsId());
        // 退还押金到余额
        int deposit = order.getDepositCents() == null ? 0 : order.getDepositCents();
        if (deposit > 0) {
            balanceService.applyDelta(order.getUserId(), deposit, "refund",
                    order.getOrderNo(), "装备归还退押金", "system");
        }
        return 1;
    }

    @Override
    public int forfeitDeposit(Long orderId, String remark) {
        return mapper.updateOrderStatus(orderId, 3);
    }

    private String formatCents(int cents) {
        int abs = Math.abs(cents);
        return (cents < 0 ? "-" : "") + (abs / 100) + "." + String.format("%02d", abs % 100);
    }
}
