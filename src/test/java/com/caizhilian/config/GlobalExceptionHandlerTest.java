package com.caizhilian.config;

import com.caizhilian.common.result.ApiResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * GlobalExceptionHandler 全局异常处理 单元测试
 */
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Before
    public void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // ==================== 参数校验异常 ====================

    @Test
    public void testHandleValidationException_singleField() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("request", "projectName", "项目名称不能为空");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                mock(MethodParameter.class), bindingResult);

        ApiResult<Void> result = handler.handleValidationException(ex);

        assertEquals("6100010", result.getCode());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("projectName"));
        assertTrue(result.getMessage().contains("不能为空"));
        assertNull(result.getData());
    }

    @Test
    public void testHandleValidationException_multiFields() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fe1 = new FieldError("request", "field1", "字段1不能为空");
        FieldError fe2 = new FieldError("request", "field2", "字段2格式不正确");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Arrays.asList(fe1, fe2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                mock(MethodParameter.class), bindingResult);

        ApiResult<Void> result = handler.handleValidationException(ex);

        assertEquals("6100010", result.getCode());
        assertTrue(result.getMessage().contains("field1"));
        assertTrue(result.getMessage().contains("field2"));
    }

    // ==================== 通用异常 ====================

    @Test
    public void testHandleException_general() {
        Exception ex = new Exception("未知错误");

        ApiResult<Void> result = handler.handleException(ex);

        assertEquals("6100020", result.getCode());
        assertTrue(result.getMessage().contains("系统内部错误"));
        assertTrue(result.getMessage().contains("未知错误"));
        assertNull(result.getData());
    }

    @Test
    public void testHandleException_runtimeException() {
        RuntimeException ex = new RuntimeException("运行时异常");

        ApiResult<Void> result = handler.handleException(ex);

        assertEquals("6100020", result.getCode());
        assertTrue(result.getMessage().contains("运行时异常"));
    }

    @Test
    public void testHandleException_nullMessage() {
        Exception ex = new RuntimeException();

        ApiResult<Void> result = handler.handleException(ex);

        assertEquals("6100020", result.getCode());
        assertNotNull(result.getMessage());
    }
}
