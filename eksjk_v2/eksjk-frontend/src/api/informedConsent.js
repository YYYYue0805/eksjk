/**
 * 知情同意书 API 接口
 */
import request from '@/utils/request'

/**
 * 按患者查询知情同意书列表
 * @param {string} patientId - 患者ID（Hashids 编码）
 */
export function getInformedConsentList(patientId) {
  return request({
    url: '/api/informed-consent/list-by-patient',
    method: 'get',
    params: { patientId }
  })
}

/**
 * 手动触发同步知情同意书
 */
export function syncInformedConsent() {
  return request({
    url: '/api/informed-consent/sync',
    method: 'post'
  })
}
