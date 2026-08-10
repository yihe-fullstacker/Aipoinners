<template>
  <div v-if="shouldRender" class="medical-assistant">
    <div
      v-if="!isOpen"
      ref="triggerShellRef"
      class="assistant-trigger-shell"
      :style="triggerShellStyle"
    >
      <button
        type="button"
        class="assistant-trigger"
        aria-label="打开医疗智能助手"
        @pointerdown="startDrag('trigger', $event)"
        @click="handleTriggerClick"
      >
        <span class="trigger-glow trigger-glow--outer"></span>
        <span class="trigger-glow trigger-glow--inner"></span>
        <span class="trigger-orbit trigger-orbit--one"></span>
        <span class="trigger-orbit trigger-orbit--two"></span>
        <span class="trigger-particle trigger-particle--one"></span>
        <span class="trigger-particle trigger-particle--two"></span>
        <span class="trigger-bot-shell">
          <img :src="botImage" alt="医疗智能助手" class="trigger-bot" />
        </span>
        <span class="trigger-tag">MEDI BOT</span>
      </button>
    </div>

    <div
      v-if="isOpen"
      ref="panelShellRef"
      class="assistant-panel-shell"
      :style="panelShellStyle"
      @pointerdown="handlePanelPointerDown"
    >
      <section
        class="assistant-panel"
        :class="{ 'assistant-panel--mobile': isMobile, 'assistant-panel--dragging': dragState.type === 'panel' }"
      >
        <header class="panel-toolbar">
          <div class="toolbar-brand">
            <span class="toolbar-label">医疗智能助理</span>
          </div>
          <div class="toolbar-actions">
            <button type="button" class="toolbar-btn" aria-label="清空对话" @click="resetConversation">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M6 7h12M9 7V5h6v2m-7 3v7m4-7v7m4-7v7M8 19h8a1 1 0 0 0 1-1V7H7v11a1 1 0 0 0 1 1z" />
              </svg>
            </button>
            <button type="button" class="toolbar-btn" aria-label="关闭助手" @click="isOpen = false">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M6 6l12 12M18 6L6 18" />
              </svg>
            </button>
          </div>
        </header>

        <div class="panel-hero">
          <div class="hero-stage hero-stage--hero">
            <div class="hero-stage__halo"></div>
            <div class="hero-stage__ring"></div>
            <div class="hero-stage__shadow"></div>
            <div class="hero-stage__platform"></div>
            <div class="hero-stage__badge">
              <div class="hero-stage__badge-core">
                <span></span>
                <span></span>
              </div>
            </div>
          </div>

          <div class="hero-copy">
            <p class="hero-kicker">Medical Copilot</p>
            <h2>你好，我是医疗智能助手</h2>
            <p>
              我可以结合当前页面场景，帮你解释医学内容、梳理报告重点，或给出下一步阅读建议。
            </p>
          </div>
        </div>

        <div v-if="messages.length === 0" class="welcome-grid">
          <article class="welcome-card welcome-card--primary">
            <div class="welcome-card__eyebrow">本页帮助</div>
            <h3>{{ primaryCard.title }}</h3>
            <p>{{ primaryCard.description }}</p>
            <button type="button" class="card-action" @click="usePrompt(primaryCard.prompt)">
              立即提问
            </button>
          </article>

          <article class="welcome-card">
            <div class="welcome-card__eyebrow">推荐问题</div>
            <ul class="topic-list">
              <li v-for="topic in quickTopics" :key="topic">
                <button type="button" class="topic-btn" @click="usePrompt(topic)">
                  {{ topic }}
                </button>
              </li>
            </ul>
          </article>
        </div>

        <div class="conversation" ref="conversationRef">
          <div
            v-for="message in messages"
            :key="message.id"
            :class="['message-row', `message-row--${message.role}`]"
          >
            <div v-if="message.role === 'assistant'" class="assistant-avatar">
              <img :src="botImage" alt="医疗智能助手" />
            </div>

            <div
              :class="[
                'message-bubble',
                `message-bubble--${message.role}`,
                { 'message-bubble--loading': message.loading }
              ]"
            >
              <template v-if="message.loading">
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
              </template>
              <template v-else>
                <div class="message-text" v-html="renderMessageHtml(message.content)"></div>
                <small>{{ formatTime(message.createdAt) }}</small>
              </template>
            </div>
          </div>
        </div>

        <footer class="composer">
          <div class="composer-shell">
            <textarea
              v-model="userInput"
              class="composer-input"
              rows="2"
              maxlength="500"
              placeholder="请输入医疗相关问题，按 Enter 发送，Shift + Enter 换行"
              @keydown="handleComposerKeydown"
            ></textarea>
            <div class="composer-meta">
              <button type="button" class="composer-chip" @click="usePrompt(primaryCard.prompt)">
                页面帮助
              </button>
              <span>{{ userInput.length }}/500</span>
            </div>
          </div>

          <button type="button" class="composer-send" :disabled="!canSend" @click="sendMessage">
            <svg v-if="!isSending" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M3 20l18-8L3 4v6l12 2-12 2z" />
            </svg>
            <span v-else class="composer-spinner"></span>
          </button>
        </footer>
      </section>
    </div>
  </div>
