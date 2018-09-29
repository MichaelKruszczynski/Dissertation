package dis;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(path = "/report")

public class ReportController {
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private ReportRepository reportRepository;
	private RoleRepository roleRepository;

	@RequestMapping
	public String main() {
		return "report";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("report", "reportAdd");
		model.addAttribute(new Report());
		model.addAttribute("accessLevel", roleRepository.findAll());
		return "reportAdd";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Report report, Errors errors, Model model) {

		if (errors.hasErrors()) {

			model.addAttribute("accessLevel", roleRepository.findAll());
			return "reportAdd";
		}

		reportRepository.save(report);
		return "reportAddResult";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<Report> findAll = reportRepository.findAll();
		model.addAttribute("reports", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();
		return "reportAll";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("report", reportRepository.findOne(id));
		// this returns JSON or XML with the department
		return "reportById";

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("report", reportRepository.findOne(id));
		model.addAttribute("accessLevel", roleRepository.findAll());
		// this returns JSON or XML with the department
		return "reportEdit";

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		Report findOne = reportRepository.findOne(id);
		reportRepository.delete(findOne);
		return readAll(model);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Report report, Errors errors,
			Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("accessLevel", roleRepository.findAll());
			return "reportEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		Report dbReport = reportRepository.findOne(id);
		// update the properties with values comming from model
		dbReport.setName(report.getName());
		dbReport.setQuery(report.getQuery());
		dbReport.setColumnNames(report.getColumnNames());
		dbReport.setRole(report.getRole());
		// then save(update) to database
		reportRepository.save(dbReport);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute Report report, Model model) {
		Report findOne = reportRepository.findOne(report.getId());
		reportRepository.delete(findOne);

		return readAll(model);
	}
}
