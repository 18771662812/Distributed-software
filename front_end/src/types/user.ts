export interface UserInfo {
  id?: number | string
  username: string
  nickname?: string
  phone?: string
  email?: string
  avatar?: string
  token?: string
  accessToken?: string
}

export interface LoginPayload {
  account: string
  password: string
  remember: boolean
}

export interface RegisterPayload {
  username: string
  password: string
  confirmPassword?: string
  phone?: string
  email?: string
}

export interface AuthResult {
  token?: string
  accessToken?: string
  userInfo?: UserInfo
  user?: UserInfo
}
