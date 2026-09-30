export interface CareerPath {
  careerPathId: number
  careerPathName: string
  description: string
  readinessScore: number
  matchedSkills: string[]
  missingSkills: string[]
}

export interface CareerPathAlternative {
  careerPathName: string
  readinessScore: number
  whyAlternative: string
  matchedSkills: string[]
  missingSkills: string[]
}

export interface CareerDirection {
  currentDirection: CareerPath
  targetDirection: CareerPath
}

export interface CareerGuidance {
  learningAreas: LearningArea[]
  projectAreas: ProjectArea[]
  roleTypes: string[]
  shortTermGuidance: string[]
  longTermGuidance: string[]
}

export interface LearningArea {
  area: string
  relatedSkills: string[]
  reason: string
}

export interface ProjectArea {
  area: string
  relatedSkills: string[]
  reason: string
}

export interface SkillIntelligence {
  skills: SkillRelationship[]
}

export interface SkillRelationship {
  skill: string
  relatedSkills: string[]
  transferableSkills: string[]
}

export interface CareerGuidance {
  learningAreas: {
    area: string
    relatedSkills: string[]
    reason: string
  }[]

  projectAreas: {
    area: string
    relatedSkills: string[]
    reason: string
  }[]

  roleTypes: string[]
  shortTermGuidance: string[]
  longTermGuidance: string[]
}