import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from 'react'

import {
  login as loginRequest,
  register as registerRequest,
} from '../services/auth/authService'

import type {
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
} from '../types/auth'

interface AuthContextType {
  token: string | null
  isAuthenticated: boolean
  isInitializing: boolean
  login: (credentials: LoginRequest) => Promise<void>
  register: (userData: RegisterRequest) => Promise<RegisterResponse>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

interface AuthProviderProps {
  children: ReactNode
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [token, setToken] = useState<string | null>(null)
  const [isInitializing, setIsInitializing] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem('placelyt_token')

    if (storedToken) {
      setToken(storedToken)
    }

    setIsInitializing(false)
  }, [])

  const login = async (credentials: LoginRequest) => {
    const response = await loginRequest(credentials)

    localStorage.setItem('placelyt_token', response.token)
    setToken(response.token)
  }

  const register = async (
    userData: RegisterRequest,
  ): Promise<RegisterResponse> => {
    return await registerRequest(userData)
  }

  const logout = () => {
    localStorage.removeItem('placelyt_token')
    setToken(null)
  }

  const value: AuthContextType = {
    token,
    isAuthenticated: token !== null,
    isInitializing,
    login,
    register,
    logout,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }

  return context
}