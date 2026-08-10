<template>
  <div :class="embedded ? 'auth-embed' : 'auth-page'">
    <!-- 背景层 -->
    <div v-if="!embedded" class="page-backdrop" aria-hidden="true">
      <span class="backdrop-orb orb-one"></span>
      <span class="backdrop-orb orb-two"></span>
      <span class="backdrop-orb orb-three"></span>
      <span class="backdrop-grid"></span>
      <span class="backdrop-lines"></span>
    </div>

    <div :class="['auth-shell', { 'auth-shell--embedded': embedded }]">

      <!-- 左侧场景面板 -->
      <section v-if="!embedded" class="scene-panel">
        <div class="scene-logo">
          <div class="logo-mark">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
          </div>
          <span>影智医疗</span>
          <span class="scene-badge">Clinical Access Portal</span>
        </div>

        <h1>登录体验，也应该像一家<br/><em>真正准备上线</em>的医疗平台。</h1>
        <p class="scene-desc">统一深色医疗科技视觉、克制的动态效果与清晰的操作层级，让用户在进入诊疗工作台之前，就对平台建立专业与可信的第一印象。</p>

        <!-- 动态波形监视器 -->
        <div class="scene-monitor">
          <div class="monitor-top">
            <span class="monitor-label">
              <span class="monitor-dot"></span>
              Diagnostic Signal · 实时
            </span>
            <span class="monitor-value">98.7%</span>
          </div>
          <div class="wave-track">
            <span
              v-for="n in 12"
              :key="`wave-${n}`"
              class="wave-bar"
              :style="{ animationDelay: `${n * 0.14}s`, height: (Math.sin(n) * 22 + 32) + 'px' }"
            ></span>
          </div>
          <div class="monitor-tags">
            <span class="mtag">身份校验</span>
            <span class="mtag">数据隔离</span>
            <span class="mtag">等保三级</span>
          </div>
        </div>

        <!-- 旋转轨道图 -->
        <div class="orbit-widget">
          <span class="orbit-ring orbit-outer"></span>
          <span class="orbit-ring orbit-mid"></span>
          <span class="orbit-core">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="#7edcff" stroke="none"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
          </span>
          <span class="orbit-chip orc-1">Scan</span>
          <span class="orbit-chip orc-2">OTP</span>
          <span class="orbit-chip orc-3">Doctor</span>
        </div>

        <!-- 指标卡 -->
        <div class="scene-metrics">
          <article v-for="item in sceneStats" :key="item.label" class="metric-card">
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </article>
        </div>

        <!-- 特性要点 -->
        <div class="scene-points">
          <article v-for="item in securePoints" :key="item.title" class="point-card">
            <div class="point-icon">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#7edcff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20,6 9,17 4,12"/></svg>
            </div>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </article>
        </div>
      </section>

      <!-- 右侧表单面板 -->
      <section :class="['auth-panel', { 'auth-panel--embedded': embedded }]">
        <div class="panel-top">
          <div>
            <p class="panel-eyebrow">账户登录</p>
            <h2>进入医疗 AI 诊断中心</h2>
          </div>
          <span class="panel-chip">
            <span class="chip-dot"></span>
            安全认证已启用
          </span>
        </div>

        <!-- 登录方式 Tab -->
        <div class="login-tabs">
          <button :class="['login-tab', { active: loginType === 'wechat' }]" @click="loginType = 'wechat'">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            微信扫码
          </button>
          <button :class="['login-tab', { active: loginType === 'phone' }]" @click="loginType = 'phone'">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
            手机验证码
          </button>
          <button :class="['login-tab', { active: loginType === 'password' }]" @click="loginType = 'password'">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            账号密码
          </button>
        </div>

        <!-- 消息提示 -->
        <transition name="msg-slide">
          <div v-if="showSuccess" class="message success-message">
            <span class="message-icon">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20,6 9,17 4,12"/></svg>
            </span>
            <span>{{ successMessage }}</span>
          </div>
        </transition>

        <transition name="msg-slide">
          <div v-if="showError" class="message error-message-box">
            <span class="message-icon">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </span>
            <span>{{ errorMessage }}</span>
          </div>
        </transition>

        <!-- 微信扫码 -->
        <transition name="panel-fade" mode="out-in">
          <div v-if="loginType === 'wechat'" class="mode-panel" key="wechat">
            <div class="mode-header">
              <h3>微信扫码登录</h3>
              <p>适合移动端快速进入平台，完成身份确认后可直接同步当前账号状态。</p>
            </div>
            <div class="wechat-box">
              <div class="wechat-card">
                <span class="qr-corner qr-corner-tl"></span>
                <span class="qr-corner qr-corner-tr"></span>
                <span class="qr-corner qr-corner-bl"></span>
                <span class="qr-corner qr-corner-br"></span>
                <div class="qr-core">
                  <div class="qr-scan-line"></div>
                </div>
                <strong>扫码登录占位区</strong>
                <p>接入微信二维码后即可直接替换当前视觉容器。</p>
              </div>
            </div>
            <div class="mode-tips">
              <span>一键登录</span>
              <span>自动绑定账号</span>
              <span>适合患者移动端</span>
            </div>
            <button class="secondary-btn" @click="refreshWechatQRCode">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23,4 23,10 17,10"/><polyline points="1,20 1,14 7,14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
              刷新二维码
            </button>
          </div>

          <!-- 手机验证码 -->
          <div v-else-if="loginType === 'phone'" class="mode-panel" key="phone">
            <div class="mode-header">
              <h3>手机验证码登录</h3>
              <p>通过手机号和验证码快速登录，适合首次体验平台或移动端频繁访问场景。</p>
            </div>
            <form @submit.prevent="handlePhoneLogin" class="auth-form">
              <div class="form-group">
                <label for="phoneNumber">手机号</label>
                <div class="input-wrap">
                  <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
                  <input id="phoneNumber" v-model="phoneNumber" type="tel" placeholder="请输入中国大陆手机号" required />
                </div>
              </div>
              <div class="form-group">
                <label for="verifyCode">验证码</label>
                <div class="input-row">
                  <div class="input-wrap" style="flex:1">
                    <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                    <input id="verifyCode" v-model="verifyCode" type="text" placeholder="请输入短信验证码" required />
                  </div>
                  <button type="button" :class="['send-code-btn', { 'is-pending': sendingCode }]" @click="sendVerifyCode" :disabled="countdown > 0 || sendingCode">
                    {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                  </button>
                </div>
              </div>
              <button type="submit" class="primary-btn" :disabled="phoneLoading">
                <span v-if="phoneLoading" class="btn-inner">
                  <span class="spinner"></span>登录中...
                </span>
                <span v-else class="btn-inner">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12,5 19,12 12,19"/></svg>
                  立即登录
                </span>
              </button>
            </form>
          </div>

          <!-- 账号密码 -->
          <div v-else-if="loginType === 'password'" class="mode-panel" key="password">
            <div class="mode-header">
              <h3>账号密码登录</h3>
              <p>通过账号密码进入影像分析、报告解读与医生协同工作台。</p>
            </div>
            <form @submit.prevent="handleLogin" class="auth-form">
              <div class="form-group">
                <label for="username">用户名</label>
                <div class="input-wrap" :class="{ 'has-error': errors.username }">
                  <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  <input id="username" v-model="username" type="text" placeholder="请输入用户名或工号" required :class="{ error: errors.username }" />
                </div>
                <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
              </div>

              <div class="form-group">
                <label for="password">密码</label>
                <div class="input-row">
                  <div class="input-wrap" :class="{ 'has-error': errors.password }" style="flex:1">
                    <svg class="input-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                    <input id="password" v-model="password" :type="showPwd ? 'text' : 'password'" placeholder="请输入登录密码" required :class="{ error: errors.password }" />
                  </div>
                  <button type="button" class="action-btn" @click="showPwd = !showPwd">
                    <svg v-if="!showPwd" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                  </button>
                </div>
                <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
              </div>

              <div class="form-row">
                <label class="remember-box">
                  <input type="checkbox" v-model="rememberPwd" />
                  <span class="custom-check"></span>
                  <span>记住密码</span>
                </label>
                <a href="#" class="text-link" @click.prevent="onForgot">忘记密码？</a>
              </div>

              <button type="submit" class="primary-btn" :disabled="loading">
                <span v-if="loading" class="btn-inner">
                  <span class="spinner"></span>登录中...
                </span>
                <span v-else class="btn-inner">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                  <span v-explain="'安全登录'">安全登录</span>
                </span>
              </button>
            </form>
          </div>
        </transition>

        <div class="panel-footer">
          <div class="footer-note">
            首次使用平台？
            <a href="#" class="text-link" @click.prevent="switchToRegister">立即注册 →</a>
          </div>
          <p class="footer-tip">登录即表示您同意平台的服务规范与医疗数据安全策略。</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { onBeforeUnmount, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import explain from '../directives/explain.js'
import { smsAPI, authAPI } from '../api/api.js'

export default {
  name: 'Login',
  props: { embedded: { type: Boolean, default: false } },
  directives: { explain },
  emits: ['switch-to-register'],
  setup(props, { emit }) {
    const username = ref('')
    const password = ref('')
    const loading = ref(false)
    const errors = ref({})
    const showSuccess = ref(false)
    const showError = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const store = useStore()
    const router = useRouter()
    const showPwd = ref(false)
    const rememberPwd = ref(true)
    const loginType = ref('password')
    const phoneNumber = ref('')
    const verifyCode = ref('')
    const phoneLoading = ref(false)
    const sendingCode = ref(false)
    const countdown = ref(0)
    let countdownTimer = null

    const sceneStats = [
      { value: '24/7', label: '统一服务入口在线' },
      { value: '3层', label: '认证安全校验' },
      { value: '秒级', label: '登录反馈体验' }
    ]

    const securePoints = [
      { title: '统一认证语言', desc: '登录页与首页视觉一致，帮助商业项目形成更完整的品牌感知。' },
      { title: '兼容多种登录方式', desc: '支持扫码、手机号与账号密码三种入口，便于不同角色快速进入。' },
      { title: '更可信的医疗气质', desc: '通过深色科技风和克制动效，提高专业感与用户信任感。' }
    ]

    const clearMessages = () => {
      showSuccess.value = false
      showError.value = false
      successMessage.value = ''
      errorMessage.value = ''
    }

    const showFeedback = (type, message, duration = 2200) => {
      clearMessages()
      if (sendingCode.value || countdown.value > 0) return
      if (type === 'success') {
        successMessage.value = message
        showSuccess.value = true
        setTimeout(() => { showSuccess.value = false }, duration)
      } else {
        errorMessage.value = message
        showError.value = true
        setTimeout(() => { showError.value = false }, duration)
      }
    }

    const clearCountdownTimer = () => {
      if (countdownTimer) { clearInterval(countdownTimer); countdownTimer = null }
    }

    const startCountdown = () => {
      clearCountdownTimer()
      countdown.value = 60
      countdownTimer = setInterval(() => {
        countdown.value -= 1
        if (countdown.value <= 0) { countdown.value = 0; clearCountdownTimer() }
      }, 1000)
    }

    const onForgot = () => {
      showFeedback('error', '请联系管理员重置密码，或使用绑定邮箱找回账号。', 2600)
    }

    const handleLogin = async () => {
      errors.value = {}
      clearMessages()
      if (!username.value || username.value.trim() === '') { errors.value.username = '用户名不能为空'; return }
      if (!password.value || password.value.trim() === '') { errors.value.password = '密码不能为空'; return }
      loading.value = true
      sendingCode.value = true
      try {
        const success = await store.dispatch('login', { username: username.value.trim(), password: password.value })
        if (success) {
          const state = store.state || {}
          const user = state.user || null
          const role = user && user.role ? user.role : null
          const userId = user && user.id ? user.id : null
          if (userId) window.localStorage.setItem('medical_user_id', String(userId))
          if (role) window.localStorage.setItem('medical_user_role', role)
          if (role === 'DOCTOR') {
            router.push({ name: 'DoctorDashboard' })
          } else {
            successMessage.value = '登录成功，正在进入用户首页...'
            showSuccess.value = true
            setTimeout(() => { router.push({ name: 'PatientHome' }) }, 800)
          }
        } else {
          errorMessage.value = '登录失败，请检查用户名和密码。'
          showError.value = true
        }
      } catch (error) {
        let msg = '登录失败'
        if (error.response?.data) {
          const data = error.response.data
          msg = data.message || data.error || msg
          if (data.message && (data.message.includes('用户不存在') || data.message.includes('密码错误'))) {
            errors.value.password = data.message; msg = data.message
          }
        } else if (error.message) { msg = error.message }
        else if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') { msg = '无法连接到服务器，请检查后端服务是否启动。' }
        errorMessage.value = msg; showError.value = true
      } finally { loading.value = false }
    }

    const switchToRegister = async (e) => {
      if (e) e.preventDefault()
      if (props.embedded) { emit('switch-to-register'); return }
      await router.push({ path: '/register' })
    }

    const refreshWechatQRCode = () => {
      showFeedback('success', '二维码已刷新，请使用微信重新扫码。')
    }

    const sendVerifyCode = async () => {
      clearMessages()
      if (!phoneNumber.value || phoneNumber.value.trim() === '') { showFeedback('error', '请输入手机号。'); return }
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(phoneNumber.value)) { showFeedback('error', '请输入正确的中国大陆手机号。'); return }
      try {
        const response = await smsAPI.sendSms(phoneNumber.value)
        if (response.data && response.data.code === 200) { showFeedback('success', response.data.message || '验证码已发送，请注意查收。'); startCountdown() }
        else { showFeedback('error', response.data?.message || '验证码发送失败，请稍后重试。') }
      } catch (error) { showFeedback('error', error.response?.data?.message || '验证码发送失败，请稍后重试。') }
      sendingCode.value = false
    }

    const handlePhoneLogin = async () => {
      clearMessages()
      if (!phoneNumber.value || phoneNumber.value.trim() === '') { showFeedback('error', '请输入手机号。'); return }
      if (!verifyCode.value || verifyCode.value.trim() === '') { showFeedback('error', '请输入验证码。'); return }
      phoneLoading.value = true
      try {
        const response = await authAPI.loginByPhone(phoneNumber.value, verifyCode.value)
        if (response.data) {
          const authData = response.data
          if (authData.token) window.localStorage.setItem('medical_auth_token', authData.token)
          const user = { id: authData.userId, username: authData.username, email: authData.email, phone: authData.phone, fullName: authData.fullName, role: authData.role }
          store.commit('setUser', user); store.commit('setLogin', true)
          window.localStorage.setItem('medical_user_id', String(user.id))
          window.localStorage.setItem('medical_user_role', user.role)
          successMessage.value = '登录成功，正在跳转首页...'; showSuccess.value = true
          setTimeout(() => { if (user.role === 'DOCTOR') { router.push({ name: 'DoctorDashboard' }) } else { router.push({ name: 'PatientHome' }) } }, 800)
        }
      } catch (error) {
        let msg = '登录失败'
        if (error.response?.data) { msg = error.response.data.message || error.response.data.error || msg }
        else if (error.message) { msg = error.message }
        else if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') { msg = '无法连接到服务器，请检查后端服务是否启动。' }
        errorMessage.value = msg; showError.value = true
      } finally { phoneLoading.value = false }
    }

    onBeforeUnmount(() => { clearCountdownTimer() })

    return { username, password, loading, errors, showSuccess, showError, successMessage, errorMessage, handleLogin, switchToRegister, showPwd, rememberPwd, onForgot, loginType, phoneNumber, verifyCode, phoneLoading, sendingCode, countdown, refreshWechatQRCode, sendVerifyCode, handlePhoneLogin, sceneStats, securePoints }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }

.auth-page, .auth-embed {
  --bg: var(--ui-bg-page);
  --panel-bg: var(--ui-surface-panel);
  --line: var(--ui-border-accent);
  --soft-line: var(--ui-border-soft);
  --text: var(--ui-text-primary);
  --muted: var(--ui-text-muted);
  --accent: var(--ui-brand-primary);
  --accent-alt: var(--ui-brand-accent);
  --danger: var(--ui-danger);
  --surface-card: var(--ui-surface-card);
  --surface-card-strong: var(--ui-surface-card-strong);
  --surface-chip: var(--ui-surface-chip);
  --shadow-panel: var(--ui-shadow-panel-strong);
  position: relative;
  width: 100%;
  font-family: var(--ui-font-family);
}

/* ===== PAGE ===== */
.auth-page {
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: clamp(8px, 1.6vw, 16px) var(--app-shell-gutter, 32px) 20px;
  overflow: hidden;
  background: var(--ui-bg-auth-gradient);
}

.auth-embed { display: flex; min-height: 100%; }

/* ===== BACKDROP ===== */
.page-backdrop { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }

.backdrop-orb { position: absolute; border-radius: 50%; }

.orb-one {
  width: 480px; height: 480px; top: -140px; left: -130px;
  background: radial-gradient(circle, rgba(84,165,255,0.22) 0%, transparent 65%);
  filter: blur(60px);
  animation: drift 18s ease-in-out infinite;
}
.orb-two {
  width: 560px; height: 560px; right: -160px; bottom: -140px;
  background: radial-gradient(circle, rgba(72,239,203,0.16) 0%, transparent 65%);
  filter: blur(70px);
  animation: drift 22s ease-in-out infinite -6s;
}
.orb-three {
  width: 320px; height: 320px; left: 45%; top: 30%;
  background: radial-gradient(circle, rgba(120,100,255,0.08) 0%, transparent 65%);
  filter: blur(50px);
  animation: drift 14s ease-in-out infinite -10s;
}

.backdrop-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(var(--ui-grid-color-strong) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color-strong) 1px, transparent 1px);
  background-size: 56px 56px;
  mask-image: linear-gradient(180deg, rgba(0,0,0,0.8) 0%, transparent 75%);
}

