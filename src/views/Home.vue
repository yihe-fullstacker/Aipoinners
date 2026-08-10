<template>
  <div class="home">
    <div class="scene-backdrop" aria-hidden="true">
      <span class="backdrop-orb orb-left"></span>
      <span class="backdrop-orb orb-right"></span>
      <span class="backdrop-grid"></span>
      <span class="backdrop-glow"></span>
    </div>

    <div class="home-shell">
      <section class="hero-panel">
        <div class="hero-top">
          <div class="brand-lockup">
            <div class="brand-mark">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              </svg>
            </div>
            <div>
              <p>Medical AI Diagnostic</p>
              <strong>影智医疗诊断平台</strong>
            </div>
          </div>

          <div class="hero-status">
            <span class="status-dot"></span>
            <span>医疗级智能诊断链路已启用</span>
          </div>
        </div>

        <div class="hero-main">
          <div class="hero-copy">
            <span class="hero-eyebrow">面向患者与医生的智能医疗入口</span>
            <h1>三维重构 + AI 分析，影像诊断的下一代引擎</h1>
            <p>
              延续医疗科技质感与企业级严谨调性，将首页主视觉聚焦于三维影像能力展示区，让医生与患者进入系统时，优先感知平台核心诊断价值，再平滑衔接登录、注册与临床工作流。

            </p>

            <div class="metric-strip">
              <article v-for="item in heroMetrics" :key="item.label" class="metric-card">
                <strong>{{ item.value }}</strong>
                <span>{{ item.label }}</span>
              </article>
            </div>
          </div>

          <div class="hero-stage">
            <div class="medical-visual">
              <div class="visual-halo halo-one"></div>
              <div class="visual-halo halo-two"></div>
              <div class="visual-ring ring-one"></div>
              <div class="visual-ring ring-two"></div>

              <div class="stage-caption">
                <span class="stage-mini-tag">3D Medical Scene</span>
                <strong>医疗 AI 三维主视觉</strong>
                <p>强化模型展示与空间层次，去掉散乱的补充卡片与说明块。</p>
              </div>

              <div class="image-orbit">
                <MedicalHeartScene v-if="showHeartScene" :model-url="heartModelUrl" />
                <div v-else class="heart-scene-shell" aria-hidden="true">
                  <span class="heart-scene-shell__core"></span>
                  <span class="heart-scene-shell__ring heart-scene-shell__ring--one"></span>
                  <span class="heart-scene-shell__ring heart-scene-shell__ring--two"></span>
                </div>
              </div>

              <div class="stage-chip chip-top-left">CT / MRI / DR</div>
              <div class="stage-chip chip-top-right">Clinical AI</div>
              <div class="stage-chip chip-bottom-left">Report Insight</div>
              <div class="stage-chip chip-bottom-right">Doctor Review</div>
            </div>
          </div>
        </div>
      </section>

      <aside class="auth-dock">
        <div class="dock-top">
          <div class="dock-heading">
            <p class="dock-kicker">{{ showRegister ? '创建平台账户' : '欢迎进入诊断工作台' }}</p>
            <h2>{{ showRegister ? '快速注册并开始体验' : '登录后继续上传与诊断' }}</h2>
            <span class="dock-subtitle">冷色医疗 AI 主题、毛玻璃质感与清晰的信息层级已统一到这一入口页。</span>
          </div>

          <div class="auth-switch">
            <button :class="['auth-switch-btn', { active: !showRegister }]" @click="switchAuth('login')">
              登录
            </button>
            <button :class="['auth-switch-btn', { active: showRegister }]" @click="switchAuth('register')">
              注册
            </button>
          </div>
        </div>

        <div class="dock-body">
          <transition name="dock-switch" mode="out-in">
            <LightLoginCard
              v-if="!showRegister"
              key="login"
              @switch-to-register="switchAuth('register')"
            />
            <LightRegisterCard
              v-else
              key="register"
              @switch-to-login="switchAuth('login')"
            />
          </transition>
        </div>
      </aside>
    </div>
  </div>
</template>

<script>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LightLoginCard from '../components/LightLoginCard.vue'
import LightRegisterCard from '../components/LightRegisterCard.vue'
import MedicalHeartScene from '../components/MedicalHeartScene.vue'
import { medicalAPI } from '../api/api'

