<!-- filepath: src/views/About.vue -->
<template>
  <div class="about-page" :class="{ 'medical-theme': isMedicalTheme }">
    <div v-if="isMedicalTheme" class="about-theme-grid"></div>

    <!-- ===== HERO ===== -->
    <div class="about-hero">
      <img class="about-hero-img" src="./element/image/1.png" alt="医疗AI诊断系统" />
      <div class="about-hero-overlay"></div>
      <div class="about-hero-text">
        <div class="hero-eyebrow">关于我们</div>
        <div class="about-hero-title">让AI成为医生的<br/>第二双眼睛</div>
        <div class="about-hero-subtitle">Make AI the second pair of eyes for doctors</div>
        <div class="hero-stats-row">
          <div class="hero-stat" v-for="s in heroStats" :key="s.label">
            <span class="hero-stat-num">{{ s.num }}</span>
            <span class="hero-stat-label">{{ s.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== MAIN CONTENT ===== -->
    <div class="about-container">

      <!-- ===== 公司简介 ===== -->
      <section class="about-section intro-section">
        <div class="section-label">公司简介</div>
        <div class="intro-grid">
          <div class="intro-text">
            <h2 class="section-title">专注医疗影像AI<br/>端到端解决方案</h2>
            <p class="intro-desc">成立于2020年，我们专注于医疗影像AI研发与产品化。融合深度学习大模型与顶尖放射科医生经验，提供端到端的影像预处理、模型推理与可视化解释能力，助力医院显著提升诊断效率与质量。</p>
            <p class="intro-desc">我们的平台已服务于全国500余家医疗机构，累计处理超200万份影像报告，获得国家三级医疗器械注册证书，通过等保三级认证。</p>
            <div class="intro-tags">
              <span class="itag" v-for="t in introTags" :key="t">{{ t }}</span>
            </div>
          </div>
          <div class="intro-visual">
            <div class="vis-card" v-for="v in visCards" :key="v.title">
              <div class="vis-icon" :class="v.color">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="v.path"></svg>
              </div>
              <div class="vis-title">{{ v.title }}</div>
              <div class="vis-val">{{ v.val }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== 技术优势 ===== -->
      <section class="about-section">
        <div class="section-label">技术优势</div>
        <h2 class="section-title center">核心技术能力</h2>
        <p class="section-sub center">自主研发的多模态影像分析引擎，集成最新深度学习架构与可解释AI技术</p>
        <div class="tech-grid">
          <div class="tech-card" v-for="t in techItems" :key="t.title">
            <div class="tech-icon-wrap" :class="t.color">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="t.icon"></svg>
            </div>
            <h3 class="tech-title">{{ t.title }}</h3>
            <p class="tech-desc">{{ t.desc }}</p>
          </div>
        </div>
      </section>

      <!-- ===== 服务人群 ===== -->
      <section class="about-section audience-section">
        <div class="section-label">服务对象</div>
        <h2 class="section-title center">面向多元用户群体</h2>
        <div class="audience-grid">
          <div class="aud-card" v-for="a in audiences" :key="a.title">
            <div class="aud-icon" :class="a.color">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="a.icon"></svg>
            </div>
            <h3 class="aud-title">{{ a.title }}</h3>
            <p class="aud-desc">{{ a.desc }}</p>
            <ul class="aud-list">
              <li v-for="item in a.items" :key="item">{{ item }}</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- ===== 资质认证 ===== -->
      <section class="about-section cert-section">
        <div class="section-label">资质认证</div>
        <h2 class="section-title center">权威认证，合规保障</h2>
        <div class="cert-grid">
          <div class="cert-card" v-for="c in certItems" :key="c.title">
            <div class="cert-badge" :class="c.color">{{ c.badge }}</div>
            <h3 class="cert-title">{{ c.title }}</h3>
            <p class="cert-desc">{{ c.desc }}</p>
          </div>
        </div>
      </section>

      <!-- ===== 合作伙伴 ===== -->
      <section class="about-section partners-section">
        <div class="section-label">合作伙伴</div>
        <h2 class="section-title center">已服务全国多家权威医疗机构</h2>
        <div class="partner-logos">
          <div class="partner-pill" v-for="p in partners" :key="p">{{ p }}</div>
        </div>
      </section>

      <!-- ===== 联系我们 ===== -->
      <section class="about-section contact-section">
        <div class="contact-grid">
          <div class="contact-left">
            <div class="section-label">联系我们</div>
            <h2 class="section-title">开启合作<br/>共建智慧医疗</h2>
            <p class="contact-desc">无论您是医院管理者、科研机构，还是寻求健康管理的个人用户，我们都期待与您合作，共同推动医疗AI的普及与应用。</p>
            <div class="contact-info-list">
              <div class="contact-info-item" v-for="c in contactInfo" :key="c.label">
                <div class="ci-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="c.icon"></svg>
                </div>
                <div>
                  <div class="ci-label">{{ c.label }}</div>
                  <div class="ci-val">{{ c.val }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="contact-right">
            <form class="contact-form" @submit.prevent="submitForm">
              <div class="form-row-two">
                <div class="form-field">
                  <label>机构 / 姓名</label>
                  <input type="text" v-model="form.name" placeholder="医院 / 公司 / 姓名" />
                </div>
                <div class="form-field">
                  <label>联系方式</label>
                  <input type="text" v-model="form.contact" placeholder="手机号或邮箱" />
                </div>
              </div>
              <div class="form-field">
                <label>合作需求</label>
                <select v-model="form.type">
                  <option value="">请选择需求类型</option>
                  <option>申请试用</option>
                  <option>商务合作</option>
                  <option>技术咨询</option>
                  <option>学术合作</option>
                  <option>其他</option>
                </select>
              </div>
              <div class="form-field">
                <label>备注说明</label>
                <textarea v-model="form.note" placeholder="请简要描述您的需求..." rows="3"></textarea>
              </div>
              <button class="submit-btn" type="submit">
                提交申请
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12,5 19,12 12,19"/></svg>
              </button>
              <p class="form-note">* 我们将在1-2个工作日内与您联系</p>
            </form>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script>
export default {
  name: 'About',
  data() {
    return {
      form: { name: '', contact: '', type: '', note: '' },

      heroStats: [
        { num: '2020', label: '年成立' },
        { num: '500+', label: '合作医院' },
        { num: '200万+', label: '处理病例' },
        { num: '98.7%', label: '诊断准确率' }
      ],

      introTags: ['医疗器械二类注册证', '等保三级认证', 'HIPAA合规', 'DICOM标准', '联邦学习'],

      visCards: [
        { title: '学术顾问', val: '12位', color: 'vc-blue', path: '<path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>' },
        { title: '研发工程师', val: '80+', color: 'vc-teal', path: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>' },
        { title: '已获专利', val: '32项', color: 'vc-green', path: '<circle cx="12" cy="8" r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/>' },
        { title: '服务城市', val: '50+', color: 'vc-amber', path: '<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>' }
      ],

      techItems: [
        {
          title: '高精度病灶检测',
          desc: '多尺度特征融合网络，病灶检测灵敏度达97.3%，支持毫米级精准定位与置信度评分。',
          color: 'ti-blue',
          icon: '<rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21,15 16,10 5,21"/>'
        },
        {
          title: '可解释AI可视化',
          desc: '集成Grad-CAM与SHAP热力图，直观展示模型决策依据，增强医生信任与临床可用性。',
          color: 'ti-teal',
          icon: '<polyline points="22,12 18,12 15,21 9,3 6,12 2,12"/>'
        },
        {
          title: '多模态数据融合',
          desc: '支持CT、MRI、X光、超声等多种影像，结合患者临床数据进行综合分析与鉴别诊断。',
          color: 'ti-green',
          icon: '<path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>'
        },
        {
          title: '联邦学习框架',
          desc: '数据不离院的前提下完成多中心模型训练，保障患者隐私同时持续提升模型性能。',
          color: 'ti-purple',
          icon: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>'
        },
        {
          title: '实时质控体系',
          desc: '全链路诊断质量监控，异常病例自动预警，确保AI输出结果符合临床规范要求。',
          color: 'ti-amber',
          icon: '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22,4 12,14.01 9,11.01"/>'
        },
        {
          title: 'HIS无缝对接',
          desc: '标准化API接口，支持与各类医院信息系统深度集成，平均部署周期小于2周。',
          color: 'ti-red',
          icon: '<rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>'
        }
      ],

      audiences: [
        {
          title: '医生与医院',
          desc: '赋能临床医生提升读片效率，助力医院构建智能影像诊断中心。',
          color: 'aud-blue',
          icon: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
          items: ['放射科AI辅助读片', '影像质控自动化', '危急值实时推送', 'PACS系统集成']
        },
        {
          title: '患者与大众',
          desc: '让优质医疗资源触手可及，为每位患者提供专业、便捷的影像诊断服务。',
          color: 'aud-teal',
          icon: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>',
          items: ['在线上传影像报告', '三甲医生远程复核', '历史报告永久存档', '健康教育课程']
        },
        {
          title: '科研机构',
          desc: '提供医疗大数据分析工具与联邦学习平台，加速医学AI研究成果转化。',
          color: 'aud-green',
          icon: '<path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>',
          items: ['多中心联合建模', '标注数据管理平台', '模型训练与评估', '学术合作发表']
        },
        {
          title: '医疗器械厂商',
          desc: '开放API生态，助力设备厂商将AI能力快速集成到影像设备与软件产品中。',
          color: 'aud-amber',
          icon: '<rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>',
          items: ['标准化SDK集成', 'AI算法模块授权', '联合产品研发', 'OEM定制服务']
        }
      ],

      certItems: [
        { badge: 'NMPA', title: '国家药品监督管理局', desc: '二类医疗器械注册证，AI影像辅助诊断软件合规上市', color: 'cc-blue' },
        { badge: 'GB/T', title: '信息安全等级保护三级', desc: '通过国家信息系统安全等级保护三级认证，数据安全有保障', color: 'cc-green' },
        { badge: 'ISO', title: 'ISO 13485医疗器械质量', desc: '医疗器械质量管理体系国际标准认证，全流程合规管理', color: 'cc-teal' },
        { badge: 'HIPAA', title: '医疗数据隐私合规', desc: '符合国际医疗数据隐私保护规范，患者数据全程加密', color: 'cc-amber' }
      ],

      partners: [
        '北京协和医院', '上海瑞金医院', '四川大学华西医院', '中山大学附属第一医院',
        '复旦大学附属华山医院', '北京大学人民医院', '解放军总医院', '南京鼓楼医院',
        '浙江大学医学院附属第一医院', '西安交通大学第一附属医院'
      ],

      contactInfo: [
        { label: '商务合作邮箱', val: 'business@yingzhi-med.com', icon: '<path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/>' },
        { label: '客服热线', val: '400-888-XXXX（工作日 9:00-18:00）', icon: '<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 13a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.56 2h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/>' },
        { label: '公司地址', val: '上海市浦东新区张江高科技园区', icon: '<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>' }
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
    submitForm() {
      if (!this.form.name || !this.form.contact) {
        alert('请填写姓名和联系方式')
        return
      }
      alert('申请已提交！我们将在1-2个工作日内与您联系。')
      this.form = { name: '', contact: '', type: '', note: '' }
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.about-page {
  position: relative;
  isolation: isolate;
  background: #f8fafc;
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.about-page > * {
  position: relative;
  z-index: 1;
}

.about-theme-grid {
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
.about-hero {
  position: relative;
  width: 100%;
  height: 548px;
  overflow: hidden;
}

.about-hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: 50% 36%;
  display: block;
  filter: brightness(0.85);
}

.about-hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(5,15,26,0.82) 0%, rgba(7,30,46,0.55) 55%, rgba(5,15,26,0.25) 100%);
}

.about-page.medical-theme .about-hero-overlay {
  background: linear-gradient(120deg, rgba(15, 39, 70, 0.42) 0%, rgba(21, 62, 114, 0.2) 52%, rgba(11, 159, 153, 0.08) 100%);
}

.about-hero-text {
  position: absolute;
  left: 80px;
  top: clamp(28px, 4vw, 52px);
  color: #fff;
}

.hero-eyebrow {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #38bdf8;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hero-eyebrow::before {
  content: '';
  width: 24px;
  height: 2px;
  background: #38bdf8;
  border-radius: 1px;
}

.about-hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -0.02em;
  margin-bottom: 10px;
}

.about-hero-subtitle {
  font-size: 15px;
  font-weight: 400;
  color: rgba(255,255,255,0.65);
  margin-bottom: 32px;
}

.hero-stats-row {
  display: flex;
  gap: 40px;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.hero-stat-num {
  font-size: 24px;
  font-weight: 800;
  color: #38bdf8;
  letter-spacing: -0.02em;
}

.hero-stat-label {
  font-size: 11px;
  color: rgba(255,255,255,0.55);
  font-weight: 500;
}

/* ===== CONTAINER ===== */
.about-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px 80px;
}

/* ===== SECTION COMMON ===== */
.about-section {
  padding: 72px 0;
  border-bottom: 1px solid #e9eef5;
}

.about-section:last-child { border-bottom: none; }

.section-label {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #0ea5e9;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-label::before {
  content: '';
  width: 20px;
  height: 2px;
  background: #0ea5e9;
  border-radius: 1px;
}

.section-title {
  font-size: clamp(1.6rem, 3vw, 2.2rem);
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
  line-height: 1.25;
  margin-bottom: 16px;
}

.section-title.center { text-align: center; }

.section-sub {
  font-size: 16px;
  color: #64748b;
  line-height: 1.7;
  margin-bottom: 48px;
  max-width: 580px;
}

.section-sub.center {
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}

/* ===== INTRO ===== */
.intro-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 64px;
  align-items: start;
  margin-top: 8px;
}

.intro-desc {
  font-size: 15px;
  color: #475569;
  line-height: 1.8;
  margin-bottom: 14px;
}

.intro-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 24px;
}

.itag {
  padding: 5px 14px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #1d4ed8;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.intro-visual {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.vis-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 24px 20px;
  text-align: center;
  transition: all 0.25s ease;
}

.vis-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 8px 24px rgba(14,165,233,0.1);
  transform: translateY(-3px);
}

.vis-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 14px;
}

.vc-blue   { background: #eff6ff; color: #1d4ed8; }
.vc-teal   { background: #f0fdfa; color: #0d9488; }
.vc-green  { background: #f0fdf4; color: #15803d; }
.vc-amber  { background: #fffbeb; color: #b45309; }

.vis-title {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  margin-bottom: 4px;
}

.vis-val {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
}

/* ===== TECH GRID ===== */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.tech-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 28px;
  transition: all 0.25s ease;
}

.tech-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 8px 28px rgba(0,0,0,0.07);
  transform: translateY(-4px);
}

.tech-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}

.ti-blue   { background: #eff6ff; color: #1d4ed8; }
.ti-teal   { background: #f0fdfa; color: #0d9488; }
.ti-green  { background: #f0fdf4; color: #15803d; }
.ti-purple { background: #f5f3ff; color: #7c3aed; }
.ti-amber  { background: #fffbeb; color: #b45309; }
.ti-red    { background: #fef2f2; color: #b91c1c; }

.tech-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 10px;
}

.tech-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.7;
}

/* ===== AUDIENCE ===== */
.audience-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 8px;
}

.aud-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 28px 24px;
  transition: all 0.25s ease;
}

.aud-card:hover {
  box-shadow: 0 12px 36px rgba(0,0,0,0.08);
  transform: translateY(-4px);
}

.aud-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}

.aud-blue   { background: linear-gradient(135deg, #dbeafe, #bfdbfe); color: #1d4ed8; }
.aud-teal   { background: linear-gradient(135deg, #ccfbf1, #99f6e4); color: #0d9488; }
.aud-green  { background: linear-gradient(135deg, #dcfce7, #bbf7d0); color: #15803d; }
.aud-amber  { background: linear-gradient(135deg, #fef9c3, #fde68a); color: #92400e; }

.aud-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 8px;
}

.aud-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.65;
  margin-bottom: 14px;
}

.aud-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.aud-list li {
  font-size: 12px;
  color: #64748b;
  padding-left: 14px;
  position: relative;
}

.aud-list li::before {
  content: '·';
  position: absolute;
  left: 0;
  color: #0ea5e9;
  font-weight: 900;
}

/* ===== CERT ===== */
.cert-section { background: transparent; }

.cert-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 8px;
}

.cert-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 28px 22px;
  text-align: center;
  transition: all 0.25s ease;
}

.cert-card:hover {
  box-shadow: 0 10px 30px rgba(0,0,0,0.07);
  transform: translateY(-3px);
}

.cert-badge {
  display: inline-block;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.06em;
  margin-bottom: 16px;
}

.cc-blue   { background: #dbeafe; color: #1d4ed8; }
.cc-green  { background: #dcfce7; color: #15803d; }
.cc-teal   { background: #ccfbf1; color: #0d9488; }
.cc-amber  { background: #fef9c3; color: #92400e; }

.cert-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 8px;
}

.cert-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.65;
}

/* ===== PARTNERS ===== */
.partner-logos {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  justify-content: center;
}

.partner-pill {
  padding: 10px 22px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  transition: all 0.2s ease;
}

.partner-pill:hover {
  border-color: #0ea5e9;
  color: #0ea5e9;
  background: #eff6ff;
}

/* ===== CONTACT ===== */
.contact-grid {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 64px;
  align-items: start;
}

.contact-desc {
  font-size: 15px;
  color: #475569;
  line-height: 1.8;
  margin: 16px 0 32px;
}

.contact-info-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contact-info-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.ci-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #eff6ff;
  color: #0ea5e9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ci-label {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  margin-bottom: 3px;
}

.ci-val {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

/* ===== FORM ===== */
.contact-form {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 36px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-row-two {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field label {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.form-field input,
.form-field select,
.form-field textarea {
  padding: 11px 14px;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #f8fafc;
  outline: none;
  transition: all 0.2s ease;
  font-family: inherit;
  resize: vertical;
}

.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  border-color: #0ea5e9;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(14,165,233,0.12);
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(14,165,233,0.35);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #38bdf8, #0ea5e9);
  box-shadow: 0 6px 24px rgba(14,165,233,0.5);
  transform: translateY(-1px);
}

.form-note {
  font-size: 12px;
  color: #94a3b8;
  text-align: center;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 1100px) {
  .tech-grid       { grid-template-columns: repeat(2, 1fr); }
  .audience-grid   { grid-template-columns: repeat(2, 1fr); }
  .cert-grid       { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 900px) {
  .intro-grid    { grid-template-columns: 1fr; gap: 32px; }
  .contact-grid  { grid-template-columns: 1fr; gap: 32px; }
  .about-hero    { height: 428px; }
  .about-hero-text { left: 32px; top: 32px; }
  .hero-stats-row { gap: 24px; }
}

@media (max-width: 640px) {
  .about-container { padding: 0 20px 60px; }
  .about-hero      { height: 348px; }
  .about-hero-text { left: 20px; top: 24px; }
  .about-hero-title { font-size: 1.6rem; }
  .tech-grid       { grid-template-columns: 1fr; }
  .audience-grid   { grid-template-columns: 1fr; }
  .cert-grid       { grid-template-columns: 1fr; }
  .intro-visual    { grid-template-columns: 1fr 1fr; }
  .form-row-two    { grid-template-columns: 1fr; }
  .hero-stats-row  { gap: 16px; flex-wrap: wrap; }
}
</style>
