import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def inactiveUserList = []

// 파일 로깅 대신 REST API를 통해 H2 데이터베이스(어드민 서버)로 데이터를 전송하는 공통 함수
def sendToH2(Map dataPayload) {
    def jsonBody = JsonOutput.toJson(dataPayload)
    // ADMIN_API_URL은 environment 블록에 'http://RHEL서버IP:8080/api/audit' 로 정의되어야 함
    sh """
        curl -s -X POST -H "Content-Type: application/json" -d '${jsonBody}' ${env.ADMIN_API_URL}
    """
}

pipeline {
    agent any
    environment {
        GITLAB_API_BASE = "http://45.219.31.83:18080/api/v4/users"
        // 신규 어드민 서버 주소 지정
        ADMIN_API_URL = "http://localhost:8080/api/audit"
        LDAP_PORT = "389"
        // 이번 파이프라인 실행의 고유 식별 시간 (H2 데이터 매핑용)
        EXECUTION_TIME = "${new Date().format('yyyy-MM-dd HH:mm:ss')}"
    }

    stages {
        stage('Gather Inactive Users (H2 Insert)') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'deployadm_dev', usernameVariable: 'UNUSED_USER', passwordVariable: 'GITLAB_TOKEN')]) {
                    script {
                        def days = params.INACTIVE_DAYS as Integer
                        def cutoffDate = new Date().minus(days).format("yyyy-MM-dd")
                        
                        env.TOTAL_USERS = 0
                        def page = 1
                        inactiveUserList.clear()

                        while (true) {
                            def response = sh(script: 'curl -s --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "${GITLAB_API_BASE}?per_page=100&page=' + page + '"', returnStdout: true).trim()
                            def users = new JsonSlurperClassic().parseText(response)
                            if (users.size() == 0) break

                            env.TOTAL_USERS = (env.TOTAL_USERS as Integer) + users.size()

                            for (int i = 0; i < users.size(); i++) {
                                def user = users[i]
                                if ((user.last_activity_on == null || user.last_activity_on.compareTo(cutoffDate) <= 0) && user.user_type != "project_bot" && !user.username.startsWith("project_")) {
                                    def lastAct = user.last_activity_on ?: "Null"
                                    
                                    inactiveUserList.add([id: user.id, username: user.username, last_activity_on: lastAct])
                                    
                                    // 요구사항 1: 전체 사용자 조회 후 H2에 실행시각, id, username, last_activity_on 기록
                                    sendToH2([
                                        executionTime: env.EXECUTION_TIME,
                                        gitlabId: user.id,
                                        username: user.username,
                                        lastActivityOn: lastAct
                                    ])
                                }
                            }
                            if (users.size() < 100) break
                            page++
                        }
                    }
                }
            }
        }
        
        stage('Process LDAP & GitLab Status (H2 Update)') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'deployadm_dev', usernameVariable: 'UNUSED_USER', passwordVariable: 'GITLAB_TOKEN'),
                    usernamePassword(credentialsId: 'ldap-auth', usernameVariable: 'LDAP_BIND_DN', passwordVariable: 'LDAP_PASSWORD'),
                    string(credentialsId: 'ldap-host', variable: 'LDAP_HOST'),
                    string(credentialsId: 'ldap-base-dn', variable: 'LDAP_BASE_DN')
                ]) {
                    script {
                        for (int i = 0; i < inactiveUserList.size(); i++) {
                            def user = inactiveUserList[i]
                            
                            // JNDI LDAP 확인 (이전 스크립트의 checkUserInLdapDebug 함수 사용 가정)
                            Map ldapResult = checkUserInLdapDebug(env.LDAP_HOST, env.LDAP_PORT, env.LDAP_BIND_DN, env.LDAP_PASSWORD, env.LDAP_BASE_DN, user.username)
                            
                            if (ldapResult.error != "None") continue
                            
                            // 요구사항 2: LDAP 존재 여부를 H2에 업데이트
                            sendToH2([
                                executionTime: env.EXECUTION_TIME,
                                username: user.username,
                                ldapExists: ldapResult.exists
                            ])
                            
                            boolean isTarget = !ldapResult.exists
                            boolean isExecuted = false
                            
                            if (isTarget) {
                                if (!params.DRY_RUN) {
                                    def actionResultCode = sh(script: 'curl -s -o /dev/null -w "%{http_code}" -X DELETE --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "${GITLAB_API_BASE}/' + user.id + '"', returnStdout: true).trim()
                                    if (actionResultCode == "204" || actionResultCode == "202" || actionResultCode == "200") {
                                        isExecuted = true
                                    }
                                }
                            }
                            
                            // 요구사항 3: 삭제 대상 여부 및 실제 삭제 실행 여부를 H2에 업데이트
                            sendToH2([
                                executionTime: env.EXECUTION_TIME,
                                username: user.username,
                                deleteTarget: isTarget,
                                deleteExecuted: isExecuted
                            ])
                        }
                    }
                }
            }
        }
    }
}