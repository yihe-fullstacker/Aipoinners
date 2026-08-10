<template>
  <div class="patient-home">

    <!-- ===== 背景装饰层 ===== -->
    <div class="ph-bg-layer" aria-hidden="true">
      <div class="ph-bg-orb orb-1"></div>
      <div class="ph-bg-orb orb-2"></div>
      <div class="ph-bg-orb orb-3"></div>
      <div class="ph-bg-grid"></div>
    </div>

    <div class="page-shell">

      <!-- ===== HERO ===== -->
      <section class="hero-section reveal-item">
        <div class="hero-copy">
          <span class="eyebrow">
            <span class="badge-dot"></span>
            AI + 医生双核驱动的医学影像智能诊断服务平台
          </span>
          <h1 class="hero-title">让 AI 预判更快<br/><em>让医生决策更稳</em></h1>
          <p class="hero-subtitle">
            面向患者、影像科与临床医生提供影像上传、AI 辅助分析、专家复核、病程追踪与医学教育的一体化体验，让每一次检查都具备更高效、更可追溯、更可信赖的诊断支持。
          </p>

          <div class="hero-actions">
            <router-link to="/upload" class="btn btn-primary">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="16,16 12,12 8,16"/><line x1="12" y1="12" x2="12" y2="21"/><path d="M20.39,18.39A5,5,0,0,0,18,9h-1.26A8,8,0,1,0,3,16.3"/></svg>
              上传影像并发起诊断
            </router-link>
            <router-link :to="{ name: 'PatientHistory' }" class="btn btn-secondary">查看历史诊疗记录</router-link>
            <button class="btn btn-ghost" type="button" @click="scrollToSection('service-matrix')">浏览平台能力</button>
          </div>

          <div class="quick-nav">
            <button
              v-for="anchor in quickAnchors"
              :key="anchor.id"
              class="quick-nav-chip"
              type="button"
              @click="scrollToSection(anchor.id)"
            >{{ anchor.label }}</button>
          </div>
        </div>

        <div class="hero-panel">
          <div class="panel-badge">
            <span class="badge-dot"></span>
            智能诊断总览
          </div>
          <div class="hero-metrics">
            <article v-for="metric in metrics" :key="metric.label" class="metric-card">
              <div class="metric-value">{{ metric.value }}</div>
              <div class="metric-label">{{ metric.label }}</div>
              <div class="metric-detail">{{ metric.detail }}</div>
            </article>
          </div>
          <div class="hero-notice">
            <div class="notice-title">当前推荐流程</div>
            <div class="notice-text">先上传影像完成 AI 预分析，再进入医生复核与问答工作站，最终在历史记录中查看完整报告与追踪建议。</div>
          </div>
        </div>
      </section>

      <!-- ===== 图片展示区：弹簧阻尼物理效果 ===== -->
      <section class="gallery-section reveal-item" @mousemove="onGalleryMouseMove" @mouseleave="onGalleryMouseLeave" ref="galleryRef">
        <div class="gallery-header">
          <div class="section-kicker">影像诊断实景</div>
          <h2 class="gallery-title">真实临床场景展示</h2>
          <p class="gallery-sub">AI 赋能的影像诊断正在各类临床场景中落地，推动医疗效率与准确率双重提升</p>
        </div>

        <div class="gallery-track">
          <div
            v-for="(img, idx) in galleryImages"
            :key="idx"
            class="gallery-card"
            :style="cardSpringStyle(idx)"
            @mouseenter="onCardEnter(idx)"
            @mouseleave="onCardLeave(idx)"
            @click="openLightbox(idx)"
          >
            <div class="gc-img-wrap">
              <img :src="img.src" :alt="img.label" class="gc-img" loading="lazy" />
              <div class="gc-overlay">
                <div class="gc-zoom-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/><line x1="11" y1="8" x2="11" y2="14"/><line x1="8" y1="11" x2="14" y2="11"/></svg>
                </div>
              </div>
            </div>
            <div class="gc-info">
              <span class="gc-tag" :class="img.tagClass">{{ img.tag }}</span>
              <div class="gc-label">{{ img.label }}</div>
              <div class="gc-acc">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="#10b981" stroke="none"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                准确率 {{ img.acc }}
              </div>
            </div>
          </div>
        </div>

        <!-- 鼠标跟随光晕 -->
        <div class="gallery-cursor-glow" :style="cursorGlowStyle" ref="cursorGlow"></div>
      </section>

      <!-- 灯箱 -->
      <transition name="lb-fade">
        <div class="lightbox" v-if="lightboxIdx !== null" @click.self="closeLightbox">
          <button class="lb-close" @click="closeLightbox">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
          <button class="lb-prev" @click="lbPrev" v-if="lightboxIdx > 0">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="15,18 9,12 15,6"/></svg>
          </button>
          <div class="lb-content">
            <img :src="galleryImages[lightboxIdx].src" :alt="galleryImages[lightboxIdx].label" class="lb-img" />
            <div class="lb-caption">
              <span class="gc-tag" :class="galleryImages[lightboxIdx].tagClass">{{ galleryImages[lightboxIdx].tag }}</span>
              <span class="lb-label">{{ galleryImages[lightboxIdx].label }}</span>
            </div>
          </div>
          <button class="lb-next" @click="lbNext" v-if="lightboxIdx < galleryImages.length - 1">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="9,18 15,12 9,6"/></svg>
          </button>
        </div>
      </transition>

      <!-- ===== 服务矩阵 ===== -->
      <section id="service-matrix" class="content-section reveal-item">
        <div class="section-head">
          <div>
            <div class="section-kicker">服务矩阵</div>
            <h2 class="section-title">核心功能按诊疗阶段清晰排布</h2>
          </div>
          <p class="section-desc">重要按钮集中放在首屏和模块卡片底部，用户进入页面后可以快速判断"下一步去哪"，减少误触和认知成本。</p>
        </div>

        <div class="service-grid">
          <article
            v-for="card in serviceCards"
            :key="card.title"
            class="service-card"
            :class="card.theme"
          >
            <div class="service-card-top">
              <span class="service-tag">{{ card.tag }}</span>
              <div class="service-title">{{ card.title }}</div>
              <p class="service-text">{{ card.text }}</p>
            </div>
            <ul class="service-points">
              <li v-for="point in card.points" :key="point">{{ point }}</li>
            </ul>
            <router-link :to="card.to" class="btn btn-card">{{ card.action }}</router-link>
          </article>
        </div>
      </section>

      <!-- ===== 双核协同模块 ===== -->
      <section id="clinical-engine" class="content-section module-section reveal-item">
        <div class="section-head">
          <div>
            <div class="section-kicker">双核协同</div>
            <h2 class="section-title">点击切换不同业务模块，查看平台如何协同工作</h2>
          </div>
          <p class="section-desc">通过交互式模块展示 AI 初筛、医生复核与知识服务之间的关系，让页面不仅"能看"，也"能用"和"能解释"。</p>
        </div>

        <div class="module-layout">
          <div class="module-tabs">
            <button
              v-for="module in intelligenceModules"
              :key="module.key"
              type="button"
              class="module-tab"
              :class="{ active: activeModule === module.key }"
              @click="activeModule = module.key"
            >
              <span class="module-tab-title">{{ module.tab }}</span>
              <span class="module-tab-desc">{{ module.brief }}</span>
            </button>
          </div>

          <div class="module-display" v-if="currentModule">
            <div class="module-display-head">
              <span class="module-display-label">{{ currentModule.label }}</span>
              <h3>{{ currentModule.title }}</h3>
              <p>{{ currentModule.description }}</p>
            </div>
            <div class="module-highlight">
              <div class="highlight-title">适用场景</div>
              <div class="highlight-text">{{ currentModule.scene }}</div>
            </div>
            <div class="module-points">
              <div v-for="point in currentModule.points" :key="point.title" class="module-point">
                <div class="module-point-title">{{ point.title }}</div>
                <div class="module-point-text">{{ point.text }}</div>
              </div>
            </div>
            <div class="module-actions">
              <router-link :to="currentModule.primaryTo" class="btn btn-primary">{{ currentModule.primaryAction }}</router-link>
              <router-link :to="currentModule.secondaryTo" class="btn btn-secondary">{{ currentModule.secondaryAction }}</router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== 诊疗路径 ===== -->
      <section id="journey-section" class="content-section reveal-item">
        <div class="section-head">
          <div>
            <div class="section-kicker">诊疗路径</div>
            <h2 class="section-title">从影像上传到结果追踪，流程更清楚</h2>
          </div>
          <p class="section-desc">页面通过分步流程把用户行为前置，帮助患者快速完成关键动作，也方便医生查看平台协同价值。</p>
        </div>

        <div class="journey-grid">
          <article v-for="step in journeySteps" :key="step.step" class="journey-card">
            <div class="journey-step">0{{ step.step }}</div>
            <div class="journey-card-title">{{ step.title }}</div>
            <p class="journey-card-text">{{ step.text }}</p>
          </article>
        </div>
      </section>

      <!-- ===== 延展能力 ===== -->
      <section id="resource-section" class="content-section resource-section reveal-item">
        <div class="section-head">
          <div>
            <div class="section-kicker">延展能力</div>
            <h2 class="section-title">除了诊断，也提供学习、问答与报告沉淀</h2>
          </div>
          <p class="section-desc">让平台从单次诊断工具升级为持续服务入口，增强用户留存与商业化展示完整度。</p>
        </div>

        <div class="resource-grid">
          <article v-for="resource in resourceCards" :key="resource.title" class="resource-card">
            <div class="resource-type">{{ resource.type }}</div>
            <div class="resource-title">{{ resource.title }}</div>
            <p class="resource-text">{{ resource.text }}</p>
            <router-link :to="resource.to" class="resource-link">{{ resource.action }}</router-link>
          </article>
        </div>
      </section>

      <!-- ===== CTA ===== -->
      <section class="cta-section reveal-item">
        <div class="cta-copy">
          <div class="section-kicker">立即开始</div>
          <h2 class="section-title">首页即服务：诊断、学习、回溯、问答</h2>
          <p class="section-desc">如果用户现在就准备上传检查影像，可以直接进入诊断；如果还在了解阶段，也能先查看课程、历史记录或进入智能问答。</p>
        </div>
        <div class="cta-actions">
          <router-link to="/upload" class="btn btn-primary">立即上传影像</router-link>
          <router-link to="/smart-qa" class="btn btn-secondary">进入智能问答工作站</router-link>
          <router-link to="/medical-course" class="btn btn-ghost btn-light">查看智慧医疗课程</router-link>
        </div>
      </section>

    </div>
  </div>
