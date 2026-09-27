import { HoverInfo, ZoneSummary } from '@/types/warehouse3d';

/** 구역(섹션) 바닥에 마우스를 올렸을 때 보여 주는 합계 툴팁 */
function ZoneTooltip({ hover, summary }: { hover: HoverInfo; summary: ZoneSummary }) {
  return (
    <div
      className="pointer-events-none absolute z-10 rounded-md bg-gray-900/90 px-3 py-2 text-xs text-white shadow"
      style={{ left: hover.x + 14, top: hover.y + 14 }}
    >
      <div className="font-semibold">{summary.zoneCode} 구역</div>
      <div className="text-gray-300">위치 {summary.locationCount}칸</div>
      <div className="mt-1 text-gray-300">
        현재 {summary.onHand} · 예약 {summary.reserved} · 가용 {summary.available}
      </div>
      {summary.soldOutCount > 0 && (
        <div className="text-red-300">소진 {summary.soldOutCount}칸</div>
      )}
      {summary.emptyCount > 0 && <div className="text-gray-400">빈 칸 {summary.emptyCount}칸</div>}
    </div>
  );
}

export default ZoneTooltip;
