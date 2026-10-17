# EKSJK 项目 AI 编码约束文档

## 项目概述

EKSJK（儿科数据管理系统）是一个面向医疗健康领域的全栈 Web 应用，专注于儿童内分泌疾病的临床数据管理、患者随访跟踪、生长发育监测和医学影像处理。系统同时提供微信小程序端供家长自助录入儿童生长数据。

当前项目包含两个版本：
- **eksjk_v1**：旧版本（Python Django + Vue 2），仅作为业务逻辑参考，不再进行新功能开发
- **eksjk_v2**：新版本（Java Spring Boot 3 + Vue 3），计划存放重构后的新代码

> ⚠️ **重要约束：** `eksjk_v1` 目录下的所有代码保持不变，不做任何修改。V1 仅作为业务逻辑和数据模型的参考依据，所有新代码均在 `eksjk_v2` 目录下独立开发。

---

## 快速上手：当前部署与运行环境

### Staging 环境

| 项目 | 值 |
|------|-----|
| 前端地址 | http://staging.eksjk.zsmm.org.cn |
| 后端 API | http://staging.eksjk.zsmm.org.cn/api/* |
| K8s 集群 | 阿里云 ACK（上海） |
| 控制面 | https://47.116.218.169:6443 |
| 命名空间 | `eksjk-staging` |
| 数据库 | RDS MySQL 8.0.36 Serverless（VPC 内网） |
| 对象存储 | OSS（S3 兼容，eksjk-staging bucket） |
| 镜像仓库 | ACR Personal（上海）: `crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/` |
| 当前镜像 | backend: `develop-806e7a2` / frontend: `develop-806e7a2` |
| 登录账号 | `super_admin` / `Test@1234`（还有 doctor_1～3, hospital_admin_1～2, parent_1，密码均为 `Test@1234`） |

### 常用命令速查

```bash
# K8s 连接（必须设置此环境变量）
export KUBECONFIG=C:/Users/Administrator/.kube/eksjk-config

# 查看 Pod 状态
kubectl get pods -n eksjk-staging

# 查看部署
kubectl get deployment -n eksjk-staging

# 查看后端日志
kubectl logs -n eksjk-staging deployment/eksjk-backend --tail=100 -f

# 重启部署
kubectl rollout restart deployment/eksjk-backend deployment/eksjk-frontend -n eksjk-staging

# 进入后端 Pod 执行命令
kubectl exec -it -n eksjk-staging deployment/eksjk-backend -- /bin/bash
```

### 本地开发

```bash
# 后端
cd eksjk_v2/eksjk-backend && mvn spring-boot:run -pl eksjk-web -Dspring-boot.run.profiles=dev

# 前端
cd eksjk_v2/eksjk-frontend && npm run dev
```

### 构建与部署到 Staging

```bash
# 1. 构建后端 JAR
cd eksjk_v2/eksjk-backend && mvn clean package -pl eksjk-web -am -DskipTests -q

# 2. 构建前端
cd eksjk_v2/eksjk-frontend && npm run build

# 3. 构建并推送 Docker 镜像（使用 --no-cache 确保干净构建）
cd eksjk_v2/eksjk-backend && docker build --no-cache -t crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/eksjk-backend:develop-806e7a2 .
cd eksjk_v2/eksjk-frontend && docker build --no-cache -t crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/eksjk-frontend:develop-806e7a2 .

# 4. 登录 ACR 并推送
docker login --username junedo@qq.com crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com
docker push crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/eksjk-backend:develop-806e7a2
docker push crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/eksjk-frontend:develop-806e7a2

# 5. 重启 K8s（KUBECONFIG 必须已设置）
export KUBECONFIG=C:/Users/Administrator/.kube/eksjk-config
kubectl rollout restart deployment/eksjk-backend deployment/eksjk-frontend -n eksjk-staging
```

> **注意：** 如果代码有未提交的改动，构建时仍用当前 HEAD 的 SHA 作为 tag（如 `develop-806e7a2`），K8s 重启后会拉取最新推送的同名 tag 镜像。

