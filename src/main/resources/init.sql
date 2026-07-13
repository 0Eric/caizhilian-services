-- =====================================================
-- 财智链系统 - DAP集成数据同步服务
-- 数据库初始化脚本
-- 使用前请先在 PostgreSQL 中创建数据库：
--   CREATE DATABASE caizhilian;
-- =====================================================

-- =====================================================
-- 通用字段说明（每张表均包含）：
--   gid          BIGSERIAL   自增主键
--   owner_id     VARCHAR(36) 所有者ID（默认admin，TODO 内网替换）
--   f_bizstate   INT         业务状态（默认0=正常）
--   create_time  TIMESTAMP   记录创建时间
--   founder      INT         创建人（默认0，TODO 内网替换）
--   last_time    TIMESTAMP   记录修改时间
--   ls_modifier  INT         修改人（默认0，TODO 内网替换）
-- =====================================================

-- 项目信息表
CREATE TABLE IF NOT EXISTS customize_project_info (
    gid BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(36) DEFAULT 'admin',
    f_bizstate INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    founder INT DEFAULT 0,
    last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ls_modifier INT DEFAULT 0,
    base_org_code VARCHAR(8),
    base_org_code_name VARCHAR(100),
    unit_code VARCHAR(10),
    unit_code_name VARCHAR(100),
    project_id BIGINT,
    project_name VARCHAR(300),
    project_code VARCHAR(500)
);

COMMENT ON TABLE customize_project_info IS '项目信息表 - 存储DAP同步过来的项目主数据';
COMMENT ON COLUMN customize_project_info.gid IS '自增主键';
COMMENT ON COLUMN customize_project_info.owner_id IS '所有者ID（默认admin，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_project_info.f_bizstate IS '业务状态（默认0=正常）';
COMMENT ON COLUMN customize_project_info.create_time IS '记录创建时间';
COMMENT ON COLUMN customize_project_info.founder IS '创建人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_project_info.last_time IS '记录修改时间';
COMMENT ON COLUMN customize_project_info.ls_modifier IS '修改人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_project_info.base_org_code IS '基层组织编码，固定取河北送变电基础组织comp_id，如0442';
COMMENT ON COLUMN customize_project_info.base_org_code_name IS '基层组织名称，如河北送变电';
COMMENT ON COLUMN customize_project_info.unit_code IS '项目责任部门编码（组织机构ID），如01000101';
COMMENT ON COLUMN customize_project_info.unit_code_name IS '项目责任部门名称，如变电一分公司';
COMMENT ON COLUMN customize_project_info.project_id IS '外部项目ID（文档中的projectId），唯一标识';
COMMENT ON COLUMN customize_project_info.project_name IS '项目名称（dxmc）';
COMMENT ON COLUMN customize_project_info.project_code IS '项目编码（xmbm）';

-- 合同信息表
CREATE TABLE IF NOT EXISTS customize_contract_info (
    gid BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(36) DEFAULT 'admin',
    f_bizstate INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    founder INT DEFAULT 0,
    last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ls_modifier INT DEFAULT 0,
    base_org_code VARCHAR(8),
    base_org_code_name VARCHAR(100),
    unit_code VARCHAR(10),
    unit_code_name VARCHAR(100),
    contract_id BIGINT,
    contract_name VARCHAR(300),
    contract_no VARCHAR(200),
    subcontractor_id BIGINT,
    subcontractor_name VARCHAR(200),
    unified_credit_code VARCHAR(50),
    contract_amount DECIMAL(20,2)
);

COMMENT ON TABLE customize_contract_info IS '合同信息表 - 存储DAP同步过来的分包合同主数据';
COMMENT ON COLUMN customize_contract_info.gid IS '自增主键';
COMMENT ON COLUMN customize_contract_info.owner_id IS '所有者ID（默认admin，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_contract_info.f_bizstate IS '业务状态（默认0=正常）';
COMMENT ON COLUMN customize_contract_info.create_time IS '记录创建时间';
COMMENT ON COLUMN customize_contract_info.founder IS '创建人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_contract_info.last_time IS '记录修改时间';
COMMENT ON COLUMN customize_contract_info.ls_modifier IS '修改人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_contract_info.base_org_code IS '所属单位编码，固定取河北送变电基础组织comp_id，如0442';
COMMENT ON COLUMN customize_contract_info.base_org_code_name IS '所属单位名称';
COMMENT ON COLUMN customize_contract_info.unit_code IS '项目责任部门编码（组织机构ID）';
COMMENT ON COLUMN customize_contract_info.unit_code_name IS '项目责任部门名称';
COMMENT ON COLUMN customize_contract_info.contract_id IS '外部合同ID（文档中的contractId），唯一标识';
COMMENT ON COLUMN customize_contract_info.contract_name IS '合同名称（dxmc）';
COMMENT ON COLUMN customize_contract_info.contract_no IS '合同编号（contractNo）';
COMMENT ON COLUMN customize_contract_info.subcontractor_id IS '分包商ID（5069ID）';
COMMENT ON COLUMN customize_contract_info.subcontractor_name IS '分包商名称';
COMMENT ON COLUMN customize_contract_info.unified_credit_code IS '统一社会信用代码';
COMMENT ON COLUMN customize_contract_info.contract_amount IS '合同金额（元）';

