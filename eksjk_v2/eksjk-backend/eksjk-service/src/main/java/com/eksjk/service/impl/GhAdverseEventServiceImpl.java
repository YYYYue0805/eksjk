package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eksjk.common.exception.BusinessException;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.common.util.SecurityUtil;
import com.eksjk.mapper.GhAdverseEventMapper;
import com.eksjk.mapper.PatientMapper;
import com.eksjk.model.dto.GhAdverseEventDTO;
import com.eksjk.model.entity.GhAdverseEvent;
import com.eksjk.model.entity.Patient;
import com.eksjk.model.vo.GhAdverseEventVO;
import com.eksjk.service.GhAdverseEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GH不良事件服务实现
 *
 * @author eksjk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GhAdverseEventServiceImpl implements GhAdverseEventService {

    private final GhAdverseEventMapper ghAdverseEventMapper;
    private final PatientMapper patientMapper;

    @Override
    public List<GhAdverseEventVO> listByPatientId(Long patientId) {
        LambdaQueryWrapper<GhAdverseEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GhAdverseEvent::getPatientId, patientId)
                .eq(GhAdverseEvent::getIsDeleted, 0)
                .orderByDesc(GhAdverseEvent::getOccurrenceDate);

        List<GhAdverseEvent> list = ghAdverseEventMapper.selectList(wrapper);
        return list.stream().map(this::convertToVO).toList();
    }

    @Override
    public GhAdverseEventVO getDetail(Long id) {
        GhAdverseEvent event = ghAdverseEventMapper.selectById(id);
        if (event == null || event.getIsDeleted() == 1) {
            throw new BusinessException("不良事件记录不存在");
        }
        return convertToVO(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(GhAdverseEventDTO dto) {
        Long patientId = HashidsUtil.decode(dto.getPatientId());

        Patient patient = patientMapper.selectById(patientId);
        if (patient == null || "0".equals(patient.getDelFlg())) {
            throw new BusinessException("关联的病例不存在");
        }

        GhAdverseEvent event = new GhAdverseEvent();
        BeanUtils.copyProperties(dto, event, "patientId");
        event.setPatientId(patientId);
        event.setCreateTime(LocalDateTime.now());
        event.setUpdateTime(LocalDateTime.now());
        event.setIsDeleted(0);

        ghAdverseEventMapper.insert(event);
        log.info("新增GH不良事件成功: patientId={}, operator={}", patientId, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, GhAdverseEventDTO dto) {
        GhAdverseEvent existing = ghAdverseEventMapper.selectById(id);
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("不良事件记录不存在");
        }

        Long originalId = existing.getId();
        Long originalPatientId = existing.getPatientId();
        LocalDateTime originalCreateTime = existing.getCreateTime();

        BeanUtils.copyProperties(dto, existing, "patientId");
        existing.setId(originalId);
        existing.setPatientId(originalPatientId);
        existing.setCreateTime(originalCreateTime);
        existing.setUpdateTime(LocalDateTime.now());

        ghAdverseEventMapper.updateById(existing);
        log.info("编辑GH不良事件成功: id={}, operator={}", id, SecurityUtil.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        GhAdverseEvent event = ghAdverseEventMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("不良事件记录不存在");
        }

        LambdaUpdateWrapper<GhAdverseEvent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(GhAdverseEvent::getId, id).set(GhAdverseEvent::getIsDeleted, 1);
        ghAdverseEventMapper.update(null, wrapper);

        log.info("删除GH不良事件成功: id={}, operator={}", id, SecurityUtil.getCurrentUsername());
    }

    /**
     * Entity -> VO 转换
     */
    private GhAdverseEventVO convertToVO(GhAdverseEvent event) {
        GhAdverseEventVO vo = new GhAdverseEventVO();
        BeanUtils.copyProperties(event, vo);
        vo.setId(HashidsUtil.encode(event.getId()));
        vo.setPatientId(HashidsUtil.encode(event.getPatientId()));
        return vo;
    }
}
