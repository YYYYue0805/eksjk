/**
 * ELTM 数据同步 API 接口
 */
import request from '@/utils/request'

/**
 * 手动触发 ELTM 数据同步
 */
export function syncEltmData() {
  return request({
    url: '/api/eltm/sync',
    method: 'post'
  })
}

/**
 * 查询 ELTM 同步状态
 */
export function getEltmSyncStatus() {
  return request({
    url: '/api/eltm/sync-status',
    method: 'get'
  })
}

/**
 * 对单个 ELTM 患者执行智能诊断
 */
export function diagnosePatient(patientId) {
  return request({
    url: `/api/eltm/diagnose/${patientId}`,
    method: 'post'
  })
}

/**
 * ELTM 患者外部数据概览
 */
export function getEltmOverview(patientId) {
  return request({
    url: `/api/eltm/patients/${patientId}/overview`,
    method: 'get'
  })
}

/**
 * ELTM 体征测量分页
 */
export function getEltmPhysical(patientId, params) {
  return request({
    url: `/api/eltm/patients/${patientId}/physical`,
    method: 'get',
    params
  })
}

/**
 * ELTM 表单提交分页
 */
export function getEltmForms(patientId, params) {
  return request({
    url: `/api/eltm/patients/${patientId}/forms`,
    method: 'get',
    params
  })
}

/**
 * ELTM 表单详情
 */
export function getEltmFormDetail(patientId, formDataId) {
  return request({
    url: `/api/eltm/patients/${patientId}/forms/${formDataId}`,
    method: 'get'
  })
}

/**
 * ELTM 病历图片
 */
export function getEltmPics(patientId) {
  return request({
    url: `/api/eltm/patients/${patientId}/pics`,
    method: 'get'
  })
}

/**
 * ELTM 评估报告分页
 */
export function getEltmReports(patientId, params) {
  return request({
    url: `/api/eltm/patients/${patientId}/reports`,
    method: 'get',
    params
  })
}

/**
 * ELTM 所属项目与医院绑定
 */
export function getEltmProjects(patientId) {
  return request({
    url: `/api/eltm/patients/${patientId}/projects`,
    method: 'get'
  })
}

/**
 * ELTM 运动与睡眠记录
 */
export function getEltmExerciseSleep(patientId) {
  return request({
    url: `/api/eltm/patients/${patientId}/exercise-sleep`,
    method: 'get'
  })
}

/**
 * 批量诊断所有未分类的 ELTM 患者
 */
export function diagnoseBatch() {
  return request({
    url: '/api/eltm/diagnose-batch',
    method: 'post'
  })
}

