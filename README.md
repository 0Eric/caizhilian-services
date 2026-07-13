# 财智链系统 - DAP集成数据同步服务

## 1. 项目概述

本项目是财智链系统与 DAP（送变电内部管理系统）之间的数据集成中间件，负责两端的业务数据互相同步。基于 Spring Boot 3.3.5 + JPA + PostgreSQL 构建。

### 1.1 集成关系

```
┌──────────────┐  同步项目/合同/工程量清单  ┌────────────────┐
│              │ ────────────────────────▶ │                │
│   DAP系统    │   POST /api/sync/**        │  财智链系统     │
│              │                            │  (本项目)       │
│              │ ◀──────────────────────── │                │
│              │   POST /dap/mock/**        │                │
└──────────────┘  推送签证/调整/进度款/结算  └────────────────┘
```

- **DAP → 财智链**（本文档 4.2.2 节）：DAP 调用本服务 `/api/sync/**` 推送主数据
- **财智链 → DAP**（本文档 4.2.3 节）：财智链签批完成后回调 DAP 接口（当前为 Mock）

### 1.2 技术栈

| 组件 | 版本 |
|:-----|:-----|
| Java | 17 |
| Spring Boot | 3.3.5 |
| Spring Data JPA | 3.3.5 |
| PostgreSQL Driver | 42.x |
| Lombok | 1.18.x |
| Jackson JSR310 | 2.17.x |
| HikariCP | 连接池 |

---

## 2. 项目结构

```
caizhilian-services/
├── src/main/java/com/caizhilian/
│   ├── CaiZhiLianApplication.java          # 启动入口
│   ├── client/
│   │   └── DapServiceClient.java           # DAP系统接口调用客户端
│   ├── common/result/
│   │   ├── ApiResult.java                  # 统一响应封装
│   │   └── SyncFeedback.java               # 同步反馈结果
│   ├── config/
│   │   ├── GlobalExceptionHandler.java     # 全局异常处理
│   │   ├── IpWhitelistInterceptor.java     # IP白名单拦截器
│   │   ├── IpWhitelistProperties.java      # 白名单配置属性
│   │   ├── JacksonConfig.java              # Jackson日期序列化配置
│   │   ├── RestTemplateConfig.java         # RestTemplate配置
│   │   └── WebMvcConfig.java              # Web MVC配置（注册拦截器）
│   ├── controller/
│   │   ├── DapMockController.java          # DAP Mock接口（4个推送接口）
│   │   └── SyncController.java            # DAP数据同步接口（3个同步接口）
│   ├── dto/request/
│   │   ├── dap/                             # DAP推送请求DTO
│   │   │   ├── BoqAdjustPushRequest.java
│   │   │   ├── ProgressPaymentPushRequest.java
│   │   │   ├── SettlementPushRequest.java
│   │   │   └── VisaPushRequest.java
│   │   ├── BoqSyncItem.java
│   │   ├── ContractSyncItem.java
│   │   └── ProjectSyncItem.java
│   ├── entity/
│   │   ├── BoqDetail.java
│   │   ├── BoqMain.java
│   │   ├── ContractInfo.java
│   │   ├── ContractProjectRef.java
│   │   └── ProjectInfo.java
│   ├── repository/
│   │   ├── BoqDetailRepository.java
│   │   ├── BoqMainRepository.java
│   │   ├── ContractInfoRepository.java
│   │   ├── ContractProjectRefRepository.java
│   │   └── ProjectInfoRepository.java
│   └── service/
│       ├── impl/
│       │   ├── BoqSyncServiceImpl.java
│       │   ├── ContractSyncServiceImpl.java
│       │   └── ProjectSyncServiceImpl.java
│       ├── BoqSyncService.java
│       ├── ContractSyncService.java
│       └── ProjectSyncService.java
├── src/main/resources/
│   ├── application.yml                     # 主配置文件
│   └── init.sql                            # 数据库初始化脚本（含测试数据）
├── pom.xml
└── .gitignore
```

---

## 3. 接口文档

### 3.1 DAP → 财智链（数据同步接口）

基路径：`/api/sync` | 受 IP 白名单保护