### ELTM 模块特性

- **ELTM 列表**（`/case/eltm`）展示**全部**病例（不限 disClass），是各病种病例的统一视图
- **诊断状态列**：非 ELTM 病例（disClass != 10000007）显示绿色「已诊断」；ELTM 未分类显示「未分类」
- ELTM 列表中点击病例会自动跳转到对应病种的详情路由
- 新建按钮为下拉菜单，可选择任意病种创建
- 其他病种（DSD/FSS/CPP 等）列表页正常运作，各自只展示本类病例

### 数据库迁移

数据库增量 SQL 在 `eksjk_v2/init-sql/` 目录下。Staging RDS 已执行完 01-15 号迁移脚本（含 gen_data 列手动补充）。如需新增迁移，需通过 K8s Pod 连接 RDS 执行。

---

## V2 技术栈（严格遵守，不得擅自升级或替换）

> **说明：** 所有技术选型均为开源组件，无任何内部/私有依赖，可在任意环境直接使用。

### 后端

| 技术     | 选型                              | 说明                                        |
| -------- | --------------------------------- | ------------------------------------------- |
| 语言     | Java 21 (JDK 21)                  | LTS 版本，支持虚拟线程等新特性              |
| 框架     | Spring Boot 3.x                   | 主体应用框架，提供 RESTful API              |
| 数据库   | MySQL 8.0+                        | 关系型数据存储，开源免费                    |
| ORM      | MyBatis-Plus 3.x                  | MyBatis 增强工具，内置通用 CRUD、分页插件，复杂查询仍可手写 SQL |
| 认证授权 | Sa-Token                          | 轻量级开源 Java 权限认证框架，支持 JWT 模式 |
| 调度     | Spring Scheduler                  | Spring 内置定时任务，无需额外中间件         |
| 日志     | SLF4J + Logback                   | Spring Boot 默认日志框架，零配置开箱即用    |
| 配置管理 | Spring Boot 原生 `application.yml` | 多环境配置（dev/prod）通过 Profile 切换     |
| 数据导出 | EasyExcel（阿里巴巴）              | 基于 POI 封装的流式 Excel 读写库，内存占用低、API 简洁 |
| 工具库   | Lombok                            | 编译期注解处理器，消除 getter/setter/constructor 样板代码 |
| 对象映射 | MapStruct                         | 编译期对象映射框架，Entity/DTO/VO 之间零运行时开销转换 |

### 前端（PC 端）

| 技术      | 选型         | 说明                                   |
| --------- | ------------ | -------------------------------------- |
| 框架      | Vue 3        | 前端主体框架，Composition API          |
| 构建工具  | Vite         | 快速构建与热更新                       |
| UI 组件   | Element Plus | 基础 UI 组件库，PC 端表单/表格体验优秀 |
| 图表      | ECharts      | 生长曲线、统计报表可视化               |
| 路由      | Vue Router   | 前端路由管理，区分用户端/医生端入口    |
| 状态管理  | Pinia        | Vue 3 官方推荐状态管理库               |
| HTTP 请求 | Axios        | 开源 HTTP 客户端，统一封装请求拦截     |

### 前端（微信小程序端）

| 技术      | 选型              | 说明                                           |
| --------- | ----------------- | ---------------------------------------------- |
| 框架      | uni-app (Vue 3)   | 跨端开发框架，基于 Vue 3 语法，一套代码编译为微信小程序 |
| UI 组件   | uni-ui            | uni-app 官方组件库，原生性能，微信小程序适配良好 |
| 状态管理  | Pinia             | 与 PC 端保持一致的状态管理方案                  |
| HTTP 请求 | uni.request 封装  | 基于 uni-app 原生请求 API 统一封装              |
| 图表      | uCharts           | 跨端图表库，支持小程序原生 Canvas 渲染          |

### 部署架构

