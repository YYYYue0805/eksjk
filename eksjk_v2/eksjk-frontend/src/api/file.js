/**
 * 文件管理 API 接口
 */
import request from '@/utils/request'

/**
 * 上传文件
 * @param {FormData} formData - 包含file, patientId, category
 */
export function uploadFile(formData) {
  return request({
    url: '/api/files/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 获取文件下载URL
 * @param {string} path - 文件路径
 */
export function getFileDownloadUrl(path) {
  return `/api/files/download?path=${encodeURIComponent(path)}`
}

/**
 * 获取文件流URL（用于影像预览）
 * @param {string} path - 文件路径
 */
export function getFileStreamUrl(path) {
  return `/api/files/stream?path=${encodeURIComponent(path)}`
}

/**
 * 携带 token 获取文件 blob URL（用于需要鉴权的图片预览）
 * @param {string} path - 文件路径
 * @returns {Promise<string>} blob URL
 */
export function fetchFileBlobUrl(path) {
  return request({
    url: '/api/files/stream',
    method: 'get',
    params: { path },
    responseType: 'blob'
  }).then(res => {
    // 拦截器对 blob 类型返回整个 response 对象
    const blob = res.data || res
    return URL.createObjectURL(blob)
  })
}

/**
 * 获取某患者的文件列表
 * @param {string} patientId - 患者ID
 * @param {string} category - 文件分类
 */
export function getFileList(patientId, category) {
  const params = {}
  if (category) params.category = category
  return request({
    url: `/api/files/patient/${patientId}`,
    method: 'get',
    params
  })
}

/**
 * 删除文件
 * @param {string} path - 文件路径
 */
export function deleteFile(path) {
  return request({
    url: '/api/files',
    method: 'delete',
    params: { path }
  })
}

/**
 * 更新文件备注
 * @param {string} path - 文件路径
 * @param {string} note - 备注内容
 */
export function updateFileNote(path, note) {
  return request({
    url: '/api/files/note',
    method: 'put',
    params: { path, note }
  })
}

/**
 * 批量打包下载
 * @param {Array<string>} patientIds - 患者ID列表
 */
export function batchDownload(patientIds) {
  return request({
    url: '/api/files/batch-download',
    method: 'post',
    data: patientIds,
    responseType: 'blob'
  })
}
