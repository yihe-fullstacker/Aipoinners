import { ref, onBeforeUnmount } from 'vue'
import type { Ref } from 'vue'
import apiClient from '../../api/api'

export interface ChatMessage {
  id: number
  content: string
  senderRole: 'DOCTOR' | 'PATIENT' | string
  createdAt: string
  [key: string]: any
}

export function useConsultationChat(
  consultationId: Ref<number | string | null>,
  pollIntervalMs = 8000
) {
  const messages = ref<ChatMessage[]>([])
  const loading = ref(false)
  const sending = ref(false)
  const error = ref<string | null>(null)
  let timer: number | null = null
  let lastMessageSignature = ''

  const buildMessageSignature = (list: ChatMessage[]) => {
    if (!Array.isArray(list) || list.length === 0) return ''
    // 简化签名计算，只比较关键信息
    return list.map((item) => `${item.id}`).join('|')
  }

  const loadMessages = async ({ silent = false }: { silent?: boolean } = {}) => {
    if (!consultationId.value) return
    if (!silent) {
      loading.value = true
    }

    try {
      const res = await apiClient.get('/chat/messages', {
        params: { consultationId: consultationId.value }
      })
      const nextMessages: ChatMessage[] = Array.isArray(res.data) ? res.data : []
      const nextSignature = buildMessageSignature(nextMessages)
      if (nextSignature !== lastMessageSignature) {
        messages.value = nextMessages
        lastMessageSignature = nextSignature
      }
      error.value = null
    } catch (e: any) {
      console.error('加载聊天记录失败', e)
      error.value = e?.message || '加载聊天记录失败'
    } finally {
      if (!silent) {
        loading.value = false
      }
    }
  }

  const sendMessage = async (content: string, senderId: number | null) => {
    if (!content || !consultationId.value || !senderId) return false
    sending.value = true
    error.value = null

    // 乐观更新：先本地添加消息，立即响应用户
    const optimisticId = Date.now()
    const optimisticMessage: ChatMessage = {
      id: optimisticId,
      content,
      senderRole: 'DOCTOR',
      createdAt: new Date().toISOString()
    }
    messages.value = [...messages.value, optimisticMessage]

    try {
      await apiClient.post('/chat/send', {
        consultationId: consultationId.value,
        senderId,
        content
      })
      await loadMessages({ silent: true })
      return true
    } catch (e: any) {
      console.error('发送消息失败', e)
      // 发送失败时移除乐观添加的消息
      messages.value = messages.value.filter((m) => m.id !== optimisticId)
      error.value = e?.message || '发送消息失败'
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
