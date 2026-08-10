<template>
  <div class="course-health">
    <div class="safety-strip">
      <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3 5 6v6c0 5 3.4 8.2 7 9 3.6-.8 7-4 7-9V6l-7-3Z"/><path d="m9.5 12 1.8 1.8 3.7-4.2"/></svg>
      本平台所有内容均为健康科普，不构成医疗建议。如有不适请及时就医。
    </div>
    <VideoModalPlayer
      v-model="showVideoModal"
      :src="currentVideoUrl"
    />
    <div class="course-layout">
      <div class="main-col">
        <section class="sec-card">
          <div class="sec-hd"><div class="sec-icon ic-red"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polygon points="5 3 19 12 5 21 5 3"/></svg></div><div><h2 class="sec-title">流感预防视频专区</h2><p class="sec-sub">点击播放，学习流感预防核心知识</p></div></div>
          <div class="video-grid">
            <div class="video-card" v-for="(video, index) in fluVideos" :key="index" @click="openVideoModal(index)">
              <div class="video-thumb"><img :src="video.bgImage" class="video-bg" :alt="video.title"><div class="video-overlay"><div class="play-rings"><div class="play-ring pr-1"></div><div class="play-ring pr-2"></div></div><div class="play-btn"><svg viewBox="0 0 24 24" fill="none" class="play-svg"><path d="M8 5v14l11-7z" fill="currentColor"/></svg></div></div></div>
              <div class="video-title-row"><div class="video-play-icon"><svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg></div><span class="video-card-title">{{ video.title }}</span></div>
            </div>
          </div>
          <div class="warn-strip"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>抗病毒药物需医生处方，请勿自行用药</div>
        </section>
        <section class="sec-card">
          <div class="sec-hd"><div class="sec-icon ic-amber"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/></svg></div><div><h2 class="sec-title">季节性疾病预警</h2><p class="sec-sub">按季节了解高发疾病与防护重点</p></div></div>
          <div class="season-grid">
            <div class="season-card" v-for="(season, index) in seasons" :key="index" :class="{ active: index === currentSeason }" @click="selectSeason(index)">
              <div class="season-icon-wrap" :class="season.iconClass"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="season.svgPath"></svg></div>
              <div class="season-name">{{ season.name }}</div><div class="season-warn">{{ season.warnings }}</div>
              <div class="season-active-dot" v-if="index === currentSeason"></div>
            </div>
          </div>
        </section>
        <section class="sec-card">
          <div class="sec-hd"><div class="sec-icon ic-blue"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg></div><div><h2 class="sec-title">预防知识库</h2><p class="sec-sub">慢性病预防与早期识别核心内容</p></div></div>
          <div class="tab-row">
            <button class="tab-btn" :class="{ active: knowledgeTab === 'list' }" @click="knowledgeTab = 'list'"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>知识列表</button>
            <button class="tab-btn" :class="{ active: knowledgeTab === 'gallery' }" @click="knowledgeTab = 'gallery'"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>信息图库</button>
          </div>
          <Transition name="tab-fade" mode="out-in">
            <div v-if="knowledgeTab === 'list'" class="knowledge-list" key="list">
              <div class="knowledge-row" v-for="item in knowledge" :key="item.name"><div class="knowledge-glyph" :class="item.gc"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="item.svgPath"></svg></div><div class="knowledge-body"><div class="knowledge-name">{{ item.name }}</div><div class="knowledge-desc">{{ item.desc }}</div></div><div class="knowledge-tag">{{ item.tag }}</div></div>
            </div>
            <div v-else class="gallery-list" key="gallery">
              <div class="gallery-row" v-for="(img, index) in healthImages" :key="index" @click="viewImage(index)"><div class="gallery-glyph" :class="img.gc"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="img.svgPath"></svg></div><span class="gallery-label">{{ img.title }}</span><svg class="gallery-chevron" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg></div>
            </div>
          </Transition>
        </section>
        <section class="sec-card">
          <div class="sec-hd"><div class="sec-icon ic-teal"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg></div><div><h2 class="sec-title">实用工具与症状自查</h2><p class="sec-sub">AI 辅助功能演示 · 症状初步参考（非诊断）</p></div></div>
          <div class="tools-grid">
            <div class="tool-card"><div class="tool-card-header"><div class="tool-icon ic-teal"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="5" width="14" height="14" rx="4"/><path d="M9 12h6M12 9v6"/><path d="M8 3v3M16 3v3M8 18v3M16 18v3M3 8h3M18 8h3M3 16h3M18 16h3"/></svg></div><div class="tool-title">AI 健康助手</div></div><div class="tool-desc">演示症状自查、用药提醒等 AI 辅助功能，体验智能健康管理流程。</div><button class="act-btn act-btn-outline"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>开始演示</button></div>
            <div class="tool-card">
              <div class="tool-card-header"><div class="tool-icon ic-violet"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg></div><div class="tool-title">症状自查</div></div>
              <div class="tool-warn-row"><svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/></svg>仅提供科普参考，不能替代医生诊断</div>
              <div class="symptom-chips"><div class="symptom-chip" v-for="(symptom, index) in symptoms" :key="index" :class="{ active: selectedSymptoms.includes(index) }" @click="toggleSymptom(index)">{{ symptom }}</div></div>
              <button class="act-btn act-btn-primary" @click="checkSymptoms" :disabled="selectedSymptoms.length === 0"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>获取建议</button>
              <Transition name="fade-up"><div class="result-box" v-if="symptomAdvice"><div class="result-box-hd"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>非诊断性建议</div><p>{{ symptomAdvice }}</p><div class="result-box-warn"><svg width="9" height="9" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/></svg>本工具不代替医生诊断</div></div></Transition>
            </div>
          </div>
        </section>
        <section class="sec-card">
          <div class="sec-hd"><div class="sec-icon ic-violet"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/></svg></div><div><h2 class="sec-title">慢病管理与随访</h2><p class="sec-sub">模拟数据分析 · 智能随访计划生成</p></div></div>
          <div class="tools-grid">
            <div class="tool-card">
              <div class="tool-card-header"><div class="tool-icon ic-blue"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg></div><div class="tool-title">慢病管理模拟</div></div>
              <div class="input-group"><label><svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 3H5a2 2 0 0 0-2 2v4m6-6h10a2 2 0 0 1 2 2v4M9 3v18m0 0h10a2 2 0 0 0 2-2v-4M9 21H5a2 2 0 0 1-2-2v-4m0 0h18"/></svg>模拟血糖值</label><input type="number" v-model="bloodSugar" placeholder="mmol/L"></div>
              <div class="input-group"><label><svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>模拟血压值</label><input type="text" v-model="bloodPressure" placeholder="120/80 mmHg"></div>
              <button class="act-btn act-btn-primary" @click="generateAnalysis"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/></svg>生成趋势分析</button>
              <Transition name="fade-up"><div class="result-box" v-if="analysis"><div class="result-box-hd"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>AI 分析建议</div><p>{{ analysis }}</p></div></Transition>
            </div>
            <div class="tool-card">
              <div class="tool-card-header"><div class="tool-icon ic-green"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/><path d="M9 14h2M13 14h2M9 17h2"/></svg></div><div class="tool-title">智能随访提醒</div></div>
              <div class="input-group"><label><svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>选择疾病类型</label><select v-model="selectedDisease"><option value="">请选择</option><option value="hypertension">高血压</option><option value="diabetes">糖尿病</option><option value="postop">术后康复</option></select></div>
              <button class="act-btn act-btn-primary" @click="generateFollowup" :disabled="!selectedDisease"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/></svg>生成随访计划</button>
              <Transition name="fade-up"><div class="result-box" v-if="followupPlan"><div class="result-box-hd"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>随访建议</div><p>{{ followupPlan }}</p><button class="act-btn act-btn-ghost" style="margin-top:8px"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/></svg>添加到日历</button></div></Transition>
            </div>
          </div>
        </section>
        <section class="sec-card">
          <div class="sec-hd"><div class="sec-icon ic-green"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg></div><div><h2 class="sec-title">课程知识测验</h2><p class="sec-sub">巩固核心知识，验证学习成果</p></div></div>
          <div class="quiz-list">
            <div class="quiz-item" v-for="(quiz, index) in quizzes" :key="index">
              <div class="quiz-q-row"><div class="quiz-badge">Q{{ index + 1 }}</div><div class="quiz-question">{{ quiz.question }}</div></div>
              <div class="quiz-options"><div class="quiz-opt" v-for="(option, optIndex) in quiz.options" :key="optIndex" :class="{ selected: quiz.selected === optIndex, correct: quiz.showResult && optIndex === quiz.correct, wrong: quiz.showResult && quiz.selected === optIndex && optIndex !== quiz.correct }" @click="selectOption(index, optIndex)"><div class="opt-letter">{{ String.fromCharCode(65 + optIndex) }}</div><span class="opt-text">{{ option }}</span><span class="opt-state" v-if="quiz.showResult && optIndex === quiz.correct"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg></span><span class="opt-state opt-wrong-state" v-if="quiz.showResult && quiz.selected === optIndex && optIndex !== quiz.correct"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg></span></div></div>
            </div>
          </div>
          <div class="quiz-footer"><button class="act-btn act-btn-primary" @click="submitQuiz">提交答案</button><button class="act-btn act-btn-ghost" @click="resetQuiz">重新作答</button></div>
          <Transition name="fade-up"><div class="score-panel" v-if="showScore"><div class="score-ring" :class="{ perfect: score === quizzes.length }"><span class="score-num">{{ score }}</span><span class="score-sep">/</span><span class="score-total">{{ quizzes.length }}</span></div><div class="score-label">{{ score === quizzes.length ? '全部答对' : score >= 2 ? '良好' : '继续加油' }}</div><div class="score-award" v-if="score >= 3"><svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/></svg>获得「健康小能手」认证</div></div></Transition>
        </section>
        <div class="complete-row"><button class="complete-btn" @click="completeCourse"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>完成本章节学习<span class="btn-shimmer"></span></button></div>
      </div>
      <aside class="sidebar-col">
        <div class="sb-card">
          <div class="tab-row"><button class="tab-btn" :class="{ active: sidebarTab === 'notes' }" @click="sidebarTab = 'notes'"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>学习笔记</button><button class="tab-btn" :class="{ active: sidebarTab === 'checklist' }" @click="sidebarTab = 'checklist'"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>就医清单</button></div>
          <Transition name="tab-fade" mode="out-in">
            <div v-if="sidebarTab === 'notes'" key="notes" class="notes-area"><textarea v-model="notes" placeholder="记录您的学习疑问，可导出为就诊前备忘录..."></textarea><button class="act-btn act-btn-ghost sb-full"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>导出笔记</button></div>
            <div v-else key="checklist" class="checklist-area"><label class="cl-row" v-for="(item, index) in medicalChecklist" :key="index" :class="{ checked: item.checked }"><input type="checkbox" v-model="item.checked"><div class="cl-box"><svg v-if="item.checked" width="9" height="9" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg></div><span>{{ item.text }}</span></label><button class="act-btn act-btn-ghost sb-full" style="margin-top:4px"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>导出 PDF</button></div>
          </Transition>
        </div>
        <div class="sb-card">
          <div class="sb-hd"><div class="sb-icon sb-amber"><svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg></div><h3 class="sb-title">每日健康提示</h3></div>
          <Transition name="tip-fade" mode="out-in">
            <div class="tip-content" :key="currentTip"><p class="tip-text">{{ dailyTips[currentTip] }}</p></div></Transition>
          <div class="tip-actions"><button class="tip-act-btn" @click="likeTip"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3z"/><path d="M7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>有用</button><button class="tip-act-btn" @click="nextTip"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.86"/></svg>换一条</button></div>
          <div class="emergency-row"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.64 13a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.55 2h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L7.91 9.91a16 16 0 0 0 6 6l.92-.92a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 17z"/></svg>如症状加重，请及时就医或拨打 120</div>
        </div>
        <div class="sb-card">
          <div class="sb-hd"><div class="sb-icon sb-violet"><svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 2 3.09 6.26L22 9.27l-5 4.87L18.18 21 12 17.77 5.82 21 7 14.14 2 9.27l6.91-1.01L12 2z"/></svg></div><h3 class="sb-title">家庭健康挑战</h3></div>
          <div class="challenge-body">
            <div class="challenge-meta"><span class="challenge-name">7 天健康打卡</span><span class="challenge-sub">喝水 · 步行 · 早睡</span></div>
            <div class="challenge-progress"><div class="cp-dot" v-for="day in 7" :key="day" :class="{ done: day <= challengeProgress }"><svg v-if="day <= challengeProgress" width="9" height="9" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg><span v-else>{{ day }}</span></div></div>
            <div class="challenge-prog-bar"><div class="challenge-prog-fill" :style="{ width: (challengeProgress / 7 * 100) + '%' }"></div></div>
            <button class="act-btn act-btn-primary sb-full" @click="joinChallenge"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>{{ challengeJoined ? '继续打卡 ＋1 天' : '加入挑战' }}</button>
            <Transition name="fade-up"><div class="challenge-reward" v-if="challengeProgress >= 7"><svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/></svg>获得「健康之星」徽章</div></Transition>
          </div>
        </div>
        <div class="sb-card">
          <div class="sb-hd"><div class="sb-icon sb-teal"><svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg></div><h3 class="sb-title">患者经验分享</h3></div>
          <div class="exp-list"><div class="exp-item" v-for="(exp, index) in experiences" :key="index"><div class="exp-avatar"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></div><div class="exp-body"><div class="exp-name">{{ exp.name }}</div><div class="exp-text">{{ exp.text }}</div></div></div></div>
          <button class="act-btn act-btn-ghost sb-full"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>我也要分享</button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script>
