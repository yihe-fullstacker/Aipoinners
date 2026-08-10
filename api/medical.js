// api/medical.js — 医学影像API
// 直接映射 Web 端 medicalAPI + aiAPI 方法签名
import http from './http'

export const medicalAPI = {
  analyze: (filePath, complaint) => {
    const formData = complaint ? { complaint } : {}
    return http.upload('/medical/analyze', filePath, 'image', formData)
  },
  analyzeDeepseek: (filePath) => http.upload('/medical/analyze-deepseek', filePath),
  getReports: () => http.get('/medical/reports'),
  getReportDetail: (id) => http.get(`/medical/reports/${id}`),
  getReportHeatmap: (id) => http.get(`/medical/reports/${id}/heatmap`),
  deleteReport: (id) => http.delete(`/medical/reports/${id}`),
  // AI Python 服务 (通过 Java 后端代理)
  analyzePython: (filePath) => http.upload('/medical/python/analyze', filePath),
  analyzeV2: (filePath) => http.upload('/medical/python/analyze-v2', filePath),
  analyzeBase64: (imageBase64) => http.post('/medical/python/analyze-base64', { image: imageBase64 }),
  evaluateModel: (imagesBase64, labels) =>
    http.post('/medical/python/evaluate-model', { images: imagesBase64, labels }),
  compareModels: (imagesBase64, labels) =>
    http.post('/medical/python/compare-models', { images: imagesBase64, labels }),
  explainPrediction: (imageBase64) =>
    http.post('/medical/python/explain-prediction', { image: imageBase64 }),
  getUncertainCases: (imagesBase64, topK = 10) =>
    http.post('/medical/python/uncertain-cases', { images: imagesBase64, top_k: topK }),
  generateHeatmap: (filePath) => http.upload('/medical/python/heatmap', filePath, 'image')
}
