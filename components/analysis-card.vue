<template>
  <!-- components/analysis-card.vue — AI分析结果卡片，替代 AnalysisResult -->
  <view class="analysis-card" v-if="analysis">
    <view class="ac-header">
      <text class="ac-title">AI 分析结果</text>
      <view class="ac-confidence" :class="confClass">{{ confidenceText }}</view>
    </view>
    <view class="ac-body">
      <view class="ac-item" v-for="(item, i) in findings" :key="i">
        <view class="ac-item-top">
          <text class="ac-item-type">{{ item.type || '发现' }}</text>
          <text class="ac-item-conf">{{ formatConf(item.confidence) }}</text>
        </view>
        <text class="ac-item-loc" v-if="item.location">{{ item.location }}</text>
      </view>
    </view>
    <view class="ac-footer" v-if="analysis.recommendations?.length">
      <text class="ac-footer-title">建议</text>
      <text class="ac-rec" v-for="(r, i) in analysis.recommendations" :key="i">{{ r }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ analysis: { type: Object, default: null } })

const confClass = computed(() => {
  const v = props.analysis?.confidence || 0
  if (v >= 0.8) return 'conf-high'
  if (v >= 0.6) return 'conf-mid'
  return 'conf-low'
})
const confidenceText = computed(() => {
  const v = props.analysis?.confidence
  return v != null ? `${(v * 100).toFixed(1)}%` : '--'
})
const findings = computed(() => props.analysis?.findings || [])
const formatConf = (v) => v != null ? `${(v * 100).toFixed(0)}%` : '--'
</script>

<style scoped>
.analysis-card { padding: 28rpx; border-radius: 24rpx; background: rgba(7,21,36,0.94); border:1px solid rgba(125,211,252,0.12); display:flex; flex-direction:column; gap:20rpx; }
.ac-header { display: flex; justify-content: space-between; align-items: center; }
.ac-title { font-size: 30rpx; font-weight: 700; color:#f0f6ff; }
.ac-confidence { padding: 6rpx 20rpx; border-radius: 999rpx; font-size: 24rpx; font-weight:700; }
.conf-high { background: rgba(248,113,113,0.16); color:#fecaca; }
.conf-mid { background: rgba(250,204,21,0.16); color:#fde68a; }
.conf-low { background: rgba(52,211,153,0.16); color:#bbf7d0; }
.ac-item { padding: 20rpx; border-radius: 16rpx; background: rgba(8,20,35,0.72); border:1px solid rgba(125,211,252,0.08); }
.ac-item-top { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.ac-item-type { font-size: 28rpx; color:#f0f6ff; font-weight:600; }
.ac-item-conf { font-size: 24rpx; color:#70d6ff; }
.ac-item-loc { font-size: 24rpx; color:rgba(200,218,242,0.65); }
.ac-footer-title { display:block; font-size:24rpx; color:rgba(210,225,245,0.75); margin-bottom:12rpx; }
.ac-rec { display:block; font-size:24rpx; color:rgba(200,218,242,0.6); line-height:1.8; padding-left:20rpx; position:relative; }
.ac-rec::before { content:'•'; position:absolute; left:0; color:#70d6ff; }
</style>
