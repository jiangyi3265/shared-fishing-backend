package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishRentalGoods;
import com.ruoyi.fishing.domain.FishRentalOrder;

public interface FishRentalMapper
{
    FishRentalGoods selectGoodsById(Long goodsId);
    List<FishRentalGoods> selectGoodsList(FishRentalGoods query);
    List<FishRentalGoods> selectGoodsAvailable();
    int insertGoods(FishRentalGoods goods);
    int updateGoods(FishRentalGoods goods);
    int deleteGoodsByIds(Long[] ids);
    int decreaseStock(@Param("goodsId") Long goodsId);
    int increaseStock(@Param("goodsId") Long goodsId);

    List<FishRentalOrder> selectOrderList(FishRentalOrder query);
    List<FishRentalOrder> selectOrdersByUser(Long userId);
    FishRentalOrder selectOrderById(@Param("orderId") Long orderId);
    int insertOrder(FishRentalOrder order);
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") int status);
    int updateOrderReturn(@Param("orderId") Long orderId);
}