-- 合同-项目关联表
CREATE TABLE IF NOT EXISTS customize_contract_project_ref (
    gid BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(36) DEFAULT 'admin',
    f_bizstate INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    founder INT DEFAULT 0,
    last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ls_modifier INT DEFAULT 0,
    contract_id BIGINT,
    project_id BIGINT
);

COMMENT ON TABLE customize_contract_project_ref IS '合同与项目关联表 - 记录合同与项目的多对多关联关系';
COMMENT ON COLUMN customize_contract_project_ref.gid IS '自增主键';
COMMENT ON COLUMN customize_contract_project_ref.owner_id IS '所有者ID（默认admin，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_contract_project_ref.f_bizstate IS '业务状态（默认0=正常）';
COMMENT ON COLUMN customize_contract_project_ref.create_time IS '记录创建时间';
COMMENT ON COLUMN customize_contract_project_ref.founder IS '创建人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_contract_project_ref.last_time IS '记录修改时间';
COMMENT ON COLUMN customize_contract_project_ref.ls_modifier IS '修改人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_contract_project_ref.contract_id IS '合同ID（关联customize_contract_info.contract_id）';
COMMENT ON COLUMN customize_contract_project_ref.project_id IS '项目ID（关联customize_project_info.project_id）';

-- 工程量清单主表
-- 注意：biz_create_time / biz_last_update_time 为 DAP 业务时间字段，
--       不与通用 create_time / last_time 混淆
CREATE TABLE IF NOT EXISTS customize_boq_main (
    gid BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(36) DEFAULT 'admin',
    f_bizstate INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    founder INT DEFAULT 0,
    last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ls_modifier INT DEFAULT 0,
    boq_id BIGINT,
    project_id BIGINT,
    contract_id BIGINT,
    base_org_code VARCHAR(8),
    unit_code VARCHAR(10),
    biz_create_time TIMESTAMP,
    biz_last_update_time TIMESTAMP
);

COMMENT ON TABLE customize_boq_main IS '工程量清单主表（CM_SCH_BOQ_MAIN）- 存储DAP同步的工程量清单主数据';
COMMENT ON COLUMN customize_boq_main.gid IS '自增主键';
COMMENT ON COLUMN customize_boq_main.owner_id IS '所有者ID（默认admin，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_boq_main.f_bizstate IS '业务状态（默认0=正常）';
COMMENT ON COLUMN customize_boq_main.create_time IS '记录创建时间';
COMMENT ON COLUMN customize_boq_main.founder IS '创建人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_boq_main.last_time IS '记录修改时间';
COMMENT ON COLUMN customize_boq_main.ls_modifier IS '修改人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_boq_main.boq_id IS '外部BOQ主键（BOQ_GID），唯一标识';
COMMENT ON COLUMN customize_boq_main.project_id IS '项目对象ID（PROJECT_OBJECT），关联项目主数据';
COMMENT ON COLUMN customize_boq_main.contract_id IS '合同对象ID（CONTRACT_OBJECT），关联合同主数据';
COMMENT ON COLUMN customize_boq_main.base_org_code IS '基层组织编码，如0442';
COMMENT ON COLUMN customize_boq_main.unit_code IS '项目责任部门编码';
COMMENT ON COLUMN customize_boq_main.biz_create_time IS '工程量清单创建时间（来自DAP，业务字段）';
COMMENT ON COLUMN customize_boq_main.biz_last_update_time IS '工程量清单最后修改时间（来自DAP，业务字段）';