</template>

<script>
export default {
  name: 'PatientHome',
  data() {
    return {
      activeModule: 'ai-screening',
      lightboxIdx: null,

      // 弹簧阻尼物理状态
      mouse: { x: 0, y: 0 },           // 鼠标在gallery区域内的归一化坐标 (-0.5 ~ 0.5)
      spring: [],                        // 每张卡片的弹簧状态 {tx, ty, rot, scale}
      springTarget: [],
      rafId: null,
      galleryHovered: false,
      cursorGlow: { x: 0, y: 0, opacity: 0 },

      // 弹簧物理参数
      STIFFNESS: 0.09,
      DAMPING: 0.75,

      quickAnchors: [
        { id: 'service-matrix', label: '服务矩阵' },
        { id: 'clinical-engine', label: 'AI+医生协同' },
        { id: 'journey-section', label: '诊疗流程' },
        { id: 'resource-section', label: '学习与报告' }
      ],

      metrics: [
        { value: '3 分钟', label: 'AI 初步分析响应', detail: '快速生成初筛建议' },
        { value: '双核协同', label: 'AI + 医生复核', detail: '兼顾速度与专业度' },
        { value: '全流程', label: '病例记录可追溯', detail: '统一沉淀检查过程' },
        { value: '7 x 24', label: '线上辅助服务', detail: '支持随时查看与追问' }
      ],

      galleryImages: [
        {
          src: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=600&q=85&auto=format&fit=crop',
          label: 'CT 肺部影像精准识别',
          tag: '肺部 CT',
          tagClass: 'tag-blue',
          acc: '97.8%'
        },
        {
          src: 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=600&q=85&auto=format&fit=crop',
          label: 'MRI 脑部病灶定位',
          tag: '脑部 MRI',
          tagClass: 'tag-purple',
          acc: '98.6%'
        },
        {
          src: 'https://images.unsplash.com/photo-1581594693702-fbdc51b2763b?w=600&q=85&auto=format&fit=crop',
          label: '数字 X 光骨折检测',
          tag: '骨骼 X-Ray',
          tagClass: 'tag-teal',
          acc: '98.9%'
        },
        {
          src: 'https://images.unsplash.com/photo-1579684453423-f84349ef60b0?w=600&q=85&auto=format&fit=crop',
          label: '医生在线复核报告',
          tag: '医生审核',
          tagClass: 'tag-green',
          acc: '双重把关'
        },
        {
          src: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600&q=85&auto=format&fit=crop',
          label: '超声影像实时分析',
          tag: '超声影像',
          tagClass: 'tag-amber',
          acc: '95.4%'
        },
        {
          src: 'https://images.unsplash.com/photo-1579154204601-01588f351e67?w=600&q=85&auto=format&fit=crop',
          label: '结构化诊断报告生成',
          tag: '智能报告',
          tagClass: 'tag-blue',
          acc: '自动生成'
        }
      ],

      serviceCards: [
        {
          tag: '第一步',
          title: '上传影像与发起诊断',
          text: '集中承接 CT、MRI、X 光等医学影像上传需求，适合作为患者进入系统后的首要动作。',
          points: ['支持多类型影像资料整理', '快速进入 AI 预分析流程', '自动衔接专家会诊页面'],
          action: '专家诊断',
          to: '/upload',
          theme: 'theme-cyan'
        },
        {
          tag: '核心分析',
          title: 'AI 影像分析中心',
          text: '对上传的检查影像进行结构化分析，帮助用户尽快获得初步结果并为医生复核提供依据。',
          points: ['输出可读性更强的分析结果', '辅助定位潜在异常区域', '缩短从检查到结论的等待时间'],
          action: '打开 AI 分析',
          to: { name: 'Dashboard' },
          theme: 'theme-blue'
        },
        {
          tag: '结果沉淀',
          title: '历史记录与随访追踪',
          text: '将既往病例、复诊建议和阶段性诊断结果统一归档，方便患者和医生持续回看。',
          points: ['回看历史诊断与结论变化', '持续追踪病程建议', '便于医患沟通和二次确认'],
          action: '查看历史诊疗记录',
          to: { name: 'PatientHistory' },
          theme: 'theme-teal'
        },
        {
          tag: '深度服务',
          title: '智能问答与课程学习',
          text: '除了结果查看，还支持进入智能问答工作站和课程中心，提升平台的陪伴式服务能力。',
          points: ['常见影像问题即时解答', '辅助理解报告与术语', '延展智慧医疗知识服务'],
          action: '进入问答工作站',
          to: '/smart-qa',
          theme: 'theme-indigo'
        }
      ],

      intelligenceModules: [
        {
          key: 'ai-screening',
          tab: 'AI 初筛引擎',
          brief: '先完成预分析，再衔接人工复核',
          label: 'AI 智能引擎',
          title: '快速识别重点区域，缩短首轮判断时间',
          description: '适合用于患者首诊、复查前置筛查和多病例集中处理，帮助平台在首页就体现效率优势。',
          scene: '用户完成影像上传后，系统优先进入 AI 分析流程，输出结构化结果与风险提示。',
          points: [
            { title: '自动化预判', text: '对影像进行初步识别，帮助快速定位疑似异常区域。' },
            { title: '结构化结果', text: '将复杂影像信息转换为更易理解的结果摘要和辅助说明。' },
            { title: '提升周转效率', text: '让医生在正式会诊前先看到重点，提高整体处理效率。' }
          ],
          primaryAction: '进入 AI 分析',
          primaryTo: { name: 'Dashboard' },
          secondaryAction: '先上传影像(专家诊断)',
          secondaryTo: '/upload'
        },
        {
          key: 'doctor-review',
          tab: '医生复核协同',
          brief: '让 AI 结果进入临床语境',
          label: '医生协同复核',
          title: '由医生把关诊断结论，提升可信度与可用性',
          description: '平台突出"AI+医生"而不是单纯 AI，首页需要明确传达最终结论有医生参与和专业审阅。',
          scene: '适合高风险病例、需要多学科会诊、或患者希望进一步沟通诊断意见的场景。',
          points: [
            { title: '双重校验', text: 'AI 提供线索，医生结合临床经验复核判断，降低误判风险。' },
            { title: '结果更可信', text: '输出内容更符合真实医疗服务场景，更便于用户理解与接受。' },
            { title: '支持持续追踪', text: '复核结论可回流到历史记录中，便于后续随访和比较。' }
          ],
          primaryAction: '查看历史记录',
          primaryTo: { name: 'PatientHistory' },
          secondaryAction: '发起影像诊断',
          secondaryTo: '/upload'
        },
        {
          key: 'knowledge-service',
          tab: '知识与服务延展',
          brief: '课程、问答、报告形成完整闭环',
          label: '知识服务体系',
          title: '把一次诊断延展为持续学习与辅助决策入口',
          description: '除了单次检查分析，平台还可以承接答疑、课程学习和报告归档，增强商业首页的完整度。',
          scene: '适用于用户想了解检查背景、医学术语、治疗方向或继续学习 AI 医学影像知识时。',
          points: [
            { title: '智能问答', text: '围绕检查报告、医学概念和平台使用方式提供辅助答疑。' },
            { title: '课程中心', text: '帮助用户系统了解智慧医疗、远程诊疗和 AI 影像分析。' },
            { title: '报告归档', text: '统一查看历史报告，让平台具备持续服务属性。' }
          ],
          primaryAction: '进入智慧医疗课程',
          primaryTo: '/medical-course',
          secondaryAction: '查看报告中心',
          secondaryTo: { name: 'Reports' }
        }
      ],

      journeySteps: [
        { step: 1, title: '提交影像资料', text: '用户从首屏主按钮进入上传页，提交检查影像、基本信息和诊断需求。' },
        { step: 2, title: '系统执行 AI 预分析', text: '平台对影像进行快速识别，提取重点区域与结构化结果，为后续诊断提供依据。' },
        { step: 3, title: '医生协同复核与问答', text: '医生结合 AI 输出进行复核，必要时通过问答工作站补充解释与沟通。' },
        { step: 4, title: '报告沉淀与随访追踪', text: '最终结果进入历史记录与报告中心，方便用户持续查看、复盘和随访。' }
      ],

      resourceCards: [
        { type: '知识服务', title: '智慧医疗课程中心', text: '把 AI 医疗影像、大数据、远程诊疗等内容整理为课程化入口，适合商业展示与用户留存。', action: '进入课程中心', to: '/medical-course' },
        { type: '智能辅助', title: '智能问答工作站', text: '针对报告解读、平台使用和常见医疗影像问题提供在线辅助解答，提高服务连续性。', action: '进入问答工作站', to: '/smart-qa' },
        { type: '结果管理', title: '历史报告与诊断归档', text: '帮助用户统一查看既往报告、追踪建议和诊疗结论，让平台具备长期使用价值。', action: '查看报告列表', to: { name: 'Reports' } }
      ],

      revealObserver: null
    }
  },

  computed: {
    currentModule() {
      return this.intelligenceModules.find(m => m.key === this.activeModule)
    },
    cursorGlowStyle() {
      return {
        left: this.cursorGlow.x + 'px',
        top: this.cursorGlow.y + 'px',
        opacity: this.cursorGlow.opacity
      }
    }
  },

  methods: {
    // ===== 原有方法 (完整保留) =====
    scrollToSection(id) {
      const section = document.getElementById(id)
      if (!section) return
      section.scrollIntoView({ behavior: 'smooth', block: 'start' })
    },
    initRevealObserver() {
      const revealItems = this.$el.querySelectorAll('.reveal-item')
      if (!('IntersectionObserver' in window)) {
        revealItems.forEach(item => item.classList.add('is-visible'))
        return
      }
      this.revealObserver = new IntersectionObserver(
        entries => {
          entries.forEach(entry => {
            if (entry.isIntersecting) {
              entry.target.classList.add('is-visible')
              this.revealObserver.unobserve(entry.target)
            }
          })
        },
        { threshold: 0.18 }
      )
      revealItems.forEach(item => this.revealObserver.observe(item))
    },

    // ===== 弹簧阻尼物理 =====
    initSpring() {
      this.spring = this.galleryImages.map(() => ({
        tx: 0, ty: 0, rot: 0, scale: 1,
        vx: 0, vy: 0, vr: 0, vs: 0
      }))
      this.springTarget = this.galleryImages.map(() => ({
        tx: 0, ty: 0, rot: 0, scale: 1
      }))
    },

    onGalleryMouseMove(e) {
      const rect = this.$refs.galleryRef.getBoundingClientRect()
      const nx = (e.clientX - rect.left) / rect.width - 0.5   // -0.5 ~ 0.5
      const ny = (e.clientY - rect.top) / rect.height - 0.5

      this.mouse = { x: nx, y: ny }
      this.galleryHovered = true

      // 更新鼠标光晕位置
      this.cursorGlow = {
        x: e.clientX - rect.left,
        y: e.clientY - rect.top,
        opacity: 1
      }

      // 根据鼠标位置为每张卡片设置目标偏移，各卡片响应强度不同（视差感）
      this.galleryImages.forEach((_, idx) => {
        const depth = 1 + (idx % 3) * 0.3          // 深度感系数
        const maxTx = 18 * depth
        const maxTy = 12 * depth
        const maxRot = 3 * depth
        this.springTarget[idx].tx = nx * maxTx
        this.springTarget[idx].ty = ny * maxTy
        this.springTarget[idx].rot = nx * maxRot
        this.springTarget[idx].scale = 1
      })

      if (!this.rafId) this.startSpringLoop()
    },

    onGalleryMouseLeave() {
      this.galleryHovered = false
      this.cursorGlow = { ...this.cursorGlow, opacity: 0 }
      this.galleryImages.forEach((_, idx) => {
        this.springTarget[idx] = { tx: 0, ty: 0, rot: 0, scale: 1 }
      })
    },

    onCardEnter(idx) {
      this.springTarget[idx].scale = 1.05
    },

    onCardLeave(idx) {
      this.springTarget[idx].scale = this.galleryHovered ? 1 : 1
    },

    startSpringLoop() {
      const loop = () => {
        let allSettled = true
        this.spring.forEach((s, idx) => {
          const t = this.springTarget[idx]
          // 弹簧：加速度 = 刚度 * (目标 - 当前) - 阻尼 * 速度
          s.vx += this.STIFFNESS * (t.tx - s.tx) - this.DAMPING * s.vx
          s.vy += this.STIFFNESS * (t.ty - s.ty) - this.DAMPING * s.vy
          s.vr += this.STIFFNESS * (t.rot - s.rot) - this.DAMPING * s.vr
          s.vs += this.STIFFNESS * (t.scale - s.scale) - this.DAMPING * s.vs

          s.tx += s.vx
          s.ty += s.vy
          s.rot += s.vr
          s.scale += s.vs

          if (Math.abs(s.vx) > 0.01 || Math.abs(s.vy) > 0.01 ||
              Math.abs(s.vr) > 0.001 || Math.abs(s.vs) > 0.001) {
            allSettled = false
          }
        })

        if (allSettled && !this.galleryHovered) {
          this.rafId = null
          return
        }
        this.rafId = requestAnimationFrame(loop)
      }
      this.rafId = requestAnimationFrame(loop)
    },

    cardSpringStyle(idx) {
      if (!this.spring[idx]) return {}
      const s = this.spring[idx]
      return {
        transform: `translate(${s.tx}px, ${s.ty}px) rotate(${s.rot}deg) scale(${s.scale})`,
        transition: 'none',
        willChange: 'transform'
      }
    },

    // ===== 灯箱 =====
    openLightbox(idx) {
      this.lightboxIdx = idx
      document.body.style.overflow = 'hidden'
    },
    closeLightbox() {
      this.lightboxIdx = null
      document.body.style.overflow = ''
    },
    lbPrev() {
      if (this.lightboxIdx > 0) this.lightboxIdx--
    },
    lbNext() {
      if (this.lightboxIdx < this.galleryImages.length - 1) this.lightboxIdx++
    },
    handleKeydown(e) {
      if (this.lightboxIdx === null) return
      if (e.key === 'Escape') this.closeLightbox()
      if (e.key === 'ArrowLeft') this.lbPrev()
      if (e.key === 'ArrowRight') this.lbNext()
    }
  },

  mounted() {
    this.initRevealObserver()
    this.initSpring()
    document.addEventListener('keydown', this.handleKeydown)
  },

  beforeUnmount() {
    if (this.revealObserver) this.revealObserver.disconnect()
    if (this.rafId) cancelAnimationFrame(this.rafId)
    document.removeEventListener('keydown', this.handleKeydown)
    document.body.style.overflow = ''
  }
}
</script>

