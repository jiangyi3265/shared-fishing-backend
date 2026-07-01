package com.ruoyi.fishing.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishPointsGoods;
import com.ruoyi.fishing.domain.FishPointsExchange;

public interface FishPointsMapper
{
    /** 积分榜：按积分余额排名 */
    List<Map<String, Object>> selectPointsRanking();

    // 积分商品
    FishPointsGoods selectGoodsById(Long goodsId);
    List<FishPointsGoods> selectGoodsList(FishPointsGoods query);
    List<FishPointsGoods> selectGoodsActive();
    int insertGoods(FishPointsGoods goods);
    int updateGoods(FishPointsGoods goods);
    int deleteGoodsByIds(Long[] ids);
    int decreaseGoodsStock(@Param("goodsId") Long goodsId, @Param("qty") int qty);

    // 兑换记录
    FishPointsExchange selectExchangeById(Long exchangeId);
    List<FishPointsExchange> selectExchangeList(FishPointsExchange query);
    List<FishPointsExchange> selectExchangeByUser(Long userId);
    int insertExchange(FishPointsExchange ex);
    int updateExchangeStatus(@Param("exchangeId") Long exchangeId, @Param("status") int status);

    // 用户积分
    int addPoints(@Param("userId") Long userId, @Param("delta") int delta);
    Integer selectUserPoints(Long userId);

    // 积分流水
    int insertPointsLog(@Param("userId") Long userId, @Param("delta") int delta,
                        @Param("pointsAfter") int pointsAfter, @Param("type") String type,
                        @Param("relatedId") String relatedId, @Param("remark") String remark);

    // 签到
    int insertCheckin(@Param("userId") Long userId, @Param("checkinDate") Date checkinDate, @Param("points") int points);
    int countCheckin(@Param("userId") Long userId, @Param("checkinDate") Date checkinDate);
    int countConsecutiveCheckin(@Param("userId") Long userId, @Param("startDate") Date startDate);
    List<String> selectCheckinDays(@Param("userId") Long userId, @Param("month") String month);
    List<String> selectRecentCheckinDays(@Param("userId") Long userId);
}
