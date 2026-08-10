<template>
  <div class="analysis-result" v-if="analysis">
    <div class="analysis-shell">
      <section class="hero-grid">
        <article class="visual-panel">
          <div class="panel-head">
            <div>
              <div class="panel-kicker">影像概览</div>
              <h3>当前医学影像</h3>
            </div>
          </div>

          <div class="image-frame">
            <img :src="annotatedImageUrl || localizedAnalysis.imageUrl" alt="医学影像" class="medical-image" />
          </div>
        </article>

        <article class="summary-panel">
          <div class="panel-head">
            <div>
              <div class="panel-kicker">结果摘要</div>
              <h3>结构化分析结果</h3>
            </div>
            <div class="summary-badge">{{ confidenceLabel }}</div>
          </div>

          <div class="confidence-card">
            <div class="confidence-row">
              <span>总体置信度</span>
              <strong>{{ confidencePercent }}</strong>
            </div>
            <div class="confidence-bar">
              <div
                class="confidence-fill"
                :style="{ width: confidencePercent }"
                :class="getConfidenceClass(localizedAnalysis.confidence)"
              ></div>
            </div>
          </div>

          <div class="finding-list" v-if="normalizedFindings.length">
            <article v-for="(finding, index) in normalizedFindings" :key="index" class="finding-card">
              <div class="finding-head">
                <strong>{{ finding.type }}</strong>
                <span>{{ finding.confidence }}</span>
              </div>
              <p>{{ finding.location }}</p>
            </article>
          </div>

          <div v-else class="empty-block">
            当前暂无结构化病灶结果，建议结合热力图和完整报告继续查看详细信息。
          </div>

          <div class="recommendation-block">
            <h4>诊疗建议</h4>
            <ul>
              <li v-for="(rec, index) in normalizedRecommendations" :key="index">{{ rec }}</li>
            </ul>
          </div>

          <button @click="generateReport" class="report-btn">
            查看详细报告
          </button>
        </article>
      </section>

      <HeatmapViewer
        v-if="localizedAnalysis.heatmapBase64"
        :originalImage="localizedAnalysis.imageUrl"
        :heatmapBase64="localizedAnalysis.heatmapBase64"
        :description="localizedAnalysis.heatmapDescription"
      />
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import HeatmapViewer from './HeatmapViewer.vue'
import { localizeAnalysisPayload, localizeText } from '../utils/analysisLocalization'

export default {
  name: 'AnalysisResult',
  components: {
    HeatmapViewer
  },
  props: {
    analysis: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const router = useRouter()

    const localizedAnalysis = computed(() => localizeAnalysisPayload(props.analysis) || {})

    const annotatedImageUrl = computed(() => {
      const base64 = localizedAnalysis.value.annotatedImageBase64 || localizedAnalysis.value.annotated_image_base64
      if (!base64) return ''
      return base64.startsWith('data:') ? base64 : `data:image/png;base64,${base64}`
    })

    const normalizedFindings = computed(() => {
      const findings = Array.isArray(localizedAnalysis.value.findings) ? localizedAnalysis.value.findings : []
      return findings.map((finding) => ({
        type: finding?.type || '待补充发现',
        location: finding?.location ? `位置：${finding.location}` : '位置：待补充',
        confidence: `${((finding?.confidence ?? 0) * 100).toFixed(1)}%`
      }))
    })

    const normalizedRecommendations = computed(() => {
      const recommendations = Array.isArray(localizedAnalysis.value.recommendations)
        ? localizedAnalysis.value.recommendations
        : []
      if (recommendations.length) {
        return recommendations.map((item) => localizeText(item))
      }
      return ['请结合临床症状和医生复核结果综合判断，不要仅依据 AI 结果直接下最终结论。']
    })

    const confidencePercent = computed(() => {
      const confidence = Number(localizedAnalysis.value.confidence ?? 0)
      return `${(confidence * 100).toFixed(1)}%`
    })

    const confidenceLabel = computed(() => {
      const confidence = Number(localizedAnalysis.value.confidence ?? 0)
      if (confidence >= 0.8) return '高置信度'
      if (confidence >= 0.6) return '建议复核'
      return '谨慎参考'
    })

    const generateReport = () => {
      if (localizedAnalysis.value && localizedAnalysis.value.id) {
        router.push(`/analysis-report/${localizedAnalysis.value.id}`)
      } else {
        alert('当前缺少报告 ID，暂时无法打开详细分析报告。')
      }
    }

    const getConfidenceClass = (confidence) => {
      if (confidence >= 0.8) return 'high'
      if (confidence >= 0.6) return 'medium'
      return 'low'
    }

    return {
      localizedAnalysis,
      annotatedImageUrl,
      normalizedFindings,
      normalizedRecommendations,
      confidencePercent,
      confidenceLabel,
      generateReport,
      getConfidenceClass
    }
  }
}
</script>

