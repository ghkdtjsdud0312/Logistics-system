import apiClient from '@/config/axios';

/**
 * 도메인별 API 서비스는 services/ 하위에 orderService.ts, stockService.ts
 * 형태로 분리해 이 클라이언트를 사용한다. (컴포넌트에서 직접 호출 금지)
 */
export default apiClient;
