<template>
  <div class="dashboard-page">
    <button class="floating-back" type="button" @click="goBack" aria-label="返回患者首页">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 5l-7 7 7 7"/>
      </svg>
    </button>

    <div class="page-shell">
      <section class="hero-panel">
        <div>
          <span class="section-badge">AI 医学影像分析中心</span>
          <h1>围绕上传、分析、报告输出构建可直接承接业务的诊断工作台</h1>
          <p>
            页面以商业化落地为目标，将影像上传、分析结果、平台价值和后续动作集中呈现，帮助用户快速理解平台能力并进入核心流程。
          </p>
          <div class="hero-actions">
            <router-link to="/upload" class="hero-btn primary">专家诊断入口</router-link>
            <router-link to="/reports" class="hero-btn secondary">查看历史报告</router-link>
          </div>
        </div>

        <div class="hero-stats">
          <article class="stat-card">
            <strong>快速预判</strong>
            <span>自动识别重点区域，缩短首轮分析时间</span>
          </article>
          <article class="stat-card">
            <strong>结果结构化</strong>
            <span>统一沉淀诊断摘要、置信度和建议</span>
          </article>
          <article class="stat-card">
            <strong>AI + 医生</strong>
            <span>把自动分析与专家复核串成完整闭环</span>
          </article>
        </div>
      </section>

      <section class="workspace-grid">
        <article class="card-surface upload-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">开始分析</div>
              <h2>上传医学影像并触发智能诊断</h2>
            </div>
          </div>
          <UploadImage @upload-complete="handleUploadComplete" />
        </article>

        <article class="card-surface result-card">
          <div class="card-head">
            <div>
              <div class="section-kicker">分析结果</div>
              <h2>{{ currentAnalysis ? '已生成最新分析结果' : '等待分析结果生成' }}</h2>
            </div>
            <router-link v-if="currentAnalysis" to="/reports" class="text-link">查看全部报告</router-link>
          </div>

          <AnalysisResult v-if="currentAnalysis" :analysis="currentAnalysis" />

          <div v-else class="empty-result">
            <div class="empty-icon">AI</div>
            <h3>上传后即可在这里查看分析结果</h3>
            <p>系统会显示影像识别结果、风险提示和诊疗建议，并进一步衔接报告中心与病例详情页。</p>
          </div>
        </article>
      </section>

      <section class="feature-section card-surface">
        <div class="feature-head">
          <div>
            <div class="section-kicker">平台价值</div>
            <h2>不仅能分析影像，也能支撑整个诊疗服务流程</h2>
          </div>
        </div>

        <div class="feature-grid">
          <article class="feature-card">
            <h3>上传即分析</h3>
            <p>患者上传后即可开始 AI 预分析，减少等待，提升诊疗流转效率。</p>
          </article>
          <article class="feature-card">
            <h3>结果可追溯</h3>
            <p>每一次分析结果都可以沉淀到历史报告和病例详情，便于长期追踪。</p>
          </article>
          <article class="feature-card">
            <h3>支持继续深挖</h3>
            <p>用户可以从分析中心继续进入深度分析、病例详情和医患沟通页面。</p>
          </article>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import explain from '../directives/explain.js'
import { useStore } from 'vuex'
import UploadImage from '../components/UploadImage.vue'
import AnalysisResult from '../components/AnalysisResult.vue'

export default {
  name: 'Dashboard',
  directives: { explain },
  components: {
    UploadImage,
    AnalysisResult
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const currentAnalysis = computed(() => store.state.currentAnalysis)

    const goBack = () => {
      router.push('/patient/home')
    }

    const handleUploadComplete = (analysis) => {
      store.commit('setAnalysis', analysis)
    }

    return {
      currentAnalysis,
      handleUploadComplete,
      goBack
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.dashboard-page {
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  background:
    radial-gradient(circle at top left, rgba(20, 184, 166, 0.16), transparent 28%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.16), transparent 26%),
    linear-gradient(180deg, #07111d 0%, #091827 100%);
  color: #eef7ff;
  padding: 8px var(--app-shell-gutter, 32px) 48px;
}

.page-shell {
  max-width: 1280px;
  margin: 0 auto;
}

.floating-back {
  position: fixed;
  top: 140px;
  left: 24px;
  width: 46px;
  height: 46px;
  border: 1px solid rgba(125, 211, 252, 0.18);
  border-radius: 14px;
  background: rgba(5, 15, 28, 0.86);
  color: #ecf9ff;
  cursor: pointer;
}

.hero-panel,
.card-surface {
  border: 1px solid rgba(125, 211, 252, 0.12);
  background: linear-gradient(180deg, rgba(7, 21, 36, 0.94), rgba(4, 12, 24, 0.96));
  box-shadow: 0 22px 54px rgba(2, 10, 20, 0.28);
}

.hero-panel {
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 22px;
  padding: 30px;
  border-radius: 30px;
}

.section-badge,
.section-kicker,
.hero-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
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
.card-head h2,
.feature-head h2 {
  color: #f7fbff;
}

.hero-panel h1 {
  margin: 16px 0 14px;
  font-size: clamp(30px, 4vw, 48px);
  line-height: 1.1;
}

.hero-panel p,
.feature-card p,
.empty-result p {
  color: rgba(220, 235, 255, 0.72);
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 24px;
}

.hero-btn {
  min-height: 46px;
  padding: 0 18px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 700;
}

.hero-btn.primary {
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
}

.hero-btn.secondary {
  background: rgba(8, 20, 35, 0.82);
  border: 1px solid rgba(125, 211, 252, 0.16);
  color: #ecf8ff;
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
  font-size: 22px;
  color: #ffffff;
  margin-bottom: 8px;
}

.workspace-grid {
  display: grid;
  grid-template-columns: minmax(320px, 0.92fr) minmax(360px, 1.08fr);
  gap: 22px;
  margin-top: 24px;
  align-items: start;
}

.upload-card,
.result-card,
.feature-section {
  padding: 24px;
  border-radius: 26px;
}

.upload-card,
.result-card {
  min-height: 100%;
}

.result-card {
  overflow: hidden;
}

.card-head,
.feature-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.card-head h2,
.feature-head h2 {
  margin-top: 10px;
  font-size: 28px;
  line-height: 1.2;
}

.text-link {
  color: #9fddff;
  text-decoration: none;
  font-weight: 600;
}

.empty-result {
  min-height: 360px;
  border-radius: 22px;
  border: 1px dashed rgba(125, 211, 252, 0.16);
  display: grid;
  place-items: center;
  text-align: center;
  padding: 26px;
}

.empty-icon {
  width: 76px;
  height: 76px;
  margin: 0 auto 16px;
  border-radius: 24px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #5eead4, #38bdf8);
  color: #032032;
  font-size: 28px;
  font-weight: 800;
}

.empty-result h3 {
  margin-bottom: 10px;
  color: #f7fbff;
}

.feature-section {
  margin-top: 24px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.feature-card {
  padding: 20px;
  border-radius: 22px;
  background: rgba(8, 20, 35, 0.72);
  border: 1px solid rgba(125, 211, 252, 0.08);
}

.feature-card h3 {
  margin-bottom: 10px;
  color: #f7fbff;
}

@media (max-width: 1080px) {
  .hero-panel,
  .workspace-grid,
  .feature-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 20px 12px 32px;
  }

  .hero-panel,
  .upload-card,
  .result-card,
  .feature-section {
    padding: 22px;
    border-radius: 24px;
  }

  .hero-actions,
  .card-head,
  .feature-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-btn {
    width: 100%;
  }
}
</style>
