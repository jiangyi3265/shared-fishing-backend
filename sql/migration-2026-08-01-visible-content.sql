-- 默认钓场可见内容初始化（可重复执行）
-- 依赖：fish_order、fish_spot、fish_qrcode、fish_competition、fish_card_* 表已经创建。

-- 一、计时订单记录具体钓位（可重复执行）
set @has_order_spot := (
  select count(*) from information_schema.columns
  where table_schema = database() and table_name = 'fish_order' and column_name = 'spot_id'
);
set @add_order_spot_sql := if(
  @has_order_spot = 0,
  'alter table fish_order add column spot_id bigint(20) null comment ''计时钓位ID'' after venue_id',
  'select 1'
);
prepare add_order_spot_stmt from @add_order_spot_sql;
execute add_order_spot_stmt;
deallocate prepare add_order_spot_stmt;

set @has_order_spot_index := (
  select count(*) from information_schema.statistics
  where table_schema = database() and table_name = 'fish_order' and index_name = 'idx_spot_status'
);
set @add_order_spot_index_sql := if(
  @has_order_spot_index = 0,
  'alter table fish_order add key idx_spot_status (spot_id, status)',
  'select 1'
);
prepare add_order_spot_index_stmt from @add_order_spot_index_sql;
execute add_order_spot_index_stmt;
deallocate prepare add_order_spot_index_stmt;

-- 二、圆形鱼塘 20 个可预订钓位
insert into fish_spot
  (venue_id, spot_name, spot_type, extra_fee_cents, capacity, sort_num, status,
   description, del_flag, create_by, create_time)
select seed.venue_id, seed.spot_name, seed.spot_type, seed.extra_fee_cents,
       seed.capacity, seed.sort_num, '0', seed.description, '0', 'admin', sysdate()
from (
  select 1 venue_id, '01号钓位' spot_name, 'normal' spot_type, 0 extra_fee_cents, 1 capacity, 1 sort_num, '北岸标准钓位' description
  union all select 1, '02号钓位', 'normal', 0, 1, 2, '北岸标准钓位'
  union all select 1, '03号钓位', 'normal', 0, 1, 3, '北岸标准钓位'
  union all select 1, '04号钓位', 'normal', 0, 1, 4, '北岸标准钓位'
  union all select 1, '05号钓位', 'normal', 0, 1, 5, '北岸标准钓位'
  union all select 1, '06号钓位', 'normal', 0, 1, 6, '东北岸标准钓位'
  union all select 1, '07号钓位', 'normal', 0, 1, 7, '东岸标准钓位'
  union all select 1, '08号钓位', 'normal', 0, 1, 8, '东岸标准钓位'
  union all select 1, '09号钓位', 'normal', 0, 1, 9, '东岸标准钓位'
  union all select 1, '10号钓位', 'normal', 0, 1, 10, '东南岸标准钓位'
  union all select 1, '11号钓位', 'normal', 0, 1, 11, '南岸标准钓位'
  union all select 1, '12号钓位', 'normal', 0, 1, 12, '南岸标准钓位'
  union all select 1, '13号钓位', 'normal', 0, 1, 13, '南岸标准钓位'
  union all select 1, '14号钓位', 'normal', 0, 1, 14, '南岸标准钓位'
  union all select 1, '15号钓位', 'normal', 0, 1, 15, '西南岸标准钓位'
  union all select 1, '16号钓位', 'normal', 0, 1, 16, '西岸标准钓位'
  union all select 1, '17号钓位', 'normal', 0, 1, 17, '西岸标准钓位'
  union all select 1, '18号钓位', 'normal', 0, 1, 18, '西岸标准钓位'
  union all select 1, '19号钓位', 'normal', 0, 1, 19, '西北岸标准钓位'
  union all select 1, '20号钓位', 'normal', 0, 1, 20, '西北岸标准钓位'
) seed
where not exists (
  select 1 from fish_spot existing
  where existing.venue_id = seed.venue_id
    and existing.spot_name = seed.spot_name
    and existing.del_flag = '0'
);

-- 三、为 20 个钓位生成各自独立的通用小程序码记录
-- 实际小程序码 scene 使用唯一 qrId，scene_value 保存该码绑定的钓位。
insert into fish_qrcode
  (venue_id, qr_type, scene_value, remark, status, create_by, create_time)
select s.venue_id,
       'common',
       concat('action=common&venueId=', s.venue_id, '&spotId=', s.spot_id),
       concat(s.spot_name, '专属码'),
       '0', 'admin', sysdate()
from fish_spot s
where s.venue_id = 1
  and s.del_flag = '0'
  and s.spot_name in (
    '01号钓位','02号钓位','03号钓位','04号钓位','05号钓位',
    '06号钓位','07号钓位','08号钓位','09号钓位','10号钓位',
    '11号钓位','12号钓位','13号钓位','14号钓位','15号钓位',
    '16号钓位','17号钓位','18号钓位','19号钓位','20号钓位'
  )
  and not exists (
    select 1 from fish_qrcode q
    where q.scene_value = concat('action=common&venueId=', s.venue_id, '&spotId=', s.spot_id)
  );

-- 四、首页与“钓王争霸”可展示的开放赛事
insert into fish_competition
  (venue_id, title, comp_date, time_slot, max_players, entry_fee_cents,
   prize_pool_cents, prize_rules, fish_species, rules, status, create_by, create_time)
select 1, '2026 夏季钓王挑战赛', '2026-08-16', '07:00-11:00', 50, 6800,
       300000,
       '[{"rank":1,"amount":120000},{"rank":2,"amount":80000},{"rank":3,"amount":50000}]',
       '鲫鱼、鲤鱼、草鱼',
       '3.6米及以下手竿，单钩单线；按有效渔获总重排名；禁止红虫及活饵；比赛期间不得换位。',
       0, 'admin', sysdate()
where not exists (
  select 1 from fish_competition
  where venue_id = 1 and title = '2026 夏季钓王挑战赛'
);

-- 五、确保极智鱼鉴活动在当前展示期内开放
update fish_card_campaign
set status = '0',
    start_time = least(start_time, '2026-08-01 00:00:00'),
    end_time = greatest(end_time, '2026-12-31 23:59:59'),
    update_time = sysdate()
where campaign_key = 'smart-fish-atlas-longshuihu-2026';
