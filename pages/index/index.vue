<!-- pages/index/index.vue — 启动页，对齐 Landing.vue 品牌入口 -->
<template>
  <view class="splash">
    <view class="splash-bg">
      <view class="bg-orb o1"></view><view class="bg-orb o2"></view>
    </view>
    <view class="splash-body">
      <view class="brand-mark"><text class="bm-icon">+</text></view>
      <text class="brand-name">影智医疗</text>
      <text class="brand-sub">AI + 医生双核驱动</text>
      <text class="brand-desc">医学影像智能诊断服务平台</text>
    </view>
    <view class="splash-actions">
      <button class="btn-start" @tap="handleEnter">进入平台</button>
      <view class="splash-links">
        <text class="link" @tap="goLogin">登录</text>
        <text class="link" @tap="goRegister">注册账户</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onMounted } from 'vue'
import { hasToken } from '@/utils/auth'

onMounted(() => {
  if (hasToken()) {
    setTimeout(() => uni.switchTab({ url: '/pages/home/home' }), 800)
  }
})
const handleEnter = () => {
  if (hasToken()) uni.switchTab({ url: '/pages/home/home' })
  else uni.navigateTo({ url: '/pages/login/login' })
}
const goLogin = () => uni.navigateTo({ url: '/pages/login/login' })
const goRegister = () => uni.navigateTo({ url: '/pages/register/register' })
</script>

<style scoped>
.splash {
  min-height: 100vh; background: radial-gradient(ellipse 80% 60% at 70% 30%, rgba(14,165,233,0.08) 0%, transparent 60%),
    radial-gradient(ellipse 60% 50% at 20% 70%, rgba(16,185,129,0.06) 0%, transparent 55%), #040b16;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60rpx 40rpx; position: relative; overflow: hidden;
}
.splash-bg { position: absolute; inset: 0; pointer-events: none; }
.bg-orb { position: absolute; border-radius: 50%; filter: blur(80px); }
.o1 { width: 600rpx; height: 600rpx; top: -120rpx; left: -100rpx; background: radial-gradient(circle, rgba(15,118,110,0.16) 0%, transparent 70%); }
.o2 { width: 700rpx; height: 700rpx; top: -80rpx; right: -150rpx; background: radial-gradient(circle, rgba(37,99,235,0.18) 0%, transparent 70%); }
.splash-body { position: relative; z-index:1; display: flex; flex-direction: column; align-items: center; gap: 18rpx; margin-bottom: 60rpx; }
.brand-mark { width: 120rpx; height: 120rpx; border-radius: 30rpx; background: linear-gradient(145deg, rgba(75,211,255,0.22), rgba(68,241,196,0.14)); border:1px solid rgba(112,214,255,0.28); display: flex; align-items: center; justify-content: center; }
.bm-icon { font-size: 60rpx; color: #70d6ff; font-weight: 300; }
.brand-name { font-size: 56rpx; font-weight: 800; color: #f8fafc; letter-spacing: 4rpx; margin-top: 20rpx; }
.brand-sub { font-size: 28rpx; background: linear-gradient(135deg, #38bdf8, #34d399); -webkit-background-clip: text; -webkit-text-fill-color: transparent; font-weight: 600; }
.brand-desc { font-size: 24rpx; color: rgba(226,232,240,0.55); }
.splash-actions { position: relative; z-index:1; display: flex; flex-direction: column; align-items: center; gap: 28rpx; width: 100%; max-width: 500rpx; }
.btn-start { width: 100%; height: 96rpx; border-radius: 20rpx; background: linear-gradient(135deg, #0ea5e9, #0284c7); color: #fff; font-size: 32rpx; font-weight: 700; border: none; box-shadow: 0 16rpx 40rpx rgba(14,165,233,0.35); }
.splash-links { display: flex; gap: 40rpx; }
.link { font-size: 28rpx; color: rgba(226,232,240,0.55); }
</style>
