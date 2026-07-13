-- 2026-07-12 小程序问题修复配套迁移
-- 适用 MySQL 5.7 / 8.0，可安全重复执行。
-- 内容：钓场导航坐标、通用场码字典、后台轮播图菜单名称。

SET @migration_schema := DATABASE();

SET @migration_sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `fish_venue` ADD COLUMN `latitude` DECIMAL(10,7) NULL COMMENT ''纬度（GCJ-02）'' AFTER `address`',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = @migration_schema
      AND table_name = 'fish_venue'
      AND column_name = 'latitude'
);
PREPARE migration_stmt FROM @migration_sql;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @migration_sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `fish_venue` ADD COLUMN `longitude` DECIMAL(10,7) NULL COMMENT ''经度（GCJ-02）'' AFTER `latitude`',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = @migration_schema
      AND table_name = 'fish_venue'
      AND column_name = 'longitude'
);
PREPARE migration_stmt FROM @migration_sql;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

UPDATE sys_menu
SET menu_name = '轮播图管理',
    remark = '轮播图与活动管理菜单',
    update_by = 'system',
    update_time = SYSDATE()
WHERE perms = 'fishing:ad:list';

INSERT INTO sys_dict_type
    (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '二维码类型', 'fish_qr_type', '0', 'system', SYSDATE(), '钓场二维码类型'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_type WHERE dict_type = 'fish_qr_type'
);

UPDATE sys_dict_type
SET dict_name = '二维码类型', status = '0', update_by = 'system', update_time = SYSDATE()
WHERE dict_type = 'fish_qr_type';

UPDATE sys_dict_data
SET is_default = 'N', update_by = 'system', update_time = SYSDATE()
WHERE dict_type = 'fish_qr_type' AND dict_value IN ('start', 'end');

INSERT INTO sys_dict_data
    (dict_sort, dict_label, dict_value, dict_type, css_class, list_class,
     is_default, status, create_by, create_time, remark)
SELECT 3, '通用场码（开始/结算）', 'common', 'fish_qr_type', '', 'primary',
       'Y', '0', 'system', SYSDATE(), '同一二维码按用户当前订单状态动态分流'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data
    WHERE dict_type = 'fish_qr_type' AND dict_value = 'common'
);

UPDATE sys_dict_data
SET dict_sort = 3,
    dict_label = '通用场码（开始/结算）',
    list_class = 'primary',
    is_default = 'Y',
    status = '0',
    update_by = 'system',
    update_time = SYSDATE(),
    remark = '同一二维码按用户当前订单状态动态分流'
WHERE dict_type = 'fish_qr_type' AND dict_value = 'common';
