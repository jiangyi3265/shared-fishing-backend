-- 2026-07-28
-- 微信头像昵称主动填写、线上支付 1 元得 5 积分（点击领取）、
-- “极智鱼鉴 · 龙水湖篇”电子鱼卡循环收集活动。

-- 一、积分待领取奖励
create table if not exists fish_points_reward (
  reward_id     bigint(20)   not null auto_increment,
  user_id       bigint(20)   not null,
  source_type   varchar(30)  not null comment 'fishing/mall/weigh',
  source_no     varchar(64)  not null,
  amount_cents  int(11)      not null comment '微信线上实付金额(分)',
  points        int(11)      not null comment '可领取积分',
  status        tinyint(2)   not null default 0 comment '0待领取 1已领取',
  claimed_time  datetime     null,
  create_time   datetime     default current_timestamp,
  primary key (reward_id),
  unique key uk_points_reward_source (source_no),
  key idx_points_reward_user (user_id, status, create_time)
) engine=innodb default charset=utf8mb4 comment '线上消费积分待领取奖励';

-- 二、钓获记录增加认证视频和鱼卡关联（允许部署重试）
set @ddl = if(
  exists(select 1 from information_schema.columns where table_schema = database() and table_name = 'fish_catch_record' and column_name = 'video_url'),
  'select 1',
  "alter table fish_catch_record add column video_url varchar(500) default '' comment '钓获及放流认证视频' after images"
);
prepare stmt from @ddl; execute stmt; deallocate prepare stmt;

set @ddl = if(
  exists(select 1 from information_schema.columns where table_schema = database() and table_name = 'fish_catch_record' and column_name = 'card_round_id'),
  'select 1',
  "alter table fish_catch_record add column card_round_id bigint(20) null comment '电子鱼卡轮次' after fishing_method"
);
prepare stmt from @ddl; execute stmt; deallocate prepare stmt;

set @ddl = if(
  exists(select 1 from information_schema.columns where table_schema = database() and table_name = 'fish_catch_record' and column_name = 'card_species_id'),
  'select 1',
  "alter table fish_catch_record add column card_species_id bigint(20) null comment '电子鱼卡鱼种' after card_round_id"
);
prepare stmt from @ddl; execute stmt; deallocate prepare stmt;

set @ddl = if(
  exists(select 1 from information_schema.statistics where table_schema = database() and table_name = 'fish_catch_record' and index_name = 'idx_card_round'),
  'select 1',
  'alter table fish_catch_record add key idx_card_round (card_round_id)'
);
prepare stmt from @ddl; execute stmt; deallocate prepare stmt;

set @ddl = if(
  exists(select 1 from information_schema.statistics where table_schema = database() and table_name = 'fish_catch_record' and index_name = 'idx_card_species'),
  'select 1',
  'alter table fish_catch_record add key idx_card_species (card_species_id)'
);
prepare stmt from @ddl; execute stmt; deallocate prepare stmt;

-- 三、可配置鱼卡活动
create table if not exists fish_card_campaign (
  campaign_id   bigint(20)   not null auto_increment,
  campaign_key  varchar(80)  not null,
  venue_id      bigint(20)   null,
  title         varchar(120) not null,
  subtitle      varchar(200) default '',
  rules         varchar(1000) default '',
  start_time    datetime     not null,
  end_time      datetime     not null,
  reward_cents  int(11)      not null default 6600 comment '单轮个人奖励',
  status        char(1)      not null default '0' comment '0启用 1停用',
  create_time   datetime     default current_timestamp,
  update_time   datetime     null,
  primary key (campaign_id),
  unique key uk_card_campaign_key (campaign_key),
  key idx_card_campaign_time (status, start_time, end_time)
) engine=innodb default charset=utf8mb4 comment '电子鱼卡活动';

create table if not exists fish_card_species (
  species_id    bigint(20)   not null auto_increment,
  campaign_id   bigint(20)   not null,
  species_name  varchar(80)  not null,
  card_image    varchar(500) default '',
  sort_num      int(11)      not null default 0,
  status        char(1)      not null default '0',
  create_time   datetime     default current_timestamp,
  primary key (species_id),
  unique key uk_card_species (campaign_id, species_name),
  key idx_card_species_sort (campaign_id, status, sort_num)
) engine=innodb default charset=utf8mb4 comment '电子鱼卡鱼种';

