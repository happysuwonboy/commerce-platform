# commerce-platform

> 타임딜/한정수량을 포함한 범용 쇼핑몰 백엔드 — Spring Boot 학습 프로젝트
> 목적: 고동시성·대용량·캐싱·비동기·관측성 등 백엔드 난제를 직접 구현/측정하며 학습.

자세한 요구사항·로드맵은 [docs/PRD.md](docs/PRD.md) 참고.

## 기술 스택
- Java 21, Spring Boot 3.5.x, Gradle (Kotlin DSL)
- Spring Web / Data JPA / Security / Validation
- MySQL 8 (Flyway 마이그레이션), Redis
- springdoc-openapi (Swagger UI)
- 테스트: JUnit 5, Testcontainers, k6(부하)
- 이후 단계: Redisson, Kafka, Prometheus/Grafana, Docker, Kubernetes

## 아키텍처
- **모듈러 모놀리스**(단일 모듈, 패키지로 도메인 경계 분리) → 통증 기반 점진적 MSA 추출.
- 도메인 패키지: `user`, `product`, `inventory`, `order`, `payment`, `timedeal`, `common`
- 모듈 간에는 엔티티 직접 참조 대신 서비스 인터페이스/ID로 결합.

## 로컬 실행
```bash
# 1) 인프라 기동 (MySQL, Redis)
docker compose up -d

# 2) 애플리케이션 실행
./gradlew bootRun

# 3) API 문서
open http://localhost:8080/swagger-ui.html
```

### 테스트
```bash
./gradlew test   # 통합 테스트는 Testcontainers 사용 → Docker 데몬 필요
```

## 단계별 진행 (Milestones)
| Phase | 목표 |
|---|---|
| 0 | MVP: 쇼핑몰 뼈대 (상품/주문/장바구니, 인증) |
| 1 | 동시성: 타임딜 오버셀링 0 (락 전략 3종 비교) |
| 2 | 성능: 대용량 조회/캐싱, 부하테스트 |
| 3 | 비동기/이벤트 (Kafka) |
| 4 | 관측성 (Actuator/Prometheus/Grafana) |
| 5 | 배포 (Docker → Kubernetes) |
| 6 | MSA 추출 |

## 학습 산출물 (작성 예정)
- [ ] 락 3종(비관/낙관/Redis 분산락) 처리량·정합성 비교표
- [ ] 부하테스트 before/after 그래프
- [ ] 아키텍처 다이어그램
- [ ] 주요 의사결정 기록: [docs/adr/](docs/adr/)
