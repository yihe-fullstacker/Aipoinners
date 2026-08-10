<template>
  <div id="app" :class="{ 'app-theme-grid': shouldShowThemeGrid }">
    <TopNav />
    <FloatingAIAssistant
      v-if="shouldShowAssistant"
      :visible="shouldShowAssistant"
      :globalPreferred="true"
    />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import TopNav from '@/components/TopNav.vue'
import FloatingAIAssistant from '@/components/FloatingAIAssistant.vue'
import navigationHistory from '@/utils/navigationHistory'
import { mapState } from 'vuex'

const ASSISTANT_HIDDEN_ROUTE_NAMES = new Set([
  'Root',
  'Landing',
  'About',
  'Instruction',
  'Login',
  'Register',
  'Home'
])

export default {
  name: 'App',
  components: {
    TopNav,
    FloatingAIAssistant
  },
  computed: {
    ...mapState(['isLoggedIn', 'theme']),
    shouldShowAssistant() {
      return !ASSISTANT_HIDDEN_ROUTE_NAMES.has(this.$route?.name)
    },
    shouldShowThemeGrid() {
      return this.isLoggedIn && this.theme === 'medical-ai'
    }
  },
  watch: {
    isLoggedIn(newVal, oldVal) {
      if (newVal && !oldVal) {
        navigationHistory.reset()
        navigationHistory.startTracking(this.$router)
      } else if (!newVal && oldVal) {
        navigationHistory.stopTracking()
      }
    },
    theme: {
      immediate: true,
      handler(theme) {
        this.applyTheme(theme)
      }
    }
  },
  methods: {
    applyTheme(theme) {
      const nextTheme = theme || 'dark'
      document.documentElement.setAttribute('data-theme', nextTheme)
      document.body.setAttribute('data-theme', nextTheme)
    }
  },
  mounted() {
    this.applyTheme(this.theme)
    if (this.isLoggedIn) {
      navigationHistory.startTracking(this.$router)
    }
  },
  beforeUnmount() {
    navigationHistory.stopTracking()
  }
}
</script>

<style>
:root {
  --app-top-nav-height: 68px;
  --app-shell-max-width: 1280px;
  --app-shell-gutter: clamp(16px, 3vw, 32px);
}

html,
body {
  min-height: 100%;
  background: var(--ui-bg-page-gradient);
}

body {
  overflow-x: hidden;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  min-height: 100vh;
  position: relative;
  isolation: isolate;
  display: flex;
  flex-direction: column;
  background: var(--ui-bg-page-gradient);
  color: var(--ui-text-primary);
  transition: background 0.35s ease, color 0.35s ease;
  overflow-x: hidden;
}

#app > * {
  position: relative;
}

#app > .top-nav {
  z-index: 1200;
}

#app > .medical-assistant {
  z-index: 1300;
}

#app > .main-content {
  z-index: 1;
}

.main-content {
  margin-top: 0;
  flex: 1 1 auto;
  min-height: calc(100vh - var(--app-top-nav-height));
  background: transparent;
  transition: background 0.35s ease;
  position: relative;
}

.main-content > :is([class$='-page'], .home, .patient-home, .auth-page, .smart-qa-workstation) {
  margin-top: 0 !important;
  padding-top: 0 !important;
}

.main-content > :is([class$='-page'], .home, .patient-home, .auth-page, .smart-qa-workstation) > :is(.page-shell, .home-shell, .auth-shell) {
  margin-top: 0 !important;
  padding-top: 0 !important;
}

body[data-theme='dark'] .main-content > :is([class$='-page'], .home, .patient-home, .auth-page, .smart-qa-workstation) {
  min-height: calc(100vh - var(--app-top-nav-height)) !important;
}

body[data-theme='dark'] .main-content > :is(
  .dashboard-page,
  .reports-page,
  .consultation-page,
  .history-page,
  .upload-page,
  .analysis-report-page,
  .deep-analysis-page,
  .patient-home
) > .page-shell,
body[data-theme='dark'] .main-content > .home > .home-shell,
body[data-theme='dark'] .main-content > .auth-page > .auth-shell {
  margin-top: 0 !important;
}

