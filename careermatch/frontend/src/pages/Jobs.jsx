import { useEffect, useState } from 'react'
import { jobApi } from '../api/client'

export default function Jobs() {
  const [jobs, setJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filters, setFilters] = useState({ keyword: '', location: '' })

  const loadLiveJobs = async (keyword = 'Software Developer', location = '') => {
    setLoading(true)
    setError('')
    try {
      const response = await jobApi.externalSearch(keyword, location)
      setJobs(response.data)
    } catch (err) {
      setJobs([])
      setError(err.response?.data?.message || 'Could not load live jobs from SerpAPI.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadLiveJobs()
  }, [])

  const handleSearch = (event) => {
    event.preventDefault()
    loadLiveJobs(filters.keyword.trim() || 'Software Developer', filters.location.trim())
  }

  const uniqueSuggestions = (values) => [...new Set(values.filter(Boolean))]
  const keywordSuggestions = uniqueSuggestions(jobs.flatMap((job) => [job.title, job.company]))
  const locationSuggestions = uniqueSuggestions(jobs.map((job) => job.location))

  return (
    <div className="container">
      <h2>Browse live jobs</h2>
      <p style={{ color: 'var(--color-text-muted)', marginTop: -12 }}>
        Current job listings from SerpAPI. Results open at the original source to apply.
      </p>

      <form onSubmit={handleSearch} className="card" style={{ marginBottom: 24 }}>
        <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', marginBottom: 12 }}>
          <input
            placeholder="Job title"
            list="job-keyword-suggestions"
            value={filters.keyword}
            onChange={(event) => setFilters({ ...filters, keyword: event.target.value })}
          />
          <datalist id="job-keyword-suggestions">
            {keywordSuggestions.map((suggestion) => <option key={suggestion} value={suggestion} />)}
          </datalist>
          <input
            placeholder="Location"
            list="job-location-suggestions"
            value={filters.location}
            onChange={(event) => setFilters({ ...filters, location: event.target.value })}
          />
          <datalist id="job-location-suggestions">
            {locationSuggestions.map((suggestion) => <option key={suggestion} value={suggestion} />)}
          </datalist>
        </div>
        <button className="btn" type="submit" disabled={loading}>{loading ? 'Searching...' : 'Search live jobs'}</button>
        <button
          type="button"
          className="btn btn-secondary"
          style={{ marginLeft: 8 }}
          onClick={() => {
            setFilters({ keyword: '', location: '' })
            loadLiveJobs()
          }}
        >
          Clear
        </button>
      </form>

      {error && <p style={{ color: 'var(--color-danger)' }}>{error}</p>}
      {loading ? <p>Loading live jobs...</p> : (
        <div className="grid grid-2">
          {jobs.map((job, index) => (
            <article key={`${job.applyUrl}-${index}`} className="card">
              <strong>{job.title}</strong>
              <p style={{ color: 'var(--color-text-muted)', fontSize: 13, margin: '4px 0' }}>
                {job.company} · {job.location}
              </p>
              {job.via && <p style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>Via {job.via}</p>}
              {job.description && (
                <p style={{ fontSize: 13, lineHeight: 1.5 }}>
                  {job.description.slice(0, 180)}{job.description.length > 180 ? '…' : ''}
                </p>
              )}
              {job.applyUrl
                ? <a className="btn btn-secondary" href={job.applyUrl} target="_blank" rel="noreferrer">Apply on job site</a>
                : <p style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>Direct application link is not available for this listing.</p>}
            </article>
          ))}
          {!error && jobs.length === 0 && <p style={{ color: 'var(--color-text-muted)' }}>No live jobs found. Try a broader title or another location.</p>}
        </div>
      )}
    </div>
  )
}
