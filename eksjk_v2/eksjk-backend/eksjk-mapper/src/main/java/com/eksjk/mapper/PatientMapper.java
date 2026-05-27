package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者主表 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
