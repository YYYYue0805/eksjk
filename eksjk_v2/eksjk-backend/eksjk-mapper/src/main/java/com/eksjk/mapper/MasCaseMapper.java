package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.MasCase;
import org.apache.ibatis.annotations.Mapper;

/**
 * McCune-Albright (MAS) Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface MasCaseMapper extends BaseMapper<MasCase> {
}
