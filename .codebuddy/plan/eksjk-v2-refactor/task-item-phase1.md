# 实施计划 — 阶段一：基础设施搭建

> **对应需求**：需求 1（后端项目基础架构搭建）、需求 2（PC 端前端项目基础架构搭建）、需求 3（统一响应格式与全局异常处理）
> **目标**：搭建前后端项目骨架，建立统一的开发规范和基础设施，为后续所有业务模块开发奠定基础。
> **前置依赖**：无

---

- [ ] 1. 创建后端 Maven 多模块项目骨架
   - 在 `eksjk_v2/eksjk-backend/` 下初始化 Maven 多模块项目，父 POM 基于 Spring Boot 3.x + JDK 21
   - 创建 5 个子模块：`eksjk-common`、`eksjk-model`、`eksjk-mapper`、`eksjk-service`、`eksjk-web`
   - 父 POM 统一管理依赖版本（Spring Boot 3.x、MyBatis-Plus 3.x、Sa-Token、Lombok、MapStruct、EasyExcel 等）
   - 配置模块间依赖关系：web → service → mapper → model → common
   - 确保 `mvn clean package` 能成功编译打包
   - _需求：1.1、1.2_

- [ ] 2. 配置后端基础组件与多环境配置
   - 在 `eksjk-web` 模块中创建 Spring Boot 启动类，配置组件扫描
   - 集成 MyBatis-Plus（通用 CRUD、分页插件、逻辑删除配置）
   - 集成 Sa-Token（JWT 模式基础配置）
   - 集成 SLF4J + Logback 日志配置（按日期滚动、日志级别分离）
   - 创建多环境配置文件：`application.yml`（公共配置）、`application-dev.yml`（开发环境）、`application-prod.yml`（生产环境）
   - 配置 MySQL 数据源、连接池、时区（Asia/Shanghai）
   - 配置 Spring Boot Actuator 健康检查端点 `/actuator/health`
   - 添加 `.gitignore`、README.md 文档
   - 确保项目能正常启动并访问健康检查端点
   - _需求：1.3、1.4、1.5、1.6_

- [ ] 3. 实现后端统一响应格式与全局异常处理
   - 在 `eksjk-common` 模块中定义统一响应类 `R<T>`，包含 `code`、`message`、`data` 三个字段，提供静态工厂方法 `ok()`、`fail()`
   - 定义业务异常类 `BusinessException`（携带错误码和错误消息）
   - 定义错误码枚举 `ErrorCode`（200 成功、400 参数错误、401 未认证、403 无权限、404 资源不存在、500 系统错误等）
   - 在 `eksjk-web` 模块中实现全局异常处理器 `@RestControllerAdvice`：
     - 捕获 `BusinessException` → 返回对应错误码和消息
     - 捕获 `MethodArgumentNotValidException`（参数校验失败）→ 返回 400 + 具体字段错误信息
     - 捕获 `Exception`（未知异常）→ 返回 500，记录详细日志，不暴露堆栈信息
     - 捕获 Sa-Token 认证异常 → 返回 401/403
   - 编写一个测试 Controller 验证统一响应和异常处理是否正常工作
   - _需求：3.1、3.2、3.3、3.4、3.5_

- [ ] 4. 实现后端 Hashids 工具类与基础工具模块
   - 在 `eksjk-common` 模块中实现 Hashids 编解码工具类，保持与 V1 版本相同的编码规则（salt、minLength 等参数一致）
   - 实现通用分页请求/响应封装类（`PageRequest`、`PageResult`），与 MyBatis-Plus 的 `Page` 对象对接
   - 实现常量定义类（角色标识常量：`super_admin`、`hospital_admin`、`doctor`、`parent` 等）
   - 实现日期工具类、字符串工具类等基础工具
   - _需求：1.1、4.1（角色常量预定义）_

