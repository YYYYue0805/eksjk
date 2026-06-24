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
 * 批量诊断所有未分类的 ELTM 患者
 */
export function diagnoseBatch() {
  return request({
    url: '/api/eltm/diagnose-batch',
    method: 'post'
  })
}

