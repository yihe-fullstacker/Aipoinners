<template>
  <div class="dashboard container">
    <h1>医学影像分析</h1>
    
    <UploadImage @upload-complete="handleUploadComplete" />
    
    <AnalysisResult v-if="currentAnalysis" :analysis="currentAnalysis" />
    
    <div v-else class="welcome-message">
      <h2>欢迎使用AI医学影像辅助诊断系统</h2>
      <p>请上传您的医学影像文件开始分析</p>
      <div class="features">
        <div class="feature">
          <h3>快速分析</h3>
          <p>基于深度学习算法，快速识别病变</p>
        </div>
        <div class="feature">
          <h3>精准识别</h3>
          <p>高准确率的病变检测和定位</p>
        </div>
        <div class="feature">
          <h3>专业报告</h3>
          <p>生成详细的诊断报告和建议</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import UploadImage from '../components/UploadImage.vue'
import AnalysisResult from '../components/AnalysisResult.vue'

export default {
  name: 'Dashboard',
  components: {
    UploadImage,
    AnalysisResult
  },
  setup() {
    const store = useStore()
    const currentAnalysis = computed(() => store.state.currentAnalysis)

    const handleUploadComplete = (analysis) => {
      store.commit('setAnalysis', analysis)
    }

    return {
      currentAnalysis,
      handleUploadComplete
    }
  }
}
</script>

<style scoped>
.welcome-message {
  text-align: center;
  margin: 3rem 0;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.feature {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.feature h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}
</style>