.backdrop-lines {
  position: absolute; inset: 0;
  background: linear-gradient(
    180deg,
    transparent 0%,
    rgba(126,220,255,0.02) 40%,
    rgba(72,239,203,0.02) 60%,
    transparent 100%
  );
}

/* ===== SHELL ===== */
.auth-shell {
  position: relative; z-index: 1;
  width: 100%; max-width: var(--app-shell-max-width, 1280px);
  min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 40px), 820px);
  display: grid;
  grid-template-columns: minmax(0,1fr) minmax(0,0.9fr);
  gap: 16px;
}

.auth-shell--embedded { max-width: none; display: block; min-height: 100%; }

/* ===== PANELS ===== */
.scene-panel, .auth-panel {
  position: relative;
  overflow: hidden;
  border-radius: var(--ui-radius-xl);
  border: 1px solid var(--soft-line);
  backdrop-filter: blur(var(--ui-glass-blur));
  box-shadow: var(--shadow-panel), inset 0 1px 0 rgba(255,255,255,0.08);
}

.scene-panel {
  min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 40px), 820px);
  padding: 28px;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(ellipse 60% 40% at 80% 0%, rgba(var(--ui-brand-accent-rgb),0.1) 0%, transparent 50%),
    radial-gradient(ellipse 50% 50% at 10% 90%, rgba(var(--ui-brand-primary-rgb),0.08) 0%, transparent 50%),
    linear-gradient(180deg, var(--ui-surface-panel), var(--ui-surface-panel-strong));
}

