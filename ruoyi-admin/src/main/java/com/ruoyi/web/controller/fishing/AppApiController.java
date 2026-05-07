package com.ruoyi.web.controller.fishing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.fishing.domain.FishAd;
import com.ruoyi.fishing.domain.FishOrder;
import com.ruoyi.fishing.domain.FishQrcode;
import com.ruoyi.fishing.domain.FishRegistration;
import com.ruoyi.fishing.domain.FishUser;
import com.ruoyi.fishing.domain.FishUserCoupon;
import com.ruoyi.fishing.domain.FishVenue;
import com.ruoyi.fishing.mapper.FishQrcodeMapper;
import com.ruoyi.fishing.service.IFishAdService;
import com.ruoyi.fishing.service.IFishBillingRuleService;
import com.ruoyi.fishing.service.IFishCouponService;
import com.ruoyi.fishing.service.IFishOrderService;
import com.ruoyi.fishing.service.IFishRegistrationService;
import com.ruoyi.fishing.service.IFishUserService;
import com.ruoyi.fishing.service.IFishVenueService;
import com.ruoyi.fishing.service.IWxAuthService;
import com.ruoyi.fishing.service.IWxPayService;

/**
 * 小程序端开放接口
 * 鉴权简化：以 userId 识别身份，生产建议替换为基于 token 的会话
 */
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
    private FishQrcodeMapper qrcodeMapper;

    /** 小程序登录：前端传 code（uni.login 获得），后端走 code2session 换 openid */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> body)
    {
        String code = body.get("code");
        String openid;
        if (code != null && !code.isEmpty()) {
            openid = wxAuthService.resolveOpenid(code);
        } else {
            // 兼容旧客户端，降级为 openid 直传
            openid = body.getOrDefault("openid", "mock_" + System.currentTimeMillis());
        }
        FishUser u = userService.loginOrRegister(openid, body.get("nickname"), body.get("avatar"));
        Map<String, Object> data = new HashMap<>();
        data.put("userId", u.getUserId());
        data.put("user", u);
        data.put("token", "mock-" + u.getUserId());
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
        Long userId = parseLong(body.get("userId"));
        Long venueId = parseLong(body.get("venueId"));
        if (userId == null) return AjaxResult.error("未登录");
        if (venueId == null)
        {
            List<FishVenue> all = venueService.selectFishVenueList(new FishVenue());
            if (all.isEmpty()) return AjaxResult.error("未配置钓场");
            venueId = all.get(0).getVenueId();
        }
        return AjaxResult.success(orderService.startOrder(userId, venueId));
    }

    /** 查询进行中订单（带实时费用） */
    @Anonymous
    @GetMapping("/order/running")
    public AjaxResult running(@RequestParam Long userId)
    {
        return AjaxResult.success(orderService.estimateRunning(userId));
    }

    /** 查询待结算订单 */
    @Anonymous
    @GetMapping("/order/pending")
    public AjaxResult pending(@RequestParam Long userId)
    {
        return AjaxResult.success(orderService.selectPendingOrder(userId));
    }

    /** 结束计时 */
    @Anonymous
    @PostMapping("/order/finish")
    public AjaxResult finishOrder(@RequestBody Map<String, Object> body)
    {
        Long userId = parseLong(body.get("userId"));
        if (userId == null) return AjaxResult.error("未登录");
        return AjaxResult.success(orderService.finishOrder(userId));
    }

    /** 支付 */
    @Anonymous
    @PostMapping("/order/pay")
    public AjaxResult pay(@RequestBody Map<String, Object> body)
    {
        Long userId = parseLong(body.get("userId"));
        Long orderId = parseLong(body.get("orderId"));
        Long couponId = parseLong(body.get("couponId"));
        if (userId == null || orderId == null) return AjaxResult.error("参数缺失");

        FishOrder order = orderService.selectFishOrderByOrderId(orderId);
        if (order == null) return AjaxResult.error("订单不存在");

        if (wxPayService.isEnabled())
        {
            FishUser user = userService.selectFishUserByUserId(userId);
            if (user == null) return AjaxResult.error("用户不存在");
            int finalAmount = order.getAmountCents() == null ? 0 : order.getAmountCents();
            Map<String, Object> prepay = wxPayService.createPrepay(order.getOrderNo(), finalAmount, user.getOpenid(),
                    "共享钓场 · " + order.getOrderNo());
            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            data.put("pay", prepay);
            data.put("needWxPay", true);
            return AjaxResult.success(data);
        }

        FishOrder paid = orderService.pay(userId, orderId, couponId);
        Map<String, Object> data = new HashMap<>();
        data.put("order", paid);
        data.put("needWxPay", false);
        return AjaxResult.success(data);
    }

    /** 微信支付异步通知 */
    @Anonymous
    @PostMapping("/order/pay/notify")
    public Map<String, Object> payNotify(HttpServletRequest request, @RequestBody String body)
    {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) { String n = names.nextElement(); headers.put(n, request.getHeader(n)); }
        Map<String, Object> res = new HashMap<>();
        try {
            String orderNo = wxPayService.handleNotify(body, headers);
            if (orderNo != null) orderService.markPaid(orderNo, headers.getOrDefault("wechatpay-serial", ""));
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
        return AjaxResult.success(orderService.selectOrdersByUser(userId));
    }

    /** 订单详情 */
    @Anonymous
    @GetMapping("/order/{orderId}")
    public AjaxResult orderDetail(@PathVariable Long orderId)
    {
        return AjaxResult.success(orderService.selectFishOrderByOrderId(orderId));
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
        return AjaxResult.success(regService.pay(regId));
    }

    /** 我的报名 */
    @Anonymous
    @GetMapping("/registration/my")
    public AjaxResult myReg(@RequestParam Long userId)
    {
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
        FishUserCoupon c = couponService.grantCoupon(userId, templateId, source == null ? "app" : source);
        return AjaxResult.success(c);
    }

    /** 我的优惠券 */
    @Anonymous
    @GetMapping("/coupon/my")
    public AjaxResult myCoupons(@RequestParam Long userId)
    {
        return AjaxResult.success(couponService.selectMyCoupons(userId));
    }

    /** 可用优惠券 */
    @Anonymous
    @GetMapping("/coupon/available")
    public AjaxResult availableCoupons(@RequestParam Long userId)
    {
        return AjaxResult.success(couponService.selectAvailableCoupons(userId));
    }

    private Long parseLong(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }
}
