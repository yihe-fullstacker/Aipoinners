// utils/permission.js — 路由权限守卫
// 被 App.vue onShow 和各页面 onLoad 调用
const TOKEN_KEY = 'medical_auth_token'

// 无需登录的页面路由 (对齐 Web 端 PUBLIC_PATHS)
const PUBLIC_ROUTES = [
  'pages/index/index',
  'pages/login/login',
  'pages/register/register'
]

export function checkLogin() {
  const token = uni.getStorageSync(TOKEN_KEY)
  if (!token) {
    uni.reLaunch({ url: '/pages/login/login' })
    return false
  }
  return true
}

export function isLoggedIn() {
  return !!uni.getStorageSync(TOKEN_KEY)
}

export function requireAuth() {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/login' })
    return false
  }
  return true
}

export function isPublicRoute(route) {
  return PUBLIC_ROUTES.includes(route)
}
