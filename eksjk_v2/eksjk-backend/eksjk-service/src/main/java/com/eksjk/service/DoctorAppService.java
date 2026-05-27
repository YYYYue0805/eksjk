package com.eksjk.service;

import com.eksjk.model.dto.DoctorAppLoginDTO;
import com.eksjk.model.vo.DoctorAppLoginVO;

import java.util.List;
import java.util.Map;

/**
 * 小程序医生端服务接口
 *
 * @author eksjk
 */
public interface DoctorAppService {

    /**
     * 微信登录（通过 code 换取 openid，查找绑定的医生账号）
     */
    DoctorAppLoginVO wxLogin(DoctorAppLoginDTO loginDTO);

    /**
     * 账号密码登录
     */
    DoctorAppLoginVO accountLogin(DoctorAppLoginDTO loginDTO);

    /**
     * 绑定微信账号（首次微信登录时，输入PC端账号密码绑定）
     */
    DoctorAppLoginVO bindWxAccount(DoctorAppLoginDTO loginDTO);

    /**
     * 获取医生个人信息
     */
    Map<String, Object> getProfile(Long doctorId);

    /**
     * 获取工作台统计数据
     */
    Map<String, Object> getDashboardStats(Long doctorId);

    /**
     * 获取医生名下的患者列表（分页）
     */
    Map<String, Object> getPatientList(Long doctorId, String keyword, String disClass, int pageNum, int pageSize);

    /**
     * 获取患者详情（含随访摘要）
     */
    Map<String, Object> getPatientDetail(Long doctorId, Long patientId);

    /**
     * 快捷新增随访记录
     */
    void quickCreateFollowUp(Long doctorId, Map<String, Object> data);

    /**
     * 获取待审核的家长数据列表
     */
    List<Map<String, Object>> getPendingReviewList(Long doctorId);

    /**
     * 审核家长提交的数据
     */
    void reviewData(Long doctorId, Long recordId, boolean approved, String rejectReason);

    /**
     * 获取统计数据（疾病分布、患者增长趋势、性别分布）
     */
    Map<String, Object> getStatistics(Long doctorId);

    /**
     * 获取医生二维码数据（供家长扫码绑定）
     */
    Map<String, Object> getQrCodeData(Long doctorId);
}
