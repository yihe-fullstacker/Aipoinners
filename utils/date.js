// utils/date.js — 日期格式化，被 report/list.vue + history/index.vue + workspace/index.vue 调用
export function formatDate(date) {
  if (!date) return ''
  try { const d = new Date(date); if (isNaN(d.getTime())) return String(date)
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}` }
  catch { return String(date) }
}
export function formatRelative(date) {
  if (!date) return ''
  const d = new Date(date); if (isNaN(d.getTime())) return String(date)
  const diff = Date.now() - d.getTime()
  if (diff < 6e4) return '刚刚'
  if (diff < 36e5) return `${Math.floor(diff/6e4)}分钟前`
  if (diff < 864e5) return `${Math.floor(diff/36e5)}小时前`
  return formatDate(date)
}
