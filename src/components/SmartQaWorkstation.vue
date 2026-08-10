<template>
  <div class="smart-qa-workstation overflow-hidden flex flex-col">
    <div class="workspace-background">
      <span class="workspace-orb orb-left"></span>
      <span class="workspace-orb orb-right"></span>
      <span class="workspace-grid"></span>
    </div>

    <header class="workspace-header glass-panel">
      <div class="workspace-title-group">
        <button class="workspace-back-btn" @click="goBack">
          <iconify-icon class="text-xl" icon="material-symbols:arrow-back-ios-new-rounded"></iconify-icon>
        </button>

        <div class="workspace-title-block">
          <span class="workspace-kicker">医疗 AI 诊断工作台</span>
          <h1>智能问答诊断工作站</h1>
          <p>面向正式业务场景的健康咨询、报告解读与专家协同服务入口</p>
        </div>
      </div>

      <div class="workspace-status">
        <div class="workspace-status-chip">
          <span class="status-dot"></span>
          <span>{{ modelName }} 在线</span>
        </div>
        <div class="workspace-status-chip muted">
          <iconify-icon icon="mdi:calendar-clock"></iconify-icon>
          <span>{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <main class="flex-1 flex overflow-hidden workspace-main">
      <aside class="workspace-left">
        <div class="workspace-left-shell">
          <section class="workspace-side-card side-hero-card">
            <span class="side-hero-label">智能辅助问诊</span>
            <h3>快速发起高质量医疗咨询</h3>
            <p>适合健康问答、报告解读与专家会诊前的信息整理。</p>
          </section>

          <section class="workspace-side-block workspace-side-block-scroll">
            <div class="side-block-header">
              <h3>常用诊断指令</h3>
              <span>一键带入问题模板</span>
            </div>

            <div class="prompt-list custom-scrollbar">
              <button
                @click="sendQuickQuestion('感冒药用药建议')"
                class="prompt-card"
              >
                <div class="prompt-icon blue">
                  <iconify-icon class="text-2xl" icon="material-symbols:medication"></iconify-icon>
                </div>
                <div class="prompt-content">
                  <strong>感冒药用药建议</strong>
                  <span>适合发热、咳嗽、鼻塞等常见问题初步咨询</span>
                </div>
              </button>

              <button
                @click="sendQuickQuestion('高血压预防指南')"
                class="prompt-card"
              >
                <div class="prompt-icon orange">
                  <iconify-icon class="text-2xl" icon="material-symbols:favorite"></iconify-icon>
                </div>
                <div class="prompt-content">
                  <strong>高血压预防指南</strong>
                  <span>了解生活方式管理与日常风险控制建议</span>
                </div>
              </button>

              <button
                @click="sendQuickQuestion('糖尿病饮食计划')"
                class="prompt-card"
              >
                <div class="prompt-icon green">
                  <iconify-icon class="text-2xl" icon="material-symbols:nutrition"></iconify-icon>
                </div>
                <div class="prompt-content">
                  <strong>糖尿病饮食计划</strong>
                  <span>从饮食控制、作息安排到慢病管理建议</span>
                </div>
              </button>

              <button
                @click="sendQuickQuestion('失眠综合诊防')"
                class="prompt-card"
              >
                <div class="prompt-icon slate">
                  <iconify-icon class="text-2xl" icon="material-symbols:bedtime"></iconify-icon>
                </div>
                <div class="prompt-content">
                  <strong>失眠综合诊防</strong>
                  <span>快速整理症状、诱因与可执行改善方案</span>
                </div>
              </button>

              <button
                @click="sendQuickQuestion('体检报告专业解读')"
                class="prompt-card"
              >
                <div class="prompt-icon cyan">
                  <iconify-icon class="text-2xl" icon="material-symbols:health_and_safety"></iconify-icon>
                </div>
                <div class="prompt-content">
                  <strong>体检报告专业解读</strong>
                  <span>帮助用户快速理解异常指标与后续建议</span>
                </div>
              </button>

              <button
                @click="addTestMessage"
                class="prompt-card prompt-card-utility"
              >
                <div class="prompt-icon amber">
                  <iconify-icon class="text-2xl" icon="material-symbols:bug-report"></iconify-icon>
                </div>
              
              </button>
            </div>
          </section>

          <section class="workspace-side-card expert-card expert-card-fixed">
            <div class="expert-card-head">
              <div class="expert-avatar">
                <img :src="doctorImage" class="w-full h-full object-cover" alt="专家头像">
              </div>
              <div class="expert-meta">
                <strong>李华 教授</strong>
                <span>心内科 副主任医师</span>
              </div>
            </div>

            <div class="expert-tags">
              <span>专家会诊</span>
              <span>复杂病例支持</span>
              <span>报告复核</span>
            </div>

            <button
              @click="$router.push('/upload')"
              class="expert-action-btn"
            >
              立即发起会诊
            </button>
          </section>
        </div>
      </aside>

      <section class="flex-1 flex flex-col relative workspace-center">
        <div class="flex-1 overflow-y-auto p-6 custom-scrollbar pb-40 workspace-messages-scroll" ref="messagesContainer">
          <div class="conversation-shell">
            <section class="conversation-banner">
              <div class="conversation-banner-icon">
                <iconify-icon class="text-2xl" icon="material-symbols:shield-with-heart-rounded"></iconify-icon>
              </div>
              <div class="conversation-banner-body">
                <h4>AI 辅助决策声明</h4>
                <p>本系统提供的分析内容仅作辅助参考，最终诊断请结合临床信息并由专业医生判断。</p>
                <div class="conversation-banner-tags">
                  <span>支持文本问诊</span>
                  <span>支持图片上传</span>
                  <span>支持专家会诊</span>
                </div>
              </div>
            </section>

            <section class="conversation-suggestion-row">
              <button class="suggestion-pill" @click="sendQuickQuestion('请帮我解读近期体检报告中的异常指标')">
                解读体检异常指标
              </button>
              <button class="suggestion-pill" @click="sendQuickQuestion('根据头痛和乏力症状，给我一个初步健康建议')">
                头痛与乏力初步建议
              </button>
              <button class="suggestion-pill" @click="sendQuickQuestion('高血压患者如何进行日常饮食管理')">
                高血压日常管理
              </button>
            </section>

          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            :data-index="index"
            :class="[
              'flex conversation-row',
              msg.role === 'user' ? 'justify-end' : 'justify-start items-start gap-3',
              'transition-all duration-300',
              (highlightedMessageIndex === index || highlightedAiMessageIndex === index) ? 'highlighted-message' : ''
            ]"
          >
            <template v-if="msg.role === 'ai'">
              <!-- 修复：只在有内容时显示对话框。如果是最后一条AI消息且正在输入，交给 typing-indicator -->
              <div v-if="msg.content && msg.content.trim()" class="flex justify-start items-start gap-3 w-full">
                <div class="assistant-avatar">
                  <iconify-icon class="text-xl" icon="bx:bot"></iconify-icon>
                </div>
                <div 
                  :class="[
                    'assistant-bubble transition-all duration-300',
                    highlightedAiMessageIndex === index ? 'ring-4 ring-yellow-400 shadow-xl scale-[1.01]' : ''
                  ]"
                >
                  <div class="text-sm text-slate-700 message-content" v-html="linkifyText(msg.content)"></div>
                  <div v-if="msg.isError && showRetryButton" class="mt-3 flex gap-2">
                    <button @click="retrySend" class="retry-btn">
                      <iconify-icon icon="material-symbols:refresh"></iconify-icon>
                      重试
                    </button>
                  </div>
                  <div class="bubble-meta">{{ msg.time }}</div>
                  <div v-if="hasUrls(msg.content)" class="mt-2 flex items-center gap-2">
                    <button 
                      @click="copyAllUrls(msg.content)" 
                      class="utility-link"
                      title="复制所有链接"
                    >
                      <iconify-icon icon="material-symbols:content-copy"></iconify-icon>
                      复制链接
                    </button>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="max-w-[72%]">
                <div v-if="msg.files && msg.files.length > 0" class="mb-2 flex flex-wrap gap-3">
                  <div v-for="(file, fileIndex) in msg.files" :key="fileIndex" class="relative">
                    <div v-if="file.type.startsWith('image/')" class="message-image-container bg-slate-100 rounded-lg overflow-hidden border border-slate-200">
                      <img :src="file.preview" class="message-image" alt="图片" @click="openMessageImagePreview(file)">
                    </div>
                    <div v-else class="message-file-container bg-slate-100 rounded-lg flex flex-col items-center justify-center border border-slate-200">
                      <iconify-icon class="text-4xl text-slate-400" icon="material-symbols:insert-drive-file"></iconify-icon>
                      <span class="text-xs text-slate-600 mt-2 text-center px-2 truncate w-full">{{ file.name }}</span>
                    </div>
                  </div>
                </div>
                <div 
                  v-if="msg.content" 
                  :class="[
                    'user-bubble transition-all duration-300',
                    highlightedMessageIndex === index ? 'ring-4 ring-yellow-400 shadow-xl scale-[1.01]' : ''
                  ]"
                >
                  <p class="text-sm message-content" v-html="linkifyText(msg.content)"></p>
                  <span class="bubble-meta bubble-meta-light">{{ msg.time }}</span>
                </div>
                <div v-else class="text-right text-[10px] text-slate-400 mt-1">{{ msg.time }}</div>
              </div>
            </template>
          </div>

          <div v-if="isTyping && (!messages.length || messages[messages.length-1].role !== 'ai' || !messages[messages.length-1].content.trim())" class="flex justify-start items-start gap-3 conversation-row">
            <div class="assistant-avatar">
              <iconify-icon class="text-xl" icon="bx:bot"></iconify-icon>
            </div>
            <div class="assistant-typing">
              <div v-if="hasImageInPreview" class="flex flex-col gap-3">
                <div class="flex items-center gap-2">
                  <div class="typing-image-badge">
                    <iconify-icon class="text-white text-sm" icon="material-symbols:image"></iconify-icon>
                  </div>
                  <span class="text-sm font-semibold text-slate-700">Qwen3-VL-Plus 正在分析文件...</span>
                </div>
                <div class="flex items-center gap-2 text-xs text-slate-500">
                  <div class="typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                  <span>视觉理解中</span>
                </div>
              </div>
              <div v-else class="flex items-center h-[52px]">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="showReserveForm" class="flex justify-center">
            <div class="w-full max-w-2xl service-form-card">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
                  <iconify-icon class="text-blue-500" icon="material-symbols:calendar-today-rounded"></iconify-icon>
                  预约一对一健康咨询
                </h3>
                <button @click="showReserveForm = false" class="p-2 hover:bg-slate-100 rounded-lg transition-colors">
                  <iconify-icon class="text-xl text-slate-500" icon="material-symbols:close"></iconify-icon>
                </button>
              </div>
              <div class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">患者姓名 *</label>
                    <input v-model="reserveForm.patientName" type="text" class="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="请输入患者姓名">
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">患者性别 *</label>
                    <select v-model="reserveForm.patientGender" class="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                      <option value="">请选择</option>
                      <option value="男">男</option>
                      <option value="女">女</option>
                    </select>
                  </div>
                </div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">患者电话 *</label>
                    <input v-model="reserveForm.patientPhone" type="tel" class="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="请输入手机号码">
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-slate-700 mb-1">预约沟通时间 *</label>
                    <input v-model="reserveForm.communicationTime" type="datetime-local" class="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                  </div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-slate-700 mb-1">患者所在省份 *</label>
                  <input v-model="reserveForm.province" type="text" class="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="请输入所在省份">
                </div>
                <div>
                  <label class="block text-sm font-medium text-slate-700 mb-1">主要症状描述 *</label>
                  <textarea v-model="reserveForm.mainSymptoms" rows="3" class="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none" placeholder="请详细描述主要症状..."></textarea>
                </div>
                <div class="flex gap-3 pt-2">
                  <button @click="showReserveForm = false" class="flex-1 px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors">
                    取消
                  </button>
                  <button @click="submitReservation" :disabled="isReserving || !canSubmitReservation" class="flex-1 px-4 py-2 medical-gradient text-white rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2">
                    <iconify-icon v-if="isReserving" class="animate-spin" icon="material-symbols:sync"></iconify-icon>
                    <span>{{ isReserving ? '提交中...' : '确认预约' }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
          </div>
        </div>

        <div class="workspace-input-dock">
          <!-- 文件预览区域 -->
          <div v-if="previewFiles.length > 0" class="preview-strip">
            <div v-for="(file, index) in previewFiles" :key="index" class="relative flex-shrink-0 group">
              <!-- 加载状态 -->
              <div v-if="file.isLoading" class="w-24 h-24 bg-slate-100 rounded-lg border border-slate-200 flex items-center justify-center">
                <div class="flex flex-col items-center">
                  <iconify-icon class="text-2xl text-blue-500 animate-spin" icon="material-symbols:refresh"></iconify-icon>
                  <span class="text-xs text-slate-500 mt-1">加载中...</span>
                </div>
              </div>
              
              <!-- 错误状态 -->
              <div v-else-if="file.hasError" class="w-24 h-24 bg-red-50 rounded-lg border border-red-200 flex flex-col items-center justify-center">
                <iconify-icon class="text-2xl text-red-500" icon="material-symbols:error-outline"></iconify-icon>
                <span class="text-xs text-red-600 mt-1 text-center px-1 truncate">{{ file.errorMessage }}</span>
              </div>
              
              <!-- 图片预览 -->
              <div v-else-if="file.type.startsWith('image/')" class="file-preview-container bg-slate-100 rounded-lg overflow-hidden border border-slate-200">
                <img :src="file.preview" class="file-preview-image" alt="预览">
                <!-- 压缩状态标签 -->
                <div v-if="file.isCompressed" class="absolute top-1 left-1 bg-green-500 text-white text-[10px] px-1.5 py-0.5 rounded">
                  已压缩
                </div>
                <!-- 尺寸信息 -->
                <div class="absolute bottom-1 right-1 bg-black/60 text-white text-[10px] px-1.5 py-0.5 rounded">
                  {{ formatFileSize(file.size) }}
                </div>
              </div>
              
              <!-- 非图片文件预览 -->
              <div v-else class="file-preview-container bg-slate-100 rounded-lg flex flex-col items-center justify-center border border-slate-200">
                <iconify-icon :class="getFileIconClass(file.type)" :icon="getFileIcon(file.type)"></iconify-icon>
                <span class="text-xs text-slate-600 mt-1 text-center px-1 truncate w-full">{{ file.name }}</span>
                <span class="text-[10px] text-slate-400 mt-0.5">{{ formatFileSize(file.size) }}</span>
              </div>
              
              <!-- 删除按钮 -->
              <button @click="removeFile(index)" class="absolute -top-2 -right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 transition-all opacity-0 group-hover:opacity-100 z-10 shadow-md">
                <iconify-icon class="text-sm" icon="material-symbols:close"></iconify-icon>
              </button>
              
              <!-- 上传进度 -->
              <div v-if="file.uploadProgress !== undefined" class="absolute bottom-0 left-0 right-0 h-1.5 bg-gray-200 rounded-b-lg overflow-hidden">
                <div class="h-full bg-gradient-to-r from-blue-500 to-blue-600 rounded transition-all duration-300" :style="{ width: file.uploadProgress + '%' }"></div>
              </div>
            </div>
          </div>

          <!-- 全新设计的输入区域 - 极简布局 -->
          <div class="w-full max-w-4xl">
            <div class="input-helper-row">
              <span>支持文本、图片和文件上传，适合正式用户问诊与报告解读场景</span>
              <span>按 Enter 发送，Shift + Enter 换行</span>
            </div>
            <div 
              class="chat-input-container-minimal input-panel"
              :class="{ 'drag-over': isDragOver }"
              @dragover.prevent="handleDragOver"
              @dragleave="handleDragLeave"
              @drop.prevent="handleDrop"
            >
              <!-- 左下角：联网搜索按钮 -->
              <div class="chat-bottom-left">
                <button 
                  @click="webSearchEnabled = !webSearchEnabled"
                  :class="[
                    'minimal-action-button',
                    webSearchEnabled ? 'minimal-action-button-active' : ''
                  ]"
                  :aria-label="webSearchEnabled ? '关闭联网搜索' : '开启联网搜索'"
                  :title="webSearchEnabled ? '点击关闭联网搜索' : '点击开启联网搜索'"
                >
                  <span class="minimal-button-text" :class="webSearchEnabled ? 'text-white' : ''">联网搜索</span>
                </button>
              </div>

              <!-- 中间：输入框 -->
              <div class="chat-input-center">
                <textarea 
                  v-model="inputMessage"
                  class="chat-textarea-minimal"
                  placeholder="输入您的问题..." 
                  rows="1"
                  @keydown.enter.prevent="handleEnter"
                  ref="textareaRef"
                  aria-label="输入消息"
                ></textarea>
              </div>

              <!-- 右下角：上传按钮 + 发送/停止按钮 -->
              <div class="chat-bottom-right">
                <!-- 上传按钮组 -->
                <div class="upload-buttons-group">
                  <!-- 图片上传 -->
                  <input type="file" ref="imageInput" accept="image/*" @change="handleImageUpload" class="hidden">
                  <button 
                    @click="$refs.imageInput.click()" 
                    class="minimal-icon-button"
                    :aria-label="'上传图片'"
                    :title="'上传图片'"
                  >
                    <iconify-icon icon="material-symbols:image"></iconify-icon>
                  </button>

                  <!-- 文件上传 -->
                  <input type="file" ref="fileInput" @change="handleFileUpload" class="hidden">
                  <button 
                    @click="$refs.fileInput.click()" 
                    class="minimal-icon-button"
                    :aria-label="'上传文件'"
                    :title="'上传文件'"
                  >
                    <iconify-icon icon="material-symbols:attach-file"></iconify-icon>
                  </button>
                </div>

                <!-- 分隔线 -->
                <div class="minimal-divider"></div>

                <!-- 发送/停止按钮 -->
                <button 
                  :class="[
                    'minimal-send-button',
                    isTyping ? 'minimal-send-button-stop' : '',
                    (!isTyping && !inputMessage.trim() && previewFiles.length === 0) ? 'minimal-send-button-disabled' : ''
                  ]"
                  @click="isTyping ? stopGeneration() : sendMessage()"
                  :disabled="!isTyping && !inputMessage.trim() && previewFiles.length === 0"
                  :aria-label="isTyping ? '停止生成' : '发送消息'"
                  :title="isTyping ? '点击停止生成' : '点击发送消息'"
                >
                  <iconify-icon 
                    v-if="isTyping" 
                    icon="material-symbols:stop"
                  ></iconify-icon>
                  <iconify-icon 
                    v-else 
                    icon="material-symbols:arrow-upward"
                  ></iconify-icon>
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 大图预览覆盖层 -->
      <transition name="fade">
        <div v-if="previewingMessageImage" class="message-image-preview-overlay" @click="closeMessageImagePreview">
          <div class="message-image-preview-container" @click.stop>
            <div class="message-image-preview-header">
              <span class="message-image-preview-title">{{ previewingMessageImage.name }}</span>
              <button class="message-image-preview-close" @click="closeMessageImagePreview">
                <iconify-icon icon="material-symbols:close"></iconify-icon>
              </button>
            </div>
            <div class="message-image-preview-content">
              <img 
                :src="previewingMessageImage.preview" 
                class="message-image-preview-img" 
                alt="预览"
                @load="onMessageImageLoad"
              >
            </div>
            <div class="message-image-preview-footer">
              <span v-if="previewingMessageImageDimensions" class="message-image-preview-dimensions">
                {{ previewingMessageImageDimensions.width }} × {{ previewingMessageImageDimensions.height }} px
              </span>
              <span class="message-image-preview-size">{{ formatFileSize(previewingMessageImage.size) }}</span>
            </div>
          </div>
        </div>
      </transition>

      <aside class="workspace-right workspace-right-panel flex flex-col">
        <div class="p-5 border-b border-slate-200">
          <div class="flex items-center justify-between mb-3">
            <h3 class="text-sm font-bold text-slate-700 flex items-center gap-2">
              <iconify-icon class="text-slate-400" icon="material-symbols:history-rounded"></iconify-icon>
              对话历史
            </h3>
            
            <!-- 清空历史按钮 -->
            <button 
              @click="clearHistory"
              class="flex items-center gap-1 px-3 py-1.5 bg-red-100 text-red-600 rounded-lg text-xs font-medium hover:bg-red-200 transition-colors"
              title="清空历史记录"
            >
              <iconify-icon icon="material-symbols:delete-outline"></iconify-icon>
              清空
            </button>
          </div>
          
          <!-- 搜索框 -->
          <div class="relative">
            <iconify-icon class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400" icon="material-symbols:search"></iconify-icon>
            <input 
              v-model="searchKeyword"
              type="text"
              placeholder="搜索对话内容..."
              class="history-search-input"
              @input="filterMessages"
            >
          </div>
        </div>
        
        <!-- 对话历史列表（消息级） -->
        <div class="workspace-history-shell">
          <div 
            ref="historyListRef"
            class="space-y-3 custom-scrollbar workspace-history-scroll"
            tabindex="0"
            @keydown="handleHistoryKeydown"
          >
            <div v-if="filteredMessageHistory.length === 0" class="text-center text-slate-400 text-xs py-8">
              <iconify-icon class="text-3xl mb-2 text-slate-300" icon="material-symbols:chat-bubble-outline"></iconify-icon>
              <p>暂无对话记录</p>
            </div>
            
            <div 
              v-for="(msg, index) in paginatedMessageHistory" 
              :key="msg.historyId"
              @click="locateMessageInChat(msg.messageIndex)"
              @mouseenter="selectedHistoryIndex = index"
              @mouseleave="selectedHistoryIndex = null"
              :class="[
                'history-item-card',
                selectedHistoryIndex === index 
                  ? 'history-item-active' 
                  : 'history-item-default',
                msg.role === 'user' ? 'ml-4' : 'mr-4'
              ]"
            >
              <div class="flex items-start justify-between mb-1">
                <span class="text-xs font-bold" :class="msg.role === 'user' ? 'text-blue-600' : 'text-green-600'">
                  <iconify-icon class="inline mr-1" :icon="msg.role === 'user' ? 'material-symbols:person' : 'material-symbols:smart-toy'"></iconify-icon>
                  {{ msg.role === 'user' ? '您' : 'AI' }}
                </span>
                <span class="text-[10px] text-slate-400">
                  {{ msg.timestamp }}
                </span>
              </div>
              
              <p class="text-xs text-slate-700 line-clamp-3">
                {{ (msg.content || '').substring(0, 100) }}{{ (msg.content || '').length > 100 ? '...' : '' }}
              </p>
            </div>
          </div>
        </div>
        
        <!-- 分页控件 -->
        <div v-if="totalPages > 1" class="p-4 border-t border-slate-200">
          <div class="flex items-center justify-between">
            <button 
              @click="prevPage"
              :disabled="currentPage === 1"
              :class="[
                'px-3 py-1.5 rounded-lg text-sm font-medium transition-all',
                currentPage === 1 
                  ? 'bg-slate-100 text-slate-400 cursor-not-allowed' 
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              ]"
            >
              上一页
            </button>
            
            <span class="text-xs text-slate-500">
              第 {{ currentPage }} / {{ totalPages }} 页
            </span>
            
            <button 
              @click="nextPage"
              :disabled="currentPage === totalPages"
              :class="[
                'px-3 py-1.5 rounded-lg text-sm font-medium transition-all',
                currentPage === totalPages 
                  ? 'bg-slate-100 text-slate-400 cursor-not-allowed' 
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              ]"
            >
              下一页
            </button>
          </div>
        </div>
      </aside>
    </main>
  </div>
