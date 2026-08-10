<template>
  <div class="instruction-page" :class="{ 'medical-theme': isMedicalTheme }">
    <div v-if="isMedicalTheme" class="instruction-theme-grid"></div>

    <!-- ===== HERO ===== -->
    <div class="instr-hero">
      <div class="instr-hero-bg"></div>
      <div class="instr-hero-content">
        <div class="hero-eyebrow">使用说明</div>
        <h1 class="instr-hero-title">快速上手<br/>医学影像AI诊断</h1>
        <p class="instr-hero-sub">从影像上传到报告解读，五分钟掌握完整操作流程</p>
      </div>
    </div>

    <!-- ===== PROGRESS STEPS ===== -->
    <div class="progress-track-wrap">
      <div class="progress-track">
        <div class="pt-step" v-for="(s, i) in progressSteps" :key="i">
          <div class="pt-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="s.icon"></svg>
          </div>
          <span class="pt-label">{{ s.label }}</span>
          <div class="pt-line" v-if="i < progressSteps.length - 1"></div>
        </div>
      </div>
    </div>

    <!-- ===== MAIN ===== -->
    <div class="instr-container">

      <!-- ===== 操作指南 ===== -->
      <section class="instr-section">
        <div class="instr-section-label">操作指南</div>
        <h2 class="instr-section-title">三步完成影像分析</h2>
        <div class="guide-grid">
          <div class="guide-card" v-for="(g, i) in guideSteps" :key="i">
            <div class="guide-num">0{{ i + 1 }}</div>
            <div class="guide-icon-wrap" :class="g.color">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="g.icon"></svg>
            </div>
            <h3 class="guide-title">{{ g.title }}</h3>
            <p class="guide-desc">{{ g.desc }}</p>
            <div class="guide-tips">
              <div class="guide-tip" v-for="t in g.tips" :key="t">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#0ea5e9" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20,6 9,17 4,12"/></svg>
                {{ t }}
              </div>
            </div>
          </div>
        </div>

        <div class="instr-note">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#0ea5e9" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          <span>温馨提示：如需批量分析建议使用队列功能并在非高峰期提交，可大幅缩短等待时间。</span>
        </div>
      </section>

      <!-- ===== 功能说明 ===== -->
      <section class="instr-section">
        <div class="instr-section-label">功能说明</div>
        <h2 class="instr-section-title">核心功能模块</h2>
        <div class="feature-rows">
          <div class="feat-row" v-for="(f, i) in featureItems" :key="i">
            <div class="feat-row-icon" :class="f.color">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="f.icon"></svg>
            </div>
            <div class="feat-row-body">
              <h3 class="feat-row-title">{{ f.title }}</h3>
              <p class="feat-row-desc">{{ f.desc }}</p>
            </div>
            <div class="feat-row-chips">
              <span class="fchip" v-for="c in f.chips" :key="c">{{ c }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== FAQ ===== -->
      <section class="instr-section">
        <div class="instr-section-label">常见问题</div>
        <h2 class="instr-section-title">FAQ</h2>
        <div class="faq-list">
          <div
            class="faq-item"
            v-for="(faq, i) in faqs"
            :key="i"
            :class="{ open: openFaq === i }"
            @click="toggleFaq(i)"
          >
            <div class="faq-q">
              <span class="faq-q-badge">Q</span>
              <span class="faq-q-text">{{ faq.q }}</span>
              <svg class="faq-chevron" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="6,9 12,15 18,9"/></svg>
            </div>
            <div class="faq-a" v-show="openFaq === i">
              <p>{{ faq.a }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== GLOSSARY ===== -->
      <section class="instr-section">
        <div class="instr-section-header">
          <div>
            <div class="instr-section-label">专业术语</div>
            <h2 class="instr-section-title">术语解释</h2>
          </div>
          <div class="glossary-actions">
            <button class="gls-btn" @click="printGlossary">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6,9 6,2 18,2 18,9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/><rect x="6" y="14" width="12" height="8"/></svg>
              打印
            </button>
            <button class="gls-btn gls-btn-outline" @click="exportGlossary">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7,10 12,15 17,10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
              导出 JSON
            </button>
          </div>
        </div>
        <div class="glossary-grid">
          <div class="gls-card" v-for="term in glossaryTerms" :key="term.term">
            <div class="gls-term">{{ term.term }}</div>
            <div class="gls-en">{{ term.en }}</div>
            <p class="gls-desc">{{ term.desc }}</p>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Instruction',
  data() {
    return {
      openFaq: null,

      progressSteps: [
        { label: '上传影像', icon: '<polyline points="16,16 12,12 8,16"/><line x1="12" y1="12" x2="12" y2="21"/><path d="M20.39,18.39A5,5,0,0,0,18,9h-1.26A8,8,0,1,0,3,16.3"/>' },
        { label: 'AI分析', icon: '<path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>' },
        { label: '医生审核', icon: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><polyline points="17,11 19,13 23,9"/>' },
        { label: '查看报告', icon: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>' },
        { label: '导出分享', icon: '<path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><polyline points="16,6 12,2 8,6"/><line x1="12" y1="2" x2="12" y2="15"/>' }
      ],

      guideSteps: [
        {
          title: '上传影像文件',
          desc: '登录后进入"上传医学影像"页面，将影像文件拖拽或点击选择上传。系统自动识别影像类型与扫描部位。',
          color: 'gs-blue',
          icon: '<polyline points="16,16 12,12 8,16"/><line x1="12" y1="12" x2="12" y2="21"/><path d="M20.39,18.39A5,5,0,0,0,18,9h-1.26A8,8,0,1,0,3,16.3"/>',
          tips: ['支持 DICOM / NIFTI / PNG / JPG 格式', '建议移除多余压缩以提升分析质量', '单次最大上传 2GB，支持批量上传']
        },
        {
          title: '设置参数并分析',
          desc: '选择检测模式与置信度阈值，系统推荐分辨率范围 40–70 APM，平衡分析速度与精度。',
          color: 'gs-teal',
          icon: '<circle cx="12" cy="12" r="3"/><path d="M19.07 4.93l-1.41 1.41M4.93 4.93l1.41 1.41M12 2v2m0 18v-2m7.07-2.07l-1.41-1.41M5.34 18.66l1.41-1.41M2 12h2m18 0h-2"/>',
          tips: ['推荐使用"标准模式"获得最佳平衡', '高优先级任务可选"急诊模式"', '置信度阈值默认0.5，可按需调整']
        },
        {
          title: '查看结果与导出',
          desc: '分析完成后查看热力图、置信度评分与AI初步建议，等待医生审核签发后获取完整报告。',
          color: 'gs-green',
          icon: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14,2 14,8 20,8"/><polyline points="9,15 12,18 15,15"/><line x1="12" y1="12" x2="12" y2="18"/>',
          tips: ['支持导出 PDF / JSON 格式', '历史报告永久保存可随时查阅', '医生签发后自动推送短信通知']
        }
      ],

      featureItems: [
        {
          title: '影像预处理',
          desc: '自动完成归一化、去噪、伽马校正与对比度增强，显著提升模型在不同扫描设备下的鲁棒性，无需手动调参。',
          color: 'fi-blue',
          icon: '<rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21,15 16,10 5,21"/>',
          chips: ['归一化', '去噪滤波', '对比度增强', 'DICOM解析']
        },
        {
          title: 'AI 病灶检测',
          desc: '多尺度特征融合检测网络，输出边界框坐标、精细分割掩模与量化置信度评分，支持毫米级精准测量。',
          color: 'fi-teal',
          icon: '<polyline points="22,12 18,12 15,21 9,3 6,12 2,12"/>',
          chips: ['多尺度检测', '候选区聚合', '置信度评分', '测量标注']
        },
        {
          title: '可解释性可视化',
          desc: '集成 Grad-CAM 与 SHAP 热力图技术，直观展示模型关注区域与关键特征贡献，帮助医生理解AI决策依据。',
          color: 'fi-purple',
          icon: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>',
          chips: ['Grad-CAM', 'SHAP分析', '热力图叠加', '特征解释']
        },
        {
          title: '结构化报告生成',
          desc: '基于诊断结果自动生成符合临床规范的结构化报告，包含影像描述、测量数据、鉴别诊断与随访建议，支持导出。',
          color: 'fi-amber',
          icon: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>',
          chips: ['自动生成', 'PDF导出', '医生签名', 'HIS对接']
        }
      ],

      faqs: [
        { q: '如何提升分析速度？', a: '推荐使用较小分辨率影像并开启轻量模式，或分批次提交，避开早9点至11点的高峰时段。急诊任务可使用优先通道，通常15分钟内出具结果。' },
        { q: '分析结果可以直接作为诊断依据吗？', a: 'AI仅为临床辅助工具，结果供参考。最终诊断必须由具有执业资质的临床医生结合完整影像资料、临床症状与病史综合判断，平台报告均需医生审核签发后才具有参考价值。' },
        { q: '支持哪些影像格式？', a: '支持标准医学影像格式 DICOM（.dcm）、NIFTI（.nii/.nii.gz），以及通用图像格式 PNG、JPG/JPEG。建议优先使用 DICOM 格式以获得最佳分析效果。' },
        { q: '患者数据如何保护隐私？', a: '所有上传数据经 AES-256 全程加密传输与存储，通过等保三级认证，数据仅用于本次诊断分析，不用于模型训练。您可随时申请删除历史数据。' },
        { q: '报告多久可以收到？', a: 'AI初步分析通常在2秒内完成。医生审核签发的完整报告：普通任务平均30分钟内完成，急诊优先通道15分钟内，特殊复杂病例可能需要1-2小时，签发后即时短信通知。' },
        { q: '如何与医院HIS系统对接？', a: '我们提供标准化 RESTful API 与 HL7/FHIR 接口，支持与主流HIS、PACS系统无缝集成。有对接需求请联系商务团队，平均实施周期2周内完成。' }
      ],

      glossaryTerms: [
        { term: '归一化', en: 'Normalization', desc: '将影像像素值映射到统一范围（如0-1），消除不同扫描设备、扫描参数导致的强度差异，提升模型泛化能力。' },
        { term: '去噪', en: 'Denoising', desc: '使用高斯滤波、中值滤波或深度学习去噪网络抑制随机噪声，提高影像结构可见性，降低假阳性率。' },
        { term: '热力图', en: 'Heatmap / Grad-CAM', desc: '基于梯度类激活映射（Grad-CAM）的可视化技术，以颜色深浅标识模型在决策时关注的空间区域，增强可解释性。' },
        { term: '多尺度检测', en: 'Multi-scale Detection', desc: '在特征金字塔的不同分辨率层上分别进行病灶检测，同时具备识别大病灶与微小结节的能力，召回率显著提升。' },
        { term: '置信度', en: 'Confidence Score', desc: '模型对每条检测结果的概率估计值（0-1），用于阈值筛选与结果排序。通常0.5以上被视为有效检测结果。' },
        { term: '分割掩模', en: 'Segmentation Mask', desc: '像素级别的预测标注结果，以二值或多类别图像形式标记病灶的精确形状、边界与位置，用于测量体积与面积。' },
        { term: 'DICOM', en: 'DICOM', desc: '医学数字影像和通信标准（Digital Imaging and Communications in Medicine），用于存储、传输和管理医学影像及相关信息的行业标准格式。' },
        { term: 'SHAP分析', en: 'SHAP Analysis', desc: '基于博弈论的特征重要性解释方法，量化每个输入特征对模型预测结果的贡献度，提供全局与局部的可解释性分析。' }
      ]
    }
  },
  computed: {
    currentTheme() {
      return this.$store.state.theme || 'dark'
    },
    isMedicalTheme() {
      return this.currentTheme === 'medical-ai'
    }
  },
  methods: {
    toggleFaq(i) {
      this.openFaq = this.openFaq === i ? null : i
    },
    printGlossary() {
      window.print()
    },
    exportGlossary() {
      const terms = this.glossaryTerms.map(t => ({ term: t.term, en: t.en, desc: t.desc }))
      const blob = new Blob([JSON.stringify(terms, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'glossary.json'
      a.click()
      URL.revokeObjectURL(url)
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.instruction-page {
  position: relative;
  isolation: isolate;
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  background: #f8fafc;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.instruction-page > * {
  position: relative;
  z-index: 1;
}

.instruction-theme-grid {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background-image:
    linear-gradient(rgba(38, 84, 126, 0.055) 1px, transparent 1px),
    linear-gradient(90deg, rgba(38, 84, 126, 0.055) 1px, transparent 1px),
    linear-gradient(rgba(38, 84, 126, 0.012) 1px, transparent 1px),
    linear-gradient(90deg, rgba(38, 84, 126, 0.012) 1px, transparent 1px);
  background-size: 64px 64px, 64px 64px, 8px 8px, 8px 8px;
  background-position: center top;
  opacity: 0.9;
}

/* ===== HERO ===== */
.instr-hero {
  position: relative;
  padding: clamp(40px, 6vw, 72px) 32px 72px;
  background: linear-gradient(135deg, #050f1a 0%, #071e2e 50%, #0a2540 100%);
  overflow: hidden;
}

.instr-hero-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 70% 80% at 80% 50%, rgba(14,165,233,0.1) 0%, transparent 60%),
    radial-gradient(ellipse 40% 60% at 10% 80%, rgba(16,185,129,0.07) 0%, transparent 55%);
  pointer-events: none;
}

.instr-hero-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(14,165,233,0.04) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(14,165,233,0.04) 1px, transparent 1px);
  background-size: 48px 48px;
}

.instruction-page.medical-theme .instr-hero-bg::after {
  background-image:
    linear-gradient(rgba(38, 84, 126, 0.055) 1px, transparent 1px),
    linear-gradient(90deg, rgba(38, 84, 126, 0.055) 1px, transparent 1px),
    linear-gradient(rgba(38, 84, 126, 0.012) 1px, transparent 1px),
    linear-gradient(90deg, rgba(38, 84, 126, 0.012) 1px, transparent 1px);
  background-size: 64px 64px, 64px 64px, 8px 8px, 8px 8px;
  background-position: center top;
}

.instr-hero-content {
  position: relative;
  z-index: 1;
  max-width: 680px;
  margin: 0 auto;
  text-align: center;
}

.hero-eyebrow {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #38bdf8;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.hero-eyebrow::before,
.hero-eyebrow::after {
  content: '';
  width: 24px;
  height: 1.5px;
  background: rgba(56,189,248,0.5);
  border-radius: 1px;
}

.instr-hero-title {
  font-size: clamp(2rem, 4vw, 2.8rem);
  font-weight: 800;
  color: #f8fafc;
  line-height: 1.2;
  letter-spacing: -0.02em;
  margin-bottom: 16px;
}

.instr-hero-sub {
  font-size: 16px;
  color: rgba(226,232,240,0.6);
  line-height: 1.7;
}

/* ===== PROGRESS TRACK ===== */
.progress-track-wrap {
  background: #fff;
  border-bottom: 1px solid #e9eef5;
  padding: 20px 32px;
  position: sticky;
  top: var(--app-top-nav-height, 68px);
  z-index: 90;
}

.progress-track {
  max-width: 860px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 0;
}

.pt-step {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  position: relative;
}

.pt-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 10px rgba(14,165,233,0.3);
}

.pt-label {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  white-space: nowrap;
}

.pt-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, #0ea5e9, #e2e8f0);
  border-radius: 1px;
  margin: 0 8px;
}

/* ===== CONTAINER ===== */
.instr-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 32px 80px;
}

/* ===== SECTION ===== */
.instr-section {
  padding: 64px 0;
  border-bottom: 1px solid #e9eef5;
}

.instr-section:last-child { border-bottom: none; }

.instr-section-label {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #0ea5e9;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.instr-section-label::before {
  content: '';
  width: 20px;
  height: 2px;
  background: #0ea5e9;
  border-radius: 1px;
}

.instr-section-title {
  font-size: clamp(1.4rem, 2.5vw, 1.9rem);
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
  margin-bottom: 36px;
}

.instr-section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
  gap: 16px;
}

.instr-section-header .instr-section-title { margin-bottom: 0; }

/* ===== GUIDE CARDS ===== */
.guide-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.guide-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 28px;
  position: relative;
  transition: all 0.25s ease;
}

.guide-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 10px 32px rgba(0,0,0,0.07);
  transform: translateY(-4px);
}

.guide-num {
  font-size: 11px;
  font-weight: 700;
  color: rgba(14,165,233,0.4);
  font-family: monospace;
  letter-spacing: 0.1em;
  margin-bottom: 14px;
}

.guide-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}

