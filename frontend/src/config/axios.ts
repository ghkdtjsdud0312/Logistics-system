import axios from 'axios';

/**
 * 전역 Axios 인스턴스
 * - baseURL은 .env의 VITE_API_BASE_URL 값을 사용
 */
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // 공통 에러 처리(401, 500 등)는 이곳에서 확장
    return Promise.reject(error);
  },
);

export default apiClient;
