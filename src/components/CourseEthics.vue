<template>
  <div class="course-ethics">

    <!-- ① 情景模拟 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-violet">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">情景模拟</h2>
          <p class="sec-sub">遇到医疗 AI 隐私问题时，您会如何应对？</p>
        </div>
      </div>

      <div class="scenario-panel">
        <div class="scenario-context">
          <div class="scenario-badge">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            情景题
          </div>
          <p class="scenario-q">你在使用一款医疗健康 App 时，发现它在未明确告知的情况下，请求读取通讯录、位置和麦克风权限，与诊疗功能明显不符。你应该如何处理？</p>
        </div>

        <div class="scenario-options" v-if="!scenarioAnswered">
          <div
            class="scenario-opt"
            v-for="(opt, i) in scenarioOptions"
            :key="i"
            :class="{ selected: scenarioSelected === i }"
            @click="scenarioSelected = i"
          >
            <div class="opt-circle">{{ String.fromCharCode(65 + i) }}</div>
            <span>{{ opt.text }}</span>
          </div>
          <button class="act-btn act-btn-primary" :disabled="scenarioSelected === null" @click="submitScenario">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
            提交判断
          </button>
        </div>

        <Transition name="fade-up">
          <div class="scenario-result" v-if="scenarioAnswered">
            <div class="result-header" :class="scenarioSelected === 0 ? 'rh-correct' : 'rh-wrong'">
              <svg v-if="scenarioSelected === 0" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
              <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              {{ scenarioSelected === 0 ? '判断正确！' : '再想一想' }}
            </div>
            <p class="result-explain">正确应对方式：<strong>立即拒绝权限并向相关部门投诉</strong>。医疗 App 收集的权限必须与诊疗功能直接相关，过度收集属于违规行为。您可以通过 12321 网络不良与垃圾信息举报受理中心或国家卫健委官方渠道进行举报。</p>
            <div class="result-actions">
              <button class="act-btn act-btn-ghost" @click="resetScenario">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.86"/></svg>
                重新作答
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </section>

    <!-- ② 隐私检查清单 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-green">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">隐私检查清单</h2>
          <p class="sec-sub">使用医疗 AI 前，用这 5 条标准评估它是否合规可信</p>
        </div>
        <div class="checklist-progress">
          <div class="cp-ring">
            <svg width="36" height="36" viewBox="0 0 36 36">
              <circle cx="18" cy="18" r="14" fill="none" stroke="rgba(255,255,255,0.08)" stroke-width="3"/>
              <circle cx="18" cy="18" r="14" fill="none" stroke="#00d4b4" stroke-width="3"
                stroke-linecap="round" stroke-dasharray="88"
                :stroke-dashoffset="88 - (checkedCount / checklist.length) * 88"
                transform="rotate(-90 18 18)" style="transition:stroke-dashoffset .6s ease"/>
            </svg>
            <div class="cp-label">{{ checkedCount }}/{{ checklist.length }}</div>
          </div>
        </div>
      </div>

      <div class="checklist-grid">
        <label
          class="cl-item"
          v-for="(item, i) in checklist" :key="i"
          :class="{ checked: item.checked }"
        >
          <input type="checkbox" v-model="item.checked">
          <div class="cl-box">
            <svg v-if="item.checked" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
          </div>
          <div class="cl-body">
            <div class="cl-title">{{ item.title }}</div>
            <div class="cl-desc">{{ item.desc }}</div>
          </div>
          <div class="cl-tag" :class="item.checked ? 'ct-done' : 'ct-todo'">
            {{ item.checked ? '已确认' : '待核查' }}
          </div>
        </label>
      </div>

      <Transition name="fade-up">
        <div class="checklist-complete" v-if="checkedCount === checklist.length">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/></svg>
          全部确认完毕，您已掌握 AI 隐私合规核心标准
        </div>
      </Transition>
    </section>

    <!-- ③ 法律法规科普 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-amber">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">法律法规科普</h2>
          <p class="sec-sub">了解保护您合法权益的三项核心法规</p>
        </div>
      </div>

      <div class="laws-list">
        <div class="law-card" v-for="(law, i) in laws" :key="i">
          <div class="law-index">{{ String(i + 1).padStart(2,'0') }}</div>
          <div class="law-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="law.svgPath"></svg>
          </div>
          <div class="law-body">
            <div class="law-title">{{ law.title }}</div>
            <div class="law-desc">{{ law.desc }}</div>
            <div class="law-key" v-if="law.key">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
              {{ law.key }}
            </div>
          </div>
          <div class="law-effect-badge">{{ law.effect }}</div>
        </div>
      </div>
    </section>

    <!-- ④ 投诉与维权 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-red">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.64 13a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.55 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l.92-.92a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 17z"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">投诉与维权</h2>
          <p class="sec-sub">发现隐私问题时，这三条路径可以有效维护您的合法权益</p>
        </div>
      </div>

      <div class="help-grid">
        <div class="help-card" v-for="(help, i) in helpList" :key="i">
          <div class="help-glyph" :class="help.gc">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="help.svgPath"></svg>
          </div>
          <div class="help-title">{{ help.title }}</div>
          <div class="help-desc">{{ help.desc }}</div>
          <div class="help-steps">
            <div class="hs-item" v-for="step in help.steps" :key="step">
              <svg width="9" height="9" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
              {{ step }}
            </div>
          </div>
          <div class="help-cta">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
            {{ help.cta }}
          </div>
        </div>
      </div>
    </section>

    <!-- 完成 -->
    <div class="complete-row">
      <button class="complete-btn" @click="completeCourse">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
        完成本章节学习
        <span class="btn-shimmer"></span>
      </button>
    </div>

  </div>
