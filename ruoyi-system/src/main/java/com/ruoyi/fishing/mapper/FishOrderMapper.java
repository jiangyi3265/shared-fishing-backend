package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishOrder;

public interface FishOrderMapper
{
    public FishOrder selectFishOrderByOrderId(Long orderId);
    public FishOrder selectFishOrderByOrderNo(@Param("orderNo") String orderNo);
    public FishOrder selectRunningOrder(@Param("userId") Long userId);
    public FishOrder selectPendingOrder(@Param("userId") Long userId);
    public List<FishOrder> selectFishOrderList(FishOrder order);
    public List<FishOrder> selectOrdersByUser(@Param("userId") Long userId);
    public int insertFishOrder(FishOrder order);
    public int updateFishOrder(FishOrder order);
    public int updateOrderStatusWithGuard(@Param("orderId") Long orderId,
                                          @Param("expectedStatus") Integer expectedStatus,
                                          @Param("newStatus") Integer newStatus);
    public int deleteFishOrderByOrderId(Long orderId);
    public int deleteFishOrderByOrderIds(Long[] orderIds);
}
