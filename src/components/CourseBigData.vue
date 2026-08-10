<template>
  <div class="course-bd">

    <!-- ① 动态可视化 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-blue">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/><path d="m6 9 4-3 4 2 4-3"/>
          </svg>
        </div>
        <div class="sec-hd-text">
          <h2 class="sec-title">医疗大数据可视化分析</h2>
          <p class="sec-sub">实时模拟大数据系统如何发现疾病规律与预测趋势</p>
        </div>
        <div class="chart-tabs">
          <button class="ctab" :class="{ active: chartTab === 'bar' }" @click="switchChart('bar')">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/></svg>
            柱状图
          </button>
          <button class="ctab" :class="{ active: chartTab === 'line' }" @click="switchChart('line')">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
            趋势线
          </button>
          <button class="ctab" :class="{ active: chartTab === 'radar' }" @click="switchChart('radar')">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 22 8.5 22 15.5 12 22 2 15.5 2 8.5 12 2"/></svg>
            雷达图
          </button>
        </div>
      </div>

      <div class="viz-panel">
        <!-- 图表区 -->
        <div class="viz-chart">

          <!-- 柱状图 -->
          <Transition name="chart-fade" mode="out-in">
          <div v-if="chartTab === 'bar'" class="chart-wrap" key="bar">
            <div class="chart-title-row">
              <span class="chart-label">流感就诊指数 · 月度趋势</span>
              <span class="chart-unit">就诊指数</span>
            </div>
            <div class="bar-area">
              <div class="y-axis">
                <span>100</span><span>75</span><span>50</span><span>25</span>
              </div>
              <div class="bars-container">
                <div class="grid-lines">
                  <div class="grid-line" v-for="n in 4" :key="n"></div>
                </div>
                <div class="bars">
                  <div class="bar-col" v-for="(bar, i) in chartBars" :key="i">
                    <div class="bar-track">
                      <div class="bar-fill" :class="bar.peak ? 'bar-peak' : 'bar-normal'" :style="{ height: animatedBars[i] + '%' }">
                        <div class="bar-shine"></div>
                      </div>
                    </div>
                    <div class="bar-val-label">{{ animatedBars[i] > 0 ? bar.pct : '' }}</div>
                    <div class="bar-lbl">{{ bar.label }}</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="chart-legend">
              <div class="legend-item"><div class="legend-dot ld-peak"></div>峰值月份</div>
              <div class="legend-item"><div class="legend-dot ld-normal"></div>常规月份</div>
            </div>
          </div>
          </Transition>

          <!-- 趋势线图 -->
          <Transition name="chart-fade" mode="out-in">
          <div v-if="chartTab === 'line'" class="chart-wrap" key="line">
            <div class="chart-title-row">
              <span class="chart-label">AI 预测 vs 实际就诊量对比</span>
              <span class="chart-unit">千人次</span>
            </div>
            <div class="line-chart-area">
              <svg class="line-svg" viewBox="0 0 400 160" preserveAspectRatio="none">
                <defs>
                  <linearGradient id="ga" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#00d4b4" stop-opacity="0.28"/>
                    <stop offset="100%" stop-color="#00d4b4" stop-opacity="0"/>
                  </linearGradient>
                  <linearGradient id="gp" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#4a9eff" stop-opacity="0.2"/>
                    <stop offset="100%" stop-color="#4a9eff" stop-opacity="0"/>
                  </linearGradient>
                </defs>
                <line v-for="y in [32,64,96,128]" :key="y" x1="0" :y1="y" x2="400" :y2="y" stroke="rgba(255,255,255,0.05)" stroke-width="1"/>
                <path :d="actualAreaPath"  fill="url(#ga)"/>
                <path :d="predictAreaPath" fill="url(#gp)"/>
                <path :d="actualLinePath"  fill="none" stroke="#00d4b4" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="line-anim"/>
                <path :d="predictLinePath" fill="none" stroke="#4a9eff"  stroke-width="2"   stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="6 3" class="line-anim line-anim-delay"/>
                <circle v-for="(pt,i) in actualPoints" :key="'a'+i" :cx="pt.x" :cy="pt.y" r="3.5" fill="#00d4b4" stroke="#07101f" stroke-width="1.5" class="dot-pop"/>
              </svg>
              <div class="line-x-labels">
                <span v-for="m in lineMonths" :key="m">{{ m }}</span>
              </div>
            </div>
            <div class="chart-legend">
              <div class="legend-item"><div class="legend-line ll-actual"></div>实际就诊量</div>
              <div class="legend-item"><div class="legend-line ll-predict"></div>AI 预测值</div>
            </div>
          </div>
          </Transition>

          <!-- 雷达图 -->
          <Transition name="chart-fade" mode="out-in">
          <div v-if="chartTab === 'radar'" class="chart-wrap" key="radar">
            <div class="chart-title-row">
              <span class="chart-label">数据质量多维评估</span>
              <span class="chart-unit">评分 0–100</span>
            </div>
            <div class="radar-area">
              <svg class="radar-svg" viewBox="0 0 240 220">
                <defs>
                  <linearGradient id="rg" x1="0" y1="0" x2="1" y2="1">
                    <stop offset="0%" stop-color="#00d4b4" stop-opacity="0.35"/>
                    <stop offset="100%" stop-color="#4a9eff" stop-opacity="0.18"/>
                  </linearGradient>
                </defs>
                <polygon v-for="r in [20,40,60,80,100]" :key="r" :points="radarPolygon(r)" fill="none" stroke="rgba(255,255,255,0.07)" stroke-width="1"/>
                <line v-for="(ax,i) in radarAxes" :key="'axl'+i" :x1="120" :y1="110" :x2="radarPt(i,100).x" :y2="radarPt(i,100).y" stroke="rgba(255,255,255,0.09)" stroke-width="1"/>
                <polygon :points="radarDataPts" fill="url(#rg)" stroke="#00d4b4" stroke-width="2" class="radar-anim"/>
                <circle v-for="(ax,i) in radarAxes" :key="'rdp'+i" :cx="radarPt(i,ax.val).x" :cy="radarPt(i,ax.val).y" r="3.5" fill="#00d4b4" stroke="#07101f" stroke-width="1.5" class="dot-pop"/>
                <text v-for="(ax,i) in radarAxes" :key="'rl'+i" :x="radarLbl(i).x" :y="radarLbl(i).y" text-anchor="middle" dominant-baseline="middle" fill="#8fa3c0" font-size="9" font-family="system-ui">{{ ax.name }}</text>
              </svg>
              <div class="radar-scores">
                <div class="rs-row" v-for="ax in radarAxes" :key="ax.name">
                  <div class="rs-dot" :style="{ background: ax.color }"></div>
                  <span class="rs-name">{{ ax.name }}</span>
                  <div class="rs-bar"><div class="rs-fill" :style="{ width: ax.val + '%', background: ax.color }"></div></div>
                  <span class="rs-val">{{ ax.val }}</span>
                </div>
              </div>
            </div>
          </div>
          </Transition>
        </div>

        <!-- 右侧信息 -->
        <div class="viz-info">
          <div class="info-badge">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
            大数据疾病规律发现
          </div>
          <h3 class="info-title">AI 驱动的流感预测系统</h3>
          <p class="info-desc">通过持续分析数百万条匿名就诊记录、气象数据与搜索热词，提前 2～3 周预测区域流感爆发峰值，辅助医院提前备药备床。</p>
          <div class="info-stats">
            <div class="stat-row" v-for="s in chartStats" :key="s.label">
              <div class="stat-head">
                <span class="stat-val">{{ s.val }}</span>
                <span class="stat-lbl">{{ s.label }}</span>
              </div>
              <div class="stat-bar"><div class="stat-fill" :style="{ width: s.pct + '%' }"></div></div>
            </div>
          </div>
          <div class="info-tip">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            所有数据均为模拟演示，不代表真实医疗数据
          </div>
        </div>
      </div>
    </section>

    <!-- ② 患者受益案例 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-teal">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">患者受益案例</h2>
          <p class="sec-sub">大数据分析为患者带来的三类核心价值</p>
        </div>
      </div>
      <div class="benefit-list">
        <div class="benefit-row" v-for="(item, i) in benefits" :key="i">
          <div class="benefit-glyph" :class="item.gc">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="item.svgPath"></svg>
          </div>
          <div class="benefit-body">
            <div class="benefit-title">{{ item.title }}</div>
            <div class="benefit-desc">{{ item.desc }}</div>
          </div>
          <div class="benefit-metric">
            <div class="bm-val">{{ item.metric }}</div>
            <div class="bm-lbl">{{ item.metricLabel }}</div>
          </div>
          <div class="benefit-tag">{{ item.tag }}</div>
        </div>
      </div>
    </section>

    <!-- ③ 数据流转路径 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-violet">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="m2 17 10 5 10-5"/><path d="m2 12 10 5 10-5"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">数据流转路径</h2>
          <p class="sec-sub">从原始数据到决策支持的完整处理链路</p>
        </div>
      </div>
      <div class="pipeline">
        <div class="pipe-step" v-for="(step, i) in pipeline" :key="i">
          <div class="pipe-node" :class="step.gc">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="step.svgPath"></svg>
          </div>
          <div class="pipe-connector" v-if="i < pipeline.length - 1">
            <div class="pipe-line"></div>
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
            <div class="pipe-line"></div>
          </div>
          <div class="pipe-text">
            <div class="pipe-label">{{ step.label }}</div>
            <div class="pipe-sub">{{ step.sub }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- ④ 隐私保护说明 -->
    <section class="sec-card">
      <div class="sec-hd">
        <div class="sec-icon ic-green">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
        </div>
        <div>
          <h2 class="sec-title">隐私保护说明</h2>
          <p class="sec-sub">三重机制保障您的数据安全与知情权利</p>
        </div>
      </div>
      <div class="privacy-grid">
        <div class="privacy-card" v-for="(item, i) in privacy" :key="i">
          <div class="prv-num">{{ String(i + 1).padStart(2,'0') }}</div>
          <div class="prv-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="item.svgPath"></svg>
          </div>
          <div class="prv-title">{{ item.title }}</div>
          <div class="prv-desc">{{ item.desc }}</div>
          <div class="prv-badge">
            <svg width="9" height="9" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            已合规
          </div>
        </div>
      </div>
      <div class="compliance-note">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
        数据使用遵循国家医疗数据安全管理规范，所有分析结果均以匿名聚合形式呈现。
      </div>
    </section>

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
  name: 'CourseBigData',
  props: ['course'],
  emits: ['complete'],
  data() {
    return {
      chartTab: 'bar',
      animatedBars: [0, 0, 0, 0, 0, 0],
      chartBars: [
        { label: '一月', pct: 35, peak: false },
        { label: '二月', pct: 55, peak: false },
        { label: '三月', pct: 80, peak: true  },
        { label: '四月', pct: 65, peak: false },
        { label: '五月', pct: 42, peak: false },
        { label: '六月', pct: 28, peak: false },
      ],
      lineMonths: ['1月','2月','3月','4月','5月','6月'],
      actualData:  [28, 45, 72, 60, 38, 22],
      predictData: [30, 48, 75, 62, 40, 24],
      radarAxes: [
        { name: '完整性', val: 88, color: '#00d4b4' },
        { name: '准确性', val: 92, color: '#4a9eff' },
        { name: '时效性', val: 76, color: '#a78bfa' },
        { name: '一致性', val: 84, color: '#f59e0b' },
        { name: '安全性', val: 95, color: '#10b981' },
        { name: '可用性', val: 79, color: '#f87171' },
      ],
      chartStats: [
        { val: '2~3周', label: '预测提前量', pct: 80 },
        { val: '94%',   label: '预测准确率', pct: 94 },
        { val: '320万', label: '分析数据点', pct: 65 },
      ],
      benefits: [
        { title:'更多相似病例参考', desc:'医生可调用全网相似病历，辅助制定更精准的诊疗方案，降低误诊风险。', tag:'诊断辅助', metric:'↑38%', metricLabel:'诊断准确率', gc:'gc-blue',  svgPath:'<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>' },
        { title:'治疗效果趋势分析', desc:'大数据追踪治疗反应，持续优化用药剂量与方案组合，实现精准医疗。',   tag:'疗效优化', metric:'↓25%', metricLabel:'用药副作用',  gc:'gc-teal',  svgPath:'<path d="M4 19h16M7 16v-5M12 16V7M17 16v-8"/><path d="m6 9 4-3 4 2 4-3"/>' },
        { title:'疾病早期预警',     desc:'区域异常就诊数据触发预警，提前部署医疗资源，压缩应急响应时间。',     tag:'主动预防', metric:'72h',  metricLabel:'提前预警',    gc:'gc-amber', svgPath:'<path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>' },
      ],
      pipeline: [
        { label:'数据采集', sub:'匿名就诊记录', gc:'pn-blue',   svgPath:'<path d="M9 3H5a2 2 0 0 0-2 2v4m6-6h10a2 2 0 0 1 2 2v4M9 3v18m0 0h10a2 2 0 0 0 2-2v-4M9 21H5a2 2 0 0 1-2-2v-4m0 0h18"/>' },
        { label:'脱敏处理', sub:'去标识化',    gc:'pn-violet', svgPath:'<rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>' },
        { label:'AI 建模', sub:'机器学习',    gc:'pn-teal',   svgPath:'<rect x="5" y="5" width="14" height="14" rx="4"/><path d="M9 12h6M12 9v6"/><path d="M8 3v3M16 3v3M8 18v3M16 18v3"/>' },
        { label:'规律发现', sub:'模式识别',   gc:'pn-amber',  svgPath:'<circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>' },
        { label:'决策支持', sub:'临床辅助',   gc:'pn-green',  svgPath:'<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>' },
      ],
      privacy: [
        { title:'数据脱敏',  desc:'姓名、身份证等个人信息在入库前严格脱敏处理，无法识别到具体个人。',                svgPath:'<rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>' },
        { title:'匿名化处理', desc:'所有参与分析的数据均已完成去标识化，分析系统无法反向还原个人身份。',              svgPath:'<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M20 14v7M23 17h-6"/>' },
        { title:'知情权保障', desc:'您有权查阅自己的数据使用记录，并可随时申请停止参与数据共享计划。',                svgPath:'<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>' },
      ]
    }
  },
  computed: {
    actualPoints() {
      return this.actualData.map((v, i) => ({ x: i * 80, y: 148 - (v / 80 * 130) }))
    },
    actualLinePath() {
      return this.actualPoints.map((p, i) => `${i===0?'M':'L'}${p.x},${p.y}`).join(' ')
    },
    actualAreaPath() {
      const pts = this.actualPoints
      return pts.map((p,i)=>`${i===0?'M':'L'}${p.x},${p.y}`).join(' ') + ` L${pts[pts.length-1].x},152 L0,152 Z`
    },
    predictPoints() {
      return this.predictData.map((v, i) => ({ x: i * 80, y: 148 - (v / 80 * 130) }))
    },
    predictLinePath() {
      return this.predictPoints.map((p, i) => `${i===0?'M':'L'}${p.x},${p.y}`).join(' ')
    },
    predictAreaPath() {
      const pts = this.predictPoints
      return pts.map((p,i)=>`${i===0?'M':'L'}${p.x},${p.y}`).join(' ') + ` L${pts[pts.length-1].x},152 L0,152 Z`
    },
    radarDataPts() {
      return this.radarAxes.map((a,i) => { const p=this.radarPt(i,a.val); return `${p.x},${p.y}` }).join(' ')
    }
  },
  mounted() { this.animateBars() },
  methods: {
    animateBars() {
      this.animatedBars = this.chartBars.map(()=>0)
      this.chartBars.forEach((bar, i) => {
        setTimeout(()=>{ this.animatedBars = [...this.animatedBars]; this.animatedBars[i] = bar.pct }, 80 + i * 110)
      })
    },
    switchChart(tab) {
      this.chartTab = tab
      if (tab === 'bar') this.$nextTick(()=>this.animateBars())
    },
    radarPt(i, val) {
      const n = this.radarAxes.length
      const angle = (i/n)*Math.PI*2 - Math.PI/2
      const r = (val/100)*80
      return { x: 120 + r*Math.cos(angle), y: 110 + r*Math.sin(angle) }
    },
    radarPolygon(val) {
      return this.radarAxes.map((_,i)=>{ const p=this.radarPt(i,val); return `${p.x},${p.y}` }).join(' ')
    },
    radarLbl(i) {
      const n = this.radarAxes.length
      const angle = (i/n)*Math.PI*2 - Math.PI/2
      const r = 102
      return { x: 120 + r*Math.cos(angle), y: 110 + r*Math.sin(angle) }
    },
    completeCourse() { this.$emit('complete') }
  }
}
</script>

