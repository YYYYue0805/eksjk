package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.EltmPatientProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * ELTM 外部患者扩展属性 Mapper
 *
 * @author eksjk
 */
@Mapper
public interface EltmPatientProfileMapper extends BaseMapper<EltmPatientProfile> {
}
