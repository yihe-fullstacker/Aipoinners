<template>
  <div class="dashboard-page">
    <div class="dashboard-shell">
      <aside class="queue-panel">
        <div class="queue-panel__glow"></div>

        <div class="queue-head">
          <div>
            <span class="eyebrow">Consultation Queue</span>
            <h2>待会诊患者</h2>
            <p>按影像进入顺序集中处理，优先查看最新回传的病例。</p>
          </div>
          <div class="queue-count">{{ queue.length }}</div>
        </div>

        <div class="queue-list">
          <button
            v-for="item in queue"
            :key="item.id"
            v-memo="[item.id, item.status, current?.id]"
            type="button"
            class="queue-item"
            :class="{ active: current && current.id === item.id }"
            @click="selectConsultation(item)"
          >
            <div class="queue-item__top">
              <strong>会诊 #{{ item.id }}</strong>
              <span class="status-pill" :class="statusTone(item.status)">
                {{ formatStatus(item.status) }}
              </span>
            </div>
            <div class="queue-item__name">{{ getPatientName(item) }}</div>
            <div class="queue-item__meta">
              <span>{{ formatTime(item.createdAt) || '刚刚进入队列' }}</span>
              <span>{{ getComplaintPreview(item) }}</span>
            </div>
          </button>

          <div v-if="queue.length === 0" class="queue-empty">
            <div class="queue-empty__title">当前暂无待处理患者</div>
            <p>新的会诊单到达后，这里会自动刷新。</p>
          </div>
        </div>
      </aside>

      <main class="workspace">
        <header class="workspace-header">
          <div>
            <span class="eyebrow">Doctor Workspace</span>
            <h1>AI 辅助阅片与会诊工作台</h1>
            <p v-if="current">
              已进入 {{ patientName }} 的会诊流程，可在同一界面完成阅片、AI 复核、诊断与患者沟通。
            </p>
            <p v-else>
              从左侧选择一位患者，即可开始本次影像会诊。
            </p>
          </div>

          <div v-if="current" class="workspace-actions">
            <span class="workspace-badge">{{ formatStatus(current.status) }}</span>
            <button type="button" class="btn btn-secondary" @click="refreshCurrent" :disabled="loadingAi">
              刷新病例
            </button>
            <button type="button" class="btn btn-primary" @click="reAnalyze" :disabled="loadingAi || !current">
              <span v-if="loadingAi">AI 分析中...</span>
              <span v-else>重新调用 AI</span>
            </button>
          </div>
        </header>

        <section v-if="current" class="workspace-grid">
          <article class="viewer-card surface-card">
            <div class="card-head">
              <div>
                <span class="eyebrow">Medical Imaging</span>
                <h3>高精度影像视图</h3>
              </div>

              <div class="viewer-badges">
                <span class="soft-pill">病例 {{ patientCode }}</span>
                <span class="soft-pill">{{ localizedAiConfidenceText }}</span>
                <span class="soft-pill">{{ localizedAiModelText }}</span>
              </div>
            </div>

            <div class="viewer-stage">
              <div class="viewer-stage__halo"></div>

              <div v-if="currentImageUrl" class="image-frame">
                <div class="hud-chip hud-chip--left">Live Diagnostic View</div>
                <div class="hud-chip hud-chip--right">
                  {{ formatTime(current.createdAt) || '实时更新' }}
                </div>
                <img
                  :src="currentImageUrl"
                  alt="医学影像"
                  :class="['diagnostic-image', { 'is-loaded': imageLoadState === 'success' }]"
                  @load="handleImageLoad"
                  @error="handleImageError"
                >
                <div class="image-vignette"></div>
                <div class="scanline"></div>
                <div v-if="imageLoadState === 'loading'" class="image-status">
                  正在载入医学影像{{ imageCandidateIndex > 0 ? '，尝试备用地址中' : '' }}...
                </div>
                <div v-else-if="imageLoadState === 'error'" class="image-status image-status--error">
                  <p>当前影像地址加载失败，已尝试所有可用候选地址。</p>
                  <button type="button" class="mini-retry-btn" @click="retryImage">
                    重新尝试
                  </button>
                </div>
              </div>

              <div v-else class="viewer-empty">
                <div class="viewer-empty__title">未找到可用医学影像</div>
                <p>当前病例缺少影像地址，请先刷新队列或重新调用 AI 分析。</p>
              </div>
            </div>

            <div class="metric-strip">
              <div v-for="metric in localizedMetricCards" :key="metric.label" class="metric-card">
                <span>{{ metric.label }}</span>
                <strong>{{ metric.value }}</strong>
              </div>
            </div>
          </article>

          <div class="insight-column">
            <article class="surface-card info-card patient-card">
              <div class="card-head compact">
                <div>
                  <span class="eyebrow">Patient Snapshot</span>
                  <h3>患者概览</h3>
                </div>
                <div class="patient-avatar">{{ patientInitial }}</div>
              </div>

              <div class="profile-grid">
                <div class="profile-item">
                  <span>患者姓名</span>
                  <strong>{{ patientName }}</strong>
                </div>
                <div class="profile-item">
                  <span>会诊编号</span>
                  <strong>#{{ current.id }}</strong>
                </div>
                <div class="profile-item">
                  <span>进入时间</span>
                  <strong>{{ formatTime(current.createdAt) || 'N/A' }}</strong>
                </div>
                <div class="profile-item">
                  <span>当前状态</span>
                  <strong>{{ formatStatus(current.status) }}</strong>
                </div>
              </div>

              <div class="focus-panel">
                <span class="focus-label">患者主诉</span>
                <p>{{ complaintText }}</p>
              </div>
            </article>

            <article class="surface-card info-card ai-card">
              <div class="card-head">
                <div>
                  <span class="eyebrow">AI Insights</span>
                  <h3>AI 诊断摘要</h3>
                </div>
              </div>

              <div v-if="loadingAi" class="ai-loading">
                <div class="loading-ring"></div>
                <p>系统正在重新分析影像并生成重点摘要。</p>
              </div>

              <template v-else>
                <p class="ai-summary">{{ localizedAiSummary }}</p>

                <div v-if="localizedInsightList.length" class="insight-list">
                  <div
                    v-for="item in localizedInsightList"
                    :key="`${item.title}-${item.location}`"
                    class="insight-item"
                  >
                    <div class="insight-item__top">
                      <strong>{{ item.title }}</strong>
                      <span>{{ item.confidence }}</span>
                    </div>
                    <p>{{ item.location }}</p>
                  </div>
                </div>

                <div v-if="localizedStructuredFacts.length" class="fact-grid">
                  <div v-for="fact in localizedStructuredFacts" :key="fact.label" class="fact-item">
                    <span>{{ fact.label }}</span>
                    <strong>{{ fact.value }}</strong>
                  </div>
                </div>

                <div v-if="localizedRawAnalysisText" class="analysis-note">
                  {{ localizedRawAnalysisText }}
                </div>

                <div v-if="!aiAnalysisObj" class="analysis-empty">
                  当前暂无结构化 AI 结果，工作台已保留病例与影像，可继续手动诊断。
                </div>
              </template>
            </article>
          </div>

          <article class="surface-card editor-card">
            <div class="card-head">
              <div>
                <span class="eyebrow">Diagnosis Draft</span>
                <h3>医生诊断建议</h3>
              </div>
              <span class="editor-count">{{ diagnosisCharCount }} 字</span>
            </div>

            <textarea
              v-model="diagnosisText"
              class="diagnosis-text"
              placeholder="请填写本次影像会诊结论、风险判断以及后续建议。"
            />

            <div class="editor-footer">
              <p>建议结合 AI 结果、影像表现与临床问诊综合判断，再提交给患者端查看。</p>
              <button
                type="button"
                class="btn btn-primary"
                @click="submitDiagnosis"
                :disabled="!canSubmitDiagnosis"
              >
                <span v-if="submitting">提交中...</span>
                <span v-else>提交诊断报告</span>
              </button>
            </div>
          </article>

          <article class="surface-card chat-card">
            <div class="card-head">
              <div>
                <span class="eyebrow">Communication</span>
                <h3>与患者沟通</h3>
              </div>
            </div>

            <div class="chat-stream">
              <div
                v-for="msg in messages"
                :key="msg.id"
                v-memo="[msg.id, msg.content, msg.senderRole]"
                :class="['chat-bubble', msg.senderRole === 'DOCTOR' ? 'is-doctor' : 'is-patient']"
              >
                <div class="chat-meta">
                  {{ msg.senderRole === 'DOCTOR' ? '我（医生）' : patientName }} · {{ formatTime(msg.createdAt) }}
                </div>
                <div class="chat-content">{{ msg.content }}</div>
              </div>

              <div v-if="chatLoading" class="chat-status">聊天记录加载中...</div>
              <div v-if="chatError" class="chat-status chat-status--error">{{ chatError }}</div>
              <div v-if="!chatLoading && !messages.length" class="chat-status">
                暂无沟通记录，您可以先向患者发送说明。
              </div>
            </div>

            <div class="chat-composer">
              <textarea
                v-model="chatInput"
                placeholder="输入发给患者的沟通内容，例如检查说明、注意事项或复诊建议。"
              />
              <button type="button" @click="sendChat" :disabled="!canSendChat">
                {{ chatSending ? '发送中...' : '发送消息' }}
              </button>
            </div>
          </article>
        </section>

        <section v-else class="workspace-empty surface-card">
          <div class="workspace-empty__badge">AI Diagnostic Center</div>
          <h3>准备开始新的会诊</h3>
          <p>左侧队列会实时展示待处理患者。选择一位患者后，右侧将切换为完整的阅片工作台。</p>
        </section>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import apiClient, { imageUtils } from '../api/api'
