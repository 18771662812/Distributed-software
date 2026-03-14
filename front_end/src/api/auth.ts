import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'
import type { AuthResult, LoginPayload, RegisterPayload } from '@/types/user'

export function loginApi(data: LoginPayload) {
  return request.post<ApiResponse<AuthResult>>('/user/login', {
    username: data.account,
    password: data.password,
  })
}

export function registerApi(data: RegisterPayload) {
  return request.post<ApiResponse<string>>('/user/register', {
    username: data.username,
    password: data.password,
    phone: data.phone,
    email: data.email,
  })
}