<style scoped>
:global(body) {
  font-family: "HarmonyOS Sans SC", "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif;
}

* { box-sizing: border-box; }

/* ===== 页面底色 ===== */
.patient-home {
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  background: #07111d;
  color: #e6f0ff;
  overflow-x: hidden;
  position: relative;
}

/* ===== 背景装饰 ===== */
.ph-bg-layer {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.ph-bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.orb-1 {
  width: 600px; height: 600px;
  top: -120px; left: -100px;
  background: radial-gradient(circle, rgba(15,118,110,0.16) 0%, transparent 70%);
}

.orb-2 {
  width: 700px; height: 700px;
  top: -80px; right: -150px;
  background: radial-gradient(circle, rgba(37,99,235,0.18) 0%, transparent 70%);
}

.orb-3 {
  width: 500px; height: 500px;
  bottom: 20%; left: 30%;
  background: radial-gradient(circle, rgba(56,189,248,0.06) 0%, transparent 70%);
}

.ph-bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(125,211,252,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(125,211,252,0.03) 1px, transparent 1px);
  background-size: 64px 64px;
}

/* ===== SHELL ===== */
.page-shell {
  position: relative;
  z-index: 1;
  width: min(1240px, calc(100% - 48px));
  margin: 0 auto;
  padding: 10px 0 80px;
  display: flex;
  flex-direction: column;
  gap: 26px;
}

