<template>
  <div :class="embedded ? 'auth-embed' : 'auth-page'">
    <div v-if="!embedded" class="page-backdrop" aria-hidden="true">
      <span class="backdrop-orb orb-one"></span>
      <span class="backdrop-orb orb-two"></span>
      <span class="backdrop-orb orb-three"></span>
      <span class="backdrop-grid"></span>
    </div>

    <div :class="['auth-shell', { 'auth-shell--embedded': embedded }]">
      <section v-if="!embedded" class="scene-panel">
        <div class="scene-logo">
          <div class="logo-mark">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
          </div>
          <span>影智医疗</span>
          <span class="scene-badge">Patient &amp; Doctor Onboarding</span>
        </div>

        <h1>注册流程保持简洁<br />同时具备医疗平台需要的专业感</h1>
        <p class="scene-desc">当前注册入口已统一改为手机号绑定，便于后续短信验证、身份识别和账号安全控制。</p>

        <div class="flow-card">
          <div class="flow-label">
            <span class="flow-dot"></span>
            Onboarding Flow
          </div>
          <div class="flow-steps">
            <div class="flow-step-item"><div class="fsi-num active">01</div><div class="fsi-line"></div></div>
            <div class="flow-step-item"><div class="fsi-num active">02</div><div class="fsi-line"></div></div>
            <div class="flow-step-item"><div class="fsi-num">03</div></div>
          </div>
          <div class="flow-labels-row">
            <span>基础信息</span>
            <span>选择角色</span>
            <span>完成注册</span>
          </div>
        </div>

        <div class="role-preview-grid">
          <article v-for="item in roleOptions" :key="item.value" class="role-preview-card">
            <div class="rpc-icon" :class="item.value === 'PATIENT' ? 'rpc-patient' : 'rpc-doctor'">
              <svg v-if="item.value === 'PATIENT'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
            </div>
            <strong>{{ item.title }}</strong>
            <span>{{ item.helper }}</span>
          </article>
        </div>

        <div class="benefit-list">
          <article v-for="item in registerBenefits" :key="item.title" class="benefit-card">
            <div class="benefit-num">{{ item.icon }}</div>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </article>
        </div>
      </section>

      <section :class="['auth-panel', { 'auth-panel--embedded': embedded }]">
        <div class="panel-top">
          <div>
            <p class="panel-eyebrow">创建账户</p>
            <h2>注册医疗 AI 平台账号</h2>
          </div>
          <span class="panel-chip"><span class="chip-dot"></span>手机号注册已开启</span>
        </div>

        <transition name="msg-slide">
          <div v-if="showSuccess" class="message success-message">
            <span class="message-icon"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20,6 9,17 4,12"/></svg></span>
            <span>{{ successMessage }}</span>
          </div>
        </transition>
        <transition name="msg-slide">
          <div v-if="showError" class="message error-message-box">
            <span class="message-icon"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg></span>
            <span>{{ errorMessage }}</span>
          </div>
        </transition>

        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-grid">
            <div class="form-group">
              <label for="register-username">用户名</label>
              <div class="input-wrap" :class="{ 'has-error': errors.username }">
                <svg class="input-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                <input id="register-username" v-model="form.username" type="text" placeholder="用户名或工号" required :class="{ error: errors.username }" />
              </div>
              <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
            </div>

            <div class="form-group">
              <label for="register-phone">手机号</label>
              <div class="input-wrap" :class="{ 'has-error': errors.phone }">
                <svg class="input-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.8 19.8 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.08 4.18 2 2 0 0 1 4.06 2h3a2 2 0 0 1 2 1.72c.12.9.33 1.78.62 2.62a2 2 0 0 1-.45 2.11L8 9.68a16 16 0 0 0 6.32 6.32l1.23-1.23a2 2 0 0 1 2.11-.45c.84.29 1.72.5 2.62.62A2 2 0 0 1 22 16.92z"/></svg>
                <input id="register-phone" v-model="form.phone" type="tel" placeholder="请输入中国大陆手机号" required :class="{ error: errors.phone }" />
              </div>
              <span v-if="errors.phone" class="error-message">{{ errors.phone }}</span>
            </div>
          </div>

          <div class="form-group">
            <label>选择身份</label>
            <div class="role-selector">
              <button
                v-for="item in roleOptions"
                :key="item.value"
                type="button"
                :class="['role-option', { active: form.role === item.value }]"
                @click="form.role = item.value"
              >
                <div class="ro-icon" :class="item.value === 'PATIENT' ? 'ro-patient' : 'ro-doctor'">
                  <svg v-if="item.value === 'PATIENT'" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                  <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
                </div>
                <div>
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.helper }}</span>
                </div>
                <div class="ro-check" v-if="form.role === item.value">
                  <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20,6 9,17 4,12"/></svg>
                </div>
              </button>
            </div>
            <span v-if="errors.role" class="error-message">{{ errors.role }}</span>
          </div>

          <div class="form-group">
            <label for="register-password">密码</label>
            <div class="input-row">
              <div class="input-wrap" :class="{ 'has-error': errors.password }" style="flex:1">
                <svg class="input-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                <input id="register-password" v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="请输入至少 6 位密码" required :class="{ error: errors.password }" />
              </div>
              <button type="button" class="action-btn" @click="showPwd = !showPwd">
                <svg v-if="!showPwd" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            <div class="strength-row">
              <div class="strength-track">
                <div :class="['strength-fill', strengthClass]" :style="{ width: `${strengthPercent}%` }"></div>
              </div>
              <span class="strength-label" :class="strengthClass">{{ strengthLabel }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="register-confirm-password">确认密码</label>
            <div class="input-row">
              <div class="input-wrap" :class="{ 'has-error': errors.confirmPassword }" style="flex:1">
                <svg class="input-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22,4 12,14.01 9,11.01"/></svg>
                <input id="register-confirm-password" v-model="form.confirmPassword" :type="showConfirmPwd ? 'text' : 'password'" placeholder="请再次输入密码" required :class="{ error: errors.confirmPassword }" />
              </div>
              <button type="button" class="action-btn" @click="showConfirmPwd = !showConfirmPwd">
                <svg v-if="!showConfirmPwd" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </button>
            </div>
            <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
          </div>

          <button type="submit" class="primary-btn" :disabled="loading">
            <span v-if="loading" class="btn-inner"><span class="spinner"></span>注册中...</span>
            <span v-else class="btn-inner">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg>
              <span v-explain="'注册账户'">注册账户</span>
            </span>
          </button>
        </form>

        <div class="panel-footer">
          <div class="footer-note">
            已有账号？<a href="#" class="text-link" @click.prevent="switchToLogin">返回登录 →</a>
          </div>
          <p class="footer-tip">注册后即可继续进入影像分析、健康问答与医生协同能力模块。</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { computed, reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import explain from '../directives/explain.js'

export default {
  name: 'Register',
  props: { embedded: { type: Boolean, default: false } },
  directives: { explain },
  emits: ['switch-to-login'],
  setup(props, { emit }) {
    const form = reactive({ username: '', phone: '', password: '', confirmPassword: '', role: 'PATIENT' })
    const loading = ref(false)
    const errors = reactive({})
    const showSuccess = ref(false)
    const showError = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const showPwd = ref(false)
    const showConfirmPwd = ref(false)
    const store = useStore()
    const router = useRouter()

    const roleOptions = [
      { value: 'PATIENT', title: '患者用户', helper: '用于健康问答、报告查看与诊疗咨询' },
      { value: 'DOCTOR', title: '医生用户', helper: '用于专业诊断协作、复核与工作台管理' }
    ]

    const registerBenefits = [
      { icon: '01', title: '流程更清晰', desc: '关键信息集中在一个表单中，减少真实用户注册时的理解成本。' },
      { icon: '02', title: '手机号统一绑定', desc: '注册后可直接用于短信验证码登录与身份校验，减少多套认证入口。' },
      { icon: '03', title: '兼容患者与医生', desc: '支持不同角色快速接入，为后续业务扩展与协同留出空间。' }
    ]

    const clearMessages = () => {
      showSuccess.value = false
      showError.value = false
      successMessage.value = ''
      errorMessage.value = ''
    }

    const validate = () => {
      Object.keys(errors).forEach((key) => delete errors[key])
      if (!form.username || !form.username.trim()) errors.username = '用户名不能为空'
      if (!form.phone || !/^1[3-9]\d{9}$/.test(form.phone)) errors.phone = '请输入有效手机号'
      if (!form.password || form.password.length < 6) errors.password = '密码长度至少为 6 位'
      if (!form.confirmPassword) errors.confirmPassword = '请再次输入密码'
      else if (form.password !== form.confirmPassword) errors.confirmPassword = '两次输入的密码不一致'
      if (!form.role) errors.role = '请选择身份'
      return Object.keys(errors).length === 0
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
          setTimeout(() => switchToLogin(), 1000)
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

    const switchToLogin = async (e) => {
      if (e) e.preventDefault()
      if (props.embedded) {
        emit('switch-to-login')
        return
      }
      await router.push({ path: '/login' })
    }

    const strengthPercent = computed(() => Math.min(100, (form.password?.length || 0) * 12))
    const strengthLabel = computed(() => {
      const len = form.password?.length || 0
      if (len >= 10) return '强密码'
      if (len >= 6) return '中等强度'
      return '建议增强'
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
      handleRegister,
      switchToLogin,
      showPwd,
      showConfirmPwd,
      roleOptions,
      registerBenefits,
      strengthPercent,
      strengthLabel,
      strengthClass
    }
  }
}
</script>
<style scoped>
* { box-sizing: border-box; }

.auth-page, .auth-embed {
  --bg: var(--ui-bg-page);
  --text: var(--ui-text-primary);
  --muted: var(--ui-text-muted);
  --accent: var(--ui-brand-primary);
  --accent-alt: var(--ui-brand-accent);
  --danger: var(--ui-danger);
  --surface-card: var(--ui-surface-card);
  --surface-card-strong: var(--ui-surface-card-strong);
  --surface-chip: var(--ui-surface-chip);
  --soft-line: var(--ui-border-soft);
  --line: var(--ui-border-accent);
  position: relative; width: 100%;
  font-family: var(--ui-font-family);
}

.auth-page {
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  display: flex; align-items: flex-start; justify-content: center;
  padding: clamp(8px, 1.6vw, 16px) var(--app-shell-gutter, 32px) 20px; overflow: hidden;
  background: var(--ui-bg-auth-gradient);
}

.auth-embed { display: flex; min-height: 100%; }

.page-backdrop { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }
.backdrop-orb { position: absolute; border-radius: 50%; }
.orb-one { width: 440px; height: 440px; top: -120px; left: -120px; background: radial-gradient(circle, rgba(84,165,255,0.2) 0%, transparent 65%); filter: blur(60px); animation: drift 18s ease-in-out infinite; }
.orb-two { width: 520px; height: 520px; right: -150px; bottom: -120px; background: radial-gradient(circle, rgba(72,239,203,0.15) 0%, transparent 65%); filter: blur(70px); animation: drift 22s ease-in-out infinite -6s; }
.orb-three { width: 300px; height: 300px; left: 50%; top: 25%; background: radial-gradient(circle, rgba(100,80,255,0.07) 0%, transparent 65%); filter: blur(50px); animation: drift 14s ease-in-out infinite -10s; }
.backdrop-grid { position: absolute; inset: 0; background-image: linear-gradient(var(--ui-grid-color-strong) 1px, transparent 1px), linear-gradient(90deg, var(--ui-grid-color-strong) 1px, transparent 1px); background-size: 56px 56px; mask-image: linear-gradient(180deg, rgba(0,0,0,0.8) 0%, transparent 75%); }

.auth-shell { position: relative; z-index: 1; width: 100%; max-width: var(--app-shell-max-width, 1280px); min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 40px), 820px); display: grid; grid-template-columns: minmax(0,1fr) minmax(0,0.95fr); gap: 16px; }
.auth-shell--embedded { max-width: none; display: block; min-height: 100%; }

.scene-panel, .auth-panel { position: relative; overflow: hidden; border-radius: var(--ui-radius-xl); border: 1px solid var(--soft-line); backdrop-filter: blur(var(--ui-glass-blur)); box-shadow: var(--ui-shadow-panel-strong), inset 0 1px 0 rgba(255,255,255,0.08); }

.scene-panel {
  min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 40px), 820px);
  padding: 28px; display: flex; flex-direction: column;
  background: radial-gradient(ellipse 55% 35% at 85% 0%, rgba(var(--ui-brand-accent-rgb),0.09) 0%, transparent 50%), radial-gradient(ellipse 45% 45% at 5% 95%, rgba(var(--ui-brand-primary-rgb),0.07) 0%, transparent 50%), linear-gradient(180deg, var(--ui-surface-panel), var(--ui-surface-panel-strong));
}

