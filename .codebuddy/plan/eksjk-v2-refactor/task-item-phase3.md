# 实施计划 — 阶段三：核心业务 — 病例管理

> **对应需求**：需求 8（患者病例管理）
> **目标**：实现系统最核心的患者病例管理功能，包括 7 种疾病类型的数据模型和 CRUD 操作，重点优化数据录入和检索查询效率。
> **前置依赖**：阶段二全部完成

---

- [ ] 1. 实现后端患者主表 Entity 与 Mapper
   - 在 `eksjk-model` 中创建 `Patient` Entity（兼容 V1 表结构），包含 30+ 字段：基本信息、编号体系、疾病分类、生长数据、家庭信息、出生信息、就诊信息、小程序关联、管理字段
   - V2 新增字段设计为可空：`sync_status`、`sync_time`
   - 在 `eksjk-mapper` 中创建 `PatientMapper`（继承 `BaseMapper<Patient>`）
   - 编写 XML 映射文件，实现多条件分页查询 SQL（支持病例编号、姓名、性别、病历号、时间范围筛选）
   - 实现病例编号自动生成逻辑（格式：疾病类型前缀 + 年月 + 序号，如 `DSD202604001`）
   - _需求：8.2_

- [ ] 2. 实现后端 7 种疾病子表 Entity 与 Mapper
   - 在 `eksjk-model` 中创建 7 个疾病 Entity（兼容 V1 表结构）：
     - `DsdCase`（性发育异常）、`FssCase`（遗传性骨病）、`CppCase`（中枢性性早熟）
     - `MasCase`（McCune-Albright）、`SgaCase`（小于胎龄儿）
     - `SssCase`（家族性矮小）、`EltmCase`（E路童萌）
   - 在 `eksjk-mapper` 中创建 7 个 Mapper 接口
   - 创建对应的 DTO 和 VO 类，使用 MapStruct 实现转换
   - _需求：8.4_

- [ ] 3. 实现后端病例管理 Service 层
   - 在 `eksjk-service` 中实现 `PatientService`：
     - 分页查询（集成数据范围过滤：超级管理员全局/医院管理员本院/普通医生本人）
     - 新建病例（创建患者主表 + 对应疾病子表，自动生成病例编号）
     - 编辑病例（更新患者主表 + 疾病子表，记录修改日志）
     - 删除病例（逻辑删除）
     - 详情查询（患者主表 + 疾病子表 + 随访列表 + 影像列表）
   - 实现疾病子表 Service 的策略模式：根据 `dis_class` 动态路由到对应的疾病 Service
   - _需求：8.1、8.3_

- [ ] 4. 实现后端病例管理 Controller 层与数据导出
   - 在 `eksjk-web` 中实现 `PatientController`：
     - `GET /api/patients`（分页列表）
     - `GET /api/patients/{id}`（详情）
     - `POST /api/patients`（新建）
     - `PUT /api/patients/{id}`（编辑）
     - `DELETE /api/patients/{id}`（逻辑删除）
     - `GET /api/patients/export`（EasyExcel 导出）
     - `POST /api/patients/batch-download`（批量下载 ZIP）
   - 实现 SDS 计算接口：`POST /api/patients/calculate-sds`
   - _需求：8.1、8.5_

- [ ] 5. 实现 PC 端病例列表页面
   - 在 `src/views/case/` 下创建病例列表页面 `CaseList.vue`：
     - 使用 `PageHeader` 组件：疾病类型名称 + 右侧操作按钮组（新建病例、导出 Excel、批量下载）
     - 使用 `SearchForm` 组件：病例编号、姓名、性别下拉、病历号、上传时间范围
     - 使用 `DataTable` 组件：列配置根据疾病类型动态调整
     - 回车搜索、重置、行高亮、双击进入详情
     - 操作列：查看、编辑、删除（确认弹窗）
   - 实现路由参数传递疾病类型，7 个疾病菜单共用同一个列表组件
   - _需求：8.1（前端部分）_

- [ ] 6. 实现 PC 端病例详情页面
   - 在 `src/views/case/` 下创建病例详情页面 `CaseDetail.vue`：
     - 顶部信息栏：患者姓名、性别、年龄、病例编号、操作按钮（编辑、返回列表）
     - 标签页布局：基本信息 Tab、疾病专项 Tab、随访记录 Tab（占位，阶段四实现）、影像资料 Tab（占位，阶段四实现）
     - 基本信息 Tab：分区展示患者主表数据
     - 疾病专项 Tab：根据疾病类型动态加载对应的详情组件
   - _需求：8.1、8.2_

- [ ] 7. 实现 PC 端 DSD 和 FSS 疾病表单组件
   - 创建 `src/views/case/forms/dsd/` 目录，拆分为子组件：
     - `DsdBasicInfo.vue`（基本信息区）
     - `DsdPhysicalExam.vue`（体格检查区）
     - `DsdLabTest.vue`（实验室检查区）
     - `DsdForm.vue`（主表单组件，组合子组件）
   - 创建 `src/views/case/forms/fss/` 目录，同样拆分为子组件
   - 实现分区表单布局（`el-card` / `el-collapse`）、智能默认值、输入联动（出生日期→年龄、身高体重→BMI）、实时校验
   - _需求：8.3、8.4_

- [ ] 8. 实现 PC 端 CPP、MAS、SGA 疾病表单组件
   - 创建 `src/views/case/forms/cpp/`、`mas/`、`sga/` 目录
   - 每个疾病表单拆分为子组件（BasicInfo、PhysicalExam、LabTest 等）
   - 复用通用的表单区域组件（如基本信息区在多个疾病间共享）
   - 单个组件文件不超过 500 行
   - _需求：8.3、8.4_

- [ ] 9. 实现 PC 端 SSS、ELTM 疾病表单组件
   - 创建 `src/views/case/forms/sss/`、`eltm/` 目录
   - 每个疾病表单拆分为子组件
   - 实现表单未保存离开提示（`beforeRouteLeave` 守卫）
   - 实现表单提交校验：未通过时自动滚动到第一个错误字段
   - _需求：8.3、8.4_

- [ ] 10. 实现 PC 端数据导出与批量操作功能
   - 实现 Excel 导出功能：点击「导出 Excel」→ 显示导出进度 → 自动触发下载
   - 实现批量下载功能：勾选多条病例 → 点击「批量下载」→ 显示下载进度 → ZIP 下载
   - 未勾选时按钮置灰并提示「请先选择病例」
   - 封装文件下载工具函数（处理 Blob 响应、文件名解析）
   - _需求：8.5_
