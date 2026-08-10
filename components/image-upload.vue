<template>
  <!-- components/image-upload.vue — 医学影像上传组件，替代 UploadImage -->
  <view class="upload-wrap">
    <view v-if="!imagePath" class="upload-zone" @tap="chooseImage">
      <text class="upload-icon">📤</text>
      <text class="upload-title">点击上传医学影像</text>
      <text class="upload-hint">支持 CT、MRI、X光等格式</text>
    </view>
    <view v-else class="upload-preview" @tap="chooseImage">
      <image :src="imagePath" mode="aspectFill" class="preview-img" />
      <view class="preview-overlay">
        <text class="preview-text">点击重新选择</text>
      </view>
    </view>
    <button v-if="imagePath && !autoAnalyze" class="analyze-btn" :loading="analyzing" @tap="handleAnalyze">
      {{ analyzing ? 'AI分析中...' : '开始AI分析' }}
    </button>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { medicalAPI } from '@/api/medical'
import { useAppStore } from '@/stores/app'

const props = defineProps({
  autoAnalyze: { type: Boolean, default: false },
  complaint: { type: String, default: '' }
})
const emit = defineEmits(['upload-complete', 'analyze-start', 'analyze-complete', 'image-selected'])

const imagePath = ref('')
const analyzing = ref(false)
const appStore = useAppStore()

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      imagePath.value = res.tempFilePaths[0]
      emit('image-selected', res.tempFilePaths[0])
      if (props.autoAnalyze) handleAnalyze()
    }
  })
}

const handleAnalyze = async () => {
  if (!imagePath.value) return
  analyzing.value = true
  emit('analyze-start')
  try {
    const res = await medicalAPI.analyze(imagePath.value, props.complaint)
    appStore.setAnalysis(res)
    emit('analyze-complete', res)
    emit('upload-complete', res)
    uni.showToast({ title: '分析完成', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '分析失败，请重试', icon: 'none' })
  } finally {
    analyzing.value = false
  }
}

const reset = () => { imagePath.value = ''; analyzing.value = false }
defineExpose({ reset })
</script>

<style scoped>
.upload-wrap { display: flex; flex-direction: column; align-items: center; gap: 28rpx; }
.upload-zone {
  width: 100%; min-height: 320rpx; border-radius: 24rpx;
  border: 2rpx dashed rgba(125,211,252,0.2);
  background: rgba(8,15,26,0.6);
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 14rpx;
}
.upload-icon { font-size: 64rpx; }
.upload-title { font-size: 28rpx; color: #f0f6ff; font-weight: 600; }
.upload-hint { font-size: 24rpx; color: rgba(200,218,242,0.65); }
.upload-preview { width: 100%; height: 400rpx; border-radius: 24rpx; overflow: hidden; position: relative; }
.preview-img { width: 100%; height: 100%; }
.preview-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.2s; }
.preview-overlay:active { opacity: 1; }
.preview-text { color: #fff; font-size: 28rpx; }
.analyze-btn {
  width: 100%; height: 88rpx; border-radius: 20rpx;
  background: linear-gradient(135deg,#5eead4,#38bdf8); color: #032032;
  font-size: 28rpx; font-weight: 700; border: none;
}
</style>
