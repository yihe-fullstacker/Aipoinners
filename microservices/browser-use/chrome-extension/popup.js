/**
 * Codex Browser Use - Popup Script
 */

let currentTab = null;

document.addEventListener('DOMContentLoaded', async () => {
  await loadTabInfo();
  await loadSettings();
  updateStatus('Connected');
});

async function loadTabInfo() {
  try {
    const response = await chrome.runtime.sendMessage({ action: 'getTabInfo' });
    if (response.error) throw new Error(response.error);
    currentTab = response;
    document.getElementById('tabTitle').textContent = response.title || 'No title';
    document.getElementById('tabUrl').textContent = response.url || 'No URL';
    document.getElementById('statusDot').className = 'status-dot connected';
  } catch (err) {
    document.getElementById('tabTitle').textContent = 'Error: ' + err.message;
    document.getElementById('statusDot').className = 'status-dot disconnected';
  }
}

async function loadSettings() {
  try {
    const settings = await chrome.runtime.sendMessage({ action: 'getSettings' });
    if (settings.autoConnect !== undefined) {
      document.getElementById('autoConnect').checked = settings.autoConnect;
    }
  } catch (e) { /* ignore */ }
}

async function saveSettings() {
  const settings = {
    autoConnect: document.getElementById('autoConnect').checked,
    serverHost: 'localhost',
    serverPort: 9876,
    showNotifications: true,
  };
  await chrome.runtime.sendMessage({ action: 'updateSettings', settings });
}

function showLoading(show) {
  document.getElementById('loading').className = show ? 'loading visible' : 'loading';
}

function showResult(html) {
  const box = document.getElementById('resultBox');
  box.innerHTML = html;
  box.className = 'result-box visible';
}

function updateStatus(text) {
  document.getElementById('statusText').textContent = text;
}

async function getPageContent() {
  showLoading(true);
  try {
    const response = await chrome.runtime.sendMessage({ action: 'getPageContent' });
    if (response.error) throw new Error(response.error);
    const data = response.data;
    showResult(`
      <strong>Title:</strong> ${escapeHtml(data.title)}<br>
      <strong>URL:</strong> ${escapeHtml(data.url)}<br>
      <strong>Links:</strong> ${data.links?.length || 0} found<br>
      <strong>Text length:</strong> ${(data.text?.length || 0).toLocaleString()} chars<br>
      <hr style="border-color:rgba(255,255,255,0.1);margin:8px 0">
      <pre style="font-size:11px;max-height:120px;overflow-y:auto">${escapeHtml(data.text?.substring(0, 1000) || '')}${(data.text?.length || 0) > 1000 ? '...' : ''}</pre>
    `);
    updateStatus('Content loaded');
  } catch (err) {
    showResult(`<span style="color:var(--error)">Error: ${escapeHtml(err.message)}</span>`);
  }
  showLoading(false);
}

async function getVisibleText() {
  showLoading(true);
  try {
    const response = await chrome.runtime.sendMessage({ action: 'getPageContent' });
    if (response.error) throw new Error(response.error);
    const text = response.data?.text || '';
    showResult(`<pre style="font-size:11px;max-height:180px;overflow-y:auto">${escapeHtml(text.substring(0, 2000))}${text.length > 2000 ? '\n...' : ''}</pre>`);
    updateStatus(`${text.length} chars of text`);
  } catch (err) {
    showResult(`<span style="color:var(--error)">Error: ${escapeHtml(err.message)}</span>`);
  }
  showLoading(false);
}

async function getPageLinks() {
  showLoading(true);
  try {
    const response = await chrome.runtime.sendMessage({ action: 'getPageContent' });
    if (response.error) throw new Error(response.error);
    const links = response.data?.links || [];
    let html = `<strong>${links.length} links found:</strong><br>`;
    links.slice(0, 20).forEach((l, i) => {
      html += `<div style="margin:4px 0;font-size:11px;word-break:break-all">
        ${i+1}. <a href="${escapeHtml(l.href)}" target="_blank" style="color:var(--primary)">${escapeHtml(l.text || l.href)}</a>
      </div>`;
    });
    if (links.length > 20) html += `<div style="color:var(--text-dim)">... and ${links.length - 20} more</div>`;
    showResult(html);
    updateStatus(`${links.length} links`);
  } catch (err) {
    showResult(`<span style="color:var(--error)">Error: ${escapeHtml(err.message)}</span>`);
  }
  showLoading(false);
}

async function execPrompt() {
  showLoading(true);
  try {
    const response = await chrome.runtime.sendMessage({ action: 'getPageContent' });
    if (response.error) throw new Error(response.error);
    const data = response.data;
    
    // Copy page summary to clipboard for Codex context
    const summary = `Page: ${data.title}\nURL: ${data.url}\nText: ${(data.text || '').substring(0, 5000)}`;
    await navigator.clipboard.writeText(summary);
    
    showResult(`
      <strong>Page info copied!</strong><br>
      <span style="color:var(--text-dim);font-size:11px">
        Title, URL, and text content (first 5000 chars) copied to clipboard.<br>
        Paste it in Codex to analyze.
      </span>
    `);
    updateStatus('Page info copied');
  } catch (err) {
    showResult(`<span style="color:var(--error)">Error: ${escapeHtml(err.message)}</span>`);
  }
  showLoading(false);
}

async function highlightLinks() {
  showLoading(true);
  try {
    await chrome.runtime.sendMessage({ action: 'highlightElements', selector: 'a[href]' });
    showResult('Links highlighted on page');
    updateStatus('Highlights active');
  } catch (err) {
    showResult(`<span style="color:var(--error)">Error: ${escapeHtml(err.message)}</span>`);
  }
  showLoading(false);
}

async function clearHighlights() {
  showLoading(true);
  try {
    await chrome.runtime.sendMessage({ action: 'clearHighlights' });
    showResult('Highlights cleared');
    updateStatus('Ready');
  } catch (err) {
    showResult(`<span style="color:var(--error)">Error: ${escapeHtml(err.message)}</span>`);
  }
  showLoading(false);
}

function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}
