﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="reports-page">
    <button class="floating-back" type="button" @click="goBack" aria-label="返回患者首页">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 5l-7 7 7 7"/>
      </svg>
    </button>

    <div class="page-shell">
      <section class="hero-panel">
        <div>
          <span class="section-badge">历史诊断报告中心</span>
          <h1>报告、影像与关键结论统一归档，便于持续复查追踪</h1>
          <p>
            该页面面向实际上线场景，帮助用户快速筛选历史分析结果、下载报告，并进一步进入病例详情或深度分析页面继续查看。
          </p>
        </div>

        <div class="hero-stats">
          <article class="stat-card">
            <strong>{{ reports.length }}</strong>
            <span>报告总数</span>
          </article>
          <article class="stat-card">
            <strong>{{ highConfidenceCount }}</strong>
            <span>高置信度报告</span>
          </article>
          <article class="stat-card">
            <strong>{{ totalFindings }}</strong>
            <span>累计病灶发现数</span>
          </article>
        </div>
      </section>

      <section class="toolbar card-surface">
        <div class="toolbar-left">
          <button
            v-for="option in filterOptions"
            :key="option.value"
            type="button"
            class="filter-chip"
            :class="{ active: activeFilter === option.value }"
            @click="activeFilter = option.value"
          >
            {{ option.label }}
          </button>
        </div>
      </section>

      <section v-if="loading" class="state-card card-surface">
        正在加载诊断报告，请稍候...
      </section>

      <section v-else-if="filteredReports.length === 0" class="state-card card-surface empty-state">
        <h2>暂无符合条件的诊断报告</h2>
        <p>您可以先上传影像并发起分析，系统会自动在这里沉淀历史报告。</p>
        <router-link to="/dashboard" class="primary-link">前往 AI 分析中心</router-link>
      </section>

      <section v-else class="report-grid">
        <article v-for="report in filteredReports" :key="report.id" class="report-card card-surface">
          <div class="report-top">
            <div>
              <div class="report-id">报告 #{{ report.id }}</div>
              <div class="report-date">{{ formatDate(report.date || report.createdAt) }}</div>
            </div>
            <div class="confidence-pill" :class="getConfidenceClass(report.confidence)">
              {{ (report.confidence * 100).toFixed(1) }}%
            </div>
          </div>

          <div class="report-body">
            <div class="image-box">
              <img :src="report.imageUrl" alt="医学影像" class="medical-image" />
            </div>
            <div class="report-info">
              <div class="info-row">
                <span>病灶发现</span>
                <strong>{{ report.findingsCount || 0 }} 项</strong>
              </div>
              <div class="info-row">
                <span>报告状态</span>
                <strong>{{ report.confidence >= 0.8 ? '重点关注' : '常规查看' }}</strong>
              </div>
              <div class="info-row">
                <span>建议动作</span>
                <strong>{{ report.findingsCount > 0 ? '查看详情并继续追踪' : '可下载存档并定期复查' }}</strong>
              </div>
            </div>
          </div>

          <div class="report-actions">
            <button type="button" class="action-btn primary" @click="viewReport(report.id)">查看病例详情</button>
            <button type="button" class="action-btn secondary" @click="downloadReport(report.id)">下载报告</button>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import explain from '../directives/explain.js'
import { useRouter } from 'vue-router'
import { medicalAPI, imageUtils } from '../api/api'

