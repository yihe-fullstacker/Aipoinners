<template>
  <div class="upload-page" :class="{ 'medical-theme': isMedicalTheme }">
    <button class="floating-back" type="button" @click="goBack" aria-label="返回患者首页">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M19 12H5M12 5l-7 7 7 7"/>
      </svg>
    </button>

    <div class="page-shell">
      <section class="hero-panel">
        <div class="hero-copy">
          <span class="hero-badge">专家会诊发起中心</span>
          <h1>上传医学影像，进入 AI 预分析与医生协同诊断流程</h1>
          <p>
            面向 CT、MRI、X 光、DICOM 与 NIfTI 数据提供标准化提交入口，帮助用户快速完成资料整理、病情说明填写和专家会诊申请。
          </p>

          <div class="hero-metrics">
            <article class="metric-card">
              <strong>24h</strong>
              <span>专家反馈时效目标</span>
            </article>
            <article class="metric-card">
              <strong>多格式</strong>
              <span>支持主流医学影像上传</span>
            </article>
            <article class="metric-card">
              <strong>双核协同</strong>
              <span>AI 初筛 + 医生复核</span>
            </article>
          </div>

          <div class="timeline-strip">
            <div class="timeline-item active">1. 上传影像</div>
            <div class="timeline-item">2. AI 预分析</div>
            <div class="timeline-item">3. 医生复核</div>
            <div class="timeline-item">4. 结果追踪</div>
          </div>
        </div>

        <div class="hero-sidecard">
          <div class="card-title">上传前提示</div>
          <ul class="hint-list">
            <li>建议上传清晰、完整的原始检查图像，避免压缩截图。</li>
            <li>病情描述中可补充症状持续时间、既往病史和检查目的。</li>
            <li>提交后将自动进入平台分析流程，并同步生成诊断记录。</li>
          </ul>
          <div class="format-tags">
            <span v-for="format in supportedFormats" :key="format">{{ format }}</span>
          </div>
        </div>
      </section>

      <section class="workspace-grid">
        <div class="upload-panel card-surface">
          <div class="panel-head">
            <div>
              <div class="section-kicker">影像资料</div>
              <h2>拖拽上传或点击选择文件</h2>
            </div>
            <div class="status-chip" :class="{ ready: selectedFile }">
              {{ selectedFile ? '已选择文件' : '等待上传' }}
            </div>
          </div>

          <div
            class="upload-area"
            :class="{ 'has-file': previewUrl }"
            @click="triggerFileInput"
            @drop.prevent="handleDrop"
            @dragover.prevent
          >
            <input
              ref="fileInput"
              type="file"
              class="hidden-input"
              @change="handleFileChange"
              accept=".jpg,.jpeg,.png,.dicom,.dcm,.nii,.nii.gz"
            />

            <div v-if="!previewUrl" class="placeholder">
              <div class="placeholder-icon">影</div>
              <div class="placeholder-title">点击或拖拽上传医学影像</div>
              <p>支持 JPG、PNG、DICOM、NIfTI 等格式</p>
            </div>

            <div v-else class="preview-box">
              <img :src="previewUrl" alt="影像预览" class="preview-img" />
              <button type="button" class="replace-btn" @click.stop="removeImage">重新选择文件</button>
            </div>
          </div>

          <div v-if="selectedFile" class="file-summary">
            <div>
              <span class="summary-label">文件名称</span>
              <strong>{{ selectedFile.name }}</strong>
            </div>
            <div>
              <span class="summary-label">文件大小</span>
              <strong>{{ formatFileSize(selectedFile.size) }}</strong>
            </div>
            <div>
              <span class="summary-label">文件类型</span>
              <strong>{{ selectedFile.type || '医学影像文件' }}</strong>
            </div>
          </div>
        </div>

        <div class="form-panel card-surface">
          <div class="panel-head">
            <div>
              <div class="section-kicker">会诊信息</div>
              <h2>填写病情说明，帮助医生更快判断</h2>
            </div>
          </div>

          <div class="form-group">
            <label for="desc">病情描述 / 主诉</label>
            <textarea
              id="desc"
              v-model="description"
              rows="8"
              placeholder="请描述症状表现、持续时间、既往病史、当前检查目的或希望重点关注的问题。"
            ></textarea>
            <div class="helper-row">
              <span>建议填写越具体，越有助于 AI 和医生形成更稳定的判断。</span>
              <span>{{ description.length }} / 500</span>
            </div>
          </div>

          <div class="service-list">
            <article class="service-item">
              <strong>智能预分析</strong>
              <span>提交后自动触发影像识别和结构化诊断流程。</span>
            </article>
            <article class="service-item">
              <strong>医生协同复核</strong>
              <span>复杂病例可进入专家会诊与问答沟通阶段。</span>
            </article>
            <article class="service-item">
              <strong>记录自动沉淀</strong>
              <span>系统自动同步到历史记录，便于后续复诊追踪。</span>
            </article>
          </div>

          <div class="action-row">
            <button
              class="submit-btn"
              type="button"
              :disabled="!selectedFile || !description || loading"
              @click="submitToDoctor"
            >
              <span v-if="loading">正在提交会诊申请...</span>
              <span v-else>提交给专家诊断</span>
            </button>
            <router-link :to="{ name: 'PatientHistory' }" class="text-link">
              查看历史诊疗记录
            </router-link>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { medicalAPI } from '../api/api'

