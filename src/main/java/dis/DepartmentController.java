package dis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// This means that this class is a Controller
@RequestMapping(path = "/department")
public class DepartmentController {
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private DepartmentRepository departmentRepository;

	@RequestMapping
	public String main() {
		return "department";
	}

	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("department", new Department());
		return "departmentAdd";
	}

	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute Department department) {
		departmentRepository.save(department);
		return "departmentAddResult";
	}

	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<Department> findAll = departmentRepository.findAll();
		model.addAttribute("departments", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();
		return "departmentAll";
	}

	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("department", departmentRepository.findOne(id));
		// this returns JSON or XML with the department
		return "departmentById";

	}

	@GetMapping(path = "{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		model.addAttribute("department", departmentRepository.findOne(id));
		// this returns JSON or XML with the department
		return "departmentEdit";
	}
}
