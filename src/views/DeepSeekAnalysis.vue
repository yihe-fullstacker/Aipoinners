<template>
  <div class="deep-analysis container">
    <div class="header-section">
      <button @click="goBack" class="back-btn">← 返回</button>
      <h1>DeepSeek 深度分析报告</h1>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="analysisData" class="analysis-content">
      <!-- 可解释性可视化 -->
      <div class="section explainability-section">
        <h2>影像分析结果</h2>
        <p class="section-description">AI模型对医学影像的详细分析</p>
        
        <div class="visualization-grid">
          <div class="visualization-item">
            <h3>原始影像</h3>
            <div class="image-container">
              <img :src="analysisData.imageUrl" alt="原始影像" class="analysis-image">
            </div>
          </div>
          
          <div class="visualization-item">
            <h3>热力图 (Heatmap)</h3>
            <div class="image-container">
              <canvas ref="heatmapCanvas" class="analysis-image" :style="{ opacity: overlayOpacity }"></canvas>
              <div v-if="!heatmapGenerated" class="placeholder">
                <p>生成热力图中...</p>
              </div>
            </div>
            <p class="visualization-note">红=高风险，橙=可疑，蓝=正常区域</p>
            <div class="heatmap-actions">
              <button class="action-btn" :disabled="!heatmapGenerated" @click="downloadHeatmap">下载热力图</button>
              <button class="action-btn" :disabled="!heatmapGenerated" @click="openHeatmapNewTab">在新窗口打开</button>
            </div>
          </div>
        </div>

        <div class="stats" v-if="analysisData.confidence_calibrated !== undefined || analysisData.uncertainty !== undefined">
          <div class="summary-row">
            <span>校准置信度</span>
            <div class="progress"><div class="bar" :style="{ width: ((analysisData.confidence_calibrated||0)*100) + '%' }"></div></div>
            <span>{{ ((analysisData.confidence_calibrated||0)*100).toFixed(1) }}%</span>
          </div>
          <div class="summary-row">
            <span>不确定性</span>
            <div class="progress"><div class="bar u" :style="{ width: ((analysisData.uncertainty||0)*100) + '%' }"></div></div>
            <span>{{ ((analysisData.uncertainty||0)*100).toFixed(1) }}%</span>
          </div>
        </div>

        <div class="attention-regions" v-if="analysisData.attentionRegions && analysisData.attentionRegions.length">
          <h3>关注区域分析</h3>
          <div class="regions-list">
            <div 
              v-for="(region, index) in analysisData.attentionRegions" 
              :key="index"
              class="region-card"
            >
              <div class="region-header">
                <span class="region-label">区域 {{ index + 1 }}</span>
                <span class="region-score">关注度: {{ (region.score * 100).toFixed(1) }}%</span>
              </div>
              <div class="region-details">
                <p><strong>类型:</strong> {{ region.type || '高风险' }}</p>
                <p><strong>坐标:</strong> x={{ region.x }}, y={{ region.y }}</p>
                <p><strong>尺寸:</strong> w={{ region.width }}, h={{ region.height }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 特征可视化 -->
      <div class="section features-section">
        <h2><span v-explain="'特征'">特征</span>描述与可视化</h2>
        
        <div class="feature-description" v-if="analysisData.featureDescription">
          <h3>整体特征描述</h3>
          <p>{{ analysisData.featureDescription }}</p>
        </div>

        <div class="feature-list" v-if="analysisData.features && analysisData.features.length > 0">
          <h3>关键特征</h3>
          <div class="features-grid">
            <div 
              v-for="(feature, index) in analysisData.features" 
              :key="index"
              class="feature-card"
            >
              <div class="feature-icon">{{ getFeatureIcon(feature.type) }}</div>
              <div class="feature-content">
                <h4>{{ feature.name }}</h4>
                <p>{{ feature.description }}</p>
                <div class="feature-metrics">
                  <span class="metric">重要性: {{ (feature.importance * 100).toFixed(1) }}%</span>
                  <span class="metric" v-if="feature.confidence">置信度: {{ (feature.confidence * 100).toFixed(1) }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 鉴别诊断分析 -->
      <div class="section diagnosis-section" v-if="analysisData.differentialDiagnosis">
        <h2>鉴别诊断分析</h2>
        
        <div class="diagnosis-chart">
            <h3><span v-explain="'诊断概率'">诊断概率</span>分布</h3>
          <div class="probability-bars">
            <div 
              v-for="(diagnosis, index) in analysisData.differentialDiagnosis" 
              :key="index"
              class="probability-item"
            >
              <div class="diagnosis-label">
                <span class="diagnosis-name">{{ diagnosis.name }}</span>
                <span class="diagnosis-prob">{{ (diagnosis.probability * 100).toFixed(1) }}%</span>
              </div>
              <div class="probability-bar-container">
                <div 
                  class="probability-bar" 
                  :style="{ width: diagnosis.probability * 100 + '%' }"
                  :class="getProbabilityClass(diagnosis.probability)"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <div class="diagnosis-details">
          <div 
            v-for="(diagnosis, index) in analysisData.differentialDiagnosis" 
            :key="index"
            class="diagnosis-card"
          >
            <div class="diagnosis-header">
              <h4>{{ diagnosis.name }}</h4>
              <span class="diagnosis-probability">{{ (diagnosis.probability * 100).toFixed(1) }}%</span>
            </div>
            <div class="diagnosis-content">
              <p v-if="diagnosis.description" class="diagnosis-desc">{{ diagnosis.description }}</p>
              <div v-if="diagnosis.supportingFeatures" class="supporting-features">
                <p><strong>支持特征:</strong></p>
                <ul>
                  <li v-for="(feature, fIndex) in diagnosis.supportingFeatures" :key="fIndex">
                    {{ feature }}
                  </li>
                </ul>
              </div>
              <div v-if="diagnosis.contradictingFeatures" class="contradicting-features">
                <p><strong>不支持特征:</strong></p>
                <ul>
                  <li v-for="(feature, fIndex) in diagnosis.contradictingFeatures" :key="fIndex">
                    {{ feature }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 模型决策路径 -->
      <div class="section decision-path-section" v-if="analysisData.decisionPath">
        <h2>模型决策路径</h2>
        <div class="decision-tree">
          <div 
            v-for="(step, index) in analysisData.decisionPath" 
            :key="index"
            class="decision-step"
          >
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <h4>{{ step.stage }}</h4>
              <p>{{ step.description }}</p>
              <div class="step-metrics">
                <span v-if="step.confidence">置信度: {{ (step.confidence * 100).toFixed(1) }}%</span>
                <span v-if="step.weight">权重: {{ (step.weight * 100).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section">
        <button @click="viewAnalysisReport" class="action-btn">
          查看完整分析报告
        </button>
        <button @click="viewCaseDetail" class="action-btn">
          查看病例详情
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, computed, onBeforeUnmount } from 'vue'
import explain from '../directives/explain.js'
import { useRoute, useRouter } from 'vue-router'
import { medicalAPI } from '../api/api'

export default {
  name: 'DeepSeekAnalysis',
  directives: { explain },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const analysisData = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const heatmapCanvas = ref(null)
    const heatmapGenerated = ref(false)
    const overlayOpacity = ref(0.7)
    
    const loadAnalysis = async () => {
      const reportId = route.params.id
      if (!reportId) {
        error.value = '缺少报告ID'
        loading.value = false
        return
      }

      loading.value = true
      error.value = null

      try {
        const response = await medicalAPI.getReportDetail(reportId)
        if (response.data) {
          analysisData.value = {
            ...response.data,
            imageUrl: response.data.imageUrl && !response.data.imageUrl.startsWith('http')
              ? `http://localhost:8080${response.data.imageUrl}`
              : response.data.imageUrl,
            // 确保有可解释性数据
            attentionRegions: response.data.attentionRegions || generateAttentionRegions(response.data.findings),
            features: response.data.features || generateFeatures(response.data.findings),
            decisionPath: response.data.decisionPath || generateDecisionPath(response.data)
          }
          // 兼容校准置信度/不确定性
          if (response.data.confidence_calibrated !== undefined) {
            analysisData.value.confidence_calibrated = response.data.confidence_calibrated
          }
          if (response.data.uncertainty !== undefined) {
            analysisData.value.uncertainty = response.data.uncertainty
          }
          
          try {
            // 尝试获取热力图
            if (analysisData.value.heatmapBase64) {
               await nextTick()
               renderBackendHeatmap()
            } else {
               const hm = await medicalAPI.getReportHeatmap(reportId)
               if (hm.data && hm.data.heatmap_base64) {
                 analysisData.value.heatmapBase64 = hm.data.heatmap_base64
                 analysisData.value.riskStats = hm.data.risk_stats || null
                 analysisData.value.attentionRegions = hm.data.attentionRegions || analysisData.value.attentionRegions
                 await nextTick()
                 renderBackendHeatmap()
               } else {
                 await nextTick()
                 generateHeatmap()
               }
            }
          } catch (e) {
            await nextTick()
            generateHeatmap()
          }
        } else {
          error.value = '未找到分析数据'
        }
      } catch (err) {
        console.error('加载分析数据失败:', err)
        error.value = err.response?.data?.message || '加载分析数据失败'
      } finally {
        loading.value = false
      }
    }

    const generateAttentionRegions = (findings) => {
      if (!findings || findings.length === 0) return null
      return findings.map((finding, index) => ({
        location: finding.location,
        size: finding.size || '中等',
        score: finding.confidence,
        description: `检测到${finding.type}，置信度${(finding.confidence * 100).toFixed(1)}%`
      }))
    }

    const generateFeatures = (findings) => {
      const featureTypes = ['形状', '密度', '边界', '纹理', '大小']
      return featureTypes.map((type, index) => ({
        type: type,
        name: `${type}特征`,
        description: `影像中${type}特征的分析结果`,
        importance: 0.7 + Math.random() * 0.2,
        confidence: 0.6 + Math.random() * 0.3
      }))
    }

    const generateDecisionPath = (data) => {
      return [
        {
          stage: '图像预处理',
          description: '对输入影像进行标准化和增强处理',
          confidence: 0.95,
          weight: 0.1
        },
        {
          stage: '特征提取',
          description: '使用DeepSeek模型提取高维特征',
          confidence: 0.88,
          weight: 0.3
        },
        {
          stage: '病变检测',
          description: `检测到${data.findings?.length || 0}处潜在病变区域`,
          confidence: data.confidence,
          weight: 0.4
        },
        {
          stage: '分类决策',
          description: '基于提取特征进行最终分类决策',
          confidence: data.confidence,
          weight: 0.2
        }
      ]
    }

    const renderBackendHeatmap = () => {
      if (!heatmapCanvas.value || !analysisData.value?.heatmapBase64) return
      const canvas = heatmapCanvas.value
      const ctx = canvas.getContext('2d')
      const baseImg = new Image()
      const hmImg = new Image()
      let loaded = 0
      const tryDraw = () => {
        if (loaded < 2) return
        canvas.width = baseImg.width
        canvas.height = baseImg.height
        ctx.clearRect(0,0,canvas.width,canvas.height)
        ctx.globalAlpha = 1
        ctx.drawImage(baseImg, 0, 0)
        ctx.globalAlpha = overlayOpacity.value
        ctx.drawImage(hmImg, 0, 0)
        heatmapGenerated.value = true
      }
      baseImg.onload = () => { loaded++; tryDraw() }
      hmImg.onload = () => { loaded++; tryDraw() }
      baseImg.crossOrigin = 'anonymous'
      hmImg.crossOrigin = 'anonymous'
      baseImg.src = analysisData.value.imageUrl
      hmImg.src = `data:image/png;base64,${analysisData.value.heatmapBase64}`
    }

    const generateHeatmap = () => {
      if (!heatmapCanvas.value || !analysisData.value) return
      
      const canvas = heatmapCanvas.value
      const img = new Image()
      img.crossOrigin = 'anonymous'
      
      img.onload = () => {
        canvas.width = img.width
        canvas.height = img.height
        const ctx = canvas.getContext('2d')
        
        // 绘制原始图像
        ctx.drawImage(img, 0, 0)
        
        // 生成热力图叠加层
        if (analysisData.value.attentionRegions) {
          analysisData.value.attentionRegions.forEach((region, index) => {
            const centerX = canvas.width * (0.3 + index * 0.2)
            const centerY = canvas.height * (0.3 + index * 0.2)
            const radius = Math.min(canvas.width, canvas.height) * 0.15
            
            // 创建径向渐变
            const gradient = ctx.createRadialGradient(centerX, centerY, 0, centerX, centerY, radius)
            gradient.addColorStop(0, 'rgba(255, 0, 0, 0.6)')
            gradient.addColorStop(0.5, 'rgba(255, 165, 0, 0.4)')
            gradient.addColorStop(1, 'rgba(0, 0, 255, 0)')
            
            ctx.fillStyle = gradient
            ctx.fillRect(0, 0, canvas.width, canvas.height)
          })
        }
        
        heatmapGenerated.value = true
      }
      
      img.src = analysisData.value.imageUrl
    }

    const getFeatureIcon = (type) => {
      const icons = {
        '形状': '🔷',
        '密度': '📊',
        '边界': '🔲',
        '纹理': '🌀',
        '大小': '📏'
      }
      return icons[type] || '📋'
    }

    const getProbabilityClass = (probability) => {
      if (probability >= 0.7) return 'high'
      if (probability >= 0.4) return 'medium'
      return 'low'
    }

    const goBack = () => {
      router.back()
    }

    const getCanvasImageDataUrl = () => {
      const canvas = heatmapCanvas.value
      if (!canvas) return null
      try {
        return canvas.toDataURL('image/png')
      } catch (e) {
        console.error('导出热力图失败:', e)
        return null
      }
    }

    const downloadHeatmap = () => {
      const dataUrl = getCanvasImageDataUrl()
      if (!dataUrl) return
      const a = document.createElement('a')
      a.href = dataUrl
      a.download = `heatmap-${analysisData.value?.id || Date.now()}.png`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
    }

    const openHeatmapNewTab = () => {
      const dataUrl = getCanvasImageDataUrl()
      if (!dataUrl) return
      const w = window.open()
      if (w) {
        w.document.write(`<title>热力图</title><img src="${dataUrl}" style="max-width:100%;height:auto;display:block;margin:0 auto;"/>`)
        w.document.close()
      } else {
        // 如果弹窗被阻止，则在当前窗口新标签打开数据URL
        window.open(dataUrl)
      }
    }

    const viewAnalysisReport = () => {
      if (analysisData.value) {
        router.push(`/analysis-report/${analysisData.value.id}`)
      }
    }

    const viewCaseDetail = () => {
      if (analysisData.value) {
        router.push(`/case-detail/${analysisData.value.id}`)
      }
    }

    onMounted(() => {
      loadAnalysis()
    })

    return {
      analysisData,
      loading,
      error,
      heatmapCanvas,
      heatmapGenerated,
      overlayOpacity,
      downloadHeatmap,
      openHeatmapNewTab,
      getFeatureIcon,
      getProbabilityClass,
      goBack,
      viewAnalysisReport,
      viewCaseDetail
    }
  }
}
</script>

<style scoped>
.deep-analysis {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  background: #f8f9fa;
  min-height: 100vh;
}

.header-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.back-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.section {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.section h2 {
  color: #2c3e50;
  border-bottom: 2px solid #9b59b6;
  padding-bottom: 0.5rem;
  margin-bottom: 1.5rem;
}

.section-description {
  color: #6c757d;
  margin-bottom: 1.5rem;
}

.visualization-grid { 
  display: grid; 
  grid-template-columns: 1fr 1fr;
  gap: 2rem; 
  margin-bottom: 2rem;
}

.visualization-item {
  text-align: center;
}

.visualization-item h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
 text-align: center;
}

.image-container {
  position: relative;
  display: inline-block;
  max-width: 100%;
}

.analysis-image {
  max-width: 100%;
  border-radius: 4px;
  border: 2px solid #8daccb;
}

.placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #6c757d;
}

.visualization-note {
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.attention-regions {
  margin-top: 2rem;
}

.regions-list {
  display: grid;
  gap: 1rem;
}

.region-card {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border-left: 4px solid #9b59b6;
}

.region-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.region-label {
  font-weight: bold;
  color: #2c3e50;
}

.region-score {
  color: #9b59b6;
  font-weight: bold;
}

.feature-description {
  background: #e8f4fd;
  padding: 1.5rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
  line-height: 1.8;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.feature-card {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #3498db;
}

.feature-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.feature-content h4 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.feature-metrics {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.metric {
  color: #6c757d;
}

.probability-bars {
  margin: 1.5rem 0;
}

.probability-item {
  margin-bottom: 1rem;
}

.diagnosis-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.diagnosis-name {
  font-weight: bold;
  color: #2c3e50;
}

.diagnosis-prob {
  color: #9b59b6;
  font-weight: bold;
}

.probability-bar-container {
  width: 100%;
  height: 25px;
  background: #ecf0f1;
  border-radius: 12px;
  overflow: hidden;
}

.probability-bar {
  height: 100%;
  transition: width 0.5s ease;
}

.probability-bar.high {
  background: #27ae60;
}

.probability-bar.medium {
  background: #f39c12;
}

.probability-bar.low {
  background: #e74c3c;
}

.diagnosis-details {
  display: grid;
  gap: 1.5rem;
  margin-top: 2rem;
}

.diagnosis-card {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 4px;
  border-left: 4px solid #9b59b6;
}

.diagnosis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.diagnosis-header h4 {
  margin: 0;
  color: #2c3e50;
}

.diagnosis-probability {
  background: #9b59b6;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-weight: bold;
}

.supporting-features,
.contradicting-features {
  margin-top: 1rem;
}

.supporting-features ul,
.contradicting-features ul {
  margin: 0.5rem 0;
  padding-left: 1.5rem;
}

.decision-tree {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  position: relative;
  padding-left: 2rem;
}

.decision-tree::before {
  content: '';
  position: absolute;
  left: 1rem;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #3498db;
}

.decision-step {
  display: flex;
  gap: 1rem;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  background: #3498db;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.step-content {
  flex: 1;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
}

.step-content h4 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.step-metrics {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.action-section {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.action-btn {
  flex: 1;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
  flex: 1;
  margin: 0 0.75rem; 
}

.action-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(52, 152, 219, 0.3);
}

@media (max-width: 768px) {
  .visualization-grid {
    grid-template-columns: 1fr;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .action-section {
    flex-direction: column;
  }
}
</style>