import { useCurrentUser } from './ts/useCurrentUser'
import { useConsultationChat } from './ts/useConsultationChat'
import { localizeAnalysisPayload, localizeText } from '../utils/analysisLocalization'

const STATUS_LABELS = {
  WAITING: '待处理',
  PENDING: '待处理',
  QUEUED: '待处理',
  IN_PROGRESS: '会诊中',
  PROCESSING: '分析中',
  COMPLETED: '已完成',
  FINISHED: '已完成',
  CLOSED: '已归档'
}

const STATUS_TONES = {
  WAITING: 'tone-pending',
  PENDING: 'tone-pending',
  QUEUED: 'tone-pending',
  IN_PROGRESS: 'tone-processing',
  PROCESSING: 'tone-processing',
  COMPLETED: 'tone-success',
  FINISHED: 'tone-success',
  CLOSED: 'tone-success'
}

const parseAiAnalysis = (payload) => {
  if (!payload) return null

  if (typeof payload === 'string') {
    try {
      return JSON.parse(payload)
    } catch (error) {
      return { result: payload }
    }
  }

  return payload
}

const resolvePatientName = (item) =>
  item?.patient?.username || item?.patient?.nickname || item?.patientName || '未命名患者'

const truncateText = (value, maxLength = 18) => {
  if (!value) return ''
  return value.length > maxLength ? `${value.slice(0, maxLength)}...` : value
}

const formatPercent = (value) => {
  const numericValue = Number(value)
  return Number.isFinite(numericValue) ? `${(numericValue * 100).toFixed(1)}%` : '待返回'
}

const wait = (ms) => new Promise((resolve) => window.setTimeout(resolve, ms))
const isLikelyFileSystemPath = (value = '') => /^[a-zA-Z]:[\\/]/.test(value) || value.startsWith('\\\\')
const normalizeImageCandidate = (value) => {
  if (typeof value !== 'string') return ''

  const trimmed = value.trim()
  if (!trimmed || isLikelyFileSystemPath(trimmed)) {
    return ''
  }

  return imageUtils.ensureImageUrl(trimmed) || ''
}

