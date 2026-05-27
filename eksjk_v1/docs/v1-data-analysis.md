# EKSJK V1 系统分析 —— 数据展示与修改功能

> 分析日期：2026-05-24
> 分析范围：eksjk_v1（Python Django 3.0.3 + Vue 2 + Element UI）
> 目的：为 V2（Spring Boot + Vue 3）重构提供业务逻辑参考

---

## 一、项目概览

### 1.1 技术架构

| 层级 | 技术 |
|------|------|
| 后端框架 | Django 3.0.3 (Python 3.7+) |
| 数据库 | MySQL 5.7+ |
| 前端框架 | Vue 2.6.11 + Vue Router 3.1.5 + Vuex 3.1.2 |
| UI 组件库 | Element UI 2.13.0 |
| HTTP 客户端 | Axios（封装在 `request.js`） |
| 图表 | ECharts |
| 医学影像 | Cornerstone.js（DICOM 渲染） |
| 认证 | Django Session（Web端） + 自定义 JWT（小程序端） |
| 密码加密 | RSA-1024（前端加密传输）+ BCrypt（后端存储） |
| 部署 | Docker + Kubernetes + Nginx |

### 1.2 Django App 模块

| App | 功能 | 说明 |
|-----|------|------|
| `login` | 用户认证与机构管理 | 登录、用户 CRUD、单位 CRUD、RSA 密钥 |
| `datamain` | 核心临床数据管理 | 患者、随访、7种疾病子表、影像、文件 |
| `school` | 学校健康筛查 | 学生体检数据、7份问卷量表 |
| `xcx` | 微信小程序接口 | 家长端小程序后端 API |
| `notiopi` | 通知与反馈 | 系统公告、用户意见反馈 |
| `statistics` | 统计报表 | 数据统计看板（集成在 datamain views 中） |

---

## 二、数据模型

### 2.1 核心实体关系图

```
login.User (用户)           login.Unit (医疗机构)
  ├── unit: CharField          ├── unit_name
  ├── role_code                 ├── province/city/district
  ├── level (0-5)               └── status
  └── is_superuser
       │
       │ (creator / up_mec)
       ▼
datamain.Patient (患者主表) ───────────────────────────────────
  ├── dis_class (疾病编码)     │ 10000001 DSD    10000005 SGA
  ├── case_num (病例号)        │ 10000002 FSS    10000006 SSS
  ├── name, sex, birth_time   │ 10000003 CPP    10000007 ELTM
  ├── height, weight, bmi     │ 10000004 MAS
  ├── contacts (JSON: 父母信息)
  ├── family_his (JSON: 家族史)
  └── tags (JSON: 诊断标签)
       │
       ├── 1:1 ──► datamain.Case (DSD子表)
       ├── 1:1 ──► datamain.Short (FSS子表)
       ├── 1:1 ──► datamain.Sexprecocity (CPP子表)
       ├── 1:1 ──► datamain.Mas (MAS子表)
       ├── 1:1 ──► datamain.SGA (SGA子表)
       ├── 1:1 ──► datamain.JzxShort (SSS子表)
       ├── 1:1 ──► datamain.SzfyEltm (ELTM子表)
       │
       └── 1:N ──► datamain.PatFoll (随访记录)
                     └── 1:1 ──► datamain.MasFoll (MAS随访)
```

### 2.2 患者主表关键字段（Patient）

| 字段 | 类型 | 说明 |
|------|------|------|
| `dis_class` | CharField(20) | 疾病编码，决定关联哪个疾病子表 |
| `case_num` | CharField(50) | 病例号，格式 `US-{缩写}{日期}{序号}` |
| `medrec_num` | CharField(50) | 病历号 |
| `name` | CharField(100) | 患者姓名 |
| `sex` | CharField(10) | 性别 |
| `birth_time` | DateTimeField | 出生日期 |
| `age` | CharField(20) | 年龄字符串（如 "5岁3个月"） |
| `height/weight/bmi` | CharField(20) | 身高/体重/BMI |
| `self_tel` | CharField(20) | 联系电话 |
| `contacts_name/contacts_num` | CharField | 联系人姓名/电话 |
| `idcard` | CharField(50) | 身份证号 |
| `doctor_name` | CharField(100) | 主治医生 |
| `ICD` | CharField(50) | ICD-10 诊断编码 |
| `chi_com` | TextField | 主诉（Chief Complaint） |
| `family_his` | TextField | 家族史（JSON 字符串） |
| `past_his` | TextField | 既往史（JSON 字符串） |
| `fet_pro_his` | TextField | 胎儿期病史 |
| `xcx_card` | CharField(100) | 小程序 OpenID 关联 |
| `baby_flag` | CharField(10) | 是否小程序添加的宝宝 |
| `up_mec` | CharField(200) | 归属医院（存单位名称字符串，非外键） |
| `del_flg` | CharField(2) | 逻辑删除标记 `'1'`=正常 `'0'`=已删除 |
| `tags` | CharField(500) | JSON 标签 |

### 2.3 疾病子表

