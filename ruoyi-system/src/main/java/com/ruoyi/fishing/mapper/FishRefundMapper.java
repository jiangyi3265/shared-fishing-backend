package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishRefund;

public interface FishRefundMapper
{
    FishRefund selectFishRefundByRefundId(Long refundId);
    FishRefund selectFishRefundByRefundNo(@Param("refundNo") String refundNo);
    /** 一个订单的活动中退款（status in 0,1） */
    FishRefund selectActiveRefundByOrderId(@Param("orderId") Long orderId,
                                           @Param("orderType") String orderType);
    Integer sumRefundAmountByOrder(@Param("orderId") Long orderId,
                                   @Param("orderType") String orderType);
    List<FishRefund> selectFishRefundList(FishRefund refund);
    List<FishRefund> selectByUserId(@Param("userId") Long userId);
    int insertFishRefund(FishRefund refund);
    int updateFishRefund(FishRefund refund);
    int updateStatusWithGuard(@Param("refundId") Long refundId,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("newStatus") Integer newStatus);
    int deleteFishRefundByRefundIds(Long[] refundIds);
}
