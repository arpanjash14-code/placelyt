import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'

import { getCurrentUser } from '../../services/user/userService'
import { getStudentProfile } from '../../services/student-profile/studentProfileService'
import { getAllJobs } from '../../services/job/jobService'
import { getJobRecommendations } from '../../services/recommendation/recommendationService'
import { getUserSkills } from '../../services/user-skills/userSkillService'

import type { User } from '../../types/user'
import type { StudentProfile } from '../../types/studentProfile'
import type { Job } from '../../types/job'
import type { JobRecommendation } from '../../types/recommendation'
import type { UserSkill } from '../../types/userSkill'

import './DashboardPage.css'

function DashboardPage() {
  const [user, setUser] = useState<User | null>(null)
  const [profile, setProfile] = useState<StudentProfile | null>(null)
  const [jobs, setJobs] = useState<Job[]>([])
  const [recommendations, setRecommendations] = useState<JobRecommendation[]>([])
  const [skills, setSkills] = useState<UserSkill[]>([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refreshKey, setRefreshKey] = useState(0)

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        setLoading(true)
        setError('')

        const currentUser = await getCurrentUser()
        setUser(currentUser)

        const results = await Promise.allSettled([
          getStudentProfile(currentUser.id),
          getAllJobs(),
          getJobRecommendations(currentUser.id),
          getUserSkills(currentUser.id),
        ])

        const [
          profileResult,
          jobsResult,
          recommendationsResult,
          skillsResult,
        ] = results

        if (profileResult.status === 'fulfilled') {
          setProfile(profileResult.value)
        }

        if (jobsResult.status === 'fulfilled') {
          setJobs(jobsResult.value)
        }

        if (recommendationsResult.status === 'fulfilled') {
          setRecommendations(recommendationsResult.value)
        }

        if (skillsResult.status === 'fulfilled') {
          setSkills(skillsResult.value)
        }
      } catch {
        setError('Unable to load your dashboard. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    fetchDashboard()
   }, [refreshKey])

  if (loading) {
    return <p>Loading your dashboard...</p>
  }

  if (error) {
    return <p role="alert">{error}</p>
  }

  if (!user) {
    return <p>Unable to load user information.</p>
  }

  const topRecommendations = [...recommendations]
    .sort((a, b) => b.matchScore - a.matchScore)
    .slice(0, 3)

  const availableJobs = jobs.filter((job) => job.status === 'OPEN')

  return (
    <main className="dashboard">
      <header className="dashboard-header">
  <div>
    <h1>Welcome, {user.name}!</h1>
    <p>Here's an overview of your placement journey.</p>
  </div>

  <button
    type="button"
    className="dashboard-action"
    onClick={() => setRefreshKey((prev) => prev + 1)}
    disabled={loading}
  >
    Refresh Dashboard
  </button>
</header>

{!profile && (
  <section className="dashboard-profile-prompt">
    <div>
      <h2>Complete your academic profile</h2>
      <p>
        Add your academic details to get personalized job recommendations.
      </p>
    </div>

    <Link to="/profile" className="dashboard-action">
      Complete Profile
    </Link>
  </section>
)}
      <section className="dashboard-grid">
        <article className="dashboard-card dashboard-stat">
          <span className="dashboard-stat-label">Available Jobs</span>
          <span className="dashboard-stat-value">{availableJobs.length}</span>
        </article>

        <article className="dashboard-card dashboard-stat">
          <span className="dashboard-stat-label">Job Recommendations</span>
          <span className="dashboard-stat-value">
            {recommendations.length}
          </span>
        </article>

        <article className="dashboard-card dashboard-stat">
          <span className="dashboard-stat-label">Your Skills</span>
          <span className="dashboard-stat-value">{skills.length}</span>
        </article>
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-card">
          <h2>Academic Profile</h2>

          {profile ? (
            <div className="dashboard-profile-details">
              <div>
                <span className="dashboard-detail-label">College</span>
                <span className="dashboard-detail-value">
                  {profile.college || 'Not provided'}
                </span>
              </div>

              <div>
                <span className="dashboard-detail-label">Degree</span>
                <span className="dashboard-detail-value">
                  {profile.degree || 'Not provided'}
                </span>
              </div>

              <div>
                <span className="dashboard-detail-label">Branch</span>
                <span className="dashboard-detail-value">
                  {profile.branch || 'Not provided'}
                </span>
              </div>

              <div>
                <span className="dashboard-detail-label">CGPA</span>
                <span className="dashboard-detail-value">
                  {profile.cgpa ?? 'Not provided'}
                </span>
              </div>

              <div>
                <span className="dashboard-detail-label">Graduation Year</span>
                <span className="dashboard-detail-value">
                  {profile.graduationYear ?? 'Not provided'}
                </span>
              </div>

              <div>
                <span className="dashboard-detail-label">Location</span>
                <span className="dashboard-detail-value">
                  {profile.location || 'Not provided'}
                </span>
              </div>
            </div>
          ) : (
            <p className="dashboard-empty">
              Your academic profile is not available. Complete your profile
              to get personalized recommendations.
            </p>
          )}

          <div className="dashboard-quick-actions">
            <Link to="/profile" className="dashboard-action">
              View Profile
            </Link>
          </div>
        </article>

        <article className="dashboard-card">
          <h2>Top Recommendations</h2>

          {topRecommendations.length > 0 ? (
            <div className="dashboard-recommendation-list">
              {topRecommendations.map((recommendation) => (
                <Link
  to={`/jobs/${recommendation.jobId}`}
  className="dashboard-recommendation"
  key={recommendation.jobId}
>
  <h3>{recommendation.jobTitle}</h3>
  <p>{recommendation.companyName}</p>
  <span className="dashboard-match">
    {recommendation.matchScore}% Match
  </span>
</Link>
              ))}
            </div>
          ) : (
            <p className="dashboard-empty">
              No recommendations available yet.
            </p>
          )}

          <div className="dashboard-quick-actions">
            <Link to="/recommendations" className="dashboard-action">
              View All Recommendations
            </Link>
          </div>
        </article>
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-card">
          <h2>Your Skills</h2>

          {skills.length > 0 ? (
            <div className="dashboard-skills">
              {skills.map((skill) => (
                <span className="dashboard-skill" key={skill.id}>
                  {skill.skillName}
                  {skill.proficiency ? ` · ${skill.proficiency}` : ''}
                </span>
              ))}
            </div>
          ) : (
            <p className="dashboard-empty">
              No skills added yet. Add your skills to improve your job matches.
            </p>
          )}
        </article>

        <article className="dashboard-card">
          <h2>Quick Actions</h2>

          <div className="dashboard-quick-actions">
            <Link to="/jobs" className="dashboard-action">
              Explore Jobs
            </Link>

            <Link to="/recommendations" className="dashboard-action">
              Recommendations
            </Link>

            <Link to="/profile" className="dashboard-action">
              My Profile
            </Link>
          </div>
        </article>
      </section>
    </main>
  )
}

export default DashboardPage