| 序号 | 方法 | 路径 | 说明 | 触发时机 |
|:---:|:-----|:-----|:-----|:-----|
| 1 | POST | `/api/sync/projectInfoList` | 同步项目信息 | 项目登记、项目变更 |
| 2 | POST | `/api/sync/contractInfoList` | 同步合同信息 | 分包合同登记、合同变更完成 |
| 3 | POST | `/api/sync/boqInfoList` | 同步工程量清单 | 工程量清单提交或审批完成 |

**共同特性：**
- 批量接收 List，支持幂等（项目ID/合同ID/BOQ_GID 存在则更新，否则新增）
- 返回 `ApiResult<List<SyncFeedback>>`，逐条反馈成功/失败
- 通过 `@PrePersist` / `@PreUpdate` 自动维护通用字段（`createTime`、`lastTime`、`fBizstate` 等）

### 3.2 财智链 → DAP（Mock 推送接口）

基路径：`/dap/mock` | 受 IP 白名单保护

| 序号 | 方法 | 路径 | 说明 | 触发时机 |
|:---:|:-----|:-----|:-----|:-----|
| 1 | POST | `/dap/mock/receive/visa` | 推送工程签证单 | 签证功能签批完成 |
| 2 | POST | `/dap/mock/receive/boq-adjust` | 推送工程量调整单 | 工程量调整签批完成 |
| 3 | POST | `/dap/mock/receive/progress-payment` | 推送进度款审批单 | 进度款审批签批完成 |
| 4 | POST | `/dap/mock/receive/settlement` | 推送结算审核定案单 | 结算审核定案签批完成 |

> ⚠️ 以上 4 个为 Mock 实现，所有 TODO 标注处为实际对接时需替换的真实业务逻辑。
> 调用客户端见 `DapServiceClient.java`，通过 `${dap.base-url}` 配置 DAP 真实地址。

---

## 4. 数据库表结构

所有表以 `customize_` 为前缀，每张表均包含 7 个通用字段：

| 通用字段 | 类型 | 默认值 | 说明 |
|:---------|:-----|:------|:-----|
| `gid` | BIGSERIAL PK | 自增 | 自增主键 |
| `owner_id` | VARCHAR(36) | `'admin'` | 所有者ID（TODO 内网替换） |
| `f_bizstate` | INT | `0` | 业务状态（0=正常） |
| `create_time` | TIMESTAMP | `now()` | 记录创建时间 |
| `founder` | INT | `0` | 创建人（TODO 内网替换） |
| `last_time` | TIMESTAMP | `now()` | 记录修改时间（自动维护） |
| `ls_modifier` | INT | `0` | 修改人（TODO 内网替换） |

### 4.1 业务表

| 表名 | 说明 | 业务主键 | 关键业务字段 |
|:-----|:-----|:---------|:-----|
| `customize_project_info` | 项目信息表 | `project_id` | base_org_code, unit_code, project_name, project_code |
| `customize_contract_info` | 合同信息表 | `contract_id` | base_org_code, subcontractor_id, contract_amount |
| `customize_contract_project_ref` | 合同-项目关联表 | — | contract_id, project_id（多对多） |
| `customize_boq_main` | 工程量清单主表 | `boq_id` | `biz_create_time` / `biz_last_update_time`（DAP业务时间，非通用字段） |
| `customize_boq_detail` | 工程量清单明细表 | `detail_id` | unit_price_with_tax, original_quantity, current_quantity, node_level |

> **注意**：`customize_boq_main` 中 `biz_create_time` 和 `biz_last_update_time` 是来自 DAP 的**业务创建/修改时间**，与通用字段 `create_time` / `last_time`（记录在本系统的创建/修改时间）含义不同。

---

## 5. 配置说明

### 5.1 数据源

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/caizhilian
    username: postgres
    password: postgres
