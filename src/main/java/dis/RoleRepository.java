package dis;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called AnswerRepository
 * CRUD refers Create, Read, Update, Delete
 * 
 */

public interface RoleRepository extends CrudRepository<Role, Long> {

	Role findByName(String name);

}