</template>

<script>
export default {
  name: 'CourseEthics',
  props: ['course'],
  emits: ['complete'],
  data() {
    return {
      scenarioSelected: null,
      scenarioAnswered: false,
      scenarioOptions: [
        { text: '立即拒绝权限并向相关部门投诉举报' },
        { text: '觉得无所谓，继续使用方便就好' },
        { text: '卸载 App 但不采取任何投诉行动' },
        { text: '在 App 内联系客服让其解释' },
      ],
      checklist: [
        { title: '有明确的隐私政策',   desc: '隐私政策应清晰说明收集哪些数据、如何使用、存储多久。', checked: false },
        { title: '只收集必要信息',    desc: '所请求的权限应与诊疗功能直接相关，不存在过度收集。',     checked: false },
        { title: '提供数据删除选项', desc: '用户可在账户设置中申请删除个人数据记录。',               checked: false },
        { title: '用户有权拒绝 AI 参与', desc: '不应将 AI 参与诊疗设为强制，应尊重患者自主选择权。', checked: false },
        { title: '有正规的投诉渠道',  desc: '平台须提供可追溯的投诉入口，且应在法定时限内响应。',     checked: false },
      ],
      laws: [
        {
          title: '个人信息保护法',
          desc: '2021 年正式施行，明确规定个人信息处理须遵循"合法、正当、必要"原则，医疗数据属于敏感个人信息，受到更高级别保护。',
          key: '敏感数据须单独授权，不得强制捆绑同意',
          effect: '2021年施行',
          svgPath: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/>'
        },
        {
          title: '医疗数据安全管理规定',
          desc: '要求医疗机构及相关平台对患者数据实施分级保护，电子病历、基因数据等属于最高保护等级，未经患者明确授权不得用于商业目的。',
          key: '电子病历不得未经授权用于商业分析',
          effect: '核心条款',
          svgPath: '<rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>'
        },
        {
          title: '患者权利告知义务',
          desc: '医疗机构在使用 AI 辅助诊疗前，须以患者可理解的方式进行知情告知，患者有权明确拒绝 AI 参与自己的诊疗决策，且不应因此受到差别对待。',
          key: '拒绝 AI 参与诊疗是受法律保护的权利',
          effect: '患者权利',
          svgPath: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>'
        },
      ],
      helpList: [
        {
          title: '官方投诉渠道',
          desc: '通过国家级监管部门进行正式投诉，适用于严重违规行为。',
          steps: ['收集证据截图', '登录 12321 举报平台', '填写投诉详情并提交'],
          cta: '12321 举报受理',
          gc: 'hg-blue',
          svgPath: '<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.64 13a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.55 2h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l.92-.92a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 17z"/>'
        },
        {
          title: '法律维权途径',
          desc: '通过法律手段维护个人权益，适用于造成实际损失的情况。',
          steps: ['咨询专业律师', '申请数据访问权', '必要时提起诉讼'],
          cta: '了解法律援助',
          gc: 'hg-amber',
          svgPath: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>'
        },
        {
          title: '患者知情权确认',
          desc: '主动向医疗机构确认 AI 参与诊疗的方式，并行使选择权。',
          steps: ['就诊前询问 AI 使用情况', '要求获取书面知情同意书', '明确表达拒绝意愿'],
          cta: '行使知情权',
          gc: 'hg-green',
          svgPath: '<polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>'
        },
      ]
    }
  },
  computed: {
    checkedCount() { return this.checklist.filter(i => i.checked).length }
  },
  methods: {
    submitScenario() {
      if (this.scenarioSelected === null) return
      this.scenarioAnswered = true
    },
    resetScenario() {
      this.scenarioSelected = null
      this.scenarioAnswered = false
    },
    completeCourse() { this.$emit('complete') }
  }
}
</script>

