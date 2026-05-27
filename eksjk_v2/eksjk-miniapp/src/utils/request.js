/**
 * 统一请求封装
 * 基于 uni.request 封装，自动附加 Token 和 OpenID
 */

const BASE_URL = 'http://localhost:8080'

/**
 * 发起请求
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径（不含 BASE_URL）
 * @param {string} options.method - 请求方法
 * @param {Object} options.data - 请求数据
 * @param {Object} options.header - 额外请求头
 * @returns {Promise}
 */
export function request(options) {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token') || ''
    const openid = uni.getStorageSync('openid') || ''

    const header = {
      'Content-Type': 'application/json',
      ...(token ? { 'satoken': token } : {}),
      ...(openid ? { 'X-Openid': openid } : {}),
      ...(options.header || {})
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        const { statusCode, data } = res

        // 401 未登录
        if (statusCode === 401 || (data && data.code === 401)) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('openid')
          uni.navigateTo({ url: '/pages-sub/login/index' })
          reject(new Error('未登录或登录已过期'))
          return
        }

        // 业务错误
        if (data && data.code !== 200) {
          const msg = data.message || '请求失败'
          uni.showToast({ title: msg, icon: 'none', duration: 2000 })
          reject(new Error(msg))
          return
        }

        resolve(data)
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常，请稍后重试', icon: 'none', duration: 2000 })
        reject(err)
      }
    })
  })
}

/** GET 请求 */
export function get(url, data) {
  return request({ url, method: 'GET', data })
}

/** POST 请求 */
export function post(url, data) {
  return request({ url, method: 'POST', data })
}

/** PUT 请求 */
export function put(url, data) {
  return request({ url, method: 'PUT', data })
}

/** DELETE 请求 */
export function del(url, data) {
  return request({ url, method: 'DELETE', data })
}

export default { request, get, post, put, del }
