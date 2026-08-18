# 知情同意书模块 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 EKSJK 病例详情新增「知情同意书」只读 Tab，展示从 E路童萌同步的签署文件；病例列表新增「已签署」标签和「医生」列。

**Architecture:** 后端新增独立表 `informed_consent`（Entity/Mapper/Service/Controller 四层），同步服务占位实现（mock 数据 + 定时轮询 + 手动触发），病例列表通过批量查询知情同意书标记签署状态。前端新增 `InformedConsentList.vue` 只读组件 + CaseDetail Tab + CaseList 两列。

**Tech Stack:** Spring Boot 3.4 / MyBatis-Plus 3.5 / Sa-Token / Lombok / Vue 3 / Element Plus / Mockito（测试）

**Spec:** `docs/superpowers/specs/2026-08-18-informed-consent-design.md`

---

### Task 1: 数据库增量 SQL

**Files:**
- Create: `eksjk_v2/init-sql/28-informed-consent.sql`

- [ ] **Step 1: 创建 SQL 文件**

```sql
CREATE TABLE IF NOT EXISTS `informed_consent` (
  `id`               BIGINT AUTO_INCREMENT PRIMARY KEY,
  `patient_id`       BIGINT       NOT NULL COMMENT '本系统患者ID',
  `eltm_id`          VARCHAR(64)  DEFAULT NULL COMMENT 'E路童萌患者唯一标识',
  `external_file_id` VARCHAR(128) DEFAULT NULL COMMENT 'E路童萌外部文件ID',
  `file_name`        VARCHAR(255) NOT NULL COMMENT '文件名称',
  `oss_url`          VARCHAR(512) NOT NULL COMMENT 'OSS 文件地址',
  `sign_time`        DATETIME     DEFAULT NULL COMMENT '签署时间',
  `patient_name`     VARCHAR(64)  DEFAULT NULL COMMENT '患者姓名',
  `doctor_name`      VARCHAR(64)  DEFAULT NULL COMMENT '公众号端医生姓名',
  `source`           VARCHAR(32)  DEFAULT 'ELTM' COMMENT '来源',
  `sync_time`        DATETIME     DEFAULT NULL COMMENT '同步时间',
  `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `is_deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_eltm_id` (`eltm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知情同意书';
```

- [ ] **Step 2: 在本地数据库执行并验证**

Run: `mysql -u eksjk -peksjk123 -h localhost eksjk_db < eksjk_v2/init-sql/28-informed-consent.sql`
Expected: 无报错，表创建成功

Run: `mysql -u eksjk -peksjk123 -h localhost eksjk_db -e "DESC informed_consent;" 2>/dev/null | head -15`
Expected: 显示 13 列字段

---

### Task 2: Entity + Mapper

**Files:**
- Create: `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/entity/InformedConsent.java`
- Create: `eksjk_v2/eksjk-backend/eksjk-mapper/src/main/java/com/eksjk/mapper/InformedConsentMapper.java`

- [ ] **Step 1: 创建 Entity**

```java
package com.eksjk.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知情同意书实体
 *
 * @author eksjk
 */
@Data
@TableName("informed_consent")
public class InformedConsent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 本系统患者ID */
    private Long patientId;

    /** E路童萌患者唯一标识 */
    private String eltmId;

    /** E路童萌外部文件ID */
    private String externalFileId;

    /** 文件名称 */
    private String fileName;

    /** OSS 文件地址 */
    private String ossUrl;

    /** 签署时间 */
    private LocalDateTime signTime;

    /** 患者姓名 */
    private String patientName;

    /** 公众号端医生姓名 */
    private String doctorName;

    /** 来源 */
    private String source;

    /** 同步时间 */
    private LocalDateTime syncTime;

    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
```

- [ ] **Step 2: 创建 Mapper**

```java
package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.InformedConsent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知情同意书 Mapper
 *
 * @author eksjk
 */
@Mapper
public interface InformedConsentMapper extends BaseMapper<InformedConsent> {
}
```

- [ ] **Step 3: 编译验证**

Run: `cd eksjk_v2/eksjk-backend && mvn clean compile -pl eksjk-model,eksjk-mapper -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add eksjk_v2/init-sql/28-informed-consent.sql eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/entity/InformedConsent.java eksjk_v2/eksjk-backend/eksjk-mapper/src/main/java/com/eksjk/mapper/InformedConsentMapper.java
git commit -m "[case] 知情同意书表结构与实体"
```

---

### Task 3: VO + 列表查询 Service（TDD）

**Files:**
- Create: `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/vo/InformedConsentVO.java`
- Create: `eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/InformedConsentService.java`
- Create: `eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/InformedConsentServiceImpl.java`
- Create: `eksjk_v2/eksjk-backend/eksjk-service/src/test/java/com/eksjk/service/InformedConsentServiceTest.java`

- [ ] **Step 1: 创建 VO**

```java
package com.eksjk.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知情同意书视图对象
 *
 * @author eksjk
 */
@Data
public class InformedConsentVO {

    private String id;

    private String patientId;

    private String externalFileId;

    private String fileName;

    private LocalDateTime signTime;

    private String patientName;

    private String doctorName;

    private String source;

    private String ossUrl;
}
```

- [ ] **Step 2: 创建 Service 接口**

```java
package com.eksjk.service;

import com.eksjk.model.vo.InformedConsentVO;

import java.util.List;

/**
 * 知情同意书服务
 *
 * @author eksjk
 */
public interface InformedConsentService {

    /**
     * 按患者查询知情同意书列表
     *
     * @param patientId 患者ID（内部自增ID）
     */
    List<InformedConsentVO> listByPatient(Long patientId);
}
```

- [ ] **Step 3: 写失败的单元测试**

```java
package com.eksjk.service;

import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.vo.InformedConsentVO;
import com.eksjk.service.impl.InformedConsentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 知情同意书服务单元测试
 *
 * @author eksjk
 */
class InformedConsentServiceTest extends BaseServiceTest {

    @InjectMocks
    private InformedConsentServiceImpl informedConsentService;

    @Mock
    private InformedConsentMapper informedConsentMapper;

    @Nested
    @DisplayName("按患者查询列表")
    class ListByPatientTests {

        @Test
        @DisplayName("无记录应返回空列表")
        void shouldReturnEmptyWhenNoRecords() {
            when(informedConsentMapper.selectList(any())).thenReturn(List.of());

            List<InformedConsentVO> result = informedConsentService.listByPatient(1L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("有记录应返回VO并填充字段")
        void shouldReturnVoList() {
            InformedConsent consent = new InformedConsent();
            consent.setId(10L);
            consent.setPatientId(1L);
            consent.setExternalFileId("file_001");
            consent.setFileName("知情同意书_张三.pdf");
            consent.setSignTime(LocalDateTime.of(2026, 8, 1, 10, 30));
            consent.setPatientName("张三");
            consent.setDoctorName("张医生");
            consent.setSource("ELTM");
            consent.setOssUrl("https://mock-oss/consent/file_001.pdf");

            when(informedConsentMapper.selectList(any())).thenReturn(List.of(consent));

            List<InformedConsentVO> result = informedConsentService.listByPatient(1L);

            assertThat(result).hasSize(1);
            InformedConsentVO vo = result.get(0);
            assertThat(vo.getFileName()).isEqualTo("知情同意书_张三.pdf");
            assertThat(vo.getSignTime()).isEqualTo(consent.getSignTime());
            assertThat(vo.getSource()).isEqualTo("ELTM");
            assertThat(vo.getDoctorName()).isEqualTo("张医生");
            assertThat(vo.getPatientName()).isEqualTo("张三");
        }
    }
}
```

- [ ] **Step 4: 运行测试确认失败**

Run: `cd eksjk_v2/eksjk-backend && mvn test -pl eksjk-service -am -Dtest=InformedConsentServiceTest`
Expected: 编译失败或测试失败（InformedConsentServiceImpl 不存在）

- [ ] **Step 5: 实现 Service**

```java
package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.vo.InformedConsentVO;
import com.eksjk.service.InformedConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知情同意书服务实现
 *
 * @author eksjk
 */
@Service
@RequiredArgsConstructor
public class InformedConsentServiceImpl implements InformedConsentService {

    private final InformedConsentMapper informedConsentMapper;

    @Override
    public List<InformedConsentVO> listByPatient(Long patientId) {
        List<InformedConsent> consents = informedConsentMapper.selectList(
                new LambdaQueryWrapper<InformedConsent>()
                        .eq(InformedConsent::getPatientId, patientId)
                        .orderByDesc(InformedConsent::getSignTime));

        return consents.stream().map(c -> {
            InformedConsentVO vo = new InformedConsentVO();
            BeanUtils.copyProperties(c, vo);
            vo.setId(String.valueOf(c.getId()));
            return vo;
        }).toList();
    }
}
```

- [ ] **Step 6: 运行测试确认通过**

Run: `cd eksjk_v2/eksjk-backend && mvn test -pl eksjk-service -am -Dtest=InformedConsentServiceTest`
Expected: PASS（2 个测试通过）

- [ ] **Step 7: Commit**

```bash
git add eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/vo/InformedConsentVO.java eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/InformedConsentService.java eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/InformedConsentServiceImpl.java eksjk_v2/eksjk-backend/eksjk-service/src/test/java/com/eksjk/service/InformedConsentServiceTest.java
git commit -m "[case] 知情同意书列表查询服务"
```

---

### Task 4: 同步服务（占位实现，TDD）

**Files:**
- Create: `eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/InformedConsentSyncService.java`
- Create: `eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/InformedConsentSyncServiceImpl.java`
- Create: `eksjk_v2/eksjk-backend/eksjk-service/src/test/java/com/eksjk/service/InformedConsentSyncServiceTest.java`

- [ ] **Step 1: 创建同步服务接口**

```java
package com.eksjk.service;

import java.util.Map;

/**
 * 知情同意书外部同步服务
 *
 * @author eksjk
 */
public interface InformedConsentSyncService {

    /**
     * 从 E路童萌 同步知情同意书记录
     * 当前为占位实现（mock 数据），对接真实接口后替换
     */
    Map<String, Object> syncFromExternal();
}
```

- [ ] **Step 2: 写失败的单元测试**

```java
package com.eksjk.service;

import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.impl.InformedConsentSyncServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 知情同意书同步服务单元测试
 *
 * @author eksjk
 */
class InformedConsentSyncServiceTest extends BaseServiceTest {

    @InjectMocks
    private InformedConsentSyncServiceImpl syncService;

    @Mock
    private InformedConsentMapper informedConsentMapper;

    @Mock
    private PatientMapper patientMapper;

    @Nested
    @DisplayName("同步 mock 数据")
    class SyncTests {

        @Test
        @DisplayName("eltm_id 未匹配到患者应计入失败")
        void shouldCountFailedWhenPatientNotFound() {
            when(patientMapper.selectOne(any())).thenReturn(null);

            Map<String, Object> result = syncService.syncFromExternal();

            assertThat(result.get("total")).isEqualTo(1);
            assertThat(result.get("failedCount")).isEqualTo(1);
            verify(informedConsentMapper, never()).insert(any());
        }

        @Test
        @DisplayName("匹配到患者且外部文件不存在应新增")
        void shouldInsertNewConsent() {
            Patient patient = new Patient();
            patient.setId(1L);
            patient.setEltmId("eltm_mock_001");
            when(patientMapper.selectOne(any())).thenReturn(patient);
            when(informedConsentMapper.selectOne(any())).thenReturn(null);

            Map<String, Object> result = syncService.syncFromExternal();

            assertThat(result.get("newCount")).isEqualTo(1);
            verify(informedConsentMapper, times(1)).insert(any(InformedConsent.class));
        }

        @Test
        @DisplayName("外部文件已存在应更新而非重复插入")
        void shouldUpdateExistingConsent() {
            Patient patient = new Patient();
            patient.setId(1L);
            when(patientMapper.selectOne(any())).thenReturn(patient);
            InformedConsent existing = new InformedConsent();
            existing.setId(99L);
            when(informedConsentMapper.selectOne(any())).thenReturn(existing);

            Map<String, Object> result = syncService.syncFromExternal();

            assertThat(result.get("updatedCount")).isEqualTo(1);
            verify(informedConsentMapper, never()).insert(any());
            verify(informedConsentMapper, times(1)).updateById(any(InformedConsent.class));
        }
    }
}
```

- [ ] **Step 3: 运行测试确认失败**

Run: `cd eksjk_v2/eksjk-backend && mvn test -pl eksjk-service -am -Dtest=InformedConsentSyncServiceTest`
Expected: 编译失败（InformedConsentSyncServiceImpl 不存在）

- [ ] **Step 4: 实现同步服务（占位 mock + upsert + 定时任务）**

```java
package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.entity.Patient;
import com.eksjk.service.InformedConsentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知情同意书外部同步服务实现（占位）
 * <p>
 * 对接 E路童萌 前先以 mock 数据跑通完整链路：
 * mock 记录按 eltm_id 匹配本系统患者，按 external_file_id 幂等 upsert。
 * 真实对接时替换 fetchFromExternal() 为 HTTP 调用即可。
 * </p>
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InformedConsentSyncServiceImpl implements InformedConsentSyncService {

    private final InformedConsentMapper informedConsentMapper;
    private final PatientMapper patientMapper;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> syncFromExternal() {
        List<Map<String, String>> records = fetchFromExternal();
        log.info("开始同步知情同意书: 共 {} 条", records.size());

        int newCount = 0;
        int updatedCount = 0;
        int failedCount = 0;

        for (Map<String, String> rec : records) {
            try {
                String eltmId = rec.get("eltmId");
                Patient patient = patientMapper.selectOne(
                        new LambdaQueryWrapper<Patient>().eq(Patient::getEltmId, eltmId));
                if (patient == null) {
                    log.warn("知情同意书未匹配到患者: eltmId={}", eltmId);
                    failedCount++;
                    continue;
                }

                InformedConsent existing = informedConsentMapper.selectOne(
                        new LambdaQueryWrapper<InformedConsent>()
                                .eq(InformedConsent::getExternalFileId, rec.get("fileId")));
                if (existing == null) {
                    InformedConsent consent = buildConsent(patient, rec);
                    informedConsentMapper.insert(consent);
                    newCount++;
                } else {
                    applyFields(existing, rec);
                    existing.setSyncTime(LocalDateTime.now());
                    informedConsentMapper.updateById(existing);
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("同步知情同意书单条失败", e);
                failedCount++;
            }
        }

        log.info("知情同意书同步完成: total={}, new={}, updated={}, failed={}",
                records.size(), newCount, updatedCount, failedCount);
        Map<String, Object> result = new HashMap<>();
        result.put("total", records.size());
        result.put("newCount", newCount);
        result.put("updatedCount", updatedCount);
        result.put("failedCount", failedCount);
        return result;
    }

    /** 每天凌晨 2:00 定时同步 */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledSync() {
        log.info("定时任务触发知情同意书同步");
        syncFromExternal();
    }

    /**
     * 从 E路童萌 拉取签署记录。
     * <p>
     * 占位实现：返回 mock 数据。真实对接时改为调用
     * {@code eksjk.informed-consent.sync-url} 并解析响应（含 OAuth/Token 认证）。
     * </p>
     */
    private List<Map<String, String>> fetchFromExternal() {
        return List.of(
                Map.of(
                        "eltmId", "eltm_mock_001",
                        "fileId", "file_mock_001",
                        "fileName", "知情同意书_张三.pdf",
                        "ossUrl", "https://mock-oss.example.com/consent/file_mock_001.pdf",
                        "signTime", "2026-08-01 10:30:00",
                        "patientName", "张三",
                        "doctorName", "张医生"),
                Map.of(
                        "eltmId", "eltm_mock_002",
                        "fileId", "file_mock_002",
                        "fileName", "知情同意书_李四.pdf",
                        "ossUrl", "https://mock-oss.example.com/consent/file_mock_002.pdf",
                        "signTime", "2026-08-02 14:00:00",
                        "patientName", "李四",
                        "doctorName", "王医生")
        );
    }

    private InformedConsent buildConsent(Patient patient, Map<String, String> rec) {
        InformedConsent consent = new InformedConsent();
        consent.setPatientId(patient.getId());
        consent.setEltmId(patient.getEltmId());
        consent.setSource("ELTM");
        consent.setCreateTime(LocalDateTime.now());
        consent.setSyncTime(LocalDateTime.now());
        applyFields(consent, rec);
        return consent;
    }

    private void applyFields(InformedConsent consent, Map<String, String> rec) {
        consent.setExternalFileId(rec.get("fileId"));
        consent.setFileName(rec.get("fileName"));
        consent.setOssUrl(rec.get("ossUrl"));
        consent.setSignTime(parseTime(rec.get("signTime")));
        consent.setPatientName(rec.get("patientName"));
        consent.setDoctorName(rec.get("doctorName"));
    }

    private LocalDateTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return null;
        try {
            if (timeStr.contains("T")) {
                return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (timeStr.contains(" ")) {
                return LocalDateTime.parse(timeStr, TIME_FORMATTER);
            }
            return LocalDateTime.parse(timeStr + " 00:00:00", TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("解析签署时间失败: {}", timeStr);
            return null;
        }
    }
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `cd eksjk_v2/eksjk-backend && mvn test -pl eksjk-service -am -Dtest=InformedConsentSyncServiceTest`
Expected: PASS（3 个测试通过）

- [ ] **Step 6: Commit**

```bash
git add eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/InformedConsentSyncService.java eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/InformedConsentSyncServiceImpl.java eksjk_v2/eksjk-backend/eksjk-service/src/test/java/com/eksjk/service/InformedConsentSyncServiceTest.java
git commit -m "[case] 知情同意书同步服务（占位实现+定时任务）"
```

---

### Task 5: Controller

**Files:**
- Create: `eksjk_v2/eksjk-backend/eksjk-web/src/main/java/com/eksjk/web/controller/InformedConsentController.java`

- [ ] **Step 1: 创建 Controller**

```java
package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.vo.InformedConsentVO;
import com.eksjk.service.InformedConsentService;
import com.eksjk.service.InformedConsentSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知情同意书 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/informed-consent")
@RequiredArgsConstructor
@SaCheckLogin
public class InformedConsentController {

    private final InformedConsentService informedConsentService;
    private final InformedConsentSyncService informedConsentSyncService;

    /**
     * 按患者查询知情同意书列表
     */
    @GetMapping("/list-by-patient")
    public R<List<InformedConsentVO>> listByPatient(@RequestParam("patientId") String patientId) {
        long id = HashidsUtil.decode(patientId);
        return R.ok(informedConsentService.listByPatient(id));
    }

    /**
     * 手动触发从 E路童萌 同步知情同意书（管理端）
     */
    @PostMapping("/sync")
    @SaCheckRole({"super_admin", "hospital_admin"})
    public R<Map<String, Object>> sync() {
        return R.ok(informedConsentSyncService.syncFromExternal());
    }

    /**
     * 查询同步状态（最近同步时间与总记录数）
     */
    @GetMapping("/sync-status")
    public R<Map<String, Object>> syncStatus() {
        Map<String, Object> status = informedConsentService.getSyncStatus();
        return R.ok(status);
    }
}
```

- [ ] **Step 2: 在 Service 接口补充 getSyncStatus 并在 Impl 实现**

在 `InformedConsentService` 接口新增方法：

```java
import java.util.Map;

Map<String, Object> getSyncStatus();
```

在 `InformedConsentServiceImpl` 新增实现（先添加 `com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper` 和 `java.util.Map`、`java.util.HashMap` 的 import）：

```java
@Override
public Map<String, Object> getSyncStatus() {
    InformedConsent latest = informedConsentMapper.selectOne(
            new LambdaQueryWrapper<InformedConsent>()
                    .orderByDesc(InformedConsent::getSyncTime)
                    .last("LIMIT 1"));
    Long total = informedConsentMapper.selectCount(null);

    Map<String, Object> result = new HashMap<>();
    result.put("lastSyncTime", latest != null ? latest.getSyncTime() : null);
    result.put("totalCount", total != null ? total : 0L);
    return result;
}
```

- [ ] **Step 3: 编译 + 全量后端测试**

Run: `cd eksjk_v2/eksjk-backend && mvn test -pl eksjk-web -am -DskipTests=false -q`
Expected: BUILD SUCCESS，全部测试通过

- [ ] **Step 4: Commit**

```bash
git add eksjk_v2/eksjk-backend/eksjk-web/src/main/java/com/eksjk/web/controller/InformedConsentController.java eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/InformedConsentService.java eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/InformedConsentServiceImpl.java
git commit -m "[case] 知情同意书接口（列表/同步/同步状态）"
```

---

### Task 6: 病例列表「已签署」标记 + 医生列（TDD）

**Files:**
- Modify: `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/vo/PatientVO.java`
- Modify: `eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/PatientServiceImpl.java`
- Modify: `eksjk_v2/eksjk-backend/eksjk-service/src/test/java/com/eksjk/service/PatientServiceTest.java`

- [ ] **Step 1: PatientVO 新增字段**

在 `PatientVO`（`eksjk-model/.../vo/PatientVO.java`）的字段区（如 `impPer` 字段附近）新增：

```java
    /** 知情同意书状态: signed/not_signed */
    private String informedConsentStatus;

    /** 公众号端医生姓名 */
    private String doctorName;
```

- [ ] **Step 2: PatientServiceImpl 注入 Mapper + 批量查询**

`PatientServiceImpl` 在现有 `final` 字段区（`private final PatientGeneticsMapper patientGeneticsMapper;` 之后）新增：

```java
    private final InformedConsentMapper informedConsentMapper;
```

import 已含 `com.eksjk.mapper.*` 和 `com.eksjk.model.entity.*`，无需新增。

在 `queryPage` 方法中 `latestFollowUpMap` 计算之后、`convertToVO` 调用之前，新增知情同意书批量查询：

```java
        // ELTM列表：批量查询各患者的最新知情同意书（标记签署状态 + 公众号端医生）
        final Map<Long, InformedConsent> latestConsentMap;
        if (!result.getRecords().isEmpty()) {
            List<Long> patientIds = result.getRecords().stream()
                    .map(Patient::getId)
                    .toList();
            List<InformedConsent> consents = informedConsentMapper.selectList(
                    new LambdaQueryWrapper<InformedConsent>()
                            .in(InformedConsent::getPatientId, patientIds)
                            .orderByAsc(InformedConsent::getPatientId)
                            .orderByDesc(InformedConsent::getSignTime));
            latestConsentMap = consents.stream().collect(Collectors.toMap(
                    InformedConsent::getPatientId,
                    c -> c,
                    (existing, replacement) -> existing));
        } else {
            latestConsentMap = Collections.emptyMap();
        }
```

注意：`collect(Collectors.toMap(...))` 中 `(existing, replacement) -> existing` 是保留每组第一条（即 sign_time 最新的一条，因为已按 sign_time 倒序）。

将 `convertToVO` 调用改为：

```java
        List<PatientVO> voList = result.getRecords().stream()
                .map(p -> convertToVO(p, latestFollowUpMap, latestConsentMap))
                .toList();
```

- [ ] **Step 3: convertToVO 填充新字段**

将现有 `private PatientVO convertToVO(Patient patient, Map<Long, PatientFollowUp> latestFollowUpMap)` 重载改为三参版本，并新增两参桥接：

```java
    private PatientVO convertToVO(Patient patient) {
        return convertToVO(patient, Collections.emptyMap(), Collections.emptyMap());
    }

    private PatientVO convertToVO(Patient patient, Map<Long, PatientFollowUp> latestFollowUpMap) {
        return convertToVO(patient, latestFollowUpMap, Collections.emptyMap());
    }

    private PatientVO convertToVO(Patient patient, Map<Long, PatientFollowUp> latestFollowUpMap,
                                  Map<Long, InformedConsent> latestConsentMap) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo, "id");
        vo.setId(HashidsUtil.encode(patient.getId()));
        vo.setDisClassName(DIS_CLASS_MAP.getOrDefault(patient.getDisClass(), "未知"));

        // 非ELTM病种视为"已诊断"
        if (patient.getDisClass() != null && !"10000007".equals(patient.getDisClass())) {
            vo.setDiagnosisStatus("diagnosed");
        }

        // ELTM：填充最新随访的审核发放信息
        if (latestFollowUpMap != null && !latestFollowUpMap.isEmpty()) {
            PatientFollowUp latestFollowUp = latestFollowUpMap.get(patient.getId());
            if (latestFollowUp != null) {
                vo.setFollowUpAuditStatus(latestFollowUp.getAuditStatus());
                vo.setFollowUpAuditBy(latestFollowUp.getAuditBy());
                vo.setFollowUpAuditTime(latestFollowUp.getAuditTime());
                vo.setFollowUpReleaseBy(latestFollowUp.getReleaseBy());
                vo.setFollowUpReleaseTime(latestFollowUp.getReleaseTime());
            }
        }

        // 知情同意书：有记录标记 signed，doctorName 取最近一条的公众号端医生
        vo.setInformedConsentStatus("not_signed");
        if (latestConsentMap != null && !latestConsentMap.isEmpty()) {
            InformedConsent consent = latestConsentMap.get(patient.getId());
            if (consent != null) {
                vo.setInformedConsentStatus("signed");
                vo.setDoctorName(consent.getDoctorName());
            }
        }

        // 性别名称
        if ("1".equals(patient.getSex())) {
            vo.setSexName("男");
        } else if ("2".equals(patient.getSex())) {
            vo.setSexName("女");
        } else {
            vo.setSexName("未知");
        }

        return vo;
    }
