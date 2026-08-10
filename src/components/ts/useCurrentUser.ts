import { computed } from 'vue'
import { useStore } from 'vuex'

export type UserRole = 'DOCTOR' | 'PATIENT' | string

const USER_ID_KEY = 'medical_user_id'
const USER_ROLE_KEY = 'medical_user_role'

interface AnyUser {
  id?: number
  role?: UserRole
  [key: string]: any
}

export function useCurrentUser() {
  const store = useStore()

  const user = computed<AnyUser | null>(() => {
    const state: any = store.state || {}
    return (state.user as AnyUser) || null
  })

  const userId = computed<number | null>(() => {
    if (user.value && typeof user.value.id === 'number') {
      return user.value.id
    }
    const stored = window.localStorage.getItem(USER_ID_KEY)
    return stored ? parseInt(stored, 10) : null
  })

  const role = computed<UserRole | null>(() => {
    if (user.value && user.value.role) {
      return user.value.role as UserRole
    }
    const stored = window.localStorage.getItem(USER_ROLE_KEY)
    return (stored as UserRole) || null
  })

  return {
    user,
    userId,
    role
  }
}