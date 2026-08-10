<template>
  <!-- components/safe-area.vue — 安全区域占位 -->
  <view :style="{ height: height || 'env(safe-area-inset-bottom)' }"></view>
</template>
<script setup>defineProps({ height: { type: String, default: '' } })</script>
