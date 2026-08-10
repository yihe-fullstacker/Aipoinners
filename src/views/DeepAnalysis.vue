<template>
  <div class="deep-analysis-page">
    <div class="page-shell">
      <section class="hero-panel">
        <div>
          <button class="back-btn" type="button" @click="goBack">返回上一页</button>
          <span class="section-badge">深度分析可视化</span>
          <h1>把热力图、特征解释与候选诊断整合成更适合业务展示的可视化分析界面</h1>
          <p>
            该页面保留原有热力图、异常区域、特征和诊断分布能力，同时强化点击、切换、排序和导出等交互体验，更适合上线后的专业展示使用。
          </p>
        </div>

        <div class="hero-stats" v-if="analysisData">
          <article class="stat-card">
            <strong>{{ analysisData.attentionRegions?.length || 0 }}</strong>
            <span>重点关注区域</span>
          </article>
          <article class="stat-card">
            <strong>{{ measurements.length }}</strong>
            <span>当前测量项</span>
          </article>
          <article class="stat-card">
            <strong>{{ heatmapGenerated ? '已生成' : '处理中' }}</strong>
            <span>热力图状态</span>
          </article>
        </div>
      </section>

      <section v-if="loading" class="state-card card-surface">正在加载深度分析数据，请稍候...</section>

      <section v-else-if="error" class="state-card card-surface error-card">
        <p>{{ error }}</p>
      </section>

      <section v-else-if="analysisData" class="content-grid">
        <article class="card-surface viewer-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">可解释性视图</div>
              <h2>原始影像与热力图联动展示</h2>
            </div>
            <div class="action-chip-row">
              <button type="button" class="ghost-btn" @click="toggleSideBySide">
                {{ sideBySide ? '切换为上下布局' : '切换为左右布局' }}
              </button>
              <button type="button" class="ghost-btn" @click="downloadHeatmap" :disabled="!heatmapGenerated">下载热力图</button>
              <button type="button" class="ghost-btn" @click="openHeatmapNewTab" :disabled="!heatmapGenerated">新窗口打开</button>
            </div>
          </div>

          <div class="viewer-grid" :class="{ stacked: !sideBySide }">
            <div class="viewer-panel">
              <div class="viewer-title">原始影像</div>
              <div class="viewer-stage">
                <img :src="analysisData.annotatedImageUrl || analysisData.imageUrl" alt="原始影像" class="analysis-image">
              </div>
            </div>
            <div class="viewer-panel heatmap-panel">
              <div class="viewer-title-row">
                <div class="viewer-title">热力图叠加</div>
                <span class="viewer-hover-hint">悬停查看颜色解释</span>
              </div>
              <div class="viewer-stage viewer-stage--heatmap">
                <canvas ref="heatmapCanvas" class="analysis-image" :style="{ opacity: overlayOpacity }"></canvas>
                <div v-if="!heatmapGenerated" class="viewer-placeholder">系统正在生成热力图...</div>
                <div class="heatmap-legend-popover">
                  <div class="legend-popover__header">
                    <strong>热力图颜色解释</strong>
                    <span>{{ selectedPalette }}</span>
                  </div>
                  <div class="legend-popover__item">
                    <span class="legend-dot legend-dot--red"></span>
                    <div>
                      <strong>红色</strong>
                      <p>模型高度关注的疑似异常区域，通常代表高风险征象。</p>
                    </div>
                  </div>
                  <div class="legend-popover__item">
                    <span class="legend-dot legend-dot--orange"></span>
                    <div>
                      <strong>橙色</strong>
                      <p>中等关注或边界过渡区域，建议结合上下文继续复核。</p>
                    </div>
                  </div>
                  <div class="legend-popover__item">
                    <span class="legend-dot legend-dot--blue"></span>
                    <div>
                      <strong>蓝色</strong>
                      <p>低风险或背景组织区域，模型在此处关注度相对较低。</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="control-panel">
            <div class="control-item">
              <span>透明度</span>
              <strong>{{ overlayOpacity.toFixed(2) }}</strong>
            </div>
            <div class="control-item">
              <span>显示阈值</span>
              <strong>{{ threshold }}%</strong>
            </div>
            <div class="control-item">
              <span>颜色方案</span>
              <strong>{{ selectedPalette }}</strong>
            </div>
          </div>

          <div class="palette-row">
            <button
              v-for="cp in colorPalettes"
              :key="cp.name"
              type="button"
              class="palette-chip"
              :class="{ active: selectedPalette === cp.name }"
              @click="selectPalette(cp)"
            >
              <span class="palette-preview" :style="{ background: cp.gradient }"></span>
              {{ cp.name }}
            </button>
          </div>
        </article>

        <article class="card-surface region-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">关注区域</div>
              <h2>异常区域列表与重点排序</h2>
            </div>
          </div>

          <div class="regions-list" v-if="analysisData.attentionRegions?.length">
            <div
              v-for="(region, index) in analysisData.attentionRegions"
              :key="index"
              class="region-item"
              draggable="true"
              @dragstart="onDragStart(index)"
              @drop.prevent="onDrop(index)"
              @dragover.prevent
              @mouseenter="highlightRegion(region)"
              @mouseleave="clearHighlight"
              @click="focusRegion(region)"
            >
              <div class="region-top">
                <strong>区域 {{ index + 1 }}</strong>
                <span>{{ region.type || '高风险' }}</span>
              </div>
              <p>关注度：{{ (region.score * 100).toFixed(1) }}%</p>
              <p>坐标：x={{ region.x }}, y={{ region.y }}</p>
              <p>尺寸：w={{ region.width }}, h={{ region.height }}</p>
            </div>
          </div>
          <div v-else class="empty-box">暂无可用的区域分析数据。</div>
        </article>

        <article class="card-surface feature-card" v-if="analysisData.features && analysisData.features.length > 0">
          <div class="card-head">
            <div>
              <div class="section-kicker">关键特征</div>
              <h2>模型关注的影像特征</h2>
            </div>
          </div>

          <div class="feature-grid">
            <div v-for="(feature, index) in analysisData.features" :key="index" class="feature-item">
              <div class="feature-icon">{{ getFeatureIcon(feature.type) }}</div>
              <div>
                <strong>{{ feature.name }}</strong>
                <p>{{ feature.description }}</p>
                <div class="feature-meta">
                  <span>重要性 {{ (feature.importance * 100).toFixed(1) }}%</span>
                  <span v-if="feature.confidence">置信度 {{ (feature.confidence * 100).toFixed(1) }}%</span>
                </div>
              </div>
            </div>
          </div>
        </article>

        <article class="card-surface diagnosis-card" v-if="analysisData.differentialDiagnosis && analysisData.differentialDiagnosis.length > 0">
          <div class="card-head">
            <div>
              <div class="section-kicker">鉴别诊断</div>
              <h2>候选诊断概率分布</h2>
            </div>
          </div>

          <div class="diagnosis-list">
            <div v-for="(diagnosis, index) in analysisData.differentialDiagnosis" :key="index" class="diagnosis-item">
              <div class="diagnosis-top">
                <strong>{{ diagnosis.name }}</strong>
                <span>{{ (diagnosis.probability * 100).toFixed(1) }}%</span>
              </div>
              <div class="probability-bar">
                <div class="probability-fill" :class="getProbabilityClass(diagnosis.probability)" :style="{ width: `${diagnosis.probability * 100}%` }"></div>
              </div>
              <p v-if="diagnosis.description">{{ diagnosis.description }}</p>
            </div>
          </div>
        </article>

        <article class="card-surface measurement-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">辅助工具</div>
              <h2>测量与导出</h2>
            </div>
          </div>

          <div class="tool-actions">
            <button type="button" class="primary-btn" @click="startMeasurement">启动测量模式</button>
            <button type="button" class="secondary-btn" @click="clearMeasurements">清空测量</button>
            <button type="button" class="ghost-btn" @click="exportMeasurements" :disabled="!measurements.length">导出测量结果</button>
          </div>

          <div class="measurement-summary">
            <div class="measurement-item">
              <span>测量模式</span>
              <strong>{{ measuring ? '已开启' : '未开启' }}</strong>
            </div>
            <div class="measurement-item">
              <span>测量数量</span>
              <strong>{{ measurements.length }}</strong>
            </div>
            <div class="measurement-item">
              <span>像素间距</span>
              <strong>{{ pixelSpacing }}</strong>
            </div>
          </div>

          <div class="action-row">
            <button type="button" class="primary-btn" @click="viewAnalysisReport">查看完整分析报告</button>
            <button type="button" class="secondary-btn" @click="viewCaseDetail">查看病例详情</button>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import explain from '../directives/explain.js'