export default {
  name: 'ExpertUpload',
  setup() {
    const router = useRouter()
    const store = useStore()
    const fileInput = ref(null)
    const selectedFile = ref(null)
    const previewUrl = ref('')
    const description = ref('')
    const loading = ref(false)
    const supportedFormats = ['JPG', 'PNG', 'DICOM', 'DCM', 'NII', 'NII.GZ']
    const isMedicalTheme = computed(() => store.state.theme === 'medical-ai')

    const goBack = () => {
      router.push('/patient/home')
    }

    const triggerFileInput = () => {
      if (fileInput.value) {
        fileInput.value.click()
      }
    }

    const processFile = (file) => {
      if (!file) return
      selectedFile.value = file
      previewUrl.value = URL.createObjectURL(file)
    }

    const handleFileChange = (event) => {
      const file = event.target.files && event.target.files[0]
      processFile(file)
    }

    const handleDrop = (event) => {
      const file = event.dataTransfer.files && event.dataTransfer.files[0]
      processFile(file)
    }

    const removeImage = () => {
      selectedFile.value = null
      previewUrl.value = ''
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const formatFileSize = (size) => {
      if (!size) return '0 KB'
      if (size < 1024 * 1024) {
        return `${(size / 1024).toFixed(1)} KB`
      }
      return `${(size / (1024 * 1024)).toFixed(2)} MB`
    }

    const submitToDoctor = async () => {
      if (!selectedFile.value) {
        alert('请先上传影像文件')
        return
      }
      if (!description.value) {
        alert('请填写病情描述')
        return
      }

      loading.value = true
      try {
        const analyzeRes = await medicalAPI.analyzeImage(
          selectedFile.value,
          description.value
        )

        if (!analyzeRes || !analyzeRes.data) {
          alert('提交失败：服务器未返回有效数据')
          return
        }

        const consultationId = analyzeRes.data.consultationId
        if (consultationId) {
          alert('提交成功，正在进入会诊详情页')
          router.push({ name: 'ConsultationDetail', params: { id: consultationId } })
          return
        }

        alert('提交成功，已为您跳转至历史记录')
        router.push({ name: 'PatientHistory' })
      } catch (error) {
        console.error('提交专家诊断失败:', error)
        const errorData = error.response?.data
        let msg = '提交失败，请稍后重试'

        if (typeof errorData === 'string') {
          msg = errorData
        } else if (errorData?.error) {
          msg = errorData.error
        } else if (errorData?.message) {
          msg = errorData.message
        } else if (error.message) {
          msg = error.message
        }

        alert(`提交失败：${msg}`)
      } finally {
        loading.value = false
      }
    }

    return {
      fileInput,
      selectedFile,
      previewUrl,
      description,
      loading,
      isMedicalTheme,
      supportedFormats,
      triggerFileInput,
      handleFileChange,
      handleDrop,
      removeImage,
      formatFileSize,
      submitToDoctor,
      goBack
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.upload-page {
  --upload-bg:
    radial-gradient(circle at top left, rgba(20, 184, 166, 0.18), transparent 30%),
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.18), transparent 28%),
    linear-gradient(180deg, #07111d 0%, #0a1625 100%);
  --upload-text: #ecf8ff;
  --upload-text-soft: rgba(220, 235, 255, 0.72);
  --upload-heading: #f7fbff;
  --upload-panel-bg: linear-gradient(180deg, rgba(7, 21, 36, 0.94), rgba(4, 12, 24, 0.96));
  --upload-panel-border: rgba(125, 211, 252, 0.12);
  --upload-panel-shadow: 0 24px 56px rgba(2, 10, 20, 0.3);
  --upload-card-bg: rgba(8, 23, 40, 0.8);
  --upload-card-bg-strong: rgba(7, 21, 36, 0.7);
  --upload-card-border: rgba(125, 211, 252, 0.1);
  --upload-card-border-soft: rgba(125, 211, 252, 0.08);
  --upload-accent-bg: rgba(45, 212, 191, 0.08);
  --upload-accent-border: rgba(94, 234, 212, 0.18);
  --upload-accent-text: #90f5e8;
  --upload-status-bg: rgba(10, 24, 41, 0.74);
  --upload-status-border: rgba(125, 211, 252, 0.14);
  --upload-status-border-strong: rgba(94, 234, 212, 0.36);
  --upload-status-text: rgba(226, 240, 255, 0.7);
  --upload-status-text-strong: #ecfeff;
  --upload-ready-bg: rgba(20, 184, 166, 0.2);
  --upload-input-bg: linear-gradient(180deg, rgba(5, 15, 27, 0.88), rgba(8, 22, 38, 0.68));
  --upload-input-bg-hover: linear-gradient(180deg, rgba(8, 20, 34, 0.9), rgba(10, 28, 46, 0.74));
  --upload-input-border: rgba(125, 211, 252, 0.28);
  --upload-button-bg: linear-gradient(135deg, #5eead4, #38bdf8);
  --upload-button-text: #032032;
  --upload-button-shadow: 0 14px 28px rgba(56, 189, 248, 0.24);
  --upload-button-shadow-hover: 0 18px 34px rgba(56, 189, 248, 0.3);
  min-height: 100vh;
  background: var(--upload-bg);
  color: var(--upload-text);
  padding: 8px 24px 48px;
}

.upload-page.medical-theme {
  --upload-bg:
    radial-gradient(circle at 12% 10%, rgba(21, 62, 114, 0.14) 0%, transparent 24%),
    radial-gradient(circle at 88% 16%, rgba(11, 159, 153, 0.12) 0%, transparent 26%),
    linear-gradient(180deg, #f7fbff 0%, #eef5fb 44%, #e6eff8 100%);
  --upload-text: #17324e;
  --upload-text-soft: rgba(61, 88, 120, 0.82);
  --upload-heading: #17324e;
  --upload-panel-bg: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(244, 249, 253, 0.92));
  --upload-panel-border: rgba(96, 132, 171, 0.18);
  --upload-panel-shadow: 0 28px 52px rgba(71, 105, 145, 0.14);
  --upload-card-bg: rgba(248, 251, 255, 0.9);
  --upload-card-bg-strong: rgba(248, 251, 255, 0.84);
  --upload-card-border: rgba(120, 151, 183, 0.18);
  --upload-card-border-soft: rgba(120, 151, 183, 0.16);
  --upload-accent-bg: rgba(14, 165, 233, 0.08);
  --upload-accent-border: rgba(14, 165, 233, 0.18);
  --upload-accent-text: #0f766e;
  --upload-status-bg: rgba(242, 247, 252, 0.92);
  --upload-status-border: rgba(120, 151, 183, 0.18);
  --upload-status-border-strong: rgba(14, 165, 233, 0.28);
  --upload-status-text: #4a6787;
  --upload-status-text-strong: #0f766e;
  --upload-ready-bg: linear-gradient(135deg, rgba(219, 244, 244, 0.96), rgba(231, 245, 255, 0.96));
  --upload-input-bg: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(244, 249, 253, 0.88));
  --upload-input-bg-hover: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 246, 252, 0.94));
  --upload-input-border: rgba(120, 151, 183, 0.24);
  --upload-button-bg: linear-gradient(135deg, #0f766e, #0284c7);
  --upload-button-text: #f8fcff;
  --upload-button-shadow: 0 14px 28px rgba(14, 116, 144, 0.16);
  --upload-button-shadow-hover: 0 18px 34px rgba(14, 116, 144, 0.2);
}

.page-shell {
  max-width: 1240px;
  margin: 0 auto;
}

.floating-back {
  position: fixed;
  top: 76px;
  left: 24px;
  z-index: 20;
  width: 46px;
  height: 46px;
  border: 1px solid var(--upload-status-border);
  border-radius: 14px;
  background: var(--upload-card-bg-strong);
  color: var(--upload-text);
  cursor: pointer;
  transition: transform 0.25s ease, border-color 0.25s ease, background 0.25s ease;
}

.floating-back:hover {
  transform: translateY(-2px);
  border-color: var(--upload-status-border-strong);
  background: var(--upload-panel-bg);
}

.hero-panel,
.card-surface {
  border: 1px solid var(--upload-panel-border);
  background: var(--upload-panel-bg);
  box-shadow: var(--upload-panel-shadow);
}

.hero-panel {
  display: grid;
  grid-template-columns: 1.35fr 0.8fr;
  gap: 22px;
  padding: 32px;
  border-radius: 30px;
}

.hero-badge,
.section-kicker,
.status-chip,
.format-tags span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid var(--upload-accent-border);
  background: var(--upload-accent-bg);
  color: var(--upload-accent-text);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero-copy h1,
.panel-head h2 {
  color: var(--upload-heading);
}

.hero-copy h1 {
  margin: 16px 0 14px;
  font-size: clamp(32px, 4vw, 50px);
  line-height: 1.08;
}

.hero-copy p,
.hero-sidecard,
.service-item span,
.helper-row,
.placeholder p {
  color: var(--upload-text-soft);
  line-height: 1.8;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 24px;
}

.metric-card,
.hero-sidecard,
.upload-panel,
.form-panel,
.service-item,
.file-summary {
  border-radius: 22px;
}

.metric-card {
  padding: 18px;
  background: var(--upload-card-bg);
  border: 1px solid var(--upload-card-border);
}

.metric-card strong {
  display: block;
  color: var(--upload-heading);
  font-size: 24px;
  margin-bottom: 6px;
}

.timeline-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.timeline-item {
  padding: 14px 16px;
  border-radius: 18px;
  background: var(--upload-status-bg);
  border: 1px solid var(--upload-card-border-soft);
  color: var(--upload-status-text);
  text-align: center;
  font-size: 13px;
  font-weight: 600;
}

.timeline-item.active {
  border-color: var(--upload-status-border-strong);
  background: var(--upload-ready-bg);
  color: var(--upload-heading);
}

.hero-sidecard {
  padding: 24px;
  background: var(--upload-card-bg-strong);
  border: 1px solid var(--upload-card-border);
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--upload-heading);
  margin-bottom: 16px;
}

.hint-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
}

