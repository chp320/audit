package swa.extension.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Gitlab 에서 추출한 계정 관리 목적의 entity
 * 
 * exeuctionTime	실행시간
 * gitlabId			gitlab의 숫자형태의 id
 * username			사용자명(사번)
 * lastActivityOn	마지막 사용시간
 * 
 * ldapExists		LDAP 존재여부
 * deleteTarget		삭제대상 여부
 * deleteExecuted	삭제처리 여부
 */
@Entity
public class GitlabAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String executionTime;
	private Long gitlabId;
	private String username;
	private String lastActivityOn;
	
	private Boolean ldapExists;
	private Boolean deleteTarget;
	private Boolean deleteExecuted;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getExexcutionTime() {
		return executionTime;
	}
	public void setExexcutionTime(String exexcutionTime) {
		this.executionTime = exexcutionTime;
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
	public Boolean getDeleteTarget() {
		return deleteTarget;
	}
	public void setDeleteTarget(Boolean deleteTarget) {
		this.deleteTarget = deleteTarget;
	}
	public Boolean getDeleteExecuted() {
		return deleteExecuted;
	}
	public void setDeleteExecuted(Boolean deleteExecuted) {
		this.deleteExecuted = deleteExecuted;
	}
	
}
