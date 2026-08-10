<template>
  <div class="course-content">
    <!-- 视频模态框 -->
    <VideoModalPlayer
      v-model="showVideoModal"
      src="https://player.bilibili.com/player.html?isOutside=true&aid=114758808311926&bvid=BV1FHK1ztEut&cid=30736321945&p=1&autoplay=1"
    />

    <!-- 视频区块 -->
    <div class="content-section">
      <div class="section-header">
        <div class="section-icon section-icon-video">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
        </div>
        <h2 class="section-title">AI 在医院里能帮助医生做什么？</h2>
      </div>
      <div class="video-section">
        <div class="video-player" @click="openVideoModal">
          <div class="video-placeholder-large">
            <img src="@/views/element/image/5.png" class="video-background" alt="AI医疗">
            <div class="video-overlay">
              <div class="play-btn-wrap">
                <div class="play-btn-rings">
                  <div class="play-ring play-ring-1"></div>
                  <div class="play-ring play-ring-2"></div>
                </div>
                <div class="bilibili-play-btn">
                  <svg class="play-icon-svg" viewBox="0 0 24 24" fill="none">
                    <path d="M8 5v14l11-7z" fill="currentColor"/>
                  </svg>
                </div>
              </div>
              <div class="video-label">点击播放</div>
            </div>
          </div>
        </div>
        <div class="video-desc">通过生动动画，了解 AI 如何在医院中辅助医生工作</div>
      </div>
    </div>

    <!-- 场景化目录 -->
    <div class="content-section">
      <div class="section-header">
        <div class="section-icon section-icon-scene">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
        </div>
        <h2 class="section-title">场景化目录</h2>
      </div>
      <div class="scenario-grid">
        <div class="scenario-card" v-for="(item, index) in scenarios" :key="index">
          <div class="scenario-icon-wrap">
            <span class="scenario-emoji">{{ item.icon }}</span>
          </div>
          <div class="scenario-title">{{ item.title }}</div>
          <div class="scenario-desc">{{ item.description }}</div>
        </div>
      </div>
    </div>

    <!-- 互动问答 -->
    <div class="content-section">
      <div class="section-header">
        <div class="section-icon section-icon-quiz">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        </div>
        <h2 class="section-title">互动问答</h2>
      </div>
      <div class="quiz-section">
        <div class="quiz-item" v-for="(quiz, index) in quizzes" :key="index">
          <div class="quiz-q-row">
            <div class="quiz-number">Q{{ index + 1 }}</div>
            <div class="quiz-question">{{ quiz.question }}</div>
          </div>
          <div class="quiz-options">
            <label
              class="quiz-option"
              v-for="(option, optIndex) in quiz.options" :key="optIndex"
              :class="{
                correct: userAnswers[index] === quiz.correct && userAnswers[index] === optIndex,
                wrong: userAnswers[index] !== undefined && userAnswers[index] !== quiz.correct && userAnswers[index] === optIndex
              }"
            >
              <input type="radio" :name="'quiz' + index" :value="optIndex" v-model="userAnswers[index]" @change="checkAnswer(index, quiz.correct)">
              <div class="opt-indicator"></div>
              <span class="opt-text">{{ option }}</span>
              <div class="opt-state-icon">
                <svg v-if="userAnswers[index] === quiz.correct && userAnswers[index] === optIndex" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                <svg v-if="userAnswers[index] !== undefined && userAnswers[index] !== quiz.correct && userAnswers[index] === optIndex" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </div>
            </label>
          </div>
          <Transition name="fade-slide">
            <div class="quiz-feedback" v-if="userAnswers[index] !== undefined">
              <div v-if="userAnswers[index] === quiz.correct" class="feedback-correct">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                回答正确 — {{ quiz.feedback }}
              </div>
              <div v-else class="feedback-wrong">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                回答错误，正确答案是：{{ quiz.options[quiz.correct] }}
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </div>

    <!-- 风险提示卡 -->
    <div class="content-section">
      <div class="section-header">
        <div class="section-icon section-icon-risk">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        </div>
        <h2 class="section-title">风险提示</h2>
      </div>
      <div class="risk-cards">
        <div class="risk-card risk-warning">
          <div class="risk-card-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          </div>
          <div class="risk-title">AI 不代替医生</div>
          <div class="risk-desc">AI 的建议仅供参考，永远不能替代专业医生的诊断与临床判断。</div>
        </div>
        <div class="risk-card risk-danger">
          <div class="risk-card-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          </div>
          <div class="risk-title">不自我诊断</div>
          <div class="risk-desc">不要根据 AI 建议自行诊断或用药，身体不适请及时就医。</div>
        </div>
      </div>
    </div>

    <!-- 就医准备清单 -->
    <div class="content-section">
      <div class="section-header">
        <div class="section-icon section-icon-check">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        </div>
        <h2 class="section-title">就医准备清单</h2>
      </div>
      <div class="checklist-section">
        <label
          class="checklist-item"
          v-for="(item, index) in checklist" :key="index"
          :class="{ checked: item.checked }"
        >
          <input type="checkbox" v-model="item.checked">
          <div class="check-box">
            <svg v-if="item.checked" width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
          </div>
          <span>{{ item.text }}</span>
        </label>

        <button class="gen-btn" @click="generateList">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
          生成就诊时可问医生的 3 个 AI 相关问题
          <span class="btn-shimmer"></span>
        </button>
      </div>
    </div>

    <!-- 完成按钮 -->
    <div class="complete-section">
      <button class="btn btn-primary complete-btn" @click="completeCourse">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
        完成本章节学习
        <span class="btn-shimmer"></span>
      </button>
    </div>
  </div>