<style scoped>
.course-ethics {
  --bg:rgba(255,255,255,0.03); --bg2:rgba(255,255,255,0.055); --bg3:rgba(255,255,255,0.09);
  --bd:rgba(255,255,255,0.07); --bd2:rgba(255,255,255,0.12);
  --cyan:#00d4b4; --cyan-d:rgba(0,212,180,0.10);
  --blue:#4a9eff; --blue-d:rgba(74,158,255,0.10);
  --violet:#a78bfa; --violet-d:rgba(167,139,250,0.10);
  --amber:#f59e0b; --amber-d:rgba(245,158,11,0.10);
  --green:#10b981; --green-d:rgba(16,185,129,0.10);
  --red:#f87171; --red-d:rgba(248,113,113,0.10);
  --t1:#eef4ff; --t2:#8fa3c0; --t3:#4a5f7a;
  --r:12px; --rl:20px; --rx:24px;
  display:flex; flex-direction:column; gap:14px;
  font-family:'SF Pro Display','PingFang SC','HarmonyOS Sans SC',system-ui,sans-serif;
}
.sec-card { padding:20px 22px; border-radius:var(--rx); border:1px solid var(--bd); background:var(--bg2); position:relative; overflow:hidden; }
.sec-card::before { content:''; position:absolute; top:0; left:0; right:0; height:1px; background:linear-gradient(90deg,transparent,rgba(0,212,180,0.2),transparent); }
.sec-hd { display:flex; align-items:flex-start; gap:11px; margin-bottom:18px; }
.sec-icon { width:33px; height:33px; border-radius:9px; flex-shrink:0; display:flex; align-items:center; justify-content:center; }
.ic-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.ic-green  { background:var(--green-d);  border:1px solid rgba(16,185,129,0.2);  color:var(--green); }
.ic-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.ic-red    { background:var(--red-d);    border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.sec-title { font-size:14px; font-weight:600; color:var(--t1); margin-bottom:2px; }
.sec-sub   { font-size:11px; color:var(--t3); }

/* scenario */
.scenario-panel { display:flex; flex-direction:column; gap:14px; }
.scenario-context { padding:16px; border-radius:var(--rl); border:1px solid rgba(167,139,250,0.18); background:rgba(167,139,250,0.06); display:flex; flex-direction:column; gap:10px; }
.scenario-badge { display:inline-flex; align-items:center; gap:5px; width:fit-content; padding:4px 10px; border-radius:999px; border:1px solid rgba(167,139,250,0.25); background:var(--violet-d); font-size:10px; font-weight:700; color:var(--violet); }
.scenario-q { font-size:13px; color:var(--t1); line-height:1.75; font-weight:500; }
.scenario-options { display:flex; flex-direction:column; gap:8px; }
.scenario-opt {
  display:flex; align-items:center; gap:10px; padding:10px 13px;
  border-radius:var(--r); border:1px solid var(--bd); background:var(--bg);
  cursor:pointer; transition:all .2s ease; font-size:12px; color:var(--t2);
}
.scenario-opt:hover { border-color:var(--bd2); color:var(--t1); }
.scenario-opt.selected { border-color:rgba(167,139,250,0.3); background:var(--violet-d); color:var(--t1); }
.opt-circle {
  width:22px; height:22px; border-radius:50%; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
  border:1px solid var(--bd2); background:var(--bg3);
  font-size:10px; font-weight:700; color:var(--t3);
}
.scenario-opt.selected .opt-circle { background:var(--violet-d); border-color:rgba(167,139,250,0.3); color:var(--violet); }

.fade-up-enter-active,.fade-up-leave-active { transition:all .28s ease; }
.fade-up-enter-from,.fade-up-leave-to { opacity:0; transform:translateY(-6px); }

.scenario-result { padding:14px; border-radius:var(--rl); border:1px solid var(--bd); background:var(--bg); display:flex; flex-direction:column; gap:10px; }
.result-header { display:flex; align-items:center; gap:7px; font-size:12px; font-weight:700; padding-bottom:10px; border-bottom:1px solid var(--bd); }
.rh-correct { color:var(--green); }
.rh-wrong   { color:var(--red); }
.result-explain { font-size:12px; color:var(--t2); line-height:1.75; }
.result-explain strong { color:var(--t1); font-weight:600; }
.result-actions { display:flex; }

/* buttons */
.act-btn { display:inline-flex; align-items:center; justify-content:center; gap:5px; height:34px; padding:0 14px; border-radius:999px; cursor:pointer; font-size:12px; font-weight:600; transition:all .2s ease; }
.act-btn-primary { background:linear-gradient(135deg,#00c9ab,#3d9fff); border:none; color:#07101f; box-shadow:0 0 14px rgba(0,212,180,0.2); }
.act-btn-primary:hover { box-shadow:0 0 24px rgba(0,212,180,0.35); }
.act-btn-primary:disabled { opacity:.4; cursor:not-allowed; box-shadow:none; }
.act-btn-ghost { background:var(--bg2); border:1px solid var(--bd2); color:var(--t2); }
.act-btn-ghost:hover { color:var(--t1); }

/* checklist */
.checklist-progress { margin-left:auto; flex-shrink:0; position:relative; }
.cp-ring { position:relative; }
.cp-label { position:absolute; inset:0; display:flex; align-items:center; justify-content:center; font-size:10px; font-weight:700; color:var(--cyan); }
.checklist-grid { display:flex; flex-direction:column; gap:8px; }
.cl-item { display:flex; align-items:flex-start; gap:11px; padding:12px 14px; border-radius:var(--r); border:1px solid var(--bd); background:var(--bg); cursor:pointer; transition:all .2s ease; }
.cl-item:hover { border-color:var(--bd2); }
.cl-item.checked { border-color:rgba(16,185,129,0.2); background:rgba(16,185,129,0.04); }
.cl-item input { display:none; }
.cl-box { width:16px; height:16px; border-radius:4px; flex-shrink:0; border:1px solid var(--bd2); display:flex; align-items:center; justify-content:center; margin-top:1px; transition:all .2s; }
.cl-item.checked .cl-box { background:var(--green); border-color:var(--green); color:#fff; }
.cl-body { flex:1; }
.cl-title { font-size:12px; font-weight:600; color:var(--t1); margin-bottom:2px; }
.cl-desc  { font-size:11px; color:var(--t2); line-height:1.55; }
.cl-tag { height:20px; padding:0 9px; border-radius:999px; flex-shrink:0; font-size:9px; font-weight:700; display:flex; align-items:center; }
.ct-todo { background:var(--bg3); border:1px solid var(--bd2); color:var(--t3); }
.ct-done { background:rgba(16,185,129,0.1); border:1px solid rgba(16,185,129,0.2); color:var(--green); }
.checklist-complete { display:flex; align-items:center; gap:7px; padding:10px 13px; border-radius:var(--r); border:1px solid rgba(16,185,129,0.22); background:rgba(16,185,129,0.07); font-size:12px; font-weight:600; color:var(--green); margin-top:4px; }

/* laws */
.laws-list { display:flex; flex-direction:column; gap:10px; }
.law-card { display:grid; grid-template-columns:auto auto 1fr auto; align-items:flex-start; gap:12px; padding:14px 16px; border-radius:var(--rl); border:1px solid var(--bd); background:var(--bg); transition:border-color .22s; }
.law-card:hover { border-color:rgba(245,158,11,0.2); }
.law-index { font-size:22px; font-weight:800; color:rgba(245,158,11,0.15); line-height:1; margin-top:2px; width:28px; }
.law-icon { width:34px; height:34px; border-radius:9px; flex-shrink:0; display:flex; align-items:center; justify-content:center; background:var(--amber-d); border:1px solid rgba(245,158,11,0.2); color:var(--amber); }
.law-body { min-width:0; }
.law-title { font-size:13px; font-weight:600; color:var(--t1); margin-bottom:5px; }
.law-desc  { font-size:11px; color:var(--t2); line-height:1.7; margin-bottom:7px; }
.law-key { display:flex; align-items:center; gap:5px; font-size:10px; color:rgba(245,158,11,0.75); font-weight:500; }
.law-key svg { flex-shrink:0; color:var(--amber); }
.law-effect-badge { height:22px; padding:0 9px; border-radius:999px; flex-shrink:0; background:var(--amber-d); border:1px solid rgba(245,158,11,0.22); font-size:9px; font-weight:700; color:var(--amber); display:flex; align-items:center; white-space:nowrap; }

/* help */
.help-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; }
.help-card { padding:16px; border-radius:var(--rl); border:1px solid var(--bd); background:var(--bg); display:flex; flex-direction:column; gap:9px; transition:border-color .22s; }
.help-card:hover { border-color:var(--bd2); }
.help-glyph { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; }
.hg-blue  { background:var(--blue-d);  border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.hg-amber { background:var(--amber-d); border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.hg-green { background:var(--green-d); border:1px solid rgba(16,185,129,0.2);  color:var(--green); }
.help-title { font-size:13px; font-weight:600; color:var(--t1); }
.help-desc  { font-size:11px; color:var(--t2); line-height:1.6; }
.help-steps { display:flex; flex-direction:column; gap:5px; padding:10px; border-radius:var(--r); background:var(--bg3); }
.hs-item { display:flex; align-items:center; gap:5px; font-size:10px; color:var(--t2); }
.hs-item svg { flex-shrink:0; color:var(--green); }
.help-cta { display:flex; align-items:center; gap:5px; font-size:11px; font-weight:600; color:var(--cyan); }

/* complete */
.complete-row { text-align:center; padding-top:4px; }
.complete-btn { display:inline-flex; align-items:center; justify-content:center; gap:8px; padding:0 32px; height:48px; border-radius:999px; border:none; cursor:pointer; font-size:14px; font-weight:700; background:linear-gradient(135deg,#00c9ab,#3d9fff); color:#07101f; box-shadow:0 0 28px rgba(0,212,180,0.28); transition:all .3s ease; position:relative; overflow:hidden; }
.complete-btn:hover { transform:translateY(-2px); box-shadow:0 0 42px rgba(0,212,180,0.44); }
.btn-shimmer { position:absolute; top:0; left:-100%; width:55%; height:100%; background:linear-gradient(90deg,transparent,rgba(255,255,255,0.28),transparent); transform:skewX(-18deg); animation:shimmer 3.5s ease-in-out infinite; }
@keyframes shimmer { 0%{left:-100%} 50%,100%{left:160%} }

@media (max-width:860px) { .help-grid { grid-template-columns:1fr; } }
@media (max-width:700px) { .law-card { grid-template-columns:auto 1fr; } .law-index,.law-effect-badge { display:none; } }
</style>