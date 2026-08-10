<template>
  <div class="heatmap-viewer">
    <div class="heatmap-header">
      <div>
        <span class="section-kicker">Explainable AI</span>
        <h3>可解释性热力图</h3>
        <p class="description">{{ description }}</p>
      </div>
      <button v-if="heatmapBase64" @click="downloadHeatmap" class="btn-download">
        下载热力图
      </button>
    </div>

    <div class="visual-container">
      <article class="image-box">
        <div class="box-head">
          <h4>原始影像</h4>
          <span class="box-chip">Original</span>
        </div>
        <div class="image-wrapper">
          <img :src="originalImage" alt="原始影像" class="medical-image" />
        </div>
      </article>

      <article class="image-box heatmap-box">
        <div class="box-head">
          <h4>AI 关注热力图</h4>
          <span class="box-chip">Hover For Legend</span>
        </div>
        <div class="heatmap-wrapper">
          <img
            v-if="heatmapBase64"
            :src="`data:image/png;base64,${heatmapBase64}`"
            alt="热力图"
            class="medical-image heatmap"
          />
          <div v-else class="loading-placeholder">
            <span>生成热力图失败或未提供</span>
          </div>

          <div class="legend-popover">
            <div class="legend-popover__header">
              <strong>颜色解释</strong>
              <span>Red-Orange-Blue</span>
            </div>
            <div class="legend-item">
              <span class="legend-swatch legend-swatch--red"></span>
              <div>
                <strong>红色</strong>
                <p>模型高度关注的可疑区域，通常代表高风险征象。</p>
              </div>
            </div>
            <div class="legend-item">
              <span class="legend-swatch legend-swatch--orange"></span>
              <div>
                <strong>橙色</strong>
                <p>过渡或中度可疑区域，建议结合临床结果继续复核。</p>
              </div>
            </div>
            <div class="legend-item">
              <span class="legend-swatch legend-swatch--blue"></span>
              <div>
                <strong>蓝色</strong>
                <p>低风险或背景组织区域，模型在此处关注较弱。</p>
              </div>
            </div>
          </div>
        </div>
      </article>
    </div>

    <div class="controls">
      <div class="legend-inline">
        <div class="legend-inline__item">
          <span class="legend-swatch legend-swatch--red"></span>
          <span>高风险</span>
        </div>
        <div class="legend-inline__item">
          <span class="legend-swatch legend-swatch--orange"></span>
          <span>复核区域</span>
        </div>
        <div class="legend-inline__item">
          <span class="legend-swatch legend-swatch--blue"></span>
          <span>低风险背景</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HeatmapViewer',
  props: {
    originalImage: {
      type: String,
      required: true
    },
    heatmapBase64: {
      type: String,
      default: ''
    },
    description: {
      type: String,
      default: '红橙蓝三色分别表示高关注、复核过渡和低风险背景区域，用于帮助医生理解模型聚焦位置。'
    }
  },
  methods: {
    downloadHeatmap() {
      if (!this.heatmapBase64) return
      const link = document.createElement('a')
      link.href = `data:image/png;base64,${this.heatmapBase64}`
      link.download = `medical_heatmap_${new Date().getTime()}.png`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }
  }
}
</script>

<style scoped>
.heatmap-viewer {
  margin-top: 2rem;
  padding: 1.6rem;
  border-radius: 28px;
  border: 1px solid rgba(125, 211, 252, 0.14);
  background: linear-gradient(180deg, rgba(7, 21, 36, 0.94), rgba(4, 12, 24, 0.98));
  box-shadow: 0 24px 60px rgba(2, 10, 20, 0.28);
  color: #eef7ff;
}

.heatmap-header,
.box-head,
.controls {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.section-kicker,
.box-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
}

.section-kicker {
  border: 1px solid rgba(94, 234, 212, 0.18);
  background: rgba(45, 212, 191, 0.08);
  color: #90f5e8;
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.heatmap-header {
  margin-bottom: 1.4rem;
}

.heatmap-header h3,
.image-box h4,
.legend-popover strong {
  color: #f7fbff;
}

.heatmap-header h3 {
  margin: 0.85rem 0 0.45rem;
  font-size: 1.8rem;
}

.description,
.legend-popover p,
.loading-placeholder,
.legend-inline__item span:last-child {
  color: rgba(220, 235, 255, 0.74);
  line-height: 1.75;
}

.btn-download {
  min-height: 44px;
  padding: 0 18px;
  border: none;
  border-radius: 999px;
  cursor: pointer;
  color: #03283f;
  font-size: 14px;
  font-weight: 700;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  box-shadow: 0 14px 30px rgba(56, 189, 248, 0.22);
}

.visual-container {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.25rem;
  margin-bottom: 1.4rem;
}

.image-box {
  padding: 1rem;
  border-radius: 24px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  background: rgba(8, 20, 35, 0.72);
}

.box-head {
  align-items: center;
  margin-bottom: 0.9rem;
}

.image-box h4 {
  margin: 0;
  font-size: 1rem;
}

.box-chip {
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: rgba(7, 20, 35, 0.82);
  color: #b6e8ff;
  font-size: 11px;
  letter-spacing: 0.06em;
}

.image-wrapper,
.heatmap-wrapper {
  position: relative;
  width: 100%;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  overflow: hidden;
  background: radial-gradient(circle at center, rgba(18, 53, 83, 0.4), rgba(1, 8, 18, 0.98) 74%);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.heatmap-wrapper::after {
  content: '';
  position: absolute;
  inset: 14px;
  border-radius: 16px;
  border: 1px solid rgba(125, 211, 252, 0.08);
  pointer-events: none;
}

.medical-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  padding: 18px;
}

.legend-popover {
  position: absolute;
  top: 16px;
  right: 16px;
  width: min(280px, calc(100% - 32px));
  padding: 14px;
  border-radius: 18px;
  border: 1px solid rgba(125, 211, 252, 0.14);
  background: linear-gradient(180deg, rgba(7, 20, 35, 0.96), rgba(4, 12, 24, 0.98));
  box-shadow: 0 20px 42px rgba(2, 10, 20, 0.28);
  opacity: 0;
  transform: translateY(8px) scale(0.98);
  transition: opacity 0.24s ease, transform 0.24s ease;
  pointer-events: none;
}

.heatmap-box:hover .legend-popover {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.legend-popover__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
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

.legend-item {
  display: grid;
  grid-template-columns: 14px 1fr;
  gap: 12px;
  align-items: flex-start;
  padding: 8px 0;
}

.legend-item p {
  margin: 6px 0 0;
  font-size: 12px;
}

.legend-swatch {
  width: 14px;
  height: 14px;
  margin-top: 3px;
  border-radius: 999px;
  box-shadow: 0 0 0 6px rgba(255, 255, 255, 0.03);
}

.legend-swatch--red {
  background: linear-gradient(135deg, #fb7185, #ef4444);
}

.legend-swatch--orange {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.legend-swatch--blue {
  background: linear-gradient(135deg, #38bdf8, #2563eb);
}

.controls {
  padding-top: 1rem;
  border-top: 1px solid rgba(125, 211, 252, 0.1);
}

.legend-inline {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.legend-inline__item {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
}

.loading-placeholder {
  font-size: 0.95rem;
}

@media (max-width: 768px) {
  .heatmap-header,
  .box-head,
  .controls {
    flex-direction: column;
    align-items: flex-start;
  }

  .visual-container {
    grid-template-columns: 1fr;
  }

  .btn-download {
    width: 100%;
  }
}
</style>
