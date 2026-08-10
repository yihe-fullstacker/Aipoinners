// utils/auth.js — Token CRUD，被 App.vue + stores/user.js + api/http.js 调用
const TOKEN_KEY = 'medical_auth_token'

export function getToken() { return uni.getStorageSync(TOKEN_KEY) }
export function setToken(t) { uni.setStorageSync(TOKEN_KEY, t) }
export function removeToken() { uni.removeStorageSync(TOKEN_KEY) }
export function hasToken() { return !!uni.getStorageSync(TOKEN_KEY) }

export function saveUserInfo(info) {
  uni.setStorageSync('medical_user_info', JSON.stringify(info))
}
export function getUserInfo() {
  try { const r = uni.getStorageSync('medical_user_info'); return r ? JSON.parse(r) : null }
  catch { return null }
}
