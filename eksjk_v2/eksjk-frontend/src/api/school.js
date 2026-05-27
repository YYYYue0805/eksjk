/**
 * 学校健康筛查 API
 */
import request from '@/utils/request'

/**
 * 获取学生列表
 */
export function getStudentList(params) {
  return request({ url: '/api/school/students', method: 'get', params })
}

/**
 * 获取学生详情
 */
export function getStudentDetail(id) {
  return request({ url: `/api/school/students/${id}`, method: 'get' })
}

/**
 * 新增学生
 */
export function createStudent(data) {
  return request({ url: '/api/school/students', method: 'post', data })
}

/**
 * 编辑学生
 */
export function updateStudent(id, data) {
  return request({ url: `/api/school/students/${id}`, method: 'put', data })
}

/**
 * 删除学生
 */
export function deleteStudent(id) {
  return request({ url: `/api/school/students/${id}`, method: 'delete' })
}

/**
 * 保存问卷数据
 * @param {string} studentId 学生ID
 * @param {string} type 问卷类型 (cchkn/cbq/mqzyfs/qzhd/pmbl/sthd/smxg)
 * @param {object} data 问卷数据
 */
export function saveQuestionnaire(studentId, type, data) {
  return request({ url: `/api/school/students/${studentId}/questionnaire/${type}`, method: 'put', data })
}

/**
 * 问卷类型配置
 */
export const questionnaireTypes = {
  cchkn: { name: 'SDQ 长处和困难问卷', short: 'SDQ' },
  cbq: { name: 'CBQ 儿童气质问卷', short: 'CBQ' },
  mqzyfs: { name: '母亲照养方式问卷', short: '照养方式' },
  qzhd: { name: '亲子活动问卷', short: '亲子活动' },
  pmbl: { name: '屏幕暴露问卷', short: '屏幕暴露' },
  sthd: { name: '身体活动问卷', short: '身体活动' },
  smxg: { name: 'CSHQ 儿童睡眠习惯问卷', short: 'CSHQ' }
}
