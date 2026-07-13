# DAP Mock 接口测试指南

> 对应文档第 **4.2.3 节 DAP系统提供的服务**
> 模拟了DAP系统接收财智链推送的4个接口，业务逻辑为 //TODO 占位，直接返回成功响应。

---

## 接口概览

| 序号 | 服务名称 | 接口地址 | HTTP方法 |
|:----:|----------|----------|:--------:|
| 1 | 推送工程签证单信息 | `POST http://localhost:8080/dap/mock/receive/visa` | POST |
| 2 | 推送工程量调整单信息 | `POST http://localhost:8080/dap/mock/receive/boq-adjust` | POST |
| 3 | 推送进度款审批单信息 | `POST http://localhost:8080/dap/mock/receive/progress-payment` | POST |
| 4 | 推送结算审核定案单信息 | `POST http://localhost:8080/dap/mock/receive/settlement` | POST |

---

## 公共响应格式

所有接口返回统一格式：

```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": {
        "id": 1234567890,
        "feedbackMessage": "xxx信息接收成功"
    }
}
```

| 状态码 | 含义 |
|:------:|------|
| 6100000 | 操作成功 |
| 6100010 | 请求数据格式校验失败 |
| 6100020 | 网络连接失败 |

---

## 接口 1：推送工程签证单信息

**触发时机**：财智链系统工程签证功能签批完成或单据作废后触发同步。

### 请求地址

```
POST http://localhost:8080/dap/mock/receive/visa
```

### 请求头

```
Content-Type: application/json
```

### 请求Body (JSON)

```json
{
    "confirmGid": 1234567890123456789,
    "changeNt": "QZ-2026-0001",
    "projectId": "2021-1000000001",
    "orgCotNo": "3523-1000000001",
    "busContent": "土质变化导致基坑开挖深度增加2m，需增加机械破碎及支护费用",
    "note": "财智链集成签证数据+【20260710113030】",
    "adjAmt": 35000.00,
    "projDeptApprAmt": 32000.00,
    "branchApprAmt": 30000.00,
    "pmsceApprAmt": 28000.00,
    "ttAmtIcTax": 28000.00,
    "compleApprTime": "2026-07-10 11:30:30",
    "creTime": "2026-07-08 14:03:30",
    "compId": "0442",
    "projCompId": 1001,
    "conCompanyId": 2000000001,
    "udsId": ["UDS-20260708-001"],
    "docuStatus": "Active"
}
```

### 参数字段说明

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| confirmGid | Long | 是 | 财智链单据主键，单据唯一标识 |
| changeNt | String | 是 | 申请编号，财智链单据编号 |
| projectId | String | 是 | 项目对象ID，DAP基于此自动填充项目编码、名称等 |
| orgCotNo | String | 是 | 合同对象ID，DAP基于此自动填充合同编码、名称等 |
| busContent | String | 是 | 签证事由，对应财智链的签证原因 |
| note | String | 是 | 备注，默认格式：财智链集成签证数据+【制单日期】 |
| adjAmt | BigDecimal | 是 | 调整/核减金额 |
| projDeptApprAmt | BigDecimal | 是 | 项目部审定金额 |
| branchApprAmt | BigDecimal | 是 | 分公司审定金额 |
| pmsceApprAmt | BigDecimal | 是 | 施工管理部审定金额 |
| ttAmtIcTax | BigDecimal | 是 | 技经中心审定金额（签证金额） |
| compleApprTime | String | 是 | 批准日期 |
| creTime | String | 是 | 创建日期 |
| compId | String | 是 | 创建单位代码（送变电公司），如0442 |
| projCompId | Integer | 是 | 项目责任部门 |
| conCompanyId | Integer | 是 | 关联签证单位，施工方管理对象ID |
| udsId | String[] | 否 | UDS附件ID，支持数组解析 |
| docuStatus | String | 是 | 单据状态：Active=正常，Inactive=作废 |

### 成功响应

```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": {
        "id": 1234567890123456789,
        "feedbackMessage": "工程签证信息接收成功"
    }
}
```

---

## 接口 2：推送工程量调整单信息

**触发时机**：财智链系统工程量调整功能签批完成或单据作废后触发同步。

### 请求地址

```
POST http://localhost:8080/dap/mock/receive/boq-adjust
```

### 请求头

```
Content-Type: application/json
```