export default {
  name: 'Home',
  components: { LightLoginCard, LightRegisterCard, MedicalHeartScene },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const showRegister = ref(false)
    const showHeartScene = ref(false)
    const heartModelUrl = `${process.env.BASE_URL}models/15.glb`
    let heartSceneTimer = null
    let heartSceneIdleId = null

    const heroMetrics = ref([
      { value: '24/7', label: '医疗服务在线响应' },
      { value: '多角色', label: '患者与医生协同接入' },
      { value: '秒级', label: '首页入口反馈体验' }
    ])

    const updateShowRegister = () => {
      showRegister.value = route.path === '/register' || route.query.action === 'register'
    }

    const switchAuth = async (mode) => {
      const targetPath = mode === 'register' ? '/register' : '/login'
      showRegister.value = mode === 'register'
      if (route.path !== targetPath) {
        await router.push(targetPath)
      }
    }

    const clearHeartSceneSchedule = () => {
      if (heartSceneTimer) {
        clearTimeout(heartSceneTimer)
        heartSceneTimer = null
      }
      if (heartSceneIdleId !== null) {
        if (typeof window.cancelIdleCallback === 'function') {
          window.cancelIdleCallback(heartSceneIdleId)
        } else {
          clearTimeout(heartSceneIdleId)
        }
      }
      heartSceneIdleId = null
    }

    const scheduleHeartScene = () => {
      if (showHeartScene.value || window.innerWidth < 900) {
        return
      }

      clearHeartSceneSchedule()

      const delay = route.path === '/login' || route.path === '/register' ? 1200 : 320
      heartSceneTimer = window.setTimeout(() => {
        const mountScene = () => {
          showHeartScene.value = true
          heartSceneIdleId = null
        }

        if (typeof window.requestIdleCallback === 'function') {
          heartSceneIdleId = window.requestIdleCallback(() => {
            mountScene()
          }, { timeout: 1800 })
        } else {
          heartSceneIdleId = window.setTimeout(mountScene, 120)
        }
      }, delay)
    }

    const loadStats = async () => {
      const token = window.localStorage.getItem('medical_auth_token')
      if (!token) {
        return
      }

      try {
        const response = await medicalAPI.getReports()
        const reports = Array.isArray(response.data) ? response.data : []
        if (!reports.length) {
          return
        }

        const confidenceValues = reports
          .map((item) => (typeof item.confidence === 'number' ? Math.round(item.confidence * 100) : null))
          .filter((value) => value !== null)

        const averageConfidence = confidenceValues.length
          ? Math.round(confidenceValues.reduce((sum, value) => sum + value, 0) / confidenceValues.length)
          : 96

        heroMetrics.value = [
          { value: `${reports.length}+`, label: '累计影像分析记录' },
          { value: `${averageConfidence}%`, label: '平均识别置信度' },
          { value: 'MDT', label: '支持多学科会诊流程' }
        ]
      } catch (error) {
        if (error.response?.status !== 401) {
          console.warn('加载首页展示数据失败，已使用默认内容。', error)
        }
      }
    }

    onMounted(async () => {
      updateShowRegister()
      scheduleHeartScene()
      await loadStats()
    })

    onBeforeUnmount(() => {
      clearHeartSceneSchedule()
    })

    watch(() => route.path, updateShowRegister)
    watch(() => route.path, scheduleHeartScene)
    watch(() => route.query.action, updateShowRegister)

    return {
      showRegister,
      showHeartScene,
      switchAuth,
      heroMetrics,
      heartModelUrl
    }
  }
}
</script>

<style scoped>
.home {
  --line: var(--ui-border-accent);
  --soft-line: var(--ui-border-soft);
  --text: var(--ui-text-primary);
  --muted: var(--ui-text-muted);
  --accent: var(--ui-brand-primary);
  --accent-alt: var(--ui-brand-accent);
  --surface-card: var(--ui-surface-card);
  --surface-chip: var(--ui-surface-chip);
  --shadow-panel: var(--ui-shadow-panel);
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  position: relative;
  overflow: hidden;
  padding: 8px var(--app-shell-gutter, 32px) 18px;
  font-family: var(--ui-font-family);
  background: var(--ui-bg-page-gradient);
}

