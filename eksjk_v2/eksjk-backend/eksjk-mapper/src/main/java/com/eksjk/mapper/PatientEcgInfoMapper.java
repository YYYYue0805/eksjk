package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.PatientEcgInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病例心电图信息 Mapper
 *
 * @author eksjk
 */
@Mapper
public interface PatientEcgInfoMapper extends BaseMapper<PatientEcgInfo> {
}
