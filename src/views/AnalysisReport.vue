<template>
  <div class="analysis-report-page">
    <div class="page-shell">
      <section class="hero-panel">
        <div>
          <button class="back-btn" type="button" @click="goBack">返回上一页</button>
          <span class="section-badge">AI 分析报告</span>
          <h1>将影像结果、模型判断与诊疗建议整合为可直接使用的商业化报告页面</h1>
          <p>
            该页面保留原有分析数据结构和调试能力，同时强化结果概览、图像承载和后续动作，让报告页更适合真实业务上线使用。
          </p>
        </div>

        <div class="hero-stats" v-if="reportData">
          <article class="stat-card">
            <strong>#{{ reportData.id }}</strong>
            <span>报告编号</span>
          </article>
          <article class="stat-card">
            <strong>{{ ((reportData.confidence || 0) * 100).toFixed(1) }}%</strong>
            <span>总体置信度</span>
          </article>
          <article class="stat-card">
            <strong>{{ reportData.findings?.length || 0 }}</strong>
            <span>病灶发现数</span>
          </article>
        </div>
      </section>

      <section v-if="loading" class="state-card card-surface">正在加载 AI 分析报告，请稍候...</section>

      <section v-else-if="error" class="state-card card-surface error-card">
        <p>{{ error }}</p>
      </section>

      <section v-else-if="reportData" class="content-grid">
        <article class="card-surface image-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">影像视图</div>
              <h2>医学影像与结果图像预览</h2>
            </div>
            <button type="button" class="ghost-btn" @click="showDebugPanel">调试图片加载</button>
          </div>

          <div class="image-stage">
            <img
              v-if="currentImageUrl"
              :src="currentImageUrl"
              alt="医学影像"
              class="medical-image"
              :class="`status-${imageLoadStatus}`"
              @load="onImageLoad"
              @error="onImageError"
            >
            <div v-if="imageLoadStatus === 'loading'" class="state-overlay">
              {{ activeUrlIndex > 0 ? '正在尝试备用影像地址...' : '影像加载中...' }}
            </div>
            <div v-else-if="imageLoadStatus === 'error'" class="state-overlay error-overlay">
              <p>影像加载失败，可以尝试备用地址或打开调试面板。</p>
              <div class="fallback-actions">
                <button type="button" class="mini-btn" @click="retryLoadImage">重试</button>
                <button
                  v-for="(url, index) in urlCandidates"
                  :key="index"
                  type="button"
                  class="mini-btn"
                  @click="tryFallbackUrl(url)"
                >
                  URL {{ index + 1 }}
                </button>
              </div>
            </div>
            <div v-else-if="!currentImageUrl" class="state-overlay error-overlay">
              <p>当前报告没有返回可用的影像地址。</p>
            </div>
          </div>

          <ImageDebugPanel
            ref="debugPanel"
            :api-response="rawApiResponse"
            :image-url="currentImageUrl"
          />
        </article>

        <article class="card-surface summary-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">报告摘要</div>
              <h2>关键分析信息</h2>
            </div>
            <button type="button" class="primary-btn" @click="downloadReport">下载报告</button>
          </div>

          <div class="summary-grid">
            <div class="summary-item">
              <span>生成时间</span>
              <strong>{{ formatDate(reportData.createdAt) }}</strong>
            </div>
            <div class="summary-item">
              <span>模型版本</span>
              <strong>{{ reportData.modelUsed || 'ResNet50' }}</strong>
            </div>
            <div class="summary-item">
              <span>分析耗时</span>
              <strong>{{ reportData.analysisTime ? `${reportData.analysisTime.toFixed(2)} 秒` : 'N/A' }}</strong>
            </div>
            <div class="summary-item">
              <span>影像类型</span>
              <strong>{{ reportData.imageType || '医学影像' }}</strong>
            </div>
          </div>

          <div class="confidence-panel">
            <div class="confidence-head">
              <span>总体置信度</span>
              <strong>{{ ((reportData.confidence || 0) * 100).toFixed(1) }}%</strong>
            </div>
            <div class="confidence-bar">
              <div class="confidence-fill" :class="getConfidenceClass(reportData.confidence)" :style="{ width: `${(reportData.confidence || 0) * 100}%` }"></div>
            </div>
          </div>

          <div class="summary-tip">
            {{ reportData.findings?.length ? '当前已检出重点关注项，建议继续查看病例详情与深度分析。' : '当前未见明显病灶，建议结合临床情况与后续随访继续观察。' }}
          </div>
        </article>

        <article class="card-surface findings-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">病灶发现</div>
              <h2>检测结果列表</h2>
            </div>
          </div>

          <div v-if="!reportData.findings || reportData.findings.length === 0" class="empty-box">
            未检测到明显病灶。
          </div>
          <div v-else class="findings-grid">
            <div v-for="(finding, index) in reportData.findings" :key="index" class="finding-card">
              <div class="finding-top">
                <strong>{{ finding.type }}</strong>
                <span class="confidence-tag" :class="getConfidenceClass(finding.confidence)">
                  {{ (finding.confidence * 100).toFixed(1) }}%
                </span>
              </div>
              <p><span>位置：</span>{{ finding.location }}</p>
              <p v-if="finding.size"><span>大小：</span>{{ finding.size }}</p>
              <p v-if="finding.description"><span>特征：</span>{{ finding.description }}</p>
              <p v-if="finding.severity"><span>严重程度：</span>{{ finding.severity }}</p>
            </div>
          </div>
        </article>

        <article v-if="reportData.featureDescription" class="card-surface feature-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">特征说明</div>
              <h2>影像特征描述</h2>
            </div>
          </div>
          <p class="feature-text">{{ reportData.featureDescription }}</p>
        </article>

        <article v-if="reportData.differentialDiagnosis && reportData.differentialDiagnosis.length > 0" class="card-surface diagnosis-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">鉴别诊断</div>
              <h2>候选诊断与概率分布</h2>
            </div>
          </div>

          <div class="diagnosis-list">
            <div v-for="(diagnosis, index) in reportData.differentialDiagnosis" :key="index" class="diagnosis-item">
              <div class="diagnosis-top">
                <strong>{{ diagnosis.name }}</strong>
                <span>{{ (diagnosis.probability * 100).toFixed(1) }}%</span>
              </div>
              <p v-if="diagnosis.description">{{ diagnosis.description }}</p>
              <p v-if="diagnosis.supportingFeatures?.length"><span>支持特征：</span>{{ diagnosis.supportingFeatures.join('，') }}</p>
            </div>
          </div>
        </article>

        <article class="card-surface recommendations-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">诊疗建议</div>
              <h2>系统建议与后续动作</h2>
            </div>
          </div>

          <ul class="recommendation-list">
            <li v-for="(rec, index) in reportData.recommendations" :key="index">{{ rec }}</li>
          </ul>

          <div class="action-row">
            <button type="button" class="primary-btn" @click="viewDeepAnalysis">查看深度分析</button>
            <button type="button" class="secondary-btn" @click="viewCaseDetail">查看病例详情</button>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import explain from '../directives/explain.js'
