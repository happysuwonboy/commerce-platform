# commerce-platform — PRD (Product Requirements Document)

> 범용 쇼핑몰 백엔드 (타임딜/한정수량 포함) · 학습용 Spring Boot 프로젝트
> 상태: living document (단계 진행하며 갱신)
> 최종 수정: 2026-05-28

---

## 1. 배경 & 목적

- **목적:** Java/Spring Boot 백엔드 역량을 *깊게* 쌓아 빅테크 백엔드로 이직하기 위한 학습용 프로젝트.
- **주제 선택 이유:** 이커머스(특히 타임딜/한정수량)는 **동시성·대용량 트래픽·캐싱·비동기·분산 트랜잭션** 등 빅테크 백엔드 난제를 자연스럽게 강제한다. 도메인이 단순/직관적이라 백엔드 메커니즘 자체에 집중할 수 있다.
- **이 문서의 성격:** 일반 제품 PRD와 달리, **비기능 요구사항(정량 목표)과 단계별 학습 로드맵**에 무게를 둔다. 기능은 "학습 난제를 끌어내기 위한 그릇"이다.

---

## 2. 목표 (Goals) & 비목표 (Non-Goals)

### Goals
- 모듈러 모놀리스로 시작해 **통증 기반으로 점진적 MSA 추출**까지 경험.
- **동시성 정합성**(오버셀링 0) 을 여러 락 전략으로 직접 구현/비교.
- **정량적 성능 개선 서사** 확보 (부하테스트 → 병목 발견 → 개선 → before/after 수치).
- 비동기/이벤트(Kafka), 캐싱(Redis), 관측성(Prometheus/Grafana) 실전 적용.
- **Docker → Kubernetes** 배포까지.

### Non-Goals
- 프론트엔드 UI (REST API + Swagger 문서로 충분; 화면은 만들지 않음).
- 실제 PG 결제 연동 (mock 결제로 대체).
- 실제 배송/물류/정산 정확성 (학습에 필요한 만큼만 모사).
- 완벽한 비즈니스 규칙 (엣지 케이스보다 백엔드 메커니즘 학습 우선).

---

## 3. 사용자 (Personas)

| 역할 | 설명 | 주요 행위 |
|---|---|---|
| **구매자 (USER)** | 일반 회원 | 회원가입/로그인, 상품 탐색, 주문/결제, 타임딜 구매 |
| **관리자 (ADMIN)** | 운영자 (최소 기능) | 상품/카테고리/타임딜 등록·수정 |

> 별도 판매자(Seller) 역할은 범위 밖. 관리자가 상품을 등록하는 단일 입점 구조로 단순화.

---

## 4. 핵심 사용자 시나리오

1. **일반 구매:** 회원가입 → 로그인(JWT) → 상품 목록/상세 탐색 → 주문 생성(재고 차감) → mock 결제 → 주문 조회.
2. **타임딜 선착순 (핵심 동시성):** 타임딜 오픈 시각에 한정 수량(예: 100개)에 수천~수만 동시 요청 → **정확히 100개만 판매(오버셀링 0)**, 1인 1구매 제한, 나머지는 매진 응답.

---

## 5. 기능 요구사항 (도메인별)

### 5.1 회원/인증 (user)
- FR-U1 이메일+비밀번호 회원가입 (비밀번호 **BCrypt 해싱**).
- FR-U2 로그인 시 **JWT access + refresh 토큰** 발급.
- FR-U3 access 토큰 만료 시 refresh 토큰으로 재발급.
- FR-U4 로그아웃. **refresh 토큰은 Redis에 저장**(TTL 기반 만료, 무효화/재발급/블랙리스트 관리).
- FR-U5 역할 기반 권한 **RBAC** (USER / ADMIN), 메서드 시큐리티.
- FR-U6 **관리자 별도 인증:** 일반 사용자와 분리된 관리자 로그인 플로우(별도 엔드포인트 `/api/admin/auth/**`, ADMIN 전용 토큰 audience).
- FR-U7 **MFA (TOTP):** 관리자 로그인 시 2단계 인증. TOTP(구글 OTP/Authy 호환) 등록 플로우, **시크릿 암호화 저장(at-rest 암호화)**, 일회용 **복구 코드** 발급/검증, 시계 오차 허용 윈도우 처리.
- FR-U8 *(심화 단계)* OAuth2 소셜 로그인(구글/카카오) → 외부 신원을 로컬 User에 매핑 → 앱 자체 JWT 발급.

### 5.2 상품/카테고리 (product)
- FR-P1 카테고리 관리 (계층형 허용), 상품 등록/수정/삭제 (ADMIN).
- FR-P2 상품 목록 조회: **커서(no-offset) 페이지네이션**, 카테고리 필터, 정렬(가격/최신).
- FR-P3 상품 상세 조회.
- FR-P4 *(심화)* 키워드 검색 (초기엔 DB LIKE → 이후 Elasticsearch 도입 검토).

