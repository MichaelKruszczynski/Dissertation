package dis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.PersistenceException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TrainingRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	TrainingRepository trainingRepository;

	@Test
	public void testAdd() {
		// given
		Training train = new Training();
		train.setDuration(2);
		String trainingName = "TestTraining";
		train.setName(trainingName);
		train.setVersion(1);
		entityManager.persist(train);
		entityManager.flush();

		// when
		Training found = trainingRepository.findAllByOrderByName().iterator().next();
		// then
		assertEquals(found.getName(), trainingName); // test add
	}

	@Test
	public void testAddDuplicateName() {
		// given
		Training train = new Training();
		train.setDuration(2);
		String trainingName = "TestTraining";
		train.setName(trainingName);
		train.setVersion(1);
		entityManager.persist(train);
		entityManager.flush();
		train = new Training();
		train.setDuration(22);
		train.setName(trainingName);
		train.setVersion(1);
		try {
			entityManager.persist(train);
			entityManager.flush();
		} catch (PersistenceException pe) {
			return;
		}
		fail("Exception was expected ! Can not duplicate name and version for training");
	}

	@Test
	public void testEdit() {
		// given
		Training train = new Training();
		train.setDuration(2);
		String trainingName = "TestTraining";
		train.setName(trainingName);
		train.setVersion(1);
		entityManager.persist(train);
		entityManager.flush();

		// when
		Training found = trainingRepository.findAllByOrderByName().iterator().next();

		found.setName(train.getName() + trainingName);
		entityManager.persist(train);
		entityManager.flush();
		// then
		found = trainingRepository.findAllByOrderByName().iterator().next();
		assertEquals(found.getName(), trainingName + trainingName);
	}
}