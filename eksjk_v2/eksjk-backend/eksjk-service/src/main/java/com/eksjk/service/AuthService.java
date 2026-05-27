package com.eksjk.service;

import com.eksjk.model.dto.LoginDTO;
import com.eksjk.model.vo.LoginVO;
import com.eksjk.model.vo.UserInfoVO;

/**
 * 认证服务接口
 *
 * @author eksjk
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @param ip       客户端 IP
     * @return 登录响应（Token + 用户信息）
     */
    LoginVO login(LoginDTO loginDTO, String ip);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getCurrentUserInfo();
}
