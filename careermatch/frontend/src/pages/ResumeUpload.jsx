import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { resumeApi } from '../api/client'

export default function ResumeUpload() {
  const navigate = useNavigate()
  const [file, setFile] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')
  const [history, setHistory] = useState([])

  useEffect(() => {
    resumeApi.history().then((res) => setHistory(res.data)).catch(() => {})
  }, [])

  const handleUpload = async (e) => {
    e.preventDefault()
    if (!file) return
    setUploading(true)
    setError('')
    try {
      const res = await resumeApi.upload(file)
      setResult(res.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Upload failed')
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="container" style={{ maxWidth: 600 }}>
      <h2>Upload your resume</h2>
      <p style={{ color: 'var(--color-text-muted)' }}>
        We'll extract your skills automatically (PDF, DOCX, or plain text) and add them to your profile.
      </p>

      <form onSubmit={handleUpload} className="card" style={{ marginBottom: 20 }}>
        <input type="file" accept=".pdf,.docx,.txt" onChange={(e) => setFile(e.target.files[0])} />
        {error && <p style={{ color: 'var(--color-danger)', fontSize: 13, marginTop: 10 }}>{error}</p>}
        <button className="btn" type="submit" disabled={!file || uploading} style={{ marginTop: 14 }}>
          {uploading ? 'Processing...' : 'Upload & Process'}
        </button>
      </form>

      {result && (
        <div className="card" style={{ marginBottom: 20, borderColor: 'var(--color-success)' }}>
          <h4>Processed: {result.fileName}</h4>
          <p style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>Status: {result.status}</p>
          <p style={{ fontSize: 13, marginBottom: 8 }}>Extracted skills:</p>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {result.extractedSkills.map((s) => <span key={s} className="badge">{s}</span>)}
          </div>
          <button className="btn" style={{ marginTop: 16 }} onClick={() => navigate('/dashboard')}>
            Go to Dashboard
          </button>
        </div>
      )}

      {history.length > 0 && (
        <div>
          <h4>Upload history</h4>
          {history.map((r) => (
            <div key={r.id} className="card" style={{ marginBottom: 8, fontSize: 13 }}>
              <strong>{r.fileName}</strong> — {r.status} — {new Date(r.uploadedAt).toLocaleString()}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
