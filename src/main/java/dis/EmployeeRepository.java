package dis;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called
 * DepartmentRepository CRUD refers Create, Read, Update, Delete
 * 
 */

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

	Employee findByEmail(String string);

	Iterable<Employee> findByDepartmentId(long l);

	@Query("SELECT emp FROM Employee emp INNER JOIN emp.department as dep inner join emp.roles as role WHERE dep.id = ?1 and role.name = ?2")
	Iterable<Employee> findByDepartmentIdAndRoleName(long l, String string);

	@Query("SELECT emp FROM Employee emp inner join emp.roles as role WHERE role.name = '" + ProjectNames.ROLE_MANAGER
			+ "' or role.name = '" + ProjectNames.ROLE_ADMIN + "'")
	Iterable<Employee> findAllManagers();
}
