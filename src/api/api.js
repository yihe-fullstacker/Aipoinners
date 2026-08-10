//作用：封装所有与后端通信的HTTP请求方法
//这一整块代码主要负责
//前端网络请求层的核心配置和API服务封装，主要负责前后端联调中的前端HTTP通信工作
//前端组件 (Vue/React)
//      ↓ 调用
//   api.js  ← 这是联调的关键
//      ↓ HTTP请求
//后端 Controller (@RequestMapping("/api/smart-qa"))
//把所有前后端联调代码都写到了api.js中，与后端代码controller层的URL路径一一对应，形成了前后端联调的桥梁。前端组件通过调用api.js中的方法来与后端进行数据交互，实现了前后端的无缝连接和协同工作。
// src/services/api.js - 合并版本
import axios from 'axios'
// 前端配置 - 根据当前访问域名自动解析后端地址，避免部署后仍指向本地或旧服务器
const DEFAULT_API_PORT = '8082'

const resolveRuntimeOrigin = (defaultPort = DEFAULT_API_PORT) => {
  if (typeof window === 'undefined' || !window.location) {
    return `http://localhost:${defaultPort}`
  }

  const { protocol, hostname, port } = window.location
  const safeProtocol = protocol === 'https:' ? 'https:' : 'http:'

  if (port === defaultPort) {
    return `${safeProtocol}//${hostname}${port ? `:${port}` : ''}`
  }

  return `${safeProtocol}//${hostname}:${defaultPort}`
}

const API_ORIGIN = (process.env.VUE_APP_API_ORIGIN || '').trim() || resolveRuntimeOrigin()
const API_BASE_URL = `${API_ORIGIN}/api`
const IMAGE_BASE_URL = API_ORIGIN
const TOKEN_KEY = 'medical_auth_token'
const PUBLIC_PATHS = new Set(['/', '/home', '/landing', '/login', '/register'])
const API_DEBUG = false

const debugLog = (...args) => {
  if (API_DEBUG) {
    console.log(...args)
  }
}

const debugWarn = (...args) => {
  if (API_DEBUG) {
    console.warn(...args)
  }
}

