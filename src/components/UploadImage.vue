<template>
  <div class="upload-container">
    <div 
      class="upload-area"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
      @click="triggerFileInput"
      :class="{ 'drag-over': isDragOver, 'has-file': selectedFile }"
    >
      <div v-if="!selectedFile" class="upload-content">
        <div class="upload-icon">📁</div>
        <h3><span v-explain="'上传医学影像'">上传医学影像</span></h3>
        <p>拖拽文件到这里，或点击选择文件</p>
        <p class="upload-hint">
          <span v-explain="'支持格式'">支持格式</span>: DICOM (.dcm, .dicom), NIfTI (.nii, .nii.gz), PNG, JPG
        </p>
        <p class="upload-hint"><span v-explain="'最大文件大小'">最大文件大小</span>: 50MB</p>
      </div>
      
      <div v-else class="file-preview">
        <div class="preview-image-wrapper">
          <img 
            v-if="previewUrl" 
            :src="previewUrl" 
            alt="预览图"
            class="preview-image"
          >
          <div v-else class="preview-placeholder">
            <span class="file-icon">📄</span>
            <p>{{ selectedFile.name }}</p>
            <p class="file-size">{{ formatFileSize(selectedFile.size) }}</p>
          </div>
        </div>
        <div class="file-actions">
          <button @click.stop="removeFile" class="remove-btn" title="删除文件">
            🗑️ 删除
          </button>
          <button @click.stop="triggerFileInput" class="change-btn" title="更换文件">
            🔄 更换
          </button>
        </div>
      </div>
      
      <input
        ref="fileInput"
        type="file"
        @change="handleFileSelect"
        accept=".jpg,.jpeg,.png,.dicom,.dcm,.nii"
        style="display: none"
      >
    </div>
    
    <div v-if="errorMessage" class="error-message">
      <span class="error-icon">⚠️</span>
      {{ errorMessage }}
    </div>
    
    <div
      v-if="selectedFile && !errorMessage"
      :class="['upload-actions', { 'upload-actions--dual': !isUploadPage }]"
    >
      <button 
        v-if="isUploadPage"
        @click.stop="uploadFile" 
        class="upload-btn upload-btn--single" 
        :disabled="isProcessing"
      >
        <span v-if="uploadingPython">
          <span class="spinner"></span>
          上传中...
        </span>
        <span v-else class="btn-copy">
          <span class="btn-title" v-explain="'上传影像'">上传影像</span>
          <span class="btn-caption">安全提交影像并进入后续分析流程</span>
        </span>
      </button>

      <template v-else>
        <button 
          @click.stop="uploadFile" 
          class="upload-btn upload-btn--python" 
          :disabled="isProcessing"
        >
          <span v-if="uploadingPython">
            <span class="spinner"></span>
            分析中...
          </span>
          <span v-else class="btn-copy">
            <span class="btn-badge">推荐</span>
            <span class="btn-title" v-explain="'调用pythonAI分析'">调用 Python AI 分析</span>
            <span class="btn-caption">标准诊断流程 · 输出结构化报告</span>
          </span>
        </button>

        <button 
          @click.stop="analyzeDeepseek" 
          class="upload-btn upload-btn--deepseek" 
          :disabled="isProcessing"
        >
          <span v-if="uploadingDeepseek">
            <span class="spinner"></span>
            分析中...
          </span>
          <span v-else class="btn-copy">
            <span class="btn-badge">增强</span>
            <span class="btn-title" v-explain="'调用DeepSeek分析'">调用 DeepSeek 分析</span>
            <span class="btn-caption">语义理解增强 · 深层解释通道</span>
          </span>
        </button>
      </template>
    </div>

    <div v-if="uploadProgress > 0" class="progress-container">
      <div class="progress-bar">
        <div class="progress" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <span class="progress-text">{{ uploadProgress }}%</span>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { medicalAPI, imageUtils } from '../api/api'