export default {
  name: 'DoctorDashboard',
  setup() {
    // 关键数据需要完整响应式以确保图片正常加载
    const queue = ref([])
    const current = ref(null)
    const diagnosisText = ref('')
    const loadingAi = ref(false)
    const submitting = ref(false)
    const chatInput = ref('')
    const currentImageUrl = ref('')
    const imageLoadState = ref('idle')
    const imageCandidateIndex = ref(0)
    const queueSnapshotSignature = ref('')
    const warmedImageUrls = new Set()
    let queueTimer = null

    const { userId } = useCurrentUser()
    const consultationId = computed(() => (current.value ? current.value.id : null))

    const {
      messages,
      loading: chatLoading,
      sending: chatSending,
      error: chatError,
      loadMessages,
      sendMessage,
      startPolling,
      stopPolling
    } = useConsultationChat(consultationId)

    const aiAnalysisObj = computed(() => parseAiAnalysis(current.value?.aiAnalysisJson))
    const localizedAiAnalysis = computed(() => {
      const parsed = aiAnalysisObj.value
      return parsed ? (localizeAnalysisPayload(parsed) || parsed) : null
    })

    const patientName = computed(() => current.value?.patientName || resolvePatientName(current.value))
    const patientInitial = computed(() => patientName.value?.slice(0, 1) || 'P')
    const patientCode = computed(() => {
      const currentPatientId = current.value?.patientId || current.value?.patient?.id
      if (currentPatientId) {
        return `P-${String(currentPatientId).padStart(4, '0')}`
      }
      if (current.value?.id) {
        return `C-${String(current.value.id).padStart(4, '0')}`
      }
      return '--'
    })

    const complaintText = computed(() => {
      const text = typeof current.value?.patientComplaint === 'string'
        ? current.value.patientComplaint.trim()
        : ''

      return text || '患者暂未填写主诉，建议结合问诊信息继续完善。'
    })

    const imageCandidates = computed(() => {
      if (!current.value) return []

      const ai = aiAnalysisObj.value
      const candidates = [
        current.value.imageUrl,
        current.value.medicalImageId ? `/api/images/${current.value.medicalImageId}` : '',
        current.value.medicalImage?.id ? `/api/images/${current.value.medicalImage.id}` : '',
        current.value.medicalImage?.imageUrl,
        current.value.medicalImage?.url,
        ai?.imageUrl,
        ai?.image,
        ai?.imgUrl,
        ai?.medicalImage?.imageUrl,
        ai?.medicalImage?.url,
        ai?.imageId ? `/api/images/${ai.imageId}` : '',
        current.value.imagePath,
        ai?.imagePath,
        current.value.medicalImage?.filePath,
        ai?.medicalImage?.filePath
      ]

      return candidates
        .map((item) => normalizeImageCandidate(item))
        .filter((item, index, list) => item && list.indexOf(item) === index)
    })

    const buildQueueSignature = (items) => {
      if (!Array.isArray(items) || items.length === 0) return ''
      // 使用更高效的签名方式，只比较关键字段
      return items
        .map((item) => `${item.id}:${item.status}:${item.updatedAt || item.createdAt || ''}`)
        .join('|')
    }

    const prefetchImage = (url) => {
      if (!url || warmedImageUrls.has(url)) return
      warmedImageUrls.add(url)
      const image = new window.Image()
      image.decoding = 'async'
      image.src = url
    }

    const warmQueueImages = (items = []) => {
      items.slice(0, 1).forEach((item) => {
        const candidate = normalizeImageCandidate(
          item?.imageUrl || (item?.medicalImageId ? `/api/images/${item.medicalImageId}` : '')
        )
        if (candidate) {
          prefetchImage(candidate)
        }
      })
    }

    const syncImageCandidate = (index = 0) => {
      imageCandidateIndex.value = index
      currentImageUrl.value = imageCandidates.value[index] || ''
      imageLoadState.value = currentImageUrl.value ? 'loading' : 'empty'
      if (currentImageUrl.value) {
        prefetchImage(currentImageUrl.value)
      }
    }

    const aiConfidenceText = computed(() => {
      if (aiAnalysisObj.value?.confidence == null) return 'AI 置信度待返回'
      return `AI 置信度 ${formatPercent(aiAnalysisObj.value.confidence)}`
    })

    const aiModelText = computed(() => {
      return aiAnalysisObj.value?.modelUsed
        ? `模型 ${aiAnalysisObj.value.modelUsed}`
        : '模型 智能诊断引擎'
    })

    const metricCards = computed(() => {
      const findings = Array.isArray(aiAnalysisObj.value?.findings) ? aiAnalysisObj.value.findings : []

      return [
        {
          label: '会诊状态',
          value: current.value ? formatStatus(current.value.status) : '待处理'
        },
        {
          label: 'AI 置信度',
          value: aiAnalysisObj.value?.confidence == null ? '待返回' : formatPercent(aiAnalysisObj.value.confidence)
        },
        {
          label: '重点征象',
          value: findings.length ? `${findings.length} 项` : (aiAnalysisObj.value ? '已生成结论' : '待分析')
        },
        {
          label: '模型版本',
          value: aiAnalysisObj.value?.modelUsed || '智能诊断引擎'
        }
      ]
    })

    const structuredFacts = computed(() => {
      const ai = aiAnalysisObj.value
      if (!ai) return []

      const facts = []

      if (ai.confidence != null) {
        facts.push({ label: '总体置信度', value: formatPercent(ai.confidence) })
      }

      if (ai.modelUsed) {
        facts.push({ label: '模型版本', value: ai.modelUsed })
      }

      if (Array.isArray(ai.findings) && ai.findings.length > 0) {
        const firstFinding = ai.findings[0]

        if (firstFinding.type) {
          facts.push({ label: '首要征象', value: firstFinding.type })
        }

        if (firstFinding.location) {
          facts.push({ label: '关注位置', value: firstFinding.location })
        }

        if (firstFinding.confidence != null) {
          facts.push({ label: '局部置信度', value: formatPercent(firstFinding.confidence) })
        }
      }

      return facts.slice(0, 4)
    })

    const insightList = computed(() => {
      const findings = Array.isArray(aiAnalysisObj.value?.findings) ? aiAnalysisObj.value.findings : []

      return findings.slice(0, 3).map((finding, index) => ({
        title: finding.type || `征象 ${index + 1}`,
        location: [
          finding.location,
          finding.size ? `大小 ${finding.size}` : '',
          finding.characteristics || finding.description || '等待补充描述'
        ].filter(Boolean).join(' · '),
        confidence: finding.confidence == null ? '待评估' : formatPercent(finding.confidence)
      }))
    })

    const aiSummary = computed(() => {
      if (loadingAi.value) {
        return 'AI 正在重新读取影像并生成会诊摘要，请稍候。'
      }

      const ai = aiAnalysisObj.value
      if (!ai) {
        return '当前尚未生成 AI 分析结果，工作台已保留病例信息，可随时重新调用 AI 进行复核。'
      }

      if (Array.isArray(ai.findings) && ai.findings.length > 0) {
        const firstFinding = ai.findings[0]
        const typeText = firstFinding.type || '影像征象'
        const locationText = firstFinding.location ? `，主要位于${firstFinding.location}` : ''
        const confidenceText = firstFinding.confidence != null
          ? `，局部置信度 ${formatPercent(firstFinding.confidence)}`
          : ''

        return `AI 已识别 ${ai.findings.length} 项重点征象，首要关注 ${typeText}${locationText}${confidenceText}。`
      }

      if (typeof ai.diagnosis === 'string' && ai.diagnosis.trim()) {
        return `系统当前建议：${ai.diagnosis.trim()}`
      }

      if (typeof ai.result === 'string' && ai.result.trim()) {
        return ai.result.trim()
      }

      if (typeof ai.summary === 'string' && ai.summary.trim()) {
        return ai.summary.trim()
      }

      return 'AI 已返回结构化数据，建议结合影像表现与临床经验综合判断。'
    })

    const rawAnalysisText = computed(() => {
      const ai = aiAnalysisObj.value
      if (!ai) return ''

      const candidates = [
        typeof ai.diagnosis === 'string' ? ai.diagnosis.trim() : '',
        typeof ai.result === 'string' ? ai.result.trim() : '',
        typeof ai.summary === 'string' ? ai.summary.trim() : ''
      ]

      return candidates.find((item) => item && item !== aiSummary.value) || ''
    })

    const localizedFindings = computed(() =>
      Array.isArray(localizedAiAnalysis.value?.findings) ? localizedAiAnalysis.value.findings : []
    )

    const localizedAiConfidenceText = computed(() => {
      if (localizedAiAnalysis.value?.confidence == null) return 'AI 置信度待返回'
      return `AI 置信度 ${formatPercent(localizedAiAnalysis.value.confidence)}`
    })

    const localizedAiModelText = computed(() => {
      return localizedAiAnalysis.value?.modelUsed
        ? `模型 ${localizeText(localizedAiAnalysis.value.modelUsed)}`
        : '模型 智能诊断引擎'
    })

    /*
    const localizedMetricCards = computed(() => ([
      {
        label: '会诊状态',
        value: current.value ? formatStatus(current.value.status) : '待处理'
      },
      {
        label: 'AI 置信度',
        value: localizedAiAnalysis.value?.confidence == null ? '待返回' : formatPercent(localizedAiAnalysis.value.confidence)
      },
      {
        label: '重点征象',
        value: localizedFindings.value.length
          ? `${localizedFindings.value.length} 项`
          : (localizedAiAnalysis.value ? '已生成结论' : '待分析')
      },
      {
        label: '模型版本',
        value: localizedAiAnalysis.value?.modelUsed
          ? localizeText(localizedAiAnalysis.value.modelUsed)
          : '智能诊断引擎'
      }
    ]))
    */

    const localizedMetricCards = computed(() => ([
      {
        label: '\u4f1a\u8bca\u72b6\u6001',
        value: current.value ? formatStatus(current.value.status) : '\u5f85\u5904\u7406'
      },
      {
        label: 'AI \u7f6e\u4fe1\u5ea6',
        value: localizedAiAnalysis.value?.confidence == null
          ? '\u5f85\u8fd4\u56de'
          : formatPercent(localizedAiAnalysis.value.confidence)
      },
      {
        label: '\u91cd\u70b9\u5f81\u8c61',
        value: localizedFindings.value.length
          ? `${localizedFindings.value.length} \u9879`
          : (localizedAiAnalysis.value ? '\u5df2\u751f\u6210\u7ed3\u8bba' : '\u5f85\u5206\u6790')
      },
      {
        label: '\u6a21\u578b\u7248\u672c',
        value: localizedAiAnalysis.value?.modelUsed
          ? localizeText(localizedAiAnalysis.value.modelUsed)
          : '\u667a\u80fd\u8bca\u65ad\u5f15\u64ce'
      }
    ]))

    /*
    const localizedStructuredFacts = computed(() => {
      const ai = localizedAiAnalysis.value
      if (!ai) return []

      const facts = []

      if (ai.confidence != null) {
        facts.push({ label: '总体置信度', value: formatPercent(ai.confidence) })
      }

      if (ai.modelUsed) {
        facts.push({ label: '模型版本', value: localizeText(ai.modelUsed) })
      }

      if (localizedFindings.value.length > 0) {
        const firstFinding = localizedFindings.value[0]

        if (firstFinding.type) {
          facts.push({ label: '首要征象', value: localizeText(firstFinding.type) })
        }

        if (firstFinding.location) {
          facts.push({ label: '关注位置', value: localizeText(firstFinding.location) })
        }
      }

      return facts.slice(0, 4)
    })

    const localizedInsightList = computed(() =>
      localizedFindings.value.slice(0, 3).map((finding, index) => ({
        title: localizeText(finding.type) || `征象 ${index + 1}`,
        location: [
          localizeText(finding.location),
          finding.size ? `大小 ${localizeText(finding.size)}` : '',
          Array.isArray(finding.characteristics)
            ? finding.characteristics.map((item) => localizeText(item)).join(' · ')
            : localizeText(finding.characteristics || finding.description || '等待补充描述')
        ].filter(Boolean).join(' · '),
        confidence: finding.confidence == null ? '待评估' : formatPercent(finding.confidence)
      }))
    )

    const localizedAiSummary = computed(() => {
      if (loadingAi.value) {
        return 'AI 正在重新读取影像并生成会诊摘要，请稍候。'
      }

      const ai = localizedAiAnalysis.value
      if (!ai) {
        return '当前尚未生成 AI 分析结果，工作台已保留病例信息，可随时重新调用 AI 进行复核。'
      }

      if (localizedFindings.value.length > 0) {
        const firstFinding = localizedFindings.value[0]
        const typeText = localizeText(firstFinding.type) || '影像征象'
        const locationText = firstFinding.location ? `，主要位于${localizeText(firstFinding.location)}` : ''
        const confidenceText = firstFinding.confidence != null
          ? `，局部置信度 ${formatPercent(firstFinding.confidence)}`
          : ''

        return `AI 已识别 ${localizedFindings.value.length} 项重点征象，首要关注 ${typeText}${locationText}${confidenceText}。`
      }

      if (typeof ai.diagnosis === 'string' && ai.diagnosis.trim()) {
        return `系统当前建议：${localizeText(ai.diagnosis.trim())}`
      }

      if (typeof ai.result === 'string' && ai.result.trim()) {
        return localizeText(ai.result.trim())
      }

      if (typeof ai.summary === 'string' && ai.summary.trim()) {
        return localizeText(ai.summary.trim())
      }

      return 'AI 已返回结构化数据，建议结合影像表现与临床经验综合判断。'
    })

    })
    */

    const localizedStructuredFacts = computed(() => {
      const ai = localizedAiAnalysis.value
      if (!ai) return []

      const facts = []

      if (ai.confidence != null) {
        facts.push({ label: '\u603b\u4f53\u7f6e\u4fe1\u5ea6', value: formatPercent(ai.confidence) })
      }

      if (ai.modelUsed) {
        facts.push({ label: '\u6a21\u578b\u7248\u672c', value: localizeText(ai.modelUsed) })
      }

      if (localizedFindings.value.length > 0) {
        const firstFinding = localizedFindings.value[0]

        if (firstFinding.type) {
          facts.push({ label: '\u9996\u8981\u5f81\u8c61', value: localizeText(firstFinding.type) })
        }

        if (firstFinding.location) {
          facts.push({ label: '\u5173\u6ce8\u4f4d\u7f6e', value: localizeText(firstFinding.location) })
        }
      }

      return facts.slice(0, 4)
    })

    const localizedInsightList = computed(() =>
      localizedFindings.value.slice(0, 3).map((finding, index) => ({
        title: localizeText(finding.type) || `\u5f81\u8c61 ${index + 1}`,
        location: [
          localizeText(finding.location),
          finding.size ? `\u5927\u5c0f ${localizeText(finding.size)}` : '',
          Array.isArray(finding.characteristics)
            ? finding.characteristics.map((item) => localizeText(item)).join(' \u00b7 ')
            : localizeText(finding.characteristics || finding.description || '\u7b49\u5f85\u8865\u5145\u63cf\u8ff0')
        ].filter(Boolean).join(' \u00b7 '),
        confidence: finding.confidence == null ? '\u5f85\u8bc4\u4f30' : formatPercent(finding.confidence)
      }))
    )

    const localizedAiSummary = computed(() => {
      if (loadingAi.value) {
        return 'AI \u6b63\u5728\u91cd\u65b0\u8bfb\u53d6\u5f71\u50cf\u5e76\u751f\u6210\u4f1a\u8bca\u6458\u8981\uff0c\u8bf7\u7a0d\u5019\u3002'
      }

      const ai = localizedAiAnalysis.value
      if (!ai) {
        return '\u5f53\u524d\u5c1a\u672a\u751f\u6210 AI \u5206\u6790\u7ed3\u679c\uff0c\u5de5\u4f5c\u53f0\u5df2\u4fdd\u7559\u75c5\u4f8b\u4fe1\u606f\uff0c\u53ef\u968f\u65f6\u91cd\u65b0\u8c03\u7528 AI \u8fdb\u884c\u590d\u6838\u3002'
      }

      if (localizedFindings.value.length > 0) {
        const firstFinding = localizedFindings.value[0]
        const typeText = localizeText(firstFinding.type) || '\u5f71\u50cf\u5f81\u8c61'
        const locationText = firstFinding.location ? `\uff0c\u4e3b\u8981\u4f4d\u4e8e${localizeText(firstFinding.location)}` : ''
        const confidenceText = firstFinding.confidence != null
          ? `\uff0c\u5c40\u90e8\u7f6e\u4fe1\u5ea6 ${formatPercent(firstFinding.confidence)}`
          : ''

        return `AI \u5df2\u8bc6\u522b ${localizedFindings.value.length} \u9879\u91cd\u70b9\u5f81\u8c61\uff0c\u9996\u8981\u5173\u6ce8 ${typeText}${locationText}${confidenceText}\u3002`
      }

      if (typeof ai.diagnosis === 'string' && ai.diagnosis.trim()) {
        return `\u7cfb\u7edf\u5f53\u524d\u5efa\u8bae\uff1a${localizeText(ai.diagnosis.trim())}`
      }

      if (typeof ai.result === 'string' && ai.result.trim()) {
        return localizeText(ai.result.trim())
      }

      if (typeof ai.summary === 'string' && ai.summary.trim()) {
        return localizeText(ai.summary.trim())
      }

      return 'AI \u5df2\u8fd4\u56de\u7ed3\u6784\u5316\u6570\u636e\uff0c\u5efa\u8bae\u7ed3\u5408\u5f71\u50cf\u8868\u73b0\u4e0e\u4e34\u5e8a\u7ecf\u9a8c\u7efc\u5408\u5224\u65ad\u3002'
    })

    const localizedRawAnalysisText = computed(() => {
      const ai = localizedAiAnalysis.value
      if (!ai) return ''

      const candidates = [
        typeof ai.diagnosis === 'string' ? localizeText(ai.diagnosis.trim()) : '',
        typeof ai.result === 'string' ? localizeText(ai.result.trim()) : '',
        typeof ai.summary === 'string' ? localizeText(ai.summary.trim()) : ''
      ]

      return candidates.find((item) => item && item !== localizedAiSummary.value) || ''
    })

    const diagnosisCharCount = computed(() => diagnosisText.value.trim().length)
    const canSubmitDiagnosis = computed(() =>
      Boolean(current.value && diagnosisText.value.trim() && !submitting.value)
    )
    const canSendChat = computed(() =>
      Boolean(current.value && chatInput.value.trim() && !chatSending.value)
    )

    const formatTime = (ts) => {
      if (!ts) return ''
      try {
        let date
        // 处理各种时间格式
        if (typeof ts === 'string') {
          // 处理逗号分隔的格式：2026,4,21,15,28,27
          if (ts.includes(',')) {
            const parts = ts.split(',').map(Number)
            if (parts.length >= 3) {
              date = new Date(
                parts[0],
                parts[1] - 1, // 月份从 0 开始
                parts[2],
                parts[3] || 0,
                parts[4] || 0,
                parts[5] || 0
              )
            }
          }
          // 如果还没转换成功，尝试直接转换
          if (!date || isNaN(date.getTime())) {
            date = new Date(ts)
          }
        } else if (typeof ts === 'number') {
          date = new Date(ts)
        } else if (ts instanceof Date) {
          date = ts
        } else {
          // 尝试转换为日期
          date = new Date(String(ts))
        }

        // 检查日期是否有效
        if (!date || isNaN(date.getTime())) {
          return String(ts)
        }

        // 格式化为 YYYY-MM-DD HH:mm:ss
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      } catch (error) {
        // 如果出错，返回原始值
        return String(ts)
      }
    }

    const formatStatus = (status) => STATUS_LABELS[status] || status || '待处理'

    const statusTone = (status) => STATUS_TONES[status] || 'tone-pending'

    const getPatientName = (item) => resolvePatientName(item)

    const getComplaintPreview = (item) => {
      const complaint = typeof item?.patientComplaint === 'string' ? item.patientComplaint.trim() : ''
      return truncateText(complaint || '等待补充主诉')
    }

    const handleImageLoad = () => {
      imageLoadState.value = 'success'
    }

    const handleImageError = () => {
      const nextIndex = imageCandidateIndex.value + 1
      if (nextIndex < imageCandidates.value.length) {
        syncImageCandidate(nextIndex)
        return
      }
      imageLoadState.value = 'error'
    }

    // 监听主题变化，确保图片正常显示
    const handleThemeChange = () => {
      if (currentImageUrl.value && imageLoadState.value !== 'success') {
        // 如果图片已加载但显示不正确，重新触发加载
        const tempUrl = currentImageUrl.value
        currentImageUrl.value = ''
        imageLoadState.value = 'empty'
        window.setTimeout(() => {
          currentImageUrl.value = tempUrl
          imageLoadState.value = 'loading'
        }, 50)
      }
    }

    const retryImage = () => {
      const nextCandidate = imageCandidates.value[0] || ''
      imageCandidateIndex.value = 0
      currentImageUrl.value = ''
      imageLoadState.value = nextCandidate ? 'loading' : 'empty'
      window.setTimeout(() => {
        currentImageUrl.value = nextCandidate
      }, 80)
    }

    const upsertQueueItem = (item) => {
      const index = queue.value.findIndex((queueItem) => queueItem.id === item.id)

      if (index >= 0) {
        queue.value.splice(index, 1, item)
        return
      }

      queue.value.unshift(item)
    }

    const applyQueueState = (nextQueue) => {
      const nextSignature = buildQueueSignature(nextQueue)
      const hasChanged = nextSignature !== queueSnapshotSignature.value

      if (hasChanged) {
        queue.value = nextQueue
        queueSnapshotSignature.value = nextSignature
        warmQueueImages(nextQueue)
      }

      if (!nextQueue.length) {
        current.value = null
        diagnosisText.value = ''
        stopPolling()
        return
      }

      if (!current.value) {
        selectConsultation(nextQueue[0])
        return
      }

      const freshCurrent = nextQueue.find((item) => item.id === current.value.id)
      if (freshCurrent) {
        current.value = freshCurrent
        diagnosisText.value = freshCurrent.doctorDiagnosis || diagnosisText.value
        return
      }

      selectConsultation(nextQueue[0])
    }

    const loadQueue = async () => {
      try {
        const res = await apiClient.get('/doctor/queue')
        applyQueueState(Array.isArray(res.data) ? res.data : [])
      } catch (error) {
        console.error('加载待诊队列失败', error)
      }
    }

    const startQueuePolling = () => {
      if (queueTimer !== null) return
      queueTimer = window.setInterval(() => {
        if (document.hidden) return
        window.requestAnimationFrame(() => loadQueue())
      }, 5000)
    }

    const stopQueuePolling = () => {
      if (queueTimer !== null) {
        window.clearInterval(queueTimer)
        queueTimer = null
      }
    }

    const selectConsultation = (item) => {
      if (!item) return
      // 避免重复选择相同会诊
      if (current.value && current.value.id === item.id) return
      current.value = item
      diagnosisText.value = item.doctorDiagnosis || ''
      stopPolling()
      warmQueueImages([item])
      loadMessages()
      startPolling()
    }

    const refreshCurrent = async () => {
      if (!current.value) return

      try {
        const res = await apiClient.get('/doctor/queue')
        const nextQueue = Array.isArray(res.data) ? res.data : []
        applyQueueState(nextQueue)
        const freshCurrent = nextQueue.find((item) => item.id === current.value.id)

        if (freshCurrent) {
          current.value = freshCurrent
          diagnosisText.value = freshCurrent.doctorDiagnosis || diagnosisText.value
          await loadMessages()
        }
      } catch (error) {
        console.error('刷新当前会诊失败', error)
      }
    }

    const pollForUpdate = async (targetId, maxAttempts = 20, interval = 2000) => {
      for (let attempt = 0; attempt < maxAttempts; attempt += 1) {
        try {
          const res = await apiClient.get('/doctor/queue')
          const list = Array.isArray(res.data) ? res.data : []
          const updatedItem = list.find((item) => item.id === targetId)

          if (updatedItem?.aiAnalysisJson) {
            return { item: updatedItem, list }
          }
        } catch (error) {
          console.error('轮询 AI 结果失败', error)
        }

        await wait(interval)
      }

      return null
    }

    const reAnalyze = async () => {
      if (!current.value) return

      loadingAi.value = true

      try {
        const res = await apiClient.post(`/doctor/consultations/${current.value.id}/ai`)
        current.value = res.data
        upsertQueueItem(res.data)

        if (!current.value.aiAnalysisJson) {
          const updatedPayload = await pollForUpdate(current.value.id)

          if (updatedPayload?.item) {
            queue.value = updatedPayload.list
            current.value = updatedPayload.item
          } else {
            window.alert('AI 分析仍在后台处理中，请稍后点击“刷新病例”查看最新结果。')
          }
        }
      } catch (error) {
        console.error('重新调用 AI 失败', error)
        window.alert('调用 AI 分析失败，请稍后重试。')
      } finally {
        loadingAi.value = false
      }
    }

    const submitDiagnosis = async () => {
      if (!current.value || !diagnosisText.value.trim()) return

      if (!userId.value) {
        window.alert('当前未获取到医生身份，请重新登录后再提交诊断。')
        return
      }

      submitting.value = true

      try {
        const res = await apiClient.post('/doctor/diagnose', null, {
          params: {
            consultationId: current.value.id,
            doctorId: userId.value,
            diagnosisText: diagnosisText.value.trim()
          }
        })

        current.value = res.data
        diagnosisText.value = res.data.doctorDiagnosis || diagnosisText.value
        queue.value = queue.value.filter((item) => item.id !== res.data.id)
        if (queue.value.length > 0) {
          selectConsultation(queue.value[0])
        } else {
          current.value = null
          diagnosisText.value = ''
          stopPolling()
        }
        window.alert('诊断报告已提交，患者端可查看最新结果。')
      } catch (error) {
        console.error('提交诊断失败', error)
        window.alert('提交诊断失败，请检查网络后重试。')
      } finally {
        submitting.value = false
      }
    }

    const sendChat = async () => {
      if (!current.value || !chatInput.value.trim()) return

      if (!userId.value) {
        window.alert('当前未获取到医生身份，请重新登录后再发送消息。')
        return
      }

      const content = chatInput.value.trim()
      const sent = await sendMessage(content, userId.value)
      if (sent) {
        chatInput.value = ''
      }
    }

    onMounted(() => {
      loadQueue()
      startQueuePolling()
    })

    watch([imageCandidates, current], () => {
      syncImageCandidate(0);
    }, { immediate: true, deep: false })

    onBeforeUnmount(() => {
      stopPolling()
      stopQueuePolling()
    })

    return {
      queue,
      current,
      diagnosisText,
      loadingAi,
      submitting,
      messages,
      chatInput,
      chatLoading,
      chatSending,
      chatError,
      aiAnalysisObj,
      patientName,
      patientInitial,
      patientCode,
      complaintText,
      currentImageUrl,
      imageLoadState,
      imageCandidateIndex,
      localizedAiConfidenceText,
      localizedAiModelText,
      localizedMetricCards,
      localizedStructuredFacts,
      localizedInsightList,
      localizedAiSummary,
      localizedRawAnalysisText,
      aiConfidenceText,
      aiModelText,
      metricCards,
      structuredFacts,
      insightList,
      aiSummary,
      rawAnalysisText,
      diagnosisCharCount,
      canSubmitDiagnosis,
      canSendChat,
      formatTime,
      formatStatus,
      statusTone,
      getPatientName,
      getComplaintPreview,
      handleImageLoad,
      handleImageError,
      retryImage,
      selectConsultation,
      refreshCurrent,
      reAnalyze,
      submitDiagnosis,
      sendChat
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.dashboard-page {
  min-height: calc(100vh - 56px);
  padding: 20px;
  background:
    radial-gradient(circle at top left, rgba(79, 209, 197, 0.18), transparent 24%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.18), transparent 28%),
    linear-gradient(180deg, #07121d 0%, #030914 100%);
  color: #edf6ff;
  font-family: "Avenir Next", "PingFang SC", "Microsoft YaHei", "Segoe UI", sans-serif;
}

.dashboard-shell {
  display: grid;
  grid-template-columns: minmax(280px, 320px) minmax(0, 1fr);
  gap: 20px;
  min-height: calc(100vh - 96px);
}

.surface-card,
.queue-panel,
.workspace-header {
  position: relative;
  border: 1px solid rgba(125, 211, 252, 0.14);
  background: linear-gradient(180deg, rgba(7, 20, 34, 0.92), rgba(3, 11, 22, 0.96));
  box-shadow: 0 24px 64px rgba(0, 7, 16, 0.34);
  backdrop-filter: blur(18px);
  contain: layout paint style;
  content-visibility: auto;
  will-change: opacity, transform;
}

.queue-panel {
  overflow: hidden;
  border-radius: 30px;
  padding: 24px;
}

.queue-panel__glow {
  position: absolute;
  top: -60px;
  right: -20px;
  width: 180px;
  height: 180px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(110, 231, 219, 0.28), rgba(110, 231, 219, 0));
  filter: blur(8px);
  /* 移除动画以提升性能 */
  opacity: 0.8;
  will-change: transform, opacity;
}

.queue-head {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 22px;
}

.queue-head h2,
.workspace-header h1,
.card-head h3,
.workspace-empty h3 {
  margin: 12px 0 0;
  color: #f7fbff;
}

.queue-head h2 {
  font-size: 30px;
}

.queue-head p,
.workspace-header p,
.focus-panel p,
.ai-summary,
.analysis-note,
.workspace-empty p,
.queue-empty p,
.insight-item p,
.editor-footer p,
.viewer-empty p {
  color: rgba(220, 235, 255, 0.74);
  line-height: 1.8;
}

.eyebrow,
.workspace-badge,
.soft-pill,
.status-pill,
.hud-chip,
.editor-count,
.workspace-empty__badge,
.focus-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
}

.eyebrow {
  min-height: 32px;
  padding: 0 12px;
  border: 1px solid rgba(110, 231, 219, 0.22);
  background: rgba(45, 212, 191, 0.08);
  color: #91f7eb;
  font-size: 11px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.queue-count {
  min-width: 56px;
  min-height: 56px;
  padding: 0 18px;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  background: rgba(5, 18, 32, 0.72);
  display: grid;
  place-items: center;
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
}

.queue-list {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: calc(100vh - 190px);
  overflow-y: auto;
  padding-right: 6px;
}

.queue-item {
  appearance: none;
  width: 100%;
  text-align: left;
  padding: 16px 18px;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: linear-gradient(180deg, rgba(10, 24, 38, 0.9), rgba(4, 13, 24, 0.9));
  color: #e8f6ff;
  cursor: pointer;
  /* 简化过渡效果 */
  transition: background 0.1s ease;
}

.queue-item:hover {
  border-color: rgba(103, 232, 249, 0.24);
}

.queue-item.active {
  border-color: rgba(103, 232, 249, 0.28);
  background:
    linear-gradient(135deg, rgba(34, 197, 94, 0.14), rgba(14, 165, 233, 0.22)),
    linear-gradient(180deg, rgba(10, 24, 38, 0.96), rgba(4, 13, 24, 0.96));
}

.queue-item__top,
.insight-item__top,
.workspace-actions,
.card-head,
.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.queue-item__top {
  margin-bottom: 12px;
}

.queue-item__name {
  font-size: 17px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 10px;
}

.queue-item__meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 12px;
  color: rgba(201, 221, 245, 0.72);
}

.status-pill,
.workspace-badge,
.soft-pill,
.editor-count,
.workspace-empty__badge,
.focus-label {
  min-height: 30px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 700;
}

.status-pill.tone-pending {
  background: rgba(56, 189, 248, 0.12);
  color: #8be5ff;
}

.status-pill.tone-processing {
  background: rgba(250, 204, 21, 0.14);
  color: #fde68a;
}

.status-pill.tone-success {
  background: rgba(74, 222, 128, 0.14);
  color: #9ff1b5;
}

.queue-empty {
  padding: 24px 20px;
  border-radius: 24px;
  border: 1px dashed rgba(125, 211, 252, 0.18);
  background: rgba(5, 16, 29, 0.68);
}

.queue-empty__title {
  font-size: 18px;
  font-weight: 700;
  color: #f7fbff;
  margin-bottom: 8px;
}

.workspace {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.workspace-header {
  border-radius: 30px;
  padding: 28px 30px;
}

.workspace-header h1 {
  font-size: clamp(30px, 4vw, 46px);
  line-height: 1.08;
}

.workspace-header p {
  margin-top: 12px;
  max-width: 760px;
}

.workspace-actions {
  flex-wrap: wrap;
  justify-content: flex-end;
}

.workspace-badge,
.soft-pill,
.editor-count,
.workspace-empty__badge,
.focus-label {
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(8, 20, 35, 0.8);
  color: #d7f1ff;
}

.btn {
  min-height: 42px;
  padding: 0 18px;
  border-radius: 999px;
  border: none;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  /* 简化过渡效果 */
  transition: opacity 0.1s ease;
}

.btn:hover:not(:disabled) {
  /* 移除变换动画 */
  opacity: 0.9;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-primary {
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #04253c;
  box-shadow: 0 14px 30px rgba(56, 189, 248, 0.22);
}

.btn-secondary {
  background: rgba(8, 20, 35, 0.8);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #eef8ff;
}

.workspace-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.85fr);
  grid-template-areas:
    "viewer side"
    "editor chat";
  gap: 20px;
  align-items: stretch;
}

.viewer-card {
  grid-area: viewer;
  padding: 24px;
  border-radius: 28px;
}

.insight-column {
  grid-area: side;
  display: grid;
  gap: 20px;
}

.editor-card {
  grid-area: editor;
  padding: 24px;
  border-radius: 28px;
  min-height: 320px;
}

.chat-card {
  grid-area: chat;
  padding: 24px;
  border-radius: 28px;
  min-height: 320px;
  display: flex;
  flex-direction: column;
}

.card-head {
  align-items: flex-start;
  margin-bottom: 18px;
}

.card-head.compact {
  align-items: center;
}

.card-head h3 {
  font-size: 28px;
  line-height: 1.18;
}

.viewer-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.viewer-stage {
  position: relative;
  min-height: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 26px;
  overflow: hidden;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: radial-gradient(circle at center, rgba(18, 53, 83, 0.48), rgba(1, 8, 18, 0.98) 74%);
}

.viewer-stage__halo {
  position: absolute;
  width: 68%;
  aspect-ratio: 1;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(74, 222, 128, 0.14), rgba(56, 189, 248, 0.16), rgba(0, 0, 0, 0));
  filter: blur(22px);
  /* 移除动画以提升性能 */
  opacity: 0.8;
  will-change: transform, opacity;
}

