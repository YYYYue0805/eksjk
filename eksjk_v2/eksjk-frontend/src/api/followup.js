/**
 * 随访管理 API 接口
 */
import request from '@/utils/request'

/**
 * 获取某患者的随访列表
 * @param {string} patientId - 患者ID（Hashids编码）
 */
export function getFollowUpList(patientId) {
  return request({
    url: `/api/followups/patient/${patientId}`,
    method: 'get'
  })
}

/**
 * 获取随访详情
 * @param {string} id - 随访ID
 */
export function getFollowUpDetail(id) {
  return request({
    url: `/api/followups/${id}`,
    method: 'get'
  })
}

/**
 * 新增随访记录
 * @param {Object} data - 随访数据
 */
export function createFollowUp(data) {
  return request({
    url: '/api/followups',
    method: 'post',
    data
  })
}

/**
 * 编辑随访记录
 * @param {string} id - 随访ID
 * @param {Object} data - 随访数据
 */
export function updateFollowUp(id, data) {
  return request({
    url: `/api/followups/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除随访记录
 * @param {string} id - 随访ID
 */
export function deleteFollowUp(id) {
  return request({
    url: `/api/followups/${id}`,
    method: 'delete'
  })
}