.scene-backdrop {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.backdrop-orb,
.backdrop-glow {
  position: absolute;
  border-radius: 999px;
}

.orb-left {
  width: 420px;
  height: 420px;
  top: -120px;
  left: -140px;
  background: radial-gradient(circle, rgba(84, 165, 255, 0.3), transparent 70%);
  filter: blur(52px);
  animation: drift 15s ease-in-out infinite;
}

.orb-right {
  width: 520px;
  height: 520px;
  right: -180px;
  top: 12%;
  background: radial-gradient(circle, rgba(69, 235, 201, 0.16), transparent 72%);
  filter: blur(68px);
  animation: drift 18s ease-in-out infinite -5s;
}

.backdrop-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--ui-grid-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color) 1px, transparent 1px);
  background-size: 56px 56px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.72) 0%, transparent 82%);
}

.backdrop-glow {
  width: 720px;
  height: 720px;
  left: 16%;
  top: 2%;
  background: radial-gradient(circle, rgba(69, 116, 255, 0.12), transparent 68%);
  filter: blur(58px);
  animation: breathe 12s ease-in-out infinite;
}

.home-shell {
  position: relative;
  z-index: 1;
  max-width: 1460px;
  min-height: calc(100vh - var(--app-top-nav-height, 68px) - 26px);
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: 24px;
}

.hero-panel,
.auth-dock {
  position: relative;
  overflow: hidden;
  border: 1px solid var(--soft-line);
  box-shadow: var(--shadow-panel), inset 0 1px 0 rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(22px);
}

.hero-panel {
  display: flex;
  flex-direction: column;
  border-radius: var(--ui-radius-xl);
  padding: 30px 34px;
  background:
    radial-gradient(ellipse 48% 28% at 78% 4%, rgba(var(--ui-brand-primary-rgb), 0.12) 0%, transparent 48%),
    radial-gradient(ellipse 32% 34% at 8% 100%, rgba(var(--ui-brand-accent-rgb), 0.08) 0%, transparent 56%),
    linear-gradient(180deg, var(--ui-surface-panel), var(--ui-surface-panel-strong));
}

.hero-panel::after,
.auth-dock::after {
  content: '';
  position: absolute;
  inset: 14px;
  border-radius: var(--ui-radius-lg);
  border: 1px solid rgba(255, 255, 255, 0.08);
  pointer-events: none;
}

.hero-top,
.dock-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
}

.dock-top {
  flex-direction: column;
  align-items: stretch;
}

.brand-lockup {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--text);
}

.brand-mark {
  width: 46px;
  height: 46px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 15px;
  background: linear-gradient(145deg, rgba(75, 211, 255, 0.22), rgba(68, 241, 196, 0.14));
  border: 1px solid rgba(112, 214, 255, 0.28);
  box-shadow: 0 0 24px rgba(84, 165, 255, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.12);
  color: var(--accent);
}

.brand-lockup p {
  margin: 0 0 4px;
  font-size: 11px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--ui-text-subtle);
}

.brand-lockup strong {
  font-size: 16px;
  letter-spacing: 0.05em;
}

.hero-status {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  min-height: 40px;
  padding: 0 14px;
  border-radius: 999px;
  background: var(--surface-chip);
  border: 1px solid var(--soft-line);
  color: var(--ui-text-secondary);
  font-size: 12px;
  font-weight: 600;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--accent-alt);
  box-shadow: 0 0 0 5px rgba(68, 241, 196, 0.12), 0 0 10px rgba(68, 241, 196, 0.5);
  animation: blink 2s ease-in-out infinite;
}

.hero-main {
  display: grid;
  grid-template-rows: auto auto;
  gap: 24px;
  flex: 1;
  min-height: 0;
  margin-top: 24px;
}

.hero-copy {
  max-width: 680px;
  display: grid;
  gap: 16px;
  align-content: start;
}

.hero-eyebrow,
.stage-mini-tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  width: fit-content;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(84, 165, 255, 0.1);
  border: 1px solid rgba(112, 214, 255, 0.18);
  color: rgba(210, 228, 255, 0.88);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-copy h1 {
  margin: 0;
  color: var(--text);
  font-size: clamp(34px, 3.2vw, 54px);
  line-height: 1.04;
  letter-spacing: -0.05em;
  font-weight: 800;
  max-width: 700px;
  text-wrap: balance;
}

.hero-copy p {
  margin: 0;
  color: var(--muted);
  font-size: 15px;
  line-height: 1.85;
  max-width: 620px;
}

.metric-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 8px;
  max-width: 640px;
}

.metric-card {
  padding: 16px 14px;
  border-radius: var(--ui-radius-md);
  background: var(--surface-card);
  border: 1px solid var(--soft-line);
}

