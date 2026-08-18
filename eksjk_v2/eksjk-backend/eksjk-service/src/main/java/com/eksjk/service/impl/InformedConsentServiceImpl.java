package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.common.util.HashidsUtil;
import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.vo.InformedConsentVO;
import com.eksjk.service.InformedConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知情同意书服务实现
 *
 * @author eksjk
 */
@Service
@RequiredArgsConstructor
public class InformedConsentServiceImpl implements InformedConsentService {

    private final InformedConsentMapper informedConsentMapper;

    @Override
    public List<InformedConsentVO> listByPatientId(Long patientId) {
        List<InformedConsent> consents = informedConsentMapper.selectList(
                new LambdaQueryWrapper<InformedConsent>()
                        .eq(InformedConsent::getPatientId, patientId)
                        .orderByDesc(InformedConsent::getSignTime));

        return consents.stream().map(c -> {
            InformedConsentVO vo = new InformedConsentVO();
            BeanUtils.copyProperties(c, vo);
            vo.setId(HashidsUtil.encode(c.getId()));
            vo.setPatientId(HashidsUtil.encode(c.getPatientId()));
            return vo;
        }).toList();
    }

    @Override
    public Map<String, Object> getSyncStatus() {
        InformedConsent latest = informedConsentMapper.selectOne(
                new LambdaQueryWrapper<InformedConsent>()
                        .isNotNull(InformedConsent::getSyncTime)
                        .orderByDesc(InformedConsent::getSyncTime)
                        .last("LIMIT 1"));
        Long total = informedConsentMapper.selectCount(null);

        Map<String, Object> result = new HashMap<>();
        result.put("lastSyncTime", latest != null ? latest.getSyncTime() : null);
        result.put("totalCount", total != null ? total : 0L);
        return result;
    }
}
