package dis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	public int getTotalHoursAvailable() {
		return totalHoursAvailable;
	}

	public void setTotalHoursAvailable(int totalHoursAvailable) {
		this.totalHoursAvailable = totalHoursAvailable;
	}

	@NotEmpty
	@Size(min = 1, message = "Department name is required.")
	private String departmentName;

	private int totalHoursAvailable;

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Department [id=" + id + ", departmentName=" + departmentName + "]";

	}

}
