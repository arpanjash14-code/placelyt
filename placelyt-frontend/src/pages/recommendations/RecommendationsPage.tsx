import { useEffect, useState } from 'react'
import { getCurrentUser } from '../../services/user/userService'
import { getJobRecommendations } from '../../services/recommendation/recommendationService'
import type { JobRecommendation } from '../../types/recommendation'
import './RecommendationsPage.css'

function RecommendationsPage() {
  const [recommendations, setRecommendations] = useState<
    JobRecommendation[]
  >([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const loadRecommendations = async () => {
      try {
        setIsLoading(true)
        setError(null)

        const user = await getCurrentUser()
        const data = await getJobRecommendations(user.id)

        setRecommendations(data)
      } catch (err) {
        console.error('Failed to load recommendations:', err)
        setError('Unable to load job recommendations. Please try again.')
      } finally {
        setIsLoading(false)
      }
    }

    loadRecommendations()
  }, [])

  if (isLoading) {
    return (
      <div className="recommendations-page">
        <div className="recommendations-state">
          <p>Loading recommendations...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="recommendations-page">
        <div className="recommendations-state recommendations-error" role="alert">
          {error}
        </div>
      </div>
    )
  }

  if (recommendations.length === 0) {
    return (
      <div className="recommendations-page">
        <header className="recommendations-header">
          <h1>Job Recommendations</h1>
          <p>Jobs matched to your profile</p>
        </header>

        <div className="recommendations-state">
          <h2>No recommendations yet</h2>
          <p>
            We couldn't find any matching jobs for your profile right now.
            Check back later as new opportunities become available.
          </p>
        </div>
      </div>
    )
  }

  return (
    <main className="recommendations-page">
      <header className="recommendations-header">
        <h1>Job Recommendations</h1>
        <p>Jobs matched to your profile</p>
      </header>

      <section className="recommendations-list">
        {recommendations.map((recommendation) => (
          <article
            className="recommendation-card"
            key={recommendation.jobId}
          >
            <div className="recommendation-card-header">
              <div>
                <h2>{recommendation.jobTitle}</h2>
                <p className="recommendation-company">
                  {recommendation.companyName}
                </p>
              </div>

              <div className="recommendation-score">
                <span>Match score</span>
                <strong>{recommendation.matchScore.toFixed(1)}%</strong>
              </div>
            </div>

            <div className="recommendation-score-bar">
              <div
                className="recommendation-score-fill"
                role="progressbar"
                aria-label="Match score"
                aria-valuenow={recommendation.matchScore}
                aria-valuemin={0}
                aria-valuemax={100}
                style={{ width: `${recommendation.matchScore}%` }}
              />
            </div>

            <div className="recommendation-eligibility">
              <span
                className={
                  recommendation.eligible
                    ? 'eligibility-badge eligible'
                    : 'eligibility-badge not-eligible'
                }
              >
                {recommendation.eligible ? 'Eligible' : 'Not eligible'}
              </span>
            </div>

            <div className="recommendation-skills">
              <div className="recommendation-skill-group">
                <h3>Matched Skills</h3>
                {recommendation.matchedSkills.length > 0 ? (
                  <ul className="skill-list matched-skills">
                    {recommendation.matchedSkills.map((skill) => (
                      <li key={skill}>{skill}</li>
                    ))}
                  </ul>
                ) : (
                  <p className="no-skills">No matched skills</p>
                )}
              </div>

              <div className="recommendation-skill-group">
                <h3>Missing Skills</h3>
                {recommendation.missingSkills.length > 0 ? (
                  <ul className="skill-list missing-skills">
                    {recommendation.missingSkills.map((skill) => (
                      <li key={skill}>{skill}</li>
                    ))}
                  </ul>
                ) : (
                  <p className="no-skills">No missing skills</p>
                )}
              </div>
            </div>
          </article>
        ))}
      </section>
    </main>
  )
}

export default RecommendationsPage