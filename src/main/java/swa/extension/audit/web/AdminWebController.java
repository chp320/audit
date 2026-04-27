package swa.extension.audit.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import swa.extension.audit.audit.AuditRepository;

@Controller
public class AdminWebController {
	
	private final AuditRepository repository;
	
	public AdminWebController(AuditRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping("/admin")
	public String adminPage(Model model) {
		// H2에서 조회한 데이터는 'audits'라는 이름으로 화면(HTML)에 전달
		model.addAttribute("audits", repository.findAll());
		return "admin/accounts";				// src/main/resources/templates/admin/accounts.html 호출
	}

}
