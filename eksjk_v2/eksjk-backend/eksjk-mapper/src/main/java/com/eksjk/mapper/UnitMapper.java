package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.Unit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 医疗机构 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface UnitMapper extends BaseMapper<Unit> {

    /**
     * 统计某机构下的用户数量
     */
    @Select("SELECT COUNT(*) FROM login_user WHERE unit = #{unitId} AND is_deleted = 0")
    long countUsersByUnitId(String unitId);

    /**
     * 统计某机构下的病例数量
     */
    @Select("SELECT COUNT(*) FROM datamain_patient WHERE hospital_id = #{unitId}")
    long countPatientsByUnitId(String unitId);
}
