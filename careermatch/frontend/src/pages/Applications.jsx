import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { applicationApi } from '../api/client'

const STATUS_COLORS = {
  APPLIED: 'var(--color-primary)',
  UNDER_REVIEW: 'var(--color-warning)',
  INTERVIEW: 'var(--color-warning)',
  OFFER: 'var(--color-success)',
  REJECTED: 'var(--color-danger)',
  WITHDRAWN: 'var(--color-text-muted)',
}

export default function Applications() {
  const [apps, setApps] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    applicationApi.list().then((res) => setApps(res.data)).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="container">Loading...</div>

  return (
    <div className="container">
      <h2>Your Applications</h2>

      {apps.length === 0 && (
        <p style={{ color: 'var(--color-text-muted)' }}>
          You haven't applied to any jobs yet. <Link to="/jobs" style={{ color: 'var(--color-primary)' }}>Browse jobs →</Link>
        </p>
      )}

      <div className="grid grid-2">
        {apps.map((app) => (
          <div key={app.id} className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <div>
                <Link to={`/jobs/${app.jobId}`}><strong>{app.jobTitle}</strong></Link>
                <p style={{ color: 'var(--color-text-muted)', fontSize: 13, margin: '4px 0' }}>{app.company}</p>
                <p style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>
                  Applied {new Date(app.appliedAt).toLocaleDateString()}
                  {app.matchScoreAtApply != null ? ` · ${Math.round(app.matchScoreAtApply * 100)}% match` : ''}
                </p>
              </div>
              <span className="badge" style={{ borderColor: STATUS_COLORS[app.status], height: 'fit-content' }}>
                {app.status.replace('_', ' ')}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
