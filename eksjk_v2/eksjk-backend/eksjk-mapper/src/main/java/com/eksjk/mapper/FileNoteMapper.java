package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.FileNote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件备注 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface FileNoteMapper extends BaseMapper<FileNote> {
}
