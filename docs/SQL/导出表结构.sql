SELECT
  COLUMN_NAME 列名,
  COLUMN_COMMENT 名称 ,
  COLUMN_TYPE 数据类型,
  DATA_TYPE 字段类型,  
  CHARACTER_MAXIMUM_LENGTH 长度,
  IS_NULLABLE 是否必填,
  COLUMN_DEFAULT 描述 
FROM
 INFORMATION_SCHEMA.COLUMNS
where
-- developerclub为数据库名称，到时候只需要修改成你要导出表结构的数据库即可
table_schema ='litchi'
AND
-- article为表名，到时候换成你要导出的表的名称
-- 如果不写的话，默认会查询出所有表中的数据，这样可能就分不清到底哪些字段是哪张表中的了，所以还是建议写上要导出的名名称
table_name  in (
	'tb_item1'
	,'tb_item2'
)

-- 导出数据库所有表表名
select table_name from information_schema.tables where table_schema='csdb' and table_type='base table';