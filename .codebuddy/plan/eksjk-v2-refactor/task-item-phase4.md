# 实施计划 — 阶段四：核心业务 — 随访与影像

> **对应需求**：需求 9（随访管理）、需求 10（医学影像处理）
> **目标**：实现患者随访跟踪和医学影像管理功能，完善核心临床数据管理闭环。
> **前置依赖**：阶段三全部完成

---

- [ ] 1. 实现后端随访数据模型与 Mapper
   - 在 `eksjk-model` 中创建 `PatientFollowUp` Entity（通用随访，兼容 V1 表结构），包含 50+ 字段：基础测量、骨龄评估、发育分期、生长因子、甲状腺功能、血糖胰岛素、性激素、肝肾功能、影像检查、诊疗方案、管理字段
   - 创建 `MasFollowUp` Entity（MAS 专用随访），在通用字段基础上额外包含：皮肤检查、骨骼检查、多器官 B 超、影像学、骨代谢检查、内分泌激素全套
   - 在 `eksjk-mapper` 中创建 `PatientFollowUpMapper`、`MasFollowUpMapper`
   - 创建对应的 DTO 和 VO 类
   - _需求：9.1_

- [ ] 2. 实现后端随访管理 Service 与 Controller
   - 在 `eksjk-service` 中实现 `FollowUpService`：
     - 查询某患者的随访列表（按时间倒序）
     - 新增随访（根据疾病类型选择通用/MAS 专用模型）
     - 编辑随访、逻辑删除随访
   - 在 `eksjk-web` 中实现 `FollowUpController`：
     - `GET /api/patients/{patientId}/follow-ups`（随访列表）
     - `GET /api/follow-ups/{id}`（随访详情）
     - `POST /api/patients/{patientId}/follow-ups`（新增）
     - `PUT /api/follow-ups/{id}`（编辑）
     - `DELETE /api/follow-ups/{id}`（删除）
   - _需求：9.2_

- [ ] 3. 实现后端文件上传与影像管理 API
   - 在 `eksjk-service` 中实现 `FileService`：
     - 文件上传（校验格式和大小，按日期+患者 ID 组织目录结构）
     - 文件下载/流式访问
     - DICOM 文件元数据解析
   - 在 `eksjk-web` 中实现 `FileController`：
     - `POST /api/files/upload`（文件上传）
     - `GET /api/files/{id}`（文件下载/预览）
     - `GET /api/patients/{patientId}/files`（患者影像列表）
     - `DELETE /api/files/{id}`（删除文件）
   - 配置文件上传大小限制、允许的文件格式
   - _需求：10.1、10.2、10.3、10.4、10.5_

- [ ] 4. 实现 PC 端随访记录列表与时间线展示
   - 在病例详情页的「随访记录」Tab 中实现：
     - 时间线列表展示所有随访记录（随访日期、关键指标摘要：身高/体重/BMI）
     - 每条记录的操作按钮：查看详情、编辑、删除
     - 新增随访按钮
   - 创建随访详情展示组件 `FollowUpDetail.vue`：分区展示随访完整数据
   - _需求：9.2（前端部分）_

- [ ] 5. 实现 PC 端随访表单（通用 + MAS 专用）
   - 创建通用随访表单组件 `FollowUpForm.vue`：
     - 分区表单布局（基础测量、骨龄评估、实验室检查、诊疗方案等）
     - 自动带入上次随访数据作为参考（灰色显示）
     - 输入联动（身高体重→BMI 自动计算）
   - 创建 MAS 专用随访表单组件 `MasFollowUpForm.vue`：
     - 继承通用表单区域 + 额外的 MAS 专项检查区域（皮肤/骨骼/多器官 B 超/骨代谢等）
   - 根据疾病类型动态加载对应的随访表单
   - _需求：9.2（前端部分）_

- [ ] 6. 实现 PC 端影像文件上传与管理
   - 在病例详情页的「影像资料」Tab 中实现：
     - 影像缩略图网格展示
     - 上传区域：支持拖拽上传和点击上传，上传进度条
     - 上传前校验文件格式和大小
     - 文件操作：预览、下载、删除
   - 封装文件上传组件 `FileUploader.vue`：支持多文件上传、进度展示、格式校验
   - _需求：10.6、10.9_

- [ ] 7. 实现 PC 端 DICOM 影像专业查看器
   - 集成 Cornerstone.js 3.x（cornerstone3D）或同等开源 DICOM 渲染库
   - 创建 DICOM 查看器组件 `DicomViewer.vue`：
     - 基础操作：缩放、旋转、平移、翻转
     - 窗宽窗位调节：预设窗口（骨窗、软组织窗等）+ 手动拖拽调节
     - 测量标注：长度测量、角度测量、ROI 标注
     - 影像信息面板：显示 DICOM 元数据（患者信息、检查信息、设备信息）
   - 非 DICOM 格式图片使用 Element Plus 大图预览（缩放、旋转）
   - _需求：10.7、10.8_

- [ ] 8. 前后端联调验证：随访与影像完整流程
   - 验证随访 CRUD 完整流程：新增 → 列表展示 → 编辑 → 删除
   - 验证 MAS 专用随访表单的加载和保存
   - 验证文件上传、预览、下载、删除完整流程
   - 验证 DICOM 影像查看器的各项功能
   - 验证上次随访数据自动带入功能
   - _需求：9、10_
