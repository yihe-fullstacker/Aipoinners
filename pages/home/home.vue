<!-- pages/home/home.vue — 患者首页，对齐 PatientHome.vue 服务矩阵入口 -->
<template>
  <view class="page-shell">
    <NavBar title="影智医疗" />
    <scroll-view scroll-y class="home-scroll" @scrolltolower="loadMore">
      <!-- Hero -->
      <view class="hero">
        <text class="hero-eyebrow"><text class="badge-dot"></text>AI + 医生双核驱动</text>
        <text class="hero-title">让 AI 预判更快<text class="hero-em">让医生决策更稳</text></text>
        <text class="hero-desc">面向患者与医生提供影像上传、AI 辅助分析、专家复核、病程追踪的一体化体验。</text>
        <view class="hero-actions">
          <button class="btn-p" @tap="goUpload">上传影像并发起诊断</button>
          <button class="btn-s" @tap="goHistory">查看历史诊疗记录</button>
        </view>
      </view>

      <!-- 服务矩阵 -->
      <view class="section">
        <view class="sec-head">
          <text class="sec-kicker">服务矩阵</text>
          <text class="sec-title">核心功能按诊疗阶段清晰排布</text>
        </view>
        <view class="service-grid">
          <view v-for="svc in services" :key="svc.title" class="service-card" :class="svc.theme" @tap="goPage(svc.to)">
            <view class="svc-top">
              <text class="svc-tag">{{ svc.tag }}</text>
              <text class="svc-title">{{ svc.title }}</text>
              <text class="svc-text">{{ svc.text }}</text>
            </view>
            <view class="svc-arrow">→</view>
          </view>
        </view>
      </view>

      <!-- 快捷入口 -->
      <view class="section">
        <view class="sec-head">
          <text class="sec-kicker">快捷入口</text>
          <text class="sec-title">常用功能一键直达</text>
        </view>
        <view class="quick-grid">
          <view class="quick-card" v-for="q in quickLinks" :key="q.label" @tap="goPage(q.to)">
            <text class="quick-icon">{{ q.icon }}</text>
            <text class="quick-label">{{ q.label }}</text>
          </view>
        </view>
      </view>

      <view class="home-spacer"></view>
    </scroll-view>

    <TabBar />
    <AiFab />
  </view>
</template>

<script setup>
import NavBar from '@/components/nav-bar.vue'
import TabBar from '@/components/tab-bar.vue'
import AiFab from '@/components/ai-fab.vue'

const services = [
  { tag:'第一步', title:'上传影像与发起诊断', text:'CT、MRI、X光等医学影像上传', to:'/pages/upload/index', theme:'theme-cyan' },
  { tag:'核心分析', title:'AI 影像分析中心', text:'结构化分析输出', to:'/subpkg-patient/upload/diagnose', theme:'theme-blue' },
  { tag:'结果沉淀', title:'历史记录与追踪', text:'既往病例统一归档', to:'/subpkg-patient/history/index', theme:'theme-teal' },
  { tag:'深度服务', title:'智能问答与课程', text:'常见问题即时解答', to:'/pages/qa/chat', theme:'theme-indigo' }
]
const quickLinks = [
  { label:'AI问答', icon:'💬', to:'/pages/qa/chat' },
  { label:'报告中心', icon:'📋', to:'/pages/report/list' },
  { label:'课程中心', icon:'📚', to:'/subpkg-patient/course/index' },
  { label:'个人中心', icon:'👤', to:'/pages/mine/index' },
  { label:'医生端', icon:'🩺', to:'/subpkg-doctor/workspace/index' }
]
const goPage = (url) => {
  const tabPages = ['/pages/home/home','/pages/qa/chat','/pages/upload/index','/pages/report/list','/pages/mine/index']
  if (tabPages.includes(url)) uni.switchTab({ url })
  else uni.navigateTo({ url })
}
const goUpload = () => uni.navigateTo({ url:'/pages/upload/index' })
const goHistory = () => uni.navigateTo({ url:'/subpkg-patient/history/index' })
</script>

