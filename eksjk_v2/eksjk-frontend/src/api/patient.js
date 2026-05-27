/**
 * 患者病例管理 API 接口
 */
import request from '@/utils/request'

/**
 * 分页查询病例列表
 * @param {Object} params - 查询参数
 */
export function getPatientList(params) {
  return request({
    url: '/api/patients',
    method: 'get',
    params
  })
}

/**
 * 获取病例详情
 * @param {string} id - 患者ID（Hashids编码）
 */
export function getPatientDetail(id) {
  return request({
    url: `/api/patients/${id}`,
    method: 'get'
  })
}

/**
 * 新建病例
 * @param {Object} data - 病例数据
 */
export function createPatient(data) {
  return request({
    url: '/api/patients',
    method: 'post',
    data
  })
}

/**
 * 编辑病例
 * @param {string} id - 患者ID
 * @param {Object} data - 病例数据
 */
export function updatePatient(id, data) {
  return request({
    url: `/api/patients/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除病例
 * @param {string} id - 患者ID
 */
export function deletePatient(id) {
  return request({
    url: `/api/patients/${id}`,
    method: 'delete'
  })
}

/**
 * 导出Excel
 * @param {Object} params - 查询参数
 */
export function exportPatientExcel(params) {
  return request({
    url: '/api/patients/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取工作台统计数据
 */
export function getDashboardStats() {
  return request({
    url: '/api/patients/dashboard/stats',
    method: 'get'
  })
}

/**
 * 疾病类型映射
 */
export const diseaseTypes = {
  dsd: { code: '10000001', name: '性发育异常 (DSD)', prefix: 'DSD' },
  fss: { code: '10000002', name: '遗传性骨病 (FSS)', prefix: 'FSS' },
  cpp: { code: '10000003', name: '中枢性性早熟 (CPP)', prefix: 'CPP' },
  mas: { code: '10000004', name: 'McCune-Albright (MAS)', prefix: 'MAS' },
  sga: { code: '10000005', name: '小于胎龄儿 (SGA)', prefix: 'SGA' },
  sss: { code: '10000006', name: '家族性矮小 (SSS)', prefix: 'SSS' },
  eltm: { code: '10000007', name: 'E路童萌 (ELTM)', prefix: 'ELTM' }
}

/**
 * 根据路由参数获取疾病分类编码
 */
export function getDisClassByType(type) {
  return diseaseTypes[type]?.code || ''
}

/**
 * 根据疾病分类编码获取名称
 */
export function getDisClassName(code) {
  const entry = Object.values(diseaseTypes).find(d => d.code === code)
  return entry?.name || '未知'
}
