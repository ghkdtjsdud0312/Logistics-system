import { useEffect, useRef, useState } from 'react';

/** 값이 바뀌면 잠깐(기본 1.2초) true를 돌려준다. 첫 렌더에서는 깜빡이지 않는다. */
export function useFlashOnChange(value: number, durationMs = 1200): boolean {
  const previous = useRef(value);
  const [flashing, setFlashing] = useState(false);

  useEffect(() => {
    if (previous.current === value) return undefined;
    previous.current = value;
    setFlashing(true);
    const timer = setTimeout(() => setFlashing(false), durationMs);
    return () => clearTimeout(timer);
  }, [value, durationMs]);

  return flashing;
}
