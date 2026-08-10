<template>
  <div class="container">
    <div class="header-section">
      <button @click="$router.back()" class="back-btn">← 返回</button>
      <h1>模型评估</h1>
    </div>
    <div class="panel">
      <h3>选择影像（可多选）</h3>
      <input type="file" multiple @change="onFiles" accept="image/*" />
      <div class="file-list" v-if="files.length">
        <div class="file-row" v-for="(f,i) in files" :key="i">
          <span>{{ f.name }}</span>
          <input v-model="labels[i]" placeholder="标签(0或1，可选)" />
        </div>
      </div>
      <div class="actions">
        <button @click="evaluate" :disabled="loading || !files.length">开始评估</button>
      </div>
      <div class="result" v-if="result">
        <p>样本数: {{ result.count }}</p>
        <p v-if="result.accuracy !== null">准确率: {{ (result.accuracy*100).toFixed(1) }}%</p>
        <p>是否包含标签: {{ result.has_labels ? '是' : '否' }}</p>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>
<script>
import { ref } from 'vue'
import { aiAPI } from '../api/api'
export default {
  name: 'ModelEvaluation',
  setup() {
    const files = ref([])
    const labels = ref([])
    const loading = ref(false)
    const result = ref(null)
    const error = ref('')
    const onFiles = (e) => {
      files.value = Array.from(e.target.files || [])
      labels.value = new Array(files.value.length).fill('')
    }
    const toBase64 = (file) => new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => resolve(reader.result)
      reader.onerror = reject
      reader.readAsDataURL(file)
    })
    const evaluate = async () => {
      loading.value = true; error.value=''; result.value=null
      try {
        const imgs = await Promise.all(files.value.map(f => toBase64(f)))
        const lbls = labels.value.some(x => x !== '') ? labels.value.map(x => x === '' ? null : Number(x)) : null
        const { data } = await aiAPI.evaluateModel(imgs, lbls)
        result.value = data
      } catch (e) {
        error.value = e.response?.data?.error || e.message
      } finally {
        loading.value = false
      }
    }
    return { files, labels, loading, result, error, onFiles, evaluate }
  }
}
</script>
<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 2rem; }
.header-section { display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem; }
.back-btn { background: #6c757d; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.panel { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.file-list { margin-top: 1rem; display: grid; gap: 0.5rem; }
.file-row { display: grid; grid-template-columns: 1fr 120px; gap: 0.5rem; align-items: center; }
.actions { margin-top: 1rem; }
.error { color: #e74c3c; }
</style>
