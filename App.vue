<script>
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'

export default {
  globalData: { systemInfo: null },
  onLaunch() {
    // 获取系统信息
    uni.getSystemInfo({ success: (res) => { this.globalData.systemInfo = res } })

    // 恢复登录态 — 对齐 Web 端 localStorage
    const token = uni.getStorageSync('medical_auth_token')
    const userInfo = uni.getStorageSync('medical_user_info')
    if (token && userInfo) {
      try {
        const parsed = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo
        useUserStore().login(parsed, token)
      } catch (e) {
        uni.removeStorageSync('medical_auth_token')
        uni.removeStorageSync('medical_user_info')
      }
    }

    // 应用主题
    const themeStore = useThemeStore()
    themeStore.applyTheme(themeStore.currentTheme)
  },
  onShow() {
    const token = uni.getStorageSync('medical_auth_token')
    const pages = getCurrentPages()
    const route = pages[pages.length - 1]?.route || ''
    const publicPages = ['pages/index/index', 'pages/login/login', 'pages/register/register']
    if (!token && !publicPages.includes(route)) {
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/global.scss';
</style>
