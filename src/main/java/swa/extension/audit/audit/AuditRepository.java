package swa.extension.audit.audit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<GitlabAudit, Long>{

//	// 실행시간과 계정명을 기준으로 기존 데이터를 조회하는 메서드
//	GitlabAudit findByExecutionTimeAndUsername(String executionTime, String username);
	
	// 1. 특정 사용자의 이력을 시간 역순(최신순)으로 조회
	List<GitlabAudit> findByUsernameOrderByCreatedAtDesc(String username);
	// 2. 검색 조건이 없을 때 전체 이력을 시간 역순(최신순)으로 조회
	List<GitlabAudit> findAllByOrderByCreatedAtDesc();
}
