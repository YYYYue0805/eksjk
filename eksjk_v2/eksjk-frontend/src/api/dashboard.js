/**
 * 仪表板相关 API 接口
 * 提供系统概览数据的获取功能
 */

import request from '@/utils/request'

/**
 * 获取仪表板统计数据
 * @returns {Promise} 统计数据
 */
export function getDashboardSummary() {
  return request({
    url: '/api/dashboard/summary',
    method: 'get'
  })
}

/**
 * 获取系统公告列表
 * @returns {Promise} 公告列表
 */
export function getDashboardNotices() {
  return request({
    url: '/api/dashboard/notices',
    method: 'get'
  })
}

/**
 * 获取参与医院列表（医院名 + 入组病例数 + 医生人数）
 * @returns {Promise} 医院列表
 */
export function getHospitalStats() {
  return request({
    url: '/api/dashboard/hospitals',
    method: 'get'
  })
}

/**
 * 获取单个医院明细（入组人数、医生人数、随访完成度）
 * @param {string} unitName 医院名称
 * @returns {Promise} 医院明细
 */
export function getHospitalDetail(unitName) {
  return request({
    url: '/api/dashboard/hospitals/detail',
    method: 'get',
    params: { unitName }
  })
}

/**
 * 某病种在各医院的录入例数
 * @param {string} diseaseName 病种显示名
 * @returns {Promise} 医院录入明细
 */
export function getDiseaseHospitals(diseaseName) {
  return request({
    url: '/api/dashboard/disease/hospitals',
    method: 'get',
    params: { diseaseName }
  })
}