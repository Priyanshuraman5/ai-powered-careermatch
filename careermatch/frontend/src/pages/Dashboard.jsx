import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { dashboardApi, jobApi } from '../api/client'

export default function Dashboard() {
  const [summary, setSummary] = useState(null)
  const [liveJobs, setLiveJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [refreshingLiveJobs, setRefreshingLiveJobs] = useState(false)
  const [error, setError] = useState('')
  const [liveError, setLiveError] = useState('')
  const hasLoaded = useRef(false)

  const loadLiveRecommendations = async () => {
    setRefreshingLiveJobs(true)
    setLiveError('')
    try {
      const response = await jobApi.liveRecommendations()
      setLiveJobs(response.data)
    } catch (err) {
      setLiveJobs([])
      setLiveError(err.response?.data?.message || 'Could not load live recommendations from SerpAPI.')
    } finally {
      setRefreshingLiveJobs(false)
    }
  }

  useEffect(() => {
    if (hasLoaded.current) return
    hasLoaded.current = true

    const loadDashboard = async () => {
      try {
        const response = await dashboardApi.getSummary()
        setSummary(response.data)
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load dashboard')
      } finally {
        setLoading(false)
      }
      loadLiveRecommendations()
    }

    loadDashboard()
  }, [])

  if (loading) return <div className="container">Loading dashboard...</div>
  if (error) return <div className="container" style={{ color: 'var(--color-danger)' }}>{error}</div>
  if (!summary) return null

  return (
    <div className="container">
      <h2>Your Dashboard</h2>

      <div className="grid grid-2" style={{ marginBottom: 24 }}>
        <StatCard label="Applications" value={summary.totalApplications} />
        <StatCard label="Interviews" value={summary.interviewCount} />
        <StatCard label="Offers" value={summary.offerCount} />
        <StatCard label="Avg match score" value={`${Math.round(summary.averageMatchScore * 100)}%`} />
      </div>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12 }}>
        <div>
          <h3 style={{ marginBottom: 4 }}>Live recommendations for you</h3>
          <p style={{ color: 'var(--color-text-muted)', fontSize: 13, marginTop: 0 }}>
            Current jobs from SerpAPI, based on your profile skills and location.
          </p>
        </div>
        <button className="btn btn-secondary" type="button" onClick={loadLiveRecommendations} disabled={refreshingLiveJobs}>
          {refreshingLiveJobs ? 'Refreshing…' : 'Refresh live jobs'}
        </button>
      </div>

      {liveError && <p style={{ color: 'var(--color-danger)' }}>{liveError}</p>}
      {refreshingLiveJobs && liveJobs.length === 0 ? <p>Loading live jobs...</p> : (
        <div className="grid grid-2" style={{ marginBottom: 24 }}>
          {liveJobs.map((job, index) => (
            <article key={`${job.applyUrl}-${index}`} className="card">
              <strong>{job.title}</strong>
              <p style={{ color: 'var(--color-text-muted)', fontSize: 13, margin: '4px 0' }}>
                {job.company} · {job.location}
              </p>
              {job.via && <p style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>Via {job.via}</p>}
              {job.applyUrl
                ? <a className="btn btn-secondary" href={job.applyUrl} target="_blank" rel="noreferrer">Apply on job site</a>
                : <p style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>Direct application link is not available for this listing.</p>}
            </article>
          ))}
          {!liveError && !refreshingLiveJobs && liveJobs.length === 0 && (
            <p style={{ color: 'var(--color-text-muted)' }}>No live jobs were found. Add skills and a location to your profile, then refresh.</p>
          )}
        </div>
      )}

      <h3>Top skills to build</h3>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 24 }}>
        {summary.topMissingSkills.length === 0 && (
          <p style={{ color: 'var(--color-text-muted)' }}>No major skill gaps detected — nice work!</p>
        )}
        {summary.topMissingSkills.map((s) => <span key={s} className="badge">{s}</span>)}
      </div>

      <Link to="/skill-gap" className="btn btn-secondary">View full skill gap breakdown →</Link>
    </div>
  )
}

function StatCard({ label, value }) {
  return (
    <div className="card">
      <div style={{ fontSize: 28, fontWeight: 700 }}>{value}</div>
      <div style={{ color: 'var(--color-text-muted)', fontSize: 13 }}>{label}</div>
    </div>
  )
}