</template>

<script>
import VideoModalPlayer from './VideoModalPlayer.vue'

export default {
  name: 'CourseBasic',
  components: { VideoModalPlayer },
  props: ['course'],
  emits: ['complete'],
  data() {
    return {
      showVideoModal: false,
      userAnswers: [],
      scenarios: [
        { icon: '🏥', title: '挂号时 AI 分诊', description: 'AI 根据症状智能推荐合适的科室，减少挂错号的情况。' },
        { icon: '🩺', title: '拍片后 AI 辅助读片', description: 'AI 快速标注影像中的可疑区域，辅助医生提高效率。' },
        { icon: '⏰', title: 'AI 提醒复诊', description: '智能提醒复查和随访时间，持续管理患者健康状态。' }
      ],
      quizzes: [
        {
          question: '你觉得以下哪个是 AI 能做的？',
          options: ['AI 可以替代医生看病', 'AI 可以辅助医生分析数据', 'AI 可以给病人做手术', 'AI 可以给病人开药'],
          correct: 1,
          feedback: 'AI 可以帮助医生分析大量的医疗数据，提高工作效率。'
        },
        {
          question: '拿到 AI 的建议后，你应该？',
          options: ['完全按照 AI 说的做', '参考 AI 建议，还是听医生的', '不相信 AI 说的任何话', '直接去药店买药'],
          correct: 1,
          feedback: 'AI 建议作为参考，最终决策请听从专业医生的建议。'
        }
      ],
      checklist: [
        { text: '我了解了 AI 在医疗中的基本作用', checked: false },
        { text: '我知道 AI 建议仅供参考', checked: false },
        { text: '我会优先听从医生建议', checked: false }
      ]
    }
  },
  methods: {
    openVideoModal() { this.showVideoModal = true },
    closeVideoModal() { this.showVideoModal = false },
    checkAnswer(index, correct) {},
    generateList() {
      alert('已为您生成就诊时可问医生的 3 个 AI 相关问题：\n\n1. 医生，这个 AI 分析结果可靠吗？\n2. AI 圈出的区域需要进一步检查吗？\n3. 这个治疗方案是 AI 建议的，您怎么看？\n\n（演示功能）');
    },
    completeCourse() { this.$emit('complete'); }
  }
}
</script>

