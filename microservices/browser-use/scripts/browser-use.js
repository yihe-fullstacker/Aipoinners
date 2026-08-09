/**
 * browser-use.js - CLI client for browser automation
 * 
 * Usage:
 *   node scripts/browser-use.js start [--headed]    Start server in background
 *   node scripts/browser-use.js stop                 Stop server
 *   node scripts/browser-use.js navigate <url>       Navigate to URL
 *   node scripts/browser-use.js getContent           Get page HTML
 *   node scripts/browser-use.js text [selector]      Get visible text
 *   node scripts/browser-use.js click <selector>     Click element
 *   node scripts/browser-use.js type <sel> <value>   Type text
 *   node scripts/browser-use.js eval <script>        Run JS
 *   node scripts/browser-use.js screenshot           Take screenshot
 *   node scripts/browser-use.js links                Get all links
 *   node scripts/browser-use.js scroll <x> <y>       Scroll page
 *   node scripts/browser-use.js status               Check server status
 *   node scripts/browser-use.js download <url>       Download page content
 */

const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');

const SERVER_SCRIPT = path.join(__dirname, 'browser-use-server.js');
const STATE_FILE = path.join(__dirname, '..', 'browser-state.json');
const CMD_SOCKET = path.join(__dirname, '..', 'cmd-pipe.json');

function readState() {
  try {
    return JSON.parse(fs.readFileSync(STATE_FILE, 'utf-8'));
  } catch { return {}; }
}

function sendToServer(cmd) {
  return new Promise((resolve, reject) => {
    const state = readState();
    if (!state.pid) {
      return reject(new Error('Browser server not running. Start it with: node scripts/browser-use.js start'));
    }

    // Try to check if process is alive
    try {
      process.kill(state.pid, 0);
    } catch {
      return reject(new Error('Browser server process is dead. Restart with: node scripts/browser-use.js start'));
    }

    // Write command to a temp file and read response
    // The server reads from stdin, so we need to use a named pipe approach
    // For simplicity, we write to a command file and the server will read it
    fs.writeFileSync(CMD_SOCKET, JSON.stringify(cmd));

    // Send command to the running server process via stdin
    // Since we can't easily pipe to a running process, we use a spawn approach
    // Each command runs a new process connected to the server

    const child = spawn(process.execPath, [SERVER_SCRIPT, '--command', JSON.stringify(cmd)], {
      stdio: ['pipe', 'pipe', 'pipe'],
    });

    let output = '';
    child.stdout.on('data', (data) => { output += data.toString(); });
    child.stderr.on('data', (data) => { /* ignore */ });

    child.on('close', (code) => {
      try {
        // Parse last line of output (responses are JSON lines)
        const lines = output.trim().split('\n');
        const lastResponse = JSON.parse(lines[lines.length - 1]);
        resolve(lastResponse);
      } catch {
        resolve({ error: 'Failed to parse response', raw: output });
      }
    });

    child.on('error', reject);
  });
}

