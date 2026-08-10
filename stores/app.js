// stores/app.js — 应用全局状态
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    systemInfo: null,
    networkType: 'unknown',
    isOnline: true,
    currentAnalysis: null
  }),
  actions: {
    initSystemInfo() {
      uni.getSystemInfo({
        success: (res) => {
          this.systemInfo = res
        }
      })
      uni.getNetworkType({
        success: (res) => {
          this.networkType = res.networkType
          this.isOnline = res.networkType !== 'none'
        }
      })
      uni.onNetworkStatusChange((res) => {
        this.networkType = res.networkType
        this.isOnline = res.isConnected
      })
    },
    setAnalysis(analysis) {
      this.currentAnalysis = analysis
    }
  }
})
