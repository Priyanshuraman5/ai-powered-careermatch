import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { jobApi, applicationApi } from '../api/client'
import { useAuth } from '../context/AuthContext.jsx'
import { matchColor } from '../utils.js'

export default function JobDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const [job, setJob] = useState(null)
  const [loading, setLoading] = useState(true)
  const [applying, setApplying] = useState(false)
  const [applyMessage, setApplyMessage] = useState('')

  useEffect(() => {
    jobApi.getDetail(id).then((res) => setJob(res.data)).finally(() => setLoading(false))
  }, [id])

  const handleApply = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    setApplying(true)
    setApplyMessage('')
    try {
      await applicationApi.apply(Number(id))
      setApplyMessage('Application submitted!')
    } catch (err) {
      setApplyMessage(err.response?.data?.message || 'Could not apply')
    } finally {
      setApplying(false)
    }
  }

  if (loading) return <div className="container">Loading...</div>
  if (!job) return <div className="container">Job not found.</div>

  return (
    <div className="container" style={{ maxWidth: 800 }}>
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
          <div>
            <h2 style={{ marginBottom: 4 }}>{job.title}</h2>
            <p style={{ color: 'var(--color-text-muted)' }}>{job.company} · {job.location}</p>
            <p style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>
              {job.employmentType?.replace('_', ' ')} · {job.experienceLevel}
              {job.salaryMin && job.salaryMax ? ` · $${job.salaryMin.toLocaleString()}–$${job.salaryMax.toLocaleString()}` : ''}
            </p>
          </div>
          {job.matchScore != null && (
            <span className="match-score" style={{ background: matchColor(job.matchScore), fontSize: 16, padding: '8px 16px' }}>
              {Math.round(job.matchScore * 100)}% match
            </span>
          )}
        </div>

        <p style={{ marginTop: 20, lineHeight: 1.6 }}>{job.description}</p>

        <div style={{ marginTop: 20 }}>
          <h4>Required skills</h4>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {job.requiredSkills.map((s) => (
              <span key={s} className="badge" style={{
                borderColor: job.matchedSkills?.includes(s) ? 'var(--color-success)' : 'var(--color-border)'
              }}>{s}</span>
            ))}
          </div>
        </div>

        {job.missingSkills?.length > 0 && (
          <div style={{ marginTop: 16 }}>
            <h4>Skills to build for this role</h4>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
              {job.missingSkills.map((s) => (
                <span key={s} className="badge" style={{ borderColor: 'var(--color-warning)' }}>{s}</span>
              ))}
            </div>
          </div>
        )}

        <button className="btn" style={{ marginTop: 24 }} onClick={handleApply} disabled={applying}>
          {applying ? 'Submitting...' : 'Apply now'}
        </button>
        {applyMessage && <p style={{ marginTop: 10, fontSize: 13 }}>{applyMessage}</p>}
      </div>
    </div>
  )
}
