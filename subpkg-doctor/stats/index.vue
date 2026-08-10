<!-- subpkg-doctor/stats/index.vue — 数据仪表盘，对齐 DataDashboard -->
<template><view class="p"><NavBar title="数据仪表盘" :showBack="true"/><view class="b"><text class="t">平台运行数据</text><view class="stats"><view v-for="s in statsData" :key="s.lbl" class="st-card"><text class="stv">{{s.val}}</text><text class="stl">{{s.lbl}}</text></view></view><view class="note"><text>实时数据监控 Panel · 持续更新中</text></view></view></view></template>
<script setup>
import NavBar from '@/components/nav-bar.vue'
const statsData=[
  {val:'98.7%',lbl:'诊断准确率'},{val:'200万+',lbl:'累计病例'},{val:'500+',lbl:'合作医院'},
  {val:'<2秒',lbl:'AI响应速度'},{val:'7×24h',lbl:'医生值守'},{val:'25+',lbl:'覆盖病种'}
]
</script>
<style scoped>.p{min-height:100vh;background:#040b16}.b{padding:28rpx}.t{font-size:36rpx;font-weight:700;color:#f0f6ff;display:block;margin-bottom:36rpx}.stats{display:flex;flex-wrap:wrap;gap:16rpx}.st-card{width:calc(33.33% - 12rpx);padding:28rpx 16rpx;border-radius:20rpx;background:linear-gradient(180deg,rgba(7,22,38,0.92),rgba(4,13,24,0.92));border:1px solid rgba(125,211,252,0.1);display:flex;flex-direction:column;align-items:center;gap:10rpx}.stv{font-size:36rpx;font-weight:800;color:#f8fafc}.stl{font-size:22rpx;color:rgba(200,218,242,0.5)}.note{margin-top:48rpx;padding:24rpx;border-radius:16rpx;background:rgba(14,165,233,0.05);border:1px solid rgba(14,165,233,0.15);text-align:center;font-size:24rpx;color:rgba(200,218,242,0.6)}</style>
