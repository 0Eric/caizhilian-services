package com.caizhilian.common.result;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * SyncFeedback 同步反馈条目 单元测试
 */
public class SyncFeedbackTest {

    @Test
    public void testNoArgsConstructor() {
        SyncFeedback feedback = new SyncFeedback();
        assertNull(feedback.getId());
        assertNull(feedback.getFeedbackMessage());
    }

    @Test
    public void testConstructor_withArgs() {
        SyncFeedback feedback = new SyncFeedback(1000000001L, "项目信息同步成功");
        assertEquals(1000000001L, feedback.getId());
        assertEquals("项目信息同步成功", feedback.getFeedbackMessage());
    }

    @Test
    public void testConstructor_withStringId() {
        SyncFeedback feedback = new SyncFeedback("BOQ-001", "工程量清单同步成功");
        assertEquals("BOQ-001", feedback.getId());
        assertEquals("工程量清单同步成功", feedback.getFeedbackMessage());
    }

    @Test
    public void testConstructor_withFailureMessage() {
        SyncFeedback feedback = new SyncFeedback(999L, "同步失败：数据库连接超时");
        assertEquals(999L, feedback.getId());
        assertTrue(feedback.getFeedbackMessage().startsWith("同步失败"));
    }

    @Test
    public void testSetterGetter() {
        SyncFeedback feedback = new SyncFeedback();
        feedback.setId(200L);
        feedback.setFeedbackMessage("合同信息同步成功");
        assertEquals(200L, feedback.getId());
        assertEquals("合同信息同步成功", feedback.getFeedbackMessage());
    }

    @Test
    public void testIdTypes() {
        // Long
        SyncFeedback f1 = new SyncFeedback(100L, "ok");
        assertEquals(Long.class, f1.getId().getClass());

        // String
        SyncFeedback f2 = new SyncFeedback("ABC", "ok");
        assertEquals(String.class, f2.getId().getClass());
    }
}
