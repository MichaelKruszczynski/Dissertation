package dis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

@Entity
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	@Range(min = 1, message = "Please input total hours available")
	private int totalHoursAvailable;

	@NotNull
	@Size(min = 1, message = "Please input department name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public int getTotalHoursAvailable() {
		return totalHoursAvailable;
	}

	public void setTotalHoursAvailable(int totalHoursAvailable) {
		this.totalHoursAvailable = totalHoursAvailable;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
