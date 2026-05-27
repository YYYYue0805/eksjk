package com.eksjk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eksjk.model.entity.ChartUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信小程序用户 Mapper 接口
 *
 * @author eksjk
 */
@Mapper
public interface ChartUserMapper extends BaseMapper<ChartUser> {
}
