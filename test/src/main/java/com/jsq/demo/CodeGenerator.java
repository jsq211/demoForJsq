//package com.jsq.demo;
//
//
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.*;
//import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
//import com.baomidou.mybatisplus.generator.config.rules.FileType;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//
//import java.io.File;
//
//
///**
// * @author: jsq
// * @Date: 2021/2/27 16:16
// **/
//public class CodeGenerator {
//
//
//
//
//    public static void main(String[] args) {
//        // 代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();
//        String projectPath = System.getProperty("user.dir");
//        gc.setOutputDir(projectPath + "/src/main/java");
//        gc.setAuthor("jsq");
//        gc.setOpen(false);
//        gc.setSwagger2(true);
//        gc.setFileOverride(true);
//        mpg.setGlobalConfig(gc);
//        mpg.setCfg(injectionConfig());
//
//        // 数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
//        dsc.setUsername("root");
//        dsc.setPassword("admin");
//        mpg.setDataSource(dsc);
//
//        // 包配置
//        PackageConfig pc = new PackageConfig();
//        pc.setModuleName("testMp");
//        pc.setParent("com.baomidou.ant");
//        mpg.setPackageInfo(pc);
//        mpg.setTemplate(initTemplateConfig());
//
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setEntityLombokModel(true);
//        strategy.setSuperMapperClass("MyBaseDAO");
//        // 写于父类中的公共字段
//        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setTablePrefix(pc.getModuleName() + "_");
//
//        mpg.setStrategy(strategy);
//
//        mpg.execute();
//    }
//    private static InjectionConfig injectionConfig() {
//        InjectionConfig injectionConfig = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };
//        injectionConfig.setFileCreate(new IFileCreate() {
//            @Override
//            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
//
//                //无参情况下，先检查.java file是否存在：
//                //如果不存在，创建；如果存在，判断是否是entity.java：如果是，创建（覆盖）；否则，不创建。
//                checkDir(filePath);
//                File file = new File(filePath);
//                boolean exist = file.exists();
//                if (exist) {
//                    if (FileType.ENTITY == fileType) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        });
//        return injectionConfig;
//    }
//
//
//    private static TemplateConfig initTemplateConfig() {
//
//        TemplateConfig tc = new TemplateConfig();
//        tc.setController(null);
//        tc.setService(null);
//        tc.setServiceImpl(null);
//        return tc;
//
//    }
//
//}