-- 工程量清单明细表
CREATE TABLE IF NOT EXISTS customize_boq_detail (
    gid BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(36) DEFAULT 'admin',
    f_bizstate INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    founder INT DEFAULT 0,
    last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ls_modifier INT DEFAULT 0,
    detail_id BIGINT,
    boq_id BIGINT,
    detail_code VARCHAR(50),
    detail_name VARCHAR(100),
    feature_description VARCHAR(1000),
    measurement_unit VARCHAR(50),
    unit_price_with_tax DECIMAL(20,4),
    taxrate DECIMAL(10,4),
    original_quantity DECIMAL(20,4),
    cumulative_change_quantity DECIMAL(18,4),
    contract_amount DECIMAL(20,2),
    current_quantity DECIMAL(20,4),
    remark VARCHAR(255),
    current_total_amount DECIMAL(20,2),
    is_leaf_node INTEGER,
    node_level VARCHAR(500),
    approved_quantity_cumulative DECIMAL(20,4)
);

COMMENT ON TABLE customize_boq_detail IS '工程量清单明细表（CM_SCH_BOQ_LIST）- 存储工程量清单的明细条目';
COMMENT ON COLUMN customize_boq_detail.gid IS '自增主键';
COMMENT ON COLUMN customize_boq_detail.owner_id IS '所有者ID（默认admin，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_boq_detail.f_bizstate IS '业务状态（默认0=正常）';
COMMENT ON COLUMN customize_boq_detail.create_time IS '记录创建时间';
COMMENT ON COLUMN customize_boq_detail.founder IS '创建人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_boq_detail.last_time IS '记录修改时间';
COMMENT ON COLUMN customize_boq_detail.ls_modifier IS '修改人（默认0，TODO 内网环境替换为当前登录用户ID）';
COMMENT ON COLUMN customize_boq_detail.detail_id IS '清单明细主键（LIST_GID），外部唯一标识';
COMMENT ON COLUMN customize_boq_detail.boq_id IS '关联customize_boq_main主键（BOQ_GID），主子表关联字段';
COMMENT ON COLUMN customize_boq_detail.detail_code IS '清单编码（LIST_CODE）';
COMMENT ON COLUMN customize_boq_detail.detail_name IS '清单名称（LIST_NAME）';
COMMENT ON COLUMN customize_boq_detail.feature_description IS '特征描述（FEATURE_DESC），如土质、深度等参数';
COMMENT ON COLUMN customize_boq_detail.measurement_unit IS '计量单位名称（MEASUR_UNIT_NAME），如m³、t';
COMMENT ON COLUMN customize_boq_detail.unit_price_with_tax IS '综合单价-含税（BID_PRICE_INC_TAX）';
COMMENT ON COLUMN customize_boq_detail.taxrate IS '税率（%），如9.0000';
COMMENT ON COLUMN customize_boq_detail.original_quantity IS '原始合同工程量（QUANTITY），此数据不会发生变化';
COMMENT ON COLUMN customize_boq_detail.cumulative_change_quantity IS '累计变更量（BOQ_CHG_CNT），工程量调整单中的变更数';
COMMENT ON COLUMN customize_boq_detail.contract_amount IS '合同金额（AMOU_IN_TOT）= 综合单价*原始合同工程量';
COMMENT ON COLUMN customize_boq_detail.current_quantity IS '当前工程量（MATE_QUAN）= 合同量 + 累计变更量';
COMMENT ON COLUMN customize_boq_detail.remark IS '备注（REMARK_ITEM）';
COMMENT ON COLUMN customize_boq_detail.current_total_amount IS '当前合计金额（T_PRI_INC_TAX）= 当前工程量*综合单价';
COMMENT ON COLUMN customize_boq_detail.is_leaf_node IS '是否末级节点（IS_LEAF_NODE）：1=是 0=否';
COMMENT ON COLUMN customize_boq_detail.node_level IS '节点层级（LEVEL_TREE），如1.1.1';
COMMENT ON COLUMN customize_boq_detail.approved_quantity_cumulative IS '累计核定数量（APPRV_QUANTITY_CUM），已完成进度款审批的工程量累计值';

-- =====================================================
-- 测试数据
-- =====================================================

-- 项目测试数据
INSERT INTO customize_project_info (base_org_code, base_org_code_name, unit_code, unit_code_name, project_id, project_name, project_code)
VALUES
('0442', '河北送变电', '01000101', '变电一分公司', 1000000001, '2025年度电网基建工程-线路改造项目', 'GCCW-2025-XMBH-0001'),
('0442', '河北送变电', '01000102', '变电二分公司', 1000000002, '2025年度配电网自动化升级改造工程', 'GCCW-2025-XMBH-0002'),
('0442', '河北送变电', '01000103', '输电分公司',   1000000003, '变电站综合自动化系统改造项目',     'GCCW-2025-XMBH-0003'),
('0442', '河北送变电', '01000101', '变电一分公司', 1000000004, '220kV输变电工程-线路施工项目',     'GCCW-2025-XMBH-0004'),
('0442', '河北送变电', '01000102', '变电二分公司', 1000000005, '110kV变电站新建工程',              'GCCW-2025-XMBH-0005');

