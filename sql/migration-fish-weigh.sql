-- ============================================================
-- 称鱼结算功能 · 增量迁移脚本
-- 适用于已部署的数据库（fishing-full.sql 是全量 DROP/CREATE，勿在生产重跑）
-- 执行一次即可。
-- ============================================================

-- 1) 钓场表增加鱼获单价列（路人价 / 会员价，单位：分/斤）
ALTER TABLE `fish_venue`
  ADD COLUMN `fish_price_cents`        INT(11) DEFAULT NULL COMMENT '路人鱼获单价(分/斤)' AFTER `rule_id`,
  ADD COLUMN `fish_member_price_cents` INT(11) DEFAULT NULL COMMENT '会员鱼获单价(分/斤)' AFTER `fish_price_cents`;

-- 默认价：路人 11.80 元/斤(1180分)，会员 9.80 元/斤(980分)
UPDATE `fish_venue`
SET `fish_price_cents` = 1180, `fish_member_price_cents` = 980
WHERE `fish_price_cents` IS NULL;

-- 2) 称鱼结算订单表
DROP TABLE IF EXISTS `fish_weigh_order`;
CREATE TABLE `fish_weigh_order` (
  `weigh_id`     BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `weigh_no`     VARCHAR(40) NOT NULL COMMENT '称鱼单号(微信outTradeNo,前缀W)',
  `user_id`      BIGINT(20)  NOT NULL,
  `venue_id`     BIGINT(20)  NULL COMMENT '钓场ID',
  `weight_grams` INT(11)     NOT NULL COMMENT '鱼获重量(克)',
  `price_cents`  INT(11)     NOT NULL COMMENT '结算单价(分/斤)快照',
  `is_member`    TINYINT(2)  NOT NULL DEFAULT 0 COMMENT '是否会员价 0否 1是',
  `amount_cents` INT(11)     NOT NULL COMMENT '应付金额(分)',
  `status`       TINYINT(2)  NOT NULL DEFAULT 0 COMMENT '0待支付 1已完成 2已取消',
  `pay_trade_no` VARCHAR(64) DEFAULT '',
  `paid_time`    DATETIME    NULL,
  `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME    NULL,
  PRIMARY KEY (`weigh_id`),
  UNIQUE KEY `uk_weigh_no` (`weigh_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_venue` (`venue_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='称鱼结算订单';
