@echo off
REM Browser-Use Native Messaging Host
REM This script bridges the Chrome extension to the Node.js server
node "%~dp0..\scripts\browser-use-server.js" --native
