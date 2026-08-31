import { useEffect, useState } from 'react'
import { notificationApi } from '../api/client'

export default function Notifications() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    notificationApi.list().then((res) => setItems(res.data)).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleMarkRead = async (id) => {
    await notificationApi.markRead(id)
    setItems((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)))
  }

  if (loading) return <div className="container">Loading...</div>

  return (
    <div className="container" style={{ maxWidth: 650 }}>
      <h2>Notifications</h2>

      {items.length === 0 && <p style={{ color: 'var(--color-text-muted)' }}>No notifications yet.</p>}

      {items.map((n) => (
        <div key={n.id} className="card" style={{
          marginBottom: 10,
          opacity: n.read ? 0.6 : 1,
          borderColor: n.read ? 'var(--color-border)' : 'var(--color-primary)'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <strong>{n.title}</strong>
            {!n.read && (
              <button className="btn btn-secondary" style={{ padding: '4px 10px', fontSize: 12 }}
                onClick={() => handleMarkRead(n.id)}>
                Mark read
              </button>
            )}
          </div>
          <p style={{ fontSize: 14, margin: '6px 0', color: 'var(--color-text-muted)' }}>{n.message}</p>
          <p style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>{new Date(n.createdAt).toLocaleString()}</p>
        </div>
      ))}
    </div>
  )
}
