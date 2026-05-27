package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.FssCase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 遗传性骨病 (FSS) Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface FssCaseMapper extends BaseMapper<FssCase> {
}
