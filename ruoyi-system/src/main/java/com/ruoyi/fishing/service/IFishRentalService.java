package com.ruoyi.fishing.service;

import java.util.List;
import com.ruoyi.fishing.domain.FishRentalGoods;
import com.ruoyi.fishing.domain.FishRentalOrder;

public interface IFishRentalService
{
    // 装备
    FishRentalGoods selectGoodsById(Long goodsId);
    List<FishRentalGoods> selectGoodsList(FishRentalGoods query);
    List<FishRentalGoods> selectGoodsAvailable();
    int insertGoods(FishRentalGoods goods);
    int updateGoods(FishRentalGoods goods);
    int deleteGoodsByIds(Long[] ids);

    // 租赁订单
    List<FishRentalOrder> selectOrderList(FishRentalOrder query);
    List<FishRentalOrder> selectOrdersByUser(Long userId);
    FishRentalOrder rent(Long userId, Long goodsId);
    int confirmReturn(Long orderId);
    int forfeitDeposit(Long orderId, String remark);
}
