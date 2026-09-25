interface Step {
  key: string;
  label: string;
  done: boolean;
}

/** 상태 흐름을 가로 단계(●─●─○)로 보여 준다. 완료 단계는 파랑, 미완료는 회색이다. */
function StepTimeline({ steps }: { steps: Step[] }) {
  return (
    <ol className="flex overflow-x-auto rounded-md border border-gray-200 bg-white p-4">
      {steps.map((s, i) => (
        <li key={s.key} className="flex min-w-24 flex-1 flex-col items-center text-center">
          <div className="flex w-full items-center">
            <span
              className={`h-0.5 flex-1 ${i === 0 ? 'bg-transparent' : s.done ? 'bg-primary' : 'bg-gray-200'}`}
            />
            <span
              className={`flex h-6 w-6 items-center justify-center rounded-full text-xs ${
                s.done
                  ? 'bg-primary text-white'
                  : 'border-2 border-gray-300 bg-white text-transparent'
              }`}
            >
              ✓
            </span>
            <span
              className={`h-0.5 flex-1 ${
                i === steps.length - 1
                  ? 'bg-transparent'
                  : steps[i + 1].done
                    ? 'bg-primary'
                    : 'bg-gray-200'
              }`}
            />
          </div>
          <span
            className={`mt-2 text-xs font-medium ${s.done ? 'text-gray-900' : 'text-gray-400'}`}
          >
            {s.label}
          </span>
        </li>
      ))}
    </ol>
  );
}

export default StepTimeline;
