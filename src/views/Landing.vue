<template>
  <div class="landing-page" :class="{ 'medical-theme': isMedicalTheme }">
    <div v-if="isMedicalTheme" class="landing-theme-grid"></div>
    <section class="hero-section">
      <div class="hero-bg-mesh"></div>
      <div class="hero-bg-grid"></div>
      <div class="hero-particles">
        <span
          v-for="i in 12"
          :key="i"
          class="particle"
          :style="particleStyle(i)"
        ></span>
      </div>

      <div class="hero-inner">
        <div class="hero-left">
          <div class="hero-badge">
            <span class="badge-pulse"></span>
            AI + 医生双核驱动 · 医学影像智能诊断平台
          </div>
          <h1 class="hero-title">
            让每一张影像<br />
            <em class="title-highlight">精准说话</em>
          </h1>
          <p class="hero-desc">
            融合顶尖放射科医生经验与深度学习大模型，毫秒级完成肺结节、脑出血、骨折等多病种影像读片，为临床决策提供权威辅助支撑。
          </p>
          <div class="hero-tags">
            <span v-for="t in heroTags" :key="t" class="htag">{{ t }}</span>
          </div>
          <div class="hero-actions">
            <router-link to="/login" class="btn-primary-hero">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10" />
                <polygon points="10,8 16,12 10,16 10,8" fill="currentColor" stroke="none" />
              </svg>
              立即体验诊断
            </router-link>
            <a href="#workflow" class="btn-ghost-hero">查看工作流程</a>
          </div>
          <div class="hero-trust">
            <div v-for="s in heroStats" :key="s.label" class="trust-item">
              <span class="trust-num">{{ s.num }}</span>
              <span class="trust-label">{{ s.label }}</span>
            </div>
          </div>
        </div>

        <div
          ref="heroDeck"
          class="hero-right hero-img-deck"
          @mousemove="onHeroMouseMove"
          @mouseleave="onHeroMouseLeave"
        >
          <div
            v-for="(card, idx) in heroImgCards"
            :key="idx"
            class="hero-img-card"
            :class="['hic-' + idx, { 'is-active': heroActiveCard === idx }]"
            :style="heroCardStyle(idx)"
            @mouseenter="heroActiveCard = idx"
            @mouseleave="heroActiveCard = null"
          >
            <div class="hic-img-wrap">
              <img :src="card.src" :alt="card.label" class="hic-img" loading="lazy" />
              <div class="hic-scan-line"></div>
              <div class="hic-top-bar">
                <span class="hic-live">
                  <span class="hic-live-dot"></span>AI分析中
                </span>
                <span class="hic-type">{{ card.type }}</span>
              </div>
              <div v-if="card.badge" class="hic-badge">
                <span class="hic-badge-icon">{{ card.badge.icon }}</span>
                <div>
                  <div class="hic-badge-val">{{ card.badge.val }}</div>
                  <div class="hic-badge-sub">{{ card.badge.sub }}</div>
                </div>
              </div>
            </div>
            <div class="hic-footer">
              <div class="hic-label">{{ card.label }}</div>
              <div class="hic-acc">
                <svg width="10" height="10" viewBox="0 0 24 24" fill="#10b981" stroke="none">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
                {{ card.acc }}
              </div>
            </div>
          </div>

          <div class="hid-cursor-glow" :style="heroCursorStyle"></div>

          <div class="hid-status-bar">
            <div class="hsb-dot"></div>
            <span>{{ heroImgCards.length }} 张影像正在分析 · 双核驱动</span>
            <div class="hsb-progress">
              <div class="hsb-fill"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="logos-section">
      <p class="logos-label">已服务全国多家三甲医院与医疗机构</p>
      <div class="logos-track">
        <div class="logos-inner">
          <span v-for="(h, index) in marqueeHospitals" :key="`${h}-${index}`" class="logo-pill">{{ h }}</span>
        </div>
      </div>
    </section>

    <section class="showcase-section">
      <div class="section-container">
        <div class="sec-header">
          <div class="sec-tag">真实场景</div>
          <h2 class="sec-title">影像诊断的全新方式</h2>
          <p class="sec-sub">从CT读片到病理分析，AI赋能每一个诊断环节</p>
        </div>

        <div class="fan-deck" @mouseleave="fanActiveIdx = null">
          <div
            v-for="(card, idx) in fanCards"
            :key="idx"
            class="fan-card"
            :class="[
              `fan-card-${idx}`,
              { 'fan-card-active': fanActiveIdx === idx, 'fan-card-peek': fanActiveIdx !== null && fanActiveIdx !== idx }
            ]"
            @mouseenter="fanActiveIdx = idx"
          >
            <div class="fan-card-img-wrap">
              <img :src="card.src" :alt="card.label" class="fan-card-img" loading="lazy" />
              <div class="fan-card-overlay"></div>
            </div>
            <div class="fan-card-body">
              <div class="fan-card-eyebrow">{{ String(idx + 1).padStart(2, '0') }} // {{ card.tag }}</div>
              <h3 class="fan-card-title">{{ card.title }}</h3>
              <p class="fan-card-desc">{{ card.desc }}</p>
              <div class="fan-card-acc">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="#10b981" stroke="none">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
                {{ card.acc }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="features" class="features-section">
      <div class="section-container">
        <div class="sec-header">
          <div class="sec-tag">核心能力</div>
          <h2 class="sec-title">AI + 医生，双重保障</h2>
          <p class="sec-sub">
            深度学习模型提供毫秒级初筛，资深医师在线复核，形成双重质控闭环，让诊断既快又准。
          </p>
        </div>

        <div class="features-grid">
          <div class="feat-card feat-card--lg">
            <div class="feat-icon-wrap feat-icon-blue">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" />
                <circle cx="8.5" cy="8.5" r="1.5" />
                <polyline points="21,15 16,10 5,21" />
              </svg>
            </div>
            <h3 class="feat-title">多模态影像识别</h3>
            <p class="feat-desc">
              支持CT、MRI、X光、超声、PET等多种影像格式，覆盖肺部、脑部、腹部、骨骼等15+解剖区域，病灶检测灵敏度达97.3%。
            </p>
            <div class="feat-chip-list">
              <span v-for="c in ['CT扫描', '核磁MRI', '数字X光', '超声影像', 'PET-CT']" :key="c" class="feat-chip">{{ c }}</span>
            </div>
          </div>

          <div class="feat-card feat-card--sm">
            <div class="feat-icon-wrap feat-icon-green">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                <polyline points="14,2 14,8 20,8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
              </svg>
            </div>
            <h3 class="feat-title">结构化诊断报告</h3>
            <p class="feat-desc">
              AI自动生成符合临床规范的结构化报告，涵盖影像描述、测量数据、鉴别诊断与随访建议，支持一键导出PDF。
            </p>
          </div>

          <div class="feat-card feat-card--sm">
            <div class="feat-icon-wrap feat-icon-amber">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
            </div>
            <h3 class="feat-title">医生在线审核</h3>
            <p class="feat-desc">
              汇聚三甲医院主任、副主任医师，7×24小时在线值守，AI初筛后由医生复核签发，平均签发时长＜30分钟。
            </p>
          </div>

          <div class="feat-card feat-card--sm">
            <div class="feat-icon-wrap feat-icon-purple">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="22,12 18,12 15,21 9,3 6,12 2,12" />
              </svg>
            </div>
            <h3 class="feat-title">实时质控监控</h3>
            <p class="feat-desc">
              全链路诊断质量监控仪表盘，关键指标实时追踪，异常病例自动预警，确保诊断质量持续在线。
            </p>
          </div>

          <div class="feat-card feat-card--sm">
            <div class="feat-icon-wrap feat-icon-teal">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              </svg>
            </div>
            <h3 class="feat-title">数据安全合规</h3>
            <p class="feat-desc">
              符合三级等保要求，患者数据全程加密传输与存储，满足HIPAA与国家医疗数据安全规范，零泄漏承诺。
            </p>
          </div>
        </div>
      </div>
    </section>

    <section id="workflow" class="workflow-section">
      <div class="section-container">
        <div class="sec-header">
          <div class="sec-tag">诊断流程</div>
          <h2 class="sec-title">四步完成专业影像诊断</h2>
          <p class="sec-sub">从影像上传到报告签发，全流程数字化管理，最快30分钟获得医生审核报告。</p>
        </div>

        <div class="workflow-steps">
          <div v-for="(step, i) in workflowSteps" :key="i" class="wf-step">
            <div class="wf-num">{{ String(i + 1).padStart(2, '0') }}</div>
            <div class="wf-icon-wrap" :class="step.color" v-html="step.icon"></div>
            <h3 class="wf-title">{{ step.title }}</h3>
            <p class="wf-desc">{{ step.desc }}</p>
            <div v-if="i < workflowSteps.length - 1" class="wf-arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="5" y1="12" x2="19" y2="12" />
                <polyline points="12,5 19,12 12,19" />
              </svg>
            </div>
          </div>
        </div>

        <div class="workflow-note">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#0ea5e9" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="8" x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
          急诊通道支持优先处理，报告最快15分钟内出具，危急值实时短信推送。
        </div>
      </div>
    </section>

    <section class="stats-section">
      <div class="stats-bg-glow"></div>
      <div class="section-container">
        <div class="stats-grid">
          <div v-for="s in platformStats" :key="s.label" class="stat-card">
            <div class="stat-icon" :class="s.color">
              <span v-html="s.icon"></span>
            </div>
            <div class="stat-number">{{ s.number }}</div>
            <div class="stat-label">{{ s.label }}</div>
            <div v-if="s.trend" class="stat-trend">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="#10b981" stroke="none">
                <polygon points="12,2 22,22 2,22" />
              </svg>
              {{ s.trend }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="diseases-section">
      <div class="section-container">
        <div class="sec-header">
          <div class="sec-tag">覆盖病种</div>
          <h2 class="sec-title">25+ 病种全面覆盖</h2>
          <p class="sec-sub">持续扩展病种库，覆盖肺部、神经、心血管、消化、肌骨等多学科领域。</p>
        </div>
        <div class="disease-tabs">
          <button
            v-for="(cat, i) in diseaseCategories"
            :key="cat.name"
            class="dis-tab"
            :class="{ active: activeDiseaseTab === i }"
            @click="activeDiseaseTab = i"
          >
            {{ cat.name }}
          </button>
        </div>
        <div class="disease-cards">
          <div v-for="d in diseaseCategories[activeDiseaseTab].items" :key="d.name" class="dis-card">
            <div class="dis-card-icon" :class="d.color"></div>
            <span class="dis-name">{{ d.name }}</span>
            <span class="dis-acc">{{ d.acc }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="testimonials-section">
      <div class="section-container">
        <div class="sec-header">
          <div class="sec-tag">用户评价</div>
          <h2 class="sec-title">医生与患者的真实反馈</h2>
        </div>
        <div class="testi-grid">
          <div v-for="t in testimonials" :key="t.name" class="testi-card">
            <div class="testi-quote">"</div>
            <p class="testi-text">{{ t.text }}</p>
            <div class="testi-author">
              <div class="testi-avatar" :class="t.color">{{ t.initial }}</div>
              <div>
                <div class="testi-name">{{ t.name }}</div>
                <div class="testi-role">{{ t.role }}</div>
              </div>
              <div class="testi-stars">
                <svg v-for="s in 5" :key="s" width="12" height="12" viewBox="0 0 24 24" fill="#f59e0b" stroke="none">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="cta-glow"></div>
      <div class="cta-inner">
        <div class="cta-badge">限时免费体验</div>
        <h2 class="cta-title">开启智能影像诊断新时代</h2>
        <p class="cta-sub">注册即可免费体验10次AI影像分析，无需信用卡，即刻开始</p>
        <div class="cta-actions">
          <router-link to="/register" class="cta-btn-primary">
            免费注册体验
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12,5 19,12 12,19" />
            </svg>
          </router-link>
          <router-link to="/about" class="cta-btn-sec">了解合作方案</router-link>
        </div>
        <div class="cta-features">
          <span v-for="f in ctaFeatures" :key="f">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20,6 9,17 4,12" />
            </svg>
            {{ f }}
          </span>
        </div>
      </div>
    </section>

    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="footer-logo-icon"></div>
            <span>影智医疗</span>
          </div>
          <p class="footer-tagline">AI + 医生双核驱动<br />医学影像智能诊断服务平台</p>
        </div>
        <div v-for="col in footerCols" :key="col.title" class="footer-links">
          <div class="footer-col-title">{{ col.title }}</div>
          <a v-for="l in col.links" :key="l" href="#" class="footer-link">{{ l }}</a>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2025 影智医疗科技有限公司 · 沪ICP备XXXXXXXX号</span>
        <span>医疗器械注册证：国械注准XXXXXXXXXXXXXXXX</span>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'Landing',
  data() {
    return {
      heroImgCards: [
        {
          src: 'https://images.unsplash.com/photo-1559757175-0eb30cd8c063?w=560&q=85&auto=format&fit=crop',
          label: 'CT 肺部结节精准定位',
          type: 'CT · 肺部',
          acc: '准确率 97.8%',
          badge: { icon: '⚡', val: '2.1s', sub: 'AI分析' }
        },
        {
          src: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=560&q=85&auto=format&fit=crop',
          label: 'MRI 脑部病灶识别',
          type: 'MRI · 脑部',
          acc: '准确率 98.6%',
          badge: { icon: '🧠', val: '98.6%', sub: '识别率' }
        },
        {
          src: 'https://images.unsplash.com/photo-1530026405186-ed1f139313f8?w=560&q=85&auto=format&fit=crop',
          label: '数字 X 光骨折检测',
          type: 'X-Ray · 骨骼',
          acc: '准确率 98.9%',
          badge: null
        }
      ],
      heroMouse: { x: 0, y: 0 },
      heroActiveCard: null,
      heroCursorPos: { x: 0, y: 0, opacity: 0 },
      activeDiseaseTab: 0,
      heroTags: ['FDA认证算法', 'DICOM标准', '二级医疗器械', '等保三级'],
      heroStats: [
        { num: '98.7%', label: '诊断准确率' },
        { num: '<2s', label: 'AI分析速度' },
        { num: '500+', label: '合作医院' },
        { num: '200万+', label: '已处理病例' }
      ],
      hospitals: [
        '北京协和医院',
        '上海瑞金医院',
        '中山大学附属医院',
        '四川大学华西医院',
        '复旦大学附属华山医院',
        '北京大学人民医院',
        '解放军总医院',
        '南京鼓楼医院'
      ],
      workflowSteps: [
        {
          title: '上传影像',
          desc: '支持DICOM、JPEG、PNG等格式，拖拽即可上传，自动识别影像类型与扫描部位。',
          color: 'step-blue',
          icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="16,16 12,12 8,16"/><line x1="12" y1="12" x2="12" y2="21"/><path d="M20.39,18.39A5,5,0,0,0,18,9h-1.26A8,8,0,1,0,3,16.3"/></svg>'
        },
        {
          title: 'AI智能分析',
          desc: '深度学习模型秒级完成病灶识别、测量定位与风险分层，生成初步诊断报告。',
          color: 'step-teal',
          icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>'
        },
        {
          title: '医生审核',
          desc: '三甲医院执业医师在线审阅AI报告，结合临床背景补充完善，确保诊断质量。',
          color: 'step-green',
          icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><polyline points="17,11 19,13 23,9"/></svg>'
        },
        {
          title: '报告签发',
          desc: '医生电子签名后报告自动推送，支持PDF下载、HIS系统对接，历史报告永久存档。',
          color: 'step-amber',
          icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14,2 14,8 20,8"/><polyline points="9,15 12,18 15,15"/><line x1="12" y1="12" x2="12" y2="18"/></svg>'
        }
      ],
      platformStats: [
        { number: '98.7%', label: '综合诊断准确率', trend: '同比提升1.2%', color: 'stat-blue', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22,12 18,12 15,21 9,3 6,12 2,12"/></svg>' },
        { number: '200万+', label: '累计处理病例', trend: '月均增长15%', color: 'stat-green', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>' },
        { number: '500+', label: '合作医疗机构', color: 'stat-teal', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9,22 9,12 15,12 15,22"/></svg>' },
        { number: '<2秒', label: 'AI分析响应速度', color: 'stat-amber', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12,6 12,12 16,14"/></svg>' },
        { number: '7×24h', label: '医生在线值守', color: 'stat-purple', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>' },
        { number: '25+', label: '覆盖临床病种', color: 'stat-red', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21,15 16,10 5,21"/></svg>' }
      ],
      diseaseCategories: [
        {
          name: '肺部疾病',
          items: [
            { name: '肺结节', acc: '97.8%', color: 'dc-blue' },
            { name: '肺炎', acc: '96.4%', color: 'dc-blue' },
            { name: '肺癌筛查', acc: '95.2%', color: 'dc-red' },
            { name: '气胸', acc: '98.1%', color: 'dc-teal' },
            { name: '胸腔积液', acc: '97.5%', color: 'dc-teal' },
            { name: '肺栓塞', acc: '94.8%', color: 'dc-red' }
          ]
        },
        {
          name: '神经系统',
          items: [
            { name: '脑出血', acc: '98.6%', color: 'dc-red' },
            { name: '脑梗死', acc: '96.9%', color: 'dc-amber' },
            { name: '脑肿瘤', acc: '95.3%', color: 'dc-purple' },
            { name: '脑萎缩', acc: '94.7%', color: 'dc-blue' },
            { name: '白质病变', acc: '93.5%', color: 'dc-teal' },
            { name: '动脉瘤', acc: '96.2%', color: 'dc-red' }
          ]
        },
        {
          name: '腹部脏器',
          items: [
            { name: '肝脏结节', acc: '95.8%', color: 'dc-amber' },
            { name: '胆囊结石', acc: '98.2%', color: 'dc-teal' },
            { name: '肾结石', acc: '97.6%', color: 'dc-blue' },
            { name: '胰腺占位', acc: '94.1%', color: 'dc-red' },
            { name: '脾大', acc: '97.0%', color: 'dc-green' },
            { name: '腹水', acc: '98.4%', color: 'dc-teal' }
          ]
        },
        {
          name: '骨骼肌肉',
          items: [
            { name: '骨折检测', acc: '98.9%', color: 'dc-blue' },
            { name: '椎间盘突出', acc: '96.5%', color: 'dc-amber' },
            { name: '关节炎', acc: '94.3%', color: 'dc-green' },
            { name: '骨质疏松', acc: '93.8%', color: 'dc-teal' },
            { name: '半月板损伤', acc: '95.7%', color: 'dc-purple' },
            { name: '脊柱侧弯', acc: '97.1%', color: 'dc-blue' }
          ]
        }
      ],
      testimonials: [
        {
          text: '影像分析速度令人惊讶，2秒内就能给出详细的病灶标注结果，大大缓解了我们科室读片压力，特别是夜班急诊的工作效率提升了80%以上。',
          name: '李主任',
          role: '北京某三甲医院放射科主任',
          initial: '李',
          color: 'tv-blue'
        },
        {
          text: 'AI报告与医生审核的双重机制非常可靠。我们中心接入三个月以来，漏诊率下降了63%，患者满意度大幅提升，几乎零投诉。',
          name: '王院长',
          role: '华东地区影像诊断中心负责人',
          initial: '王',
          color: 'tv-teal'
        },
        {
          text: '作为患者，我在基层诊所做了CT，当天下午就收到了三甲医院医生审核签发的报告，再也不用专程跑大医院排队了，真正实现了优质医疗资源下沉。',
          name: '陈女士',
          role: '普通患者用户',
          initial: '陈',
          color: 'tv-green'
        }
      ],
      fanActiveIdx: null,
      fanCards: [
        {
          src: 'https://images.unsplash.com/photo-1530026405186-ed1f139313f8?w=700&q=85&auto=format&fit=crop',
          tag: 'IMAGING',
          title: 'CT 肺部影像',
          desc: '毫秒级肺结节自动识别，多尺度病灶精准定位，灵敏度达97.8%。',
          acc: '准确率 97.8%'
        },
        {
          src: 'https://images.unsplash.com/photo-1559757175-0eb30cd8c063?w=700&q=85&auto=format&fit=crop',
          tag: 'NEUROLOGY',
          title: 'MRI 脑部精读',
          desc: '脑出血、脑梗死、白质病变智能分层，支持三维可视化定位。',
          acc: '准确率 98.6%'
        },
        {
          src: require('./element/image/16.png'),
          tag: 'BONE',
          title: 'X 光骨折检测',
          desc: '细微骨折自动标注，全身骨骼15个区域覆盖，节省80%读片时间。',
          acc: '准确率 98.9%'
        },
        {
          src: 'https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=700&q=85&auto=format&fit=crop',
          tag: 'REVIEW',
          title: '医生在线审核',
          desc: '三甲医院医师7×24小时复核签发，平均30分钟出具权威报告。',
          acc: '双重保障'
        },
        {
          src: 'https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=700&q=85&auto=format&fit=crop',
          tag: 'REPORT',
          title: '结构化报告',
          desc: '一键生成符合临床规范的诊断报告，支持PDF导出与HIS系统对接。',
          acc: '自动生成'
        }
      ],
      ctaFeatures: ['无需信用卡', '10次免费体验', '实时客服支持', 'DICOM格式兼容'],
      footerCols: [
        { title: '产品服务', links: ['AI影像分析', '报告管理系统', 'HIS对接方案', '移动端App'] },
        { title: '解决方案', links: ['三甲医院方案', '基层医院方案', '体检中心方案', '互联网医院方案'] },
        { title: '关于我们', links: ['公司介绍', '新闻动态', '合作伙伴', '联系我们'] }
      ]
    }
  },
  computed: {
    currentTheme() {
      return this.$store.state.theme || 'dark'
    },
    isMedicalTheme() {
      return this.currentTheme === 'medical-ai'
    },
    marqueeHospitals() {
      return [...this.hospitals, ...this.hospitals]
    },
    heroCursorStyle() {
      return {
        left: this.heroCursorPos.x + 'px',
        top: this.heroCursorPos.y + 'px',
        opacity: this.heroCursorPos.opacity
      }
    }
  },
  methods: {
    onHeroMouseMove(e) {
      const el = this.$refs.heroDeck
      if (!el) return
      const rect = el.getBoundingClientRect()
      const nx = (e.clientX - rect.left) / rect.width - 0.5
      const ny = (e.clientY - rect.top) / rect.height - 0.5
      this.heroMouse = { x: nx, y: ny }
      this.heroCursorPos = {
        x: e.clientX - rect.left,
        y: e.clientY - rect.top,
        opacity: 1
      }
    },
    onHeroMouseLeave() {
      this.heroMouse = { x: 0, y: 0 }
      this.heroCursorPos = { ...this.heroCursorPos, opacity: 0 }
      this.heroActiveCard = null
    },
    heroCardStyle(idx) {
      const mx = this.heroMouse.x
      const my = this.heroMouse.y
      const baseConfigs = [
        { baseRot: -8, baseX: -20, baseY: 10, depth: 1.2, zIndex: 30 },
        { baseRot: -2, baseX: 0, baseY: 0, depth: 0.8, zIndex: 20 },
        { baseRot: 5, baseX: 18, baseY: 14, depth: 0.5, zIndex: 10 }
      ]
      const cfg = baseConfigs[idx] || baseConfigs[0]
      const isActive = this.heroActiveCard === idx

      const rotX = my * -14 * cfg.depth
      const rotY = mx * 18 * cfg.depth
      const tx = mx * 24 * cfg.depth + cfg.baseX
      const ty = my * 16 * cfg.depth + cfg.baseY

      return {
        transform: `
          translate(${tx}px, ${ty}px)
          rotateX(${rotX}deg)
          rotateY(${rotY + cfg.baseRot}deg)
          scale(${isActive ? 1.06 : 1})
        `,
        zIndex: isActive ? 40 : cfg.zIndex,
        transition: 'transform 0.45s cubic-bezier(0.22,1,0.36,1), z-index 0s'
      }
    },
    particleStyle(i) {
      const size = Math.random() * 3 + 1
      return {
        width: size + 'px',
        height: size + 'px',
        top: Math.random() * 100 + '%',
        left: Math.random() * 100 + '%',
        animationDelay: i * 0.8 + 's',
        animationDuration: Math.random() * 10 + 8 + 's'
      }
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.landing-page {
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
  position: relative;
  isolation: isolate;
  background:
    radial-gradient(ellipse 80% 60% at 70% 30%, rgba(14, 165, 233, 0.08) 0%, transparent 60%),
    radial-gradient(ellipse 60% 50% at 20% 70%, rgba(16, 185, 129, 0.06) 0%, transparent 55%),
    linear-gradient(180deg, #041420 0%, #071e2e 48%, #050f1a 100%);
  color: var(--ui-text-primary);
  overflow-x: hidden;
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
}

.landing-page > * {
  position: relative;
  z-index: 1;
}

.landing-theme-grid {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background-image:
    linear-gradient(var(--ui-grid-color-strong) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color-strong) 1px, transparent 1px),
    linear-gradient(var(--ui-grid-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color) 1px, transparent 1px);
  background-size: 56px 56px, 56px 56px, 8px 8px, 8px 8px;
  background-position: center top;
  opacity: 0.72;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.88) 0%, rgba(0, 0, 0, 0.72) 42%, rgba(0, 0, 0, 0.5) 100%);
}

.landing-page.medical-theme {
  background:
    radial-gradient(ellipse 82% 52% at 18% -8%, rgba(29, 88, 152, 0.1) 0%, transparent 58%),
    radial-gradient(ellipse 62% 44% at 88% 108%, rgba(0, 171, 163, 0.09) 0%, transparent 54%),
    linear-gradient(135deg, #f7fbff 0%, #edf4fb 45%, #e5eef8 100%);
  color: #11294b;
}

.section-container {
  max-width: var(--app-shell-max-width, 1280px);
  margin: 0 auto;
  padding: 0 var(--app-shell-gutter, 32px);
  box-sizing: border-box;
}

.sec-header {
  text-align: center;
  margin-bottom: 56px;
}

.sec-tag {
  display: inline-block;
  padding: 5px 16px;
  background: rgba(14, 165, 233, 0.12);
  border: 1px solid rgba(14, 165, 233, 0.25);
  color: #38bdf8;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 16px;
}

.sec-title {
  font-size: clamp(1.75rem, 3.5vw, 2.25rem);
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: -0.02em;
  margin-bottom: 14px;
}

.sec-sub {
  font-size: 16px;
  color: rgba(226, 232, 240, 0.55);
  line-height: 1.75;
  max-width: 560px;
  margin: 0 auto;
}

.hero-section {
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  padding: 0 0 80px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: flex-start;
}

.hero-bg-mesh {
  position: absolute;
  inset: 0;
  background: transparent;
  pointer-events: none;
}

.hero-bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--ui-grid-color-strong) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color-strong) 1px, transparent 1px);
  background-size: 56px 56px;
  background-position: center top;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.82) 0%, rgba(0, 0, 0, 0.62) 48%, transparent 100%);
  pointer-events: none;
}

.hero-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: #38bdf8;
  border-radius: 50%;
  opacity: 0.4;
  animation: drift linear infinite;
}

@keyframes drift {
  0% {
    transform: translateY(0) translateX(0);
    opacity: 0.4;
  }
  50% {
    opacity: 0.8;
  }
  100% {
    transform: translateY(-80px) translateX(30px);
    opacity: 0;
  }
}

.hero-inner {
  position: relative;
  z-index: 2;
  max-width: var(--app-shell-max-width, 1280px);
  margin: 0 auto;
  width: 100%;
  padding: 0 var(--app-shell-gutter, 32px);
  box-sizing: border-box;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 64px;
  align-items: start;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 16px;
  background: rgba(14, 165, 233, 0.1);
  border: 1px solid rgba(14, 165, 233, 0.25);
  border-radius: 20px;
  font-size: 12px;
  color: #7dd3fc;
  font-weight: 500;
  letter-spacing: 0.02em;
  margin-bottom: 24px;
}

.badge-pulse {
  width: 7px;
  height: 7px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
  box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4);
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.5);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(16, 185, 129, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}

.hero-title {
  font-size: clamp(2.2rem, 5vw, 3.2rem);
  font-weight: 800;
  color: #f8fafc;
  line-height: 1.15;
  letter-spacing: -0.03em;
  margin-bottom: 20px;
}

.title-highlight {
  font-style: normal;
  background: linear-gradient(135deg, #38bdf8 0%, #34d399 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 16px;
  color: rgba(226, 232, 240, 0.65);
  line-height: 1.8;
  margin-bottom: 24px;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 32px;
}

.htag {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  font-size: 11px;
  color: rgba(226, 232, 240, 0.6);
  font-weight: 500;
  letter-spacing: 0.03em;
}

.hero-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  margin-bottom: 40px;
}

.btn-primary-hero {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.25s ease;
  box-shadow: 0 4px 20px rgba(14, 165, 233, 0.35);
  border: 1px solid rgba(14, 165, 233, 0.5);
}

.btn-primary-hero:hover {
  background: linear-gradient(135deg, #38bdf8, #0ea5e9);
  box-shadow: 0 8px 32px rgba(14, 165, 233, 0.5);
  transform: translateY(-2px);
}

.btn-ghost-hero {
  display: inline-flex;
  align-items: center;
  padding: 14px 28px;
  color: rgba(226, 232, 240, 0.75);
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  text-decoration: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  transition: all 0.25s ease;
}

.btn-ghost-hero:hover {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.04);
}

.hero-trust {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.trust-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.trust-num {
  font-size: 22px;
  font-weight: 800;
  color: #38bdf8;
  letter-spacing: -0.02em;
}

.trust-label {
  font-size: 11px;
  color: rgba(226, 232, 240, 0.45);
  font-weight: 500;
}

.hero-right.hero-img-deck {
  position: relative;
  perspective: 1000px;
  min-height: 440px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-img-card {
  position: absolute;
  width: 300px;
  background: rgba(6, 18, 32, 0.92);
  border: 1px solid rgba(14, 165, 233, 0.2);
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 20px 56px rgba(0, 0, 0, 0.55);
  cursor: pointer;
  will-change: transform;
  transform-style: preserve-3d;
  backface-visibility: hidden;
}

.hic-0 {
  top: 10%;
  left: 0;
}

.hic-1 {
  top: 8%;
  left: 15%;
}

.hic-2 {
  top: 15%;
  left: 30%;
}

.hero-img-card.is-active {
  box-shadow: 0 28px 72px rgba(14, 165, 233, 0.35);
  border-color: rgba(14, 165, 233, 0.45);
}

.hic-img-wrap {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #040e18;
}

.hic-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.hero-img-card.is-active .hic-img {
  transform: scale(1.05);
}

.hic-scan-line {
  position: absolute;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(14, 165, 233, 0.7), transparent);
  animation: hic-scan 3s linear infinite;
  pointer-events: none;
}

@keyframes hic-scan {
  0% {
    top: 0%;
    opacity: 0;
  }
  5% {
    opacity: 1;
  }
  95% {
    opacity: 1;
  }
  100% {
    top: 100%;
    opacity: 0;
  }
}

.hic-top-bar {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  pointer-events: none;
}

.hic-live {
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(5, 15, 26, 0.82);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: #34d399;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(6px);
}

.hic-live-dot {
  width: 5px;
  height: 5px;
  background: #10b981;
  border-radius: 50%;
  animation: hic-blink 1.5s ease-in-out infinite;
}

@keyframes hic-blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}

.hic-type {
  background: rgba(5, 15, 26, 0.82);
  color: rgba(226, 232, 240, 0.7);
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 10px;
  font-weight: 600;
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hic-badge {
  position: absolute;
  bottom: 10px;
  right: 10px;
  display: flex;
  align-items: center;
  gap: 7px;
  background: rgba(5, 15, 26, 0.88);
  border: 1px solid rgba(14, 165, 233, 0.25);
  border-radius: 10px;
  padding: 7px 10px;
  backdrop-filter: blur(8px);
}

.hic-badge-icon {
  font-size: 16px;
}

.hic-badge-val {
  font-size: 14px;
  font-weight: 800;
  color: #38bdf8;
  letter-spacing: -0.02em;
  line-height: 1;
}

.hic-badge-sub {
  font-size: 9px;
  color: rgba(226, 232, 240, 0.45);
  margin-top: 2px;
}

.hic-footer {
  padding: 12px 14px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.hic-label {
  font-size: 13px;
  font-weight: 600;
  color: #e2e8f0;
  line-height: 1.4;
}

.hic-acc {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #34d399;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
}

.hid-cursor-glow {
  position: absolute;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(14, 165, 233, 0.1) 0%, transparent 65%);
  transform: translate(-50%, -50%);
  pointer-events: none;
  transition: opacity 0.4s ease;
  z-index: 0;
}

.hid-status-bar {
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(6, 18, 32, 0.92);
  border: 1px solid rgba(14, 165, 233, 0.18);
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 11px;
  color: rgba(226, 232, 240, 0.55);
  white-space: nowrap;
  backdrop-filter: blur(10px);
  z-index: 50;
}

.hsb-dot {
  width: 6px;
  height: 6px;
  background: #10b981;
  border-radius: 50%;
  animation: hic-blink 2s ease-in-out infinite;
  flex-shrink: 0;
}

.hsb-progress {
  width: 48px;
  height: 3px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 2px;
  overflow: hidden;
  flex-shrink: 0;
}

.hsb-fill {
  height: 100%;
  background: linear-gradient(90deg, #0ea5e9, #10b981);
  border-radius: 2px;
  animation: hsb-progress 3s ease-in-out infinite;
}

@keyframes hsb-progress {
  0% {
    width: 15%;
  }
  50% {
    width: 85%;
  }
  100% {
    width: 15%;
  }
}

.logos-section {
  padding: 36px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
  overflow: hidden;
}

.logos-label {
  text-align: center;
  font-size: 12px;
  color: rgba(226, 232, 240, 0.3);
  font-weight: 500;
  letter-spacing: 0.05em;
  margin-bottom: 20px;
  text-transform: uppercase;
}

.logos-track {
  overflow: hidden;
  position: relative;
}

.logos-track::before,
.logos-track::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  width: 120px;
  z-index: 1;
}

.logos-track::before {
  left: 0;
  background: linear-gradient(90deg, #041420, transparent);
}

.logos-track::after {
  right: 0;
  background: linear-gradient(-90deg, #041420, transparent);
}

.logos-inner {
  display: flex;
  gap: 24px;
  animation: marquee 25s linear infinite;
  width: max-content;
}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

.logo-pill {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 6px;
  font-size: 13px;
  color: rgba(226, 232, 240, 0.45);
  font-weight: 500;
  white-space: nowrap;
}

.showcase-section {
  padding: 96px 0;
  background: rgba(255, 255, 255, 0.015);
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.fan-deck {
  position: relative;
  height: 460px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  perspective: 1200px;
  margin-top: 16px;
  overflow: visible;
}

.fan-card {
  position: absolute;
  bottom: 0;
  width: 250px;
  height: 390px;
  border-radius: 22px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 18px 52px rgba(0, 0, 0, 0.55);
  transition:
    transform 0.48s cubic-bezier(0.34, 1.25, 0.64, 1),
    width 0.4s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.35s ease,
    box-shadow 0.35s ease;
  transform-origin: bottom center;
  will-change: transform;
}

.fan-card-0 {
  transform: rotate(-22deg) translateX(-230px);
  z-index: 1;
}

.fan-card-1 {
  transform: rotate(-11deg) translateX(-115px);
  z-index: 2;
}

.fan-card-2 {
  transform: rotate(0deg) translateX(0);
  z-index: 3;
}

.fan-card-3 {
  transform: rotate(11deg) translateX(115px);
  z-index: 2;
}

.fan-card-4 {
  transform: rotate(22deg) translateX(230px);
  z-index: 1;
}

.fan-card-active {
  transform: rotate(0deg) translateX(0) scale(1.04) !important;
  z-index: 10 !important;
  width: 310px !important;
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.7), 0 0 0 1.5px rgba(14, 165, 233, 0.45) !important;
}

.fan-deck:hover .fan-card-0:not(.fan-card-active) {
  transform: rotate(-25deg) translateX(-290px) scale(0.92);
  opacity: 0.72;
}

.fan-deck:hover .fan-card-1:not(.fan-card-active) {
  transform: rotate(-14deg) translateX(-155px) scale(0.94);
  opacity: 0.78;
}

.fan-deck:hover .fan-card-2:not(.fan-card-active) {
  transform: rotate(0deg) translateX(0) scale(0.94);
  opacity: 0.78;
}

.fan-deck:hover .fan-card-3:not(.fan-card-active) {
  transform: rotate(14deg) translateX(155px) scale(0.94);
  opacity: 0.78;
}

.fan-deck:hover .fan-card-4:not(.fan-card-active) {
  transform: rotate(25deg) translateX(290px) scale(0.92);
  opacity: 0.72;
}

.fan-card-img-wrap {
  position: absolute;
  inset: 0;
}

.fan-card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.fan-card-active .fan-card-img {
  transform: scale(1.06);
}

.fan-card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    0deg,
    rgba(4, 12, 24, 0.92) 0%,
    rgba(4, 12, 24, 0.52) 45%,
    rgba(4, 12, 24, 0.06) 100%
  );
  transition: background 0.3s ease;
}

.fan-card-active .fan-card-overlay {
  background: linear-gradient(
    0deg,
    rgba(4, 12, 24, 0.88) 0%,
    rgba(4, 12, 24, 0.38) 50%,
    rgba(4, 12, 24, 0.03) 100%
  );
}

.fan-card-body {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px 18px 22px;
  transform: translateY(8px);
  transition: transform 0.35s ease;
}

.fan-card-active .fan-card-body {
  transform: translateY(0);
}

.fan-card-eyebrow {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(56, 189, 248, 0.85);
  margin-bottom: 7px;
}

.fan-card-title {
  font-size: 19px;
  font-weight: 800;
  color: #f8fafc;
  line-height: 1.25;
  letter-spacing: -0.02em;
  margin-bottom: 7px;
}

.fan-card-desc {
  font-size: 13px;
  color: rgba(226, 232, 240, 0.7);
  line-height: 1.7;
  margin-bottom: 10px;
  max-height: 0;
  overflow: hidden;
  opacity: 0;
  transition: max-height 0.38s ease, opacity 0.3s ease 0.05s;
}

.fan-card-active .fan-card-desc {
  max-height: 80px;
  opacity: 1;
}

.fan-card-acc {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #34d399;
  font-weight: 600;
}

.features-section {
  padding: 96px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 20px;
}

.feat-card {
  background: rgba(7, 30, 46, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 16px;
  padding: 28px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feat-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 60%, rgba(14, 165, 233, 0.03));
  pointer-events: none;
}

.feat-card:hover {
  border-color: rgba(14, 165, 233, 0.25);
  background: rgba(7, 30, 46, 0.9);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
}

.feat-card--lg {
  grid-column: span 5;
}

.feat-card--sm {
  grid-column: span 3.5;
}

.features-grid .feat-card:nth-child(1) {
  grid-column: 1 / 6;
}

.features-grid .feat-card:nth-child(2) {
  grid-column: 6 / 9;
}

.features-grid .feat-card:nth-child(3) {
  grid-column: 9 / 13;
}

.features-grid .feat-card:nth-child(4) {
  grid-column: 1 / 5;
}

.features-grid .feat-card:nth-child(5) {
  grid-column: 5 / 9;
}

.feat-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}

.feat-icon-blue {
  background: rgba(14, 165, 233, 0.12);
  color: #38bdf8;
  border: 1px solid rgba(14, 165, 233, 0.2);
}

.feat-icon-green {
  background: rgba(16, 185, 129, 0.12);
  color: #34d399;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.feat-icon-amber {
  background: rgba(245, 158, 11, 0.12);
  color: #fcd34d;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.feat-icon-purple {
  background: rgba(139, 92, 246, 0.12);
  color: #c4b5fd;
  border: 1px solid rgba(139, 92, 246, 0.2);
}

.feat-icon-teal {
  background: rgba(20, 184, 166, 0.12);
  color: #5eead4;
  border: 1px solid rgba(20, 184, 166, 0.2);
}

.feat-title {
  font-size: 17px;
  font-weight: 700;
  color: #f1f5f9;
  margin-bottom: 10px;
}

.feat-desc {
  font-size: 14px;
  color: rgba(226, 232, 240, 0.55);
  line-height: 1.75;
  margin-bottom: 16px;
}

.feat-chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 7px;
}

.feat-chip {
  padding: 4px 10px;
  background: rgba(14, 165, 233, 0.08);
  border: 1px solid rgba(14, 165, 233, 0.15);
  border-radius: 4px;
  font-size: 11px;
  color: #7dd3fc;
  font-weight: 500;
}

.workflow-section {
  padding: 96px 0;
  background: rgba(255, 255, 255, 0.015);
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.workflow-steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
  position: relative;
}

.wf-step {
  padding: 28px 24px;
  position: relative;
  text-align: center;
}

.wf-step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 52px;
  right: -1px;
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.08);
}

