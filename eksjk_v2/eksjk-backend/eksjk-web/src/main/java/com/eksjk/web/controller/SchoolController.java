package com.eksjk.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.eksjk.common.result.PageResult;
import com.eksjk.common.result.R;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.model.dto.StudentDTO;
import com.eksjk.model.dto.StudentQueryDTO;
import com.eksjk.model.vo.StudentVO;
import com.eksjk.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学校健康筛查 Controller
 *
 * @author eksjk
 */
@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
@SaCheckLogin
public class SchoolController {

    private final SchoolService schoolService;

    /**
     * 分页查询学生列表
     */
    @GetMapping("/students")
    public R<PageResult<StudentVO>> list(StudentQueryDTO queryDTO) {
        PageResult<StudentVO> result = schoolService.queryPage(queryDTO);
        return R.ok(result);
    }

    /**
     * 获取学生详情（含全部问卷数据）
     */
    @GetMapping("/students/{id}")
    public R<StudentVO> detail(@PathVariable String id) {
        long studentId = HashidsUtil.decode(id);
        StudentVO detail = schoolService.getDetail(studentId);
        return R.ok(detail);
    }

    /**
     * 新增学生
     */
    @PostMapping("/students")
    public R<Void> create(@RequestBody StudentDTO studentDTO) {
        schoolService.create(studentDTO);
        return R.ok();
    }

    /**
     * 编辑学生基本信息
     */
    @PutMapping("/students/{id}")
    public R<Void> update(@PathVariable String id, @RequestBody StudentDTO studentDTO) {
        long studentId = HashidsUtil.decode(id);
        schoolService.update(studentId, studentDTO);
        return R.ok();
    }

    /**
     * 删除学生（逻辑删除）
     */
    @DeleteMapping("/students/{id}")
    public R<Void> delete(@PathVariable String id) {
        long studentId = HashidsUtil.decode(id);
        schoolService.delete(studentId);
        return R.ok();
    }

    /**
     * 保存单张问卷数据
     */
    @PutMapping("/students/{id}/questionnaire/{type}")
    public R<Void> saveQuestionnaire(
            @PathVariable String id,
            @PathVariable String type,
            @RequestBody Map<String, Object> data) {
        long studentId = HashidsUtil.decode(id);
        schoolService.saveQuestionnaire(studentId, type, data);
        return R.ok();
    }
}
