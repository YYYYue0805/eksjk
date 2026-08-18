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
