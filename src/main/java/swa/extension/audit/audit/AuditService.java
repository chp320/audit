package swa.extension.audit.audit;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

	private final AuditRepository auditRepository;
	public AuditService(AuditRepository auditRepository) {
		this.auditRepository = auditRepository;
	}
	
	// 젠킨스에서 보내는 데이터를 이력성으로 기록
	@Transactional
	public void recordAuditLog(GitlabAudit auditData) {
		// 젠킨스에서 보내는 데이터는 감사용이기 때문에 무조건 insert 처리
		auditRepository.save(auditData);
	}
	
	// 화면에서 username 으로 조회 시 호출
	@Transactional(readOnly = true)
	public List<GitlabAudit> getAuditHistory(String username) {
		if(username == null || username.trim().isEmpty()) {
			return auditRepository.findAllByOrderByCreatedAtDesc();
		}
		return auditRepository.findByUsernameOrderByCreatedAtDesc(username);
	}
	
}
