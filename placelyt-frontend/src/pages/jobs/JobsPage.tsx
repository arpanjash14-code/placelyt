import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAllJobs } from '../../services/job/jobService'
import type { Job } from '../../types/job'

type SortOption =
  | 'title'
  | 'salary-asc'
  | 'salary-desc'
  | 'deadline'

const JOBS_PER_PAGE = 10

function JobsPage() {
  const [jobs, setJobs] = useState<Job[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const [searchTerm, setSearchTerm] = useState('')
  const [employmentType, setEmploymentType] = useState('')
  const [workMode, setWorkMode] = useState('')
  const [sortBy, setSortBy] = useState<SortOption>('title')
  const [currentPage, setCurrentPage] = useState(1)

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        setIsLoading(true)
        setError('')

        const data = await getAllJobs()
        setJobs(data)
      } catch {
        setError('Unable to load jobs. Please try again.')
      } finally {
        setIsLoading(false)
      }
    }

    void fetchJobs()
  }, [])

  const employmentTypes = [
    ...new Set(jobs.map((job) => job.employmentType)),
  ].sort()

  const workModes = [
    ...new Set(jobs.map((job) => job.workMode)),
  ].sort()

  const formatLabel = (value: string) =>
    value.replaceAll('_', ' ')

  const filteredJobs = jobs.filter((job) => {
    const query = searchTerm.trim().toLowerCase()

    const matchesSearch =
      job.title.toLowerCase().includes(query) ||
      job.companyName.toLowerCase().includes(query) ||
      job.location.toLowerCase().includes(query)

    const matchesEmploymentType =
      employmentType === '' ||
      job.employmentType === employmentType

    const matchesWorkMode =
      workMode === '' || job.workMode === workMode

    return (
      matchesSearch &&
      matchesEmploymentType &&
      matchesWorkMode
    )
  })

  const sortedJobs = [...filteredJobs].sort((a, b) => {
    switch (sortBy) {
      case 'title':
        return a.title.localeCompare(b.title)

      case 'salary-asc':
        if (a.minimumSalary === null) return 1
        if (b.minimumSalary === null) return -1
        return a.minimumSalary - b.minimumSalary

      case 'salary-desc':
        if (a.minimumSalary === null) return 1
        if (b.minimumSalary === null) return -1
        return b.minimumSalary - a.minimumSalary

      case 'deadline':
        if (!a.applicationDeadline) return 1
        if (!b.applicationDeadline) return -1
        return (
          new Date(a.applicationDeadline).getTime() -
          new Date(b.applicationDeadline).getTime()
        )

      default:
        return 0
    }
  })

  const totalPages = Math.ceil(sortedJobs.length / JOBS_PER_PAGE)

  const startIndex = (currentPage - 1) * JOBS_PER_PAGE

  const paginatedJobs = sortedJobs.slice(
    startIndex,
    startIndex + JOBS_PER_PAGE,
  )

  const handleSearchChange = (value: string) => {
    setSearchTerm(value)
    setCurrentPage(1)
  }

  const handleEmploymentTypeChange = (value: string) => {
    setEmploymentType(value)
    setCurrentPage(1)
  }

  const handleWorkModeChange = (value: string) => {
    setWorkMode(value)
    setCurrentPage(1)
  }

  const handleSortChange = (value: SortOption) => {
    setSortBy(value)
    setCurrentPage(1)
  }

  const clearFilters = () => {
    setSearchTerm('')
    setEmploymentType('')
    setWorkMode('')
    setSortBy('title')
    setCurrentPage(1)
  }

  if (isLoading) {
    return <p>Loading jobs...</p>
  }

  if (error) {
    return (
      <main>
        <h1>Jobs</h1>
        <p role="alert">{error}</p>
        <button onClick={() => window.location.reload()}>
          Try again
        </button>
      </main>
    )
  }

  return (
    <main>
      <h1>Explore Jobs</h1>
      <p>Discover opportunities that match your career goals.</p>

      <div>
        <label htmlFor="job-search">Search jobs</label>
        <input
          id="job-search"
          type="search"
          placeholder="Search by title, company, or location..."
          value={searchTerm}
          onChange={(event) => handleSearchChange(event.target.value)}
        />
      </div>

      <div>
        <label htmlFor="employment-type">
          Employment Type
        </label>
        <select
          id="employment-type"
          value={employmentType}
          onChange={(event) =>
            handleEmploymentTypeChange(event.target.value)
          }
        >
          <option value="">All employment types</option>
          {employmentTypes.map((type) => (
            <option key={type} value={type}>
              {formatLabel(type)}
            </option>
          ))}
        </select>
      </div>

      <div>
        <label htmlFor="work-mode">Work Mode</label>
        <select
          id="work-mode"
          value={workMode}
          onChange={(event) =>
            handleWorkModeChange(event.target.value)
          }
        >
          <option value="">All work modes</option>
          {workModes.map((mode) => (
            <option key={mode} value={mode}>
              {formatLabel(mode)}
            </option>
          ))}
        </select>
      </div>

      <div>
        <label htmlFor="sort-by">Sort by</label>
        <select
          id="sort-by"
          value={sortBy}
          onChange={(event) =>
            handleSortChange(event.target.value as SortOption)
          }
        >
          <option value="title">Job title (A–Z)</option>
          <option value="salary-asc">Salary (Low to High)</option>
          <option value="salary-desc">Salary (High to Low)</option>
          <option value="deadline">
            Application Deadline (Earliest)
          </option>
        </select>
      </div>

      <button type="button" onClick={clearFilters}>
        Clear filters
      </button>

      <p>
        Showing {paginatedJobs.length} of {sortedJobs.length} matching
        jobs ({jobs.length} total)
      </p>

      {jobs.length === 0 ? (
        <p>No jobs are currently available.</p>
      ) : sortedJobs.length === 0 ? (
        <p>No jobs match your search or filters.</p>
      ) : (
        <>
          <section aria-label="Available jobs">
            {paginatedJobs.map((job) => (
              <article key={job.id}>
                <h2>{job.title}</h2>
                <p>{job.companyName}</p>
                <p>{job.location}</p>
                <p>{formatLabel(job.employmentType)}</p>

                {job.minimumSalary !== null ||
                job.maximumSalary !== null ? (
                  <p>
                    Salary: {job.minimumSalary ?? 'Not specified'} –{' '}
                    {job.maximumSalary ?? 'Not specified'}
                  </p>
                ) : null}

                <Link to={`/jobs/${job.id}`}>
                  View details
                </Link>
              </article>
            ))}
          </section>

          {totalPages > 1 && (
            <nav aria-label="Job list pagination">
              <button
                type="button"
                onClick={() => setCurrentPage((page) => page - 1)}
                disabled={currentPage === 1}
              >
                Previous
              </button>

              {Array.from(
                { length: totalPages },
                (_, index) => index + 1,
              ).map((page) => (
                <button
                  key={page}
                  type="button"
                  onClick={() => setCurrentPage(page)}
                  aria-current={currentPage === page ? 'page' : undefined}
                  disabled={currentPage === page}
                >
                  {page}
                </button>
              ))}

              <button
                type="button"
                onClick={() => setCurrentPage((page) => page + 1)}
                disabled={currentPage === totalPages}
              >
                Next
              </button>
            </nav>
          )}
        </>
      )}
    </main>
  )
}

export default JobsPage