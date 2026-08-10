import { ref, onBeforeUnmount } from 'vue'
import apiClient from '../../api/api'

export function useConsultationChat(consultationId, pollIntervalMs = 3000) {
  const messages = ref([])
  const loading = ref(false)
  const sending = ref(false)
  const error = ref(null)
  let timer = null
  let lastMessageSignature = ''

  const buildMessageSignature = (list) => {
    if (!Array.isArray(list) || list.length === 0) return ''
    return list.map((item) => `${item.id}:${item.createdAt || ''}:${item.content || ''}`).join('|')
  }

  const loadMessages = async ({ silent = false } = {}) => {
    if (!consultationId.value) return
    if (!silent) {
      loading.value = true
    }

    try {
      const res = await apiClient.get('/chat/messages', {
        params: { consultationId: consultationId.value }
      })
      const nextMessages = Array.isArray(res.data) ? res.data : []
      const nextSignature = buildMessageSignature(nextMessages)
      if (nextSignature !== lastMessageSignature) {
        messages.value = nextMessages
        lastMessageSignature = nextSignature
      }
      error.value = null
    } catch (e) {
      console.error('加载聊天记录失败', e)
      error.value = (e && e.message) || '加载聊天记录失败'
    } finally {
      if (!silent) {
        loading.value = false
      }
    }
  }

  const sendMessage = async (content, senderId) => {
    if (!content || !consultationId.value || !senderId) return false
    sending.value = true
    error.value = null

    try {
      await apiClient.post('/chat/send', {
        consultationId: consultationId.value,
        senderId,
        content
      })
      await loadMessages({ silent: true })
      return true
    } catch (e) {
      console.error('发送消息失败', e)
      error.value = (e && e.message) || '发送消息失败'
      return false
    } finally {
      sending.value = false
    }
  }

  const startPolling = () => {
    if (timer !== null) return
    timer = window.setInterval(() => {
      if (document.hidden) return
      loadMessages({ silent: true })
    }, pollIntervalMs)
  }

  const stopPolling = () => {
    if (timer !== null) {
      clearInterval(timer)
      timer = null
    }
  }

  onBeforeUnmount(() => {
    stopPolling()
  })

  return {
    messages,
    loading,
    sending,
    error,
    loadMessages,
    sendMessage,
    startPolling,
    stopPolling
  }
}
