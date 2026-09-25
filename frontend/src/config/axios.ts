import axios from 'axios';
import { API_BASE_URL } from '@/config/env';
import { DEFAULT_ACTOR } from '@/constants/actor';

/**
 * 전역 Axios 인스턴스
 * - baseURL은 .env의 VITE_API_BASE_URL 값을 사용
 * - 인증은 없으며 감사로그용 X-Actor 헤더(URL 인코딩)만 보낸다.
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'X-Actor': encodeURIComponent(DEFAULT_ACTOR),
  },
});

export default apiClient;