<style scoped>
/* ══════════════════════ 设计令牌 ══════════════════════ */
.course-content {
  --c-bg: rgba(255,255,255,0.03);
  --c-bg2: rgba(255,255,255,0.06);
  --c-border: rgba(255,255,255,0.08);
  --c-border2: rgba(255,255,255,0.12);
  --c-cyan: #00d4b4;
  --c-cyan-dim: rgba(0,212,180,0.10);
  --c-blue: #4a9eff;
  --c-green: #10b981;
  --c-amber: #f59e0b;
  --c-red: #f87171;
  --c-text1: #eef4ff;
  --c-text2: #8fa3c0;
  --c-text3: #4a5f7a;
  --r-sm: 10px; --r-md: 14px; --r-lg: 20px;

  display: flex; flex-direction: column; gap: 16px;
  font-family: 'SF Pro Display', 'PingFang SC', 'HarmonyOS Sans SC', system-ui, sans-serif;
}

/* ══════════════════════ 视频模态框 ══════════════════════ */
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

/* ══════════════════════ 内容区块 ══════════════════════ */
.content-section {
  padding: 20px 22px; border-radius: var(--r-lg);
  border: 1px solid var(--c-border); background: var(--c-bg2);
  position: relative; overflow: hidden;
}
.content-section::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,212,180,0.2), transparent);
}

.section-header { display: flex; align-items: center; gap: 10px; margin-bottom: 18px; }
.section-icon {
  width: 32px; height: 32px; border-radius: 9px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.section-icon-video { background: rgba(248,113,113,0.1); border: 1px solid rgba(248,113,113,0.2); color: var(--c-red); }
.section-icon-scene { background: rgba(74,158,255,0.1); border: 1px solid rgba(74,158,255,0.2); color: var(--c-blue); }
.section-icon-quiz { background: rgba(167,139,250,0.1); border: 1px solid rgba(167,139,250,0.2); color: #a78bfa; }
.section-icon-risk { background: rgba(245,158,11,0.1); border: 1px solid rgba(245,158,11,0.2); color: var(--c-amber); }
.section-icon-check { background: rgba(16,185,129,0.1); border: 1px solid rgba(16,185,129,0.2); color: var(--c-green); }

.section-title { font-size: 15px; font-weight: 600; color: var(--c-text1); }

/* ══════════════════════ 视频播放 ══════════════════════ */
.video-section { display: flex; flex-direction: column; gap: 12px; }
.video-player { width: 100%; cursor: pointer; }

.video-placeholder-large {
  position: relative; border-radius: var(--r-md); overflow: hidden;
  border: 1px solid var(--c-border2); transition: all 0.3s ease;
}
.video-background { width: 100%; display: block; border-radius: calc(var(--r-md) - 1px); }

.video-overlay {
  position: absolute; inset: 0;
  background: rgba(4,8,20,0.5);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 10px; transition: background 0.3s ease;
}
.video-placeholder-large:hover .video-overlay { background: rgba(4,8,20,0.3); }

.play-btn-wrap { position: relative; display: flex; align-items: center; justify-content: center; }
.play-btn-rings { position: absolute; inset: -24px; }
.play-ring {
  position: absolute; border-radius: 50%; border: 1px solid rgba(0,212,180,0.25);
  animation: ringPulse 2s ease-out infinite;
}
.play-ring-1 { inset: 0; }
.play-ring-2 { inset: -14px; animation-delay: 0.6s; }
@keyframes ringPulse { 0% { opacity:0.6; transform:scale(0.8); } 100% { opacity:0; transform:scale(1.3); } }

.bilibili-play-btn {
  width: 72px; height: 72px; border-radius: 50%;
  background: rgba(255,255,255,0.95); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.4,0,0.2,1);
  box-shadow: 0 8px 28px rgba(0,0,0,0.4), 0 0 0 1px rgba(255,255,255,0.3) inset;
  position: relative; z-index: 1;
}
.play-icon-svg { width: 28px; height: 28px; color: #0d1526; margin-left: 4px; transition: all 0.3s ease; }
.video-placeholder-large:hover .bilibili-play-btn {
  transform: scale(1.12); box-shadow: 0 12px 36px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.5) inset;
}
.video-placeholder-large:hover .play-icon-svg { color: #00a1d6; transform: scale(1.05); }
.video-label { font-size: 12px; color: rgba(255,255,255,0.7); font-weight: 500; }

.video-desc { font-size: 12px; color: var(--c-text2); line-height: 1.6; padding: 0 2px; }

/* ══════════════════════ 场景卡 ══════════════════════ */
.scenario-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px,1fr)); gap: 12px; }
.scenario-card {
  background: var(--c-bg); border-radius: var(--r-md);
  padding: 18px 16px; text-align: center;
  border: 1px solid var(--c-border); transition: all 0.25s ease;
}
.scenario-card:hover { border-color: rgba(0,212,180,0.25); transform: translateY(-3px); }
.scenario-icon-wrap { margin-bottom: 10px; }
.scenario-emoji { font-size: 32px; display: block; filter: drop-shadow(0 2px 8px rgba(0,0,0,0.3)); }
.scenario-title { font-size: 13px; font-weight: 600; color: var(--c-text1); margin-bottom: 6px; }
.scenario-desc { font-size: 11px; color: var(--c-text2); line-height: 1.6; }

