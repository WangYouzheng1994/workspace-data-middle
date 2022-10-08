1.搭建memcached 与mysql集成
2.innodb_memcache.containers 表中的映射关系name字段，设置value是：default，表示默认否则会取第一行数据                                                                   
3.注意每次修改表containers 中的数据需要重新执行 
                    UNINSTALL PLUGIN daemon_memcached;
                    INSTALL PLUGIN daemon_memcached soname "libmemcached.so";
4.innodb_memcache.containers 表中的value_columns字段 中用| 问题： 数据中不能存在 |
  ，否则mysql自动截取会缺失数据 ,解决方式：修改innodb_memcache.config_oppions表中的separator字段