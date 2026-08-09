/**
 * browser-use-server.js - Persistent browser automation server
 * 
 * Starts a Playwright-driven Chrome browser and keeps it running.
 * Accepts commands via stdin (JSON) and returns results via stdout (JSON).
 * 
 * Usage:
 *   node scripts/browser-use-server.js              # Start server (headless)
 *   node scripts/browser-use-server.js --headed      # Start server (visible)
 * 
 * Commands (stdin JSON lines):
 *   {"action":"navigate","url":"https://example.com"}
 *   {"action":"screenshot"}
 *   {"action":"getContent"}
 *   {"action":"getVisibleText","selector":"body"}
 *   {"action":"click","selector":"#button"}
 *   {"action":"type","selector":"#input","value":"text"}
 *   {"action":"evaluate","script":"document.title"}
 *   {"action":"getText","selector":"h1"}
 *   {"action":"getLinks"}
 *   {"action":"scroll","x":0,"y":500}
 *   {"action":"close"}
 *   {"action":"status"}
 */

const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');
const readline = require('readline');

const STATE_FILE = path.join(__dirname, '..', 'browser-state.json');
const SCREENSHOTS_DIR = path.join(__dirname, '..', 'screenshots');

const HEADED = process.argv.includes('--headed');

if (!fs.existsSync(SCREENSHOTS_DIR)) {
  fs.mkdirSync(SCREENSHOTS_DIR, { recursive: true });
}

let browser = null;
let context = null;
let page = null;
let currentUrl = '';

function respond(data) {
  console.log(JSON.stringify(data));
}

function error(msg) {
  respond({ error: msg });
}

async function initBrowser() {
  try {
    browser = await chromium.launch({
      headless: !HEADED,
      args: [
        '--disable-blink-features=AutomationControlled',
        '--no-sandbox',
        '--disable-web-security',
        '--disable-features=IsolateOrigins,site-per-process',
      ],
    });

    context = await browser.newContext({
      viewport: { width: 1280, height: 800 },
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    });

    page = await context.newPage();
    respond({ ok: true, message: 'Browser launched', headed: HEADED });
  } catch (err) {
    error('Failed to launch browser: ' + err.message);
  }
}

async function handleCommand(cmd) {
  if (!browser && cmd.action !== 'close' && cmd.action !== 'status') {
    await initBrowser();
    if (!browser) return;
  }

  try {
    switch (cmd.action) {

      case 'navigate':
        if (!cmd.url) return error('url required');
        await page.goto(cmd.url, { waitUntil: 'networkidle', timeout: 30000 });
        currentUrl = page.url();
        respond({ ok: true, url: currentUrl, title: await page.title() });
        break;

      case 'getContent': {
        const html = await page.content();
        respond({ ok: true, content: html, url: page.url(), title: await page.title() });
        break;
      }

      case 'getVisibleText': {
        const sel = cmd.selector || 'body';
        const els = await page.$$(sel);
        const texts = [];
        for (const el of els) {
          const text = await el.textContent();
          if (text) texts.push(text.trim());
        }
        respond({ ok: true, texts, url: page.url(), title: await page.title() });
        break;
      }

      case 'click':
        if (!cmd.selector) return error('selector required');
        await page.click(cmd.selector, { timeout: 10000 });
        respond({ ok: true, url: page.url() });
        break;

      case 'type':
        if (!cmd.selector || cmd.value === undefined) return error('selector and value required');
        await page.fill(cmd.selector, cmd.value);
        respond({ ok: true });
        break;

      case 'evaluate':
        if (!cmd.script) return error('script required');
        const result = await page.evaluate(cmd.script);
        respond({ ok: true, result });
        break;

      case 'getText': {
        if (!cmd.selector) return error('selector required');
        const el = await page.$(cmd.selector);
        if (!el) return error('Element not found: ' + cmd.selector);
        const text = await el.textContent();
        respond({ ok: true, text: text ? text.trim() : '' });
        break;
      }

      case 'getAttribute': {
        if (!cmd.selector || !cmd.name) return error('selector and name required');
        const attrEl = await page.$(cmd.selector);
        if (!attrEl) return error('Element not found: ' + cmd.selector);
        const attrVal = await attrEl.getAttribute(cmd.name);
        respond({ ok: true, value: attrVal });
        break;
      }

      case 'scroll':
        await page.evaluate(({ x, y }) => window.scrollTo(x || 0, y || 0), cmd);
        respond({ ok: true });
        break;

      case 'waitForSelector': {
        if (!cmd.selector) return error('selector required');
        await page.waitForSelector(cmd.selector, { timeout: cmd.timeout || 10000 });
        respond({ ok: true });
        break;
      }

      case 'getLinks': {
        const links = await page.evaluate(() => {
          return Array.from(document.querySelectorAll('a[href]')).map(a => ({
            text: a.textContent.trim(),
            href: a.href,
          })).filter(l => l.href && !l.href.startsWith('javascript:'));
        });
        respond({ ok: true, links, url: page.url(), title: await page.title() });
        break;
      }

      case 'screenshot': {
        const filename = `screenshot-${Date.now()}.png`;
        const filepath = path.join(SCREENSHOTS_DIR, filename);
        await page.screenshot({ path: filepath, fullPage: cmd.fullPage !== false });
        respond({ ok: true, path: filepath, url: page.url() });
        break;
      }

      case 'download':
        if (!cmd.url) return error('url required');
        await page.goto(cmd.url, { waitUntil: 'networkidle', timeout: 30000 });
        currentUrl = page.url();
        const htmlContent = await page.content();
        respond({ ok: true, url: currentUrl, title: await page.title(), content: htmlContent });
        break;

      case 'status':
        respond({
          ok: true,
          browserRunning: !!browser,
          currentUrl,
          pid: process.pid,
        });
        break;

      case 'close':
        if (browser) {
          await browser.close();
          browser = null; context = null; page = null;
        }
        respond({ ok: true, message: 'Browser closed' });
        process.exit(0);
        break;

      default:
        error('Unknown action: ' + cmd.action);
    }
  } catch (err) {
    error(err.message);
  }
}

async function main() {
  respond({ ok: true, message: 'Browser Use Server ready', version: '1.0.0' });

  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false,
  });

  for await (const line of rl) {
    try {
      const cmd = JSON.parse(line.trim());
      await handleCommand(cmd);
    } catch (err) {
      error('Invalid JSON: ' + err.message);
    }
  }
}

main().catch(err => {
  error('Fatal: ' + err.message);
  process.exit(1);
});