/* ===== SCENE LOGO ===== */
.scene-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
}

.logo-mark {
  width: 38px; height: 38px;
  background: linear-gradient(135deg, rgba(var(--ui-brand-primary-rgb),0.2), rgba(var(--ui-brand-accent-rgb),0.15));
  border: 1px solid rgba(var(--ui-brand-primary-rgb),0.25);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: var(--accent);
  box-shadow: 0 0 20px rgba(var(--ui-brand-primary-rgb),0.12);
}

.scene-logo > span:nth-child(2) {
  font-size: 16px;
  font-weight: 700;
  color: var(--text);
  letter-spacing: 0.04em;
}

.scene-badge {
  margin-left: auto;
  display: inline-flex; align-items: center;
  padding: 5px 12px;
  border-radius: 999px;
  background: var(--surface-chip);
  border: 1px solid var(--soft-line);
  color: var(--ui-text-secondary);
  font-size: 11px; font-weight: 700;
  letter-spacing: 0.06em; text-transform: uppercase;
}

.scene-panel h1 {
  margin: 0 0 12px;
  font-size: clamp(26px,2.8vw,40px);
  font-weight: 800;
  line-height: 1.15;
  letter-spacing: -0.03em;
  color: var(--text);
}

.scene-panel h1 em {
  font-style: normal;
  background: linear-gradient(135deg, var(--accent), var(--accent-alt));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.scene-desc {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.8;
  margin-bottom: 20px;
}

/* ===== MONITOR ===== */
.scene-monitor {
  background: var(--surface-card);
  border: 1px solid var(--soft-line);
  border-radius: var(--ui-radius-md);
  padding: 16px;
  margin-bottom: 14px;
}

.monitor-top {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 14px;
}

.monitor-label {
  display: flex; align-items: center; gap: 7px;
  font-size: 11px; font-weight: 700;
  color: var(--ui-text-secondary);
  letter-spacing: 0.06em; text-transform: uppercase;
}

.monitor-dot {
  width: 7px; height: 7px; border-radius: 50%;
  background: var(--accent-alt);
  box-shadow: 0 0 8px rgba(72,239,203,0.7);
  animation: blink 2s ease-in-out infinite;
}

.monitor-value {
  font-size: 22px; font-weight: 800; color: var(--accent);
  letter-spacing: -0.02em;
}

.wave-track {
  height: 60px;
  display: flex; align-items: flex-end; gap: 4px;
}

.wave-bar {
  flex: 1; min-height: 8px;
  border-radius: 4px 4px 2px 2px;
  background: linear-gradient(180deg, var(--accent) 0%, var(--accent-alt) 100%);
  opacity: 0.85;
  animation: waveMove 2s ease-in-out infinite;
}

.monitor-tags {
  display: flex; gap: 8px; margin-top: 12px; flex-wrap: wrap;
}

.mtag {
  padding: 4px 12px;
  border-radius: 999px;
  background: var(--surface-chip);
  border: 1px solid var(--soft-line);
  color: var(--ui-text-secondary);
  font-size: 11px; font-weight: 600;
}

/* ===== ORBIT ===== */
.orbit-widget {
  position: relative;
  height: 120px;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 14px;
}

.orbit-ring {
  position: absolute;
  border-radius: 50%;
}

.orbit-outer {
  width: 110px; height: 110px;
  border: 1px solid rgba(126,220,255,0.16);
  animation: spinSlow 14s linear infinite;
}

.orbit-mid {
  width: 68px; height: 68px;
  border: 1px dashed rgba(72,239,203,0.2);
  animation: spinSlow 9s linear infinite reverse;
}

.orbit-core {
  position: absolute;
  width: 28px; height: 28px;
  background: linear-gradient(135deg, rgba(126,220,255,0.2), rgba(72,239,203,0.15));
  border: 1px solid rgba(126,220,255,0.3);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 20px rgba(126,220,255,0.25);
}

.orbit-chip {
  position: absolute;
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--surface-chip);
  border: 1px solid var(--soft-line);
  color: var(--text);
  font-size: 11px; font-weight: 700;
}