**DSD（性发育异常）— datamain.Case**
| 字段 | 说明 |
|------|------|
| karyotype | 核型（如 46,XX / 46,XY） |
| gonadal_status | 性腺状态描述 |
| external_genitalia | 外生殖器检查（JSON Text） |
| internal_genitalia | 内生殖器检查（JSON Text） |
| hormone_levels | 性激素水平（JSON Text） |
| diagnosis | 诊断结论 |
| treatment_plan | 治疗方案 |

**FSS（遗传性骨病/矮小症）— datamain.Short**
| 字段 | 说明 |
|------|------|
| bone_age | 骨龄 |
| height_sds | 身高标准差评分 |
| genetic_test | 基因检测结果 |
| diagnosis | 诊断结论 |
| treatment_plan | 治疗方案 |

**CPP（中枢性性早熟）— datamain.Sexprecocity**
| 字段 | 说明 |
|------|------|
| onset_age | 发病年龄 |
| bone_age_advance | 骨龄超前程度 |
| lh_peak | LH 激发峰值 |
| fsh_peak | FSH 激发峰值 |
| diagnosis | 诊断结论 |
| treatment_plan | 治疗方案 |

**MAS（McCune-Albright 综合征）— datamain.Mas**
| 字段 | 说明 |
|------|------|
| cafe_au_lait_spots | 咖啡牛奶斑 |
| fibrous_dysplasia | 骨纤维异常增殖 |
| precocious_puberty | 性早熟 |
| thyroid_abnormality | 甲状腺异常 |
| gh_excess | GH 分泌过多 |
| cushing_syndrome | 库欣综合征 |
| phosphate_wasting | 磷酸盐丢失 |

**MAS 随访 — datamain.MasFoll**（独立于普通随访的 MAS 专项随访表）
| 字段 | 说明 |
|------|------|
| is_per_pre / per_pre_sf | 外周性性早熟 |
| is_hyper / hyper_sf | 甲亢 |
| is_gro_hor / gro_hor_sf | 生长激素过量 |
| is_tre_hpy / tre_hpy_sf | 治疗性甲减 |
| is_inc_cor / inc_cor_sf | 皮质醇增多 |
| is_int_sur / is_bil_adr | 手术/肾上腺 |
| is_bon_pai / bon_pai_sf | 骨痛 |
| is_ske_sur / is_cafe_spot | 骨骼手术/咖啡斑 |
| is_psy_cou | 心理咨询 |

**SGA（小于胎龄儿）— datamain.SGA**
| 字段 | 说明 |
|------|------|
| birth_weight | 出生体重 |
| birth_length | 出生身长 |
| gestational_age | 胎龄 |
| catch_up_growth | 追赶生长情况 |

**SSS（家族性矮小）— datamain.JzxShort**
| 字段 | 说明 |
|------|------|
| father_height | 父亲身高 |
| mother_height | 母亲身高 |
| target_height | 靶身高 |
| genetic_test | 基因检测 |
| diagnosis / treatment_plan | 诊断/方案 |

**ELTM（E路童萌）— datamain.SzfyEltm**
| 字段 | 说明 |
|------|------|
| screening_result | 筛查结果 |
| assessment_data | 评估数据（JSON Text） |
| diagnosis / treatment_plan | 诊断/方案 |

### 2.4 随访记录（PatFoll）

| 字段 | 说明 |
|------|------|
| foll_time | 随访日期 |
| age / Ht / Wt / bmi | 年龄/身高/体重/BMI |
| body_fat / waistline / hips | 体脂/腰围/臀围 |
| rbone_age / cbone_age | 骨龄（R骨龄/C骨龄） |
| gen_stag / pub_stag | Tanner 分期（生殖器/阴毛） |
| IGF1 / IGFBP3 | 胰岛素样生长因子 |
| fas_blood_glu / fas_insulin / gly_hem | 空腹血糖/胰岛素/糖化血红蛋白 |
| LH / FSH / E2 / T / DHT | 性激素六项 |
| gon_b_ult | 性腺 B 超（JSON Text） |
| tes_size | 睾丸大小 |
| dia_trea_plan | 诊断与治疗方案（JSON Text） |
| beh_dev_ass | 行为发育评估 |
| disease / dsdk / clls / qtyl | 伴随疾病信息 |
| image | 影像资料（JSON Text） |
| del_flg | 逻辑删除标记 |

### 2.5 学校筛查模型（School）

**school.Student** — 学生主表
| 关键字段 | 说明 |
|----------|------|
| num | 学号 |
| sclass | 班级 |
| name / sex / birth_time | 基本信息 |
| height / weight / bmi | 体格测量 |
| jtnsr | 家庭年收入 |
| 7份问卷字段 | SDQ、CBQ、养育方式、亲子活动、屏幕暴露、体力活动、睡眠习惯 |

7份问卷的模型类：`Sdq、Cbq、FfssFmfs、FfssQzhd、FfssPmbw、FfssTlhd、FfssSmxg`

### 2.6 用户与机构模型

**login.User**（继承 Django AbstractUser）
| 字段 | 说明 |
|------|------|
| name | 中文姓名 |
| unit | 所属机构 ID（CharField，非外键） |
| level | 用户级别（0=普通, 1=初审, 2=复审, 3=管理员, 4=医学编辑, 5=终审） |
| professional | 职称 |
| department | 科室 |
| role_code | 角色编码 |
| phone | 手机号 |
| job_number | 工号 |
| wx_openid | 微信 OpenID |