.image-frame {
  position: relative;
  width: calc(100% - 54px);
  height: calc(100% - 54px);
  min-height: 468px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: linear-gradient(180deg, rgba(5, 14, 25, 0.98), rgba(1, 6, 14, 0.98));
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.02),
    inset 0 -40px 90px rgba(2, 10, 20, 0.4),
    0 26px 60px rgba(1, 8, 16, 0.36);
}

.hud-chip {
  position: absolute;
  top: 18px;
  left: 18px;
  z-index: 3;
  min-height: 30px;
  padding: 0 12px;
  border: 1px solid rgba(125, 211, 252, 0.14);
  background: rgba(4, 14, 27, 0.72);
  color: #d7f3ff;
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.hud-chip--right {
  left: auto;
  right: 18px;
}

.diagnostic-image {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 28px;
  opacity: 1;
  filter: brightness(1.04) contrast(1.12) saturate(1.04);
  /* 移除动画以提升性能 */
  transition: opacity 0.32s ease, filter 0.32s ease;
  will-change: opacity;
}

.diagnostic-image:not(.is-loaded) {
  opacity: 0.3;
  filter: brightness(1.03) contrast(1.08) saturate(1.02);
}

.image-vignette {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  background: radial-gradient(circle at center, transparent 38%, rgba(0, 0, 0, 0.42) 100%);
}

.scanline {
  position: absolute;
  inset: -20%;
  z-index: 2;
  pointer-events: none;
  background: linear-gradient(180deg, transparent 0%, rgba(125, 211, 252, 0.02) 48%, transparent 100%);
  mix-blend-mode: screen;
  /* 移除动画以提升性能 */
  opacity: 0.3;
}

.image-status {
  position: absolute;
  inset: auto 18px 18px 18px;
  z-index: 4;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 18px;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(4, 14, 27, 0.82);
  color: #d7f3ff;
  backdrop-filter: blur(12px);
}

.image-status--error {
  border-color: rgba(248, 113, 113, 0.2);
  background: rgba(56, 16, 26, 0.8);
  color: #fee2e2;
}

.image-status p {
  margin: 0;
  line-height: 1.7;
}

.mini-retry-btn {
  min-width: 104px;
  min-height: 38px;
  padding: 0 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: inherit;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.viewer-empty {
  position: relative;
  z-index: 1;
  max-width: 420px;
  padding: 28px;
  text-align: center;
}

.viewer-empty__title {
  font-size: 24px;
  font-weight: 700;
  color: #f7fbff;
  margin-bottom: 12px;
}

.metric-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.metric-card,
.profile-item,
.fact-item,
.insight-item {
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(8, 20, 35, 0.72);
}

.metric-card {
  padding: 16px;
}

.metric-card span,
.profile-item span,
.fact-item span {
  display: block;
  font-size: 12px;
  color: #90dfff;
  margin-bottom: 8px;
}

.metric-card strong,
.profile-item strong,
.fact-item strong,
.insight-item strong {
  color: #f7fbff;
}

.info-card {
  padding: 22px;
  border-radius: 28px;
}

.patient-avatar {
  width: 54px;
  height: 54px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, rgba(94, 234, 212, 0.18), rgba(56, 189, 248, 0.28));
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.06);
}

