import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { useAuth } from '../../context/AuthContext'
import {
  getCurrentUser,
  updateCurrentUser,
} from '../../services/user/userService'

import type { User } from '../../types/user'

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
    return <p>Loading profile...</p>
  }

  if (error) {
    return <p role="alert">{error}</p>
  }

  if (!user) {
    return <p role="alert">Profile information is unavailable.</p>
  }

  return (
    <main>
      <h1>Profile</h1>

      {!isEditing ? (
        <>
          <div>
            <p>
              <strong>Name:</strong> {user.name}
            </p>

            <p>
              <strong>Email:</strong> {user.email}
            </p>

            <p>
              <strong>Role:</strong> {user.role}
            </p>
          </div>

          {saveSuccess && <p role="status">{saveSuccess}</p>}

          <button type="button" onClick={handleEdit}>
            Edit Profile
          </button>
        </>
      ) : (
        <>
          <div>
            <label htmlFor="profile-name">Name</label>

            <input
              id="profile-name"
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              maxLength={100}
              disabled={isSaving}
            />
          </div>

          {saveError && <p role="alert">{saveError}</p>}

          <button
            type="button"
            onClick={handleSave}
            disabled={isSaving}
          >
            {isSaving ? 'Saving...' : 'Save Changes'}
          </button>

          <button
            type="button"
            onClick={handleCancel}
            disabled={isSaving}
          >
            Cancel
          </button>
        </>
      )}

      <button type="button" onClick={handleLogout}>
        Logout
      </button>
    </main>
  )
}

export default ProfilePage