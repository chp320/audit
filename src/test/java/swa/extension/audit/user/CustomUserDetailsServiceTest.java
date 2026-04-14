package swa.extension.audit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

	@Mock
	private AdminUserRepository adminUserRepository;
	
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;
	
	@Test
	@DisplayName("DB에 존재하는 사용자 조회 시 UserDetails 객체 정상 반환 테스트")
	public void testLoadUserByUsername_Success() {
		
		// 1. given
		AdminUser mockUser = new AdminUser();
		mockUser.setUsername("admin");
		mockUser.setPassword("realSlave123!");
		mockUser.setRole("ROEL_ADMIN");
		
		when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
		
		// 2. when
		UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");
		
		// 3. then
		assertEquals("admin", userDetails.getUsername());
		assertEquals("realSlave123!", userDetails.getPassword());
	}
	
	@Test
	@DisplayName("DB에 존재하지 않는 사용자 조회 시 UsernameNotFoundException 발생 테스트")
	public void testLoadUserByUsername_NotFound() {
		// 1. given
		when(adminUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());
		
		// 2. when
		// 3. then
		assertThrows(UsernameNotFoundException.class, () -> {
			customUserDetailsService.loadUserByUsername("unknown");
		});
	}
}
