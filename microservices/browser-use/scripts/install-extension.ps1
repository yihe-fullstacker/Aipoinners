# Install Chrome Extension Native Messaging Host
# Run this script as Administrator to register the native messaging host

$manifestPath = "E:\BaiduNetdiskDownload\qianhouduan+fanqiang\医疗AI诊断\Aipoinners\Aipoinners\AIPioneers\microservices\browser-use\chrome-extension\com.codex.browseruse.json"
$regPath = "HKLM:\SOFTWARE\Google\Chrome\NativeMessagingHosts\com.codex.browseruse"

# Check if running as admin
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")

if (-not $isAdmin) {
    Write-Host "Please run this script as Administrator!" -ForegroundColor Red
    Write-Host "Right-click PowerShell and select 'Run as Administrator', then run:"
    Write-Host "  .\scripts\install-extension.ps1" -ForegroundColor Yellow
    exit 1
}

# Update the path in the manifest to be absolute
$manifest = Get-Content $manifestPath -Raw | ConvertFrom-Json
$manifest.path = "E:\BaiduNetdiskDownload\qianhouduan+fanqiang\医疗AI诊断\Aipoinners\Aipoinners\AIPioneers\microservices\browser-use\chrome-extension\browser-use-server.bat"
$manifest | ConvertTo-Json | Set-Content $manifestPath

# Create registry key
if (-not (Test-Path $regPath)) {
    New-Item -Path $regPath -Force | Out-Null
}
Set-ItemProperty -Path $regPath -Name "(Default)" -Value $manifestPath

Write-Host "Native messaging host registered successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Open Chrome and go to chrome://extensions"
Write-Host "2. Enable 'Developer mode' (top-right toggle)"
Write-Host "3. Click 'Load unpacked' and select:"
Write-Host "   E:\BaiduNetdiskDownload\qianhouduan+fanqiang\医疗AI诊断\Aipoinners\Aipoinners\AIPioneers\microservices\browser-use\chrome-extension"
Write-Host "4. The extension should appear in your toolbar"
Write-Host ""
Write-Host "To use browser automation, run:"
Write-Host "   cd browser-use && npm start"
Write-Host "   (This starts the browser-use server)"
