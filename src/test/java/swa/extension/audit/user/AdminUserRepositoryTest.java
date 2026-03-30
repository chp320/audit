package swa.extension.audit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import swa.extension.audit.user.AdminUser;
import swa.extension.audit.user.AdminUserRepository;

/**
 * H2 DB 정상 생성, 실행 검증
 */
@DataJpaTest
public class AdminUserRepositoryTest {

	@Autowired
	private AdminUserRepository adminUserRepository;
	
	@Test
	@DisplayName("관리자 계정 생성 및 username으로 조회 테스트")
	public void testSaveAndFindByUsername() {
		// 1. given - 테스트용 계정 객체 생성
		AdminUser user = new AdminUser();
		user.setUsername("admin_test");
		user.setPassword("hashed_password_123");
		user.setRole("ROLE_ADMIN");
		
		// 2. when - DB저장 후 username 으로 조회
		adminUserRepository.save(user);
		// 데이터 조회 실패(null)이어도 NullPointerException 발생하지 않고 '비어(empty)있는 optional객체'를 반환하도록 사용
		Optional<AdminUser> foundUser = adminUserRepository.findByUsername("admin_test");
		
		// 3. then - 데이터 검증
		assertTrue(foundUser.isPresent(), "DB에서 계정을 찾을 수 있어야 함");
		assertEquals("admin_test", foundUser.get().getUsername(), "조회된 username이 일치애햐 함");
		assertEquals("ROLE_ADMIN", foundUser.get().getRole(), "조회된 role이 일치해야 함");
	}
	
	@Test
	@DisplayName("존재하지 않는 username 조회 시 빈 Optional 객체 반환 테스트")
	public void testFindByUsername_NotFound() {
		// 1. given
		// 2. when - DB에 없는 username 조회
		Optional<AdminUser> foundUser = adminUserRepository.findByUsername("unknown_user");
		
		// 3. then - 결과는 비어있어야 함
		assertTrue(foundUser.isEmpty(), "존재하지 않는 게정은 빈 Optional을 반환해야 함");
	}
}
