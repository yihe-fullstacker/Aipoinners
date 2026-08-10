<template>
  <!-- components/empty-state.vue — 空状态组件，被 list 类页面 import -->
  <view class="empty-state"><text class="e-icon">{{ icon }}</text><text class="e-title">{{ title }}</text><text class="e-desc" v-if="desc">{{ desc }}</text><slot /></view>
</template>
<script setup>defineProps({icon:{type:String,default:'📭'},title:{type:String,default:'暂无数据'},desc:{type:String,default:''}})</script>
<style scoped>.empty-state{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:80rpx 40rpx;text-align:center}.e-icon{font-size:80rpx;margin-bottom:24rpx}.e-title{font-size:30rpx;font-weight:700;color:#f0f6ff;margin-bottom:14rpx}.e-desc{font-size:26rpx;color:rgba(200,218,242,0.65);line-height:1.7;max-width:500rpx}</style>