.orc-1 { top: 8px; left: 0; }
.orc-2 { right: 0; top: 40px; }
.orc-3 { bottom: 8px; left: 50%; transform: translateX(-50%); }

/* ===== METRICS ===== */
.scene-metrics {
  display: grid; grid-template-columns: repeat(3,1fr);
  gap: 10px; margin-bottom: 12px;
}

.metric-card {
  padding: 14px 12px;
  border-radius: var(--ui-radius-md);
  background: var(--surface-card);
  border: 1px solid var(--soft-line);
  transition: border-color 0.2s, transform 0.2s;
}

.metric-card:hover { border-color: rgba(126,220,255,0.2); transform: translateY(-2px); }

.metric-card strong { display: block; color: var(--text); font-size: 20px; font-weight: 800; letter-spacing: -0.02em; }
.metric-card span { display: block; margin-top: 4px; color: var(--muted); font-size: 11px; line-height: 1.55; }

/* ===== POINTS ===== */
.scene-points { display: flex; flex-direction: column; gap: 10px; margin-top: auto; padding-top: 12px; }

.point-card {
  display: flex; align-items: flex-start; gap: 12px;
  padding: 12px 14px;
  border-radius: var(--ui-radius-sm);
  background: var(--surface-card);
  border: 1px solid var(--soft-line);
  transition: all 0.2s;
}

