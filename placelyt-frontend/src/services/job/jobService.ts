import apiClient from '../api/apiClient'
import type { Job } from '../../types/job'

export const getAllJobs = async (): Promise<Job[]> => {
  const response = await apiClient.get<Job[]>('/api/jobs')
  return response.data
}

export const getJobById = async (jobId: number): Promise<Job> => {
  const response = await apiClient.get<Job>(`/api/jobs/${jobId}`)
  return response.data
}

export const searchJobs = async (keyword: string): Promise<Job[]> => {
  const response = await apiClient.get<Job[]>('/api/jobs/search', {
    params: { keyword },
  })
  return response.data
}