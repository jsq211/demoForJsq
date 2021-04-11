//package com.jsq.component.config;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Lists;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.context.ResourceLoaderAware;
//import org.springframework.core.PriorityOrdered;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternUtils;
//import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
//import org.springframework.core.type.classreading.MetadataReader;
//import org.springframework.core.type.classreading.MetadataReaderFactory;
//import org.springframework.util.ClassUtils;
//import org.springframework.util.SystemPropertyUtils;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * 手动注入监控对象字段
// * @author jsq
// * date: 2021/4/10 17:36
// **/
//public class MybatisComponentScanRegister  implements BeanDefinitionRegistryPostProcessor, PriorityOrdered , ResourceLoaderAware {
//    private static final Logger logger = LoggerFactory.getLogger(MybatisComponentScanRegister.class);
//    private ResourceLoader resourceLoader;
//    private ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
//    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
//    private static final String PATH = "jsq.sync.path";
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//        try {
//            Set<Class<?>> classes = new HashSet<>();
//            String[] packages = getProperty(PATH).split(",");
//            List<Resource> resourceList = Lists.newArrayList();
//            for (String path : packages) {
//                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
//                        .concat(ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(path))
//                                .concat("/**/*.class"));
//                Resource[] resources = resolver.getResources(packageSearchPath);
//                if (null != resources){
//                    resourceList.addAll(Lists.newArrayList(resources));
//                }
//            }
//            for (Resource resource : resourceList) {
//                if (resource.isReadable()) {
//                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
//                    try {
//                        // 当类型不是抽象类或接口在添加到集合
//                        if (metadataReader.getClassMetadata().isConcrete()) {
//                            classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            System.out.println(JSON.toJSONString(classes));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//
//    }
//
//    @Override
//    public int getOrder() {
//        return PriorityOrdered.LOWEST_PRECEDENCE;
//    }
//
//    private String getProperty(String key){
//        Resource resource = new ClassPathResource("application.yml");
//        Properties properties;
//        try {
//            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
//            yamlFactory.setResources(resource);
//            properties = yamlFactory.getObject();
//        } catch (Exception e) {
//            logger.warn("读取application.yml配置失败，请检查配置信息");
//            return "";
//        }
//
//        if (properties != null) {
//            return properties.getProperty(key);
//        }
//        return "";
//    }
//
//    @Override
//    public void setResourceLoader(ResourceLoader resourceLoader) {
//        this.resourceLoader = resourceLoader;
//    }
//}