</template>

<script>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { assistantAPI } from '@/api/api'
import botImage from '@/views/element/image/13.png'

const EXCLUDED_ROUTE_NAMES = new Set([
  'Landing',
  'About',
  'Instruction',
  'Login',
  'Register',
  'Home',
  'Root'
])

const VIEWPORT_PADDING = 12
const DEFAULT_TRIGGER_SIZE = { width: 128, height: 148 }
const DEFAULT_PANEL_SIZE = { width: 720, height: 760 }

let assistantMessageSeed = 0

export default {
  name: 'FloatingAIAssistant',
  props: {
    visible: {
      type: Boolean,
      default: true
    },
    courseContext: {
      type: Object,
      default: () => ({})
    },
    globalPreferred: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const route = useRoute()
    const isOpen = ref(false)
    const isSending = ref(false)
    const isMobile = ref(window.innerWidth <= 768)
    const scrollPosition = ref({ x: window.scrollX || 0, y: window.scrollY || 0 })
    const userInput = ref('')
    const messages = ref([])
    const conversationRef = ref(null)
    const isPrimaryInstance = ref(false)
    const triggerShellRef = ref(null)
    const panelShellRef = ref(null)
    const triggerPosition = ref({ x: 0, y: 0 })
    const panelPosition = ref({ x: 0, y: 0 })
    const hasTriggerPosition = ref(false)
    const hasPanelPosition = ref(false)
    const suppressTriggerClick = ref(false)
    const dragState = ref({
      type: '',
      pointerId: null,
      startPointerX: 0,
      startPointerY: 0,
      startX: 0,
      startY: 0,
      moved: false,
      sourceEl: null
    })
    let layoutObserver = null
    let scrollFrameId = 0

    const triggerShellStyle = computed(() => ({
      transform: `translate3d(${triggerPosition.value.x - scrollPosition.value.x}px, ${triggerPosition.value.y - scrollPosition.value.y}px, 0)`
    }))

    const panelShellStyle = computed(() => ({
      transform: `translate3d(${panelPosition.value.x - scrollPosition.value.x}px, ${panelPosition.value.y - scrollPosition.value.y}px, 0)`
    }))

    const syncScrollPosition = () => {
      scrollPosition.value = {
        x: window.scrollX || window.pageXOffset || 0,
        y: window.scrollY || window.pageYOffset || 0
      }
    }

    const getDocumentBounds = () => {
      const docEl = document.documentElement
      const body = document.body
      return {
        width: Math.max(docEl?.scrollWidth || 0, body?.scrollWidth || 0, window.innerWidth),
        height: Math.max(docEl?.scrollHeight || 0, body?.scrollHeight || 0, window.innerHeight)
      }
    }

    const getPointerPagePosition = (event) => ({
      x: typeof event.pageX === 'number' ? event.pageX : event.clientX + scrollPosition.value.x,
      y: typeof event.pageY === 'number' ? event.pageY : event.clientY + scrollPosition.value.y
    })

    const getSize = (type) => {
      const element = type === 'trigger' ? triggerShellRef.value : panelShellRef.value
      if (element) {
        return {
          width: element.offsetWidth,
          height: element.offsetHeight
        }
      }
      return type === 'trigger' ? DEFAULT_TRIGGER_SIZE : DEFAULT_PANEL_SIZE
    }

    const clamp = (value, min, max) => {
      if (max <= min) {
        return min
      }
      return Math.min(Math.max(value, min), max)
    }

    const clampPosition = (type, position) => {
      const size = getSize(type)
      const bounds = getDocumentBounds()
      return {
        x: clamp(position.x, VIEWPORT_PADDING, bounds.width - size.width - VIEWPORT_PADDING),
        y: clamp(position.y, VIEWPORT_PADDING, bounds.height - size.height - VIEWPORT_PADDING)
      }
    }

    const setDefaultTriggerPosition = () => {
      if (hasTriggerPosition.value) {
        return
      }
      const size = getSize('trigger')
      syncScrollPosition()
      triggerPosition.value = {
        x: scrollPosition.value.x + window.innerWidth - size.width - 24,
        y: scrollPosition.value.y + window.innerHeight - size.height - 24
      }
      triggerPosition.value = clampPosition('trigger', triggerPosition.value)
      hasTriggerPosition.value = true
    }

    const setDefaultPanelPosition = () => {
      const size = getSize('panel')
      const triggerSize = getSize('trigger')

      if (!hasPanelPosition.value) {
        const preferred = {
          x: triggerPosition.value.x + triggerSize.width - size.width + 16,
          y: triggerPosition.value.y - size.height + triggerSize.height
        }
        panelPosition.value = clampPosition('panel', preferred)
        hasPanelPosition.value = true
      } else {
        panelPosition.value = clampPosition('panel', panelPosition.value)
      }
    }

    const refreshViewportState = async () => {
      syncScrollPosition()
      isMobile.value = window.innerWidth <= 768
      await nextTick()
      if (hasTriggerPosition.value) {
        triggerPosition.value = clampPosition('trigger', triggerPosition.value)
      } else {
        setDefaultTriggerPosition()
      }
      if (isOpen.value) {
        setDefaultPanelPosition()
      } else if (hasPanelPosition.value) {
        panelPosition.value = clampPosition('panel', panelPosition.value)
      }
    }

    const finishDrag = () => {
      if (dragState.value.sourceEl?.releasePointerCapture && dragState.value.pointerId !== null) {
        try {
          dragState.value.sourceEl.releasePointerCapture(dragState.value.pointerId)
        } catch (error) {
          // Ignore release errors when the pointer capture was already cleared.
        }
      }
      if (dragState.value.type === 'trigger' && dragState.value.moved) {
        suppressTriggerClick.value = true
      }
      dragState.value = {
        type: '',
        pointerId: null,
        startPointerX: 0,
        startPointerY: 0,
        startX: 0,
        startY: 0,
        moved: false,
        sourceEl: null
      }
      document.body.style.userSelect = ''
      document.body.style.cursor = ''
      document.documentElement.style.cursor = ''
    }

    const handlePointerMove = (event) => {
      if (!dragState.value.type || dragState.value.pointerId !== event.pointerId) {
        return
      }

      const pointer = getPointerPagePosition(event)
      const deltaX = pointer.x - dragState.value.startPointerX
      const deltaY = pointer.y - dragState.value.startPointerY
      const moved = Math.abs(deltaX) > 4 || Math.abs(deltaY) > 4

      if ((dragState.value.type === 'panel' || moved || dragState.value.moved) && event.cancelable) {
        event.preventDefault()
      }

      if (dragState.value.type === 'trigger') {
        triggerPosition.value = clampPosition('trigger', {
          x: dragState.value.startX + deltaX,
          y: dragState.value.startY + deltaY
        })
      } else {
        panelPosition.value = clampPosition('panel', {
          x: dragState.value.startX + deltaX,
          y: dragState.value.startY + deltaY
        })
      }

      dragState.value.moved = dragState.value.moved || moved
    }

    const handlePointerUp = (event) => {
      if (dragState.value.pointerId !== event.pointerId) {
        return
      }
      if ((dragState.value.type === 'panel' || dragState.value.moved) && event.cancelable) {
        event.preventDefault()
      }
      finishDrag()
    }

    const shouldIgnorePanelDrag = (target) => {
      if (!(target instanceof Element)) {
        return false
      }

      if (target.closest('.toolbar-btn, .composer-send, .composer-chip, .card-action, .topic-btn')) {
        return true
      }

      const interactiveSelector = [
        'a',
        'button',
        'textarea',
        'input',
        'select',
        'option',
        'label',
        '[role="button"]',
        '[contenteditable="true"]'
      ].join(', ')

      return Boolean(target.closest(interactiveSelector))
    }

    const startDrag = (type, event) => {
      if (event.button !== undefined && event.button !== 0) {
        return
      }
      if (type === 'panel' && shouldIgnorePanelDrag(event.target)) {
        return
      }

      const sourceEl = event.currentTarget instanceof Element ? event.currentTarget : null
      const pointer = getPointerPagePosition(event)

      const current = type === 'trigger' ? triggerPosition.value : panelPosition.value
      dragState.value = {
        type,
        pointerId: event.pointerId,
        startPointerX: pointer.x,
        startPointerY: pointer.y,
        startX: current.x,
        startY: current.y,
        moved: false,
        sourceEl
      }

      if (type === 'panel' && event.cancelable) {
        event.preventDefault()
      }
      document.body.style.userSelect = 'none'
      document.body.style.cursor = 'grabbing'
      document.documentElement.style.cursor = 'grabbing'

      if (sourceEl?.setPointerCapture) {
        try {
          sourceEl.setPointerCapture(event.pointerId)
        } catch (error) {
          // setPointerCapture is best-effort; dragging still works via window listeners.
        }
      }
    }

    const handlePanelPointerDown = (event) => {
      startDrag('panel', event)
    }

    const handleTriggerClick = async () => {
      if (suppressTriggerClick.value) {
        suppressTriggerClick.value = false
        return
      }
      isOpen.value = true
      await nextTick()
      setDefaultPanelPosition()
    }

    const shouldRender = computed(() => {
      if (!props.visible || !isPrimaryInstance.value) {
        return false
      }
      return !EXCLUDED_ROUTE_NAMES.has(route.name)
    })

    const pageLabel = computed(() => route.meta?.title || props.courseContext?.title || route.name || '当前页面')

    const primaryCard = computed(() => {
      const byRoute = {
        Dashboard: {
          title: '分析结果解读',
          description: '结合当前影像分析页面，帮你快速理解指标、风险提示和下一步建议。',
          prompt: '请结合当前分析页面，帮我用通俗语言解释结果重点。'
        },
        AnalysisReport: {
          title: '报告结构梳理',
          description: '提炼报告结论、重点术语和适合进一步咨询医生的问题。',
          prompt: '请帮我梳理这份医学分析报告的关键结论和重点术语。'
        },
        ConsultationDetail: {
          title: '会诊沟通辅助',
          description: '整理补充说明和追问方向，让与医生的沟通更聚焦。',
          prompt: '请帮我整理几个适合继续追问医生的医学问题。'
        },
        MedicalCourse: {
          title: '课程答疑',
          description: '针对当前课程内容做知识解释，避免抽象术语难理解。',
          prompt: '请结合当前课程内容，用简洁方式帮我解释核心知识点。'
        }
      }

      return byRoute[route.name] || {
        title: pageLabel.value,
        description: '围绕当前页面内容提供医疗知识解释、阅读辅助和问题梳理。',
        prompt: `请结合 ${pageLabel.value} 页面，帮我解释这里最值得关注的医学信息。`
      }
    })

    const quickTopics = computed(() => {
      const routeTopics = {
        Dashboard: [
          '这一页里的置信度应该怎么理解？',
          '出现高风险提示时应该先做什么？',
          '热力图能说明什么，不能说明什么？'
        ],
        Reports: [
          '怎么看历史报告之间的变化？',
          '复查时最值得对比哪些指标？',
          '报告里哪些内容需要优先问医生？'
        ],
        MedicalCourse: [
          '请帮我总结这一页课程的重点。',
          '有哪些内容只适合科普，不能当医疗结论？',
          '如果我要复习，建议先看哪几个知识点？'
        ],
        ConsultationDetail: [
          '怎样补充病情说明更有效？',
          '我现在适合继续追问哪些问题？',
          '这条医生意见应该怎么理解？'
        ]
      }

      return routeTopics[route.name] || [
        '请帮我概括当前页面内容。',
        '这一页中最重要的医学信息是什么？',
        '有哪些地方需要我重点留意？'
      ]
    })

    const canSend = computed(() => !isSending.value && userInput.value.trim().length > 0)

    const scrollToBottom = async () => {
      await nextTick()
      if (conversationRef.value) {
        conversationRef.value.scrollTop = conversationRef.value.scrollHeight
      }
    }

    const scheduleScrollToBottom = () => {
      if (scrollFrameId) {
        return
      }
      scrollFrameId = window.requestAnimationFrame(() => {
        scrollFrameId = 0
        if (conversationRef.value) {
          conversationRef.value.scrollTop = conversationRef.value.scrollHeight
        }
      })
    }

    const resetConversation = () => {
      messages.value = []
      userInput.value = ''
    }

    const usePrompt = (prompt) => {
      userInput.value = prompt
      if (!isOpen.value) {
        isOpen.value = true
      }
    }

    const formatTime = (value) => {
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return ''
      }
      return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }

    const escapeHtml = (value) => String(value || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;')

    const renderMessageHtml = (value) => {
      let html = escapeHtml(value)
      html = html.replace(/(^|[\s(])((https?:\/\/|www\.)[^\s<]+)/g, (match, prefix, url) => {
        const href = url.startsWith('www.') ? `https://${url}` : url
        return `${prefix}<a href="${escapeHtml(href)}" target="_blank" rel="noopener noreferrer">${escapeHtml(url)}</a>`
      })
      return html.replace(/\n/g, '<br>')
    }

    const appendAssistantChunk = (message, chunk) => {
      const nextChunk = String(chunk || '')
      if (!nextChunk) {
        return
      }

      const currentContent = message.content || ''
      if (!currentContent) {
        message.content = nextChunk
        return
      }

      if (nextChunk === currentContent || nextChunk.startsWith(currentContent)) {
        message.content = nextChunk
        return
      }

      if (currentContent.endsWith(nextChunk)) {
        return
      }

      message.content = currentContent + nextChunk
    }

    const buildHistoryPayload = () => {
      return messages.value
        .filter((message) => !message.loading)
        .slice(-6)
        .map((message) => ({
          role: message.role,
          content: message.content
        }))
    }

    const createMessage = (role, content, extra = {}) => {
      assistantMessageSeed += 1
      return {
        id: `${role}-${assistantMessageSeed}`,
        role,
        content,
        createdAt: new Date().toISOString(),
        loading: false,
        ...extra
      }
    }

    const sendMessage = async () => {
      if (!canSend.value) {
        return
      }

      const content = userInput.value.trim()
      userInput.value = ''
      isOpen.value = true
      isSending.value = true

      const userMessage = createMessage('user', content)
      const assistantMessage = createMessage('assistant', '', { loading: true })
      messages.value.push(userMessage, assistantMessage)
      await scrollToBottom()

      const payload = {
        message: content,
        routeName: route.name || '',
        routePath: route.path || '',
        pageTitle: pageLabel.value,
        contextTitle: props.courseContext?.title || '',
        userId: window.localStorage.getItem('medical_user_id') || '',
        history: buildHistoryPayload()
      }

      try {
        let hasChunk = false
        await new Promise((resolve, reject) => {
          assistantAPI.runWorkflowStream(
            payload,
            async (chunk) => {
              if (!hasChunk) {
                assistantMessage.loading = false
                assistantMessage.content = ''
                hasChunk = true
              }
              appendAssistantChunk(assistantMessage, chunk)
              scheduleScrollToBottom()
            },
            resolve,
            reject
          )
        })

        if (!hasChunk) {
          assistantMessage.content = '当前工作流没有返回可读结果，请稍后再试。'
        }
        assistantMessage.loading = false
      } catch (error) {
        assistantMessage.content = error.response?.data?.message || error.message || '医疗助手当前不可用，请稍后重试。'
        assistantMessage.loading = false
      } finally {
        isSending.value = false
        await scrollToBottom()
      }
    }

    const handleComposerKeydown = (event) => {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        sendMessage()
      }
    }

    onMounted(async () => {
      if (props.globalPreferred) {
        window.__medicalAssistantPrimaryMounted = true
        isPrimaryInstance.value = true
      } else {
        isPrimaryInstance.value = !window.__medicalAssistantPrimaryMounted
        requestAnimationFrame(() => {
          if (window.__medicalAssistantPrimaryMounted) {
            isPrimaryInstance.value = false
          }
        })
      }

      await nextTick()
      syncScrollPosition()
      setDefaultTriggerPosition()

      window.addEventListener('resize', refreshViewportState)
      window.addEventListener('scroll', syncScrollPosition, { passive: true })
      window.addEventListener('pointermove', handlePointerMove)
      window.addEventListener('pointerup', handlePointerUp)
      window.addEventListener('pointercancel', handlePointerUp)

      if (typeof ResizeObserver !== 'undefined') {
        layoutObserver = new ResizeObserver(() => {
          refreshViewportState()
        })
        if (document.body) {
          layoutObserver.observe(document.body)
        }
        const appRoot = document.getElementById('app')
        if (appRoot) {
          layoutObserver.observe(appRoot)
        }
      }
    })

    onBeforeUnmount(() => {
      window.removeEventListener('resize', refreshViewportState)
      window.removeEventListener('scroll', syncScrollPosition)
      window.removeEventListener('pointermove', handlePointerMove)
      window.removeEventListener('pointerup', handlePointerUp)
      window.removeEventListener('pointercancel', handlePointerUp)
      layoutObserver?.disconnect()
      if (scrollFrameId) {
        window.cancelAnimationFrame(scrollFrameId)
        scrollFrameId = 0
      }
      document.body.style.userSelect = ''
      document.body.style.cursor = ''
      document.documentElement.style.cursor = ''
      if (props.globalPreferred) {
        delete window.__medicalAssistantPrimaryMounted
      }
    })

    watch(
      () => route.fullPath,
      async () => {
        isOpen.value = false
        userInput.value = ''
        messages.value = []
        await refreshViewportState()
      }
    )

    watch(isOpen, async (open) => {
      if (open) {
        await nextTick()
        setDefaultPanelPosition()
      }
    })

    return {
      botImage,
      canSend,
      conversationRef,
      dragState,
      formatTime,
      handleComposerKeydown,
      handlePanelPointerDown,
      handleTriggerClick,
      isMobile,
      isOpen,
      isSending,
      messages,
      pageLabel,
      panelShellRef,
      panelShellStyle,
      primaryCard,
      quickTopics,
      renderMessageHtml,
      resetConversation,
      sendMessage,
      shouldRender,
      startDrag,
      triggerShellRef,
      triggerShellStyle,
      usePrompt,
      userInput
    }
  }
}
</script>

