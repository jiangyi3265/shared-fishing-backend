-- ============================================================
-- 共享钓场全量初始化 SQL
-- Generated: 2026-05-12
-- Source: RuoYi-Vue/sql/*.sql
-- 用法：mysql -u<user> -p <database> < fishing-full.sql
-- 说明：本文件不创建/切换数据库，请先选择目标库；导入会重建/变更若依基础表和钓场业务表。
-- ============================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- BEGIN SOURCE: ry_20250522.sql
-- ============================================================
-- ============================================================
-- 若依极简空壳模板 SQL
-- 仅保留框架启动和登录所需的最少表和数据
-- 所有后台管理菜单已隐藏，留出干净的空间写自己的业务
-- ============================================================

-- ----------------------------
-- 1、部门表（框架必须）
-- ----------------------------
drop table if exists sys_dept;
create table sys_dept (
  dept_id           bigint(20)      not null auto_increment    comment '部门id',
  parent_id         bigint(20)      default 0                  comment '父部门id',
  ancestors         varchar(50)     default ''                 comment '祖级列表',
  dept_name         varchar(30)     default ''                 comment '部门名称',
  order_num         int(4)          default 0                  comment '显示顺序',
  leader            varchar(20)     default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  email             varchar(50)     default null               comment '邮箱',
  status            char(1)         default '0'                comment '部门状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (dept_id)
) engine=innodb auto_increment=200 comment = '部门表';

insert into sys_dept values(100, 0, '0', '总公司', 0, 'admin', '15888888888', 'admin@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(101, 100, '0,100', '默认部门', 1, 'admin', '15888888888', 'admin@qq.com', '0', '0', 'admin', sysdate(), '', null);


-- ----------------------------
-- 2、用户信息表（框架必须）
-- ----------------------------
drop table if exists sys_user;
create table sys_user (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  dept_id           bigint(20)      default null               comment '部门ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  user_type         varchar(2)      default '00'               comment '用户类型（00系统用户）',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phonenumber       varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(100)    default ''                 comment '头像地址',
  password          varchar(100)    default ''                 comment '密码',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  pwd_update_date   datetime                                   comment '密码最后更新时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '用户信息表';

-- 临时初始密码: ChangeMe#2026，首次登录后立即修改
insert into sys_user values(1, 101, 'admin', '管理员', '00', 'admin@qq.com', '15888888888', '1', '', '$2a$10$aMN3sHertnAYeI93ULvBlOPpbGuTT3DIHzCaTuU8dttO4OnwZIHDS', '0', '0', '127.0.0.1', sysdate(), null, 'admin', sysdate(), '', null, '超级管理员');


-- ----------------------------
-- 3、岗位信息表（框架必须）
-- ----------------------------
drop table if exists sys_post;
create table sys_post (
  post_id       bigint(20)      not null auto_increment    comment '岗位ID',
  post_code     varchar(64)     not null                   comment '岗位编码',
  post_name     varchar(50)     not null                   comment '岗位名称',
  post_sort     int(4)          not null                   comment '显示顺序',
  status        char(1)         not null                   comment '状态（0正常 1停用）',
  create_by     varchar(64)     default ''                 comment '创建者',
  create_time   datetime                                   comment '创建时间',
  update_by     varchar(64)     default ''                 comment '更新者',
  update_time   datetime                                   comment '更新时间',
  remark        varchar(500)    default null               comment '备注',
  primary key (post_id)
) engine=innodb comment = '岗位信息表';

insert into sys_post values(1, 'admin', '管理员', 1, '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 4、角色信息表（框架必须）
-- ----------------------------
drop table if exists sys_role;
create table sys_role (
  role_id              bigint(20)      not null auto_increment    comment '角色ID',
  role_name            varchar(30)     not null                   comment '角色名称',
  role_key             varchar(100)    not null                   comment '角色权限字符串',
  role_sort            int(4)          not null                   comment '显示顺序',
  data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
  dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
  status               char(1)         not null                   comment '角色状态（0正常 1停用）',
  del_flag             char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by            varchar(64)     default ''                 comment '创建者',
  create_time          datetime                                   comment '创建时间',
  update_by            varchar(64)     default ''                 comment '更新者',
  update_time          datetime                                   comment '更新时间',
  remark               varchar(500)    default null               comment '备注',
  primary key (role_id)
) engine=innodb auto_increment=100 comment = '角色信息表';

insert into sys_role values('1', '超级管理员', 'admin', 1, 1, 1, 1, '0', '0', 'admin', sysdate(), '', null, '超级管理员');


-- ----------------------------
-- 5、菜单权限表（框架必须）
-- 所有内置菜单已隐藏，你可以在这里添加自己的业务菜单
-- ----------------------------
drop table if exists sys_menu;
create table sys_menu (
  menu_id           bigint(20)      not null auto_increment    comment '菜单ID',
  menu_name         varchar(50)     not null                   comment '菜单名称',
  parent_id         bigint(20)      default 0                  comment '父菜单ID',
  order_num         int(4)          default 0                  comment '显示顺序',
  path              varchar(200)    default ''                 comment '路由地址',
  component         varchar(255)    default null               comment '组件路径',
  query             varchar(255)    default null               comment '路由参数',
  route_name        varchar(50)     default ''                 comment '路由名称',
  is_frame          int(1)          default 1                  comment '是否为外链（0是 1否）',
  is_cache          int(1)          default 0                  comment '是否缓存（0缓存 1不缓存）',
  menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
  visible           char(1)         default 0                  comment '菜单状态（0显示 1隐藏）',
  status            char(1)         default 0                  comment '菜单状态（0正常 1停用）',
  perms             varchar(100)    default null               comment '权限标识',
  icon              varchar(100)    default '#'                comment '菜单图标',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default ''                 comment '备注',
  primary key (menu_id)
) engine=innodb auto_increment=2000 comment = '菜单权限表';

-- 菜单表为空，你可以在这里添加自己的业务菜单，示例：
-- insert into sys_menu values(2000, '我的业务', '0', '1', 'business', null, '', '', 1, 0, 'M', '0', '0', '', 'guide', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 6、用户和角色关联表（框架必须）
-- ----------------------------
drop table if exists sys_user_role;
create table sys_user_role (
  user_id   bigint(20) not null comment '用户ID',
  role_id   bigint(20) not null comment '角色ID',
  primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

insert into sys_user_role values ('1', '1');


-- ----------------------------
-- 7、角色和菜单关联表（框架必须）
-- ----------------------------
drop table if exists sys_role_menu;
create table sys_role_menu (
  role_id   bigint(20) not null comment '角色ID',
  menu_id   bigint(20) not null comment '菜单ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';


-- ----------------------------
-- 8、角色和部门关联表（框架必须）
-- ----------------------------
drop table if exists sys_role_dept;
create table sys_role_dept (
  role_id   bigint(20) not null comment '角色ID',
  dept_id   bigint(20) not null comment '部门ID',
  primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';


-- ----------------------------
-- 9、用户与岗位关联表（框架必须）
-- ----------------------------
drop table if exists sys_user_post;
create table sys_user_post (
  user_id   bigint(20) not null comment '用户ID',
  post_id   bigint(20) not null comment '岗位ID',
  primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

insert into sys_user_post values ('1', '1');


-- ----------------------------
-- 10、操作日志记录（框架 @Log 注解写入，必须保留表结构）
-- ----------------------------
drop table if exists sys_oper_log;
create table sys_oper_log (
  oper_id           bigint(20)      not null auto_increment    comment '日志主键',
  title             varchar(50)     default ''                 comment '模块标题',
  business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
  method            varchar(200)    default ''                 comment '方法名称',
  request_method    varchar(10)     default ''                 comment '请求方式',
  operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
  oper_name         varchar(50)     default ''                 comment '操作人员',
  dept_name         varchar(50)     default ''                 comment '部门名称',
  oper_url          varchar(255)    default ''                 comment '请求URL',
  oper_ip           varchar(128)    default ''                 comment '主机地址',
  oper_location     varchar(255)    default ''                 comment '操作地点',
  oper_param        varchar(2000)   default ''                 comment '请求参数',
  json_result       varchar(2000)   default ''                 comment '返回参数',
  status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
  error_msg         varchar(2000)   default ''                 comment '错误消息',
  oper_time         datetime                                   comment '操作时间',
  cost_time         bigint(20)      default 0                  comment '消耗时间',
  primary key (oper_id),
  key idx_sys_oper_log_bt (business_type),
  key idx_sys_oper_log_s  (status),
  key idx_sys_oper_log_ot (oper_time)
) engine=innodb auto_increment=100 comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表（框架启动加载，必须保留表结构）
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type (
  dict_id          bigint(20)      not null auto_increment    comment '字典主键',
  dict_name        varchar(100)    default ''                 comment '字典名称',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_id),
  unique (dict_type)
) engine=innodb auto_increment=100 comment = '字典类型表';

-- 仅保留框架内部必须用到的字典
insert into sys_dict_type values(1, '用户性别',  'sys_user_sex',       '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_type values(2, '菜单状态',  'sys_show_hide',      '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_type values(3, '系统开关',  'sys_normal_disable', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_type values(6, '系统是否',  'sys_yes_no',         '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_type values(10, '系统状态', 'sys_common_status',  '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 12、字典数据表（框架启动加载，必须保留表结构）
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data (
  dict_code        bigint(20)      not null auto_increment    comment '字典编码',
  dict_sort        int(4)          default 0                  comment '字典排序',
  dict_label       varchar(100)    default ''                 comment '字典标签',
  dict_value       varchar(100)    default ''                 comment '字典键值',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
  list_class       varchar(100)    default null               comment '表格回显样式',
  is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_code)
) engine=innodb auto_increment=100 comment = '字典数据表';

insert into sys_dict_data values(1, 1, '男',   '0', 'sys_user_sex',       '', '',        'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(2, 2, '女',   '1', 'sys_user_sex',       '', '',        'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(3, 3, '未知', '2', 'sys_user_sex',       '', '',        'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(4, 1, '显示', '0', 'sys_show_hide',      '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(5, 2, '隐藏', '1', 'sys_show_hide',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(12, 1, '是',  'Y', 'sys_yes_no',         '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(13, 2, '否',  'N', 'sys_yes_no',         '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(29, 2, '失败', '1', 'sys_common_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 13、参数配置表（框架必须）
-- ----------------------------
drop table if exists sys_config;
create table sys_config (
  config_id         int(5)          not null auto_increment    comment '参数主键',
  config_name       varchar(100)    default ''                 comment '参数名称',
  config_key        varchar(100)    default ''                 comment '参数键名',
  config_value      varchar(500)    default ''                 comment '参数键值',
  config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (config_id)
) engine=innodb auto_increment=100 comment = '参数配置表';

-- 仅保留框架启动必须的配置
insert into sys_config values(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName',            'skin-blue',  'Y', 'admin', sysdate(), '', null, '');
insert into sys_config values(2, '用户管理-账号初始密码',      'sys.user.initPassword',         'ChangeMe#2026', 'Y', 'admin', sysdate(), '', null, '');
insert into sys_config values(3, '主框架页-侧边栏主题',        'sys.index.sideTheme',           'theme-dark', 'Y', 'admin', sysdate(), '', null, '');
insert into sys_config values(4, '账号自助-验证码开关',        'sys.account.captchaEnabled',    'true',       'Y', 'admin', sysdate(), '', null, '');
insert into sys_config values(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser',    'false',      'Y', 'admin', sysdate(), '', null, '');
insert into sys_config values(6, '账号安全-初始密码提示修改',  'sys.account.initPasswordModify','1',          'Y', 'admin', sysdate(), '', null, '');
insert into sys_config values(7, '账号安全-密码有效天数',      'sys.account.passwordValidateDays','90',       'Y', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 14、系统访问记录（登录时写入，必须保留表结构）
-- ----------------------------
drop table if exists sys_logininfor;
create table sys_logininfor (
  info_id        bigint(20)     not null auto_increment   comment '访问ID',
  user_name      varchar(50)    default ''                comment '用户账号',
  ipaddr         varchar(128)   default ''                comment '登录IP地址',
  login_location varchar(255)   default ''                comment '登录地点',
  browser        varchar(50)    default ''                comment '浏览器类型',
  os             varchar(50)    default ''                comment '操作系统',
  status         char(1)        default '0'               comment '登录状态（0成功 1失败）',
  msg            varchar(255)   default ''                comment '提示消息',
  login_time     datetime                                 comment '访问时间',
  primary key (info_id),
  key idx_sys_logininfor_s  (status),
  key idx_sys_logininfor_lt (login_time)
) engine=innodb auto_increment=100 comment = '系统访问记录';


-- ----------------------------
-- 15、通知公告表（框架代码引用，必须保留表结构）
-- ----------------------------
drop table if exists sys_notice;
create table sys_notice (
  notice_id         int(4)          not null auto_increment    comment '公告ID',
  notice_title      varchar(50)     not null                   comment '公告标题',
  notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
  notice_content    longblob        default null               comment '公告内容',
  status            char(1)         default '0'                comment '公告状态（0正常 1关闭）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(255)    default null               comment '备注',
  primary key (notice_id)
) engine=innodb auto_increment=10 comment = '通知公告表';

-- END SOURCE: ry_20250522.sql

-- ============================================================
-- BEGIN SOURCE: fishing.sql
-- ============================================================
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
  KEY `idx_venue` (`venue_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_paid_time` (`paid_time`)
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

-- ----------------------------
-- 初始化默认计费规则与钓场
-- ----------------------------
INSERT INTO `fish_billing_rule` (`rule_id`, `rule_name`, `step_minutes`, `price_per_step_cents`, `min_duration_minutes`, `round_type`, `summary`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES (1, '标准计费', 30, 300, 30, 'ceil_step', '起步 30 分钟起计', '0', '0', 'admin', sysdate());

INSERT INTO `fish_venue` (`venue_id`, `name`, `address`, `notice`, `phone`, `rule_id`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES (1, '共享钓场', '', '', '', 1, '0', '0', 'admin', sysdate());

-- END SOURCE: fishing.sql

-- ============================================================
-- BEGIN SOURCE: fishing-refund.sql
-- ============================================================
-- 钓场退款表 + 菜单权限
-- 状态: 0 待审核, 1 已通过/退款中, 2 已完成, 3 已驳回, 4 退款失败

drop table if exists fish_refund;
create table fish_refund (
  refund_id            bigint(20)    not null auto_increment comment '退款主键',
  refund_no            varchar(40)   not null comment '退款单号',
  order_id             bigint(20)    not null comment '订单ID',
  order_no             varchar(40)   not null comment '订单号',
  user_id              bigint(20)    not null comment '用户ID',
  apply_amount_cents   int(11)       not null default 0 comment '申请退款金额(分)',
  refund_amount_cents  int(11)       null     default 0 comment '实际退款金额(分)',
  reason               varchar(200)  null     default '' comment '退款原因',
  status               tinyint(2)    not null default 0  comment '0待审核 1退款中 2已完成 3已驳回 4退款失败',
  audit_remark         varchar(200)  null     default '' comment '审批意见',
  audit_by             varchar(64)   null     default '' comment '审批人',
  audit_time           datetime      null     comment '审批时间',
  wx_refund_no         varchar(64)   null     default '' comment '微信退款单号',
  finish_time          datetime      null     comment '完成时间',
  create_by            varchar(64)   default '' comment '创建者',
  create_time          datetime      default current_timestamp comment '创建时间',
  update_by            varchar(64)   default '' comment '更新者',
  update_time          datetime      null comment '更新时间',
  primary key (refund_id),
  unique key uk_refund_no (refund_no),
  key idx_order_id (order_id),
  key idx_user_id (user_id),
  key idx_status (status)
) engine=innodb default charset=utf8mb4 comment '钓场订单退款';

-- 菜单：放在 "钓场管理" 目录下（parent_id 与 fishing.sql 中目录一致：实际写入时请按你的 fishing 目录 menu_id 调整）
-- 这里使用占位 @parent；如已知钓场目录 menu_id 可手工替换
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款管理', @parent, 9, 'refund', 'fishing/refund/index', 1, 0, 'C', '0', '0', 'fishing:refund:list', 'money', 'admin', sysdate(), '', null, '订单退款审批');

set @m := last_insert_id();

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('退款查询', @m, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:refund:query',   '#', 'admin', sysdate(), '', null, ''),
('退款审批', @m, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:refund:audit',   '#', 'admin', sysdate(), '', null, ''),
('退款导出', @m, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:refund:export',  '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-refund.sql

-- ============================================================
-- BEGIN SOURCE: fishing-mall.sql
-- ============================================================
-- 钓场商城：分类 + 商品 + 订单 + 订单明细 + 菜单权限

drop table if exists fish_mall_category;
create table fish_mall_category (
  cat_id      bigint(20)   not null auto_increment comment '分类ID',
  name        varchar(50)  not null comment '分类名称',
  icon        varchar(20)  default '' comment '图标(emoji 或 url)',
  sort        int(11)      default 0  comment '排序',
  status      char(1)      default '0' comment '0正常 1停用',
  create_by   varchar(64)  default '' ,
  create_time datetime     default current_timestamp,
  update_by   varchar(64)  default '' ,
  update_time datetime     null,
  primary key (cat_id),
  key idx_sort (sort)
) engine=innodb default charset=utf8mb4 comment '商城分类';

drop table if exists fish_mall_goods;
create table fish_mall_goods (
  goods_id    bigint(20)   not null auto_increment comment '商品ID',
  cat_id      bigint(20)   not null comment '分类ID',
  name        varchar(80)  not null comment '商品名',
  subtitle    varchar(120) default '' comment '副标题/卖点',
  cover       varchar(255) default '' comment '封面(emoji 或 url)',
  description varchar(1000) default '' comment '商品详情',
  price_cents int(11)      not null default 0 comment '售价(分)',
  stock       int(11)      not null default 0 comment '库存',
  sales       int(11)      not null default 0 comment '已售',
  sort        int(11)      default 0  comment '排序',
  status      char(1)      default '0' comment '0上架 1下架',
  create_by   varchar(64)  default '' ,
  create_time datetime     default current_timestamp,
  update_by   varchar(64)  default '' ,
  update_time datetime     null,
  primary key (goods_id),
  key idx_cat (cat_id),
  key idx_status (status)
) engine=innodb default charset=utf8mb4 comment '商城商品';

drop table if exists fish_mall_order;
create table fish_mall_order (
  mall_order_id   bigint(20)   not null auto_increment comment '订单ID',
  mall_order_no   varchar(40)  not null comment '订单号',
  user_id         bigint(20)   not null comment '用户ID',
  venue_id        bigint(20)   null comment '关联钓场',
  total_cents     int(11)      not null default 0 comment '合计金额(分)',
  amount_paid     int(11)      null     default 0 comment '实付金额(分)',
  status          tinyint(2)   not null default 0 comment '0待支付 1待核销 2已核销 3已取消',
  remark          varchar(200) default '' comment '用户备注',
  redeem_code     varchar(20)  default '' comment '核销码',
  pay_trade_no    varchar(64)  default '' comment '微信支付单号',
  paid_time       datetime     null,
  redeemed_time   datetime     null,
  redeemed_by     varchar(64)  default '' ,
  create_by       varchar(64)  default '' ,
  create_time     datetime     default current_timestamp,
  update_by       varchar(64)  default '' ,
  update_time     datetime     null,
  primary key (mall_order_id),
  unique key uk_mall_order_no (mall_order_no),
  key idx_user (user_id),
  key idx_redeem (redeem_code),
  key idx_status (status)
) engine=innodb default charset=utf8mb4 comment '商城订单';

drop table if exists fish_mall_order_item;
create table fish_mall_order_item (
  item_id        bigint(20)   not null auto_increment,
  mall_order_id  bigint(20)   not null,
  goods_id       bigint(20)   not null,
  name           varchar(80)  not null comment '快照',
  subtitle       varchar(120) default '',
  cover          varchar(255) default '',
  price_cents    int(11)      not null comment '下单价(分)',
  qty            int(11)      not null comment '数量',
  primary key (item_id),
  key idx_order (mall_order_id)
) engine=innodb default charset=utf8mb4 comment '商城订单明细';

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('商城分类', @parent, 21, 'mallCategory', 'fishing/mallCategory/index', 1, 0, 'C', '0', '0', 'fishing:mallCategory:list', 'tree', 'admin', sysdate(), '', null, '商城分类管理');
set @mc := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('分类查询', @mc, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallCategory:query',  '#', 'admin', sysdate(), '', null, ''),
('分类新增', @mc, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallCategory:add',    '#', 'admin', sysdate(), '', null, ''),
('分类修改', @mc, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallCategory:edit',   '#', 'admin', sysdate(), '', null, ''),
('分类删除', @mc, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallCategory:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('商城商品', @parent, 22, 'mallGoods', 'fishing/mallGoods/index', 1, 0, 'C', '0', '0', 'fishing:mallGoods:list', 'shopping', 'admin', sysdate(), '', null, '商城商品管理');
set @mg := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('商品查询', @mg, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallGoods:query',  '#', 'admin', sysdate(), '', null, ''),
('商品新增', @mg, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallGoods:add',    '#', 'admin', sysdate(), '', null, ''),
('商品修改', @mg, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallGoods:edit',   '#', 'admin', sysdate(), '', null, ''),
('商品删除', @mg, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallGoods:remove', '#', 'admin', sysdate(), '', null, ''),
('商品上下架', @mg, 5, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallGoods:toggle', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('商城订单', @parent, 23, 'mallOrder', 'fishing/mallOrder/index', 1, 0, 'C', '0', '0', 'fishing:mallOrder:list', 'documentation', 'admin', sysdate(), '', null, '商城订单 / 核销');
set @mo := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('订单查询', @mo, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallOrder:query',  '#', 'admin', sysdate(), '', null, ''),
('订单核销', @mo, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallOrder:redeem', '#', 'admin', sysdate(), '', null, ''),
('订单导出', @mo, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:mallOrder:export', '#', 'admin', sysdate(), '', null, '');

-- 示例数据
insert into fish_mall_category (cat_id, name, icon, sort) values
(2, '鱼饵', '🪱', 10),
(3, '渔具', '🎣', 20),
(4, '饮料', '🥤', 30),
(5, '小吃', '🍢', 40);

insert into fish_mall_goods (cat_id, name, subtitle, cover, description, price_cents, stock, sales) values
(2,'红虫(冻干)','40g · 经典款','🪱','低温冷冻保存红虫，开袋即用',1500,88,326),
(2,'玉米饵','300g · 散装','🌽','甜玉米发酵饵，野钓大鱼利器',800,120,412),
(3,'碳素鱼竿 5.4米','高碳素 · 28调','🎣','5.4 米高碳素台钓竿',28800,12,47),
(3,'伊势尼鱼钩','6号 · 一包10枚','🪝','黑色高碳钢，鲫鱼必备',1200,200,612),
(4,'冰镇可乐','330ml','🥤','冰柜直取',400,200,1245),
(4,'矿泉水','550ml','💧','5 元 3 瓶',200,500,2134),
(5,'现烤鸡翅','一份 · 4个','🍗','炭火现烤',1800,30,156),
(5,'泡面套餐','康师傅红烧牛肉','🍜','附热水+火腿肠+卤蛋',800,100,432);

-- END SOURCE: fishing-mall.sql

-- ============================================================
-- BEGIN SOURCE: fishing-staff.sql
-- ============================================================
-- 给小程序用户表加店员标记，老板/店员通过工作台核销商城订单
alter table fish_user add column is_staff tinyint(1) not null default 0 comment '0普通用户 1店员/老板' after status;

-- 把第一个用户标为店员（仅作示例，请按实际改）
-- update fish_user set is_staff = 1 where user_id = 1;

-- END SOURCE: fishing-staff.sql

-- ============================================================
-- BEGIN SOURCE: fishing-combined-pay.sql
-- ============================================================
-- 钓场结算时合并支付的商城订单：用 fish_order 上的字段记录关联商城订单 ID（逗号分隔）
alter table fish_order add column mall_order_ids varchar(255) not null default '' comment '合并支付的商城订单ID,逗号分隔' after rule_snapshot;

-- END SOURCE: fishing-combined-pay.sql

-- ============================================================
-- BEGIN SOURCE: fishing-balance.sql
-- ============================================================
-- 储值卡：用户余额 + 流水 + 充值套餐 + 充值订单

drop table if exists fish_user_balance;
create table fish_user_balance (
  user_id              bigint(20)  not null comment '用户ID(主键)',
  balance_cents        int(11)     not null default 0 comment '当前余额(分)',
  total_recharge_cents int(11)     not null default 0 comment '累计充值(分)',
  total_consumed_cents int(11)     not null default 0 comment '累计消费(分)',
  update_time          datetime    null,
  primary key (user_id)
) engine=innodb default charset=utf8mb4 comment '用户储值余额';

drop table if exists fish_balance_log;
create table fish_balance_log (
  log_id              bigint(20)   not null auto_increment,
  user_id             bigint(20)   not null,
  delta_cents         int(11)      not null comment '正=入账,负=扣减(分)',
  balance_after_cents int(11)      not null comment '本次后余额(分)',
  type                varchar(30)  not null comment 'recharge/gift/consume_fishing/consume_mall/refund/admin_adjust',
  related_order_no    varchar(40)  default '' comment '关联订单号',
  related_recharge_id bigint(20)   null,
  remark              varchar(200) default '',
  operator            varchar(64)  default '',
  create_time         datetime     default current_timestamp,
  primary key (log_id),
  key idx_user (user_id),
  key idx_type (type),
  key idx_create (create_time)
) engine=innodb default charset=utf8mb4 comment '余额流水';

drop table if exists fish_recharge_plan;
create table fish_recharge_plan (
  plan_id      bigint(20)   not null auto_increment,
  title        varchar(80)  default '' comment '展示名',
  amount_cents int(11)      not null comment '需支付金额(分)',
  bonus_cents  int(11)      not null default 0 comment '赠送金额(分)',
  badge        varchar(40)  default '' comment '角标,如"超值"',
  sort         int(11)      default 0,
  status       char(1)      default '0' comment '0上架 1下架',
  create_by    varchar(64)  default '',
  create_time  datetime     default current_timestamp,
  update_by    varchar(64)  default '',
  update_time  datetime     null,
  primary key (plan_id),
  key idx_status_sort (status, sort)
) engine=innodb default charset=utf8mb4 comment '充值套餐';

drop table if exists fish_recharge_order;
create table fish_recharge_order (
  recharge_id        bigint(20)   not null auto_increment,
  recharge_no        varchar(40)  not null comment '订单号(微信outTradeNo,前缀R)',
  user_id            bigint(20)   not null,
  plan_id            bigint(20)   null,
  amount_cents       int(11)      not null comment '支付金额(分)',
  bonus_cents        int(11)      not null default 0 comment '赠送金额(分)',
  total_credit_cents int(11)      not null comment '实际入账(=amount+bonus)',
  status             tinyint(2)   not null default 0 comment '0待支付 1已完成 2已取消',
  pay_trade_no       varchar(64)  default '',
  paid_time          datetime     null,
  create_time        datetime     default current_timestamp,
  update_time        datetime     null,
  primary key (recharge_id),
  unique key uk_recharge_no (recharge_no),
  key idx_user (user_id),
  key idx_status (status)
) engine=innodb default charset=utf8mb4 comment '充值订单';

-- 示例套餐
insert into fish_recharge_plan (title, amount_cents, bonus_cents, badge, sort) values
('日常补给', 5000, 0, '', 10),
('常用首选', 10000, 1000, '送10元', 20),
('热门推荐', 30000, 5000, '送50元 · 推荐', 30),
('超值储值', 50000, 12000, '送120元 · 超值', 40),
('钓王套餐', 100000, 30000, '送300元 · 立省23%', 50);

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('充值套餐', @parent, 31, 'rechargePlan', 'fishing/rechargePlan/index', 1, 0, 'C', '0', '0', 'fishing:rechargePlan:list', 'star', 'admin', sysdate(), '', null, '储值充值套餐管理');
set @rp := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('套餐查询', @rp, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargePlan:query',  '#', 'admin', sysdate(), '', null, ''),
('套餐新增', @rp, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargePlan:add',    '#', 'admin', sysdate(), '', null, ''),
('套餐修改', @rp, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargePlan:edit',   '#', 'admin', sysdate(), '', null, ''),
('套餐删除', @rp, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargePlan:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('充值订单', @parent, 32, 'rechargeOrder', 'fishing/rechargeOrder/index', 1, 0, 'C', '0', '0', 'fishing:rechargeOrder:list', 'money', 'admin', sysdate(), '', null, '充值订单 + 余额查看');
set @ro := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('充值订单查询', @ro, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargeOrder:query',  '#', 'admin', sysdate(), '', null, ''),
('余额调整',     @ro, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargeOrder:adjust', '#', 'admin', sysdate(), '', null, ''),
('充值订单导出', @ro, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:rechargeOrder:export', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-balance.sql

-- ============================================================
-- BEGIN SOURCE: fishing-balance-deduct.sql
-- ============================================================
-- 余额抵扣：给钓场订单和商城订单加记录字段
-- 执行前请先跑过 fishing-balance.sql（创建了 fish_user_balance / fish_balance_log）

alter table fish_order
  add column balance_cents int(11) not null default 0 comment '使用余额抵扣(分)' after amount_paid;

alter table fish_mall_order
  add column balance_cents int(11) not null default 0 comment '使用余额抵扣(分)' after amount_paid;

-- END SOURCE: fishing-balance-deduct.sql

-- ============================================================
-- BEGIN SOURCE: fishing-mall-refund.sql
-- ============================================================
-- 商城订单退款：复用 fish_refund 表，加 order_type 区分
-- 'fishing' = 钓场计时订单(默认)；'mall' = 商城订单

alter table fish_refund
  add column order_type varchar(20) not null default 'fishing' comment 'fishing|mall' after user_id;

-- 已有的退款都是钓场类型，不需要补数据

-- END SOURCE: fishing-mall-refund.sql

-- ============================================================
-- BEGIN SOURCE: fishing-stocking.sql
-- ============================================================
-- 放鱼通知：店家发布今日放鱼记录，小程序端展示

drop table if exists fish_stocking_record;
create table fish_stocking_record (
  record_id     bigint(20)   not null auto_increment,
  venue_id      bigint(20)   not null comment '钓场ID',
  fish_species  varchar(80)  not null default '' comment '鱼种(如鲤鱼、草鱼、鲫鱼)',
  weight_jin    decimal(10,2) not null default 0 comment '本次放鱼斤数',
  fish_count    int(11)      not null default 0 comment '尾数(可为0)',
  stocking_time datetime     not null comment '放鱼时间',
  image         varchar(255) default '' comment '现场图片URL',
  content       varchar(500) default '' comment '描述/规格说明',
  status        char(1)      not null default '0' comment '0已发布 1隐藏',
  del_flag      char(1)      not null default '0',
  create_by     varchar(64)  default '',
  create_time   datetime     default current_timestamp,
  update_by     varchar(64)  default '',
  update_time   datetime     null,
  remark        varchar(255) default '',
  primary key (record_id),
  key idx_venue_time (venue_id, stocking_time)
) engine=innodb default charset=utf8mb4 comment '放鱼记录';

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('放鱼记录', @parent, 33, 'stocking', 'fishing/stocking/index', 1, 0, 'C', '0', '0', 'fishing:stocking:list', 'log', 'admin', sysdate(), '', null, '店家发布放鱼通知');
set @sk := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('放鱼查询', @sk, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:stocking:query',  '#', 'admin', sysdate(), '', null, ''),
('放鱼新增', @sk, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:stocking:add',    '#', 'admin', sysdate(), '', null, ''),
('放鱼修改', @sk, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:stocking:edit',   '#', 'admin', sysdate(), '', null, ''),
('放鱼删除', @sk, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:stocking:remove', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-stocking.sql

-- ============================================================
-- BEGIN SOURCE: fishing-blacklist.sql
-- ============================================================
-- 黑名单：用户拉黑后禁止开新订单/下商城单/活动报名
alter table fish_user
  add column is_blacklist tinyint(1) not null default 0 comment '0正常 1黑名单' after is_staff,
  add column blacklist_reason varchar(255) default '' comment '拉黑原因' after is_blacklist;

-- 黑名单管理权限（细分按钮，挂在已有小程序用户菜单下）
set @userMenu := (select menu_id from sys_menu where perms = 'fishing:user:list' limit 1);
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('拉黑/解除', @userMenu, 10, '#', '', 1, 0, 'F', '0', '0', 'fishing:user:blacklist', '#', 'admin', sysdate(), '', null, '黑名单标记');

-- END SOURCE: fishing-blacklist.sql

-- ============================================================
-- BEGIN SOURCE: fishing-spot.sql
-- ============================================================
-- 钓位预订：钓位管理 + 预订记录

drop table if exists fish_spot;
create table fish_spot (
  spot_id       bigint(20)   not null auto_increment,
  venue_id      bigint(20)   not null comment '所属钓场',
  spot_name     varchar(60)  not null comment '钓位名称(如A1、VIP-01)',
  spot_type     varchar(30)  default 'normal' comment 'normal普通 vip贵宾',
  extra_fee_cents int(11)    not null default 0 comment '钓位附加费(分)，叠加到计时费上',
  capacity      int(11)      not null default 1 comment '可容纳人数',
  sort_num      int(11)      default 0,
  status        char(1)      not null default '0' comment '0可用 1维护中 2停用',
  description   varchar(255) default '',
  del_flag      char(1)      not null default '0',
  create_by     varchar(64)  default '',
  create_time   datetime     default current_timestamp,
  update_by     varchar(64)  default '',
  update_time   datetime     null,
  primary key (spot_id),
  key idx_venue (venue_id)
) engine=innodb default charset=utf8mb4 comment '钓位';

drop table if exists fish_reservation;
create table fish_reservation (
  reservation_id  bigint(20)   not null auto_increment,
  reservation_no  varchar(40)  not null comment '预订号(前缀B)',
  user_id         bigint(20)   not null,
  venue_id        bigint(20)   not null,
  spot_id         bigint(20)   not null,
  reserve_date    date         not null comment '预订日期',
  time_slot       varchar(40)  default '' comment '时段描述(如06:00-12:00)',
  deposit_cents   int(11)      not null default 0 comment '押金(分)',
  status          tinyint(2)   not null default 0 comment '0待确认 1已确认 2已到场 3已取消 4超时释放',
  cancel_reason   varchar(200) default '',
  arrive_time     datetime     null comment '实际到场时间',
  expire_time     datetime     not null comment '超时未到自动释放时间',
  pay_trade_no    varchar(64)  default '' comment '押金支付流水号',
  refund_status   tinyint(2)   default 0 comment '0无需退 1已退押金',
  create_time     datetime     default current_timestamp,
  update_time     datetime     null,
  primary key (reservation_id),
  unique key uk_no (reservation_no),
  key idx_user (user_id),
  key idx_spot_date (spot_id, reserve_date),
  key idx_expire (status, expire_time)
) engine=innodb default charset=utf8mb4 comment '钓位预订';

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('钓位管理', @parent, 34, 'spot', 'fishing/spot/index', 1, 0, 'C', '0', '0', 'fishing:spot:list', 'tree', 'admin', sysdate(), '', null, '钓位管理');
set @sp := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('钓位查询', @sp, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:spot:query',  '#', 'admin', sysdate(), '', null, ''),
('钓位新增', @sp, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:spot:add',    '#', 'admin', sysdate(), '', null, ''),
('钓位修改', @sp, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:spot:edit',   '#', 'admin', sysdate(), '', null, ''),
('钓位删除', @sp, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:spot:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('预订记录', @parent, 35, 'reservation', 'fishing/reservation/index', 1, 0, 'C', '0', '0', 'fishing:reservation:list', 'date', 'admin', sysdate(), '', null, '预订记录管理');
set @rv := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('预订查询', @rv, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:reservation:query',  '#', 'admin', sysdate(), '', null, ''),
('预订确认', @rv, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:reservation:confirm','#', 'admin', sysdate(), '', null, ''),
('预订取消', @rv, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:reservation:cancel', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-spot.sql

-- ============================================================
-- BEGIN SOURCE: fishing-catch.sql
-- ============================================================
-- 钓获打卡：用户晒鱼 + 后台审核/精选

drop table if exists fish_catch_record;
create table fish_catch_record (
  catch_id      bigint(20)   not null auto_increment,
  user_id       bigint(20)   not null,
  venue_id      bigint(20)   null comment '关联钓场',
  order_id      bigint(20)   null comment '关联订单(可空)',
  fish_species  varchar(80)  default '' comment '鱼种',
  weight_jin    decimal(10,2) default 0 comment '渔获重量(斤)',
  fish_count    int(11)      default 0 comment '尾数',
  images        varchar(1000) default '' comment '图片URL,逗号分隔(最多3张)',
  content       varchar(500) default '' comment '心得/描述',
  fishing_method varchar(60) default '' comment '钓法(台钓/路亚/筏钓等)',
  is_featured   tinyint(1)   not null default 0 comment '0普通 1精选(首页展示)',
  like_count    int(11)      not null default 0,
  status        tinyint(2)   not null default 0 comment '0待审核 1已通过 2已拒绝',
  reject_reason varchar(200) default '',
  del_flag      char(1)      not null default '0',
  create_by     varchar(64)  default '',
  create_time   datetime     default current_timestamp,
  update_by     varchar(64)  default '',
  update_time   datetime     null,
  primary key (catch_id),
  key idx_user (user_id),
  key idx_venue (venue_id),
  key idx_featured (is_featured, status, create_time)
) engine=innodb default charset=utf8mb4 comment '钓获打卡';

-- 点赞记录(防重复)
drop table if exists fish_catch_like;
create table fish_catch_like (
  user_id   bigint(20) not null,
  catch_id  bigint(20) not null,
  create_time datetime default current_timestamp,
  primary key (user_id, catch_id),
  key idx_catch (catch_id)
) engine=innodb default charset=utf8mb4 comment '钓获点赞';

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('钓获打卡', @parent, 36, 'catch', 'fishing/catch/index', 1, 0, 'C', '0', '0', 'fishing:catch:list', 'peoples', 'admin', sysdate(), '', null, '用户晒鱼审核');
set @ct := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('打卡查询', @ct, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:catch:query',    '#', 'admin', sysdate(), '', null, ''),
('打卡审核', @ct, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:catch:audit',    '#', 'admin', sysdate(), '', null, ''),
('设为精选', @ct, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:catch:feature',  '#', 'admin', sysdate(), '', null, ''),
('打卡删除', @ct, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:catch:remove',   '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-catch.sql

-- ============================================================
-- BEGIN SOURCE: fishing-member-level.sql
-- ============================================================
-- 会员等级：等级配置 + 用户等级字段

drop table if exists fish_member_level;
create table fish_member_level (
  level_id          bigint(20)   not null auto_increment,
  level_name        varchar(40)  not null comment '等级名称(青铜/白银/黄金/黑金)',
  level_icon        varchar(255) default '' comment '等级图标URL',
  min_consume_cents int(11)      not null default 0 comment '累计消费门槛(分)',
  discount_rate     int(11)      not null default 100 comment '折扣(百分比,如90=9折)',
  free_deposit      tinyint(1)   not null default 0 comment '是否免押金预订',
  priority_reserve  tinyint(1)   not null default 0 comment '是否优先订位',
  extra_benefits    varchar(500) default '' comment '其他权益描述',
  sort_num          int(11)      default 0,
  status            char(1)      not null default '0' comment '0启用 1停用',
  create_time       datetime     default current_timestamp,
  update_time       datetime     null,
  primary key (level_id),
  key idx_threshold (min_consume_cents)
) engine=innodb default charset=utf8mb4 comment '会员等级配置';

-- 默认等级
insert into fish_member_level (level_name, min_consume_cents, discount_rate, free_deposit, priority_reserve, sort_num) values
('青铜', 0, 100, 0, 0, 1),
('白银', 50000, 95, 0, 0, 2),
('黄金', 200000, 90, 1, 0, 3),
('黑金', 500000, 85, 1, 1, 4);

-- 用户表加等级字段
alter table fish_user
  add column member_level_id bigint(20) null comment '当前会员等级ID' after blacklist_reason,
  add column member_level_name varchar(40) default '' comment '冗余等级名' after member_level_id;

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('会员等级', @parent, 37, 'memberLevel', 'fishing/memberLevel/index', 1, 0, 'C', '0', '0', 'fishing:memberLevel:list', 'peoples', 'admin', sysdate(), '', null, '会员等级配置');
set @ml := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('等级查询', @ml, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:memberLevel:query',  '#', 'admin', sysdate(), '', null, ''),
('等级新增', @ml, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:memberLevel:add',    '#', 'admin', sysdate(), '', null, ''),
('等级修改', @ml, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:memberLevel:edit',   '#', 'admin', sysdate(), '', null, ''),
('等级删除', @ml, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:memberLevel:remove', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-member-level.sql

-- ============================================================
-- BEGIN SOURCE: fishing-points.sql
-- ============================================================
-- 积分系统：用户积分 + 积分流水 + 积分商品 + 兑换记录

-- 用户表加积分字段
alter table fish_user
  add column points int(11) not null default 0 comment '当前可用积分' after member_level_name;

-- 积分流水
drop table if exists fish_points_log;
create table fish_points_log (
  log_id        bigint(20)   not null auto_increment,
  user_id       bigint(20)   not null,
  delta         int(11)      not null comment '正=获得,负=消耗',
  points_after  int(11)      not null comment '本次后积分',
  type          varchar(30)  not null comment 'consume/checkin/catch/invite/exchange/admin',
  related_id    varchar(60)  default '' comment '关联ID(订单号/打卡ID等)',
  remark        varchar(200) default '',
  create_time   datetime     default current_timestamp,
  primary key (log_id),
  key idx_user (user_id),
  key idx_type (type),
  key idx_time (create_time)
) engine=innodb default charset=utf8mb4 comment '积分流水';

-- 积分商品(可兑换的奖品)
drop table if exists fish_points_goods;
create table fish_points_goods (
  goods_id      bigint(20)   not null auto_increment,
  name          varchar(100) not null comment '商品名称',
  image         varchar(255) default '',
  type          varchar(30)  not null default 'coupon' comment 'coupon优惠券/goods实物/duration免费时长',
  points_cost   int(11)      not null comment '所需积分',
  stock         int(11)      not null default 999 comment '库存',
  related_id    bigint(20)   null comment '关联ID(优惠券模板ID/商城商品ID等)',
  duration_minutes int(11)   default 0 comment 'type=duration时的免费分钟数',
  sort_num      int(11)      default 0,
  status        char(1)      not null default '0' comment '0上架 1下架',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (goods_id),
  key idx_status (status, sort_num)
) engine=innodb default charset=utf8mb4 comment '积分商品';

-- 兑换记录
drop table if exists fish_points_exchange;
create table fish_points_exchange (
  exchange_id   bigint(20)   not null auto_increment,
  user_id       bigint(20)   not null,
  goods_id      bigint(20)   not null,
  goods_name    varchar(100) default '',
  points_cost   int(11)      not null,
  status        tinyint(2)   not null default 0 comment '0待发放 1已发放 2已取消',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (exchange_id),
  key idx_user (user_id)
) engine=innodb default charset=utf8mb4 comment '积分兑换记录';

-- 签到记录(防重复)
drop table if exists fish_checkin;
create table fish_checkin (
  user_id       bigint(20)   not null,
  checkin_date  date         not null,
  points_earned int(11)      not null default 0,
  create_time   datetime     default current_timestamp,
  primary key (user_id, checkin_date)
) engine=innodb default charset=utf8mb4 comment '签到记录';

-- 示例积分商品
insert into fish_points_goods (name, type, points_cost, stock, sort_num) values
('5元优惠券', 'coupon', 200, 999, 1),
('10元优惠券', 'coupon', 380, 999, 2),
('免费30分钟', 'duration', 500, 999, 3),
('免费1小时', 'duration', 900, 999, 4),
('定制鱼护', 'goods', 5000, 10, 5);

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('积分商品', @parent, 38, 'pointsGoods', 'fishing/pointsGoods/index', 1, 0, 'C', '0', '0', 'fishing:pointsGoods:list', 'shopping', 'admin', sysdate(), '', null, '积分商品管理');
set @pg := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('积分商品查询', @pg, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:pointsGoods:query',  '#', 'admin', sysdate(), '', null, ''),
('积分商品新增', @pg, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:pointsGoods:add',    '#', 'admin', sysdate(), '', null, ''),
('积分商品修改', @pg, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:pointsGoods:edit',   '#', 'admin', sysdate(), '', null, ''),
('积分商品删除', @pg, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:pointsGoods:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('兑换记录', @parent, 39, 'pointsExchange', 'fishing/pointsExchange/index', 1, 0, 'C', '0', '0', 'fishing:pointsExchange:list', 'list', 'admin', sysdate(), '', null, '积分兑换记录');
set @pe := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('兑换查询', @pe, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:pointsExchange:query',  '#', 'admin', sysdate(), '', null, ''),
('兑换发放', @pe, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:pointsExchange:deliver', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-points.sql

-- ============================================================
-- BEGIN SOURCE: fishing-group.sql
-- ============================================================
-- 拼场约钓：用户发起拼场，其他人加入，满员自动锁位

drop table if exists fish_group_fishing;
create table fish_group_fishing (
  group_id      bigint(20)   not null auto_increment,
  user_id       bigint(20)   not null comment '发起人',
  venue_id      bigint(20)   not null,
  spot_id       bigint(20)   null comment '指定钓位(可空=不限位)',
  title         varchar(100) not null comment '标题(如"周六上午拼钓鲤鱼")',
  fishing_date  date         not null comment '拼场日期',
  time_slot     varchar(40)  default '' comment '时段(06:00-12:00)',
  max_members   int(11)      not null default 4 comment '最大人数(含发起人)',
  current_count int(11)      not null default 1 comment '当前人数',
  description   varchar(500) default '' comment '描述/要求',
  status        tinyint(2)   not null default 0 comment '0招募中 1已满员 2已完成 3已取消',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (group_id),
  key idx_venue_date (venue_id, fishing_date),
  key idx_user (user_id),
  key idx_status (status)
) engine=innodb default charset=utf8mb4 comment '拼场活动';

drop table if exists fish_group_member;
create table fish_group_member (
  id            bigint(20)   not null auto_increment,
  group_id      bigint(20)   not null,
  user_id       bigint(20)   not null,
  role          varchar(20)  not null default 'member' comment 'creator/member',
  status        tinyint(2)   not null default 0 comment '0已加入 1已退出',
  join_time     datetime     default current_timestamp,
  quit_time     datetime     null,
  primary key (id),
  unique key uk_group_user (group_id, user_id),
  key idx_user (user_id)
) engine=innodb default charset=utf8mb4 comment '拼场成员';

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('拼场管理', @parent, 40, 'groupFishing', 'fishing/groupFishing/index', 1, 0, 'C', '0', '0', 'fishing:group:list', 'peoples', 'admin', sysdate(), '', null, '拼场约钓管理');
set @gf := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('拼场查询', @gf, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:group:query',  '#', 'admin', sysdate(), '', null, ''),
('拼场取消', @gf, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:group:cancel', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-group.sql

-- ============================================================
-- BEGIN SOURCE: fishing-rental.sql
-- ============================================================
-- 装备租赁：租赁商品 + 租赁订单

drop table if exists fish_rental_goods;
create table fish_rental_goods (
  goods_id      bigint(20)   not null auto_increment,
  name          varchar(100) not null comment '装备名称',
  image         varchar(255) default '',
  category      varchar(40)  default '' comment '分类(钓椅/抄网/饵料/鱼竿等)',
  deposit_cents int(11)      not null default 0 comment '押金(分)',
  rent_cents    int(11)      not null default 0 comment '租金(分/次或分/小时)',
  rent_unit     varchar(20)  not null default 'per_use' comment 'per_use按次 per_hour按小时',
  stock         int(11)      not null default 1 comment '可租数量',
  description   varchar(500) default '',
  sort_num      int(11)      default 0,
  status        char(1)      not null default '0' comment '0上架 1下架',
  del_flag      char(1)      not null default '0',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (goods_id),
  key idx_status (status, sort_num)
) engine=innodb default charset=utf8mb4 comment '租赁装备';

drop table if exists fish_rental_order;
create table fish_rental_order (
  order_id      bigint(20)   not null auto_increment,
  order_no      varchar(40)  not null comment '租赁单号(前缀L)',
  user_id       bigint(20)   not null,
  goods_id      bigint(20)   not null,
  goods_name    varchar(100) default '',
  deposit_cents int(11)      not null default 0 comment '押金',
  rent_cents    int(11)      not null default 0 comment '租金',
  status        tinyint(2)   not null default 0 comment '0租借中 1已归还 2已取消 3押金扣除(损坏)',
  rent_time     datetime     not null comment '租借时间',
  return_time   datetime     null comment '归还时间',
  deposit_refunded tinyint(1) not null default 0 comment '0未退 1已退押金',
  remark        varchar(200) default '',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (order_id),
  unique key uk_no (order_no),
  key idx_user (user_id),
  key idx_status (status)
) engine=innodb default charset=utf8mb4 comment '租赁订单';

-- 示例装备
insert into fish_rental_goods (name, category, deposit_cents, rent_cents, rent_unit, stock, sort_num) values
('折叠钓椅', '钓椅', 5000, 1000, 'per_use', 10, 1),
('碳素抄网', '抄网', 3000, 500, 'per_use', 8, 2),
('3.6m鱼竿', '鱼竿', 10000, 2000, 'per_use', 5, 3),
('活饵盒(蚯蚓)', '饵料', 0, 1500, 'per_use', 20, 4);

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('租赁装备', @parent, 41, 'rentalGoods', 'fishing/rentalGoods/index', 1, 0, 'C', '0', '0', 'fishing:rental:list', 'component', 'admin', sysdate(), '', null, '装备租赁管理');
set @rg := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('租赁查询', @rg, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:rental:query',  '#', 'admin', sysdate(), '', null, ''),
('租赁新增', @rg, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:rental:add',    '#', 'admin', sysdate(), '', null, ''),
('租赁修改', @rg, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:rental:edit',   '#', 'admin', sysdate(), '', null, ''),
('租赁删除', @rg, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:rental:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('租赁订单', @parent, 42, 'rentalOrder', 'fishing/rentalOrder/index', 1, 0, 'C', '0', '0', 'fishing:rentalOrder:list', 'log', 'admin', sysdate(), '', null, '租赁订单管理');
set @ro2 := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('租赁订单查询', @ro2, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:rentalOrder:query',   '#', 'admin', sysdate(), '', null, ''),
('确认归还',     @ro2, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:rentalOrder:return',  '#', 'admin', sysdate(), '', null, ''),
('扣除押金',     @ro2, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:rentalOrder:forfeit', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-rental.sql

-- ============================================================
-- BEGIN SOURCE: fishing-competition.sql
-- ============================================================
-- 比赛称重：比赛活动 + 参赛选手(含称重) + 排行榜

drop table if exists fish_competition;
create table fish_competition (
  comp_id       bigint(20)   not null auto_increment,
  ad_id         bigint(20)   null comment '关联活动(fish_ad)',
  venue_id      bigint(20)   not null,
  title         varchar(100) not null comment '比赛名称',
  comp_date     date         not null comment '比赛日期',
  time_slot     varchar(40)  default '',
  max_players   int(11)      default 0 comment '最大参赛人数(0=不限)',
  entry_fee_cents int(11)    default 0 comment '报名费(分)',
  prize_pool_cents int(11)   default 0 comment '奖池总额(分)',
  prize_rules   varchar(1000) default '' comment '奖金分配规则(JSON: [{rank:1,amount:5000},{rank:2,amount:3000}])',
  fish_species  varchar(80)  default '' comment '目标鱼种',
  rules         varchar(1000) default '' comment '比赛规则',
  status        tinyint(2)   not null default 0 comment '0报名中 1进行中 2称重中 3已结束 4已取消',
  create_by     varchar(64)  default '',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (comp_id),
  key idx_venue (venue_id),
  key idx_date (comp_date)
) engine=innodb default charset=utf8mb4 comment '比赛活动';

drop table if exists fish_competition_entry;
create table fish_competition_entry (
  entry_id      bigint(20)   not null auto_increment,
  comp_id       bigint(20)   not null,
  user_id       bigint(20)   not null,
  nickname      varchar(60)  default '',
  phone         varchar(20)  default '',
  weight_gram   int(11)      not null default 0 comment '称重(克)',
  fish_count    int(11)      not null default 0 comment '尾数',
  ranking       int(11)      default 0 comment '排名(结算后填入)',
  prize_cents   int(11)      default 0 comment '获奖金额(分)',
  prize_status  tinyint(2)   default 0 comment '0未发 1已发到钱包',
  weigh_time    datetime     null comment '称重时间',
  weigh_by      varchar(64)  default '' comment '称重人(店员)',
  weigh_image   varchar(255) default '' comment '称重照片',
  status        tinyint(2)   not null default 0 comment '0已报名 1已签到 2已称重 3已弃权',
  create_time   datetime     default current_timestamp,
  primary key (entry_id),
  unique key uk_comp_user (comp_id, user_id),
  key idx_comp_weight (comp_id, weight_gram)
) engine=innodb default charset=utf8mb4 comment '参赛选手';

-- 菜单
set @parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values ('比赛管理', @parent, 43, 'competition', 'fishing/competition/index', 1, 0, 'C', '0', '0', 'fishing:competition:list', 'skill', 'admin', sysdate(), '', null, '比赛称重管理');
set @cp := last_insert_id();
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) values
('比赛查询', @cp, 1, '#', '', 1, 0, 'F', '0', '0', 'fishing:competition:query',  '#', 'admin', sysdate(), '', null, ''),
('比赛新增', @cp, 2, '#', '', 1, 0, 'F', '0', '0', 'fishing:competition:add',    '#', 'admin', sysdate(), '', null, ''),
('比赛修改', @cp, 3, '#', '', 1, 0, 'F', '0', '0', 'fishing:competition:edit',   '#', 'admin', sysdate(), '', null, ''),
('称重录入', @cp, 4, '#', '', 1, 0, 'F', '0', '0', 'fishing:competition:weigh',  '#', 'admin', sysdate(), '', null, ''),
('结算发奖', @cp, 5, '#', '', 1, 0, 'F', '0', '0', 'fishing:competition:settle', '#', 'admin', sysdate(), '', null, '');

-- END SOURCE: fishing-competition.sql

-- ============================================================
-- BEGIN SOURCE: fishing-catch-comment.sql
-- ============================================================
-- 钓获评论：用户对钓获打卡进行评论

drop table if exists fish_catch_comment;
create table fish_catch_comment (
  comment_id    bigint(20)   not null auto_increment,
  catch_id      bigint(20)   not null comment '关联钓获ID',
  user_id       bigint(20)   not null comment '评论人',
  reply_to_id   bigint(20)   null comment '回复的评论ID(null=顶级评论)',
  reply_to_user bigint(20)   null comment '被回复人ID',
  content       varchar(500) not null comment '评论内容',
  status        tinyint(2)   not null default 1 comment '0待审核 1正常 2已删除',
  create_time   datetime     default current_timestamp,
  primary key (comment_id),
  key idx_catch (catch_id, status, create_time),
  key idx_user (user_id)
) engine=innodb default charset=utf8mb4 comment '钓获评论';

-- 给 fish_catch_record 加评论数冗余
alter table fish_catch_record add column comment_count int(11) not null default 0 after like_count;

-- END SOURCE: fishing-catch-comment.sql

SET FOREIGN_KEY_CHECKS = 1;


