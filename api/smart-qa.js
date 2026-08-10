// api/smart-qa.js — 智能问答API，被 pages/qa/chat.vue 调用
// 映射 Web 端 smartQaAPI: POST /api/smart-qa/chat, chat-stream, chat-with-image
import http from './http'

const BASE_URL = 'http://47.108.52.206:8082/api'
const TOKEN_KEY = 'medical_auth_token'

export const smartQaAPI = {
  chat: (memoryId, message, webSearchEnabled = false) =>
    http.post('/smart-qa/chat', { memoryId, message, webSearchEnabled }),

  chatWithImage: (filePath, memoryId, message) =>
    http.upload('/smart-qa/chat-with-image', filePath, 'image', {
      memoryId: memoryId || 'default', message: message || ''
    }),

  chatStream: (memoryId, message, onChunk, onComplete, onError, webSearchEnabled = false) => {
    const token = uni.getStorageSync(TOKEN_KEY)
    uni.request({
      url: `${BASE_URL}/smart-qa/chat-stream`,
      method: 'POST',
      data: { memoryId, message, webSearchEnabled },
      header: {
        'Content-Type': 'application/x-www-form-urlencoded',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
      },
      timeout: 65000,
      responseType: 'text',
      enableChunked: true,
      success: (res) => {
        if (res.statusCode === 200) { onChunk && onChunk(res.data); onComplete && onComplete() }
        else { onError && onError(new Error(`HTTP ${res.statusCode}`)) }
      },
      fail: (err) => { onError && onError(err) }
    })
  }
}
