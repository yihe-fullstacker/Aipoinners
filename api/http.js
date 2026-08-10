// api/http.js — 网络请求封装
// 完全对齐 aipionners-porject2/src/api/api.js 的请求/响应拦截器逻辑
// 被 api/auth.js, api/medical.js, api/doctor.js 等模块 import

const BASE_URL = 'http://47.108.52.206:8082/api'
const TOKEN_KEY = 'medical_auth_token'

const http = {
  request(config) {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync(TOKEN_KEY)
      if (!config.header) config.header = {}
      if (token) {
        config.header['Authorization'] = `Bearer ${token}`
      }
      if (config.data && !(config.data.constructor && config.data.constructor.name === 'FormData')) {
        config.header['Content-Type'] = 'application/json'
      }

      uni.request({
        url: BASE_URL + config.url,
        method: config.method || 'GET',
        data: config.data,
        header: config.header,
        timeout: config.timeout || 30000,
        success: (res) => {
          // 401 统一处理 — 对齐 Web 端 401 逻辑
          if (res.statusCode === 401) {
            uni.removeStorageSync(TOKEN_KEY)
            uni.removeStorageSync('medical_user_info')
            uni.reLaunch({ url: '/pages/login/login' })
            return reject(new Error('UNAUTHORIZED'))
          }
          if (res.statusCode >= 200 && res.statusCode < 300) {
            resolve(res.data)
          } else {
            uni.showToast({ title: res.data?.error || res.data?.message || '请求失败', icon: 'none' })
            reject(res.data)
          }
        },
        fail: (err) => {
          uni.showToast({ title: '网络异常，请检查连接', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  get(url, params) { return this.request({ url, method: 'GET', data: params }) },
  post(url, data, timeout) { return this.request({ url, method: 'POST', data, timeout }) },
  put(url, data) { return this.request({ url, method: 'PUT', data }) },
  delete(url, data) { return this.request({ url, method: 'DELETE', data }) },

  // 文件上传 — 对齐 Web 端 FormData + axios.post
  upload(url, filePath, name = 'image', formData = {}) {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync(TOKEN_KEY)
      uni.uploadFile({
        url: BASE_URL + url,
        filePath,
        name,
        formData,
        header: token ? { 'Authorization': `Bearer ${token}` } : {},
        timeout: 60000,
        success: (res) => {
          if (res.statusCode === 401) {
            uni.removeStorageSync(TOKEN_KEY)
            uni.reLaunch({ url: '/pages/login/login' })
            return reject(new Error('UNAUTHORIZED'))
          }
          try { resolve(JSON.parse(res.data)) }
          catch { reject(res.data) }
        },
        fail: reject
      })
    })
  }
}

// 图片URL工具 — 复刻 imageUtils
export const imageUtils = {
  ensureImageUrl(imagePath) {
    if (!imagePath) return null
    if (/^[a-zA-Z]:[\\/]/.test(imagePath) || imagePath.startsWith('\\\\') || imagePath.startsWith('file:')) {
      return null
    }
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://') || imagePath.startsWith('data:')) {
      return imagePath
    }
    const normalizedPath = imagePath.startsWith('/') ? imagePath : '/' + imagePath
    return `http://47.108.52.206:8082${normalizedPath}`
  },
  extractImageUrl(responseData) {
    if (!responseData) return null
    const fields = ['imageUrl', 'image_url', 'image', 'photoUrl', 'photo_url', 'pictureUrl', 'imgUrl']
    for (const field of fields) {
      if (responseData[field]) return this.ensureImageUrl(responseData[field])
    }
    if (responseData.data) {
      for (const field of fields) {
        if (responseData.data[field]) return this.ensureImageUrl(responseData.data[field])
      }
    }
    return null
  }
}

export default http