/* SCENE LOGO */
.scene-logo { display: flex; align-items: center; gap: 10px; margin-bottom: 22px; }
.logo-mark { width: 36px; height: 36px; background: linear-gradient(135deg, rgba(var(--ui-brand-primary-rgb),0.18), rgba(var(--ui-brand-accent-rgb),0.13)); border: 1px solid rgba(var(--ui-brand-primary-rgb),0.22); border-radius: 11px; display: flex; align-items: center; justify-content: center; color: var(--accent); box-shadow: 0 0 16px rgba(var(--ui-brand-primary-rgb),0.1); }
.scene-logo > span:nth-child(2) { font-size: 15px; font-weight: 700; color: var(--text); letter-spacing: 0.04em; }
.scene-badge { margin-left: auto; display: inline-flex; align-items: center; padding: 4px 11px; border-radius: 999px; background: var(--surface-chip); border: 1px solid var(--soft-line); color: var(--ui-text-secondary); font-size: 10px; font-weight: 700; letter-spacing: 0.06em; text-transform: uppercase; }

.scene-panel h1 { margin: 0 0 12px; font-size: clamp(24px,2.6vw,38px); font-weight: 800; line-height: 1.15; letter-spacing: -0.03em; color: var(--text); }
.scene-panel h1 em { font-style: normal; background: linear-gradient(135deg, var(--accent), var(--accent-alt)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.scene-desc { font-size: 14px; color: var(--muted); line-height: 1.8; margin-bottom: 20px; }

/* FLOW CARD */
.flow-card { background: var(--surface-card); border: 1px solid var(--soft-line); border-radius: var(--ui-radius-md); padding: 16px; margin-bottom: 14px; }
.flow-label { display: flex; align-items: center; gap: 7px; font-size: 11px; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: rgba(200,220,248,0.7); margin-bottom: 14px; }
.flow-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--accent-alt); box-shadow: 0 0 7px rgba(72,239,203,0.8); animation: blink 2s ease-in-out infinite; }

