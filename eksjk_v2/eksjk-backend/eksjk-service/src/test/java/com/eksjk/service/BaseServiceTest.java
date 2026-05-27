package com.eksjk.service;

import com.eksjk.model.dto.LoginDTO;
import com.eksjk.model.dto.PatientDTO;
import com.eksjk.model.dto.PatientQueryDTO;
import com.eksjk.model.dto.FollowUpDTO;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

/**
 * Service 层单元测试基类
 * <p>
 * 提供统一的 Mock 环境配置和测试数据工厂方法，
 * 所有 Service 单元测试类应继承此基类。
 * </p>
 *
 * <h3>使用方式：</h3>
 * <pre>
 * class PatientServiceTest extends BaseServiceTest {
 *     &#64;InjectMocks
 *     private PatientServiceImpl patientService;
 *     &#64;Mock
 *     private PatientMapper patientMapper;
 * }
 * </pre>
 *
 * @author eksjk
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    // ==================== 常量定义 ====================

    /** 测试用超级管理员 ID */
    protected static final Long SUPER_ADMIN_ID = 1L;

    /** 测试用医院管理员 ID */
    protected static final Long HOSPITAL_ADMIN_ID = 2L;

    /** 测试用普通医生 ID */
    protected static final Long DOCTOR_ID = 3L;

    /** 测试用医院 ID */
    protected static final String HOSPITAL_UNIT_ID = "100";

    /** 测试用疾病分类编码 - DSD */
    protected static final String DIS_CLASS_DSD = "10000001";

    /** 测试用疾病分类编码 - CPP */
    protected static final String DIS_CLASS_CPP = "10000003";

    /** 测试用疾病分类编码 - MAS */
    protected static final String DIS_CLASS_MAS = "10000004";

    /** 测试用密码（明文） */
    protected static final String TEST_PASSWORD = "Test@1234";

    /** 测试用 BCrypt 密码（Test@1234 的 BCrypt 加密值） */
    protected static final String TEST_PASSWORD_BCRYPT =
            "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";

    // ==================== 生命周期 ====================

    @BeforeEach
    protected void baseSetUp() {
        // 子类可覆盖此方法进行额外初始化
    }

    // ==================== 测试数据工厂方法 ====================

    /**
     * 创建测试用 User 实体（超级管理员）
     */
    protected User createSuperAdmin() {
        User user = new User();
        user.setId(SUPER_ADMIN_ID);
        user.setUsername("super_admin");
        user.setPassword(TEST_PASSWORD_BCRYPT);
        user.setName("超级管理员");
        user.setRoleCode("super_admin");
        user.setIsActive(true);
        user.setIsSuperuser(true);
        user.setDateJoined(LocalDateTime.now());
        return user;
    }

    /**
     * 创建测试用 User 实体（医院管理员）
     */
    protected User createHospitalAdmin() {
        User user = new User();
        user.setId(HOSPITAL_ADMIN_ID);
        user.setUsername("hospital_admin_1");
        user.setPassword(TEST_PASSWORD_BCRYPT);
        user.setName("医院管理员A");
        user.setRoleCode("hospital_admin");
        user.setUnit(HOSPITAL_UNIT_ID);
        user.setIsActive(true);
        user.setIsSuperuser(false);
        user.setDateJoined(LocalDateTime.now());
        return user;
    }

    /**
     * 创建测试用 User 实体（普通医生）
     */
    protected User createDoctor() {
        User user = new User();
        user.setId(DOCTOR_ID);
        user.setUsername("doctor_1");
        user.setPassword(TEST_PASSWORD_BCRYPT);
        user.setName("测试医生");
        user.setRoleCode("doctor");
        user.setUnit(HOSPITAL_UNIT_ID);
        user.setIsActive(true);
        user.setIsSuperuser(false);
        user.setDateJoined(LocalDateTime.now());
        return user;
    }

    /**
     * 创建测试用 LoginDTO
     */
    protected LoginDTO createLoginDTO(String username, String password) {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }

    /**
     * 创建测试用 Patient 实体
     *
     * @param id       患者 ID
     * @param disClass 疾病分类编码
     * @param name     患者姓名
     */
    protected Patient createPatient(Long id, String disClass, String name) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setDisClass(disClass);
        patient.setName(name);
        patient.setCaseNum("CASE" + id);
        patient.setSex("男");
        patient.setBirthTime(LocalDateTime.of(2015, 6, 15, 0, 0));
        patient.setHeight("120.5");
        patient.setWeight("25.0");
        patient.setBmi("17.2");
        patient.setDoctorName("测试医生");
        patient.setHospitalName("测试医院A");
        patient.setUpMec(HOSPITAL_UNIT_ID);
        patient.setImpPer(String.valueOf(DOCTOR_ID));
        patient.setCTime(LocalDateTime.now());
        patient.setDelFlg("1");
        return patient;
    }

    /**
     * 创建测试用 PatientDTO
     */
    protected PatientDTO createPatientDTO(String disClass, String name) {
        PatientDTO dto = new PatientDTO();
        dto.setDisClass(disClass);
        dto.setName(name);
        dto.setSex("男");
        dto.setHeight("120.5");
        dto.setWeight("25.0");
        return dto;
    }

    /**
     * 创建测试用 PatientQueryDTO
     */
    protected PatientQueryDTO createPatientQueryDTO() {
        PatientQueryDTO dto = new PatientQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        return dto;
    }

    /**
     * 创建测试用 PatientFollowUp 实体
     *
     * @param id        随访 ID
     * @param patientId 患者 ID
     */
    protected PatientFollowUp createFollowUp(Long id, Long patientId) {
        PatientFollowUp followUp = new PatientFollowUp();
        followUp.setId(id);
        followUp.setPatientId(patientId);
        followUp.setHt("125.0");
        followUp.setWt("27.0");
        followUp.setBmi("17.3");
        followUp.setFollTime(LocalDateTime.now());
        followUp.setAge("10");
        followUp.setDelFlg("1");
        return followUp;
    }

    /**
     * 创建测试用 FollowUpDTO
     *
     * @param patientIdEncoded Hashids 编码后的患者 ID
     */
    protected FollowUpDTO createFollowUpDTO(String patientIdEncoded) {
        FollowUpDTO dto = new FollowUpDTO();
        dto.setPatientId(patientIdEncoded);
        dto.setHt("125.0");
        dto.setWt("27.0");
        dto.setBmi("17.3");
        dto.setFollTime(LocalDateTime.now());
        dto.setAge("10");
        return dto;
    }
}
