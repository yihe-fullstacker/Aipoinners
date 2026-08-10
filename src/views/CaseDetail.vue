<template>
  <div class="case-detail-page">
    <div class="page-shell">
      <section class="hero-panel">
        <div class="hero-left">
          <button class="back-btn" type="button" @click="goBack">返回上一页</button>
          <span class="section-badge">病例详情</span>
          <h1>聚合病例基础信息、影像预览和病灶结果概览</h1>
          <p>
            面向上线使用场景，将单次影像分析的核心病例信息、原始影像、诊断风险和后续动作整合为更清晰、更适合持续使用的病例详情页。
          </p>
        </div>

        <div class="hero-stats" v-if="caseData">
          <article class="stat-card">
            <strong>#{{ caseData.id }}</strong>
            <span>病例编号</span>
          </article>
          <article class="stat-card">
            <strong>{{ ((caseData.confidence || 0) * 100).toFixed(1) }}%</strong>
            <span>总体置信度</span>
          </article>
          <article class="stat-card">
            <strong>{{ caseData.findings?.length || 0 }}</strong>
            <span>病灶发现数</span>
          </article>
        </div>
      </section>

      <section v-if="loading" class="state-card card-surface">正在加载病例详情，请稍候...</section>

      <section v-else-if="error" class="state-card card-surface error-card">
        <p>{{ error }}</p>
        <button type="button" class="primary-btn" @click="loadCaseDetail">重新加载</button>
      </section>

      <section v-else-if="caseData" class="content-grid">
        <article class="card-surface info-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">基础信息</div>
              <h2>病例概览</h2>
            </div>
          </div>

          <div class="info-grid">
            <div class="info-item">
              <span class="label">病例 ID</span>
              <strong>#{{ caseData.id }}</strong>
            </div>
            <div class="info-item">
              <span class="label">分析时间</span>
              <strong>{{ formatDate(caseData.createdAt) }}</strong>
            </div>
            <div class="info-item">
              <span class="label">模型版本</span>
              <strong>{{ caseData.modelUsed || 'ResNet50' }}</strong>
            </div>
            <div class="info-item">
              <span class="label">分析耗时</span>
              <strong>{{ caseData.analysisTime ? `${caseData.analysisTime.toFixed(2)} 秒` : 'N/A' }}</strong>
            </div>
          </div>

          <div class="confidence-panel">
            <div class="confidence-head">
              <span>总体风险置信度</span>
              <strong>{{ ((caseData.confidence || 0) * 100).toFixed(1) }}%</strong>
            </div>
            <div class="confidence-bar">
              <div class="confidence-fill" :class="getConfidenceClass(caseData.confidence)" :style="{ width: `${(caseData.confidence || 0) * 100}%` }"></div>
            </div>
          </div>
        </article>

        <article class="card-surface image-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">原始影像</div>
              <h2>医学影像预览</h2>
            </div>
          </div>

          <div class="image-container">
            <img :src="caseData.imageUrl" alt="医学影像" class="medical-image" @error="handleImageError">
            <div class="image-overlay" v-if="caseData.findings && caseData.findings.length > 0">
              <div
                v-for="(finding, index) in caseData.findings"
                :key="index"
                class="finding-marker"
                :style="getMarkerStyle({ ...finding, index })"
                :title="`${finding.type} - ${finding.location}`"
              ></div>
            </div>
            <div v-if="imageError" class="image-error">影像加载异常，请稍后重试或查看报告页。</div>
          </div>
        </article>

        <article class="card-surface findings-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">分析摘要</div>
              <h2>病灶发现与描述</h2>
            </div>
          </div>

          <div class="findings-list" v-if="caseData.findings && caseData.findings.length">
            <div v-for="(finding, index) in caseData.findings" :key="index" class="finding-card">
              <div class="finding-top">
                <strong>{{ finding.type }}</strong>
                <span class="finding-confidence">{{ (finding.confidence * 100).toFixed(1) }}%</span>
              </div>
              <p><span>位置：</span>{{ finding.location }}</p>
              <p v-if="finding.size"><span>大小：</span>{{ finding.size }}</p>
              <p v-if="finding.description"><span>描述：</span>{{ finding.description }}</p>
            </div>
          </div>
          <div v-else class="empty-findings">当前未检出明显病灶，可结合完整报告继续查看系统建议。</div>
        </article>

        <article class="card-surface action-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">后续动作</div>
              <h2>继续深挖病例信息</h2>
            </div>
          </div>

          <div class="action-list">
            <button type="button" class="primary-btn" @click="viewAnalysisReport">查看完整分析报告</button>
            <button type="button" class="secondary-btn" @click="viewDeepAnalysis">进入深度分析可视化</button>
            <button type="button" class="ghost-btn" @click="downloadReport">下载病例报告</button>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { medicalAPI, imageUtils } from '../api/api'
import { localizeAnalysisPayload } from '../utils/analysisLocalization'

