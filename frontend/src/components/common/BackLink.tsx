import { Link } from 'react-router-dom';

/** 목록으로 돌아가는 링크 */
function BackLink({ to, label }: { to: string; label: string }) {
  return (
    <Link to={to} className="mb-3 inline-block text-sm text-gray-500 hover:text-primary">
      ← {label}
    </Link>
  );
}

export default BackLink;