// 统一的图片URL处理工具
export const imageUtils = {
  // 调试模式
  debugMode: false,
  
  // 确保图片URL是完整的
  ensureImageUrl: (imagePath) => {
    if (!imagePath) {
      debugWarn('ensureImageUrl - 输入为空')
      return null
    }

    if (/^[a-zA-Z]:[\\/]/.test(imagePath) || imagePath.startsWith('\\\\') || imagePath.startsWith('file:')) {
      debugWarn('ensureImageUrl - 检测到本地文件路径，无法直接作为前端图片地址使用:', imagePath)
      return null
    }
    
    if (imageUtils.debugMode) {
      debugLog('=== ensureImageUrl 调试信息 ===')
      debugLog('输入:', imagePath)
      debugLog('类型:', typeof imagePath)
      debugLog('长度:', imagePath.length)
    }
    
    // 如果已经是完整URL，直接返回
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://') || imagePath.startsWith('data:')) {
      debugLog('ensureImageUrl - 已是完整URL')
      return imagePath
    }
    
    // 尝试多种可能的URL构建方式
    const urlCandidates = []
    
    // 方式1: 直接拼接
    const normalizedPath = imagePath.startsWith('/') ? imagePath : '/' + imagePath
    urlCandidates.push(IMAGE_BASE_URL + normalizedPath)
    
    // 方式2: 尝试当前站点同源路径（兼容反向代理或同端口部署）
    if (typeof window !== 'undefined' && window.location?.origin) {
      urlCandidates.push(window.location.origin + normalizedPath)
    }
    
    debugLog('ensureImageUrl - 生成的URL候选:', urlCandidates)
    
    // 返回第一个候选（主URL）
    return urlCandidates[0]
  },
  
  // 从响应数据中提取并处理图片URL
  extractImageUrl: (responseData) => {
    debugLog('=== extractImageUrl 调试信息 ===')
    debugLog('完整响应数据:', responseData)
    debugLog('响应数据类型:', typeof responseData)
    
    if (!responseData) {
      debugWarn('extractImageUrl - 响应数据为空')
      return null
    }
    
    // 尝试多种可能的字段名
    const fieldCandidates = [
      'imageUrl', 'image_url', 'image',
      'photoUrl', 'photo_url', 'photo',
      'pictureUrl', 'picture_url', 'picture',
      'imgUrl', 'img_url', 'img'
    ]
    
    let foundUrl = null
    let foundField = null
    
    for (const field of fieldCandidates) {
      if (responseData[field]) {
        foundUrl = responseData[field]
        foundField = field
        debugLog(`extractImageUrl - 在字段 "${field}" 中找到值:`, foundUrl)
        break
      }
    }
    
    // 检查嵌套数据结构
    if (!foundUrl && responseData.data) {
      debugLog('extractImageUrl - 检查嵌套 data 字段')
      for (const field of fieldCandidates) {
        if (responseData.data[field]) {
          foundUrl = responseData.data[field]
          foundField = `data.${field}`
          debugLog(`extractImageUrl - 在嵌套字段 "${foundField}" 中找到值:`, foundUrl)
          break
        }
      }
    }
    
    if (!foundUrl) {
      debugWarn('extractImageUrl - 未找到任何图片字段，可用字段:', Object.keys(responseData))
      return null
    }
    
    const result = imageUtils.ensureImageUrl(foundUrl)
    debugLog('extractImageUrl - 最终结果:', result)
    return result
  },
  
  // 从响应数据中提取热力图base64
  extractHeatmapBase64: (responseData) => {
    debugLog('=== extractHeatmapBase64 调试信息 ===')
    debugLog('热力图响应数据:', responseData)
    
    if (!responseData) {
      debugWarn('extractHeatmapBase64 - 响应数据为空')
      return null
    }
    
    // 尝试多种可能的字段名
    const fieldCandidates = [
      'heatmap_base64', 'heatmapBase64', 'heatmap',
      'heatmap_data', 'heatmapData',
      'image_base64', 'imageBase64',
      'base64'
    ]
    
    let foundHeatmap = null
    let foundField = null
    
    for (const field of fieldCandidates) {
      if (responseData[field]) {
        foundHeatmap = responseData[field]
        foundField = field
        debugLog(`extractHeatmapBase64 - 在字段 "${field}" 中找到数据，长度:`, foundHeatmap.length)
        break
      }
    }
    
    // 检查嵌套数据结构
    if (!foundHeatmap && responseData.data) {
      debugLog('extractHeatmapBase64 - 检查嵌套 data 字段')
      for (const field of fieldCandidates) {
        if (responseData.data[field]) {
          foundHeatmap = responseData.data[field]
          foundField = `data.${field}`
          debugLog(`extractHeatmapBase64 - 在嵌套字段 "${foundField}" 中找到数据，长度:`, foundHeatmap.length)
          break
        }
      }
    }
    
    if (!foundHeatmap) {
      debugWarn('extractHeatmapBase64 - 未找到任何热力图字段，可用字段:', Object.keys(responseData))
      return null
    }
    
    // 检查并移除可能的data:image前缀
    if (foundHeatmap.startsWith('data:')) {
      const commaIndex = foundHeatmap.indexOf(',')
      if (commaIndex !== -1) {
        foundHeatmap = foundHeatmap.substring(commaIndex + 1)
        debugLog('extractHeatmapBase64 - 移除了data:image前缀')
      }
    }
    
    return foundHeatmap
  },
  
  // 生成占位图
  generatePlaceholder: (width = 400, height = 300, text = '图片加载失败') => {
    const canvas = document.createElement('canvas')
    canvas.width = width
    canvas.height = height
    const ctx = canvas.getContext('2d')
    
    // 绘制背景
    ctx.fillStyle = '#f0f0f0'
    ctx.fillRect(0, 0, width, height)
    
    // 绘制文字
    ctx.fillStyle = '#999'
    ctx.font = '16px sans-serif'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillText(text, width / 2, height / 2)
    
    return canvas.toDataURL('image/png')
  }
}

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
})

