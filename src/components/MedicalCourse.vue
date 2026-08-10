<template>
  <div class="medical-course" :class="{ 'medical-theme': isMedicalTheme }">
    <!-- 背景装饰 -->
    <div class="bg-canvas" aria-hidden="true">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="course-shell">
      <!-- 医学提示横幅 -->
      <div class="safety-banner">
        <div class="safety-icon">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 3 5 6v6c0 5 3.4 8.2 7 9 3.6-.8 7-4 7-9V6l-7-3Z" />
            <path d="m9.5 12 1.8 1.8 3.7-4.2" />
          </svg>
        </div>
        <div class="safety-copy">
          <strong>医学内容提示</strong>
          <span>课程仅用于健康教育与知识理解，不替代医生面诊、检查和临床决策。出现急症或持续不适，请及时就医。</span>
        </div>
      </div>

      <!-- 目录视图 -->
      <div v-if="!selectedCourse" class="catalog-view">
        <button class="page-back-btn" @click="goBackToPreviousPage">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <path d="M19 12H5" /><path d="m12 5-7 7 7 7" />
          </svg>
          返回
        </button>

        <!-- Hero -->
        <section class="course-hero">
          <div class="hero-copy">
            <div class="eyebrow">
              <span class="eyebrow-dot"></span>
              MEDICAL LEARNING CENTER
            </div>
            <h1 class="hero-title">专业医疗 AI<br><span class="hero-title-accent">系统化学习平台</span></h1>
            <p class="hero-subtitle">围绕健康管理、医学影像、远程医疗与合规治理，建立一套更适合真实用户持续学习的课程体验。</p>

            <div class="hero-actions">
              <button class="hero-primary" @click="selectCourse(recommendedCourse)">
                <span class="btn-inner">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
                  开始学习
                </span>
                <span class="btn-shimmer"></span>
              </button>
              <div class="hero-caption">精选课程结构、学习路径与课后资料已整合</div>
            </div>

            <div class="hero-stats">
              <div class="stat-card" v-for="item in courseStats" :key="item.label">
                <div class="stat-value">{{ item.num }}</div>
                <div class="stat-label">{{ item.label }}</div>
              </div>
            </div>
          </div>

          <div class="hero-visual">
            <div class="hero-spotlight">
              <div class="spotlight-top">
                <span class="spotlight-badge">
                  <span class="badge-pulse"></span>本周推荐
                </span>
                <TagLevel :level="recommendedCourse.level" size="small" />
              </div>
              <h2 class="spotlight-title">{{ recommendedCourse.title }}</h2>
              <p class="spotlight-desc">{{ recommendedCourse.description }}</p>
              <div class="spotlight-meta">
                <span>{{ recommendedCourse.duration }}</span>
                <span>{{ recommendedCourse.views }} 人学习</span>
                <span>{{ recommendedCourse.audience }}</span>
              </div>
              <div class="spotlight-points">
                <div class="spotlight-point" v-for="point in recommendedCourse.outline" :key="point">
                  <span class="spotlight-dot"></span>
                  <span>{{ point }}</span>
                </div>
              </div>
            </div>

            <div class="hero-orbit-card">
              <div class="orbit-grid"></div>
              <div class="orbit-ring orbit-ring-lg"></div>
              <div class="orbit-ring orbit-ring-md"></div>
              <div class="orbit-core">
                <div class="orbit-core-inner">
                  <CourseGlyph :icon="recommendedCourse.icon" :tone="recommendedCourse.iconTone" compact />
                </div>
              </div>
              <div class="orbit-pill orbit-pill-top">Clinical</div>
              <div class="orbit-pill orbit-pill-right">Imaging</div>
              <div class="orbit-pill orbit-pill-bottom">Data</div>
            </div>
          </div>
        </section>

        <!-- 课程矩阵 -->
        <section class="catalog-panel">
          <div class="panel-header">
            <div>
              <div class="panel-kicker"><span class="kicker-line"></span>课程矩阵</div>
              <h2 class="panel-title">按学习阶段组织内容，建立连续学习节奏</h2>
            </div>
            <div class="level-filters">
              <button
                v-for="filter in levelFilters" :key="filter.key"
                class="lf-btn"
                :class="{ active: activeFilter === filter.key }"
                @click="activeFilter = filter.key"
              >{{ filter.label }}</button>
            </div>
          </div>

          <div class="course-grid">
            <article
              class="course-card"
              v-for="course in filteredCourses" :key="course.id"
              :class="`tone-${course.level}`"
              @click="selectCourse(course)"
            >
              <div class="card-glow"></div>
              <div class="card-top">
                <div class="course-symbol">
                  <div class="course-symbol-orbit"></div>
                  <div class="course-symbol-core">
                    <CourseGlyph :icon="course.icon" :tone="course.iconTone" compact />
                  </div>
                </div>
                <TagLevel :level="course.level" size="small" />
              </div>
              <div class="card-body">
                <div class="card-track">{{ course.track }}</div>
                <h3 class="card-title">{{ course.title }}</h3>
                <p class="card-desc">{{ course.description }}</p>
                <div class="card-meta">
                  <div class="meta-chip">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
                    {{ course.duration }}
                  </div>
                  <div class="meta-chip">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                    {{ course.views }} 人学习
                  </div>
                </div>
                <div class="card-insight">
                  <div class="insight-label">你将获得</div>
                  <div class="insight-text">{{ course.takeaway }}</div>
                </div>
              </div>
              <div class="card-footer">
                <div class="card-audience">{{ course.audience }}</div>
                <button class="start-btn">
                  开始学习
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
                </button>
              </div>
            </article>
          </div>
        </section>

        <!-- 学习路径 -->
        <section class="journey-grid">
          <div class="learning-journey">
            <div class="journey-head">
              <div class="panel-kicker"><span class="kicker-line"></span>推荐路径</div>
              <h2 class="panel-title">更适合医疗用户认知节奏的学习顺序</h2>
              <p class="journey-subtitle">先建立基础概念，再进入具体应用、数据治理和合规边界，避免碎片化浏览。</p>
            </div>
            <div class="path-timeline">
              <div class="timeline-item" v-for="(step, index) in learningPath" :key="step.title">
                <div class="timeline-index"><span>{{ index + 1 }}</span></div>
                <div class="timeline-body">
                  <h3>{{ step.title }}</h3>
                  <p>{{ step.description }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="journey-notes">
            <div class="panel-kicker"><span class="kicker-line"></span>设计理念</div>
            <h2 class="panel-title">简洁、高级、克制的产品语气</h2>
            <div class="notes-list">
              <div class="note-item" v-for="note in experienceNotes" :key="note.title">
                <div class="note-icon">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                </div>
                <div>
                  <div class="note-title">{{ note.title }}</div>
                  <p class="note-desc">{{ note.description }}</p>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- 详情视图 -->
      <div v-else class="detail-view">
        <div class="detail-topbar">
          <button class="back-btn" @click="goBack">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M19 12H5"/><path d="m12 5-7 7 7 7"/></svg>
            返回课程列表
          </button>
          <div class="detail-breadcrumb">
            <span>课程中心</span>
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg>
            <span class="current">{{ selectedCourse.title }}</span>
          </div>
        </div>

        <div class="detail-layout">
          <aside class="detail-sidebar">
            <!-- 进度 -->
            <section class="sidebar-card sidebar-progress">
              <div class="sidebar-title-row">
                <span class="sidebar-title">学习进度</span>
                <span class="sidebar-status" :class="progress >= 100 ? 'done' : 'ongoing'">
                  {{ progress >= 100 ? '已完成' : '学习中' }}
                </span>
              </div>
              <div class="progress-visual">
                <svg width="112" height="112" viewBox="0 0 112 112">
                  <defs>
                    <linearGradient id="progressGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                      <stop offset="0%" stop-color="#00d4b4"/>
                      <stop offset="100%" stop-color="#3b82f6"/>
                    </linearGradient>
                  </defs>
                  <circle cx="56" cy="56" r="45" class="progress-track"></circle>
                  <circle cx="56" cy="56" r="45" class="progress-value"
                    :stroke-dasharray="progressCircumference"
                    :stroke-dashoffset="progressCircumference * (1 - progress / 100)"
                  ></circle>
                </svg>
                <div class="progress-center">
                  <strong>{{ progress }}%</strong>
                  <span>{{ progressText }}</span>
                </div>
              </div>
              <div class="progress-hint">{{ selectedCourse.takeaway }}</div>
            </section>

            <!-- 便签 -->
            <section class="sidebar-card">
              <div class="sidebar-title-row">
                <span class="sidebar-title">课程便签</span>
                <span class="sidebar-muted">随学随记</span>
              </div>
              <textarea class="notes-textarea" v-model="courseNotes" placeholder="记录关键概念、面诊前想问的问题或复习重点。"></textarea>
              <button class="sidebar-btn" @click="exportNotes">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                导出便签
              </button>
            </section>

            <!-- 测验 -->
            <section class="sidebar-card">
              <div class="sidebar-title-row">
                <span class="sidebar-title">课后测验</span>
                <span class="sidebar-muted">{{ miniAnswers.filter(v => v !== undefined).length }}/{{ miniQuiz.length }}</span>
              </div>
              <div class="quiz-list">
                <div class="quiz-item" v-for="(quiz, index) in miniQuiz" :key="quiz.question">
                  <div class="quiz-question">{{ quiz.question }}</div>
                  <label class="quiz-option" v-for="(option, optionIndex) in quiz.options" :key="option">
                    <input type="radio" :name="`mini-${index}`" :value="optionIndex" v-model="miniAnswers[index]">
                    <span>{{ option }}</span>
                  </label>
                </div>
              </div>
              <button class="sidebar-btn sidebar-btn-primary" @click="submitMiniQuiz">提交测验</button>
            </section>

            <!-- 风险 -->
            <section class="sidebar-card sidebar-warning">
              <div class="sidebar-title-row">
                <span class="sidebar-title" style="color:var(--red)">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:5px"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                  风险提醒
                </span>
              </div>
              <ul class="warning-list">
                <li>AI 可辅助理解信息，但不能替代医生诊断。</li>
                <li>涉及用药、急症、手术判断时，应优先遵循专业医嘱。</li>
                <li>课程中的演示与案例属于科普示意，不构成个体化建议。</li>
              </ul>
            </section>
          </aside>

          <main class="detail-main">
            <!-- 课程 Hero -->
            <section class="detail-hero">
              <div class="detail-copy">
                <div class="detail-header-row">
                  <TagLevel :level="selectedCourse.level" size="large" />
                  <span class="detail-track">{{ selectedCourse.track }}</span>
                </div>
                <h1 class="detail-title">{{ selectedCourse.title }}</h1>
                <p class="detail-desc">{{ selectedCourse.description }}</p>
                <div class="detail-meta">
                  <div class="meta-box"><div class="meta-label">课程时长</div><div class="meta-value">{{ selectedCourse.duration }}</div></div>
                  <div class="meta-box"><div class="meta-label">学习人数</div><div class="meta-value">{{ selectedCourse.views }}</div></div>
                  <div class="meta-box"><div class="meta-label">适合对象</div><div class="meta-value">{{ selectedCourse.audience }}</div></div>
                </div>
                <div class="detail-outcomes">
                  <div class="outcome-chip" v-for="item in selectedCourse.outline" :key="item">
                    <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                    {{ item }}
                  </div>
                </div>
              </div>
              <div class="detail-symbol-card" :class="`tone-${selectedCourse.level}`">
                <div class="detail-symbol-orbit"></div>
                <div class="detail-symbol-core">
                  <CourseGlyph :icon="selectedCourse.icon" :tone="selectedCourse.iconTone" />
                </div>
                <div class="detail-symbol-caption">{{ selectedCourse.takeaway }}</div>
              </div>
            </section>

            <!-- 课程内容 -->
            <section class="course-content-shell">
              <div class="shell-head">
                <div class="panel-kicker"><span class="kicker-line"></span>课程内容</div>
                <h2 class="panel-title" style="margin-top:8px">深度学习内容，循序渐进掌握核心知识</h2>
              </div>
              <div class="course-component-surface">
                <component :is="getCourseComponent" :course="selectedCourse" @complete="markComplete"></component>
              </div>
            </section>

            <!-- 完成区域 -->
            <section v-if="progress >= 100" class="completion-section">
              <div class="completion-badge">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
                已完成
              </div>
              <h2 class="completion-title">恭喜完成课程学习</h2>
              <p class="completion-subtitle">{{ selectedCourse.title }} 的核心内容已学习完成，可继续领取资料或进入下一门课程。</p>
              <div class="completion-actions">
                <button class="completion-btn completion-btn-primary" @click="downloadCertificate">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/></svg>
                  下载完成证明
                </button>
                <button class="completion-btn" @click="downloadReward">{{ getRewardButtonText }}</button>
              </div>
            </section>
          </main>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CourseBasic from './CourseBasic.vue'
import CourseHealth from './CourseHealth.vue'
import CourseImage from './CourseImage.vue'
import CourseTele from './CourseTele.vue'
import CourseBigData from './CourseBigData.vue'
import CourseEthics from './CourseEthics.vue'
import TagLevel from './TagLevel.vue'
import CourseGlyph from './CourseGlyph.vue'

export default {
  name: 'MedicalCourse',
  components: { CourseBasic, CourseHealth, CourseImage, CourseTele, CourseBigData, CourseEthics, TagLevel, CourseGlyph },
  data() {
    return {
      selectedCourse: null,
      courseNotes: '',
      progress: 0,
      miniAnswers: [],
      activeFilter: 'all',
      progressCircumference: 282.74,
      levelFilters: [
        { key: 'all', label: '全部' },
        { key: 'beginner', label: '入门级' },
        { key: 'intermediate', label: '进阶级' },
        { key: 'advanced', label: '高级' }
      ],
      courseStats: [
        { num: '6', label: '精品课程' },
        { num: '48,180+', label: '累计学习人次' },
        { num: '4.9 / 5', label: '平均评分' },
        { num: '持续更新', label: '课程状态' }
      ],
      miniQuiz: [
        { question: '关于 AI 在医疗中的作用，下列哪项更准确？', options: ['可以完全替代医生', '更适合作为医生的辅助工具', '主要用于直接开药', '只适合科研，不能落地'] },
        { question: '如果出现明显急症或持续恶化的不适，应当怎么做？', options: ['继续观察几天', '只看网络内容', '及时前往医院或联系医生', '完全依赖课程中的案例'] }
      ],
      experienceNotes: [
        { title: '模块清晰', description: '课程列表、推荐路径、详情学习三段式组织，更接近商业产品的信息架构。' },
        { title: '动效克制', description: '只保留轻微悬浮、轨道旋转和图标呼吸感，提升精致度但不过度炫技。' },
        { title: '医学语气', description: '避免"AI 味"过重的视觉符号，使用更理性、稳重的医疗产品语言。' }
      ],
      courses: [
        { id: 'basic', symbol: 'AI', icon: 'ai', iconTone: 'teal', level: 'beginner', track: '基础认知', title: 'AI 医疗基础入门', description: '理解 AI 在医疗场景中的实际价值、局限与基本工作方式，建立后续课程的认知基础。', duration: '2 小时', views: '12,580', audience: '适合初次接触医疗 AI 的用户', takeaway: '建立对 AI 辅助诊疗的正确预期', outline: ['认识医疗 AI 的能力边界', '看懂辅助诊断基本逻辑', '学会就医时如何与医生沟通 AI 结果'], reward: '患者 AI 就医指南', rewardButton: '下载患者 AI 就医指南' },
        { id: 'image', symbol: 'CT', icon: 'imaging', iconTone: 'blue', level: 'intermediate', track: '影像理解', title: '医学影像深度学习', description: '从真实使用场景理解 AI 如何参与影像识别、分割和辅助阅片，并认识结果解释的边界。', duration: '4 小时', views: '8,920', audience: '适合关注影像检查与诊断流程的用户', takeaway: '更清楚地理解 AI 影像结论如何辅助判断', outline: ['理解影像 AI 的常见任务', '认识敏感度与特异度', '学会与影像报告交叉阅读'], reward: 'AI 影像检查患者指引', rewardButton: '下载 AI 影像检查患者指引' },
        { id: 'health', symbol: 'HM', icon: 'health', iconTone: 'rose', level: 'beginner', track: '健康管理', title: '健康管理与预防', description: '围绕日常健康监测、慢病管理和风险预防，帮助用户建立长期可执行的健康习惯。', duration: '1.5 小时', views: '15,340', audience: '适合家庭健康管理与日常预防用户', takeaway: '形成可执行的自我管理与预防框架', outline: ['看懂日常健康数据', '建立症状与风险意识', '形成稳定的随访习惯'], reward: '家庭健康管理手册', rewardButton: '下载家庭健康管理手册' },
        { id: 'bigdata', symbol: 'DX', icon: 'data', iconTone: 'amber', level: 'advanced', track: '数据治理', title: '医疗大数据分析', description: '从数据质量、隐私保护到决策支持，系统了解医疗数据在业务和临床中的协同价值。', duration: '6 小时', views: '5,680', audience: '适合希望理解数据治理的进阶用户', takeaway: '理解数据如何支撑临床与管理决策', outline: ['认识医疗数据治理流程', '理解统计与分析方法', '掌握数据合规基本原则'], reward: '医疗数据知情权指引', rewardButton: '下载医疗数据知情权指引' },
        { id: 'tele', symbol: 'TM', icon: 'tele', iconTone: 'violet', level: 'intermediate', track: '远程服务', title: '远程医疗实践', description: '了解在线问诊、远程随访和连续监测的典型模式，理解远程医疗的便利性与适用边界。', duration: '3 小时', views: '7,210', audience: '适合有在线问诊与居家监测需求的用户', takeaway: '知道哪些场景适合远程医疗，哪些不适合', outline: ['熟悉远程问诊流程', '识别远程服务适用场景', '明确线下复诊触发条件'], reward: '远程就医操作手册', rewardButton: '下载远程就医操作手册' },
        { id: 'ethics', symbol: 'PR', icon: 'ethics', iconTone: 'teal', level: 'advanced', track: '伦理合规', title: '医学伦理与隐私', description: '理解医疗 AI 使用中的隐私保护、患者知情同意和伦理边界，让技术使用更安全可信。', duration: '1 小时', views: '9,450', audience: '适合关注隐私、授权与合规风险的用户', takeaway: '建立对隐私保护和知情同意的基本判断', outline: ['理解知情同意要点', '认识隐私保护责任', '识别高风险的数据使用情境'], reward: '患者医疗隐私保护手册', rewardButton: '下载患者医疗隐私保护手册' }
      ],
      learningPath: [
        { title: 'AI 医疗基础入门', description: '先建立对 AI 辅助诊疗的基本认知，避免误解其能力边界。' },
        { title: '健康管理与预防', description: '从日常健康场景切入，让知识更容易和真实生活连接。' },
        { title: '医学伦理与隐私', description: '在进入更复杂应用前，先理解数据与患者权益的边界。' },
        { title: '医学影像深度学习', description: '进一步理解更典型的医疗 AI 应用场景与结果解释。' },
        { title: '远程医疗实践', description: '把前面的认知迁移到真实服务流程与线上线下协同中。' }
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
    filteredCourses() {
      return this.activeFilter === 'all' ? this.courses : this.courses.filter(c => c.level === this.activeFilter)
    },
    recommendedCourse() { return this.filteredCourses[0] || this.courses[0] },
    getCourseComponent() {
      if (!this.selectedCourse) return null
      const map = { basic: CourseBasic, health: CourseHealth, image: CourseImage, tele: CourseTele, bigdata: CourseBigData, ethics: CourseEthics }
      return map[this.selectedCourse.id] || CourseBasic
    },
    getRewardButtonText() { return this.selectedCourse ? this.selectedCourse.rewardButton : '' },
    progressText() {
      if (this.progress >= 100) return '已完成'
      if (this.progress >= 50) return '进行中'
      return '未完成'
    }
  },
  methods: {
    goBackToPreviousPage() { this.$router.push('/patient/home') },
    selectCourse(course) { this.selectedCourse = course; this.progress = 0; this.miniAnswers = []; this.courseNotes = ''; window.scrollTo({ top: 0, behavior: 'smooth' }) },
    goBack() { this.selectedCourse = null; window.scrollTo({ top: 0, behavior: 'smooth' }) },
    markComplete() { this.progress = 100 },
    exportNotes() {
      if (!this.courseNotes.trim()) { window.alert('请先记录课程便签，再执行导出。'); return }
      window.alert(`课程便签已生成导出内容：\n\n${this.courseNotes}`)
    },
    submitMiniQuiz() {
      if (this.miniAnswers.length < this.miniQuiz.length || this.miniAnswers.some(v => v === undefined)) { window.alert('请先完成全部测验题目。'); return }
      this.progress = Math.max(this.progress, 35)
      window.alert('测验已提交。\n\n参考答案：\n1. 更适合作为医生的辅助工具\n2. 及时前往医院或联系医生')
    },
    downloadCertificate() { window.alert('完成证明已生成。') },
    downloadReward() { window.alert(`${this.selectedCourse.reward} 已开始下载。`) }
  }
}
</script>

<style scoped>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

/* ══════════════════════ 设计令牌 ══════════════════════ */
.medical-course {
  --bg: #07101f;
  --surface: rgba(255,255,255,0.03);
  --surface-2: rgba(255,255,255,0.06);
  --surface-3: rgba(255,255,255,0.10);
  --border: rgba(255,255,255,0.07);
  --border-2: rgba(255,255,255,0.11);
  --border-accent: rgba(0,212,180,0.28);
  --text-1: #eef4ff;
  --text-2: #8fa3c0;
  --text-3: #4a5f7a;
  --cyan: #00d4b4;
  --cyan-dim: rgba(0,212,180,0.10);
  --blue: #4a9eff;
  --blue-dim: rgba(74,158,255,0.10);
  --green: #10b981;
  --amber: #f59e0b;
  --red: #f87171;
  --purple: #a78bfa;
  --r-sm: 12px; --r-md: 16px; --r-lg: 22px; --r-xl: 28px;

  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  background: var(--bg);
  color: var(--text-1);
  font-family: 'SF Pro Display', 'PingFang SC', 'HarmonyOS Sans SC', system-ui, sans-serif;
  position: relative;
  overflow-x: hidden;
}

.medical-course.medical-theme {
  --bg:
    radial-gradient(circle at 12% 10%, rgba(21, 62, 114, 0.14) 0%, transparent 26%),
    radial-gradient(circle at 86% 18%, rgba(11, 159, 153, 0.12) 0%, transparent 28%),
    linear-gradient(180deg, #f7fbff 0%, #eef5fb 44%, #e7f0f8 100%);
  --surface: rgba(255,255,255,0.52);
  --surface-2: rgba(255,255,255,0.7);
  --surface-3: rgba(244,248,253,0.92);
  --border: rgba(120,151,183,0.18);
  --border-2: rgba(96,132,171,0.24);
  --border-accent: rgba(21,62,114,0.16);
  --text-1: #11294b;
  --text-2: #5f7690;
  --text-3: #7f92aa;
  --cyan: #0b9f99;
  --cyan-dim: rgba(11,159,153,0.08);
  --blue: #153e72;
  --blue-dim: rgba(21,62,114,0.08);
  --green: #17a773;
  --amber: #d79933;
  --red: #d75c69;
  --purple: #7567d8;
}

/* ══════════════════════ 背景 ══════════════════════ */
.bg-canvas { position: fixed; inset: 0; pointer-events: none; z-index: 0; overflow: hidden; }

.bg-orb {
  position: absolute; border-radius: 50%;
  filter: blur(90px); opacity: 0.18;
}
.bg-orb-1 { width: 700px; height: 700px; background: radial-gradient(circle, #00d4b4, transparent 70%); top: -250px; left: -150px; animation: drift1 22s ease-in-out infinite alternate; }
.bg-orb-2 { width: 550px; height: 550px; background: radial-gradient(circle, #3b82f6, transparent 70%); top: 15%; right: -180px; animation: drift2 28s ease-in-out infinite alternate; }
.bg-orb-3 { width: 450px; height: 450px; background: radial-gradient(circle, #7c3aed, transparent 70%); bottom: 5%; left: 25%; animation: drift3 20s ease-in-out infinite alternate; }
.bg-grid {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(255,255,255,0.025) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.025) 1px, transparent 1px);
  background-size: 52px 52px;
}
.medical-course.medical-theme .bg-orb { opacity: 0.14; filter: blur(110px); }
.medical-course.medical-theme .bg-orb-1 { background: radial-gradient(circle, rgba(21, 62, 114, 0.9), transparent 70%); }
.medical-course.medical-theme .bg-orb-2 { background: radial-gradient(circle, rgba(11, 159, 153, 0.72), transparent 70%); }
.medical-course.medical-theme .bg-orb-3 { background: radial-gradient(circle, rgba(117, 103, 216, 0.4), transparent 70%); }
.medical-course.medical-theme .bg-grid {
  background-image:
    linear-gradient(rgba(38,84,126,0.055) 1px, transparent 1px),
    linear-gradient(90deg, rgba(38,84,126,0.055) 1px, transparent 1px);
}
@keyframes drift1 { to { transform: translate(70px,50px) scale(1.1); } }
@keyframes drift2 { to { transform: translate(-50px,70px); } }
@keyframes drift3 { to { transform: translate(60px,-40px) scale(0.9); } }

/* ══════════════════════ Shell ══════════════════════ */
.course-shell {
  position: relative; z-index: 1;
  max-width: 1380px; margin: 0 auto;
  padding: 24px 28px 96px;
}

/* ══════════════════════ 安全横幅 ══════════════════════ */
.safety-banner {
  display: flex; align-items: center; gap: 14px;
  padding: 13px 18px;
  border-radius: var(--r-md);
  border: 1px solid rgba(245,158,11,0.18);
  background: rgba(245,158,11,0.05);
  margin-bottom: 22px;
  backdrop-filter: blur(16px);
}
.medical-course.medical-theme .safety-banner {
  border-color: rgba(215,153,51,0.18);
  background: rgba(255,255,255,0.74);
  box-shadow: 0 16px 36px rgba(20,55,97,0.08);
}
.safety-icon {
  width: 34px; height: 34px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(245,158,11,0.12); color: var(--amber);
  border: 1px solid rgba(245,158,11,0.22); flex-shrink: 0;
}
.safety-copy { display: flex; flex-direction: column; gap: 2px; }
.safety-copy strong { color: var(--amber); font-size: 12px; font-weight: 600; }
.safety-copy span { font-size: 12px; color: rgba(245,158,11,0.65); line-height: 1.55; }
.medical-course.medical-theme .safety-copy span { color: rgba(143, 102, 26, 0.88); }

/* ══════════════════════ 按钮 ══════════════════════ */
.page-back-btn, .back-btn {
  display: inline-flex; align-items: center; gap: 7px;
  height: 38px; padding: 0 14px;
  border-radius: 999px; border: 1px solid var(--border-2);
  background: var(--surface-2); color: var(--text-2);
  font-size: 13px; font-weight: 500; cursor: pointer;
  transition: all 0.2s ease; backdrop-filter: blur(12px);
}
.page-back-btn:hover, .back-btn:hover {
  background: var(--surface-3); border-color: var(--border-accent);
  color: var(--text-1); transform: translateX(-2px);
}

/* ══════════════════════ 标签 ══════════════════════ */
.eyebrow {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 5px 12px; border-radius: 999px;
  border: 1px solid var(--border-accent); background: var(--cyan-dim);
  color: var(--cyan); font-size: 10px; font-weight: 700; letter-spacing: 0.14em;
}
.eyebrow-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: var(--cyan); animation: pulseDot 2.2s ease-in-out infinite;
}
@keyframes pulseDot { 0%,100% { opacity:1; transform:scale(1); } 50% { opacity:0.4; transform:scale(0.65); } }

.panel-kicker {
  display: inline-flex; align-items: center; gap: 8px;
  font-size: 10px; font-weight: 700; letter-spacing: 0.14em; color: var(--cyan);
}
.kicker-line { display: inline-block; width: 18px; height: 2px; background: linear-gradient(90deg, var(--cyan), transparent); border-radius: 1px; }

.panel-title {
  font-size: clamp(1.2rem,1.8vw,1.7rem); line-height: 1.25;
  letter-spacing: -0.03em; color: var(--text-1); font-weight: 600;
}

/* ══════════════════════ Hero ══════════════════════ */
.course-hero {
  display: grid; grid-template-columns: minmax(0,1.2fr) minmax(350px,0.9fr);
  gap: 26px; padding: 38px;
  border-radius: var(--r-xl); border: 1px solid var(--border);
  background: var(--surface); backdrop-filter: blur(24px);
  margin-top: 18px; position: relative; overflow: hidden;
}
.course-hero::before {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(0,212,180,0.04) 0%, transparent 55%, rgba(74,158,255,0.03) 100%);
  pointer-events: none;
}

.hero-copy { display: flex; flex-direction: column; gap: 18px; min-width: 0; }

.hero-title { font-size: clamp(1.9rem,3vw,2.9rem); line-height: 1.1; letter-spacing: -0.04em; color: var(--text-1); font-weight: 700; }
.hero-title-accent {
  background: linear-gradient(120deg, var(--cyan) 0%, var(--blue) 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.hero-subtitle { font-size: 14px; line-height: 1.85; color: var(--text-2); max-width: 560px; }

.hero-actions { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
.hero-primary {
  position: relative; overflow: hidden; border: none; border-radius: 999px;
  background: linear-gradient(135deg, #00c9ab, #3d9fff);
  color: #07101f; font-weight: 700; font-size: 13px; cursor: pointer;
  transition: all 0.3s ease; box-shadow: 0 0 28px rgba(0,212,180,0.28);
}
.medical-course.medical-theme .hero-primary,
.medical-course.medical-theme .lf-btn.active,
.medical-course.medical-theme .sidebar-btn-primary {
  background: linear-gradient(135deg, var(--blue), var(--cyan));
  color: #ffffff;
  box-shadow: 0 14px 30px rgba(20,55,97,0.16);
}
.btn-inner { display: flex; align-items: center; gap: 7px; padding: 11px 20px; position: relative; z-index: 1; }
.btn-shimmer {
  position: absolute; top: 0; left: -100%; width: 55%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.35), transparent);
  transform: skewX(-18deg); animation: shimmer 3.5s ease-in-out infinite;
}
@keyframes shimmer { 0% { left:-100%; } 50%,100% { left:160%; } }
.hero-primary:hover { transform: translateY(-2px); box-shadow: 0 0 44px rgba(0,212,180,0.42); }
.medical-course.medical-theme .hero-primary:hover,
.medical-course.medical-theme .sidebar-btn-primary:hover,
.medical-course.medical-theme .lf-btn.active:hover {
  box-shadow: 0 18px 34px rgba(20,55,97,0.2);
}

.hero-caption { font-size: 12px; color: var(--text-3); line-height: 1.6; }

.hero-stats { display: grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap: 10px; }
.stat-card {
  padding: 14px; border-radius: var(--r-md); border: 1px solid var(--border);
  background: var(--surface-2); transition: border-color 0.2s;
}
.stat-card:hover { border-color: var(--border-accent); }
.stat-value { font-size: 20px; font-weight: 700; letter-spacing: -0.03em; color: var(--text-1); }
.stat-label { margin-top: 3px; font-size: 10px; color: var(--text-3); }

/* ══════════════════════ Hero 右侧 ══════════════════════ */
.hero-visual { display: grid; gap: 14px; }
.hero-spotlight {
  padding: 18px 20px; border-radius: var(--r-lg);
  border: 1px solid var(--border); background: var(--surface-2); backdrop-filter: blur(12px);
}
.spotlight-top { display: flex; align-items: center; justify-content: space-between; gap: 10px; }
.spotlight-badge {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 3px 9px; border-radius: 999px;
  border: 1px solid rgba(0,212,180,0.25); background: var(--cyan-dim);
  color: var(--cyan); font-size: 10px; font-weight: 600;
}
.badge-pulse { width: 5px; height: 5px; border-radius: 50%; background: var(--cyan); animation: pulseDot 2.2s ease-in-out infinite; }
.spotlight-title { margin-top: 10px; font-size: 18px; line-height: 1.3; color: var(--text-1); font-weight: 600; }
.spotlight-desc { margin-top: 7px; font-size: 12px; line-height: 1.75; color: var(--text-2); }
.spotlight-meta { display: flex; flex-wrap: wrap; gap: 7px; margin-top: 12px; }
.spotlight-meta span {
  display: inline-flex; align-items: center; height: 24px; padding: 0 9px;
  border-radius: 999px; background: var(--surface); border: 1px solid var(--border);
  font-size: 10px; color: var(--text-2);
}
.medical-course.medical-theme .spotlight-badge {
  border-color: rgba(11,159,153,0.18);
  background: rgba(11,159,153,0.08);
}
.spotlight-points { margin-top: 12px; display: grid; gap: 9px; }
.spotlight-point { display: flex; align-items: center; gap: 9px; font-size: 12px; color: var(--text-2); }
.spotlight-dot { width: 5px; height: 5px; border-radius: 50%; background: linear-gradient(135deg, var(--cyan), var(--blue)); flex-shrink: 0; }

.hero-orbit-card {
  min-height: 230px; border-radius: var(--r-lg);
  border: 1px solid var(--border); background: var(--surface);
  position: relative; display: flex; align-items: center; justify-content: center; overflow: hidden;
}
.orbit-grid {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(0,212,180,0.05) 1px, transparent 1px), linear-gradient(90deg, rgba(0,212,180,0.05) 1px, transparent 1px);
  background-size: 26px 26px; mask-image: radial-gradient(circle at center, black, transparent 72%);
}
.medical-course.medical-theme .orbit-grid {
  background-image:
    linear-gradient(rgba(21,62,114,0.07) 1px, transparent 1px),
    linear-gradient(90deg, rgba(21,62,114,0.07) 1px, transparent 1px);
}
.orbit-ring { position: absolute; border-radius: 50%; animation: orbitSpin 16s linear infinite; }
.orbit-ring-lg { width: 190px; height: 190px; border: 1px solid rgba(0,212,180,0.18); }
.orbit-ring-md { width: 128px; height: 128px; border: 1px solid rgba(74,158,255,0.22); animation-direction: reverse; animation-duration: 11s; }
.orbit-core {
  position: relative; z-index: 1; width: 92px; height: 92px; border-radius: 26px;
  background: var(--surface-2); border: 1px solid var(--border-2);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 36px rgba(0,212,180,0.12);
}
.orbit-core-inner {
  font-size: 24px; font-weight: 800; letter-spacing: 0.1em;
  background: linear-gradient(135deg, var(--cyan), var(--blue));
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.orbit-pill {
  position: absolute; height: 24px; padding: 0 9px; border-radius: 999px;
  display: inline-flex; align-items: center; background: var(--surface-2);
  border: 1px solid var(--border-2); font-size: 10px; color: var(--text-2);
}
.orbit-pill-top { top: 18px; left: 50%; transform: translateX(-50%); }
.orbit-pill-right { right: 18px; top: 50%; transform: translateY(-50%); }
.orbit-pill-bottom { bottom: 18px; left: 32px; }

/* ══════════════════════ 课程矩阵 ══════════════════════ */
.catalog-panel {
  margin-top: 22px; padding: 30px; border-radius: var(--r-xl);
  border: 1px solid var(--border); background: var(--surface); backdrop-filter: blur(20px);
}
.panel-header { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 26px; flex-wrap: wrap; }
.level-filters { display: flex; flex-wrap: wrap; gap: 7px; }
.lf-btn {
  height: 34px; padding: 0 13px; border-radius: 999px;
  border: 1px solid var(--border-2); background: var(--surface-2);
  color: var(--text-2); font-size: 12px; font-weight: 600; cursor: pointer; transition: all 0.2s ease;
}
.lf-btn:hover { border-color: var(--border-accent); color: var(--cyan); }
.lf-btn.active {
  background: linear-gradient(135deg, #00c9ab, #3d9fff); border-color: transparent;
  color: #07101f; box-shadow: 0 0 18px rgba(0,212,180,0.22);
}

.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px,1fr)); gap: 16px; }

.course-card {
  position: relative; display: flex; flex-direction: column; min-height: 350px;
  padding: 18px; border-radius: var(--r-lg); border: 1px solid var(--border);
  background: var(--surface-2); overflow: hidden; cursor: pointer;
  transition: all 0.3s ease;
}
.course-card:hover {
  transform: translateY(-4px); border-color: var(--border-accent);
  box-shadow: 0 20px 50px rgba(0,0,0,0.5), 0 0 0 1px rgba(0,212,180,0.08);
}
.medical-course.medical-theme .course-card:hover {
  box-shadow: 0 20px 44px rgba(20,55,97,0.12), 0 0 0 1px rgba(21,62,114,0.08);
}
.card-glow {
  position: absolute; inset: 0 auto auto 0; width: 150px; height: 150px;
  border-radius: 50%; background: radial-gradient(circle, rgba(0,212,180,0.07), transparent 70%);
  transform: translate(-30%,-30%); pointer-events: none; transition: opacity 0.3s;
}
.course-card:hover .card-glow { opacity: 2.5; }

.card-top { display: flex; align-items: center; justify-content: space-between; gap: 10px; }

.course-symbol {
  position: relative; width: 60px; height: 60px; border-radius: 18px;
  display: flex; align-items: center; justify-content: center;
  background: var(--surface-3); border: 1px solid var(--border-2);
}
.course-symbol-orbit { position: absolute; inset: 5px; border-radius: 14px; border: 1px solid rgba(0,212,180,0.18); animation: orbitSpin 10s linear infinite; }
.course-symbol-core {
  position: relative; z-index: 1; width: 38px; height: 38px; border-radius: 11px;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; font-weight: 800; letter-spacing: 0.1em;
  color: var(--cyan); background: linear-gradient(135deg, var(--cyan-dim), var(--blue-dim));
  border: 1px solid rgba(0,212,180,0.18);
}

.card-body { flex: 1; margin-top: 18px; }
.card-track { font-size: 9px; font-weight: 700; letter-spacing: 0.13em; color: var(--cyan); text-transform: uppercase; }
.card-title { margin-top: 7px; font-size: 17px; line-height: 1.28; letter-spacing: -0.02em; color: var(--text-1); font-weight: 600; }
.card-desc { margin-top: 9px; font-size: 12px; line-height: 1.8; color: var(--text-2); }

.card-meta { display: flex; flex-wrap: wrap; gap: 7px; margin-top: 12px; }
.meta-chip {
  display: inline-flex; align-items: center; gap: 4px; height: 24px; padding: 0 9px;
  border-radius: 999px; background: var(--surface); border: 1px solid var(--border);
  font-size: 10px; color: var(--text-2);
}
.card-insight {
  margin-top: 14px; padding: 10px 12px; border-radius: var(--r-sm);
  border: 1px solid var(--border-accent); background: var(--cyan-dim);
}
.insight-label { font-size: 9px; font-weight: 700; color: var(--cyan); letter-spacing: 0.08em; margin-bottom: 3px; }
.insight-text { font-size: 11px; color: rgba(0,212,180,0.75); line-height: 1.55; }

.card-footer { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-top: 16px; }
.card-audience { font-size: 10px; color: var(--text-3); flex: 1; }
.start-btn {
  display: inline-flex; align-items: center; gap: 5px; height: 32px; padding: 0 13px;
  border-radius: 999px; border: 1px solid var(--border-accent); background: var(--cyan-dim);
  color: var(--cyan); font-size: 11px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; flex-shrink: 0;
}
.start-btn:hover { background: var(--cyan); color: #07101f; border-color: transparent; box-shadow: 0 0 18px rgba(0,212,180,0.28); }
.medical-course.medical-theme .start-btn:hover {
  color: #ffffff;
  box-shadow: 0 14px 26px rgba(20,55,97,0.14);
}

/* ══════════════════════ 路径 ══════════════════════ */
.journey-grid { display: grid; grid-template-columns: 1.4fr 1fr; gap: 18px; margin-top: 22px; }
.learning-journey, .journey-notes {
  padding: 26px; border-radius: var(--r-xl);
  border: 1px solid var(--border); background: var(--surface); backdrop-filter: blur(20px);
}
.journey-head { margin-bottom: 22px; }
.journey-subtitle { margin-top: 8px; font-size: 12px; color: var(--text-2); line-height: 1.7; }

.path-timeline { display: grid; gap: 0; }
.timeline-item {
  display: flex; align-items: flex-start; gap: 12px; padding: 14px 0;
  border-bottom: 1px solid var(--border);
}
.timeline-item:last-child { border-bottom: none; }
.timeline-index {
  width: 30px; height: 30px; border-radius: 9px; display: flex; align-items: center; justify-content: center;
  background: var(--surface-3); border: 1px solid var(--border-accent);
  font-size: 12px; font-weight: 700; color: var(--cyan); flex-shrink: 0;
}
.timeline-body h3 { font-size: 13px; font-weight: 600; color: var(--text-1); margin-bottom: 3px; }
.timeline-body p { font-size: 11px; color: var(--text-2); line-height: 1.6; }

.notes-list { margin-top: 18px; display: grid; gap: 13px; }
.note-item { display: flex; align-items: flex-start; gap: 10px; }
.note-icon {
  width: 26px; height: 26px; border-radius: 8px; display: flex; align-items: center; justify-content: center;
  background: rgba(16,185,129,0.08); border: 1px solid rgba(16,185,129,0.18); color: var(--green); flex-shrink: 0;
}
.note-title { font-size: 13px; font-weight: 600; color: var(--text-1); margin-bottom: 3px; }
.note-desc { font-size: 11px; color: var(--text-2); line-height: 1.6; }

/* ══════════════════════ 详情视图 ══════════════════════ */
.detail-topbar { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 18px; flex-wrap: wrap; }
.detail-breadcrumb { display: flex; align-items: center; gap: 5px; font-size: 12px; color: var(--text-3); }
.detail-breadcrumb .current { color: var(--text-2); font-weight: 500; }

.detail-layout { display: grid; grid-template-columns: 288px 1fr; gap: 18px; align-items: start; }

.detail-sidebar { position: sticky; top: 20px; display: grid; gap: 12px; }

.sidebar-card {
  padding: 16px 18px; border-radius: var(--r-lg);
  border: 1px solid var(--border); background: var(--surface-2);
  backdrop-filter: blur(16px); position: relative; overflow: hidden;
}
.sidebar-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,180,0.3), transparent);
}
.sidebar-title-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.sidebar-title { font-size: 12px; font-weight: 600; color: var(--text-1); display: flex; align-items: center; }
.sidebar-muted { font-size: 10px; color: var(--text-3); }
.sidebar-status { font-size: 10px; font-weight: 600; padding: 3px 8px; border-radius: 999px; }
.sidebar-status.done { background: rgba(16,185,129,0.1); color: var(--green); border: 1px solid rgba(16,185,129,0.18); }
.sidebar-status.ongoing { background: var(--blue-dim); color: var(--blue); border: 1px solid rgba(74,158,255,0.18); }

.progress-visual { position: relative; width: 112px; height: 112px; margin: 14px auto; }
.progress-track { fill: none; stroke: var(--surface-3); stroke-width: 7; }
.progress-value {
  fill: none; stroke: url(#progressGrad); stroke-width: 7; stroke-linecap: round;
  transform: rotate(-90deg); transform-origin: 50% 50%;
  transition: stroke-dashoffset 0.9s cubic-bezier(0.4,0,0.2,1);
  filter: drop-shadow(0 0 5px rgba(0,212,180,0.35));
}
.progress-center { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 2px; }
.progress-center strong { font-size: 20px; font-weight: 700; color: var(--text-1); }
.progress-center span { font-size: 10px; color: var(--text-3); }
.progress-hint {
  font-size: 11px; color: var(--text-2); line-height: 1.55; text-align: center;
  padding: 9px; border-radius: var(--r-sm); background: var(--surface); border: 1px solid var(--border);
}

.notes-textarea {
  width: 100%; min-height: 90px; padding: 10px; border-radius: var(--r-sm);
  border: 1px solid var(--border); background: var(--surface); color: var(--text-1);
  font-size: 12px; line-height: 1.65; resize: vertical; font-family: inherit; margin-bottom: 9px;
  transition: border-color 0.2s;
}
.notes-textarea:focus { outline: none; border-color: var(--border-accent); }
.notes-textarea::placeholder { color: var(--text-3); }

.sidebar-btn {
  display: flex; align-items: center; justify-content: center; gap: 5px;
  width: 100%; height: 36px; border-radius: 999px;
  border: 1px solid var(--border-2); background: var(--surface-2);
  color: var(--text-2); font-size: 12px; font-weight: 500; cursor: pointer; transition: all 0.2s ease;
}
.sidebar-btn:hover { border-color: var(--border-accent); color: var(--cyan); }
.sidebar-btn-primary {
  background: linear-gradient(135deg, #00c9ab, #3d9fff); border-color: transparent;
  color: #07101f; font-weight: 700; box-shadow: 0 0 18px rgba(0,212,180,0.18);
}
.sidebar-btn-primary:hover { box-shadow: 0 0 28px rgba(0,212,180,0.32); }

.quiz-list { display: grid; gap: 14px; margin-bottom: 10px; }
.quiz-question { font-size: 12px; font-weight: 600; color: var(--text-1); margin-bottom: 8px; line-height: 1.5; }
.quiz-option {
  display: flex; align-items: center; gap: 7px; padding: 7px 9px; border-radius: var(--r-sm);
  cursor: pointer; transition: background 0.2s; font-size: 11px; color: var(--text-2);
  border: 1px solid transparent; margin-bottom: 3px;
}
.quiz-option:hover { background: var(--surface-3); border-color: var(--border); }

.sidebar-warning { border-color: rgba(248,113,113,0.12); background: rgba(248,113,113,0.03); }
.warning-list { list-style: none; display: grid; gap: 7px; }
.warning-list li {
  font-size: 11px; color: rgba(248,113,113,0.65); line-height: 1.55;
  padding-left: 12px; position: relative;
}
.warning-list li::before { content: '·'; position: absolute; left: 3px; color: var(--red); }

/* ══════════════════════ 详情主体 ══════════════════════ */
.detail-main { display: grid; gap: 18px; }

.detail-hero {
  display: grid; grid-template-columns: 1fr auto; gap: 22px; padding: 30px;
  border-radius: var(--r-xl); border: 1px solid var(--border);
  background: var(--surface); backdrop-filter: blur(20px); position: relative; overflow: hidden;
}
.detail-hero::before {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(0,212,180,0.03), transparent 60%); pointer-events: none;
}
.detail-copy { min-width: 0; }
.detail-header-row { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; }
.detail-track { font-size: 10px; font-weight: 700; letter-spacing: 0.1em; color: var(--text-3); }
.detail-title { font-size: clamp(1.5rem,2.5vw,2.2rem); line-height: 1.15; letter-spacing: -0.03em; color: var(--text-1); font-weight: 700; margin-bottom: 10px; }
.detail-desc { font-size: 13px; color: var(--text-2); line-height: 1.85; }

.detail-meta { display: grid; grid-template-columns: repeat(3,1fr); gap: 10px; margin-top: 18px; }
.meta-box { padding: 11px 12px; border-radius: var(--r-sm); border: 1px solid var(--border); background: var(--surface-2); }
.meta-label { font-size: 9px; color: var(--text-3); font-weight: 600; letter-spacing: 0.08em; margin-bottom: 3px; }
.meta-value { font-size: 13px; color: var(--text-1); font-weight: 600; }

.detail-outcomes { display: flex; flex-wrap: wrap; gap: 7px; margin-top: 16px; }
.outcome-chip {
  display: inline-flex; align-items: center; gap: 4px; height: 26px; padding: 0 10px;
  border-radius: 999px; border: 1px solid var(--border-accent); background: var(--cyan-dim);
  font-size: 11px; color: var(--cyan);
}

.detail-symbol-card {
  width: 148px; min-height: 168px; border-radius: var(--r-lg);
  border: 1px solid var(--border); background: var(--surface-2);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 18px; position: relative; overflow: hidden; gap: 9px; flex-shrink: 0;
}
.detail-symbol-orbit { position: absolute; inset: 18px; border-radius: 50%; border: 1px solid rgba(0,212,180,0.18); animation: orbitSpin 14s linear infinite; }
.detail-symbol-core {
  position: relative; z-index: 1; width: 64px; height: 64px; border-radius: 18px;
  display: flex; align-items: center; justify-content: center;
  font-size: 19px; font-weight: 800; letter-spacing: 0.1em;
  color: var(--cyan); background: linear-gradient(135deg, var(--cyan-dim), var(--blue-dim));
  border: 1px solid var(--border-accent);
}
.detail-symbol-caption { font-size: 10px; color: var(--text-2); text-align: center; line-height: 1.5; position: relative; z-index: 1; }

/* ══════════════════════ 课程内容 ══════════════════════ */
.course-content-shell {
  padding: 26px; border-radius: var(--r-xl);
  border: 1px solid var(--border); background: var(--surface); backdrop-filter: blur(20px);
}
.shell-head { margin-bottom: 20px; }

/* ══════════════════════ 完成 ══════════════════════ */
.completion-section {
  padding: 38px; border-radius: var(--r-xl);
  border: 1px solid rgba(16,185,129,0.18);
  background: rgba(16,185,129,0.03); text-align: center;
}
.completion-badge {
  display: inline-flex; align-items: center; gap: 5px; padding: 5px 12px; border-radius: 999px;
  border: 1px solid rgba(16,185,129,0.25); background: rgba(16,185,129,0.08);
  color: var(--green); font-size: 10px; font-weight: 700; letter-spacing: 0.08em; margin-bottom: 14px;
}
.completion-title { font-size: 1.7rem; font-weight: 700; letter-spacing: -0.03em; color: var(--text-1); margin-bottom: 9px; }
.completion-subtitle { font-size: 13px; color: var(--text-2); line-height: 1.75; max-width: 520px; margin: 0 auto 22px; }
.completion-actions { display: flex; justify-content: center; gap: 10px; flex-wrap: wrap; }
.completion-btn {
  display: inline-flex; align-items: center; gap: 6px; height: 42px; padding: 0 16px;
  border-radius: 999px; border: 1px solid var(--border-2); background: var(--surface-2);
  color: var(--text-1); font-size: 12px; font-weight: 600; cursor: pointer; transition: all 0.2s ease;
}
.completion-btn:hover { border-color: var(--border-accent); }
.completion-btn-primary {
  background: linear-gradient(135deg, var(--green), #059669); border-color: transparent;
  color: #fff; box-shadow: 0 0 22px rgba(16,185,129,0.22);
}
.completion-btn-primary:hover { box-shadow: 0 0 34px rgba(16,185,129,0.38); }

/* ══════════════════════ TagLevel deep 适配 ══════════════════════ */
.medical-course :deep(.tag-level) {
  border-radius: 999px !important; font-size: 9px !important; font-weight: 700 !important;
  letter-spacing: 0.1em !important; padding: 3px 9px !important; border: 1px solid !important;
}
.medical-course :deep(.tag-level[data-level="beginner"]) {
  background: rgba(16,185,129,0.1) !important; border-color: rgba(16,185,129,0.22) !important; color: var(--green) !important;
}
.medical-course :deep(.tag-level[data-level="intermediate"]) {
  background: var(--blue-dim) !important; border-color: rgba(74,158,255,0.22) !important; color: var(--blue) !important;
}
.medical-course :deep(.tag-level[data-level="advanced"]) {
  background: rgba(167,139,250,0.1) !important; border-color: rgba(167,139,250,0.22) !important; color: var(--purple) !important;
}

/* ══════════════════════ 子组件 deep 覆盖 ══════════════════════ */
.medical-course :deep(.floating-ai-container) { display: none !important; }

.medical-course :deep(.content-section) {
  border-radius: var(--r-lg) !important; border: 1px solid var(--border) !important;
  background: var(--surface-2) !important; padding: 20px !important; margin-bottom: 14px;
}
.medical-course :deep(.section-title) { color: var(--text-1) !important; font-size: 15px !important; }
.medical-course :deep(.section-desc), .medical-course :deep(.scenario-desc),
.medical-course :deep(.risk-desc), .medical-course :deep(.video-desc) { color: var(--text-2) !important; }

.medical-course :deep(.scenario-card), .medical-course :deep(.risk-card),
.medical-course :deep(.checklist-item label), .medical-course :deep(.quiz-item) {
  border-radius: var(--r-md) !important; background: var(--surface-3) !important; border: 1px solid var(--border) !important;
}
.medical-course :deep(.scenario-card:hover), .medical-course :deep(.risk-card:hover) {
  border-color: var(--border-accent) !important;
}
.medical-course :deep(.scenario-title), .medical-course :deep(.risk-title),
.medical-course :deep(.quiz-question) { color: var(--text-1) !important; }

.medical-course :deep(.btn-primary), .medical-course :deep(.complete-btn) {
  background: linear-gradient(135deg, #00c9ab, #3d9fff) !important;
  color: #07101f !important; border: none !important; font-weight: 700 !important;
  box-shadow: 0 0 22px rgba(0,212,180,0.22) !important;
}
.medical-course.medical-theme :deep(.btn-primary),
.medical-course.medical-theme :deep(.complete-btn) {
  background: linear-gradient(135deg, var(--blue), var(--cyan)) !important;
  color: #ffffff !important;
  box-shadow: 0 14px 30px rgba(20,55,97,0.14) !important;
}
.medical-course :deep(.btn-primary:hover), .medical-course :deep(.complete-btn:hover) {
  box-shadow: 0 0 34px rgba(0,212,180,0.38) !important; transform: translateY(-1px) !important;
}
.medical-course.medical-theme :deep(.btn-primary:hover),
.medical-course.medical-theme :deep(.complete-btn:hover) {
  box-shadow: 0 16px 34px rgba(20,55,97,0.2) !important;
}
.medical-course :deep(.quiz-option) {
  background: var(--surface) !important; border: 1px solid var(--border) !important; color: var(--text-2) !important;
}
.medical-course :deep(.quiz-option:hover) { border-color: var(--border-accent) !important; }
.medical-course :deep(.quiz-option.correct) { background: rgba(16,185,129,0.12) !important; border-color: rgba(16,185,129,0.3) !important; }
.medical-course :deep(.quiz-option.wrong) { background: rgba(248,113,113,0.1) !important; border-color: rgba(248,113,113,0.28) !important; }

.medical-course :deep(.notes-textarea), .medical-course :deep(.notes-area textarea) {
  background: var(--surface) !important; border-color: var(--border) !important; color: var(--text-1) !important; border-radius: var(--r-md) !important;
}
.medical-course :deep(.video-placeholder-large) {
  border-radius: var(--r-md) !important; border-color: var(--border-2) !important;
}
.medical-course :deep(.video-placeholder-large:hover) { border-color: var(--border-accent) !important; }

/* ══════════════════════ 动画 ══════════════════════ */
@keyframes orbitSpin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* ══════════════════════ 响应式 ══════════════════════ */
@media (max-width: 1200px) {
  .course-hero, .detail-hero { grid-template-columns: 1fr; }
  .detail-layout { grid-template-columns: 256px 1fr; }
  .journey-grid { grid-template-columns: 1fr; }
  .hero-stats { grid-template-columns: repeat(2,1fr); }
}
@media (max-width: 860px) {
  .detail-layout { grid-template-columns: 1fr; }
  .detail-sidebar { position: static; }
}
@media (max-width: 640px) {
  .course-shell { padding: 14px 14px 80px; }
  .course-hero, .catalog-panel, .learning-journey, .journey-notes { padding: 18px 16px; }
  .hero-stats { grid-template-columns: repeat(2,1fr); }
  .detail-meta { grid-template-columns: 1fr; }
  .completion-actions, .hero-actions { flex-direction: column; align-items: stretch; }
}
@media (prefers-reduced-motion: reduce) {
  .orbit-ring, .course-symbol-orbit, .detail-symbol-orbit, .bg-orb,
  .eyebrow-dot, .badge-pulse, .btn-shimmer { animation: none !important; }
}
</style>
