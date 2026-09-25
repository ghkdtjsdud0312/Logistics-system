import { INPUT_CLASS } from '@/constants/styles';
import { Option } from '@/types/ui';

interface SelectFieldProps {
  value: string;
  onChange: (value: string) => void;
  options: Option[];
  label?: string;
  placeholder?: string;
}

/** 선택 필드. placeholder가 있으면 빈 값 선택지를 함께 보여 준다. */
function SelectField({ value, onChange, options, label, placeholder }: SelectFieldProps) {
  return (
    <label className="block text-xs text-gray-600">
      {label}
      <select
        className={`${INPUT_CLASS} ${label ? 'mt-1' : ''}`}
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
    </label>
  );
}

export default SelectField;
