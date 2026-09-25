import { INPUT_CLASS, INPUT_ERROR_CLASS } from '@/constants/styles';

interface TextFieldProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  placeholder?: string;
  required?: boolean;
  error?: string;
}

/** 라벨이 있는 입력 필드. 필수는 *로 표시하고 검증 문구는 입력 아래에 보여 준다. */
function TextField({
  label,
  value,
  onChange,
  type = 'text',
  placeholder,
  required,
  error,
}: TextFieldProps) {
  return (
    <label className="block text-xs text-gray-600">
      {label}
      {required && <span className="ml-0.5 text-red-500">*</span>}
      <input
        className={`${INPUT_CLASS} ${error ? INPUT_ERROR_CLASS : ''} mt-1`}
        type={type}
        value={value}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
      />
      {error && <span className="mt-0.5 block text-red-500">{error}</span>}
    </label>
  );
}

export default TextField;
