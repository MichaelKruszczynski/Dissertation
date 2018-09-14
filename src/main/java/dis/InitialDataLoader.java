package dis;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
// public class InitialDataLoader implements
// ApplicationListener<ContextRefreshedEvent> {
public class InitialDataLoader implements ApplicationListener<ApplicationReadyEvent> {
	boolean alreadySetup = false;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PrivilegeRepository privilegeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ReportRepository reportRepository;

	@Transactional
	private Privilege createPrivilegeIfNotFound(String name) {

		Privilege privilege = privilegeRepository.findByName(name);
		if (privilege == null) {
			privilege = new Privilege(name);
			privilegeRepository.save(privilege);
		}
		return privilege;
	}

	@Transactional
	private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = new Role(name);
			role.setPrivileges(privileges);
			roleRepository.save(role);
		}
		return role;
	}

	@Transactional
	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {

		if (alreadySetup)
			return;
		Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
		Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

		List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
		createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
		createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

		Role adminRole = roleRepository.findByName("ROLE_ADMIN");
		Employee findByEmail = employeeRepository.findByEmail("test@test.com");
		if (findByEmail == null) {
			Employee employee = new Employee();
			employee.setName("Test");
			employee.setPassword(passwordEncoder.encode("test"));
			employee.setEmail("test@test.com");
			employee.setRoles(Arrays.asList(adminRole));
			employee.setEnabled(true);
			employeeRepository.save(employee);
			// userRepository.saveOrUpdate(user);
		}
		createReportIfNotExists("test", AccessLevel.ADMIN, "select * from Employee emp");

		createReportIfNotExists("test2", AccessLevel.ADMIN, "select emp.name from Employee emp");

		createReportIfNotExists("test3", AccessLevel.ADMIN,
				"select usr.first_name, usr.last_name from User usr where usr.id=?");

		createReportIfNotExists("test4", AccessLevel.ADMIN,
				"select usr.first_name, usr.last_name from User usr where usr.id=? and usr.first_name=?");

		createReportIfNotExists("test5", AccessLevel.ADMIN,
				"select emp.name, emp.total_Annual_Holiday_Days from Employee emp where emp.id=4");

		createReportIfNotExists("test6", AccessLevel.ADMIN, "SELECT CURRENT_USER, emp.name from Employee emp");

		createReportIfNotExists("test7", AccessLevel.ADMIN, "select hol.day from Holiday hol");

		alreadySetup = true;
	}

	public void createReportIfNotExists(String name, AccessLevel accesslevel, String query) {
		Report fourthReport = reportRepository.findByName(name);
		if (fourthReport == null) {
			fourthReport = new Report();
			fourthReport.setName(name);
			fourthReport.setQuery(query);
			fourthReport.setAccessLevel(accesslevel);
			fourthReport.setColumnNames("Not yet developed");
			reportRepository.save(fourthReport);
		}
	}

}