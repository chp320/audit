package swa.extension.audit.audit;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuditWebController {
	
	private final AuditService auditService;
	public AuditWebController (AuditService auditService) {
		this.auditService = auditService;
	}
	
	@GetMapping("/admin/audit")
	public String viewAuditDashboard(
			@RequestParam(name = "username", required = false) String username,
			Model model,
			Principal principal) {
		
		// 현 로그인 정보 셋팅
		if (principal != null) {
			model.addAttribute("currentUsername", principal.getName());
		}
		
		// 값이 미전달되는 경우 전체 조회
		model.addAttribute("audits", auditService.getAuditHistory(username));
		model.addAttribute("username", username);
		
		return "admin/audit";			// src/main/resources/templates/admin/audit.html 반환
	}
}
