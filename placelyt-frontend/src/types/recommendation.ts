export interface JobRecommendation {
  jobId: number
  jobTitle: string
  companyName: string
  matchScore: number
  eligible: boolean
  matchedSkills: string[]
  missingSkills: string[]
}