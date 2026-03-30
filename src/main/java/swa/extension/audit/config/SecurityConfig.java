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
			.csrf(csrf -> csrf.disable())	// POST 요청 시 CSRF 토큰 필요하나 젠킨스에서 단순 호출 처리를 위해 비활성화
			.headers(headers -> headers.frameOptions(frame -> frame.disable()))	// <frame>,<iframe> 태그 사용을 막는 X-Frame-Options 헤더 off -> H2 콘솔이 frame 사용하고 있어서 백지화 방지 목적
			// url별 인가 처리
			.authorizeHttpRequests(auth -> auth
				// /api/audit, /h2-console 은 무조건 허용
				.requestMatchers("/api/audit/**").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				// /admin 하위 모든 페이지는 역할이 ADMIN인 경우만 접근 허용
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 그외 모든 url은 로그인 통과 시 허용
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.defaultSuccessUrl("/admin", true)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/login")
				// 로그아웃 시 사용자 세션 모두 삭제 (세션 하이재킹 회피 목적)
				.invalidateHttpSession(true)
				.permitAll()
			);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		// 단방향 해시 알고리즘 BCrypt 사용해서 암호화
		return new BCryptPasswordEncoder();
	}
}
