package dis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
// This means that this class is a Controller
// This means URL's start with /demo (after Application path)
@RequestMapping(path = "/")
public class MainController {

	@Autowired
	private EmployeeRepository employeeRepository;

	@RequestMapping
	public String main(Model model) {
		if (getCurrentUser().hasRole(ProjectNames.ROLE_ADMIN)) {
			model.addAttribute("userLevel", ProjectNames.ROLE_ADMIN);
		} else if (getCurrentUser().hasRole(ProjectNames.ROLE_MANAGER)) {
			model.addAttribute("userLevel", ProjectNames.ROLE_MANAGER);
		} else {
			model.addAttribute("userLevel", ProjectNames.ROLE_USER);
		}
		return "index";
	}

	private Employee getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
		Employee employee = principal.getEmployee();
		return employeeRepository.findOne(employee.getId());
	}
}
