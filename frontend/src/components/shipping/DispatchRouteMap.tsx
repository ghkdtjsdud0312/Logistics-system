import { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { RouteOrigin, RouteStop } from '@/types/dispatch';

interface DispatchRouteMapProps {
  origin: RouteOrigin | null;
  stops: RouteStop[];
}

const originIcon = L.divIcon({
  html: '★',
  className: 'text-lg text-primary',
  iconSize: [20, 20],
  iconAnchor: [10, 10],
});

/** 출발지·방문 순서를 보여 주는 지도. 선은 실제 도로가 아니라 직선이다. */
function DispatchRouteMap({ origin, stops }: DispatchRouteMapProps) {
  const elRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<L.Map | null>(null);

  useEffect(() => {
    if (!elRef.current) return;
    const map = L.map(elRef.current);
    mapRef.current = map;
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 18,
    }).addTo(map);
    return () => {
      map.remove();
    };
  }, []);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;
    const points: L.LatLngExpression[] = [];
    if (origin?.latitude !== null && origin?.latitude !== undefined && origin.longitude !== null) {
      const point: L.LatLngExpression = [origin.latitude, origin.longitude];
      points.push(point);
      L.marker(point, { icon: originIcon }).addTo(map).bindPopup(`출발지: ${origin.name}`);
    }
    stops.forEach((stop, i) => {
      if (stop.latitude === null || stop.longitude === null) return;
      const point: L.LatLngExpression = [stop.latitude, stop.longitude];
      points.push(point);
      L.marker(point)
        .addTo(map)
        .bindPopup(`방문 ${i + 1}: ${stop.orderNo}<br />${stop.address}`);
    });
    if (points.length > 0) {
      L.polyline(points, { color: '#2563eb', dashArray: '6 4' }).addTo(map);
      map.fitBounds(L.latLngBounds(points).pad(0.2));
    } else {
      map.setView([36.5, 127.8], 7);
    }
    return () => {
      map.eachLayer((layer) => {
        if (!(layer instanceof L.TileLayer)) map.removeLayer(layer);
      });
    };
  }, [origin, stops]);

  return <div ref={elRef} className="h-64 w-full rounded-md border border-gray-200" />;
}

export default DispatchRouteMap;
