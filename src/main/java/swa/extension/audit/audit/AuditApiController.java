package swa.extension.audit.audit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 젠킨스에서 curl 로 전달하는 json 데이터를 수신해서 H2 에 저장하는 api
 */

@RestController
@RequestMapping("/api/audit")
public class AuditApiController {

    private final AuditRepository repository;
    
    public AuditApiController (AuditRepository repository) {
    	this.repository = repository;
    }

    @PostMapping
    public void saveOrUpdateAudit(@RequestBody GitlabAudit data) {
        GitlabAudit existing = repository.findByExecutionTimeAndUsername(data.getExexcutionTime(), data.getUsername());
        
        // 기존 데이터가 있는 경우, 조회한 데이터로 업데이트 (patch)
        if (existing != null) {
        	if (data.getLdapExists() != null) existing.setLdapExists(data.getLdapExists());
        	if (data.getDeleteTarget() != null ) existing.setDeleteTarget(data.getDeleteTarget());
        	if (data.getDeleteExecuted() != null ) existing.setDeleteExecuted(data.getDeleteExecuted());
        	
        	repository.save(existing);
        } else {
        	repository.save(existing);
        }
        
    }
    
}