/* ===== COMMON SECTION CHROME ===== */
.hero-section,
.content-section,
.cta-section {
  position: relative;
  border: 1px solid rgba(125,211,252,0.1);
  background: linear-gradient(180deg, rgba(8,19,34,0.95), rgba(5,14,26,0.96));
  box-shadow: 0 24px 64px rgba(1,10,20,0.4);
  border-radius: 28px;
}

.hero-section::before,
.cta-section::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    linear-gradient(120deg, rgba(34,211,238,0.07), transparent 34%),
    linear-gradient(300deg, rgba(59,130,246,0.07), transparent 28%);
  pointer-events: none;
}

/* ===== HERO ===== */
.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 420px);
  gap: 28px;
  padding: 40px;
}

.hero-copy,
.hero-panel,
.cta-copy,
.cta-actions {
  position: relative;
  z-index: 1;
}

.eyebrow,
.section-kicker,
.panel-badge,
.service-tag,
.resource-type,
.module-display-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border-radius: 999px;
  padding: 6px 14px;
  background: rgba(34,211,238,0.08);
  border: 1px solid rgba(34,211,238,0.18);
  color: #86e9ff;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  font-weight: 600;
}

.badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #5eead4;
  box-shadow: 0 0 10px rgba(94,234,212,0.8);
  flex-shrink: 0;
  animation: blink 2s ease-in-out infinite;
}