</template>

<script>
import { smartQaAPI, healthConsultationAPI } from '../api/api.js'
import { linkifier } from '../utils/url-linkifier.js'
import navigationHistory from '@/utils/navigationHistory'

export default {
  name: 'SmartQaWorkstation',
  data() {
    return {
      currentTime: '',
      inputMessage: '',
      isTyping: false,
      messages: [],
      memoryId: this.generateMemoryId(),
      showReserveForm: false,
      awaitingReservationReply: false,
      reserveForm: {
        patientName: '',
        patientGender: '',
        patientPhone: '',
        communicationTime: '',
        province: '',
        mainSymptoms: ''
      },
      isReserving: false,
      previewFiles: [],
      doctorImage: require('../views/element/image/4.png'),
      webSearchEnabled: false,
      abortController: null,
      isDragOver: false,
      retryCount: 0,
      maxRetryCount: 3,
      lastError: null,
      showRetryButton: false,
      // 对话历史相关
      allMessageHistory: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 20,
      totalMessages: 0,
      modelName: 'Qwen-Plus',
      selectedHistoryIndex: null,
      highlightedMessageIndex: null,
      highlightedAiMessageIndex: null,
      highlightTimeout: null,
      clockTimer: null,
      pageScrollLockState: null,
      // 消息图片大图预览
      previewingMessageImage: null,
      previewingMessageImageDimensions: null,
      isAnalyzingImage: false
    }
  },
  mounted() {
    this.lockPageScroll()
    this.updateTime()
    this.clockTimer = setInterval(this.updateTime, 1000)
    this.addWelcomeMessage()
  },
  beforeUnmount() {
    if (this.clockTimer) {
      clearInterval(this.clockTimer)
      this.clockTimer = null
    }
    this.unlockPageScroll()
  },
  methods: {
    lockPageScroll() {
      const html = document.documentElement
      const body = document.body

      this.pageScrollLockState = {
        htmlOverflow: html.style.overflow,
        bodyOverflow: body.style.overflow,
        bodyOverscrollBehavior: body.style.overscrollBehavior
      }

      html.style.overflow = 'hidden'
      body.style.overflow = 'hidden'
      body.style.overscrollBehavior = 'none'
    },
    unlockPageScroll() {
      if (!this.pageScrollLockState) {
        return
      }

      const html = document.documentElement
      const body = document.body

      html.style.overflow = this.pageScrollLockState.htmlOverflow
      body.style.overflow = this.pageScrollLockState.bodyOverflow
      body.style.overscrollBehavior = this.pageScrollLockState.bodyOverscrollBehavior
      this.pageScrollLockState = null
    },
    goBack() {
      this.$router.push('/patient/home')
    },
    linkifyText(text) {
      return linkifier.toHtmlWithBreaks(text)
    },
    checkShouldShowReserveForm() {
      if (this.messages.length < 1) return false

      const lastAiMessage = this.messages[this.messages.length - 1]
      if (!lastAiMessage || lastAiMessage.role !== 'ai') return false

      const aiContent = String(lastAiMessage.content || '')

      const completeReservationFields = [
        '\u60a3\u8005\u59d3\u540d',
        '\u60a3\u8005\u6027\u522b',
        '\u8054\u7cfb\u7535\u8bdd',
        '\u9884\u7ea6\u6c9f\u901a\u65f6\u95f4',
        '\u6240\u5728\u7701\u4efd',
        '\u4e3b\u8981\u75c7\u72b6'
      ]

      const confirmReservationPrompts = [
        '\u4e3a\u60a8\u5b89\u6392\u9884\u7ea6',
        '\u4e3a\u60a8\u5b89\u6392\u4e00\u5bf9\u4e00',
        '\u5065\u5eb7\u54a8\u8be2\u670d\u52a1',
        '\u8bf7\u60a8\u63d0\u4f9b\u4ee5\u4e0b',
        '\u63d0\u4ea4\u9884\u7ea6',
        '\u5b89\u6392\u54a8\u8be2',
        '\u5b89\u6392\u95ee\u8bca',
        '\u9884\u7ea6\u4e13\u5bb6',
        '\u9884\u7ea6\u533b\u751f',
        '\u9884\u7ea6\u8868\u5355'
      ]

      const reservationKeywords = [
        '\u9884\u7ea6',
        '\u4e00\u5bf9\u4e00',
        '\u5065\u5eb7\u54a8\u8be2',
        '\u54a8\u8be2\u65f6\u95f4',
        '\u63d0\u4ea4\u9884\u7ea6',
        '\u5b89\u6392\u54a8\u8be2',
        '\u5b89\u6392\u95ee\u8bca',
        '\u9884\u7ea6\u6c9f\u901a\u65f6\u95f4',
        '\u9884\u7ea6\u533b\u751f',
        '\u9884\u7ea6\u4e13\u5bb6',
        '\u95e8\u8bca\u65f6\u95f4'
      ]

      const reservationExclusionKeywords = [
        '\u9884\u7ea6\u6210\u529f',
        '\u5df2\u7ecf\u9884\u7ea6\u6210\u529f',
        '\u65e0\u9700\u9884\u7ea6',
        '\u6682\u4e0d\u9700\u8981\u9884\u7ea6',
        '\u4e0d\u7528\u9884\u7ea6'
      ]

      const isCompleteReservationPrompt = completeReservationFields.every((field) => aiContent.includes(field))
      const isSimpleReservationConfirm = confirmReservationPrompts.some((keyword) => aiContent.includes(keyword))
      const hasReservationIntent = reservationKeywords.some((keyword) => aiContent.includes(keyword))
      const hasReservationExclusion = reservationExclusionKeywords.some((keyword) => aiContent.includes(keyword))

      return isCompleteReservationPrompt || isSimpleReservationConfirm || (hasReservationIntent && !hasReservationExclusion)
    },
    shouldWaitForReservationReply(text) {
      const content = String(text || '')
      const reservationQuestionKeywords = [
        '是否需要预约',
        '需要预约吗',
        '是否需要我为您预约',
        '要不要帮您预约',
        '是否要预约',
        '需要我帮您预约吗'
      ]

      return reservationQuestionKeywords.some((keyword) => content.includes(keyword))
    },
    isReservationAffirmativeReply(text) {
      const content = String(text || '').trim()
      if (!content) return false

      const normalizedContent = content.replace(/\s+/g, '')
      const negativeKeywords = [
        '不需要',
        '不用',
        '暂时不用',
        '先不用',
        '暂不预约',
        '不用预约',
        '先不预约',
        '不预约',
        '否'
      ]

      if (negativeKeywords.some((keyword) => normalizedContent.includes(keyword))) {
        return false
      }

      const affirmativeKeywords = [
        '需要',
        '要',
        '好的',
        '好',
        '可以',
        '行',
        '预约',
        '帮我预约',
        '请帮我预约',
        '那就预约',
        '安排一下'
      ]

      return affirmativeKeywords.some((keyword) => normalizedContent.includes(keyword))
    },
    generateMemoryId() {
      return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    },
    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    addWelcomeMessage() {
      this.messages.push({
        role: 'ai',
        content: '您好，我是您的智能医疗助手。\n\n您可以向我咨询：\n• 常见症状的初步分析\n• 体检报告与指标解读\n• 慢病管理与用药建议\n• 专家会诊前的信息整理\n\n温馨提示：本系统内容仅供辅助参考，不能替代执业医生的线下诊断。',
        time: this.getCurrentTime()
      })
    },
    addTestMessage() {
      console.log('[SmartQA] 添加测试消息')
      const testMessage = {
        role: 'ai',
        content: '这是一个 URL 链接展示测试消息，包含多种格式的网址：\n\n' +
          '1. 完整 HTTPS 协议：https://www.nhc.gov.cn\n' +
          '2. 完整 HTTP 协议：http://www.example.com\n' +
          '3. WWW 开头域名：www.cma.org.cn\n' +
          '4. 另一个 WWW 域名：www.nhsa.gov.cn\n' +
          '5. 带路径的 URL：https://www.cdc.cn/ncdc/index.htm\n\n' +
          '这些链接应显示为可点击状态，并支持在新标签页打开。',
        time: this.getCurrentTime()
      }
      this.messages.push(testMessage)
      console.log('[SmartQA] 测试消息已添加:', testMessage)
      this.scrollToBottom()
    },
    getCurrentTime() {
      const now = new Date()
      return now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    },
    getCurrentDateTime() {
      const now = new Date()
      return now.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    addMessageToHistory(message, messageIndex) {
      const historyMessage = {
        historyId: 'msg_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
        role: message.role,
        content: message.content,
        timestamp: this.getCurrentDateTime(),
        messageIndex: messageIndex,
        sessionId: this.memoryId,
        createdAt: Date.now()
      }
      
      // 添加到历史记录（时间倒序）
      this.allMessageHistory.unshift(historyMessage)
      this.totalMessages = this.allMessageHistory.length
      
      // 限制历史记录数量（最多1000条）
      if (this.allMessageHistory.length > 1000) {
        this.allMessageHistory = this.allMessageHistory.slice(0, 1000)
      }
    },
    filterMessages() {
      this.currentPage = 1
    },
    locateMessageInChat(messageIndex) {
      if (messageIndex === undefined || messageIndex === null) return
      
      const startTime = performance.now()
      
      // 检查消息索引是否有效
      if (messageIndex < 0 || messageIndex >= this.messages.length) {
        console.warn('消息索引无效:', messageIndex)
        return
      }
      
      // 设置高亮 - 同时高亮用户问题和对应的AI回复
      this.highlightedMessageIndex = messageIndex
      
      // 同时标记下一条消息（AI回复）也高亮
      const aiMessageIndex = messageIndex + 1
      this.highlightedAiMessageIndex = aiMessageIndex < this.messages.length ? aiMessageIndex : null
      
      // 平滑滚动到目标消息
      this.$nextTick(() => {
        this.scrollToMessage(messageIndex)
      })
      
      // 3秒后取消高亮
      if (this.highlightTimeout) {
        clearTimeout(this.highlightTimeout)
      }
      this.highlightTimeout = setTimeout(() => {
        this.highlightedMessageIndex = null
        this.highlightedAiMessageIndex = null
      }, 3000)
      
      const endTime = performance.now()
      console.log(`消息定位响应时间: ${(endTime - startTime).toFixed(2)}ms`)
    },
    scrollToMessage(messageIndex) {
      const chatContainer = this.$refs.messagesContainer
      if (!chatContainer) return
      
      // 使用 data-index 属性精确查找目标消息元素
      const targetElement = chatContainer.querySelector(`[data-index="${messageIndex}"]`)
      
      if (targetElement) {
        // 平滑滚动到目标消息
        targetElement.scrollIntoView({
          behavior: 'smooth',
          block: 'center'
        })
      } else {
        // 降级方案：滚动到底部
        this.scrollToBottom()
      }
    },
    handleHistoryKeydown(event) {
      if (this.filteredMessageHistory.length === 0) return
      
      const maxIndex = this.paginatedMessageHistory.length - 1
      
      if (event.key === 'ArrowUp') {
        event.preventDefault()
        if (this.selectedHistoryIndex === null || this.selectedHistoryIndex <= 0) {
          this.selectedHistoryIndex = maxIndex
        } else {
          this.selectedHistoryIndex--
        }
        this.scrollHistoryListToSelected()
      } else if (event.key === 'ArrowDown') {
        event.preventDefault()
        if (this.selectedHistoryIndex === null || this.selectedHistoryIndex >= maxIndex) {
          this.selectedHistoryIndex = 0
        } else {
          this.selectedHistoryIndex++
        }
        this.scrollHistoryListToSelected()
      } else if (event.key === 'Enter') {
        event.preventDefault()
        if (this.selectedHistoryIndex !== null && this.paginatedMessageHistory[this.selectedHistoryIndex]) {
          const msg = this.paginatedMessageHistory[this.selectedHistoryIndex]
          this.locateMessageInChat(msg.messageIndex)
        }
      }
    },
    scrollHistoryListToSelected() {
      if (!this.$refs.historyListRef || this.selectedHistoryIndex === null) return
      
      const historyItems = this.$refs.historyListRef.querySelectorAll('[class*="rounded-xl"]')
      if (historyItems[this.selectedHistoryIndex]) {
        historyItems[this.selectedHistoryIndex].scrollIntoView({
          behavior: 'smooth',
          block: 'nearest'
        })
      }
    },
    clearHistory() {
      if (confirm('确定要清空所有对话历史吗？此操作不可恢复。')) {
        this.allMessageHistory = []
        this.totalMessages = 0
        this.selectedHistoryIndex = null
        this.currentPage = 1
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.selectedHistoryIndex = null
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.selectedHistoryIndex = null
      }
    },
    handleEnter(e) {
      if (!e.shiftKey) {
        this.sendMessage()
      }
    },
    sendQuickQuestion(question) {
      this.inputMessage = question
      this.sendMessage()
    },
    stopGeneration() {
      if (this.abortController) {
        this.abortController.abort()
        this.isTyping = false
        this.abortController = null
        // 提示用户已终止
        // 注意：这里由于没有全局 toast 组件，可以根据项目实际情况添加 UI 提示
        console.log('用户终止了回答生成')
      }
    },
    async sendMessage() {
      if ((!this.inputMessage.trim() && this.previewFiles.length === 0) || this.isTyping) {
        return
      }

      const userMessage = this.inputMessage.trim()
      this.inputMessage = ''

      if (this.awaitingReservationReply && this.previewFiles.length === 0) {
        if (this.isReservationAffirmativeReply(userMessage)) {
          const messageData = {
            role: 'user',
            content: userMessage,
            time: this.getCurrentTime()
          }
          this.messages.push(messageData)
          this.addMessageToHistory(messageData, this.messages.length - 1)
          this.awaitingReservationReply = false
          this.showReserveForm = true
          this.$nextTick(() => this.scrollToBottom())
          return
        }

        this.awaitingReservationReply = false
      }

      const messageData = {
        role: 'user',
        content: userMessage,
        time: this.getCurrentTime()
      }

      if (this.previewFiles.length > 0) {
        messageData.files = [...this.previewFiles]
      }

      this.messages.push(messageData)
      
      // 添加用户消息到历史记录
      this.addMessageToHistory(messageData, this.messages.length - 1)
      
      const filesToSend = [...this.previewFiles]
      this.previewFiles = []

      this.isTyping = true
      this.scrollToBottom()

      const hasAttachment = filesToSend.length > 0

      if (hasAttachment) {
        let aiMessageIndex = -1
        try {
          this.showRetryButton = false
          this.lastError = null
          this.isAnalyzingImage = true

          const attachmentFile = filesToSend.find(f => f && f.file)
          let isImage = false
          if (attachmentFile && attachmentFile.file) {
            // 文件大小检查（最大50MB）
            if (attachmentFile.file.size > 50 * 1024 * 1024) {
              throw new Error('文件大小超过50MB限制')
            }

            const mimeType = (attachmentFile.file.type || '').toLowerCase()
            const fileName = (attachmentFile.file.name || '').toLowerCase()
            isImage = mimeType.startsWith('image/')
            const isPdf = mimeType.includes('pdf') || fileName.endsWith('.pdf')
            const isWord = mimeType.includes('word') || mimeType.includes('officedocument') || fileName.endsWith('.doc') || fileName.endsWith('.docx')
            const isText = mimeType.startsWith('text/') || fileName.endsWith('.txt') || fileName.endsWith('.md') || fileName.endsWith('.csv') || fileName.endsWith('.json')

            if (!(isImage || isPdf || isWord || isText)) {
              throw new Error('不支持的文件格式，请上传图片、PDF 或 Word/TXT 文档')
            }
          }

          const buildAttachmentFormData = () => {
            const formData = new FormData()
            formData.append('userMessage', userMessage || '请分析我上传的文件并给出建议')

            if (attachmentFile && attachmentFile.file) {
              if (isImage) {
                formData.append('image', attachmentFile.file)
              }
              formData.append('file', attachmentFile.file)
            }

            return formData
          }

          const waitingContent = '正在解析上传文件，Qwen3-VL-Plus 将在首个片段返回后开始流式输出...'
          let hasReceivedAttachmentChunk = false

          // 添加模型选择提示消息占位符
          aiMessageIndex = this.messages.length
          this.messages.push({
            role: 'ai',
            content: waitingContent,
            time: this.getCurrentTime(),
            modelInfo: 'Qwen3-VL-Plus',
            isPending: true
          })
          this.$nextTick(() => this.scrollToBottom())

          const finalizeAttachmentResponse = () => {
            const aiMessage = this.messages[aiMessageIndex]
            if (!aiMessage || !aiMessage.content) {
              return
            }

            this.addMessageToHistory(aiMessage, aiMessageIndex)

            if (this.checkShouldShowReserveForm()) {
              if (this.shouldWaitForReservationReply(aiMessage.content)) {
                this.awaitingReservationReply = true
              } else {
                setTimeout(() => {
                  this.showReserveForm = true
                  this.scrollToBottom()
                }, 300)
              }
            }
          }

          this.abortController = new AbortController()
          let attachmentStreamError = null

          try {
            await smartQaAPI.chatWithImageStream(
              buildAttachmentFormData(),
              (chunk) => {
                if (this.isTyping && chunk) {
                  if (!hasReceivedAttachmentChunk) {
                    hasReceivedAttachmentChunk = true
                    this.messages[aiMessageIndex].content = ''
                    this.messages[aiMessageIndex].isPending = false
                  }
                  this.messages[aiMessageIndex].content += chunk
                  this.scrollToBottom()
                }
              },
              () => {
                if (!hasReceivedAttachmentChunk && this.messages[aiMessageIndex].content === waitingContent) {
                  this.messages[aiMessageIndex].content = '服务返回为空，请稍后重试'
                  this.messages[aiMessageIndex].isPending = false
                } else if (!this.messages[aiMessageIndex].content.trim()) {
                  this.messages[aiMessageIndex].content = '服务返回为空，请稍后重试'
                }
                this.isTyping = false
                this.isAnalyzingImage = false
                this.abortController = null
                finalizeAttachmentResponse()
                this.scrollToBottom()
              },
              (error) => {
                attachmentStreamError = error
                if (error.name === 'AbortError') {
                  console.log('附件分析请求被用户终止')
                } else {
                  console.error('附件流式分析错误:', error)
                }
              },
              this.abortController.signal
            )

            if (attachmentStreamError) {
              throw attachmentStreamError
            }
          } catch (streamError) {
            if (streamError.name === 'AbortError') {
              throw streamError
            }

            console.warn('附件流式接口失败，回退到非流式接口:', streamError)
            const response = await smartQaAPI.chatWithImage(buildAttachmentFormData())
            if (response?.data?.success === false) {
              throw new Error(response?.data?.message || '文件分析失败')
            }

            const fullResponse = this.extractSmartQaResponse(response?.data)
            if (!fullResponse) {
              throw new Error('服务返回为空，请稍后重试')
            }

            this.messages[aiMessageIndex].content = fullResponse
            this.messages[aiMessageIndex].isPending = false
            finalizeAttachmentResponse()
          }
        } catch (error) {
          if (error.name !== 'AbortError') {
            console.error('图片分析错误:', error)

            const errorMessage = this.getErrorMessage(error)
            if (aiMessageIndex >= 0 && this.messages[aiMessageIndex]) {
              this.messages[aiMessageIndex].content = `❌ 抱歉，图片分析失败。\n\n错误原因：${errorMessage}\n\n建议：检查网络连接或稍后再试。`
              this.messages[aiMessageIndex].isError = true
              this.messages[aiMessageIndex].isPending = false
            } else {
              this.messages.push({
                role: 'ai',
                content: `❌ 抱歉，图片分析失败。\n\n错误原因：${errorMessage}\n\n建议：检查网络连接或稍后再试。`,
                time: this.getCurrentTime(),
                isError: true
              })
            }
          }
          this.showRetryButton = false
        } finally {
          this.isTyping = false
          this.isAnalyzingImage = false
          this.abortController = null
          this.scrollToBottom()
        }
      } else {
        try {
          // Send request with stream
          
          // We only push ONE empty message placeholder for the AI's response
          const aiMessageIndex = this.messages.length
          this.messages.push({
            role: 'ai',
            content: '',
            time: this.getCurrentTime()
          })

          this.abortController = new AbortController()
          
          await smartQaAPI.chatStream(
            this.memoryId, // Keep using same memoryId for context
            userMessage,
            (chunk) => {
              // 只有在 typing 状态且没有被 abort 时才更新内容
              if (this.isTyping) {
                // 过滤可能存在的空 chunk
                if (chunk && chunk.trim() !== '') {
                  this.messages[aiMessageIndex].content += chunk
                  console.log('[SmartQA] 收到chunk，当前内容长度:', this.messages[aiMessageIndex].content.length)
                }
                this.scrollToBottom()
              }
            },
            () => {
              this.isTyping = false
              this.abortController = null
              this.scrollToBottom()
              
              console.log('[SmartQA] AI回复完成，最终内容:', this.messages[aiMessageIndex].content)
              
              // 添加AI消息到历史记录
              if (this.messages.length > 0) {
                const lastAiMessage = this.messages[this.messages.length - 1]
                if (lastAiMessage && lastAiMessage.role === 'ai') {
                  this.addMessageToHistory(lastAiMessage, this.messages.length - 1)
                }
              }
              
              // 检查是否需要显示预约表单
              if (this.checkShouldShowReserveForm()) {
                if (this.shouldWaitForReservationReply(this.messages[aiMessageIndex].content)) {
                  this.awaitingReservationReply = true
                } else {
                  setTimeout(() => {
                    this.showReserveForm = true
                    this.scrollToBottom()
                  }, 300)
                }
              }
            },
            (error) => {
              if (error.name === 'AbortError') {
                console.log('请求被用户终止')
              } else {
                console.error('聊天错误:', error)
                this.messages[aiMessageIndex].content = '抱歉，发生了错误，请稍后再试。' + (error.message || '')
              }
              this.isTyping = false
              this.abortController = null
              this.scrollToBottom()
            },
            this.webSearchEnabled, // 传递联网搜索参数
            this.abortController.signal
          )
        } catch (error) {
          if (error.name !== 'AbortError') {
            console.error('聊天错误:', error)
            // If the stream request fails entirely before starting
            if (this.messages.length > 0 && this.messages[this.messages.length - 1].role === 'ai' && this.messages[this.messages.length - 1].content === '') {
               this.messages[this.messages.length - 1].content = '抱歉，发生了错误，请稍后再试。' + (error.message || '')
            } else {
               this.messages.push({
                 role: 'ai',
                 content: '抱歉，发生了错误，请稍后再试。' + (error.message || ''),
                 time: this.getCurrentTime()
               })
            }
          }
          this.isTyping = false
          this.abortController = null
          this.scrollToBottom()
        }
      }
    },
    handleImageUpload(event) {
      const files = event.target.files
      if (files && files.length > 0) {
        this.processFiles(files)
      }
      event.target.value = ''
    },
    handleFileUpload(event) {
      const files = event.target.files
      if (files && files.length > 0) {
        this.processFiles(files)
      }
      event.target.value = ''
    },
    processFiles(files) {
      Array.from(files).forEach(async (file) => {
        const fileData = {
          file: file,
          name: file.name,
          type: file.type,
          size: file.size,
          originalSize: file.size,
          preview: null,
          uploadProgress: 0,
          isCompressed: false,
          isLoading: true,
          hasError: false,
          errorMessage: ''
        }

        this.previewFiles.push(fileData)
        const index = this.previewFiles.length - 1
        
        try {
          if (file.type.startsWith('image/')) {
            await this.processImageFile(fileData, index)
          } else {
            await this.processNonImageFile(fileData, index)
          }
        } catch (error) {
          console.error('文件处理失败:', error)
          this.previewFiles[index].hasError = true
          this.previewFiles[index].errorMessage = error.message || '文件处理失败'
          this.previewFiles[index].isLoading = false
        }
      })
    },
    
    async processImageFile(fileData, index) {
      try {
        const compressedResult = await this.compressImage(fileData.file)
        
        if (compressedResult) {
          fileData.file = compressedResult.file
          fileData.size = compressedResult.file.size
          fileData.preview = compressedResult.preview
          fileData.isCompressed = compressedResult.isCompressed
          
          console.log(`图片压缩完成: ${this.formatFileSize(fileData.originalSize)} → ${this.formatFileSize(fileData.size)}`)
        }
      } catch (error) {
        console.warn('图片压缩失败，使用原始文件:', error)
        const reader = new FileReader()
        reader.onload = (e) => {
          fileData.preview = e.target.result
          fileData.isLoading = false
        }
        reader.onerror = () => {
          fileData.hasError = true
          fileData.errorMessage = '图片加载失败'
          fileData.isLoading = false
        }
        reader.readAsDataURL(fileData.file)
        return
      }
      
      fileData.isLoading = false
      this.simulateUploadProgress(index)
    },
    
    async processNonImageFile(fileData, index) {
      fileData.preview = null
      fileData.isLoading = false
      this.simulateUploadProgress(index)
    },
    
    async compressImage(file) {
      return new Promise((resolve, reject) => {
        const maxSize = 500 * 1024
        const quality = 0.75
        
        if (file.size <= maxSize) {
          const reader = new FileReader()
          reader.onload = (e) => {
            resolve({
              file: file,
              preview: e.target.result,
              isCompressed: false
            })
          }
          reader.onerror = reject
          reader.readAsDataURL(file)
          return
        }
        
        const img = new Image()
        const url = URL.createObjectURL(file)
        
        img.onload = () => {
          URL.revokeObjectURL(url)
          
          let { width, height } = img
          const maxDimension = 1920
          
          if (width > maxDimension || height > maxDimension) {
            if (width > height) {
              height = Math.round((height / width) * maxDimension)
              width = maxDimension
            } else {
              width = Math.round((width / height) * maxDimension)
              height = maxDimension
            }
          }
          
          const canvas = document.createElement('canvas')
          canvas.width = width
          canvas.height = height
          
          const ctx = canvas.getContext('2d')
          ctx.fillStyle = '#ffffff'
          ctx.fillRect(0, 0, width, height)
          ctx.drawImage(img, 0, 0, width, height)
          
          canvas.toBlob(
            (blob) => {
              if (!blob) {
                reject(new Error('图片压缩失败'))
                return
              }
              
              const compressedFile = new File([blob], file.name, {
                type: 'image/jpeg',
                lastModified: Date.now()
              })
              
              const reader = new FileReader()
              reader.onload = (e) => {
                resolve({
                  file: compressedFile,
                  preview: e.target.result,
                  isCompressed: true
                })
              }
              reader.onerror = reject
              reader.readAsDataURL(compressedFile)
            },
            'image/jpeg',
            quality
          )
        }
        
        img.onerror = () => {
          URL.revokeObjectURL(url)
          reject(new Error('图片加载失败'))
        }
        
        img.src = url
      })
    },
    
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    
    // URL相关辅助方法
    hasUrls(text) {
      if (!text) return false
      const urls = linkifier.findUrls(text)
      return urls.length > 0
    },
    
    async copyAllUrls(text) {
      if (!text) return
      const urls = linkifier.findUrls(text)
      if (urls.length === 0) return
      
      const urlList = urls.map(u => u.url).join('\n')
      const success = await linkifier.copyToClipboard(urlList)
      
      if (success) {
        alert('链接已复制到剪贴板！')
      } else {
        alert('复制失败，请手动复制')
      }
    },
    
    // 打开消息图片大图预览
    openMessageImagePreview(file) {
      this.previewingMessageImage = file
      this.previewingMessageImageDimensions = null
    },
    
    // 关闭消息图片大图预览
    closeMessageImagePreview() {
      this.previewingMessageImage = null
      this.previewingMessageImageDimensions = null
    },
    
    // 大图加载完成时获取尺寸
    onMessageImageLoad(event) {
      if (this.previewingMessageImage) {
        this.previewingMessageImageDimensions = {
          width: event.target.naturalWidth,
          height: event.target.naturalHeight
        }
      }
    },
    
    getFileIcon(type) {
      if (type.includes('pdf')) return 'material-symbols:picture-as-pdf'
      if (type.includes('word') || type.includes('document')) return 'material-symbols:description'
      if (type.includes('sheet') || type.includes('excel')) return 'material-symbols:table-chart'
      if (type.includes('presentation') || type.includes('powerpoint')) return 'material-symbols:slideshow'
      if (type.includes('zip') || type.includes('rar') || type.includes('archive')) return 'material-symbols:folder-zip'
      if (type.includes('audio') || type.includes('music')) return 'material-symbols:audio-file'
      if (type.includes('video')) return 'material-symbols:video-file'
      if (type.includes('text')) return 'material-symbols:text-snippet'
      return 'material-symbols:insert-drive-file'
    },
    
    getFileIconClass(type) {
      let baseClass = 'text-3xl'
      if (type.includes('pdf')) return baseClass + ' text-red-500'
      if (type.includes('word') || type.includes('document')) return baseClass + ' text-blue-500'
      if (type.includes('sheet') || type.includes('excel')) return baseClass + ' text-green-500'
      if (type.includes('presentation') || type.includes('powerpoint')) return baseClass + ' text-orange-500'
      if (type.includes('zip') || type.includes('rar') || type.includes('archive')) return baseClass + ' text-yellow-600'
      if (type.includes('audio') || type.includes('music')) return baseClass + ' text-purple-500'
      if (type.includes('video')) return baseClass + ' text-pink-500'
      return baseClass + ' text-slate-400'
    },
    simulateUploadProgress(index) {
      let progress = 0
      const interval = setInterval(() => {
        progress += Math.random() * 30
        if (progress >= 100) {
          progress = 100
          clearInterval(interval)
          this.previewFiles[index].uploadProgress = undefined
        } else {
          this.previewFiles[index].uploadProgress = Math.round(progress)
        }
      }, 200)
    },
    handleDragOver(event) {
      this.isDragOver = true
    },
    handleDragLeave(event) {
      this.isDragOver = false
    },
    handleDrop(event) {
      this.isDragOver = false
      const files = event.dataTransfer.files
      if (files && files.length > 0) {
        this.processFiles(files)
      }
    },
    extractSmartQaResponse(payload) {
      if (!payload) return ''
      if (typeof payload === 'string') return payload.trim()

      const candidates = [
        payload.response,
        payload?.data?.response,
        payload.content,
        payload?.data?.content,
        payload.result,
        payload?.data?.result,
        payload.message,
        payload?.data?.message
      ]

      const text = candidates.find(item => typeof item === 'string' && item.trim().length > 0)
      return text ? text.trim() : ''
    },
    getErrorMessage(error) {
      if (error?.code === 'ECONNABORTED' || error?.message?.toLowerCase().includes('timeout')) {
        return '请求超时，文件分析时间较长，请稍后重试'
      }

      if (error?.response) {
        const backendMessage = this.extractSmartQaResponse(error.response.data)
        if (backendMessage && backendMessage.toLowerCase() !== 'ok') {
          return backendMessage
        }

        if (error.response.status === 413) {
          return '文件过大，请压缩后重试'
        }
        if (error.response.status === 415) {
          return '不支持的文件格式'
        }
        if (error.response.status === 500) {
          return '服务器内部错误，请稍后再试'
        }
        if (error.response.status === 502 || error.response.status === 503) {
          return '服务暂时不可用，请稍后再试'
        }
      }

      if (error.message) {
        if (error.message.includes('网络') || error.message.includes('Network') || error.message.includes('timeout')) {
          return '网络连接异常，请检查网络后重试'
        }
        if (error.message.includes('大小') || error.message.includes('size')) {
          return error.message
        }
        if (error.message.includes('格式') || error.message.includes('format')) {
          return error.message
        }
        if (error.message.includes('50MB')) {
          return error.message
        }
      }
      return '发生了未知错误，请稍后再试'
    },
    retrySend() {
      if (!this.lastError) return
      
      this.showRetryButton = false
      
      const lastUserMessage = this.messages.findLast(m => m.role === 'user')
      if (lastUserMessage) {
        this.inputMessage = lastUserMessage.content || ''
        if (lastUserMessage.files) {
          this.previewFiles = [...lastUserMessage.files]
        }
      }
      
      this.sendMessage()
    },
    removeFile(index) {
      this.previewFiles.splice(index, 1)
    },
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.messagesContainer) {
          this.$refs.messagesContainer.scrollTop = this.$refs.messagesContainer.scrollHeight
        }
      })
    },
    async submitReservation() {
      if (!this.canSubmitReservation || this.isReserving) {
        return
      }

      this.isReserving = true
      try {
        await healthConsultationAPI.reserve(this.reserveForm)
        
        this.showReserveForm = false
        this.messages.push({
          role: 'ai',
          content: '恭喜您，一对一健康咨询服务已经预约成功，我们会准时联系您，请注意接听电话！',
          time: this.getCurrentTime()
        })
        this.reserveForm = {
          patientName: '',
          patientGender: '',
          patientPhone: '',
          communicationTime: '',
          province: '',
          mainSymptoms: ''
        }
        this.scrollToBottom()
      } catch (error) {
        console.error('预约失败:', error)
        alert('预约失败：' + (error.response?.data?.message || error.message))
      } finally {
        this.isReserving = false
      }
    }
  },
  computed: {
    hasImageInPreview() {
      return this.isAnalyzingImage
    },
    canSubmitReservation() {
      return this.reserveForm.patientName &&
             this.reserveForm.patientGender &&
             this.reserveForm.patientPhone &&
             this.reserveForm.communicationTime &&
             this.reserveForm.province &&
             this.reserveForm.mainSymptoms
    },
    filteredMessageHistory() {
      // 只显示用户的输入语句
      let userMessages = this.allMessageHistory.filter(msg => msg.role === 'user')
      
      if (!this.searchKeyword.trim()) {
        return userMessages
      }
      
      const keyword = this.searchKeyword.toLowerCase()
      return userMessages.filter(msg => 
        (msg.content || '').toLowerCase().includes(keyword)
      )
    },
    paginatedMessageHistory() {
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      return this.filteredMessageHistory.slice(startIndex, endIndex)
    },
    totalPages() {
      return Math.ceil(this.filteredMessageHistory.length / this.pageSize)
    }
  },
  watch: {
  }
}
</script>

