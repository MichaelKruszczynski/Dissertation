package dis;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(path = "/holiday")
public class HolidayController {
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private HolidayRepository holidayRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	@RequestMapping
	public String main() {
		return "holiday";
	}

	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("holiday", new Holiday());
		model.addAttribute("employees", employeeRepository.findAll());
		List<HolidayType> holType = new ArrayList<HolidayType>();
		holType.add(HolidayType.FULL_DAY);
		holType.add(HolidayType.HALF_DAY);
		model.addAttribute("holidayType", holType);

		return "holidayAdd";
	}

	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Holiday holiday, Errors errors, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("employees", employeeRepository.findAll());
			List<HolidayType> holType = new ArrayList<HolidayType>();
			holType.add(HolidayType.FULL_DAY);
			holType.add(HolidayType.HALF_DAY);
			model.addAttribute("holidayType", holType);
			return "holidayAdd";
		}
		holidayRepository.save(holiday);
		return "holidayAddResult";
	}

	@GetMapping(path = "/request")
	public String createRequestForm(Model model) {
		model.addAttribute("holiday", new Holiday());
		model.addAttribute("employees", employeeRepository.findAll());
		List<HolidayType> holType = new ArrayList<HolidayType>();
		holType.add(HolidayType.FULL_DAY);
		holType.add(HolidayType.HALF_DAY);
		model.addAttribute("holidayType", holType);

		return "holidayRequest";
	}

	@PostMapping(path = "/request")
	public String createRequestSubmit(@ModelAttribute @Valid Holiday holiday, Errors errors, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("employees", employeeRepository.findAll());
			List<HolidayType> holType = new ArrayList<HolidayType>();
			holType.add(HolidayType.FULL_DAY);
			holType.add(HolidayType.HALF_DAY);
			model.addAttribute("holidayType", holType);
			return "holidayRequest";
		}
		holidayRepository.save(holiday);
		return "holidayAddResult";

	}

	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<Holiday> findAll = holidayRepository.findAll();
		model.addAttribute("holidays", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();

		return "holidayAll";
	}

	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("holiday", holidayRepository.findOne(id));
		// this returns JSON or XML with the department
		return "holidayById";

	}

	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("holiday", holidayRepository.findOne(id));
		// this returns JSON or XML with the department
		model.addAttribute("employees", employeeRepository.findAll());
		List<HolidayType> holType = new ArrayList<HolidayType>();
		holType.add(HolidayType.FULL_DAY);
		holType.add(HolidayType.HALF_DAY);
		model.addAttribute("holidayType", holType);
		return "holidayEdit";

	}

	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		Holiday findOne = holidayRepository.findOne(id);
		holidayRepository.delete(findOne);
		return readAll(model);
	}

	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Holiday holiday, Errors errors,
			Model model) {
		if (errors.hasErrors()) {
			holiday.setId(id);
			model.addAttribute("employees", employeeRepository.findAll());
			List<HolidayType> holType = new ArrayList<HolidayType>();
			holType.add(HolidayType.FULL_DAY);
			holType.add(HolidayType.HALF_DAY);
			model.addAttribute("holidayType", holType);
			return "holidayEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		Holiday dbHoliday = holidayRepository.findOne(id);
		// update the properties with values comming from model
		dbHoliday.setDay(holiday.getDay());
		dbHoliday.setType(holiday.getType());
		dbHoliday.setEmployee(holiday.getEmployee());
		// dbHoliday.setHalfDay(holiday.isHalfDay());
		// then save(update) to database
		holidayRepository.save(dbHoliday);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute Holiday holiday, Model model) {
		Holiday findOne = holidayRepository.findOne(holiday.getId());
		holidayRepository.delete(findOne);

		return readAll(model);
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");

		dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String value) {
				try {
					setValue(new SimpleDateFormat("yyyy-MM-dd").parse(value));
				} catch (ParseException e) {
					setValue(null);
				}
			}
		});
	}
}