**login.Unit** — 医疗机构
| 字段 | 说明 |
|------|------|
| unit_name / unit_short_name / unit_code | 单位信息 |
| contact_name / contact_phone | 联系人 |
| province / city / district | 省市区 |
| status | 状态（1=启用） |
| del_flg | 逻辑删除标记 |

> ⚠️ **数据完整性警告**：`User.unit` 存储的是 Unit 的 ID 字符串而非 ForeignKey，没有数据库级别的引用完整性约束。

### 2.7 其他模型

- **OperLog**（datamain）：操作审计日志
- **Notice**（notiopi）：系统公告
- **Opinion**（notiopi）：用户反馈意见
- **ChartUser**（xcx）：微信小程序用户（关联 OpenID）

---

## 三、后端 API 与数据展示

### 3.1 基础架构：FormattedView

所有 View 类继承自 `common/utils.py` 中的 `FormattedView`（基于 Django `View` 基类）：

```python
class FormattedView(View):
    loginRequired = True  # 默认需要登录

    def dispatch(self, request, *args, **kwargs):
        # 1. 检查登录状态
        # 2. 根据 HTTP Method 分发到 get/put/post/delete
        # 3. 返回统一 JSON: {"code": int, "data": any}
```

**核心工具函数：**
- `FormattedResponse(code, data)` — 统一响应格式（0=成功, 1=未登录, 2=密码错误...）
- `parse_arguments(source='url')` 装饰器 — 解析 URL 参数到 `**kwargs`
- `require_arguments(fields, source)` 装饰器 — 参数必填校验
- `encode_id(id)` / `decode_id(hashed)` — Hashids ID 混淆
- `Extractor` 类 — Model 实例序列化为 dict（自动 Hashids 编码 ID、格式化日期）
- `get_page(source)` — 分页参数提取（page/number 参数）

### 3.2 核心列表/搜索功能

#### 3.2.1 患者列表（CaseListView）

**端点：** `GET /api/datamain/caseList/`

**权限过滤规则：**
| 用户类型 | 过滤规则 |
|----------|----------|
| 超级管理员（is_superuser=1） | 查看全部患者 |
| 普通用户（医院管理员/医生） | 仅查看 `up_mec == request.user.unit` 的患者 |

**搜索过滤器：**
| 参数 | 查询方式 | 说明 |
|------|----------|------|
| `name` | `name__contains` | 姓名模糊查询 |
| `case_num` | `case_num__contains` | 病例号模糊查询 |
| `dis_class` | `dis_class__exact` | 疾病类型精确匹配 |
| `createDateRange` | `c_time__gte` + `c_time__lte` | 创建时间范围（逗号分隔） |
| `sex` | `sex__exact` | 性别过滤 |
| `idcard` | `idcard__contains` | 身份证号模糊查询 |
| `doctor_name` | `doctor_name__contains` | 医生姓名模糊查询 |

**返回结构：**
```json
{
  "code": 0,
  "data": {
    "contacts": [
      {
        "id": "hash编码的ID",
        "name": "患者姓名",
        "sex": "男/女",
        "age": "5岁3个月",
        "case_num": "US-Short20210524001",
        "dis_class": "10000002",
        "doctor_name": "张医生",
        "c_time": "2021-05-24",
        "hospital_name": "测试医院A",
        "chi_com": "主诉内容...",
        "ICD": "E34.3",
        "tags": "[...]",
        "up_mec": "1",
        ...
      }
    ],
    "pagedata": {
      "count": 100,         // 总记录数
      "num_pages": 10,      // 总页数
      "per_page": 10,       // 每页记录数
      "current": 1          // 当前页码
    }
  }
}
```

**排序：** `order_by('-c_time')` 按创建时间降序

**分页：** Django `Paginator(queryset, per_page)`，每页 10 条

#### 3.2.2 患者详情/编辑查询（PatientView.GET）

**端点：** `GET /api/datamain/patient/`

**参数：**
| 参数 | 来源 | 说明 |
|------|------|------|
| `queryId` | URL | Hashids 编码的患者 ID |
| `organ` | URL | 疾病类型编码（新建时使用，查询疾病子表） |

**逻辑流程：**
1. 解码 `queryId` 获取患者自增 ID
2. 查询 Patient 主表
3. 根据 `dis_class` 调用 `query_sub_table()` 查询对应的疾病子表
4. 合并主表和子表数据返回

**query_sub_table() 分发逻辑：**
| dis_class | 查询子表 |
|-----------|----------|
| 10000001 | Case |
| 10000002 | Short |
| 10000003 | Sexprecocity |
| 10000004 | Mas |
| 10000005 | SGA |
| 10000006 | JzxShort |
| 10000007 | SzfyEltm |

#### 3.2.3 随访列表（FollowView.GET + EFollowView.GET）

**患者随访列表：** `GET /api/datamain/follow/`
- 参数：`patient_id`（Hashids 编码）
- 返回该患者所有随访记录，按 `foll_time` 降序
- 随访记录中的 JSON Text 字段（如 `image`、`other_ima_name`）被反序列化为对象

**单条随访记录：** `GET /api/datamain/Efollow/`
- 参数：`id`（随访记录 Hashids ID）
- 用于编辑随访时的数据回填
- 支持通过 `organ` 参数返回 MAS 随访数据（`MasFoll`）