```

- [ ] **Step 4: 更新 PatientServiceTest（补 @Mock + 新用例）**

在 `PatientServiceTest` 类字段区新增（`@Mock private PatientGeneticsMapper ...` 之后若有，否则在 `followUpMapper` 之后）：

```java
    @Mock
    private InformedConsentMapper informedConsentMapper;
```

import 需新增：`import com.eksjk.model.entity.InformedConsent;`

在 `QueryPageTests` 内新增测试：

```java
        @Test
        @DisplayName("患者有知情同意书记录应标记已签署并带出医生")
        void shouldMarkSignedWhenConsentExists() {
            PatientQueryDTO queryDTO = createPatientQueryDTO();
            Patient p1 = createPatient(1L, DIS_CLASS_DSD, "测试患者");
            Page<Patient> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(p1));
            mockPage.setTotal(1);

            when(patientMapper.selectPage(any(), any())).thenReturn(mockPage);
            when(informedConsentMapper.selectList(any())).thenReturn(
                    Arrays.asList(createConsent(1L, "张医生")));

            PageResult<PatientVO> result = patientService.queryPage(queryDTO);

            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getInformedConsentStatus()).isEqualTo("signed");
            assertThat(result.getRecords().get(0).getDoctorName()).isEqualTo("张医生");
        }

        @Test
        @DisplayName("患者无知情同意书应标记未签署")
        void shouldMarkNotSignedWhenNoConsent() {
            PatientQueryDTO queryDTO = createPatientQueryDTO();
            Patient p1 = createPatient(1L, DIS_CLASS_DSD, "测试患者");
            Page<Patient> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(p1));
            mockPage.setTotal(1);

            when(patientMapper.selectPage(any(), any())).thenReturn(mockPage);
            when(informedConsentMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());

            PageResult<PatientVO> result = patientService.queryPage(queryDTO);

            assertThat(result.getRecords().get(0).getInformedConsentStatus()).isEqualTo("not_signed");
        }
