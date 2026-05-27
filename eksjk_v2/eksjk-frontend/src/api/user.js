/**
 * 用户管理 API 接口
 */
import request from '@/utils/request'

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({ url: '/api/users', method: 'get', params })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id) {
  return request({ url: `/api/users/${id}`, method: 'get' })
}

/**
 * 新增用户
 */
export function createUser(data) {
  return request({ url: '/api/users', method: 'post', data })
}

/**
 * 编辑用户
 */
export function updateUser(id, data) {
  return request({ url: `/api/users/${id}`, method: 'put', data })
}

/**
 * 启用/禁用用户
 */
export function updateUserStatus(id, isActive) {
  return request({ url: `/api/users/${id}/status`, method: 'put', params: { isActive } })
}

/**
 * 重置密码
 */
export function resetUserPassword(id) {
  return request({ url: `/api/users/${id}/reset-password`, method: 'put' })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({ url: `/api/users/${id}`, method: 'delete' })
}

/**
 * 获取个人信息
 */
export function getProfile() {
  return request({ url: '/api/users/profile', method: 'get' })
}

/**
 * 更新个人信息
 */
export function updateProfile(data) {
  return request({ url: '/api/users/profile', method: 'put', data })
}