#### 3.2.4 用户列表（UserListView）

**端点：** `GET /api/login/user`

**搜索过滤：** `name__contains`、`username__contains`
**权限过滤：** `is_superuser == 0`（不显示超级管理员）

#### 3.2.5 单位列表（UnitListView）

**端点：** `GET /api/login/unit`

**搜索过滤：** `unit_name__contains`
**权限过滤：** 无限制（所有用户可见所有单位）

#### 3.2.6 学校学生列表（StudentListView）

**端点：** `GET /api/school/studentList/`

**权限过滤：**
| 用户类型 | 过滤规则 |
|----------|----------|
| 超级管理员 | 查看全部 |
| 管理员/初审人(level=1) | 仅查看 `up_mec == request.user.unit` |
| 普通用户 | 仅查看 `doctor == request.user` |

**搜索过滤：** `num__contains`、`sclass__contains`、`name__contains`

#### 3.2.7 统计看板（StatisticPosi / Dashboard）

**端点：** `GET /api/datamain/staCensus/`

**返回数据：**
- 按 `organ`（疾病类型）分类统计病例数
- 按 `sex` 性别统计
- 按 `up_mec` 单位统计
- 随访次数统计

> ⚠️ **安全警告**：统计查询使用原始 SQL 拼接，存在潜在的 SQL 注入风险。

#### 3.2.8 操作日志（ModifylodListView）

**端点：** `GET /api/datamain/getModifyLog/`

- 按创建时间降序
- 支持按时间范围和操作人姓名过滤
- 将 `oper_per_id` 解析为用户真实姓名

### 3.3 报表导出功能

**单病例报告：** `PUT /api/datamain/loadFile`
**MAS 病例报告：** `PUT /api/datamain/loadFilemas`

- 根据 `patient_id` 查询完整病例数据（主表 + 子表 + 随访 + MAS 随访）
- 使用 `ExcelFile` 工具类生成 Excel 报告文件
- 返回文件下载 URL

### 3.4 影像/文件管理

**上传：** `POST /api/datamain/image` — multipart/form-data，支持进度回调
**下载：** `GET /api/datamain/image` — 通过 Nginx `X-Accel-Redirect` 头代理到受保护的存储目录
**批量下载：** `PUT /api/datamain/downZipPl` — 将多个影像打包为 ZIP 返回下载 URL
**删除：** `DELETE /api/datamain/image`

存储路径：`E:/imgtest/{patient_id}/{category}/`（开发环境）

### 3.5 后端数据提取器（Extractor）

Extractor 是 V1 中核心的数据序列化工具，定义了每个模型的字段映射关系：

- **Extractor.patient_extractor()**：提取 Patient + 疾病子表字段，自动 Hashids 编码 ID
- **Extractor.follow_extractor()**：提取 PatFoll 字段，含 JSON 字段反序列化
- **Extractor.patientlist_extractor()**：列表页专用，按器官（疾病）类型返回不同字段子集
- **Extractor.extractor_school()**：学校筛查数据字段映射

> **V2 对应：** 在 V2 中这些 Extractor 的角色由 MapStruct 的 Entity → VO 转换取代。


---

## 四、前端数据展示与交互

### 4.1 技术栈与项目结构

```
ek-frontend/src/
├── main.js                          # Vue 入口
├── App.vue                          # 根组件
├── router.js                        # 路由配置
├── store.js                         # Vuex 状态管理（仅存用户信息）
├── script/
│   ├── request.js                   # Axios 封装（核心 API 层）
│   ├── selectCount.js               # 枚举值映射表（性别/分娩方式/职称等）
│   ├── image.js                     # 图片处理
│   └── otherImage.js                # 随访图片类型定义
├── utils/
│   ├── heightValidator.js           # 身高校验（年龄感知）
│   ├── ICDData.js                   # ICD-10 编码库（~2000条）
│   ├── ethnicityData.js             # 民族数据
│   └── cityData.js                  # 省市数据
├── components/
│   ├── Login.vue                    # 登录页
│   ├── Home.vue                     # 患者列表主页
│   ├── Editor.vue                   # 病例编辑器（新建/编辑）
│   ├── Detail.vue                   # 病例详情展示
│   ├── diseaseSelect.vue            # 疾病类型选择页
│   ├── StudentEditor.vue            # 学校筛查录入
│   ├── TianYuan.vue                 # 学校数据展示
│   ├── User.vue / Unit.vue          # 用户/单位管理
│   ├── UserProfile.vue              # 个人中心
│   ├── editLog.vue                  # 操作日志
│   ├── StatisticsPosi.vue           # 统计看板
│   └── common/
│       ├── SearchBar.vue            # 通用搜索栏
│       ├── SearchMenu.vue           # 通用搜索菜单
│       ├── SearchUser.vue           # 用户搜索
│       ├── SearchUnit.vue           # 单位搜索
│       ├── EditorUser.vue           # 用户编辑弹窗
│       ├── EditorUnit.vue           # 单位编辑弹窗
│       ├── InsertUser.vue           # 新增用户弹窗
│       ├── InsertUnit.vue           # 新增单位弹窗
│       ├── DSD.vue                  # 性发育异常子表单
│       ├── FSS.vue                  # 遗传性骨病子表单
│       ├── CPP.vue                  # 性早熟子表单
│       ├── MAS.vue                  # MAS 子表单
│       ├── SGA.vue                  # SGA 子表单
│       ├── SSS.vue                  # 家族性矮小子表单
│       ├── ELTM.vue                 # E路童萌子表单
│       ├── TYGX.vue                 # 生长发育子表单
│       ├── shortFollow.vue          # 随访记录表单
│       ├── logSearchBar.vue         # 日志搜索栏
│       └── TYSearchBar.vue          # 学校数据搜索栏
│   └── imageViewer/
│       ├── ImageViewer.vue          # 医学影像查看器（Cornerstone.js）
│       ├── ImageUpload.vue          # 图片上传组件
│       ├── ImagePalette.vue         # 图片调色板
│       └── fileUpload.vue           # 报告文件上传组件
```

