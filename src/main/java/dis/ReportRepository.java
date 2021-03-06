package dis;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called
 * DepartmentRepository CRUD refers Create, Read, Update, Delete
 * 
 */

public interface ReportRepository extends CrudRepository<Report, Long> {
	Report findByName(String string);
}
