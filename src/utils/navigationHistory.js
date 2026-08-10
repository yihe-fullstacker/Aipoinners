/**
 * 导航历史管理工具
 * 用于跟踪用户浏览历史，实现逐级返回逻辑
 */

class NavigationHistory {
  constructor() {
    this.history = []
    this.currentIndex = -1
    this.isTracking = false
    this.targetPath = '/patient/home'
  }

  /**
   * 开始跟踪导航历史
   */
  startTracking(router) {
    if (this.isTracking) return
    
    this.isTracking = true
    this.history = []
    this.currentIndex = -1

    // 监听路由变化
    router.afterEach((to, from) => {
      // 跳过目标路径本身，避免循环
      if (to.path === this.targetPath && from.path === this.targetPath) {
        return
      }

      // 添加到历史栈
      if (from.path && from.path !== this.targetPath) {
        if (this.currentIndex < this.history.length - 1) {
          // 如果当前不在栈顶，删除后面的历史
          this.history = this.history.slice(0, this.currentIndex + 1)
        }
        this.history.push(from.path)
        this.currentIndex = this.history.length - 1
      }

      console.log('[NavigationHistory] 历史栈:', this.history)
    })
  }

  /**
   * 停止跟踪
   */
  stopTracking() {
    this.isTracking = false
    this.history = []
    this.currentIndex = -1
  }

  /**
   * 检查是否可以返回
   */
  canGoBack() {
    return this.history.length > 0
  }

  /**
   * 获取上一页路径
   */
  getPreviousPath() {
    if (!this.canGoBack()) {
      console.warn('[NavigationHistory] 历史栈为空，无法返回')
      return null
    }
    return this.history[this.currentIndex]
  }

  /**
   * 检查是否在目标路径
   */
  isAtTargetPath(path) {
    return path === this.targetPath
  }

  /**
   * 逐级返回
   * @param {Object} router - Vue Router实例
   * @param {Function} onConfirm - 表单页二次确认回调
   * @returns {boolean} 是否成功返回
   */
  async goBackStep(router, onConfirm = null) {
    if (!this.canGoBack()) {
      console.warn('[NavigationHistory] 历史栈为空，无法继续返回')
      return false
    }

    const previousPath = this.getPreviousPath()

    // 检查是否到达目标路径
    if (previousPath === this.targetPath || this.history.length === 1) {
      console.log('[NavigationHistory] 到达目标路径，停止返回')
      router.push(this.targetPath)
      this.stopTracking()
      return true
    }

    // 检查是否是表单页（这里简化处理，实际可以根据路由meta判断）
    const isFormPage = previousPath.includes('/upload') || 
                       previousPath.includes('/smart-qa') ||
                       previousPath.includes('/medical-course')

    if (isFormPage && onConfirm) {
      const confirmed = await onConfirm()
      if (!confirmed) {
        console.log('[NavigationHistory] 用户取消返回')
        return false
      }
    }

    // 执行返回
    router.push(previousPath)
    this.history.pop()
    this.currentIndex--

    return true
  }

  /**
   * 重置历史栈（登录成功后调用）
   */
  reset() {
    this.history = []
    this.currentIndex = -1
    console.log('[NavigationHistory] 历史栈已重置')
  }
}

// 导出单例
export default new NavigationHistory()
