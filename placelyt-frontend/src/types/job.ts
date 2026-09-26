export interface Job {
  id: number
  companyId: number
  companyName: string
  title: string
  description: string
  employmentType: string
  workMode: string
  location: string
  minimumSalary: number | null
  maximumSalary: number | null
  minimumCgpa: number | null
  requiredDegree: string | null
  eligibleGraduationYear: number | null
  eligibleBranches: string[]
  requiredSkills: string[]
  applicationDeadline: string | null
  status: string
}