<template>
  <div class="image-debug-panel" v-if="visible">
    <div class="debug-header">
      <h3>🔍 图片加载调试面板</h3>
      <button @click="visible = false" class="close-btn">✕</button>
    </div>
    
    <div class="debug-content">
      <div class="debug-section">
        <h4>API响应数据</h4>
        <pre>{{ JSON.stringify(apiResponse, null, 2) }}</pre>
      </div>
      
      <div class="debug-section">
        <h4>处理后的图片URL</h4>
        <div class="url-display">
          <code>{{ imageUrl }}</code>
        </div>
        <button v-if="imageUrl" @click="testImageUrl" class="test-btn">
          测试URL是否可访问
        </button>
      </div>
      
      <div class="debug-section">
        <h4>URL候选列表</h4>
        <div v-for="(url, index) in urlCandidates" :key="index" class="url-candidate">
          <span class="index">#{{ index + 1 }}</span>
          <code>{{ url }}</code>
          <button @click="testCandidateUrl(url)" class="test-small-btn">测试</button>
        </div>
      </div>
      
      <div class="debug-section">
        <h4>图片加载状态</h4>
        <div class="status-display" :class="loadStatus">
          <span class="status-icon">{{ statusIcon }}</span>
          <span class="status-text">{{ statusText }}</span>
        </div>
        <div v-if="loadError" class="error-display">
          <p>错误信息: {{ loadError }}</p>
        </div>
      </div>
      
      <div class="debug-section">
        <h4>可用字段</h4>
        <div class="fields-list">
          <span v-for="field in availableFields" :key="field" class="field-tag">
            {{ field }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { imageUtils } from '../api/api'

export default {
  name: 'ImageDebugPanel',
  props: {
    apiResponse: {
      type: Object,
      default: null
    },
    imageUrl: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      visible: false,
      loadStatus: 'unknown',
      loadError: null,
      urlCandidates: []
    }
  },
  computed: {
    availableFields() {
      if (!this.apiResponse) return []
      return Object.keys(this.apiResponse)
    },
    statusIcon() {
      switch(this.loadStatus) {
        case 'success': return '✅'
        case 'error': return '❌'
        case 'loading': return '⏳'
        default: return '❓'
      }
    },
    statusText() {
      switch(this.loadStatus) {
        case 'success': return '加载成功'
        case 'error': return '加载失败'
        case 'loading': return '加载中...'
        default: return '未知状态'
      }
    }
  },
  methods: {
    show() {
      this.visible = true
      this.generateUrlCandidates()
    },
    generateUrlCandidates() {
      if (!this.apiResponse) return
      
      const fieldCandidates = [
        'imageUrl', 'image_url', 'image',
        'photoUrl', 'photo_url', 'photo',
        'pictureUrl', 'picture_url', 'picture',
        'imgUrl', 'img_url', 'img'
      ]
      
      this.urlCandidates = []
      
      for (const field of fieldCandidates) {
        if (this.apiResponse[field]) {
          const url = this.apiResponse[field]
          if (!url.startsWith('http://') && !url.startsWith('https://') && !url.startsWith('data:')) {
            this.urlCandidates.push('http://47.108.52.206:8082' + (url.startsWith('/') ? '' : '/') + url)
            this.urlCandidates.push('http://localhost' + (url.startsWith('/') ? '' : '/') + url)
          } else {
            this.urlCandidates.push(url)
          }
        }
      }
      
      // 去重
      this.urlCandidates = [...new Set(this.urlCandidates)]
    },
    testImageUrl() {
      if (!this.imageUrl) return
      this.testUrl(this.imageUrl)
    },
    testCandidateUrl(url) {
      this.testUrl(url)
    },
    testUrl(url) {
      this.loadStatus = 'loading'
      this.loadError = null
      
      const img = new Image()
      img.crossOrigin = 'anonymous'
      
      img.onload = () => {
        console.log('✅ 图片加载成功:', url)
        this.loadStatus = 'success'
        alert(`✅ 图片加载成功！\n尺寸: ${img.width}x${img.height}`)
      }
      
      img.onerror = (err) => {
        console.error('❌ 图片加载失败:', url, err)
        this.loadStatus = 'error'
        this.loadError = '加载失败，请检查URL是否正确'
        alert(`❌ 图片加载失败！\nURL: ${url}`)
      }
      
      img.src = url
    }
  }
}
</script>

<style scoped>
.image-debug-panel {
  position: fixed;
  top: 20px;
  right: 20px;
  width: 500px;
  max-height: 80vh;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.3);
  z-index: 10000;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.debug-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.debug-header h3 {
  margin: 0;
  font-size: 16px;
}

.close-btn {
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: rgba(255,255,255,0.3);
}

.debug-content {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.debug-section {
  margin-bottom: 24px;
}

.debug-section h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.debug-section pre {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 11px;
  max-height: 200px;
  overflow-y: auto;
}

.url-display code {
  background: #f0f4ff;
  padding: 12px;
  border-radius: 8px;
  display: block;
  word-break: break-all;
  font-size: 12px;
  color: #667eea;
  border: 1px solid #d6dcff;
}

.test-btn {
  margin-top: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.test-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.url-candidate {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f9f9f9;
  padding: 8px 12px;
  border-radius: 6px;
  margin-bottom: 8px;
}

.url-candidate .index {
  background: #667eea;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
}

.url-candidate code {
  flex: 1;
  font-size: 11px;
  word-break: break-all;
  color: #555;
}

.test-small-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  flex-shrink: 0;
}

.status-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  background: #f5f5f5;
}

.status-display.success {
  background: #d4edda;
  color: #155724;
}

.status-display.error {
  background: #f8d7da;
  color: #721c24;
}

.status-display.loading {
  background: #fff3cd;
  color: #856404;
}

.status-icon {
  font-size: 24px;
}

.status-text {
  font-weight: 500;
}

.error-display {
  margin-top: 12px;
  padding: 12px;
  background: #fff3f3;
  border-radius: 8px;
  color: #c0392b;
}

.fields-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.field-tag {
  background: #e8f4fd;
  color: #2980b9;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
</style>