- [ ] 5. 创建 PC 端前端 Vue 3 项目骨架
   - 在 `eksjk_v2/eksjk-frontend/` 下使用 Vite + Vue 3 初始化前端项目
   - 创建标准目录结构：`src/api`、`src/assets`、`src/components`、`src/composables`、`src/layouts`、`src/router`、`src/stores`、`src/styles`、`src/utils`、`src/views`
   - 安装并配置核心依赖：Vue 3、Vue Router、Pinia、Axios、Element Plus（按需引入）、ECharts
   - 配置 Vite 开发服务器代理（API 请求代理到后端）
   - 确保 `npm run dev` 能正常启动开发服务器，`npm run build` 能成功构建
   - _需求：2.1、2.2、2.3、2.4_

- [ ] 6. 建立前端全局设计规范与样式体系
   - 在 `src/styles/` 下创建 CSS 变量体系文件（`variables.scss`）：主色调、辅助色、文字色、边框色、间距系统、圆角、阴影等
   - 配置 Element Plus 主题色覆盖（与 CSS 变量体系统一）
   - 创建全局排版规范文件（`typography.scss`）：标题层级（h1-h6）、正文字号、行高
   - 创建通用样式工具类文件（`utilities.scss`）：间距、对齐、文本截断、Flex 布局辅助类
   - 创建全局样式入口文件（`index.scss`），统一引入上述样式文件
   - 在 `main.js` 中引入全局样式
   - _需求：2.6_

- [ ] 7. 封装前端 Axios 请求模块与统一错误处理
   - 在 `src/utils/` 下封装 Axios 实例（`request.js`）：
     - 请求拦截器：自动从 Pinia store 读取 Token 并附加到请求头（Sa-Token 格式）
     - 响应拦截器：统一处理后端返回的 `{ code, message, data }` 格式
     - 401 响应：清除本地 Token，跳转登录页
     - 403 响应：弹出 Element Plus 消息提示「无权限访问」
     - 其他错误：弹出 Element Plus 错误消息提示
   - 在 `src/stores/` 下创建用户状态 store（`user.js`）：管理 Token 存储、持久化到 localStorage、清除逻辑
   - 在 `src/api/` 下创建 API 模块示例文件，演示接口封装规范
   - _需求：2.5、3.6、3.7、3.8_

- [ ] 8. 开发前端通用基础组件
   - 开发 `SearchForm` 组件（`src/components/SearchForm.vue`）：
     - 支持通过 slot 或配置项定义搜索条件
     - 支持条件折叠/展开（「更多筛选」按钮）
     - 支持回车键触发搜索、重置按钮清空条件
     - 提供 `search` 和 `reset` 事件
   - 开发 `DataTable` 组件（`src/components/DataTable.vue`）：
     - 封装 Element Plus `el-table` + `el-pagination`
     - 支持通过 props 配置列定义、分页参数
     - 支持排序、多选、行点击/双击事件
     - 内置 loading 状态和空数据提示
   - 开发 `FormDialog` 组件（`src/components/FormDialog.vue`）：
     - 封装 Element Plus `el-drawer` 或 `el-dialog`
     - 支持新增/编辑两种模式，自动管理表单数据回填和重置
     - 提供 `submit` 和 `cancel` 事件
   - 开发 `PageHeader` 组件（`src/components/PageHeader.vue`）：
     - 页面标题 + 副标题 + 右侧操作按钮区（通过 slot）
   - _需求：2.7_

- [ ] 9. 配置前端路由基础结构与路由守卫
   - 在 `src/router/index.js` 中配置 Vue Router：
     - 定义路由模式（history 模式）
     - 创建基础路由表：登录页路由、404 页路由、主布局路由（占位，阶段二实现具体布局）
   - 实现路由前置守卫（`beforeEach`）：
     - 检查目标路由是否需要认证（通过 `meta.requiresAuth`）
     - 未登录时跳转登录页，记录原始目标路径（登录后回跳）
     - 已登录时访问登录页自动跳转首页
   - 创建登录页占位组件（`src/views/login/LoginView.vue`），仅包含基础结构，具体 UI 在阶段二实现
   - 创建 404 页面组件（`src/views/error/NotFound.vue`）
   - _需求：2.1、3.7_
