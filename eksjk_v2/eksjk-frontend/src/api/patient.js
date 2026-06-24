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

/**
 * 根据疾病分类编码获取路由 key
 */
export function getDiseaseTypeByCode(code) {
  const entry = Object.entries(diseaseTypes).find(([key, val]) => val.code === code)
  return entry ? entry[0] : 'eltm'
}

// ==================== 家庭关联 API ====================

/**
 * 按病历号精确搜索患者
 * @param {string} medrecNum - 病历号
 */
export function searchPatientByMedrec(medrecNum) {
  return request({
    url: '/api/patients/search-by-medrec',
    method: 'get',
    params: { medrecNum }
  })
}

/**
 * 查询同一家庭的所有患者
 * @param {string} familyId - 家庭分组ID
 */
export function getFamilyMembers(familyId) {
  return request({
    url: `/api/patients/family/${familyId}`,
    method: 'get'
  })
}

/**
 * 将患者关联到目标患者的家庭
 * @param {string} patientId - 当前患者ID
 * @param {string} targetMedrecNum - 目标患者病历号
 */
export function linkFamilyMember(patientId, targetMedrecNum) {
  return request({
    url: `/api/patients/${patientId}/link-family`,
    method: 'put',
    data: { targetMedrecNum }
  })
}

/**
 * 解除患者与家庭的关联
 * @param {string} patientId - 当前患者ID
 */
export function unlinkFamilyMember(patientId) {
  return request({
    url: `/api/patients/${patientId}/unlink-family`,
    method: 'put'
  })
}

/**
 * 重新分类患者疾病类型（ELTM → 目标病种）
 * @param {string} patientId - 患者ID
 * @param {string} targetDisClass - 目标疾病分类编码
 */
export function reclassifyPatient(patientId, targetDisClass) {
  return request({
    url: `/api/patients/${patientId}/reclassify`,
    method: 'put',
    data: { targetDisClass }
  })
}