<style scoped>
.analysis-result {
  height: 100%;
}

.analysis-shell {
  display: grid;
  gap: 20px;
}

.hero-grid {
  display: grid;
  grid-template-columns: minmax(280px, 0.92fr) minmax(320px, 1.08fr);
  gap: 20px;
  align-items: stretch;
}

.visual-panel,
.summary-panel {
  border-radius: 24px;
  border: 1px solid rgba(125, 211, 252, 0.1);
  background: rgba(7, 20, 35, 0.72);
  padding: 20px;
}

.summary-panel {
  display: grid;
  align-content: start;
  gap: 16px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
}

.panel-kicker,
.summary-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid rgba(94, 234, 212, 0.18);
  background: rgba(45, 212, 191, 0.08);
  color: #90f5e8;
  font-size: 11px;
  letter-spacing: 0.08em;
}

.panel-head h3,
.recommendation-block h4 {
  margin: 10px 0 0;
  color: #f7fbff;
}

.image-frame {
  margin-top: 18px;
  min-height: 380px;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: radial-gradient(circle at center, rgba(20, 61, 96, 0.42), rgba(3, 11, 20, 0.98) 74%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.medical-image {
  width: 100%;
  height: 100%;
  max-height: 380px;
  object-fit: contain;
  padding: 20px;
}

.confidence-card,
.finding-card,
.recommendation-block,
.empty-block {
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(4, 14, 26, 0.72);
}

.confidence-card,
.recommendation-block,
.empty-block {
  padding: 18px;
}

.confidence-row,
.finding-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.confidence-row span,
.finding-card p,
.empty-block,
.recommendation-block li {
  color: rgba(220, 235, 255, 0.76);
}

.confidence-row strong {
  color: #f7fbff;
  font-size: 20px;
}

.confidence-bar {
  width: 100%;
  height: 12px;
  margin-top: 14px;
  border-radius: 999px;
  overflow: hidden;
  background: rgba(226, 232, 240, 0.12);
}

.confidence-fill {
  height: 100%;
  border-radius: inherit;
  transition: width 0.3s ease;
}

.confidence-fill.high {
  background: linear-gradient(90deg, #22c55e, #14b8a6);
}

.confidence-fill.medium {
  background: linear-gradient(90deg, #f59e0b, #f97316);
}

.confidence-fill.low {
  background: linear-gradient(90deg, #fb7185, #ef4444);
}

.finding-list {
  display: grid;
  gap: 12px;
}

.finding-card {
  padding: 16px 18px;
}

.finding-head strong {
  color: #f8fbff;
}

.finding-head span {
  color: #ff9b8b;
  font-weight: 700;
}

.finding-card p {
  margin: 8px 0 0;
}

.recommendation-block ul {
  list-style: none;
  padding: 0;
  margin: 12px 0 0;
  display: grid;
  gap: 10px;
}

.recommendation-block li {
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(10, 27, 45, 0.72);
  border-left: 3px solid #4cc9f0;
}

.report-btn {
  min-height: 48px;
  justify-self: start;
  padding: 0 20px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 14px 28px rgba(56, 189, 248, 0.24);
}

.report-btn:hover {
  transform: translateY(-1px);
}

@media (max-width: 960px) {
  .hero-grid {
    grid-template-columns: 1fr;
  }

  .image-frame {
    min-height: 300px;
  }

  .medical-image {
    max-height: 300px;
  }
}
</style>