```

并在 `PatientServiceTest` 内新增私有工厂方法（类内任意位置）：

```java
    private InformedConsent createConsent(Long patientId, String doctorName) {
        InformedConsent consent = new InformedConsent();
        consent.setId(1L);
        consent.setPatientId(patientId);
        consent.setExternalFileId("file_001");
        consent.setFileName("知情同意书.pdf");
        consent.setDoctorName(doctorName);
        consent.setSource("ELTM");
        consent.setSignTime(LocalDateTime.now());
        return consent;
    }
```

- [ ] **Step 5: 运行测试确认通过**

Run: `cd eksjk_v2/eksjk-backend && mvn test -pl eksjk-service -am -Dtest=PatientServiceTest`
Expected: PASS（新增用例 + 原有用例全通过）

- [ ] **Step 6: Commit**

```bash
git add eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/vo/PatientVO.java eksjk_v2/eksjk-backend/eksjk-service/src/main/java/com/eksjk/service/impl/PatientServiceImpl.java eksjk_v2/eksjk-backend/eksjk-service/src/test/java/com/eksjk/service/PatientServiceTest.java
git commit -m "[case] 病例列表知情同意书标记与医生列数据"
```

---

### Task 7: 后端配置

**Files:**
- Modify: `eksjk_v2/eksjk-backend/eksjk-web/src/main/resources/application.yml`
- Modify: `eksjk_v2/eksjk-backend/eksjk-web/src/main/resources/application-dev.yml`

- [ ] **Step 1: application.yml 增加占位配置**

在文件末尾 `eksjk.upload` 配置块之后追加：

```yaml
  # 知情同意书外部同步（占位地址，对接 E路童萌 后替换）
  informed-consent:
    sync-url: http://36.26.56.94:8026/datamain/getconsent
    source: ELTM
