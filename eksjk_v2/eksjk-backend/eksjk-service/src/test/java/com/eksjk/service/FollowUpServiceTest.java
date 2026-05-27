package com.eksjk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.MasFollowUpMapper;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.dto.FollowUpDTO;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.vo.FollowUpVO;
import com.eksjk.service.impl.FollowUpServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 随访管理服务单元测试
 *
 * @author eksjk
 */
class FollowUpServiceTest extends BaseServiceTest {

    @InjectMocks
    private FollowUpServiceImpl followUpService;

    @Mock
    private PatientFollowUpMapper followUpMapper;

    @Mock
    private MasFollowUpMapper masFollowUpMapper;

    @Mock
    private PatientMapper patientMapper;

    // ==================== 查询随访列表测试 ====================

    @Nested
    @DisplayName("查询患者随访列表")
    class ListByPatientIdTests {

        @Test
        @DisplayName("有随访记录的患者应返回列表")
        void shouldReturnFollowUpList() {
            // Given
            PatientFollowUp f1 = createFollowUp(1L, 100L);
            PatientFollowUp f2 = createFollowUp(2L, 100L);

            when(followUpMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(f1, f2));

            // When
            List<FollowUpVO> result = followUpService.listByPatientId(100L);

            // Then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("无随访记录的患者应返回空列表")
        void shouldReturnEmptyListForNoFollowUps() {
            // Given
            when(followUpMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(List.of());

            // When
            List<FollowUpVO> result = followUpService.listByPatientId(999L);

            // Then
            assertThat(result).isEmpty();
        }
    }

    // ==================== 获取随访详情测试 ====================

    @Nested
    @DisplayName("获取随访详情")
    class GetDetailTests {

        @Test
        @DisplayName("存在的随访记录应返回详情")
        void shouldReturnFollowUpDetail() {
            // Given
            PatientFollowUp followUp = createFollowUp(1L, 100L);
            Patient patient = createPatient(100L, DIS_CLASS_DSD, "测试患者");

            when(followUpMapper.selectById(1L)).thenReturn(followUp);
            when(patientMapper.selectById(100L)).thenReturn(patient);

            // When
            FollowUpVO result = followUpService.getDetail(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getHt()).isEqualTo("125.0");
        }

        @Test
        @DisplayName("不存在的随访记录应抛出异常")
        void shouldThrowExceptionForNonExistent() {
            // Given
            when(followUpMapper.selectById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> followUpService.getDetail(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("已删除的随访记录应抛出异常")
        void shouldThrowExceptionForDeleted() {
            // Given
            PatientFollowUp followUp = createFollowUp(1L, 100L);
            followUp.setDelFlg("0");
            when(followUpMapper.selectById(1L)).thenReturn(followUp);

            // When & Then
            assertThatThrownBy(() -> followUpService.getDetail(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== 新增随访记录测试 ====================

    @Nested
    @DisplayName("新增随访记录")
    class CreateTests {

        @Test
        @DisplayName("有效数据应成功创建随访记录")
        void shouldCreateFollowUpSuccessfully() {
            // Given
            Patient patient = createPatient(100L, DIS_CLASS_DSD, "测试患者");
            FollowUpDTO dto = createFollowUpDTO("test_encoded_id");

            try (MockedStatic<HashidsUtil> hashMock = mockStatic(HashidsUtil.class);
                 MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {

                hashMock.when(() -> HashidsUtil.decode("test_encoded_id")).thenReturn(100L);
                secMock.when(SecurityUtil::getCurrentUsername).thenReturn("doctor_1");

                when(patientMapper.selectById(100L)).thenReturn(patient);
                when(followUpMapper.insert(any(PatientFollowUp.class))).thenReturn(1);

                // When & Then
                assertThatCode(() -> followUpService.create(dto)).doesNotThrowAnyException();
                verify(followUpMapper, times(1)).insert(any(PatientFollowUp.class));
            }
        }

        @Test
        @DisplayName("关联的病例不存在应抛出异常")
        void shouldThrowExceptionWhenPatientNotExist() {
            // Given
            FollowUpDTO dto = createFollowUpDTO("invalid_id");

            try (MockedStatic<HashidsUtil> hashMock = mockStatic(HashidsUtil.class)) {
                hashMock.when(() -> HashidsUtil.decode("invalid_id")).thenReturn(999L);
                when(patientMapper.selectById(999L)).thenReturn(null);

                // When & Then
                assertThatThrownBy(() -> followUpService.create(dto))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("病例不存在");
            }
        }

        @Test
        @DisplayName("应自动计算BMI值")
        void shouldAutoCalculateBMI() {
            // Given
            Patient patient = createPatient(100L, DIS_CLASS_DSD, "测试患者");
            FollowUpDTO dto = createFollowUpDTO("test_id");
            dto.setHt("150.0"); // 150cm
            dto.setWt("45.0");  // 45kg

            try (MockedStatic<HashidsUtil> hashMock = mockStatic(HashidsUtil.class);
                 MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {

                hashMock.when(() -> HashidsUtil.decode("test_id")).thenReturn(100L);
                secMock.when(SecurityUtil::getCurrentUsername).thenReturn("doctor_1");

                when(patientMapper.selectById(100L)).thenReturn(patient);
                when(followUpMapper.insert(any(PatientFollowUp.class))).thenAnswer(inv -> {
                    PatientFollowUp saved = inv.getArgument(0);
                    // BMI = 45 / (1.5 * 1.5) = 20.0
                    assertThat(saved.getBmi()).isEqualTo("20.0");
                    return 1;
                });

                // When
                followUpService.create(dto);

                // Then
                verify(followUpMapper, times(1)).insert(any(PatientFollowUp.class));
            }
        }
    }

    // ==================== 编辑随访记录测试 ====================

    @Nested
    @DisplayName("编辑随访记录")
    class UpdateTests {

        @Test
        @DisplayName("存在的随访记录应成功更新")
        void shouldUpdateFollowUpSuccessfully() {
            // Given
            PatientFollowUp existing = createFollowUp(1L, 100L);
            FollowUpDTO dto = createFollowUpDTO("test_id");
            dto.setHt("130.0");
            dto.setWt("30.0");

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentUsername).thenReturn("doctor_1");

                when(followUpMapper.selectById(1L)).thenReturn(existing);
                when(followUpMapper.updateById(any(PatientFollowUp.class))).thenReturn(1);

                // When & Then
                assertThatCode(() -> followUpService.update(1L, dto)).doesNotThrowAnyException();
                verify(followUpMapper, times(1)).updateById(any(PatientFollowUp.class));
            }
        }

        @Test
        @DisplayName("不存在的随访记录应抛出异常")
        void shouldThrowExceptionForNonExistent() {
            // Given
            FollowUpDTO dto = createFollowUpDTO("test_id");
            when(followUpMapper.selectById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> followUpService.update(999L, dto))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== 删除随访记录测试 ====================

    @Nested
    @DisplayName("删除随访记录")
    class DeleteTests {

        @Test
        @DisplayName("存在的随访记录应成功逻辑删除")
        void shouldDeleteFollowUpSuccessfully() {
            // Given
            PatientFollowUp followUp = createFollowUp(1L, 100L);

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentUsername).thenReturn("doctor_1");

                when(followUpMapper.selectById(1L)).thenReturn(followUp);
                when(followUpMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);

                // When & Then
                assertThatCode(() -> followUpService.delete(1L)).doesNotThrowAnyException();
                verify(followUpMapper, times(1)).update(any(), any(LambdaUpdateWrapper.class));
            }
        }

        @Test
        @DisplayName("不存在的随访记录应抛出异常")
        void shouldThrowExceptionForNonExistent() {
            // Given
            when(followUpMapper.selectById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> followUpService.delete(999L))
                    .isInstanceOf(BusinessException.class);
        }
    }
}
