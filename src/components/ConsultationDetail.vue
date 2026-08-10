﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="consultation-page">
    <button class="floating-back" type="button" @click="goBack" aria-label="返回患者首页">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 5l-7 7 7 7"/>
      </svg>
    </button>

    <div class="page-shell">
      <section class="hero-panel">
        <div>
          <span class="section-badge">医生会诊详情</span>
          <h1>医患沟通、诊断结论与影像信息统一查看</h1>
          <p>
            这里集中展示当前会诊影像、医生诊断意见和实时沟通内容，帮助患者围绕同一病例完成追问、补充说明和后续诊疗决策。
          </p>
        </div>

        <div class="hero-stats">
          <article class="stat-card">
            <strong>{{ consultation.id ? `#${consultation.id}` : '--' }}</strong>
            <span>会诊编号</span>
          </article>
          <article class="stat-card">
            <strong>{{ messages.length }}</strong>
            <span>沟通消息数</span>
          </article>
          <article class="stat-card">
            <strong>{{ consultation.doctorDiagnosis ? '已反馈' : '待反馈' }}</strong>
            <span>医生结论状态</span>
          </article>
        </div>
      </section>

      <section class="content-grid">
        <div class="left-column">
          <article class="card-surface image-card">
            <div class="card-head">
              <div>
                <div class="section-kicker">影像资料</div>
                <h2>当前会诊影像</h2>
              </div>
              <router-link to="/smart-qa" class="ghost-link">进入智能问答</router-link>
            </div>

            <div class="image-panel">
              <img v-if="imageUrl" :src="imageUrl" alt="医学影像" />
              <div v-else class="empty-image">暂未获取到影像预览</div>
            </div>
          </article>

          <article class="card-surface diagnosis-card">
            <div class="card-head">
              <div>
                <div class="section-kicker">诊断结论</div>
                <h2>医生复核意见</h2>
              </div>
              <div class="status-pill" :class="consultation.doctorDiagnosis ? 'done' : 'pending'">
                {{ consultation.doctorDiagnosis ? '已出具意见' : '等待更新' }}
              </div>
            </div>
            <p class="diagnosis-text">
              {{ consultation.doctorDiagnosis || '医生暂未提交诊断结果，您可以先在下方补充说明、追问，或稍后刷新查看最新内容。' }}
            </p>
          </article>
        </div>

        <article class="card-surface chat-card">
          <div class="card-head chat-head">
            <div>
              <div class="section-kicker">在线沟通</div>
              <h2>医患交流记录</h2>
            </div>
            <div class="chat-meta">自动刷新中</div>
          </div>

          <div class="chat-guide">
            建议在提问中补充症状变化、用药情况和想重点了解的问题，便于医生更快给出更有针对性的回复。
          </div>

          <div class="chat-messages">
            <div
              v-for="msg in messages"
              :key="msg.id"
              :class="['msg', msg.senderRole === 'DOCTOR' ? 'msg-doctor' : 'msg-patient']"
            >
              <div class="msg-meta">
                <span>{{ msg.senderRole === 'DOCTOR' ? '医生' : '我' }}</span>
                <span>{{ formatTime(msg.createdAt) }}</span>
              </div>
              <div class="msg-content">{{ msg.content }}</div>
            </div>

            <div v-if="messages.length === 0" class="empty-chat">
              当前还没有沟通记录，您可以先发送第一条补充说明。
            </div>
          </div>

          <div class="chat-input">
            <textarea
              v-model="input"
              placeholder="向医生补充说明、描述症状变化，或继续追问报告中的疑问..."
            />
            <div class="chat-action-row">
              <span>{{ input.length }} / 500</span>
              <button type="button" @click="send" :disabled="sending || !input.trim()">
                {{ sending ? '发送中...' : '发送消息' }}
              </button>
            </div>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import apiClient, { imageUtils } from '../api/api'

