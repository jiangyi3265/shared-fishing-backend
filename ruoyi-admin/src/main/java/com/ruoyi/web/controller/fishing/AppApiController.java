package com.ruoyi.web.controller.fishing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishAd;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.domain.FishQrcode;
import com.ruoyi.fishing.domain.FishRegistration;
import com.ruoyi.fishing.domain.FishUser;
import com.ruoyi.fishing.domain.FishUserCoupon;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.domain.FishWeighOrder;
import com.ruoyi.fishing.service.IFishWeighService;
import com.ruoyi.fishing.mapper.FishQrcodeMapper;
import com.ruoyi.fishing.service.AppTokenService;
import com.ruoyi.fishing.service.IFishAdService;
import com.ruoyi.fishing.service.IFishBillingRuleService;
import com.ruoyi.fishing.service.IFishCouponService;
import com.ruoyi.fishing.domain.FishMallGoods;
import com.ruoyi.fishing.domain.FishMallOrder;
import com.ruoyi.fishing.domain.FishRechargeOrder;
import com.ruoyi.fishing.service.IFishBalanceService;
import com.ruoyi.fishing.service.IFishMallService;
import com.ruoyi.fishing.service.IFishOrderService;
import com.ruoyi.fishing.service.IFishRefundService;
import com.ruoyi.fishing.service.IFishRegistrationService;
import com.ruoyi.fishing.service.IFishStockingService;
import com.ruoyi.fishing.service.IFishSpotService;
import com.ruoyi.fishing.service.IFishCatchService;
import com.ruoyi.fishing.service.IFishMemberLevelService;
import com.ruoyi.fishing.service.IWeatherService;
import com.ruoyi.fishing.service.IFishPointsService;
import com.ruoyi.fishing.service.IFishGroupService;
import com.ruoyi.fishing.service.IFishRentalService;
import com.ruoyi.fishing.service.IFishCompetitionService;
import com.ruoyi.fishing.service.IFishUserService;
import com.ruoyi.fishing.service.IFishVenueService;
import com.ruoyi.fishing.service.IWxAuthService;
import com.ruoyi.fishing.service.IWxPayService;

/**
 * 小程序端开放接口
 */
@Anonymous
@RestController
@RequestMapping("/app")
public class AppApiController
{
    @Autowired
    private IFishUserService userService;
    @Autowired
    private IFishVenueService venueService;
    @Autowired
    private IFishBillingRuleService ruleService;
    @Autowired
    private IFishOrderService orderService;
    @Autowired
    private IFishAdService adService;
    @Autowired
    private IFishRegistrationService regService;
    @Autowired
    private IFishCouponService couponService;
    @Autowired
    private IWxAuthService wxAuthService;
    @Autowired
    private IWxPayService wxPayService;
    @Autowired
    private IFishRefundService refundService;
    @Autowired
    private IFishMallService mallService;
    @Autowired
    private IFishBalanceService balanceService;
    @Autowired
    private IFishStockingService stockingService;
    @Autowired
    private IFishSpotService spotService;
    @Autowired
    private IFishCatchService catchService;
    @Autowired
    private IFishMemberLevelService memberLevelService;
    @Autowired
    private IWeatherService weatherService;
    @Autowired
    private IFishPointsService pointsService;
    @Autowired
    private IFishGroupService groupService;
    @Autowired
    private IFishRentalService rentalService;
    @Autowired
    private IFishCompetitionService competitionService;
    @Autowired
    private IFishWeighService weighService;
    @Autowired
    private FishQrcodeMapper qrcodeMapper;
    @Autowired
    private AppTokenService appTokenService;
    @Autowired
    private HttpServletRequest request;

