package dis;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	@RequestMapping
	public String main() {
		return "holiday";
	}

	// @PreAuthorize("hasRole('" + ProjectNames.ROLE_ADMIN + "')")
	// @PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("holiday", new Holiday());
		model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
		List<HolidayType> holType = new ArrayList<HolidayType>();
		holType.add(HolidayType.FULL_DAY);
		holType.add(HolidayType.HALF_DAY);
		model.addAttribute("holidayType", holType);

		return "holidayAdd";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid Holiday holiday, Errors errors, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
			List<HolidayType> holType = new ArrayList<HolidayType>();
			holType.add(HolidayType.FULL_DAY);
			holType.add(HolidayType.HALF_DAY);
			model.addAttribute("holidayType", holType);
			return "holidayAdd";
		}

		// Employee employee = getCurrentUser();
		// Collection<Role> roles = getCurrentUser().getRoles();
		// boolean admin = false;
		// for (Role role : roles) {
		// if ("ROLE_ADMIN".equals(role.getName())) {
		// admin = true;
		// break;
		// }
		// }
		// if (admin && holiday.getEmployee().getId() != getCurrentUser().getId()) {
		// holiday.setActivatedBy(employee.getName());
		// }

		holidayRepository.save(holiday);
		return "holidayAddResult";
	}

	@GetMapping(path = "/request")
	public String createRequestForm(Model model) {

		model.addAttribute("holiday", new Holiday());

		List<HolidayType> holType = new ArrayList<HolidayType>();
		holType.add(HolidayType.FULL_DAY);
		holType.add(HolidayType.HALF_DAY);

		model.addAttribute("holidayType", holType);

		return "holidayRequest";
	}

	@PostMapping(path = "/request")
	public String createRequestSubmit(@ModelAttribute @Valid Holiday holiday, Errors errors, Model model) {
		System.out.println(holiday.getDay2());
		if (errors.getErrorCount() > 1) {
			model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
			List<HolidayType> holType = new ArrayList<HolidayType>();
			holType.add(HolidayType.FULL_DAY);
			holType.add(HolidayType.HALF_DAY);
			model.addAttribute("holidayType", holType);
			return "holidayRequest";
		}
		long daysBetween = 0;
		if (holiday.getDay2() != null) {

			daysBetween = holiday.getDay2().getTime() - holiday.getDay().getTime();
			daysBetween = TimeUnit.DAYS.convert(daysBetween, TimeUnit.MILLISECONDS);
		}
		System.out.println("daysBetween:" + daysBetween);
		Date holidayDate = holiday.getDay();
		Date firstHolidayDate = holiday.getDay();
		Holiday lastHoliday = null;
		int index = (int) (daysBetween + 1);
		for (int i = 0; i < index; i++) {
			Employee employee = getCurrentUser();
			employee = employeeRepository.findOne(employee.getId());
			Holiday holidayToSave = new Holiday();
			holidayToSave.setType(holiday.getType());
			holidayToSave.setEmployee(employee);
			holidayToSave.setDay(holidayDate);
			holidayToSave.setDay2(holidayDate);
			holidayRepository.save(holidayToSave);
			lastHoliday = holidayToSave;
			holidayDate = new Date(holidayDate.getTime() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

		}
		try {
			// inform employee
			new Mail(lastHoliday.getEmployee().getEmail()).sendMail("Holiday Requested",
					"Your holiday request for " + dateFormat(firstHolidayDate) + " - "
							+ dateFormat(lastHoliday.getDay()) + " was created and will be reviewed shortly");

			// now inform manager
			Employee manager = lastHoliday.getEmployee().getManager();
			new Mail(manager.getEmail()).sendMail("Holiday Requested",
					"Employee " + lastHoliday.getEmployee().getEmail() + " requested a "
							+ ((HolidayType.HALF_DAY.equals(lastHoliday.getType()) ? "" : "half day")) + " holiday on "
							+ dateFormat(firstHolidayDate) + " - " + dateFormat(lastHoliday.getDay()));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "holidayAllResult";
	}

	private String dateFormat(Date day) {
		return dateFormat.format(day);
	}

	private Employee getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
		Employee employee = principal.getEmployee();
		return employeeRepository.findOne(employee.getId());
	}

	@PreAuthorize("hasRole('" + ProjectNames.ROLE_ADMIN + "')")
	// @PreAuthorize("hasRole('ADMIN')")
	// @PreAuthorize("hasRole('ROLE_USER')")
	// @PreAuthorize("hasRole('USER')")
	// @PreAuthorize("hasAnyRole('ADMIN','USER')")
	// @PreAuthorize("hasAnyRole('ADMIN','USER','ROLE_ADMIN','ROLE_USER')")
	@GetMapping(path = "/all")
	public String readAll(Model model) {

		Iterable<Holiday> findAll = holidayRepository.findAllByOrderByEmployeeNameAscDayAsc();
		model.addAttribute("holidays", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();

		return "holidayAll";
	}

	@PreAuthorize("hasAnyRole('" + ProjectNames.ROLE_ADMIN + "','" + ProjectNames.ROLE_MANAGER + "', )")
	@GetMapping(path = "/requested")
	public String readRequested(Model model) {
		Iterable<Holiday> findAll = null;
		if (getCurrentUser().hasRole(ProjectNames.ROLE_ADMIN)) {
			findAll = holidayRepository.findAllByDepartmentByDayAsc(getCurrentUser().getDepartment().getId());
		} else {
			findAll = holidayRepository.findAllWhereEmployeeIsManager(getCurrentUser().getId());
		}

		model.addAttribute("holidays", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();

		return "holidayRequested";
	}

	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAnyRole('" + ProjectNames.ROLE_ADMIN + "','" + ProjectNames.ROLE_MANAGER + "', )")
	@PostMapping(path = "/requested", params = "edit=Accept")
	public ModelAndView acceptRequested(@RequestParam(value = "holidayIds", required = true) Long[] holidayIds,
			Model model) {
		List<Holiday> holidays = new ArrayList<Holiday>();
		for (Long holidayId : holidayIds) {
			holidays.add(holidayRepository.findOne(holidayId));
		}
		Collections.sort(holidays, new HolidayComparator());

		boolean chain = false;
		Holiday firstChainHoliday = null;
		Holiday currentChainHoliday = null;
		for (int i = 0; i < holidays.size(); i++) {
			chain = false;
			Holiday currentHoliday = holidays.get(i);
			{ // save first in case messages cause some unknown exception
				Holiday holidayToSave = holidayRepository.findOne(currentHoliday.getId());
				holidayToSave.setActivatedBy(getCurrentUser().getEmail());
				holidayRepository.save(holidayToSave);
			}
			if (firstChainHoliday == null) {
				firstChainHoliday = currentHoliday;
			} else {
				if (firstChainHoliday.getEmployee().getId() == currentHoliday.getEmployee().getId()) {
					int daysBetween = daysBetween(firstChainHoliday.getDay(), currentHoliday.getDay());
					if (daysBetween == 1) {
						currentChainHoliday = currentHoliday;
						chain = true;
					}
				}
				if (!chain) {
					sendRequestAcceptatedMessages(firstChainHoliday, currentChainHoliday);
					firstChainHoliday = currentHoliday;
					currentChainHoliday = null;
				}
			}

		}
		if (firstChainHoliday != null) {
			sendRequestAcceptatedMessages(firstChainHoliday, currentChainHoliday);
		}

		return new ModelAndView("redirect:/holiday/requested");
	}

	private void sendRequestAcceptatedMessages(Holiday firstChainHoliday, Holiday currentChainHoliday) {
		try {
			// mail to User
			String userMessage = "Your holiday request on " + dateFormat(firstChainHoliday.getDay());
			if (currentChainHoliday != null) {
				userMessage += " - " + dateFormat(currentChainHoliday.getDay());
			}
			userMessage += " was accepted by " + getCurrentUser().getName() + " on "
					+ dateFormat(new Timestamp(System.currentTimeMillis()));
			new Mail(firstChainHoliday.getEmployee().getEmail()).sendMail("Holiday Accepted", userMessage);

			// mail to HR
			String hrMessage = "Employee's " + firstChainHoliday.getEmployee().getName() + " holiday request on "
					+ dateFormat(firstChainHoliday.getDay());
			if (currentChainHoliday != null) {
				hrMessage += " - " + dateFormat(currentChainHoliday.getDay());
			}
			hrMessage += " was accepted by " + getCurrentUser().getName() + " on "
					+ dateFormat(new Timestamp(System.currentTimeMillis()));
			new Mail(Mail.companyHrEmail).sendMail("Holiday Accepted", hrMessage);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private void sendRequestDeclinedMessages(Holiday firstChainHoliday, Holiday currentChainHoliday) {
		try {
			// mail to User
			String userMessage = "Your holiday request on " + dateFormat(firstChainHoliday.getDay());
			if (currentChainHoliday != null) {
				userMessage += " - " + dateFormat(currentChainHoliday.getDay());
			}
			userMessage += " was declined by " + getCurrentUser().getName() + " on "
					+ dateFormat(new Timestamp(System.currentTimeMillis()));
			new Mail(firstChainHoliday.getEmployee().getEmail()).sendMail("Holiday Declined", userMessage);

			// mail to HR
			String hrMessage = "Employee's " + firstChainHoliday.getEmployee().getName() + " holiday request on "
					+ dateFormat(firstChainHoliday.getDay());
			if (currentChainHoliday != null) {
				hrMessage += " - " + dateFormat(currentChainHoliday.getDay());
			}
			hrMessage += " was declined by " + getCurrentUser().getName() + " on "
					+ dateFormat(new Timestamp(System.currentTimeMillis()));
			new Mail(Mail.companyHrEmail).sendMail("Holiday Declined", hrMessage);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@PreAuthorize("hasAnyRole('" + ProjectNames.ROLE_ADMIN + "','" + ProjectNames.ROLE_MANAGER + "', )")
	@PostMapping(path = "/requested", params = "edit=Decline")
	public ModelAndView declineRequested(@RequestParam(value = "holidayIds", required = true) Long[] holidayIds,
			Model model) {
		List<Holiday> holidays = new ArrayList<Holiday>();
		for (Long holidayId : holidayIds) {
			holidays.add(holidayRepository.findOne(holidayId));
		}
		Collections.sort(holidays, new HolidayComparator());

		boolean chain = false;
		Holiday firstChainHoliday = null;
		Holiday currentChainHoliday = null;
		List<Holiday> chainedHolidays = new ArrayList<Holiday>();
		for (int i = 0; i < holidays.size(); i++) {
			chain = false;
			Holiday currentHoliday = holidays.get(i);
			if (firstChainHoliday == null) {
				firstChainHoliday = currentHoliday;
				chainedHolidays.add(firstChainHoliday);
			} else {
				if (firstChainHoliday.getEmployee().getId() == currentHoliday.getEmployee().getId()) {
					int daysBetween = daysBetween(firstChainHoliday.getDay(), currentHoliday.getDay());
					if (daysBetween == 1) {
						currentChainHoliday = currentHoliday;
						chain = true;
						chainedHolidays.add(currentChainHoliday);
					}
				}
				if (!chain) {
					for (Holiday holidayToRemove : chainedHolidays) {
						Holiday findOne = holidayRepository.findOne(holidayToRemove.getId());
						if (findOne.getActivatedBy() == null || getCurrentUser().hasRole(ProjectNames.ROLE_ADMIN)) {
							holidayRepository.delete(findOne);
						}
					}
					chainedHolidays.clear();
					sendRequestDeclinedMessages(firstChainHoliday, currentChainHoliday);
					firstChainHoliday = currentHoliday;
					chainedHolidays.add(currentHoliday);
					currentChainHoliday = null;
				}
			}

		}
		if (firstChainHoliday != null) {
			for (Holiday holidayToRemove : chainedHolidays) {
				Holiday findOne = holidayRepository.findOne(holidayToRemove.getId());
				if (findOne.getActivatedBy() == null || getCurrentUser().hasRole(ProjectNames.ROLE_ADMIN)) {
					holidayRepository.delete(findOne);
				}
			}
			sendRequestAcceptatedMessages(firstChainHoliday, currentChainHoliday);
		}

		return new ModelAndView("redirect:/holiday/requested");
	}

	@GetMapping(path = "/my")
	public String readMy(Model model) {
		Employee emp = getCurrentUser();
		Iterable<Holiday> findAll = holidayRepository.findAllByEmployeeId(emp.getId());
		model.addAttribute("holidays", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();

		return "holidayMy";
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
		model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
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

	@RequestMapping(path = "/{id}/cancel")
	public String createCancel(@PathVariable("id") long id, Model model) {
		Holiday holiday = holidayRepository.findOne(id);
		if (holiday.getDay().after(new Date())) {
			holidayRepository.delete(holiday);
			Employee currentUser = getCurrentUser();
			try {
				String message = "You cancelled your holiday request on " + dateFormat(holiday.getDay())
						+ " was rejected by " + currentUser.getName() + " on "
						+ dateFormat(new Timestamp(System.currentTimeMillis()));
				new Mail(holiday.getEmployee().getEmail()).sendMail("Holiday Request cancelled", message);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return readMy(model);
		} else {
			return "Holiday in the past";// @ TODO
		}
	}

	@RequestMapping(path = "/{id}/cancelRequest")
	public ModelAndView createCancelRequest(@PathVariable("id") long id, Model model) {
		Holiday holiday = holidayRepository.findOne(id);
		holidayRepository.delete(holiday);
		try {
			Employee currentUser = getCurrentUser();
			new Mail(holiday.getEmployee().getEmail()).sendMail("Holiday Rejected",
					"Your holiday request on " + dateFormat(holiday.getDay()) + " was rejected by "
							+ currentUser.getName() + " on " + dateFormat(new Timestamp(System.currentTimeMillis())));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/holiday/requested");
	}

	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid Holiday holiday, Errors errors,
			Model model) {
		if (errors.hasErrors()) {
			holiday.setId(id);
			model.addAttribute("employees", employeeRepository.findAllByOrderByNameAsc());
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
		// Employee employee = getCurrentUser();
		// then save(update) to database
		holidayRepository.save(dbHoliday);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));

	}

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
}
