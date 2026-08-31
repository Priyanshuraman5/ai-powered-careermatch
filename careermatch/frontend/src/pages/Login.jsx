import { useState } from 'react'
import { useNavigate, Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('demo@careermatch.dev')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, password)
      navigate(location.state?.from?.pathname || '/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container" style={{ maxWidth: 400, paddingTop: 60 }}>
      <div className="card">
        <h2>Log in</h2>
        <p style={{ color: 'var(--color-text-muted)', fontSize: 13, marginTop: -8 }}>
          Demo account: demo@careermatch.dev / password123
        </p>
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 14 }}>
            <label>Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <div style={{ marginBottom: 14 }}>
            <label>Password</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          {error && <p style={{ color: 'var(--color-danger)', fontSize: 13 }}>{error}</p>}
          <button className="btn" type="submit" disabled={loading} style={{ width: '100%' }}>
            {loading ? 'Logging in...' : 'Log in'}
          </button>
        </form>
        <p style={{ fontSize: 13, marginTop: 16, color: 'var(--color-text-muted)' }}>
          No account? <Link to="/register" style={{ color: 'var(--color-primary)' }}>Sign up</Link>
        </p>
      </div>
    </div>
  )
}
