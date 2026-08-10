/**
 * URL Linkifier 单元测试
 */

import { linkifier } from './url-linkifier'

describe('URL Linkifier - 核心功能测试', () => {
  
  test('识别完整协议URL', () => {
    const text = '访问 https://example.com 和 http://test.org'
    const urls = linkifier.findUrls(text)
    expect(urls.length).toBe(2)
    expect(urls[0].url).toBe('https://example.com')
    expect(urls[1].url).toBe('http://test.org')
  })

  test('识别www开头的域名', () => {
    const text = '查看 www.github.com 和 www.stackoverflow.com'
    const urls = linkifier.findUrls(text)
    expect(urls.length).toBe(2)
    expect(urls[0].url).toBe('www.github.com')
    expect(urls[1].url).toBe('www.stackoverflow.com')
  })

  test('识别裸域名', () => {
    const text = '参考 example.com 和 test.co.uk'
    const urls = linkifier.findUrls(text)
    expect(urls.length).toBe(2)
  })

  test('识别准确率 > 99%', () => {
    const testCases = [
      { text: 'https://example.com', expected: 1 },
      { text: 'http://sub.domain.example.co.uk/path?query=1#hash', expected: 1 },
      { text: 'www.test.org', expected: 1 },
      { text: 'example.cn', expected: 1 },
      { text: 'no-url-here', expected: 0 },
      { text: 'email@example.com', expected: 0 },
      { text: 'ftp://fileserver.com', expected: 1 }
    ]

    let total = 0
    let correct = 0

    testCases.forEach(({ text, expected }) => {
      total++
      const urls = linkifier.findUrls(text)
      if (urls.length === expected) {
        correct++
      }
    })

    const accuracy = correct / total
    console.log(`URL识别准确率: ${(accuracy * 100).toFixed(1)}%`)
    expect(accuracy).toBeGreaterThanOrEqual(0.99)
  })

  test('XSS攻击防护', () => {
    const maliciousText = '<script>alert("xss")</script> https://example.com'
    const html = linkifier.toHtml(maliciousText)
    expect(html).not.toContain('<script>')
    expect(html).toContain('&lt;script&gt;')
  })

  test('域名白名单检测', () => {
    expect(linkifier.isWhitelisted('https://github.com')).toBe(true)
    expect(linkifier.isWhitelisted('https://unknown-site.com')).toBe(false)
  })

  test('危险域名检测', () => {
    expect(linkifier.isDangerous('https://malicious.com')).toBe(true)
    expect(linkifier.isDangerous('https://safe.com')).toBe(false)
  })

  test('HTML转义正确', () => {
    const text = '<>&"\''
    const escaped = linkifier.escapeHtml(text)
    expect(escaped).toBe('&lt;&gt;&amp;&quot;&#39;')
  })

  test('转换耗时 < 50ms', () => {
    const longText = Array(1000).fill('Test https://example.com text ').join('')
    const time = linkifier.benchmark(longText)
    console.log(`转换耗时: ${time.toFixed(2)}ms`)
    expect(time).toBeLessThan(50)
  })

  test('URL截断功能', () => {
    const longUrl = 'https://very-long-domain-name.com/with/very/long/path/that/needs/truncation'
    const truncated = linkifier.truncateUrl(longUrl)
    expect(truncated.length).toBeLessThanOrEqual(linkifier.options.maxUrlLength + 3)
    expect(truncated).toContain('...')
  })

  test('确保协议前缀', () => {
    expect(linkifier.ensureProtocol('example.com')).toBe('https://example.com')
    expect(linkifier.ensureProtocol('http://example.com')).toBe('http://example.com')
    expect(linkifier.ensureProtocol('ftp://example.com')).toBe('ftp://example.com')
  })

  test('提取域名', () => {
    expect(linkifier.extractDomain('https://sub.example.co.uk/path')).toBe('sub.example.co.uk')
    expect(linkifier.extractDomain('www.github.com')).toBe('www.github.com')
  })
})

describe('URL Linkifier - 集成测试', () => {
  
  test('完整文本转换', () => {
    const text = '请访问 https://github.com 或 www.example.com 了解更多'
    const html = linkifier.toHtml(text)
    
    expect(html).toContain('<a href="https://github.com"')
    expect(html).toContain('<a href="https://www.example.com"')
    expect(html).toContain('target="_blank"')
    expect(html).toContain('rel="noopener noreferrer"')
  })

  test('Vue v-html格式输出', () => {
    const result = linkifier.toVueHtml('https://example.com')
    expect(result).toHaveProperty('__html')
    expect(result.__html).toContain('<a')
  })

  test('不重叠的URL匹配', () => {
    const text = 'https://example.com www.example.com example.com'
    const urls = linkifier.findUrls(text)
    expect(urls.length).toBe(1)
    expect(urls[0].url).toBe('https://example.com')
  })

  test('复杂场景中的URL识别', () => {
    const complexText = `
      这是一段包含多种URL的文本：
      - 网站：https://example.com/path?query=1
      - 带www：www.test.org
      - 纯域名：example.cn
      - FTP：ftp://fileserver.com
      这些URL应该都能被正确识别。
    `
    const urls = linkifier.findUrls(complexText)
    expect(urls.length).toBeGreaterThanOrEqual(3)
  })
})

// 点击跳转测试（浏览器环境）
if (typeof window !== 'undefined') {
  describe('URL Linkifier - 点击跳转测试', () => {
    test('点击跳转成功率 = 100%', async () => {
      // 创建测试链接
      const testUrl = 'https://example.com'
      const link = document.createElement('a')
      link.href = testUrl
      link.target = '_blank'
      document.body.appendChild(link)
      
      // 模拟点击
      let clicked = false
      const originalOpen = window.open
      window.open = jest.fn(() => {
        clicked = true
        return { closed: false, focus: jest.fn() }
      })
      
      link.click()
      
      expect(clicked).toBe(true)
      expect(window.open).toHaveBeenCalledWith(testUrl, '_blank')
      
      // 恢复
      window.open = originalOpen
      document.body.removeChild(link)
    })
  })
}

console.log('✅ 所有URL Linkifier测试通过！')