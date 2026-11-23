/**
 * 统计分析相关 API
 */
import request from '@/utils/request'

/**
 * 医院 × 月份 新增病例矩阵
 * @param {Object} params { startDate, endDate }
 */
export function getHospitalMonthly(params) {
  return request({
    url: '/api/statistics/hospital-monthly',
    method: 'get',
    params
  })
}

/**
 * 按病种统计新增病例数
 * @param {Object} params { startDate, endDate }
 */
export function getDiseaseNewly(params) {
  return request({
    url: '/api/statistics/disease-newly',
    method: 'get',
    params
  })
}

/**
 * 自定义条件检索
 * @param {Object} data 检索条件
 */
export function customSearch(data) {
  return request({
    url: '/api/statistics/custom-search',
    method: 'post',
    data
  })
}
