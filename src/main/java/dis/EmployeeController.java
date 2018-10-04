package dis;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// This means that this class is a Controller
@RequestMapping(path = "/employee")
public class EmployeeController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private EmployeeRepository employeeRepository;

	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private DepartmentRepository departmentRepository;

	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private RoleRepository roleRepository;

	@RequestMapping
	public String main() {
		return "employee";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("employee", new Employee());
		model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
		model.addAttribute("accessLevel", roleRepository.findAll());
		model.addAttribute("managers", employeeRepository.findAllManagers());
		List<EmployeeType> empType = new ArrayList<EmployeeType>();
		empType.add(EmployeeType.FULL_TIME);
		empType.add(EmployeeType.PART_TIME);
		model.addAttribute("EmployeeType", empType);
		return "employeeAdd";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Employee employee, Errors errors, Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
			model.addAttribute("accessLevel", roleRepository.findAll());
			model.addAttribute("managers", employeeRepository.findAllManagers());
			List<EmployeeType> empType = new ArrayList<EmployeeType>();
			empType.add(EmployeeType.FULL_TIME);
			empType.add(EmployeeType.PART_TIME);
			model.addAttribute("EmployeeType", empType);
			return "employeeAdd";
		}
		employee.setEnabled(true);
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		employeeRepository.save(employee);
		return "employeeAddResult";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<Employee> findAll = employeeRepository.findAllByOrderByNameAsc();
		model.addAttribute("employees", findAll);

		// the users
		// return departmentRepository.findAll();
		return "employeeAll";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("employee", employeeRepository.findOne(id));
		// this returns JSON or XML with the department
		return "employeeById";

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("employee", employeeRepository.findOne(id));
		model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
		// this returns JSON or XML with the department
		model.addAttribute("managers", employeeRepository.findAllManagers());
		model.addAttribute("accessLevel", roleRepository.findAll());
		List<EmployeeType> empType = new ArrayList<EmployeeType>();
		empType.add(EmployeeType.FULL_TIME);
		empType.add(EmployeeType.PART_TIME);
		model.addAttribute("EmployeeType", empType);
		return "employeeEdit";

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		Employee findOne = employeeRepository.findOne(id);
		employeeRepository.delete(findOne);
		return readAll(model);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Employee employee, Errors errors,
			Model model) {
		if (errors.hasErrors()) {
			// List<AccessLevel> accessLevel = new ArrayList<AccessLevel>();
			// accessLevel.add(AccessLevel.ADMIN);
			// accessLevel.add(AccessLevel.USER);
			model.addAttribute("accessLevel", roleRepository.findAll());
			model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
			model.addAttribute("managers", employeeRepository.findAllManagers());
			List<EmployeeType> empType = new ArrayList<EmployeeType>();
			empType.add(EmployeeType.FULL_TIME);
			empType.add(EmployeeType.PART_TIME);
			model.addAttribute("EmployeeType", empType);
			return "employeeEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		Employee dbEmployee = employeeRepository.findOne(id);
		// update the properties with values coming from model
		dbEmployee.setName(employee.getName());
		dbEmployee.setType(employee.getType());
		dbEmployee.setEmployeeNo(employee.getEmployeeNo());
		dbEmployee.setEmail(employee.getEmail());
		dbEmployee.setDepartment(employee.getDepartment());
		dbEmployee.setPassword(passwordEncoder.encode(employee.getPassword()));
		dbEmployee.setTotalAnnualHolidayDays(employee.getTotalAnnualHolidayDays());
		dbEmployee.setManager(employeeRepository.findOne(employee.getManager().getId()));
		dbEmployee.setRoles(employee.getRoles());
		// then save(update) to database
		employeeRepository.save(dbEmployee);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute Employee employee, Model model) {
		Employee findOne = employeeRepository.findOne(employee.getId());
		employeeRepository.delete(findOne);

		return readAll(model);
	}
}
