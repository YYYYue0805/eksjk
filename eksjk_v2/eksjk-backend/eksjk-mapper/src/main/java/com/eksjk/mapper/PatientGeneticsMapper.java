package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.PatientGenetics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者遗传学染色体核型 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface PatientGeneticsMapper extends BaseMapper<PatientGenetics> {
}
