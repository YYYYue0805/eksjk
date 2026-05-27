# 实施计划：Mock 数据脚本（Plan 3）

> 本任务清单基于 `mock-data-validation/requirements.md` 需求文档生成。
> 前置依赖：Plan 1 完成（K8S 环境就绪，MySQL 已在 K8S 中运行）。

---

- [ ] 1. 创建 Mock 数据目录结构和工程化脚手架
   - 创建 `eksjk_v2/scripts/mock/` 目录及 `sql/` 子目录
   - 创建 `README.md` 模板：包含测试账号列表、密码、角色说明、执行前置条件、各 SQL 文件说明
   - 创建 `init-mock-data.sh`：按 `01 → 05` 顺序执行 SQL 文件，支持配置 MySQL 连接信息
   - 创建 `clean-mock-data.sh`：按外键依赖逆序删除 Mock 数据，恢复空库状态
   - _需求：6.1 ~ 6.5_

- [ ] 2. 编写医院 Mock 数据 SQL（01_hospitals.sql）
   - 插入测试医院A（`HOSPITAL_A`）和测试医院B（`HOSPITAL_B`）
   - 使用 `INSERT IGNORE` 保证幂等性，每条语句包含中文注释
   - _需求：2.1 ~ 2.3_

- [ ] 3. 编写用户与权限 Mock 数据 SQL（02_users.sql）
   - 插入 7 个测试账号（super_admin、hospital_admin_1/2、doctor_1/2/3、parent_1）
   - 密码使用 BCrypt 加密（明文 `Test@1234`），正确关联角色和所属医院
   - 使用 `INSERT IGNORE` 保证幂等性，每条语句包含中文注释
   - _需求：1.1 ~ 1.5_

- [ ] 4. 编写患者基础信息 Mock 数据 SQL（03_patients.sql）
   - 为 7 种疾病类型各创建 3 名患者（共 21+ 名），命名格式 `测试患者_DSD_001`
   - 覆盖男/女性别、2010～2020 年出生日期、测试号段身份证和电话
   - 正确关联疾病类型编码（disClass）、所属医院 ID、创建医生 ID
   - 使用 `INSERT IGNORE` 保证幂等性，每条语句包含中文注释
   - _需求：3.1 ~ 3.4_

- [ ] 5. 编写疾病病例详情 Mock 数据 SQL（04_disease_cases.sql）
   - 为每位患者创建对应疾病类型的病例详情记录（7 张疾病子表：dsd_case、fss_case、cpp_case、mas_case、sga_case、sss_case、eltm_case）
   - 字段值在合理医学范围内（身高 80～180cm、体重 10～80kg 等）
   - 使用 `INSERT IGNORE` 保证幂等性，每条语句包含中文注释
   - _需求：4.1 ~ 4.3_

- [ ] 6. 编写随访记录 Mock 数据 SQL（05_follow_ups.sql）
   - 为每位患者创建 3～5 条随访记录（`patient_follow_up` 表），时间间隔 3～6 个月
   - 身高/体重随时间递增（模拟儿童生长趋势），BMI 根据身高体重计算，包含骨龄字段
   - 为 MAS 患者额外创建 `mas_follow_up` 表中的 MAS 专属随访记录
   - 使用 `INSERT IGNORE` 保证幂等性，每条语句包含中文注释
   - _需求：5.1 ~ 5.5_