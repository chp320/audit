package swa.extension.audit.user;

/**
 * 계정 생성/수정용 DTO
 */
public class AdminUserRequest {

	private String username;
	private String password;
	private String role;
	
	// 기본 생성자
	public AdminUserRequest() {}
	
	// getter & setter
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
