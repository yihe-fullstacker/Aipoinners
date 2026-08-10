// stores/theme.js — 主题状态管理 (对齐 Web 端 Vuex theme)
// 被 App.vue, components/theme-toggle.vue 等组件调用
import { defineStore } from 'pinia'

const THEME_KEY = 'medical_app_theme'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    theme: uni.getStorageSync(THEME_KEY) || 'dark'
  }),
  getters: {
    currentTheme: (state) => state.theme,
    isDark: (state) => state.theme === 'dark',
    isMedical: (state) => state.theme === 'medical-ai'
  },
  actions: {
    setTheme(theme) {
      this.theme = theme
      uni.setStorageSync(THEME_KEY, theme)
      this.applyTheme(theme)
    },
    toggleTheme() {
      const next = this.theme === 'dark' ? 'medical-ai' : 'dark'
      this.setTheme(next)
    },
    applyTheme(theme) {
      // UniApp 通过全局 CSS 变量 + class 切换主题
      // 对齐 Web 端 document.documentElement.setAttribute('data-theme', ...)
      const themeClass = theme === 'medical-ai' ? 'theme-medical' : 'theme-dark'
      // 通过 globalData 传递给各页面
      const app = getApp()
      if (app) {
        app.globalData.themeClass = themeClass
        app.globalData.theme = theme
      }
    }
  }
})
