-- 给称重付款记录增加独立的后台入口，避免运营人员只看到计时订单。
set @fish_parent := (select menu_id from sys_menu where menu_name = '钓场管理' and menu_type = 'M' limit 1);
set @timer_order_menu := (select menu_id from sys_menu where path = 'order' and component = 'fishing/order/index' limit 1);

insert into sys_menu
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache,
   menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '鱼获订单', @fish_parent, 4, 'weighOrder', 'fishing/order/index', 1, 0,
       'C', '0', '0', 'fishing:order:list', 'money', 'admin', sysdate(), '', null,
       '称重付款订单独立入口'
where @fish_parent is not null
  and not exists (select 1 from sys_menu where path = 'weighOrder' and menu_type = 'C');

set @weigh_order_menu := (select menu_id from sys_menu where path = 'weighOrder' and menu_type = 'C' limit 1);

-- 已经能看计时订单的普通角色，自动获得鱼获订单入口；超级管理员本身拥有全部菜单。
insert ignore into sys_role_menu (role_id, menu_id)
select role_id, @weigh_order_menu
from sys_role_menu
where menu_id = @timer_order_menu
  and @weigh_order_menu is not null;