<style scoped>
.course-bd {
  --bg:rgba(255,255,255,0.03); --bg2:rgba(255,255,255,0.055); --bg3:rgba(255,255,255,0.09);
  --bd:rgba(255,255,255,0.07); --bd2:rgba(255,255,255,0.12);
  --cyan:#00d4b4; --cyan-d:rgba(0,212,180,0.10);
  --blue:#4a9eff; --blue-d:rgba(74,158,255,0.10);
  --violet:#a78bfa; --violet-d:rgba(167,139,250,0.10);
  --amber:#f59e0b; --amber-d:rgba(245,158,11,0.10);
  --green:#10b981; --green-d:rgba(16,185,129,0.10);
  --t1:#eef4ff; --t2:#8fa3c0; --t3:#4a5f7a;
  --r:12px; --rl:20px; --rx:24px;
  display:flex; flex-direction:column; gap:14px;
  font-family:'SF Pro Display','PingFang SC','HarmonyOS Sans SC',system-ui,sans-serif;
}
.sec-card { padding:20px 22px; border-radius:var(--rx); border:1px solid var(--bd); background:var(--bg2); position:relative; overflow:hidden; }
.sec-card::before { content:''; position:absolute; top:0; left:0; right:0; height:1px; background:linear-gradient(90deg,transparent,rgba(0,212,180,0.2),transparent); }
.sec-hd { display:flex; align-items:flex-start; gap:11px; margin-bottom:18px; flex-wrap:wrap; }
.sec-hd-text { flex:1; min-width:0; }
.sec-icon { width:33px; height:33px; border-radius:9px; flex-shrink:0; display:flex; align-items:center; justify-content:center; }
.ic-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.ic-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.ic-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.2); color:var(--violet); }
.ic-green  { background:var(--green-d);  border:1px solid rgba(16,185,129,0.2);  color:var(--green); }
.sec-title { font-size:14px; font-weight:600; color:var(--t1); margin-bottom:2px; }
.sec-sub   { font-size:11px; color:var(--t3); }

