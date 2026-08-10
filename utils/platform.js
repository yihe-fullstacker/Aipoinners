// utils/platform.js — 平台判断工具
// 被需要条件编译逻辑的组件调用

export function isApp() {
  // #ifdef APP-PLUS
  return true
  // #endif
  // #ifndef APP-PLUS
  return false
  // #endif
}

export function isH5() {
  // #ifdef H5
  return true
  // #endif
  // #ifndef H5
  return false
  // #endif
}

// 获取安全区域高度
export function getSafeAreaInsets() {
  const systemInfo = uni.getSystemInfoSync()
  return {
    top: systemInfo.statusBarHeight || 0,
    bottom: systemInfo.safeAreaInsets?.bottom || 0
  }
}

// 获取系统信息
export function getSystemInfo() {
  return uni.getSystemInfoSync()
}
