package dis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Iterators;

@Configuration
@EnableWebSecurity
@EnableScheduling
// @EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// @Order(ManagementServerProperties.BASIC_AUTH_ORDER + 1)
// @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
// @Order(SecurityProperties.IGNORED_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private MyUserDetailsService userDetailsService;
	@Autowired
	private TrainingRecordRepository trainingRecordRepository;
	@Autowired
	private HolidayRepository holidayRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
		// auth.build();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// http.authorizeRequests()//
		// .anyRequest()//
		// .authenticated()//
		// .and()//
		// .formLogin()//
		// .loginPage("/login")// only this added by me rest from superclass (kept
		// default)
		// .permitAll() //
		// .and() //
		// .logout() //
		// .permitAll();
		http.authorizeRequests() //
				.antMatchers("/css/style.css", "/Images/dhl-logo2.png") //
				.permitAll() //
				.anyRequest() //
				.authenticated() //
				.and() //
				.formLogin(). //
				loginPage("/login") //
				.permitAll() //
				.and() //
				.logout() //
				.permitAll();
	}

	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// // TODO Auto-generated method stub
	//// super.configure(http);
	//// http.anyMatchers("/").hasAnyAuthority("ROLE_USER").anyRequest().authenticated();
	// // .accessDecisionManager(accessDecisionManager());
	// }

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(getEncoder());
		return authProvider;

	}

	@Bean
	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	// @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
	// @Scheduled(fixedRate = 60 * 1000)
	@Scheduled(cron = "0 22 2 * * *") // 2:22 am
	public void dailyHolidayUserReport() {
		List<Employee> employees = new ArrayList<Employee>();
		for (Department dept : departmentRepository.findAll()) {
			Iterable<Holiday> todaysHolidays = holidayRepository.findAllAcceptedByDepartmentAndDayByDayAsc(dept.getId(),
					new Date());
			if (todaysHolidays != null && todaysHolidays.iterator().hasNext()) {
				// gather relevant people: each manager from teh department
				for (Employee employee : employeeRepository.findByDepartmentIdAndRoleName(dept.getId(),
						ProjectNames.ROLE_MANAGER)) {
					employees.add(employee);
				}

				// prepare email Topic
				String emailTopic = dateFormat.format(new Date()) + "'s holidays for department " + dept.getName();
				// prepare email String
				String emailString = "Currently there are " + Iterators.size(todaysHolidays.iterator())
						+ " holidays booked for today:\n";
				for (Holiday holiday : todaysHolidays) {
					emailString += holiday.getEmployee().getName() + " " + holiday.getEmployee().getEmployeeNo() + "("
							+ holiday.getEmployee().getEmail() + ") on " + holiday.getType() + "\n";
				}
				// send email to each of the employees
				for (Employee employee : employees) {
					try {
						new Mail(employee.getEmail()).sendMail(emailTopic, emailString);
					} catch (AddressException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	// @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
	// @Scheduled(fixedRate = 60 * 1000)
	@Scheduled(cron = "0 22 2 * * *") // 2:22 am
	public void dailyHolidayManagerReport() {
		List<Employee> employees = new ArrayList<Employee>();
		for (Department dept : departmentRepository.findAll()) {
			Iterable<Holiday> todaysHolidays = holidayRepository.findAllAcceptedByDepartmentAndDayAndRoleNameByDayAsc(
					dept.getId(), new Date(), ProjectNames.ROLE_MANAGER);
			if (todaysHolidays != null && todaysHolidays.iterator().hasNext()) {
				// gather relevant people 1: each admin from the department
				for (Employee employee : employeeRepository.findByDepartmentIdAndRoleName(dept.getId(),
						ProjectNames.ROLE_ADMIN)) {
					employees.add(employee);
				}

				// prepare email Topic
				String emailTopic = dateFormat.format(new Date()) + "'s holidays for department " + dept.getName();
				// prepare email String
				String emailString = "Currently there are " + Iterators.size(todaysHolidays.iterator())
						+ " manager holidays booked for today:\n";
				for (Holiday holiday : todaysHolidays) {
					emailString += "Manager " + holiday.getEmployee().getName() + " - "
							+ holiday.getEmployee().getEmployeeNo() + "(" + holiday.getEmployee().getEmail() + ") on "
							+ holiday.getType() + "\n";
				}
				// send email to each of the employees
				for (Employee employee : employees) {
					try {
						new Mail(employee.getEmail()).sendMail(emailTopic, emailString);
					} catch (AddressException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	// @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
	@Scheduled(fixedRate = 60 * 1000)
	// @Scheduled(cron = "0 22 2 * * *") // 2:22 am
	public void dailyMissingUserTrainingReport() {
		List<Map<String, Object>> queryForList = null;
		String query = ReportController.outversionedTrainingRecordsQuery;
		query = new QueryTokenizer().deTokenize(query);
		queryForList = jdbcTemplate.queryForList(query);
		List<Map<String, Object>> managers = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
		// mine the data
		for (Map<String, Object> map : queryForList) {
			// [Employee email, Training name, Current version, Newest version]
			String email = (String) map.get("Employee email");
			Employee employee = employeeRepository.findByEmail(email);
			if (employee.hasRole(ProjectNames.ROLE_MANAGER)) {
				map.put("emp", employee);
				managers.add(map);
			} else if (employee.hasRole(ProjectNames.ROLE_USER)) {
				map.put("emp", employee);
				users.add(map);
			}
		}
		// send email to managers
		sendUserMissingTrainingEmails(users);
		// send email to admins
		sendManagersMissingTrainingEmails(managers);
	}

	private void sendManagersMissingTrainingEmails(List<Map<String, Object>> managers) {
		// mine the departments
		Set<String> departments = new HashSet<String>();
		for (Map<String, Object> map : managers) {
			Employee emp = (Employee) map.get("emp");
			map.put("dept", emp.getDepartment().getName());
			departments.add(emp.getDepartment().getName());
		}
		// send message for each department
		for (String departmentName : departments) {
			Iterable<Employee> admins = employeeRepository.findByDepartmentIdAndRoleName(
					departmentRepository.findByName(departmentName).getId(), ProjectNames.ROLE_ADMIN);
			String emailTitle = "Missing training - department: " + departmentName;
			String emailString = "This is the list of currently held qualifications matched against highest available at the moment, please make sure personel is appropriately trained\n";
			for (Map<String, Object> map : managers) {
				// [Employee email, Training name, Current version, Newest version]
				Employee emp = (Employee) map.get("emp");
				if (map.get("dept").equals(departmentName)) {
					emailString += "Manager " + emp.getName() + " - " + emp.getEmployeeNo() + "(" + emp.getEmail()
							+ "):" + map.get("Training name") + " has version " + map.get("Current version") + ","
							+ "version " + map.get("Newest version") + " is advised\n";
				}
			}
			for (Employee admin : admins) {
				try {
					new Mail(admin.getEmail()).sendMail(emailTitle, emailString);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void sendUserMissingTrainingEmails(List<Map<String, Object>> users) {
		// mine the departments
		Set<String> departments = new HashSet<String>();
		for (Map<String, Object> map : users) {
			Employee emp = (Employee) map.get("emp");
			map.put("dept", emp.getDepartment().getName());
			departments.add(emp.getDepartment().getName());
		}
		// send message for each department
		for (String departmentName : departments) {
			Iterable<Employee> managers = employeeRepository.findByDepartmentIdAndRoleName(
					departmentRepository.findByName(departmentName).getId(), ProjectNames.ROLE_MANAGER);
			String emailTitle = "Missing training - department: " + departmentName;
			String emailString = "This is the list of currently held qualifications matched against highest available at the moment, please make sure personell is appropriately trained\n";
			for (Map<String, Object> map : users) {
				// [Employee email, Training name, Current version, Newest version]
				Employee emp = (Employee) map.get("emp");
				if (map.get("dept").equals(departmentName)) {
					emailString += emp.getName() + " - " + emp.getEmployeeNo() + "(" + emp.getEmail() + "):"
							+ map.get("Training name") + " has version " + map.get("Current version") + "," + "version "
							+ map.get("Newest version") + " is advised\n";
				}
			}
			for (Employee manager : managers) {
				try {
					new Mail(manager.getEmail()).sendMail(emailTitle, emailString);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void sendUserOutdatedTrainingEmails(List<Map<String, Object>> users) {
		// mine the departments
		Set<String> departments = new HashSet<String>();
		for (Map<String, Object> map : users) {
			Employee emp = (Employee) map.get("emp");
			map.put("dept", emp.getDepartment().getName());
			departments.add(emp.getDepartment().getName());
		}
		// send message for each department
		for (String departmentName : departments) {
			Iterable<Employee> managers = employeeRepository.findByDepartmentIdAndRoleName(
					departmentRepository.findByName(departmentName).getId(), ProjectNames.ROLE_MANAGER);
			String emailTitle = "Outdated training - department: " + departmentName;
			String emailString = "This is the list of currently held qualifications that are out of date, please make sure personel is appropriately trained\n";
			for (Map<String, Object> map : users) {
				// [id, day, Employee email, Training Name, Latest version, Training passed]
				Employee emp = (Employee) map.get("emp");
				Training training = trainingRecordRepository.findOne((Long) map.get("id")).getTraining();
				if (map.get("dept").equals(departmentName)) {
					emailString += emp.getName() + " - " + emp.getEmployeeNo() + "(" + emp.getEmail() + "):"
							+ map.get("Training name") + " was passed " + map.get("Training passed")
							+ " and it's duration is " + training.getDuration() + " years\n";
				}
			}
			for (Employee manager : managers) {
				try {
					new Mail(manager.getEmail()).sendMail(emailTitle, emailString);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void sendManagerOutdatedTrainingEmails(List<Map<String, Object>> managers) {
		// mine the departments
		Set<String> departments = new HashSet<String>();
		for (Map<String, Object> map : managers) {
			Employee emp = (Employee) map.get("emp");
			map.put("dept", emp.getDepartment().getName());
			departments.add(emp.getDepartment().getName());
		}
		// send message for each department
		for (String departmentName : departments) {
			Iterable<Employee> admins = employeeRepository.findByDepartmentIdAndRoleName(
					departmentRepository.findByName(departmentName).getId(), ProjectNames.ROLE_ADMIN);
			String emailTitle = "Outdated training - managers department: " + departmentName;
			String emailString = "This is the list of currently held qualifications that are out of date, please make sure personel is appropriately trained\n";
			for (Map<String, Object> map : managers) {
				// [id, day, Employee email, Training Name, Latest version, Training passed]
				Employee emp = (Employee) map.get("emp");
				Training training = trainingRecordRepository.findOne((Long) map.get("id")).getTraining();
				if (map.get("dept").equals(departmentName)) {
					emailString += "Manager " + emp.getName() + " - " + emp.getEmployeeNo() + "(" + emp.getEmail()
							+ "):" + map.get("Training name") + " was passed " + map.get("Training passed")
							+ " and it's duration is " + training.getDuration() + " years\n";
				}
			}
			for (Employee manager : admins) {
				try {
					new Mail(manager.getEmail()).sendMail(emailTitle, emailString);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
	@Scheduled(fixedRate = 60 * 1000)
	// @Scheduled(cron = "0 22 2 * * *") // 2:22 am
	public void dailyOutdatedUserTrainingReport() {
		List<Map<String, Object>> queryForList = null;
		String query = ReportController.outdatedTrainingRecordsQuery;
		query = new QueryTokenizer().deTokenize(query);
		queryForList = jdbcTemplate.queryForList(query);
		List<Map<String, Object>> managers = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
		// mine the data
		for (Map<String, Object> map : queryForList) {
			// [id, day, Employee email, Training Name, Latest version, Training passed]
			String email = (String) map.get("Employee email");
			Employee employee = employeeRepository.findByEmail(email);
			if (employee.hasRole(ProjectNames.ROLE_MANAGER)) {
				map.put("emp", employee);
				managers.add(map);
			} else if (employee.hasRole(ProjectNames.ROLE_USER)) {
				map.put("emp", employee);
				users.add(map);
			}
		}
		// send email to managers
		sendUserOutdatedTrainingEmails(users);
		// // send email to admins
		sendManagerOutdatedTrainingEmails(managers);
	}
}