<style scoped>
.page-shell { min-height:100vh; background:radial-gradient(circle at top left,rgba(14,165,233,0.06),transparent 50%),#040b16; }
.home-scroll { height:calc(100vh - 200rpx); padding:0 28rpx; }
.hero { padding:30rpx 0 40rpx; display:flex; flex-direction:column; gap:20rpx; }
.hero-eyebrow{display:inline-flex;align-items:center;gap:8rpx;padding:8rpx 20rpx;background:rgba(34,211,238,0.08);border:1px solid rgba(34,211,238,0.18);border-radius:999rpx;color:#86e9ff;font-size:22rpx;font-weight:600;width:fit-content}
.badge-dot{width:12rpx;height:12rpx;border-radius:50%;background:#5eead4;box-shadow:0 0 10rpx rgba(94,234,212,0.8)}
.hero-title{font-size:48rpx;font-weight:800;color:#f8fafc;line-height:1.2;letter-spacing:-1rpx}
.hero-em{display:block;background:linear-gradient(135deg,#5eead4,#38bdf8,#818cf8);-webkit-background-clip:text;-webkit-text-fill-color:transparent}
.hero-desc{font-size:26rpx;color:rgba(200,218,242,0.7);line-height:1.7}
.hero-actions{display:flex;gap:16rpx}
.btn-p{flex:1;height:80rpx;border-radius:999rpx;background:linear-gradient(135deg,#5eead4,#38bdf8);color:#032032;font-size:26rpx;font-weight:700;border:none}
.btn-s{flex:1;height:80rpx;border-radius:999rpx;background:rgba(8,20,35,0.82);color:#ecf8ff;border:1px solid rgba(125,211,252,0.16);font-size:26rpx;font-weight:600}
.section{margin-top:40rpx}
.sec-head{margin-bottom:28rpx;display:flex;flex-direction:column;gap:12rpx}
.sec-kicker{display:inline-flex;padding:6rpx 16rpx;background:rgba(34,211,238,0.08);border:1px solid rgba(34,211,238,0.18);border-radius:999rpx;color:#86e9ff;font-size:22rpx;font-weight:600;width:fit-content}
.sec-title{font-size:36rpx;font-weight:700;color:#f8fafc}
.service-grid{display:flex;flex-direction:column;gap:16rpx}
.service-card{display:flex;justify-content:space-between;align-items:center;padding:28rpx;border-radius:22rpx;background:linear-gradient(180deg,rgba(7,22,38,0.92),rgba(4,13,24,0.92));border:1px solid rgba(125,211,252,0.1);transition:all 0.2s}
.service-card:active{transform:scale(0.98);border-color:rgba(125,211,252,0.25)}
.svc-top{display:flex;flex-direction:column;gap:10rpx;flex:1}
.svc-tag{font-size:20rpx;padding:4rpx 14rpx;border-radius:999rpx;background:rgba(34,211,238,0.08);color:#86e9ff;width:fit-content}
.svc-title{font-size:30rpx;font-weight:700;color:#f0f6ff}
.svc-text{font-size:24rpx;color:rgba(200,218,242,0.65);line-height:1.6}
.svc-arrow{font-size:40rpx;color:#70d6ff;padding-left:20rpx}
.quick-grid{display:flex;flex-wrap:wrap;gap:16rpx}
.quick-card{width:calc(33.33% - 12rpx);padding:28rpx 0;border-radius:20rpx;background:rgba(8,15,26,0.6);border:1px solid rgba(125,211,252,0.08);display:flex;flex-direction:column;align-items:center;gap:12rpx}
.quick-card:active{background:rgba(14,30,52,0.8);border-color:rgba(125,211,252,0.2)}
.quick-icon{font-size:44rpx}
.quick-label{font-size:24rpx;color:rgba(210,225,245,0.75);font-weight:600}
.home-spacer{height:140rpx}
</style>
