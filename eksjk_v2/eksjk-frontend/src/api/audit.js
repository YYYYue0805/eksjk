import request from '@/utils/request'

// ==================== 基线审核发放 ====================

/** 分页查询基线审核列表 */
export function getPatientAuditList(params) {
  return request({ url: '/api/audit/patients', method: 'get', params })
}

/** 审核通过基线 */
export function approvePatient(id, data) {
  return request({ url: `/api/audit/patients/${id}/approve`, method: 'post', data })
}

/** 驳回基线 */
export function rejectPatient(id, data) {
  return request({ url: `/api/audit/patients/${id}/reject`, method: 'post', data })
}

/** 发放基线 */
export function releasePatient(id) {
  return request({ url: `/api/audit/patients/${id}/release`, method: 'post' })
}

// ==================== 随访审核发放 ====================

/** 分页查询随访审核列表 */
export function getFollowUpAuditList(params) {
  return request({ url: '/api/audit/followups', method: 'get', params })
}

/** 审核通过随访 */
export function approveFollowUp(id, data) {
  return request({ url: `/api/audit/followups/${id}/approve`, method: 'post', data })
}

/** 驳回随访 */
export function rejectFollowUp(id, data) {
  return request({ url: `/api/audit/followups/${id}/reject`, method: 'post', data })
}

/** 发放随访 */
export function releaseFollowUp(id) {
  return request({ url: `/api/audit/followups/${id}/release`, method: 'post' })
}

// ==================== 统计 ====================

/** 获取审核发放统计 */
export function getAuditStats() {
  return request({ url: '/api/audit/stats', method: 'get' })
}
