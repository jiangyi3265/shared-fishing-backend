-- 下竿前安全协议留痕（可重复执行）
-- 每笔新计时订单记录用户确认的协议版本和确认时间。

set @has_safety_version := (
  select count(*) from information_schema.columns
  where table_schema = database()
    and table_name = 'fish_order'
    and column_name = 'safety_agreement_version'
);
set @add_safety_version_sql := if(
  @has_safety_version = 0,
  'alter table fish_order add column safety_agreement_version varchar(32) null comment ''下竿前确认的安全协议版本'' after rule_snapshot',
  'select 1'
);
prepare add_safety_version_stmt from @add_safety_version_sql;
execute add_safety_version_stmt;
deallocate prepare add_safety_version_stmt;

set @has_safety_time := (
  select count(*) from information_schema.columns
  where table_schema = database()
    and table_name = 'fish_order'
    and column_name = 'safety_agreed_time'
);
set @add_safety_time_sql := if(
  @has_safety_time = 0,
  'alter table fish_order add column safety_agreed_time datetime null comment ''安全协议确认时间'' after safety_agreement_version',
  'select 1'
);
prepare add_safety_time_stmt from @add_safety_time_sql;
execute add_safety_time_stmt;
deallocate prepare add_safety_time_stmt;