export default {
  name: 'ConsultationDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const consultationId = ref(null)
    const consultation = ref({})
    const messages = ref([])
    const input = ref('')
    const sending = ref(false)
    const userId = ref(null)
    let timer = null

    const imageUrl = computed(() => {
      const imageId = consultation.value.medicalImageId || consultation.value.medicalImage?.id
      if (!imageId && !consultation.value.imageUrl) return ''
      const rawUrl = consultation.value.imageUrl || `/api/images/${imageId}`
      return imageUtils.ensureImageUrl(rawUrl)
    })

    const goBack = () => {
      router.push('/patient/home')
    }

    const formatTime = (ts) => {
      if (!ts) return ''
      return String(ts).replace('T', ' ').substring(0, 19)
    }

    const loadDetail = async () => {
      try {
        const res = await apiClient.get(`/patient/consultations/${consultationId.value}`)
        consultation.value = res.data || {}
      } catch (e) {
        console.error('加载会诊详情失败', e)
      }
    }

    const loadMessages = async () => {
      try {
        const res = await apiClient.get('/chat/messages', {
          params: { consultationId: consultationId.value }
        })
        messages.value = res.data || []
      } catch (e) {
        console.error('加载聊天记录失败', e)
      }
    }

    const send = async () => {
      if (!input.value.trim()) return
      sending.value = true
      try {
        await apiClient.post('/chat/send', {
          consultationId: consultationId.value,
          senderId: userId.value,
          content: input.value
        })
        input.value = ''
        await loadMessages()
      } catch (e) {
        console.error('发送消息失败', e)
      } finally {
        sending.value = false
      }
    }

    onMounted(() => {
      consultationId.value = route.params.id
      const id = window.localStorage.getItem('medical_user_id')
      if (id) {
        userId.value = parseInt(id, 10)
      }
      if (consultationId.value) {
        loadDetail()
        loadMessages()
        timer = setInterval(loadMessages, 3000)
      }
    })

    onBeforeUnmount(() => {
      if (timer) clearInterval(timer)
    })

    return {
      consultation,
      messages,
      input,
      sending,
      imageUrl,
      goBack,
      formatTime,
      send
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.consultation-page {
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
  transition: transform 0.25s ease, border-color 0.25s ease;
}

.floating-back:hover {
  transform: translateY(-2px);
  border-color: rgba(125, 211, 252, 0.4);
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
.section-kicker,
.status-pill,
.chat-meta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.section-badge,
.section-kicker,
.chat-meta {
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
.diagnosis-text,
.chat-guide,
.empty-chat,
.msg-content,
.chat-input textarea {
  color: rgba(220, 235, 255, 0.72);
  line-height: 1.8;
}

.hero-stats {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.stat-card,
.image-card,
.diagnosis-card,
.chat-card {
  border-radius: 26px;
}

.stat-card {
  padding: 18px;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(7, 20, 35, 0.76);
}

.stat-card strong {
  display: block;
  font-size: 28px;
  color: #ffffff;
  margin-bottom: 8px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1.02fr;
  gap: 22px;
  margin-top: 24px;
}

.left-column {
  display: grid;
  gap: 22px;
}

.card-surface {
  padding: 24px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.card-head h2 {
  margin-top: 10px;
  font-size: 28px;
  color: #f7fbff;
}

.ghost-link {
  color: #9fddff;
  text-decoration: none;
  font-weight: 600;
}

.ghost-link:hover {
  color: #effaff;
}

.image-panel {
  min-height: 360px;
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(6, 16, 28, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-panel img {
  width: 100%;
  height: 360px;
  object-fit: contain;
  background: #020817;
}

.empty-image {
  color: rgba(220, 235, 255, 0.58);
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

.diagnosis-text {
  min-height: 140px;
  padding: 18px 20px;
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.74);
  border: 1px solid rgba(125, 211, 252, 0.08);
  white-space: pre-wrap;
}

.chat-card {
  display: flex;
  flex-direction: column;
  min-height: 720px;
}

.chat-head {
  margin-bottom: 14px;
}

.chat-guide {
  margin-bottom: 16px;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(8, 21, 36, 0.7);
  border: 1px solid rgba(125, 211, 252, 0.08);
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-right: 4px;
}

.msg {
  max-width: 78%;
  padding: 16px 18px;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.1);
}

.msg-doctor {
  align-self: flex-start;
  background: rgba(8, 23, 39, 0.84);
}

.msg-patient {
  align-self: flex-end;
  background: linear-gradient(135deg, rgba(8, 75, 92, 0.86), rgba(19, 63, 104, 0.88));
}

.msg-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 12px;
  color: rgba(185, 214, 244, 0.7);
}

.empty-chat {
  padding: 18px;
  border-radius: 18px;
  border: 1px dashed rgba(125, 211, 252, 0.16);
  text-align: center;
}

.chat-input {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid rgba(125, 211, 252, 0.08);
}

.chat-input textarea {
  width: 100%;
  min-height: 130px;
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  background: rgba(6, 17, 30, 0.84);
  color: #f2f9ff;
  padding: 16px;
  resize: vertical;
}

.chat-input textarea:focus {
  outline: none;
  border-color: rgba(94, 234, 212, 0.4);
}

.chat-action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  font-size: 12px;
  color: rgba(185, 214, 244, 0.68);
}

.chat-action-row button {
  min-width: 130px;
  min-height: 46px;
  padding: 0 18px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
  font-weight: 700;
  cursor: pointer;
}

.chat-action-row button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

@media (max-width: 1080px) {
  .hero-panel,
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .consultation-page {
    padding: 20px 12px 32px;
  }

  .hero-panel,
  .card-surface {
    padding: 22px;
    border-radius: 24px;
  }

  .card-head,
  .chat-action-row,
  .msg-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .msg {
    max-width: 100%;
  }
}
</style>
