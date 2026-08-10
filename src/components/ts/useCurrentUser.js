//当前用户信息管理
import { computed } from 'vue'
import { useStore } from 'vuex'

const USER_ID_KEY = 'medical_user_id'
const USER_ROLE_KEY = 'medical_user_role'

export function useCurrentUser() {
  const store = useStore()

  const user = computed(() => {
    const state = store.state || {}
    return state.user || null
  })

  const userId = computed(() => {
    if (user.value && typeof user.value.id === 'number') {
      return user.value.id
    }
    const stored = window.localStorage.getItem(USER_ID_KEY)
    return stored ? parseInt(stored, 10) : null
  })

  const role = computed(() => {
    if (user.value && user.value.role) {
      return user.value.role
    }
    const stored = window.localStorage.getItem(USER_ROLE_KEY)
    return stored || null
  })

  return {
    user,
    userId,
    role
  }
}