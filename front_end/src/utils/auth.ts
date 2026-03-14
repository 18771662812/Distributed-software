const TOKEN_KEY = 'mall_token'
const USER_KEY = 'mall_user_info'
const REMEMBER_KEY = 'mall_remember_login'

const getStorage = (remember = true) => (remember ? localStorage : sessionStorage)

export function setToken(token: string, remember = true) {
  const storage = getStorage(remember)
  storage.setItem(TOKEN_KEY, token)
  localStorage.setItem(REMEMBER_KEY, String(remember))
  if (remember) {
    sessionStorage.removeItem(TOKEN_KEY)
  } else {
    localStorage.removeItem(TOKEN_KEY)
  }
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY) || ''
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
}

export function setUserInfo(userInfo: unknown, remember = true) {
  getStorage(remember).setItem(USER_KEY, JSON.stringify(userInfo))
  if (remember) {
    sessionStorage.removeItem(USER_KEY)
  } else {
    localStorage.removeItem(USER_KEY)
  }
}

export function getUserInfo<T>() {
  const raw = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as T
  } catch {
    return null
  }
}

export function clearUserInfo() {
  localStorage.removeItem(USER_KEY)
  sessionStorage.removeItem(USER_KEY)
}

export function getRememberLogin() {
  return localStorage.getItem(REMEMBER_KEY) !== 'false'
}