- 前后端分离：PC 端前端构建为静态资源，后端提供 RESTful API
- PC 端前端通过 Nginx 托管，API 请求反向代理至 Spring Boot 服务
- 微信小程序（家长端和医生端）通过微信开发者工具构建并上传至微信公众平台，审核后发布
- 配置通过本地 `application-prod.yml` 管理，无需外部配置中心
- PC 端用户通过浏览器直接访问，家长端用户通过微信扫码/搜索进入小程序，医生端用户通过独立的医生版小程序入口访问

---

## V2 项目结构（规划）

```
eksjk_v2/
├── eksjk-backend/                # 后端 Maven 多模块项目
│   ├── eksjk-common/             # 公共工具类、常量定义、通用异常
│   ├── eksjk-model/              # 数据模型（Entity、DTO、VO）
│   ├── eksjk-mapper/             # MyBatis-Plus Mapper 接口和 XML 映射文件
│   ├── eksjk-service/            # 业务逻辑层
│   ├── eksjk-web/                # Controller 层 + Spring Boot 启动类
│   └── pom.xml                   # 父 POM
│
├── eksjk-frontend/               # PC 端 Vue 3 项目
│   ├── src/
│   │   ├── api/                  # API 接口封装
│   │   ├── assets/               # 静态资源（图片、样式）
│   │   ├── components/           # 公共组件
│   │   ├── composables/          # 组合式函数（Composition API hooks）
│   │   ├── layouts/              # 页面布局组件
│   │   ├── router/               # Vue Router 路由配置
│   │   ├── stores/               # Pinia 状态管理
│   │   ├── styles/               # 全局样式、CSS 变量、主题配置
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面视图组件
│   ├── vite.config.js
│   └── package.json
│
├── eksjk-miniapp/                # 微信小程序家长端 uni-app 项目
│   ├── src/
│   │   ├── api/                  # API 接口封装（基于 uni.request）
│   │   ├── components/           # 公共组件
│   │   ├── pages/                # 页面文件
│   │   ├── stores/               # Pinia 状态管理
│   │   ├── static/               # 静态资源
│   │   └── utils/                # 工具函数
│   ├── pages.json                # 页面路由配置
│   ├── manifest.json             # 应用配置
│   └── package.json
│
├── eksjk-miniapp-doctor/         # 微信小程序医生端 uni-app 项目（独立小程序）
│   ├── src/
│   │   ├── api/                  # API 接口封装（基于 uni.request）
│   │   ├── components/           # 公共组件
│   │   ├── pages/                # 页面文件
│   │   ├── stores/               # Pinia 状态管理
│   │   ├── static/               # 静态资源
│   │   └── utils/                # 工具函数
│   ├── pages.json                # 页面路由配置
│   ├── manifest.json             # 应用配置
│   └── package.json
│
└── docs/                         # 项目文档
```

---

## V2 代码规范

### 后端规范

1. **分层架构**: 严格遵循 Controller → Service → Mapper 三层架构
   - Controller：接收请求、参数校验、调用 Service、返回响应
   - Service：业务逻辑处理，事务管理
   - Mapper：数据库访问，继承 MyBatis-Plus 的 `BaseMapper`
2. **命名规范**:
   - 包名：全小写，如 `com.eksjk.model.entity`
   - 类名：PascalCase，如 `PatientService`、`PatientController`
   - 方法名/变量名：camelCase
   - 常量：UPPER_SNAKE_CASE
   - 数据库表名/字段名：snake_case
3. **数据模型分层**:
   - Entity：数据库表映射对象，使用 Lombok `@Data` 注解
   - DTO：数据传输对象，用于接收前端请求参数
   - VO：视图对象，用于返回给前端的响应数据
   - 使用 MapStruct 进行 Entity ↔ DTO ↔ VO 之间的转换
4. **MyBatis-Plus 使用规范**:
   - 单表 CRUD 操作使用 `BaseMapper` 内置方法，不写 SQL
   - 复杂查询（多表关联、统计等）使用 XML 手写 SQL
   - 分页查询使用 MyBatis-Plus 内置 `Page` 对象
   - 逻辑删除使用 `@TableLogic` 注解
