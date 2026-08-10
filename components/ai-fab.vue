<template>
  <!-- components/ai-fab.vue — 悬浮AI助手按钮，替代 FloatingAIAssistant -->
  <view class="ai-fab" @tap="openQA">
    <text class="ai-fab-icon">AI</text>
  </view>
</template>

<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const openQA = () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  uni.navigateTo({ url: '/pages/qa/chat' })
}
</script>

<style scoped>
.ai-fab {
  position: fixed; right: 28rpx; bottom: 200rpx; z-index: 900;
  width: 96rpx; height: 96rpx; border-radius: 50%;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  box-shadow: 0 12rpx 36rpx rgba(56, 189, 248, 0.4);
  display: flex; align-items: center; justify-content: center;
}
.ai-fab-icon { font-size: 28rpx; font-weight: 800; color: #032032; }
</style>
