package dis;

import java.util.Date;

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

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dep INNER JOIN emp.roles as role WHERE role.name= ?3 AND hol.day= ?2 AND hol.activatedBy is not null AND dep.id = ?1 order by emp.name asc,hol.day asc,hol.type asc")
	Iterable<Holiday> findAllAcceptedByDepartmentAndDayAndRoleNameByDayAsc(long departmentId, Date day,
			String roleName);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dep WHERE hol.day= ?2 AND hol.activatedBy is not null AND dep.id = ?1 order by emp.name asc,hol.day asc,hol.type asc")
	Iterable<Holiday> findAllAcceptedByDepartmentAndDayByDayAsc(long departmentId, Date day);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dep WHERE dep.id = ?1 order by emp.name asc,hol.day asc")
	Iterable<Holiday> findAllByDepartmentByDayAsc(long departmentId);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dep WHERE dep.id = ?1 AND emp.id != ?2  order by emp.name asc,hol.day asc")
	Iterable<Holiday> findAllByDepartmentByDayAscExcludingEmployee(long departmentId, long employeeId);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.manager as man WHERE man.id = ?1 order by emp.name asc, hol.day asc")
	Iterable<Holiday> findAllWhereEmployeeIsManager(long id);

	Iterable<Holiday> findAllByOrderByEmployeeNameAscDayAsc();

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.manager as man WHERE hol.activatedAt is null AND man.id = ?1 order by emp.name asc, hol.day asc")
	Iterable<Holiday> findNotRequestedWhereEmployeeIsManager(long id);

	@Query("SELECT hol FROM Holiday AS hol INNER JOIN hol.employee AS emp INNER JOIN emp.department as dept WHERE hol.activatedAt is null AND dept.id = ?1 order by emp.name asc, hol.day asc")
	Iterable<Holiday> findByDepartmentWhereActivatedAtIsNullByDayAsc(long id);

}