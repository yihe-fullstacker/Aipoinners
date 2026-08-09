# Browser Use Skill

A browser automation and computer-use skill powered by Playwright + Chrome Extension.

## Prerequisites

- Node.js (v18+) and npm installed
- Google Chrome browser installed
- Playwright installed (included in `browser-use` project)

## Architecture

```
browser-use/
├── scripts/
│   ├── browser-use.js          # CLI client - one-shot browser actions
│   └── browser-use-server.js   # Server - persistent browser session
├── chrome-extension/
│   ├── manifest.json           # Chrome extension manifest (MV3)
│   ├── background.js           # Extension service worker
│   ├── content.js              # Page content script
│   ├── popup.html              # Extension popup UI
│   └── popup.js                # Popup logic
├── skills/
│   └── SKILL.md                # This file
├── bin/
│   └── browser-use.bat         # CLI wrapper
└── package.json
```

## Commands (via shell)

### Quick one-shot browser actions:

**Navigate to a URL and get page info:**
```powershell
cd <browser-use-dir>
node scripts/browser-use.js navigate https://example.com
```

**Download page content:**
```powershell
node scripts/browser-use.js download https://example.com
```

**Take a screenshot of current page:**
```powershell
node scripts/browser-use.js screenshot
```

**Get visible text from page:**
```powershell
node scripts/browser-use.js text
node scripts/browser-use.js text h1      # specific selector
```

**Get all links:**
```powershell
node scripts/browser-use.js links
```

### Persistent server mode:

**Start server (headless):**
```powershell
node scripts/browser-use.js start
```

**Start server (visible browser window):**
```powershell
node scripts/browser-use.js start --headed
```

**Stop server:**
```powershell
node scripts/browser-use.js stop
```

**Check status:**
```powershell
node scripts/browser-use.js status
```

## Chrome Extension

The Chrome extension provides deep browser integration:

### Installation:
1. Open Chrome and go to `chrome://extensions/`
2. Enable **Developer mode** (top-right toggle)
3. Click **Load unpacked**
4. Select the `browser-use/chrome-extension` folder
5. The extension icon appears in the toolbar

### Features:
- Read page content (title, text, links, HTML)
- Execute JavaScript on pages
- Click elements, fill forms
- Highlight elements on page
- Get tab info
- Copy page context for Codex analysis

## Usage Examples

### Web Research:
```powershell
# Navigate to a page and get its content
node scripts/browser-use.js navigate https://example.com
```

### Form Filling:
```powershell
# Navigate to a form, then use Chrome extension to fill fields
# The background.js handles click, fill, and script execution
```

## Notes

- One-shot commands launch and close a browser per invocation
- Server mode keeps the browser running for faster repeated commands
- The Chrome extension connects to the user\'s existing Chrome session
- Use `--headed` flag to see what the browser is doing
- Screenshots are saved to `browser-use/screenshots/`
