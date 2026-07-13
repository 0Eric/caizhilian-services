package com.caizhilian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工程量清单明细实体
 * 对应文档 CM_SCH_BOQ_LIST
 */
@Entity
@Table(name = "customize_boq_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoqDetail {

    // ==================== 通用字段 ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Long gid;

    /** 所有者ID */
    @Column(name = "owner_id", length = 36)
    private String ownerId;

    /** 业务状态 */
    @Column(name = "f_bizstate")
    private Integer fBizstate;

    /** 记录创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 创建人 */
    @Column(name = "founder")
    private Integer founder;

    /** 记录修改时间 */
    @Column(name = "last_time")
    private LocalDateTime lastTime;

    /** 修改人 */
    @Column(name = "ls_modifier")
    private Integer lsModifier;

    // ==================== 业务字段 ====================

    /** 清单明细主键（外部），LIST_GID */
    @Column(name = "detail_id")
    private Long detailId;

    /** 工程量主表主键（外键，关联customize_boq_main.boq_id），BOQ_GID */
    @Column(name = "boq_id")
    private Long boqId;

    /** 清单编码 */
    @Column(name = "detail_code", length = 50)
    private String detailCode;

    /** 清单名称 */
    @Column(name = "detail_name", length = 100)
    private String detailName;

    /** 特征描述 */
    @Column(name = "feature_description", length = 1000)
    private String featureDescription;

    /** 计量单位名称 */
    @Column(name = "measurement_unit", length = 50)
    private String measurementUnit;

    /** 综合单价（含税） */
    @Column(name = "unit_price_with_tax", precision = 20, scale = 4)
    private BigDecimal unitPriceWithTax;

    /** 税率 */
    @Column(name = "taxrate", precision = 10, scale = 4)
    private BigDecimal taxrate;

    /** 原始合同工程量 */
    @Column(name = "original_quantity", precision = 20, scale = 4)
    private BigDecimal originalQuantity;

    /** 累计变更量 */
    @Column(name = "cumulative_change_quantity", precision = 18, scale = 4)
    private BigDecimal cumulativeChangeQuantity;

    /** 合同金额 */
    @Column(name = "contract_amount", precision = 20, scale = 2)
    private BigDecimal contractAmount;

    /** 当前工程量 = 合同量+累计变更量 */
    @Column(name = "current_quantity", precision = 20, scale = 4)
    private BigDecimal currentQuantity;

    /** 备注 */
    @Column(name = "remark", length = 255)
    private String remark;

    /** 当前合计 = 当前工程量*综合单价 */
    @Column(name = "current_total_amount", precision = 20, scale = 2)
    private BigDecimal currentTotalAmount;

    /** 是否末级节点 1=是 0=否 */
    @Column(name = "is_leaf_node")
    private Integer isLeafNode;

    /** 节点层级 */
    @Column(name = "node_level", length = 500)
    private String nodeLevel;

    /** 累计核定数量 */
    @Column(name = "approved_quantity_cumulative", precision = 20, scale = 4)
    private BigDecimal approvedQuantityCumulative;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        lastTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastTime = LocalDateTime.now();
    }
}
