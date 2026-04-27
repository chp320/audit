package swa.extension.audit.audit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 젠킨스에서 curl 로 전달하는 json 데이터를 수신해서 service 로 전달
 */

@RestController
@RequestMapping("/api/audit")
public class AuditApiController {

	private final AuditService auditService;
	public AuditApiController (AuditService auditService) {
		this.auditService = auditService;
	}
    
    @PostMapping
    public ResponseEntity<String> receiveData(@RequestBody GitlabAudit data) {
    	auditService.recordAuditLog(data);
    	return ResponseEntity.ok("Audit history successfully appended!");
    }
    
}