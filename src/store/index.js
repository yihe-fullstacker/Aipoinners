//状态管理入口
//作用：管理全局共享数据
//用来全局管理用户的登录状态和信息。简单说就是：统一管理"谁登录了"这个信息，让所有组件都能知道。
import { createStore } from 'vuex'
import { authAPI } from '../api/api'

const TOKEN_KEY = 'medical_auth_token'
const THEME_KEY = 'medical_app_theme'

export default createStore({
  state: {
    isLoggedIn: localStorage.getItem(TOKEN_KEY) !== null,
    user: null,
    currentAnalysis: null,
    theme: localStorage.getItem(THEME_KEY) || 'dark'
  },
  mutations: {
    login(state, { user, token }) {
      state.isLoggedIn = true
      state.user = user
      localStorage.setItem(TOKEN_KEY, token)
    },
    logout(state) {
      state.isLoggedIn = false
      state.user = null
      localStorage.removeItem(TOKEN_KEY)
    },
    setAnalysis(state, analysis) {
      state.currentAnalysis = analysis
    },
    setUser(state, user) {
      state.user = user
      const token = localStorage.getItem(TOKEN_KEY)
      if (token) {
        state.isLoggedIn = true
      }
    },
    setTheme(state, theme) {
      state.theme = theme
      localStorage.setItem(THEME_KEY, theme)
    }
  },
  actions: {
    async login({ commit }, { username, password }) {
      try {
        const response = await authAPI.login({ username, password })
        if (response.data && response.data.token) {
          commit('login', {
            user: {
              id: response.data.userId,
              username: response.data.username,
              email: response.data.email,
              phone: response.data.phone,
              fullName: response.data.fullName,
              role: response.data.role
            },
            token: response.data.token
          })
          return true
        }
        return false
      } catch (error) {
        console.error('Login error:', error)
        throw error
      }
    },
    async register({ commit }, userData) {
      try {
        const response = await authAPI.register(userData)
        if (response.data && response.data.token) {
          commit('login', {
            user: {
              id: response.data.userId,
              username: response.data.username,
              email: response.data.email,
              phone: response.data.phone,
              fullName: response.data.fullName,
              role: response.data.role
            },
            token: response.data.token
          })
          return true
        }
        return false
      } catch (error) {
        console.error('Register error:', error)
        throw error
      }
    },
    logout({ commit }) {
      commit('logout')
    },
    setTheme({ commit }, theme) {
      commit('setTheme', theme)
    }
  }
})