async function main() {
  const args = process.argv.slice(2);
  const action = args[0];

  if (!action) {
    console.log(`Usage:
  node scripts/browser-use.js start [--headed]    Start browser server
  node scripts/browser-use.js stop                 Stop server
  node scripts/browser-use.js navigate <url>       Navigate to URL
  node scripts/browser-use.js getContent           Get page HTML
  node scripts/browser-use.js text [selector]      Get visible text
  node scripts/browser-use.js click <selector>     Click element
  node scripts/browser-use.js type <sel> <val>     Type text
  node scripts/browser-use.js eval <script>        Run JS
  node scripts/browser-use.js screenshot           Take screenshot
  node scripts/browser-use.js links                Get all links
  node scripts/browser-use.js scroll <x> <y>       Scroll page
  node scripts/browser-use.js status               Check server status
  node scripts/browser-use.js download <url>       Download page content
    `);
    return;
  }

  if (action === 'start') {
    const isHeaded = args.includes('--headed');
    const state = readState();

    // Check if already running
    if (state.pid) {
      try {
        process.kill(state.pid, 0);
        console.log('Browser server is already running (PID: ' + state.pid + ')');
        return;
      } catch { /* stale PID, clean up */ }
    }

    // Start server as a detached child process
    const serverArgs = [SERVER_SCRIPT];
    if (isHeaded) serverArgs.push('--headed');

    const child = spawn(process.execPath, serverArgs, {
      detached: true,
      stdio: ['pipe', 'pipe', 'pipe'],
    });

    // Save state
    const pid = child.pid;
    fs.writeFileSync(STATE_FILE, JSON.stringify({ pid, status: 'starting', startedAt: Date.now() }, null, 2));

    let started = false;
    child.stdout.on('data', (data) => {
      const text = data.toString();
      if (!started && text.includes('"ok":true,"message":"Browser Use Server ready"')) {
        started = true;
        // Update state
        fs.writeFileSync(STATE_FILE, JSON.stringify({ pid, status: 'running', startedAt: Date.now() }, null, 2));
        console.log('Browser server started (PID: ' + pid + ')');
        if (isHeaded) console.log('Browser window is visible on screen');
        console.log('Ready to accept commands');
      }
    });

    child.stderr.on('data', (data) => {
      // forward stderr
    });

    child.on('error', (err) => {
      console.error('Failed to start server:', err.message);
    });

    child.unref();

    // Wait a moment for startup
    await new Promise(r => setTimeout(r, 3000));

    if (!started) {
      console.log('Server starting in background (PID: ' + pid + ')...');
    }
    return;
  }

  if (action === 'stop') {
    const state = readState();
    if (state.pid) {
      try {
        process.kill(state.pid);
        console.log('Browser server stopped (PID: ' + state.pid + ')');
      } catch {
        console.log('Server process already stopped');
      }
      fs.writeFileSync(STATE_FILE, JSON.stringify({ status: 'stopped' }, null, 2));
    } else {
      console.log('No running server found');
    }
    return;
  }

  // --- Command mode (one-shot) ---
  // For one-shot commands, we need to use stdin/stdout connection
  // Since we can't easily communicate with a background process,
  // we use the simpler approach: start a new browser session for each command

  if (action === 'navigate' || action === 'download') {
    const url = args[1];
    if (!url) { console.error('URL required'); process.exit(1); }
    const isDownload = action === 'download';
    const { chromium } = require('playwright');

    const browser = await chromium.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-blink-features=AutomationControlled'],
    });
    const context = await browser.newContext({
      viewport: { width: 1280, height: 800 },
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    });
    const page = await context.newPage();

    try {
      await page.goto(url, { waitUntil: 'networkidle', timeout: 30000 });
      if (isDownload) {
        const content = await page.content();
        console.log(JSON.stringify({
          ok: true,
          url: page.url(),
          title: await page.title(),
          content: content,
        }, null, 2));
      } else {
        console.log(JSON.stringify({
          ok: true,
          url: page.url(),
          title: await page.title(),
        }, null, 2));
      }
    } catch (err) {
      console.error(JSON.stringify({ error: err.message }));
    } finally {
      await browser.close();
    }
    return;
  }

  if (action === 'getContent') {
    const { chromium } = require('playwright');
    const browser = await chromium.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-blink-features=AutomationControlled'],
    });
    const context = await browser.newContext({
      viewport: { width: 1280, height: 800 },
    });
    const page = await context.newPage();
    try {
      const html = await page.content();
      console.log(JSON.stringify({ ok: true, content: html }));
    } catch (err) {
      console.error(JSON.stringify({ error: err.message }));
    } finally {
      await browser.close();
    }
    return;
  }

  if (action === 'text') {
    const { chromium } = require('playwright');
    const browser = await chromium.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-blink-features=AutomationControlled'],
    });
    const context = await browser.newContext();
    const page = await context.newPage();
    try {
      const sel = args[1] || 'body';
      const els = await page.$$(sel);
      const texts = [];
      for (const el of els) {
        const text = await el.textContent();
        if (text) texts.push(text.trim());
      }
      console.log(JSON.stringify({ ok: true, texts }));
    } catch (err) {
      console.error(JSON.stringify({ error: err.message }));
    } finally {
      await browser.close();
    }
    return;
  }

  if (action === 'screenshot') {
    const { chromium } = require('playwright');
    const browser = await chromium.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-blink-features=AutomationControlled'],
    });
    const context = await browser.newContext({
      viewport: { width: 1280, height: 800 },
    });
    const page = await context.newPage();
    try {
      const scDir = path.join(__dirname, '..', 'screenshots');
      if (!fs.existsSync(scDir)) fs.mkdirSync(scDir, { recursive: true });
      const filepath = path.join(scDir, `screenshot-${Date.now()}.png`);
      await page.screenshot({ path: filepath, fullPage: true });
      console.log(JSON.stringify({ ok: true, path: filepath }));
    } catch (err) {
      console.error(JSON.stringify({ error: err.message }));
    } finally {
      await browser.close();
    }
    return;
  }

  if (action === 'links') {
    const { chromium } = require('playwright');
    const browser = await chromium.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-blink-features=AutomationControlled'],
    });
    const context = await browser.newContext();
    const page = await context.newPage();
    try {
      const links = await page.evaluate(() => {
        return Array.from(document.querySelectorAll('a[href]')).map(a => ({
          text: a.textContent.trim(),
          href: a.href,
        })).filter(l => l.href && !l.href.startsWith('javascript:'));
      });
      console.log(JSON.stringify({ ok: true, links }));
    } catch (err) {
      console.error(JSON.stringify({ error: err.message }));
    } finally {
      await browser.close();
    }
    return;
  }

  if (action === 'status') {
    const state = readState();
    console.log(JSON.stringify(state, null, 2));
    return;
  }

  console.error('Unknown action: ' + action);
  process.exit(1);
}

main().catch(err => {
  console.error(JSON.stringify({ error: err.message }));
  process.exit(1);
});
