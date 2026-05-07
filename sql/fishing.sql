-- ----------------------------
-- 共享钓场业务表
-- 金额统一以"分"为单位
-- ----------------------------

-- 钓场
DROP TABLE IF EXISTS `fish_venue`;
CREATE TABLE `fish_venue` (
  `venue_id`       BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '钓场ID',
  `name`           VARCHAR(100) NOT NULL                COMMENT '钓场名称',
  `address`        VARCHAR(200) DEFAULT ''              COMMENT '地址',
  `notice`         VARCHAR(500) DEFAULT ''              COMMENT '营业说明',
  `phone`          VARCHAR(30)  DEFAULT ''              COMMENT '联系方式',
  `rule_id`        BIGINT(20)   DEFAULT NULL            COMMENT '默认计费规则ID',
  `status`         CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `del_flag`       CHAR(1)      DEFAULT '0'             COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by`      VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`    DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_by`      VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`    DATETIME     DEFAULT NULL            COMMENT '更新时间',
  `remark`         VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`venue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='钓场信息';

-- 计费规则
DROP TABLE IF EXISTS `fish_billing_rule`;
CREATE TABLE `fish_billing_rule` (
  `rule_id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name`           VARCHAR(100) NOT NULL                COMMENT '规则名称',
  `unit_type`           VARCHAR(20)  DEFAULT 'minute'        COMMENT '计费单位(minute/half_hour/hour)',
  `step_minutes`        INT(11)      DEFAULT 30              COMMENT '单位时长(分钟)',
  `price_per_step_cents` INT(11)     DEFAULT 300             COMMENT '单位价格(分)',
  `min_duration_minutes` INT(11)     DEFAULT 30              COMMENT '起步时长(分钟)',
  `round_type`          VARCHAR(20)  DEFAULT 'ceil_step'     COMMENT '进位方式(ceil_minute/ceil_step/ceil_hour)',
  `cap_amount_cents`    INT(11)      DEFAULT 0               COMMENT '单次封顶金额(分, 0表示无封顶)',
  `daily_cap_cents`     INT(11)      DEFAULT 0               COMMENT '单日封顶金额(分)',
  `time_segment_json`   TEXT         DEFAULT NULL            COMMENT '分时段价格JSON',
  `summary`             VARCHAR(255) DEFAULT ''              COMMENT '规则摘要',
  `status`              CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `del_flag`            CHAR(1)      DEFAULT '0'             COMMENT '删除标志',
  `create_by`           VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`         DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_by`           VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`         DATETIME     DEFAULT NULL            COMMENT '更新时间',
  `remark`              VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='计费规则';

-- 小程序用户
DROP TABLE IF EXISTS `fish_user`;
CREATE TABLE `fish_user` (
  `user_id`     BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid`      VARCHAR(64)  NOT NULL                COMMENT '微信openid',
  `unionid`     VARCHAR(64)  DEFAULT ''              COMMENT '微信unionid',
  `nickname`    VARCHAR(60)  DEFAULT ''              COMMENT '昵称',
  `avatar`      VARCHAR(500) DEFAULT ''              COMMENT '头像',
  `phone`       VARCHAR(20)  DEFAULT ''              COMMENT '手机号',
  `status`      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1禁用）',
  `last_login_time` DATETIME DEFAULT NULL            COMMENT '最近登录时间',
  `create_time` DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='小程序用户';

-- 二维码
DROP TABLE IF EXISTS `fish_qrcode`;
CREATE TABLE `fish_qrcode` (
  `qr_id`       BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '二维码ID',
  `venue_id`    BIGINT(20)   NOT NULL                COMMENT '钓场ID',
  `qr_type`     VARCHAR(10)  NOT NULL                COMMENT '类型(start进场 end离场)',
  `scene_value` VARCHAR(100) DEFAULT ''              COMMENT '场景值',
  `remark`      VARCHAR(255) DEFAULT ''              COMMENT '备注',
  `status`      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `create_by`   VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_by`   VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (`qr_id`),
  KEY `idx_venue` (`venue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='钓场二维码';

-- 订单
DROP TABLE IF EXISTS `fish_order`;
CREATE TABLE `fish_order` (
  `order_id`         BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`         VARCHAR(40)  NOT NULL                COMMENT '订单号',
  `user_id`          BIGINT(20)   NOT NULL                COMMENT '用户ID',
  `venue_id`         BIGINT(20)   NOT NULL                COMMENT '钓场ID',
  `status`           TINYINT(2)   DEFAULT 1               COMMENT '状态(0待支付 1计时中 2待结算 3已完成 4已取消)',
  `start_time`       DATETIME     DEFAULT NULL            COMMENT '开始时间',
  `end_time`         DATETIME     DEFAULT NULL            COMMENT '结束时间',
  `duration_seconds` INT(11)      DEFAULT 0               COMMENT '计费时长(秒)',
  `elapsed_seconds`  INT(11)      DEFAULT 0               COMMENT '实际时长(秒)',
  `amount_cents`     INT(11)      DEFAULT 0               COMMENT '应付金额(分)',
  `discount_cents`   INT(11)      DEFAULT 0               COMMENT '优惠金额(分)',
  `amount_paid`      INT(11)      DEFAULT 0               COMMENT '实付金额(分)',
  `coupon_id`        BIGINT(20)   DEFAULT NULL            COMMENT '使用的用户优惠券ID',
  `paid_time`        DATETIME     DEFAULT NULL            COMMENT '支付时间',
  `pay_trade_no`     VARCHAR(64)  DEFAULT ''              COMMENT '支付流水号',
  `rule_snapshot`    TEXT         DEFAULT NULL            COMMENT '计费规则快照',
  `cancel_reason`    VARCHAR(255) DEFAULT ''              COMMENT '取消原因',
  `create_by`        VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`      DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_by`        VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`      DATETIME     DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_venue` (`venue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='钓场订单';

-- 广告/活动
DROP TABLE IF EXISTS `fish_ad`;
CREATE TABLE `fish_ad` (
  `ad_id`           BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '广告ID',
  `ad_type`         VARCHAR(20)  DEFAULT 'ad'            COMMENT '类型(ad广告 activity活动)',
  `title`           VARCHAR(100) NOT NULL                COMMENT '标题',
  `description`     VARCHAR(255) DEFAULT ''              COMMENT '描述',
  `image`           VARCHAR(500) DEFAULT ''              COMMENT '封面图',
  `bg_color`        VARCHAR(30)  DEFAULT ''              COMMENT '背景色',
  `content`         TEXT         DEFAULT NULL            COMMENT '详情内容',
  `sort_num`        INT(11)      DEFAULT 0               COMMENT '排序',
  `activity_name`   VARCHAR(100) DEFAULT ''              COMMENT '活动名称',
  `activity_date`   VARCHAR(50)  DEFAULT ''              COMMENT '活动日期',
  `activity_location` VARCHAR(200) DEFAULT ''            COMMENT '活动地点',
  `activity_fee_cents` INT(11)   DEFAULT 0               COMMENT '活动报名费(分)',
  `activity_slots`  INT(11)      DEFAULT 0               COMMENT '活动名额',
  `activity_rules`  TEXT         DEFAULT NULL            COMMENT '活动规则',
  `coupon_template_id` BIGINT(20) DEFAULT NULL           COMMENT '关联优惠券模板ID',
  `status`          CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `del_flag`        CHAR(1)      DEFAULT '0'             COMMENT '删除标志',
  `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`     DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`     DATETIME     DEFAULT NULL            COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`ad_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='广告活动';

-- 活动报名
DROP TABLE IF EXISTS `fish_registration`;
CREATE TABLE `fish_registration` (
  `reg_id`      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `ad_id`       BIGINT(20)   NOT NULL                COMMENT '活动ID',
  `user_id`     BIGINT(20)   NOT NULL                COMMENT '用户ID',
  `name`        VARCHAR(60)  NOT NULL                COMMENT '报名人姓名',
  `phone`       VARCHAR(20)  NOT NULL                COMMENT '联系电话',
  `remark`      VARCHAR(500) DEFAULT ''              COMMENT '备注',
  `fee_cents`   INT(11)      DEFAULT 0               COMMENT '报名费(分)',
  `paid`        TINYINT(1)   DEFAULT 0               COMMENT '是否支付',
  `paid_time`   DATETIME     DEFAULT NULL            COMMENT '支付时间',
  `status`      TINYINT(2)   DEFAULT 0               COMMENT '状态(0正常 1取消)',
  `create_time` DATETIME     DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (`reg_id`),
  KEY `idx_ad_user` (`ad_id`, `user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='活动报名';

-- 优惠券模板
DROP TABLE IF EXISTS `fish_coupon_template`;
CREATE TABLE `fish_coupon_template` (
  `template_id`       BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `title`             VARCHAR(100) NOT NULL                COMMENT '名称',
  `coupon_type`       VARCHAR(20)  NOT NULL                COMMENT '类型(amount满减 duration免费时长)',
  `coupon_value`      INT(11)      NOT NULL                COMMENT '金额券:面值分,时长券:分钟',
  `min_amount_cents`  INT(11)      DEFAULT 0               COMMENT '最低使用门槛(分)',
  `valid_days`        INT(11)      DEFAULT 30              COMMENT '有效天数',
  `total_stock`       INT(11)      DEFAULT 0               COMMENT '库存(0不限)',
  `issued_count`      INT(11)      DEFAULT 0               COMMENT '已发放数量',
  `source`            VARCHAR(50)  DEFAULT ''              COMMENT '发放渠道',
  `status`            CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `del_flag`          CHAR(1)      DEFAULT '0'             COMMENT '删除标志',
  `create_by`         VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`       DATETIME     DEFAULT NULL            COMMENT '创建时间',
  `update_by`         VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`       DATETIME     DEFAULT NULL            COMMENT '更新时间',
  `remark`            VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='优惠券模板';

-- 用户优惠券
DROP TABLE IF EXISTS `fish_user_coupon`;
CREATE TABLE `fish_user_coupon` (
  `coupon_id`         BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '券ID',
  `template_id`       BIGINT(20)   NOT NULL                COMMENT '模板ID',
  `user_id`           BIGINT(20)   NOT NULL                COMMENT '用户ID',
  `title`             VARCHAR(100) NOT NULL                COMMENT '名称快照',
  `coupon_type`       VARCHAR(20)  NOT NULL                COMMENT '类型(amount/duration)',
  `coupon_value`      INT(11)      NOT NULL                COMMENT '值',
  `min_amount_cents`  INT(11)      DEFAULT 0               COMMENT '门槛(分)',
  `expire_time`       DATETIME     DEFAULT NULL            COMMENT '过期时间',
  `used`              TINYINT(1)   DEFAULT 0               COMMENT '是否使用',
  `used_time`         DATETIME     DEFAULT NULL            COMMENT '使用时间',
  `order_id`          BIGINT(20)   DEFAULT NULL            COMMENT '使用订单ID',
  `source`            VARCHAR(50)  DEFAULT ''              COMMENT '来源',
  `create_time`       DATETIME     DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (`coupon_id`),
  KEY `idx_user_used` (`user_id`, `used`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='用户优惠券';

-- ----------------------------
-- 初始化菜单（钓场业务模块）
-- ----------------------------
INSERT INTO sys_menu VALUES (2000, '钓场管理', 0, 3, 'fishing', null, '', '', 1, 0, 'M', '0', '0', '', 'fish', 'admin', sysdate(), '', null, '钓场业务目录');

INSERT INTO sys_menu VALUES (2010, '钓场信息', 2000, 1, 'venue', 'fishing/venue/index', '', '', 1, 0, 'C', '0', '0', 'fishing:venue:list', 'shopping', 'admin', sysdate(), '', null, '钓场信息菜单');
INSERT INTO sys_menu VALUES (2011, '钓场查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:venue:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2012, '钓场新增', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:venue:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2013, '钓场修改', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:venue:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2014, '钓场删除', 2010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:venue:remove', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2020, '计费规则', 2000, 2, 'rule', 'fishing/rule/index', '', '', 1, 0, 'C', '0', '0', 'fishing:rule:list', 'rate', 'admin', sysdate(), '', null, '计费规则菜单');
INSERT INTO sys_menu VALUES (2021, '规则查询', 2020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:rule:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2022, '规则新增', 2020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:rule:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2023, '规则修改', 2020, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:rule:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2024, '规则删除', 2020, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:rule:remove', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2030, '订单管理', 2000, 3, 'order', 'fishing/order/index', '', '', 1, 0, 'C', '0', '0', 'fishing:order:list', 'form', 'admin', sysdate(), '', null, '订单管理菜单');
INSERT INTO sys_menu VALUES (2031, '订单查询', 2030, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:order:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2032, '人工结束', 2030, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:order:finish', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2033, '订单取消', 2030, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:order:cancel', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2034, '订单导出', 2030, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:order:export', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2040, '广告活动', 2000, 4, 'ad', 'fishing/ad/index', '', '', 1, 0, 'C', '0', '0', 'fishing:ad:list', 'guide', 'admin', sysdate(), '', null, '广告活动菜单');
INSERT INTO sys_menu VALUES (2041, '广告查询', 2040, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:ad:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2042, '广告新增', 2040, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:ad:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2043, '广告修改', 2040, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:ad:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2044, '广告删除', 2040, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:ad:remove', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2050, '活动报名', 2000, 5, 'registration', 'fishing/registration/index', '', '', 1, 0, 'C', '0', '0', 'fishing:registration:list', 'people', 'admin', sysdate(), '', null, '活动报名菜单');
INSERT INTO sys_menu VALUES (2051, '报名查询', 2050, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:registration:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2052, '报名导出', 2050, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:registration:export', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2060, '优惠券模板', 2000, 6, 'coupon', 'fishing/coupon/index', '', '', 1, 0, 'C', '0', '0', 'fishing:coupon:list', 'tree-table', 'admin', sysdate(), '', null, '优惠券模板菜单');
INSERT INTO sys_menu VALUES (2061, '模板查询', 2060, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:coupon:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2062, '模板新增', 2060, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:coupon:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2063, '模板修改', 2060, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:coupon:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2064, '模板删除', 2060, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:coupon:remove', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2070, '用户管理', 2000, 7, 'user', 'fishing/user/index', '', '', 1, 0, 'C', '0', '0', 'fishing:user:list', 'user', 'admin', sysdate(), '', null, '小程序用户');
INSERT INTO sys_menu VALUES (2071, '用户查询', 2070, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:user:query', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu VALUES (2080, '数据看板', 2000, 0, 'dashboard', 'fishing/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'fishing:order:list', 'chart', 'admin', sysdate(), '', null, '钓场数据看板');

INSERT INTO sys_menu VALUES (2090, '二维码管理', 2000, 8, 'qrcode', 'fishing/qrcode/index', '', '', 1, 0, 'C', '0', '0', 'fishing:qrcode:list', 'qrcode', 'admin', sysdate(), '', null, '入口/出口二维码');
INSERT INTO sys_menu VALUES (2091, '二维码查询', 2090, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:qrcode:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2092, '二维码新增', 2090, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:qrcode:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2093, '二维码修改', 2090, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:qrcode:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES (2094, '二维码删除', 2090, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'fishing:qrcode:remove', '#', 'admin', sysdate(), '', null, '');

-- 字典
INSERT INTO sys_dict_type VALUES (100, '订单状态', 'fish_order_status', '0', 'admin', sysdate(), '', null, '钓场订单状态');
INSERT INTO sys_dict_data VALUES (500, 1, '待支付', '0', 'fish_order_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (501, 2, '计时中', '1', 'fish_order_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (502, 3, '待结算', '2', 'fish_order_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (503, 4, '已完成', '3', 'fish_order_status', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (504, 5, '已取消', '4', 'fish_order_status', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '');

INSERT INTO sys_dict_type VALUES (101, '二维码类型', 'fish_qr_type', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (510, 1, '入场', 'start', 'fish_qr_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (511, 2, '离场', 'end',   'fish_qr_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');

INSERT INTO sys_dict_type VALUES (102, '广告类型', 'fish_ad_type', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (520, 1, '广告', 'ad',       'fish_ad_type', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (521, 2, '活动', 'activity', 'fish_ad_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');

INSERT INTO sys_dict_type VALUES (103, '优惠券类型', 'fish_coupon_type', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (530, 1, '满减券', 'amount',   'fish_coupon_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
INSERT INTO sys_dict_data VALUES (531, 2, '时长券', 'duration', 'fish_coupon_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
