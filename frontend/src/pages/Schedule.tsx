import { useMemo, useState } from 'react';
import PageHeader from '@/components/common/PageHeader';
import CalendarGrid from '@/components/schedule/CalendarGrid';
import KindFilter from '@/components/schedule/KindFilter';
import MonthNav from '@/components/schedule/MonthNav';
import { useFetch } from '@/hooks/useFetch';
import { useRefreshOnEvents } from '@/hooks/useRefreshOnEvents';
import { getDispatches } from '@/services/dispatchService';
import { getInbounds } from '@/services/inboundService';
import { ScheduleKind } from '@/types/schedule';
import { addMonths, dateKey, groupByDate, monthGrid } from '@/utils/calendar';
import { dispatchEvents, inboundEvents } from '@/utils/scheduleEvents';

const ALL_KINDS: ScheduleKind[] = ['DISPATCH', 'INBOUND'];

/** 스케줄: 배차 일정과 입고 예정을 월간 달력으로 보여 준다. */
function SchedulePage() {
  const [cursor, setCursor] = useState(() => ({
    year: new Date().getFullYear(),
    month: new Date().getMonth(),
  }));
  const [kinds, setKinds] = useState<ScheduleKind[]>(ALL_KINDS);
  const dispatches = useFetch(getDispatches);
  const inbounds = useFetch(() => getInbounds());
  useRefreshOnEvents(() => {
    dispatches.reload();
    inbounds.reload();
  });

  const eventsByDate = useMemo(
    () =>
      groupByDate(
        [...dispatchEvents(dispatches.data ?? []), ...inboundEvents(inbounds.data ?? [])].filter(
          (e) => kinds.includes(e.kind),
        ),
      ),
    [dispatches.data, inbounds.data, kinds],
  );
  const move = (delta: number) => setCursor(addMonths(cursor.year, cursor.month, delta));
  const toggleKind = (kind: ScheduleKind) =>
    setKinds(kinds.includes(kind) ? kinds.filter((k) => k !== kind) : [...kinds, kind]);

  return (
    <>
      <PageHeader
        title="스케줄"
        description="배차 일정(출발~도착 시각)과 입고 예정일을 달력으로 확인합니다. 항목을 누르면 해당 화면으로 이동합니다."
      />
      <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
        <MonthNav
          label={`${cursor.year}년 ${cursor.month + 1}월`}
          onPrev={() => move(-1)}
          onNext={() => move(1)}
          onToday={() =>
            setCursor({ year: new Date().getFullYear(), month: new Date().getMonth() })
          }
        />
        <KindFilter selected={kinds} onToggle={toggleKind} />
      </div>
      <CalendarGrid
        weeks={monthGrid(cursor.year, cursor.month)}
        eventsByDate={eventsByDate}
        todayKey={dateKey(new Date())}
      />
    </>
  );
}

export default SchedulePage;
