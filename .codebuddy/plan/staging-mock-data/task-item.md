# 实施计划：测试环境（Staging）Mock 数据初始化

- [ ] 1. 创建脚本目录结构与 README 文档
   - 在 `eksjk_v2/scripts/mock-staging/` 下创建目录结构（`sql/` 子目录）
   - 编写 `README.md`，包含所有 22 个测试账号说明、执行前置条件、各 SQL 文件数据统计、与本地 mock 数据的差异对比
   - _需求：6.1、6.5_

- [ ] 2. 编写医院与用户账号 SQL（`01_hospitals.sql`、`02_users.sql`）
   - `01_hospitals.sql`：插入 5 家医院（北京/上海/广州/成都/武汉），含省市区、联系人等完整字段，使用 `INSERT IGNORE` 保证幂等
   - `02_users.sql`：插入 22 个账号（1 超管 + 5 医院管理员 + 15 医生 + 10 家长），密码统一 BCrypt(`Test@1234`)，家长账号关联对应患者
   - _需求：1.1、2.1、6.3、6.6_

- [ ] 3. 编写 7 种疾病患者基础信息 SQL（`03_patients_dsd.sql` ～ `09_patients_eltm.sql`）
   - 每个疾病类型各 30 名患者，分布在 5 家医院（每院 6 名），使用真实感中文姓名
   - 性别分布符合流行病学特征（CPP 女性 80%，DSD 男女各半等），出生日期覆盖 2008～2020 年
   - 创建时间均匀分布在 2022 年至今，身份证号使用合法测试号段
   - _需求：3.1、3.2、7.1、7.2_

- [ ] 4. 编写 DSD / FSS / CPP 病例详情 SQL（`10_cases_dsd.sql`、`11_cases_fss.sql`、`12_cases_cpp.sql`）
   - DSD：覆盖 `46,XY`/`46,XX`/`45,X/46,XY`/`47,XXY` 等核型，5 种诊断亚型，含激素检测数值
   - FSS：含骨龄、身高 SDS（-4.0～-1.5）、基因检测结果、父母身高、遗传靶身高，5 种亚型
   - CPP：含初诊年龄、骨龄超前年数（1.0～3.5）、LH/FSH 峰值、Tanner 分期，GnRHa 治疗方案
   - 所有数值字段不为 NULL，轻/中/重病情各约 1/3
   - _需求：4.2、4.3、4.4、4.9、4.10、7.4_

- [ ] 5. 编写 MAS / SGA / SSS / ELTM 病例详情 SQL（`13_cases_mas.sql` ～ `16_cases_eltm.sql`）
   - MAS：含咖啡斑描述、骨纤维异常增殖部位、甲状腺/肾上腺异常、GH 过多标志，完全型/不完全型亚型
   - SGA：含出生体重（1500～2500g）、出生身长、胎龄、追赶生长情况、IGF-1、GH 治疗方案
   - SSS：含父母身高（偏矮）、遗传靶身高、生长速率（4.0～6.0 cm/year）、基因检测阴性
   - ELTM：按 `datamain_szfyeltm` 表结构填充完整字段，覆盖多种典型场景
   - _需求：4.5、4.6、4.7、4.8、4.9、4.10_

- [ ] 6. 编写随访记录 SQL（`17_follow_ups.sql`）
   - 为全部 210+ 名患者各生成 5～10 条随访记录，总计 ≥ 1050 条，时间跨度 2～3 年（每 3～6 个月一次）
   - 身高单调递增（每次 +1.5～4.0cm），体重与 BMI 协调变化，骨龄随时间递增
   - CPP 患者体现 GnRHa 治疗效果（骨龄超前幅度逐渐减小，LH/FSH 峰值下降）
   - MAS 患者同步写入 `datamain_masfoll` 表的专属随访字段
   - _需求：5.1、5.2、5.3、5.4、7.3、7.5_

- [ ] 7. 编写初始化与清理 Shell 脚本（`init-staging-mock-data.sh`、`clean-staging-mock-data.sh`）
   - `init-staging-mock-data.sh`：按 `01 → 17` 顺序执行 SQL，支持本地 mysql 客户端和 K8S Pod 两种执行方式，完成后打印数据统计摘要（医院/用户/患者/病例/随访数量）
   - `clean-staging-mock-data.sh`：按外键依赖逆序删除所有 staging mock 数据，执行前需二次确认
   - 支持通过环境变量 `INIT_STAGING_MOCK=true` 触发自动执行，适配 GitHub Actions CI/CD 流程
   - _需求：6.2、6.3、6.4、6.7_