<style scoped>
/* ============ BASE ============ */
.smart-qa-workstation {
  --workspace-header-height: 80px;
  --col-left: 288px;
  --col-right: 280px;
  --radius-card: 16px;
  --radius-lg: 20px;
  --shadow-card: 0 1px 3px rgba(15,23,42,0.06), 0 4px 16px rgba(15,23,42,0.05);
  --shadow-float: 0 8px 32px rgba(15,23,42,0.1), 0 2px 8px rgba(15,23,42,0.06);
  --c-bg: #f5f7fb;
  --c-surface: rgba(255,255,255,0.97);
  --c-border: rgba(226,232,240,0.65);
  --c-text: #0f172a;
  --c-muted: #64748b;
  --c-accent: #1d4ed8;
  --c-accent-light: #eff6ff;
  position: relative;
  height: calc(100vh - var(--app-top-nav-height, 68px));
  min-height: calc(100vh - var(--app-top-nav-height, 68px));
  max-height: calc(100vh - var(--app-top-nav-height, 68px));
  background: var(--c-bg);
  font-family: "PingFang SC","HarmonyOS Sans SC","Microsoft YaHei","Noto Sans SC",sans-serif;
  color: var(--c-text);
  overflow: hidden;
}

.workspace-background {
  position: absolute;
  inset: var(--workspace-header-height) 0 0;
  overflow: hidden;
  pointer-events: none;
}

