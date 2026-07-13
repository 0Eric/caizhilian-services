# 财智链系统 - 接口测试指南

## 通用说明

### IP访问白名单

所有 API 接口受 **IP白名单** 保护，只有配置在 `application.yml` 中的 IP 才能访问：

```yaml
api:
  whitelist:
    enabled: true
    ips:
      - 127.0.0.1        # 本机
      - ::1              # IPv6 本机
      - 192.168.1.0/24   # 内网段
      - 10.0.0.0/8       # 内网段
      - 172.16.0.0/12    # 内网段
```

- 支持**单IP**（如 `192.168.1.100`）和 **CIDR网段**（如 `10.0.0.0/8`）
- 不在白名单的 IP 请求会返回：
  ```json
  {"code":"6100010","message":"IP不在访问白名单中，请联系管理员添加: xxx.xxx.xxx.xxx","data":null}
  ```
- 本地测试默认已包含 `127.0.0.1` 和 `::1`，默认不受影响
- 修改 `api.whitelist.enabled: false` 可关闭白名单

---

## 财智链服务（4.2.2节 财智链系统提供的服务）

| 序号 | 服务名称 | 接口地址 | HTTP方法 |
|:----:|----------|----------|:--------:|
| 1 | 同步项目信息 | `POST http://localhost:8080/api/sync/projectInfoList` | POST |
| 2 | 同步合同信息 | `POST http://localhost:8080/api/sync/contractInfoList` | POST |
| 3 | 同步工程量清单信息 | `POST http://localhost:8080/api/sync/boqInfoList` | POST |

---

## DAP Mock服务（4.2.3节 DAP系统提供的服务-模拟）

| 序号 | 服务名称 | 接口地址 | HTTP方法 |
|:----:|----------|----------|:--------:|
| 1 | 推送工程签证单 | `POST http://localhost:8080/dap/mock/receive/visa` | POST |
| 2 | 推送工程量调整单 | `POST http://localhost:8080/dap/mock/receive/boq-adjust` | POST |
| 3 | 推送进度款审批单 | `POST http://localhost:8080/dap/mock/receive/progress-payment` | POST |
| 4 | 推送结算审核定案单 | `POST http://localhost:8080/dap/mock/receive/settlement` | POST |

---

## DAP Mock 测试数据

### 1. 推送工程签证单

```
POST http://localhost:8080/dap/mock/receive/visa
Content-Type: application/json
```

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

**预期响应：**
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

### 2. 推送工程量调整单

```
POST http://localhost:8080/dap/mock/receive/boq-adjust
Content-Type: application/json
```

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

**预期响应：**
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

### 3. 推送进度款审批单

```
POST http://localhost:8080/dap/mock/receive/progress-payment
Content-Type: application/json
```

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

**预期响应：**
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

### 4. 推送结算审核定案单

```
POST http://localhost:8080/dap/mock/receive/settlement
Content-Type: application/json
```

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

**预期响应：**
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

## 数据库连接信息

---

## 接口 1：同步项目信息

### 请求地址
```
POST http://localhost:8080/api/sync/projectInfoList
```

### 请求头
```
Content-Type: application/json
```

### 请求Body (JSON)
```json
[
    {
        "baseOrgCode": "0442",
        "baseOrgCodeName": "河北送变电",
        "unitCode": "01000101",
        "unitCodeName": "变电一分公司",
        "projectId": 1000000001,
        "projectName": "2025年度电网基建工程-线路改造项目",
        "projectCode": "GCCW-2025-XMBH-0001"
    },
    {
        "baseOrgCode": "0442",
        "baseOrgCodeName": "河北送变电",
        "unitCode": "01000102",
        "unitCodeName": "变电二分公司",
        "projectId": 1000000002,
        "projectName": "2025年度配电网自动化升级改造工程",
        "projectCode": "GCCW-2025-XMBH-0002"
    },
    {
        "baseOrgCode": "0442",
        "baseOrgCodeName": "河北送变电",
        "unitCode": "01000103",
        "unitCodeName": "输电分公司",
        "projectId": 1000000003,
        "projectName": "变电站综合自动化系统改造项目",
        "projectCode": "GCCW-2025-XMBH-0003"
    }
]
```

