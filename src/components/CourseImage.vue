<template>
  <div class="course-image">

    <!-- 安全提示 -->
    <div class="safety-strip">
      <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M12 3 5 6v6c0 5 3.4 8.2 7 9 3.6-.8 7-4 7-9V6l-7-3Z"/><path d="m9.5 12 1.8 1.8 3.7-4.2"/>
      </svg>
      本平台所有内容均为健康科普，不构成医疗建议
    </div>

    <!-- 视频模态框 -->
    <VideoModalPlayer
      v-model="showVideoModal"
      src="https://player.bilibili.com/player.html?isOutside=true&aid=115445466141213&bvid=BV169s1zgEVM&cid=33438895108&p=1&autoplay=1"
    />

    <div class="course-layout">

      <!-- ════════ 主内容 ════════ -->
      <div class="main-content">

        <!-- 视频讲解 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-blue">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polygon points="5 3 19 12 5 21 5 3"/></svg>
            </div>
            <div>
              <h2 class="sec-title">什么是医学影像深度学习？</h2>
              <p class="sec-sub">理解 AI 如何辅助放射科医生提升诊断效率</p>
            </div>
          </div>
          <div class="video-player" @click="openVideoModal">
            <div class="video-thumb">
              <img src="@/views/element/image/11.png" class="video-bg" alt="医学影像深度学习">
              <div class="video-overlay">
                <div class="play-rings"><div class="pr pr-1"></div><div class="pr pr-2"></div></div>
                <div class="play-btn">
                  <svg viewBox="0 0 24 24" fill="none" class="play-svg"><path d="M8 5v14l11-7z" fill="currentColor"/></svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 比喻讲解 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-violet">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            </div>
            <div>
              <h2 class="sec-title">一句话理解 AI 读片</h2>
              <p class="sec-sub">用类比拆解技术原理</p>
            </div>
          </div>
          <div class="metaphor-card">
            <div class="metaphor-q">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="q-mark">
                <path d="M3 21c3 0 7-1 7-8V5c0-1.25-.756-2.017-2-2H4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2 1 0 1 0 1 1v1c0 1-1 2-2 2s-1 .008-1 1.031V20c0 1 0 1 1 1z"/>
                <path d="M15 21c3 0 7-1 7-8V5c0-1.25-.757-2.017-2-2h-4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2h.75c0 2.25.25 4-2.75 4v3c0 1 0 1 1 1z"/>
              </svg>
              <p class="metaphor-text">AI 看片，就像同时对比 10 万张影像来寻找异常——帮助医生更快定位可疑区域。</p>
            </div>
            <p class="metaphor-desc">AI 通过深度学习数百万张标注影像，学会识别各类病变特征。但最终诊断结论，仍需医生结合临床信息综合判断。</p>
          </div>
        </div>

        <!-- 示例影像库 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-teal">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
            </div>
            <div>
              <h2 class="sec-title">示例影像库</h2>
              <p class="sec-sub">点击影像卡片，查看 AI 模拟标注区域</p>
            </div>
          </div>
          <div class="gallery-grid">
            <div class="gallery-card" v-for="(img, index) in sampleImages" :key="index" @click="showAnnotation(index)">
              <div class="gallery-preview" :style="{ background: img.color }">
                <div class="gallery-glyph" :class="img.gc">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" v-html="img.svgPath"></svg>
                </div>
              </div>
              <div class="gallery-meta">
                <div class="gallery-name">{{ img.name }}</div>
                <div class="gallery-type">{{ img.type }}</div>
              </div>
              <div class="gallery-cta">
                <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                查看 AI 标注
              </div>
            </div>
          </div>
        </div>

        <!-- 真实案例 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-green">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            </div>
            <div>
              <h2 class="sec-title">真实案例解析</h2>
              <p class="sec-sub">了解 AI 在实际临床中如何辅助诊断</p>
            </div>
          </div>
          <div class="case-list">
            <div class="case-card" v-for="(c, i) in cases" :key="i">
              <div class="case-hd">
                <div class="case-glyph" :class="c.gc">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="c.svgPath"></svg>
                </div>
                <span class="case-title">{{ c.title }}</span>
                <span class="case-tag">{{ c.tag }}</span>
              </div>
              <p class="case-desc">{{ c.desc }}</p>
              <div class="doc-comment">
                <div class="doc-avatar">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </div>
                <div class="doc-body">
                  <div class="doc-label">医生点评</div>
                  <p>{{ c.comment }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 报告术语解读 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-amber">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
            </div>
            <div>
              <h2 class="sec-title">报告术语解读</h2>
              <p class="sec-sub">看懂 AI 影像报告中的常见专业词汇</p>
            </div>
          </div>
          <div class="terms-grid">
            <div class="term-card" v-for="term in terms" :key="term.title">
              <div class="term-icon">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="term.svgPath"></svg>
              </div>
              <div>
                <div class="term-title">{{ term.title }}</div>
                <div class="term-desc">{{ term.desc }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 就医提问清单 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-teal">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            </div>
            <div>
              <h2 class="sec-title">就医提问清单</h2>
              <p class="sec-sub">勾选您想问的问题，一键导出就诊备忘</p>
            </div>
          </div>
          <div class="question-list">
            <label class="q-item" v-for="(q, i) in questions" :key="i" :class="{ checked: q.checked }">
              <input type="checkbox" v-model="q.checked">
              <div class="q-box">
                <svg v-if="q.checked" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
              </div>
              <span class="q-num">Q{{ i + 1 }}</span>
              <span class="q-text">{{ q.text }}</span>
            </label>
          </div>
          <button class="export-btn" @click="exportQuestions">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
            导出为就诊清单
            <span class="shimmer"></span>
          </button>
        </div>

        <!-- AI 五个边界 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-red">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            </div>
            <div>
              <h2 class="sec-title">AI 读片的五个边界</h2>
              <p class="sec-sub">了解 AI 的局限，正确使用辅助工具</p>
            </div>
          </div>
          <div class="risk-grid">
            <div class="risk-card" v-for="(risk, i) in risks" :key="i">
              <div class="risk-num">{{ String(i + 1).padStart(2, '0') }}</div>
              <div class="risk-icon">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="risk.svgPath"></svg>
              </div>
              <div class="risk-title">{{ risk.title }}</div>
              <div class="risk-desc">{{ risk.desc }}</div>
            </div>
          </div>
        </div>

        <!-- 课后测验 -->
        <div class="content-section">
          <div class="sec-hd">
            <div class="sec-icon c-violet">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
            </div>
            <div>
              <h2 class="sec-title">课后测验</h2>
              <p class="sec-sub">巩固核心知识，验证学习成果</p>
            </div>
          </div>
          <div class="quiz-section">
            <div class="quiz-item" v-for="(quiz, i) in quizzes" :key="i">
              <div class="quiz-qrow">
                <div class="quiz-num">Q{{ i + 1 }}</div>
                <div class="quiz-question">{{ quiz.question }}</div>
              </div>
              <div class="quiz-options">
                <div
                  class="quiz-opt"
                  v-for="(opt, oi) in quiz.options" :key="oi"
                  :class="{
                    selected: quiz.selected === oi,
                    correct: quiz.showResult && oi === quiz.correct,
                    wrong: quiz.showResult && quiz.selected === oi && oi !== quiz.correct
                  }"
                  @click="selectOption(i, oi)"
                >
                  <div class="opt-letter">{{ String.fromCharCode(65 + oi) }}</div>
                  <span class="opt-text">{{ opt }}</span>
                  <span class="opt-icon" v-if="quiz.showResult && oi === quiz.correct">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                  </span>
                  <span class="opt-icon opt-wrong" v-if="quiz.showResult && quiz.selected === oi && oi !== quiz.correct">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                  </span>
                </div>
              </div>
            </div>
            <div class="quiz-actions">
              <button class="mini-btn mini-primary" @click="submitQuiz">提交答案</button>
              <button class="mini-btn mini-secondary" @click="resetQuiz">重新作答</button>
            </div>
            <Transition name="fsld">
              <div class="score-panel" v-if="showScore">
                <div class="score-badge" :class="{ perfect: score === quizzes.length }">
                  {{ score }} / {{ quizzes.length }}
                </div>
                <div class="score-msg" v-if="score >= 2">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/></svg>
                  已掌握 AI 影像辅助诊断核心知识
                </div>
              </div>
            </Transition>
          </div>
        </div>

        <div class="complete-wrap">
          <button class="complete-btn" @click="completeCourse">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
            完成本章节学习
            <span class="shimmer"></span>
          </button>
        </div>

      </div>

      <!-- ════════ 侧边栏 ════════ -->
      <aside class="sidebar">

        <!-- 学习笔记 -->
        <div class="sb-card">
          <div class="sb-hd">
            <div class="sb-icon sb-blue">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
            </div>
            <h3 class="sb-title">学习笔记</h3>
          </div>
          <textarea class="notes-ta" v-model="notes" placeholder="记录您的学习疑问，可导出为就诊前备忘录..."></textarea>
          <button class="mini-btn mini-secondary sb-full">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
            导出笔记
          </button>
        </div>

        <!-- 学习进度 -->
        <div class="sb-card">
          <div class="sb-hd">
            <div class="sb-icon sb-teal">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/></svg>
            </div>
            <h3 class="sb-title">学习进度</h3>
            <span class="prog-pct">{{ progress }}%</span>
          </div>
          <div class="prog-track">
            <div class="prog-fill" :style="{ width: progress + '%' }"></div>
          </div>
          <div class="prog-labels">
            <span>{{ progress >= 100 ? '已完成' : progress >= 50 ? '进行中' : '学习中' }}</span>
            <span>{{ progress }}% 完成</span>
          </div>
        </div>

        <!-- 核心要点 -->
        <div class="sb-card">
          <div class="sb-hd">
            <div class="sb-icon sb-amber">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            </div>
            <h3 class="sb-title">核心要点</h3>
          </div>
          <div class="kp-list">
            <div class="kp-item" v-for="(kp, i) in keypoints" :key="i">
              <div class="kp-idx">{{ i + 1 }}</div>
              <span>{{ kp }}</span>
            </div>
          </div>
        </div>

      </aside>
    </div>
  </div>
</template>

<script>
import VideoModalPlayer from './VideoModalPlayer.vue'

export default {
  name: 'CourseImage',
  components: { VideoModalPlayer },
  props: ['course'],
  emits: ['complete'],
  data() {
    return {
      showVideoModal: false,
      notes: '',
      progress: 40,
      showScore: false,
      score: 0,
      sampleImages: [
        { name:'胸部 X 光', type:'脱敏胸片', gc:'gg-teal',   color:'linear-gradient(135deg,rgba(16,185,129,0.15),rgba(74,158,255,0.15))',   svgPath:'<path d="M12 5v6"/><path d="M12 11c-1.6-3-4-4-5.8-3.6C4 8 3 10.2 3 12.8c0 3.3 1.9 6 5 6 2.2 0 3.4-1.5 4-3.6"/><path d="M12 11c1.6-3 4-4 5.8-3.6C20 8 21 10.2 21 12.8c0 3.3-1.9 6-5 6-2.2 0-3.4-1.5-4-3.6"/>' },
        { name:'肺部 CT',  type:'胸部 CT',   gc:'gg-blue',   color:'linear-gradient(135deg,rgba(74,158,255,0.15),rgba(0,212,180,0.15))',     svgPath:'<rect x="4" y="5" width="16" height="14" rx="4"/><circle cx="10" cy="11" r="2.4"/><path d="m13.5 14 2-2 2.5 3"/><path d="M8 16.5 11 13l2 2.5"/>' },
        { name:'骨骼扫描', type:'骨骼影像',  gc:'gg-amber',  color:'linear-gradient(135deg,rgba(245,158,11,0.15),rgba(248,113,113,0.15))',   svgPath:'<path d="M6.2 9.2a2.2 2.2 0 1 1 2.9-3.3l5.8 5.1a2.6 2.6 0 0 1 .4 3.6l-1.3 1.6a2.6 2.6 0 0 1-3.6.4L4.6 11.5a2.2 2.2 0 1 1 1.6-2.3Z"/><path d="M17.8 14.8a2.2 2.2 0 1 1-2.9 3.3l-5.8-5.1a2.6 2.6 0 0 1-.4-3.6L10 7.8a2.6 2.6 0 0 1 3.6-.4l5.8 5.1a2.2 2.2 0 1 1-1.6 2.3Z"/>' },
        { name:'脑部 MRI', type:'头颅影像',  gc:'gg-violet', color:'linear-gradient(135deg,rgba(167,139,250,0.15),rgba(74,158,255,0.15))',   svgPath:'<path d="M9.5 5.5a3 3 0 0 1 5.4 1.8 3.3 3.3 0 0 1 2.7 3.2 3.1 3.1 0 0 1-.6 1.8 3 3 0 0 1 .8 2 3.2 3.2 0 0 1-3.2 3.2H9.2A3.2 3.2 0 0 1 6 14.3c0-.8.3-1.5.8-2A3.1 3.1 0 0 1 6.2 10a3.3 3.3 0 0 1 2.7-3.2 3 3 0 0 1 .6-1.3Z"/>' },
        { name:'心脏超声', type:'心脏影像',  gc:'gg-rose',   color:'linear-gradient(135deg,rgba(248,113,113,0.15),rgba(245,158,11,0.15))',   svgPath:'<path d="M12 20s-7-4.3-8.5-8.3A5 5 0 0 1 12 6a5 5 0 0 1 8.5 5.7C19 15.7 12 20 12 20Z"/><path d="M12 8.5v7M9.5 12h5"/>' }
      ],
      cases: [
        { title:'肺结节筛查', tag:'辅助检出', gc:'gg-teal',   svgPath:'<circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>',                                                                                                                                                                                    desc:'65 岁男性，胸部 CT 发现 4mm 小结节，AI 辅助检测标记后，3 个月复查无明显变化。', comment:'AI 帮助发现了这个小结节，但需要动态观察，不要过度紧张。结节大小和变化趋势才是关键。' },
        { title:'骨折漏诊辅助', tag:'精度提升', gc:'gg-amber', svgPath:'<path d="M6.2 9.2a2.2 2.2 0 1 1 2.9-3.3l5.8 5.1a2.6 2.6 0 0 1 .4 3.6l-1.3 1.6a2.6 2.6 0 0 1-3.6.4L4.6 11.5a2.2 2.2 0 1 1 1.6-2.3Z"/>',                                                                                                               desc:'手腕 X 光片，AI 提示可疑线性骨折，经 CT 证实，避免了漏诊风险。',                    comment:'AI 可以帮助发现肉眼容易遗漏的细微骨折，但确认诊断仍需 CT 或体格检查。' },
        { title:'脑出血快速预警', tag:'急性处置', gc:'gg-violet', svgPath:'<path d="M9.5 5.5a3 3 0 0 1 5.4 1.8 3.3 3.3 0 0 1 2.7 3.2 3.1 3.1 0 0 1-.6 1.8 3 3 0 0 1 .8 2 3.2 3.2 0 0 1-3.2 3.2H9.2A3.2 3.2 0 0 1 6 14.3c0-.8.3-1.5.8-2A3.1 3.1 0 0 1 6.2 10a3.3 3.3 0 0 1 2.7-3.2 3 3 0 0 1 .6-1.3Z"/>',  desc:'高血压患者突发头痛，CT 显示少量出血，AI 快速标注后医生立即启动处置流程。',          comment:'时间就是大脑。AI 辅助可以将影像评估时间压缩到分钟级，对急性脑血管病意义重大。' }
      ],
      terms: [
        { title:'"结节概率 0.85"是什么意思？', desc:'表示 AI 认为有 85% 的可能性该区域存在结节，概率越高越需要关注，但不能替代医生判断。', svgPath:'<path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/>' },
        { title:'AI 标注的"可疑区域"', desc:'AI 发现需要关注的区域，通常高亮标注。这些区域可能是病变，也可能是正常变异，需医生综合判断。', svgPath:'<circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>' },
        { title:'"低风险"/"高风险"如何理解？', desc:'AI 对病灶恶性可能性的评估，仅供参考。高风险不代表癌症，低风险也不代表完全安全。', svgPath:'<path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/>' },
        { title:'"建议随访"意味着什么？', desc:'AI 认为病变暂时无需立即干预，但需定期复查观察变化趋势，是最常见的处置建议之一。', svgPath:'<rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/>' }
      ],
      questions: [
        { text:'医生，AI 圈出的这个区域需要进一步检查吗？', checked: false },
        { text:'这个 AI 分析结果的可靠性有多高？', checked: false },
        { text:'建议随访的话，需要多久复查一次？', checked: false },
        { text:'除了 AI，还有其他更精确的检查方法吗？', checked: false },
        { text:'这个治疗方案中有多少是基于 AI 判断的？', checked: false }
      ],
      risks: [
        { title:'无法识别罕见病', desc:'AI 主要从常见病例中学习，对罕见或非典型病变的识别能力有限。', svgPath:'<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>' },
        { title:'无法替代病理活检', desc:'病理检查是诊断肿瘤性质的金标准，AI 影像无法取代组织学分析。', svgPath:'<path d="M9 3H5a2 2 0 0 0-2 2v4m6-6h10a2 2 0 0 1 2 2v4M9 3v18m0 0h10a2 2 0 0 0 2-2v-4M9 21H5a2 2 0 0 1-2-2v-4m0 0h18"/>' },
        { title:'不读取临床病史', desc:'AI 只分析影像像素，不了解患者的症状、病史和用药情况。', svgPath:'<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>' },
        { title:'不能制定治疗方案', desc:'治疗需要医生综合影像、化验、症状等多维度信息做出整体判断。', svgPath:'<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>' },
        { title:'存在漏诊可能', desc:'没有任何 AI 系统能保证 100% 准确率，医生把关是最后一道安全防线。', svgPath:'<polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>' }
      ],
      quizzes: [
        { question:'AI 标注的"可疑区域"意味着什么？', options:['一定有病变','需要医生进一步判断','完全不用担心','必须立即手术'], correct:1, selected:null, showResult:false },
        { question:'影像报告中"结节概率 0.85"表示？', options:['85% 是癌症','AI 有 85% 的把握认为是结节','不需要关注','需要立即手术'], correct:1, selected:null, showResult:false },
        { question:'关于 AI 读片，以下哪项正确？', options:['AI 可以替代医生','AI 完全不会出错','AI 辅助医生，最终诊断靠医生','只看 AI 结果就够了'], correct:2, selected:null, showResult:false }
      ],
      keypoints: ['AI 辅助检出，医生最终决策','定期随访是监测病变的关键','理解报告术语，准备好问题','高风险 ≠ 癌症，保持理性','AI 无法读取临床病史']
    }
  },
  methods: {
    openVideoModal() { this.showVideoModal = true },
    closeVideoModal() { this.showVideoModal = false },
    showAnnotation(index) {
      const img = this.sampleImages[index];
      alert(`查看 ${img.name} 的 AI 模拟标注区域：\n\n发现 2 个可疑区域\n· 区域 1：概率 0.72（低风险）\n· 区域 2：概率 0.88（建议关注）\n\n以上为演示数据，实际结果请咨询医生`);
    },
    selectOption(qi, oi) { this.quizzes[qi].selected = oi; },
    submitQuiz() {
      let s = 0;
      this.quizzes.forEach(q => { q.showResult = true; if (q.selected === q.correct) s++; });
      this.score = s; this.showScore = true;
      if (s >= 2) this.progress = Math.min(100, this.progress + 20);
    },
    resetQuiz() {
      this.quizzes.forEach(q => { q.selected = null; q.showResult = false; });
      this.showScore = false; this.score = 0;
    },
    exportQuestions() {
      const checked = this.questions.filter(q => q.checked);
      if (!checked.length) { alert('请先勾选您想问的问题！'); return; }
      alert('已为您生成就诊清单：\n\n' + checked.map((q,i) => `${i+1}. ${q.text}`).join('\n') + '\n\n（演示功能）');
    },
    completeCourse() { this.progress = 100; this.$emit('complete'); }
  }
}
</script>

<style scoped>
/* ═══════════ 令牌 ═══════════ */
.course-image {
  --bg:rgba(255,255,255,0.03); --bg2:rgba(255,255,255,0.055); --bg3:rgba(255,255,255,0.09);
  --bd:rgba(255,255,255,0.07); --bd2:rgba(255,255,255,0.11);
  --cyan:#00d4b4; --cyan-d:rgba(0,212,180,0.09);
  --blue:#4a9eff; --blue-d:rgba(74,158,255,0.09);
  --violet:#a78bfa; --violet-d:rgba(167,139,250,0.09);
  --amber:#f59e0b; --amber-d:rgba(245,158,11,0.09);
  --green:#10b981; --green-d:rgba(16,185,129,0.09);
  --red:#f87171;   --red-d:rgba(248,113,113,0.09);
  --t1:#eef4ff; --t2:#8fa3c0; --t3:#4a5f7a;
  --r:12px; --rl:20px; --rx:24px;
  font-family:'SF Pro Display','PingFang SC','HarmonyOS Sans SC',system-ui,sans-serif;
}

/* Safety strip */
.safety-strip {
  display:flex; align-items:center; gap:7px;
  padding:9px 14px; border-radius:var(--r);
  border:1px solid rgba(245,158,11,0.18); background:rgba(245,158,11,0.05);
  color:rgba(245,158,11,0.75); font-size:12px; font-weight:500; margin-bottom:14px;
}
.safety-strip svg { flex-shrink:0; color:var(--amber); }

/* Modal */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s ease;
}
.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}
.video-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 15000;
  isolation: isolate;
}
.video-modal-content {
  position: relative;
  width: 90%;
  max-width: 900px;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
}
.video-close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  border: none;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  z-index: 10;
}
.video-iframe-container {
  width: 100%;
  aspect-ratio: 16/9;
}
.video-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* Layout */
.course-layout { display:grid; grid-template-columns:1fr 296px; gap:16px; align-items:start; }
.main-content { display:flex; flex-direction:column; gap:14px; }

