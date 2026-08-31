import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'

import Landing from './pages/Landing.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import ResumeUpload from './pages/ResumeUpload.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Jobs from './pages/Jobs.jsx'
import JobDetail from './pages/JobDetail.jsx'
import SkillGap from './pages/SkillGap.jsx'
import Applications from './pages/Applications.jsx'
import Notifications from './pages/Notifications.jsx'
import Profile from './pages/Profile.jsx'

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/jobs" element={<Jobs />} />
        <Route path="/jobs/:id" element={<JobDetail />} />

        <Route path="/resume-upload" element={
          <ProtectedRoute><ResumeUpload /></ProtectedRoute>
        } />
        <Route path="/dashboard" element={
          <ProtectedRoute><Dashboard /></ProtectedRoute>
        } />
        <Route path="/skill-gap" element={
          <ProtectedRoute><SkillGap /></ProtectedRoute>
        } />
        <Route path="/applications" element={
          <ProtectedRoute><Applications /></ProtectedRoute>
        } />
        <Route path="/notifications" element={
          <ProtectedRoute><Notifications /></ProtectedRoute>
        } />
        <Route path="/profile" element={
          <ProtectedRoute><Profile /></ProtectedRoute>
        } />
      </Routes>
    </>
  )
}
