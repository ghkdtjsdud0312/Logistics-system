import CalendarDayCell from '@/components/schedule/CalendarDayCell';
import { WEEKDAYS } from '@/constants/scheduleKind';
import { CalendarDay, ScheduleEvent } from '@/types/schedule';

interface CalendarGridProps {
  weeks: CalendarDay[][];
  eventsByDate: Record<string, ScheduleEvent[]>;
  todayKey: string;
}

/** 월간 달력 격자 (일~토) */
function CalendarGrid({ weeks, eventsByDate, todayKey }: CalendarGridProps) {
  return (
    <div className="overflow-hidden rounded-md border border-gray-200 bg-white">
      <div className="grid grid-cols-7 bg-gray-50 text-center text-xs font-medium text-gray-500">
        {WEEKDAYS.map((name, i) => (
          <div
            key={name}
            className={`py-2 ${i === 0 ? 'text-red-400' : i === 6 ? 'text-blue-400' : ''}`}
          >
            {name}
          </div>
        ))}
      </div>
      {weeks.map((week) => (
        <div key={week[0].key} className="grid grid-cols-7">
          {week.map((day) => (
            <CalendarDayCell
              key={day.key}
              day={day}
              events={eventsByDate[day.key] ?? []}
              isToday={day.key === todayKey}
            />
          ))}
        </div>
      ))}
    </div>
  );
}

export default CalendarGrid;