.profile-grid,
.fact-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.profile-grid {
  margin-bottom: 16px;
}

.profile-item,
.fact-item {
  padding: 16px;
}

.focus-panel {
  padding: 18px;
  border-radius: 24px;
  border: 1px solid rgba(110, 231, 219, 0.1);
  background: linear-gradient(180deg, rgba(8, 20, 35, 0.8), rgba(4, 13, 24, 0.88));
}

.focus-label {
  margin-bottom: 12px;
}

.ai-loading {
  padding: 26px 0;
  display: grid;
  place-items: center;
  gap: 14px;
}

.loading-ring {
  width: 48px;
  height: 48px;
  border-radius: 999px;
  border: 3px solid rgba(125, 211, 252, 0.18);
  border-top-color: #6ce7da;
  animation: spin 1s linear infinite;
}

.insight-list {
  display: grid;
  gap: 12px;
  margin: 18px 0;
}

.insight-item {
  padding: 16px;
}

.insight-item__top {
  margin-bottom: 8px;
}

.analysis-note,
.analysis-empty {
  margin-top: 16px;
  padding: 16px;
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(8, 20, 35, 0.72);
}

.diagnosis-text,
.chat-composer textarea {
  width: 100%;
  border-radius: 24px;
  border: 1px solid rgba(125, 211, 252, 0.1);
  background: linear-gradient(180deg, rgba(6, 16, 29, 0.9), rgba(3, 11, 20, 0.96));
  color: #eef7ff;
  font-size: 14px;
  line-height: 1.8;
  resize: vertical;
  outline: none;
  transition: border-color 0.24s ease, box-shadow 0.24s ease;
}