.metric-card strong {
  display: block;
  color: var(--text);
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.metric-card span {
  display: block;
  margin-top: 5px;
  color: var(--muted);
  font-size: 11px;
  line-height: 1.55;
}

.hero-stage {
  max-width: 900px;
  min-height: 0;
}

.medical-visual {
  position: relative;
  min-height: 460px;
  border-radius: calc(var(--ui-radius-lg) + 4px);
  background:
    radial-gradient(circle at center, rgba(var(--ui-brand-primary-rgb), 0.16), transparent 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.03));
  border: 1px solid var(--line);
  overflow: hidden;
  perspective: 1600px;
}

.medical-visual::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.82), transparent 100%);
}

.stage-caption {
  position: absolute;
  top: 18px;
  left: 18px;
  z-index: 4;
  max-width: 260px;
  padding: 14px 16px;
  border-radius: 22px;
  background: rgba(7, 21, 43, 0.48);
  border: 1px solid rgba(126, 220, 255, 0.16);
  box-shadow: 0 16px 36px rgba(2, 15, 36, 0.22);
  backdrop-filter: blur(16px);
}

.stage-caption strong {
  display: block;
  margin-top: 10px;
  color: rgba(245, 250, 255, 0.96);
  font-size: 18px;
  line-height: 1.3;
  letter-spacing: -0.02em;
}

.stage-caption p {
  margin: 8px 0 0;
  color: rgba(220, 232, 248, 0.76);
  font-size: 12px;
  line-height: 1.7;
}

.visual-halo,
.visual-ring {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border-radius: 999px;
  pointer-events: none;
}

.halo-one {
  width: 420px;
  height: 420px;
  background: radial-gradient(circle, rgba(88, 152, 255, 0.24), transparent 70%);
  filter: blur(30px);
  animation: haloPulse 6.4s ease-in-out infinite;
}

.halo-two {
  width: 280px;
  height: 280px;
  background: radial-gradient(circle, rgba(68, 241, 196, 0.2), transparent 72%);
  filter: blur(18px);
  animation: haloPulse 5.6s ease-in-out infinite -2.2s;
}

.ring-one,
.ring-two {
  border: 1px solid rgba(126, 220, 255, 0.18);
  box-shadow: inset 0 0 20px rgba(126, 220, 255, 0.06);
}

.ring-one {
  width: 460px;
  height: 180px;
  animation: ringFloat 8s ease-in-out infinite;
}

.ring-two {
  width: 320px;
  height: 120px;
  border-color: rgba(68, 241, 196, 0.18);
  animation: ringFloat 8s ease-in-out infinite -3.2s;
}

.image-orbit {
  position: absolute;
  left: 50%;
  top: 54%;
  width: min(700px, 94%);
  height: min(540px, 96%);
  transform: translate(-50%, -50%);
}

.heart-scene-shell {
  position: relative;
  width: 100%;
  height: 100%;
}

.heart-scene-shell__core,
.heart-scene-shell__ring {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border-radius: 999px;
}

.heart-scene-shell__core {
  width: 210px;
  height: 210px;
  background: radial-gradient(circle, rgba(136, 221, 255, 0.78), rgba(33, 118, 182, 0.22) 56%, transparent 76%);
  filter: blur(10px);
}

.heart-scene-shell__ring {
  border: 1px solid rgba(125, 225, 255, 0.26);
  box-shadow: 0 0 24px rgba(88, 152, 255, 0.12);
}

.heart-scene-shell__ring--one {
  width: 320px;
  height: 124px;
  transform: translate(-50%, -50%) rotateX(72deg);
}

.heart-scene-shell__ring--two {
  width: 240px;
  height: 96px;
  border-color: rgba(72, 228, 200, 0.28);
  transform: translate(-50%, -50%) rotateX(72deg) scale(1.08);
}

.stage-chip {
  position: absolute;
  z-index: 4;
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(5, 18, 40, 0.72);
  border: 1px solid rgba(126, 220, 255, 0.16);
  color: rgba(224, 235, 255, 0.88);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.05em;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(10px);
}

.chip-top-left {
  left: 18px;
  top: 132px;
}

.chip-top-right {
  right: 18px;
  top: 28px;
}

.chip-bottom-left {
  left: 28px;
  bottom: 28px;
}

.chip-bottom-right {
  right: 26px;
  bottom: 36px;
}

