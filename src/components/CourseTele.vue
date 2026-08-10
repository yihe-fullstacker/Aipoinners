<template>
  <div class="course-content">

    <!-- ── 操作模拟 ── -->
    <div class="content-section">
      <div class="sec-hd">
        <div class="sec-icon c-teal">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">线上问诊流程模拟</h2>
          <p class="sec-sub">完整体验预约 → 视频问诊 → 开药 → 随访的远程医疗闭环</p>
        </div>
      </div>

      <div class="flow-track">
        <div
          class="flow-step"
          v-for="(step, i) in flowSteps" :key="i"
          :class="{ active: activeStep === i, done: i < activeStep }"
          @click="activeStep = i"
        >
          <div class="flow-node">
            <svg v-if="i < activeStep" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            <span v-else>{{ i + 1 }}</span>
          </div>
          <div class="flow-connector" v-if="i < flowSteps.length - 1"></div>
          <div class="flow-label">{{ step.label }}</div>
        </div>
      </div>

      <div class="step-panel">
        <div class="step-panel-hd">
          <div class="step-panel-icon">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="flowSteps[activeStep].svgPath"></svg>
          </div>
          <div>
            <div class="step-panel-title">{{ flowSteps[activeStep].label }}</div>
            <div class="step-panel-desc">{{ flowSteps[activeStep].desc }}</div>
          </div>
        </div>
        <ul class="step-tips">
          <li v-for="tip in flowSteps[activeStep].tips" :key="tip">
            <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            {{ tip }}
          </li>
        </ul>
        <div class="step-actions">
          <button class="step-btn" :disabled="activeStep === 0" @click="activeStep--">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5"/><path d="m12 5-7 7 7 7"/></svg>
            上一步
          </button>
          <button class="step-btn step-btn-primary" :disabled="activeStep === flowSteps.length - 1" @click="activeStep++">
            下一步
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
          </button>
        </div>
      </div>
    </div>

    <!-- ── 设备演示 ── -->
    <div class="content-section">
      <div class="sec-hd">
        <div class="sec-icon c-blue">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <rect x="5" y="2" width="14" height="20" rx="2"/><path d="M12 18h.01"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">远程监测设备</h2>
          <p class="sec-sub">居家常用监测设备与数据同步方式</p>
        </div>
      </div>
      <div class="device-list">
        <div class="device-row" v-for="device in devices" :key="device.name">
          <div class="device-glyph" :class="device.color">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="device.svgPath"></svg>
          </div>
          <div class="device-info">
            <div class="device-name">{{ device.name }}</div>
            <div class="device-desc">{{ device.desc }}</div>
          </div>
          <div class="device-badge">{{ device.badge }}</div>
        </div>
      </div>
    </div>

    <!-- ── 场景对比 ── -->
    <div class="content-section">
      <div class="sec-hd">
        <div class="sec-icon c-violet">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 3H5a2 2 0 0 0-2 2v4m6-6h10a2 2 0 0 1 2 2v4M9 3v18m0 0h10a2 2 0 0 0 2-2v-4M9 21H5a2 2 0 0 1-2-2v-4m0 0h18"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">适用场景对照</h2>
          <p class="sec-sub">了解远程医疗的边界，做出正确的就医选择</p>
        </div>
      </div>
      <div class="scene-grid">
        <div class="scene-col">
          <div class="scene-label scene-yes">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            适合远程
          </div>
          <div class="scene-items">
            <div class="scene-item scene-item-yes" v-for="s in scenesYes" :key="s">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
              {{ s }}
            </div>
          </div>
        </div>
        <div class="scene-divider">
          <div class="scene-divider-bar"></div>
          <div class="scene-vs">VS</div>
          <div class="scene-divider-bar"></div>
        </div>
        <div class="scene-col">
          <div class="scene-label scene-no">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            建议线下
          </div>
          <div class="scene-items">
            <div class="scene-item scene-item-no" v-for="s in scenesNo" :key="s">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              {{ s }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 注意事项 ── -->
    <div class="content-section">
      <div class="sec-hd">
        <div class="sec-icon c-amber">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">使用注意事项</h2>
          <p class="sec-sub">保障您的远程就医安全与合法权益</p>
        </div>
      </div>
      <div class="tips-list">
        <div class="tip-row" :class="tip.ok ? 'tip-ok' : 'tip-warn'" v-for="(tip, i) in tips" :key="i">
          <div class="tip-dot">
            <svg v-if="tip.ok" width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            <svg v-else width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/></svg>
          </div>
          <span>{{ tip.text }}</span>
        </div>
      </div>
    </div>

    <!-- ── 完成 ── -->
    <div class="complete-wrap">
      <button class="complete-btn" @click="completeCourse">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="20 6 9 17 4 12"/></svg>
        完成本章节学习
        <span class="shimmer"></span>
      </button>
    </div>

  </div>
</template>

<script>
export default {
  name: 'CourseTele',
  props: ['course'],
  emits: ['complete'],
  data() {
    return {
      activeStep: 0,
      flowSteps: [
        {
          label: '在线预约',
          desc: '选择科室、医生与时间段，填写主诉和基本健康信息。',
          tips: ['提前准备好身份证与医保卡信息', '症状描述尽量具体，包含起始时间和变化情况'],
          svgPath: '<rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/>'
        },
        {
          label: '视频问诊',
          desc: '通过加密视频通话接诊医生，展示症状并提交检查资料。',
          tips: ['确保网络稳定、光线充足', '提前上传既往检查报告或当前用药清单'],
          svgPath: '<path d="M15 10l4.553-2.069A1 1 0 0 1 21 8.82v6.36a1 1 0 0 1-1.447.89L15 14"/><rect x="1" y="6" width="15" height="12" rx="2"/>'
        },
        {
          label: '电子处方',
          desc: '医生在线出具电子处方，可配送到家或前往药房自取。',
          tips: ['仔细核对药名、剂量与用法说明', '对处方有疑问可通过平台留言咨询'],
          svgPath: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="9" y1="13" x2="15" y2="13"/><line x1="9" y1="17" x2="15" y2="17"/>'
        },
        {
          label: '智能随访',
          desc: '系统推送健康问卷与复诊提醒，医生持续跟踪恢复情况。',
          tips: ['按时完成随访问卷以获得更精准的健康建议', '若症状加重，应立即转为线下就医'],
          svgPath: '<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.64 13a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.55 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l.92-.92a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 17z"/>'
        }
      ],
      devices: [
        { name: '电子血压计', desc: '蓝牙连接后自动上传血压数据，医生可实时查看趋势', badge: '已认证', color: 'g-rose', svgPath: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>' },
        { name: '血糖仪', desc: '检测后同步至健康档案，支持趋势图和异常预警', badge: '已认证', color: 'g-amber', svgPath: '<path d="M9 3H5a2 2 0 0 0-2 2v4m6-6h10a2 2 0 0 1 2 2v4M9 3v18m0 0h10a2 2 0 0 0 2-2v-4M9 21H5a2 2 0 0 1-2-2v-4m0 0h18"/>' },
        { name: '心电监测贴片', desc: '持续 24h 心电记录，AI 自动识别异常心律', badge: '处方级', color: 'g-blue', svgPath: '<polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>' }
      ],
      scenesYes: ['慢病复诊与药物续开', '检查报告解读咨询', '轻症感冒等常见病', '术后恢复期跟踪', '心理健康初步咨询'],
      scenesNo:  ['初次诊断不明症状', '急性胸痛或呼吸困难', '需要体格检查的情况', '需抽血或影像检查', '儿童急性高热'],
      tips: [
        { ok: true,  text: '慢病复诊和轻症问诊最适合使用远程医疗' },
        { ok: false, text: '初诊建议优先前往线下医院进行全面评估' },
        { ok: false, text: '遇到急症请立即拨打 120 或前往急诊' },
        { ok: true,  text: '远程问诊记录可作为线下就诊的重要参考' },
        { ok: false, text: '远程医疗不能替代体格检查与面诊判断' }
      ]
    }
  },
  methods: {
    completeCourse() { this.$emit('complete') }
  }
}
</script>

<style scoped>
.course-content {
  --bg: rgba(255,255,255,0.03);
  --bg2: rgba(255,255,255,0.055);
  --bg3: rgba(255,255,255,0.09);
  --bd: rgba(255,255,255,0.07);
  --bd2: rgba(255,255,255,0.11);
  --cyan: #00d4b4;  --cyan-d: rgba(0,212,180,0.09);
  --blue: #4a9eff;  --blue-d: rgba(74,158,255,0.09);
  --violet: #a78bfa; --violet-d: rgba(167,139,250,0.09);
  --amber: #f59e0b; --amber-d: rgba(245,158,11,0.09);
  --green: #10b981; --green-d: rgba(16,185,129,0.09);
  --red: #f87171;   --red-d: rgba(248,113,113,0.09);
  --t1:#eef4ff; --t2:#8fa3c0; --t3:#4a5f7a;
  --r:12px; --rl:20px; --rx:24px;
  display:flex; flex-direction:column; gap:14px;
  font-family:'SF Pro Display','PingFang SC','HarmonyOS Sans SC',system-ui,sans-serif;
}

/* Section */
.content-section {
  padding:20px 22px; border-radius:var(--rx);
  border:1px solid var(--bd); background:var(--bg2);
  position:relative; overflow:hidden;
}
.content-section::before {
  content:''; position:absolute; top:0; left:0; right:0; height:1px;
  background:linear-gradient(90deg,transparent,rgba(0,212,180,0.18),transparent);
}
.sec-hd { display:flex; align-items:flex-start; gap:11px; margin-bottom:18px; }
.sec-icon {
  width:33px; height:33px; border-radius:9px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
}
.c-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.c-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.c-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.c-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.sec-title { font-size:14px; font-weight:600; color:var(--t1); margin-bottom:2px; }
.sec-sub   { font-size:11px; color:var(--t3); line-height:1.5; }

/* Flow stepper */
.flow-track {
  display:flex; align-items:flex-start; margin-bottom:16px;
  overflow-x:auto; padding-bottom:2px;
}
.flow-step {
  display:flex; flex-direction:column; align-items:center;
  gap:6px; flex:1; min-width:72px; position:relative; cursor:pointer;
}
.flow-node {
  width:30px; height:30px; border-radius:50%; z-index:1;
  display:flex; align-items:center; justify-content:center;
  font-size:11px; font-weight:700;
  border:1px solid var(--bd2); background:var(--bg2); color:var(--t3);
  transition:all 0.25s ease;
}
.flow-step.active .flow-node {
  background:linear-gradient(135deg,#00c9ab,#3d9fff);
  border-color:transparent; color:#07101f;
  box-shadow:0 0 14px rgba(0,212,180,0.3);
}
.flow-step.done .flow-node {
  background:var(--cyan-d); border-color:rgba(0,212,180,0.28); color:var(--cyan);
}
.flow-connector {
  position:absolute; top:15px; left:calc(50% + 15px);
  width:calc(100% - 30px); height:1px; background:var(--bd2);
}
.flow-step.done .flow-connector { background:rgba(0,212,180,0.3); }
.flow-label { font-size:10px; color:var(--t2); text-align:center; white-space:nowrap; }
.flow-step.active .flow-label { color:var(--cyan); font-weight:600; }

.step-panel {
  padding:16px; border-radius:var(--rl);
  border:1px solid rgba(0,212,180,0.15); background:var(--cyan-d);
}
.step-panel-hd { display:flex; align-items:flex-start; gap:11px; margin-bottom:12px; }
.step-panel-icon {
  width:36px; height:36px; border-radius:10px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
  background:rgba(0,212,180,0.12); border:1px solid rgba(0,212,180,0.2); color:var(--cyan);
}
.step-panel-title { font-size:13px; font-weight:600; color:var(--t1); margin-bottom:3px; }
.step-panel-desc  { font-size:12px; color:var(--t2); line-height:1.65; }
.step-tips { list-style:none; display:flex; flex-direction:column; gap:6px; margin-bottom:14px; }
.step-tips li { display:flex; align-items:center; gap:6px; font-size:11px; color:rgba(0,212,180,0.75); }
.step-tips li svg { flex-shrink:0; color:var(--cyan); }
.step-actions { display:flex; gap:8px; justify-content:flex-end; }
.step-btn {
  display:inline-flex; align-items:center; gap:5px;
  height:32px; padding:0 14px; border-radius:999px;
  border:1px solid var(--bd2); background:var(--bg2);
  color:var(--t2); font-size:12px; font-weight:500; cursor:pointer; transition:all 0.2s ease;
}
.step-btn:disabled { opacity:.35; cursor:not-allowed; }
.step-btn:not(:disabled):hover { color:var(--t1); }
.step-btn-primary {
  background:linear-gradient(135deg,#00c9ab,#3d9fff); border-color:transparent;
  color:#07101f; font-weight:700;
}
.step-btn-primary:not(:disabled):hover { box-shadow:0 0 16px rgba(0,212,180,0.28); }

/* Device */
.device-list { display:flex; flex-direction:column; gap:9px; }
.device-row {
  display:flex; align-items:center; gap:13px; padding:13px 15px;
  border-radius:var(--r); border:1px solid var(--bd); background:var(--bg);
  transition:border-color 0.2s;
}
.device-row:hover { border-color:var(--bd2); }
.device-glyph {
  width:40px; height:40px; border-radius:11px; flex-shrink:0;
  display:flex; align-items:center; justify-content:center;
}
.g-rose  { background:var(--red-d);   border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.g-amber { background:var(--amber-d); border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.g-blue  { background:var(--blue-d);  border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.device-info { flex:1; min-width:0; }
.device-name { font-size:13px; font-weight:600; color:var(--t1); margin-bottom:3px; }
.device-desc { font-size:11px; color:var(--t2); line-height:1.55; }
.device-badge {
  height:22px; padding:0 9px; border-radius:999px; flex-shrink:0;
  border:1px solid rgba(0,212,180,0.22); background:var(--cyan-d);
  font-size:10px; font-weight:600; color:var(--cyan);
  display:flex; align-items:center;
}

/* Scene compare */
.scene-grid { display:grid; grid-template-columns:1fr auto 1fr; gap:14px; align-items:start; }
.scene-label {
  display:inline-flex; align-items:center; gap:5px;
  height:26px; padding:0 10px; border-radius:999px;
  font-size:10px; font-weight:700; margin-bottom:10px;
}
.scene-yes { background:var(--green-d); border:1px solid rgba(16,185,129,0.2); color:var(--green); }
.scene-no  { background:var(--red-d);   border:1px solid rgba(248,113,113,0.2); color:var(--red); }
.scene-items { display:flex; flex-direction:column; gap:7px; }
.scene-item {
  display:flex; align-items:center; gap:7px; padding:8px 10px;
  border-radius:var(--r); font-size:12px; color:var(--t2);
}
.scene-item-yes { background:rgba(16,185,129,0.05); border:1px solid rgba(16,185,129,0.1); }
.scene-item-yes svg { color:var(--green); flex-shrink:0; }
.scene-item-no  { background:rgba(248,113,113,0.05); border:1px solid rgba(248,113,113,0.1); }
.scene-item-no  svg { color:var(--red); flex-shrink:0; }
.scene-divider { display:flex; flex-direction:column; align-items:center; gap:8px; padding-top:38px; }
.scene-divider-bar { width:1px; flex:1; background:var(--bd2); }
.scene-vs {
  width:28px; height:28px; border-radius:50%;
  display:flex; align-items:center; justify-content:center;
  border:1px solid var(--bd2); background:var(--bg3);
  font-size:9px; font-weight:700; color:var(--t3);
}

/* Tips */
.tips-list { display:flex; flex-direction:column; gap:8px; }
.tip-row {
  display:flex; align-items:center; gap:10px; padding:10px 13px;
  border-radius:var(--r); font-size:12px; font-weight:500;
}
.tip-ok   { background:rgba(16,185,129,0.06); border:1px solid rgba(16,185,129,0.15); color:rgba(110,231,183,0.85); }
.tip-warn { background:rgba(245,158,11,0.06);  border:1px solid rgba(245,158,11,0.15);  color:rgba(253,186,116,0.85); }
.tip-dot { display:flex; align-items:center; flex-shrink:0; }
.tip-ok  .tip-dot { color:var(--green); }
.tip-warn .tip-dot { color:var(--amber); }

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

@media (max-width:600px) {
  .scene-grid { grid-template-columns:1fr; }
  .scene-divider { flex-direction:row; padding-top:0; }
  .scene-divider-bar { flex:1; height:1px; width:auto; }
}
</style>