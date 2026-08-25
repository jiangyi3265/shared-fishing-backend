-- 极智鱼鉴：六种常见鱼参与本轮，集齐奖励 66 元；其余卡片显示“待解锁”。
-- 本脚本可重复执行。

insert into fish_card_campaign
  (campaign_key, venue_id, title, subtitle, rules, start_time, end_time, reward_cents, status)
values
  ('smart-fish-atlas-longshuihu-2026', 1, '极智鱼鉴 · 龙水湖篇',
   '集齐6种常见鱼，获得66元奖励',
   '认证鱼种：小白条、鲫鱼、鲤鱼、草鱼、花鲢、黄辣丁。清晰拍摄钓获、说出鱼种，并记录完整放回鱼塘过程；后台审核通过后鱼卡点亮。',
   '2026-08-01 00:00:00', '2026-10-30 23:59:59', 6600, '0')
on duplicate key update
  subtitle = values(subtitle), rules = values(rules), reward_cents = 6600, update_time = sysdate();

set @fish_card_campaign_id := (
  select campaign_id from fish_card_campaign
  where campaign_key = 'smart-fish-atlas-longshuihu-2026' limit 1
);

-- 先锁定全部旧鱼种，再开启本轮指定的六种；旧鱼种记录保留用于历史进度追溯。
update fish_card_species
set status = '1', sort_num = 100 + species_id
where campaign_id = @fish_card_campaign_id;

insert into fish_card_species (campaign_id, species_name, card_image, sort_num, status) values
(@fish_card_campaign_id, '小白条', '/static/fish-card-atlas.png', 1, '0'),
(@fish_card_campaign_id, '鲫鱼',   '/static/fish-card-atlas.png', 2, '0'),
(@fish_card_campaign_id, '鲤鱼',   '/static/fish-card-atlas.png', 3, '0'),
(@fish_card_campaign_id, '草鱼',   '/static/fish-card-atlas.png', 4, '0'),
(@fish_card_campaign_id, '花鲢',   '/static/fish-card-atlas.png', 5, '0'),
(@fish_card_campaign_id, '黄辣丁', '/static/fish-card-atlas.png', 6, '0')
on duplicate key update
  card_image = values(card_image), sort_num = values(sort_num), status = '0';

-- 未完成轮次统一按本期 66 元奖励快照结算；已完成历史轮次不改动。
update fish_card_round
set reward_cents = 6600, update_time = sysdate()
where campaign_id = @fish_card_campaign_id and status = 0;

-- 给鱼鉴页面增加独立审核权限，超级管理员无需额外配置；普通运营角色需勾选该按钮权限。
set @fish_card_menu := (select menu_id from sys_menu where perms = 'fishing:fishCard:list' limit 1);
insert into sys_menu
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
   visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '鱼鉴审核', @fish_card_menu, 2, '#', '', 1, 0, 'F',
       '0', '0', 'fishing:fishCard:audit', '#', 'admin', sysdate(), '', null, ''
where @fish_card_menu is not null
  and not exists (select 1 from sys_menu where perms = 'fishing:fishCard:audit');