### 5.3 재고 (inventory)
- FR-I1 상품별 재고 수량 관리.
- FR-I2 주문 시 재고 차감, 주문 취소 시 복원.
- FR-I3 **동시성 안전성** 보장 (Phase 1에서 락 전략 적용; 음수 재고 불가).

### 5.4 주문 (order)
- FR-O1 단건/다건 주문 생성: 재고 차감 + 주문/주문항목 생성 + 결제 요청을 **하나의 일관된 트랜잭션 흐름**으로.
- FR-O2 내 주문 목록/상세 조회.
- FR-O3 주문 취소 (재고 복원, 결제 취소).
- FR-O4 *(심화)* **멱등성** — 중복 주문 방지(따닥 클릭/네트워크 재시도, 멱등키).
- FR-O5 **장바구니 (MVP 포함):** 담기 / 목록 조회 / 수량 변경 / 삭제, 장바구니 → 주문 전환.

### 5.5 결제 (payment, mock)
- FR-PAY1 가짜 결제 처리 (성공/실패/지연 시뮬레이션).
- FR-PAY2 *(심화)* 멱등키 기반 중복 결제 방지.
- FR-PAY3 주문 상태와 결제 상태 동기화 (실패 시 보상 처리).

### 5.6 타임딜/한정수량 (timedeal) — 핵심
> **도입 시점: Phase 1 (동시성).** Phase 0 MVP는 일반 주문/장바구니까지만, 타임딜 선착순은 동시성 학습과 함께 도입.
- FR-T1 타임딜 등록: 대상 상품, 시작/종료 시각, 한정 수량, 할인가, **1인 구매 제한**.
- FR-T2 오픈 시각 전 구매 차단, 종료 후 차단.
- FR-T3 선착순 구매: **정확히 한정 수량만 판매(오버셀링 절대 불가)**.
- FR-T4 매진 시 명확한 응답, 1인 중복 구매 차단.

---

## 6. 도메인 모델 (개략)

```
User (1) ──< Order (1) ──< OrderItem >── (N) Product
                  │                          │
                  └── Payment (1:1)          ├── Inventory (1:1)
                                             └── Category (N:1)
TimeDeal (N:1 Product)
```

핵심 엔티티: `User`, `Category`, `Product`, `Inventory`, `Order`, `OrderItem`, `Payment`, `TimeDeal`.
> 모듈 간에는 엔티티 직접 참조 대신 **서비스 인터페이스/식별자(ID)로 결합**해, 추후 MSA 추출 시 경계를 깨끗이 유지.

---

## 7. API 개요 (핵심 엔드포인트)

| 메서드 | 경로 | 설명 | 권한 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | 공개 |
| POST | `/api/auth/login` | 로그인(JWT 발급) | 공개 |
| POST | `/api/auth/reissue` | 토큰 재발급 | 공개(refresh) |
| GET | `/api/products` | 상품 목록(커서 페이지네이션) | 공개 |
| GET | `/api/products/{id}` | 상품 상세 | 공개 |
| POST | `/api/admin/products` | 상품 등록 | ADMIN |
| POST | `/api/orders` | 주문 생성 | USER |
| GET | `/api/orders` | 내 주문 목록 | USER |
| POST | `/api/orders/{id}/cancel` | 주문 취소 | USER |
| GET | `/api/timedeals` | 진행/예정 타임딜 | 공개 |
| POST | `/api/timedeals/{id}/purchase` | 타임딜 선착순 구매 | USER |

> 상세 스펙은 springdoc(Swagger)로 코드와 함께 관리.

---

## 8. 비기능 요구사항 / 학습 목표 (NFR) — 이 프로젝트의 핵심

| 영역 | 요구사항 / 정량 목표 | 검증 방법 |
|---|---|---|
| **정합성** | 타임딜 한정 N개에 동시요청 ≫N 이 와도 **판매 수량 == N (오버셀링 0)**, 음수 재고 불가, 1인 제한 정확 | 동시성 테스트(JUnit + 스레드풀/CompletableFuture) |
| **성능(읽기)** | 상품 **1000만 건** 적재 상태에서 목록/상세 조회 p95 목표치 설정·달성 (인덱스/커서 페이지네이션) | k6 부하테스트, EXPLAIN 분석 |
| **성능(쓰기/동시성)** | 타임딜 구매 처리량(TPS)을 락 전략별로 측정·개선, before/after 기록 | k6 + Grafana |
| **회복탄력성** | 외부 호출(결제) 타임아웃/재시도/서킷브레이커(Resilience4j) | 장애 주입 테스트 |
| **관측성** | 메트릭(Actuator+Micrometer→Prometheus), 대시보드(Grafana), 분산 추적 | 대시보드/추적 확인 |
| **테스트** | 통합테스트(Testcontainers: MySQL/Redis/Kafka), 동시성 테스트, 부하테스트 | CI에서 실행 |
| **보안** | JWT 인증, RBAC, 비밀번호 해싱, 입력 검증(Validation), **관리자 MFA(TOTP) + 시크릿 암호화 저장** | — |

