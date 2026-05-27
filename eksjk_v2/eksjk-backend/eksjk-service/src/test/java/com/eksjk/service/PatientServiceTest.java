package com.eksjk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.*;
import com.eksjk.model.dto.PatientDTO;
import com.eksjk.model.dto.PatientQueryDTO;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.vo.PatientVO;
import com.eksjk.service.impl.PatientServiceImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 患者管理服务单元测试
 *
 * @author eksjk
 */
class PatientServiceTest extends BaseServiceTest {

    @InjectMocks
    private PatientServiceImpl patientService;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private DsdCaseMapper dsdCaseMapper;

    @Mock
    private FssCaseMapper fssCaseMapper;

    @Mock
    private CppCaseMapper cppCaseMapper;

    @Mock
    private MasCaseMapper masCaseMapper;

    @Mock
    private SgaCaseMapper sgaCaseMapper;

    @Mock
    private SssCaseMapper sssCaseMapper;

    @Mock
    private EltmCaseMapper eltmCaseMapper;

    @Mock
    private PatientFollowUpMapper followUpMapper;

    // ==================== 分页查询测试 ====================

    @Nested
    @DisplayName("分页查询病例列表")
    class QueryPageTests {

        @Test
        @DisplayName("无筛选条件应返回所有有效数据")
        void shouldReturnAllActivePatients() {
            // Given
            PatientQueryDTO queryDTO = createPatientQueryDTO();
            Patient p1 = createPatient(1L, DIS_CLASS_DSD, "测试患者_DSD_001");
            Patient p2 = createPatient(2L, DIS_CLASS_CPP, "测试患者_CPP_001");

            Page<Patient> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(p1, p2));
            mockPage.setTotal(2);

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");
                when(patientMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                        .thenReturn(mockPage);

                // When
                PageResult<PatientVO> result = patientService.queryPage(queryDTO);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getTotal()).isEqualTo(2);
                assertThat(result.getRecords()).hasSize(2);
            }
        }

        @Test
        @DisplayName("按疾病类型筛选应返回对应数据")
        void shouldFilterByDisClass() {
            // Given
            PatientQueryDTO queryDTO = createPatientQueryDTO();
            queryDTO.setDisClass(DIS_CLASS_DSD);

            Patient p1 = createPatient(1L, DIS_CLASS_DSD, "测试患者_DSD_001");

            Page<Patient> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(p1));
            mockPage.setTotal(1);

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");
                when(patientMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                        .thenReturn(mockPage);

                // When
                PageResult<PatientVO> result = patientService.queryPage(queryDTO);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getTotal()).isEqualTo(1);
            }
        }

        @Test
        @DisplayName("按患者姓名模糊搜索应返回匹配数据")
        void shouldFilterByName() {
            // Given
            PatientQueryDTO queryDTO = createPatientQueryDTO();
            queryDTO.setName("DSD");

            Patient p1 = createPatient(1L, DIS_CLASS_DSD, "测试患者_DSD_001");

            Page<Patient> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(p1));
            mockPage.setTotal(1);

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");
                when(patientMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                        .thenReturn(mockPage);

                // When
                PageResult<PatientVO> result = patientService.queryPage(queryDTO);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getRecords()).hasSize(1);
            }
        }
    }

    // ==================== 获取详情测试 ====================

    @Nested
    @DisplayName("获取病例详情")
    class GetDetailTests {

        @Test
        @DisplayName("存在的病例应返回完整详情")
        void shouldReturnDetailForExistingPatient() {
            // Given
            Patient patient = createPatient(1L, DIS_CLASS_DSD, "测试患者_DSD_001");
            when(patientMapper.selectById(1L)).thenReturn(patient);

            // When
            PatientVO result = patientService.getDetail(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("测试患者_DSD_001");
        }

        @Test
        @DisplayName("不存在的病例应抛出异常")
        void shouldThrowExceptionForNonExistentPatient() {
            // Given
            when(patientMapper.selectById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> patientService.getDetail(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("已删除的病例应抛出异常")
        void shouldThrowExceptionForDeletedPatient() {
            // Given
            Patient patient = createPatient(1L, DIS_CLASS_DSD, "测试患者_DSD_001");
            patient.setDelFlg("0"); // 已删除
            when(patientMapper.selectById(1L)).thenReturn(patient);

            // When & Then
            assertThatThrownBy(() -> patientService.getDetail(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== 创建病例测试 ====================

    @Nested
    @DisplayName("新建病例")
    class CreateTests {

        @Test
        @DisplayName("有效数据应成功创建病例")
        void shouldCreatePatientSuccessfully() {
            // Given
            PatientDTO dto = createPatientDTO(DIS_CLASS_DSD, "新建测试患者");

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentUserId).thenReturn(DOCTOR_ID);
                secMock.when(SecurityUtil::getCurrentUsername).thenReturn("doctor_1");
                secMock.when(SecurityUtil::getCurrentHospitalId).thenReturn(HOSPITAL_UNIT_ID);
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("doctor");

                when(patientMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
                when(patientMapper.insert(any(Patient.class))).thenReturn(1);

                // When
                String caseNum = patientService.create(dto);

                // Then
                assertThat(caseNum).isNotNull();
                verify(patientMapper, times(1)).insert(any(Patient.class));
            }
        }
    }

    // ==================== 删除病例测试 ====================

    @Nested
    @DisplayName("删除病例")
    class DeleteTests {

        @Test
        @DisplayName("存在的病例应成功逻辑删除")
        void shouldDeletePatientSuccessfully() {
            // Given
            Patient patient = createPatient(1L, DIS_CLASS_DSD, "测试患者_DSD_001");
            when(patientMapper.selectById(1L)).thenReturn(patient);
            when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentUserId).thenReturn(DOCTOR_ID);
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("doctor");

                // When
                patientService.delete(1L);

                // Then
                verify(patientMapper, times(1)).updateById(any(Patient.class));
            }
        }
    }

    // ==================== 工作台统计测试 ====================

    @Nested
    @DisplayName("工作台统计数据")
    class DashboardStatsTests {

        @Test
        @DisplayName("应返回各疾病类型的统计数据")
        void shouldReturnDashboardStats() {
            try (MockedStatic<SecurityUtil> secMock = mockStatic(SecurityUtil.class)) {
                secMock.when(SecurityUtil::getCurrentRoleCode).thenReturn("super_admin");
                when(patientMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);

                // When
                var result = patientService.getDashboardStats();

                // Then
                assertThat(result).isNotNull();
                assertThat(result).isNotEmpty();
            }
        }
    }
}