### 请求Body (JSON)

```json
{
    "gid": 2000000001,
    "changeDocNo": "TZ-2026-0001",
    "projectId": 1000000001,
    "contractId": 1000000001,
    "purChaRea": "由于设计变更，基坑土方开挖工程量增加100m³",
    "adjAmt": 2550.00,
    "projDeptApprAmt": 2550.00,
    "branchApprAmt": 2550.00,
    "pmsceApprAmt": 2550.00,
    "ttAmtIcTax": 2550.00,
    "projCompId": "01000101",
    "mainDeptId": "5069-2000000001",
    "creTime": "2026-07-08 10:00:00",
    "compleApprTime": "2026-07-10 11:00:00",
    "compId": "0442",
    "udsId": ["UDS-20260708-002"],
    "docuStatus": "Active",
    "items": [
        {
            "billid": 2000000001001,
            "listGid": "LIST-010101001",
            "parentListId": "LIST-010101000",
            "listCode": "010101001",
            "mateQuan": 1000.00,
            "changeAftCumuCptWorkload": 1100.00,
            "changeAfterAmount": 28050.00,
            "adjustAmount": 2550.00,
            "changeType": 1
        }
    ]
}
```

### 参数字段说明

#### 主表字段

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| gid | Long | 是 | 单据主键，单据唯一标识 |
| changeDocNo | String | 否 | 变更单号，对应财智链单据编号 |
| projectId | Long | 是 | 项目对象ID |
| contractId | Long | 是 | 合同对象ID |
| purChaRea | String | 是 | 变更原因 |
| adjAmt | BigDecimal | 是 | 调整/核减金额 |
| projDeptApprAmt | BigDecimal | 是 | 项目部审定金额 |
| branchApprAmt | BigDecimal | 是 | 分公司审定金额 |
| pmsceApprAmt | BigDecimal | 是 | 施工管理部审定金额 |
| ttAmtIcTax | BigDecimal | 是 | 技经中心审定金额 |
| projCompId | String | 是 | 项目责任部门 |
| mainDeptId | String | 是 | 提出单位（施工单位对象ID） |
| creTime | String | 是 | 创建时间 |
| compleApprTime | String | 是 | 批准时间 |
| compId | String | 是 | 所属单位（送变电公司） |
| udsId | String[] | 否 | UDS附件ID |
| docuStatus | String | 是 | 单据状态 |

#### items 子表字段

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| billid | Long | 是 | 明细主键 |
| listGid | String | 是 | 清单主键，来源于清单基本信息 |
| parentListId | String | 是 | 父节点清单ID，构建树形关系 |
| listCode | String | 是 | 清单编码 |
| mateQuan | BigDecimal | 是 | 当前工程量（合同数量） |
| changeAftCumuCptWorkload | BigDecimal | 是 | 变更后工程量（实际数量） |
| changeAfterAmount | BigDecimal | 是 | 变更后金额（实际金额）= 实际数量×单价 |
| adjustAmount | BigDecimal | 是 | 调整金额 = 实际金额 - 合同金额 |
| changeType | Integer | 是 | 变更类型：0=无变更 1=调整 2=新增 |

### 成功响应

```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": {
        "id": 2000000001,
        "feedbackMessage": "工程量调整信息接收成功"
    }
}
```

---

## 接口 3：推送进度款审批单信息

**触发时机**：财智链系统进度款审批功能签批完成或单据作废后触发同步。

### 请求地址

```
POST http://localhost:8080/dap/mock/receive/progress-payment
```

### 请求头

```
Content-Type: application/json
```

### 请求Body (JSON)

```json
{
    "gid": 3000000001,
    "billid": "JDK-2026-0001",
    "projectId": "2021-1000000001",
    "contractId": "3523-1000000001",
    "remarkItem": "财智链集成进度款审批单数据+【20260710113030】",
    "projCompId": "01000101",
    "mainDeptId": "1378-2000000001",
    "creTime": "2026-07-05 09:00:00",
    "compleApprTime": "2026-07-10 11:30:30",
    "compId": "0442",
    "currentPaymentAmount": 150000.00,
    "paySalaryMonth": "2026-06",
    "paySalaryAmount": 80000.00,
    "unifiedPaymentAmount": 150000.00,
    "safetyCivilFee": 5000.00,
    "udsId": ["UDS-20260705-001"],
    "docuStatus": "Active",
    "items": [
        {
            "billid": 3000000001001,
            "ywid": 3000000001,
            "listGid": "LIST-010101001",
            "listCode": "010101001",
            "apprvQuantity": 500.00,
            "currTermApprvAmt": 12750.00,
            "remarkItem": "财智链集成工程量计量清单数据"
        },
        {
            "billid": 3000000001002,
            "ywid": 3000000001,
            "listGid": "LIST-010101002",
            "listCode": "010101002",
            "apprvQuantity": 300.00,
            "currTermApprvAmt": 11400.00,
            "remarkItem": "财智链集成工程量计量清单数据"
        }
    ]
}
```

