# 👭 픽플 (Pickple)


## 📝 프로젝트 소개

픽플(Pickple) 은
의뢰인과 프리랜서를 프로젝트 단위로 연결하고,
요구사항 → 계약 → 진행 → 산출물 → 평가까지의 전 과정을
하나의 라이프사이클로 관리하는 프리랜서 매칭 플랫폼입니다.

기존 재능마켓/매칭 서비스는 다음과 같은 문제를 가지고 있었습니다.
- 요구사항이 정제되지 않아 초기 커뮤니케이션 비용이 큼
- 별점 위주의 평가로 프리랜서 신뢰도 판단이 어려움
- 계약 이후 진행 상황(마일스톤, 산출물)을 체계적으로 관리하기 어려움
- AI 매칭 및 상대방와의 소통에 대한 어려움의 부재로 인한 어려움

픽플은 “매칭 이후의 실패”까지 줄이는 것을 목표로 설계되었습니다.

### 🎯 핵심 가치
- 📌 자동 브리프 기반 프로젝트 등록
- 📊 해석 가능한 매칭 점수 & 추천 근거
- 🧩 마일스톤 기반 협업 관리
- 🔔 실시간 메시지·알림으로 즉각적인 커뮤니케이션
- 🧠 확장 가능한 AI 매칭/추천 구조

### [🎆픽플 FE Repository](https://github.com/prgrms-aibe-devcourse/AIBE3_Project2_Team1_FE)

### [🎇픽플 BE Repository](https://github.com/prgrms-aibe-devcourse/AIBE3_Project2_Team1_BE)

---

## 📌 팀원 구성

|<img src="https://avatars.githubusercontent.com/u/144124353?s=400&u=9bda70cb07b771d6301ac64df65acb931406b09e&v=4" width="125" />|<img src="https://avatars.githubusercontent.com/u/82808715?v=4" width="125" />|<img src="https://avatars.githubusercontent.com/u/217855127?v=4" width="125" />|<img src="https://avatars.githubusercontent.com/u/99888873?v=4" width="125" />|<img src="https://avatars.githubusercontent.com/u/218869457?v=4" width="125" />|<img src="https://avatars.githubusercontent.com/u/132271194?v=4" width="125" />|
|:---------:|:---------:|:---------:|:---------:|:---------:|:---------:|
|[유승인 (팀장)](https://github.com/seung-in-Yoo)|[김정호](https://github.com/Unoguna)|[김지윤](https://github.com/jiyoon-00)|[심수민](https://github.com/SWWWin)|[안지협](https://github.com/TooTo3)|[이해민](https://github.com/haemin4738)|
|FE, BE|FE, BE|FE, BE|FE, BE|FE, BE|FE, BE|

---

## 🔎 기술 스택

| Category             | Stack                                                                                                                                                                                                                                                                       |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Framework / Runtime  | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Java](https://img.shields.io/badge/Java%2021-007396?style=for-the-badge&logo=java&logoColor=white) |
| Programming Language | ![Java](https://img.shields.io/badge/Java%2021-007396?style=for-the-badge&logo=java&logoColor=white) |
| Database             | ![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) |
| Infrastructure       | ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white) ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white) ![AWS CloudWatch](https://img.shields.io/badge/AWS%20CloudWatch-FF4F8B?style=for-the-badge&logo=amazoncloudwatch&logoColor=white) ![AWS Route 53](https://img.shields.io/badge/AWS%20Route%2053-232F3E?style=for-the-badge&logo=amazonroute53&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) |
| CI/CD                | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) |
| Version Control      | ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white) |
| Authentication |![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white) ![OAuth2](https://img.shields.io/badge/OAuth2-3C3C3C?style=for-the-badge) ![Kakao](https://img.shields.io/badge/Kakao-FFCD00?style=for-the-badge&logo=kakao&logoColor=000000) ![Google](https://img.shields.io/badge/Google-4285F4?style=for-the-badge&logo=google&logoColor=white) ![Naver](https://img.shields.io/badge/Naver-03C75A?style=for-the-badge&logo=naver&logoColor=white)
| Token / Session | ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
| AI / Recommendation | ![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white) ![LLM](https://img.shields.io/badge/LLM--Based%20Matching-000000?style=for-the-badge)
| Real-time Communication | ![SSE](https://img.shields.io/badge/SSE-000000?style=for-the-badge)

---

## ✨ 핵심 기능
### 🧑‍🤝‍🧑 프로젝트 & 프리랜서 탐색
- 프로젝트/프리랜서 목록
- 카테고리/조건 기반 필터링
- 최신순 / 추천순 정렬
- 클라이언트 ↔ 프리랜서 역할 분리

### 📝 프로젝트 상세 & 제안/계약
- 프로젝트 상세
- 프로젝트 정보, 예산, 마감일
- 제안 → 수락 → 계약 상태 흐름 관리
- 상태 머신 기반 계약 로직

### 💬 실시간 채팅
- 실시간 메시지,알림 (SSE)
- 프로젝트 단위 메시지 스레드
- 읽음/안읽음 상태 관리
- 계약/마일스톤 이벤트와 연동

### 🧩 마일스톤 & 협업 관리
- 칸반 보드
- 마일스톤 생성/수정/완료
- 진행 상태 시각화
- 산출물 제출 & 승인 흐름

|<img width="1918" height="991" alt="픽플2" src="https://github.com/user-attachments/assets/3e0a5366-c92c-4f6a-982a-3482e3c6e4eb" />|
|:---------:|
|**홈 화면**|


|<img width="400" height="700" alt="픽플6" src="https://github.com/user-attachments/assets/6ef768f4-05ba-44cb-a540-1ea20ac92347" />|<img width="400" height="700" alt="픽플5" src="https://github.com/user-attachments/assets/af00db54-6ee6-41f3-a877-e95c53df48fb" />|
|:---------:|:---------:|
|**회원가입**|**로그인**|


|<img width="500" height="300" alt="픽플7" src="https://github.com/user-attachments/assets/089cbd3d-9e72-4b16-9cac-15451bb5fc9b" />|<img width="500" height="300" alt="픽플8" src="https://github.com/user-attachments/assets/f2328393-f059-431f-af8a-95553b81434c" />|
|:---------:|:---------:|
|**프리랜서 찾기**|**클라이언트 찾기**|


|<img width="500" height="300" alt="픽플7" src="https://github.com/user-attachments/assets/0c5240d8-21c1-4bc6-889f-3aa6687e2d7b" />|<img width="500" height="300" alt="픽플8" src="https://github.com/user-attachments/assets/8ccee4ab-6a42-45ec-affb-4e77422082a7">|
|:---------:|:---------:|
|**매칭 페이지**|**매칭 제안**|


|<img width="1918" height="991" alt="픽플2" src="https://github.com/user-attachments/assets/dd93f7e9-c147-44b7-906c-86dc39ff4172" />|
|:---------:|
|**AI 매칭 추천**|

|<img width="1918" height="991" alt="픽플2" src="https://github.com/user-attachments/assets/339c1f19-a268-4820-b877-9faf7442dd15" />|
|:---------:|
|**유저 간 채팅**|

|<img width="500" height="300" alt="픽플7" src="https://github.com/user-attachments/assets/85ec5212-7b7b-44ea-85ef-6f811619f21f" />|<img width="500" height="300" alt="픽플8" src="https://github.com/user-attachments/assets/602c9a6b-cdc1-45a5-b509-f5f4d0a4c32f">|
|:---------:|:---------:|
|**마일스톤 페이지(1)**|**마일스톤 페이지(2)**|