### 成功响应
```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": [
        {
            "id": 1000000001,
            "feedbackMessage": "项目信息同步成功"
        },
        {
            "id": 1000000002,
            "feedbackMessage": "项目信息同步成功"
        },
        {
            "id": 1000000003,
            "feedbackMessage": "项目信息同步成功"
        }
    ]
}
```

---

## 接口 2：同步合同信息

### 请求地址
```
POST http://localhost:8080/api/sync/contractInfoList
```

### 请求头
```
Content-Type: application/json
```

### 请求Body (JSON)
```json
[
    {
        "baseOrgCode": "0442",
        "baseOrgCodeName": "河北送变电",
        "unitCode": "01000101",
        "unitCodeName": "变电一分公司",
        "contractId": 1000000001,
        "contractName": "2025年度线路改造工程施工分包合同",
        "contractNo": "HT-2025-0001",
        "subcontractorId": 2000000001,
        "subcontractorName": "XX电力建设有限公司",
        "unifiedCreditCode": "91110000123456789X",
        "contractAmount": 5000000.00,
        "projectIds": [1000000001, 1000000002]
    },
    {
        "baseOrgCode": "0442",
        "baseOrgCodeName": "河北送变电",
        "unitCode": "01000102",
        "unitCodeName": "变电二分公司",
        "contractId": 1000000002,
        "contractName": "变电站综合自动化系统改造分包合同",
        "contractNo": "HT-2025-0002",
        "subcontractorId": 2000000002,
        "subcontractorName": "YY机电安装工程有限公司",
        "unifiedCreditCode": "91310000987654321Y",
        "contractAmount": 3200000.50,
        "projectIds": [1000000003]
    },
    {
        "baseOrgCode": "0442",
        "baseOrgCodeName": "河北送变电",
        "unitCode": "01000103",
        "unitCodeName": "输电分公司",
        "contractId": 1000000003,
        "contractName": "配电网自动化升级改造劳务分包合同",
        "contractNo": "HT-2025-0003",
        "subcontractorId": 2000000003,
        "subcontractorName": "ZZ送变电工程公司",
        "unifiedCreditCode": "91410000555554444Z",
        "contractAmount": 1800000.00,
        "projectIds": []
    }
]
```

### 成功响应
```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": [
        {
            "id": 1000000001,
            "feedbackMessage": "合同信息同步成功"
        },
        {
            "id": 1000000002,
            "feedbackMessage": "合同信息同步成功"
        },
        {
            "id": 1000000003,
            "feedbackMessage": "合同信息同步成功"
        }
    ]
}
```

---

## 接口 3：同步工程量清单信息

### 请求地址
```
POST http://localhost:8080/api/sync/boqInfoList
```

### 请求头
```
Content-Type: application/json
```

