import apiClient from '../api/apiClient'
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from '../../types/auth'

export const login = async (
  credentials: LoginRequest,
): Promise<LoginResponse> => {
  const response = await apiClient.post<LoginResponse>(
    '/api/auth/login',
    credentials,
  )

  return response.data
}

export const register = async (
  userData: RegisterRequest,
): Promise<RegisterResponse> => {
  const response = await apiClient.post<RegisterResponse>(
    '/api/users',
    userData,
  )

  return response.data
}
export const logout = (): void => {
  localStorage.removeItem('placelyt_token')
}