<style scoped>
.medical-assistant {
  position: fixed;
  inset: 0;
  z-index: 1100;
  pointer-events: none;
}

.assistant-trigger-shell,
.assistant-panel-shell {
  position: absolute;
  left: 0;
  top: 0;
  pointer-events: auto;
  will-change: transform;
  transform: translate3d(0, 0, 0);
}

.assistant-trigger-shell {
  touch-action: none;
}

.assistant-panel-shell {
  touch-action: none;
  cursor: grab;
  overscroll-behavior: none;
}

.assistant-trigger {
  position: relative;
  width: 128px;
  height: 148px;
  border: none;
  background: transparent;
  cursor: grab;
  padding: 0;
  transform-style: preserve-3d;
  animation: stageDrift 5.2s ease-in-out infinite;
}

.assistant-trigger:active {
  cursor: grabbing;
}

.trigger-glow,
.trigger-orbit,
.trigger-particle,
.trigger-tag,
.trigger-bot-shell,
.trigger-bot {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.trigger-glow--outer {
  bottom: 18px;
  width: 112px;
  height: 112px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(110, 240, 255, 0.32), rgba(110, 240, 255, 0));
  filter: blur(8px);
  animation: assistantPulse 3s ease-in-out infinite;
}

.trigger-glow--inner {
  bottom: 28px;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  border: 1px solid rgba(148, 247, 255, 0.7);
  box-shadow: 0 0 22px rgba(118, 241, 255, 0.55);
}

