package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.MasFollowUpMapper;
import com.eksjk.mapper.PatientFollowUpMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.dto.FollowUpDTO;
import com.eksjk.model.entity.MasFollowUp;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.entity.PatientFollowUp;
import com.eksjk.model.vo.FollowUpVO;
import com.eksjk.service.FollowUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 随访管理服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowUpServiceImpl implements FollowUpService {

    private final PatientFollowUpMapper followUpMapper;
    private final MasFollowUpMapper masFollowUpMapper;
    private final PatientMapper patientMapper;

    @Override
    public List<FollowUpVO> listByPatientId(Long patientId) {
        LambdaQueryWrapper<PatientFollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientFollowUp::getPatientId, patientId)
                .eq(PatientFollowUp::getDelFlg, "1")
                .orderByDesc(PatientFollowUp::getFollTime);

        List<PatientFollowUp> list = followUpMapper.selectList(wrapper);
        return list.stream().map(this::convertToVO).toList();
    }

    @Override
    public FollowUpVO getDetail(Long id) {
        PatientFollowUp followUp = followUpMapper.selectById(id);
        if (followUp == null || "0".equals(followUp.getDelFlg())) {
            throw new BusinessException("随访记录不存在");
        }

        FollowUpVO vo = convertToVO(followUp);

        // 如果是MAS疾病类型，加载MAS专用随访数据
        Patient patient = patientMapper.selectById(followUp.getPatientId());
        if (patient != null && "10000004".equals(patient.getDisClass())) {
            // MAS专用随访数据通过masId关联
            // 此处简化处理，实际需要根据业务逻辑关联
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(FollowUpDTO followUpDTO) {
        Long patientId = HashidsUtil.decode(followUpDTO.getPatientId());

        // 验证患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("关联的病例不存在");
        }

        PatientFollowUp followUp = new PatientFollowUp();
        BeanUtils.copyProperties(followUpDTO, followUp);
        followUp.setPatientId(patientId);
        followUp.setUpTime(LocalDateTime.now());
        followUp.setDelFlg("1");
        followUp.setAuditStatus("pending_review");

        // 自动计算BMI
        if (followUp.getHt() != null && followUp.getWt() != null) {
            try {
                double h = Double.parseDouble(followUp.getHt()) / 100.0;
                double w = Double.parseDouble(followUp.getWt());
                if (h > 0) {
                    followUp.setBmi(String.format("%.1f", w / (h * h)));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // 自动计算腰臀比
        if (followUp.getWaistline() != null && followUp.getHips() != null) {
            try {
                double waist = Double.parseDouble(followUp.getWaistline());
                double hip = Double.parseDouble(followUp.getHips());
                if (hip > 0) {
                    followUp.setWaistToHipRatio(String.format("%.2f", waist / hip));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        followUpMapper.insert(followUp);

        // 如果是MAS疾病类型且有MAS专用数据，保存MAS随访
        if ("10000004".equals(patient.getDisClass()) && followUpDTO.getMasFollowUpData() != null) {
            MasFollowUp masFollowUp = new MasFollowUp();
            org.springframework.beans.MutablePropertyValues pvs =
                    new org.springframework.beans.MutablePropertyValues(followUpDTO.getMasFollowUpData());
            org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(masFollowUp);
            binder.bind(pvs);
            masFollowUp.setDelFlg("1");
            masFollowUpMapper.insert(masFollowUp);
        }

        log.info("新增随访记录成功: patientId={}, operator={}", patientId, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, FollowUpDTO followUpDTO) {
        PatientFollowUp existing = followUpMapper.selectById(id);
        if (existing == null || "0".equals(existing.getDelFlg())) {
            throw new BusinessException("随访记录不存在");
        }

        // 保留原有的 id、patientId、delFlg 等关键字段，仅更新业务字段
        Long originalId = existing.getId();
        Long originalPatientId = existing.getPatientId();
        String originalDelFlg = existing.getDelFlg();

        BeanUtils.copyProperties(followUpDTO, existing, "patientId");
        existing.setId(originalId);
        existing.setPatientId(originalPatientId);
        existing.setDelFlg(originalDelFlg);

        // 驳回后编辑自动重置为待审核
        if ("rejected".equals(existing.getAuditStatus())) {
            existing.setAuditStatus("pending_review");
            existing.setAuditTime(null);
            existing.setAuditBy(null);
        }

        // 重新计算BMI
        if (existing.getHt() != null && existing.getWt() != null) {
            try {
                double h = Double.parseDouble(existing.getHt()) / 100.0;
                double w = Double.parseDouble(existing.getWt());
                if (h > 0) {
                    existing.setBmi(String.format("%.1f", w / (h * h)));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        followUpMapper.updateById(existing);
        log.info("编辑随访记录成功: id={}, operator={}", id, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PatientFollowUp followUp = followUpMapper.selectById(id);
        if (followUp == null) {
            throw new BusinessException("随访记录不存在");
        }

        LambdaUpdateWrapper<PatientFollowUp> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PatientFollowUp::getId, id).set(PatientFollowUp::getDelFlg, "0");
        followUpMapper.update(null, wrapper);

        log.info("删除随访记录成功: id={}, operator={}", id, SecurityUtil.getCurrentUsername());
    }

    /**
     * PatientFollowUp -> FollowUpVO 转换
     */
    private FollowUpVO convertToVO(PatientFollowUp followUp) {
        FollowUpVO vo = new FollowUpVO();
        BeanUtils.copyProperties(followUp, vo);
        vo.setId(HashidsUtil.encode(followUp.getId()));
        vo.setPatientId(HashidsUtil.encode(followUp.getPatientId()));
        return vo;
    }
}
