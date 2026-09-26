import { useState } from 'react';
import { BUTTON_PRIMARY, INPUT_CLASS } from '@/constants/styles';

interface SearchBarProps {
  placeholder: string;
  onSearch: (keyword: string) => void;
}

/** 검색어 입력 + 검색 버튼 (Enter로도 검색) */
function SearchBar({ placeholder, onSearch }: SearchBarProps) {
  const [text, setText] = useState('');

  return (
    <form
      className="mb-3 ml-auto flex w-full max-w-md gap-2"
      onSubmit={(e) => {
        e.preventDefault();
        onSearch(text.trim());
      }}
    >
      <input
        className={INPUT_CLASS}
        value={text}
        placeholder={placeholder}
        onChange={(e) => setText(e.target.value)}
      />
      <button className={BUTTON_PRIMARY}>검색</button>
    </form>
  );
}

export default SearchBar;
