import { createContext, useContext, useState, useCallback, useEffect } from 'react'
import { authApi } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('cm_user')
    return stored ? JSON.parse(stored) : null
  })

  const persist = (authResponse) => {
    const { token, ...userInfo } = authResponse
    localStorage.setItem('cm_token', token)
    localStorage.setItem('cm_user', JSON.stringify(userInfo))
    setUser(userInfo)
  }

  const logout = useCallback(() => {
    localStorage.removeItem('cm_token')
    localStorage.removeItem('cm_user')
    setUser(null)
  }, [])

  useEffect(() => {
    window.addEventListener('cm-auth-expired', logout)
    return () => window.removeEventListener('cm-auth-expired', logout)
  }, [logout])

  const login = useCallback(async (email, password) => {
    const res = await authApi.login({ email, password })
    persist(res.data)
    return res.data
  }, [])

  const register = useCallback(async (email, password, fullName) => {
    const res = await authApi.register({ email, password, fullName })
    persist(res.data)
    return res.data
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
