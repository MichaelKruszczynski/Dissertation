package dis;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called
 * DepartmentRepository CRUD refers Create, Read, Update, Delete
 * 
 */

public interface TrainingRecordRepository extends CrudRepository<TrainingRecord, Long> {

	@Query("select tr from TrainingRecord tr inner join tr.training train " + "where train.version=max(train.version) "
			+ "group by train.name,tr.day,tr.employee.id")
	Iterable<TrainingRecord> findAllByLatestVersion();

	Iterable<TrainingRecord> findAllByOrderByEmployeeNameAsc();

}