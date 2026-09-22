import { BrowserRouter, Routes, Route } from 'react-router-dom'

import LoginPage from '../pages/auth/LoginPage'
import RegisterPage from '../pages/auth/RegisterPage'
import DashboardPage from '../pages/dashboard/DashboardPage'
import JobsPage from '../pages/jobs/JobsPage'
import JobDetailsPage from '../pages/jobs/JobDetailsPage'
import RecommendationsPage from '../pages/recommendations/RecommendationsPage'
import ProfilePage from '../pages/profile/ProfilePage'
import CareerPage from '../pages/career/CareerPage'
import CareerDetailsPage from '../pages/career/CareerDetailsPage'
import ProtectedRoute from './ProtectedRoute'

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<h1>Placelyt Home</h1>} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/jobs" element={<JobsPage />} />
          <Route path="/jobs/:jobId" element={<JobDetailsPage />} />
          <Route
            path="/recommendations"
            element={<RecommendationsPage />}
          />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/career" element={<CareerPage />} />
          <Route
            path="/career/:careerId"
            element={<CareerDetailsPage />}
          />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes