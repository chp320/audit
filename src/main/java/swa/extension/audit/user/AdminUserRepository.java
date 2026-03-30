package swa.extension.audit.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 로그인 시 입력받은 ID(username)로 DB에서 계정 정보 조회하기 위한 interface
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, Long>{
	Optional<AdminUser> findByUsername(String username);
}