```

注意 YAML 缩进：`informed-consent` 与 `upload` 同级（都在 `eksjk:` 下）。

- [ ] **Step 2: application-dev.yml 增加同名配置**

在 `eksjk:` 块内（`eltm` 配置附近）追加：

```yaml
  informed-consent:
    sync-url: http://36.26.56.94:8026/datamain/getconsent
    source: ELTM
```

- [ ] **Step 3: 编译验证**

Run: `cd eksjk_v2/eksjk-backend && mvn compile -pl eksjk-web -am -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add eksjk_v2/eksjk-backend/eksjk-web/src/main/resources/application.yml eksjk_v2/eksjk-backend/eksjk-web/src/main/resources/application-dev.yml
git commit -m "[case] 知情同意书同步配置"
```

---

### Task 8: 前端 API 封装

**Files:**
- Create: `eksjk_v2/eksjk-frontend/src/api/informedConsent.js`

- [ ] **Step 1: 创建 API 文件**

```js
/**
 * 知情同意书 API 接口
 */
import request from '@/utils/request'

/**
 * 按患者查询知情同意书列表
 * @param {string} patientId - 患者ID（Hashids 编码）
 */
export function getInformedConsentList(patientId) {
  return request({
    url: '/api/informed-consent/list-by-patient',
    method: 'get',
    params: { patientId }
  })
}

