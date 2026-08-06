package com.ruoyi.fishing.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.fishing.domain.FishPointsGoods;
import com.ruoyi.fishing.domain.FishPointsExchange;
import com.ruoyi.fishing.domain.FishPointsReward;

public interface IFishPointsService
{
    // 积分商品 CRUD
    FishPointsGoods selectGoodsById(Long goodsId);
    List<FishPointsGoods> selectGoodsList(FishPointsGoods query);
    List<FishPointsGoods> selectGoodsActive();
    int insertGoods(FishPointsGoods goods);
    int updateGoods(FishPointsGoods goods);
    int deleteGoodsByIds(Long[] ids);

    // 兑换
    List<FishPointsExchange> selectExchangeList(FishPointsExchange query);
    List<FishPointsExchange> selectExchangeByUser(Long userId);
    FishPointsExchange exchange(Long userId, Long goodsId);
    int deliverExchange(Long exchangeId);

    // 积分榜
    List<Map<String, Object>> selectPointsRanking();

    // 积分操作
    int getUserPoints(Long userId);
    void addPoints(Long userId, int delta, String type, String relatedId, String remark);

    // 签到
    Map<String, Object> checkin(Long userId);
    Map<String, Object> checkinCalendar(Long userId, String month);

    // 线上消费赠积分（每实付1元=5积分，支付成功后生成，用户主动领取）
    FishPointsReward prepareConsumeReward(Long userId, int amountCents, String sourceType, String sourceNo);
    FishPointsReward getConsumeReward(Long userId, String sourceNo);
    Map<String, Object> claimConsumeReward(Long userId, String sourceNo);
}
