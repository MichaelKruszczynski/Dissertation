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
@RequestMapping(path = "/report")

public class ReportController {
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private ReportRepository reportRepository;

	@RequestMapping
	public String main() {
		return "report";
	}

	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("report", "reportAdd");
		model.addAttribute(new Report());
		List<AccessLevel> accessLevel = new ArrayList<AccessLevel>();
		accessLevel.add(AccessLevel.ADMIN);
		accessLevel.add(AccessLevel.USER);
		model.addAttribute("accessLevel", accessLevel);
		return "reportAdd";
	}

	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Report report, Errors errors, Model model) {

		if (errors.hasErrors()) {
			List<AccessLevel> accessLevel = new ArrayList<AccessLevel>();
			accessLevel.add(AccessLevel.ADMIN);
			accessLevel.add(AccessLevel.USER);
			model.addAttribute("accessLevel", accessLevel);
			return "reportAdd";
		}

		reportRepository.save(report);
		return "reportAddResult";
	}

	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<Report> findAll = reportRepository.findAll();
		model.addAttribute("reports", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();
		return "reportAll";
	}

	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("report", reportRepository.findOne(id));
		// this returns JSON or XML with the department
		return "reportById";

	}

	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("report", reportRepository.findOne(id));
		List<AccessLevel> accessLevel = new ArrayList<AccessLevel>();
		accessLevel.add(AccessLevel.ADMIN);
		accessLevel.add(AccessLevel.USER);
		model.addAttribute("accessLevel", accessLevel);
		// this returns JSON or XML with the department
		return "reportEdit";

	}

	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		Report findOne = reportRepository.findOne(id);
		reportRepository.delete(findOne);
		return readAll(model);
	}

	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Report report, Errors errors,
			Model model) {
		if (errors.hasErrors()) {
			List<AccessLevel> accessLevel = new ArrayList<AccessLevel>();
			accessLevel.add(AccessLevel.ADMIN);
			accessLevel.add(AccessLevel.USER);
			model.addAttribute("accessLevel", accessLevel);
			return "reportEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		Report dbReport = reportRepository.findOne(id);
		// update the properties with values comming from model
		dbReport.setName(report.getName());
		dbReport.setQuery(report.getQuery());
		dbReport.setColumnNames(report.getColumnNames());
		dbReport.setAccessLevel(report.getAccessLevel());
		// then save(update) to database
		reportRepository.save(dbReport);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute Report report, Model model) {
		Report findOne = reportRepository.findOne(report.getId());
		reportRepository.delete(findOne);

		return readAll(model);
	}
}
