# 实施计划 — 阶段五：扩展业务模块

> **对应需求**：需求 11（学校健康筛查）、需求 12（小程序家长端）、需求 13（小程序医生端）
> **目标**：实现学校健康筛查、微信小程序家长端和微信小程序医生端等扩展业务功能。
> **前置依赖**：阶段二（认证与基础管理）；需求 13（小程序医生端）还依赖阶段三和阶段四

---

- [ ] 1. 实现后端学校健康筛查 API
   - 在 `eksjk-model` 中创建 `Student` Entity + 7 张问卷 Entity（SDQ、CBQ、Mqzyfs、Qzhd、Pmbl、Sthd、CSHQ），兼容 V1 表结构
   - 在 `eksjk-mapper` 中创建对应 Mapper
   - 在 `eksjk-service` 中实现 `StudentService`：学生 CRUD、问卷数据 CRUD、Excel 批量导入（EasyExcel 解析 + 数据校验 + 冲突处理）
   - 在 `eksjk-web` 中实现 `StudentController`：
     - `GET /api/students`（分页列表）
     - `GET /api/students/{id}`（详情含全部问卷）
     - `POST /api/students`（新增）
     - `PUT /api/students/{id}`（编辑）
     - `POST /api/students/import`（Excel 批量导入）
     - `GET /api/students/import-template`（下载导入模板）
     - 问卷数据 CRUD 接口
   - _需求：11.1、11.2、11.3、11.4、11.5_

- [ ] 2. 实现 PC 端学校健康筛查页面
   - 在 `src/views/school/` 下创建学生列表页面 `StudentList.vue`：
     - 搜索区：编号、班级、姓名、性别
     - 数据表格 + 操作列
     - 「批量导入」按钮 → 导入对话框（下载模板 + 上传 Excel + 导入结果展示）
   - 创建学生详情页面 `StudentDetail.vue`：
     - 标签页（Tabs）布局展示 7 张问卷
     - 每张问卷标签显示填写状态（已填/未填）
     - 问卷表单采用分组布局，Likert 量表使用单选按钮组横向排列
     - 支持单张问卷独立保存
   - _需求：11.6、11.7、11.8、11.9_

- [ ] 3. 创建小程序家长端项目骨架
   - 在 `eksjk_v2/eksjk-miniapp/` 下使用 uni-app (Vue 3) 初始化项目
   - 创建标准目录结构：`src/api`、`src/components`、`src/pages`、`src/stores`、`src/utils`、`src/static`
   - 安装并配置核心依赖：Pinia、uCharts
   - 封装 `uni.request` 统一请求模块（请求/响应拦截器、Token 自动附加、错误处理）
   - 配置 `pages.json` 底部 TabBar（首页、评测、记录、我的）
   - 配置 `manifest.json`（微信小程序 AppID 等）
   - 实现品牌启动页
   - _需求：12.1、12.7_

- [ ] 4. 实现后端微信授权登录与家长用户 API
   - 在 `eksjk-service` 中实现 `WxAuthService`：
     - 微信 `code` 换取 `openid`（调用微信 `code2Session` API）
     - 自动创建/关联家长用户记录（角色为 `parent`）
     - Sa-Token 生成 Token 返回
   - 在 `eksjk-web` 中实现 `WxAuthController`：
     - `POST /api/wx/login`（微信授权登录）
     - `POST /api/wx/bindPhone`（绑定手机号）
   - 实现家长个人信息 API：`GET /api/wx/profile`、`PUT /api/wx/profile`
   - _需求：12.2、12.3_

- [ ] 5. 实现小程序家长端核心页面（登录 + 首页 + 宝宝管理）
   - 实现微信授权登录流程：检查 Token → `wx.login` → 后端换 Token → 存储本地缓存
   - 实现首页 `pages/index/index.vue`：宝宝列表卡片、空状态引导页
   - 实现宝宝管理：
     - 添加宝宝表单页（姓名、性别、出生日期、身高、体重、头像上传）
     - 宝宝详情页（基本信息 + 历史评测记录）
     - 编辑/删除操作
   - _需求：12.2、12.4_