    /** 小程序登录：前端传 code（uni.login 获得），后端走 code2session 换 openid */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> body)
    {
        String code = body.get("code");
        String openid;
        if (code == null || code.isEmpty()) return AjaxResult.error("缺少微信登录 code");
        openid = wxAuthService.resolveOpenid(code);
        FishUser u = userService.loginOrRegister(openid, body.get("nickname"), body.get("avatar"));
        Map<String, Object> data = new HashMap<>();
        data.put("userId", u.getUserId());
        data.put("user", u);
        data.put("token", appTokenService.createToken(u.getUserId()));
        return AjaxResult.success(data);
    }

    /** 默认钓场与规则（小程序首页/开始页使用） */
    @Anonymous
    @GetMapping("/venue/default")
    public AjaxResult defaultVenue()
    {
        List<FishVenue> venues = venueService.selectFishVenueList(new FishVenue());
        if (venues.isEmpty()) return AjaxResult.success(null);
        FishVenue v = venues.get(0);
        Map<String, Object> data = new HashMap<>();
        data.put("venue", v);
        if (v.getRuleId() != null) data.put("rule", ruleService.selectFishBillingRuleByRuleId(v.getRuleId()));
        return AjaxResult.success(data);
    }

    @Anonymous
    @GetMapping("/venue/{venueId}")
    public AjaxResult venue(@PathVariable Long venueId)
    {
        FishVenue v = venueService.selectFishVenueByVenueId(venueId);
        if (v == null) return AjaxResult.error("钓场不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("venue", v);
        if (v.getRuleId() != null) data.put("rule", ruleService.selectFishBillingRuleByRuleId(v.getRuleId()));
        return AjaxResult.success(data);
    }

    /** 放鱼记录（按钓场） */
    @Anonymous
    @GetMapping("/stocking/list")
    public AjaxResult stockingList(@RequestParam(required = false) Long venueId)
    {
        return AjaxResult.success(stockingService.selectPublicByVenue(venueId));
    }

    /** 天气信息 */
    @Anonymous
    @GetMapping("/weather")
    public AjaxResult weather(@RequestParam(required = false, defaultValue = "116.41,39.92") String location)
    {
        return AjaxResult.success(weatherService.getCurrentWeather(location));
    }

    /** 广告/活动列表 */
    @Anonymous
    @GetMapping("/ad/list")
    public AjaxResult adList()
    {
        FishAd q = new FishAd();
        q.setStatus("0");
        return AjaxResult.success(adService.selectFishAdList(q));
    }

    @Anonymous
    @GetMapping("/ad/{adId}")
    public AjaxResult adDetail(@PathVariable Long adId)
    {
        return AjaxResult.success(adService.selectFishAdByAdId(adId));
    }

    /** 开始计时 */
    @Anonymous
    @PostMapping("/order/start")
    public AjaxResult startOrder(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long bodyUserId = parseLong(body.get("userId"));
        if (bodyUserId != null && !bodyUserId.equals(userId)) return unauthorized();
        FishQrcode qr = requireScanQrcode(body, "start");
        Long venueId = parseLong(body.get("venueId"));
        if (venueId != null && qr.getVenueId() != null && !venueId.equals(qr.getVenueId()))
        {
            return AjaxResult.error("入口码与当前钓场不匹配");
        }
        venueId = qr.getVenueId();
        if (venueId == null) return AjaxResult.error("入口码未绑定钓场");
        return AjaxResult.success(orderService.startOrder(userId, venueId));
    }

    /** 查询进行中订单（带实时费用） */
    @Anonymous
    @GetMapping("/order/running")
    public AjaxResult running(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(orderService.estimateRunning(userId));
    }

    /** 查询待结算订单 */
    @Anonymous
    @GetMapping("/order/pending")
    public AjaxResult pending(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(orderService.selectPendingOrder(userId));
    }

    /** 结束计时 */
    @Anonymous
    @PostMapping("/order/finish")
    public AjaxResult finishOrder(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long bodyUserId = parseLong(body.get("userId"));
        if (bodyUserId != null && !bodyUserId.equals(userId)) return unauthorized();
        FishQrcode qr = requireScanQrcode(body, "end");
        FishOrder running = orderService.selectRunningOrder(userId);
        if (running == null) return AjaxResult.error("未检测到进行中的订单");
        if (qr.getVenueId() != null && running.getVenueId() != null && !qr.getVenueId().equals(running.getVenueId()))
        {
            return AjaxResult.error("出口码与当前订单钓场不匹配");
        }
        return AjaxResult.success(orderService.finishOrder(userId));
    }

    /** 支付（支持带 mallOrderIds 一并合并支付；支持 useBalance 余额抵扣） */
    @Anonymous
    @PostMapping("/order/pay")
    public AjaxResult pay(@RequestBody Map<String, Object> body)
    {
        Long userId = parseLong(body.get("userId"));
        Long orderId = parseLong(body.get("orderId"));
        Long couponId = parseLong(body.get("couponId"));
        boolean useBalance = Boolean.TRUE.equals(body.get("useBalance"))
                || "true".equalsIgnoreCase(String.valueOf(body.get("useBalance")));
        if (userId == null || orderId == null) return AjaxResult.error("参数缺失");
        if (!isCurrentUser(userId)) return unauthorized();

        java.util.List<Long> mallOrderIds = new java.util.ArrayList<>();
        Object raw = body.get("mallOrderIds");
        if (raw instanceof java.util.List)
        {
            for (Object o : (java.util.List<?>) raw)
            {
                Long mid = parseLong(o);
                if (mid != null) mallOrderIds.add(mid);
            }
        }

        FishOrder order = orderService.selectFishOrderByOrderId(orderId);
        if (order == null) return AjaxResult.error("订单不存在");
        if (!userId.equals(order.getUserId())) return unauthorized();

        if (wxPayService.isEnabled())
        {
            FishUser user = userService.selectFishUserByUserId(userId);
            if (user == null) return AjaxResult.error("用户不存在");
            FishOrder prepared = orderService.preparePayment(userId, orderId, couponId, mallOrderIds, useBalance);
            int finalAmount = prepared.getAmountPaid() == null ? 0 : prepared.getAmountPaid();
            if (finalAmount <= 0)
            {
                // 全额由余额 + 优惠券覆盖，直接走 markPaid（内部会扣余额）
                orderService.markPaid(prepared.getOrderNo(), "ZERO_PAY");
                Map<String, Object> data = new HashMap<>();
                data.put("order", orderService.selectFishOrderByOrderId(orderId));
                data.put("needWxPay", false);
                return AjaxResult.success(data);
            }
            Map<String, Object> prepay = wxPayService.createPrepay(prepared.getOrderNo(), finalAmount, user.getOpenid(),
                    "共享钓场 · " + prepared.getOrderNo());
            Map<String, Object> data = new HashMap<>();
            data.put("order", prepared);
            data.put("pay", prepay);
            data.put("needWxPay", true);
            return AjaxResult.success(data);
        }

        if (!wxPayService.isMockEnabled()) return AjaxResult.error("微信支付未配置");
        FishOrder paid = orderService.pay(userId, orderId, couponId, mallOrderIds, useBalance);
        Map<String, Object> data = new HashMap<>();
        data.put("order", paid);
        data.put("needWxPay", false);
        return AjaxResult.success(data);
    }

    /** 微信支付异步通知（按订单号前缀分发：FD=钓场计时；M=商城） */
    @Anonymous
    @PostMapping("/order/pay/notify")
    public Map<String, Object> payNotify(HttpServletRequest request, @RequestBody String body)
    {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) { String n = names.nextElement(); headers.put(n, request.getHeader(n)); }
        Map<String, Object> res = new HashMap<>();
        try {
            IWxPayService.PayCallback cb = wxPayService.handleNotify(body, headers);
            String orderNo = cb == null ? null : cb.orderNo;
            String tradeNo = cb == null ? "" : cb.transactionId;
            if (orderNo != null)
            {
                if (orderNo.startsWith("R")) balanceService.markRechargePaid(orderNo, tradeNo);
                else if (orderNo.startsWith("M")) mallService.markPaid(orderNo, tradeNo);
                else if (orderNo.startsWith("W")) weighService.markPaid(orderNo, tradeNo);
                else if (orderNo.startsWith("A")) regService.pay(parseLong(orderNo.substring(1)));
                else orderService.markPaid(orderNo, tradeNo);
            }
            res.put("code", "SUCCESS"); res.put("message", "成功");
        } catch (Exception e) {
            res.put("code", "FAIL"); res.put("message", "处理失败");
        }
        return res;
    }

    /** 扫码落地：解析 scene_value / qrId 后返回 action + venueId */
    @Anonymous
    @GetMapping("/qrcode/resolve")
    public AjaxResult resolveQrcode(@RequestParam(required = false) Long qrId,
                                    @RequestParam(required = false) String scene)
    {
        FishQrcode qr = null;
        if (qrId != null) qr = qrcodeMapper.selectFishQrcodeByQrId(qrId);
        if (qr == null && scene != null && !scene.isEmpty()) {
            FishQrcode query = new FishQrcode();
            query.setSceneValue(scene);
            List<FishQrcode> list = qrcodeMapper.selectFishQrcodeList(query);
            if (!list.isEmpty()) qr = list.get(0);
        }
        if (qr == null) return AjaxResult.error("二维码无效");
        if ("1".equals(qr.getStatus())) return AjaxResult.error("二维码已停用");
        Map<String, Object> data = new HashMap<>();
        data.put("qrId", qr.getQrId());
        data.put("venueId", qr.getVenueId());
        data.put("action", "end".equals(qr.getQrType()) ? "end" : "start");
        return AjaxResult.success(data);
    }

    /** 历史订单 */
    @Anonymous
    @GetMapping("/order/list")
    public AjaxResult orderList(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(orderService.selectOrdersByUser(userId));
    }

    /** 订单详情 */
    @Anonymous
    @GetMapping("/order/{orderId}")
    public AjaxResult orderDetail(@PathVariable Long orderId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        FishOrder order = orderService.selectFishOrderByOrderId(orderId);
        if (order == null) return AjaxResult.error("订单不存在");
        if (!userId.equals(order.getUserId())) return unauthorized();
        return AjaxResult.success(order);
    }

    /** 活动报名 */
    @Anonymous
    @PostMapping("/registration/submit")
    public AjaxResult submitReg(@RequestBody Map<String, Object> body)
    {
        Long userId = parseLong(body.get("userId"));
        Long adId = parseLong(body.get("adId"));
        String name = (String) body.get("name");
        String phone = (String) body.get("phone");
        String remark = (String) body.get("remark");
        if (userId == null || adId == null) return AjaxResult.error("参数缺失");
        if (!isCurrentUser(userId)) return unauthorized();
        FishRegistration r = regService.submit(adId, userId, name, phone, remark);
        return AjaxResult.success(r);
    }

    /** 报名支付 */
    @Anonymous
    @PostMapping("/registration/pay")
    public AjaxResult payReg(@RequestBody Map<String, Object> body)
    {
        Long regId = parseLong(body.get("regId"));
        if (regId == null) return AjaxResult.error("参数缺失");
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        FishRegistration reg = regService.selectFishRegistrationByRegId(regId);
        if (reg == null) return AjaxResult.error("报名不存在");
        if (!userId.equals(reg.getUserId())) return unauthorized();
        int fee = reg.getFeeCents() == null ? 0 : reg.getFeeCents();
        if (reg.getPaid() != null && reg.getPaid() == 1)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("order", reg);
            data.put("needWxPay", false);
            return AjaxResult.success(data);
        }
        if (fee <= 0)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("order", regService.pay(regId));
            data.put("needWxPay", false);
            return AjaxResult.success(data);
        }
        if (wxPayService.isEnabled())
        {
            FishUser user = userService.selectFishUserByUserId(userId);
            if (user == null) return AjaxResult.error("用户不存在");
            Map<String, Object> prepay = wxPayService.createPrepay("A" + regId, fee, user.getOpenid(),
                    "活动报名 · " + regId);
            Map<String, Object> data = new HashMap<>();
            data.put("order", reg);
            data.put("pay", prepay);
            data.put("needWxPay", true);
            return AjaxResult.success(data);
        }
        if (!wxPayService.isMockEnabled()) return AjaxResult.error("微信支付未配置");
        Map<String, Object> data = new HashMap<>();
        data.put("order", regService.pay(regId));
        data.put("needWxPay", false);
        return AjaxResult.success(data);
    }

    /** 我的报名 */
    @Anonymous
    @GetMapping("/registration/my")
    public AjaxResult myReg(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(regService.selectByUserId(userId));
    }

    /** 领取优惠券 */
    @Anonymous
    @PostMapping("/coupon/grant")
    public AjaxResult grantCoupon(@RequestBody Map<String, Object> body)
    {
        Long userId = parseLong(body.get("userId"));
        Long templateId = parseLong(body.get("templateId"));
        String source = (String) body.get("source");
        if (userId == null || templateId == null) return AjaxResult.error("参数缺失");
        if (!isCurrentUser(userId)) return unauthorized();
        FishUserCoupon c = couponService.grantCoupon(userId, templateId, source == null ? "app" : source);
        return AjaxResult.success(c);
    }

    /** 我的优惠券 */
    @Anonymous
    @GetMapping("/coupon/my")
    public AjaxResult myCoupons(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(couponService.selectMyCoupons(userId));
    }

    /** 可用优惠券 */
    @Anonymous
    @GetMapping("/coupon/available")
    public AjaxResult availableCoupons(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(couponService.selectAvailableCoupons(userId));
    }

    /** 申请退款（钓场订单） */
    @Anonymous
    @PostMapping("/refund/apply")
    public AjaxResult applyRefund(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long orderId = parseLong(body.get("orderId"));
        if (orderId == null) return AjaxResult.error("参数缺失");
        Integer amount = null;
        Object a = body.get("applyAmountCents");
        if (a instanceof Number) amount = ((Number) a).intValue();
        else if (a != null) try { amount = Integer.parseInt(a.toString()); } catch (Exception ignore) {}
        String reason = (String) body.get("reason");
        return AjaxResult.success(refundService.applyRefund(userId, orderId, amount, reason));
    }

    /** 申请退款（商城订单） */
    @Anonymous
    @PostMapping("/mall/refund/apply")
    public AjaxResult applyMallRefund(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long mallOrderId = parseLong(body.get("mallOrderId"));
        if (mallOrderId == null) return AjaxResult.error("参数缺失");
        Integer amount = null;
        Object a = body.get("applyAmountCents");
        if (a instanceof Number) amount = ((Number) a).intValue();
        else if (a != null) try { amount = Integer.parseInt(a.toString()); } catch (Exception ignore) {}
        String reason = (String) body.get("reason");
        return AjaxResult.success(refundService.applyMallRefund(userId, mallOrderId, amount, reason));
    }

    /** 我的退款记录 */
    @Anonymous
    @GetMapping("/refund/my")
    public AjaxResult myRefunds(@RequestParam Long userId)
    {
        if (!isCurrentUser(userId)) return unauthorized();
        return AjaxResult.success(refundService.selectByUserId(userId));
    }

    // ===================== 商城 =====================

    @Anonymous
    @GetMapping("/mall/category/list")
    public AjaxResult mallCategories()
    {
        return AjaxResult.success(mallService.listCategory(new com.ruoyi.fishing.domain.FishMallCategory()));
    }

    @Anonymous
    @GetMapping("/mall/goods/list")
    public AjaxResult mallGoods(@RequestParam(required = false) Long catId)
    {
        return AjaxResult.success(mallService.listActiveGoods(catId));
    }

    @Anonymous
    @GetMapping("/mall/goods/{goodsId}")
    public AjaxResult mallGoodsDetail(@PathVariable Long goodsId)
    {
        FishMallGoods g = mallService.getGoods(goodsId);
        if (g == null) return AjaxResult.error("商品不存在");
        return AjaxResult.success(g);
    }

    /** 提交商城订单：扣库存 + 生成订单 + 返回支付参数（mock 模式直接置已支付） */
    @Anonymous
    @PostMapping("/mall/order/submit")
    public AjaxResult submitMallOrder(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        String remark = (String) body.get("remark");
        Long venueId = parseLong(body.get("venueId"));
        boolean useBalance = Boolean.TRUE.equals(body.get("useBalance"))
                || "true".equalsIgnoreCase(String.valueOf(body.get("useBalance")));
        int pointsToUse = 0;
        Object pointsRaw = body.get("pointsToUse");
        if (pointsRaw != null)
        {
            try { pointsToUse = (int) Math.round(Double.parseDouble(String.valueOf(pointsRaw))); }
            catch (Exception ignore) { }
        }
        if (pointsToUse < 0) pointsToUse = 0;

        FishMallOrder order = mallService.submitOrder(userId, items, remark, venueId, useBalance, pointsToUse);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);

        int total = order.getTotalCents() == null ? 0 : order.getTotalCents();
        int balance = order.getBalanceCents() == null ? 0 : order.getBalanceCents();
        int pointsCents = order.getPointsDeductCents() == null ? 0 : order.getPointsDeductCents();
        int wxAmount = Math.max(0, total - pointsCents - balance);

        // 全额免付（0 元 或 余额全覆盖）
        if (wxAmount <= 0)
        {
            mallService.markPaid(order.getMallOrderNo(), "ZERO_PAY");
            data.put("order", mallService.getOrder(order.getMallOrderId()));
            data.put("needWxPay", false);
            return AjaxResult.success(data);
        }

        if (wxPayService.isEnabled())
        {
            FishUser user = userService.selectFishUserByUserId(userId);
            if (user == null) return AjaxResult.error("用户不存在");
            Map<String, Object> prepay = wxPayService.createPrepay(order.getMallOrderNo(), wxAmount,
                    user.getOpenid(), "钓场补给 · " + order.getMallOrderNo());
            data.put("pay", prepay);
            data.put("needWxPay", true);
            return AjaxResult.success(data);
        }

        if (!wxPayService.isMockEnabled()) return AjaxResult.error("微信支付未配置");
        FishMallOrder paid = mallService.markPaid(order.getMallOrderNo(), "MOCK" + System.currentTimeMillis());
        data.put("order", paid);
        data.put("needWxPay", false);
        return AjaxResult.success(data);
    }

    @Anonymous
    @GetMapping("/mall/order/my")
    public AjaxResult myMallOrders()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(mallService.listMyOrders(userId));
    }

    @Anonymous
    @GetMapping("/mall/order/{mallOrderId}")
    public AjaxResult mallOrderDetail(@PathVariable Long mallOrderId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        FishMallOrder o = mallService.getOrder(mallOrderId);
        if (o == null) return AjaxResult.error("订单不存在");
        if (!userId.equals(o.getUserId())) return unauthorized();
        return AjaxResult.success(o);
    }

    // ===================== 储值钱包 =====================

    /** 我的余额 + 最近流水 */
    @Anonymous
    @GetMapping("/wallet/info")
    public AjaxResult walletInfo()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Map<String, Object> data = new HashMap<>();
        data.put("balance", balanceService.getBalance(userId));
        data.put("logs", balanceService.recentLogs(userId));
        return AjaxResult.success(data);
    }

    /** 充值套餐列表（仅上架） */
    @Anonymous
    @GetMapping("/wallet/plans")
    public AjaxResult walletPlans()
    {
        return AjaxResult.success(balanceService.listActivePlans());
    }

    /**
     * 发起充值：支持套餐(planId) 或 自定义金额(amountCents)。
     * 返回充值订单 + wxpay 预支付参数，mock 模式下直接置完成并入账。
     */
    @Anonymous
    @PostMapping("/wallet/recharge")
    public AjaxResult recharge(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long planId = parseLong(body.get("planId"));
        Object rawAmt = body.get("amountCents");
        Integer customAmount = null;
        if (rawAmt instanceof Number) customAmount = ((Number) rawAmt).intValue();
        else if (rawAmt != null) try { customAmount = Integer.parseInt(rawAmt.toString()); } catch (Exception ignore) {}

        FishRechargeOrder order = balanceService.createRechargeOrder(userId, planId, customAmount);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);

        if (wxPayService.isEnabled())
        {
            FishUser user = userService.selectFishUserByUserId(userId);
            if (user == null) return AjaxResult.error("用户不存在");
            Map<String, Object> prepay = wxPayService.createPrepay(order.getRechargeNo(), order.getAmountCents(),
                    user.getOpenid(), "钓场储值充值 · " + order.getRechargeNo());
            data.put("pay", prepay);
            data.put("needWxPay", true);
            return AjaxResult.success(data);
        }

        if (!wxPayService.isMockEnabled()) return AjaxResult.error("微信支付未配置");
        FishRechargeOrder paid = balanceService.markRechargePaid(order.getRechargeNo(), "MOCK" + System.currentTimeMillis());
        data.put("order", paid);
        data.put("needWxPay", false);
        return AjaxResult.success(data);
    }

    /** 我的充值历史 */
    @Anonymous
    @GetMapping("/wallet/recharges")
    public AjaxResult myRecharges()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(balanceService.listMyRechargeOrders(userId));
    }

    // ===================== 店员工作台 =====================

    /** 当前用户是否为店员 + 最近 N 单已领取简报 */
    @Anonymous
    @GetMapping("/staff/info")
    public AjaxResult staffInfo()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        FishUser user = userService.selectFishUserByUserId(userId);
        if (user == null) return AjaxResult.error("用户不存在");
        boolean staff = user.getIsStaff() != null && user.getIsStaff() == 1;
        Map<String, Object> data = new HashMap<>();
        data.put("isStaff", staff);
        data.put("nickname", user.getNickname());
        if (staff)
        {
            FishMallOrder q = new FishMallOrder();
            q.setStatus(2);
            // 借列表分页参数：当前实现 selectList 不分页，前端自行截取。简单做：直接调，前端取前 10
            data.put("recent", mallService.listOrder(q));
            // 待领取数量
            FishMallOrder pendQ = new FishMallOrder();
            pendQ.setStatus(1);
            data.put("pendingCount", mallService.listOrder(pendQ).size());
        }
        return AjaxResult.success(data);
    }

    /** 店员确认领取 */
    @Anonymous
    @PostMapping("/staff/redeem")
    public AjaxResult staffRedeem(@RequestBody Map<String, String> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        FishUser user = userService.selectFishUserByUserId(userId);
        if (user == null || user.getIsStaff() == null || user.getIsStaff() != 1)
        {
            return AjaxResult.error(403, "无确认领取权限");
        }
        String code = body == null ? "" : body.getOrDefault("code", "");
        FishMallOrder order = mallService.redeem(code, user.getNickname());
        return AjaxResult.success(order);
    }

    /** 微信退款异步通知 */
    @Anonymous
    @PostMapping("/order/pay/refund/notify")
    public Map<String, Object> refundNotify(HttpServletRequest req, @RequestBody String body)
    {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) { String n = names.nextElement(); headers.put(n, req.getHeader(n)); }
        Map<String, Object> res = new HashMap<>();
        try {
            IWxPayService.RefundCallback cb = wxPayService.handleRefundNotify(body, headers);
            if (cb != null && cb.refundNo != null)
            {
                refundService.handleRefundCallback(cb.refundNo, cb.success, cb.wxRefundNo);
            }
            res.put("code", "SUCCESS"); res.put("message", "成功");
        } catch (Exception e) {
            res.put("code", "FAIL"); res.put("message", "处理失败");
        }
        return res;
    }

    private FishQrcode requireScanQrcode(Map<String, Object> body, String expectedType)
    {
        FishQrcode qr = resolveRequestQrcode(body);
        if (qr == null)
        {
            throw new ServiceException("start".equals(expectedType) ? "请先扫描入口码" : "请先扫描出口码");
        }
        if ("1".equals(qr.getStatus()))
        {
            throw new ServiceException("二维码已停用");
        }
        if (!expectedType.equals(qr.getQrType()))
        {
            throw new ServiceException("start".equals(expectedType) ? "请扫描入口码" : "请扫描出口码");
        }
        return qr;
    }

    private FishQrcode resolveRequestQrcode(Map<String, Object> body)
    {
        if (body == null) return null;
        Long qrId = parseLong(body.get("qrId"));
        if (qrId != null) return qrcodeMapper.selectFishQrcodeByQrId(qrId);

        String scene = body.get("scene") == null ? "" : String.valueOf(body.get("scene")).trim();
        if (scene.isEmpty()) return null;
        FishQrcode query = new FishQrcode();
        query.setSceneValue(scene);
        List<FishQrcode> list = qrcodeMapper.selectFishQrcodeList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    private Long parseLong(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    private Long currentUserId()
    {
        return appTokenService.resolveUserId(request.getHeader("Authorization"));
    }

    private boolean isCurrentUser(Long userId)
    {
        Long current = currentUserId();
        return current != null && current.equals(userId);
    }

    private AjaxResult unauthorized()
    {
        return AjaxResult.error(401, "请先登录");
    }

    // ===== 钓位预订 =====

    @Anonymous
    @GetMapping("/spot/list")
    public AjaxResult spotList(@RequestParam Long venueId)
    {
        return AjaxResult.success(spotService.selectAvailableByVenue(venueId));
    }

    @PostMapping("/reservation/submit")
    public AjaxResult reservationSubmit(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        if (body.get("venueId") == null || body.get("spotId") == null || body.get("reserveDate") == null)
            return AjaxResult.error("参数缺失");
        Long venueId = Long.valueOf(body.get("venueId").toString());
        Long spotId = Long.valueOf(body.get("spotId").toString());
        String reserveDate = body.get("reserveDate").toString();
        String timeSlot = body.getOrDefault("timeSlot", "").toString();
        return AjaxResult.success(spotService.submitReservation(userId, venueId, spotId, reserveDate, timeSlot));
    }

    @GetMapping("/reservation/mine")
    public AjaxResult reservationMine()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(spotService.selectReservationsByUser(userId));
    }

    @PutMapping("/reservation/cancel/{id}")
    public AjaxResult reservationCancel(@PathVariable Long id)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(spotService.cancelReservation(id, userId, "用户取消"));
    }

    // ===== 钓获打卡 =====

    @Anonymous
    @GetMapping("/catch/list")
    public AjaxResult catchList()
    {
        Long userId = currentUserId();
        return AjaxResult.success(catchService.selectPublicList(userId));
    }

    @GetMapping("/catch/mine")
    public AjaxResult catchMine()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(catchService.selectByUser(userId));
    }

    @PostMapping("/catch/publish")
    public AjaxResult catchPublish(@RequestBody com.ruoyi.fishing.domain.FishCatchRecord record)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        record.setUserId(userId);
        return AjaxResult.success(catchService.publish(record));
    }

    @PostMapping("/catch/like/{catchId}")
    public AjaxResult catchLike(@PathVariable Long catchId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        int result = catchService.toggleLike(userId, catchId);
        return AjaxResult.success(result == 1 ? "已点赞" : "已取消", result);
    }

    @Anonymous
    @GetMapping("/catch/comments/{catchId}")
    public AjaxResult catchComments(@PathVariable Long catchId)
    {
        return AjaxResult.success(catchService.getComments(catchId));
    }

    @PostMapping("/catch/comment")
    public AjaxResult catchComment(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        if (body.get("catchId") == null || body.get("content") == null)
            return AjaxResult.error("参数缺失");
        Long catchId = Long.valueOf(body.get("catchId").toString());
        String content = body.get("content").toString().trim();
        if (content.isEmpty()) return AjaxResult.error("评论内容不能为空");
        Long replyToId = body.containsKey("replyToId") && body.get("replyToId") != null ? Long.valueOf(body.get("replyToId").toString()) : null;
        Long replyToUser = body.containsKey("replyToUser") && body.get("replyToUser") != null ? Long.valueOf(body.get("replyToUser").toString()) : null;
        return AjaxResult.success(catchService.addComment(catchId, userId, content, replyToId, replyToUser));
    }

    // ===== 会员等级 =====

    @Anonymous
    @GetMapping("/member/levels")
    public AjaxResult memberLevels()
    {
        return AjaxResult.success(memberLevelService.selectAllActive());
    }

    @GetMapping("/member/my")
    public AjaxResult memberMy()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        memberLevelService.refreshUserLevel(userId);
        Map<String, Object> data = new HashMap<>();
        FishUser u = userService.selectFishUserByUserId(userId);
        data.put("levelId", u.getMemberLevelId());
        data.put("levelName", u.getMemberLevelName());
        data.put("discountRate", memberLevelService.getUserDiscountRate(userId));
        return AjaxResult.success(data);
    }

    // ===== 积分 =====

    @GetMapping("/points/my")
    public AjaxResult pointsMy()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Map<String, Object> data = new HashMap<>();
        data.put("points", pointsService.getUserPoints(userId));
        data.put("exchanges", pointsService.selectExchangeByUser(userId));
        return AjaxResult.success(data);
    }

    @Anonymous
    @GetMapping("/points/goods")
    public AjaxResult pointsGoods()
    {
        return AjaxResult.success(pointsService.selectGoodsActive());
    }

    @PostMapping("/points/checkin")
    public AjaxResult pointsCheckin()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(pointsService.checkin(userId));
    }

    @GetMapping("/points/checkin/calendar")
    public AjaxResult checkinCalendar(@RequestParam(required = false) String month)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(pointsService.checkinCalendar(userId, month));
    }

    @PostMapping("/points/exchange/{goodsId}")
    public AjaxResult pointsExchange(@PathVariable Long goodsId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(pointsService.exchange(userId, goodsId));
    }

    // ===== 拼场约钓 =====

    @Anonymous
    @GetMapping("/group/list")
    public AjaxResult groupList(@RequestParam(required = false) Long venueId)
    {
        return AjaxResult.success(groupService.selectActiveList(venueId));
    }

    @GetMapping("/group/{id}")
    public AjaxResult groupDetail(@PathVariable Long id)
    {
        return AjaxResult.success(groupService.selectById(id));
    }

    @GetMapping("/group/mine")
    public AjaxResult groupMine()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(groupService.selectByUser(userId));
    }

    @PostMapping("/group/create")
    public AjaxResult groupCreate(@RequestBody com.ruoyi.fishing.domain.FishGroupFishing g)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        g.setUserId(userId);
        return AjaxResult.success(groupService.create(g));
    }

    @PostMapping("/group/join/{groupId}")
    public AjaxResult groupJoin(@PathVariable Long groupId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(groupService.join(groupId, userId));
    }

    @PostMapping("/group/quit/{groupId}")
    public AjaxResult groupQuit(@PathVariable Long groupId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(groupService.quit(groupId, userId));
    }

    @PostMapping("/group/cancel/{groupId}")
    public AjaxResult groupCancelByUser(@PathVariable Long groupId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(groupService.cancel(groupId, userId));
    }

    // ===== 装备租赁 =====

    @Anonymous
    @GetMapping("/rental/list")
    public AjaxResult rentalList()
    {
        return AjaxResult.success(rentalService.selectGoodsAvailable());
    }

    @PostMapping("/rental/rent/{goodsId}")
    public AjaxResult rentalRent(@PathVariable Long goodsId)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(rentalService.rent(userId, goodsId));
    }

    @GetMapping("/rental/mine")
    public AjaxResult rentalMine()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(rentalService.selectOrdersByUser(userId));
    }

    // ===== 比赛 =====

    @Anonymous
    @GetMapping("/competition/list")
    public AjaxResult competitionList(@RequestParam(required = false) Long venueId)
    {
        return AjaxResult.success(competitionService.selectActiveList(venueId));
    }

    @Anonymous
    @GetMapping("/competition/{id}")
    public AjaxResult competitionDetail(@PathVariable Long id)
    {
        return AjaxResult.success(competitionService.selectById(id));
    }

    @Anonymous
    @GetMapping("/competition/ranking/{id}")
    public AjaxResult competitionRanking(@PathVariable Long id)
    {
        return AjaxResult.success(competitionService.selectRanking(id));
    }

    @PostMapping("/competition/enter/{compId}")
    public AjaxResult competitionEnter(@PathVariable Long compId, @RequestBody Map<String, String> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        String nickname = body.getOrDefault("nickname", "");
        String phone = body.getOrDefault("phone", "");
        return AjaxResult.success(competitionService.enter(compId, userId, nickname, phone));
    }

    // ===== 称鱼结算 =====

    /** 鱼获单价（按钓场区分路人价/会员价，并返回当前用户是否会员） */
    @Anonymous
    @GetMapping("/fish-weigh/price")
    public AjaxResult fishWeighPrice(@RequestParam(required = false) Long venueId)
    {
        Long resolvedVenueId = resolveDefaultVenueId(venueId);
        int[] prices = weighService.getPrices(resolvedVenueId);
        boolean isMember = false;
        Long userId = currentUserId();
        if (userId != null)
        {
            FishUser u = userService.selectFishUserByUserId(userId);
            isMember = u != null && u.getMemberLevelId() != null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("normalPricePerJin", prices[0]);
        data.put("memberPricePerJin", prices[1]);
        data.put("isMember", isMember);
        return AjaxResult.success(data);
    }

    /** 提交称鱼：服务端按重量×单价重算金额 */
    @Anonymous
    @PostMapping("/fish-weigh/submit")
    public AjaxResult fishWeighSubmit(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long bodyUserId = parseLong(body.get("userId"));
        if (bodyUserId != null && !bodyUserId.equals(userId)) return unauthorized();

        Integer weightGrams = null;
        Object w = body.get("weightGrams");
        if (w instanceof Number) weightGrams = ((Number) w).intValue();
        else if (w != null) try { weightGrams = Integer.parseInt(String.valueOf(w)); } catch (Exception ignore) {}
        if (weightGrams == null || weightGrams <= 0) return AjaxResult.error("请输入有效的鱼获重量");

        Long venueId = resolveDefaultVenueId(parseLong(body.get("venueId")));
        FishUser u = userService.selectFishUserByUserId(userId);
        boolean isMember = u != null && u.getMemberLevelId() != null;

        FishWeighOrder order = weighService.createOrder(userId, venueId, weightGrams, isMember);
        return AjaxResult.success(order);
    }

    /** 称鱼支付（支持 useBalance 全额余额抵扣，否则走微信支付） */
    @Anonymous
    @PostMapping("/fish-weigh/pay")
    public AjaxResult fishWeighPay(@RequestBody Map<String, Object> body)
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        Long fishWeighId = parseLong(body.get("fishWeighId"));
        if (fishWeighId == null) return AjaxResult.error("参数缺失");
        boolean useBalance = Boolean.TRUE.equals(body.get("useBalance"))
                || "true".equalsIgnoreCase(String.valueOf(body.get("useBalance")));

        FishWeighOrder order = weighService.selectById(fishWeighId);
        if (order == null) return AjaxResult.error("称鱼订单不存在");
        if (!userId.equals(order.getUserId())) return unauthorized();
        if (order.getStatus() != null && order.getStatus() == 1)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            data.put("needWxPay", false);
            return AjaxResult.success(data);
        }

        int amount = order.getAmountCents() == null ? 0 : order.getAmountCents();

        if (useBalance)
        {
            FishWeighOrder paid = weighService.payByBalance(userId, fishWeighId);
            Map<String, Object> data = new HashMap<>();
            data.put("order", paid);
            data.put("needWxPay", false);
            return AjaxResult.success(data);
        }

        if (wxPayService.isEnabled())
        {
            FishUser user = userService.selectFishUserByUserId(userId);
            if (user == null) return AjaxResult.error("用户不存在");
            Map<String, Object> prepay = wxPayService.createPrepay(order.getWeighNo(), amount, user.getOpenid(),
                    "共享钓场 · 称鱼结算 " + order.getWeighNo());
            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            data.put("pay", prepay);
            data.put("needWxPay", true);
            return AjaxResult.success(data);
        }

        if (!wxPayService.isMockEnabled()) return AjaxResult.error("微信支付未配置");
        FishWeighOrder paid = weighService.markPaid(order.getWeighNo(), "MOCK");
        Map<String, Object> data = new HashMap<>();
        data.put("order", paid);
        data.put("needWxPay", false);
        return AjaxResult.success(data);
    }

    /** 我的称鱼记录 */
    @Anonymous
    @GetMapping("/fish-weigh/my")
    public AjaxResult fishWeighMy()
    {
        Long userId = currentUserId();
        if (userId == null) return unauthorized();
        return AjaxResult.success(weighService.selectByUser(userId));
    }

    /** 解析钓场ID：为空时取默认（第一个）钓场 */
    private Long resolveDefaultVenueId(Long venueId)
    {
        if (venueId != null) return venueId;
        List<FishVenue> venues = venueService.selectFishVenueList(new FishVenue());
        return venues.isEmpty() ? null : venues.get(0).getVenueId();
    }
}