.trigger-orbit {
  bottom: 30px;
  width: 94px;
  height: 86px;
  border-radius: 50%;
  border: 1px solid rgba(143, 244, 255, 0.4);
}

.trigger-orbit--one {
  animation: orbitSpin 7s linear infinite;
}

.trigger-orbit--two {
  width: 72px;
  height: 68px;
  opacity: 0.7;
  animation: orbitSpinReverse 5.8s linear infinite;
}

.trigger-particle {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.96), rgba(110, 240, 255, 0.12));
  box-shadow: 0 0 16px rgba(110, 240, 255, 0.72);
}

.trigger-particle--one {
  bottom: 104px;
  margin-left: -34px;
  animation: particleRise 4s ease-in-out infinite;
}

.trigger-particle--two {
  bottom: 84px;
  margin-left: 34px;
  width: 6px;
  height: 6px;
  animation: particleRise 4.8s ease-in-out infinite 1s;
}

.trigger-bot-shell {
  bottom: 8px;
  width: 112px;
  height: 126px;
  overflow: visible;
  transform-style: preserve-3d;
  animation: botHoverTrigger 3.8s ease-in-out infinite;
}

.trigger-bot {
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center center;
  filter: drop-shadow(0 0 20px rgba(110, 240, 255, 0.58));
  transform: translate(-50%, 0);
  left: 50%;
  position: absolute;
}

