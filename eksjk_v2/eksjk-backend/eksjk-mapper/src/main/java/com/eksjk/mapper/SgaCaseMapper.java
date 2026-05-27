package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.SgaCase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 小于胎龄儿 (SGA) Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface SgaCaseMapper extends BaseMapper<SgaCase> {
}
