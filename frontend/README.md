# logistics-system_front

물류 통합 관리 시스템 - 프론트엔드 (로그인 없음)

## 기술 스택
- Build Tool & Runtime: Vite, Yarn
- Framework & Language: React 18+, TypeScript
- Routing: React Router v6+
- Styling: Tailwind CSS
- UI Toast Notice: Sonner

## 시작하기
```bash
yarn install
yarn dev
```

## 폴더 구조
```
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
```

## 화면 구조 (라우트)
```
대시보드
주문관리   : 주문 목록 / 주문 상세
창고관리   : 재고 현황 / 입고·적치 / 피킹·포장(탭)
배송관리   : 상차관리 / 배차관리 / 배송현황 / 배송완료·실패
반품관리
감사로그
기준정보   : 상품관리 / 창고·위치관리 / 차량·기사관리
```
상세는 [../.ai/REQUIREMENTS.md](../.ai/REQUIREMENTS.md), API는 [../.ai/API_SPEC.md](../.ai/API_SPEC.md)를 참고하세요.

## 규칙
- 타입은 `src/types/`, 상태 한글 매핑·목 데이터는 `src/constants/`, API 호출은 `src/services/`, 순수 계산은 `src/utils/`로 분리합니다.
- 대시보드는 SSE(`/api/events/logistics`)의 `status-changed` 수신 시 요약을 재조회합니다.
- 파일 하나는 약 30~50줄을 넘기지 않고 컴포넌트·훅으로 분리합니다.
- 3D 창고는 Three.js로 구현되어 있다(창고·위치관리 > 3D 보기). 나중에 추가: 달력, 지도.
