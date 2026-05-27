package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生主表 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
