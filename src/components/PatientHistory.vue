<template>
  <div class="history-page">
    <button class="floating-back" type="button" @click="goBack" aria-label="返回患者首页">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 5l-7 7 7 7"/>
      </svg>
    </button>

    <div class="page-shell">
      <section class="hero-panel">
        <div>
          <span class="section-badge">患者诊疗记录中心</span>
          <h1>历史会诊、检查时间线与结果状态一页掌握</h1>
          <p>
            将历次诊断申请、会诊进度和结果沉淀在同一页面，方便患者快速查找病例，也便于平台形成持续服务闭环。
          </p>
        </div>

        <div class="hero-stats">
          <article class="stat-card">
            <strong>{{ list.length }}</strong>
            <span>累计会诊记录</span>
          </article>
          <article class="stat-card">
            <strong>{{ completedCount }}</strong>
            <span>已完成诊断</span>
          </article>
          <article class="stat-card">
            <strong>{{ pendingCount }}</strong>
            <span>等待处理</span>
          </article>
        </div>
      </section>

      <section class="toolbar card-surface">
        <div class="toolbar-left">
          <button
            v-for="item in filters"
            :key="item.value"
            type="button"
            class="filter-chip"
            :class="{ active: statusFilter === item.value }"
            @click="statusFilter = item.value"
          >
            {{ item.label }}
          </button>
        </div>

        <label class="search-box">
          <input v-model="keyword" type="text" placeholder="搜索记录编号或诊断内容" />
        </label>
      </section>

      <section class="records-grid">
        <article
          v-for="item in filteredList"
          :key="item.id"
          class="record-card card-surface"
          @click="openDetail(item)"
        >
          <div class="record-top">
            <div>
              <div class="record-id">检查记录 #{{ item.id }}</div>
              <div class="record-time">{{ formatTime(item.createdAt) }}</div>
            </div>
            <span class="status-pill" :class="item.status === 'COMPLETED' ? 'done' : 'pending'">
              {{ item.status === 'COMPLETED' ? '已完成' : '待诊断' }}
            </span>
          </div>

          <div class="record-body">
            <div class="summary-item">
              <span class="summary-label">诊断摘要</span>
              <p>{{ item.doctorDiagnosis || '医生诊断结果待更新，可点击进入详情页继续查看会诊进展。' }}</p>
            </div>
            <div class="summary-item">
              <span class="summary-label">下一步建议</span>
              <p>
                {{ item.status === 'COMPLETED' ? '建议查看完整会诊详情，并根据结果进入课程、问答或报告页继续追踪。' : '当前记录仍在处理中，建议保留联系方式并稍后查看最新状态。' }}
              </p>
            </div>
          </div>

          <div class="record-footer">
            <span>点击查看会诊详情</span>
            <span class="arrow">→</span>
          </div>
        </article>

        <article v-if="filteredList.length === 0" class="empty-card card-surface">
          <div class="empty-title">暂无匹配的检查记录</div>
          <p>您可以先上传医学影像发起诊断，系统会自动在这里沉淀后续诊疗记录。</p>
          <router-link to="/upload" class="empty-action">立即上传影像</router-link>
        </article>
      </section>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '../api/api'

