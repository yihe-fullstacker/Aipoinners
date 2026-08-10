<template>
  <div class="light-auth">
    <transition name="fade-slide">
      <div v-if="showSuccess" class="feedback success">
        <span>{{ successMessage }}</span>
      </div>
    </transition>

    <transition name="fade-slide">
      <div v-if="showError" class="feedback error">
        <span>{{ errorMessage }}</span>
      </div>
    </transition>

    <div class="method-switch">
      <button :class="['method-btn', { active: loginType === 'password' }]" @click="loginType = 'password'">密码</button>
      <button :class="['method-btn', { active: loginType === 'phone' }]" @click="loginType = 'phone'">短信</button>
      <button :class="['method-btn', { active: loginType === 'wechat' }]" @click="loginType = 'wechat'">授权</button>
    </div>

    <transition name="panel-fade" mode="out-in">
      <div v-if="loginType === 'password'" key="password" class="panel-body">
        <div class="field-group">
          <label for="light-login-username">工作邮箱</label>
          <div class="field-wrap" :class="{ error: errors.username }">
            <input
              id="light-login-username"
              v-model="username"
              type="text"
              placeholder="dr.thorne@lumina.ai / 用户名"
              required
            />
          </div>
          <p v-if="errors.username" class="error-text">{{ errors.username }}</p>
        </div>

        <div class="field-group">
          <label for="light-login-password">密码</label>
          <div class="password-row">
            <div class="field-wrap" :class="{ error: errors.password }">
              <input
                id="light-login-password"
                v-model="password"
                :type="showPwd ? 'text' : 'password'"
                placeholder="••••••••"
                required
              />
            </div>
            <button type="button" class="icon-btn" @click="showPwd = !showPwd">
              <svg v-if="!showPwd" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
            </button>
          </div>
          <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
        </div>

        <div class="helper-row">
          <label class="remember-box">
            <input v-model="rememberPwd" type="checkbox" />
            <span class="check-ui"></span>
            <span>记住我</span>
          </label>
          <button type="button" class="link-btn" @click="onForgot">忘记密码？</button>
        </div>

        <button class="primary-btn" :disabled="loading" @click="handleLogin">
          <span v-if="loading" class="btn-inline"><span class="spinner"></span>登录中...</span>
          <span v-else class="btn-inline">
            <span>进入平台</span>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12,5 19,12 12,19"/></svg>
          </span>
        </button>

        <div class="auth-divider">
          <span>授权入口</span>
        </div>

        <button type="button" class="secondary-btn" @click="loginType = 'wechat'">
          <span class="medical-id-dot"></span>
          使用微信 扫码 登录
        </button>
      </div>

      <div v-else-if="loginType === 'phone'" key="phone" class="panel-body">
        <div class="card-copy">
          <h3>短信验证码登录</h3>
          <p>通过手机号与验证码快速进入平台，保留原有验证逻辑。</p>
        </div>

        <div class="field-group">
          <label for="light-login-phone">手机号</label>
          <div class="field-wrap">
            <input id="light-login-phone" v-model="phoneNumber" type="tel" placeholder="请输入中国大陆手机号" required />
          </div>
        </div>

        <div class="field-group">
          <label for="light-login-code">验证码</label>
          <div class="code-row">
            <div class="field-wrap">
              <input id="light-login-code" v-model="verifyCode" type="text" placeholder="请输入短信验证码" required />
            </div>
            <button type="button" class="code-btn" :disabled="countdown > 0 || sendingCode" @click="sendVerifyCode">
              <span class="btn-inline">
                <span v-if="sendingCode" class="spinner spinner--dark"></span>
                {{ sendingCode ? '发送中...' : (countdown > 0 ? `${countdown}s` : '获取验证码') }}
              </span>
            </button>
          </div>
        </div>

        <button class="primary-btn" :disabled="phoneLoading" @click="handlePhoneLogin">
          <span v-if="phoneLoading" class="btn-inline"><span class="spinner"></span>登录中...</span>
          <span v-else class="btn-inline">
            <span>进入平台</span>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12,5 19,12 12,19"/></svg>
          </span>
        </button>
      </div>

      <div v-else key="wechat" class="panel-body">
        <div class="card-copy">
          <h3>微信 扫码 快捷登录</h3>
          <p>这里保留原有的授权登录入口，可继续接入真实二维码服务。</p>
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
            <strong>微信 扫码 授权区</strong>
            <p>接入真实授权二维码后，这里可以直接承接微信 扫码 登录流程。</p>
          </div>
        </div>

        <div class="mode-tips">
          <span>授权访问</span>
          <span>快速进入</span>
          <span>医疗入口</span>
        </div>

        <button class="secondary-action-btn" @click="refreshWechatQRCode">
          刷新授权码
        </button>
      </div>
    </transition>

    <div class="footer-row">
      <span>首次使用平台？</span>
      <button type="button" class="link-btn strong" @click="switchToRegister">立即注册</button>
    </div>
  </div>
