package swa.extension.audit.audit;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "gitlab_audit", indexes = {
		@Index(name = "idx_audit_username", columnList = "username")
})
public class GitlabAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long gitlabId;
	private String username;
	
	private String finalAction; 	// deactivated, blocked, deleted, failed, skipped
	
	@Column(updatable = false)
	private LocalDateTime createdAt;	// DB에 적재되는 변경 일시
	
	private String executionDate;		// 배치작업 수행일자 (yyyymmdd)
	private String lastActivityOn;		// 깃랩 마지막 접속일
	private Boolean ldapExists;			// LDAP 존재여부
	private String errorMessage;		// 에러 메시지
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();	// DB에 insert 하기 전 현재시간 설정
	}
	
	
	// ---------- getter/setter ---------- //
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGitlabId() {
		return gitlabId;
	}

	public void setGitlabId(Long gitlabId) {
		this.gitlabId = gitlabId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFinalAction() {
		return finalAction;
	}

	public void setFinalAction(String finalAction) {
		this.finalAction = finalAction;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}

	public String getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(String lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

	public Boolean getLdapExists() {
		return ldapExists;
	}

	public void setLdapExists(Boolean ldapExists) {
		this.ldapExists = ldapExists;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}	
}