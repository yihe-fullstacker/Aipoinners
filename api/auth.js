// api/auth.js — 认证API
// 直接映射 Web 端 authAPI 的方法签名
import http from './http'

export const authAPI = {
  login: (credentials) => http.post('/auth/login', credentials, 12000),
  register: (userData) => http.post('/auth/register', userData, 12000),
  getCaptcha: () => http.get('/auth/captcha'),
  loginByPhone: (phone, code) =>
    http.post('/auth/login/phone', { phoneNumber: phone, verifyCode: code })
}
