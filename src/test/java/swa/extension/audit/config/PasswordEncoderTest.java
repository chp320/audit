package swa.extension.audit.config;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security Config에 등록된 BCryptPasswordEncoder 가 정상적으로 해싱을 수행하고 검증하는지 확인
 */
public class PasswordEncoderTest {
	
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Test
	@DisplayName("평문 비밀번호가 BCrypt 알고리즘으로 정상 암호화 및 매칭되는지 테스트")
	public void testPasswordEncryptionAndMatching() {
		// 1. given - 사용자가 입력한 평문 비밀번호
		String rawPassword = "realSlave123!";
		
		// 2. when - DB에 저장하기 위해 단방향 해싱 수행
		String encodedPassword = passwordEncoder.encode(rawPassword);
		
		// 3. then - 평문과 암호문은 달라야 함
		assertNotEquals(rawPassword, encodedPassword, "평문과 암호문이 동일하면 안됨");
		
		// 3. then - BCrypt의 matches 메서드를 통해 일치 여부 확인
		assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "입력한 평문이 암호문과 매칭되어야 함");
	}

}
