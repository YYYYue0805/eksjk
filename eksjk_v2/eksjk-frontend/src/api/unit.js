/**
 * 医疗机构管理 API 接口
 */
import request from '@/utils/request'

/**
 * 获取机构列表
 */
export function getUnitList(params) {
  return request({ url: '/api/units', method: 'get', params })
}

/**
 * 获取机构详情
 */
export function getUnitDetail(id) {
  return request({ url: `/api/units/${id}`, method: 'get' })
}

/**
 * 新增机构
 */
export function createUnit(data) {
  return request({ url: '/api/units', method: 'post', data })
}

/**
 * 编辑机构
 */
export function updateUnit(id, data) {
  return request({ url: `/api/units/${id}`, method: 'put', data })
}

/**
 * 启用/禁用机构
 */
export function updateUnitStatus(id, status) {
  return request({ url: `/api/units/${id}/status`, method: 'put', params: { status } })
}

/**
 * 删除机构
 */
export function deleteUnit(id) {
  return request({ url: `/api/units/${id}`, method: 'delete' })
}

/**
 * 获取机构选项列表（下拉选择）
 */
export function getUnitOptions(keyword) {
  return request({ url: '/api/units/options', method: 'get', params: { keyword } })
}

/**
 * 获取机构统计
 */
export function getUnitStatistics(id) {
  return request({ url: `/api/units/${id}/statistics`, method: 'get' })
}