@keyframes blink {
  0%,100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.hero-title {
  margin: 18px 0 16px;
  font-size: clamp(30px, 4.5vw, 50px);
  line-height: 1.1;
  letter-spacing: -0.03em;
  color: #f8fbff;
  font-weight: 800;
}

.hero-title em {
  font-style: normal;
  background: linear-gradient(135deg, #5eead4 0%, #38bdf8 60%, #818cf8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle,
.section-desc,
.service-text,
.journey-card-text,
.resource-text,
.module-display p,
.highlight-text,
.module-point-text,
.notice-text {
  color: rgb(152, 149, 149);
  line-height: 1.8;
  font-size: 15px;
}

.hero-actions,
.quick-nav,
.module-actions,
.cta-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hero-actions { margin-top: 28px; }
.quick-nav { margin-top: 16px; }

/* ===== BUTTONS ===== */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 46px;
  padding: 0 22px;
  border-radius: 999px;
  border: 1px solid transparent;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.01em;
  cursor: pointer;
  transition: transform 0.25s cubic-bezier(0.34,1.56,0.64,1), box-shadow 0.25s ease, background 0.25s ease, border-color 0.25s ease;
}

.btn:hover { transform: translateY(-2px); }

.btn-primary {
  color: #03131f;
  background: linear-gradient(135deg, #5eead4 0%, #38bdf8 100%);
  box-shadow: 0 8px 24px rgba(56,189,248,0.28);
}

.btn-primary:hover { box-shadow: 0 14px 32px rgba(56,189,248,0.38); }

.btn-secondary {
  color: #ecf8ff;
  background: rgba(15,23,42,0.72);
  border-color: rgba(125,211,252,0.26);
}

.btn-ghost,
.quick-nav-chip {
  color: #a8d9ff;
  background: rgba(10,22,38,0.68);
  border: 1px solid rgba(148,163,184,0.16);
  font-size: 13px;
}

.btn-secondary:hover,
.btn-ghost:hover,
.quick-nav-chip:hover {
  border-color: rgba(125,211,252,0.42);
  background: rgba(18,40,67,0.84);
  color: #f4fbff;
}

.btn-light { color: #dff7ff; }

/* ===== HERO PANEL ===== */
.hero-panel {
  padding: 22px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(7,27,42,0.96), rgba(4,15,28,0.98)),
              radial-gradient(circle at top, rgba(34,211,238,0.1), transparent 45%);
  border: 1px solid rgba(125,211,252,0.12);
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0,1fr));
  gap: 12px;
  margin-top: 16px;
}

.metric-card {
  padding: 16px;
  background: rgba(7,21,35,0.84);
  border: 1px solid rgba(125,211,252,0.1);
  border-radius: 16px;
  transition: transform 0.25s ease, border-color 0.25s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  border-color: rgba(94,234,212,0.25);
}

.metric-value {
  font-size: 22px;
  font-weight: 800;
  color: #f7fbff;
  letter-spacing: -0.02em;
}

.metric-label {
  margin-top: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #ffffff;
}

.metric-detail {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.68);
  line-height: 1.6;
}

