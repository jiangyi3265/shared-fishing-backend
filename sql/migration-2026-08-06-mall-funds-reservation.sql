-- 商城混合支付资金冻结标记（幂等）。
-- 0：历史订单，积分/余额尚未在下单时冻结；1：新订单，已在下单时冻结。
SET @db := DATABASE();
SET @column_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'fish_mall_order'
    AND COLUMN_NAME = 'funds_reserved'
);
SET @ddl := IF(
  @column_exists = 0,
  'ALTER TABLE fish_mall_order ADD COLUMN funds_reserved TINYINT NOT NULL DEFAULT 0 COMMENT ''0未冻结 1已冻结'' AFTER points_deduct_cents',
  'DO 0'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = @db
  AND TABLE_NAME = 'fish_mall_order'
  AND COLUMN_NAME = 'funds_reserved';

-- 修正历史“下单即计销量”造成的虚高：只统计已支付或已领取订单。
UPDATE fish_mall_goods g
LEFT JOIN (
  SELECT i.goods_id, SUM(i.qty) AS paid_sales
  FROM fish_mall_order_item i
  INNER JOIN fish_mall_order o ON o.mall_order_id = i.mall_order_id
  WHERE o.status IN (1, 2)
  GROUP BY i.goods_id
) s ON s.goods_id = g.goods_id
SET g.sales = COALESCE(s.paid_sales, 0);
