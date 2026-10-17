package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.EltmSyncError;
import org.apache.ibatis.annotations.Mapper;

/**
 * ELTM 同步失败明细 Mapper
 *
 * @author eksjk
 */
@Mapper
public interface EltmSyncErrorMapper extends BaseMapper<EltmSyncError> {
}
