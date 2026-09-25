import { INPUT_CLASS } from '@/constants/styles';

interface TextFieldProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  placeholder?: string;
}

/** 라벨이 있는 입력 필드 */
function TextField({ label, value, onChange, type = 'text', placeholder }: TextFieldProps) {
  return (
    <label className="block text-xs text-gray-600">
      {label}
      <input
        className={`${INPUT_CLASS} mt-1`}
        type={type}
        value={value}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
      />
    </label>
  );
}

export default TextField;