/* ══════════════════════ 问答 ══════════════════════ */
.quiz-section { display: flex; flex-direction: column; gap: 18px; }
.quiz-item {
  background: var(--c-bg); border-radius: var(--r-md); padding: 16px 18px;
  border: 1px solid var(--c-border);
}
.quiz-q-row { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 14px; }
.quiz-number {
  height: 24px; min-width: 32px; border-radius: 7px; padding: 0 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 10px; font-weight: 700; letter-spacing: 0.06em;
  background: rgba(0,212,180,0.1); border: 1px solid rgba(0,212,180,0.2); color: var(--c-cyan);
  flex-shrink: 0; margin-top: 1px;
}
.quiz-question { font-size: 13px; font-weight: 600; color: var(--c-text1); line-height: 1.6; }

.quiz-options { display: flex; flex-direction: column; gap: 7px; }
.quiz-option {
  display: flex; align-items: center; gap: 9px; padding: 10px 12px;
  border-radius: var(--r-sm); cursor: pointer; transition: all 0.2s ease;
  border: 1px solid transparent; background: rgba(255,255,255,0.03);
}
.quiz-option:hover { background: rgba(255,255,255,0.06); border-color: var(--c-border2); }
.quiz-option input { display: none; }
.opt-indicator {
  width: 16px; height: 16px; border-radius: 50%;
  border: 1px solid var(--c-border2); flex-shrink: 0; transition: all 0.2s ease;
}
.quiz-option:hover .opt-indicator { border-color: var(--c-cyan); }
.opt-text { flex: 1; font-size: 12px; color: var(--c-text2); }
.opt-state-icon { width: 18px; height: 18px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }

