package com.caizhilian.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 配置
 * 解决 LocalDateTime 序列化/反序列化格式问题
 * 支持 "yyyy-MM-dd HH:mm:ss" 格式（带空格）和 ISO 格式
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
        return builder -> {
            JavaTimeModule module = new JavaTimeModule();
            // 反序列化：支持 "yyyy-MM-dd HH:mm:ss" 格式
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
            // 序列化：输出 "yyyy-MM-dd HH:mm:ss" 格式
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
            builder.modules(module);
        };
    }
}
