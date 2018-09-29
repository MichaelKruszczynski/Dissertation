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
	private DepartmentRepository departmentRepository;

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
		createRoleIfNotFound(ProjectNames.ROLE_ADMIN, adminPrivileges);
		createRoleIfNotFound(ProjectNames.ROLE_USER, Arrays.asList(readPrivilege));
		String departmentName = "deptName";
		Department department = departmentRepository.findByDepartmentName(departmentName);
		if (department == null) {

			department = new Department();
			department.setDepartmentName(departmentName);
			department.setTotalHoursAvailable(40);
			departmentRepository.save(department);
		}
		Role adminRole = roleRepository.findByName("ROLE_ADMIN");
		Employee findByEmail = employeeRepository.findByEmail("test@test.com");
		if (findByEmail == null) {
			Employee employee = new Employee();
			employee.setName("Test");
			employee.setPassword(passwordEncoder.encode("test"));
			employee.setEmail("test@test.com");
			employee.setRoles(Arrays.asList(adminRole));
			employee.setEnabled(true);
			employee.setDepartment(department);
			employee.setTotalAnnualHolidayDays(686);
			employee.setEmployeeNo(665);
			employeeRepository.save(employee);
			// userRepository.saveOrUpdate(user);
		}
		createReportIfNotExists("test", ProjectNames.ROLE_ADMIN, "select * from Employee emp");

		createReportIfNotExists("test2", ProjectNames.ROLE_ADMIN, "select emp.name from Employee emp");

		createReportIfNotExists("test3", ProjectNames.ROLE_ADMIN, "select emp.name from Employee emp where emp.id=?");

		createReportIfNotExists("test4", ProjectNames.ROLE_ADMIN,
				"select emp.name from Employee emp where emp.id=? and emp.name=?");

		createReportIfNotExists("test5", ProjectNames.ROLE_ADMIN,
				"select emp.name, emp.total_Annual_Holiday_Days from Employee emp where emp.id=4");

		createReportIfNotExists("test6", ProjectNames.ROLE_ADMIN, "SELECT CURRENT_USER, emp.name from Employee emp");

		createReportIfNotExists("test7", ProjectNames.ROLE_ADMIN, "select hol.day from Holiday hol");

		createReportIfNotExists("Tk", ProjectNames.ROLE_ADMIN, "select emp.* from Employee emp where name= :myName");
		createReportIfNotExists("Tk2", ProjectNames.ROLE_ADMIN,
				"select emp.* from Employee emp where name= :myName or id=?");

		createReportIfNotExists("Tk3", ProjectNames.ROLE_ADMIN,
				"select * from Holiday hol join Employee emp on emp.id=hol.employee_id where emp.name= :myName or emp.id=?");

		createReportIfNotExists("myholidays", ProjectNames.ROLE_ADMIN,
				"select emp.name, coalesce(emp.total_annual_holiday_days, 0 ) 'Annual Entitlement' , sum(if(hol.type=0,1,0.5)) 'Holidays Taken', coalesce(emp.total_annual_holiday_days, 0 ) - sum(if(hol.type=0,1,0.5)) 'Remaining holidays'  from employee emp left join holiday hol on hol.employee_id=emp.id where hol.activated_at is not null and emp.id=:myId group by emp.id;");

		alreadySetup = true;
	}

	public void createReportIfNotExists(String name, String accesslevel, String query) {
		Report fourthReport = reportRepository.findByName(name);
		if (fourthReport == null) {
			fourthReport = new Report();
			fourthReport.setName(name);
			fourthReport.setQuery(query);
			fourthReport.setRole(roleRepository.findByName(accesslevel));
			fourthReport.setColumnNames("Not yet developed");
			reportRepository.save(fourthReport);
		}
	}

}