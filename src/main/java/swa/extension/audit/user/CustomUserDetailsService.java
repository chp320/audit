package swa.extension.audit.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자가 로그인 폼에 아이디 입력하면, DB에서 사용자 정보를 가져와서 Spring Security 객체(UserDetails)로 변환하여 전달 (비밀번호 대조 목적)
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final AdminUserRepository adminUserRepository;
	
	public CustomUserDetailsService(AdminUserRepository adminUserRepository) {
		this.adminUserRepository = adminUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// 1. DB에서 사용자 조회
		AdminUser adminUser = adminUserRepository.findByUsername(username)
				.orElseThrow( () -> new UsernameNotFoundException("사용자를 찾을 수 없음: " + username));
		
		// 2. spring security가 인식할 수 있는 UserDetails 객체로 변환하여 반환
		return User.builder()
				.username(adminUser.getUsername())
				.password(adminUser.getPassword())		// DB에 저장된 암호화된 비밀번호
				.authorities(adminUser.getRole())		// 권한 부여 (ex. "ROLE_ADMIN")
				.build();
		
	}
}
