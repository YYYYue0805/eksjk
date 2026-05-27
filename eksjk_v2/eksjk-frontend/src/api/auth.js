/**
 * 认证相关 API 接口
 * 演示接口封装规范
 */
import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - { username, password }
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前登录用户信息
 * @returns {Promise}
 */
export function getUserInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

/**
 * 修改密码
 * @param {Object} data - { oldPassword, newPassword, confirmPassword }
 * @returns {Promise}
 */
export function changePassword(data) {
  return request({
    url: '/api/users/password',
    method: 'put',
    data
  })
}