### 4.2 路由设计

```javascript
// router.js 路由表
routes: [
  { path: '/login', component: Login },                         // 登录
  { path: '/', component: Home },                                // 患者列表主页
  { path: '/selectOrgan', component: diseaseSelect },            // 选择疾病类型
  { path: '/editor', component: Editor },                        // 新建/编辑病例
  { path: '/detail', component: Detail },                        // 病例详情
  { path: '/students', component: TianYuan },                    // 学校数据列表
  { path: '/studentEditor', component: StudentEditor },          // 学校数据录入
  { path: '/userprofile', component: UserProfile },              // 个人中心
  { path: '/users', component: User },                           // 用户管理
  { path: '/units', component: Unit },                           // 单位管理
  { path: '/statistics', component: StatisticsPosi },            // 统计看板
  { path: '/editLog', component: editLog },                      // 操作日志
]
```

**路由守卫：** `router.beforeEach` 检查 `localStorage` 中的用户信息，未登录跳转 `/login`。

### 4.3 API 请求层（request.js）

`Request` 类封装了所有 HTTP 交互，是前端数据流的核心中转：

```javascript
class Request {
    constructor() {
        this.BASE_URL = ''                // API 基础路径
        this.TIMEOUT = 10000              // 请求超时 10s
        this.instance = axios.create()    // Axios 实例
    }

    _get(url, data, success, error) {}    // GET 请求
    _post(url, data, success, error) {}   // POST 请求
    _put(url, data, success, error) {}    // PUT 请求
    _delete(url, data, success, error) {} // DELETE 请求
}
```

**统一响应处理逻辑：**
```
HTTP 200 + code == 0  → 调用 success(data) 回调
HTTP 200 + code == 1  → 跳转登录页（未认证）
HTTP 200 + code != 0  → 调用 error(response) 回调
HTTP 4xx/5xx          → 显示错误消息
```

**登录流程：**
1. `GET /api/login/login/` 获取 RSA 公钥
2. 前端使用 `jsencrypt` 库以公钥加密密码
3. `POST /api/login/login/` 发送 `{username, password, authKey}` 完成认证
4. 成功后将用户信息存入 `localStorage` + Vuex

### 4.4 患者列表页（Home.vue）

**核心数据展示功能：**

1. **表格列动态配置** — 根据当前疾病类型（`organ`）展示不同列：
   - 所有类型共享：姓名、性别、年龄（计算）、病例号、时间
   - 矮小症(FSS/SSS)额外显示：身高、骨龄、身高 SDS
   - 性发育异常(DSD)额外显示：核型、性腺
   - 性早熟(CPP)额外显示：发病年龄、骨龄超前
   - MAS 额外显示：咖啡斑、骨纤维异常增殖

2. **搜索条件动态切换** — SearchBar 组件根据疾病类型渲染不同的搜索表单项

3. **分页** — Element UI `<el-pagination>`，current-page 绑定 vue data

4. **排序** — 默认按创建时间降序，后台排序

**关键 Data 属性：**
```javascript
data() {
    return {
        organ: '',              // 当前疾病类型编码
        tableData: [],          // 表格数据
        currentPage: 1,         // 当前页码
        pageSize: 10,           // 每页条数
        total: 0,               // 总记录数
        filters: {},            // 搜索条件
        loading: false,         // 加载状态
    }
}
```

**数据获取流程：**
```
Home.activated()
  → 读取 sessionStorage.getItem('organ')
  → 调用 update(filters)
  → request.getCaseList(filters, success, error)
  → GET /api/datamain/caseList/?page=1&number=10&dis_class=10000002&name=xxx
  → success: tableData = response.contacts; total = response.pagedata.count
```

**操作入口：**
- 点击"新增" → `/selectOrgan`（选择疾病类型）→ `/editor?organ=10000002`
- 点击行"编辑" → `/editor?queryId=<hashid>&disClass=10000002`
- 点击行"详情" → `/detail?queryId=<hashid>`
- 支持行内快速操作（通过操作列按钮）

### 4.5 疾病类型选择页（diseaseSelect.vue）

**功能：** 新建病例前的疾病类型分流页面

