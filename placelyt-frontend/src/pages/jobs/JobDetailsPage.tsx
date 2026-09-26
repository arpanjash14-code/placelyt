import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getJobById } from '../../services/job/jobService'
import type { Job } from '../../types/job'
import axios from 'axios'

function JobDetailsPage() {
  const { jobId } = useParams<{ jobId: string }>()

  const [job, setJob] = useState<Job | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchJob = async () => {
      if (!jobId || !/^\d+$/.test(jobId)) {
        setError('Invalid job ID.')
        setIsLoading(false)
        return
      }

      try {
        setIsLoading(true)
        setError('')

        const data = await getJobById(Number(jobId))
        setJob(data)
      } catch (error) {
  if (axios.isAxiosError(error) && error.response?.status === 404) {
    setError('Job not found.')
  } else {
    setError('Unable to load job details. Please try again.')
  }
} finally {
        setIsLoading(false)
      }
    }

    void fetchJob()
  }, [jobId])

  if (isLoading) {
    return <p>Loading job details...</p>
  }

  if (error) {
    return (
      <main>
        <h1>Job Details</h1>
        <p role="alert">{error}</p>
        <Link to="/jobs">Back to jobs</Link>
      </main>
    )
  }

  if (!job) {
    return (
      <main>
        <h1>Job not found</h1>
        <Link to="/jobs">Back to jobs</Link>
      </main>
    )
  }

  return (
    <main>
      <Link to="/jobs">← Back to jobs</Link>

      <h1>{job.title}</h1>
      <h2>{job.companyName}</h2>

      <p>{job.location}</p>
      <p>{job.employmentType.replaceAll('_', ' ')}</p>
      <p>{job.workMode.replaceAll('_', ' ')}</p>

      <h2>Salary</h2>
      <p>
        {job.minimumSalary ?? 'Not specified'} –{' '}
        {job.maximumSalary ?? 'Not specified'}
      </p>

      <h2>Job Description</h2>
      <p>{job.description || 'No description provided.'}</p>

      <h2>Eligibility</h2>
      <p>
        Minimum CGPA: {job.minimumCgpa ?? 'Not specified'}
      </p>
      <p>
        Required Degree: {job.requiredDegree ?? 'Not specified'}
      </p>
      <p>
        Eligible Graduation Year:{' '}
        {job.eligibleGraduationYear ?? 'Not specified'}
      </p>
      <p>
        Eligible Branches:{' '}
        {job.eligibleBranches?.length
          ? job.eligibleBranches.join(', ')
          : 'Not specified'}
      </p>

      <h2>Required Skills</h2>
      {job.requiredSkills?.length ? (
        <ul>
          {job.requiredSkills.map((skill) => (
            <li key={skill}>{skill}</li>
          ))}
        </ul>
      ) : (
        <p>No specific skills listed.</p>
      )}

      <h2>Application Deadline</h2>
      <p>{job.applicationDeadline ?? 'Not specified'}</p>

      <p>Status: {job.status}</p>
    </main>
  )
}

export default JobDetailsPage