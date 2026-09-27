import apiClient from '../api/apiClient'
import type { JobRecommendation } from '../../types/recommendation'

export const getJobRecommendations = async (
  userId: number,
): Promise<JobRecommendation[]> => {
  const response = await apiClient.get<JobRecommendation[]>(
    `/api/recommendations/jobs/${userId}`,
  )

  return response.data
}