# 实施计划 — 阶段六：辅助功能与收尾

> **对应需求**：需求 14（通知反馈 + 统计分析 + 操作日志 + 数据库兼容与迁移 + 部署运维）
> **目标**：实现通知反馈、统计分析、操作日志等辅助功能，完成数据库兼容性设计、数据迁移脚本开发、部署配置和项目收尾。
> **前置依赖**：阶段四、阶段五全部完成

---

- [ ] 1. 实现后端通知公告与意见反馈 API
   - 在 `eksjk-model` 中创建 `Notice`、`PerNoticeLog`、`Opinion` Entity（兼容 V1 表结构）
   - 在 `eksjk-service` 中实现 `NoticeService`：公告 CRUD、用户未读公告查询、关闭状态记录
   - 实现 `OpinionService`：反馈提交（支持图片/视频附件）、反馈列表查询、管理员回复
   - 在 `eksjk-web` 中实现 `NoticeController` 和 `OpinionController`
   - _需求：14.1_

- [ ] 2. 实现 PC 端通知公告与意见反馈页面
   - 在 `src/views/notice/` 下创建公告管理页面：
     - 管理员视图：公告列表 + 新增/编辑/删除公告
     - 用户视图：公告列表（卡片式布局，标题、摘要、发布时间）
   - 实现登录后未读公告弹窗（支持「不再提示」）
   - 在 `src/views/feedback/` 下创建反馈页面：
     - 用户提交反馈表单（文本 + 附件上传）
     - 管理员查看反馈列表 + 回复功能
   - _需求：14.1（前端部分）_

- [ ] 3. 实现后端统计分析 API
   - 在 `eksjk-service` 中实现 `StatisticsService`：
     - 疾病分布统计（各疾病类型病例数量）
     - 月度新增病例趋势
     - 性别分布统计
     - 数据比例分析
   - 在 `eksjk-web` 中实现 `StatisticsController`：返回适合 ECharts 渲染的 JSON 数据格式
   - 数据范围根据用户角色自动过滤
   - _需求：14.2_

- [ ] 4. 实现 PC 端统计分析仪表盘页面
   - 在 `src/views/statistics/` 下创建统计分析页面 `StatisticsView.vue`：
     - 疾病分布饼图/环形图（ECharts）
     - 月度新增病例趋势折线图
     - 性别分布柱状图
     - 图表交互：悬浮详情、点击下钻
     - 筛选条件：时间范围、疾病类型
   - _需求：14.2（前端部分）_

- [ ] 5. 实现后端操作日志与审计功能
   - 在 `eksjk-model` 中创建 `OperLog` Entity（兼容 V1 表结构）
   - 实现 AOP 日志切面 `@OperLog` 自定义注解：
     - 自动拦截标注了 `@OperLog` 的 Controller 方法
     - 记录操作人、操作时间、操作类型、请求参数、响应结果、IP 地址
     - 异步写入数据库，不影响业务性能
   - 在 `eksjk-web` 中实现 `OperLogController`：`GET /api/oper-logs`（分页列表，支持时间范围、操作人、操作类型筛选）
   - 在关键业务 Controller 方法上添加 `@OperLog` 注解
   - _需求：14.3_

- [ ] 6. 实现 PC 端操作日志查看页面
   - 在 `src/views/system/` 下创建操作日志页面 `OperLogList.vue`：
     - 使用 `SearchForm` 组件：时间范围、操作人、操作类型下拉
     - 使用 `DataTable` 组件：操作人、操作类型、操作描述、IP 地址、操作时间
     - 点击行展开详情（请求参数、响应结果）
   - _需求：14.3（前端部分）_

- [ ] 7. 实现数据库表结构兼容性设计与 SQL 脚本
   - 在 `eksjk_v2/sql/` 目录下创建 SQL 脚本：
     - `V2.0.0__init_schema.sql`：V2 新增表的建表语句
     - `V2.0.1__add_v2_fields.sql`：V2 新增字段的 ALTER TABLE 语句（User 表新增 role_code/password_changed_at/wx_openid，Patient 表新增 sync_status/sync_time，ChartUser 表新增 sync_status/reject_reason）
   - 验证所有 MyBatis-Plus Entity 的 `@TableName` 注解正确映射到 V1 现有表名
   - 验证 Hashids 编码规则与 V1 完全一致（相同 salt、minLength、alphabet）
   - 编写 SQL 脚本执行说明文档
   - _需求：14.4.1_

- [ ] 8. 实现数据导入脚本（基础数据 + 用户 + 病例）
   - 在 `eksjk-web` 模块的 `migration` 包下创建数据导入工具：
     - `DataMigrationRunner`（主入口，CommandLineRunner）
     - `MigrationConfig`（配置类：命令行参数解析 --step/--mode/--batch-size/--dry-run/--source-db）
     - `AbstractTableMigrator`（抽象基类：幂等性检查、批量插入、事务管理、日志输出）
   - 实现第 1-3 步导入器：
     - `UnitMigrator`：医疗机构导入
     - `RoleMigrator`：角色权限导入
     - `UserMigrator`：用户导入（V1 level 字段映射为 V2 role_code）
     - `PatientMigrator`：患者主表 + 7 种疾病子表导入
   - 实现三种导入模式：skip（跳过已存在）、update（按修改时间更新）、force（强制覆盖）
   - _需求：14.4.2、14.4.3_

- [ ] 9. 实现数据导入脚本（随访 + 学校 + 小程序 + 辅助数据）与导入报告
   - 实现第 4-7 步导入器：
     - `FollowUpMigrator`：通用随访 + MAS 专用随访导入
     - `StudentMigrator`：学生主表 + 7 张问卷子表导入
     - `ChartUserMigrator`：微信用户导入
     - `AuxDataMigrator`：公告、反馈、操作日志、登录日志导入
   - 实现导入报告生成器 `MigrationReportGenerator`：
     - 输出到控制台 + 写入日志文件
     - 每张表统计：源数据总数、新增数、更新数、跳过数、失败数
     - 失败记录详情（主键 ID、失败原因）
   - 实现数据一致性校验：源表/目标表记录数对比、关键字段抽样校验、外键关联完整性校验
   - _需求：14.4.2、14.4.3、14.4.4_

- [ ] 10. 部署配置与项目收尾
   - 创建 Nginx 配置示例文件（`nginx.conf.example`）：前端静态资源托管 + API 反向代理
   - 完善 `application-prod.yml` 生产环境配置
   - 编写部署说明文档（`docs/deployment.md`）：后端构建部署、前端构建部署、小程序构建上传、Nginx 配置、数据库初始化
   - 编写项目 README.md：项目介绍、技术栈、目录结构、快速开始、开发指南
   - 验证 `/actuator/health` 健康检查端点
   - 全流程端到端验证：登录 → 病例管理 → 随访 → 影像 → 统计 → 小程序联调
   - _需求：14.5、14.6_
