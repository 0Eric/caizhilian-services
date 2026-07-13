package com.caizhilian.common.result;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * ApiResult 统一响应格式 单元测试
 */
public class ApiResultTest {

    @Test
    public void testSuccess_withData() {
        ApiResult<String> result = ApiResult.success("hello");
        assertEquals("6100000", result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("hello", result.getData());
    }

    @Test
    public void testSuccess_withNullData() {
        ApiResult<Void> result = ApiResult.success(null);
        assertEquals("6100000", result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testSuccess_withCustomMessage() {
        ApiResult<Integer> result = ApiResult.success("自定义成功", 100);
        assertEquals("6100000", result.getCode());
        assertEquals("自定义成功", result.getMessage());
        assertEquals(Integer.valueOf(100), result.getData());
    }

    @Test
    public void testSuccess_withListData() {
        List<String> list = Arrays.asList("a", "b", "c");
        ApiResult<List<String>> result = ApiResult.success(list);
        assertEquals("6100000", result.getCode());
        assertEquals(3, result.getData().size());
    }

    @Test
    public void testError() {
        ApiResult<Void> result = ApiResult.error("6100099", "自定义错误");
        assertEquals("6100099", result.getCode());
        assertEquals("自定义错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testValidateError() {
        ApiResult<Void> result = ApiResult.validateError("字段不能为空");
        assertEquals("6100010", result.getCode());
        assertEquals("字段不能为空", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testNetworkError() {
        ApiResult<Void> result = ApiResult.networkError("连接超时");
        assertEquals("6100020", result.getCode());
        assertEquals("连接超时", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    public void testAllArgsConstructor() {
        ApiResult<String> result = new ApiResult<>("6100030", "测试", "data");
        assertEquals("6100030", result.getCode());
        assertEquals("测试", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    public void testSetterGetter() {
        ApiResult<String> result = new ApiResult<>();
        result.setCode("6100050");
        result.setMessage("setter测试");
        result.setData("test_data");
        assertEquals("6100050", result.getCode());
        assertEquals("setter测试", result.getMessage());
        assertEquals("test_data", result.getData());
    }

    @Test
    public void testGenericType_integers() {
        ApiResult<Integer> result = ApiResult.success(42);
        assertEquals(Integer.valueOf(42), result.getData());
    }

    @Test
    public void testCodeConsistency() {
        ApiResult<String> r1 = ApiResult.success("ok");
        ApiResult<String> r2 = ApiResult.success("ok2");
        assertEquals("所有成功码应一致", r1.getCode(), r2.getCode());
    }
}
