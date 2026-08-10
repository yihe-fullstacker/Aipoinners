<template>
  <div class="container">
    <div class="header-section">
      <button @click="$router.back()" class="back-btn">← 返回</button>
      <h1>主动学习（不确定性采样）</h1>
    </div>
    <div class="panel">
      <h3>选择影像（可多选）</h3>
      <input type="file" multiple @change="onFiles" accept="image/*" />
      <div class="file-list" v-if="files.length">
        <div class="file-row" v-for="(f,i) in files" :key="i">
          <span>{{ f.name }}</span>
        </div>
      </div>
      <div class="actions">
        <input v-model.number="topK" type="number" min="1" max="50" />
        <button @click="sample" :disabled="loading || !files.length">获取Top不确定病例</button>
        <button v-if="result" @click="downloadJSON">下载结果JSON</button>
      </div>
      <div class="result" v-if="result">
        <p>样本数: {{ result.count }}</p>
        <table class="table">
          <thead><tr><th>索引</th><th>不确定性</th><th>文件名</th></tr></thead>
          <tbody>
            <tr v-for="item in result.top_uncertain" :key="item.index">
              <td>{{ item.index }}</td>
              <td>{{ (item.uncertainty*100).toFixed(1) }}%</td>
              <td>{{ files[item.index]?.name }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>
<script>
import { ref } from 'vue'
import { aiAPI } from '../api/api'
export default {
  name: 'ActiveLearning',
  setup() {
    const files = ref([])
    const topK = ref(10)
    const loading = ref(false)
    const result = ref(null)
    const error = ref('')
    const onFiles = (e) => { files.value = Array.from(e.target.files || []) }
    const toBase64 = (file) => new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => resolve(reader.result)
      reader.onerror = reject
      reader.readAsDataURL(file)
    })
    const sample = async () => {
      loading.value = true; error.value=''; result.value=null
      try {
        const imgs = await Promise.all(files.value.map(f => toBase64(f)))
        const { data } = await aiAPI.getUncertainCases(imgs, topK.value)
        result.value = data
      } catch (e) {
        error.value = e.response?.data?.error || e.message
      } finally {
        loading.value = false
      }
    }
    const downloadJSON = () => {
      const blob = new Blob([JSON.stringify(result.value)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url; a.download = 'uncertain_cases.json'
      document.body.appendChild(a); a.click(); document.body.removeChild(a)
      URL.revokeObjectURL(url)
    }
    return { files, topK, loading, result, error, onFiles, sample, downloadJSON }
  }
}
</script>
<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 2rem; }
.header-section { display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem; }
.back-btn { background: #6c757d; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.panel { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.file-list { margin-top: 1rem; display: grid; gap: 0.5rem; }
.file-row { display: grid; grid-template-columns: 1fr; gap: 0.5rem; align-items: center; }
.actions { margin-top: 1rem; display: flex; gap: 0.5rem; align-items: center; }
.table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
.table th, .table td { border: 1px solid #eee; padding: 0.5rem; text-align: left; }
.error { color: #e74c3c; }
</style>
