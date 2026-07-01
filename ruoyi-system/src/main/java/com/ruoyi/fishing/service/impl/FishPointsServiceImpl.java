package com.ruoyi.fishing.service.impl;

import java.util.*;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishPointsExchange;
import com.ruoyi.fishing.domain.FishPointsGoods;
import com.ruoyi.fishing.mapper.FishPointsMapper;
import com.ruoyi.fishing.service.IFishPointsService;

@Service
public class FishPointsServiceImpl implements IFishPointsService
{
    private static final int BASE_CHECKIN_POINTS = 5;
    private static final int BONUS_3DAY = 5;
    private static final int BONUS_7DAY = 20;
    private static final int BONUS_14DAY = 50;
    private static final int BONUS_30DAY = 150;

    @Autowired
    private FishPointsMapper mapper;

    @Override public FishPointsGoods selectGoodsById(Long id) { return mapper.selectGoodsById(id); }
    @Override public List<FishPointsGoods> selectGoodsList(FishPointsGoods q) { return mapper.selectGoodsList(q); }
    @Override public List<FishPointsGoods> selectGoodsActive() { return mapper.selectGoodsActive(); }
    @Override public int insertGoods(FishPointsGoods g) { if (g.getStatus() == null) g.setStatus("0"); return mapper.insertGoods(g); }
    @Override public int updateGoods(FishPointsGoods g) { return mapper.updateGoods(g); }
    @Override public int deleteGoodsByIds(Long[] ids) { return mapper.deleteGoodsByIds(ids); }

    @Override public List<FishPointsExchange> selectExchangeList(FishPointsExchange q) { return mapper.selectExchangeList(q); }
    @Override public List<FishPointsExchange> selectExchangeByUser(Long userId) { return mapper.selectExchangeByUser(userId); }

    @Override public List<Map<String, Object>> selectPointsRanking() { return mapper.selectPointsRanking(); }

    @Override
    public int getUserPoints(Long userId) {
        Integer p = mapper.selectUserPoints(userId);
        return p != null ? p : 0;
    }

    @Override
    @Transactional
    public void addPoints(Long userId, int delta, String type, String relatedId, String remark) {
        int rows = mapper.addPoints(userId, delta);
        if (rows == 0 && delta < 0) throw new ServiceException("积分不足");
        int after = getUserPoints(userId);
        mapper.insertPointsLog(userId, delta, after, type, relatedId == null ? "" : relatedId, remark == null ? "" : remark);
    }

    @Override
    @Transactional
    public FishPointsExchange exchange(Long userId, Long goodsId) {
        FishPointsGoods g = mapper.selectGoodsById(goodsId);
        if (g == null || !"0".equals(g.getStatus())) throw new ServiceException("商品不存在或已下架");

        int points = getUserPoints(userId);
        if (points < g.getPointsCost()) throw new ServiceException("积分不足");

        int dec = mapper.decreaseGoodsStock(goodsId, 1);
        if (dec == 0) throw new ServiceException("库存不足");

        addPoints(userId, -g.getPointsCost(), "exchange", goodsId.toString(), "兑换:" + g.getName());

        FishPointsExchange ex = new FishPointsExchange();
        ex.setUserId(userId);
        ex.setGoodsId(goodsId);
        ex.setGoodsName(g.getName());
        ex.setPointsCost(g.getPointsCost());
        ex.setStatus(0);
        mapper.insertExchange(ex);
        return ex;
    }

    @Override
    public int deliverExchange(Long exchangeId) {
        return mapper.updateExchangeStatus(exchangeId, 1);
    }

    @Override
    @Transactional
    public Map<String, Object> checkin(Long userId) {
        Date today = new Date();
        int already = mapper.countCheckin(userId, today);
        if (already > 0) throw new ServiceException("今日已签到");

        int consecutive = calcConsecutiveDays(userId) + 1;

        int earned = BASE_CHECKIN_POINTS;
        if (consecutive >= 30) earned += BONUS_30DAY;
        else if (consecutive >= 14) earned += BONUS_14DAY;
        else if (consecutive >= 7) earned += BONUS_7DAY;
        else if (consecutive >= 3) earned += BONUS_3DAY;

        mapper.insertCheckin(userId, today, earned);
        addPoints(userId, earned, "checkin", "", "签到+" + earned);

        Map<String, Object> result = new HashMap<>();
        result.put("earned", earned);
        result.put("consecutive", consecutive);
        result.put("totalPoints", getUserPoints(userId));
        return result;
    }

    @Override
    public Map<String, Object> checkinCalendar(Long userId, String month) {
        if (month == null || month.isEmpty()) {
            month = new java.text.SimpleDateFormat("yyyy-MM").format(new Date());
        }
        List<String> days = mapper.selectCheckinDays(userId, month);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -29);
        int consecutive = mapper.countConsecutiveCheckin(userId, cal.getTime());

        Date today = new Date();
        boolean todayChecked = mapper.countCheckin(userId, today) > 0;

        Map<String, Object> result = new HashMap<>();
        result.put("days", days);
        result.put("consecutive", consecutive);
        result.put("todayChecked", todayChecked);
        result.put("totalPoints", getUserPoints(userId));
        return result;
    }

    @Override
    @Transactional
    public void grantConsumePoints(Long userId, int amountCents, String orderNo) {
        int pts = amountCents / 100;
        if (pts > 0) {
            addPoints(userId, pts, "consume", orderNo, "消费赠送");
        }
    }

    private int calcConsecutiveDays(Long userId) {
        List<String> days = mapper.selectRecentCheckinDays(userId);
        if (days == null || days.isEmpty()) return 0;
        int count = 0;
        LocalDate expected = LocalDate.now().minusDays(1);
        for (String day : days) {
            if (LocalDate.parse(day).equals(expected)) {
                count++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return count;
    }
}