</template>

<script>
import { onBeforeUnmount, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { authAPI, smsAPI } from '../api/api'

export default {
  name: 'LightLoginCard',
  emits: ['switch-to-register'],
  setup(props, { emit }) {
    const store = useStore()
    const router = useRouter()

    const username = ref('')
    const password = ref('')
    const loginType = ref('password')
    const loading = ref(false)
    const phoneLoading = ref(false)
    const showPwd = ref(false)
    const rememberPwd = ref(true)
    const phoneNumber = ref('')
    const verifyCode = ref('')
    const countdown = ref(0)
    const sendingCode = ref(false)
    const errors = ref({})
    const showSuccess = ref(false)
    const showError = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')

    let countdownTimer = null

    const clearMessages = () => {
      showSuccess.value = false
      showError.value = false
      successMessage.value = ''
      errorMessage.value = ''
    }

    const showFeedback = (type, message, duration = 2200) => {
      clearMessages()
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
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }

    const startCountdown = () => {
      clearCountdownTimer()
      countdown.value = 60
      countdownTimer = setInterval(() => {
        countdown.value -= 1
        if (countdown.value <= 0) {
          countdown.value = 0
          clearCountdownTimer()
        }
      }, 1000)
    }

    const finalizeLogin = async (user) => {
      if (user?.id) window.localStorage.setItem('medical_user_id', String(user.id))
      if (user?.role) window.localStorage.setItem('medical_user_role', user.role)

      const targetPath = user?.role === 'DOCTOR' ? '/doctor/dashboard' : '/patient/home'
      await router.replace(targetPath)
    }

    const handleLogin = async () => {
      errors.value = {}
      clearMessages()

      if (!username.value || username.value.trim() === '') {
        errors.value.username = '请输入工作邮箱或用户名'
        return
      }
      if (!password.value || password.value.trim() === '') {
        errors.value.password = '请输入密码'
        return
      }

      loading.value = true
      try {
        const success = await store.dispatch('login', {
          username: username.value.trim(),
          password: password.value
        })

        if (!success) {
          showFeedback('error', '登录失败，请检查账号和密码。')
          return
        }

        showFeedback('success', '登录成功，正在进入平台...', 1200)
        await finalizeLogin(store.state.user || null)
      } catch (error) {
        let msg = '登录失败'
        if (error.response?.data) {
          const data = error.response.data
          msg = data.message || data.error || msg
          if (data.message && (data.message.includes('用户不存在') || data.message.includes('密码错误'))) {
            errors.value.password = data.message
          }
        } else if (error.message) {
          msg = error.message
        } else if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
          msg = '无法连接到服务器，请检查后端服务是否已启动。'
        }
        showFeedback('error', msg, 2600)
      } finally {
        loading.value = false
      }
    }

    const sendVerifyCode = async () => {
      clearMessages()
      if (sendingCode.value || countdown.value > 0) return
      if (!phoneNumber.value || phoneNumber.value.trim() === '') {
        showFeedback('error', '请输入手机号。')
        return
      }
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(phoneNumber.value)) {
        showFeedback('error', '请输入正确的中国大陆手机号。')
        return
      }

      sendingCode.value = true
      try {
        const response = await smsAPI.sendSms(phoneNumber.value)
        if (response.data && response.data.code === 200) {
          showFeedback('success', response.data.message || '验证码已发送，请注意查收。')
          startCountdown()
        } else {
          showFeedback('error', response.data?.message || '验证码发送失败，请稍后重试。')
        }
      } catch (error) {
        showFeedback('error', error.response?.data?.message || '验证码发送失败，请稍后重试。')
      } finally {
        sendingCode.value = false
      }
    }

    const handlePhoneLogin = async () => {
      clearMessages()
      if (!phoneNumber.value || phoneNumber.value.trim() === '') {
        showFeedback('error', '请输入手机号。')
        return
      }
      if (!verifyCode.value || verifyCode.value.trim() === '') {
        showFeedback('error', '请输入验证码。')
        return
      }

      phoneLoading.value = true
      try {
        const response = await authAPI.loginByPhone(phoneNumber.value, verifyCode.value)
        const authData = response.data || {}
        if (!authData.token) throw new Error('登录失败，请稍后重试。')

        const user = {
          id: authData.userId,
          username: authData.username,
          email: authData.email,
          phone: authData.phone,
          fullName: authData.fullName,
          role: authData.role
        }
        store.commit('login', { user, token: authData.token })
        showFeedback('success', '登录成功，正在进入平台...', 1200)
        await finalizeLogin(user)
      } catch (error) {
        let msg = '登录失败'
        if (error.response?.data) {
          msg = error.response.data.message || error.response.data.error || msg
        } else if (error.message) {
          msg = error.message
        }
        showFeedback('error', msg, 2600)
      } finally {
        phoneLoading.value = false
      }
    }

    const onForgot = () => {
      showFeedback('error', '请联系管理员重置密码，或使用已绑定方式找回账号。', 2600)
    }

    const refreshWechatQRCode = () => {
      showFeedback('success', '授权码已刷新，请重新扫码。')
    }

    const switchToRegister = () => emit('switch-to-register')

    onBeforeUnmount(() => {
      clearCountdownTimer()
    })

    return {
      username,
      password,
      loginType,
      loading,
      phoneLoading,
      showPwd,
      rememberPwd,
      phoneNumber,
      verifyCode,
      countdown,
      sendingCode,
      errors,
      showSuccess,
      showError,
      successMessage,
      errorMessage,
      handleLogin,
      handlePhoneLogin,
      sendVerifyCode,
      onForgot,
      refreshWechatQRCode,
      switchToRegister
    }
  }
}
</script>

