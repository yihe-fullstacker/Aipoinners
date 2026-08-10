<template>
  <div class="report-list">
    <h3>最近报告</h3>
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else-if="reports.length === 0" class="no-reports">
      <p>暂无报告记录</p>
    </div>
    <div v-else class="reports">
      <div v-for="report in reports" :key="report.id" class="report-item">
        <div class="report-info">
          <div class="report-title">报告 #{{ report.id }}</div>
          <div class="report-date">{{ formatDate(report.date) }}</div>
          <div class="report-meta">
            <span>置信度: {{ (report.confidence * 100).toFixed(1) }}%</span>
            <span>发现: {{ report.findingsCount || 0 }} 处</span>
          </div>
        </div>
        <div class="report-status" :class="report.status">
          {{ report.status === 'completed' ? '已完成' : '处理中' }}
        </div>
        <div class="report-actions">
          <button @click="viewReport(report.id)" class="action-btn view-btn">查看</button>
          <button @click="downloadReport(report)" class="action-btn download-btn">下载</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { medicalAPI } from '../api/api'

export default {
  name: 'ReportList',
  setup() {
    const router = useRouter()
    const reports = ref([])
    const loading = ref(false)

    const loadReports = async () => {
      loading.value = true
      try {
        const response = await medicalAPI.getReports()
        if (response.data) {
          reports.value = response.data.map(report => ({
            ...report,
            status: 'completed'
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
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const viewReport = (id) => {
      router.push(`/case-detail/${id}`)
    }

    const downloadReport = async (report) => {
      try {
        const response = await medicalAPI.getReportDetail(report.id)
        if (response.data) {
          const reportText = `医学影像诊断报告\n\n` +
            `报告ID: ${report.id}\n` +
            `分析时间: ${formatDate(response.data.analysisTime || response.data.createdAt)}\n` +
            `模型: ${response.data.modelUsed || 'ResNet50'}\n` +
            `总体置信度: ${((response.data.confidence || 0) * 100).toFixed(1)}%\n\n` +
            `发现病灶:\n${response.data.findings?.map(f =>
              `- ${f.type} (${f.location}): 置信度 ${(f.confidence * 100).toFixed(1)}%`
            ).join('\n') || '无'}\n\n` +
            `建议:\n${response.data.recommendations?.map(r => `- ${r}`).join('\n') || '无'}`

          const blob = new Blob([reportText], { type: 'text/plain;charset=utf-8' })
          const url = URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = `诊断报告-${report.id}.txt`
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
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
      formatDate,
      loadReports,
      viewReport,
      downloadReport
    }
  }
}
</script>

<style scoped>
.report-list {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.report-list h3 {
  margin-bottom: 1rem;
  color: #2c3e50;
}

.no-reports {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

.report-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  border-bottom: 1px solid #ecf0f1;
}

.report-item:last-child {
  border-bottom: none;
}

.report-title {
  font-weight: bold;
  color: #2c3e50;
}

.report-date {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-top: 0.25rem;
}

.report-meta {
  font-size: 0.85rem;
  color: #95a5a6;
  margin-top: 0.5rem;
  display: flex;
  gap: 1rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

.report-status {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
}

.report-status.completed {
  background: #d5f4e6;
  color: #27ae60;
}

.report-status.processing {
  background: #fdebd0;
  color: #f39c12;
}
</style>