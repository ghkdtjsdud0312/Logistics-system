/** 수정 모달의 입력 필드 정의 */
export interface EditField {
  name: string;
  label: string;
  type?: string;
  /** false면 비워도 된다. 기본은 필수. */
  required?: boolean;
}
