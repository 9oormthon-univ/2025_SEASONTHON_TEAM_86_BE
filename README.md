# DERERE — Demand-Responsive Restaurant

수요응답형 팝업 레스토랑 플랫폼 — 주민이 정하고, 셰프가 찾아오는 동네 미식 솔루션

---

## 프로젝트 요약

지역별 음식 접근성 불균형을 해결하기 위해 주민 참여형 데이터 기반 팝업 레스토랑을 운영합니다.  
고객은 집 근처에서 다양한 음식 경험을 얻고, 점주는 검증된 수요에서 안전하게 시장을 테스트할 수 있습니다.

**주요 키워드**:  
- 타깃: 지역 주민  
- 타깃: 외식업 점주  
- MVP: 웹 기반 플랫폼  
- 운영: 순환 입점 (3~6개월)

---

## 팀원 소개

<table>
<tr>
<td align="center">
<a href="https://github.com/luxogus" target="_blank">
<img src="https://avatars.githubusercontent.com/luxogus" width="60px;" alt="김태현"/><br />
<b>김태현</b>
</a><br/>
팀장 / 기획
</td>
<td align="center">
<a href="" target="_blank">
<img src="./hansol.png" width="60px;" alt="유한솔"/><br />
<b>유한솔</b>
</a><br/>
디자인
</td>
<td align="center">
<a href="https://github.com/hee4040" target="_blank">
<img src="https://avatars.githubusercontent.com/hee4040" width="60px;" alt="최희우"/><br />
<b>최희우</b>
</a><br/>
프론트엔드
</td>
<td align="center">
<a href="https://github.com/ssarangkim" target="_blank">
<img src="https://avatars.githubusercontent.com/ssarangkim" width="60px;" alt="김사랑"/><br />
<b>김사랑</b>
</a><br/>
프론트엔드
</td>
<td align="center">
<a href="https://github.com/zzfbwoals" target="_blank">
<img src="https://avatars.githubusercontent.com/zzfbwoals" width="60px;" alt="류재민"/><br />
<b>류재민</b>
</a><br/>
백엔드
</td>
<td align="center">
<a href="https://github.com/duddns6290" target="_blank">
<img src="https://avatars.githubusercontent.com/duddns6290" width="60px;" alt="황영은"/><br />
<b>황영은</b>
</a><br/>
백엔드
</td>
</tr>
</table>

---

## 문제 정의

- **소비자:** 지역 선택지가 한정되어 반복적이고 단조로운 외식 경험  
- **점주:** 신규 진출 시 입지·수요 불확실성으로 인한 높은 리스크  
- **지역사회:** 상권 불균형으로 인한 지역 활성화 저하  

---

## 해결방안 — DERERE 시스템

1. **수요 분석** — 투표, 검색, 예약 데이터를 수집해 지역별 선호 메뉴를 도출  
2. **순환 입점** — 3~6개월 단위로 검증된 점주 입점  
3. **주민 참여** — 주민이 직접 메뉴/입점을 투표로 결정  
4. **데이터 기반 확장** — 축적된 데이터로 창업 인사이트 제공  

**핵심 차별점:** 주민 참여 + 데이터 기반 의사결정

---

## 효과 및 기대 성과

- **주민:** 지역 내에서 지속적인 신규 미식 경험 확보  
- **점주:** 리스크 감소, 데이터 기반의 창업 판단 근거 확보  
- **지역사회:** 상권 활성화 및 음식 다양성 증가  

---

## 비교: 푸드트럭 / 일반 음식점 / DERERE

| 항목 | 푸드트럭 | 일반 음식점 | DERERE |
| --- | --- | --- | --- |
| 수익성 | 이동비·운영 불안정 | 안정적 수익 | 수요 보장으로 안정적 수익 |
| 개점효과 | 짧은 기간만 효과 | 초기 효과 후 감소 | 주기적 개점효과 지속 |
| 안정성 | 낮음 | 중간~높음 | 데이터로 보장된 안정성 |
| 메뉴 | 공간 제약 | 모든 메뉴 가능 | 모든 메뉴 가능 |

---

## MVP — 핵심 기능 (1차 해커톤)

