package swa.extension.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 인입되는 HTTP 요청에 대해 인증, 인가를 위한 config 파일
 * - URL별 접근 권한(RBAC) 매핑, 
 * - 비밀번호 암호화 알고리즘 등록
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// HttpSecurity 객체를 사용해서 보안 필터 조립
		http
//			.csrf(csrf -> csrf.disable())	// POST 요청 시 CSRF 토큰 필요하나 젠킨스에서 단순 호출 처리를 위해 비활성화 (테스트 목적)
			.csrf(csrf -> csrf.ignoringRequestMatchers("/api/audit/**", "/h2-console/**"))	// 젠킨스에서 호출, h2
			.headers(headers -> headers.frameOptions(frame -> frame.disable()))	// <frame>,<iframe> 태그 사용을 막는 X-Frame-Options 헤더 off -> H2 콘솔이 iframe 사용하고 있어서 백지화 방지 목적
			// url별 권한 처리
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/audit/**").permitAll()	// 젠킨스 호출 /api/audit 은 무조건 허용
				.requestMatchers("/h2-console/**").permitAll()	// /h2-console 은 무조건 허용
				.requestMatchers("/admin/accounts/**").hasRole("ADMIN")	// 계정 메뉴는 ADMIN인 경우만 접근 허용
				.requestMatchers("/admin/**").authenticated()	// 나머지 admin 하위는 로그인 필수
				.anyRequest().authenticated()					// 그외 모든 url은 로그인 통과 시 허용
			)
			// 로그인 폼 설정
			.formLogin(form -> form
				.defaultSuccessUrl("/admin/audit", true)		// 로그인 성공 시 메인 대시보드
				.permitAll()
			)
			// 로그 아웃 처리
			.logout(logout -> logout
				.logoutUrl("/logout")							// html 에서 호출할 url
				.logoutSuccessUrl("/login?logout")				// 로그아웃 시 이동할 url
				.invalidateHttpSession(true)					// 로그아웃 시 사용자 세션 모두 삭제 (세션 하이재킹 회피 목적)
				.deleteCookies("JSESSIONID")					// 인증 쿠키 삭제
				.permitAll()
			)
			// 예외 처리
			.exceptionHandling(handler -> handler
                .accessDeniedPage("/admin/access-denied")		// 권한 부족 시 이동할 전용 안내 페이지
            );
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		// 단방향 해시 알고리즘 BCrypt 사용해서 암호화
		return new BCryptPasswordEncoder();
	}
}
