import { useEffect, useState } from 'react'

import { getCurrentUser } from '../../services/user/userService'
import {
  getCareerPaths,
  getCareerPathAlternatives,
  getCareerGuidance,
} from '../../services/career/careerService'

import type {
  CareerPath,
  CareerPathAlternative,
  CareerGuidance,
} from '../../types/career'

import './CareerPage.css'

const CareerPage = () => {
  const [careerPaths, setCareerPaths] = useState<CareerPath[]>([])
  const [alternatives, setAlternatives] = useState<
    Record<number, CareerPathAlternative[]>
  >({})
  const [guidance, setGuidance] = useState<
    Record<number, CareerGuidance>
  >({})

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [loadingAlternatives, setLoadingAlternatives] = useState<
    Record<number, boolean>
  >({})
  const [alternativeErrors, setAlternativeErrors] = useState<
    Record<number, string>
  >({})

  const [loadingGuidance, setLoadingGuidance] = useState<
    Record<number, boolean>
  >({})
  const [guidanceErrors, setGuidanceErrors] = useState<
    Record<number, string>
  >({})

  const [userId, setUserId] = useState<number | null>(null)
  const [selectedCareerId, setSelectedCareerId] = useState<number | null>(
    null,
  )

  useEffect(() => {
    const loadCareerPaths = async () => {
      try {
        setLoading(true)
        setError('')

        const user = await getCurrentUser()
        setUserId(user.id)

        const data = await getCareerPaths(user.id)
        setCareerPaths(data)
      } catch (err) {
        console.error('Failed to load career paths:', err)
        setError('Unable to load career paths. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    loadCareerPaths()
  }, [])

  // Close the guidance modal when Escape is pressed.
  useEffect(() => {
    if (selectedCareerId === null) return

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setSelectedCareerId(null)
      }
    }

    window.addEventListener('keydown', handleEscape)

    return () => {
      window.removeEventListener('keydown', handleEscape)
    }
  }, [selectedCareerId])

  const handleViewAlternatives = async (careerPathId: number) => {
    if (userId === null) return

    if (alternatives[careerPathId]) {
      setAlternatives((previous) => {
        const updated = { ...previous }
        delete updated[careerPathId]
        return updated
      })
      return
    }

    try {
      setLoadingAlternatives((previous) => ({
        ...previous,
        [careerPathId]: true,
      }))

      setAlternativeErrors((previous) => ({
        ...previous,
        [careerPathId]: '',
      }))

      const data = await getCareerPathAlternatives(userId, careerPathId)

      setAlternatives((previous) => ({
        ...previous,
        [careerPathId]: data,
      }))
    } catch (err) {
      console.error('Failed to load career alternatives:', err)

      setAlternativeErrors((previous) => ({
        ...previous,
        [careerPathId]: 'Unable to load alternatives. Please try again.',
      }))
    } finally {
      setLoadingAlternatives((previous) => ({
        ...previous,
        [careerPathId]: false,
      }))
    }
  }

  const handleViewGuidance = async (careerPathId: number) => {
    // Clicking the same career's button closes its modal.
    if (selectedCareerId === careerPathId) {
      setSelectedCareerId(null)
      return
    }

    // Open the modal immediately.
    setSelectedCareerId(careerPathId)

    // Use cached guidance if it has already been loaded.
    if (guidance[careerPathId]) return

    if (userId === null) return

    try {
      setLoadingGuidance((previous) => ({
        ...previous,
        [careerPathId]: true,
      }))

      setGuidanceErrors((previous) => ({
        ...previous,
        [careerPathId]: '',
      }))

      const data = await getCareerGuidance(userId, careerPathId)

      setGuidance((previous) => ({
        ...previous,
        [careerPathId]: data,
      }))
    } catch (err) {
      console.error('Failed to load career guidance:', err)

      setGuidanceErrors((previous) => ({
        ...previous,
        [careerPathId]:
          'Unable to load career guidance. Please try again.',
      }))
    } finally {
      setLoadingGuidance((previous) => ({
        ...previous,
        [careerPathId]: false,
      }))
    }
  }

  const selectedCareer = careerPaths.find(
    (career) => career.careerPathId === selectedCareerId,
  )

  const selectedGuidance =
    selectedCareerId !== null ? guidance[selectedCareerId] : undefined

  const hasGuidance = (data: CareerGuidance) =>
    (data.learningAreas?.length ?? 0) > 0 ||
    (data.projectAreas?.length ?? 0) > 0 ||
    (data.roleTypes?.length ?? 0) > 0 ||
    (data.shortTermGuidance?.length ?? 0) > 0 ||
    (data.longTermGuidance?.length ?? 0) > 0

  if (loading) {
    return (
      <div className="career-page">
        <div className="career-message">Loading career paths...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="career-page">
        <div className="career-message error-message">{error}</div>
      </div>
    )
  }

  return (
    <div className="career-page">
      <div className="career-header">
        <h1>Career Intelligence</h1>
        <p>
          Explore career paths, discover alternatives, and get guidance
          for your next steps.
        </p>
      </div>

      {careerPaths.length === 0 ? (
        <div className="career-message">
          No career paths are available yet.
        </div>
      ) : (
        <div className="career-grid">
          {careerPaths.map((career) => (
            <div className="career-card" key={career.careerPathId}>
              <div className="career-card-header">
                <h2>{career.careerPathName}</h2>
                <span className="readiness-score">
                  {Math.round(career.readinessScore)}%
                </span>
              </div>

              <p className="career-description">{career.description}</p>

              <div className="readiness-section">
                <div className="readiness-header">
                  <span>Career Readiness</span>
                  <span>{Math.round(career.readinessScore)}%</span>
                </div>

                <div className="readiness-track">
                  <div
                    className="readiness-fill"
                    style={{
                      width: `${Math.min(
                        100,
                        Math.max(0, career.readinessScore),
                      )}%`,
                    }}
                  />
                </div>
              </div>

              <div className="skill-section">
                <h3>Matched Skills</h3>

                {career.matchedSkills.length > 0 ? (
                  <div className="skill-list">
                    {career.matchedSkills.map((skill) => (
                      <span className="skill-badge" key={skill}>
                        {skill}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="empty-skill-message">
                    No matched skills yet.
                  </p>
                )}
              </div>

              <div className="skill-section">
                <h3>Skills to Develop</h3>

                {career.missingSkills.length > 0 ? (
                  <div className="skill-list">
                    {career.missingSkills.map((skill) => (
                      <span
                        className="skill-badge missing-skill"
                        key={skill}
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="empty-skill-message">
                    No missing skills identified.
                  </p>
                )}
              </div>

              <button
                className="alternatives-button"
                onClick={() =>
                  handleViewAlternatives(career.careerPathId)
                }
                disabled={loadingAlternatives[career.careerPathId]}
              >
                {loadingAlternatives[career.careerPathId]
                  ? 'Loading alternatives...'
                  : alternatives[career.careerPathId]
                    ? 'Hide Alternatives'
                    : 'View Alternatives'}
              </button>

              {alternativeErrors[career.careerPathId] && (
                <p className="career-message error-message">
                  {alternativeErrors[career.careerPathId]}
                </p>
              )}

              {alternatives[career.careerPathId] && (
                <div className="alternatives-section">
                  <h3>Alternative Career Paths</h3>

                  {alternatives[career.careerPathId].length > 0 ? (
                    alternatives[career.careerPathId].map(
                      (alternative, index) => (
                        <div
                          className="alternative-card"
                          key={`${alternative.careerPathName}-${index}`}
                        >
                          <h4>{alternative.careerPathName}</h4>

                          <div className="alternative-readiness">
                            Readiness:{' '}
                            {Math.round(alternative.readinessScore)}%
                          </div>

                          <p>{alternative.whyAlternative}</p>

                          {alternative.matchedSkills.length > 0 && (
                            <div className="skill-section">
                              <h4>Matched Skills</h4>
                              <div className="skill-list">
                                {alternative.matchedSkills.map((skill) => (
                                  <span
                                    className="skill-badge"
                                    key={skill}
                                  >
                                    {skill}
                                  </span>
                                ))}
                              </div>
                            </div>
                          )}

                          {alternative.missingSkills.length > 0 && (
                            <div className="skill-section">
                              <h4>Skills to Develop</h4>
                              <div className="skill-list">
                                {alternative.missingSkills.map((skill) => (
                                  <span
                                    className="skill-badge missing-skill"
                                    key={skill}
                                  >
                                    {skill}
                                  </span>
                                ))}
                              </div>
                            </div>
                          )}
                        </div>
                      ),
                    )
                  ) : (
                    <p>No alternatives found for this career path.</p>
                  )}
                </div>
              )}

              <button
                className="alternatives-button guidance-button"
                onClick={() =>
                  handleViewGuidance(career.careerPathId)
                }
                disabled={loadingGuidance[career.careerPathId]}
              >
                {loadingGuidance[career.careerPathId]
                  ? 'Loading guidance...'
                  : selectedCareerId === career.careerPathId
                    ? 'Close Career Guidance'
                    : 'View Career Guidance'}
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Career Guidance Floating Modal */}
      {selectedCareer && (
        <div
          className="guidance-modal-overlay"
          onClick={() => setSelectedCareerId(null)}
        >
          <div
            className="guidance-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="guidance-modal-title"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="guidance-modal-header">
              <div>
                <span className="guidance-modal-eyebrow">
                  CAREER INTELLIGENCE
                </span>
                <h2 id="guidance-modal-title">
                  {selectedCareer.careerPathName}
                </h2>
                <p>Personalized career guidance</p>
              </div>

              <button
                className="guidance-modal-close"
                onClick={() => setSelectedCareerId(null)}
                aria-label="Close career guidance"
              >
                &times;
              </button>
            </div>

            <div className="guidance-modal-content">
              {loadingGuidance[selectedCareer.careerPathId] ? (
                <div className="guidance-modal-message">
                  Loading career guidance...
                </div>
              ) : guidanceErrors[selectedCareer.careerPathId] ? (
                <div className="guidance-modal-message error-message">
                  {guidanceErrors[selectedCareer.careerPathId]}
                </div>
              ) : selectedGuidance ? (
                hasGuidance(selectedGuidance) ? (
                  <>
                    {(selectedGuidance.learningAreas?.length ?? 0) > 0 && (
                      <section className="guidance-group">
                        <h3>Learning Areas</h3>

                        {selectedGuidance.learningAreas.map(
                          (area, index) => (
                            <div
                              className="guidance-card"
                              key={`${area.area}-${index}`}
                            >
                              <h4>{area.area}</h4>

                              {area.reason && <p>{area.reason}</p>}

                              {area.relatedSkills?.length > 0 && (
                                <div className="skill-list">
                                  {area.relatedSkills.map((skill) => (
                                    <span
                                      className="skill-badge"
                                      key={skill}
                                    >
                                      {skill}
                                    </span>
                                  ))}
                                </div>
                              )}
                            </div>
                          ),
                        )}
                      </section>
                    )}

                    {(selectedGuidance.projectAreas?.length ?? 0) > 0 && (
                      <section className="guidance-group">
                        <h3>Project Areas</h3>

                        {selectedGuidance.projectAreas.map(
                          (project, index) => (
                            <div
                              className="guidance-card"
                              key={`${project.area}-${index}`}
                            >
                              <h4>{project.area}</h4>

                              {project.reason && <p>{project.reason}</p>}

                              {project.relatedSkills?.length > 0 && (
                                <div className="skill-list">
                                  {project.relatedSkills.map((skill) => (
                                    <span
                                      className="skill-badge"
                                      key={skill}
                                    >
                                      {skill}
                                    </span>
                                  ))}
                                </div>
                              )}
                            </div>
                          ),
                        )}
                      </section>
                    )}

                    {(selectedGuidance.roleTypes?.length ?? 0) > 0 && (
                      <section className="guidance-group">
                        <h3>Relevant Role Types</h3>
                        <div className="skill-list">
                          {selectedGuidance.roleTypes.map((role) => (
                            <span className="skill-badge" key={role}>
                              {role}
                            </span>
                          ))}
                        </div>
                      </section>
                    )}

                    {(selectedGuidance.shortTermGuidance?.length ?? 0) >
                      0 && (
                      <section className="guidance-group">
                        <h3>Short-Term Guidance</h3>
                        <ul className="guidance-list">
                          {selectedGuidance.shortTermGuidance.map(
                            (item, index) => (
                              <li key={index}>{item}</li>
                            ),
                          )}
                        </ul>
                      </section>
                    )}

                    {(selectedGuidance.longTermGuidance?.length ?? 0) >
                      0 && (
                      <section className="guidance-group">
                        <h3>Long-Term Guidance</h3>
                        <ul className="guidance-list">
                          {selectedGuidance.longTermGuidance.map(
                            (item, index) => (
                              <li key={index}>{item}</li>
                            ),
                          )}
                        </ul>
                      </section>
                    )}
                  </>
                ) : (
                  <div className="guidance-modal-message">
                    No career guidance is available yet.
                  </div>
                )
              ) : (
                <div className="guidance-modal-message">
                  Career guidance is not available.
                </div>
              )}
            </div>

            <div className="guidance-modal-footer">
              <button
                className="guidance-modal-done"
                onClick={() => setSelectedCareerId(null)}
              >
                Done
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default CareerPage