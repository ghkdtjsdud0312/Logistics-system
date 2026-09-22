import apiClient from '@/config/axios';

/**
 * 도메인별 API 서비스는 이 파일을 참고하여 services/ 하위에
 * inboundService.ts, outboundService.ts, dispatchService.ts 형태로 분리 작성
 *
 * 예시:
 * export const getInboundList = () => apiClient.get('/inbound');
 */
export default apiClient;