/**
 * 手动触发同步知情同意书
 */
export function syncInformedConsent() {
  return request({
    url: '/api/informed-consent/sync',
    method: 'post'
  })
}
```

- [ ] **Step 2: Commit**

```bash
git add eksjk_v2/eksjk-frontend/src/api/informedConsent.js
git commit -m "[case] 知情同意书前端 API"
```

---

### Task 9: InformedConsentList 组件

**Files:**
- Create: `eksjk_v2/eksjk-frontend/src/views/case/components/InformedConsentList.vue`

- [ ] **Step 1: 创建组件（只读列表，参照遗传学检查 report-section 样式）**

```vue
<template>
  <div class="informed-consent-list">
    <div class="tab-pane-header">
      <el-tag type="info" size="small" effect="plain">来源：E路童萌</el-tag>
    </div>

    <!-- 文件列表 -->
    <div v-if="files.length > 0" class="consent-section">
      <div v-for="file in files" :key="file.id" class="consent-item">
        <el-icon><Document /></el-icon>
        <div class="consent-info">
          <div class="consent-name" :title="file.fileName">{{ file.fileName }}</div>
          <div class="consent-meta">
            <span>签署时间：{{ formatTime(file.signTime) }}</span>
            <span v-if="file.patientName">患者：{{ file.patientName }}</span>
            <span v-if="file.doctorName">医生：{{ file.doctorName }}</span>
          </div>
        </div>
        <span class="consent-source">E路童萌</span>
        <el-button link type="primary" size="small" @click="downloadFile(file)">查看/下载</el-button>
      </div>
    </div>
    <el-empty v-else-if="!loading" description="暂无知情同意书" :image-size="60" />

    <!-- 提示：不能手动上传 -->
    <el-alert v-if="files.length > 0" title="知情同意书由 E路童萌 微信端同步，仅支持查看，不可手动上传"
              type="info" :closable="false" show-icon style="margin-top:12px" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { getInformedConsentList } from '@/api/informedConsent'

