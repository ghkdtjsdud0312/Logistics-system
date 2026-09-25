export type Errors = Record<string, string>;
export type Rule = (value: string) => string | null;

export const required =
  (label: string): Rule =>
  (v) =>
    v.trim() ? null : `${label}을(를) 입력하세요.`;

export const selected =
  (label: string): Rule =>
  (v) =>
    v ? null : `${label}을(를) 선택하세요.`;

export const positive =
  (label: string): Rule =>
  (v) =>
    Number(v) > 0 ? null : `${label}은(는) 0보다 커야 합니다.`;

/** 여러 규칙을 순서대로 적용해 처음 실패한 메시지를 돌려준다. */
export const all =
  (...rules: Rule[]): Rule =>
  (v) =>
    rules.map((rule) => rule(v)).find((message) => message !== null) ?? null;

/** 값 전체를 규칙으로 검사해 필드별 오류 메시지를 돌려준다. 통과하면 빈 객체다. */
export function validate(values: Record<string, string>, rules: Record<string, Rule>): Errors {
  const errors: Errors = {};
  Object.entries(rules).forEach(([field, rule]) => {
    const message = rule(values[field] ?? '');
    if (message) errors[field] = message;
  });
  return errors;
}
