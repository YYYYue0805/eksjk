package com.eksjk.service;

import com.eksjk.common.result.PageResult;
import com.eksjk.model.dto.ChangePasswordDTO;
import com.eksjk.model.dto.UserDTO;
import com.eksjk.model.dto.UserQueryDTO;
import com.eksjk.model.vo.UserInfoVO;

import java.util.Map;

/**
 * 用户管理服务接口
 *
 * @author eksjk
 */
public interface UserService {

    /**
     * 分页查询用户列表（根据角色自动过滤数据范围）
     */
    PageResult<UserInfoVO> queryPage(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     */
    UserInfoVO getDetail(Long id);

    /**
     * 新增用户
     * @return 包含初始密码的 Map
     */
    Map<String, String> create(UserDTO userDTO);

    /**
     * 编辑用户
     */
    void update(Long id, UserDTO userDTO);

    /**
     * 启用/禁用用户
     */
    void updateStatus(Long id, Boolean isActive);

    /**
     * 重置密码
     * @return 新密码
     */
    String resetPassword(Long id);

    /**
     * 删除用户（逻辑删除）
     */
    void delete(Long id);

    /**
     * 获取个人信息
     */
    UserInfoVO getProfile();

    /**
     * 更新个人信息
     */
    void updateProfile(UserDTO userDTO);

    /**
     * 修改密码
     */
    void changePassword(ChangePasswordDTO dto);
}
