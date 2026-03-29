package swa.extension.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<GitlabAudit, Long>{

	// 실행시간과 계정명을 기준으로 기존 데이터를 조회하는 메서드
	GitlabAudit findByExecutionTimeAndUsername(String executionTime, String username);
}
