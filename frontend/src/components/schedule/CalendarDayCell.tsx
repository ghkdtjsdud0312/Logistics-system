import { useState } from 'react';
import EventChip from '@/components/schedule/EventChip';
import { CalendarDay, ScheduleEvent } from '@/types/schedule';

const VISIBLE = 3;

interface CalendarDayCellProps {
  day: CalendarDay;
  events: ScheduleEvent[];
  isToday: boolean;
}

/** 달력 한 칸: 날짜와 일정(최대 3건, 나머지는 '더보기') */
function CalendarDayCell({ day, events, isToday }: CalendarDayCellProps) {
  const [expanded, setExpanded] = useState(false);
  const shown = expanded ? events : events.slice(0, VISIBLE);

  return (
    <div
      className={`min-h-28 border-l border-t border-gray-100 p-1.5 ${day.inMonth ? 'bg-white' : 'bg-gray-50'}`}
    >
      <div
        className={`mb-1 inline-flex h-6 w-6 items-center justify-center rounded-full text-xs ${
          isToday
            ? 'bg-primary font-bold text-white'
            : day.inMonth
              ? 'text-gray-700'
              : 'text-gray-300'
        }`}
      >
        {day.day}
      </div>
      <div className="space-y-0.5">
        {shown.map((event) => (
          <EventChip key={event.id} event={event} />
        ))}
      </div>
      {events.length > VISIBLE && (
        <button
          className="mt-0.5 text-xs text-gray-500 hover:text-primary"
          onClick={() => setExpanded(!expanded)}
        >
          {expanded ? '접기' : `+${events.length - VISIBLE}건 더보기`}
        </button>
      )}
    </div>
  );
}

export default CalendarDayCell;
