import axios from 'axios'

const rawBaseUrl = import.meta.env.VITE_API_BASE_URL ? import.meta.env.VITE_API_BASE_URL.replace(/\/+$/, '') : ''

const api = axios.create({
  baseURL: rawBaseUrl ? `${rawBaseUrl}/api` : '/api',
})

// Backend origin, used to resolve uploaded-file URLs (profile picture,
// resume, certificates) returned by the server, e.g. "/uploads/12/picture/x.jpg".
export const API_ORIGIN = rawBaseUrl

export const resolveFileUrl = (path) => {
  if (!path) return null
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  return `${API_ORIGIN}${path.startsWith('/') ? path : '/' + path}`
}

api.interceptors.request.use((config) => {
  // Looks for 'cm_token' first, then falls back to 'token' or 'jwt'
  const token = localStorage.getItem('cm_token') || localStorage.getItem('token') || localStorage.getItem('jwt')
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    console.log(`[API Auth] Token attached to ${config.url}`);
  } else {
    console.warn(`[API Auth] ⚠️ No token found in localStorage for request: ${config.url}`);
    console.log("Current localStorage keys:", Object.keys(localStorage));
  }
  
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if ([401, 403].includes(err.response?.status)) {
      console.warn("Auth check failed for:", err.config?.url);
    }
    return Promise.reject(err)
  }
)

export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
}

export const userApi = {
  // FIX: backend UserProfileController is mapped at /api/profile/me, not
  // /api/users/me. That mismatch was the main reason saves "looked"
  // successful (some other /api/users/me endpoint may have returned 200 for
  // basic account fields) but the profile data itself was never written.
  getProfile: () => api.get('/profile/me'),
  updateProfile: (data) => api.put('/profile/me', data),

  // Real file uploads - the actual bytes go to the server and come back as a URL
  uploadProfilePicture: (file) => {
    const form = new FormData()
    form.append('file', file)
    return api.post('/profile/me/picture', form, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
  uploadResume: (file) => {
    const form = new FormData()
    form.append('file', file)
    return api.post('/profile/me/resume', form, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
  uploadCertificateFiles: (files) => {
    const form = new FormData()
    files.forEach((file) => form.append('files', file))
    return api.post('/profile/me/certificates', form, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
}

export const codingApi = {
  getStats: () => api.get('/coding-profiles/stats'),
  getProfiles: () => api.get('/coding-profiles'),
  getProfileByPlatform: (platform) => api.get(`/coding-profiles/${platform}`),
  saveProfile: (data) => api.post('/coding-profiles', data),
  deleteProfile: (platform) => api.delete(`/coding-profiles/${platform}`),
}

export const resumeApi = {
  upload: (file) => {
    const form = new FormData()
    form.append('file', file)
    return api.post('/resumes/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
  history: () => api.get('/resumes'),
}

export const jobApi = {
  listAll: () => api.get('/jobs'),
  search: (filters) => api.post('/jobs/search', filters),
  externalSearch: (keyword, location) => api.get('/jobs/external-search', {
    params: { keyword, location: location || undefined },
  }),
  liveRecommendations: () => api.get('/jobs/live-recommendations'),
  getDetail: (id) => api.get(`/jobs/${id}`),
}

export const applicationApi = {
  apply: (jobId) => api.post('/applications', { jobId }),
  list: () => api.get('/applications'),
  updateStatus: (id, status) => api.patch(`/applications/${id}/status`, { status }),
}

export const dashboardApi = {
  getSummary: () => api.get('/dashboard'),
}

export const notificationApi = {
  list: () => api.get('/notifications'),
  unreadCount: () => api.get('/notifications/unread-count'),
  markRead: (id) => api.patch(`/notifications/${id}/read`),
}

export default api