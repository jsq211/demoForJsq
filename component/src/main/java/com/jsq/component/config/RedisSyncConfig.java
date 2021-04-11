package com.jsq.component.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jsq.component.factory.RedisFactory;
import com.jsq.component.util.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * redis配置加载
 * @author jsq
 */
@Configuration
@ConditionalOnProperty(prefix = "jsq.sync",name = "enabled",havingValue = "true")
public class RedisSyncConfig {
    @Value("${jsq.sync.database:7}")
    private String database;
    @Bean
    @ConfigurationProperties(
            prefix = "spring.redis"
    )
    public RedisSyncProperties redisSyncProperties(){
        return new RedisSyncProperties();
    }

    @Bean("redisSpringUtil")
    public SpringUtil springUtil(){
        return new SpringUtil();
    }
    @Bean("redisSyncConnectionFactory")
    @ConditionalOnBean(RedisSyncProperties.class)
    public LettuceConnectionFactory redisSyncConnectionFactory(RedisSyncProperties redisSyncProperties){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(Integer.valueOf(database));
        redisStandaloneConfiguration.setHostName(redisSyncProperties.getHost());
        redisStandaloneConfiguration.setPort(redisSyncProperties.getPort());
        if (!StringUtils.isEmpty(redisStandaloneConfiguration.getPassword())){
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisSyncProperties.getPassword()));
        }
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,
                lettuceClientConfigurationBuilder.build());
        return factory;
    }

    @Bean(name = "redisSyncTemplate")
    public RedisTemplate<String, Object> redisSyncTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        RedisTemplate<String, Object> template = RedisFactory.createTemplate(lettuceConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
//        JavaTimeModule timeModule = new JavaTimeModule();
        registerTimeModule(om);
//        timeModule.addDeserializer(LocalDate.class,
//                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        timeModule.addDeserializer(LocalDateTime.class,
//                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        timeModule.addSerializer(LocalDate.class,
//                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        timeModule.addSerializer(LocalDateTime.class,
//                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        om.registerModule(timeModule);
        om.registerModule(new JavaTimeModule());
        om.registerModule(new Jdk8Module());
//        om.findAndRegisterModules();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
    private void registerTimeModule(ObjectMapper objectMapper){
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        /**
         * 演示：Date序列化为时间戳
         *
         * 通过继承JsonSerializer<Type> 抽象类（注意是泛型）， 实现serialize方法实现
         * 要被序列化的内容是：第一个参数
         * 序列化的后内容是：jsonGenerator.writeString() 里的内容
         */
        javaTimeModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(String.valueOf(value.getTime()));
            }
        });
        /**
         * 演示：时间戳反序列Date
         *
         * 通过继承JsonDeserializer<Type> 抽象类（注意是泛型）， 实现deserialize 方法实现
         * 要被反序列化的内容：JsonParser.getText()
         * 反序列化后的内容是：返回值
         */
        javaTimeModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getText();
                Long time=Long.valueOf(value);
                Date date=new Date(time);
                return date;
            }
        });
        objectMapper.registerModule(javaTimeModule);
    }


}
