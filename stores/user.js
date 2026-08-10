// stores/user.js — 用户状态管理 (替代 Vuex store/index.js)
// 被 App.vue, pages/login/login.vue, components/nav-bar.vue 等组件调用
import { defineStore } from 'pinia'

const TOKEN_KEY = 'medical_auth_token'
const USER_KEY = 'medical_user_info'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: !!uni.getStorageSync(TOKEN_KEY),
    user: null
  }),
  getters: {
    userName: (state) => state.user?.username || state.user?.fullName || '用户',
    userRole: (state) => state.user?.role || 'PATIENT',
    userInitial: (state) => (state.user?.fullName || state.user?.username || 'U').slice(0, 1).toUpperCase(),
    isDoctor: (state) => state.user?.role === 'DOCTOR',
    userId: (state) => state.user?.id || null
  },
  actions: {
    login(userData, token) {
      this.isLoggedIn = true
      this.user = {
        id: userData.userId || userData.id,
        username: userData.username,
        email: userData.email,
        phone: userData.phone,
        fullName: userData.fullName,
        role: userData.role
      }
      uni.setStorageSync(TOKEN_KEY, token)
      uni.setStorageSync(USER_KEY, JSON.stringify(this.user))
    },
    logout() {
      this.isLoggedIn = false
      this.user = null
      uni.removeStorageSync(TOKEN_KEY)
      uni.removeStorageSync(USER_KEY)
      uni.reLaunch({ url: '/pages/login/login' })
    },
    setUser(userData) {
      this.user = { ...this.user, ...userData }
      const token = uni.getStorageSync(TOKEN_KEY)
      if (token) this.isLoggedIn = true
      uni.setStorageSync(USER_KEY, JSON.stringify(this.user))
    }
  }
})