<style scoped>
.light-auth {
  color: #0f2342;
  display: grid;
  gap: 18px;
}

.feedback {
  padding: 12px 14px;
  border-radius: 16px;
  font-size: 13px;
  line-height: 1.5;
}

.feedback.success {
  background: rgba(36, 168, 104, 0.1);
  color: #176742;
  border: 1px solid rgba(36, 168, 104, 0.16);
}

.feedback.error {
  background: rgba(210, 71, 71, 0.1);
  color: #9f2f39;
  border: 1px solid rgba(210, 71, 71, 0.14);
}

.method-switch {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  padding: 6px;
  border-radius: 999px;
  background: rgba(226, 232, 241, 0.86);
}

.method-btn {
  border: none;
  height: 38px;
  border-radius: 999px;
  background: transparent;
  color: #7b879a;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.method-btn.active {
  background: rgba(255, 255, 255, 0.96);
  color: #0d2d63;
  box-shadow: 0 6px 14px rgba(0, 28, 59, 0.08);
}

.panel-body {
  display: grid;
  gap: 16px;
}

.card-copy h3 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 800;
  color: #10284f;
}

.card-copy p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #768399;
}

.field-group {
  display: grid;
  gap: 8px;
}

.field-group label {
  margin-left: 2px;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: #8b96aa;
}