create table if not exists fish_card_round (
  round_id          bigint(20)   not null auto_increment,
  campaign_id       bigint(20)   not null,
  user_id           bigint(20)   not null,
  round_no          int(11)      not null,
  status            tinyint(2)   not null default 0 comment '0收集中 1已集齐',
  started_time      datetime     not null,
  completed_time    datetime     null,
  duration_seconds  int(11)      null,
  reward_cents      int(11)      not null default 6600,
  reward_status     tinyint(2)   not null default 0 comment '0待发放 1已发放',
  reward_paid_time  datetime     null,
  reward_paid_by    varchar(64)  default '',
  create_time       datetime     default current_timestamp,
  update_time       datetime     null,
  primary key (round_id),
  unique key uk_card_round (campaign_id, user_id, round_no),
  key idx_card_round_open (campaign_id, user_id, status),
  key idx_card_ranking (campaign_id, status, duration_seconds),
  key idx_card_reward (status, reward_status, completed_time)
) engine=innodb default charset=utf8mb4 comment '用户电子鱼卡收集轮次';

create table if not exists fish_card_progress (
  progress_id    bigint(20)   not null auto_increment,
  round_id       bigint(20)   not null,
  species_id     bigint(20)   not null,
  catch_id       bigint(20)   not null,
  status         tinyint(2)   not null default 0 comment '0待审核 1已获得 2已拒绝',
  submitted_time datetime     not null,
  reviewed_time  datetime     null,
  create_time    datetime     default current_timestamp,
  update_time    datetime     null,
  primary key (progress_id),
  unique key uk_card_progress (round_id, species_id),
  unique key uk_card_progress_catch (catch_id),
  key idx_card_progress_status (round_id, status)
) engine=innodb default charset=utf8mb4 comment '电子鱼卡收集进度';

-- 四、龙水湖篇活动：6种常见鱼 + 4张待解锁鱼卡
insert ignore into fish_card_campaign
  (campaign_key, venue_id, title, subtitle, rules, start_time, end_time, reward_cents, status)
values
  ('smart-fish-atlas-longshuihu-2026', 1, '极智鱼鉴 · 龙水湖篇',
   '集齐6种常见鱼，获得66元奖励',
   '认证鱼种：小白条、鲫鱼、鲤鱼、草鱼、花鲢、黄辣丁。清晰拍摄钓获、说出鱼种，并记录完整放回鱼塘过程；后台审核通过后鱼卡点亮。',
   '2026-08-01 00:00:00', '2026-10-30 23:59:59', 6600, '0');

set @fish_card_campaign_id := (
  select campaign_id from fish_card_campaign
  where campaign_key = 'smart-fish-atlas-longshuihu-2026' limit 1
);

insert ignore into fish_card_species (campaign_id, species_name, card_image, sort_num, status) values
(@fish_card_campaign_id, '小白条', '/static/fish-card-atlas.png', 1,  '0'),
(@fish_card_campaign_id, '鲫鱼',   '/static/fish-card-atlas.png', 2,  '0'),
(@fish_card_campaign_id, '鲤鱼',   '/static/fish-card-atlas.png', 3,  '0'),
(@fish_card_campaign_id, '草鱼',   '/static/fish-card-atlas.png', 4,  '0'),
(@fish_card_campaign_id, '花鲢',   '/static/fish-card-atlas.png', 5,  '0'),
(@fish_card_campaign_id, '黄辣丁', '/static/fish-card-atlas.png', 6,  '0'),
(@fish_card_campaign_id, '翘嘴',   '/static/fish-card-atlas.png', 101, '1'),
(@fish_card_campaign_id, '鳜鱼',   '/static/fish-card-atlas.png', 102, '1'),
(@fish_card_campaign_id, '青鱼',   '/static/fish-card-atlas.png', 103, '1'),
(@fish_card_campaign_id, '黑鱼',   '/static/fish-card-atlas.png', 104, '1');

-- 五、管理后台“鱼鉴奖励”菜单
set @fish_parent := (select menu_id from sys_menu where menu_name = '钓场管理' limit 1);
insert into sys_menu
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
   visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '鱼鉴奖励', @fish_parent, 37, 'fishCard', 'fishing/fishCard/index', 1, 0, 'C',
       '0', '0', 'fishing:fishCard:list', 'star', 'admin', sysdate(), '', null, '电子鱼卡集齐与奖励发放'
where not exists (select 1 from sys_menu where perms = 'fishing:fishCard:list');

set @fish_card_menu := (select menu_id from sys_menu where perms = 'fishing:fishCard:list' limit 1);
insert into sys_menu
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
   visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '奖励发放', @fish_card_menu, 1, '#', '', 1, 0, 'F',
       '0', '0', 'fishing:fishCard:pay', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where perms = 'fishing:fishCard:pay');

insert into sys_menu
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
   visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '鱼鉴审核', @fish_card_menu, 2, '#', '', 1, 0, 'F',
       '0', '0', 'fishing:fishCard:audit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where perms = 'fishing:fishCard:audit');
