package com.eksjk.service;

import com.eksjk.model.dto.BabyDTO;
import com.eksjk.model.dto.HeightAssessDTO;
import com.eksjk.model.dto.MiniAppProfileDTO;
import com.eksjk.model.dto.WxLoginDTO;
import com.eksjk.model.vo.BabyVO;
import com.eksjk.model.vo.HeightAssessVO;
import com.eksjk.model.vo.WxLoginVO;

import java.util.List;
import java.util.Map;

/**
 * 小程序家长端服务接口
 *
 * @author eksjk
 */
public interface MiniAppService {

    /**
     * 微信登录（通过 code 换取 openid，自动注册/登录）
     */
    WxLoginVO wxLogin(WxLoginDTO loginDTO);

    /**
     * 获取个人信息
     */
    Map<String, Object> getProfile(String openid);

    /**
     * 保存个人信息
     */
    void saveProfile(String openid, MiniAppProfileDTO profileDTO);

    /**
     * 获取宝宝列表
     */
    List<BabyVO> getBabyList(String openid);

    /**
     * 获取宝宝详情
     */
    BabyVO getBabyDetail(String openid, Long babyId);

    /**
     * 添加宝宝
     */
    void addBaby(String openid, BabyDTO babyDTO);

    /**
     * 编辑宝宝
     */
    void editBaby(String openid, BabyDTO babyDTO);

    /**
     * 删除宝宝
     */
    void deleteBaby(String openid, Long babyId);

    /**
     * 身高评测
     */
    HeightAssessVO assessHeight(String openid, HeightAssessDTO assessDTO);

    /**
     * 获取历史评测记录
     */
    List<HeightAssessVO> getAssessHistory(String openid, Long babyId);

    /**
     * 绑定医生
     */
    void bindDoctor(String openid, String doctorId);

    /**
     * 解绑医生
     */
    void unbindDoctor(String openid);

    /**
     * 获取绑定的医生信息
     */
    Map<String, Object> getBoundDoctor(String openid);
}
