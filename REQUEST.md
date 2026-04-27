# GitLab Audit Admin 개선

## 1. 프로젝트 개요
Jenkins 잡에서 실행, 관리 예정인 깃랩 불필요 계정을 확인하고 비활성, 삭제 등의 작업을 수행한다.
각 작업 실행 결과를 확인 가능한 Admin 페이지를 만들고, 어드민 페이지에 계정 관리 등의 작업을 수행 가능하도록 기능 개선한다.

## 2. 보안 및 통합 요구사항 정의서
어드민 페이지 관리 및 역할 기반 접근 제어(RBAC) 구현을 위한 상세 요구사항은 다음과 같음.

| 대분류 | 요구사항 ID | 기능명 | 상세 요구사항 | 접근 권한 |
| :--- | :--- | :--- | :--- | :--- |
| **인증/인가** | SEC-001 | 폼 기반 로그인/로그아웃 | 사전에 부여된 ID/Password를 통한 폼(Form) 기반 인증 및 세션 파기. | 비로그인/공통 |
| **인증/인가** | SEC-002 | 비밀번호 암호화 저장 | 계정 생성 및 비밀번호 변경 시 DB에 평문 저장을 금지하고 BCrypt 등 단방향 해시 알고리즘 의무 적용. | 시스템 |
| **인증/인가** | SEC-003 | 세션 및 동시성 제어 | 동일 계정의 중복 로그인 차단(또는 기존 세션 만료) 및 일정 시간 활동 부재 시 자동 로그아웃 처리. | 시스템 |
| **계정 관리** | USR-001 | 관리자 계정 CRUD | 어드민 시스템 접근용 계정 생성, 상세 조회, 정보 수정, 비활성화(정지) 기능 제공. | Administrator |
| **권한 관리** | USR-002 | 역할 기반 접근 제어 (RBAC) | 계정별 2가지 역할(Administrator, User) 부여. <br> - Administrator: 전체 URL 접근 가능.<br> - User: 계정 관리 메뉴 접근 차단, 로그 조회 등 제한적 기능만 허용. | 시스템 |
| **보안/감사** | AUD-001 | 어드민 활동 로그 기록 | 계정 생성/권한 변경, 주요 데이터 삭제 등 시스템에 영향을 미치는 관리자 행위에 대한 이력(시간, 행위자 ID, IP, 내용) 기록. | Administrator |
| **UI/UX** | UI-001 | 시스템 대시보드 | 로그인 직후 진입하는 메인 화면. 전체 계정 수, 최근 감사 로그 요약, 시스템 상태 등 주요 지표 시각화. | 공통 |
| **UI/UX** | UI-002 | 다중 조건 검색 및 페이징 | 데이터 그리드(표) 제공 시, 날짜 범위, ID, 상태 등 다중 조건 필터링 기능 및 서버 사이드 페이징 처리 기능 제공. | 공통 |


## 3. 젠킨스 빌드 스크립트
```
pipeline {
    agent any
    
    environment {
        // 깃랩 API 인증 토큰 (젠킨스 Credentials에 등록된 ID 사용)
        GITLAB_TOKEN = credentials('gitlab-api-token')
        GITLAB_URL = 'http://gitlab.samsungsecurities.local/api/v4'
        
        // [신규 추가] 어드민 대시보드 API 서버 주소 
        // (주의: 젠킨스 서버와 로컬PC가 다를 경우, localhost 대신 PC의 실제 IP를 입력하세요)
        ADMIN_API_URL = 'http://localhost:8080/api/audit' 
    }

    stages {
        stage('계정 추출 및 처리') {
            steps {
                script {
                    // 배치 실행일자 (예: 20260427)
                    def executionDate = new Date().format("yyyyMMdd")
                    
                    echo "========================================"
                    echo "GitLab 미사용 계정 정리 배치를 시작합니다."
                    echo "========================================"

                    // 1. GitLab API를 통해 90일 이상 미접속 계정 목록을 가져오는 로직 (가정)
                    // def response = sh(script: "curl -s --header 'PRIVATE-TOKEN: ${GITLAB_TOKEN}' ${GITLAB_URL}/users?active=true", returnStdout: true)
                    // def users = readJSON text: response
                    
                    // (테스트용 하드코딩 데이터 - 실제 운영 시 위 API 호출로 교체)
                    def users = [
                        [id: 456, username: " skyfox ", last_activity_on: "2026-02-01"], // 의도된 공백 포함 데이터
                        [id: 457, username: "testuser1", last_activity_on: "2025-12-15"]
                    ]

                    for (user in users) {
                        // [핵심 보완] 4/25에 발생했던 공백 에러를 방지하기 위한 trim() 처리
                        def cleanUsername = user.username.trim()
                        def gitlabId = user.id
                        def lastActivityOn = user.last_activity_on ?: "N/A"
                        def ldapExists = false // 사내 LDAP 연동 확인 로직 (기본값 설정)
                        def finalAction = ""
                        def errorMessage = "None"

                        try {
                            // 2. 사내 LDAP 퇴사자 여부 점검 (가상의 로직)
                            // ldapExists = checkLdapStatus(cleanUsername)
                            
                            if (!ldapExists) {
                                // 3-1. LDAP에 없는 퇴사자: 계정 완전 차단 (Block)
                                echo "[BLOCKED] 퇴사자 차단 처리: ${cleanUsername}"
                                // sh "curl -X POST --header 'PRIVATE-TOKEN: ${GITLAB_TOKEN}' ${GITLAB_URL}/users/${gitlabId}/block"
                                finalAction = "BLOCKED"
                            } else {
                                // 3-2. LDAP에는 있으나 미사용자: 비활성화 (Deactivate)
                                echo "[DEACTIVATED] 장기 미사용자 비활성화 처리: ${cleanUsername}"
                                // sh "curl -X POST --header 'PRIVATE-TOKEN: ${GITLAB_TOKEN}' ${GITLAB_URL}/users/${gitlabId}/deactivate"
                                finalAction = "DEACTIVATED"
                            }

                        } catch (Exception e) {
                            errorMessage = e.getMessage().take(50) // 에러 메시지가 너무 길면 자름
                            finalAction = "ERROR"
                            echo "[ERROR] ${cleanUsername} 처리 중 예외 발생: ${errorMessage}"
                        }

                        // 4. [신규 추가] 처리 결과를 어드민 서버(Spring Boot API)로 전송
                        echo "어드민 서버로 감사 이력 전송 중..."
                        def payload = """
                        {
                            "executionDate": "${executionDate}",
                            "gitlabId": ${gitlabId},
                            "username": "${cleanUsername}",
                            "lastActivityOn": "${lastActivityOn}",
                            "ldapExists": ${ldapExists},
                            "finalAction": "${finalAction}",
                            "errorMessage": "${errorMessage}"
                        }
                        """
                        
                        // 백그라운드 서버로 POST 요청 발송
                        sh """
                            curl -X POST ${ADMIN_API_URL} \\
                            -H 'Content-Type: application/json' \\
                            -d '${payload}'
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo "GitLab 계정 정리 배치가 완료되었습니다."
        }
    }
}
```