import img6 from '@/views/element/image/6.png'
import img7 from '@/views/element/image/7.png'
import img8 from '@/views/element/image/8.png'
import VideoModalPlayer from './VideoModalPlayer.vue'
export default {
  name: 'CourseHealth', components: { VideoModalPlayer }, props: ['course'], emits: ['complete'],
  data() {
    return {
      showVideoModal: false, currentVideoUrl: '', knowledgeTab: 'list', sidebarTab: 'notes',
      notes: '', bloodSugar: '', bloodPressure: '', analysis: '', selectedDisease: '', followupPlan: '',
      currentSeason: 0, currentTip: 0, challengeJoined: false, challengeProgress: 0,
      selectedSymptoms: [], symptomAdvice: '', showScore: false, score: 0,
      fluVideos: [
        { title: '感冒、流感、肺炎，究竟有何区别？', bgImage: img6, bvid: 'BV1bkPReqE4t' },
        { title: '家庭流感预防三步法', bgImage: img7, bvid: 'BV1UqcyzfEoj' },
        { title: '得了流感怎么办？', bgImage: img8, bvid: 'BV1iwStBbEvL' }
      ],
      seasons: [
        { name: '春季', warnings: '花粉过敏、流感尾声', iconClass: 'si-green', svgPath: '<path d="M12 22V12"/><path d="M12 12C12 7 7 4 4 6"/><path d="M12 12c0-5 5-8 8-6"/><path d="M12 12c-4 0-8 3-8 6"/><path d="M12 12c4 0 8 3 8 6"/>' },
        { name: '夏季', warnings: '中暑预警、肠胃炎', iconClass: 'si-amber', svgPath: '<circle cx="12" cy="12" r="5"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/>' },
        { name: '秋季', warnings: '呼吸道疾病、鼻炎', iconClass: 'si-orange', svgPath: '<path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/>' },
        { name: '冬季', warnings: '流感高峰、心脑血管', iconClass: 'si-blue', svgPath: '<line x1="2" y1="12" x2="22" y2="12"/><line x1="12" y1="2" x2="12" y2="22"/><path d="m20 16-4-4 4-4"/><path d="m4 8 4 4-4 4"/><path d="m16 4-4 4-4-4"/><path d="m8 20 4-4 4 4"/>' }
      ],
      knowledge: [
        { name: '高血压', desc: 'AI 预警信号与日常预防', tag: '心血管', gc: 'kg-red', svgPath: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>' },
        { name: '糖尿病', desc: '血糖监测与饮食管理', tag: '代谢', gc: 'kg-amber', svgPath: '<path d="M9 3H5a2 2 0 0 0-2 2v4m6-6h10a2 2 0 0 1 2 2v4M9 3v18m0 0h10a2 2 0 0 0 2-2v-4M9 21H5a2 2 0 0 1-2-2v-4m0 0h18"/>' },
        { name: '流感', desc: '预防措施与早期识别', tag: '呼吸道', gc: 'kg-blue', svgPath: '<path d="M12 5v6"/><path d="M12 11c-1.6-3-4-4-5.8-3.6C4 8 3 10.2 3 12.8c0 3.3 1.9 6 5 6 2.2 0 3.4-1.5 4-3.6"/><path d="M12 11c1.6-3 4-4 5.8-3.6C20 8 21 10.2 21 12.8c0 3.3-1.9 6-5 6-2.2 0-3.4-1.5-4-3.6"/>' }
      ],
      healthImages: [
        { title: '高血压预防金字塔', gc: 'kg-red', svgPath: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>' },
        { title: '糖尿病饮食红绿灯', gc: 'kg-amber', svgPath: '<circle cx="12" cy="7" r="4"/><path d="M5 21v-2a7 7 0 0 1 14 0v2"/>' },
        { title: '骨质疏松三要素', gc: 'kg-violet', svgPath: '<path d="M6.2 9.2a2.2 2.2 0 1 1 2.9-3.3l5.8 5.1a2.6 2.6 0 0 1 .4 3.6l-1.3 1.6a2.6 2.6 0 0 1-3.6.4L4.6 11.5a2.2 2.2 0 1 1 1.6-2.3Z"/><path d="M17.8 14.8a2.2 2.2 0 1 1-2.9 3.3l-5.8-5.1a2.6 2.6 0 0 1-.4-3.6L10 7.8a2.6 2.6 0 0 1 3.6-.4l5.8 5.1a2.2 2.2 0 1 1-1.6 2.3Z"/>' },
        { title: '脑卒中识别 120', gc: 'kg-teal', svgPath: '<path d="M9.5 5.5a3 3 0 0 1 5.4 1.8 3.3 3.3 0 0 1 2.7 3.2 3.1 3.1 0 0 1-.6 1.8 3 3 0 0 1 .8 2 3.2 3.2 0 0 1-3.2 3.2H9.2A3.2 3.2 0 0 1 6 14.3c0-.8.3-1.5.8-2A3.1 3.1 0 0 1 6.2 10a3.3 3.3 0 0 1 2.7-3.2 3 3 0 0 1 .6-1.3Z"/>' }
      ],
      symptoms: ['发热', '咳嗽', '头痛', '咽痛', '鼻塞', '乏力'],
      dailyTips: [
        '多喝水有助于预防尿路感染，每日建议饮水 1500～2000ml。',
        '换季提醒：早晚温差大，注意颈部保暖，可有效降低感冒概率。',
        '用药安全：布洛芬和感冒药不要同时服用，以免成分叠加。',
        '睡眠建议：固定作息比补觉更有助于提升免疫力与精力恢复。'
      ],
      medicalChecklist: [
        { text: '测量并记录最近 3 天的血压 / 血糖值', checked: false },
        { text: '列出正在服用的所有药物清单', checked: false },
        { text: '准备好要问医生的 3 个问题', checked: false },
        { text: '带上既往病历和检查报告', checked: false }
      ],
      experiences: [
        { name: '李阿姨，62 岁', text: '按课程教的测血糖，现在控制得稳多了' },
        { name: '王先生，45 岁', text: '流感季节提前打了疫苗，全家都没中招' }
      ],
      quizzes: [
        { question: '以下哪项是流感的典型症状？', options: ['流鼻涕', '高烧＋全身酸痛', '轻微喉咙痛'], correct: 1, selected: null, showResult: false },
        { question: '高血压患者应该多久测一次血压？', options: ['每天', '每周一次', '每年一次'], correct: 0, selected: null, showResult: false },
        { question: '糖尿病饮食应该注意什么？', options: ['多吃甜食', '控制碳水摄入', '不需要控制'], correct: 1, selected: null, showResult: false }
      ]
    }
  },
  methods: {
    openVideoModal(index) { const v = this.fluVideos[index]; this.currentVideoUrl = `https://player.bilibili.com/player.html?isOutside=true&bvid=${v.bvid}&p=1&autoplay=1`; this.showVideoModal = true },
    closeVideoModal() { this.showVideoModal = false; this.currentVideoUrl = '' },
    selectSeason(i) { this.currentSeason = i },
    viewImage(i) { alert('放大查看：' + this.healthImages[i].title + '\n\n（图片放大功能演示）') },
    toggleSymptom(i) { const idx = this.selectedSymptoms.indexOf(i); if (idx > -1) this.selectedSymptoms.splice(idx, 1); else this.selectedSymptoms.push(i) },
    checkSymptoms() { this.symptomAdvice = '根据您选择的症状，常见原因可能为感冒 / 流感。\n\n居家护理建议：多休息、多喝温水、保持室内通风。\n\n就医信号：体温超过 39°C 持续 2 天以上，请及时就医。' },
    generateAnalysis() { this.analysis = '根据输入数据，AI 建议：\n1. 保持规律作息，减少高糖高盐摄入\n2. 注意饮食清淡，增加蔬菜比例\n3. 定期监测数据并记录趋势\n4. 如有不适及时联系主治医生\n\n（模拟分析，仅供参考）' },
    generateFollowup() { const p = { hypertension: '建议每月测一次血压，每半年查一次血脂与肾功能。', diabetes: '建议每周测 2～3 次血糖，每 3 个月查一次糖化血红蛋白。', postop: '建议术后第 1、2 周及第 1、3 个月分别进行复查。' }; this.followupPlan = p[this.selectedDisease] || '' },
    selectOption(qi, oi) { this.quizzes[qi].selected = oi },
    submitQuiz() { let s = 0; this.quizzes.forEach(q => { q.showResult = true; if (q.selected === q.correct) s++ }); this.score = s; this.showScore = true },
    resetQuiz() { this.quizzes.forEach(q => { q.selected = null; q.showResult = false }); this.showScore = false; this.score = 0 },
    likeTip() { alert('感谢您的反馈！') },
    nextTip() { this.currentTip = (this.currentTip + 1) % this.dailyTips.length },
    joinChallenge() { if (!this.challengeJoined) this.challengeJoined = true; if (this.challengeProgress < 7) this.challengeProgress++ },
    completeCourse() { this.$emit('complete') }
  }
}
</script>

<style scoped>
.course-health {
  --bg:rgba(255,255,255,0.03); --bg2:rgba(255,255,255,0.055); --bg3:rgba(255,255,255,0.09);
  --bd:rgba(255,255,255,0.07); --bd2:rgba(255,255,255,0.12);
  --cyan:#00d4b4; --cyan-d:rgba(0,212,180,0.09);
  --blue:#4a9eff; --blue-d:rgba(74,158,255,0.09);
  --violet:#a78bfa; --violet-d:rgba(167,139,250,0.09);
  --amber:#f59e0b; --amber-d:rgba(245,158,11,0.09);
  --green:#10b981; --green-d:rgba(16,185,129,0.09);
  --red:#f87171; --red-d:rgba(248,113,113,0.09);
  --t1:#eef4ff; --t2:#8fa3c0; --t3:#4a5f7a;
  --r:12px; --rl:20px; --rx:24px;
  font-family:'SF Pro Display','PingFang SC','HarmonyOS Sans SC',system-ui,sans-serif;
}
.safety-strip { display:flex;align-items:center;gap:7px;padding:9px 14px;border-radius:var(--r);border:1px solid rgba(245,158,11,0.18);background:rgba(245,158,11,0.05);color:rgba(245,158,11,0.75);font-size:12px;font-weight:500;margin-bottom:14px; }
.safety-strip svg { flex-shrink:0;color:var(--amber); }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
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
.course-layout { display:grid;grid-template-columns:1fr 308px;gap:16px;align-items:start; }
.main-col { display:flex;flex-direction:column;gap:14px; }
.sec-card { padding:18px 20px;border-radius:var(--rx);border:1px solid var(--bd);background:var(--bg2);position:relative;overflow:hidden; }
.sec-card::before { content:'';position:absolute;top:0;left:0;right:0;height:1px;background:linear-gradient(90deg,transparent,rgba(0,212,180,0.18),transparent); }
.sec-hd { display:flex;align-items:flex-start;gap:11px;margin-bottom:16px; }
.sec-icon { width:33px;height:33px;border-radius:9px;flex-shrink:0;display:flex;align-items:center;justify-content:center; }
.ic-red    { background:var(--red-d);    border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.ic-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.ic-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.ic-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.ic-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.ic-green  { background:var(--green-d);  border:1px solid rgba(16,185,129,0.2);  color:var(--green); }
.sec-title { font-size:14px;font-weight:600;color:var(--t1);margin-bottom:2px; }
.sec-sub   { font-size:11px;color:var(--t3); }
.video-grid { display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-bottom:10px; }
.video-card { cursor:pointer;display:flex;flex-direction:column;gap:7px; }
.video-thumb { position:relative;aspect-ratio:16/9;border-radius:var(--r);overflow:hidden;border:1px solid var(--bd2);transition:all .3s ease; }
.video-thumb:hover { border-color:rgba(0,212,180,0.3);transform:translateY(-2px); }
.video-bg { width:100%;height:100%;object-fit:cover;display:block; }
.video-overlay { position:absolute;inset:0;background:rgba(4,8,20,0.5);display:flex;align-items:center;justify-content:center;transition:background .3s ease; }
.video-thumb:hover .video-overlay { background:rgba(4,8,20,0.3); }
.play-rings { position:absolute; }
.play-ring { position:absolute;border-radius:50%;border:1px solid rgba(0,212,180,0.22);animation:rp 2s ease-out infinite; }
.pr-1 { width:60px;height:60px;top:-30px;left:-30px; }
.pr-2 { width:80px;height:80px;top:-40px;left:-40px;animation-delay:.55s; }
@keyframes rp { 0%{opacity:.5;transform:scale(.7)} 100%{opacity:0;transform:scale(1.2)} }
.play-btn { width:44px;height:44px;border-radius:50%;background:rgba(255,255,255,0.92);backdrop-filter:blur(6px);display:flex;align-items:center;justify-content:center;position:relative;z-index:1;transition:all .3s ease;box-shadow:0 6px 20px rgba(0,0,0,.36); }
.play-svg { width:18px;height:18px;color:#0d1526;margin-left:3px;transition:color .3s; }
.video-thumb:hover .play-btn { transform:scale(1.1); }
.video-thumb:hover .play-svg { color:#00a1d6; }
.video-title-row { display:flex;align-items:flex-start;gap:5px; }
.video-play-icon { width:18px;height:18px;border-radius:5px;flex-shrink:0;background:var(--red-d);border:1px solid rgba(248,113,113,0.18);color:var(--red);display:flex;align-items:center;justify-content:center;margin-top:1px; }
.video-card-title { font-size:11px;font-weight:500;color:var(--t1);line-height:1.4;flex:1; }
.warn-strip { display:flex;align-items:center;gap:6px;padding:8px 11px;border-radius:var(--r);border:1px solid rgba(248,113,113,0.18);background:rgba(248,113,113,0.05);color:rgba(248,113,113,0.7);font-size:11px; }
.season-grid { display:grid;grid-template-columns:repeat(4,1fr);gap:10px; }
.season-card { padding:14px 12px 16px;border-radius:var(--rl);border:1px solid var(--bd);background:var(--bg);cursor:pointer;transition:all .22s ease;position:relative;display:flex;flex-direction:column;align-items:center;gap:7px;text-align:center; }
.season-card:hover { border-color:var(--bd2);transform:translateY(-2px); }
.season-card.active { border-color:rgba(0,212,180,0.3);background:var(--cyan-d); }
.season-icon-wrap { width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center; }
.si-green  { background:rgba(16,185,129,0.1);border:1px solid rgba(16,185,129,0.2); color:var(--green); }
.si-amber  { background:rgba(245,158,11,0.1); border:1px solid rgba(245,158,11,0.2); color:var(--amber); }
.si-orange { background:rgba(249,115,22,0.1); border:1px solid rgba(249,115,22,0.2); color:#fb923c; }
.si-blue   { background:var(--blue-d);         border:1px solid rgba(74,158,255,0.2); color:var(--blue); }
.season-name { font-size:12px;font-weight:600;color:var(--t1); }
.season-warn { font-size:10px;color:var(--t2);line-height:1.5; }
.season-active-dot { position:absolute;bottom:8px;left:50%;transform:translateX(-50%);width:5px;height:5px;border-radius:50%;background:var(--cyan); }
.tab-row { display:flex;gap:7px;margin-bottom:13px; }
.tab-btn { display:flex;align-items:center;gap:5px;height:32px;padding:0 12px;border-radius:999px;border:1px solid var(--bd2);background:var(--bg);color:var(--t2);font-size:12px;font-weight:500;cursor:pointer;transition:all .2s ease; }
.tab-btn:hover { color:var(--t1); }
.tab-btn.active { background:linear-gradient(135deg,#00c9ab,#3d9fff);border-color:transparent;color:#07101f;font-weight:700; }
.tab-fade-enter-active,.tab-fade-leave-active { transition:all .2s ease; }
.tab-fade-enter-from,.tab-fade-leave-to { opacity:0;transform:translateY(4px); }
.knowledge-list { display:flex;flex-direction:column;gap:8px; }
.knowledge-row { display:flex;align-items:center;gap:12px;padding:11px 13px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg);transition:border-color .22s; }
.knowledge-row:hover { border-color:var(--bd2); }
.knowledge-glyph { width:36px;height:36px;border-radius:10px;flex-shrink:0;display:flex;align-items:center;justify-content:center; }
.kg-red    { background:var(--red-d);    border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.kg-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.kg-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.kg-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.kg-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.knowledge-body { flex:1; }
.knowledge-name { font-size:13px;font-weight:600;color:var(--t1);margin-bottom:2px; }
.knowledge-desc { font-size:11px;color:var(--t2);line-height:1.55; }
.knowledge-tag { height:20px;padding:0 9px;border-radius:999px;flex-shrink:0;background:var(--bg3);border:1px solid var(--bd2);font-size:9px;font-weight:600;color:var(--t3);display:flex;align-items:center; }
.gallery-list { display:flex;flex-direction:column;gap:7px; }
.gallery-row { display:flex;align-items:center;gap:11px;padding:10px 12px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg);cursor:pointer;transition:all .22s ease; }
.gallery-row:hover { border-color:rgba(0,212,180,0.22);transform:translateX(2px); }
.gallery-glyph { width:32px;height:32px;border-radius:9px;flex-shrink:0;display:flex;align-items:center;justify-content:center; }
.gallery-label { flex:1;font-size:12px;color:var(--t1);font-weight:500; }
.gallery-chevron { color:var(--t3);flex-shrink:0; }
.tools-grid { display:grid;grid-template-columns:1fr 1fr;gap:12px; }
.tool-card { padding:15px;border-radius:var(--rl);border:1px solid var(--bd);background:var(--bg);display:flex;flex-direction:column;gap:10px; }
.tool-card-header { display:flex;align-items:center;gap:9px; }
.tool-icon { width:30px;height:30px;border-radius:8px;flex-shrink:0;display:flex;align-items:center;justify-content:center; }
.tool-title { font-size:13px;font-weight:600;color:var(--t1); }
.tool-desc { font-size:11px;color:var(--t2);line-height:1.65; }
.tool-warn-row { display:flex;align-items:center;gap:5px;padding:6px 9px;border-radius:var(--r);border:1px solid rgba(248,113,113,0.16);background:rgba(248,113,113,0.05);color:rgba(248,113,113,0.65);font-size:10px; }
.tool-warn-row svg { flex-shrink:0; }
.symptom-chips { display:flex;flex-wrap:wrap;gap:5px; }
.symptom-chip { padding:4px 10px;border-radius:999px;font-size:11px;font-weight:500;border:1px solid var(--bd2);background:var(--bg2);color:var(--t2);cursor:pointer;transition:all .2s ease; }
.symptom-chip:hover { color:var(--t1); }
.symptom-chip.active { background:linear-gradient(135deg,#00c9ab,#3d9fff);border-color:transparent;color:#07101f;font-weight:700; }
.input-group { display:flex;flex-direction:column;gap:5px; }
.input-group label { display:flex;align-items:center;gap:5px;font-size:11px;color:var(--t2);font-weight:500; }
.input-group input,.input-group select { padding:8px 10px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg);color:var(--t1);font-size:12px;font-family:inherit;transition:border-color .2s; }
.input-group input:focus,.input-group select:focus { outline:none;border-color:rgba(0,212,180,0.3); }
.input-group select option { background:#0d1526; }
.act-btn { display:inline-flex;align-items:center;justify-content:center;gap:5px;height:34px;padding:0 14px;border-radius:999px;cursor:pointer;font-size:12px;font-weight:600;transition:all .2s ease; }
.act-btn-primary { background:linear-gradient(135deg,#00c9ab,#3d9fff);border:none;color:#07101f;box-shadow:0 0 14px rgba(0,212,180,0.2); }
.act-btn-primary:hover { box-shadow:0 0 24px rgba(0,212,180,0.35); }
.act-btn-primary:disabled { opacity:.4;cursor:not-allowed;box-shadow:none; }
.act-btn-outline { background:transparent;border:1px solid var(--bd2);color:var(--t2); }
.act-btn-outline:hover { border-color:rgba(0,212,180,0.25);color:var(--cyan); }
.act-btn-ghost { background:var(--bg2);border:1px solid var(--bd2);color:var(--t2); }
.act-btn-ghost:hover { color:var(--t1); }
.sb-full { width:100%;margin-top:4px; }
.fade-up-enter-active,.fade-up-leave-active { transition:all .28s ease; }
.fade-up-enter-from,.fade-up-leave-to { opacity:0;transform:translateY(-6px); }
.result-box { padding:11px 13px;border-radius:var(--r);background:rgba(16,185,129,0.07);border:1px solid rgba(16,185,129,0.18); }
.result-box-hd { display:flex;align-items:center;gap:6px;font-size:10px;font-weight:700;color:var(--green);margin-bottom:5px;letter-spacing:.04em; }
.result-box p { font-size:11px;color:var(--t2);line-height:1.7;white-space:pre-wrap; }
.result-box-warn { display:flex;align-items:center;gap:4px;margin-top:6px;font-size:10px;color:rgba(248,113,113,0.6); }
.quiz-list { display:flex;flex-direction:column;gap:12px;margin-bottom:12px; }
.quiz-item { padding:13px 15px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg); }
.quiz-q-row { display:flex;align-items:flex-start;gap:8px;margin-bottom:10px; }
.quiz-badge { height:21px;min-width:28px;border-radius:6px;padding:0 6px;display:flex;align-items:center;justify-content:center;flex-shrink:0;margin-top:1px;background:var(--cyan-d);border:1px solid rgba(0,212,180,0.2);font-size:9px;font-weight:700;color:var(--cyan); }
.quiz-question { font-size:13px;font-weight:600;color:var(--t1);line-height:1.55; }
.quiz-options { display:flex;flex-direction:column;gap:6px; }
.quiz-opt { display:flex;align-items:center;gap:8px;padding:7px 9px;border-radius:var(--r);cursor:pointer;transition:all .2s ease;border:1px solid transparent;background:rgba(255,255,255,0.02); }
.quiz-opt:hover { background:var(--bg3);border-color:var(--bd); }
.quiz-opt.selected { border-color:rgba(0,212,180,0.22);background:var(--cyan-d); }
.quiz-opt.correct  { border-color:rgba(16,185,129,0.25);background:rgba(16,185,129,0.07); }
.quiz-opt.wrong    { border-color:rgba(248,113,113,0.22);background:rgba(248,113,113,0.06); }
.opt-letter { width:19px;height:19px;border-radius:5px;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:10px;font-weight:700;background:var(--bg3);color:var(--t3); }
.quiz-opt.selected .opt-letter { background:var(--cyan-d);color:var(--cyan); }
.quiz-opt.correct  .opt-letter { background:rgba(16,185,129,0.14);color:var(--green); }
.quiz-opt.wrong    .opt-letter { background:rgba(248,113,113,0.12);color:var(--red); }
.opt-text { flex:1;font-size:12px;color:var(--t2); }
.opt-state { margin-left:auto;display:flex;align-items:center;color:var(--green); }
.opt-wrong-state { color:var(--red); }
.quiz-footer { display:flex;gap:8px;justify-content:center; }
.score-panel { text-align:center;padding:18px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg);margin-top:12px; }
.score-ring { display:inline-flex;align-items:baseline;gap:3px;padding:10px 22px;border-radius:999px;margin-bottom:8px;background:linear-gradient(135deg,var(--blue),#6366f1);box-shadow:0 0 20px rgba(74,158,255,0.25); }
.score-ring.perfect { background:linear-gradient(135deg,var(--cyan),var(--green));box-shadow:0 0 20px rgba(0,212,180,0.28); }
.score-num { font-size:22px;font-weight:800;color:#fff; }
.score-sep { font-size:14px;color:rgba(255,255,255,0.5); }
.score-total { font-size:16px;font-weight:700;color:rgba(255,255,255,0.7); }
.score-label { font-size:13px;color:var(--t2);margin-bottom:8px; }
.score-award { display:inline-flex;align-items:center;gap:6px;font-size:12px;font-weight:600;color:#fbbf24; }
.complete-row { text-align:center;padding-top:4px; }
.complete-btn { display:inline-flex;align-items:center;justify-content:center;gap:8px;padding:0 32px;height:48px;border-radius:999px;border:none;cursor:pointer;font-size:14px;font-weight:700;background:linear-gradient(135deg,#00c9ab,#3d9fff);color:#07101f;box-shadow:0 0 28px rgba(0,212,180,0.28);transition:all .3s ease;position:relative;overflow:hidden; }
.complete-btn:hover { transform:translateY(-2px);box-shadow:0 0 42px rgba(0,212,180,0.44); }
.btn-shimmer { position:absolute;top:0;left:-100%;width:55%;height:100%;background:linear-gradient(90deg,transparent,rgba(255,255,255,0.28),transparent);transform:skewX(-18deg);animation:shimmer 3.5s ease-in-out infinite; }
@keyframes shimmer { 0%{left:-100%} 50%,100%{left:160%} }
.sidebar-col { display:flex;flex-direction:column;gap:12px;position:sticky;top:20px; }
.sb-card { padding:15px 17px;border-radius:var(--rx);border:1px solid var(--bd);background:var(--bg2);position:relative;overflow:hidden; }
.sb-card::before { content:'';position:absolute;top:0;left:0;right:0;height:1px;background:linear-gradient(90deg,transparent,rgba(0,212,180,0.18),transparent); }
.sb-hd { display:flex;align-items:center;gap:8px;margin-bottom:12px; }
.sb-icon { width:27px;height:27px;border-radius:7px;flex-shrink:0;display:flex;align-items:center;justify-content:center; }
.sb-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.sb-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.sb-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.sb-title { font-size:12px;font-weight:600;color:var(--t1); }
.notes-area textarea { width:100%;min-height:100px;padding:9px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg);color:var(--t1);font-size:12px;resize:vertical;font-family:inherit;transition:border-color .2s;margin-bottom:6px;display:block; }
.notes-area textarea:focus { outline:none;border-color:rgba(0,212,180,0.25); }
.notes-area textarea::placeholder { color:var(--t3); }
.checklist-area { display:flex;flex-direction:column;gap:5px; }
.cl-row { display:flex;align-items:center;gap:8px;padding:8px 10px;border-radius:var(--r);border:1px solid var(--bd);background:var(--bg);cursor:pointer;font-size:11px;color:var(--t2);transition:all .2s ease; }
.cl-row:hover { border-color:var(--bd2); }
.cl-row.checked { border-color:rgba(16,185,129,0.2);background:rgba(16,185,129,0.05);color:var(--t1); }
.cl-row input { display:none; }
.cl-box { width:15px;height:15px;border-radius:4px;flex-shrink:0;border:1px solid var(--bd2);display:flex;align-items:center;justify-content:center;transition:all .2s; }
.cl-row.checked .cl-box { background:var(--green);border-color:var(--green);color:#fff; }
.tip-fade-enter-active,.tip-fade-leave-active { transition:all .3s ease; }
.tip-fade-enter-from,.tip-fade-leave-to { opacity:0;transform:translateY(6px); }
.tip-content { padding:11px;border-radius:var(--r);background:var(--bg);border:1px solid var(--bd);margin-bottom:9px; }
.tip-text { font-size:12px;color:var(--t1);line-height:1.7; }
.tip-actions { display:flex;gap:6px;margin-bottom:9px; }
.tip-act-btn { flex:1;display:flex;align-items:center;justify-content:center;gap:5px;height:29px;border-radius:999px;border:1px solid var(--bd2);background:var(--bg);color:var(--t2);font-size:11px;cursor:pointer;transition:all .2s; }
.tip-act-btn:hover { border-color:rgba(0,212,180,0.25);color:var(--cyan); }
.emergency-row { display:flex;align-items:center;gap:5px;font-size:10px;color:rgba(248,113,113,0.6);line-height:1.5; }
.emergency-row svg { flex-shrink:0;color:var(--red); }
.challenge-body { display:flex;flex-direction:column;gap:10px; }
.challenge-meta { display:flex;align-items:baseline;justify-content:space-between; }
.challenge-name { font-size:13px;font-weight:600;color:var(--t1); }
.challenge-sub { font-size:10px;color:var(--t3); }
.challenge-progress { display:flex;gap:5px; }
.cp-dot { flex:1;height:28px;border-radius:7px;display:flex;align-items:center;justify-content:center;border:1px solid var(--bd2);background:var(--bg);font-size:10px;color:var(--t3);transition:all .3s ease; }
.cp-dot.done { background:linear-gradient(135deg,var(--cyan),var(--green));border-color:transparent;color:#07101f;box-shadow:0 0 8px rgba(0,212,180,0.22); }
.challenge-prog-bar { height:4px;background:var(--bg3);border-radius:999px;overflow:hidden; }
.challenge-prog-fill { height:100%;background:linear-gradient(90deg,var(--cyan),var(--green));border-radius:999px;transition:width .6s cubic-bezier(.4,0,.2,1); }
.challenge-reward { display:flex;align-items:center;gap:6px;padding:8px 10px;border-radius:var(--r);border:1px solid rgba(251,191,36,0.22);background:rgba(251,191,36,0.07);color:#fbbf24;font-size:11px;font-weight:600; }
.exp-list { display:flex;flex-direction:column;gap:8px;margin-bottom:9px; }
.exp-item { display:flex;gap:9px;padding:9px;border-radius:var(--r);background:var(--bg);border:1px solid var(--bd); }
.exp-avatar { width:28px;height:28px;border-radius:50%;flex-shrink:0;background:var(--blue-d);border:1px solid rgba(74,158,255,0.18);color:var(--blue);display:flex;align-items:center;justify-content:center; }
.exp-body { flex:1; }
.exp-name { font-size:11px;font-weight:600;color:var(--t1);margin-bottom:3px; }
.exp-text { font-size:11px;color:var(--t2);line-height:1.5; }
@media (max-width:1100px) { .course-layout { grid-template-columns:1fr; } .sidebar-col { position:static; } }
@media (max-width:700px) { .video-grid { grid-template-columns:1fr; } .season-grid { grid-template-columns:repeat(2,1fr); } .tools-grid { grid-template-columns:1fr; } }
</style>