### 请求Body (JSON)
```json
[
    {
        "boqId": 1000000000000000001,
        "projectId": 1000000001,
        "contractId": 1000000001,
        "baseOrgCode": "0442",
        "unitCode": "01000101",
        "createTime": "2026-06-01 10:30:00",
        "lastUpdateTime": "2026-07-01 14:20:00",
        "boqDetailList": [
            {
                "detailId": 2000000000000000001,
                "boqId": 1000000000000000001,
                "detailCode": "010101001",
                "detailName": "基坑土方开挖",
                "featureDescription": "土质：一般土；深度：≤2m；运距：1km以内",
                "measurementUnit": "m³",
                "unitPriceWithTax": 25.50,
                "taxrate": 9.00,
                "originalQuantity": 1000.00,
                "cumulativeChangeQuantity": 100.00,
                "contractAmount": 25500.00,
                "currentQuantity": 1100.00,
                "remark": "包含支护费用",
                "currentTotalAmount": 28050.00,
                "isLeafNode": 1,
                "nodeLevel": "1.1.1",
                "approvedQuantityCumulative": 500.00
            },
            {
                "detailId": 2000000000000000002,
                "boqId": 1000000000000000001,
                "detailCode": "010101002",
                "detailName": "基坑回填砂砾石",
                "featureDescription": "压实度：≥95%；材料：天然级配砂砾石",
                "measurementUnit": "m³",
                "unitPriceWithTax": 38.00,
                "taxrate": 9.00,
                "originalQuantity": 800.00,
                "cumulativeChangeQuantity": 0.00,
                "contractAmount": 30400.00,
                "currentQuantity": 800.00,
                "remark": "",
                "currentTotalAmount": 30400.00,
                "isLeafNode": 1,
                "nodeLevel": "1.1.2",
                "approvedQuantityCumulative": 300.00
            },
            {
                "detailId": 2000000000000000003,
                "boqId": 1000000000000000001,
                "detailCode": "010101003",
                "detailName": "基础钢筋制安",
                "featureDescription": "钢筋种类：HRB400；直径：≥18mm",
                "measurementUnit": "t",
                "unitPriceWithTax": 5200.00,
                "taxrate": 9.00,
                "originalQuantity": 50.00,
                "cumulativeChangeQuantity": 5.00,
                "contractAmount": 260000.00,
                "currentQuantity": 55.00,
                "remark": "",
                "currentTotalAmount": 286000.00,
                "isLeafNode": 1,
                "nodeLevel": "1.2.1",
                "approvedQuantityCumulative": 30.00
            }
        ]
    },
    {
        "boqId": 1000000000000000002,
        "projectId": 1000000002,
        "contractId": 1000000002,
        "baseOrgCode": "0442",
        "unitCode": "01000102",
        "createTime": "2026-06-05 09:15:00",
        "lastUpdateTime": "2026-07-03 16:45:00",
        "boqDetailList": [
            {
                "detailId": 2000000000000000004,
                "boqId": 1000000000000000002,
                "detailCode": "020201001",
                "detailName": "混凝土C30浇筑",
                "featureDescription": "强度等级：C30；抗渗等级：P8；泵送",
                "measurementUnit": "m³",
                "unitPriceWithTax": 480.00,
                "taxrate": 9.00,
                "originalQuantity": 2000.00,
                "cumulativeChangeQuantity": 0.00,
                "contractAmount": 960000.00,
                "currentQuantity": 2000.00,
                "remark": "",
                "currentTotalAmount": 960000.00,
                "isLeafNode": 1,
                "nodeLevel": "2.1.1",
                "approvedQuantityCumulative": 800.00
            },
            {
                "detailId": 2000000000000000005,
                "boqId": 1000000000000000002,
                "detailCode": "020201002",
                "detailName": "混凝土C25浇筑",
                "featureDescription": "强度等级：C25；抗渗等级：P6；泵送",
                "measurementUnit": "m³",
                "unitPriceWithTax": 420.00,
                "taxrate": 9.00,
                "originalQuantity": 1500.00,
                "cumulativeChangeQuantity": -50.00,
                "contractAmount": 630000.00,
                "currentQuantity": 1450.00,
                "remark": "",
                "currentTotalAmount": 609000.00,
                "isLeafNode": 1,
                "nodeLevel": "2.1.2",
                "approvedQuantityCumulative": 600.00
            },
            {
                "detailId": 2000000000000000006,
                "boqId": 1000000000000000002,
                "detailCode": "020202001",
                "detailName": "钢筋制安（主体）",
                "featureDescription": "钢筋种类：HRB400E；直径：12-25mm",
                "measurementUnit": "t",
                "unitPriceWithTax": 5500.00,
                "taxrate": 9.00,
                "originalQuantity": 120.00,
                "cumulativeChangeQuantity": 10.00,
                "contractAmount": 660000.00,
                "currentQuantity": 130.00,
                "remark": "含试验费",
                "currentTotalAmount": 715000.00,
                "isLeafNode": 1,
                "nodeLevel": "2.2.1",
                "approvedQuantityCumulative": 80.00
            }
        ]
    }
]
```

### 成功响应
```json
{
    "code": "6100000",
    "message": "操作成功",
    "data": [
        {
            "id": 1000000000000000001,
            "feedbackMessage": "工程量清单同步成功"
        },
        {
            "id": 1000000000000000002,
            "feedbackMessage": "工程量清单同步成功"
        }
    ]
}
```

---

## 数据库连接信息

| 项目 | 值 |
|:----|:----|
| 数据库类型 | PostgreSQL |
| 主机 | localhost |
| 端口 | 5432 |
| 数据库名 | caizhilian |
| 用户名 | postgres |
| 密码 | postgres |

## 建库SQL
```sql
CREATE DATABASE caizhilian;
```

建表脚本在项目的 `src/main/resources/init.sql` 中，启动项目时 JPA 会自动建表（ddl-auto: update）。
init.sql 中还包含了测试数据，建库后可以手动执行一次导入测试数据。