.trigger-tag {
  bottom: 0;
  min-height: 24px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(9, 30, 47, 0.9);
  border: 1px solid rgba(127, 240, 255, 0.38);
  color: #d9fbff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.assistant-panel {
  width: min(720px, calc(100vw - 32px));
  height: min(760px, calc(100vh - 32px));
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(91, 132, 255, 0.08), transparent 36%),
    radial-gradient(circle at top center, rgba(108, 216, 255, 0.12), transparent 40%),
    #ffffff;
  border: 1px solid rgba(148, 163, 184, 0.22);
  box-shadow: 0 28px 80px rgba(15, 23, 42, 0.18);
  display: grid;
  grid-template-rows: auto auto auto minmax(0, 1fr) auto;
  overflow: hidden;
  cursor: inherit;
}

.assistant-panel--mobile {
  width: calc(100vw - 20px);
  height: min(92vh, 760px);
}

.assistant-panel--dragging {
  box-shadow: 0 36px 90px rgba(15, 23, 42, 0.22);
}

.assistant-panel--dragging,
.assistant-panel--dragging * {
  cursor: grabbing !important;
}

.panel-toolbar {
  grid-row: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px 8px;
  cursor: grab;
  user-select: none;
}

.toolbar-label {
  font-size: 14px;
  font-weight: 800;
  color: #0f172a;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.toolbar-btn {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  background: rgba(255, 255, 255, 0.88);
  color: #334155;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.toolbar-btn svg,
.composer-send svg {
  width: 18px;
  height: 18px;
  stroke: currentColor;
  stroke-width: 2;
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.panel-hero {
  grid-row: 2;
  display: grid;
  grid-template-columns: 164px 1fr;
  gap: 26px;
  align-items: center;
  padding: 10px 22px 22px;
}

.hero-copy h2 {
  margin: 0 0 10px;
  font-size: clamp(28px, 3.6vw, 40px);
  line-height: 1.08;
  color: #0f172a;
}

.hero-copy p {
  margin: 0;
  color: #64748b;
  line-height: 1.7;
}

.hero-kicker {
  margin: 0 0 8px;
  color: #4f46e5;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-stage {
  position: relative;
  width: 164px;
  height: 156px;
  display: grid;
  place-items: center;
}

.hero-stage__halo,
.hero-stage__ring,
.hero-stage__shadow,
.hero-stage__platform,
.hero-stage__badge,
.hero-stage__badge-core,
.hero-stage__badge-core span {
  position: absolute;
}

.hero-stage__halo {
  left: 50%;
  top: 8px;
  width: 134px;
  height: 134px;
  margin-left: -67px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(108, 216, 255, 0.18), rgba(108, 216, 255, 0));
  filter: blur(10px);
}

.hero-stage__ring {
  left: 50%;
  top: 8px;
  width: 134px;
  height: 134px;
  margin-left: -67px;
  border-radius: 50%;
  border: 1px solid rgba(108, 216, 255, 0.52);
  box-shadow: 0 0 18px rgba(108, 216, 255, 0.3);
}

.hero-stage__shadow {
  left: 50%;
  bottom: 14px;
  width: 92px;
  height: 18px;
  margin-left: -46px;
  border-radius: 999px;
  background: rgba(8, 15, 36, 0.24);
  filter: blur(10px);
  animation: shadowPulse 3.6s ease-in-out infinite;
}

.hero-stage__platform {
  left: 50%;
  bottom: 12px;
  width: 126px;
  height: 22px;
  margin-left: -63px;
  border-radius: 999px;
  background:
    radial-gradient(circle at center, rgba(198, 249, 255, 0.98), rgba(96, 211, 232, 0.74) 48%, rgba(46, 98, 124, 0.96));
  box-shadow:
    0 10px 20px rgba(43, 102, 122, 0.25),
    inset 0 2px 8px rgba(255, 255, 255, 0.52);
}

.hero-stage__badge {
  left: 50%;
  top: 38px;
  width: 74px;
  height: 74px;
  margin-left: -37px;
  animation: badgeSpinClockwise 7.5s linear infinite;
  transform-origin: center center;
}

.hero-stage__badge-core {
  inset: 0;
  border-radius: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(233, 245, 255, 0.9));
  border: 1px solid rgba(137, 199, 255, 0.46);
  box-shadow:
    0 18px 28px rgba(80, 145, 255, 0.16),
    inset 0 2px 10px rgba(255, 255, 255, 0.8);
}

