import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Landing() {
  const { isAuthenticated } = useAuth()

  return (
    <div className="container" style={{ paddingTop: 80 }}>
      <div style={{ maxWidth: 700, margin: '0 auto', textAlign: 'center' }}>
        <h1 style={{ fontSize: 44, marginBottom: 12 }}>
          Find the job that actually fits <span style={{ color: 'var(--color-primary)' }}>your skills</span>
        </h1>
        <p style={{ color: 'var(--color-text-muted)', fontSize: 17, marginBottom: 32 }}>
          Upload your resume, let our matching engine score you against real openings,
          and see exactly which skills to build next to land the role you want.
        </p>
        <div style={{ display: 'flex', gap: 12, justifyContent: 'center' }}>
          {isAuthenticated ? (
            <Link to="/dashboard" className="btn">Go to Dashboard</Link>
          ) : (
            <>
              <Link to="/register" className="btn">Get started</Link>
              <Link to="/jobs" className="btn btn-secondary">Browse jobs</Link>
            </>
          )}
        </div>
      </div>

      <div className="grid grid-2" style={{ marginTop: 64 }}>
        <div className="card">
          <h3>Smart matching</h3>
          <p style={{ color: 'var(--color-text-muted)', fontSize: 14 }}>
            Our semantic matching engine compares your resume and skills against every job
            posting to give each one a real match score — not just keyword search.
          </p>
        </div>
        <div className="card">
          <h3>Skill gap insights</h3>
          <p style={{ color: 'var(--color-text-muted)', fontSize: 14 }}>
            See exactly which in-demand skills you're missing across the roles you care
            about, so you know what to learn next.
          </p>
        </div>
        <div className="card">
          <h3>One-click resume parsing</h3>
          <p style={{ color: 'var(--color-text-muted)', fontSize: 14 }}>
            Upload a PDF or Word resume and we'll automatically extract your skills and
            keep your profile up to date.
          </p>
        </div>
        <div className="card">
          <h3>Application tracking</h3>
          <p style={{ color: 'var(--color-text-muted)', fontSize: 14 }}>
            Track every application's status in one dashboard, from applied to offer.
          </p>
        </div>
      </div>
    </div>
  )
}