import { useRouter, useRoute } from 'vue-router'
import { medicalAPI, imageUtils } from '../api/api'
import ImageDebugPanel from '../components/ImageDebugPanel.vue'
import { localizeAnalysisPayload } from '../utils/analysisLocalization'

export default {
  name: 'AnalysisReport',
  directives: { explain },
  components: { ImageDebugPanel },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const reportData = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const rawApiResponse = ref(null)
    const imageLoadStatus = ref('unknown')
    const currentImageUrl = ref('')
    const urlCandidates = ref([])
    const debugPanel = ref(null)
    const activeUrlIndex = ref(0)

    const buildImageCandidates = (reportPayload, reportId) => {
      const imageId = reportPayload.imageId || reportPayload.medicalImage?.id || reportId
      const annotatedImage = reportPayload.annotatedImageBase64 || reportPayload.annotated_image_base64
      const rawCandidates = [
        annotatedImage ? (annotatedImage.startsWith('data:') ? annotatedImage : `data:image/png;base64,${annotatedImage}`) : '',
        reportPayload.imageUrl,
        reportPayload.medicalImage?.imageUrl,
        reportPayload.medicalImage?.filePath,
        imageId ? `/api/images/${imageId}` : '',
        imageId ? `http://47.108.52.206:8082/api/images/${imageId}` : '',
        imageId ? `http://localhost:8080/api/images/${imageId}` : '',
        imageId ? `http://localhost/api/images/${imageId}` : ''
      ]

      return rawCandidates
        .map((value) => (value ? imageUtils.ensureImageUrl(value) : ''))
        .filter((value, index, list) => value && list.indexOf(value) === index)
    }

    const applyImageCandidate = (index = 0) => {
      activeUrlIndex.value = index
      currentImageUrl.value = urlCandidates.value[index] || ''
      imageLoadStatus.value = currentImageUrl.value ? 'loading' : 'error'
    }

    const loadReport = async () => {
      const reportId = route.params.id
      if (!reportId) {
        error.value = '缺少报告 ID'
        loading.value = false
        return
      }

      loading.value = true
      error.value = null

      try {
        const response = await medicalAPI.getReportDetail(reportId)
        rawApiResponse.value = response.data

        if (!response.data) {
          error.value = '未找到报告数据'
          return
        }

        const localizedPayload = localizeAnalysisPayload(response.data)

        urlCandidates.value = buildImageCandidates(localizedPayload, reportId)
        const normalizedImageUrl = urlCandidates.value[0] || ''

        reportData.value = {
          ...localizedPayload,
          imageUrl: normalizedImageUrl
        }
        applyImageCandidate(0)
      } catch (err) {
        console.error('加载报告失败:', err)
        error.value = err.response?.data?.message || '加载报告失败'
      } finally {
        loading.value = false
      }
    }

    const onImageLoad = () => {
      imageLoadStatus.value = 'success'
    }

    const onImageError = (err) => {
      console.error('图片加载失败:', err)
      const nextIndex = activeUrlIndex.value + 1
      if (nextIndex < urlCandidates.value.length) {
        applyImageCandidate(nextIndex)
        return
      }
      imageLoadStatus.value = 'error'
    }

    const retryLoadImage = () => {
      const temp = currentImageUrl.value || urlCandidates.value[activeUrlIndex.value] || urlCandidates.value[0]
      imageLoadStatus.value = temp ? 'loading' : 'error'
      currentImageUrl.value = ''
      setTimeout(() => {
        currentImageUrl.value = temp
      }, 100)
    }

    const tryFallbackUrl = (url) => {
      const targetIndex = urlCandidates.value.indexOf(url)
      applyImageCandidate(targetIndex >= 0 ? targetIndex : 0)
    }

    const showDebugPanel = () => {
      if (debugPanel.value) {
        debugPanel.value.show()
      }
    }

    const formatDate = (date) => {
      if (!date) return 'N/A'
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const getConfidenceClass = (confidence = 0) => {
      if (confidence >= 0.8) return 'high'
      if (confidence >= 0.6) return 'medium'
      return 'low'
    }

    const goBack = () => {
      router.back()
    }

    const viewDeepAnalysis = () => {
      if (reportData.value) {
        router.push(`/deep-analysis/${reportData.value.id}`)
      }
    }

    const viewCaseDetail = () => {
      if (reportData.value) {
        router.push(`/case-detail/${reportData.value.id}`)
      }
    }

    const generateReportText = (data) => {
      return `医学影像 AI 诊断报告\n================================\n\n报告编号: #${data.id}\n生成时间: ${formatDate(data.createdAt)}\n模型版本: ${data.modelUsed || 'ResNet50'}\n分析耗时: ${data.analysisTime ? data.analysisTime.toFixed(2) + ' 秒' : 'N/A'}\n\n总体置信度: ${((data.confidence || 0) * 100).toFixed(1)}%\n\n发现病灶:\n${data.findings?.map((f, index) => `${index + 1}. ${f.type} (${f.location}) - 置信度 ${(f.confidence * 100).toFixed(1)}%`).join('\n') || '无'}\n\n建议:\n${data.recommendations?.map(item => `- ${item}`).join('\n') || '无'}\n`
    }

    const downloadReport = () => {
      if (!reportData.value) return

      try {
        const reportText = generateReportText(reportData.value)
        const blob = new Blob([reportText], { type: 'text/plain;charset=utf-8' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `AI分析报告_${reportData.value.id}_${new Date().getTime()}.txt`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)
      } catch (err) {
        console.error('下载报告失败:', err)
        alert('下载报告失败')
      }
    }

    onMounted(() => {
      loadReport()
    })

    return {
      reportData,
      loading,
      error,
      rawApiResponse,
      imageLoadStatus,
      currentImageUrl,
      urlCandidates,
      debugPanel,
      activeUrlIndex,
      loadReport,
      formatDate,
      getConfidenceClass,
      goBack,
      viewDeepAnalysis,
      viewCaseDetail,
      downloadReport,
      onImageLoad,
      onImageError,
      retryLoadImage,
      tryFallbackUrl,
      showDebugPanel
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.analysis-report-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(20, 184, 166, 0.16), transparent 28%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), transparent 26%),
    linear-gradient(180deg, #07111d 0%, #091827 100%);
  color: #eef7ff;
  padding: 28px 24px 48px;
}

.page-shell {
  max-width: 1280px;
  margin: 0 auto;
}

.hero-panel,
.card-surface {
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: linear-gradient(180deg, rgba(7, 21, 36, 0.94), rgba(4, 12, 24, 0.96));
  box-shadow: 0 22px 54px rgba(2, 10, 20, 0.28);
}

.hero-panel {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 22px;
  padding: 30px;
  border-radius: 30px;
}

.back-btn,
.primary-btn,
.secondary-btn,
.ghost-btn,
.mini-btn,
.section-badge,
.section-kicker,
.confidence-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
}

.back-btn,
.primary-btn,
.secondary-btn,
.ghost-btn,
.mini-btn {
  min-height: 42px;
  padding: 0 18px;
  border: none;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.back-btn,
.secondary-btn,
.ghost-btn,
.mini-btn {
  background: rgba(8, 20, 35, 0.82);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #ecf8ff;
}

.primary-btn {
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
}

.section-badge,
.section-kicker {
  min-height: 32px;
  padding: 0 12px;
  border: 1px solid rgba(94, 234, 212, 0.18);
  background: rgba(45, 212, 191, 0.08);
  color: #90f5e8;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero-panel h1,
.card-head h2,
.state-card h2 {
  color: #f7fbff;
}

.hero-panel h1 {
  margin: 16px 0 14px;
  font-size: clamp(30px, 4vw, 48px);
  line-height: 1.1;
}

.hero-panel p,
.summary-tip,
.finding-card p,
.feature-text,
.diagnosis-item p,
.recommendation-list li,
.state-card p {
  color: rgba(220, 235, 255, 0.72);
  line-height: 1.8;
}

.hero-stats {
  display: grid;
  gap: 14px;
}

.stat-card {
  padding: 18px;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(7, 20, 35, 0.76);
}

.stat-card strong {
  display: block;
  font-size: 28px;
  color: #ffffff;
  margin-bottom: 8px;
}

.state-card {
  margin-top: 22px;
  padding: 34px;
  border-radius: 26px;
  text-align: center;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px;
  margin-top: 22px;
}

.image-card,
.summary-card,
.findings-card,
.feature-card,
.diagnosis-card,
.recommendations-card {
  padding: 24px;
  border-radius: 26px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.card-head h2 {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1.2;
}

.image-stage {
  position: relative;
  min-height: 420px;
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid rgba(125, 211, 252, 0.1);
  background: rgba(6, 17, 30, 0.9);
}

.medical-image {
  width: 100%;
  height: 420px;
  object-fit: contain;
  background: #020817;
  opacity: 0.18;
  transform: scale(1.015);
  transition: opacity 0.35s ease, transform 0.35s ease, filter 0.35s ease;
  filter: saturate(0.96) contrast(1.04);
}

.medical-image.status-success {
  opacity: 1;
  transform: scale(1);
  filter: saturate(1.02) contrast(1.08);
}

.medical-image.status-error {
  opacity: 0.12;
}

.state-overlay {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 24px;
  text-align: center;
  background: rgba(4, 12, 24, 0.82);
  color: #eaf7ff;
}

.error-overlay {
  gap: 16px;
}

.fallback-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.summary-item,
.finding-card,
.diagnosis-item {
  padding: 18px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.summary-item span,
.finding-card span {
  color: #9ce8ff;
}

.summary-item strong,
.confidence-head strong,
.finding-top strong,
.diagnosis-top strong {
  color: #f7fbff;
}

.confidence-panel {
  margin-top: 18px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.confidence-head,
.finding-top,
.diagnosis-top,
.action-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.confidence-head {
  margin-bottom: 12px;
}

.confidence-bar {
  width: 100%;
  height: 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.confidence-fill {
  height: 100%;
  border-radius: inherit;
}

.confidence-fill.high,
.confidence-tag.high {
  background: linear-gradient(90deg, #f97316, #ef4444);
  color: #fff7ed;
}

.confidence-fill.medium,
.confidence-tag.medium {
  background: linear-gradient(90deg, #facc15, #fb923c);
  color: #1f2937;
}

.confidence-fill.low,
.confidence-tag.low {
  background: linear-gradient(90deg, #34d399, #22c55e);
  color: #052e16;
}

.confidence-tag {
  min-height: 32px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 700;
}

.summary-tip {
  margin-top: 18px;
  padding: 18px;
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(8, 20, 35, 0.72);
}

.findings-grid,
.diagnosis-list {
  display: grid;
  gap: 14px;
}

.empty-box {
  padding: 18px;
  border-radius: 20px;
  border: 1px dashed rgba(125, 211, 252, 0.16);
  color: rgba(220, 235, 255, 0.72);
}

.recommendation-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
}

.action-row {
  margin-top: 20px;
}

@media (max-width: 1080px) {
  .hero-panel,
  .content-grid,
  .summary-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .analysis-report-page {
    padding: 20px 12px 32px;
  }

  .hero-panel,
  .image-card,
  .summary-card,
  .findings-card,
  .feature-card,
  .diagnosis-card,
  .recommendations-card,
  .state-card {
    padding: 22px;
    border-radius: 24px;
  }

  .card-head,
  .confidence-head,
  .finding-top,
  .diagnosis-top,
  .action-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .primary-btn,
  .secondary-btn,
  .ghost-btn {
    width: 100%;
  }
}
</style>