```

### 5.2 IP 白名单

拦截 `/api/sync/**` 和 `/dap/mock/**`，支持精确 IP（如 `127.0.0.1`）和 CIDR 网段（如 `192.168.1.0/24`）。

```yaml
api:
  whitelist:
    enabled: true                  # 生产环境建议开启
    ips:
      - 127.0.0.1                  # 本机IPv4回环
      - ::1                        # 本机IPv6回环
      - 192.168.1.0/24             # 局域网C段
      - 10.0.0.0/8                 # 企业大内网
      - 172.16.0.0/12              # 企业内网
    protected-paths:
      - /api/sync/**
      - /dap/mock/**
```

### 5.3 DAP 地址

```yaml
dap:
  base-url: http://localhost:8080/dap/mock   # 开发/Mock模式
  # base-url: http://dap-server:8080         # 生产环境改为DAP真实地址
```

### 5.4 JPA 配置

- `ddl-auto: update` — 开发环境自动建表，生产环境建议改为 `validate` 或 `none`
- `show-sql: true` — 开发时打印 SQL，生产建议关闭
- `open-in-view: false` — 关闭延时加载避免性能问题

---

## 6. 快速开始

### 6.1 环境要求

- JDK 17+
- Maven 3.9+
- PostgreSQL 12+

### 6.2 创建数据库

```sql
CREATE DATABASE caizhilian;
```

### 6.3 导入初始化数据

```bash
psql -U postgres -d caizhilian -f src/main/resources/init.sql
```

### 6.4 配置数据库连接

编辑 `src/main/resources/application.yml`，修改数据库用户名密码。

### 6.5 编译启动

```bash
mvn clean package -DskipTests
java -jar target/caizhilian-services-1.0.0.jar
```

或 IDE 中直接运行 `CaiZhiLianApplication.java`。

服务启动后访问 `http://localhost:8080`。

### 6.6 测试同步接口

```bash
# 同步项目信息
curl -X POST http://localhost:8080/api/sync/projectInfoList \
  -H "Content-Type: application/json" \
  -d '[{"projectId": 1000000001, "baseOrgCode": "0442", "baseOrgCodeName": "河北送变电", "unitCode": "01000101", "unitCodeName": "变电一分公司", "projectName": "测试项目", "projectCode": "GCCW-TEST-0001"}]'
```

---

## 7. 待办事项（TODO）

| 优先级 | 事项 | 说明 |
|:-----:|:-----|:-----|
| 高 | 内网认证对接 | `ownerId`/`founder`/`lsModifier` 当前写死为 `admin`/`0`，需从内网 SecurityContext 获取真实用户ID |
| 高 | DAP Mock 替换 | 4 个 `/dap/mock/**` 接口当前只打日志，需接入真实 DAP 业务表写入逻辑 |
| 高 | UDS 附件处理 | 推送接口中的附件下载和关联未实现 |
| 中 | 生产 JPA 配置 | `ddl-auto: update` 改为 `validate`/`none`，`show-sql: false` |
| 中 | 接口鉴权 | 当前仅 IP 白名单，建议增加 Token/OAuth2 验证 |
| 低 | API 文档 | 集成 Swagger/OpenAPI 自动生成接口文档 |

---

## 8. 维护记录

| 日期 | 变更内容 |
|:-----|:-----|
| 2026-07-10 | 初始化项目结构，实现同步接口和 Mock 接口 |
| 2026-07-10 | 添加 Jackson 日期序列化配置，统一 `yyyy-MM-dd HH:mm:ss` 格式 |
| 2026-07-10 | 添加 IP 白名单拦截器，修复 IPv6 匹配问题 |
| 2026-07-10 | 表主键 `id` 改为 `gid` |
| 2026-07-13 | 表名统一加 `customize_` 前缀，新增 7 个通用字段 |
| 2026-07-13 | 通用字段默认值逻辑（`@PrePersist` / `@PreUpdate`），TODO 移至 Service 层 |
| 2026-07-13 | 添加 `.gitignore`，清理 git 中的 `target/` 编译文件 |
| 2026-07-13 | 编写项目 README.md 文档 |
| 2026-07-13 | 补充 12 个测试类（98 个用例），覆盖 Controller/Service/Client/Config/Common；JUnit4 + Mockito + H2 |
| 2026-07-13 | pom.xml 添加 JaCoCo 插件，排除 entity/repository/dto 三个包（无需单元测试） |
