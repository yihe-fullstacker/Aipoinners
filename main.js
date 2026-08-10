// main.js — UniApp 入口
import { createSSRApp } from 'vue'
import App from './App.vue'
import pinia from './stores'

export function createApp() {
  const app = createSSRApp(App)
  app.use(pinia)
  app.config.globalProperties.$apiBase = 'http://47.108.52.206:8082/api'
  return { app }
}