- [ ] 6. 实现后端身高评测与生长记录 API + 医生绑定 API
   - 在 `eksjk-service` 中实现 `GrowthService`：
     - SDS/百分位计算（根据身高、年龄、性别）
     - 评测记录 CRUD
     - 生长曲线数据查询（历次评测 + WHO 标准参考线）
   - 实现 `DoctorBindService`：家长绑定/解绑医生、数据同步状态机管理
   - 在 `eksjk-web` 中实现对应 Controller
   - _需求：12.5、12.6_

- [ ] 7. 实现小程序家长端评测、记录、个人中心页面
   - 实现评测页 `pages/assessment/index.vue`：评测表单（日期、身高、体重）→ 结果展示（SDS、百分位、结论、与上次对比）
   - 实现记录页 `pages/record/index.vue`：历史评测列表 + uCharts 生长曲线图（叠加 WHO P3/P50/P97 参考线）
   - 实现个人中心页 `pages/mine/index.vue`：个人信息、医生绑定（扫码/邀请码）、已绑定医生信息、同步状态展示
   - 实现版本更新检测（`wx.getUpdateManager`）
   - _需求：12.5、12.6、12.7_

- [ ] 8. 创建小程序医生端项目骨架
   - 在 `eksjk_v2/eksjk-miniapp-doctor/` 下使用 uni-app (Vue 3) 初始化独立项目
   - 创建标准目录结构，安装核心依赖
   - 封装 `uni.request` 统一请求模块
   - 配置 `pages.json` 底部 TabBar（工作台、患者、统计、我的）
   - 实现医生端品牌启动页（Logo + "医生版"标识）
   - _需求：13.1、13.9_

- [ ] 9. 实现后端医生端登录与工作台 API
   - 在 `eksjk-service` 中扩展 `WxAuthService`：
     - 医生微信登录（OpenID 关联已有医生账号）
     - 首次微信登录需账号密码绑定
     - 账号密码登录（复用 PC 端认证逻辑）
   - 实现医生端工作台 API：`GET /api/doctor/dashboard`（患者总数、本月新增、待随访、待同步数据、最近病例）
   - 实现家长数据审核 API：`GET /api/doctor/pending-reviews`、`PUT /api/doctor/reviews/{id}/approve`、`PUT /api/doctor/reviews/{id}/reject`
   - _需求：13.2、13.3、13.7_

- [ ] 10. 实现小程序医生端核心页面（登录 + 工作台 + 患者列表）
   - 实现登录页：微信快捷登录 + 账号密码登录 + 首次绑定流程
   - 实现工作台首页 `pages/index/index.vue`：数据概览卡片、快捷操作、待办提醒列表、下拉刷新
   - 实现患者列表页 `pages/patient/list.vue`：顶部搜索栏、疾病类型筛选标签栏、卡片式患者列表、上拉加载更多
   - 实现患者详情页 `pages/patient/detail.vue`：标签页布局（基本信息、随访记录时间线、影像资料缩略图）
   - _需求：13.2、13.3、13.4、13.5_

- [ ] 11. 实现小程序医生端随访、审核、统计页面
   - 实现快捷随访表单 `pages/followup/add.vue`：简化版（日期、身高、体重、BMI 自动算、骨龄、备注）+ 「完整录入」提示前往 PC 端
   - 实现待审核页面 `pages/review/list.vue`：家长提交数据列表、详情查看、通过/拒绝操作（拒绝需填写原因）
   - 实现统计页面 `pages/stats/index.vue`：uCharts 疾病分布饼图、患者增长趋势折线图、性别分布柱状图
   - 实现「我的」页面 `pages/mine/index.vue`：医生信息卡片、我的二维码（供家长扫码绑定）、设置、版本更新检测
   - _需求：13.6、13.7、13.8、13.9_
