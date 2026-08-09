/**
 * Codex Browser Use - Content Script
 * 
 * Injected into every page to enable:
 * - Page content extraction
 * - Element interaction (click, type, scroll)
 * - DOM manipulation
 * - Form filling
 * - Screenshot data provision
 */

// Host page data for background script requests
const pageData = {
  title: document.title,
  url: window.location.href,
  text: '',
  html: '',
};

// Listen for messages from background script
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === 'getPageData') {
    sendResponse({
      title: document.title,
      url: window.location.href,
      text: document.body?.innerText || '',
      html: document.documentElement?.outerHTML || '',
    });
  }
  return true;
});

// Notify background that content script is ready
chrome.runtime.sendMessage({
  action: 'contentScriptReady',
  url: window.location.href,
  title: document.title,
});