/* Section */
.content-section {
  padding:18px 20px; border-radius:var(--rx);
  border:1px solid var(--bd); background:var(--bg2);
  position:relative; overflow:hidden;
}
.content-section::before {
  content:''; position:absolute; top:0; left:0; right:0; height:1px;
  background:linear-gradient(90deg,transparent,rgba(0,212,180,0.18),transparent);
}
.sec-hd { display:flex; align-items:flex-start; gap:11px; margin-bottom:16px; }
.sec-icon {
  width:32px; height:32px; border-radius:9px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
}
.c-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.c-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.c-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.c-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.c-green  { background:var(--green-d);  border:1px solid rgba(16,185,129,0.2);  color:var(--green); }
.c-red    { background:var(--red-d);    border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.sec-title { font-size:14px; font-weight:600; color:var(--t1); margin-bottom:2px; }
.sec-sub   { font-size:11px; color:var(--t3); }

/* Video player */
.video-player { cursor:pointer; }
.video-thumb {
  position:relative; aspect-ratio:16/9; border-radius:var(--rl);
  overflow:hidden; border:1px solid var(--bd2); transition:all 0.3s ease;
}
.video-thumb:hover { border-color:rgba(0,212,180,0.3); transform:translateY(-2px); }
.video-bg { width:100%; height:100%; object-fit:cover; display:block; }
.video-overlay {
  position:absolute; inset:0; background:rgba(4,8,20,0.5);
  display:flex; align-items:center; justify-content:center; transition:background 0.3s ease;
}
.video-thumb:hover .video-overlay { background:rgba(4,8,20,0.3); }
.play-rings { position:absolute; }
.pr { position:absolute; border-radius:50%; border:1px solid rgba(0,212,180,0.2); animation:rp 2s ease-out infinite; }
.pr-1 { width:80px; height:80px; top:-40px; left:-40px; }
.pr-2 { width:108px; height:108px; top:-54px; left:-54px; animation-delay:.55s; }
@keyframes rp { 0%{opacity:.5;transform:scale(.7)} 100%{opacity:0;transform:scale(1.2)} }
.play-btn {
  width:56px; height:56px; border-radius:50%;
  background:rgba(255,255,255,0.92); backdrop-filter:blur(6px);
  display:flex; align-items:center; justify-content:center;
  position:relative; z-index:1; transition:all 0.3s ease;
  box-shadow:0 6px 22px rgba(0,0,0,0.36);
}
.play-svg { width:22px; height:22px; color:#0d1526; margin-left:3px; transition:color 0.3s; }
.video-thumb:hover .play-btn { transform:scale(1.1); }
.video-thumb:hover .play-svg { color:#00a1d6; }

/* Metaphor */
.metaphor-card {
  padding:20px; border-radius:var(--rl);
  border:1px solid rgba(74,158,255,0.18); background:rgba(74,158,255,0.05);
}
.metaphor-q { display:flex; align-items:flex-start; gap:12px; margin-bottom:12px; }
.q-mark { flex-shrink:0; color:var(--blue); opacity:.6; margin-top:3px; }
.metaphor-text { font-size:14px; font-weight:600; color:#93c5fd; line-height:1.75; font-style:italic; }
.metaphor-desc { font-size:12px; color:var(--t2); line-height:1.75; padding-left:30px; }

/* Gallery */
.gallery-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(148px,1fr)); gap:12px; }
.gallery-card {
  border-radius:var(--r); border:1px solid var(--bd); background:var(--bg);
  overflow:hidden; cursor:pointer; transition:all 0.25s ease;
}
.gallery-card:hover { border-color:rgba(0,212,180,0.25); transform:translateY(-3px); box-shadow:0 10px 28px rgba(0,0,0,0.4); }
.gallery-preview { height:96px; display:flex; align-items:center; justify-content:center; }
.gallery-glyph { width:46px; height:46px; border-radius:13px; display:flex; align-items:center; justify-content:center; }
.gg-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.gg-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.gg-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.gg-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.gg-rose   { background:var(--red-d);    border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.gallery-meta { padding:9px 10px 5px; }
.gallery-name { font-size:12px; font-weight:600; color:var(--t1); margin-bottom:5px; }
.gallery-type {
  display:inline-block; height:17px; padding:0 7px; border-radius:999px; line-height:17px;
  background:var(--blue-d); border:1px solid rgba(74,158,255,0.18);
  font-size:9px; font-weight:600; color:var(--blue);
}
.gallery-cta {
  display:flex; align-items:center; gap:4px; padding:6px 10px;
  border-top:1px solid var(--bd); font-size:10px; color:var(--cyan); font-weight:500;
}

/* Cases */
.case-list { display:flex; flex-direction:column; gap:12px; }
.case-card {
  padding:15px; border-radius:var(--rl);
  border:1px solid var(--bd); background:var(--bg); transition:border-color 0.2s;
}
.case-card:hover { border-color:var(--bd2); }
.case-hd { display:flex; align-items:center; gap:9px; margin-bottom:9px; }
.case-glyph { width:29px; height:29px; border-radius:8px; flex-shrink:0; display:flex; align-items:center; justify-content:center; }
.case-title { font-size:13px; font-weight:600; color:var(--t1); flex:1; }
.case-tag {
  height:19px; padding:0 7px; border-radius:999px; flex-shrink:0;
  border:1px solid var(--bd2); background:var(--bg3);
  font-size:9px; font-weight:600; color:var(--t3); display:flex; align-items:center;
}
.case-desc { font-size:12px; color:var(--t2); line-height:1.7; margin-bottom:10px; }
.doc-comment {
  display:flex; align-items:flex-start; gap:9px; padding:10px 11px;
  border-radius:var(--r); background:rgba(16,185,129,0.06);
  border:1px solid rgba(16,185,129,0.15);
}
.doc-avatar {
  width:26px; height:26px; border-radius:50%; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
  background:rgba(16,185,129,0.12); border:1px solid rgba(16,185,129,0.2); color:var(--green);
}
.doc-body { flex:1; }
.doc-label { font-size:10px; font-weight:700; color:var(--green); margin-bottom:3px; letter-spacing:.06em; }
.doc-body p { font-size:11px; color:var(--t2); line-height:1.65; margin:0; }

/* Terms */
.terms-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(235px,1fr)); gap:10px; }
.term-card {
  display:flex; align-items:flex-start; gap:10px; padding:12px 13px;
  border-radius:var(--r); border:1px solid var(--bd); background:var(--bg); transition:border-color 0.2s;
}
.term-card:hover { border-color:rgba(245,158,11,0.2); }
.term-icon {
  width:27px; height:27px; border-radius:8px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
  background:var(--amber-d); border:1px solid rgba(245,158,11,0.2); color:var(--amber);
}
.term-title { font-size:12px; font-weight:600; color:var(--t1); margin-bottom:4px; line-height:1.4; }
.term-desc  { font-size:11px; color:var(--t2); line-height:1.65; }

/* Question list */
.question-list { display:flex; flex-direction:column; gap:7px; margin-bottom:11px; }
.q-item {
  display:flex; align-items:flex-start; gap:8px; padding:9px 11px;
  border-radius:var(--r); border:1px solid var(--bd); background:var(--bg);
  cursor:pointer; transition:all 0.2s ease; font-size:12px; color:var(--t2);
}
.q-item input { display:none; }
.q-item.checked { border-color:rgba(0,212,180,0.22); background:var(--cyan-d); color:var(--t1); }
.q-box {
  width:15px; height:15px; border-radius:4px; flex-shrink:0;
  border:1px solid var(--bd2); display:flex; align-items:center; justify-content:center;
  margin-top:1px; transition:all 0.2s;
}
.q-item.checked .q-box { background:var(--cyan); border-color:var(--cyan); color:#07101f; }
.q-num { font-weight:700; color:var(--cyan); flex-shrink:0; }
.q-text { flex:1; line-height:1.55; }
.export-btn {
  display:flex; align-items:center; justify-content:center; gap:6px;
  width:100%; height:37px; border-radius:999px;
  border:1px solid rgba(0,212,180,0.22); background:var(--cyan-d);
  color:var(--cyan); font-size:12px; font-weight:600; cursor:pointer;
  transition:all 0.22s ease; position:relative; overflow:hidden;
}
.export-btn:hover { background:var(--cyan); color:#07101f; border-color:transparent; }

/* Risk grid */
.risk-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(175px,1fr)); gap:10px; }
.risk-card {
  padding:13px 13px 15px; border-radius:var(--r);
  border:1px solid rgba(248,113,113,0.14); background:rgba(248,113,113,0.04);
  position:relative; transition:border-color 0.22s;
}
.risk-card:hover { border-color:rgba(248,113,113,0.24); }
.risk-num {
  position:absolute; top:10px; right:11px;
  font-size:20px; font-weight:800; color:rgba(248,113,113,0.1); line-height:1;
}
.risk-icon {
  width:30px; height:30px; border-radius:8px; margin-bottom:9px;
  display:flex; align-items:center; justify-content:center;
  background:var(--red-d); border:1px solid rgba(248,113,113,0.2); color:var(--red);
}
.risk-title { font-size:12px; font-weight:600; color:var(--t1); margin-bottom:4px; }
.risk-desc  { font-size:11px; color:var(--t2); line-height:1.6; }

/* Quiz */
.quiz-section { display:flex; flex-direction:column; gap:12px; }
.quiz-item { padding:13px; border-radius:var(--r); border:1px solid var(--bd); background:var(--bg); }
.quiz-qrow { display:flex; align-items:flex-start; gap:7px; margin-bottom:9px; }
.quiz-num {
  height:21px; min-width:28px; border-radius:6px; padding:0 6px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center; margin-top:1px;
  background:var(--violet-d); border:1px solid rgba(167,139,250,0.2);
  font-size:9px; font-weight:700; color:var(--violet);
}
.quiz-question { font-size:13px; font-weight:600; color:var(--t1); line-height:1.55; }
.quiz-options { display:flex; flex-direction:column; gap:6px; }
.quiz-opt {
  display:flex; align-items:center; gap:7px; padding:7px 9px;
  border-radius:var(--r); cursor:pointer; transition:all 0.2s ease;
  border:1px solid transparent; background:rgba(255,255,255,0.02);
}
.quiz-opt:hover { background:var(--bg3); border-color:var(--bd); }
.quiz-opt.selected { border-color:rgba(0,212,180,0.22); background:var(--cyan-d); }
.quiz-opt.correct  { border-color:rgba(16,185,129,0.25); background:rgba(16,185,129,0.07); }
.quiz-opt.wrong    { border-color:rgba(248,113,113,0.22); background:rgba(248,113,113,0.06); }
.opt-letter {
  width:19px; height:19px; border-radius:5px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
  font-size:10px; font-weight:700; background:var(--bg3); color:var(--t3);
}
.quiz-opt.correct .opt-letter  { background:rgba(16,185,129,0.14); color:var(--green); }
.quiz-opt.wrong   .opt-letter  { background:rgba(248,113,113,0.12); color:var(--red); }
.opt-text { flex:1; font-size:12px; color:var(--t2); }
.opt-icon { margin-left:auto; color:var(--green); display:flex; align-items:center; }
.opt-wrong { color:var(--red); }

.quiz-actions { display:flex; gap:8px; justify-content:center; }
.mini-btn {
  display:inline-flex; align-items:center; justify-content:center; gap:5px;
  height:34px; padding:0 16px; border-radius:999px;
  font-size:12px; font-weight:600; cursor:pointer; transition:all 0.2s ease;
}
.mini-primary {
  background:linear-gradient(135deg,#00c9ab,#3d9fff); border:none;
  color:#07101f; box-shadow:0 0 14px rgba(0,212,180,0.2);
}
.mini-primary:hover { box-shadow:0 0 24px rgba(0,212,180,0.35); }
.mini-secondary {
  background:var(--bg2); border:1px solid var(--bd2); color:var(--t2);
}
.mini-secondary:hover { color:var(--t1); }

.fsld-enter-active { transition:all 0.28s ease; }
.fsld-enter-from { opacity:0; transform:translateY(-6px); }
.score-panel { text-align:center; padding:14px; border-radius:var(--r); border:1px solid var(--bd); background:var(--bg); }
.score-badge {
  display:inline-flex; align-items:center; justify-content:center;
  height:40px; padding:0 22px; border-radius:999px;
  background:linear-gradient(135deg,var(--blue),#6366f1);
  color:#fff; font-size:17px; font-weight:700; margin-bottom:10px;
}
.score-badge.perfect { background:linear-gradient(135deg,var(--cyan),var(--green)); }
.score-msg { display:flex; align-items:center; justify-content:center; gap:6px; font-size:12px; font-weight:600; color:#fbbf24; }

/* Complete */
.complete-wrap { text-align:center; padding-top:4px; }
.complete-btn {
  display:inline-flex; align-items:center; justify-content:center; gap:8px;
  padding:0 32px; height:48px; border-radius:999px; border:none; cursor:pointer;
  font-size:14px; font-weight:700;
  background:linear-gradient(135deg,#00c9ab,#3d9fff);
  color:#07101f; box-shadow:0 0 28px rgba(0,212,180,0.28);
  transition:all 0.3s ease; position:relative; overflow:hidden;
}
.complete-btn:hover { transform:translateY(-2px); box-shadow:0 0 42px rgba(0,212,180,0.44); }
.shimmer {
  position:absolute; top:0; left:-100%; width:55%; height:100%;
  background:linear-gradient(90deg,transparent,rgba(255,255,255,0.28),transparent);
  transform:skewX(-18deg); animation:shimmer 3.5s ease-in-out infinite;
}
@keyframes shimmer { 0%{left:-100%} 50%,100%{left:160%} }

/* Sidebar */
.sidebar { display:flex; flex-direction:column; gap:12px; position:sticky; top:20px; }
.sb-card {
  padding:14px 16px; border-radius:var(--rx);
  border:1px solid var(--bd); background:var(--bg2); position:relative; overflow:hidden;
}
.sb-card::before {
  content:''; position:absolute; top:0; left:0; right:0; height:1px;
  background:linear-gradient(90deg,transparent,rgba(0,212,180,0.18),transparent);
}
.sb-hd { display:flex; align-items:center; gap:8px; margin-bottom:11px; }
.sb-icon {
  width:26px; height:26px; border-radius:7px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
}
.sb-blue  { background:var(--blue-d);  border:1px solid rgba(74,158,255,0.18);  color:var(--blue); }
.sb-teal  { background:var(--cyan-d);  border:1px solid rgba(0,212,180,0.18);   color:var(--cyan); }
.sb-amber { background:var(--amber-d); border:1px solid rgba(245,158,11,0.18);  color:var(--amber); }
.sb-title { font-size:12px; font-weight:600; color:var(--t1); flex:1; }
.prog-pct { font-size:11px; font-weight:700; color:var(--cyan); }

.notes-ta {
  width:100%; min-height:96px; padding:9px; border-radius:var(--r);
  border:1px solid var(--bd); background:var(--bg);
  color:var(--t1); font-size:12px; resize:vertical; font-family:inherit;
  transition:border-color 0.2s; margin-bottom:8px;
}
.notes-ta:focus { outline:none; border-color:rgba(0,212,180,0.28); }
.notes-ta::placeholder { color:var(--t3); }
.sb-full { width:100%; }

.prog-track { width:100%; height:5px; background:var(--bg3); border-radius:999px; overflow:hidden; margin-bottom:7px; }
.prog-fill { height:100%; background:linear-gradient(90deg,var(--cyan),var(--blue)); border-radius:999px; transition:width 0.6s cubic-bezier(.4,0,.2,1); }
.prog-labels { display:flex; justify-content:space-between; font-size:10px; color:var(--t3); }

.kp-list { display:flex; flex-direction:column; gap:6px; }
.kp-item {
  display:flex; align-items:center; gap:8px; padding:7px 9px;
  border-radius:var(--r); border:1px solid var(--bd); background:var(--bg);
  font-size:11px; color:var(--t2);
}
.kp-idx {
  width:17px; height:17px; border-radius:4px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
  background:var(--amber-d); border:1px solid rgba(245,158,11,0.18);
  font-size:9px; font-weight:700; color:var(--amber);
}

/* Responsive */
@media (max-width:1100px) { .course-layout { grid-template-columns:1fr; } .sidebar { position:static; } }
@media (max-width:640px) { .gallery-grid { grid-template-columns:repeat(2,1fr); } .risk-grid { grid-template-columns:1fr; } }
</style>