.workspace-orb {
  position: absolute; border-radius: 50%;
  filter: blur(100px); opacity: 0.3;
  animation: workspaceFloat 22s ease-in-out infinite;
}

.orb-left {
  width: 480px; height: 480px; top: -120px; left: -80px;
  background: radial-gradient(circle, rgba(59,130,246,0.22) 0%, transparent 70%);
}

.orb-right {
  width: 540px; height: 540px; right: -160px; bottom: -160px;
  background: radial-gradient(circle, rgba(13,148,136,0.16) 0%, transparent 70%);
  animation-delay: -10s;
}

.workspace-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.055) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.055) 1px, transparent 1px);
  background-size: 56px 56px;
  mask-image: linear-gradient(180deg, rgba(0,0,0,0.5), transparent 80%);
}

.glass-panel {
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
}

.workspace-header,
.workspace-main {
  position: relative;
}

/* ============ HEADER ============ */
.workspace-header {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  min-height: 72px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  border-bottom: 1px solid rgba(226,232,240,0.6);
  box-shadow: 0 1px 0 rgba(255,255,255,0.8), 0 2px 12px rgba(15,23,42,0.05);
  background: rgba(255,255,255,0.97);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  /* CSS vars for chip theming - overridden by dark themes below */
  --chip-bg: linear-gradient(135deg, #eff6ff, #f0fdf9);
  --chip-color: #1d4ed8;
  --chip-border: rgba(147,197,253,0.35);
  --chip-muted-bg: #f8fafc;
  --chip-muted-color: #64748b;
  --chip-muted-border: rgba(226,232,240,0.9);
  --header-title-color: #0f172a;
  --header-kicker-color: #0891b2;
  --header-sub-color: #94a3b8;
  --header-back-bg: #f8fafc;
  --header-back-color: #475569;
  --header-back-border: rgba(226,232,240,0.8);
}

/* ─── Deep-blue / dark header theme ─── 
   Triggered when a parent sets data-theme="deep-blue" or a .theme-deep-blue class,
   OR when the header has an explicit dark background utility class.             */
.smart-qa-workstation[data-theme="deep-blue"] .workspace-header,
.smart-qa-workstation[data-theme="dark"] .workspace-header,
[data-theme="deep-blue"] .workspace-header,
[data-theme="dark"] .workspace-header,
[class*="theme-deep"] .workspace-header,
[class*="theme-dark"] .workspace-header {
  background: linear-gradient(135deg, #0c1445 0%, #0e1d5e 100%) !important;
  border-bottom-color: rgba(255,255,255,0.08) !important;
  box-shadow: 0 2px 16px rgba(0,0,0,0.28) !important;
  --chip-bg: rgba(255,255,255,0.11);
  --chip-color: #d4e4ff;
  --chip-border: rgba(255,255,255,0.2);
  --chip-muted-bg: rgba(255,255,255,0.07);
  --chip-muted-color: rgba(180,200,240,0.85);
  --chip-muted-border: rgba(255,255,255,0.1);
  --header-title-color: #f0f6ff;
  --header-kicker-color: #7dd3fc;
  --header-sub-color: rgba(180,200,240,0.65);
  --header-back-bg: rgba(255,255,255,0.1);
  --header-back-color: #c8dcff;
  --header-back-border: rgba(255,255,255,0.15);
}

.workspace-title-group {
  display: flex; align-items: center; gap: 16px;
}

.workspace-back-btn {
  width: 36px; height: 36px;
  border: 1px solid var(--header-back-border, rgba(226,232,240,0.8));
  border-radius: 10px;
  background: var(--header-back-bg, #f8fafc);
  color: var(--header-back-color, #475569);
  cursor: pointer;
  transition: all 0.18s ease;
  display: flex; align-items: center; justify-content: center;
}

.workspace-back-btn:hover {
  border-color: rgba(59,130,246,0.25);
  color: #2563eb;
  background: #eff6ff;
  transform: translateX(-1px);
}

.workspace-title-block { display: flex; flex-direction: column; gap: 1px; }

.workspace-kicker {
  font-size: 10px; font-weight: 700;
  letter-spacing: 0.1em; text-transform: uppercase;
  color: var(--header-kicker-color, #0891b2);
}

.workspace-title-block h1 {
  margin: 0; font-size: 19px; font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--header-title-color, #0f172a);
  line-height: 1.2;
}

.workspace-title-block p {
  margin: 0;
  color: var(--header-sub-color, #94a3b8);
  font-size: 11.5px;
}

.workspace-status { display: flex; flex-wrap: wrap; gap: 8px; }

.workspace-status-chip {
  display: inline-flex; align-items: center; gap: 7px;
  height: 36px; padding: 0 14px;
  border-radius: 999px;
  background: var(--chip-bg, linear-gradient(135deg, #eff6ff, #f0fdf9));
  color: var(--chip-color, #1d4ed8);
  border: 1px solid var(--chip-border, rgba(147,197,253,0.35));
  font-size: 12px; font-weight: 600;
  box-shadow: 0 1px 4px rgba(37,99,235,0.07);
  transition: background 0.2s, color 0.2s, border-color 0.2s;
}

.workspace-status-chip.muted {
  background: var(--chip-muted-bg, #f8fafc);
  color: var(--chip-muted-color, #64748b);
  border-color: var(--chip-muted-border, rgba(226,232,240,0.9));
  box-shadow: none;
}

.status-dot {
  width: 7px; height: 7px; border-radius: 50%;
  background: #10b981;
  box-shadow: 0 0 0 3px rgba(16,185,129,0.2);
  animation: statusPulse 2.5s ease-in-out infinite;
}

/* ============ LAYOUT ============ */
.workspace-left, .workspace-right {
  position: relative; z-index: 1;
  background: rgba(255,255,255,0.88);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  min-height: 0;
  max-height: 100%;
}

.workspace-left {
  width: var(--col-left);
  border-right: 1px solid var(--c-border);
  display: flex; min-height: 0;
  overflow: hidden;
}

.workspace-right {
  width: var(--col-right);
  border-left: 1px solid var(--c-border);
  min-height: 0;
  overflow: hidden;
  scrollbar-width: none;
  background: rgba(255,255,255,0.95);
}

.workspace-center {
  background: var(--c-bg);
  min-height: 0;
  overflow: hidden;
}

.workspace-main {
  z-index: 1;
  flex: 1 1 auto;
  min-height: 0;
  height: 100%;
  overflow: hidden;
}

/* ============ LEFT PANEL ============ */
.workspace-side-card, .service-form-card {
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
}

.workspace-left-shell {
  flex: 1; min-height: 0;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;
  gap: 10px; padding: 14px;
  overflow: hidden;
}

/* Hero card */
.side-hero-card {
  padding: 16px 18px;
  background: linear-gradient(140deg, #0c1445 0%, #1a3070 50%, #0c7a8a 100%);
  color: #f8fbff;
  box-shadow: 0 6px 24px rgba(12,20,69,0.22), inset 0 1px 0 rgba(255,255,255,0.08);
  border: none;
}

.side-hero-label {
  display: inline-flex; padding: 3px 9px;
  border-radius: 999px;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.14);
  font-size: 9px; font-weight: 700;
  letter-spacing: 0.08em; text-transform: uppercase;
  color: rgba(255,255,255,0.85);
}

.side-hero-card h3 {
  margin: 10px 0 5px; font-size: 15px; line-height: 1.3; font-weight: 700;
}

.side-hero-card p {
  margin: 0; color: rgba(241,245,249,0.7); font-size: 11.5px; line-height: 1.6;
}

/* Side block */
.workspace-side-block {
  min-height: 0; display: flex; flex-direction: column;
}
.workspace-side-block-scroll { overflow: hidden; }

.side-block-header {
  display: flex; align-items: center;
  justify-content: space-between; gap: 10px;
  margin-bottom: 10px;
  padding: 0 2px;
}

.side-block-header h3 {
  margin: 0; font-size: 13px; font-weight: 700; color: #0f172a;
}

.side-block-header span {
  color: #94a3b8; font-size: 10.5px;
}

.prompt-list {
  display: grid; gap: 7px;
  overflow-y: auto; min-height: 0; padding-right: 4px;
}

/* Prompt cards */
.prompt-card {
  display: grid; grid-template-columns: 36px 1fr;
  gap: 10px; align-items: center;
  width: 100%; padding: 10px 12px;
  border: 1px solid var(--c-border);
  border-radius: 12px;
  background: rgba(255,255,255,0.92);
  cursor: pointer; text-align: left;
  transition: all 0.18s ease;
}

.prompt-card:hover {
  transform: translateY(-1px);
  border-color: rgba(59,130,246,0.22);
  box-shadow: 0 4px 16px rgba(59,130,246,0.07);
  background: #fff;
}

.prompt-card-utility { background: linear-gradient(180deg, #fffdf5, #fff); }

.prompt-icon {
  width: 36px; height: 36px;
  border-radius: 10px;
  display: inline-flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.prompt-icon.blue   { background: rgba(59,130,246,0.09);  color: #2563eb; }
.prompt-icon.orange { background: rgba(249,115,22,0.09);  color: #ea580c; }
.prompt-icon.green  { background: rgba(16,185,129,0.09);  color: #059669; }
.prompt-icon.slate  { background: rgba(71,85,105,0.08);   color: #334155; }
.prompt-icon.cyan   { background: rgba(6,182,212,0.09);   color: #0891b2; }
.prompt-icon.amber  { background: rgba(245,158,11,0.10);  color: #d97706; }

.prompt-content strong {
  display: block; color: #0f172a; font-size: 12.5px; font-weight: 700;
}

.prompt-content span {
  display: block; margin-top: 2px;
  color: #64748b; font-size: 10.5px; line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Expert card */
.expert-card {
  padding: 14px 16px 14px;
  background: linear-gradient(160deg, #fafcff 0%, #f0f7ff 100%) !important;
  border: 1px solid rgba(147,197,253,0.2) !important;
}
.expert-card-fixed { position: sticky; bottom: 0; }

.expert-card-head {
  display: flex; align-items: center; gap: 12px;
}

.expert-avatar {
  width: 44px; height: 44px; border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(15,23,42,0.1);
  border: 2px solid rgba(255,255,255,0.9);
  flex-shrink: 0;
}

.expert-meta strong { display: block; color: #0f172a; font-size: 14px; font-weight: 700; }
.expert-meta span { display: block; margin-top: 2px; color: #64748b; font-size: 11px; }

.expert-tags {
  display: flex; flex-wrap: wrap; gap: 5px; margin: 10px 0 12px;
}

.expert-tags span {
  padding: 3px 9px; border-radius: 999px;
  background: rgba(255,255,255,0.85); color: #3b5ea6;
  font-size: 10px; font-weight: 600;
  border: 1px solid rgba(147,197,253,0.3);
}

.expert-action-btn {
  width: 100%; min-height: 38px;
  border: none; border-radius: 10px; cursor: pointer;
  color: #fff; font-size: 13px; font-weight: 700;
  background: linear-gradient(135deg, #0c1445 0%, #1d4ed8 55%, #0891b2 100%);
  box-shadow: 0 6px 18px rgba(20,52,160,0.2);
  transition: all 0.2s ease;
  letter-spacing: 0.01em;
}

.expert-action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 26px rgba(20,52,160,0.28);
}

/* ============ CHAT CENTER ============ */
.conversation-shell {
  width: 100%;
  max-width: 980px;
  margin: 0 auto;
  padding: 6px 0 0;
}

.workspace-messages-scroll {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  scroll-behavior: smooth;
  padding-bottom: 220px !important;
}

.conversation-banner {
  display: grid; grid-template-columns: 56px 1fr;
  gap: 18px; padding: 22px 26px;
  border-radius: var(--radius-card);
  border: 1px solid rgba(147,197,253,0.2);
  background: linear-gradient(135deg, rgba(239,246,255,0.9) 0%, rgba(248,252,255,0.95) 100%);
  box-shadow: 0 2px 12px rgba(37,99,235,0.05);
}

.conversation-banner-icon {
  width: 56px; height: 56px;
  display: inline-flex; align-items: center; justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #1730a0, #0881b0);
  color: #fff;
  box-shadow: 0 4px 14px rgba(23,48,160,0.22);
  flex-shrink: 0;
}

.conversation-banner-body h4 {
  margin: 2px 0 8px; font-size: 15px; font-weight: 700; color: #0f172a;
}

.conversation-banner-body p {
  margin: 0; color: #475569; line-height: 1.82; font-size: 14px;
}

.conversation-banner-tags, .conversation-suggestion-row {
  display: flex; flex-wrap: wrap; gap: 7px;
}

.conversation-banner-tags { margin-top: 14px; }

.conversation-banner-tags span, .suggestion-pill {
  height: 30px; padding: 0 12px;
  border-radius: 999px; font-size: 12px; font-weight: 600;
}

.conversation-banner-tags span {
  display: inline-flex; align-items: center;
  background: rgba(255,255,255,0.9);
  border: 1px solid rgba(226,232,240,0.7);
  color: #334155;
}

.conversation-suggestion-row {
  margin: 18px 0 28px;
  padding: 0 2px;
}

.suggestion-pill {
  border: 1px solid rgba(226,232,240,0.7);
  background: rgba(255,255,255,0.9);
  color: #334155;
  cursor: pointer;
  transition: all 0.16s ease;
  display: inline-flex; align-items: center;
}

.suggestion-pill:hover {
  border-color: rgba(59,130,246,0.28);
  color: #1d4ed8;
  background: #eff6ff;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(37,99,235,0.08);
}

.conversation-row { margin-bottom: 24px; }

/* ============ MESSAGE BUBBLES ============ */
.assistant-avatar {
  width: 38px; height: 38px;
  border-radius: 11px;
  display: inline-flex; align-items: center; justify-content: center;
  flex-shrink: 0; color: #fff;
  background: linear-gradient(135deg, #1730a0, #0881b0);
  box-shadow: 0 6px 16px rgba(23,48,160,0.18);
}

.assistant-bubble, .assistant-typing {
  width: fit-content;
  max-width: min(76%, 720px);
  min-width: min(520px, 100%);
  padding: 22px 24px 18px;
  border-radius: 20px 20px 20px 6px;
  border: 1px solid rgba(226,232,240,0.55);
  background: #fff;
  box-shadow: 0 1px 4px rgba(15,23,42,0.04), 0 10px 28px rgba(15,23,42,0.06);
}

.user-bubble {
  padding: 13px 16px;
  border-radius: 16px 16px 4px 16px;
  color: rgba(255,255,255,0.98);
  background: linear-gradient(135deg, #152a6e 0%, #1d4ed8 60%, #0d6f87 100%);
  border: 1px solid rgba(96,165,250,0.22);
  box-shadow: 0 8px 24px rgba(15,23,42,0.16), 0 4px 12px rgba(29,78,216,0.18);
}

.message-content {
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
  line-height: 1.92;
}

.user-bubble .message-content,
.user-bubble .message-content :deep(*) {
  color: rgba(255,255,255,0.98);
}

.user-bubble .message-content {
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(8,15,35,0.15);
}

.assistant-bubble .message-content {
  color: #334155;
  font-size: 15px;
  line-height: 2;
  letter-spacing: 0.01em;
}

.bubble-meta {
  margin-top: 10px; color: #cbd5e1; font-size: 11px;
}

.bubble-meta-light {
  display: block; color: rgba(255,255,255,0.75);
}

.typing-image-badge {
  width: 22px; height: 22px; border-radius: 50%;
  display: inline-flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #1d4ed8, #0891b2);
}

.retry-btn, .utility-link {
  display: inline-flex; align-items: center; gap: 6px;
  font-size: 12px; font-weight: 600;
}

.retry-btn {
  min-height: 32px; padding: 0 12px;
  border: none; border-radius: 9px; cursor: pointer;
  color: #fff;
  background: linear-gradient(135deg, #1d4ed8, #0891b2);
}

.utility-link {
  border: none; background: transparent;
  color: #2563eb; cursor: pointer;
}

.service-form-card { padding: 22px; }

/* ============ INPUT DOCK ============ */
.workspace-input-dock {
  position: absolute;
  left: 24px; right: 24px; bottom: 22px;
  display: flex; flex-direction: column;
  align-items: center; gap: 10px; z-index: 2;
}

.preview-strip {
  width: 100%; max-width: 860px;
  display: flex; gap: 10px;
  overflow-x: auto; padding-bottom: 4px;
}

.input-helper-row {
  display: flex; justify-content: space-between; gap: 14px;
  margin-bottom: 8px;
  color: #94a3b8; font-size: 11px;
}

/* Main input container */
.chat-input-container-minimal {
  background: rgba(255,255,255,0.99);
  border: 1px solid rgba(226,232,240,0.75);
  border-radius: 20px;
  padding: 11px 14px;
  display: flex; align-items: flex-end; gap: 10px;
  box-shadow:
    0 0 0 0 transparent,
    0 4px 20px rgba(15,23,42,0.08),
    0 1px 4px rgba(15,23,42,0.05),
    inset 0 1px 0 rgba(255,255,255,1);
  transition: all 0.2s ease;
}

/* Tech-blue theme: ensure input panel is always bright/readable */
[data-theme="tech-blue"] .chat-input-container-minimal,
[class*="theme-tech"] .chat-input-container-minimal {
  background: rgba(255,255,255,1) !important;
  border-color: rgba(59,130,246,0.22) !important;
  box-shadow:
    0 0 0 0 transparent,
    0 4px 24px rgba(15,23,42,0.10),
    0 1px 4px rgba(15,23,42,0.06),
    inset 0 1px 0 rgba(255,255,255,1) !important;
}

.chat-input-container-minimal:focus-within {
  border-color: rgba(59,130,246,0.28);
  box-shadow:
    0 0 0 3px rgba(59,130,246,0.06),
    0 4px 20px rgba(15,23,42,0.08),
    0 1px 4px rgba(15,23,42,0.05);
}

.chat-input-container-minimal.drag-over {
  border-color: rgba(37,99,235,0.3);
  background: rgba(239,246,255,0.99);
  box-shadow: 0 0 0 4px rgba(59,130,246,0.07);
}

.chat-bottom-left {
  flex-shrink: 0; display: flex; align-items: center;
}

.minimal-action-button {
  padding: 7px 13px;
  border-radius: 999px;
  border: 1px solid rgba(226,232,240,0.85);
  background: #f8fafc;
  color: #64748b;
  font-size: 12px; font-weight: 600;
  cursor: pointer;
  transition: all 0.16s ease;
  display: flex; align-items: center; gap: 5px;
}

.minimal-action-button:hover {
  background: #f1f5f9; border-color: rgba(148,163,184,0.5); color: #475569;
}

.minimal-action-button:focus-visible { outline: 2px solid #3b82f6; outline-offset: 2px; }

.minimal-action-button-active {
  background: linear-gradient(135deg, #1730a0, #1d4ed8);
  border-color: transparent; color: white;
  box-shadow: 0 2px 10px rgba(29,78,216,0.22);
}

.minimal-action-button-active:hover {
  background: linear-gradient(135deg, #1730a0, #2563eb); color: white;
}

.minimal-button-text { color: inherit; }

.chat-input-center { flex: 1; min-width: 0; }

.chat-textarea-minimal {
  width: 100%; padding: 5px 0;
  background: transparent; border: none;
  color: #0f172a; font-size: 14.5px; line-height: 1.7;
  resize: none; font-family: inherit;
}

/* Stronger placeholder - visible on both light and tech-blue themes */
.chat-textarea-minimal::placeholder {
  color: #7a8fa6;
  font-size: 14px;
  opacity: 1; /* Firefox fix */
}
.chat-textarea-minimal::-webkit-input-placeholder { color: #7a8fa6; opacity: 1; }
.chat-textarea-minimal::-moz-placeholder { color: #7a8fa6; opacity: 1; }

.chat-textarea-minimal:focus { outline: none; }

.chat-bottom-right {
  flex-shrink: 0; display: flex; align-items: center; gap: 6px;
}

.upload-buttons-group { display: flex; align-items: center; gap: 3px; }

.minimal-icon-button {
  width: 34px; height: 34px; border-radius: 9px;
  border: none;
  background: #f8fafc;
  color: #64748b; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.16s ease;
}

.minimal-icon-button:hover { background: #eff6ff; color: #3b82f6; }
.minimal-icon-button:active { transform: scale(0.93); }
.minimal-icon-button:focus-visible { outline: 2px solid #3b82f6; outline-offset: 2px; }
.minimal-icon-button iconify-icon { font-size: 19px; }

.minimal-divider {
  width: 1px; height: 20px; background: #e2e8f0;
  margin: 0 2px; flex-shrink: 0;
}

.minimal-send-button {
  width: 38px; height: 38px; border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #1730a0, #1d4ed8);
  color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s cubic-bezier(0.34,1.56,0.64,1);
  box-shadow: 0 3px 12px rgba(29,78,216,0.28);
}

.minimal-send-button:hover:not(.minimal-send-button-disabled) {
  transform: scale(1.08);
  box-shadow: 0 5px 16px rgba(29,78,216,0.36);
}

.minimal-send-button:active:not(.minimal-send-button-disabled) { transform: scale(0.94); }
.minimal-send-button:focus-visible { outline: 2px solid #3b82f6; outline-offset: 2px; }

.minimal-send-button-stop {
  background: linear-gradient(135deg, #dc2626, #ef4444);
  box-shadow: 0 3px 12px rgba(220,38,38,0.22);
}

.minimal-send-button-stop:hover:not(.minimal-send-button-disabled) {
  box-shadow: 0 5px 16px rgba(220,38,38,0.32);
}

.minimal-send-button-disabled {
  background: #e2e8f0; cursor: not-allowed;
  transform: none; box-shadow: none;
}

.minimal-send-button iconify-icon { font-size: 19px; }

/* ============ FILE PREVIEW ============ */
.file-preview-container {
  width: auto; max-width: 280px; min-width: 88px;
  height: auto; min-height: 88px; max-height: 180px;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}

.file-preview-image {
  max-width: 100%; max-height: 180px;
  width: auto; height: auto;
  object-fit: contain; display: block;
}

/* ============ RIGHT PANEL ============ */
.workspace-right > div:first-child {
  padding: 16px 16px 14px !important;
  border-bottom: 1px solid rgba(226,232,240,0.55) !important;
  background: rgba(255,255,255,0.96);
}

.workspace-history-shell {
  flex: 1 1 auto;
  min-height: 0;
  padding: 12px 10px 16px 8px;
  overflow: hidden;
}

.history-search-input {
  width: 100%; min-height: 36px;
  padding: 0 13px 0 36px;
  border-radius: 10px;
  border: 1px solid rgba(226,232,240,0.75);
  background: #f8fafc;
  color: #0f172a; font-size: 12.5px;
  outline: none; transition: all 0.16s ease;
  font-family: inherit;
}

.history-search-input:focus {
  border-color: rgba(59,130,246,0.25);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.06);
}

.workspace-history-scroll {
  height: 100%;
  min-height: 0;
  padding: 0 4px 0 8px;
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  scrollbar-gutter: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(148,163,184,0.6) transparent;
}

.workspace-history-scroll::-webkit-scrollbar { width: 4px; }
.workspace-history-scroll::-webkit-scrollbar-track { background: transparent; }
.workspace-history-scroll::-webkit-scrollbar-thumb {
  background: rgba(148,163,184,0.65); border-radius: 999px;
}
.workspace-history-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(100,116,139,0.85);
}

.history-item-card {
  padding: 11px 12px; border-radius: 12px;
  border: 1px solid transparent;
  cursor: pointer; transition: all 0.16s ease;
}

.history-item-default {
  background: #f8fafc;
  border-color: rgba(226,232,240,0.7);
}

.history-item-active {
  background: #eff6ff;
  border-color: rgba(147,197,253,0.35);
  box-shadow: 0 2px 10px rgba(59,130,246,0.07);
}

/* Right panel pagination */
.workspace-right > div:last-child {
  padding: 12px 16px !important;
  border-top: 1px solid rgba(226,232,240,0.55) !important;
  background: rgba(248,250,252,0.9);
}

.medical-gradient {
  background: linear-gradient(135deg, #0c1445 0%, #1d4ed8 55%, #0891b2 100%);
}

.card-shadow { box-shadow: var(--shadow-card); }

/* ============ TYPING INDICATOR ============ */
.typing-indicator {
  display: flex; gap: 4px; padding: 5px 0; align-items: center;
}

.typing-indicator span {
  width: 6px; height: 6px;
  background: #94a3b8;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

/* ============ MESSAGE IMAGE ============ */
.message-image-container {
  width: auto; max-width: 260px; min-width: 110px;
  height: auto; min-height: 88px; max-height: 220px;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden; cursor: pointer;
  transition: all 0.2s ease; border-radius: 12px;
}

.message-image-container:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

.message-image {
  max-width: 100%; max-height: 220px;
  width: auto; height: auto;
  object-fit: contain; display: block;
}

.message-file-container {
  padding: 14px; border-radius: 12px;
  min-width: 110px;
}

/* ============ IMAGE PREVIEW OVERLAY ============ */
.message-image-preview-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.82);
  backdrop-filter: blur(14px);
  z-index: 9999;
  display: flex; align-items: center; justify-content: center;
}

.message-image-preview-container {
  background: #fff; border-radius: 16px;
  overflow: hidden; max-width: 90vw; max-height: 90vh;
  display: flex; flex-direction: column;
  box-shadow: 0 32px 80px rgba(0,0,0,0.45);
}

.message-image-preview-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid rgba(226,232,240,0.6);
  background: #f8fafc;
}

.message-image-preview-title {
  font-size: 13.5px; font-weight: 600; color: #0f172a;
}

.message-image-preview-close {
  width: 30px; height: 30px; border-radius: 8px;
  border: none; background: rgba(226,232,240,0.55);
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  color: #475569; transition: all 0.16s;
}

.message-image-preview-close:hover { background: #e2e8f0; }

.message-image-preview-content {
  flex: 1; overflow: auto;
  display: flex; align-items: center; justify-content: center;
  padding: 20px;
}

.message-image-preview-img {
  max-width: 100%; max-height: calc(90vh - 120px);
  display: block; border-radius: 8px;
}

.message-image-preview-footer {
  padding: 10px 18px;
  border-top: 1px solid rgba(226,232,240,0.6);
  background: #f8fafc;
  display: flex; gap: 12px; align-items: center;
}

.message-image-preview-dimensions,
.message-image-preview-size {
  font-size: 11.5px; color: #94a3b8; font-weight: 500;
}

/* ============ SCROLLBAR ============ */
.custom-scrollbar::-webkit-scrollbar { width: 4px; height: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d9e6; border-radius: 999px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

/* ============ HIGHLIGHTED MESSAGE ============ */
.highlighted-message .assistant-bubble,
.highlighted-message .user-bubble {
  box-shadow: 0 0 0 3px rgba(251,191,36,0.22) !important;
}

/* ============ ANIMATIONS ============ */
@keyframes workspaceFloat {
  0%,100% { transform: translate3d(0,0,0) scale(1); }
  50% { transform: translate3d(12px,-8px,0) scale(1.025); }
}

@keyframes statusPulse {
  0%,100% { box-shadow: 0 0 0 3px rgba(16,185,129,0.2); }
  50% { box-shadow: 0 0 0 6px rgba(16,185,129,0.05); }
}

@keyframes typing {
  0%,80%,100% { transform: scale(0.7); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.animate-spin { animation: spin 0.9s linear infinite; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ============ RESPONSIVE ============ */

/* Right panel: show from 1024px (lg) so it's visible at 100% zoom on 1366px+ screens */
.workspace-right-panel {
  display: flex;
}

@media (max-width: 1280px) {
  .workspace-left { width: 264px; }
  .workspace-right { width: 256px; }
  .workspace-title-block p, .input-helper-row span:last-child { display: none; }
}

@media (max-width: 1024px) {
  .workspace-left { width: 240px; }
  /* Hide right panel below 1024px to give center area room */
  .workspace-right-panel { display: none !important; }
}

@media (max-width: 768px) {
  .smart-qa-workstation {
    --workspace-header-height: 88px;
  }
  .workspace-header {
    padding: 10px 14px 8px;
    align-items: flex-start;
    flex-direction: column;
  }
  .workspace-title-group, .workspace-status { width: 100%; }
  .workspace-main { flex-direction: column; }
  .workspace-left { width: 100%; max-height: 300px; }
  .workspace-input-dock { left: 12px; right: 12px; bottom: 12px; }
  .conversation-banner { grid-template-columns: 1fr; }
  .assistant-bubble, .assistant-typing { width: 100%; max-width: 100%; min-width: 0; padding: 18px 18px 15px; }
  .input-helper-row { flex-direction: column; gap: 4px; }
  .chat-input-container-minimal { flex-wrap: wrap; padding: 10px 12px; }
  .chat-bottom-left { order: 2; width: 100%; justify-content: flex-start; margin-top: 7px; }
  .chat-input-center { order: 1; flex: 1; }
  .chat-bottom-right { order: 3; }
  .file-preview-container { max-width: 180px; max-height: 140px; }
  .file-preview-image { max-height: 140px; }
}

/* ============ SUPPLEMENTAL REFINEMENTS ============ */

/* Center area subtle gradient */
.workspace-center {
  background: linear-gradient(180deg, #f5f7fb 0%, #f0f4f9 100%);
}

/* Message scroll: tighter padding on desktop */
@media (min-width: 769px) {
  .workspace-messages-scroll {
    padding: 24px 28px 220px !important;
  }
}

/* Chat input: max width aligns with conversation shell */
.workspace-input-dock .w-full.max-w-4xl {
  max-width: 980px;
}

/* Input panel drop-shadow on desktop */
@media (min-width: 769px) {
  .input-panel {
    box-shadow:
      0 0 0 0 transparent,
      0 6px 28px rgba(15,23,42,0.09),
      0 2px 6px rgba(15,23,42,0.05),
      inset 0 1px 0 rgba(255,255,255,1);
  }
}

/* Service form card integration */
.service-form-card {
  border-radius: var(--radius-lg) !important;
  padding: 22px !important;
  box-shadow: var(--shadow-float) !important;
}

.service-form-card input,
.service-form-card select,
.service-form-card textarea {
  border-radius: 9px !important;
  font-size: 13px !important;
  background: #f8fafc !important;
  transition: all 0.16s ease !important;
}

.service-form-card input:focus,
.service-form-card select:focus,
.service-form-card textarea:focus {
  background: #fff !important;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.06) !important;
}

/* Tighten left shell grid on smaller panels */
@media (max-width: 1024px) {
  .workspace-left-shell { padding: 12px; gap: 9px; }
}

/* ==================== AI LINK STYLES ==================== */
.message-content .ai-link,
div.text-slate-700 .ai-link,
div.text-white .ai-link,
.message-content a.ai-link,
.ai-link {
  color: #2563eb !important;
  text-decoration: underline !important;
  text-underline-offset: 2px !important;
  text-decoration-thickness: 1px !important;
  cursor: pointer !important;
  display: inline-block !important;
  transform-origin: center center !important;
  line-height: 1.4 !important;
  padding: 0 3px !important;
  margin: 0 1px !important;
  border-radius: 4px !important;
  font-weight: 500 !important;
  background: transparent !important;
  transition:
    color 180ms ease,
    background-color 180ms ease,
    box-shadow 180ms ease,
    transform 180ms ease !important;
}

.message-content .ai-link:hover,
.ai-link:hover {
  color: #1d4ed8 !important;
  text-decoration-thickness: 2px !important;
  background-color: rgba(37,99,235,0.07) !important;
  box-shadow: 0 2px 8px rgba(37,99,235,0.14) !important;
  transform: scale(1.04) !important;
}

.message-content .ai-link:active,
.ai-link:active {
  color: #1e40af !important;
  background-color: rgba(37,99,235,0.1) !important;
  transform: scale(1.02) !important;
  animation: none !important;
}

.message-content .ai-link:focus,
.ai-link:focus {
  outline: 2px solid #3b82f6 !important;
  outline-offset: 2px !important;
  animation: none !important;
}

.message-content .ai-link:link,
.message-content .ai-link:visited,
.ai-link:link, .ai-link:visited {
  color: #2563eb !important;
  text-decoration: underline !important;
}

.bg-blue-600 .ai-link,
.bg-blue-600 .message-content .ai-link {
  color: #bfdbfe !important;
}

.bg-blue-600 .ai-link:hover,
.bg-blue-600 .message-content .ai-link:hover {
  color: #fff !important;
  background-color: rgba(191,219,254,0.15) !important;
}

.message-content .ai-link:focus:not(:focus-visible),
.ai-link:focus:not(:focus-visible) { outline: none !important; }

@media (prefers-reduced-motion: reduce) {
  .message-content .ai-link, .message-content .ai-link:hover,
  .ai-link, .ai-link:hover {
    transition: none !important; animation: none !important; transform: none !important;
  }
}

body .ai-link, html .ai-link, * .ai-link {
  color: #2563eb !important;
  text-decoration: underline !important;
}
</style>