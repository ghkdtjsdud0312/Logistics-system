import { isAxiosError } from 'axios';

/** 서버 오류 응답의 message를 우선 사용하고 없으면 기본 문구를 돌려준다. */
export function getErrorMessage(error: unknown, fallback = '요청을 처리하지 못했습니다.'): string {
  if (isAxiosError(error)) {
    return error.response?.data?.message ?? fallback;
  }
  return fallback;
}