.quiz-option.correct {
  background: rgba(16,185,129,0.08); border-color: rgba(16,185,129,0.25);
}
.quiz-option.correct .opt-indicator { background: var(--c-green); border-color: var(--c-green); }
.quiz-option.correct .opt-text { color: #6ee7b7; }
.quiz-option.correct .opt-state-icon { color: var(--c-green); }

.quiz-option.wrong {
  background: rgba(248,113,113,0.07); border-color: rgba(248,113,113,0.22);
}
.quiz-option.wrong .opt-indicator { background: var(--c-red); border-color: var(--c-red); }
.quiz-option.wrong .opt-text { color: #fca5a5; }
.quiz-option.wrong .opt-state-icon { color: var(--c-red); }

.fade-slide-enter-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(-6px); }

.quiz-feedback { margin-top: 10px; }
.feedback-correct, .feedback-wrong {
  display: flex; align-items: center; gap: 7px;
  padding: 10px 12px; border-radius: var(--r-sm); font-size: 12px; line-height: 1.5;
}
.feedback-correct { background: rgba(16,185,129,0.08); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.18); }
.feedback-wrong { background: rgba(248,113,113,0.07); color: #fca5a5; border: 1px solid rgba(248,113,113,0.18); }

/* ══════════════════════ 风险卡 ══════════════════════ */
.risk-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px,1fr)); gap: 12px; }
.risk-card {
  border-radius: var(--r-md); padding: 18px;
  display: flex; flex-direction: column; gap: 8px;
}
.risk-card.risk-warning {
  background: rgba(245,158,11,0.06); border: 1px solid rgba(245,158,11,0.18);
}
.risk-card.risk-danger {
  background: rgba(248,113,113,0.06); border: 1px solid rgba(248,113,113,0.18);
}
.risk-card-icon { width: 40px; height: 40px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.risk-warning .risk-card-icon { background: rgba(245,158,11,0.1); color: var(--c-amber); }
.risk-danger .risk-card-icon { background: rgba(248,113,113,0.1); color: var(--c-red); }
.risk-title { font-size: 14px; font-weight: 600; color: var(--c-text1); }
.risk-warning .risk-title { color: #fcd34d; }
.risk-danger .risk-title { color: #fca5a5; }
.risk-desc { font-size: 12px; line-height: 1.65; }
.risk-warning .risk-desc { color: rgba(245,158,11,0.65); }
.risk-danger .risk-desc { color: rgba(248,113,113,0.6); }

/* ══════════════════════ 清单 ══════════════════════ */
.checklist-section { display: flex; flex-direction: column; gap: 8px; }
.checklist-item {
  display: flex; align-items: center; gap: 10px; padding: 10px 12px;
  background: var(--c-bg); border-radius: var(--r-sm);
  border: 1px solid var(--c-border); cursor: pointer; transition: all 0.2s ease;
  font-size: 13px; color: var(--c-text2);
}
.checklist-item:hover { border-color: var(--c-border2); }
.checklist-item.checked { border-color: rgba(16,185,129,0.2); background: rgba(16,185,129,0.05); color: var(--c-text1); }
.checklist-item input { display: none; }
.check-box {
  width: 18px; height: 18px; border-radius: 5px; flex-shrink: 0;
  border: 1px solid var(--c-border2); display: flex; align-items: center; justify-content: center;
  transition: all 0.2s ease;
}
.checklist-item.checked .check-box { background: var(--c-green); border-color: var(--c-green); color: #fff; }

.gen-btn {
  position: relative; overflow: hidden;
  display: flex; align-items: center; justify-content: center; gap: 7px;
  margin-top: 8px; width: 100%; height: 42px; border-radius: 999px;
  border: 1px solid var(--c-border2); background: var(--c-bg2);
  color: var(--c-text1); font-size: 12px; font-weight: 600; cursor: pointer;
  transition: all 0.25s ease;
}
.gen-btn:hover { border-color: rgba(0,212,180,0.3); background: var(--c-cyan-dim); color: var(--c-cyan); }

/* ══════════════════════ 完成按钮 ══════════════════════ */
.complete-section { text-align: center; padding-top: 8px; }
.btn {
  display: inline-flex; align-items: center; justify-content: center; gap: 8px;
  padding: 0 28px; height: 48px; border-radius: 999px;
  border: none; cursor: pointer; font-size: 14px; font-weight: 700;
  text-decoration: none; transition: all 0.3s ease; position: relative; overflow: hidden;
}
.btn-primary {
  background: linear-gradient(135deg, #00c9ab, #3d9fff);
  color: #07101f; box-shadow: 0 0 28px rgba(0,212,180,0.3);
}
.btn-primary:hover { transform: translateY(-2px) scale(1.02); box-shadow: 0 0 42px rgba(0,212,180,0.45); }

.btn-shimmer {
  position: absolute; top: 0; left: -100%; width: 55%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.28), transparent);
  transform: skewX(-18deg); animation: shimmer 3.5s ease-in-out infinite;
}
@keyframes shimmer { 0% { left:-100%; } 50%,100% { left:160%; } }
</style>
