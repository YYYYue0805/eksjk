package com.eksjk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eksjk.mapper.InformedConsentMapper;
import com.eksjk.model.entity.InformedConsent;
import com.eksjk.model.vo.InformedConsentVO;
import com.eksjk.service.InformedConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<InformedConsentVO> listByPatient(Long patientId) {
        List<InformedConsent> consents = informedConsentMapper.selectList(
                new LambdaQueryWrapper<InformedConsent>()
                        .eq(InformedConsent::getPatientId, patientId)
                        .orderByDesc(InformedConsent::getSignTime));

        return consents.stream().map(c -> {
            InformedConsentVO vo = new InformedConsentVO();
            BeanUtils.copyProperties(c, vo);
            vo.setId(String.valueOf(c.getId()));
            return vo;
        }).toList();
    }
}