.point-card:hover { border-color: var(--line); background: var(--surface-card-strong); }

.point-icon {
  width: 26px; height: 26px; flex-shrink: 0;
  background: rgba(126,220,255,0.1);
  border: 1px solid rgba(126,220,255,0.2);
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  margin-top: 1px;
}

.point-card strong { display: block; color: var(--text); font-size: 13px; font-weight: 700; margin-bottom: 3px; }
.point-card p { margin: 0; color: var(--muted); font-size: 12px; line-height: 1.65; }

/* ===== AUTH PANEL ===== */
.auth-panel {
  display: flex; flex-direction: column;
  min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 40px), 820px);
  padding: 28px;
  background:
    radial-gradient(ellipse 60% 30% at 90% 0%, rgba(var(--ui-brand-primary-rgb),0.08) 0%, transparent 50%),
    linear-gradient(180deg, var(--ui-surface-panel), var(--ui-surface-panel-strong));
}

.auth-panel--embedded {
  min-height: 100%; padding: 0;
  border: none; border-radius: 0; box-shadow: none; background: transparent;
}

.auth-panel--embedded::after { display: none; }

/* ===== PANEL TOP ===== */
.panel-top {
  display: flex; align-items: flex-start;
  justify-content: space-between; gap: 14px;
  margin-bottom: 20px;
}