.hero-notice {
  margin-top: 16px;
  padding: 16px;
  border-radius: 16px;
  background: rgba(5,16,28,0.72);
  border: 1px solid rgba(125,211,252,0.1);
}

.notice-title,
.section-title,
.service-title,
.journey-card-title,
.resource-title,
.module-point-title {
  color: #f8fbff;
}

.notice-title {
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 700;
}

/* ===== GALLERY SECTION ===== */
.gallery-section {
  position: relative;
  padding: 48px 40px 52px;
  border: 1px solid rgba(125,211,252,0.1);
  background: linear-gradient(180deg, rgba(6,16,28,0.96), rgba(4,12,22,0.98));
  border-radius: 28px;
  overflow: hidden;
  cursor: none;
}

.gallery-header {
  text-align: center;
  margin-bottom: 44px;
}

.gallery-title {
  font-size: clamp(22px, 3vw, 32px);
  font-weight: 800;
  color: #f8fbff;
  letter-spacing: -0.02em;
  margin: 12px 0 10px;
}

.gallery-sub {
  font-size: 15px;
  color: rgba(214,230,255,0.55);
  line-height: 1.7;
  max-width: 520px;
  margin: 0 auto;
}

/* 卡片轨道 */
.gallery-track {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  position: relative;
  z-index: 2;
}

.gallery-card {
  border-radius: 18px;
  overflow: hidden;
  background: rgba(7,20,34,0.9);
  border: 1px solid rgba(125,211,252,0.1);
  cursor: pointer;
  transform: translateZ(0);
  /* transform 由 JS 控制 */
}

.gallery-card:hover .gc-overlay { opacity: 1; }
.gallery-card:hover .gc-img { transform: scale(1.06); }

/* 图片容器 - 约第612行附近 */
.gc-img-wrap {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #061020;
  border-radius: 20px;  /* 新增：给图片区域加圆角 */
}

.gc-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s cubic-bezier(0.25,0.46,0.45,0.94);
  border-radius: 18px;  /* 添加这一行 */
}

