package swa.extension.audit.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import swa.extension.audit.user.AdminUser;
import swa.extension.audit.user.AdminUserRepository;

/**
 * 초기 테스트 계정 생성
 * - springboot 기동 시 기동되고, DB에 관리자계정이 없으면 테스트용 어드민 계정(admin/amin123!)을 자동 생성
 */
@Component
public class DataInitializer implements CommandLineRunner {

	private final AdminUserRepository adminUserRepository;
	private final PasswordEncoder passwordEncoder;
	
	public DataInitializer(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
		this.adminUserRepository = adminUserRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		// admin 계정이 미존재시에만 생성
		if(adminUserRepository.findByUsername("admin").isEmpty()) {
			AdminUser defaultAdmin = new AdminUser();
			defaultAdmin.setUsername("admin");
			
			// 평문 비밀번호를 단방향 암호화하여 저장
			defaultAdmin.setPassword(passwordEncoder.encode("admin123!"));
			defaultAdmin.setRole("ROLE_ADMIN");
			
			adminUserRepository.save(defaultAdmin);
			System.out.println("===== 초기 관리자 계정 생성 (id: admin) =====");
		}
	}
}
