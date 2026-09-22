import { useState, type FormEvent } from 'react'
import { Eye, EyeOff } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')

  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const hasMinimumLength = password.length >= 8
  const hasUppercase = /[A-Z]/.test(password)
  const hasLowercase = /[a-z]/.test(password)
  const hasNumber = /[0-9]/.test(password)
  const hasSpecialCharacter = /[^A-Za-z0-9]/.test(password)

  const isPasswordValid =
    hasMinimumLength &&
    hasUppercase &&
    hasLowercase &&
    hasNumber &&
    hasSpecialCharacter

  const passwordsMatch =
    password.length > 0 && password === confirmPassword

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    setError('')

    if (!isPasswordValid) {
      setError('Please meet all password requirements.')
      return
    }

    if (!passwordsMatch) {
      setError('Passwords do not match.')
      return
    }

    setIsLoading(true)

    try {
      await register({
        name,
        email,
        password,
        role: 'STUDENT',
      })

      navigate('/login', { replace: true })
    } catch {
      setError('Registration failed. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <main>
      <h1>Create your Placelyt account</h1>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Name</label>
          <input
            id="name"
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
          />
        </div>

        <div>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </div>

        <div>
          <label htmlFor="password">Password</label>

          <div>
            <input
              id="password"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />

            <button
              type="button"
              onClick={() => setShowPassword((current) => !current)}
              aria-label={showPassword ? 'Hide password' : 'Show password'}
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
        </div>

        <div>
          <p>Password requirements:</p>

          <ul>
            <li>{hasMinimumLength ? '✓' : '○'} At least 8 characters</li>
            <li>{hasUppercase ? '✓' : '○'} One uppercase letter</li>
            <li>{hasLowercase ? '✓' : '○'} One lowercase letter</li>
            <li>{hasNumber ? '✓' : '○'} One number</li>
            <li>
              {hasSpecialCharacter ? '✓' : '○'} One special character
            </li>
          </ul>
        </div>

        <div>
          <label htmlFor="confirm-password">Confirm password</label>

          <div>
            <input
              id="confirm-password"
              type={showConfirmPassword ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
              required
            />

            <button
              type="button"
              onClick={() =>
                setShowConfirmPassword((current) => !current)
              }
              aria-label={
                showConfirmPassword
                  ? 'Hide confirm password'
                  : 'Show confirm password'
              }
            >
              {showConfirmPassword ? (
                <EyeOff size={18} />
              ) : (
                <Eye size={18} />
              )}
            </button>
          </div>
        </div>

        {confirmPassword.length > 0 && (
          <p>
            {passwordsMatch
              ? 'Passwords match.'
              : 'Passwords do not match.'}
          </p>
        )}

        {error && <p role="alert">{error}</p>}

        <button
          type="submit"
          disabled={isLoading || !isPasswordValid || !passwordsMatch}
        >
          {isLoading ? 'Creating account...' : 'Create account'}
        </button>
      </form>
    </main>
  )
}

export default RegisterPage