package com.caizhilian.common.result;

import lombok.Data;

/**
 * 同步结果反馈条目
 * 对应文档中 data 数组内的反馈对象
 */
@Data
public class SyncFeedback {
    private Object id;
    private String feedbackMessage;

    public SyncFeedback() {}

    public SyncFeedback(Object id, String feedbackMessage) {
        this.id = id;
        this.feedbackMessage = feedbackMessage;
    }
}
