import { ApiResponse } from '@/types/common';

/** axios 응답에서 ApiResponse의 data만 꺼낸다. */
export async function unwrap<T>(request: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  return (await request).data.data;
}
