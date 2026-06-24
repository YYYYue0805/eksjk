/**
 * GH不良事件 API 接口
 */
import request from '@/utils/request'

/**
 * 获取某患者的不良事件列表
 * @param {string} patientId - 患者ID（Hashids编码）
 */
export function getAdverseEventList(patientId) {
  return request({
    url: `/api/gh-adverse-events/patient/${patientId}`,
    method: 'get'
  })
}

/**
 * 获取不良事件详情
 * @param {string} id - 不良事件ID
 */
export function getAdverseEventDetail(id) {
  return request({
    url: `/api/gh-adverse-events/${id}`,
    method: 'get'
  })
}

/**
 * 新增不良事件
 * @param {Object} data - 不良事件数据
 */
export function createAdverseEvent(data) {
  return request({
    url: '/api/gh-adverse-events',
    method: 'post',
    data
  })
}

/**
 * 编辑不良事件
 * @param {string} id - 不良事件ID
 * @param {Object} data - 不良事件数据
 */
export function updateAdverseEvent(id, data) {
  return request({
    url: `/api/gh-adverse-events/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除不良事件
 * @param {string} id - 不良事件ID
 */
export function deleteAdverseEvent(id) {
  return request({
    url: `/api/gh-adverse-events/${id}`,
    method: 'delete'
  })
}
