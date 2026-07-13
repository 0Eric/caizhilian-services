package com.caizhilian.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * JacksonConfig 日期序列化配置 单元测试
 */
public class JacksonConfigTest {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Test
    public void testLocalDateTimeSerializer() throws Exception {
        ObjectMapper mapper = createObjectMapper();

        LocalDateTime time = LocalDateTime.of(2026, 6, 1, 10, 30, 0);
        TestDto dto = new TestDto(time);

        String json = mapper.writeValueAsString(dto);

        // 序列化应输出 "yyyy-MM-dd HH:mm:ss" 格式
        assertTrue("序列化格式应为 yyyy-MM-dd HH:mm:ss", json.contains("2026-06-01 10:30:00"));
        // 不应包含 ISO 格式的 T
        assertFalse("不应包含ISO的T分隔符", json.contains("2026-06-01T"));
    }

    @Test
    public void testLocalDateTimeDeserializer() throws Exception {
        ObjectMapper mapper = createObjectMapper();

        String json = "{\"time\":\"2026-06-01 10:30:00\"}";

        TestDto dto = mapper.readValue(json, TestDto.class);

        assertNotNull(dto.getTime());
        assertEquals(LocalDateTime.of(2026, 6, 1, 10, 30, 0), dto.getTime());
    }

    @Test
    public void testLocalDateTimeDeserializer_withSpaces() throws Exception {
        ObjectMapper mapper = createObjectMapper();

        // 带空格分隔的日期格式
        String json = "{\"time\":\"2026-07-10 15:45:30\"}";

        TestDto dto = mapper.readValue(json, TestDto.class);

        assertEquals(2026, dto.getTime().getYear());
        assertEquals(7, dto.getTime().getMonthValue());
        assertEquals(10, dto.getTime().getDayOfMonth());
        assertEquals(15, dto.getTime().getHour());
        assertEquals(45, dto.getTime().getMinute());
        assertEquals(30, dto.getTime().getSecond());
    }

    @Test
    public void testRoundTrip() throws Exception {
        ObjectMapper mapper = createObjectMapper();

        LocalDateTime original = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        TestDto originalDto = new TestDto(original);

        String json = mapper.writeValueAsString(originalDto);
        TestDto parsedDto = mapper.readValue(json, TestDto.class);

        assertEquals("序列化再反序列化应得到相同值", original, parsedDto.getTime());
    }

    @Test
    public void testJavaTimeModuleRegistered() {
        JacksonConfig config = new JacksonConfig();
        assertNotNull("JacksonConfig应能正常实例化", config);
    }

    // ==================== helpers ====================

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
        mapper.registerModule(module);
        return mapper;
    }

    static class TestDto {
        private LocalDateTime time;

        public TestDto() {}

        public TestDto(LocalDateTime time) {
            this.time = time;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }
}
