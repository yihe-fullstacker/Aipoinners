// api/doctor.js — 医生端API，被 subpkg-doctor/workspace/index.vue 调用
// 映射 Web 端 DoctorController: GET /api/doctor/queue 等
import http from './http'

export const doctorAPI = {
  getQueue: () => http.get('/doctor/queue'),
  submitDiagnosis: (consultationId, doctorId, diagnosisText) =>
    http.post('/doctor/diagnose', null, { params: { consultationId, doctorId, diagnosisText } }),
  reAnalyze: (id) => http.post(`/doctor/consultations/${id}/ai`),
  assistantChat: (message) => http.post('/doctor/assistant/chat', null, { params: { message } })
}