.panel-eyebrow {
  margin: 0 0 8px;
  font-size: 11px; font-weight: 700;
  letter-spacing: 0.1em; text-transform: uppercase;
  color: rgba(var(--ui-brand-primary-rgb),0.78);
}

.panel-top h2 {
  margin: 0;
  font-size: 24px; font-weight: 800;
  color: var(--text); line-height: 1.2;
  letter-spacing: -0.02em;
}

.panel-chip {
  display: inline-flex; align-items: center; gap: 7px;
  flex-shrink: 0;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(var(--ui-brand-accent-rgb),0.08);
  border: 1px solid rgba(var(--ui-brand-accent-rgb),0.18);
  color: var(--ui-text-secondary);
  font-size: 11px; font-weight: 700;
}

.chip-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: var(--accent-alt);
  box-shadow: 0 0 7px rgba(72,239,203,0.8);
  animation: blink 2s ease-in-out infinite;
}

/* ===== TABS ===== */
.login-tabs {
  display: grid; grid-template-columns: repeat(3,1fr);
  gap: 6px; padding: 5px;
  border-radius: var(--ui-radius-md);
  background: var(--surface-card);
  border: 1px solid var(--soft-line);
  margin-bottom: 16px;
}

.login-tab {
  height: 40px; border: none; border-radius: var(--ui-radius-sm);
  display: inline-flex; align-items: center; justify-content: center; gap: 6px;
  background: transparent;
  color: var(--muted);
  font-size: 13px; font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
}

.login-tab:hover { color: var(--text); }

.login-tab.active {
  background: linear-gradient(135deg, rgba(var(--ui-brand-primary-rgb),0.18), rgba(var(--ui-brand-accent-rgb),0.12));
  border: 1px solid rgba(var(--ui-brand-primary-rgb),0.22);
  color: var(--text);
  box-shadow: 0 4px 16px rgba(var(--ui-brand-primary-rgb),0.1);
}

/* ===== MESSAGE ===== */
.message {
  display: flex; align-items: center; gap: 10px;
  padding: 11px 14px; border-radius: 14px;
  font-size: 13px; margin-bottom: 12px;
}

.message-icon {
  width: 22px; height: 22px; flex-shrink: 0;
  display: inline-flex; align-items: center; justify-content: center;
  border-radius: 50%;
}