.diagnosis-text:focus,
.chat-composer textarea:focus {
  border-color: rgba(94, 234, 212, 0.26);
  /* 移除box-shadow以避免重绘 */
}

.diagnosis-text {
  min-height: 220px;
  padding: 20px;
  flex: 1;
}

.editor-footer {
  margin-top: 18px;
  align-items: flex-end;
}

.editor-footer p {
  max-width: 520px;
}

.chat-stream {
  flex: 1;
  min-height: 260px;
  max-height: 460px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 4px 2px 12px;
}

.chat-bubble {
  max-width: 78%;
  padding: 14px 16px;
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(8, 20, 35, 0.78);
  box-shadow: 0 16px 28px rgba(2, 10, 20, 0.16);
}

.chat-bubble.is-doctor {
  align-self: flex-end;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.26), rgba(16, 185, 129, 0.22));
  border-color: rgba(94, 234, 212, 0.18);
}

.chat-bubble.is-patient {
  align-self: flex-start;
}

.chat-meta {
  font-size: 11px;
  color: rgba(220, 235, 255, 0.64);
  margin-bottom: 8px;
}

.chat-content {
  white-space: pre-wrap;
  line-height: 1.75;
  color: #eef7ff;
}

.chat-status {
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(8, 20, 35, 0.68);
  color: rgba(220, 235, 255, 0.78);
  text-align: center;
}