export default {
  name: 'UploadImage',
  emits: ['upload-complete'],
  setup(props, { emit }) {
    const isDragOver = ref(false)
    const selectedFile = ref(null)
    const previewUrl = ref('')
    const uploadingPython = ref(false)
    const uploadingDeepseek = ref(false)
    const uploadProgress = ref(0)
    const errorMessage = ref('')
    const fileInput = ref(null)
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    
    const isProcessing = computed(() => uploadingPython.value || uploadingDeepseek.value)
    const isUploadPage = computed(() => route.name === 'UploadImage' || route.path === '/upload')

    // 允许的文件类型
    const allowedTypes = [
      'image/jpeg',
      'image/jpg', 
      'image/png',
      'application/dicom',
      'application/x-dicom'
    ]
    
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.dicom', '.dcm', '.nii', '.nii.gz']
    const maxFileSize = 50 * 1024 * 1024 // 50MB

    // 验证文件
    const validateFile = (file) => {
      errorMessage.value = ''
      
      // 检查文件大小
      if (file.size > maxFileSize) {
        errorMessage.value = '文件大小超过限制（最大50MB）'
        return false
      }
      
      // 检查文件扩展名
      const fileName = file.name.toLowerCase()
      const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext))
      
      if (!hasValidExtension) {
        errorMessage.value = '不支持的文件格式。仅支持: DICOM, NIfTI, PNG, JPG'
        return false
      }
      
      // 检查MIME类型（可选，因为某些浏览器可能不准确）
      if (file.type && !allowedTypes.includes(file.type.toLowerCase())) {
        // 如果扩展名有效，仍然允许（因为MIME类型可能不准确）
        if (!hasValidExtension) {
          errorMessage.value = '不支持的文件类型'
          return false
        }
      }
      
      return true
    }

    // 生成预览
    const generatePreview = (file) => {
      previewUrl.value = ''
      
      // 只对图片格式生成预览
      if (file.type && file.type.startsWith('image/')) {
        const reader = new FileReader()
        reader.onload = (e) => {
          previewUrl.value = e.target.result
        }
        reader.readAsDataURL(file)
      }
    }

    const handleDragOver = (e) => {
      e.preventDefault()
      isDragOver.value = true
    }

    const handleDragLeave = (e) => {
      e.preventDefault()
      isDragOver.value = false
    }

    const handleDrop = (e) => {
      e.preventDefault()
      isDragOver.value = false
      const files = e.dataTransfer.files
      if (files.length > 0) {
        handleFile(files[0])
      }
    }

    const triggerFileInput = () => {
      fileInput.value?.click()
    }

    const handleFileSelect = (e) => {
      const files = e.target.files
      if (files.length > 0) {
        handleFile(files[0])
      }
    }

    const handleFile = (file) => {
      if (validateFile(file)) {
        selectedFile.value = file
        generatePreview(file)
      } else {
        selectedFile.value = null
        previewUrl.value = ''
      }
    }

    const removeFile = () => {
      selectedFile.value = null
      previewUrl.value = ''
      errorMessage.value = ''
      uploadProgress.value = 0
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    }

    const uploadFile = async () => {
      if (!selectedFile.value || errorMessage.value || isProcessing.value) return

      uploadingPython.value = true
      uploadProgress.value = 0
      errorMessage.value = ''

      try {
        // 模拟上传进度
        const progressInterval = setInterval(() => {
          if (uploadProgress.value < 90) {
            uploadProgress.value += 10
          }
        }, 200)

        // 调用真实API上传并分析
        const response = await medicalAPI.analyzeImage(selectedFile.value)
        
        clearInterval(progressInterval)
        uploadProgress.value = 100

        if (response.data) {
          const analysisResult = response.data
          console.log('UploadImage - 收到API响应:', analysisResult)
          
          // 确保imageUrl是完整的URL
          analysisResult.imageUrl = imageUtils.extractImageUrl(analysisResult)
          console.log('UploadImage - 处理后的imageUrl:', analysisResult.imageUrl)

          store.commit('setAnalysis', analysisResult)
          emit('upload-complete', analysisResult)

          // 如果后端返回了分析报告 id，则跳转到分析报告页面（与 DeepSeek 一致）
          const reportId = analysisResult.id || analysisResult.reportId || (analysisResult.data && analysisResult.data.id)
          if (reportId) {
            router.push(`/analysis-report/${reportId}`)
          }
        }

        // 重置状态
        setTimeout(() => {
          removeFile()
          uploadingPython.value = false
        }, 1500)

      } catch (error) {
        console.error('上传失败:', error)
        const errorData = error.response?.data
        let errorMsg = '上传失败，请重试'
        
        if (errorData) {
          if (typeof errorData === 'string') {
            try {
              const parsed = JSON.parse(errorData)
              errorMsg = parsed.error || errorMsg
            } catch {
              errorMsg = errorData
            }
          } else if (errorData.error) {
            errorMsg = errorData.error
          } else if (errorData.message) {
            errorMsg = errorData.message
          }
        } else if (error.message) {
          errorMsg = error.message
        }
        
        errorMessage.value = errorMsg
        uploadingPython.value = false
        uploadProgress.value = 0
      }
    }

    const analyzeDeepseek = async () => {
      if (!selectedFile.value || errorMessage.value || isProcessing.value) return

      uploadingDeepseek.value = true
      uploadProgress.value = 0
      errorMessage.value = ''

      try {
        // 模拟上传进度
        const progressInterval = setInterval(() => {
          if (uploadProgress.value < 90) {
            uploadProgress.value += 10
          }
        }, 200)

        // 调用DeepSeek API
        const response = await medicalAPI.analyzeImageDeepseek(selectedFile.value)
        
        clearInterval(progressInterval)
        uploadProgress.value = 100

        if (response.data && response.data.id) {
           store.commit('setAnalysis', response.data)
           router.push(`/analysis-report/${response.data.id}`)
        } else {
           throw new Error("分析结果无效")
        }
        
        // 重置状态
        setTimeout(() => {
          removeFile()
          uploadingDeepseek.value = false
        }, 1500)

      } catch (error) {
        console.error('DeepSeek分析失败:', error)
        const errorData = error.response?.data
        let errorMsg = '分析失败，请重试'
        
        if (errorData) {
          if (typeof errorData === 'string') {
            try {
              const parsed = JSON.parse(errorData)
              errorMsg = parsed.error || errorMsg
            } catch {
              errorMsg = errorData
            }
          } else if (errorData.error) {
            errorMsg = errorData.error
          } else if (errorData.message) {
            errorMsg = errorData.message
          }
        } else if (error.message) {
          errorMsg = error.message
        }
        
        errorMessage.value = errorMsg
        uploadingDeepseek.value = false
        uploadProgress.value = 0
      }
    }


    return {
      isDragOver,
      selectedFile,
      previewUrl,
      uploadingPython,
      uploadingDeepseek,
      isProcessing,
      isUploadPage,
      uploadProgress,
      errorMessage,
      fileInput,
      handleDragOver,
      handleDragLeave,
      handleDrop,
      triggerFileInput,
      handleFileSelect,
      removeFile,
      uploadFile,
      analyzeDeepseek,
      formatFileSize
    }
  }
}
</script>

