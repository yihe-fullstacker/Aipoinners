<!-- report/deep.vue -- 深度分析，对齐 DeepAnalysis -->
<template><view class="p"><NavBar title="深度分析" :showBack="true"/><LoadingSkeleton v-if="ld"/><view v-else class="b"><view class="h"><text class="sk">深度分析</text><text class="hi" v-if="r">#{{r.id}}</text></view><view class="cd"><text class="ct">深度分析结果</text><text class="no">{{r?.diagnosis||'深度分析模块，等待数据加载...'}}</text></view></view></view></template>
<script setup>
import{ref,onMounted}from'vue';import NavBar from'@/components/nav-bar.vue';import LoadingSkeleton from'@/components/loading-skeleton.vue';import{medicalAPI}from'@/api/medical'
const ld=ref(true);const r=ref(null)
onMounted(async()=>{const id=getCurrentPages().pop()?.options?.id;if(!id)return;try{const d=await medicalAPI.getReportDetail(id);r.value=d?.data||d}catch(e){}finally{ld.value=false}})
</script>
<style scoped>.p{min-height:100vh;background:#040b16}.b{padding:28rpx}.h{display:flex;flex-direction:column;gap:14rpx;margin-bottom:28rpx}.sk{display:inline-flex;padding:6rpx 16rpx;background:rgba(34,211,238,.08);border:1px solid rgba(34,211,238,.18);border-radius:999rpx;color:#86e9ff;font-size:22rpx;width:fit-content;font-weight:600}.hi{font-size:40rpx;font-weight:800;color:#f7fbff}.cd{padding:24rpx;border-radius:22rpx;background:linear-gradient(180deg,rgba(7,21,36,.94),rgba(4,12,24,.96));border:1px solid rgba(125,211,252,.12)}.ct{font-size:30rpx;font-weight:700;color:#f7fbff;margin-bottom:20rpx;display:block}.no{font-size:26rpx;color:rgba(200,218,242,.5);padding:40rpx 0;text-align:center}</style>
