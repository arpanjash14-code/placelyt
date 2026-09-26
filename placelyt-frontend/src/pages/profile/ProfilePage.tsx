import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Check,
  LogOut,
  Pencil,
  Save,
  Shield,
  User as UserIcon,
  X,
} from 'lucide-react'

import { useAuth } from '../../context/AuthContext'
import {
  getCurrentUser,
  updateCurrentUser,
} from '../../services/user/userService'

import type { User } from '../../types/user'

import './ProfilePage.css'

function ProfilePage() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const [user, setUser] = useState<User | null>(null)
  const [name, setName] = useState('')
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)

  const [error, setError] = useState('')
  const [saveError, setSaveError] = useState('')
  const [saveSuccess, setSaveSuccess] = useState('')

  const [isEditing, setIsEditing] = useState(false)

  useEffect(() => {
    const loadUser = async () => {
      try {
        const currentUser = await getCurrentUser()

        setUser(currentUser)
        setName(currentUser.name)
      } catch {
        setError('Unable to load your profile.')
      } finally {
        setIsLoading(false)
      }
    }

    loadUser()
  }, [])

  const handleEdit = () => {
    if (!user) {
      return
    }

    setName(user.name)
    setSaveError('')
    setSaveSuccess('')
    setIsEditing(true)
  }

  const handleCancel = () => {
    if (user) {
      setName(user.name)
    }

    setSaveError('')
    setSaveSuccess('')
    setIsEditing(false)
  }

  const handleSave = async () => {
    const trimmedName = name.trim()

    setSaveError('')
    setSaveSuccess('')

    if (!trimmedName) {
      setSaveError('Name is required.')
      return
    }

    if (trimmedName.length > 100) {
      setSaveError('Name must not exceed 100 characters.')
      return
    }

    setIsSaving(true)

    try {
      const updatedUser = await updateCurrentUser({
        name: trimmedName,
      })

      setUser(updatedUser)
      setName(updatedUser.name)
      setIsEditing(false)
      setSaveSuccess('Profile updated successfully.')
    } catch {
      setSaveError('Unable to update your profile.')
    } finally {
      setIsSaving(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  if (isLoading) {
    return (
      <main className="profile-page">
        <section className="profile-card profile-card--state">
          <p>Loading profile...</p>
        </section>
      </main>
    )
  }

  if (error) {
    return (
      <main className="profile-page">
        <section className="profile-card profile-card--state">
          <p className="profile-message profile-message--error" role="alert">
            {error}
          </p>
        </section>
      </main>
    )
  }

  if (!user) {
    return (
      <main className="profile-page">
        <section className="profile-card profile-card--state">
          <p className="profile-message profile-message--error" role="alert">
            Profile information is unavailable.
          </p>
        </section>
      </main>
    )
  }

  const initials = user.name
    .trim()
    .split(/\s+/)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

  return (
    <main className="profile-page">
      <section className="profile-card">
        <header className="profile-header">
          <div className="profile-avatar" aria-hidden="true">
            {initials || 'U'}
          </div>

          <div className="profile-header-content">
            <p className="profile-eyebrow">Your account</p>
            <h1>Profile</h1>
            <p className="profile-subtitle">
              Manage your personal information and account details.
            </p>
          </div>
        </header>

        <div className="profile-divider" />

        {!isEditing ? (
          <>
            <div className="profile-identity">
              <div>
                <h2>{user.name}</h2>
                <p>{user.role}</p>
              </div>
            </div>

            <div className="profile-details">
              <div className="profile-detail">
                <div className="profile-detail-icon" aria-hidden="true">
                  <UserIcon size={18} />
                </div>

                <div>
                  <span className="profile-detail-label">Name</span>
                  <span className="profile-detail-value">{user.name}</span>
                </div>
              </div>

              <div className="profile-detail">
                <div className="profile-detail-icon" aria-hidden="true">
                  <Shield size={18} />
                </div>

                <div>
                  <span className="profile-detail-label">Email</span>
                  <span className="profile-detail-value">
                    {user.email}
                  </span>
                </div>
              </div>

              <div className="profile-detail">
                <div className="profile-detail-icon" aria-hidden="true">
                  <Shield size={18} />
                </div>

                <div>
                  <span className="profile-detail-label">Role</span>
                  <span className="profile-detail-value">
                    {user.role}
                  </span>
                </div>
              </div>
            </div>

            {saveSuccess && (
              <p className="profile-message profile-message--success" role="status">
                <Check size={17} aria-hidden="true" />
                {saveSuccess}
              </p>
            )}

            <div className="profile-actions">
              <button
                className="profile-button profile-button--primary"
                type="button"
                onClick={handleEdit}
              >
                <Pencil size={17} aria-hidden="true" />
                Edit Profile
              </button>

              <button
                className="profile-button profile-button--secondary"
                type="button"
                onClick={handleLogout}
              >
                <LogOut size={17} aria-hidden="true" />
                Logout
              </button>
            </div>
          </>
        ) : (
          <>
            <div className="profile-edit-header">
              <div>
                <h2>Edit Profile</h2>
                <p>Update the name displayed on your account.</p>
              </div>
            </div>

            <div className="profile-form">
              <div className="profile-field">
                <label htmlFor="profile-name">Name</label>

                <input
                  id="profile-name"
                  type="text"
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  maxLength={100}
                  disabled={isSaving}
                  autoFocus
                />

                <span className="profile-field-hint">
                  Maximum 100 characters.
                </span>
              </div>
            </div>

            {saveError && (
              <p className="profile-message profile-message--error" role="alert">
                {saveError}
              </p>
            )}

            <div className="profile-actions">
              <button
                className="profile-button profile-button--primary"
                type="button"
                onClick={handleSave}
                disabled={isSaving}
              >
                <Save size={17} aria-hidden="true" />
                {isSaving ? 'Saving...' : 'Save Changes'}
              </button>

              <button
                className="profile-button profile-button--secondary"
                type="button"
                onClick={handleCancel}
                disabled={isSaving}
              >
                <X size={17} aria-hidden="true" />
                Cancel
              </button>
            </div>
          </>
        )}
      </section>
    </main>
  )
}

export default ProfilePage