<template>
  <!-- components/tab-bar.vue — 自定义底部TabBar 5项 -->
  <view class="tab-bar safe-bottom">
    <view class="tab-bar-inner">
      <view
        v-for="(item, idx) in tabs"
        :key="idx"
        class="tab-item"
        :class="{ active: idx === activeIndex }"
        @tap="switchTab(idx, item.path)"
      >
        <text class="tab-icon">{{ item.icon }}</text>
        <text class="tab-text">{{ item.text }}</text>
        <view v-if="idx === activeIndex" class="tab-indicator"></view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const TAB_LIST = [
  { path: '/pages/home/home', text: '首页', icon: '🏠' },
  { path: '/pages/qa/chat', text: 'AI问答', icon: '💬' },
  { path: '/pages/upload/index', text: '上传', icon: '➕' },
  { path: '/pages/report/list', text: '报告', icon: '📋' },
  { path: '/pages/mine/index', text: '我的', icon: '👤' }
]

const tabs = ref(TAB_LIST)
const activeIndex = ref(0)

const findIndex = (path) => {
  return tabs.value.findIndex(t => t.path === path)
}

onMounted(() => {
  const pages = getCurrentPages()
  const route = pages[pages.length - 1]?.route || ''
  const idx = findIndex('/' + route)
  if (idx >= 0) activeIndex.value = idx
})

const switchTab = (idx, path) => {
  activeIndex.value = idx
  uni.switchTab({ url: path })
}
</script>

<style scoped>
.tab-bar {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 1000;
  background: rgba(4, 11, 22, 0.95);
  border-top: 1px solid rgba(125, 211, 252, 0.08);
  backdrop-filter: blur(20px);
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
.tab-bar-inner {
  display: flex; align-items: center; justify-content: space-around;
  height: 100rpx;
}
.tab-item {
  display: flex; flex-direction: column; align-items: center;
  gap: 4rpx; padding: 8rpx 24rpx; position: relative;
  transition: all 0.2s;
}
.tab-item.active { }
.tab-icon { font-size: 36rpx; }
.tab-text { font-size: 20rpx; color: #94A3B8; font-weight: 500; }
.tab-item.active .tab-text { color: #70d6ff; font-weight: 700; }
.tab-indicator {
  position: absolute; top: -4rpx; width: 40rpx; height: 4rpx;
  border-radius: 2rpx; background: linear-gradient(90deg, #5eead4, #38bdf8);
}
.safe-bottom { padding-bottom: constant(safe-area-inset-bottom); padding-bottom: env(safe-area-inset-bottom); }
</style>