.chat-status--error {
  border: 1px solid rgba(248, 113, 113, 0.24);
  background: rgba(72, 22, 32, 0.44);
  color: #fecaca;
}

.chat-composer {
  margin-top: 16px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 140px;
  gap: 12px;
}

.chat-composer textarea {
  min-height: 88px;
  padding: 16px 18px;
}

.chat-composer button {
  border: none;
  border-radius: 22px;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #03283f;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 14px 30px rgba(56, 189, 248, 0.22);
}

.chat-composer button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  box-shadow: none;
}

.workspace-empty {
  min-height: 420px;
  border-radius: 30px;
  padding: 48px 32px;
  display: grid;
  place-items: center;
  text-align: center;
}

@keyframes haloBreath {
  0%,
  100% {
    transform: scale(0.96);
    opacity: 0.82;
  }

  50% {
    transform: scale(1.04);
    opacity: 1;
  }
}

@keyframes imageFloat {
  0%,
  100% {
    transform: scale(1.01) translateY(0);
  }

  50% {
    transform: scale(1.015) translateY(-4px);
  }
}

@keyframes sweep {
  0% {
    transform: translateY(-65%);
  }

  100% {
    transform: translateY(65%);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1380px) {
  .workspace-grid {
    grid-template-columns: 1fr;
    grid-template-areas:
      "viewer"
      "side"
      "editor"
      "chat";
  }
}

@media (max-width: 1120px) {
  .dashboard-shell {
    grid-template-columns: 1fr;
  }

  .queue-list {
    max-height: none;
  }

  .workspace-header {
    display: grid;
    gap: 18px;
  }

  .workspace-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 12px;
  }

  .queue-panel,
  .workspace-header,
  .viewer-card,
  .info-card,
  .editor-card,
  .chat-card,
  .workspace-empty {
    padding: 20px;
    border-radius: 24px;
  }

  .card-head,
  .card-head.compact,
  .queue-item__top,
  .queue-item__meta,
  .editor-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .viewer-badges,
  .workspace-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .viewer-stage {
    min-height: 360px;
  }

  .image-frame {
    width: calc(100% - 26px);
    height: calc(100% - 26px);
    min-height: 320px;
  }

  .metric-strip,
  .profile-grid,
  .fact-grid,
  .chat-composer {
    grid-template-columns: 1fr;
  }

  .chat-bubble {
    max-width: 100%;
  }

  .workspace-actions .btn,
  .editor-footer .btn {
    width: 100%;
  }
}

@media (prefers-reduced-motion: reduce), (update: slow) {
  .queue-panel__glow,
  .viewer-stage__halo,
  .diagnostic-image,
  .scanline,
  .loading-ring {
    animation: none !important;
  }

  .surface-card,
  .queue-panel,
  .workspace-header,
  .image-status {
    backdrop-filter: none;
  }
}
</style>
