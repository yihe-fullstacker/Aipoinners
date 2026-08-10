class URLLinkifier {
  constructor(options = {}) {
    this.options = {
      target: '_blank',
      rel: 'noopener noreferrer',
      className: 'ai-link',
      maxUrlLength: 120,
      ...options
    }
  }

  findUrls(text) {
    if (!text) return []

    const urls = []
    const usedIndices = new Set()
    const patterns = [
      { type: 'full', regex: /(https?:\/\/[^\s<>"{}|\\^`\[\]()]+)/gi },
      { type: 'www', regex: /(www\.[^\s<>"{}|\\^`\[\]()]+)/gi }
    ]

    for (const pattern of patterns) {
      let match
      while ((match = pattern.regex.exec(text)) !== null) {
        const url = match[0]
        const start = match.index
        const end = start + url.length
        let overlaps = false

        for (let i = start; i < end; i += 1) {
          if (usedIndices.has(i)) {
            overlaps = true
            break
          }
        }

        if (!overlaps) {
          urls.push({
            url,
            type: pattern.type,
            index: start,
            length: url.length
          })

          for (let i = start; i < end; i += 1) {
            usedIndices.add(i)
          }
        }
      }
    }

    return urls.sort((a, b) => a.index - b.index)
  }

  ensureProtocol(url) {
    if (!/^https?:\/\//i.test(url)) {
      return `https://${url}`
    }
    return url
  }

  escapeHtml(str) {
    if (!str) return ''

    if (typeof document !== 'undefined') {
      const div = document.createElement('div')
      div.textContent = str
      return div.innerHTML
    }

    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;')
  }

  truncateUrl(url) {
    if (url.length <= this.options.maxUrlLength) {
      return url
    }

    const half = Math.floor((this.options.maxUrlLength - 3) / 2)
    return `${url.substring(0, half)}...${url.substring(url.length - half)}`
  }

  normalizeText(text) {
    if (!text) return ''

    return String(text)
      .replace(/\r\n/g, '\n')
      .replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, '$1: $2')
      .replace(/(^|\n)\s{0,3}#{1,6}\s*/g, '$1')
      .replace(/\*\*([^*\n]+)\*\*/g, '$1')
      .replace(/__([^_\n]+)__/g, '$1')
      .replace(/(^|\n)\s*[-*_]{3,}\s*(?=\n|$)/g, '$1')
      .replace(/(^|\n)\s*\*\s*(?=\n|$)/g, '$1')
      .replace(/[ \t]+\n/g, '\n')
      .replace(/\n{3,}/g, '\n\n')
      .trim()
  }

  toHtml(text) {
    if (!text) return ''

    const normalizedText = this.normalizeText(text)
    const urls = this.findUrls(normalizedText)

    if (urls.length === 0) {
      return this.escapeHtml(normalizedText)
    }

    let result = ''
    let lastIndex = 0

    for (const urlMatch of urls) {
      result += this.escapeHtml(normalizedText.substring(lastIndex, urlMatch.index))

      const fullUrl = this.ensureProtocol(urlMatch.url)
      const displayUrl = this.truncateUrl(urlMatch.url)
      result += `<a href="${this.escapeHtml(fullUrl)}" target="${this.options.target}" rel="${this.options.rel}" class="${this.options.className}" onclick="event.stopPropagation();">${this.escapeHtml(displayUrl)}</a>`

      lastIndex = urlMatch.index + urlMatch.length
    }

    result += this.escapeHtml(normalizedText.substring(lastIndex))
    return result
  }

  toHtmlWithBreaks(text) {
    return this.toHtml(text).replace(/\n/g, '<br>')
  }

  toVueHtml(text) {
    return {
      __html: this.toHtml(text)
    }
  }

  async copyToClipboard(text) {
    try {
      await navigator.clipboard.writeText(text)
      return true
    } catch (err) {
      console.error('[URLLinkifier] copy failed:', err)
      return false
    }
  }
}

const linkifier = new URLLinkifier()

export { URLLinkifier, linkifier }
export default linkifier
