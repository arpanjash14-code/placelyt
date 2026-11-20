
import { useState, type ReactNode } from 'react'

import { AuthContext } from './AuthContext'

import {
  login as loginRequest,
  register as registerRequest,
} from '../services/auth/authService'

import type {
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
} from '../types/auth'

interface AuthProviderProps {
  children: ReactNode
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem('placelyt_token'),
  )

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

  const value = {
    token,
    isAuthenticated: token !== null,
    isInitializing: false,
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