.wf-num {
  font-size: 11px;
  font-weight: 700;
  color: rgba(14, 165, 233, 0.4);
  font-family: monospace;
  letter-spacing: 0.1em;
  margin-bottom: 12px;
}

.wf-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  position: relative;
}

.step-blue {
  background: rgba(14, 165, 233, 0.12);
  color: #38bdf8;
  border: 1px solid rgba(14, 165, 233, 0.25);
}

.step-teal {
  background: rgba(20, 184, 166, 0.12);
  color: #5eead4;
  border: 1px solid rgba(20, 184, 166, 0.25);
}

.step-green {
  background: rgba(16, 185, 129, 0.12);
  color: #34d399;
  border: 1px solid rgba(16, 185, 129, 0.25);
}

.step-amber {
  background: rgba(245, 158, 11, 0.12);
  color: #fcd34d;
  border: 1px solid rgba(245, 158, 11, 0.25);
}

.wf-title {
  font-size: 16px;
  font-weight: 700;
  color: #f1f5f9;
  margin-bottom: 8px;
}

.wf-desc {
  font-size: 13px;
  color: rgba(226, 232, 240, 0.5);
  line-height: 1.7;
}

.wf-arrow {
  position: absolute;
  top: 48px;
  right: -12px;
  z-index: 2;
  color: rgba(14, 165, 233, 0.4);
  background: #060f1a;
  padding: 2px;
  border-radius: 4px;
}