.field-wrap {
  background: rgba(239, 243, 248, 0.92);
  border-radius: 16px;
  border: 1px solid rgba(201, 211, 224, 0.28);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.field-wrap:focus-within {
  border-color: rgba(38, 109, 194, 0.24);
  box-shadow: 0 0 0 3px rgba(70, 145, 214, 0.12);
}

.field-wrap.error {
  border-color: rgba(210, 71, 71, 0.35);
}

.field-wrap input {
  width: 100%;
  height: 52px;
  border: none;
  background: transparent;
  padding: 0 18px;
  color: #122543;
  font-size: 14px;
  outline: none;
  font-family: inherit;
}

.field-wrap input::placeholder {
  color: #afb7c5;
}

.password-row,
.code-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.password-row .field-wrap,
.code-row .field-wrap {
  flex: 1;
}

.icon-btn,
.code-btn {
  border: none;
  border-radius: 14px;
  background: rgba(232, 237, 244, 0.96);
  color: #56657b;
  font-weight: 700;
  cursor: pointer;
}

.icon-btn {
  width: 52px;
  height: 52px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.code-btn {
  width: 118px;
  min-width: 118px;
  height: 52px;
  padding: 0 16px;
  color: #0d2d63;
  transition: opacity 0.2s ease, transform 0.2s ease;
  will-change: transform, opacity;
}

.code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.helper-row,
.footer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.remember-box {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #68758b;
  cursor: pointer;
}

.remember-box input {
  display: none;
}

.check-ui {
  width: 15px;
  height: 15px;
  border-radius: 4px;
  border: 1px solid rgba(165, 176, 194, 0.85);
  background: rgba(255, 255, 255, 0.9);
}

.remember-box input:checked + .check-ui {
  background: #0c2d63;
  border-color: #0c2d63;
}

.link-btn {
  border: none;
  background: transparent;
  padding: 0;
  font-size: 12px;
  font-weight: 600;
  color: #5b7aa4;
  cursor: pointer;
}

.link-btn.strong {
  font-weight: 700;
}

.primary-btn,
.secondary-btn,
.secondary-action-btn {
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.primary-btn {
  height: 54px;
  border-radius: 999px;
  background: #0b2d63;
  color: #ffffff;
  font-size: 15px;
  font-weight: 800;
  box-shadow: 0 10px 20px rgba(0, 37, 77, 0.2);
}

.primary-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-inline {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.auth-divider {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}

.auth-divider::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  border-top: 1px solid rgba(187, 197, 211, 0.38);
}

.auth-divider span {
  position: relative;
  padding: 0 12px;
  background: rgba(248, 251, 255, 0.94);
  font-size: 10px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: #8d97aa;
}

.secondary-btn,
.secondary-action-btn {
  min-height: 50px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(193, 202, 215, 0.45);
  color: #143463;
  font-size: 14px;
  font-weight: 800;
}

.secondary-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.medical-id-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f1b555, #77cfe0);
  box-shadow: 0 0 0 3px rgba(241, 181, 85, 0.12);
}

.wechat-box {
  padding: 18px;
  border-radius: 24px;
  background: rgba(241, 245, 250, 0.9);
}

.wechat-card {
  position: relative;
  padding: 22px 20px 18px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(193, 203, 217, 0.42);
  text-align: center;
}

.wechat-card strong {
  display: block;
  font-size: 16px;
  color: #10284f;
  margin-bottom: 8px;
}

.wechat-card p {
  margin: 0;
  font-size: 12px;
  color: #7a879a;
  line-height: 1.7;
}

.qr-core {
  position: relative;
  width: 112px;
  height: 112px;
  margin: 6px auto 16px;
  border-radius: 18px;
  background:
    linear-gradient(90deg, rgba(91, 118, 168, 0.08) 1px, transparent 1px),
    linear-gradient(rgba(91, 118, 168, 0.08) 1px, transparent 1px),
    radial-gradient(circle at center, rgba(66, 128, 198, 0.12), rgba(66, 128, 198, 0.03));
  background-size: 14px 14px, 14px 14px, 100% 100%;
  overflow: hidden;
}

.qr-scan-line {
  position: absolute;
  inset: 0 auto auto 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(32, 102, 176, 0.7), transparent);
  animation: qrScan 2.5s linear infinite;
}

.qr-corner {
  position: absolute;
  width: 22px;
  height: 22px;
  border-color: rgba(31, 102, 175, 0.5);
  border-style: solid;
}

.qr-corner-tl { top: 14px; left: 14px; border-width: 2px 0 0 2px; }
.qr-corner-tr { top: 14px; right: 14px; border-width: 2px 2px 0 0; }
.qr-corner-bl { bottom: 14px; left: 14px; border-width: 0 0 2px 2px; }
.qr-corner-br { bottom: 14px; right: 14px; border-width: 0 2px 2px 0; }

.mode-tips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.mode-tips span {
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(232, 238, 245, 0.85);
  color: #687891;
  font-size: 12px;
  font-weight: 700;
}

.error-text {
  margin: 0 0 0 2px;
  color: #b33644;
  font-size: 12px;
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.32);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.75s linear infinite;
}

.spinner--dark {
  border-color: rgba(13, 45, 99, 0.18);
  border-top-color: #0d2d63;
}

.footer-row {
  padding-top: 6px;
  border-top: 1px solid rgba(187, 197, 211, 0.32);
  color: #7c889c;
  font-size: 12px;
}

.fade-slide-enter-active,
.fade-slide-leave-active,
.panel-fade-enter-active,
.panel-fade-leave-active {
  transition: all 0.22s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.panel-fade-enter-from,
.panel-fade-leave-to {
  opacity: 0;
  transform: translateX(6px);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes qrScan {
  0% { top: 0; opacity: 0; }
  5% { opacity: 1; }
  95% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

@media (max-width: 480px) {
  .method-switch {
    grid-template-columns: 1fr;
    border-radius: 22px;
  }

  .password-row,
  .code-row,
  .helper-row,
  .footer-row {
    flex-direction: column;
    align-items: stretch;
  }

  .code-btn,
  .icon-btn {
    width: 100%;
  }
}
</style>
