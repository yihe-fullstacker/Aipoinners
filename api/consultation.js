// api/consultation.js — 健康咨询API，被 subpkg-patient/consultation/detail.vue 调用
// 映射 Web 端 healthConsultationAPI: POST/GET /api/health-consultation/*
import http from './http'

export const consultationAPI = {
  reserve: (data) => http.post('/health-consultation/reserve', data),
  query: (phone) => http.get(`/health-consultation/query/${phone}`)
}
