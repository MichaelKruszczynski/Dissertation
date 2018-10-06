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
		createRoleIfNotFound(ProjectNames.ROLE_MANAGER, adminPrivileges);
		createRoleIfNotFound(ProjectNames.ROLE_USER, Arrays.asList(readPrivilege));
		String departmentName = "deptName";
		Department department = departmentRepository.findByName(departmentName);
		if (department == null) {

			department = new Department();
			department.setName(departmentName);
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
			employee.setManager(employee);
			employeeRepository.save(employee);
			// userRepository.saveOrUpdate(user);
		}
		createReportIfNotExists("test", ProjectNames.ROLE_ADMIN, "select * from Employee emp");

		createReportIfNotExists("test2", ProjectNames.ROLE_ADMIN, "select emp.name from Employee emp where emp.id=?");

		createReportIfNotExists("test3", ProjectNames.ROLE_ADMIN,
				"select emp.name from Employee emp where emp.id=? and emp.name=?");

		createReportIfNotExists("myholidays", ProjectNames.ROLE_USER,
				"select emp.name, coalesce(emp.total_annual_holiday_days, 0 ) 'Annual Entitlement' , sum(if(hol.type=0,1,0.5)) 'Holidays Taken', coalesce(emp.total_annual_holiday_days, 0 ) - sum(if(hol.type=0,1,0.5)) 'Remaining holidays'  from employee emp left join holiday hol on hol.employee_id=emp.id where hol.activated_at is not null and emp.id=:myId group by emp.id;");
		createReportIfNotExists("holidaysTakenByWeek", ProjectNames.ROLE_MANAGER,
				" select week,count(distinct empid) as 'Employees on Holiday',count(id) as 'Holidays taken',sum(day_value) as 'Days taken',sum(day_value)*7.5 'Hours taken',departmentName,total_hours_available 'Total hours',total_hours_available-(sum(day_value)*7.5) 'Hours remaining' from (select hol.id,hol.type,hol.employee_id empid, hol.day, week(hol.day,5) week,if(hol.type=0,1,0.5) day_value,hol.activated_at,hol.activated_by,dept.name departmentName, dept.total_hours_available from holiday hol inner join employee emp on hol.employee_id=emp.id inner join Department dept on dept.id=emp.department_id where hol.activated_by is not null) z group by week order by week desc;");
		createReportIfNotExists("holidaysTakenByDepartmentByWeek", ProjectNames.ROLE_MANAGER,
				" select week,count(distinct empid) as 'Employees on Holiday',count(id) as 'Holidays taken',sum(day_value) as 'Days taken',sum(day_value)*7.5 'Hours taken',departmentName,total_hours_available 'Total hours',total_hours_available-(sum(day_value)*7.5) 'Hours remaining' from (select hol.id,hol.type,hol.employee_id empid, hol.day, week(hol.day,5) week,if(hol.type=0,1,0.5) day_value,hol.activated_at,hol.activated_by,dept.name departmentName, dept.total_hours_available from holiday hol inner join employee emp on hol.employee_id=emp.id inner join Department dept on dept.id=emp.department_id where dept.name=? and hol.activated_by is not null) z group by week order by week desc;");
		createReportIfNotExists("holidaysTakenByDay", ProjectNames.ROLE_MANAGER,
				" select day,count(distinct empid) as 'Employees on Holiday',count(id) as 'Holidays taken',sum(day_value) as 'Days taken',sum(day_value)*7.5 'Hours taken',departmentName from (select hol.id,hol.type,hol.employee_id empid, hol.day, if(hol.type=0,1,0.5) day_value,dept.name departmentName from holiday hol inner join employee emp on hol.employee_id=emp.id inner join Department dept on dept.id=emp.department_id where hol.activated_by is not null ) z group by day order by day desc");
		createReportIfNotExists("holidaysTakenByDepartmentByDay", ProjectNames.ROLE_MANAGER,
				" select day,count(distinct empid) as 'Employees on Holiday',count(id) as 'Holidays taken',sum(day_value) as 'Days taken',sum(day_value)*7.5 'Hours taken',departmentName from (select hol.id,hol.type,hol.employee_id empid, hol.day, if(hol.type=0,1,0.5) day_value,dept.name departmentName from holiday hol inner join employee emp on hol.employee_id=emp.id inner join Department dept on dept.id=emp.department_id where dept.name=? and hol.activated_by is not null ) z group by day order by day desc");

		createReportIfNotExists("trainingRecords", ProjectNames.ROLE_MANAGER,
				"select tr.id,tr.day,emp.name,dept.name,train.name,train.duration,DATE_ADD(tr.day, INTERVAL train.duration YEAR) 'Expiration date', datediff(DATE_ADD(tr.day, INTERVAL train.duration YEAR), CURDATE()) 'Days until expiration' from training_record tr inner join employee emp on emp.id=tr.employee_id inner join training train on train.id=tr.training_id inner join department dept on dept.id=emp.department_id");

		createReportIfNotExists("trainingRecordsByDepartment", ProjectNames.ROLE_MANAGER,
				"select tr.id,tr.day,emp.name,dept.name,train.name,train.duration,DATE_ADD(tr.day, INTERVAL train.duration YEAR) 'Expiration date', datediff(DATE_ADD(tr.day, INTERVAL train.duration YEAR), CURDATE()) 'Days until expiration' from training_record tr inner join employee emp on emp.id=tr.employee_id inner join training train on train.id=tr.training_id inner join department dept on dept.id=emp.department_id where dept.name = ?");

		createReportIfNotExists("trainingRecordsByEmployee", ProjectNames.ROLE_MANAGER,
				"select tr.id,tr.day,emp.name,dept.name,train.name,train.duration,DATE_ADD(tr.day, INTERVAL train.duration YEAR) 'Expiration date', datediff(DATE_ADD(tr.day, INTERVAL train.duration YEAR), CURDATE()) 'Days until expiration' from training_record tr inner join employee emp on emp.id=tr.employee_id inner join training train on train.id=tr.training_id inner join department dept on dept.id=emp.department_id where emp.id = ?");
		createReportIfNotExists("trainingRecordLatest", ProjectNames.ROLE_MANAGER,
				"select tr.id,tr.day,emp.name 'Employee name',train.name 'Training Name',max(train.version) 'Latest version',tr.day 'Training passed' from training_record tr inner join training train on train.id=tr.training_id inner join Employee emp on emp.id=tr.employee_id group by tr.employee_id,train.name order by train.version asc;");// createReportIfNotExists("trainingRecordLatest",
		createReportIfNotExists("outversionedTrainingRecordsQuery", ProjectNames.ROLE_MANAGER,
				ReportController.outversionedTrainingRecordsQuery); // ProjectNames.ROLE_ADMIN,
		createReportIfNotExists("outdatedTrainingRecords", ProjectNames.ROLE_MANAGER,
				ReportController.outdatedTrainingRecordsQuery); // ProjectNames.ROLE_ADMIN,
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