.format-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.workspace-grid {
  display: grid;
  grid-template-columns: minmax(340px, 0.94fr) minmax(400px, 1.06fr);
  gap: 22px;
  margin-top: 24px;
  align-items: start;
}

.upload-panel,
.form-panel {
  padding: 26px;
  min-height: 100%;
}

.upload-panel {
  display: grid;
  align-content: start;
  gap: 18px;
}

.form-panel {
  display: grid;
  align-content: start;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 22px;
}

.panel-head h2 {
  margin-top: 12px;
  font-size: 28px;
  line-height: 1.2;
}

.status-chip.ready {
  color: var(--upload-status-text-strong);
  border-color: var(--upload-status-border-strong);
  background: var(--upload-ready-bg);
}

.upload-area {
  min-height: 360px;
  border-radius: 24px;
  border: 1px dashed var(--upload-input-border);
  background: var(--upload-input-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.25s ease, transform 0.25s ease, background 0.25s ease;
}

.upload-area:hover {
  transform: translateY(-2px);
  border-color: var(--upload-status-border-strong);
  background: var(--upload-input-bg-hover);
}

.upload-area.has-file {
  border-style: solid;
}

.hidden-input {
  display: none;
}

.placeholder {
  text-align: center;
  padding: 24px;
}

.placeholder-icon {
  width: 74px;
  height: 74px;
  margin: 0 auto 18px;
  border-radius: 22px;
  display: grid;
  place-items: center;
  background: var(--upload-button-bg);
  color: var(--upload-button-text);
  font-size: 28px;
  font-weight: 800;
}

.placeholder-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--upload-heading);
  margin-bottom: 8px;
}