const props = defineProps({
  patientId: { type: String, default: '' }
})

const files = ref([])
const loading = ref(false)

onMounted(() => { if (props.patientId) loadList() })

watch(() => props.patientId, (val) => { if (val) loadList() })

async function loadList() {
  loading.value = true
  try {
    const res = await getInformedConsentList(props.patientId)
    files.value = res.data || []
  } catch (error) {
    console.error('加载知情同意书失败', error)
  } finally {
    loading.value = false
  }
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function downloadFile(file) {
  if (file && file.ossUrl) window.open(file.ossUrl, '_blank')
}
</script>

<style scoped>
.tab-pane-header { display: flex; justify-content: flex-end; margin-bottom: 12px; }
.consent-section { margin-bottom: 12px; }
.consent-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 8px;
}
.consent-item .el-icon { color: #409eff; font-size: 18px; }
.consent-info { flex: 1; overflow: hidden; }
.consent-name {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.consent-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.consent-source {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add eksjk_v2/eksjk-frontend/src/views/case/components/InformedConsentList.vue
git commit -m "[case] 知情同意书列表组件"
```

---

### Task 10: CaseDetail 新增 Tab

**Files:**
- Modify: `eksjk_v2/eksjk-frontend/src/views/case/CaseDetail.vue`

- [ ] **Step 1: tabList 追加知情同意书**

在 `tabList` computed（约 827-835 行）末尾追加：

```js
  { name: 'ghAdverseEvent', label: 'GH不良事件' },
  { name: 'informedConsent', label: '知情同意书' }
```

- [ ] **Step 2: 异步加载组件**

在 `defineAsyncComponent` 声明区（约 796-803 行）新增：

```js
const InformedConsentList = defineAsyncComponent(() => import('@/views/case/components/InformedConsentList.vue'))
```

- [ ] **Step 3: 新增 el-tab-pane**

在「GH不良事件」tab-pane（约 762-769 行）之后、`</el-tabs>` 之前新增：

```html
        <!-- Tab 8：知情同意书 -->
        <el-tab-pane label="知情同意书" name="informedConsent" lazy>
          <InformedConsentList :patient-id="patientId" />
        </el-tab-pane>
```

- [ ] **Step 4: Commit**

```bash
git add eksjk_v2/eksjk-frontend/src/views/case/CaseDetail.vue
git commit -m "[case] 病例详情新增知情同意书 Tab"
```

---

### Task 11: CaseList 新增两列

**Files:**
- Modify: `eksjk_v2/eksjk-frontend/src/views/case/CaseList.vue`

- [ ] **Step 1: 新增「知情同意书」和「医生」列**

在「上传时间」列（约 99-103 行）之后、操作列之前新增：

```html
      <el-table-column label="知情同意书" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.informedConsentStatus === 'signed'" type="success" size="small">已签署</el-tag>
          <span v-else style="color:#c0c4cc">未签署</span>
        </template>
      </el-table-column>
      <el-table-column prop="doctorName" label="医生" width="100" show-overflow-tooltip />
```

- [ ] **Step 2: Commit**

```bash
git add eksjk_v2/eksjk-frontend/src/views/case/CaseList.vue
git commit -m "[case] 病例列表新增知情同意书与医生列"
```

---

### Task 12: 全量构建与本地联调

**Files:** 无（验证）

- [ ] **Step 1: 后端全量测试**

Run: `cd eksjk_v2/eksjk-backend && mvn clean package -pl eksjk-web -am -DskipTests=false -q`
Expected: BUILD SUCCESS，全部测试通过

- [ ] **Step 2: 前端构建**

Run: `cd eksjk_v2/eksjk-frontend && npm run build`
Expected: 构建成功，`dist/` 生成

- [ ] **Step 3: 本地起服务（若未运行）**

Run（后端）: `cd eksjk_v2/eksjk-backend && mvn spring-boot:run -pl eksjk-web -Dspring-boot.run.profiles=dev`（后台）
Run（前端）: `cd eksjk_v2/eksjk-frontend && npm run dev`（后台）

- [ ] **Step 4: 插入一条匹配的 ELTM 患者 + 触发同步**

先确认有一名 eltm_id 为 `eltm_mock_001` 的患者（mock 同步依赖它）：

Run: `mysql -u eksjk -peksjk123 -h localhost eksjk_db -e "UPDATE datamain_patient SET eltm_id='eltm_mock_001' WHERE id=(SELECT id FROM (SELECT MIN(id) id FROM datamain_patient WHERE del_flg='1') t);" 2>/dev/null`

Run: `curl -X POST http://localhost:8080/api/informed-consent/sync -H "satoken: <替换为登录token>"`（或用浏览器登录后手动触发）
Expected: 返回 `{ "total": 2, "newCount": 2, "failedCount": 0 }`（第二条 eltm_mock_002 无患者则 failed=1）

- [ ] **Step 5: 浏览器验证**

1. 登录 `http://localhost:3000`
2. 进入 ELTM 病例列表 → 确认「知情同意书」列显示绿色「已签署」、部分行「医生」列有值
3. 打开匹配的患者详情 → 「知情同意书」Tab → 确认文件列表展示（文件名/签署时间/患者/来源/查看下载）
4. 确认 Tab 无上传按钮

- [ ] **Step 6: 全部提交完成确认**

Run: `git log --oneline -8`
Expected: 看到本功能各 Task 的提交

---

## Self-Review 记录

**1. Spec 覆盖：**
- Tab 位置（GH 之后）→ Task 10 ✅
- 只读列表（无上传）→ Task 9（组件无 el-upload）✅
- 列表字段（文件名/签署时间/患者姓名/来源/操作）→ Task 9 ✅
- 后端接口 `/api/informed-consent/list-by-patient` → Task 5 ✅
- 对接留占位（mock + 可配 URL）→ Task 4 + Task 7 ✅
- 定时轮询 + 手动触发 → Task 4（@Scheduled + POST /sync）✅
- 已签署标记 → Task 6 ✅
- 病例列表「知情同意书」「医生」两列 → Task 11 ✅
- eltm_id 关联 → Task 4（PatientMapper 按 eltmId 匹配）✅

**2. 占位符扫描：** 无 TBD/TODO；Step 命令中 `<替换为登录token>` 是运行时操作说明而非计划占位。

**3. 类型一致性：** `InformedConsentVO` 字段、`InformedConsent` 实体字段、`PatientVO` 新增字段在前后任务中一致；Service 方法签名 `listByPatient(Long)` / `syncFromExternal()` / `getSyncStatus()` 各任务引用一致。
