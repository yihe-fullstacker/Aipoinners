// utils/image.js — 图片URL处理，被 report/ + consultation/ + workspace/ 等组件调用
const BASE = 'http://47.108.52.206:8082'
export function ensureImageUrl(path) {
  if (!path) return null
  if (/^[a-zA-Z]:[\\/]/.test(path) || path.startsWith('\\\\') || path.startsWith('file:')) return null
  if (path.startsWith('http') || path.startsWith('data:')) return path
  return BASE + (path.startsWith('/') ? path : '/' + path)
}
export function extractImageUrl(data) {
  if (!data) return null
  const fields = ['imageUrl','image_url','image','photoUrl','photo_url','pictureUrl','imgUrl']
  for (const f of fields) { if (data[f]) return ensureImageUrl(data[f]) }
  if (data.data) for (const f of fields) { if (data.data[f]) return ensureImageUrl(data.data[f]) }
  return null
}
