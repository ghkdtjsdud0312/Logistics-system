import { Link } from 'react-router-dom';
import { SCHEDULE_KIND_ICON } from '@/constants/scheduleKind';
import { TONE_CLASS } from '@/constants/tone';
import { ScheduleEvent } from '@/types/schedule';

/** 달력 칸 안의 일정 한 줄. 누르면 관련 화면으로 이동한다. */
function EventChip({ event }: { event: ScheduleEvent }) {
  return (
    <Link
      to={event.to}
      title={`${event.title} · ${event.subtitle}`}
      className={`block truncate rounded px-1.5 py-0.5 text-xs hover:brightness-95 ${TONE_CLASS[event.tone]}`}
    >
      {SCHEDULE_KIND_ICON[event.kind]} {event.time && <b>{event.time} </b>}
      {event.title}
    </Link>
  );
}

export default EventChip;
