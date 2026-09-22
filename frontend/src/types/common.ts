/**
 * 공통 타입 정의
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
