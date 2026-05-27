package com.eksjk.web.controller;

import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.BabyDTO;
import com.eksjk.model.dto.HeightAssessDTO;
import com.eksjk.model.dto.MiniAppProfileDTO;
import com.eksjk.model.dto.WxLoginDTO;
import com.eksjk.model.vo.BabyVO;
import com.eksjk.model.vo.HeightAssessVO;
import com.eksjk.model.vo.WxLoginVO;
import com.eksjk.service.MiniAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 小程序家长端 Controller
 * <p>
 * 所有接口路径以 /api/miniapp 开头，与 PC 端 API 区分。
 * 登录接口不需要认证，其余接口通过 openid 参数（请求头）鉴权。
 * </p>
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/miniapp")
@RequiredArgsConstructor
public class MiniAppController {

    private final MiniAppService miniAppService;

    // ==================== 登录认证 ====================

    /**
     * 微信登录（通过 code 换取 token）
     */
    @PostMapping("/login")
    public R<WxLoginVO> wxLogin(@RequestBody WxLoginDTO loginDTO) {
        WxLoginVO result = miniAppService.wxLogin(loginDTO);
        return R.ok(result);
    }

    // ==================== 个人信息 ====================

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public R<Map<String, Object>> getProfile(@RequestHeader("X-Openid") String openid) {
        Map<String, Object> profile = miniAppService.getProfile(openid);
        return R.ok(profile);
    }

    /**
     * 保存个人信息
     */
    @PutMapping("/profile")
    public R<Void> saveProfile(@RequestHeader("X-Openid") String openid,
                               @RequestBody MiniAppProfileDTO profileDTO) {
        miniAppService.saveProfile(openid, profileDTO);
        return R.ok();
    }

    // ==================== 宝宝管理 ====================

    /**
     * 获取宝宝列表
     */
    @GetMapping("/babies")
    public R<List<BabyVO>> getBabyList(@RequestHeader("X-Openid") String openid) {
        List<BabyVO> list = miniAppService.getBabyList(openid);
        return R.ok(list);
    }

    /**
     * 获取宝宝详情
     */
    @GetMapping("/babies/{id}")
    public R<BabyVO> getBabyDetail(@RequestHeader("X-Openid") String openid,
                                   @PathVariable String id) {
        long babyId = HashidsUtil.decode(id);
        BabyVO detail = miniAppService.getBabyDetail(openid, babyId);
        return R.ok(detail);
    }

    /**
     * 添加宝宝
     */
    @PostMapping("/babies")
    public R<Void> addBaby(@RequestHeader("X-Openid") String openid,
                           @RequestBody BabyDTO babyDTO) {
        miniAppService.addBaby(openid, babyDTO);
        return R.ok();
    }

    /**
     * 编辑宝宝
     */
    @PutMapping("/babies/{id}")
    public R<Void> editBaby(@RequestHeader("X-Openid") String openid,
                            @PathVariable String id,
                            @RequestBody BabyDTO babyDTO) {
        babyDTO.setId(id);
        miniAppService.editBaby(openid, babyDTO);
        return R.ok();
    }

    /**
     * 删除宝宝
     */
    @DeleteMapping("/babies/{id}")
    public R<Void> deleteBaby(@RequestHeader("X-Openid") String openid,
                              @PathVariable String id) {
        long babyId = HashidsUtil.decode(id);
        miniAppService.deleteBaby(openid, babyId);
        return R.ok();
    }

    // ==================== 身高评测 ====================

    /**
     * 提交身高评测
     */
    @PostMapping("/assess")
    public R<HeightAssessVO> assessHeight(@RequestHeader("X-Openid") String openid,
                                          @RequestBody HeightAssessDTO assessDTO) {
        HeightAssessVO result = miniAppService.assessHeight(openid, assessDTO);
        return R.ok(result);
    }

    /**
     * 获取历史评测记录
     */
    @GetMapping("/assess/history/{babyId}")
    public R<List<HeightAssessVO>> getAssessHistory(@RequestHeader("X-Openid") String openid,
                                                    @PathVariable String babyId) {
        long id = HashidsUtil.decode(babyId);
        List<HeightAssessVO> list = miniAppService.getAssessHistory(openid, id);
        return R.ok(list);
    }

    // ==================== 医生绑定 ====================

    /**
     * 绑定医生
     */
    @PostMapping("/doctor/bind")
    public R<Void> bindDoctor(@RequestHeader("X-Openid") String openid,
                              @RequestBody Map<String, String> body) {
        String doctorId = body.get("doctorId");
        miniAppService.bindDoctor(openid, doctorId);
        return R.ok();
    }

    /**
     * 解绑医生
     */
    @PostMapping("/doctor/unbind")
    public R<Void> unbindDoctor(@RequestHeader("X-Openid") String openid) {
        miniAppService.unbindDoctor(openid);
        return R.ok();
    }

    /**
     * 获取绑定的医生信息
     */
    @GetMapping("/doctor")
    public R<Map<String, Object>> getBoundDoctor(@RequestHeader("X-Openid") String openid) {
        Map<String, Object> doctor = miniAppService.getBoundDoctor(openid);
        return R.ok(doctor);
    }
}