展示 8 种疾病类型卡片（含图标和说明）：
| 编码 | 疾病 | 卡片标签 |
|------|------|----------|
| 10000001 | 性发育异常 | DSD |
| 10000002 | 矮小症 | FSS / 遗传性骨病 |
| 10000003 | 中枢性性早熟 | CPP |
| 10000004 | McCune-Albright | MAS |
| 10000005 | 小于胎龄儿 | SGA |
| 10000006 | 家族性矮小 | SSS |
| 10000007 | 生长发育 | ELTM / E路童萌 |
| 10000010 | 生长发育 | TYGX |

选择后将 `organ` 存入 `sessionStorage`，跳转到 `/editor?organ=<编码>`。

### 4.6 病例详情页（Detail.vue）

**核心展示功能：**

采用**左右两栏布局**：

**左栏 — 患者基本信息：**
- 姓名、性别、年龄（计算显示）、出生日期、联系电话
- 联系人与关系
- 主治医生、医院
- ICD 诊断编码
- 主诉（chi_com 完整文本展示）
- 家族史（family_his JSON 解析展示）
- 既往史（past_his JSON 解析展示）
- 标签（tags JSON 解析为标签云）

**右栏 — 疾病专项信息（按 dis_class 分发展示）：**
- DSD：核型、性腺状态、外生殖器、内生殖器、激素水平、诊断、治疗方案
- FSS/SSS：身高、骨龄、身高 SDS、父母身高、遗传检测、诊断、方案
- CPP：发病年龄、骨龄超前、LH/FSH 峰值、诊断、方案
- MAS：咖啡斑、骨异常、性早熟、甲状腺、GH、诊断、方案 + MAS 专项随访
- SGA：出生体重/身长、胎龄、追赶生长、诊断、方案
- ELTM/TYGX：筛查结果、评估数据、诊断、方案

**底部 — 随访记录时间线：**
- 按随访日期倒序排列
- 每次随访显示：日期、年龄、身高、体重、BMI、骨龄、Tanner 分期、性激素
- 点击可展开查看完整随访详情
- 支持"新增随访"按钮跳转到 Editor 的随访模式

**影像查看：** ImageViewer 组件嵌入，支持 Cornerstone.js DICOM 渲染

### 4.7 统计看板（StatisticsPosi.vue）

**数据来源：** `GET /api/datamain/staCensus/`

**可视化内容：**
- **疾病类型分布** — 饼图/柱状图（ECharts），按 dis_class 统计病例数
- **性别分布** — 饼图
- **机构分布** — 横向柱状图，按 up_mec 统计
- **随访统计** — 随访次数分布
- **趋势图** — 按月份的病例增长趋势


---


## 五、数据修改功能

### 5.1 病例编辑器（Editor.vue）

Editor.vue 是 V1 系统中最重要的组件，承载了所有病例的新建和编辑功能。

整体采用三区布局：
- 顶部操作栏：保存 / 保存并新建 / 取消按钮 + 当前病例号
- 左面板：患者基本信息（约40个字段）
- 右面板：根据疾病类型动态加载的疾病专项表单
- 底部（编辑模式）：历史随访列表 + 新增随访表单 + 影像上传

疾病子表单通过 v-if 动态加载：
  - organ=10000001 → DSD.vue (性发育异常)
  - organ=10000002 → FSS.vue (遗传性骨病)
  - organ=10000003 → CPP.vue (中枢性性早熟)
  - organ=10000004 → MAS.vue (MAS综合征)
  - organ=10000005 → SGA.vue (小于胎龄儿)
  - organ=10000006 → SSS.vue (家族性矮小)
  - organ=10000007 → ELTM.vue (E路童萌)
  - organ=10000010 → TYGX.vue (生长发育)

### 5.2 新建病例数据流

diseaseSelect.vue (选择疾病类型) → /editor?organ=10000002
  → Editor.activated()
    → 初始化空 ruleForm
    → 用户填写左面板 + 右面板
  → 点击保存 → addData()
    → 收集左面板数据
    → this.$refs.form.addData() 收集疾病专项数据
    → Object.assign() 合并
    → PUT /api/datamain/patient/
      → 后端自动生成 case_num (US-Short20240524001)
      → 创建 Patient + 疾病子表记录
      → 返回 id, case_num

### 5.3 编辑病例数据流

Home.vue → /editor?queryId=hashid&disClass=10000002
  → Editor.activated()
    → getPatientInfo()
      → GET /api/datamain/patient/?queryId=hashid&organ=10000002
      → 填充左面板 ruleForm
    → 子表单自动触发 getCase()
      → GET /api/datamain/case/?queryId=hashid
      → 解析 JSON Text 字段填充右面板
    → getFollowList()
      → GET /api/datamain/follow/?patient_id=hashid
    → getImageList()
      → GET /api/datamain/image/?patient_id=hashid
  → 修改 → 保存 → PUT /api/datamain/patient/

### 5.4 随访记录管理

新增随访：
  → 展开 shortFollow.vue 表单
  → 填写：日期、身高、体重、BMI、骨龄、Tanner分期、性激素六项、IGF1/IGFBP3、B超等
  → 上传随访影像（骨龄图、脊柱全长片、其他）
  → PUT /api/datamain/follow/
  → MAS(10000004) 额外创建 MasFoll 记录

编辑随访：
  → GET /api/datamain/Efollow/?id=hashid&organ=dis_class
  → 回填表单 → 修改 → PUT /api/datamain/follow/