.flow-steps { display: flex; align-items: center; margin-bottom: 8px; }
.flow-step-item { display: flex; align-items: center; flex: 1; }
.fsi-num { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: rgba(200,218,245,0.5); flex-shrink: 0; transition: all 0.3s; }
.fsi-num.active { background: linear-gradient(135deg, rgba(126,220,255,0.2), rgba(72,239,203,0.15)); border-color: rgba(126,220,255,0.3); color: var(--text); box-shadow: 0 0 16px rgba(126,220,255,0.15); }
.fsi-line { flex: 1; height: 1px; background: linear-gradient(90deg, rgba(126,220,255,0.2), rgba(255,255,255,0.05)); margin: 0 6px; }

.flow-labels-row { display: flex; gap: 0; }
.flow-labels-row span { flex: 1; text-align: center; font-size: 11px; color: var(--muted); }

/* ROLE PREVIEW */
.role-preview-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 14px; }
.role-preview-card { padding: 14px; border-radius: var(--ui-radius-sm); background: var(--surface-card); border: 1px solid var(--soft-line); transition: all 0.2s; }
.role-preview-card:hover { border-color: var(--line); background: var(--surface-card-strong); }
.rpc-icon { width: 32px; height: 32px; border-radius: 10px; display: flex; align-items: center; justify-content: center; margin-bottom: 10px; }
.rpc-patient { background: rgba(126,220,255,0.1); color: var(--accent); border: 1px solid rgba(126,220,255,0.18); }
.rpc-doctor  { background: rgba(72,239,203,0.1); color: var(--accent-alt); border: 1px solid rgba(72,239,203,0.18); }
.role-preview-card strong { display: block; color: var(--text); font-size: 13px; font-weight: 700; margin-bottom: 4px; }
.role-preview-card span { color: var(--muted); font-size: 11px; line-height: 1.6; }

