package dis;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class EmployeeTraining {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@OneToOne
	private Employee employee;
	@OneToOne
	private Training training;
	private Timestamp dateTaken;

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public Timestamp getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(Timestamp dateTaken) {
		this.dateTaken = dateTaken;
	}

}
