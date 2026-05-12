package com.ruoyi.fishing.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.fishing.domain.FishMallCategory;
import com.ruoyi.fishing.domain.FishMallGoods;
import com.ruoyi.fishing.domain.FishMallOrder;

/**
 * 商城统一 Service：分类 / 商品 / 订单。
 * 单一服务避免多文件，订单逻辑较重时再拆。
 */
public interface IFishMallService
{
    // 分类
    List<FishMallCategory> listCategory(FishMallCategory q);
    FishMallCategory getCategory(Long catId);
    int saveCategory(FishMallCategory cat);
    int updateCategory(FishMallCategory cat);
    int deleteCategories(Long[] catIds);

    // 商品
    List<FishMallGoods> listGoods(FishMallGoods q);
    List<FishMallGoods> listActiveGoods(Long catId);
    FishMallGoods getGoods(Long goodsId);
    int saveGoods(FishMallGoods g);
    int updateGoods(FishMallGoods g);
    int toggleGoodsStatus(Long goodsId, String status);
    int deleteGoods(Long[] goodsIds);

    // 订单
    /**
     * 提交订单：扣库存、生成订单+明细、生成核销码。
     * items: List<Map<goodsId, qty>>。
     * 默认状态 0 待支付（mock 模式可由 controller 调 markPaid 直接置 1）。
     */
    FishMallOrder submitOrder(Long userId, List<Map<String, Object>> items, String remark, Long venueId);

    /**
     * 提交订单（支持余额抵扣）：useBalance=true 时优先扣余额，不足部分由微信支付。
     * 余额在订单提交时以 balance_cents 记录（冻结语义），正式扣减发生在 markPaid。
     */
    FishMallOrder submitOrder(Long userId, List<Map<String, Object>> items, String remark, Long venueId, boolean useBalance);

    /** 标记已支付：0 → 1 待核销 */
    FishMallOrder markPaid(String orderNo, String tradeNo);

    /** 后台核销：1 → 2 已核销，按 orderNo 或 redeemCode */
    FishMallOrder redeem(String orderNoOrCode, String operator);

    /** 后台取消（仅待支付可取消，回滚库存） */
    FishMallOrder cancel(Long mallOrderId);

    /**
     * 扫描待支付超过 timeoutMinutes 分钟的商城订单，全部取消并回滚库存。
     * 仅处理未被钓场合并支付占用的商城订单。
     * @return 成功取消的订单数
     */
    int autoCancelTimeoutOrders(int timeoutMinutes);

    List<FishMallOrder> listOrder(FishMallOrder q);
    FishMallOrder getOrder(Long mallOrderId);
    List<FishMallOrder> listMyOrders(Long userId);
}
