<!-- subpkg-patient/mine/about.vue — 关于页，对齐 About -->
<template><view class="p"><NavBar title="关于" :showBack="true"/><view class="b"><text class="t">影智医疗</text><text class="d">AI + 医生双核驱动医学影像智能诊断平台</text><text class="v">版本 1.0.0</text></view></view></template>
<script setup>import NavBar from'@/components/nav-bar.vue'</script>
<style scoped>.p{min-height:100vh;background:#040b16}.b{padding:48rpx 28rpx;display:flex;flex-direction:column;align-items:center;gap:24rpx}.t{font-size:40rpx;font-weight:800;color:#f0f6ff}.d{font-size:26rpx;color:rgba(200,218,242,0.65);text-align:center;line-height:1.7}.v{font-size:24rpx;color:rgba(200,218,242,0.4)}</style>
