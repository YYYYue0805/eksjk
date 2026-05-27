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