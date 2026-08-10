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

    <div class="card-copy">
      <h3>创建平台账户</h3>
      <p>保留原有注册逻辑，当前注册方式已切换为手机号绑定。</p>
    </div>

    <form class="register-form" @submit.prevent="handleRegister">
      <div class="field-grid">
        <div class="field-group">
          <label for="light-register-username">用户名</label>
          <div class="field-wrap" :class="{ error: errors.username }">
            <input
              id="light-register-username"
              v-model="form.username"
              type="text"
              placeholder="用户名或工号"
              required
            />
          </div>
          <p v-if="errors.username" class="error-text">{{ errors.username }}</p>
        </div>

        <div class="field-group">
          <label for="light-register-phone">手机号</label>
          <div class="field-wrap" :class="{ error: errors.phone }">
            <input
              id="light-register-phone"
              v-model="form.phone"
              type="tel"
              placeholder="请输入中国大陆手机号"
              required
            />
          </div>
          <p v-if="errors.phone" class="error-text">{{ errors.phone }}</p>
        </div>
      </div>

      <div class="field-group">
        <label>选择身份</label>
        <div class="role-grid">
          <button
            v-for="item in roleOptions"
            :key="item.value"
            type="button"
            :class="['role-card', { active: form.role === item.value }]"
            @click="form.role = item.value"
          >
            <div class="role-icon" :class="item.value === 'PATIENT' ? 'patient' : 'doctor'">
              <svg v-if="item.value === 'PATIENT'" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
              <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
            </div>
            <div class="role-copy">
              <strong>{{ item.title }}</strong>
              <span>{{ item.helper }}</span>
            </div>
            <span v-if="form.role === item.value" class="role-check">✓</span>
          </button>
        </div>
        <p v-if="errors.role" class="error-text">{{ errors.role }}</p>
      </div>

      <div class="field-group">
        <label for="light-register-password">密码</label>
        <div class="password-row">
          <div class="field-wrap" :class="{ error: errors.password }">
            <input
              id="light-register-password"
              v-model="form.password"
              :type="showPwd ? 'text' : 'password'"
              placeholder="请输入至少 6 位密码"
              required
            />
          </div>
          <button type="button" class="icon-btn" @click="showPwd = !showPwd">
            <svg v-if="!showPwd" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
          </button>
        </div>
        <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
        <div class="strength-row">
          <div class="strength-track">
            <div :class="['strength-fill', strengthClass]" :style="{ width: `${strengthPercent}%` }"></div>
          </div>
          <span :class="['strength-label', strengthClass]">{{ strengthLabel }}</span>
        </div>
      </div>

      <div class="field-group">
        <label for="light-register-confirm-password">确认密码</label>
        <div class="password-row">
          <div class="field-wrap" :class="{ error: errors.confirmPassword }">
            <input
              id="light-register-confirm-password"
              v-model="form.confirmPassword"
              :type="showConfirmPwd ? 'text' : 'password'"
              placeholder="请再次输入密码"
              required
            />
          </div>
          <button type="button" class="icon-btn" @click="showConfirmPwd = !showConfirmPwd">
            <svg v-if="!showConfirmPwd" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
          </button>
        </div>
        <p v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</p>
      </div>

      <button class="primary-btn" type="submit" :disabled="loading">
        <span v-if="loading" class="btn-inline"><span class="spinner"></span>注册中...</span>
        <span v-else class="btn-inline">
          <span>注册账户</span>
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12,5 19,12 12,19"/></svg>
        </span>
      </button>
    </form>

    <div class="footer-row">
      <span>已有账户？</span>
      <button type="button" class="link-btn strong" @click="switchToLogin">返回登录</button>
    </div>
  </div>
</template>