.gc-overlay {
  position: absolute;
  inset: 0;
  background: rgba(5,15,26,0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.25s ease;
}

.gc-zoom-icon {
  width: 44px;
  height: 44px;
  background: rgba(56,189,248,0.2);
  border: 1px solid rgba(56,189,248,0.4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #38bdf8;
  backdrop-filter: blur(8px);
}

.gc-info {
  padding: 14px 16px 18px;
}

.gc-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.tag-blue   { background: rgba(56,189,248,0.12); color: #38bdf8; border: 1px solid rgba(56,189,248,0.2); }
.tag-purple { background: rgba(139,92,246,0.12); color: #c4b5fd; border: 1px solid rgba(139,92,246,0.2); }
.tag-teal   { background: rgba(20,184,166,0.12); color: #5eead4; border: 1px solid rgba(20,184,166,0.2); }
.tag-green  { background: rgba(16,185,129,0.12); color: #34d399; border: 1px solid rgba(16,185,129,0.2); }
.tag-amber  { background: rgba(245,158,11,0.12); color: #fcd34d; border: 1px solid rgba(245,158,11,0.2); }

.gc-label {
  font-size: 14px;
  font-weight: 600;
  color: #e2e8f0;
  margin-bottom: 6px;
  line-height: 1.4;
}

.gc-acc {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #34d399;
  font-weight: 600;
}

/* 鼠标光晕 */
.gallery-cursor-glow {
  position: absolute;
  width: 340px;
  height: 340px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(56,189,248,0.12) 0%, transparent 65%);
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 1;
  transition: opacity 0.4s ease;
}

/* ===== 灯箱 ===== */
.lightbox {
  position: fixed;
  inset: 0;
  background: rgba(3,9,18,0.94);
  backdrop-filter: blur(16px);
  z-index: 9000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lb-content {
  position: relative;
  max-width: 860px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.lb-img {
  max-width: 100%;
  max-height: 76vh;
  border-radius: 16px;
  box-shadow: 0 32px 80px rgba(0,0,0,0.7);
  display: block;
  object-fit: contain;
}

.lb-caption {
  display: flex;
  align-items: center;
  gap: 10px;
}

.lb-label {
  font-size: 15px;
  font-weight: 600;
  color: #e2e8f0;
}

.lb-close, .lb-prev, .lb-next {
  position: fixed;
  background: rgba(10,22,38,0.85);
  border: 1px solid rgba(125,211,252,0.2);
  color: #e2e8f0;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 9001;
  backdrop-filter: blur(8px);
}

.lb-close { top: 24px; right: 24px; }
.lb-prev  { left: 24px; top: 50%; transform: translateY(-50%); }
.lb-next  { right: 24px; top: 50%; transform: translateY(-50%); }

.lb-close:hover, .lb-prev:hover, .lb-next:hover {
  background: rgba(56,189,248,0.15);
  border-color: rgba(56,189,248,0.4);
  color: #38bdf8;
}

.lb-fade-enter-active, .lb-fade-leave-active { transition: opacity 0.25s ease; }
.lb-fade-enter-from, .lb-fade-leave-to { opacity: 0; }

/* ===== CONTENT SECTION ===== */
.content-section {
  padding: 32px;
}

.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
}

.section-title {
  margin-top: 12px;
  font-size: clamp(22px, 3.5vw, 32px);
  line-height: 1.2;
  letter-spacing: -0.02em;
  font-weight: 700;
}

.section-desc { max-width: 520px; }

/* ===== SERVICE GRID ===== */
.service-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0,1fr));
  gap: 16px;
}

.service-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 320px;
  padding: 24px;
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(7,22,38,0.92), rgba(4,13,24,0.92));
  border: 1px solid rgba(125,211,252,0.1);
  transition: transform 0.35s cubic-bezier(0.34,1.56,0.64,1), border-color 0.3s ease, box-shadow 0.3s ease;
}

.service-card:hover {
  transform: translateY(-8px) scale(1.01);
  border-color: rgba(125,211,252,0.22);
  box-shadow: 0 24px 48px rgba(3,12,24,0.4);
}

.theme-cyan   { background: linear-gradient(180deg, rgba(6,40,48,0.92), rgba(4,15,24,0.95)); }
.theme-blue   { background: linear-gradient(180deg, rgba(8,33,62,0.92), rgba(5,14,26,0.95)); }
.theme-teal   { background: linear-gradient(180deg, rgba(5,38,49,0.92), rgba(5,14,26,0.95)); }
.theme-indigo { background: linear-gradient(180deg, rgba(18,28,62,0.92), rgba(5,14,26,0.95)); }

.service-title {
  margin-top: 12px;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.35;
}

.service-points {
  display: grid;
  gap: 8px;
  margin: 18px 0 22px;
  padding: 0;
  list-style: none;
}

.service-points li,
.module-point {
  position: relative;
  padding-left: 18px;
}

.service-points li::before,
.module-point::before {
  content: "";
  position: absolute;
  left: 0;
  top: 9px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67e8f9, #38bdf8);
  box-shadow: 0 0 8px rgba(56,189,248,0.5);
}

.service-points li {
  color: rgba(223,236,255,0.72);
  font-size: 14px;
  line-height: 1.7;
}

.btn-card {
  align-self: flex-start;
  color: #edf9ff;
  background: rgba(8,20,36,0.92);
  border-color: rgba(125,211,252,0.2);
  font-size: 13px;
  min-height: 40px;
  padding: 0 18px;
}

.btn-card:hover {
  background: rgba(16,38,62,0.95);
  border-color: rgba(125,211,252,0.38);
}

/* ===== MODULE LAYOUT ===== */
.module-layout {
  display: grid;
  grid-template-columns: 300px minmax(0,1fr);
  gap: 18px;
}

.module-tabs { display: grid; gap: 12px; }

.module-tab {
  padding: 18px;
  text-align: left;
  border: 1px solid rgba(125,211,252,0.1);
  background: rgba(8,19,34,0.8);
  border-radius: 18px;
  cursor: pointer;
  transition: transform 0.25s ease, border-color 0.25s ease, background 0.25s ease;
}

.module-tab:hover {
  background: rgba(14,30,52,0.9);
  border-color: rgba(94,234,212,0.22);
}

.module-tab.active {
  border-color: rgba(94,234,212,0.32);
  background: linear-gradient(180deg, rgba(8,33,48,0.92), rgba(7,19,33,0.95));
  box-shadow: inset 0 0 0 1px rgba(94,234,212,0.1);
}

.module-tab-title { display: block; color: #f8fbff; font-size: 16px; font-weight: 700; }
.module-tab-desc { display: block; margin-top: 6px; color: rgba(214,230,255,0.58); font-size: 13px; line-height: 1.6; }

.module-display {
  padding: 28px;
  background: radial-gradient(circle at top right, rgba(56,189,248,0.1), transparent 28%),
              linear-gradient(180deg, rgba(7,22,38,0.96), rgba(4,13,24,0.96));
  border: 1px solid rgba(125,211,252,0.12);
  border-radius: 24px;
  transition: box-shadow 0.3s ease;
}

.module-display:hover { box-shadow: 0 16px 40px rgba(3,12,24,0.3); }

.module-display-head h3 { margin: 12px 0 10px; font-size: 26px; line-height: 1.25; color: #f8fbff; }

.module-highlight {
  margin: 20px 0;
  padding: 16px 20px;
  border-radius: 16px;
  background: rgba(10,27,44,0.72);
  border: 1px solid rgba(125,211,252,0.12);
}

.highlight-title { margin-bottom: 6px; color: #8fe7ff; font-size: 12px; font-weight: 700; letter-spacing: 0.06em; }

.module-points {
  display: grid;
  grid-template-columns: repeat(3, minmax(0,1fr));
  gap: 14px;
  margin-bottom: 24px;
}

.module-point {
  padding: 16px 16px 16px 22px;
  border-radius: 16px;
  background: rgba(6,18,31,0.72);
  border: 1px solid rgba(125,211,252,0.08);
  transition: border-color 0.2s ease;
}

.module-point:hover { border-color: rgba(125,211,252,0.18); }
.module-point-title { font-size: 14px; font-weight: 700; margin-bottom: 6px; }
.module-point-text { font-size: 13px; color: rgba(166, 170, 177, 0.854); line-height: 1.65; }

/* ===== JOURNEY ===== */
.journey-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0,1fr));
  gap: 16px;
}

.journey-card,
.resource-card {
  padding: 24px;
  background: rgba(7,21,35,0.84);
  border: 1px solid rgba(125,211,252,0.1);
  border-radius: 22px;
  transition: transform 0.3s cubic-bezier(0.34,1.56,0.64,1), border-color 0.3s ease, box-shadow 0.3s ease;
}

.journey-card:hover,
.resource-card:hover {
  transform: translateY(-6px);
  border-color: rgba(125,211,252,0.22);
  box-shadow: 0 18px 40px rgba(3,12,24,0.3);
}

.journey-step {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 52px;
  height: 32px;
  margin-bottom: 16px;
  border-radius: 999px;
  background: rgba(34,211,238,0.08);
  border: 1px solid rgba(34,211,238,0.2);
  color: #86e9ff;
  font-size: 13px;
  font-weight: 700;
}

.journey-card-title,
.resource-title { font-size: 19px; font-weight: 700; margin-bottom: 10px; }

/* ===== RESOURCE ===== */
.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0,1fr));
  gap: 16px;
}

