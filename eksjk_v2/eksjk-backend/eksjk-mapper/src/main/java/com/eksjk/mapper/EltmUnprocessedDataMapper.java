package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.EltmUnprocessedData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ELTM 外部接口未处理数据 Mapper
 *
 * @author eksjk
 */
public interface EltmUnprocessedDataMapper extends BaseMapper<EltmUnprocessedData> {

    /**
     * 按唯一键批量 upsert；已人工标记 MAPPED/IGNORED 的记录保留原状态。
     */
    int upsertBatch(@Param("list") List<EltmUnprocessedData> list);
}