- 웹 기반 투표 페이지: 주민이 입점 원하는 메뉴/콘셉트 투표  
- 점주용 신청 폼: 입점 희망 점주 등록  
- 관리자 대시보드(간이): 수요 집계 및 입점 스케줄 관리  
- Notion으로 기획문서 연결 (MVP 링크 포함)  

---

## 시스템 아키텍처 (요약)

- Frontend: React + Vite  
- Backend: Spring Boot  
- DB: MySQL  
- Auth: OAuth2  
- Docs: Swagger

---

## 주요 백엔드 로직

### 1. 인증 및 보안

- **JWT 토큰 발급 및 검증**  
  `TokenProvider` 클래스에서 JWT 토큰을 생성하며, 사용자 인증 정보를 바탕으로 Access/Refresh 토큰을 발급합니다.  
  - `generateAccessToken(Authentication authentication)` : 인증 정보를 기반으로 AccessToken 생성  
  - `validateToken(token)` : 토큰 유효성 검사  
  - `getAuthentication(token)` : 토큰에서 사용자 인증 객체 추출  
  - 토큰에는 사용자 ID, 권한(role) 등이 포함됩니다.

- **OAuth2 인증**  
  Spring Security OAuth2를 통해 소셜 로그인 및 인증 정보를 획득합니다.

---

### 2. 음식점 관리

- **음식점 엔티티**  
  - 필드: 이름, 정보, 타입, 연락처, 위치, 운영시간, 이미지URL, 좋아요수 등
  - 편의 메소드: 음식점 정보 수정, 메뉴 추가/삭제, 좋아요 증감 등
  
- **CRUD 서비스**  
  - 전체 음식점 조회, 단일 음식점 상세 조회(메뉴 포함), 타입별/지역별/좋아요순/투표순 정렬 등 다양한 조회 API 제공
  - 음식점 정보 수정/삭제는 로그인한 사용자만 가능

- **좋아요/투표 기능**
  - 좋아요 및 투표수에 따라 음식점을 정렬하거나 TOP3 추출
  - 좋아요 수 및 투표수 증감 로직 내장

---

### 3. 메뉴 관리

- **RestaurantMenu 엔티티**  
  - 필드: 메뉴명, 가격, 설명, 이미지URL, 소속 음식점 등
  - 편의 메소드: 메뉴 추가/삭제 등

- **메뉴 CRUD 및 이미지 관리**
  - 메뉴 등록/수정/삭제 API 제공
  - 메뉴 이미지 S3 업로드 지원

---

### 4. 이미지 업로드/관리 (AWS S3)

- **S3 Presigned URL 생성 및 이미지 관리**  
  - 음식점 및 메뉴 이미지 업로드를 위해 Presigned URL 발급 (`S3Controller`)
  - 이미지 삭제 기능도 지원
  - 업로드 성공 시 이미지 URL 반환

---

### 5. 설문 및 투표 관리

- **설문/투표 API**
  - 음식점별 투표수 조회, 설문 응답 등록/수정/삭제, 설문 통계 조회 등
  - 사용자별/음식점별 투표 관리 및 통계 제공

---

## 주요 API 예시

- 음식점 전체 조회: `GET /api/restaurant`
- 음식점 상세 조회(메뉴 포함): `GET /api/restaurant/{id}`
- 음식점 좋아요순/투표순 정렬: `GET /api/restaurant/my-location/like/all`, `GET /api/restaurant/my-location/type/{type}`
- 음식점 정보 수정: `PUT /api/restaurant/{id}`
- 음식점 이미지 업로드: `POST /api/images/upload-url`
- 음식점 메뉴 관리: `POST /api/restaurant/{id}/menu`, `DELETE /api/restaurant/{id}/menu/{menuId}`
- 설문 투표 관리: `GET /api/surveys/restaurant/{restaurantId}/votes`

---

## 프로젝트 링크

- [Notion 기획서 (MVP)](https://www.notion.so/261fa9922bb1803ab10ef2b2d6fb02d4)  
- [GitHub FE 리포지토리](https://github.com/9oormthon-univ/2025_SEASONTHON_TEAM_86_FE)  
- [GitHub BE 리포지토리](https://github.com/9oormthon-univ/2025_SEASONTHON_TEAM_86_BE)

---

© 2025 DERERE Team 86
