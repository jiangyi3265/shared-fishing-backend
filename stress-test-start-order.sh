#!/bin/bash
# 压测脚本：模拟 50 并发同一用户扫码入场，验证不会产生重复订单
# 使用前先启动后端服务，确保数据库中有 venueId=1 的钓场和计费规则
# 用法: bash stress-test-start-order.sh

BASE_URL="${1:-http://localhost:8080}"
CONCURRENT=50

echo "=== 钓场订单并发压测 ==="
echo "目标: ${BASE_URL}"
echo "并发数: ${CONCURRENT}"
echo ""

# 1. 先登录获取 token（使用 mock 模式）
echo "[1/4] 登录获取 token..."
LOGIN_RESP=$(curl -s -X POST "${BASE_URL}/app/login" \
  -H "Content-Type: application/json" \
  -d '{"code":"test_stress_user_001"}')

TOKEN=$(echo "$LOGIN_RESP" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
  echo "登录失败: $LOGIN_RESP"
  exit 1
fi
echo "Token: ${TOKEN:0:20}..."

# 2. 确保没有遗留订单（取消所有进行中的）
echo "[2/4] 清理遗留订单..."
curl -s -X POST "${BASE_URL}/app/order/finish" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" > /dev/null 2>&1

# 3. 并发发起 50 个入场请求
echo "[3/4] 发起 ${CONCURRENT} 并发入场请求..."

TMPDIR=$(mktemp -d)
for i in $(seq 1 $CONCURRENT); do
  curl -s -X POST "${BASE_URL}/app/order/start" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{"venueId":1}' \
    -o "${TMPDIR}/resp_${i}.json" &
done

# 等待所有请求完成
wait
echo "所有请求已完成"

# 4. 分析结果
echo "[4/4] 分析结果..."
echo ""

SUCCESS=0
DUPLICATE=0
ERROR=0
ORDER_IDS=""

for f in ${TMPDIR}/resp_*.json; do
  CODE=$(grep -o '"code":[0-9]*' "$f" | head -1 | cut -d: -f2)
  if [ "$CODE" = "200" ]; then
    SUCCESS=$((SUCCESS + 1))
    OID=$(grep -o '"orderId":[0-9]*' "$f" | head -1 | cut -d: -f2)
    ORDER_IDS="${ORDER_IDS} ${OID}"
  elif grep -q "频繁\|未支付\|进行中" "$f" 2>/dev/null; then
    DUPLICATE=$((DUPLICATE + 1))
  else
    ERROR=$((ERROR + 1))
    echo "  异常响应: $(cat $f | head -c 200)"
  fi
done

# 去重统计实际创建的订单数
UNIQUE_ORDERS=$(echo $ORDER_IDS | tr ' ' '\n' | sort -u | grep -v '^$' | wc -l)

echo "=== 压测结果 ==="
echo "成功响应: ${SUCCESS}"
echo "被拦截(重复/频繁): ${DUPLICATE}"
echo "异常错误: ${ERROR}"
echo "实际创建订单数: ${UNIQUE_ORDERS}"
echo ""

if [ "$UNIQUE_ORDERS" -le 1 ]; then
  echo "✅ 通过! 50 并发下只创建了 ${UNIQUE_ORDERS} 个订单，无超卖/重复"
else
  echo "❌ 失败! 创建了 ${UNIQUE_ORDERS} 个订单，存在并发安全问题"
fi

# 清理
rm -rf "$TMPDIR"

# 清理测试订单
echo ""
echo "清理: 结束测试订单..."
curl -s -X POST "${BASE_URL}/app/order/finish" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" > /dev/null 2>&1

echo "完成"
