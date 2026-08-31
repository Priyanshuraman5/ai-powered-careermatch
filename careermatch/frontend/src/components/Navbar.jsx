import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '14px 24px', borderBottom: '1px solid var(--color-border)',
      background: 'var(--color-surface)'
    }}>
      <Link to="/" style={{ fontWeight: 800, fontSize: 18 }}>
        Career<span style={{ color: 'var(--color-primary)' }}>Match</span>
      </Link>

      <div style={{ display: 'flex', gap: 20, alignItems: 'center', fontSize: 14 }}>
        {isAuthenticated ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/jobs">Jobs</Link>
            <Link to="/skill-gap">Skill Gap</Link>
            <Link to="/applications">Applications</Link>
            <Link to="/notifications">Notifications</Link>
            <Link to="/profile">Profile</Link>
            <span style={{ color: 'var(--color-text-muted)' }}>Hi, {user.fullName?.split(' ')[0]}</span>
            <button className="btn btn-secondary" onClick={handleLogout}>Log out</button>
          </>
        ) : (
          <>
            <Link to="/jobs">Browse Jobs</Link>
            <Link to="/login">Log in</Link>
            <Link to="/register" className="btn">Sign up</Link>
          </>
        )}
      </div>
    </nav>
  )
}