.resource-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  margin-top: 20px;
  padding: 0 18px;
  border-radius: 999px;
  border: 1px solid rgba(125,211,252,0.2);
  background: rgba(10,22,38,0.68);
  color: #c9eeff;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: transform 0.25s ease, border-color 0.25s ease, background 0.25s ease, color 0.25s ease;
}

.resource-link:hover {
  transform: translateY(-2px);
  border-color: rgba(125,211,252,0.38);
  background: rgba(18,40,67,0.84);
  color: #f4fbff;
}

/* ===== CTA ===== */
.cta-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 36px 40px;
}

/* ===== REVEAL ANIMATION ===== */
.reveal-item {
  opacity: 0;
  transform: translateY(28px);
  transition: opacity 0.7s ease, transform 0.7s ease;
}

.reveal-item.is-visible {
  opacity: 1;
  transform: translateY(0);
}

/* ===== RESPONSIVE ===== */
@media (max-width: 1180px) {
  .hero-section,
  .module-layout,
  .cta-section { grid-template-columns: 1fr; }
  .hero-section, .cta-section { display: grid; }
  .service-grid, .journey-grid { grid-template-columns: repeat(2, minmax(0,1fr)); }
  .resource-grid, .module-points { grid-template-columns: 1fr; }
  .gallery-track { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 900px) {
  .gallery-track { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 820px) {
  .page-shell { width: min(100% - 24px, 100%); padding: 10px 0 48px; }
  .hero-section, .content-section, .cta-section, .gallery-section { padding: 24px; border-radius: 22px; }
  .section-head { flex-direction: column; align-items: flex-start; }
  .section-desc { max-width: none; }
  .service-grid, .journey-grid, .resource-grid, .hero-metrics { grid-template-columns: 1fr; }
  .module-layout { grid-template-columns: 1fr; }
  .module-points { grid-template-columns: 1fr; }
  .hero-title { font-size: 30px; }
  .gallery-track { grid-template-columns: repeat(2, 1fr); gap: 14px; }
  .gallery-section { cursor: default; }
}

@media (max-width: 560px) {
  .btn, .resource-link, .quick-nav-chip { width: 100%; }
  .hero-actions, .quick-nav, .module-actions, .cta-actions { flex-direction: column; }
  .gallery-track { grid-template-columns: 1fr; }
  .lb-prev { left: 8px; }
  .lb-next { right: 8px; }
}
</style>
