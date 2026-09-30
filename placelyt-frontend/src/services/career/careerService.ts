import apiClient from '../api/apiClient'
import type {
  CareerPath,
  CareerPathAlternative,
  CareerGuidance,
} from '../../types/career'

export const getCareerPaths = async (
  userId: number,
): Promise<CareerPath[]> => {
  const response = await apiClient.get<CareerPath[]>(
    `/api/career-paths/${userId}`,
  )

  return response.data
}

export const getCareerPathAlternatives = async (
  userId: number,
  targetCareerPathId: number,
): Promise<CareerPathAlternative[]> => {
  const response = await apiClient.get<CareerPathAlternative[]>(
    `/api/career-paths/${userId}/alternatives/${targetCareerPathId}`,
  )

  return response.data
}

export const getCareerGuidance = async (
  userId: number,
  targetCareerPathId: number,
): Promise<CareerGuidance> => {
  const response = await apiClient.get<CareerGuidance>(
    `/api/career-paths/${userId}/${targetCareerPathId}/guidance`,
  )

  return response.data
}