5. **API 风格**: RESTful 风格，统一返回 `{ code, message, data }` 格式
6. **异常处理**: 使用全局异常处理器 `@RestControllerAdvice`，不在业务代码中直接返回错误
7. **日志**: 使用 SLF4J 的 `@Slf4j`（Lombok 注解）记录日志，**禁止**使用 `System.out.println`

### 前端规范（PC 端）

1. **组件风格**: 使用 Vue 3 Composition API（`<script setup>` 语法糖）
2. **命名规范**:
   - 组件文件名使用 PascalCase（如 `PatientList.vue`、`LoginForm.vue`）
   - 变量和方法使用 camelCase
   - 常量使用 UPPER_SNAKE_CASE
3. **样式**: 组件内样式使用 `<style scoped>`
4. **HTTP 请求**: 统一使用 `src/api/` 目录下封装的接口方法，**不要**在组件中直接调用 Axios
5. **路由**: 新增页面必须在 `src/router/` 中注册路由
6. **状态管理**: 全局状态通过 Pinia（`src/stores/`）管理，组件间通信优先使用 props/emit
7. **Element Plus**: 按需引入 Element Plus 组件，保持页面风格一致

### 前端规范（微信小程序端）

1. **框架**: 使用 uni-app (Vue 3) 开发，`<script setup>` 语法糖
2. **命名规范**: 与 PC 端保持一致（PascalCase 组件名、camelCase 变量名）
3. **HTTP 请求**: 统一使用 `src/api/` 目录下基于 `uni.request` 封装的接口方法
4. **页面路由**: 在 `pages.json` 中注册页面路由
5. **UI 组件**: 使用 uni-ui 组件库，保持小程序原生体验
6. **状态管理**: 使用 Pinia 管理全局状态，与 PC 端保持一致
7. **条件编译**: 如有平台差异，使用 uni-app 条件编译（`#ifdef MP-WEIXIN`）处理

### 通用规范

1. **注释语言**: 代码注释使用中文
2. **编码**: 所有文件使用 UTF-8 编码
3. **缩进**: 前端使用 2 空格缩进，后端使用 4 空格缩进
4. **Git 提交**: 提交信息使用中文，格式为 `[模块] 操作描述`，如 `[datamain] 新增随访记录导出功能`

---

## 架构规则

### 疾病类型体系

系统支持 7 大疾病类型，每种疾病对应独立的前端组件和后端数据模型：

| 编码 | 疾病类型 | 前端组件 | 后端 Entity |
|------|----------|----------|-------------|
| 10000001 | 性发育异常 (DSD) | `DsdForm.vue` | `DsdCase` |
| 10000002 | 遗传性骨病 (FSS) | `FssForm.vue` | `FssCase` |
| 10000003 | 中枢性性早熟 (CPP) | `CppForm.vue` | `CppCase` |
| 10000004 | McCune-Albright (MAS) | `MasForm.vue` | `MasCase` |
| 10000005 | 小于胎龄儿 (SGA) | `SgaForm.vue` | `SgaCase` |
| 10000006 | 家族性矮小 (SSS) | `SssForm.vue` | `SssCase` |
| 10000007 | E路童萌 (ELTM) | `EltmForm.vue` | `EltmCase` |

**规则**:
- 新增疾病类型时，必须同时创建前端组件和后端 Entity/Mapper/Service
- 疾病编码使用 8 位数字，前缀 `1000` 开头

### 权限模型

- 系统使用 Sa-Token 实现 RBAC 权限模型
- 角色分为四级：超级管理员（`super_admin`）、医院管理员（`hospital_admin`）、普通医生（`doctor`）、家长（`parent`）
- 超级管理员可管理所有医院、所有用户、所有数据
- 医院管理员可管理本院用户（仅普通医生）、查看本院所有病例数据
- 普通医生仅可管理自己创建的病例和随访数据
- 家长仅可通过小程序端访问家长相关功能
- 数据查询 API 根据角色自动过滤数据范围（全局 / 按 hospital_id / 按 creator_id）
- 使用 Sa-Token 的注解式鉴权（`@SaCheckRole`、`@SaCheckPermission`）
- 对外暴露用户 ID 时使用 Hashids 编码，不直接暴露数据库自增 ID

