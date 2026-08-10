import { TERMS } from '../utils/terms'

const style = `
.explain-tooltip{position:absolute;z-index:9999;max-width:260px;background:#2c3e50;color:#fff;padding:.5rem .75rem;border-radius:6px;box-shadow:0 6px 20px rgba(0,0,0,.2);font-size:.85rem;line-height:1.4}
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

function showTooltip(el: HTMLElement, text: string) {
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
  ;(el as any).__explainTip = tip
}

function hideTooltip(el: HTMLElement) {
  const tip = (el as any).__explainTip as HTMLElement | null
  if (tip && tip.parentNode) tip.parentNode.removeChild(tip)
  ;(el as any).__explainTip = null
}

export default {
  mounted(el: HTMLElement, binding: any) {
    const key = String(binding.value || '').trim()
    const text = TERMS[key] || key
    el.style.textDecoration = 'underline dotted'
    el.style.cursor = 'help'
    const onEnter = () => showTooltip(el, text)
    const onLeave = () => hideTooltip(el)
    el.addEventListener('mouseenter', onEnter)
    el.addEventListener('mouseleave', onLeave)
    ;(el as any).__explainHandlers = { onEnter, onLeave }
  },
  unmounted(el: HTMLElement) {
    const h = (el as any).__explainHandlers
    if (h) {
      el.removeEventListener('mouseenter', h.onEnter)
      el.removeEventListener('mouseleave', h.onLeave)
    }
    hideTooltip(el)
  }
}
