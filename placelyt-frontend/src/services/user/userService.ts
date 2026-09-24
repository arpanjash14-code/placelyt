import apiClient from '../api/apiClient'
import type { User } from '../../types/user'

export interface UpdateUserRequest {
  name: string
}

export const getCurrentUser = async (): Promise<User> => {
  const response = await apiClient.get<User>('/api/users/me')

  return response.data
}

export const updateCurrentUser = async (
  request: UpdateUserRequest,
): Promise<User> => {
  const response = await apiClient.put<User>(
    '/api/users/me',
    request,
  )

  return response.data
}