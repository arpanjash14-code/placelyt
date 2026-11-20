
import { createContext } from 'react'
import type {
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
} from '../types/auth'

export interface AuthContextType {
  token: string | null
  isAuthenticated: boolean
  isInitializing: boolean
  login: (credentials: LoginRequest) => Promise<void>
  register: (userData: RegisterRequest) => Promise<RegisterResponse>
  logout: () => void
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
)
