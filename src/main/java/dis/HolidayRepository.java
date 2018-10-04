package dis;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called
 * DepartmentRepository CRUD refers Create, Read, Update, Delete
 * 
 */

public interface HolidayRepository extends CrudRepository<Holiday, Long> {

	Iterable<Holiday> findAllByEmployeeId(long id);

	Iterable<Holiday> findAllByOrderByDayAsc();

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dep WHERE dep.id = ?1 order by emp.name asc,hol.day asc")
	Iterable<Holiday> findAllByDepartmentByDayAsc(long departmentId);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dep WHERE dep.id = ?1 AND emp.id != ?2  order by emp.name asc,hol.day asc")
	Iterable<Holiday> findAllByDepartmentByDayAscExcludingEmployee(long departmentId, long employeeId);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.manager as man WHERE man.id = ?1 order by emp.name asc, hol.day asc")
	Iterable<Holiday> findAllWhereEmployeeIsManager(long id);

	Iterable<Holiday> findAllByOrderByEmployeeNameAscDayAsc();

}