### 参数字段说明

#### 主表字段

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| gid | Long | 是 | 单据主键，单据唯一标识 |
| billid | String | 是 | 单据编号，对应财智链单据编号 |
| projectId | String | 是 | 工程项目ID |
| contractId | String | 是 | 工程合同ID |
| remarkItem | String | 是 | 备注项目 |
| projCompId | String | 是 | 项目责任部门 |
| mainDeptId | String | 是 | 提出单位（施工单位对象ID） |
| creTime | String | 是 | 创建时间 |
| compleApprTime | String | 是 | 批准时间 |
| compId | String | 是 | 所属单位（送变电公司） |
| currentPaymentAmount | BigDecimal | 是 | 本次进度款申请金额 |
| paySalaryMonth | String | 是 | 支付工资月份，如"2026-06" |
| paySalaryAmount | BigDecimal | 是 | 支付工资金额 |
| unifiedPaymentAmount | BigDecimal | 是 | 统一支付金额 |
| safetyCivilFee | BigDecimal | 是 | 其中安全文明施工费 |
| udsId | String[] | 否 | UDS附件ID |
| docuStatus | String | 是 | 单据状态 |

#### items 子表字段

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| billid | Long | 是 | 明细主键 |
| ywid | Long | 是 | 外键，关联主表主键 |
| listGid | String | 是 | 清单主键，来源于清单基本信息 |
| listCode | String | 是 | 清单编码 |
| apprvQuantity | BigDecimal | 是 | 本期核定量，对应财智链的工程量 |
| currTermApprvAmt | BigDecimal | 是 | 本期核定金额（元） |
| remarkItem | String | 是 | 备注 |

### 成功响应

```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": {
        "id": 3000000001,
        "feedbackMessage": "进度款审批信息接收成功"
    }
}
```

---

## 接口 4：推送结算审核定案单信息

**触发时机**：财智链系统结算审核定案单功能签批完成或单据作废后触发同步。

### 请求地址

```
POST http://localhost:8080/dap/mock/receive/settlement
```

### 请求头

```
Content-Type: application/json
```

### 请求Body (JSON)

```json
{
    "gid": 4000000001,
    "appliNo": "JS-2026-0001",
    "projectId": 1000000001,
    "stlCfmDate": "2026-07-10 11:30:30",
    "projCompId": "01000101",
    "creTime": "2026-07-01 08:00:00",
    "compleApprTime": "2026-07-10 11:30:30",
    "compId": "0442",
    "udsId": ["UDS-20260701-001", "UDS-20260701-002"],
    "docuStatus": "Active",
    "contractId": 1000000001,
    "mainDeptId": "5069-2000000001",
    "amtSubRev": 1250000.00,
    "confirmVariatOrder": 28000.00,
    "bodyAdjAmt": 2550.00,
    "outsourcingContent": "2025年度线路改造工程施工分包",
    "outsourcingType": "专业分包",
    "detailItems": [
        {
            "billid": 4000000001001,
            "listGid": "LIST-010101001",
            "parentListId": "LIST-010101000",
            "listCode": "010101001",
            "mateQuan": 1000.00,
            "changeAftCumuCptWorkload": 1100.00,
            "changeAfterAmount": 28050.00,
            "adjustAmount": 2550.00
        },
        {
            "billid": 4000000001002,
            "listGid": "LIST-010101002",
            "parentListId": "LIST-010101000",
            "listCode": "010101002",
            "mateQuan": 800.00,
            "changeAftCumuCptWorkload": 800.00,
            "changeAfterAmount": 30400.00,
            "adjustAmount": 0.00
        }
    ],
    "visaItems": [
        {
            "visaCode": "QZ-2026-0001",
            "visaName": "基坑土方开挖深度增加签证",
            "settlementAmount": 28000.00,
            "adjustAmount": 28000.00
        }
    ]
}
```

