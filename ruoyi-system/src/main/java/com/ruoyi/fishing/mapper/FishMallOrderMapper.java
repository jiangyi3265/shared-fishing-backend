package com.ruoyi.fishing.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishMallOrderItem;

public interface FishMallOrderMapper
{
    FishMallOrder selectById(Long mallOrderId);
    FishMallOrder selectByOrderNo(@Param("orderNo") String orderNo);
    FishMallOrder selectByRedeemCode(@Param("code") String code);
    List<FishMallOrder> selectList(FishMallOrder o);
    List<FishMallOrder> selectByUser(@Param("userId") Long userId);
    /** 扫描待支付超过 threshold 的订单（未被合并支付占用） */
    List<FishMallOrder> selectTimeoutUnpaidOrders(@Param("threshold") Date threshold);
    int insert(FishMallOrder o);
    int update(FishMallOrder o);
    int updateStatusWithGuard(@Param("mallOrderId") Long mallOrderId,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("newStatus") Integer newStatus);

    int insertItem(FishMallOrderItem item);
    List<FishMallOrderItem> selectItemsByOrderId(@Param("mallOrderId") Long mallOrderId);
}
