import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { loginApi } from '@/api/auth'
import type { LoginPayload, UserInfo, AuthResult } from '@/types/user'
import {
  clearToken,
  clearUserInfo,
  getRememberLogin,
  getToken,
  getUserInfo,
  setToken,
  setUserInfo,
} from '@/utils/auth'

function normalizeToken(payload?: AuthResult) {
  return payload?.token || payload?.accessToken || ''
}

function normalizeUserInfo(payload?: AuthResult) {
  return payload?.userInfo || payload?.user || null
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken())
  const userInfo = ref<UserInfo | null>(getUserInfo<UserInfo>())
  const rememberLogin = ref<boolean>(getRememberLogin())

  const isLoggedIn = computed(() => Boolean(token.value))

  async function login(payload: LoginPayload) {
    const res = await loginApi(payload)
    const authPayload = (res.data || {}) as AuthResult
    const nextToken = normalizeToken(authPayload)
    const nextUser = normalizeUserInfo(authPayload)

    if (!nextToken) {
      throw new Error('登录接口未返回 token，请将后端返回结构替换为标准鉴权数据')
    }

    token.value = nextToken
    userInfo.value = nextUser
    rememberLogin.value = payload.remember

    setToken(nextToken, payload.remember)
    setUserInfo(nextUser, payload.remember)

    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    clearToken()
    clearUserInfo()
  }

  return {
    token,
    userInfo,
    rememberLogin,
    isLoggedIn,
    login,
    logout,
  }
})
