// api/image.js — 图片资源API
import http from './http'

const BASE_URL = 'http://47.108.52.206:8082/api'

export const imageAPI = {
  getImageUrl: (imageId) => `${BASE_URL}/images/${imageId}`,
  getImage: (imageId) => http.get(`/images/${imageId}`)
}