<script>
import { computed, reactive, ref } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'LightRegisterCard',
  emits: ['switch-to-login'],
  setup(props, { emit }) {
    const store = useStore()
    const form = reactive({
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      role: 'PATIENT'
    })
    const loading = ref(false)
    const errors = reactive({})
    const showSuccess = ref(false)
    const showError = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const showPwd = ref(false)
    const showConfirmPwd = ref(false)

    const roleOptions = [
      { value: 'PATIENT', title: '患者用户', helper: '用于健康问答、报告查看与诊疗咨询' },
      { value: 'DOCTOR', title: '医生用户', helper: '用于专业诊断协作、复核与工作台管理' }
    ]

    const clearMessages = () => {
      showSuccess.value = false
      showError.value = false
      successMessage.value = ''
      errorMessage.value = ''
    }

    const validate = () => {
      Object.keys(errors).forEach((key) => delete errors[key])
      if (!form.username || !form.username.trim()) errors.username = '请输入用户名'
      if (!form.phone || !/^1[3-9]\d{9}$/.test(form.phone)) errors.phone = '请输入有效手机号'
      if (!form.password || form.password.length < 6) errors.password = '密码长度至少为 6 位'
      if (!form.confirmPassword) errors.confirmPassword = '请再次输入密码'
      else if (form.password !== form.confirmPassword) errors.confirmPassword = '两次输入的密码不一致'
      if (!form.role) errors.role = '请选择身份'
      return Object.keys(errors).length === 0
    }

    const switchToLogin = () => {
      emit('switch-to-login')
    }

    const handleRegister = async () => {
      clearMessages()
      if (!validate()) return

      loading.value = true
      try {
        const success = await store.dispatch('register', {
          username: form.username.trim(),
          phone: form.phone.trim(),
          password: form.password,
          role: form.role
        })
        if (success) {
          successMessage.value = '注册成功，请使用新账户登录。'
          showSuccess.value = true
          setTimeout(() => {
            switchToLogin()
          }, 1000)
        } else {
          errorMessage.value = '注册失败，请稍后重试。'
          showError.value = true
        }
      } catch (err) {
        errorMessage.value = err.response?.data?.message || err.message || '注册失败'
        showError.value = true
      } finally {
        loading.value = false
      }
    }

    const strengthPercent = computed(() => Math.min(100, (form.password?.length || 0) * 12))
    const strengthLabel = computed(() => {
      const len = form.password?.length || 0
      if (len >= 10) return '强'
      if (len >= 6) return '中'
      return '弱'
    })
    const strengthClass = computed(() => {
      const len = form.password?.length || 0
      if (len >= 10) return 'strong'
      if (len >= 6) return 'medium'
      return 'weak'
    })

    return {
      form,
      loading,
      errors,
      showSuccess,
      showError,
      successMessage,
      errorMessage,
      showPwd,
      showConfirmPwd,
      roleOptions,
      handleRegister,
      switchToLogin,
      strengthPercent,
      strengthLabel,
      strengthClass
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

.register-form {
  display: grid;
  gap: 16px;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
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

.role-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.role-card {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px;
  border: 1px solid rgba(195, 205, 219, 0.38);
  border-radius: 20px;
  background: rgba(248, 251, 255, 0.74);
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.role-card.active {
  border-color: rgba(58, 137, 200, 0.34);
  background: rgba(236, 248, 251, 0.94);
  box-shadow: inset 0 0 0 1px rgba(109, 205, 201, 0.22);
}

.role-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.role-icon.patient {
  background: rgba(84, 178, 211, 0.12);
  color: #2c89ad;
}

.role-icon.doctor {
  background: rgba(91, 196, 165, 0.12);
  color: #289579;
}

.role-copy strong {
  display: block;
  font-size: 14px;
  color: #13284c;
  margin-bottom: 4px;
}

.role-copy span {
  display: block;
  font-size: 12px;
  color: #7b8799;
  line-height: 1.65;
}

.role-check {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #56d2d1;
  color: #083259;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
}

.password-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.password-row .field-wrap {
  flex: 1;
}

.icon-btn {
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 14px;
  background: rgba(232, 237, 244, 0.96);
  color: #56657b;
  cursor: pointer;
}

.strength-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.strength-track {
  flex: 1;
  height: 6px;
  border-radius: 999px;
  background: rgba(220, 227, 236, 0.92);
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: inherit;
  transition: width 0.25s ease;
}

.strength-fill.weak { background: linear-gradient(90deg, #fb7185, #ef4444); }
.strength-fill.medium { background: linear-gradient(90deg, #f59e0b, #f97316); }
.strength-fill.strong { background: linear-gradient(90deg, #49bdd8, #56d2d1); }

.strength-label {
  min-width: 22px;
  font-size: 12px;
  font-weight: 800;
  text-align: right;
}

.strength-label.weak { color: #ef4444; }
.strength-label.medium { color: #f59e0b; }
.strength-label.strong { color: #2498ae; }

.primary-btn {
  height: 54px;
  border: none;
  border-radius: 999px;
  background: #0b2d63;
  color: #ffffff;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;
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

.footer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 6px;
  border-top: 1px solid rgba(187, 197, 211, 0.32);
  color: #7c889c;
  font-size: 12px;
}

.link-btn {
  border: none;
  background: transparent;
  padding: 0;
  font-size: 12px;
  color: #5b7aa4;
  cursor: pointer;
}

.link-btn.strong {
  font-weight: 700;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.22s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 520px) {
  .field-grid,
  .role-grid {
    grid-template-columns: 1fr;
  }

  .password-row,
  .footer-row {
    flex-direction: column;
    align-items: stretch;
  }

  .icon-btn {
    width: 100%;
  }
}
</style>


