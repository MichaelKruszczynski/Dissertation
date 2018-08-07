package dis;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Holiday {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Temporal(TemporalType.DATE)
	private Date day;

	public long getId() {
		return id;
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

	public boolean isHalfDay() {
		return halfDay;
	}

	public void setHalfDay(boolean halfDay) {
		this.halfDay = halfDay;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	private boolean halfDay;
	@ManyToOne
	private Employee employee;

}