export default {
  name: 'PatientHistory',
  setup() {
    const router = useRouter()
    const list = ref([])
    const userId = ref(null)
    const statusFilter = ref('ALL')
    const keyword = ref('')
    const filters = [
      { label: '全部记录', value: 'ALL' },
      { label: '已完成', value: 'COMPLETED' },
      { label: '待诊断', value: 'PENDING' }
    ]

    const goBack = () => {
      router.push('/patient/home')
    }

    const formatTime = (ts) => {
      if (!ts) return ''
      return String(ts).replace('T', ' ').substring(0, 19)
    }

    const loadHistory = async () => {
      if (!userId.value) return
      try {
        const res = await apiClient.get('/patient/consultations', {
          params: { userId: userId.value }
        })
        list.value = res.data || []
      } catch (e) {
        console.error('加载检查记录失败', e)
      }
    }

    const completedCount = computed(() => list.value.filter(item => item.status === 'COMPLETED').length)
    const pendingCount = computed(() => list.value.filter(item => item.status !== 'COMPLETED').length)

    const filteredList = computed(() => {
      return list.value.filter(item => {
        const matchStatus = statusFilter.value === 'ALL'
          ? true
          : statusFilter.value === 'COMPLETED'
            ? item.status === 'COMPLETED'
            : item.status !== 'COMPLETED'

        const text = `${item.id} ${item.doctorDiagnosis || ''}`.toLowerCase()
        const matchKeyword = !keyword.value || text.includes(keyword.value.toLowerCase())

        return matchStatus && matchKeyword
      })
    })

    const openDetail = (item) => {
      router.push({ name: 'ConsultationDetail', params: { id: item.id } })
    }

    onMounted(() => {
      const id = window.localStorage.getItem('medical_user_id')
      if (id) {
        userId.value = parseInt(id, 10)
        loadHistory()
      }
    })

    return {
      list,
      filters,
      statusFilter,
      keyword,
      completedCount,
      pendingCount,
      filteredList,
      formatTime,
      openDetail,
      goBack
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.history-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(20, 184, 166, 0.16), transparent 28%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), transparent 26%),
    linear-gradient(180deg, #07111d 0%, #091827 100%);
  color: #eef7ff;
  padding: 28px 24px 48px;
}

.page-shell {
  max-width: 1260px;
  margin: 0 auto;
}

.floating-back {
  position: fixed;
  top: 92px;
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
.status-pill,
.empty-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
}

.section-badge {
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
.summary-item p,
.empty-card p {
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
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: center;
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

.search-box {
  flex: 0 0 320px;
}

.search-box input {
  width: 100%;
  min-height: 46px;
  border-radius: 999px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  background: rgba(6, 17, 30, 0.84);
  color: #f2f9ff;
  padding: 0 18px;
}

.records-grid {
  display: grid;
  gap: 18px;
  margin-top: 22px;
}

.record-card,
.empty-card {
  padding: 24px;
  border-radius: 26px;
}

.record-card {
  cursor: pointer;
  transition: transform 0.25s ease, border-color 0.25s ease;
}

.record-card:hover {
  transform: translateY(-4px);
  border-color: rgba(125, 211, 252, 0.24);
}

.record-top,
.record-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.record-id {
  font-size: 22px;
  font-weight: 700;
  color: #f7fbff;
}

.record-time {
  margin-top: 8px;
  color: rgba(185, 214, 244, 0.66);
  font-size: 13px;
}

.status-pill.pending {
  border: 1px solid rgba(250, 204, 21, 0.26);
  background: rgba(250, 204, 21, 0.12);
  color: #fde68a;
}

.status-pill.done {
  border: 1px solid rgba(52, 211, 153, 0.24);
  background: rgba(52, 211, 153, 0.12);
  color: #86efac;
}

.record-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 18px;
}

.summary-item {
  padding: 18px;
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(8, 20, 35, 0.72);
}

.summary-label {
  display: block;
  margin-bottom: 10px;
  font-size: 12px;
  color: #9ce8ff;
  letter-spacing: 0.08em;
}

.record-footer {
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid rgba(125, 211, 252, 0.08);
  color: #b8e8ff;
  font-weight: 600;
}

.arrow {
  font-size: 18px;
}

.empty-card {
  text-align: center;
}

.empty-title {
  font-size: 24px;
  font-weight: 700;
  color: #f7fbff;
  margin-bottom: 12px;
}

.empty-action {
  margin-top: 16px;
  text-decoration: none;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
}

@media (max-width: 1080px) {
  .hero-panel,
  .record-body {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    flex-basis: auto;
  }
}

@media (max-width: 768px) {
  .history-page {
    padding: 20px 12px 32px;
  }

  .hero-panel,
  .toolbar,
  .record-card,
  .empty-card {
    padding: 22px;
    border-radius: 24px;
  }

  .record-top,
  .record-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