<style scoped>
.upload-container {
  margin: 0;
  display: grid;
  gap: 18px;
}

.upload-area {
  border: 1px dashed rgba(125, 211, 252, 0.28);
  border-radius: 24px;
  padding: 2.5rem 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(180deg, rgba(7, 20, 35, 0.68), rgba(5, 15, 27, 0.96));
  min-height: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.upload-area:hover,
.upload-area.drag-over {
  border-color: rgba(94, 234, 212, 0.42);
  background: linear-gradient(180deg, rgba(10, 27, 45, 0.82), rgba(5, 16, 30, 0.98));
  transform: translateY(-2px);
}

.upload-area.has-file {
  padding: 1.5rem;
  min-height: auto;
}

.upload-content {
  max-width: 420px;
  color: rgba(220, 235, 255, 0.78);
}

.upload-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  display: block;
}

.upload-content h3 {
  color: #f7fbff;
  margin-bottom: 0.5rem;
  font-size: 1.5rem;
}

.upload-hint {
  font-size: 0.9rem;
  color: rgba(185, 214, 244, 0.66);
  margin-top: 0.5rem;
}

.file-preview {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.preview-image-wrapper {
  width: 100%;
  max-height: 400px;
  border-radius: 18px;
  overflow: hidden;
  background: radial-gradient(circle at center, rgba(20, 61, 96, 0.4), rgba(4, 10, 20, 0.98) 74%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.preview-placeholder {
  padding: 2rem;
  text-align: center;
}

.file-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 0.5rem;
}

.preview-placeholder p {
  margin: 0.5rem 0;
  color: rgba(220, 235, 255, 0.78);
}

.file-size {
  font-size: 0.9rem;
  color: rgba(185, 214, 244, 0.66);
}

.file-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.remove-btn,
.change-btn {
  padding: 0.5rem 1rem;
  border: 1px solid rgba(125, 211, 252, 0.16);
  border-radius: 999px;
  background: rgba(4, 12, 24, 0.82);
  color: #eff8ff;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.remove-btn:hover {
  background: rgba(95, 24, 34, 0.4);
  border-color: rgba(239, 68, 68, 0.42);
  color: #ffd0d0;
}

.change-btn:hover {
  background: rgba(14, 35, 58, 0.92);
  border-color: rgba(94, 234, 212, 0.32);
  color: #d9fbff;
}

.error-message {
  padding: 1rem;
  background: rgba(94, 23, 33, 0.36);
  border: 1px solid rgba(239, 68, 68, 0.4);
  border-radius: 18px;
  color: #ffd1d1;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.error-icon {
  font-size: 1.2rem;
}

.upload-actions {
  margin-top: 1.5rem;
  display: grid;
  gap: 1rem;
}

.upload-actions--dual {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.upload-btn {
  position: relative;
  overflow: hidden;
  border: none;
  width: 100%;
  min-height: 84px;
  padding: 1.1rem 1.4rem;
  border-radius: 22px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  color: white;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.18);
}

.upload-btn > span {
  position: relative;
  z-index: 1;
}

.upload-btn::before {
  content: '';
  position: absolute;
  inset: 1px;
  border-radius: inherit;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.14), rgba(255, 255, 255, 0.02));
  opacity: 0.8;
}