.workflow-note {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 40px;
  padding: 14px 20px;
  background: rgba(14, 165, 233, 0.05);
  border: 1px solid rgba(14, 165, 233, 0.15);
  border-radius: 10px;
  font-size: 13px;
  color: rgba(226, 232, 240, 0.6);
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.stats-section {
  padding: 96px 0;
  position: relative;
  overflow: hidden;
}

.stats-bg-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 700px;
  height: 400px;
  background: radial-gradient(ellipse, rgba(14, 165, 233, 0.05) 0%, transparent 70%);
  pointer-events: none;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  background: rgba(7, 30, 46, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 16px;
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  border-color: rgba(14, 165, 233, 0.2);
  transform: translateY(-3px);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
}

.stat-blue {
  background: rgba(14, 165, 233, 0.12);
  color: #38bdf8;
}

.stat-green {
  background: rgba(16, 185, 129, 0.12);
  color: #34d399;
}

.stat-teal {
  background: rgba(20, 184, 166, 0.12);
  color: #5eead4;
}

.stat-amber {
  background: rgba(245, 158, 11, 0.12);
  color: #fcd34d;
}

.stat-purple {
  background: rgba(139, 92, 246, 0.12);
  color: #c4b5fd;
}

.stat-red {
  background: rgba(239, 68, 68, 0.12);
  color: #fca5a5;
}

.stat-number {
  font-size: 2.2rem;
  font-weight: 800;
  color: #f8fafc;
  letter-spacing: -0.03em;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: rgba(226, 232, 240, 0.5);
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  color: #34d399;
  font-weight: 600;
  margin-top: 4px;
}

.diseases-section {
  padding: 96px 0;
  background: rgba(255, 255, 255, 0.015);
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.disease-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 28px;
  flex-wrap: wrap;
  justify-content: center;
}

.dis-tab {
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: transparent;
  color: rgba(226, 232, 240, 0.55);
  cursor: pointer;
  transition: all 0.2s ease;
}

.dis-tab:hover {
  color: #e2e8f0;
  border-color: rgba(255, 255, 255, 0.15);
}

.dis-tab.active {
  background: rgba(14, 165, 233, 0.12);
  border-color: rgba(14, 165, 233, 0.3);
  color: #38bdf8;
}

.disease-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.dis-card {
  background: rgba(7, 30, 46, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.25s ease;
  cursor: default;
}

.dis-card:hover {
  border-color: rgba(14, 165, 233, 0.2);
  background: rgba(7, 30, 46, 0.9);
  transform: translateY(-3px);
}

.dis-card-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.dc-blue {
  background: rgba(14, 165, 233, 0.15);
  box-shadow: 0 0 10px rgba(14, 165, 233, 0.15);
}

.dc-teal {
  background: rgba(20, 184, 166, 0.15);
  box-shadow: 0 0 10px rgba(20, 184, 166, 0.15);
}

.dc-green {
  background: rgba(16, 185, 129, 0.15);
  box-shadow: 0 0 10px rgba(16, 185, 129, 0.15);
}

.dc-amber {
  background: rgba(245, 158, 11, 0.15);
  box-shadow: 0 0 10px rgba(245, 158, 11, 0.15);
}

.dc-red {
  background: rgba(239, 68, 68, 0.15);
  box-shadow: 0 0 10px rgba(239, 68, 68, 0.15);
}

.dc-purple {
  background: rgba(139, 92, 246, 0.15);
  box-shadow: 0 0 10px rgba(139, 92, 246, 0.15);
}

.dis-name {
  font-size: 13px;
  font-weight: 600;
  color: #e2e8f0;
}

.dis-acc {
  font-size: 11px;
  color: #34d399;
  font-weight: 600;
}

.testimonials-section {
  padding: 96px 0;
}

.testi-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.testi-card {
  background: rgba(7, 30, 46, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 16px;
  padding: 28px;
  position: relative;
  transition: all 0.3s ease;
}

.testi-card:hover {
  border-color: rgba(14, 165, 233, 0.2);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

.testi-quote {
  font-size: 52px;
  line-height: 0.8;
  color: rgba(14, 165, 233, 0.25);
  font-family: Georgia, serif;
  margin-bottom: 12px;
}

.testi-text {
  font-size: 14px;
  color: rgba(226, 232, 240, 0.65);
  line-height: 1.8;
  margin-bottom: 24px;
}

.testi-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.testi-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}

.tv-blue {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
}

.tv-teal {
  background: linear-gradient(135deg, #14b8a6, #0d9488);
}

.tv-green {
  background: linear-gradient(135deg, #10b981, #059669);
}

.testi-name {
  font-size: 14px;
  font-weight: 600;
  color: #f1f5f9;
}

.testi-role {
  font-size: 11px;
  color: rgba(226, 232, 240, 0.4);
  margin-top: 1px;
}

.testi-stars {
  display: flex;
  gap: 2px;
  margin-left: auto;
  flex-shrink: 0;
  align-self: flex-start;
}

.cta-section {
  padding: 100px 32px;
  text-align: center;
  position: relative;
  overflow: hidden;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
}

.cta-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600px;
  height: 400px;
  background: radial-gradient(ellipse, rgba(14, 165, 233, 0.08) 0%, transparent 65%);
  pointer-events: none;
}

.cta-inner {
  position: relative;
  z-index: 1;
  max-width: 640px;
  margin: 0 auto;
}

.cta-badge {
  display: inline-block;
  padding: 5px 16px;
  background: rgba(16, 185, 129, 0.12);
  border: 1px solid rgba(16, 185, 129, 0.25);
  color: #34d399;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.05em;
  margin-bottom: 20px;
}

.cta-title {
  font-size: clamp(1.75rem, 4vw, 2.4rem);
  font-weight: 800;
  color: #f8fafc;
  letter-spacing: -0.03em;
  margin-bottom: 14px;
}

.cta-sub {
  font-size: 16px;
  color: rgba(226, 232, 240, 0.5);
  line-height: 1.7;
  margin-bottom: 36px;
}

.cta-actions {
  display: flex;
  gap: 14px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 28px;
}

.cta-btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 15px 32px;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.25s ease;
  box-shadow: 0 4px 20px rgba(14, 165, 233, 0.4);
}

.cta-btn-primary:hover {
  background: linear-gradient(135deg, #38bdf8, #0ea5e9);
  box-shadow: 0 8px 32px rgba(14, 165, 233, 0.55);
  transform: translateY(-2px);
}

.cta-btn-sec {
  display: inline-flex;
  align-items: center;
  padding: 15px 28px;
  color: rgba(226, 232, 240, 0.75);
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  text-decoration: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  transition: all 0.25s ease;
}

.cta-btn-sec:hover {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.cta-features {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-features span {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(226, 232, 240, 0.45);
}

.footer {
  background: #030c13;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  padding: 60px 32px 32px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px repeat(3, 1fr);
  gap: 48px;
  margin-bottom: 48px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.footer-logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #0ea5e9, #10b981);
  border-radius: 8px;
}

.footer-logo span {
  font-size: 16px;
  font-weight: 700;
  color: #f1f5f9;
}

.footer-tagline {
  font-size: 13px;
  color: rgba(226, 232, 240, 0.3);
  line-height: 1.75;
}

.footer-col-title {
  font-size: 13px;
  font-weight: 600;
  color: rgba(226, 232, 240, 0.5);
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 16px;
}

.footer-link {
  display: block;
  font-size: 14px;
  color: rgba(226, 232, 240, 0.4);
  text-decoration: none;
  margin-bottom: 10px;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #38bdf8;
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: rgba(226, 232, 240, 0.2);
}

.landing-page.medical-theme .hero-bg-mesh {
  background: transparent;
}

.landing-page.medical-theme .hero-bg-grid {
  background-image:
    linear-gradient(var(--ui-grid-color-strong) 1px, transparent 1px),
    linear-gradient(90deg, var(--ui-grid-color-strong) 1px, transparent 1px);
  background-size: 56px 56px;
  background-position: center top;
}

.landing-page.medical-theme .logos-track::before {
  background: linear-gradient(90deg, #eef5fb, transparent);
}

.landing-page.medical-theme .logos-track::after {
  background: linear-gradient(-90deg, #eef5fb, transparent);
}

.landing-page.medical-theme :is(
  .feat-card,
  .stat-card,
  .dis-card,
  .testi-card,
  .hero-img-card,
  .workflow-note,
  .cta-inner,
  .logo-pill,
  .hid-status-bar
) {
  background: rgba(255, 255, 255, 0.62);
  border-color: rgba(120, 151, 183, 0.22);
  box-shadow: 0 18px 42px rgba(20, 55, 97, 0.1);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}

.landing-page.medical-theme :is(
  .logos-section,
  .cta-section,
  .showcase-section,
  .workflow-section,
  .diseases-section
) {
  background: rgba(255, 255, 255, 0.18);
  border-top-color: rgba(21, 62, 114, 0.08);
  border-bottom-color: rgba(21, 62, 114, 0.08);
}

.landing-page.medical-theme :is(
  .sec-title,
  .hero-title,
  .feat-title,
  .wf-title,
  .stat-number,
  .dis-name,
  .testi-name,
  .cta-title,
  .hic-label,
  .fan-card-title,
  .footer-logo span
) {
  color: #11294b;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.16);
}

.landing-page.medical-theme :is(
  .sec-sub,
  .hero-desc,
  .feat-desc,
  .wf-desc,
  .stat-label,
  .testi-text,
  .testi-role,
  .cta-sub,
  .footer-tagline,
  .footer-col-title,
  .footer-link,
  .footer-bottom,
  .hic-type,
  .hic-badge-sub,
  .trust-label,
  .fan-card-desc,
  .logos-label,
  .logo-pill,
  .cta-features span
) {
  color: #617894;
}

.landing-page.medical-theme :is(.hero-badge, .sec-tag, .cta-badge) {
  background: rgba(21, 62, 114, 0.08);
  border-color: rgba(21, 62, 114, 0.14);
  color: #153e72;
}

.landing-page.medical-theme :is(.htag, .feat-chip) {
  background: rgba(255, 255, 255, 0.72);
  border-color: rgba(120, 151, 183, 0.18);
  color: #153e72;
}

.landing-page.medical-theme :is(.btn-primary-hero, .cta-btn-primary) {
  background: linear-gradient(135deg, #153e72, #0b9f99);
  border-color: rgba(21, 62, 114, 0.14);
  box-shadow: 0 14px 32px rgba(20, 55, 97, 0.16);
}

.landing-page.medical-theme :is(.btn-ghost-hero, .cta-btn-sec) {
  color: #153e72;
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(120, 151, 183, 0.24);
}

.landing-page.medical-theme :is(.btn-ghost-hero:hover, .cta-btn-sec:hover) {
  color: #153e72;
  background: rgba(255, 255, 255, 0.78);
  border-color: rgba(21, 62, 114, 0.18);
}

.landing-page.medical-theme :is(.trust-num, .hic-badge-val, .dis-tab.active) {
  color: #153e72;
}

.landing-page.medical-theme :is(.hic-live, .spotlight-badge) {
  background: rgba(255, 255, 255, 0.82);
  border-color: rgba(11, 159, 153, 0.22);
  color: #0b9f99;
}

.landing-page.medical-theme .hic-type,
.landing-page.medical-theme .hic-badge {
  background: rgba(255, 255, 255, 0.82);
  border-color: rgba(120, 151, 183, 0.22);
}

.landing-page.medical-theme .hic-img-wrap {
  background: linear-gradient(180deg, rgba(234, 243, 250, 0.92), rgba(219, 232, 244, 0.82));
}

.landing-page.medical-theme .hsb-progress {
  background: rgba(21, 62, 114, 0.1);
}

.landing-page.medical-theme .hid-cursor-glow,
.landing-page.medical-theme .stats-bg-glow,
.landing-page.medical-theme .cta-glow {
  background: radial-gradient(circle, rgba(21, 62, 114, 0.08) 0%, transparent 68%);
}

.landing-page.medical-theme .fan-card {
  box-shadow: 0 18px 52px rgba(20, 55, 97, 0.12);
}

.landing-page.medical-theme .fan-card-active {
  box-shadow: 0 28px 62px rgba(20, 55, 97, 0.18), 0 0 0 1px rgba(21, 62, 114, 0.14) !important;
}

.landing-page.medical-theme .fan-card-overlay {
  background: linear-gradient(
    0deg,
    rgba(255, 255, 255, 0.94) 0%,
    rgba(244, 248, 253, 0.62) 46%,
    rgba(255, 255, 255, 0.08) 100%
  );
}

.landing-page.medical-theme .fan-card-active .fan-card-overlay {
  background: linear-gradient(
    0deg,
    rgba(255, 255, 255, 0.92) 0%,
    rgba(244, 248, 253, 0.42) 50%,
    rgba(255, 255, 255, 0.03) 100%
  );
}

.landing-page.medical-theme .fan-card-eyebrow {
  color: #0b9f99;
}

.landing-page.medical-theme .workflow-note,
.landing-page.medical-theme .dis-tab.active {
  background: rgba(21, 62, 114, 0.08);
  border-color: rgba(21, 62, 114, 0.14);
}

.landing-page.medical-theme .workflow-note,
.landing-page.medical-theme .dis-tab,
.landing-page.medical-theme .hero-badge,
.landing-page.medical-theme .trust-label,
.landing-page.medical-theme .wf-num {
  color: #617894;
}

.landing-page.medical-theme .footer {
  background: rgba(236, 244, 251, 0.84);
  border-top-color: rgba(21, 62, 114, 0.08);
}

.landing-page.medical-theme .footer-bottom {
  border-top-color: rgba(21, 62, 114, 0.08);
}

@media (max-width: 1100px) {
  .features-grid .feat-card:nth-child(1) {
    grid-column: 1 / 7;
  }

  .features-grid .feat-card:nth-child(2) {
    grid-column: 7 / 13;
  }

  .features-grid .feat-card:nth-child(3) {
    grid-column: 1 / 5;
  }

  .features-grid .feat-card:nth-child(4) {
    grid-column: 5 / 9;
  }

  .features-grid .feat-card:nth-child(5) {
    grid-column: 9 / 13;
  }

  .disease-cards {
    grid-template-columns: repeat(4, 1fr);
  }

  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .fan-deck {
    height: 380px;
  }
}

@media (max-width: 900px) {
  .hero-inner {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .hero-right {
    display: none;
  }

  .hero-trust {
    justify-content: center;
  }

  .hero-tags {
    justify-content: center;
  }

  .workflow-steps {
    grid-template-columns: 1fr 1fr;
  }

  .testi-grid {
    grid-template-columns: 1fr;
  }

  .footer-inner {
    grid-template-columns: 1fr 1fr;
  }

  .fan-deck {
    height: 320px;
  }

  .fan-card {
    width: 200px;
    height: 320px;
  }

  .fan-card-active {
    width: 260px !important;
  }
}

@media (max-width: 768px) {
  .section-container {
    padding: 0 20px;
  }

  .features-grid .feat-card {
    grid-column: 1 / 13 !important;
  }

  .disease-cards {
    grid-template-columns: repeat(3, 1fr);
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
  }

  .workflow-steps {
    grid-template-columns: 1fr;
  }

  .footer-inner {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .footer-bottom {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .disease-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
