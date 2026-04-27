package swa.extension.audit.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import swa.extension.audit.config.SecurityConfig;

/**
 * 계정 관리 controller
 */
@Controller
@RequestMapping("/admin/accounts")
public class AdminAccountController {

	private final AdminUserRepository adminUserRepository;
	private final PasswordEncoder passwordEncoder;
	
	public AdminAccountController(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
		this.adminUserRepository = adminUserRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	// 계정 목록 조회
	@GetMapping
	public String listAccounts(Model model) {
		model.addAttribute("accounts", adminUserRepository.findAll());
		return "admin/accounts";
	}
	
	// 계정 생성
	@PostMapping("/create")
	public String createAccount(@ModelAttribute AdminUserRequest request) {
		AdminUser newUser = new AdminUser();
		newUser.setUsername(request.getUsername());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		newUser.setRole(request.getRole());
		adminUserRepository.save(newUser);
		return "redirect:/admin/accounts";
	}

	// 계정 삭제
	@PostMapping("/delete/{id}")
	public String deleteAccount(@PathVariable Long id) {
		adminUserRepository.deleteById(id);
		return "redirect:/admin/accounts";
	}
}
