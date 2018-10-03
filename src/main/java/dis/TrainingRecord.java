package dis;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "training_id", "employee_id", "day" }) })
public class TrainingRecord {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private long id;
	@NotNull(message = "Please provide a training name.")
	@OneToOne
	private Training training;
	@NotNull(message = "Please provide a employee name.")
	@OneToOne
	private Employee employee;
	@Past(message = "Only the past data is valid")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Please provide a date.")
	private Date day;

	public long getId() {
		return id;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
}
