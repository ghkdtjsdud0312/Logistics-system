# logistics-system_front

물류 담당자 입장에서의 물류 입·출고 및 배차 최적화 시스템 - 프론트엔드

## 기술 스택
- Build Tool & Runtime: Vite, Yarn
- Framework & Language: React 18+, TypeScript
- Routing: React Router v6+
- Styling: Tailwind CSS
- UI Toast Notice: Sonner

## 시작하기
\`\`\`bash
yarn install
yarn dev
\`\`\`

## 폴더 구조
\`\`\`
src/
├── components/   # 재사용 가능한 UI 컴포넌트
├── config/       # 환경 변수 및 글로벌 설정 (Axios 인스턴스 등)
├── constants/    # 공통 상수 정의
├── context/      # React Context (전역 상태 관리)
├── hooks/        # 커스텀 훅 (useName 형태)
├── layout/       # 공통 레이아웃 (Header, Sidebar, Main Layout)
├── pages/        # 라우트별 페이지 컴포넌트
├── services/     # API 통신 로직 및 apiClient
├── types/        # TypeScript 타입/인터페이스 정의
└── utils/        # 공통 유틸리티 및 계산식 함수
\`\`\`
