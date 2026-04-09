# BBOSONG Server Feature Expansion Plan

## 1. Goal

현재 서버는 회원가입/로그인, 세탁 이미지 분석, AI 채팅, 주변 장소 검색의 기본 골격은 갖추고 있다.
다만 실제 서비스로 이어지려면 사용자 기록 보존, 계정 관리 확장, 검색 결과 구조화, 운영 안정성 보강이 필요하다.

이 문서는 다음 구현 작업의 기준 문서로 사용한다.

## 2. Current State Summary

### Implemented

- JWT 기반 로그인
- 마이페이지 조회/수정
- 이미지 기반 세탁 분석
- 대화형 AI 채팅
- 카카오 장소 검색 연동

### Gaps

- 세탁 분석 결과가 저장되지 않음
- 채팅 기록이 메모리에만 저장되고 조회 API가 없음
- 비밀번호 변경/회원 탈퇴 기능 없음
- 장소 검색 응답이 문자열 원본 그대로 반환됨
- 예외 처리와 테스트 범위가 얕음

## 3. Product Direction

핵심 방향은 "한 번 쓰고 끝나는 AI API"에서 "기록과 계정이 남는 세탁 도우미 서비스"로 확장하는 것이다.

우선순위는 아래 기준으로 정한다.

- 사용자 체감 가치가 큰가
- 현재 코드 구조에 자연스럽게 붙는가
- 프론트와 바로 연결 가능한가
- 운영 리스크를 줄이는가

## 4. Expansion Priorities

### Phase 1. Analysis History

가장 먼저 추가할 기능은 세탁 분석 결과 저장과 조회다.

#### Why first

- 지금 가장 아쉬운 지점은 AI 결과가 일회성이라는 점이다.
- 저장 기능이 생기면 "최근 분석", "다시 보기", "즐겨찾기", "마이페이지 연동"까지 확장 가능하다.
- 현재 회원 도메인과 자연스럽게 연결된다.

#### Scope

- 분석 결과 저장 엔티티 추가
- 로그인 사용자 기준 분석 이력 저장
- 최근 분석 목록 조회 API
- 단건 상세 조회 API
- 선택적으로 즐겨찾기 여부 필드 추가

#### API draft

- `POST /api/analyze/image`
  - 기존 분석 수행 후 결과 저장
- `GET /api/analyze/history`
  - 내 분석 이력 목록 조회
- `GET /api/analyze/history/{id}`
  - 분석 상세 조회
- `PATCH /api/analyze/history/{id}/favorite`
  - 즐겨찾기 토글

#### Data draft

- `LaundryAnalysis`
  - `id`
  - `member`
  - `imageUrl` or `originalFilename`
  - `material`
  - `color`
  - `washingMethod`
  - `cautions`
  - `recommendedSymbols`
  - `favorite`
  - `createdAt`

### Phase 2. Chat Persistence

채팅 기능은 현재 메모리 저장소 기반이라 서버 재시작 시 기록이 사라진다.

#### Scope

- 대화방 엔티티 추가
- 메시지 엔티티 추가
- 대화 시작/메시지 전송 시 DB 저장
- 대화 목록 조회 API
- 대화 상세 조회 API

#### API draft

- `POST /api/chat/start`
- `POST /api/chat/send`
- `GET /api/chat/conversations`
- `GET /api/chat/conversations/{conversationId}`

#### Note

현재 `ConversationStore`는 개발용 캐시 용도로 남기거나 제거 대상이다.
실서비스 기준으로는 DB 저장으로 전환하는 것이 맞다.

### Phase 3. Member Feature Completion

회원 도메인은 기본 인증까지만 있고 계정 관리 기능이 부족하다.

#### Scope

- 비밀번호 변경
- 회원 탈퇴
- 아이디 중복 확인
- 이메일 중복 확인
- 내 정보 재조회 응답 정리

#### API draft

- `POST /member/check-login-id`
- `POST /member/check-email`
- `PATCH /member/password`
- `DELETE /member/me`

### Phase 4. Place Search Improvement

현재 장소 검색은 카카오 원본 문자열을 그대로 반환하고 있어 백엔드의 역할이 약하다.

#### Scope

- 장소 응답 DTO 정의
- 필요한 필드만 가공해서 반환
- 거리순 정렬 유지
- 프론트에서 바로 쓸 수 있는 응답 구조 제공
- 추후 카테고리 필터 확장

#### API draft

- `GET /api/places/search?query=&x=&y=&radius=`

#### Response draft

- `name`
- `address`
- `roadAddress`
- `distance`
- `phone`
- `placeUrl`
- `x`
- `y`

### Phase 5. Stabilization

기능 확장과 함께 반드시 같이 가야 하는 보강 영역이다.

#### Scope

- 커스텀 예외 체계 정리
- 인증/인가 정책 정리
- 공개 API 최소화
- 서비스/컨트롤러 테스트 추가
- 환경변수 문서화

## 5. Technical Improvement Items

### Security

- `/member/all` 공개 여부 재검토
- JWT 예외 응답 명확화
- 민감 정보 환경변수 전환 유지

### Validation

- 회원가입 DTO에 이메일 형식 검증 추가
- 비밀번호 정책 추가
- 마이페이지 수정 시 이메일 중복 검증 추가

### Persistence

- 채팅/분석 기록 엔티티에 `createdAt`, `updatedAt` 공통 적용 고려
- 텍스트 길이 제한 및 인덱스 고려

### Error Handling

- `RuntimeException` 직접 사용 축소
- 도메인별 `CustomException` 사용
- 외부 API 실패 시 사용자 메시지 정리

### Testing

- 회원가입/로그인 테스트
- 분석 결과 저장 테스트
- 채팅 저장/조회 테스트
- 장소 검색 서비스 단위 테스트

## 6. Recommended Implementation Order

1. 세탁 분석 결과 저장
2. 분석 이력 조회 API
3. 채팅 DB 저장 및 조회 API
4. 비밀번호 변경 / 회원 탈퇴
5. 장소 검색 DTO화
6. 예외 체계 정리
7. 테스트 추가

## 7. Definition of Done

아래 조건을 만족하면 1차 확장 완료로 본다.

- 로그인 사용자의 세탁 분석 결과가 DB에 저장된다
- 사용자는 자신의 최근 분석 목록과 상세를 조회할 수 있다
- 채팅 기록이 재시작 후에도 유지된다
- 주요 계정 관리 기능이 완성된다
- 외부 API 응답이 프론트 친화적인 DTO로 정리된다
- 주요 핵심 흐름에 대한 테스트가 추가된다

## 8. Immediate Next Task

첫 구현 작업은 `세탁 분석 결과 저장 + 이력 조회 API`로 진행한다.

이 작업이 적합한 이유는 다음과 같다.

- 가장 빠르게 사용자 가치가 올라간다
- 현재 회원/AI 분석 구조에 자연스럽게 연결된다
- 이후 마이페이지, 즐겨찾기, 추천 기능의 기반이 된다