### 参数字段说明

#### 主表字段

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| gid | Long | 是 | 单据主键，单据唯一标识 |
| appliNo | String | 是 | 结算编号，对应财智链单据编号 |
| projectId | Long | 是 | 项目ID |
| stlCfmDate | String | 是 | 结算审定日期 |
| projCompId | String | 是 | 项目所属组织 |
| creTime | String | 是 | 创建时间 |
| compleApprTime | String | 是 | 批准时间 |
| compId | String | 是 | 所属单位（送变电公司） |
| udsId | String[] | 否 | UDS附件ID，支持数组解析 |
| docuStatus | String | 是 | 单据状态 |
| contractId | Long | 是 | 合同ID |
| mainDeptId | String | 是 | 分包单位（施工单位对象ID） |
| amtSubRev | BigDecimal | 是 | 结算审定金额 |
| confirmVariatOrder | BigDecimal | 是 | 签证金额合计 |
| bodyAdjAmt | BigDecimal | 是 | 本体工程量调整金额 |
| outsourcingContent | String | 是 | 施工业务外包内容 |
| outsourcingType | String | 是 | 施工业务外包类型 |

#### detailItems 结算明细子表

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| billid | Long | 是 | 明细主键 |
| listGid | String | 是 | 清单主键 |
| parentListId | String | 是 | 父节点清单ID |
| listCode | String | 是 | 清单编码 |
| mateQuan | BigDecimal | 是 | 当前工程量（合同数量） |
| changeAftCumuCptWorkload | BigDecimal | 是 | 变更后工程量（实际数量） |
| changeAfterAmount | BigDecimal | 是 | 变更后金额（结算金额） |
| adjustAmount | BigDecimal | 是 | 调整金额 |

#### visaItems 签证明细子表

| 参数名 | 类型 | 必填 | 说明 |
|:-------|:----:|:----:|------|
| visaCode | String | 是 | 签证编码 |
| visaName | String | 是 | 签证名称 |
| settlementAmount | BigDecimal | 是 | 结算金额 |
| adjustAmount | BigDecimal | 是 | 调整金额（=结算金额） |

### 成功响应

```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": {
        "id": 4000000001,
        "feedbackMessage": "结算审核定案信息接收成功"
    }
}
```

---

## 客户端调用（DapServiceClient）

项目中已提供 `DapServiceClient` 客户端类，可直接注入使用：

```java
@Service
public class SomeBizService {

    @Autowired
    private DapServiceClient dapClient;

    // 签证审批完成后调用
    public void afterVisaApproved(VisaPushRequest request) {
        ApiResult<SyncFeedback> result = dapClient.pushVisa(request);
        if ("6100000".equals(result.getCode())) {
            log.info("签证推送DAP成功");
        }
    }

    // 工程量调整审批完成后调用
    public void afterBoqAdjustApproved(BoqAdjustPushRequest request) {
        dapClient.pushBoqAdjust(request);
    }

    // 进度款审批完成后调用
    public void afterProgressPaymentApproved(ProgressPaymentPushRequest request) {
        dapClient.pushProgressPayment(request);
    }

    // 结算定案完成后调用
    public void afterSettlementApproved(SettlementPushRequest request) {
        dapClient.pushSettlement(request);
    }
}
```

DAP地址可通过 `application.yml` 配置切换：

```yaml
dap:
  base-url: http://localhost:8080/dap/mock    # Mock模式（本服务）
  # base-url: http://真实DAP地址/dap/api       # 正式对接时切换
```

---

## 完整项目结构（新增文件）

```
caizhilian-services/src/main/java/com/caizhilian/
├── client/
│   └── DapServiceClient.java                  # ← 调用DAP接口的客户端
├── controller/
│   └── DapMockController.java                 # ← 模拟DAP服务的4个接收接口
├── dto/request/dap/
│   ├── VisaPushRequest.java                   #   工程签证单推送请求
│   ├── BoqAdjustPushRequest.java              #   工程量调整单推送请求
│   ├── ProgressPaymentPushRequest.java        #   进度款审批单推送请求
│   └── SettlementPushRequest.java             #   结算审核定案单推送请求
```
