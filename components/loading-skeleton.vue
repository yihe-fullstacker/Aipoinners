<template>
  <!-- components/loading-skeleton.vue — 骨架屏，被 list 类页面 import -->
  <view class="sk-wrap"><view v-for="i in count" :key="i" class="sk-card"><view class="sk-l sk-s"/><view class="sk-l"/><view class="sk-l sk-lg"/></view></view>
</template>
<script setup>defineProps({count:{type:Number,default:3}})</script>
<style scoped>.sk-wrap{display:flex;flex-direction:column;gap:20rpx;padding:28rpx}.sk-card{padding:28rpx;border-radius:20rpx;background:rgba(8,15,26,0.6);border:1px solid rgba(125,211,252,0.08);display:flex;flex-direction:column;gap:16rpx}.sk-l{height:24rpx;border-radius:6rpx;background:linear-gradient(90deg,rgba(255,255,255,0.04)25%,rgba(255,255,255,0.08)50%,rgba(255,255,255,0.04)75%);background-size:200%100%;animation:shimmer 1.5s infinite}.sk-s{width:40%}.sk-lg{width:75%}@keyframes shimmer{0%{background-position:200%0}100%{background-position:-200%0}}</style>