### 5.5 影像上传

ImageUpload 组件：
  → 选择文件 → 自动上传
  → POST /api/datamain/image (multipart/form-data, 参数: patient_id, category, file)
  → 保存病例时 imageList 随表单一起提交

三种随访影像分类：骨龄图片、脊柱全长片、其他

### 5.6 疾病子表单通用模式

每个疾病子表单遵循相同结构：
  - props: [ruleForm]  接收父组件传入的患者信息
  - addData(): 返回疾病专项数据给 Editor
  - getCase(): 编辑模式下通过 API 加载已有数据
  - watch ruleForm.queryId: 自动触发数据加载

8个子表单核心字段：
  DSD - 核型、性腺状态、内外生殖器、激素水平
  FSS - 骨龄、身高SDS、遗传检测
  CPP - 发病年龄、骨龄超前、LH/FSH峰值、GnRH试验、MRI
  MAS - 咖啡斑、骨异常、性早熟、甲状腺、GH、库欣
  SGA - 出生体重/身长、胎龄、追赶生长
  SSS - 父母身高、靶身高、遗传检测
  ELTM - 筛查结果、评估数据
  TYGX - 通用生长发育评估

### 5.7 用户与单位管理

用户管理 API: /api/login/user
  GET - 列表 (name/username 模糊搜索, 排除超管)
  PUT - 新增 (InsertUser.vue 弹窗)
  POST - 编辑 (EditorUser.vue 弹窗)
  DELETE - 硬删除 (系统中唯一不使用软删除的实体)
  POST /userStatus - 切换启用/禁用
  表单字段: username, password, name, role_code, unit, level, professional, department, phone, job_number

单位管理 API: /api/login/unit
  GET - 列表 (unit_name 模糊搜索)
  PUT - 新增
  POST - 编辑
  DELETE - 软删除 (del_flg=0)

### 5.8 通知与反馈

公告 Notice: /api/notiopi/notice/
  GET/PUT/DELETE, title 模糊搜索, 分页
  closeCount 倒计时: POST /api/notiopi/closeNotice/ → 递减后归零不再展示

反馈 Opinion: /api/notiopi/opinionView/
  用户提交 → 管理员查看 → POST /api/notiopi/replyOpinionView/ 标记已处理



---

## 六、微信小程序端数据流

### 6.1 双认证体系

V1 小程序端存在两套认证系统：

**系统A - Session-based（类视图）：**
- CSessionKeyView: GET 调用微信 code2session，自动创建 ChartUser
- BdDectorView: POST 绑定医生到 ChartUser
- oneCaseByBLPView: GET/POST 患者数据查询与创建
- CaseByBLPView: GET 返回身高体重图表数据, POST 新增随访

**系统B - JWT-based（函数视图 + @csrf_exempt + @token_required）：**
- doLoginView: 微信 code2session + 生成 JWT（7天, secret: mk5677123）
- selfInfoStoreView: POST 更新用户资料
- addBabyView/editBabyView/deletBabyView/selectBabyView: 宝宝CRUD
- selectHistroyView: 获取随访历史
- againReviewView: 再次随访（未完整实现）

### 6.2 数据关联机制

OpenID 是核心关联键：
- ChartUser.openid = 微信返回的 openid
- Patient.xcx_card = ChartUser.openid（区分小程序患者和Web端患者）
- Patient.baby_flag = 1（标记为小程序添加的宝宝）

### 6.3 微信号使用

- AppID: wx1ae7e6dba9c5e94b（生产环境）
- 密钥硬编码在 xcx/views.py 中
- JWT 使用 HS256 对称加密，密钥: mk5677123

### 6.4 小程序数据流

家长登录 → wx.login() 获取code → 后端交换openid → 生成JWT
  → addBaby: 填写宝宝信息 → POST /xcx/addBaby/
    → 后端创建 Patient(xcx_card=openid, baby_flag=1) + PatFoll
  → selectBabyAll: GET 该家长所有宝宝列表
  → editBaby: POST 修改宝宝信息
  → addFollow: POST /xcx/caseByBLP/ 添加随访（身高、体重、年龄）
  → selectHistroyView: POST 获取某宝宝的随访历史

---

## 七、核心架构模式总结

### 7.1 疾病类型路由模式

dis_class 是整个系统的核心路由字段：
- 前端: sessionStorage.organ 决定表格列、搜索栏、子表单
- 后端: PatientView.put() 中根据 dis_class 调用 modifyorAdd*()
- 列表: Extractor.patientlist_extractor() 根据类型返回不同字段
- 详情: query_sub_table() 根据类型查询对应子表

### 7.2 病例号生成规则

格式: US-{疾病缩写}{YYYYMMDD}{序号}
- DSD → US-Case20240524001
- FSS → US-Short20240524001
- CPP → US-Sexpre20240524001
- MAS → US-Mas20240524001
- SGA → US-SGA20240524001
- SSS → US-JzxShort20240524001
- ELTM → US-SzfyEltm20240524001
- 小程序 → US-Xcx20240524001

### 7.3 JSON Text 存储模式

