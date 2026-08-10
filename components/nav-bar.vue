<template>
  <!-- components/nav-bar.vue — 自定义导航栏，替代 Web TopNav.vue -->
  <view class="nav-bar" :class="{ 'nav-scrolled': scrolled }" :style="{ paddingTop: statusBar + 'px' }">
    <view class="nav-inner">
      <view class="nav-left">
        <view v-if="showBack" class="nav-back" @tap="handleBack">
          <text class="nav-back-icon">←</text>
        </view>
        <view v-else class="nav-brand" @tap="goHome">
          <text class="nav-logo-text">影智医疗</text>
        </view>
      </view>
      <view class="nav-center">
        <text class="nav-title" v-if="title">{{ title }}</text>
      </view>
      <view class="nav-right">
        <view v-if="showTheme" class="nav-icon-btn" @tap="toggleTheme">
          <text class="nav-icon">{{ themeIcon }}</text>
        </view>
        <view v-if="showUser" class="nav-avatar" @tap="goMine">
          <text class="nav-avatar-text">{{ userStore.userInitial }}</text>
        </view>
      </view>
    </view>
  </view>
  <!-- 占位 -->
  <view :style="{ height: (statusBar + 44) + 'px' }"></view>
</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'

const props = defineProps({
  title: { type: String, default: '' },
  showBack: { type: Boolean, default: false },
  showTheme: { type: Boolean, default: true },
  showUser: { type: Boolean, default: true }
})

const emit = defineEmits(['back'])
const { proxy } = getCurrentInstance()
const userStore = useUserStore()
const themeStore = useThemeStore()
const statusBar = ref(uni.getSystemInfoSync().statusBarHeight || 20)
const scrolled = ref(false)

const themeIcon = computed(() => themeStore.isDark ? '☀️' : '🌙')

const toggleTheme = () => themeStore.toggleTheme()
const handleBack = () => {
  if (props.showBack) {
    emit('back')
    return
  }
  const pages = getCurrentPages()
  if (pages.length > 1) uni.navigateBack()
  else uni.switchTab({ url: '/pages/home/home' })
}
const goHome = () => uni.switchTab({ url: '/pages/home/home' })
const goMine = () => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  uni.navigateTo({ url: '/pages/mine/index' })
}

// 页面滚动监听
if (proxy) {
  uni.$onPageScroll = (e) => { scrolled.value = e.scrollTop > 20 }
}
</script>

<style scoped>
.nav-bar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  background: rgba(4, 11, 22, 0.92);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(125, 211, 252, 0.08);
  transition: background 0.25s;
}
.nav-bar.nav-scrolled { background: rgba(4, 11, 22, 0.98); }
.nav-inner {
  height: 88rpx; display: flex; align-items: center; justify-content: space-between;
  padding: 0 28rpx;
}
.nav-left, .nav-right { display: flex; align-items: center; gap: 20rpx; min-width: 140rpx; }
.nav-right { justify-content: flex-end; }
.nav-center { flex: 1; text-align: center; }
.nav-title { font-size: 32rpx; font-weight: 700; color: #f0f6ff; }
.nav-back { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; }
.nav-back-icon { font-size: 36rpx; color: #70d6ff; }
.nav-logo-text { font-size: 30rpx; font-weight: 800; color: #70d6ff; letter-spacing: 2rpx; }
.nav-icon-btn { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; }
.nav-icon { font-size: 32rpx; }
.nav-avatar {
  width: 64rpx; height: 64rpx; border-radius: 50%;
  background: linear-gradient(135deg, rgba(94,234,212,0.18), rgba(56,189,248,0.28));
  display: flex; align-items: center; justify-content: center;
  border: 1px solid rgba(125,211,252,0.2);
}
.nav-avatar-text { font-size: 24rpx; font-weight: 700; color: #fff; }
</style>
