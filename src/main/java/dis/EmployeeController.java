package dis;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private EmployeeRepository employeeRepository;

	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private DepartmentRepository departmentRepository;

	@RequestMapping
	public String main() {
		return "employee";
	}

	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("employee", new Employee());
		model.addAttribute("departments", departmentRepository.findAll());

		List<EmployeeType> empType = new ArrayList<EmployeeType>();
		empType.add(EmployeeType.FULL_TIME);
		empType.add(EmployeeType.PART_TIME);
		model.addAttribute("EmployeeType", empType);
		return "employeeAdd";
	}

	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Employee employee, Errors errors, Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("departments", departmentRepository.findAll());

			List<EmployeeType> empType = new ArrayList<EmployeeType>();
			empType.add(EmployeeType.FULL_TIME);
			empType.add(EmployeeType.PART_TIME);
			model.addAttribute("EmployeeType", empType);
			return "employeeAdd";
		}
		employeeRepository.save(employee);
		return "employeeAddResult";
	}

	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<Employee> findAll = employeeRepository.findAll();
		model.addAttribute("employees", findAll);

		// the users
		// return departmentRepository.findAll();
		return "employeeAll";
	}

	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("employee", employeeRepository.findOne(id));
		// this returns JSON or XML with the department
		return "employeeById";

	}

	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("employee", employeeRepository.findOne(id));
		model.addAttribute("departments", departmentRepository.findAll());
		// this returns JSON or XML with the department

		List<EmployeeType> empType = new ArrayList<EmployeeType>();
		empType.add(EmployeeType.FULL_TIME);
		empType.add(EmployeeType.PART_TIME);
		model.addAttribute("EmployeeType", empType);
		return "employeeEdit";

	}

	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		Employee findOne = employeeRepository.findOne(id);
		employeeRepository.delete(findOne);
		return readAll(model);
	}

	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Employee employee, Errors errors,
			Model model) {
		if (errors.hasErrors()) {

			model.addAttribute("departments", departmentRepository.findAll());
			List<EmployeeType> empType = new ArrayList<EmployeeType>();
			empType.add(EmployeeType.FULL_TIME);
			empType.add(EmployeeType.PART_TIME);
			model.addAttribute("EmployeeType", empType);
			return "employeeEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		Employee dbEmployee = employeeRepository.findOne(id);
		// update the properties with values comming from model
		dbEmployee.setName(employee.getName());
		dbEmployee.setType(employee.getType());
		dbEmployee.setEmployeeNo(employee.getEmployeeNo());
		dbEmployee.setTotalAnnualHolidayDays(employee.getTotalAnnualHolidayDays());
		// then save(update) to database
		employeeRepository.save(dbEmployee);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute Employee employee, Model model) {
		Employee findOne = employeeRepository.findOne(employee.getId());
		employeeRepository.delete(findOne);

		return readAll(model);
	}
}
