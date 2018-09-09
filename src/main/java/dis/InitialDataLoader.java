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
	private UserRepository userRepository;

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
		User findByEmail = userRepository.findByEmail("test@test.com");
		if (findByEmail == null) {
			User user = new User();
			user.setFirstName("Test");
			user.setLastName("Test");
			user.setPassword(passwordEncoder.encode("test"));
			user.setEmail("test@test.com");
			user.setRoles(Arrays.asList(adminRole));
			user.setEnabled(true);
			userRepository.save(user);
			// userRepository.saveOrUpdate(user);
		}
		String firstName = "test";
		Report firstReport = reportRepository.findByName(firstName);
		if (firstReport == null) {
			firstReport = new Report();
			firstReport.setName(firstName);
			// firstReport.setQuery("Select user.firstName, user.LastName from User user
			// where user.id=:id");
			// firstReport.setQuery("select usr.firstName, usr.lastName from User usr");
			firstReport.setQuery("select * from User usr");
			firstReport.setAccessLevel(AccessLevel.ADMIN);
			firstReport.setColumnNames("First name, Second Name");
			reportRepository.save(firstReport);
		}
		String secondName = "test2";
		Report secondReport = reportRepository.findByName(secondName);
		if (secondReport == null) {
			secondReport = new Report();
			secondReport.setName(secondName);
			// firstReport.setQuery("Select user.firstName, user.LastName from User user
			// where user.id=:id");
			secondReport.setQuery("select usr.first_name, usr.last_name from User usr");
			secondReport.setAccessLevel(AccessLevel.ADMIN);
			secondReport.setColumnNames("First name, Second Name");
			reportRepository.save(secondReport);
		}
		String thirdName = "test3";
		Report thirdReport = reportRepository.findByName(thirdName);
		if (thirdReport == null) {
			thirdReport = new Report();
			thirdReport.setName(thirdName);
			thirdReport.setQuery("select usr.first_name, usr.last_name from User usr where usr.id=?");
			thirdReport.setAccessLevel(AccessLevel.ADMIN);
			thirdReport.setColumnNames("First name, Second Name");
			reportRepository.save(thirdReport);
		}
		String fourthName = "test4";
		Report fourthReport = reportRepository.findByName(fourthName);
		if (fourthReport == null) {
			fourthReport = new Report();
			fourthReport.setName(fourthName);
			fourthReport
					.setQuery("select usr.first_name, usr.last_name from User usr where usr.id=? and usr.first_name=?");
			fourthReport.setAccessLevel(AccessLevel.ADMIN);
			fourthReport.setColumnNames("First name, Second Name");
			reportRepository.save(fourthReport);
		}

		alreadySetup = true;
	}

}