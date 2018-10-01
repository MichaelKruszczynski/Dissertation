package dis;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "employee_id", "day", "type" }) })
public class Holiday {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private HolidayType type;
	private Timestamp activatedAt;
	private String activatedBy;
	@Future(message = "Only the future data is valid")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Please provide a date.")
	private Date day;

	@Transient
	// @NotNull(message = "Please provide a date.")
	@Future(message = "Only the future data is valid")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date day2;

	@NotNull(message = "Please assign an employee")
	@ManyToOne
	private Employee employee;

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

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setType(HolidayType type) {
		this.type = type;
	}

	public HolidayType getType() {
		return type;

	}

	public Date getDay2() {
		return day2;
	}

	public void setDay2(Date day2) {
		if (!day2.before(getDay())) {
			this.day2 = day2;
		} else {
			throw new RuntimeException("Not correct date");
		}
	}

	public Timestamp getActivatedAt() {
		return activatedAt;
	}

	public String getActivatedBy() {
		return activatedBy;
	}

	public void setActivatedBy(String userName) {

		this.activatedBy = userName;
		this.activatedAt = new Timestamp(System.currentTimeMillis());

	}

}