body[data-theme='dark'] .main-content > :is(
  .dashboard-page,
  .reports-page,
  .consultation-page,
  .history-page,
  .upload-page,
  .analysis-report-page,
  .deep-analysis-page,
  .landing-page,
  .instruction-page,
  .home,
  .patient-home,
  .auth-page,
  .smart-qa-workstation
) {
  margin-top: 0 !important;
}

body[data-theme='dark'] .main-content > .smart-qa-workstation,
body[data-theme='medical-ai'] .main-content > .smart-qa-workstation {
  padding-top: 0 !important;
  margin-top: 0 !important;
}

body[data-theme='dark'] .main-content > .smart-qa-workstation > .workspace-header,
body[data-theme='medical-ai'] .main-content > .smart-qa-workstation > .workspace-header {
  margin-top: 0 !important;
}

#app.app-theme-grid::before {
  content: '';
  position: fixed;
  top: var(--app-top-nav-height);
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 0;
  pointer-events: none;
  background-image:
    linear-gradient(var(--ui-grid-color-strong) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color-strong) 1px, transparent 1px);
  background-size: 56px 56px;
  background-position: center top;
  opacity: 0.72;
}

#app.app-theme-grid::after {
  content: '';
  position: fixed;
  top: var(--app-top-nav-height);
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 18% 14%, rgba(var(--ui-brand-primary-rgb), 0.07), transparent 34%),
    radial-gradient(circle at 78% 24%, rgba(var(--ui-brand-accent-rgb), 0.05), transparent 30%),
    radial-gradient(circle at 50% 100%, rgba(var(--ui-brand-primary-rgb), 0.03), transparent 40%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.16) 0%, rgba(255, 255, 255, 0) 34%);
  opacity: 0.9;
}
/* =========================================
   视频模态框全局优化（不改变任何界面内容）
   ========================================= */
.video-modal {
  z-index: 99999 !important;
  isolation: isolate !important;
  position: fixed !important;
  inset: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  background: rgba(0, 0, 0, 0.85) !important;
}

.video-modal-content {
  z-index: 99999 !important;
  position: relative !important;
  width: 90% !important;
  max-width: 900px !important;
  background: #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.video-close-btn {
  z-index: 100000 !important;
  position: absolute !important;
  top: 12px !important;
  right: 12px !important;
  width: 36px !important;
  height: 36px !important;
  border-radius: 50% !important;
  background: rgba(255,255,255,0.2) !important;
  border: none !important;
  color: #fff !important;
  font-size: 18px !important;
  cursor: pointer !important;
}

.video-iframe-container {
  z-index: 99999 !important;
  width: 100% !important;
  aspect-ratio: 16/9 !important;
}

.video-iframe {
  z-index: 100000 !important;
  width: 100% !important;
  height: 100% !important;
  border: none !important;
  display: block !important;
}

/* 科技医疗主题特殊保护 */
body[data-theme='medical-ai'] .video-modal,
body[data-theme='medical-ai'] .video-modal-content,
body[data-theme='medical-ai'] .video-iframe-container,
body[data-theme='medical-ai'] .video-iframe,
body[data-theme='medical-ai'] .video-close-btn {
  z-index: 99999 !important;
  isolation: isolate !important;
  position: relative !important;
}

body[data-theme='medical-ai'] .video-modal {
  position: fixed !important;
  inset: 0 !important;
}

/* 深色主题特殊保护 */
body[data-theme='dark'] .video-modal,
body[data-theme='dark'] .video-modal-content,
body[data-theme='dark'] .video-iframe-container,
body[data-theme='dark'] .video-iframe,
body[data-theme='dark'] .video-close-btn {
  z-index: 99999 !important;
  isolation: isolate !important;
  position: relative !important;
}

body[data-theme='dark'] .video-modal {
  position: fixed !important;
  inset: 0 !important;
}
</style>