-- 合同测试数据
INSERT INTO customize_contract_info (base_org_code, base_org_code_name, unit_code, unit_code_name, contract_id, contract_name, contract_no, subcontractor_id, subcontractor_name, unified_credit_code, contract_amount)
VALUES
('0442', '河北送变电', '01000101', '变电一分公司', 1000000001, '2025年度线路改造工程施工分包合同',   'HT-2025-0001', 2000000001, 'XX电力建设有限公司',      '91110000123456789X', 5000000.00),
('0442', '河北送变电', '01000102', '变电二分公司', 1000000002, '变电站综合自动化系统改造分包合同', 'HT-2025-0002', 2000000002, 'YY机电安装工程有限公司',  '91310000987654321Y', 3200000.50),
('0442', '河北送变电', '01000103', '输电分公司',   1000000003, '配电网自动化升级改造劳务分包合同', 'HT-2025-0003', 2000000003, 'ZZ送变电工程公司',        '91410000555554444Z', 1800000.00),
('0442', '河北送变电', '01000101', '变电一分公司', 1000000004, '220kV线路施工劳务分包合同',         'HT-2025-0004', 2000000001, 'XX电力建设有限公司',      '91110000123456789X', 6800000.00);

-- 合同-项目关联数据
INSERT INTO customize_contract_project_ref (contract_id, project_id)
VALUES
(1000000001, 1000000001),
(1000000001, 1000000002),
(1000000002, 1000000003),
(1000000003, 1000000002),
(1000000004, 1000000004);

-- 工程量清单主表测试数据
INSERT INTO customize_boq_main (boq_id, project_id, contract_id, base_org_code, unit_code, biz_create_time, biz_last_update_time)
VALUES
(1000000000000000001, 1000000001, 1000000001, '0442', '01000101', '2026-06-01 10:30:00', '2026-07-01 14:20:00'),
(1000000000000000002, 1000000002, 1000000002, '0442', '01000102', '2026-06-05 09:15:00', '2026-07-03 16:45:00');

-- 工程量清单明细测试数据
INSERT INTO customize_boq_detail (detail_id, boq_id, detail_code, detail_name, feature_description, measurement_unit, unit_price_with_tax, taxrate, original_quantity, cumulative_change_quantity, contract_amount, current_quantity, remark, current_total_amount, is_leaf_node, node_level, approved_quantity_cumulative)
VALUES
(2000000000000000001, 1000000000000000001, '010101001', '基坑土方开挖',     '土质：一般土；深度：≤2m；运距：1km以内',       'm³',  25.50, 9.0000, 1000.00,  100.00, 25500.00, 1100.00, '包含支护费用',    28050.00, 1, '1.1.1', 500.00),
(2000000000000000002, 1000000000000000001, '010101002', '基坑回填砂砾石',   '压实度：≥95%；材料：天然级配砂砾石',            'm³',  38.00, 9.0000, 800.00,   0.00,   30400.00, 800.00,  '',                 30400.00, 1, '1.1.2', 300.00),
(2000000000000000003, 1000000000000000001, '010101003', '基础钢筋制安',     '钢筋种类：HRB400；直径：≥18mm',                 't',   5200.00, 9.0000, 50.00,   5.00,   260000.00, 55.00,  '',                 286000.00, 1, '1.2.1', 30.00),
(2000000000000000004, 1000000000000000002, '020201001', '混凝土C30浇筑',    '强度等级：C30；抗渗等级：P8；泵送',              'm³',  480.00, 9.0000, 2000.00, 0.00,   960000.00, 2000.00, '',                 960000.00, 1, '2.1.1', 800.00),
(2000000000000000005, 1000000000000000002, '020201002', '混凝土C25浇筑',    '强度等级：C25；抗渗等级：P6；泵送',              'm³',  420.00, 9.0000, 1500.00, -50.00, 630000.00, 1450.00, '',                 609000.00, 1, '2.1.2', 600.00),
(2000000000000000006, 1000000000000000002, '020202001', '钢筋制安（主体）', '钢筋种类：HRB400E；直径：12-25mm',              't',   5500.00, 9.0000, 120.00,  10.00,  660000.00, 130.00,  '含试验费',         715000.00, 1, '2.2.1', 80.00);