// 创建不需要认证的axios实例（用于医学影像分析等公开接口）
const noAuthClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
})

// 请求拦截器，自动添加token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 确保JSON请求设置正确的Content-Type
    if (config.data && !(config.data instanceof FormData)) {
      config.headers['Content-Type'] = 'application/json'
    }
    debugLog('API请求:', config.method?.toUpperCase(), config.url, config.data ? '有数据' : '无数据')
    if (config.data && !(config.data instanceof FormData)) {
      debugLog('请求数据:', JSON.stringify(config.data))
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// noAuthClient 的请求拦截器 - 不添加 Authorization header
noAuthClient.interceptors.request.use(
  (config) => {
    debugLog('NoAuth API请求:', config.method?.toUpperCase(), config.url, config.data ? '有数据' : '无数据')
    if (config.data && !(config.data instanceof FormData)) {
      debugLog('请求数据:', JSON.stringify(config.data))
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// noAuthClient 的响应拦截器
noAuthClient.interceptors.response.use(
  (response) => {
    debugLog('NoAuth API响应成功:', response.config.url)
    return response
  },
  (error) => {
    const errorInfo = {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    }
    console.error('NoAuth API调用错误:', errorInfo)
    if (error.response?.data) {
      console.error('错误响应详情:', error.response.data)
    }
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    debugLog('API响应成功:', response.config.url)
    return response
  },
  (error) => {
    const errorInfo = {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    }
    console.error('API调用错误:', errorInfo)
    // 详细输出错误响应数据
    if (error.response?.data) {
      console.error('错误响应详情:', JSON.stringify(error.response.data, null, 2))
    }
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      const currentPath = window.location.pathname
      if (!PUBLIC_PATHS.has(currentPath)) {
        window.location.href = '/'
      }
    }
    return Promise.reject(error)
  }
)

// 统一导出所有API
//API接口封装-联调的桥梁
export const authAPI = {
  login: (credentials) => noAuthClient.post('/auth/login', credentials, { timeout: 12000 }),
  register: (userData) => noAuthClient.post('/auth/register', userData, { timeout: 12000 }),
  getCaptcha: () => noAuthClient.get('/auth/captcha', { timeout: 12000 }),
  loginByPhone: (phoneNumber, verifyCode) => noAuthClient.post('/auth/login/phone', { phoneNumber, verifyCode })
}

// 短信验证API
export const smsAPI = {
  sendSms: (phoneNumber) => noAuthClient.post('/sms/send', { phoneNumber }),
  verifySms: (phoneNumber, verifyCode) => noAuthClient.post('/sms/verify', { phoneNumber, verifyCode })
}

export const medicalAPI = {
  // 上传并分析医学影像
  analyzeImage: (imageFile, complaint) => {
    const formData = new FormData()
    formData.append('image', imageFile)
    if (complaint && typeof complaint === 'string' && complaint.trim().length > 0) {
      formData.append('complaint', complaint.trim())
    }
    
    return apiClient.post('/medical/analyze', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000 // 上传需要更长时间
    })
  },

  // DeepSeek 智能分析
  analyzeImageDeepseek: (imageFile) => {
    const formData = new FormData()
    formData.append('image', imageFile)
    
    return apiClient.post('/medical/analyze-deepseek', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000
    })
  },

  // 获取历史诊断报告列表
  getReports: () => {
    return apiClient.get('/medical/reports')
  },

  // 获取特定报告详情
  getReportDetail: (reportId) => {
    return apiClient.get(`/medical/reports/${reportId}`)
  },

  getReportHeatmap: (reportId) => {
    return apiClient.get(`/medical/reports/${reportId}/heatmap`)
  },

  // 删除报告
  deleteReport: (reportId) => {
    return apiClient.delete(`/medical/reports/${reportId}`)
  }
}

export const assistantAPI = {
  runWorkflow: (payload) => apiClient.post('/assistant/workflow', payload, {
    timeout: 65000
  }),
  runWorkflowStream: (payload, onChunk, onComplete, onError, abortSignal = null) => {
    const token = localStorage.getItem(TOKEN_KEY)
    return fetch(`${API_BASE_URL}/assistant/workflow-stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
      },
      body: JSON.stringify(payload),
      signal: abortSignal
    }).then(async (response) => {
      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`HTTP ${response.status}: ${errorText}`)
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8')
      let buffer = ''

      const flushEvent = (eventText) => {
        let eventName = 'message'
        const dataLines = []

        eventText.split(/\r?\n/).forEach(line => {
          if (line.startsWith('event:')) {
            eventName = line.slice(6).trim()
          } else if (line.startsWith('data:')) {
            dataLines.push(line.slice(5).trimStart())
          }
        })

        const data = dataLines.join('\n')
        if ((eventName === 'message' || eventName === 'chunk' || eventName === '') && data && data !== '[DONE]') {
          onChunk(data)
        }
      }

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })

        const events = buffer.split(/\r?\n\r?\n/)
        buffer = events.pop() || ''
        events.forEach(flushEvent)
      }

      buffer += decoder.decode()
      if (buffer.trim()) {
        flushEvent(buffer)
      }
      onComplete()
    }).catch((error) => {
      onError(error)
      throw error
    })
  }
}

//资源获取API
export const imageAPI = {
  getImageUrl: (imageId) => `${API_BASE_URL}/images/${imageId}`,
  
  getImage: (imageId) => noAuthClient.get(`/images/${imageId}`, {
    responseType: 'blob'
  })
}

/*
//如下的代码块前端直连Python AI服务的完整通信管道。
const aiClient = apiClient

*/
// Python AI requests go through the Java backend proxy in every environment.
const aiClient = apiClient

export const aiAPI = {
  analyzePython: (imageFile) => {
    const formData = new FormData()
    formData.append('image', imageFile)
    return aiClient.post('/medical/python/analyze', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  analyzeV2: (imageFile) => {
    const formData = new FormData()
    formData.append('image', imageFile)
    return aiClient.post('/medical/python/analyze-v2', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  analyzeBase64: (imageBase64) => {
    return aiClient.post('/medical/python/analyze-base64', { image: imageBase64 })
  },
  evaluateModel: (imagesBase64, labels) => {
    return aiClient.post('/medical/python/evaluate-model', { images: imagesBase64, labels })
  },
  compareModels: (imagesBase64, labels) => {
    return aiClient.post('/medical/python/compare-models', { images: imagesBase64, labels })
  },
  explainPrediction: (imageBase64) => {
    return aiClient.post('/medical/python/explain-prediction', { image: imageBase64 })
  },
  getUncertainCases: (imagesBase64, top_k = 10) => {
    return aiClient.post('/medical/python/uncertain-cases', { images: imagesBase64, top_k })
  },
  generateHeatmap: (imageFileOrBase64) => {
    if (typeof imageFileOrBase64 === 'string') {
      return aiClient.post('/medical/python/heatmap', { image: imageFileOrBase64 })
    }
    const formData = new FormData()
    formData.append('image', imageFileOrBase64)
    return aiClient.post('/medical/python/heatmap', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
// 智能问答API//普通聊天
export const smartQaAPI = {
  chat: (memoryId, message, webSearchEnabled = false) => {
    const params = new URLSearchParams()
    params.append('memoryId', memoryId)
    params.append('message', message)
    params.append('webSearchEnabled', webSearchEnabled)
    return apiClient.post('/smart-qa/chat', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
  },
  chatStream: (memoryId, message, onChunk, onComplete, onError, webSearchEnabled = false, abortSignal = null) => {
    debugLog('chatStream call', { memoryId, webSearchEnabled })
    const params = new URLSearchParams()
    params.append('memoryId', memoryId)
    params.append('message', message)
    params.append('webSearchEnabled', webSearchEnabled)
    //流式聊天请求
    const token = localStorage.getItem(TOKEN_KEY)
    return fetch(`${API_BASE_URL}/smart-qa/chat-stream`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/x-www-form-urlencoded',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
      },
      body: params,
      signal: abortSignal
    }).then(async (response) => {
      debugLog('chatStream response', response.status, response.statusText)
      
      if (!response.ok) {
        const errorText = await response.text()
        console.error('chatStream 错误响应:', errorText)
        throw new Error(`HTTP ${response.status}: ${errorText}`)
      }
      
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      try {
        while (true) {
          const { done, value } = await reader.read()
          if (done) break
          const chunk = decoder.decode(value, { stream: true })
          if (chunk) {
            onChunk(chunk)
          }
        }
        onComplete()
      } catch (error) {
        console.error('chatStream 读取响应出错:', error)
        onError(error)
        throw error
      }
    }).catch((error) => {
      console.error('chatStream 请求出错:', error)
      onError(error)
      throw error
    })
  },
  //带图片的智能问答
  chatWithImage: (formData) => {
    return apiClient.post('/smart-qa/chat-with-image', formData, {
      // 视觉模型分析文件通常超过 30 秒，避免前端误判为网络异常
      timeout: 180000
    })
  },
  chatWithImageStream: (formData, onChunk, onComplete, onError, abortSignal = null) => {
    debugLog('chatWithImageStream call')
    
    const token = localStorage.getItem(TOKEN_KEY)
    const headers = {
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    }
    //带图片的流式聊天请求
    return fetch(`${API_BASE_URL}/smart-qa/chat-with-image-stream`, {
      method: 'POST',
      headers: headers,
      body: formData,
      signal: abortSignal
    }).then(async (response) => {
      debugLog('chatWithImageStream response', response.status, response.statusText)
      
      if (!response.ok) {
        const errorText = await response.text()
        console.error('chatWithImageStream 错误响应:', errorText)
        throw new Error(`HTTP ${response.status}: ${errorText}`)
      }
      
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      const isEventStream = (response.headers.get('content-type') || '').includes('text/event-stream')
      let buffer = ''

      const flushSseEvent = (eventText) => {
        let eventName = 'message'
        const dataLines = []

        eventText.split(/\r?\n/).forEach(line => {
          if (line.startsWith('event:')) {
            eventName = line.slice(6).trim()
          } else if (line.startsWith('data:')) {
            dataLines.push(line.slice(5).trimStart())
          }
        })

        if (eventName === 'message') {
          const data = dataLines.join('\n')
          if (data && data !== '[DONE]') {
            onChunk(data)
          }
        }
      }

      try {
        while (true) {
          const { done, value } = await reader.read()
          if (done) break
          const chunk = decoder.decode(value, { stream: true })
          if (isEventStream) {
            buffer += chunk
            const events = buffer.split(/\r?\n\r?\n/)
            buffer = events.pop() || ''
            events.forEach(flushSseEvent)
          } else if (chunk) {
            onChunk(chunk)
          }
        }

        const tail = decoder.decode()
        if (isEventStream) {
          buffer += tail
          if (buffer.trim()) {
            flushSseEvent(buffer)
          }
        } else if (tail) {
          onChunk(tail)
        }
        onComplete()
      } catch (error) {
        console.error('chatWithImageStream 读取响应出错:', error)
        throw error
      }
    }).catch((error) => {
      console.error('chatWithImageStream 请求出错:', error)
      onError(error)
      throw error
    })
  }
}

// 健康咨询预约API
export const healthConsultationAPI = {
  reserve: (data) => {
    return apiClient.post('/health-consultation/reserve', data)
  },
  query: (phone) => {
    return apiClient.get(`/health-consultation/query/${phone}`)
  }
}

export default apiClient
