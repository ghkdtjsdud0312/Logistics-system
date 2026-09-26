import { Link } from 'react-router-dom';

/** "상세보기 >" 바로가기 */
function DetailLink({ to }: { to: string }) {
  return (
    <Link to={to} className="text-xs text-primary hover:underline">
      상세보기 &gt;
    </Link>
  );
}

export default DetailLink;
