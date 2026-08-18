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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        @DisplayName("eltm_id 均未匹配到患者应全部计入失败")
        void shouldCountFailedWhenPatientNotFound() {
            when(patientMapper.selectOne(any())).thenReturn(null);

            Map<String, Object> result = syncService.syncFromExternal();

            assertThat(result.get("total")).isEqualTo(2);
            assertThat(result.get("newCount")).isEqualTo(0);
            assertThat(result.get("updatedCount")).isEqualTo(0);
            assertThat(result.get("failedCount")).isEqualTo(2);
            verify(informedConsentMapper, never()).insert(any(InformedConsent.class));
        }

        @Test
        @DisplayName("匹配到患者且外部文件均不存在应新增")
        void shouldInsertNewConsents() {
            Patient patient = new Patient();
            patient.setId(1L);
            patient.setEltmId("eltm_mock_001");
            when(patientMapper.selectOne(any())).thenReturn(patient);
            when(informedConsentMapper.selectOne(any())).thenReturn(null);

            Map<String, Object> result = syncService.syncFromExternal();

            assertThat(result.get("total")).isEqualTo(2);
            assertThat(result.get("newCount")).isEqualTo(2);
            assertThat(result.get("updatedCount")).isEqualTo(0);
            assertThat(result.get("failedCount")).isEqualTo(0);
            verify(informedConsentMapper, times(2)).insert(any(InformedConsent.class));
            verify(informedConsentMapper, never()).updateById(any(InformedConsent.class));
        }

        @Test
        @DisplayName("同一条记录既有新增又有已存在时应分别计数")
        void shouldMixNewAndUpdated() {
            Patient patient = new Patient();
            patient.setId(1L);
            when(patientMapper.selectOne(any())).thenReturn(patient);
            InformedConsent existing = new InformedConsent();
            existing.setId(99L);
            // 第一条记录外部文件不存在 -> 新增；第二条记录外部文件已存在 -> 更新
            when(informedConsentMapper.selectOne(any())).thenReturn(null, existing);

            Map<String, Object> result = syncService.syncFromExternal();

            assertThat(result.get("total")).isEqualTo(2);
            assertThat(result.get("newCount")).isEqualTo(1);
            assertThat(result.get("updatedCount")).isEqualTo(1);
            assertThat(result.get("failedCount")).isEqualTo(0);
            verify(informedConsentMapper, times(1)).insert(any(InformedConsent.class));
            verify(informedConsentMapper, times(1)).updateById(any(InformedConsent.class));
        }
    }
}