/* chart tabs */
.chart-tabs { display:flex; gap:5px; margin-left:auto; flex-shrink:0; }
.ctab { display:inline-flex; align-items:center; gap:4px; height:29px; padding:0 10px; border-radius:999px; border:1px solid var(--bd2); background:var(--bg); color:var(--t2); font-size:11px; font-weight:500; cursor:pointer; transition:all .2s; }
.ctab:hover { color:var(--t1); }
.ctab.active { background:linear-gradient(135deg,#00c9ab,#3d9fff); border-color:transparent; color:#07101f; font-weight:700; }

/* viz panel */
.viz-panel { display:grid; grid-template-columns:1.3fr 1fr; gap:14px; }
.viz-chart { background:var(--bg); border-radius:var(--rl); border:1px solid var(--bd2); overflow:hidden; min-height:240px; }
.chart-fade-enter-active,.chart-fade-leave-active { transition:all .25s ease; }
.chart-fade-enter-from,.chart-fade-leave-to { opacity:0; transform:translateY(6px); }
.chart-wrap { padding:16px; display:flex; flex-direction:column; gap:10px; }
.chart-title-row { display:flex; align-items:center; justify-content:space-between; }
.chart-label { font-size:12px; font-weight:600; color:var(--t1); }
.chart-unit  { font-size:10px; color:var(--t3); background:var(--bg3); padding:2px 8px; border-radius:999px; }

/* bar chart */
.bar-area { display:flex; gap:8px; }
.y-axis { display:flex; flex-direction:column; justify-content:space-between; font-size:9px; color:var(--t3); padding-bottom:22px; padding-top:2px; width:22px; text-align:right; }
.bars-container { flex:1; position:relative; }
.grid-lines { position:absolute; inset:0; bottom:22px; display:flex; flex-direction:column; justify-content:space-between; pointer-events:none; }
.grid-line { border-top:1px solid rgba(255,255,255,0.05); }
.bars { display:flex; align-items:flex-end; gap:5px; height:140px; padding-bottom:22px; }
.bar-col { flex:1; display:flex; flex-direction:column; align-items:center; gap:3px; height:100%; }
.bar-track { flex:1; width:100%; background:var(--bg3); border-radius:5px 5px 0 0; display:flex; align-items:flex-end; overflow:hidden; }
.bar-fill { width:100%; border-radius:5px 5px 0 0; transition:height .9s cubic-bezier(.34,1.56,.64,1); position:relative; overflow:hidden; }
.bar-normal { background:linear-gradient(180deg,#4a9eff 0%,#2a5cb0 100%); }
.bar-peak   { background:linear-gradient(180deg,#00d4b4 0%,#00967e 100%); box-shadow:0 0 10px rgba(0,212,180,0.28); }
.bar-shine  { position:absolute; top:0; left:0; right:0; height:40%; background:linear-gradient(180deg,rgba(255,255,255,0.18),transparent); border-radius:5px 5px 0 0; }
.bar-val-label { font-size:9px; color:var(--t2); font-weight:600; min-height:12px; }
.bar-lbl { font-size:9px; color:var(--t3); }
.chart-legend { display:flex; gap:12px; }
.legend-item { display:flex; align-items:center; gap:5px; font-size:10px; color:var(--t2); }
.legend-dot { width:8px; height:8px; border-radius:2px; }
.ld-peak { background:var(--cyan); } .ld-normal { background:var(--blue); }

/* line chart */
.line-chart-area { position:relative; }
.line-svg { width:100%; height:160px; }
.line-anim { stroke-dasharray:1000; stroke-dashoffset:1000; animation:drawLine 1.4s ease forwards; }
.line-anim-delay { animation-delay:.25s; }
@keyframes drawLine { to { stroke-dashoffset:0; } }
.dot-pop { animation:dotPop .4s cubic-bezier(.34,1.56,.64,1) forwards; opacity:0; animation-delay:.8s; }
@keyframes dotPop { to { opacity:1; } }
.line-x-labels { display:flex; justify-content:space-between; font-size:9px; color:var(--t3); padding:0 2px; }
.legend-line { width:18px; height:2px; border-radius:999px; }
.ll-actual { background:var(--cyan); }
.ll-predict { background:repeating-linear-gradient(90deg,var(--blue) 0,var(--blue) 4px,transparent 4px,transparent 7px); }

/* radar chart */
.radar-area { display:flex; align-items:center; gap:14px; }
.radar-svg { width:200px; flex-shrink:0; }
.radar-anim { animation:radarExpand .9s ease forwards; transform-origin:120px 110px; }
@keyframes radarExpand { from{transform:scale(0);opacity:0} to{transform:scale(1);opacity:1} }
.radar-scores { display:flex; flex-direction:column; gap:7px; flex:1; }
.rs-row { display:flex; align-items:center; gap:6px; }
.rs-dot { width:7px; height:7px; border-radius:2px; flex-shrink:0; }
.rs-name { font-size:10px; color:var(--t2); width:36px; flex-shrink:0; }
.rs-bar { flex:1; height:3px; background:var(--bg3); border-radius:999px; overflow:hidden; }
.rs-fill { height:100%; border-radius:999px; transition:width 1s ease; }
.rs-val { font-size:11px; font-weight:700; color:var(--t1); width:24px; text-align:right; flex-shrink:0; }

/* viz info */
.viz-info { background:var(--bg); border-radius:var(--rl); border:1px solid var(--bd2); padding:16px; display:flex; flex-direction:column; gap:11px; }
.info-badge { display:inline-flex; align-items:center; gap:5px; width:fit-content; padding:4px 10px; border-radius:999px; border:1px solid rgba(74,158,255,0.22); background:var(--blue-d); font-size:10px; font-weight:700; color:var(--blue); }
.info-title { font-size:13px; font-weight:600; color:var(--t1); }
.info-desc  { font-size:11px; color:var(--t2); line-height:1.75; }
.info-stats { display:flex; flex-direction:column; gap:9px; padding-top:8px; border-top:1px solid var(--bd); }
.stat-row { display:flex; flex-direction:column; gap:4px; }
.stat-head { display:flex; align-items:baseline; gap:6px; }
.stat-val { font-size:17px; font-weight:700; color:var(--cyan); letter-spacing:-0.02em; }
.stat-lbl { font-size:10px; color:var(--t3); }
.stat-bar { height:3px; background:var(--bg3); border-radius:999px; overflow:hidden; }
.stat-fill { height:100%; background:linear-gradient(90deg,var(--cyan),var(--blue)); border-radius:999px; transition:width 1.2s ease; }
.info-tip { display:flex; align-items:flex-start; gap:5px; font-size:10px; color:var(--t3); line-height:1.5; }

/* benefit list */
.benefit-list { display:flex; flex-direction:column; gap:10px; }
.benefit-row { display:grid; grid-template-columns:auto 1fr auto auto; align-items:center; gap:13px; padding:13px 15px; border-radius:var(--rl); border:1px solid var(--bd); background:var(--bg); transition:all .22s ease; }
.benefit-row:hover { border-color:rgba(0,212,180,0.2); background:var(--bg2); }
.benefit-glyph { width:40px; height:40px; border-radius:11px; flex-shrink:0; display:flex; align-items:center; justify-content:center; }
.gc-blue  { background:var(--blue-d);  border:1px solid rgba(74,158,255,0.2);  color:var(--blue); }
.gc-teal  { background:var(--cyan-d);  border:1px solid rgba(0,212,180,0.2);   color:var(--cyan); }
.gc-amber { background:var(--amber-d); border:1px solid rgba(245,158,11,0.2);  color:var(--amber); }
.benefit-body { min-width:0; }
.benefit-title { font-size:13px; font-weight:600; color:var(--t1); margin-bottom:3px; }
.benefit-desc  { font-size:11px; color:var(--t2); line-height:1.6; }
.benefit-metric { text-align:right; flex-shrink:0; }
.bm-val { font-size:14px; font-weight:700; color:var(--cyan); }
.bm-lbl { font-size:9px; color:var(--t3); white-space:nowrap; }
.benefit-tag { height:22px; padding:0 9px; border-radius:999px; flex-shrink:0; border:1px solid rgba(0,212,180,0.22); background:var(--cyan-d); font-size:10px; font-weight:600; color:var(--cyan); display:flex; align-items:center; }

/* pipeline */
.pipeline { display:flex; align-items:flex-start; gap:0; overflow-x:auto; padding-bottom:4px; }
.pipe-step { display:flex; flex-direction:column; align-items:center; gap:6px; flex:1; min-width:80px; }
.pipe-node { width:44px; height:44px; border-radius:12px; display:flex; align-items:center; justify-content:center; }
.pn-blue   { background:var(--blue-d);   border:1px solid rgba(74,158,255,0.25);  color:var(--blue); }
.pn-violet { background:var(--violet-d); border:1px solid rgba(167,139,250,0.25); color:var(--violet); }
.pn-teal   { background:var(--cyan-d);   border:1px solid rgba(0,212,180,0.25);   color:var(--cyan); }
.pn-amber  { background:var(--amber-d);  border:1px solid rgba(245,158,11,0.25);  color:var(--amber); }
.pn-green  { background:var(--green-d);  border:1px solid rgba(16,185,129,0.25);  color:var(--green); }
.pipe-connector { display:flex; align-items:center; color:var(--t3); padding:0 2px; margin-top:14px; }
.pipe-line { flex:1; height:1px; background:var(--bd2); }
.pipe-text { text-align:center; }
.pipe-label { font-size:11px; font-weight:600; color:var(--t1); }
.pipe-sub   { font-size:9px; color:var(--t3); }

/* privacy grid */
.privacy-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; margin-bottom:12px; }
.privacy-card { padding:15px; border-radius:var(--rl); border:1px solid var(--bd); background:var(--bg); display:flex; flex-direction:column; gap:7px; position:relative; transition:border-color .22s; }
.privacy-card:hover { border-color:rgba(16,185,129,0.2); }
.prv-num { position:absolute; top:10px; right:11px; font-size:22px; font-weight:800; color:rgba(16,185,129,0.1); line-height:1; }
.prv-icon { width:34px; height:34px; border-radius:9px; display:flex; align-items:center; justify-content:center; background:var(--green-d); border:1px solid rgba(16,185,129,0.2); color:var(--green); }
.prv-title { font-size:13px; font-weight:600; color:var(--t1); }
.prv-desc  { font-size:11px; color:var(--t2); line-height:1.65; flex:1; }
.prv-badge { display:inline-flex; align-items:center; gap:4px; width:fit-content; height:19px; padding:0 8px; border-radius:999px; background:rgba(16,185,129,0.1); border:1px solid rgba(16,185,129,0.2); font-size:9px; font-weight:700; color:var(--green); }
.compliance-note { display:flex; align-items:flex-start; gap:7px; padding:10px 13px; border-radius:var(--r); border:1px solid rgba(74,158,255,0.15); background:var(--blue-d); font-size:11px; color:rgba(74,158,255,0.7); line-height:1.6; }
.compliance-note svg { flex-shrink:0; margin-top:1px; color:var(--blue); }

/* complete */
.complete-row { text-align:center; padding-top:4px; }
.complete-btn { display:inline-flex; align-items:center; justify-content:center; gap:8px; padding:0 32px; height:48px; border-radius:999px; border:none; cursor:pointer; font-size:14px; font-weight:700; background:linear-gradient(135deg,#00c9ab,#3d9fff); color:#07101f; box-shadow:0 0 28px rgba(0,212,180,0.28); transition:all .3s ease; position:relative; overflow:hidden; }
.complete-btn:hover { transform:translateY(-2px); box-shadow:0 0 42px rgba(0,212,180,0.44); }
.btn-shimmer { position:absolute; top:0; left:-100%; width:55%; height:100%; background:linear-gradient(90deg,transparent,rgba(255,255,255,0.28),transparent); transform:skewX(-18deg); animation:shimmer 3.5s ease-in-out infinite; }
@keyframes shimmer { 0%{left:-100%} 50%,100%{left:160%} }

@media (max-width:860px) { .viz-panel { grid-template-columns:1fr; } .privacy-grid { grid-template-columns:1fr; } }
@media (max-width:600px) { .benefit-row { grid-template-columns:auto 1fr; } .benefit-metric,.benefit-tag { display:none; } .pipeline { gap:2px; } }
</style>