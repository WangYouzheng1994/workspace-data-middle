package com.yqwl.datamiddle.realtime.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisPlusCodeGenerator {
	
	//项目路径
	private static String canonicalPath = "";

    //作者
    private static String authorName = "yiqi";

    //基本包名
    private static String basePackage = "com.example";

    //项目的模块名称
    private static String projectModuleName = "";

    //model 名字
    private static String moduleName = "sptb02";

    //要生成的表名
    private static String[] tables = {"dwd_vlms_sptb02"};

    //table前缀
    private static String prefix = "";

    //数据库类型
    private static DbType dbType = DbType.MYSQL;
    //数据库配置四要素
    private static String driverName = "com.mysql.jdbc.Driver";
    //private static String url = "jdbc:mysql://192.168.1.189:8099/wh_migratemodel?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false";
    //private static String url = "jdbc:mysql://192.168.1.189:8099/my_wenhuigou?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false";
    private static String url = "jdbc:mysql://192.168.3.4:3306/data_middle_flink?characterEncoding=utf-8&useSSL=false";
    private static String username = "fengqiwulian";
    private static String password = "fengqiwulian";


    public static void main(String[] args) {

        AutoGenerator gen = new AutoGenerator();
        
        /**
         * 获取项目路径
         */
		try {
			canonicalPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

        /**
         * 数据库配置
         */
        gen.setDataSource(new DataSourceConfig()
                .setDbType(dbType)
                .setDriverName(driverName)
                .setUrl(url)
                .setUsername(username)
                .setPassword(password)
                .setTypeConvert(new MySqlTypeConvert() {
                    // 自定义数据库表字段类型转换【可选】
                    //@Override
                    //public DbColumnType processTypeConvert(String fieldType) {
                        //System.out.println("转换类型：" + fieldType);
                        // if ( fieldType.toLowerCase().contains( "tinyint" ) ) {
                        //    return DbColumnType.BOOLEAN;
                        // }
                        //return super.processTypeConvert(fieldType);
                    //}
                }));

        /**
         * 全局配置
         */
        gen.setGlobalConfig(new GlobalConfig()
                .setOutputDir(canonicalPath + "/src/main/java")//输出目录
                .setFileOverride(false)// 是否覆盖文件
                .setActiveRecord(false)// 开启 activeRecord 模式
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(true)// XML ResultMap
                .setBaseColumnList(true)// XML columList
                .setOpen(false)//生成后打开文件夹
                .setAuthor(authorName)
                // 自定义文件命名，注意 %s 会自动填充表实体属性！
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                .setServiceName("%sService")
                .setServiceImplName("%sServiceImpl")
                .setControllerName("%sController")
        );

        /**
         * 策略配置
         */
        gen.setStrategy(new StrategyConfig()
                // .setCapitalMode(true)// 全局大写命名
                //.setDbColumnUnderline(true)//全局下划线命名
                .setTablePrefix(new String[]{prefix})// 此处可以修改为您的表前缀
                .setNaming(NamingStrategy.underline_to_camel)// 表名生成策略
                .setInclude(tables) // 需要生成的表
                .setRestControllerStyle(true)
                //.setExclude(new String[]{"test"}) // 排除生成的表
                // 自定义实体父类
                // .setSuperEntityClass("com.baomidou.demo.TestEntity")
                // 自定义实体，公共字段
                //.setSuperEntityColumns(new String[]{"test_id"})
                //.setTableFillList(tableFillList)
                // 自定义 mapper 父类 默认BaseMapper
                //.setSuperMapperClass("com.maryun.mapper.BaseMapper")
                // 自定义 service 父类 默认IService
                // .setSuperServiceClass("com.baomidou.demo.TestService")
                // 自定义 service 实现类父类 默认ServiceImpl
                // .setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl")
                // 自定义 controller 父类
                //com.maryun.controller.base.BaseController
                //.setSuperControllerClass("com.maryun.controller.base.BaseController")
                // 【实体】是否生成字段常量（默认 false）
                // public static final String ID = "test_id";
                //.setEntityColumnConstant(true)
                // 【实体】是否为构建者模型（默认 false）
                // public User setName(String name) {this.name = name; return this;}
                // .setEntityBuilderModel(true)
                // 【实体】是否为lombok模型（默认 false）<a href="https://projectlombok.org/">document</a>
                .setEntityLombokModel(true)
                // Boolean类型字段是否移除is前缀处理
                .setEntityBooleanColumnRemoveIsPrefix(true)
                .setRestControllerStyle(false)
                .setControllerMappingHyphenStyle(false)
                .setEntityTableFieldAnnotationEnable(true)
        );

        /**
         * 包配置
         */
        gen.setPackageInfo(new PackageConfig()
                        //.setModuleName("User")
                        .setParent(basePackage + projectModuleName)// 自定义包路径
                        .setModuleName(moduleName)
                        .setController("controller")// 这里是控制器包名，默认 web
                        .setEntity("entity") // 设置Entity包名，默认entity
                        .setMapper("mapper") // 设置Mapper包名，默认mapper
                        .setService("service") // 设置Service包名，默认service
                        .setServiceImpl("service.impl") // 设置Service Impl包名，默认service.impl
                        .setXml("mapper") // 设置Mapper XML包名，默认mapper.xml
                        );

        /**
         * 注入自定义配置
         */
        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig abc = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        //自定义文件输出位置（非必须）
        //mapper/system/AuthMapper.xml
        List<FileOutConfig> fileOutList = new ArrayList<FileOutConfig>();
        fileOutList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return canonicalPath + "/src/main/resources/mapper/"+projectModuleName+"/" + moduleName + "/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        abc.setFileOutConfigList(fileOutList);
        gen.setCfg(abc);

        /**
         * 指定模板引擎 默认是VelocityTemplateEngine ，需要引入相关引擎依赖
         */
        //gen.setTemplateEngine(new FreemarkerTemplateEngine());



        /**
         * 模板配置
         */
        gen.setTemplate(
                // 关闭默认 xml 生成，调整生成 至 根目录
                new TemplateConfig().setXml(null)
                // 自定义模板配置，模板可以参考源码 /mybatis-plus/src/main/resources/template 使用 copy
                // 至您项目 src/main/resources/template 目录下，模板名称也可自定义如下配置：
                // .setController("...");
                // .setEntity("...");
                // .setMapper("...");
                // .setXml("...");
                // .setService("...");
                // .setServiceImpl("...");
        );

        // 执行生成
        gen.execute();
        System.out.println("执行完成...");
    }
}