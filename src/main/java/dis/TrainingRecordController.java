package dis;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// This means that this class is a Controller
@RequestMapping(path = "/trainingrecord")
public class TrainingRecordController {
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private TrainingRecordRepository trainingRecordRepository;
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private EmployeeRepository employeeRepository;

	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private TrainingRepository trainingRepository;

	@RequestMapping
	public String main() {
		return "trainingRecord";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("trainingRecord", new TrainingRecord());
		model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
		model.addAttribute("trainings", trainingRepository.findAllByOrderByNameAsc());
		return "trainingRecordAdd";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid TrainingRecord trainingRecord, Errors errors, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
			model.addAttribute("trainings", trainingRepository.findAllByOrderByNameAsc());
			return "trainingRecordAdd";
		}

		trainingRecordRepository.save(trainingRecord);
		return "trainingRecordAddResult";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<TrainingRecord> findAll = trainingRecordRepository.findAllByOrderByEmployeeNameAsc();
		model.addAttribute("trainingRecords", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();
		return "trainingRecordAll";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("trainingRecord", trainingRecordRepository.findOne(id));
		// this returns JSON or XML with the department
		return "trainingRecordById";

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("trainingRecord", trainingRecordRepository.findOne(id));
		model.addAttribute("trainings", trainingRepository.findAllByOrderByNameAsc());
		model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
		// this returns JSON or XML with the department
		return "trainingRecordEdit";

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		TrainingRecord findOne = trainingRecordRepository.findOne(id);
		trainingRecordRepository.delete(findOne);
		return readAll(model);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid TrainingRecord trainingRecord,
			Errors errors, Model model) {

		if (errors.hasErrors()) {
			trainingRecord.setId(id);
			model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
			model.addAttribute("trainings", trainingRepository.findAllByOrderByNameAsc());
			return "trainingRecordEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		TrainingRecord dbTrainingRecord = trainingRecordRepository.findOne(id);
		// update the properties with values comming from model
		dbTrainingRecord.setTraining(trainingRecord.getTraining());
		dbTrainingRecord.setDay(trainingRecord.getDay());
		dbTrainingRecord.setEmployee(trainingRecord.getEmployee());
		// then save(update) to database
		trainingRecordRepository.save(dbTrainingRecord);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute TrainingRecord trainingRecord, Model model) {
		TrainingRecord findOne = trainingRecordRepository.findOne(trainingRecord.getId());
		trainingRecordRepository.delete(findOne);

		return readAll(model);
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));

	}
}