.hero-stage__badge-core span {
  left: 50%;
  top: 50%;
  width: 34px;
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(90deg, #3d63ff, #69d4ff);
  transform: translate(-50%, -50%);
  box-shadow: 0 0 18px rgba(96, 165, 250, 0.34);
}

.hero-stage__badge-core span:last-child {
  transform: translate(-50%, -50%) rotate(90deg);
}

.welcome-grid {
  grid-row: 3;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 18px;
  padding: 0 22px 18px;
}

.welcome-card {
  border-radius: 22px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.86), rgba(241, 245, 249, 0.96));
  padding: 22px;
}

.welcome-card--primary {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(239, 246, 255, 0.92));
}

.welcome-card__eyebrow {
  color: #4f46e5;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.welcome-card h3 {
  margin: 14px 0 10px;
  color: #0f172a;
  font-size: 24px;
}

.welcome-card p {
  margin: 0 0 16px;
  color: #64748b;
  line-height: 1.65;
}

.card-action,
.topic-btn,
.composer-chip {
  border-radius: 999px;
  border: 1px solid rgba(99, 102, 241, 0.16);
  background: #ffffff;
  color: #334155;
  cursor: pointer;
}

.card-action {
  min-height: 42px;
  padding: 0 18px;
  font-weight: 700;
}

