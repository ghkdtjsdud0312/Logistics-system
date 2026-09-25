import { INPUT_CLASS, INPUT_ERROR_CLASS } from '@/constants/styles';
import { Option } from '@/types/ui';

interface SelectFieldProps {
  value: string;
  onChange: (value: string) => void;
  options: Option[];
  label?: string;
  placeholder?: string;
  required?: boolean;
  error?: string;
}

/** 선택 필드. placeholder가 있으면 빈 값 선택지를 함께 보여 준다. */
function SelectField({
  value,
  onChange,
  options,
  label,
  placeholder,
  required,
  error,
}: SelectFieldProps) {
  return (
    <label className="block text-xs text-gray-600">
      {label}
      {label && required && <span className="ml-0.5 text-red-500">*</span>}
      <select
        className={`${INPUT_CLASS} ${error ? INPUT_ERROR_CLASS : ''} ${label ? 'mt-1' : ''}`}
        value={value}
        onChange={(e) => onChange(e.target.value)}
      >
        {placeholder !== undefined && <option value="">{placeholder}</option>}
        {options.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
      {error && <span className="mt-0.5 block text-red-500">{error}</span>}
    </label>
  );
}

export default SelectField;
