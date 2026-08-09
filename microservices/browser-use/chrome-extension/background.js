/**
 * Codex Browser Use - Background Service Worker
 * 
 * Handles:
 * - Connection to browser-use server via Native Messaging
 * - Tab management and content script coordination
 * - Message routing between extension and browser-use tools
 */

// Tab state cache
const tabState = new Map();

// Native messaging host name (configured in OS registry)
const NATIVE_HOST = 'com.codex.browseruse';

// Track active connections
let nativePort = null;
let reconnectTimer = null;

// Initialize
chrome.runtime.onInstalled.addListener((details) => {
  console.log('Codex Browser Use installed:', details.reason);
  setupDefaultSettings();
});

function setupDefaultSettings() {
  chrome.storage.local.get(['settings'], (result) => {
    if (!result.settings) {
      chrome.storage.local.set({
        settings: {
          serverHost: 'localhost',
          serverPort: 9876,
          autoConnect: true,
          showNotifications: true,
        }
      });
    }
  });
}

// Listen for tab updates
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  if (changeInfo.status === 'complete' && tab.url && !tab.url.startsWith('chrome://')) {
    tabState.set(tabId, {
      url: tab.url,
      title: tab.title,
      lastActive: Date.now(),
    });
  }
});

chrome.tabs.onRemoved.addListener((tabId) => {
  tabState.delete(tabId);
});

// Message handler from popup and content scripts
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  const handler = messageHandlers[message.action];
  if (handler) {
    handler(message, sender).then(sendResponse).catch(err => {
      sendResponse({ error: err.message });
    });
    return true; // Keep channel open for async
  }
});

const messageHandlers = {

  // Get info about the current tab
  async getTabInfo(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    const tab = tabs[0];
    return {
      tabId: tab.id,
      url: tab.url,
      title: tab.title,
      favIconUrl: tab.favIconUrl,
    };
  },

  // Get all tabs
  async getAllTabs() {
    const tabs = await chrome.tabs.query({});
    return tabs.map(t => ({
      id: t.id,
      url: t.url,
      title: t.title,
      active: t.active,
      windowId: t.windowId,
    }));
  },

  // Navigate current tab
  async navigate(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    await chrome.tabs.update(tabs[0].id, { url: message.url });
    return { ok: true, url: message.url };
  },

  // Execute script in current tab
  async executeScript(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    
    try {
      const results = await chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        func: new Function(message.code),
      });
      return { ok: true, result: results[0]?.result };
    } catch (err) {
      return { error: err.message };
    }
  },

  // Get page content (from content script)
  async getPageContent(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    
    try {
      const results = await chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        func: () => {
          return {
            title: document.title,
            url: window.location.href,
            text: document.body?.innerText || '',
            html: document.documentElement?.outerHTML || '',
            links: Array.from(document.querySelectorAll('a[href]')).map(a => ({
              text: a.textContent.trim(),
              href: a.href
            })).filter(l => l.href && !l.href.startsWith('javascript:')),
          };
        },
      });
      return { ok: true, data: results[0]?.result };
    } catch (err) {
      return { error: err.message };
    }
  },

  // Highlight elements
  async highlightElements(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    
    try {
      await chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        func: (selector) => {
          const elements = document.querySelectorAll(selector);
          elements.forEach((el, i) => {
            el.style.outline = '3px solid #339cff';
            el.style.outlineOffset = '2px';
            el.setAttribute('data-codex-highlight', 'true');
          });
          return elements.length;
        },
        args: [message.selector],
      });
      return { ok: true };
    } catch (err) {
      return { error: err.message };
    }
  },

  // Clear highlights
  async clearHighlights(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    
    try {
      await chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        func: () => {
          document.querySelectorAll('[data-codex-highlight]').forEach(el => {
            el.style.outline = '';
            el.style.outlineOffset = '';
            el.removeAttribute('data-codex-highlight');
          });
        },
      });
      return { ok: true };
    } catch (err) {
      return { error: err.message };
    }
  },

  // Click element
  async clickElement(message, sender) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    
    try {
      const results = await chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        func: (selector) => {
          const el = document.querySelector(selector);
          if (!el) return { error: 'Element not found: ' + selector };
          el.click();
          return { ok: true };
        },
        args: [message.selector],
      });
      return results[0]?.result || { error: 'Script execution failed' };
    } catch (err) {
      return { error: err.message };
    }
  },

  // Fill input
  async fillInput(message, selector, value) {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (tabs.length === 0) return { error: 'No active tab' };
    
    try {
      const results = await chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        func: (sel, val) => {
          const el = document.querySelector(sel);
          if (!el) return { error: 'Element not found: ' + sel };
          el.value = val;
          el.dispatchEvent(new Event('input', { bubbles: true }));
          el.dispatchEvent(new Event('change', { bubbles: true }));
          return { ok: true };
        },
        args: [selector, value],
      });
      return results[0]?.result || { error: 'Script execution failed' };
    } catch (err) {
      return { error: err.message };
    }
  },

  // Get settings
  async getSettings() {
    const result = await chrome.storage.local.get(['settings']);
    return result.settings || {};
  },

  // Update settings
  async updateSettings(message, sender) {
    await chrome.storage.local.set({ settings: message.settings });
    return { ok: true };
  },
};