.auth-dock {
  display: flex;
  flex-direction: column;
  min-height: 0;
  border-radius: var(--ui-radius-xl);
  padding: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92) 0%, rgba(248, 251, 255, 0.84) 100%);
  border-color: rgba(170, 186, 211, 0.26);
  box-shadow:
    0 28px 64px rgba(0, 28, 59, 0.12),
    0 10px 28px rgba(0, 28, 59, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.65);
}

.dock-heading {
  display: grid;
  gap: 8px;
}

.dock-kicker {
  margin: 0;
  color: var(--ui-text-secondary);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.dock-top h2 {
  margin: 0;
  color: var(--text);
  font-size: 28px;
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -0.03em;
}

.dock-subtitle {
  color: #718198;
  font-size: 12px;
  line-height: 1.7;
}

.auth-switch {
  display: inline-flex;
  width: 100%;
  padding: 5px;
  border-radius: 999px;
  background: rgba(222, 227, 236, 0.96);
  border: 1px solid rgba(194, 204, 219, 0.65);
}

.auth-switch-btn {
  flex: 1;
  min-width: 0;
  height: 40px;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: var(--muted);
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.auth-switch-btn.active {
  background: rgba(255, 255, 255, 0.98);
  color: #062b60;
  box-shadow: 0 6px 14px rgba(0, 28, 59, 0.08);
}

.dock-body {
  flex: 1;
  min-height: 0;
  margin-top: 18px;
  overflow: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(6, 43, 96, 0.28) transparent;
}

.dock-body :deep(.auth-embed),
.dock-body :deep(.auth-shell--embedded),
.dock-body :deep(.auth-panel--embedded) {
  height: 100%;
}

.dock-body :deep(.panel-top) {
  display: none;
}

.dock-body :deep(.mode-header) {
  margin-bottom: 16px;
}

.dock-body :deep(.mode-header h3) {
  font-size: 18px;
  color: #0b2448;
}

.dock-body :deep(.mode-header p),
.dock-body :deep(.footer-tip) {
  font-size: 12px;
  line-height: 1.65;
  color: #738198;
}

.dock-body :deep(.login-tabs) {
  margin-bottom: 14px;
}

.dock-body :deep(.register-form),
.dock-body :deep(.auth-form) {
  gap: 14px;
}

.dock-body :deep(.message) {
  margin-bottom: 10px;
}

.dock-body :deep(.panel-footer) {
  padding-top: 16px;
  border-top-color: rgba(188, 198, 214, 0.42);
}

.dock-switch-enter-active,
.dock-switch-leave-active {
  transition: all 0.22s ease;
}

.dock-switch-enter-from,
.dock-switch-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@keyframes drift {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(18px, -14px, 0) scale(1.07);
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: scale(0.95);
    opacity: 0.68;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}

@keyframes blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.32;
  }
}

@keyframes haloPulse {
  0%,
  100% {
    transform: translate(-50%, -50%) scale(0.95);
    opacity: 0.72;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.06);
    opacity: 1;
  }
}

@keyframes ringFloat {
  0%,
  100% {
    transform: translate(-50%, -50%) rotateX(72deg) scale(0.98);
    opacity: 0.72;
  }
  50% {
    transform: translate(-50%, -50%) rotateX(72deg) scale(1.03);
    opacity: 1;
  }
}

@media (max-width: 1180px) {
  .home {
    overflow-y: auto;
  }

  .home-shell {
    min-height: auto;
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .hero-top {
    flex-direction: column;
    align-items: flex-start;
  }

  .metric-strip {
    grid-template-columns: 1fr;
    max-width: none;
  }

  .medical-visual {
    min-height: 380px;
  }

  .stage-caption {
    position: static;
    max-width: none;
    margin: 14px;
  }

  .image-orbit {
    top: 56%;
    width: min(520px, 92%);
    height: min(400px, 84%);
  }
}

@media (max-width: 720px) {
  .home {
    padding: 10px;
  }

  .hero-panel,
  .auth-dock {
    padding: 18px;
    border-radius: var(--ui-radius-lg);
  }

  .hero-copy h1 {
    font-size: 30px;
  }

  .hero-copy p {
    font-size: 14px;
    line-height: 1.8;
  }

  .medical-visual {
    min-height: 320px;
  }

  .image-orbit {
    width: min(360px, 90%);
    height: min(280px, 76%);
  }

  .stage-chip {
    font-size: 10px;
    min-height: 28px;
    padding: 0 10px;
  }

  .chip-top-left {
    top: 112px;
  }
}
</style>