/* BENEFITS */
.benefit-list { display: flex; flex-direction: column; gap: 10px; margin-top: auto; }
.benefit-card { display: flex; align-items: flex-start; gap: 12px; padding: 12px 14px; border-radius: var(--ui-radius-sm); background: var(--surface-card); border: 1px solid var(--soft-line); transition: all 0.2s; }
.benefit-card:hover { border-color: var(--line); }
.benefit-num { width: 28px; height: 28px; border-radius: 8px; background: rgba(126,220,255,0.08); border: 1px solid rgba(126,220,255,0.16); display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 800; color: var(--accent); flex-shrink: 0; font-family: monospace; }
.benefit-card strong { display: block; color: var(--text); font-size: 13px; font-weight: 700; margin-bottom: 3px; }
.benefit-card p { margin: 0; color: var(--muted); font-size: 12px; line-height: 1.65; }

/* AUTH PANEL */
.auth-panel { display: flex; flex-direction: column; min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 40px), 820px); padding: 28px; background: radial-gradient(ellipse 55% 28% at 90% 0%, rgba(var(--ui-brand-primary-rgb),0.07) 0%, transparent 50%), linear-gradient(180deg, var(--ui-surface-panel), var(--ui-surface-panel-strong)); }
.auth-panel--embedded { min-height: 100%; padding: 0; border: none; border-radius: 0; box-shadow: none; background: transparent; }
.auth-panel--embedded::after { display: none; }