export default {
  name: 'Reports',
  directives: { explain },
  setup() {
    const reports = ref([])
    const loading = ref(false)
    const router = useRouter()
    const activeFilter = ref('ALL')
    const filterOptions = [
      { label: '全部报告', value: 'ALL' },
      { label: '高置信度', value: 'HIGH' },
      { label: '有病灶发现', value: 'FINDINGS' }
    ]

    const goBack = () => {
      router.push('/patient/home')
    }

    const loadReports = async () => {
      loading.value = true
      try {
        const response = await medicalAPI.getReports()
        if (response.data) {
          reports.value = response.data.map(report => ({
            ...report,
            imageUrl: imageUtils.ensureImageUrl(report.imageUrl)
          }))
        }
      } catch (error) {
        console.error('加载报告失败:', error)
        reports.value = []
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadReports()
    })

    const formatDate = (date) => {
      if (!date) return ''
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const highConfidenceCount = computed(() => reports.value.filter(report => report.confidence >= 0.8).length)
    const totalFindings = computed(() => reports.value.reduce((total, report) => total + (report.findingsCount || 0), 0))

    const filteredReports = computed(() => {
      return reports.value.filter(report => {
        if (activeFilter.value === 'HIGH') {
          return report.confidence >= 0.8
        }
        if (activeFilter.value === 'FINDINGS') {
          return (report.findingsCount || 0) > 0
        }
        return true
      })
    })

    const getConfidenceClass = (confidence) => {
      if (confidence >= 0.8) return 'high'
      if (confidence >= 0.6) return 'medium'
      return 'low'
    }

    const viewReport = async (id) => {
      router.push(`/case-detail/${id}`)
    }

    const downloadReport = async (id) => {
      try {
        const response = await medicalAPI.getReportDetail(id)
        if (response.data) {
          const reportText = `医学影像诊断报告\n\n` +
            `报告ID: ${id}\n` +
            `分析时间: ${formatDate(response.data.analysisTime || response.data.createdAt)}\n` +
            `模型: ${response.data.modelUsed || 'ResNet50'}\n` +
            `总体置信度: ${((response.data.confidence || 0) * 100).toFixed(1)}%\n\n` +
            `发现病灶:\n${response.data.findings?.map(f =>
              `- ${f.type} (${f.location}): 置信度 ${(f.confidence * 100).toFixed(1)}%`
            ).join('\n') || '无'}\n\n` +
            `建议:\n${response.data.recommendations?.map(r => `- ${r}`).join('\n') || '无'}`

          const blob = new Blob([reportText], { type: 'text/plain;charset=utf-8' })
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `诊断报告_${id}.txt`
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        }
      } catch (error) {
        console.error('下载报告失败:', error)
        alert('下载报告失败')
      }
    }

    return {
      reports,
      loading,
      filterOptions,
      activeFilter,
      filteredReports,
      highConfidenceCount,
      totalFindings,
      goBack,
      formatDate,
      getConfidenceClass,
      viewReport,
      downloadReport
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.reports-page {
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  background:
    radial-gradient(circle at top left, rgba(20, 184, 166, 0.16), transparent 28%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), transparent 26%),
    linear-gradient(180deg, #07111d 0%, #091827 100%);
  color: #eef7ff;
  padding: 8px var(--app-shell-gutter, 32px) 48px;
}

.page-shell {
  max-width: 1260px;
  margin: 0 auto;
}

.floating-back {
  position: fixed;
  top: 140px;
  left: 24px;
  width: 46px;
  height: 46px;
  border: 1px solid rgba(125, 211, 252, 0.18);
  border-radius: 14px;
  background: rgba(5, 15, 28, 0.86);
  color: #ecf9ff;
  cursor: pointer;
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
  gap: 20px;
  padding: 30px;
  border-radius: 30px;
}

.section-badge,
.filter-chip,
.primary-link,
.action-btn,
.confidence-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
}

.section-badge {
  min-height: 32px;
  padding: 0 12px;
  border: 1px solid rgba(94, 234, 212, 0.18);
  background: rgba(45, 212, 191, 0.08);
  color: #90f5e8;
}

.hero-panel h1 {
  margin: 16px 0 14px;
  font-size: clamp(30px, 4vw, 48px);
  line-height: 1.1;
  color: #f7fbff;
}

.hero-panel p,
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

.toolbar {
  margin-top: 24px;
  padding: 20px 22px;
  border-radius: 24px;
}

.toolbar-left {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-chip {
  min-height: 36px;
  padding: 0 14px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  background: rgba(7, 21, 36, 0.68);
  color: rgba(220, 235, 255, 0.74);
  cursor: pointer;
}

.filter-chip.active {
  background: linear-gradient(135deg, rgba(24, 84, 92, 0.6), rgba(14, 40, 70, 0.82));
  border-color: rgba(94, 234, 212, 0.3);
  color: #f6ffff;
}

.state-card {
  margin-top: 22px;
  padding: 34px;
  border-radius: 26px;
  text-align: center;
}

.primary-link {
  margin-top: 18px;
  min-height: 44px;
  padding: 0 18px;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
  text-decoration: none;
}

.report-grid {
  display: grid;
  gap: 18px;
  margin-top: 22px;
}

.report-card {
  padding: 24px;
  border-radius: 26px;
}

.report-top,
.report-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.report-id {
  font-size: 22px;
  font-weight: 700;
  color: #f7fbff;
}

.report-date {
  margin-top: 8px;
  color: rgba(185, 214, 244, 0.66);
  font-size: 13px;
}

.confidence-pill {
  min-height: 36px;
  padding: 0 16px;
}

.confidence-pill.high {
  background: rgba(248, 113, 113, 0.16);
  color: #fecaca;
}

.confidence-pill.medium {
  background: rgba(250, 204, 21, 0.16);
  color: #fde68a;
}

.confidence-pill.low {
  background: rgba(52, 211, 153, 0.16);
  color: #bbf7d0;
}

.report-body {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 18px;
  margin-top: 18px;
}

.image-box {
  overflow: hidden;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.1);
  background: rgba(6, 17, 30, 0.9);
}

.medical-image {
  width: 100%;
  height: 210px;
  object-fit: cover;
}

.report-info {
  display: grid;
  gap: 14px;
}

.info-row {
  padding: 18px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.info-row span {
  color: rgba(220, 235, 255, 0.72);
}

.info-row strong {
  color: #f7fbff;
  text-align: right;
}

.report-actions {
  margin-top: 18px;
}

.action-btn {
  min-height: 46px;
  padding: 0 18px;
  border: none;
  cursor: pointer;
}

.action-btn.primary {
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
}

.action-btn.secondary {
  background: rgba(8, 20, 35, 0.82);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #ecf8ff;
}

@media (max-width: 1080px) {
  .hero-panel,
  .report-body {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .reports-page {
    padding: 8px 12px 32px;
  }

  .hero-panel,
  .toolbar,
  .report-card,
  .state-card {
    padding: 22px;
    border-radius: 24px;
  }

  .report-top,
  .report-actions,
  .info-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-btn,
  .primary-link {
    width: 100%;
  }
}
</style>