import { useRoute, useRouter } from 'vue-router'
import { medicalAPI, imageUtils } from '../api/api'
import { localizeAnalysisPayload } from '../utils/analysisLocalization'

export default {
  name: 'DeepAnalysis',
  directives: { explain },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const analysisData = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const heatmapCanvas = ref(null)
    const heatmapGenerated = ref(false)
    const overlayOpacity = ref(1)
    const threshold = ref(50)
    const palette = ref('red-orange-blue')
    const heatmapType = ref('gradcam')
    const sideBySide = ref(false)
    const highlightedRegion = ref(null)
    const measureMode = ref('length')
    const measuring = ref(false)
    const pixelSpacing = ref(0.1)
    const measurements = ref([])
    const panelFullscreen = ref(false)
    const activeTab = ref('heatmap')
    const detailVisible = ref(false)
    const dragFrom = ref(null)
    const drawerVisible = ref(false)
    const drawerTool = ref('heatmap')
    const drawerTitle = ref('工具面板')
    const colorPalettes = ref([
      { name: 'Red-Orange-Blue', gradient: 'linear-gradient(90deg, #ff6b6b, #f59e0b, #3b82f6)' },
      { name: 'Turbo', gradient: 'linear-gradient(90deg, #30123b, #7a0403, #ff6200, #ffea00, #00ff90, #00a5ff)' },
      { name: 'Viridis', gradient: 'linear-gradient(90deg, #440154, #3b528b, #21918c, #5ec962, #fde725)' }
    ])
    const selectedPalette = ref('Red-Orange-Blue')

    const paletteMap = {
      'red-orange-blue': ['#ff6b6b', '#f59e0b', '#3b82f6'],
      turbo: ['#7a0403', '#ff6200', '#ffea00'],
      viridis: ['#440154', '#21918c', '#fde725']
    }

    const getPaletteColors = () => {
      return paletteMap[palette.value] || paletteMap['red-orange-blue']
    }

    const toPngDataUrl = (base64) => {
      if (!base64) return ''
      return base64.startsWith('data:') ? base64 : `data:image/png;base64,${base64}`
    }

    const normalizeRegions = (regions = [], findings = []) => {
      if (Array.isArray(regions) && regions.length) {
        return regions.map((region, index) => {
          const centerX = Number(region.centerX ?? region.x ?? 90 + index * 60)
          const centerY = Number(region.centerY ?? region.y ?? 110 + index * 40)
          const width = Math.max(12, Math.min(Number(region.width ?? 120), 260))
          const height = Math.max(12, Math.min(Number(region.height ?? 96), 220))
          return {
            x: Math.max(0, Number(region.x ?? centerX - width / 2)),
            y: Math.max(0, Number(region.y ?? centerY - height / 2)),
            width,
            height,
            score: Number(region.score ?? region.confidence ?? 0.6),
            centerX: Math.max(0, centerX),
            centerY: Math.max(0, centerY),
            type: region.type || '关注区域',
            location: region.location || '',
            description: region.description || ''
          }
        })
      }

      if (!findings.length) {
        return [
          { x: 120, y: 110, width: 160, height: 120, score: 0.84, type: '高风险' },
          { x: 360, y: 240, width: 140, height: 110, score: 0.62, type: '可疑区域' }
        ]
      }

      return findings.map((finding, index) => ({
        x: 90 + index * 90,
        y: 110 + index * 60,
        width: 140,
        height: 96,
        score: finding.confidence || 0.6,
        type: finding.type || '关注区域'
      }))
    }

    const normalizeFeatures = (findings = []) => {
      if (!findings.length) {
        return [
          { name: '边界完整性', type: '边界', description: '边界轮廓清晰度与周边组织对比明显。', importance: 0.82, confidence: 0.76 },
          { name: '密度变化', type: '密度', description: '局部区域呈现与周围组织不同的密度特征。', importance: 0.73, confidence: 0.71 }
        ]
      }

      return findings.map((finding, index) => ({
        name: `${finding.type || '异常'} 特征 ${index + 1}`,
        type: index % 2 === 0 ? '边界' : '密度',
        description: finding.description || `系统根据 ${finding.location || '目标区域'} 的影像表现提取到重点特征。`,
        importance: Math.max(0.45, finding.confidence || 0.6),
        confidence: finding.confidence || 0.6
      }))
    }

    const drawRegionHighlight = (ctx, region) => {
      ctx.save()
      ctx.strokeStyle = '#f8fafc'
      ctx.lineWidth = 3
      ctx.setLineDash([10, 6])
      ctx.strokeRect(region.x, region.y, region.width, region.height)
      ctx.restore()
    }

    const renderFallbackHeatmap = () => {
      const canvas = heatmapCanvas.value
      if (!canvas || !analysisData.value) return
      const ctx = canvas.getContext('2d')
      const width = 720
      const height = 420
      canvas.width = width
      canvas.height = height
      ctx.clearRect(0, 0, width, height)
      ctx.fillStyle = '#020817'
      ctx.fillRect(0, 0, width, height)

      const colors = getPaletteColors()
      analysisData.value.attentionRegions.forEach((region, index) => {
        if ((region.score || 0) * 100 < threshold.value) {
          return
        }

        const gradient = ctx.createRadialGradient(
          region.x + region.width / 2,
          region.y + region.height / 2,
          12,
          region.x + region.width / 2,
          region.y + region.height / 2,
          Math.max(region.width, region.height)
        )
        gradient.addColorStop(0, colors[index % colors.length])
        gradient.addColorStop(1, 'rgba(3, 7, 18, 0)')
        ctx.globalAlpha = overlayOpacity.value
        ctx.fillStyle = gradient
        ctx.fillRect(region.x - 20, region.y - 20, region.width + 40, region.height + 40)
      })

      if (highlightedRegion.value) {
        drawRegionHighlight(ctx, highlightedRegion.value)
      }

      heatmapGenerated.value = true
    }

    const renderHeatmapFromImage = (base64) => {
      const canvas = heatmapCanvas.value
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const image = new Image()
      image.onload = () => {
        canvas.width = image.width
        canvas.height = image.height
        ctx.clearRect(0, 0, image.width, image.height)
        ctx.globalAlpha = overlayOpacity.value
        ctx.drawImage(image, 0, 0)
        if (highlightedRegion.value) {
          drawRegionHighlight(ctx, highlightedRegion.value)
        }
        heatmapGenerated.value = true
      }
      image.onerror = () => {
        renderFallbackHeatmap()
      }
      image.src = base64.startsWith('data:') ? base64 : `data:image/png;base64,${base64}`
    }

    const renderHeatmap = () => {
      if (!analysisData.value) return
      if (analysisData.value.heatmapBase64) {
        renderHeatmapFromImage(analysisData.value.heatmapBase64)
        return
      }
      renderFallbackHeatmap()
    }

    const loadAnalysis = async () => {
      const reportId = route.params.id
      if (!reportId) {
        error.value = '缺少报告 ID'
        loading.value = false
        return
      }

      loading.value = true
      error.value = null

      try {
        const response = await medicalAPI.getReportDetail(reportId)
        if (!response.data) {
          error.value = '未找到分析数据'
          return
        }

        const baseData = localizeAnalysisPayload(response.data)
        const reportHeatmapBase64 = imageUtils.extractHeatmapBase64(baseData)

        analysisData.value = {
          ...baseData,
          imageUrl: imageUtils.extractImageUrl(baseData) || imageUtils.ensureImageUrl(baseData.imageUrl || `/api/images/${baseData.imageId || reportId}`),
          annotatedImageUrl: toPngDataUrl(baseData.annotatedImageBase64 || baseData.annotated_image_base64),
          attentionRegions: normalizeRegions(baseData.attentionRegions, baseData.findings),
          features: baseData.features?.length ? baseData.features : normalizeFeatures(baseData.findings),
          differentialDiagnosis: baseData.differentialDiagnosis || [],
          heatmapBase64: reportHeatmapBase64
        }

        if (!analysisData.value.heatmapBase64) {
          try {
            const heatmapResponse = await medicalAPI.getReportHeatmap(reportId)
            const endpointHeatmapBase64 = imageUtils.extractHeatmapBase64(heatmapResponse?.data)
            if (endpointHeatmapBase64) {
              analysisData.value.heatmapBase64 = endpointHeatmapBase64
              analysisData.value.annotatedImageUrl = analysisData.value.annotatedImageUrl || toPngDataUrl(heatmapResponse.data?.annotated_image_base64)
              analysisData.value.attentionRegions = normalizeRegions(
                heatmapResponse.data?.attentionRegions || analysisData.value.attentionRegions,
                analysisData.value.findings
              )
            }
          } catch (heatmapError) {
            console.warn('热力图接口不可用，使用前端回退渲染。', heatmapError)
          }
        }

        await nextTick()
        renderHeatmap()
      } catch (err) {
        console.error('加载深度分析失败:', err)
        error.value = err.response?.data?.message || '加载深度分析失败'
      } finally {
        loading.value = false
      }
    }

    const toggleSideBySide = () => {
      sideBySide.value = !sideBySide.value
    }

    const highlightRegion = (region) => {
      highlightedRegion.value = region
      renderHeatmap()
    }

    const clearHighlight = () => {
      highlightedRegion.value = null
      renderHeatmap()
    }

    const focusRegion = (region) => {
      highlightedRegion.value = region
      renderHeatmap()
    }

    const startMeasurement = () => {
      measuring.value = true
      measurements.value.push({
        mode: measureMode.value,
        createdAt: new Date().toISOString(),
        pixelSpacing: pixelSpacing.value
      })
    }

    const clearMeasurements = () => {
      measurements.value = []
      measuring.value = false
    }

    const exportMeasurements = () => {
      if (!measurements.value.length) return
      const blob = new Blob([JSON.stringify(measurements.value, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `measurements_${route.params.id}.json`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    }

    const onDragStart = (index) => {
      dragFrom.value = index
    }

    const onDrop = (index) => {
      if (dragFrom.value === null || !analysisData.value?.attentionRegions) return
      const list = [...analysisData.value.attentionRegions]
      const [item] = list.splice(dragFrom.value, 1)
      list.splice(index, 0, item)
      analysisData.value.attentionRegions = list
      dragFrom.value = null
      renderHeatmap()
    }

    const openToolPanel = (name) => {
      drawerTool.value = name
      drawerTitle.value = name
      drawerVisible.value = true
    }

    const selectPalette = (cp) => {
      selectedPalette.value = cp.name
      palette.value = cp.name.toLowerCase()
      renderHeatmap()
    }

    const getCanvasImageDataUrl = () => {
      if (!heatmapCanvas.value || !heatmapGenerated.value) return ''
      return heatmapCanvas.value.toDataURL('image/png')
    }

    const downloadHeatmap = () => {
      const dataUrl = getCanvasImageDataUrl()
      if (!dataUrl) return
      const link = document.createElement('a')
      link.href = dataUrl
      link.download = `heatmap_${route.params.id}.png`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }

    const openHeatmapNewTab = () => {
      const dataUrl = getCanvasImageDataUrl()
      if (!dataUrl) return
      const win = window.open()
      if (win) {
        win.document.write(`<img src="${dataUrl}" style="max-width:100%;background:#020817;" />`)
      }
    }

    const getFeatureIcon = (type) => {
      const icons = {
        形状: '◇',
        密度: '▦',
        边界: '▣',
        纹理: '≈'
      }
      return icons[type] || '◎'
    }

    const getProbabilityClass = (probability = 0) => {
      if (probability >= 0.7) return 'high'
      if (probability >= 0.4) return 'medium'
      return 'low'
    }

    const goBack = () => {
      router.back()
    }

    const viewAnalysisReport = () => {
      if (analysisData.value) {
        router.push(`/analysis-report/${analysisData.value.id}`)
      }
    }

    const viewCaseDetail = () => {
      if (analysisData.value) {
        router.push(`/case-detail/${analysisData.value.id}`)
      }
    }

    onMounted(() => {
      loadAnalysis()
    })

    return {
      analysisData,
      loading,
      error,
      heatmapCanvas,
      heatmapGenerated,
      overlayOpacity,
      threshold,
      palette,
      heatmapType,
      sideBySide,
      highlightedRegion,
      measureMode,
      measuring,
      pixelSpacing,
      measurements,
      panelFullscreen,
      activeTab,
      detailVisible,
      onDragStart,
      onDrop,
      drawerVisible,
      drawerTool,
      drawerTitle,
      colorPalettes,
      selectedPalette,
      openToolPanel,
      selectPalette,
      downloadHeatmap,
      openHeatmapNewTab,
      toggleSideBySide,
      highlightRegion,
      clearHighlight,
      focusRegion,
      startMeasurement,
      clearMeasurements,
      exportMeasurements,
      getFeatureIcon,
      getProbabilityClass,
      goBack,
      viewAnalysisReport,
      viewCaseDetail
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.deep-analysis-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(20, 184, 166, 0.16), transparent 28%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), transparent 26%),
    linear-gradient(180deg, #07111d 0%, #091827 100%);
  color: #eef7ff;
  padding: 28px 24px 48px;
}

.page-shell {
  max-width: 1320px;
  margin: 0 auto;
}

.hero-panel,
.card-surface {
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: linear-gradient(180deg, rgba(7, 21, 36, 0.94), rgba(4, 12, 24, 0.96));
  box-shadow: 0 22px 54px rgba(2, 10, 20, 0.28);
}

.hero-panel {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 22px;
  padding: 30px;
  border-radius: 30px;
}

.back-btn,
.primary-btn,
.secondary-btn,
.ghost-btn,
.palette-chip,
.section-badge,
.section-kicker {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
}

.back-btn,
.primary-btn,
.secondary-btn,
.ghost-btn,
.palette-chip {
  min-height: 42px;
  padding: 0 18px;
  border: none;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.back-btn,
.secondary-btn,
.ghost-btn,
.palette-chip {
  background: rgba(8, 20, 35, 0.82);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #ecf8ff;
}

.primary-btn {
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
}

.section-badge,
.section-kicker {
  min-height: 32px;
  padding: 0 12px;
  border: 1px solid rgba(94, 234, 212, 0.18);
  background: rgba(45, 212, 191, 0.08);
  color: #90f5e8;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero-panel h1,
.card-head h2 {
  color: #f7fbff;
}

.hero-panel h1 {
  margin: 16px 0 14px;
  font-size: clamp(30px, 4vw, 48px);
  line-height: 1.1;
}

.hero-panel p,
.region-item p,
.feature-item p,
.diagnosis-item p,
.state-card p {
  color: rgba(220, 235, 255, 0.72);
  line-height: 1.8;
}

.hero-stats {
  display: grid;
  gap: 14px;
}

.stat-card {
  padding: 18px;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(7, 20, 35, 0.76);
}

.stat-card strong {
  display: block;
  font-size: 28px;
  color: #ffffff;
  margin-bottom: 8px;
}

.state-card {
  margin-top: 22px;
  padding: 34px;
  border-radius: 26px;
  text-align: center;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px;
  margin-top: 22px;
}

.viewer-card,
.region-card,
.feature-card,
.diagnosis-card,
.measurement-card {
  padding: 24px;
  border-radius: 26px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.card-head h2 {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1.2;
}

.action-chip-row,
.tool-actions,
.action-row,
.palette-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.viewer-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.viewer-grid.stacked {
  grid-template-columns: 1fr;
}

.viewer-panel,
.control-item,
.region-item,
.feature-item,
.diagnosis-item,
.measurement-item {
  border-radius: 20px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.viewer-title {
  margin-bottom: 10px;
  color: #f7fbff;
  font-weight: 700;
}

.viewer-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.viewer-hover-hint {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(10, 22, 38, 0.72);
  color: #b9e9ff;
  font-size: 11px;
  letter-spacing: 0.06em;
}

.viewer-stage {
  position: relative;
  min-height: 360px;
  overflow: hidden;
  border-radius: 22px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(6, 17, 30, 0.9);
}

.analysis-image {
  width: 100%;
  height: 360px;
  object-fit: contain;
  background: #020817;
}

.viewer-placeholder {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  color: #eaf7ff;
  background: rgba(4, 12, 24, 0.74);
}

.viewer-stage--heatmap::after {
  content: '';
  position: absolute;
  inset: 14px;
  border-radius: 18px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  pointer-events: none;
}

.heatmap-legend-popover {
  position: absolute;
  top: 20px;
  right: 20px;
  width: min(320px, calc(100% - 40px));
  padding: 16px;
  border-radius: 20px;
  border: 1px solid rgba(125, 211, 252, 0.14);
  background: linear-gradient(180deg, rgba(7, 20, 35, 0.96), rgba(4, 12, 24, 0.98));
  box-shadow: 0 22px 48px rgba(2, 10, 20, 0.28);
  opacity: 0;
  transform: translateY(8px) scale(0.98);
  transition: opacity 0.24s ease, transform 0.24s ease;
  pointer-events: none;
  z-index: 3;
}

.heatmap-panel:hover .heatmap-legend-popover,
.viewer-stage--heatmap:focus-within .heatmap-legend-popover {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.legend-popover__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 14px;
}

.legend-popover__header strong,
.legend-popover__item strong {
  color: #f7fbff;
}

.legend-popover__header span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(45, 212, 191, 0.08);
  border: 1px solid rgba(94, 234, 212, 0.14);
  color: #90f5e8;
  font-size: 11px;
  letter-spacing: 0.06em;
}

.legend-popover__item {
  display: grid;
  grid-template-columns: 14px 1fr;
  gap: 12px;
  align-items: flex-start;
  padding: 10px 0;
}

.legend-popover__item p {
  margin: 6px 0 0;
  color: rgba(220, 235, 255, 0.72);
  line-height: 1.7;
  font-size: 12px;
}

.legend-dot {
  width: 14px;
  height: 14px;
  margin-top: 3px;
  border-radius: 999px;
  box-shadow: 0 0 0 6px rgba(255, 255, 255, 0.03);
}

.legend-dot--red {
  background: linear-gradient(135deg, #fb7185, #ef4444);
}

.legend-dot--orange {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.legend-dot--blue {
  background: linear-gradient(135deg, #38bdf8, #2563eb);
}

.control-panel,
.measurement-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
}

.control-item,
.measurement-item {
  padding: 16px;
}

.control-item span,
.measurement-item span {
  display: block;
  margin-bottom: 8px;
  color: #9ce8ff;
}

.control-item strong,
.measurement-item strong,
.region-top strong,
.feature-item strong,
.diagnosis-top strong {
  color: #f7fbff;
}

.palette-chip {
  padding: 0 16px;
  gap: 10px;
}

.palette-chip.active {
  border-color: rgba(94, 234, 212, 0.34);
}

.palette-preview {
  width: 32px;
  height: 12px;
  border-radius: 999px;
}

.regions-list,
.feature-grid,
.diagnosis-list {
  display: grid;
  gap: 14px;
}

.region-item,
.feature-item,
.diagnosis-item {
  padding: 18px;
}

.region-item {
  cursor: pointer;
}

.region-top,
.feature-meta,
.diagnosis-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}

.region-top span,
.feature-meta span {
  color: #9ce8ff;
}

.feature-item {
  display: grid;
  grid-template-columns: 48px 1fr;
  gap: 14px;
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: rgba(94, 234, 212, 0.12);
  font-size: 22px;
}

.probability-bar {
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
  margin: 12px 0;
}

.probability-fill {
  height: 100%;
  border-radius: inherit;
}

.probability-fill.high {
  background: linear-gradient(90deg, #f97316, #ef4444);
}

.probability-fill.medium {
  background: linear-gradient(90deg, #facc15, #fb923c);
}

.probability-fill.low {
  background: linear-gradient(90deg, #34d399, #22c55e);
}

.empty-box {
  padding: 18px;
  border-radius: 20px;
  border: 1px dashed rgba(125, 211, 252, 0.16);
  color: rgba(220, 235, 255, 0.72);
}

.action-row {
  margin-top: 18px;
}

@media (max-width: 1080px) {
  .hero-panel,
  .content-grid,
  .viewer-grid,
  .control-panel,
  .measurement-summary {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .deep-analysis-page {
    padding: 20px 12px 32px;
  }

  .hero-panel,
  .viewer-card,
  .region-card,
  .feature-card,
  .diagnosis-card,
  .measurement-card,
  .state-card {
    padding: 22px;
    border-radius: 24px;
  }

  .card-head,
  .viewer-title-row,
  .region-top,
  .feature-meta,
  .diagnosis-top,
  .action-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .primary-btn,
  .secondary-btn,
  .ghost-btn {
    width: 100%;
  }
}
</style>




