import apiClient from '../api/apiClient'
import type { StudentProfile } from '../../types/studentProfile'

export const getStudentProfile = async (
  userId: number,
): Promise<StudentProfile> => {
  const response = await apiClient.get<StudentProfile>(
    `/api/student-profile/${userId}`,
  )

  return response.data
}