.success-message { color: #c9ffe4; background: rgba(22,163,74,0.12); border: 1px solid rgba(74,222,128,0.2); }
.success-message .message-icon { background: #16a34a; }

.error-message-box { color: #ffd8d8; background: rgba(220,38,38,0.12); border: 1px solid rgba(248,113,113,0.2); }
.error-message-box .message-icon { background: #dc2626; }

/* ===== MODE PANEL ===== */
.mode-panel { display: flex; flex: 1; flex-direction: column; }

.mode-header { margin-bottom: 18px; }
.mode-header h3 { margin: 0 0 7px; color: var(--text); font-size: 19px; font-weight: 700; }
.mode-header p { margin: 0; color: var(--muted); font-size: 13px; line-height: 1.75; }

/* ===== WECHAT BOX ===== */
.wechat-box {
  flex: 1; display: flex; align-items: center; justify-content: center;
  min-height: 200px; border-radius: var(--ui-radius-md);
  background: var(--surface-card);
  border: 1px solid var(--soft-line);
}

.wechat-card {
  position: relative; width: min(260px, 100%);
  padding: 22px 18px 18px;
  border-radius: var(--ui-radius-md); text-align: center;
  background: var(--ui-surface-panel);
  border: 1px solid var(--soft-line);
}

.qr-corner {
  position: absolute; width: 24px; height: 24px;
  border-color: rgba(126,220,255,0.7); border-style: solid;
}
.qr-corner-tl { top: 12px; left: 12px; border-width: 2px 0 0 2px; }
.qr-corner-tr { top: 12px; right: 12px; border-width: 2px 2px 0 0; }
.qr-corner-bl { bottom: 12px; left: 12px; border-width: 0 0 2px 2px; }
.qr-corner-br { bottom: 12px; right: 12px; border-width: 0 2px 2px 0; }

.qr-core {
  position: relative; width: 106px; height: 106px;
  margin: 6px auto 14px;
  border-radius: 14px;
  background:
    linear-gradient(90deg, rgba(255,255,255,0.07) 1px, transparent 1px),
    linear-gradient(rgba(255,255,255,0.07) 1px, transparent 1px),
    radial-gradient(circle at center, rgba(126,220,255,0.18), rgba(126,220,255,0.04));
  background-size: 16px 16px, 16px 16px, 100% 100%;
  overflow: hidden;
}

.qr-scan-line {
  position: absolute; width: 100%; height: 2px;
  background: linear-gradient(90deg, transparent, rgba(126,220,255,0.7), transparent);
  animation: qrScan 2.5s linear infinite;
}

.wechat-card strong { display: block; color: var(--text); font-size: 16px; font-weight: 700; }
.wechat-card p { margin: 8px 0 0; color: var(--muted); font-size: 12px; line-height: 1.75; }

.mode-tips { display: flex; flex-wrap: wrap; gap: 8px; margin: 14px 0 16px; }

.mode-tips span {
  display: inline-flex; align-items: center;
  padding: 4px 12px; border-radius: 999px;
  background: var(--surface-chip);
  border: 1px solid var(--soft-line);
  color: var(--ui-text-secondary);
  font-size: 12px; font-weight: 600;
}

/* ===== FORM ===== */
.auth-form { display: grid; gap: 14px; }

.form-group { display: grid; gap: 8px; }
.form-group label { color: var(--ui-text-secondary); font-size: 13px; font-weight: 600; }

.input-wrap {
  position: relative;
  display: flex; align-items: center;
}

.input-icon {
  position: absolute; left: 14px;
  color: var(--ui-text-subtle); pointer-events: none; flex-shrink: 0;
}

.input-wrap input {
  width: 100%; height: 48px;
  padding: 0 16px 0 40px;
  border-radius: var(--ui-radius-sm);
  border: 1px solid var(--soft-line);
  background: var(--surface-card);
  color: var(--text); font-size: 14px;
  transition: all 0.2s ease;
  font-family: inherit;
}

.input-wrap input::placeholder { color: var(--ui-text-subtle); }

.input-wrap input:focus {
  outline: none;
  border-color: rgba(126,220,255,0.38);
  background: rgba(126,220,255,0.04);
  box-shadow: 0 0 0 3px rgba(126,220,255,0.07);
}

.input-wrap input.error { border-color: rgba(248,113,113,0.45); }

.input-wrap.has-error input { border-color: rgba(248,113,113,0.45); }

.input-row { display: flex; gap: 10px; align-items: center; }
.input-row .input-wrap { flex: 1; }

.form-row {
  display: flex; align-items: center;
  justify-content: space-between; gap: 12px;
}

.remember-box {
  display: inline-flex; align-items: center; gap: 9px;
  color: rgba(210,225,245,0.75); font-size: 13px; cursor: pointer;
}

.custom-check {
  width: 16px; height: 16px;
  border-radius: 5px;
  border: 1.5px solid rgba(126,220,255,0.3);
  background: rgba(255,255,255,0.04);
  flex-shrink: 0;
  transition: all 0.2s;
}

.remember-box input[type="checkbox"] { display: none; }
.remember-box input[type="checkbox"]:checked + .custom-check {
  background: var(--accent);
  border-color: var(--accent);
}

/* ===== BUTTONS ===== */
.action-btn {
  min-width: 48px; height: 48px; padding: 0 14px;
  border-radius: var(--ui-radius-sm); border: none;
  background: var(--surface-chip);
  color: var(--ui-text-secondary);
  cursor: pointer; transition: all 0.2s;
  display: flex; align-items: center; justify-content: center;
}

.action-btn:hover { background: rgba(126,220,255,0.1); }

.send-code-btn {
  width: 116px; min-width: 116px; height: 48px; padding: 0 14px;
  border-radius: var(--ui-radius-sm); border: none; flex-shrink: 0;
  background: linear-gradient(135deg, var(--accent), var(--accent-alt));
  color: var(--ui-text-contrast); font-size: 13px; font-weight: 700;
  cursor: pointer; transition: all 0.2s ease;
  box-shadow: var(--ui-shadow-accent);
  will-change: transform, opacity;
}

.send-code-btn:hover:not(:disabled) { box-shadow: 0 10px 26px rgba(126,220,255,0.28); transform: translateY(-1px); }
.send-code-btn.is-pending { opacity: 0.72; transform: none; }
.send-code-btn:disabled { opacity: 0.5; cursor: not-allowed; box-shadow: none; }

.primary-btn {
  width: 100%; height: 50px;
  border: none; border-radius: var(--radius-button);
  background: linear-gradient(135deg, var(--accent), var(--accent-alt));
  color: var(--ui-text-contrast);
  font-size: 15px; font-weight: 700;
  cursor: pointer;
  box-shadow: var(--ui-shadow-accent);
  transition: all 0.25s;
  position: relative; overflow: hidden;
}

.primary-btn::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.15), transparent 50%);
  pointer-events: none;
}

.primary-btn:hover:not(:disabled) {
  box-shadow: 0 16px 36px rgba(126,220,255,0.3);
  transform: translateY(-2px);
}

.primary-btn:disabled { opacity: 0.55; cursor: not-allowed; box-shadow: none; transform: none; }

.btn-inner { display: inline-flex; align-items: center; gap: 8px; }

.secondary-btn {
  width: 100%; height: 46px;
  border: 1px solid var(--soft-line);
  border-radius: var(--ui-radius-sm);
  display: inline-flex; align-items: center; justify-content: center; gap: 8px;
  background: var(--surface-card);
  color: var(--text);
  font-size: 13px; font-weight: 600;
  cursor: pointer; transition: all 0.22s;
}

.secondary-btn:hover { background: var(--surface-card-strong); border-color: var(--line); }

.text-link { color: var(--accent); font-weight: 700; text-decoration: none; transition: opacity 0.2s; }
.text-link:hover { opacity: 0.8; }

.error-message { color: var(--danger); font-size: 12px; display: flex; align-items: center; gap: 5px; }

/* ===== FOOTER ===== */
.panel-footer { margin-top: auto; padding-top: 18px; border-top: 1px solid var(--soft-line); }
.footer-note { color: var(--ui-text-secondary); font-size: 14px; margin-bottom: 8px; }
.footer-tip { margin: 0; color: var(--ui-text-subtle); font-size: 12px; line-height: 1.65; }

/* ===== SPINNER ===== */
.spinner {
  width: 15px; height: 15px;
  border: 2px solid rgba(6,17,29,0.25);
  border-top-color: #06111d;
  border-radius: 50%;
  animation: spin 0.75s linear infinite;
}

/* ===== TRANSITIONS ===== */
.msg-slide-enter-active, .msg-slide-leave-active { transition: all 0.25s ease; }
.msg-slide-enter-from, .msg-slide-leave-to { opacity: 0; transform: translateY(-6px); }

.panel-fade-enter-active, .panel-fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.panel-fade-enter-from, .panel-fade-leave-to { opacity: 0; transform: translateX(6px); }

/* ===== KEYFRAMES ===== */
@keyframes drift {
  0%,100% { transform: translate3d(0,0,0) scale(1); }
  50% { transform: translate3d(20px,-16px,0) scale(1.08); }
}

@keyframes waveMove {
  0%,100% { transform: scaleY(0.65); opacity: 0.7; }
  50% { transform: scaleY(1); opacity: 1; }
}

@keyframes spinSlow { to { transform: rotate(360deg); } }
@keyframes spin { to { transform: rotate(360deg); } }

@keyframes blink {
  0%,100% { opacity: 1; }
  50% { opacity: 0.35; }
}

@keyframes qrScan {
  0% { top: 0; opacity: 0; }
  5% { opacity: 1; }
  95% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

/* ===== RESPONSIVE ===== */
@media (max-width: 1080px) {
  .auth-shell { grid-template-columns: 1fr; min-height: auto; }
  .scene-panel, .auth-panel { min-height: auto; }
}

@media (max-width: 760px) {
  .auth-page { padding: 8px var(--app-shell-gutter, 32px) 14px; }
  .scene-panel, .auth-panel { padding: 22px; border-radius: var(--ui-radius-lg); }
  .panel-top, .form-row, .input-row { flex-direction: column; align-items: stretch; }
  .scene-metrics { grid-template-columns: 1fr; }
  .login-tabs { gap: 4px; }
  .panel-chip { align-self: flex-start; }
}

@media (max-height: 900px) {
  .auth-shell { min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 24px), 760px); }
  .scene-panel, .auth-panel { min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 24px), 760px); padding: 20px; }
  .scene-panel h1 { font-size: clamp(22px, 2.5vw, 34px); }
}
</style>
