package com.caizhilian.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 同步工程量清单信息 - 请求DTO（主表）
 * 对应文档4.2.2节 "同步工程量清单信息服务"
 */
@Data
public class BoqSyncItem {
    /** 主键（工程量清单主表主键），BOQ_GID */
    @NotNull
    private Long boqId;

    /** 项目对象ID */
    @NotNull
    private Long projectId;

    /** 合同对象ID */
    @NotNull
    private Long contractId;

    /** 基层组织编码，如0442 */
    @NotBlank
    private String baseOrgCode;

    /** 项目责任部门 */
    @NotBlank
    private String unitCode;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后修改时间 */
    private LocalDateTime lastUpdateTime;

    /** 工程量清单明细列表 */
    @Valid
    private List<BoqDetailItem> boqDetailList;

    @Data
    public static class BoqDetailItem {
        /** 清单明细主键，LIST_GID */
        @NotNull
        private Long detailId;

        /** 工程量主表主键（外键），BOQ_GID */
        private Long boqId;

        /** 清单编码 */
        private String detailCode;

        /** 清单名称 */
        private String detailName;

        /** 特征描述 */
        private String featureDescription;

        /** 计量单位名称 */
        private String measurementUnit;

        /** 综合单价（含税） */
        private BigDecimal unitPriceWithTax;

        /** 税率 */
        private BigDecimal taxrate;

        /** 原始合同工程量 */
        private BigDecimal originalQuantity;

        /** 累计变更量 */
        private BigDecimal cumulativeChangeQuantity;

        /** 合同金额 */
        private BigDecimal contractAmount;

        /** 当前工程量 = 合同量+累计变更量 */
        private BigDecimal currentQuantity;

        /** 备注 */
        private String remark;

        /** 当前合计 = 当前工程量*综合单价 */
        private BigDecimal currentTotalAmount;

        /** 是否末级节点 1=是 0=否 */
        private Integer isLeafNode;

        /** 节点层级，如 "1.1.1" */
        private String nodeLevel;

        /** 累计核定数量 */
        private BigDecimal approvedQuantityCumulative;
    }
}