.topic-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 10px;
}

.topic-btn {
  width: 100%;
  min-height: 44px;
  padding: 0 14px;
  text-align: left;
  font-size: 14px;
}

.conversation {
  grid-row: 4;
  overflow-y: auto;
  padding: 0 22px 12px;
  display: grid;
  gap: 14px;
  min-height: 0;
}

.message-row {
  display: flex;
  gap: 12px;
}

.message-row--user {
  justify-content: flex-end;
}

.assistant-avatar {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(180deg, #eff6ff, #dbeafe);
  border: 1px solid rgba(125, 211, 252, 0.22);
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.assistant-avatar img {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.message-bubble {
  max-width: min(78%, 480px);
  border-radius: 24px;
  padding: 14px 16px;
  display: grid;
  gap: 8px;
}

.message-text {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.7;
  word-break: break-word;
}

.message-text :deep(a) {
  text-decoration: underline;
  text-underline-offset: 3px;
  word-break: break-all;
}

.message-bubble--assistant .message-text :deep(a) {
  color: #2563eb;
}

.message-bubble--user .message-text :deep(a) {
  color: #ffffff;
}

.message-bubble small {
  color: #94a3b8;
}

.message-bubble--assistant {
  background: #ffffff;
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 12px 22px rgba(15, 23, 42, 0.05);
}

.message-bubble--user {
  background: linear-gradient(135deg, #315efb, #7c8cff);
  color: #ffffff;
}

.message-bubble--user small {
  color: rgba(255, 255, 255, 0.78);
}

.message-bubble--loading {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 52px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #60a5fa;
  animation: typingBlink 1.2s ease-in-out infinite;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.16s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.32s;
}

.composer {
  grid-row: 5;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
  padding: 12px 18px 16px;
  display: grid;
  grid-template-columns: 1fr 48px;
  gap: 10px;
  align-items: stretch;
  height: 120px;
}

.composer-shell {
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 20px;
  background: #ffffff;
  padding: 10px 14px 8px;
  height: 92px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.composer-input {
  width: 100%;
  border: none;
  resize: none;
  outline: none;
  font: inherit;
  height: 46px;
  min-height: 46px;
  max-height: 46px;
  color: #0f172a;
  line-height: 1.55;
  overflow-y: auto;
}

.composer-input::placeholder {
  color: #94a3b8;
}

.composer-meta {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #94a3b8;
  font-size: 12px;
}

.composer-chip {
  min-height: 30px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 700;
}

.composer-send {
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #315efb, #7c8cff);
  color: #ffffff;
  cursor: pointer;
  display: grid;
  place-items: center;
  height: 92px;
  box-shadow: 0 10px 20px rgba(79, 70, 229, 0.18);
}

.composer-send:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  box-shadow: none;
}

.composer-spinner {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  animation: slowSpin 0.9s linear infinite;
}

@keyframes assistantPulse {
  0%,
  100% {
    transform: translateX(-50%) scale(0.92);
    opacity: 0.72;
  }
  50% {
    transform: translateX(-50%) scale(1.08);
    opacity: 1;
  }
}

@keyframes botHoverTrigger {
  0%,
  100% {
    transform: translateX(-50%) translateY(0) rotateX(10deg) rotateY(-12deg);
  }
  50% {
    transform: translateX(-50%) translateY(-10px) rotateX(3deg) rotateY(10deg) rotateZ(-1deg);
  }
}

@keyframes badgeSpinClockwise {
  from {
    transform: translateX(0) rotate(0deg);
  }
  to {
    transform: translateX(0) rotate(360deg);
  }
}

@keyframes slowSpin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes orbitSpin {
  from {
    transform: translateX(-50%) rotateX(72deg) rotateZ(0deg);
  }
  to {
    transform: translateX(-50%) rotateX(72deg) rotateZ(360deg);
  }
}

@keyframes orbitSpinReverse {
  from {
    transform: translateX(-50%) rotateX(72deg) rotateZ(360deg);
  }
  to {
    transform: translateX(-50%) rotateX(72deg) rotateZ(0deg);
  }
}

@keyframes particleRise {
  0%,
  100% {
    transform: translateX(-50%) translateY(0) scale(0.72);
    opacity: 0.18;
  }
  40% {
    opacity: 0.96;
  }
  50% {
    transform: translateX(-50%) translateY(-12px) scale(1);
    opacity: 0.72;
  }
}

@keyframes shadowPulse {
  0%,
  100% {
    transform: translateX(-50%) scaleX(0.88);
    opacity: 0.42;
  }
  50% {
    transform: translateX(-50%) scaleX(1.06);
    opacity: 0.2;
  }
}

@keyframes stageDrift {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

@keyframes typingBlink {
  0%,
  80%,
  100% {
    transform: scale(0.6);
    opacity: 0.45;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@media (max-width: 900px) {
  .panel-hero,
  .welcome-grid {
    grid-template-columns: 1fr;
  }

  .hero-stage {
    justify-self: center;
  }
}

@media (max-width: 768px) {
  .assistant-trigger {
    width: 110px;
    height: 136px;
  }

  .trigger-bot-shell {
    width: 92px;
    height: 112px;
  }

  .assistant-panel {
    width: calc(100vw - 20px);
    height: min(92vh, 760px);
  }

  .composer {
    grid-template-columns: 1fr 46px;
    height: 112px;
    padding: 12px;
  }

  .message-bubble {
    max-width: 100%;
  }
}

@media (max-height: 860px) {
  .assistant-panel {
    height: min(760px, calc(100vh - 16px));
    grid-template-rows: auto auto auto minmax(0, 1fr) auto;
  }

  .panel-toolbar {
    padding: 14px 18px 6px;
  }

  .panel-hero {
    gap: 18px;
    padding: 6px 18px 14px;
    grid-template-columns: 132px 1fr;
  }

  .hero-stage {
    width: 132px;
    height: 124px;
  }

  .hero-copy h2 {
    font-size: clamp(24px, 3vw, 32px);
    margin-bottom: 8px;
  }

  .hero-copy p {
    line-height: 1.5;
  }

  .welcome-grid {
    gap: 14px;
    padding: 0 18px 14px;
  }

  .welcome-card {
    padding: 18px;
  }

  .welcome-card h3 {
    margin: 10px 0 8px;
    font-size: 20px;
  }

  .welcome-card p {
    margin-bottom: 12px;
    line-height: 1.55;
  }

  .conversation {
    padding: 0 18px 10px;
  }

  .composer {
    padding: 10px 14px 14px;
    height: 106px;
  }

  .composer-shell,
  .composer-send {
    height: 82px;
  }

  .composer-input {
    height: 40px;
    min-height: 40px;
    max-height: 40px;
  }
}
</style>