export default {
  name: 'CaseDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const caseData = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const imageError = ref(false)

    const loadCaseDetail = async () => {
      const reportId = route.params.id
      if (!reportId) {
        error.value = '缺少病例 ID'
        loading.value = false
        return
      }

      loading.value = true
      error.value = null

      try {
        const response = await medicalAPI.getReportDetail(reportId)
        if (response.data) {
          const localizedPayload = localizeAnalysisPayload(response.data)
          caseData.value = {
            ...localizedPayload,
            imageUrl: imageUtils.ensureImageUrl(localizedPayload.imageUrl)
          }
        } else {
          error.value = '未找到病例数据'
        }
      } catch (err) {
        console.error('加载病例详情失败:', err)
        error.value = err.response?.data?.message || '加载病例详情失败'
      } finally {
        loading.value = false
      }
    }

    const formatDate = (date) => {
      if (!date) return 'N/A'
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }

    const getConfidenceClass = (confidence) => {
      if (confidence >= 0.8) return 'high'
      if (confidence >= 0.6) return 'medium'
      return 'low'
    }

    const getMarkerStyle = (finding) => {
      const positions = [
        { top: '18%', left: '24%' },
        { top: '34%', left: '66%' },
        { top: '58%', left: '38%' },
        { top: '42%', left: '76%' }
      ]
      const pos = positions[finding.index || 0] || positions[0]
      return {
        ...pos,
        backgroundColor: finding.confidence >= 0.8 ? 'rgba(248, 113, 113, 0.88)' : 'rgba(250, 204, 21, 0.88)'
      }
    }

    const handleImageError = () => {
      imageError.value = true
    }

    const goBack = () => {
      router.back()
    }

    const viewAnalysisReport = () => {
      if (caseData.value) {
        router.push(`/analysis-report/${caseData.value.id}`)
      }
    }

    const viewDeepAnalysis = () => {
      if (caseData.value) {
        router.push(`/deep-analysis/${caseData.value.id}`)
      }
    }

    const downloadReport = async () => {
      if (!caseData.value) return

      try {
        const reportText = generateReportText(caseData.value)
        const blob = new Blob([reportText], { type: 'text/plain;charset=utf-8' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `病例报告_${caseData.value.id}_${new Date().getTime()}.txt`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)
      } catch (err) {
        console.error('下载报告失败:', err)
        alert('下载报告失败')
      }
    }

    const generateReportText = (data) => {
      return `医学影像 AI 诊断报告\n================================\n\n病例ID: ${data.id}\n分析时间: ${formatDate(data.createdAt)}\n模型版本: ${data.modelUsed || 'ResNet50'}\n分析耗时: ${data.analysisTime ? data.analysisTime.toFixed(2) + ' 秒' : 'N/A'}\n\n总体置信度: ${((data.confidence || 0) * 100).toFixed(1)}%\n\n发现病灶 (${data.findings?.length || 0} 项):\n${data.findings?.map((f, i) => `\n${i + 1}. ${f.type}\n   位置: ${f.location}\n   置信度: ${(f.confidence * 100).toFixed(1)}%\n   ${f.size ? `大小: ${f.size}` : ''}\n   ${f.description ? `描述: ${f.description}` : ''}\n`).join('') || '无'}\n\n建议:\n${data.recommendations?.map(r => `- ${r}`).join('\n') || '无'}\n\n================================\n报告生成时间: ${new Date().toLocaleString('zh-CN')}\n`
    }

    onMounted(() => {
      loadCaseDetail()
    })

    return {
      caseData,
      loading,
      error,
      imageError,
      loadCaseDetail,
      formatDate,
      getConfidenceClass,
      getMarkerStyle,
      handleImageError,
      goBack,
      viewAnalysisReport,
      viewDeepAnalysis,
      downloadReport
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.case-detail-page {
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
.section-badge,
.section-kicker {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
}

.back-btn,
.primary-btn,
.secondary-btn,
.ghost-btn {
  min-height: 44px;
  padding: 0 18px;
  border: none;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.back-btn {
  margin-bottom: 18px;
  background: rgba(8, 20, 35, 0.82);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #ecf8ff;
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
.card-head h2 {
  color: #f7fbff;
}

.hero-panel h1 {
  margin: 16px 0 14px;
  font-size: clamp(30px, 4vw, 48px);
  line-height: 1.1;
}

.hero-panel p,
.finding-card p,
.state-card p,
.empty-findings,
.image-error {
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

.info-card,
.image-card,
.findings-card,
.action-card {
  padding: 24px;
  border-radius: 26px;
}

.card-head {
  margin-bottom: 18px;
}

.card-head h2 {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1.2;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.info-item {
  padding: 18px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.label {
  display: block;
  margin-bottom: 10px;
  color: #9ce8ff;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.info-item strong,
.confidence-head strong,
.finding-top strong {
  color: #f7fbff;
}

.confidence-panel {
  margin-top: 18px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.confidence-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
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

.confidence-fill.high {
  background: linear-gradient(90deg, #f97316, #ef4444);
}

.confidence-fill.medium {
  background: linear-gradient(90deg, #facc15, #fb923c);
}

.confidence-fill.low {
  background: linear-gradient(90deg, #34d399, #22c55e);
}

.image-container {
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
}

.image-overlay {
  position: absolute;
  inset: 0;
}

.finding-marker {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 0 8px rgba(255, 255, 255, 0.12);
}

.image-error {
  position: absolute;
  left: 18px;
  right: 18px;
  bottom: 18px;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(127, 29, 29, 0.74);
}

.findings-list {
  display: grid;
  gap: 14px;
}

.finding-card {
  padding: 18px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.finding-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.finding-confidence {
  color: #9ce8ff;
  font-weight: 700;
}

.finding-card span {
  color: #9ce8ff;
}

.empty-findings {
  padding: 18px;
  border-radius: 20px;
  border: 1px dashed rgba(125, 211, 252, 0.16);
}

.action-list {
  display: grid;
  gap: 14px;
}

.primary-btn {
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
}

.secondary-btn {
  background: rgba(8, 20, 35, 0.82);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #ecf8ff;
}

.ghost-btn {
  background: transparent;
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #bfe9ff;
}

@media (max-width: 1080px) {
  .hero-panel,
  .content-grid,
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .case-detail-page {
    padding: 20px 12px 32px;
  }

  .hero-panel,
  .info-card,
  .image-card,
  .findings-card,
  .action-card,
  .state-card {
    padding: 22px;
    border-radius: 24px;
  }

  .confidence-head,
  .finding-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