大量复杂数据结构以 JSON 字符串存储在 TextField 中：
- family_his: 家族史
- past_his: 既往史
- fet_pro_his: 胎儿期病史
- contacts: 联系人信息
- gon_b_ult: B超结果
- dia_trea_plan: 诊疗方案
- image: 影像列表
- tags: 诊断标签
- external_genitalia/hormone_levels 等: 疾病专项检查

这种模式灵活但不具备数据库级别的查询能力。

### 7.4 前后端数据交互模式

V1 使用回调模式（而非 Promise/async-await）：
```
request.getCaseList(params,
  (data) => { /* 成功回调 */ },
  (err)  => { /* 错误回调 */ }
)
```

前端通过 Axios 拦截器统一处理：
- code=0 → success(data)
- code=1 → 重定向登录
- HTTP错误 → Message.error

### 7.5 逻辑删除模式

几乎所有表使用 del_flg 字段标记删除：
- del_flg=1 表示正常（未删除）
- del_flg=0 表示已删除
- 所有列表查询过滤 del_flg=1
- User 表例外：使用硬删除

### 7.6 ID 混淆

所有对外暴露的数据库ID使用 Hashids 编码：
- 前端收到的 id 是 Hashids 字符串
- 后端 decode_id() 解码为数据库ID
- encode_id() 编码数据库ID为字符串
- 防止直接暴露自增ID序列

---

## 八、安全与代码质量问题

### 8.1 安全风险

| 问题 | 位置 | 严重度 |
|------|------|--------|
| 原始SQL拼接（SQL注入风险） | statistics/views.py StatisticPosi | 高 |
| CSRF中间件被注释掉 | settings.py | 中 |
| JWT密钥硬编码 | common/utils.py (mk5677123) | 中 |
| 微信AppSecret硬编码 | xcx/views.py | 高 |
| RSA-1024加密强度不足 | login/views.py | 低 |
| User.unit非外键，无引用完整性 | login/models.py | 中 |
| 无请求频率限制 | 所有登录接口 | 中 |
| 权限中间件被注释/禁用 | settings.py | 中 |

### 8.2 代码质量问题

| 问题 | 位置 |
|------|------|
| 大量重复代码（modifyorAdd* 函数相似度>90%） | datamain/views.py |
| JSON通过字符串拼接构造（易出错） | modifyorAdd* 函数中 |
| Model字段 unique=True 滥用（height/weight/age/bmi等） | datamain/models.py |
| contacts_num 被错误赋值 relation 的值 | EPatientNewView (line 6230) |
| OperLog.oper_case_id 重复声明 | models.py |
| 单文件过大（views.py 约7000行） | datamain/views.py |
| Session和JWT两套认证并存 | xcx/views.py |
| 无API版本管理 | 全局 |

---

## 九、V1 到 V2 迁移要点对照

### 9.1 架构差异

| 方面 | V1 | V2 (规划) |
|------|-----|-----------|
| 后端框架 | Django 3.0 | Spring Boot 3.x |
| ORM | Django ORM | MyBatis-Plus |
| 认证 | Session + 自研JWT | Sa-Token JWT |
| 前端 | Vue 2 Options API | Vue 3 Composition API |
| UI库 | Element UI | Element Plus |
| 状态管理 | Vuex | Pinia |
| 序列化 | 自研Extractor | MapStruct |
| 文件存储 | 本地磁盘+Nginx | MinIO (S3) |
| API风格 | 自研FormattedView | RESTful + 全局异常处理 |
| 参数校验 | 装饰器 | Spring Validation |

### 9.2 需要保留的核心业务逻辑

1. **疾病类型路由机制**: dis_class → 子表映射关系（10000001-10000007 → Case/Short/...）
2. **病例号生成规则**: US-{缩写}{YYYYMMDD}{序号}
3. **权限分级模型**: super_admin / hospital_admin / doctor / parent 四级
4. **数据过滤规则**: 超级管理员全量 / 医院管理员按unit / 医生按creator
5. **随访记录关联**: Patient → PatFoll, MAS专项 → MasFoll
6. **影像三类分类**: 骨龄图片 / 脊柱全长片 / 其他
7. **小程序数据隔离**: 通过 xcx_card(openid) 和 baby_flag 区分
8. **8种疾病子表字段**: 作为V2 Entity设计的参考
9. **ICD-10编码库**: 前端约2000条的诊断编码数据
10. **身高年龄感知校验**: 成人130-190cm / 儿童40-190cm

### 9.3 建议在V2中改进的方面

1. **JSON字段正规化**: 将 family_his, contacts, dia_trea_plan 等JSON Text字段拆分为独立关联表
2. **User.unit 改为外键**: 使用数据库级别的引用完整性
3. **统一认证方案**: 只用Sa-Token JWT，Web和小程序统一
4. **疾病子表抽象**: 使用接口/抽象类统一 modifyorAdd* 的模式，消除重复代码
5. **参数校验移入Controller层**: 使用Spring Validation注解
6. **文件存储统一S3**: 用MinIO替代本地磁盘+Nginx X-Accel-Redirect
7. **API版本化**: URL前缀 /api/v1/
8. **完善操作审计**: 使用AOP自动记录操作日志，而非散落在各处的手动调用
9. **分页统一**: 使用MyBatis-Plus Page对象
10. **前端async/await**: 替代回调模式，使用现代异步语法