.preview-box {
  position: relative;
  width: 100%;
  height: 100%;
}

.preview-img {
  width: 100%;
  height: 320px;
  object-fit: cover;
}

.replace-btn {
  position: absolute;
  right: 18px;
  bottom: 18px;
  min-height: 42px;
  padding: 0 18px;
  border: 1px solid var(--upload-status-border);
  border-radius: 999px;
  background: var(--upload-card-bg-strong);
  color: var(--upload-text);
  cursor: pointer;
}

.file-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
  padding: 18px;
  background: var(--upload-card-bg-strong);
  border: 1px solid var(--upload-card-border);
}

.summary-label {
  display: block;
  margin-bottom: 6px;
  color: var(--upload-text-soft);
  font-size: 12px;
}

.file-summary strong {
  color: var(--upload-heading);
  word-break: break-word;
}

.form-group label {
  display: block;
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 700;
  color: var(--upload-heading);
}

.form-group textarea {
  width: 100%;
  padding: 18px;
  border-radius: 20px;
  border: 1px solid var(--upload-input-border);
  background: var(--upload-input-bg);
  color: var(--upload-heading);
  resize: vertical;
  min-height: 190px;
  font-size: 15px;
  line-height: 1.8;
}

.form-group textarea:focus {
  outline: none;
  border-color: var(--upload-status-border-strong);
  box-shadow: 0 0 0 3px var(--upload-accent-bg);
}

