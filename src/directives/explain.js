import { TERMS } from '../utils/terms.js'

const style = `
.explain-tooltip{position:absolute;z-index:9999;max-width:280px;background:#2c3e50;color:#fff;padding:.55rem .8rem;border-radius:6px;box-shadow:0 6px 20px rgba(0,0,0,.25);font-size:.85rem;line-height:1.5}
.explain-arrow{position:absolute;width:0;height:0;border-left:6px solid transparent;border-right:6px solid transparent;border-top:6px solid #2c3e50}
`

function ensureStyle() {
  if (!document.getElementById('explain-tooltip-style')) {
    const el = document.createElement('style')
    el.id = 'explain-tooltip-style'
    el.textContent = style
    document.head.appendChild(el)
  }
}

function showTooltip(el, text) {
  ensureStyle()
  const tip = document.createElement('div')
  tip.className = 'explain-tooltip'
  tip.textContent = text
  const arrow = document.createElement('div')
  arrow.className = 'explain-arrow'
  tip.appendChild(arrow)
  document.body.appendChild(tip)
  const rect = el.getBoundingClientRect()
  tip.style.left = `${rect.left + window.scrollX}px`
  tip.style.top = `${rect.top + window.scrollY - tip.offsetHeight - 10}px`
  arrow.style.left = '12px'
  arrow.style.top = `${tip.offsetHeight}px`
  el.__explainTip = tip
}

function hideTooltip(el) {
  const tip = el.__explainTip
  if (tip && tip.parentNode) tip.parentNode.removeChild(tip)
  el.__explainTip = null
}

export default {
  mounted(el, binding) {
    const key = String(binding.value || '').trim()
    const text = TERMS[key] || key
    el.style.textDecoration = 'underline dotted'
    el.style.cursor = 'help'
    const onEnter = () => showTooltip(el, text)
    const onLeave = () => hideTooltip(el)
    el.addEventListener('mouseenter', onEnter)
    el.addEventListener('mouseleave', onLeave)
    el.__explainHandlers = { onEnter, onLeave }
  },
  unmounted(el) {
    const h = el.__explainHandlers
    if (h) {
      el.removeEventListener('mouseenter', h.onEnter)
      el.removeEventListener('mouseleave', h.onLeave)
    }
    hideTooltip(el)
  }
}
