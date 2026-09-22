import { useContext } from 'react';
// import { AuthContext } from '@/context/AuthContext';

/**
 * 인증 관련 커스텀 훅 (예시 뼈대)
 */
export function useAuth() {
  // return useContext(AuthContext);
  return { user: null, isAuthenticated: false };
}
