package dis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
	private HolidayRepository holidayRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
		// auth.build();
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
	@Scheduled(cron = "0 24 20 * * *")
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
	@Scheduled(fixedRate = 60 * 1000)
	// @Scheduled(cron = "0 15 10 15 * ?")
	// @Scheduled(cron = "0 19 1 * * *")
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

}