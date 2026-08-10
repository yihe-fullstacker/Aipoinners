<!-- pages/login/login.vue — 登录页 -->
<template>
  <view class="page-shell">
    <NavBar title="登录" :showBack="true" />
    <view class="login-body">
      <view class="login-header">
        <text class="login-title">欢迎回来</text>
        <text class="login-sub">登录影智医疗诊断平台</text>
      </view>
      <LoginForm @success="onLoginSuccess" @swReg="goRegister" />
    </view>
  </view>
</template>

<script setup>
import NavBar from '@/components/nav-bar.vue'
import LoginForm from '@/components/login-form.vue'

const onLoginSuccess = (res) => {
  const role = res?.role || 'PATIENT'
  if (role === 'DOCTOR') {
    setTimeout(() => uni.navigateTo({ url: '/subpkg-doctor/workspace/index' }), 600)
  }
}
const goRegister = () => uni.navigateTo({ url: '/pages/register/register' })
</script>

<style scoped>
.page-shell { min-height:100vh; background:#040b16; }
.login-body { padding: 40rpx 36rpx; }
.login-header { margin-bottom: 48rpx; display:flex; flex-direction:column; gap:12rpx; }
.login-title { font-size: 48rpx; font-weight: 800; color: #f8fafc; }
.login-sub { font-size: 26rpx; color: rgba(200,218,242,0.65); }
</style>