### 数据模型

- 患者主表 `Patient` 是核心实体，通过 `disClass` 字段关联具体疾病子表
- 随访记录 `PatientFollowUp` 与患者是一对多关系
- MAS 疾病有独立的随访模型 `MasFollowUp`
- 数据库 ID 对外暴露时必须使用 Hashids 编码
- 逻辑删除使用 MyBatis-Plus 的 `@TableLogic` 注解

---

## 安全规范

1. **密码存储**: 使用 BCrypt 加密存储密码，**禁止**明文存储
2. **ID 混淆**: 所有对外暴露的数据库主键 ID 必须使用 Hashids 编码，**禁止**直接暴露自增 ID
3. **Token 认证**: 使用 Sa-Token JWT 模式进行身份认证，前端在请求头中携带 Token
4. **敏感数据**: 患者姓名、身份证号、联系电话等属于敏感信息，日志中**禁止**打印完整敏感数据
5. **参数校验**: 使用 Spring Validation 注解进行参数校验，**禁止**在 Controller 中手动校验

---

## 部署注意事项

1. **后端构建**: `mvn clean package` 生成可执行 JAR 包
2. **前端构建**: `npm run build` 生成静态资源到 `dist/` 目录
3. **Nginx 部署**: 前端静态资源由 Nginx 托管，API 请求反向代理至 Spring Boot 服务
4. **环境配置**: 生产环境使用 `application-prod.yml`，开发环境使用 `application-dev.yml`
5. **时区**: 系统统一使用 `Asia/Shanghai` 时区

---

## V1 旧项目技术栈（仅供参考）

> 以下为 eksjk_v1 旧版本的技术栈，仅作为业务逻辑参考，V2 开发中**不要**使用这些技术。

### 前端（V1）
- Vue.js 2.6.11 + Element UI 2.13.0 + Vuex 3.1.2 + Vue Router 3.1.5
- Cornerstone.js（DICOM 医学影像渲染）
- Less 3.11.1

### 后端（V1）
- Django 3.0.3（Python 3.7+）+ MySQL 5.7+ + Gunicorn
- pycryptodome（RSA/AES 加密）+ pydicom（DICOM 解析）+ xlwt（Excel 导出）+ hashids

### 部署（V1）
- Docker + Kubernetes + Nginx

---

## 常见开发场景指引（V2）

### 新增一个疾病类型

1. 在 `eksjk-model` 模块中新增疾病 Entity 类（使用 Lombok `@Data`）
2. 在 `eksjk-mapper` 模块中新增 Mapper 接口（继承 `BaseMapper`）
3. 在 `eksjk-service` 模块中新增 Service 接口和实现类
4. 在 `eksjk-web` 模块中新增 Controller
5. 在前端 `src/views/` 下新增对应的表单组件
6. 执行数据库增量 SQL 脚本

### 新增一个 API 接口

1. 在对应模块的 Controller 中编写接口方法
2. 在 Service 层实现业务逻辑
3. 使用 Sa-Token 注解控制权限（`@SaCheckLogin`、`@SaCheckRole` 等）
4. 前端在 `src/api/` 目录下封装接口调用方法

### 新增 PC 端前端页面

1. 在 `src/views/` 下创建新的 Vue 组件（使用 `<script setup>` 语法）
2. 在 `src/router/` 中注册路由
3. 使用 Element Plus 组件构建界面，保持与现有页面风格一致

### 新增小程序页面

1. 在 `eksjk-miniapp/src/pages/`（家长端）或 `eksjk-miniapp-doctor/src/pages/`（医生端）下创建新的页面组件（使用 `<script setup>` 语法）
2. 在对应项目的 `pages.json` 中注册页面路由
3. 使用 uni-ui 组件构建界面，保持小程序原生风格
4. 如需调用后端 API，在 `src/api/` 目录下封装接口方法