.upload-btn::after {
  content: '';
  position: absolute;
  inset: auto -30% -40% auto;
  width: 140px;
  height: 140px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.24), rgba(255, 255, 255, 0));
  transform: translate3d(0, 0, 0);
  transition: transform 0.32s ease;
}

.upload-btn--single,
.upload-btn--python {
  background: linear-gradient(135deg, #153b84 0%, #2563eb 52%, #0f766e 100%);
}

.upload-btn--deepseek {
  background: linear-gradient(135deg, #0f172a 0%, #155e75 50%, #22c55e 100%);
}

.btn-copy {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.25rem;
  width: 100%;
  text-align: left;
}

.upload-btn--single .btn-copy {
  align-items: center;
  text-align: center;
}

.btn-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
  padding: 0 0.6rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.16);
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.btn-title {
  font-size: 1.06rem;
  font-weight: 800;
  line-height: 1.2;
}

.btn-caption {
  color: rgba(255, 255, 255, 0.78);
  font-size: 0.82rem;
  line-height: 1.4;
}

.upload-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 24px 44px rgba(15, 23, 42, 0.28);
}

.upload-btn:hover:not(:disabled)::after {
  transform: translate3d(-22px, -12px, 0);
}

.upload-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: rgba(226, 232, 240, 0.12);
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #38bdf8 0%, #5eead4 100%);
  transition: width 0.3s ease;
  border-radius: 4px;
}

.progress-text {
  font-size: 0.9rem;
  color: rgba(220, 235, 255, 0.78);
  font-weight: 600;
  min-width: 45px;
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-area {
    padding: 2rem 1rem;
  }
  
  .upload-icon {
    font-size: 3rem;
  }
  
  .file-actions {
    flex-direction: column;
  }

  .upload-actions--dual {
    grid-template-columns: 1fr;
  }
  
  .remove-btn,
  .change-btn {
    width: 100%;
  }
}
</style>