.helper-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
  font-size: 12px;
}

.service-list {
  display: grid;
  gap: 14px;
  margin-top: 22px;
}

.service-item {
  padding: 18px;
  background: var(--upload-card-bg-strong);
  border: 1px solid var(--upload-card-border-soft);
}

.service-item strong {
  display: block;
  margin-bottom: 8px;
  color: var(--upload-heading);
}

.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-top: 24px;
  padding-top: 4px;
}

.submit-btn {
  min-width: 240px;
  min-height: 52px;
  padding: 0 24px;
  border: none;
  border-radius: 999px;
  background: var(--upload-button-bg);
  color: var(--upload-button-text);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease, opacity 0.25s ease;
  box-shadow: var(--upload-button-shadow);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--upload-button-shadow-hover);
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.text-link {
  color: var(--upload-accent-text);
  text-decoration: none;
  font-weight: 600;
}

.text-link:hover {
  color: var(--upload-heading);
}

@media (max-width: 1080px) {
  .hero-panel,
  .workspace-grid {
    grid-template-columns: 1fr;
  }

  .hero-metrics,
  .timeline-strip,
  .file-summary {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .upload-page {
    padding: 8px 12px 32px;
  }

  .hero-panel,
  .upload-panel,
  .form-panel {
    padding: 22px;
    border-radius: 24px;
  }

  .panel-head,
  .action-row,
  .helper-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .submit-btn,
  .text-link {
    width: 100%;
    text-align: center;
  }
}
</style>
