// api/sms.js — 短信验证API
import http from './http'

export const smsAPI = {
  sendSms: (phone) => http.post('/sms/send', { phoneNumber: phone }),
  verifySms: (phone, code) => http.post('/sms/verify', { phoneNumber: phone, verifyCode: code })
}
