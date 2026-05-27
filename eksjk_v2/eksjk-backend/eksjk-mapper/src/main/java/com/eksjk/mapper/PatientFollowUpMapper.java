package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.PatientFollowUp;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用随访 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface PatientFollowUpMapper extends BaseMapper<PatientFollowUp> {
}
