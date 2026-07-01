package com.ruoyi.fishing.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.fishing.domain.FishWeighOrder;

/**
 * 称鱼结算 Service
 */
public interface IFishWeighService
{
    /** 钓王榜：按当月累计钓获重量排名（venueId 为空则全场） */
    List<Map<String, Object>> selectWeightRanking(Long venueId);

    /** 默认路人鱼获单价(分/斤) */
    int DEFAULT_PRICE_CENTS = 1180;
    /** 默认会员鱼获单价(分/斤) */
    int DEFAULT_MEMBER_PRICE_CENTS = 980;

    /** 返回 [路人单价, 会员单价]（分/斤），无配置时取默认 */
    int[] getPrices(Long venueId);

    /** 创建称鱼订单：服务端按重量×单价重新计算金额，不信任前端金额 */
    FishWeighOrder createOrder(Long userId, Long venueId, int weightGrams, boolean isMember);

    FishWeighOrder selectById(Long fishWeighId);

    FishWeighOrder selectByWeighNo(String weighNo);

    /** 标记已支付（微信回调 / mock）。幂等。 */
    FishWeighOrder markPaid(String weighNo, String tradeNo);

    /** 余额全额支付：扣余额并标记完成。 */
    FishWeighOrder payByBalance(Long userId, Long fishWeighId);

    List<FishWeighOrder> selectByUser(Long userId);

    List<FishWeighOrder> selectList(FishWeighOrder q);
}
