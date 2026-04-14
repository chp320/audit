# Getting Started

## 프로그램 설명
* Gitlab에 생성된 계정이 이미 만료(퇴사, 철수 등)되었지만 목록에 남아있어 불필요 계정은 정리 필요
* Jenkins에서 계정 정리 job을 주기적으로 수행하고, 어드민페이지에서 조회

## 시스템 구조
* client: Jenkins 계정 정리 Job
* Server: Audit 어드민 (SpringBoot + embeded tomcat + SpringSecurity)
* 화면: Thymeleaf
* DB: H2

### Jenkins 계정 정리 JOB
* 배경
  - Gitlab은 LDAP에 연동되어 있고, 로그인 시 LDAP에서 계정 정보를 가져와 Gitlab 사용자 목록에 등록하여 관리함
  - 등록된 계정은 로그인 시마다 '마지막 접속일자'를 update하며 sysdate 기준으로 30일 초과하는 계정에 대해 정리함
  - LDAP에 계정이 존재하면 재직중인 사용자이고 단순 미접속 -> 계정 비활성화
  - LDAP에 계정이 미존재하면 퇴사, 철수 등의 사유로 불필요 계정 -> 계정 삭제 (Gitlab 사용자목록에서 삭제)
* 작업 상세
  - 일단위 작업 실행하여 30일이상 미접속 계정 목록 추출 -> 추출 목록은 H2에 저장
    ( id, user, username, last_activity_on 정보 저장 )
  - 추출된 계정으로 LDAP 조회 (존재: 계정 비활성화, 미존재: 계정 삭제)
    ( LDAP 조회 결과, 삭제 대상여부, 삭제 실행여부 정보 H2에 저장 )
  - 작업 완료 후 요약 리포트 생성
    ( todo: 완료 리포트 정보 H2에 저장 )
  ```
  	- 전체 사용자 수: OOO
  	- 30일 미사용 계정 수: OOO
  	- 비활성 처리 계정 수: OOO
  	- 삭제 계정 수: OOO
  	- 작업 실패 계정 수: OOO
  ```
* 결론
  - Jenkins는 REST API를 호출해서 데이터 저장, SpringBoot는 H2 저장, 어드민 페이지 제공

### Audit 어드민
* 배경
  - SpringBoot 기반으로 Gitlab 불필요 계정 정리 작업에 대한 결과 저장 및 조회 기능 제공
* 작업 상세
  - 어드민 화면 접근 시 id/pw 통한 로그인
  
## 개발 환경
* Project: Gradle
* Language: Java 17 (with SpringBoot)
* Dependency
  - Spring Web (REST API, MVC처리 - 백엔드)
  - Thymeleaf (HTML 렌더링 - 프론트엔드)
  - Spring Data JPA (H2 ORM처리)
  - H2 Database

  
## 빌드 및 실행 방법
### 빌드
* 외부 라이브러리 수집 (의존성 추가 시): Gradle Tasks > audit > custom build > copyDependencies
* 컴파일 및 .jar 파일 생성 (소스 수정 시): Gradle Tasks > audit > build > build (or bootJar)
* 결과물 경로: build/libs/audit-0.0.1-SNAPSHOT.jar 확인
  - 실제 회사에서 빌드 시에는 build.gradle 에서 repositories() 의 flatDir 활성화 후 빌드 실행!!

### 실행
* java -jar audit-0.0.1-SNAPSHOT.jar

##### 참고
* 사내 폐쇄망에서는 원격 저장소 접근이 불가하므로 외부 인터넷이 되는 환경에서 수집한 라이브러리를 프로젝트 하위 libs 폴더에 저장함
* 반드시 옮겨야할 필수 디렉토리
  - 소스 및 라이브러리: C:\study\eclipse-workspace
  - 개발 도구: C:\Users\user\study\STS\sts-5.0.1.RELEASE
  - 빌드 엔진 및 플러그인: C:\Users\user\.gradle
* 사내 복사 후 빌드 전 필수 실행 항목
  - 상기 3개 폴더를 위치 (C:\Users\실제디렉토리명)
  - build.gradle 내 mavenCentral() 주석 처리 및 하위 flatDir() 주석 해제
* 사내 개발PC에 .gradle 디렉토리가 존재하는 경우 아래 2가지 방법 중 한 가지 진행
  (방법1) 기존 디렉토리에 병합
    1) 사내 PC의 C:\Users\사내계정명\.gradle 폴더는 그대로 유지
    2) USB에 가져간 .gradle 폴더 내부에서 아래 2개 폴더 내용만 복사
       - caches/modules-2 (Spring Boot 빌드 플러그인 캐시)
       - wrapper/dists (Gradle 구동 엔진 바이너리)
   3) 사내 PC의 .gradle 폴더 하위 동일 경로에 붙여넣기 실행
      (주의) 윈도우 파일 복사 창이 떴을 때 "대상 폴더의 파일 덮어쓰기" 대신!!! "건너뛰기(Skip)" 또는 "파일 병합"을 선택 (기존 파일 유지 목적)
  (방법2) [권장] 독립된 Gradle User Home 격리 지정
    1) USB에 가져간 .gradle 폴더를 사내 PC의 임의의 독립된 경로(예: C:\study\offline-gradle\.gradle)에 복사
    2) 터미널(CLI)에서 빌드할 때, 기존 시스템 경로 대신 방금 복사한 폴더를 빌드 엔진 집(Home)으로 사용하도록 명령어를 명시적으로 강제
       - 명령어: gradlew --gradle-user-home C:\study\offline-gradle\.gradle build
       - 기존 사내 환경과 완벽히 차단된 상태에서 USB로 가져온 오프라인 플러그인과 엔진만을 사용하여 안전하게 빌드 수행 가능!!! 


### 개선 사항
* 어드민 페이지 보안 취약점 개선
  - 지금은 정적 리소스(html)로 만들어서 인증/인가, ACL 등 적용 불가함
  - 계정 관리 기능, 비밀번호 암호화(단방향), 세션 통제, 감사(audit trail) 로그, 대시보드 기능 등 구현 필요

* 보안정책 기준?
  - 비밀번호 복잡도 (최소 길이 8자리, 특수문자 포함, 대소문자 사용..?)
  - 세션 타임아웃 (미사용 시 자동 로그아웃.. 30분?)
  - 인증 주체 (LDAP? 개별 관리?)
  
