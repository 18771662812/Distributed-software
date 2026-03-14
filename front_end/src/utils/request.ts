import axios, { AxiosError, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

import router from '@/router'
import { useUserStore } from '@/stores/modules/user'
import { getToken } from '@/utils/auth'
import type { ApiResponse } from '@/types/api'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 12000,
})

request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    if (typeof res?.code === 'number' && res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.replace({
          path: '/login',
          query: { redirect: router.currentRoute.value.fullPath },
        })
      }
      return Promise.reject(res)
    }

    return res as unknown as AxiosResponse<ApiResponse>
  },
  (error: AxiosError<ApiResponse>) => {
    const status = error.response?.status
    const message =
      error.response?.data?.msg ||
      (status === 401 ? '登录状态已失效，请重新登录' : '网络异常，请稍后重试')

    ElMessage.error(message)

    if (status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.replace('/login')
    }

    return Promise.reject(error)
  },
)

export default request
