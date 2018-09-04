package dis;

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
		model.addAttribute("department", "departmentAdd");
		model.addAttribute(new Department());
		return "departmentAdd";
	}

	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Department department, Errors errors, Model model) {

		if (errors.hasErrors()) {

			return "departmentAdd";
		}

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

	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("department", departmentRepository.findOne(id));
		// this returns JSON or XML with the department
		return "departmentEdit";

	}

	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		Department findOne = departmentRepository.findOne(id);
		departmentRepository.delete(findOne);
		return readAll(model);
	}

	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Department department, Errors errors,
			Model model) {
		if (errors.hasErrors()) {

			return "departmentEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		Department dbDepartment = departmentRepository.findOne(id);
		// update the properties with values comming from model
		dbDepartment.setTotalHoursAvailable(department.getTotalHoursAvailable());
		dbDepartment.setDepartmentName(department.getDepartmentName());
		// then save(update) to database
		departmentRepository.save(dbDepartment);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute Department department, Model model) {
		Department findOne = departmentRepository.findOne(department.getId());
		departmentRepository.delete(findOne);

		return readAll(model);
	}
}
