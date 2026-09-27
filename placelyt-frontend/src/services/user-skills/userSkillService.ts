import apiClient from '../api/apiClient'
import type { UserSkill } from '../../types/userSkill'

export const getUserSkills = async (
  userId: number,
): Promise<UserSkill[]> => {
  const response = await apiClient.get<UserSkill[]>(
    `/api/user-skills/${userId}`,
  )

  return response.data
}