.panel-top { display: flex; align-items: flex-start; justify-content: space-between; gap: 14px; margin-bottom: 20px; }
.panel-eyebrow { margin: 0 0 8px; font-size: 11px; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; color: rgba(var(--ui-brand-primary-rgb),0.8); }
.panel-top h2 { margin: 0; font-size: 24px; font-weight: 800; color: var(--text); line-height: 1.2; letter-spacing: -0.02em; }
.panel-chip { display: inline-flex; align-items: center; gap: 7px; flex-shrink: 0; padding: 7px 14px; border-radius: 999px; background: rgba(var(--ui-brand-accent-rgb),0.08); border: 1px solid rgba(var(--ui-brand-accent-rgb),0.16); color: var(--ui-text-secondary); font-size: 11px; font-weight: 700; }
.chip-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--accent-alt); box-shadow: 0 0 7px rgba(72,239,203,0.8); animation: blink 2s ease-in-out infinite; }

/* MESSAGES */
.message { display: flex; align-items: center; gap: 10px; padding: 11px 14px; border-radius: 14px; font-size: 13px; margin-bottom: 12px; }
.message-icon { width: 22px; height: 22px; flex-shrink: 0; display: inline-flex; align-items: center; justify-content: center; border-radius: 50%; }
.success-message { color: #c9ffe4; background: rgba(22,163,74,0.12); border: 1px solid rgba(74,222,128,0.2); }
.success-message .message-icon { background: #16a34a; }
.error-message-box { color: #ffd8d8; background: rgba(220,38,38,0.12); border: 1px solid rgba(248,113,113,0.2); }
.error-message-box .message-icon { background: #dc2626; }

/* FORM */
.register-form { display: grid; gap: 14px; }
.form-grid { display: grid; grid-template-columns: repeat(2,minmax(0,1fr)); gap: 12px; }
.form-group { display: grid; gap: 8px; }
.form-group label { color: var(--ui-text-secondary); font-size: 13px; font-weight: 600; }

.input-wrap { position: relative; display: flex; align-items: center; }
.input-icon { position: absolute; left: 14px; color: var(--ui-text-subtle); pointer-events: none; flex-shrink: 0; }
.input-wrap input { width: 100%; height: 46px; padding: 0 14px 0 40px; border-radius: var(--ui-radius-sm); border: 1px solid var(--soft-line); background: var(--surface-card); color: var(--text); font-size: 14px; transition: all 0.2s ease; font-family: inherit; }
.input-wrap input::placeholder { color: var(--ui-text-subtle); }
.input-wrap input:focus { outline: none; border-color: rgba(126,220,255,0.36); background: rgba(126,220,255,0.04); box-shadow: 0 0 0 3px rgba(126,220,255,0.07); }
.input-wrap input.error { border-color: rgba(248,113,113,0.45); }
.input-wrap.has-error input { border-color: rgba(248,113,113,0.45); }

.input-row { display: flex; gap: 10px; align-items: center; justify-content: center; }

/* ROLE SELECTOR */
.role-selector { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.role-option { display: flex; align-items: center; gap: 12px; padding: 14px; border-radius: var(--ui-radius-sm); border: 1px solid var(--soft-line); background: var(--surface-card); text-align: left; cursor: pointer; transition: all 0.22s ease; position: relative; }
.role-option:hover { border-color: var(--line); background: var(--surface-card-strong); }
.role-option.active { background: linear-gradient(180deg, rgba(var(--ui-brand-primary-rgb),0.1), rgba(var(--ui-brand-accent-rgb),0.06)); border-color: rgba(var(--ui-brand-primary-rgb),0.26); box-shadow: 0 8px 24px rgba(var(--ui-brand-primary-rgb),0.08); }
.ro-icon { width: 34px; height: 34px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.ro-patient { background: rgba(126,220,255,0.1); color: var(--accent); border: 1px solid rgba(126,220,255,0.2); }
.ro-doctor  { background: rgba(72,239,203,0.1); color: var(--accent-alt); border: 1px solid rgba(72,239,203,0.2); }
.role-option strong { display: block; color: var(--text); font-size: 13px; font-weight: 700; margin-bottom: 2px; }
.role-option span { color: var(--muted); font-size: 11px; line-height: 1.5; }
.ro-check { position: absolute; top: 10px; right: 10px; width: 20px; height: 20px; background: linear-gradient(135deg, var(--accent), var(--accent-alt)); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #06111d; }

/* STRENGTH */
.strength-row { display: flex; align-items: center; gap: 10px; }
.strength-track { flex: 1; height: 5px; border-radius: 999px; background: var(--surface-chip); overflow: hidden; }
.strength-fill { height: 100%; border-radius: inherit; transition: width 0.3s ease; }
.strength-fill.weak   { background: linear-gradient(90deg, #fb7185, #ef4444); }
.strength-fill.medium { background: linear-gradient(90deg, #f59e0b, #f97316); }
.strength-fill.strong { background: linear-gradient(90deg, var(--accent), var(--accent-alt)); }
.strength-label { min-width: 64px; font-size: 12px; font-weight: 700; text-align: right; }
.strength-label.weak   { display: none; }
.strength-label.medium { color: #fbbf24; }
.strength-label.strong { color: var(--accent); }

/* BUTTONS */
.action-btn { min-width: 46px; height: 46px; padding: 0 12px; border-radius: var(--ui-radius-sm); border: none; background: var(--surface-chip); color: var(--ui-text-secondary); cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; }
.action-btn:hover { background: var(--surface-card-strong); }

.primary-btn { width: 100%; height: 50px; border: none; border-radius: var(--radius-button); background: linear-gradient(135deg, var(--accent), var(--accent-alt)); color: var(--ui-text-contrast); font-size: 15px; font-weight: 700; cursor: pointer; box-shadow: var(--ui-shadow-accent); transition: all 0.25s; position: relative; overflow: hidden; }
.primary-btn::before { content: ''; position: absolute; inset: 0; background: linear-gradient(135deg, rgba(255,255,255,0.15), transparent 50%); pointer-events: none; }
.primary-btn:hover:not(:disabled) { box-shadow: 0 16px 36px rgba(126,220,255,0.3); transform: translateY(-2px); }
.primary-btn:disabled { opacity: 0.55; cursor: not-allowed; box-shadow: none; transform: none; }
.btn-inner { display: inline-flex; align-items: center; gap: 8px; }

.text-link { color: var(--accent); font-weight: 700; text-decoration: none; transition: opacity 0.2s; }
.text-link:hover { opacity: 0.8; }
.error-message { color: var(--danger); font-size: 12px; }

/* FOOTER */
.panel-footer { margin-top: auto; padding-top: 18px; border-top: 1px solid var(--soft-line); }
.footer-note { color: var(--ui-text-secondary); font-size: 14px; margin-bottom: 8px; }
.footer-tip { margin: 0; color: var(--ui-text-subtle); font-size: 12px; line-height: 1.65; }

.spinner { width: 15px; height: 15px; border: 2px solid rgba(6,17,29,0.25); border-top-color: #06111d; border-radius: 50%; animation: spin 0.75s linear infinite; }

/* TRANSITIONS */
.msg-slide-enter-active, .msg-slide-leave-active { transition: all 0.25s ease; }
.msg-slide-enter-from, .msg-slide-leave-to { opacity: 0; transform: translateY(-6px); }

/* KEYFRAMES */
@keyframes drift { 0%,100% { transform: translate3d(0,0,0) scale(1); } 50% { transform: translate3d(20px,-16px,0) scale(1.08); } }
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0.35; } }

/* RESPONSIVE */
@media (max-width: 1080px) { .auth-shell { grid-template-columns: 1fr; min-height: auto; } .scene-panel, .auth-panel { min-height: auto; } }
@media (max-width: 760px) { .auth-page { padding: 8px var(--app-shell-gutter, 32px) 14px; } .scene-panel, .auth-panel { padding: 22px; border-radius: var(--ui-radius-lg); } .panel-top, .input-row { flex-direction: column; align-items: stretch; } .form-grid, .role-selector { grid-template-columns: 1fr; } }
@media (max-height: 900px) { .auth-shell { min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 24px), 760px); } .scene-panel, .auth-panel { min-height: min(calc(100vh - var(--app-top-nav-height, 68px) - 24px), 760px); padding: 20px; } .scene-panel h1 { font-size: clamp(20px, 2.4vw, 32px); } }
</style>