.gs-blue  { background: #eff6ff; color: #1d4ed8; }
.gs-teal  { background: #f0fdfa; color: #0d9488; }
.gs-green { background: #f0fdf4; color: #15803d; }

.guide-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 10px;
}

.guide-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.7;
  margin-bottom: 18px;
}

.guide-tips {
  display: flex;
  flex-direction: column;
  gap: 7px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.guide-tip {
  display: flex;
  align-items: flex-start;
  gap: 7px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.instr-note {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 10px;
  font-size: 14px;
  color: #1e40af;
}

/* ===== FEATURE ROWS ===== */
.feature-rows {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feat-row {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.25s ease;
}

.feat-row:hover {
  border-color: #93c5fd;
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
  transform: translateX(4px);
}

.feat-row-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.fi-blue   { background: #eff6ff; color: #1d4ed8; }
.fi-teal   { background: #f0fdfa; color: #0d9488; }
.fi-purple { background: #f5f3ff; color: #7c3aed; }
.fi-amber  { background: #fffbeb; color: #b45309; }

.feat-row-body {
  flex: 1;
  min-width: 0;
}

.feat-row-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.feat-row-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.65;
}

.feat-row-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex-shrink: 0;
}

.fchip {
  padding: 3px 10px;
  background: #f1f5f9;
  border-radius: 20px;
  font-size: 11px;
  color: #475569;
  font-weight: 500;
}

/* ===== FAQ ===== */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.faq-item {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
}

.faq-item:hover,
.faq-item.open {
  border-color: #93c5fd;
}

.faq-item.open {
  box-shadow: 0 4px 16px rgba(14,165,233,0.1);
}

.faq-q {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  user-select: none;
}

.faq-q-badge {
  width: 26px;
  height: 26px;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.faq-q-text {
  flex: 1;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.faq-chevron {
  color: #94a3b8;
  transition: transform 0.25s ease;
  flex-shrink: 0;
}

.faq-item.open .faq-chevron {
  transform: rotate(180deg);
  color: #0ea5e9;
}

.faq-a {
  padding: 0 20px 18px 60px;
}

.faq-a p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.8;
}

/* ===== GLOSSARY ===== */
.glossary-actions {
  display: flex;
  gap: 10px;
}

.gls-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 18px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #0ea5e9;
  color: #fff;
  border: none;
}

.gls-btn:hover {
  background: #0284c7;
}

.gls-btn-outline {
  background: transparent;
  color: #0ea5e9;
  border: 1px solid #bfdbfe;
}

.gls-btn-outline:hover {
  background: #eff6ff;
}

.glossary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.gls-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 20px;
  transition: all 0.25s ease;
}

.gls-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
  transform: translateY(-3px);
}

.gls-term {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 3px;
}

.gls-en {
  font-size: 11px;
  color: #0ea5e9;
  font-weight: 500;
  letter-spacing: 0.03em;
  margin-bottom: 10px;
  font-family: monospace;
}

.gls-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.7;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 960px) {
  .progress-track-wrap { position: static; }
  .glossary-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .instr-container { padding: 0 20px 60px; }
  .guide-grid { grid-template-columns: 1fr; }
  .feat-row { flex-wrap: wrap; }
  .feat-row-chips { width: 100%; }
  .glossary-grid { grid-template-columns: 1fr; }
  .instr-section-header { flex-direction: column; align-items: flex-start; }
  .progress-track { overflow-x: auto; padding-bottom: 4px; }
}

@media print {
  .instr-hero, .progress-track-wrap, .glossary-actions { display: none; }
  .instruction-page { background: #fff; }
  .gls-card, .guide-card, .feat-row { box-shadow: none; }
}
</style>