> **면접 무기 산출물:** 락 3종(비관/낙관/Redis 분산락) 처리량·정합성 비교표 + 부하테스트 before/after 그래프.

---

## 9. 단계별 로드맵 (Milestones)

| Phase | 목표 | 핵심 기술 |
|---|---|---|
| **0. MVP** | 돌아가는 쇼핑몰 뼈대 | Spring Boot, JPA, MySQL, Security/JWT, Flyway, Docker Compose |
| **1. 동시성** | 타임딜 오버셀링 0 | 비관/낙관/Redis(Redisson) 락 비교, 동시성 테스트, 멱등성 |
| **2. 성능** | 대용량 조회/처리 최적화 | DataFaker 대량 적재, 인덱스/쿼리 최적화, Redis 캐싱, k6 부하테스트 |
| **3. 비동기/이벤트** | 부가 처리 분리 | Kafka(주문 후 알림·집계·랭킹), eventual consistency |
| **4. 관측성** | 운영 가시성 | Actuator, Micrometer, Prometheus, Grafana, 분산 추적 |
| **5. 배포** | 컨테이너 운영 | Dockerfile, docker-compose → Kubernetes(매니페스트, HPA, 헬스체크) |
| **6. MSA 추출** | 트리거 기반 분리 | 모듈 → 서비스 추출, Saga/보상 트랜잭션, 서비스 간 통신 |

> Phase는 순차 진행하되, 각 Phase 종료 시 "무엇이 아팠고 다음 Phase가 그걸 어떻게 해결하는지" 한 줄 회고를 남긴다 (= 면접 서사 재료).

---

## 10. 기술 스택

- **언어/런타임:** Java 21 (LTS)
- **빌드:** Gradle (Kotlin DSL)
- **프레임워크:** Spring Boot 3.x (Web, Data JPA, Security, Validation)
- **DB:** MySQL 8 / 마이그레이션: Flyway
- **캐시·락:** Redis (+ Redisson) — Phase 1~2
- **메시징:** Kafka — Phase 3
- **테스트:** JUnit 5, Testcontainers, k6(부하)
- **문서:** springdoc-openapi (Swagger)
- **관측성:** Actuator, Micrometer, Prometheus, Grafana — Phase 4
- **인프라:** Docker, docker-compose → Kubernetes — Phase 5
- **보조:** Lombok

---

## 11. 범위 밖 (Out of Scope) / 향후 검토

- 실제 PG 결제, 실제 배송/물류, 쿠폰/프로모션 엔진 전반.
- 추천 시스템, 리뷰/평점.
- 검색은 초기 DB 기반 → 트래픽/요구에 따라 Elasticsearch 도입 검토(Phase 2~3).

---

## 12. 결정 사항 (Resolved)

- [x] **장바구니 → MVP 포함** (FR-O5).
- [x] **관리자 → 별도 인증** (`/api/admin/auth/**`) **+ MFA(TOTP)** 적용 (FR-U6, FR-U7).
- [x] **refresh 토큰 저장소 → Redis** (FR-U4).
- [x] **타임딜 → Phase 1(동시성)에서 도입.**

---

## 13. 개발 워크플로우 & 포트폴리오 이력 관리

> 취업/이직용 프로젝트이므로, 코드만큼이나 **"어떻게 일했는지 보이는 흔적"**이 평가된다.

- **Public GitHub 저장소**를 Day 1부터 운영.
- **Conventional Commits** 규칙 사용: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`, `perf:`. 논리적 단위로 커밋(거대한 한 방 커밋 금지).
- **기능/Phase별 브랜치 + PR**: 1인 개발이라도 PR을 열고 **본인이 셀프 리뷰** → PR 설명에 "무엇을/왜/트레이드오프"를 적는다. (실무 협업 흐름 + 의사결정 기록)
- **ADR(Architecture Decision Record)**: 락 전략 선택, MSA 추출 시점 등 주요 결정은 `docs/adr/`에 짧게 남긴다.
- **README가 곧 포트폴리오**: 프로젝트 목적, 아키텍처 다이어그램, **성능 개선 before/after 그래프**, 락 3종 비교표, 실행 방법(docker-compose) 정리.
- **품질 > 빈도**: 잔디(커밋 그래프)를 위한 가짜 커밋은 금물. 의미 있는 커밋 메시지와 PR이 실제로 검토 대상이다.
