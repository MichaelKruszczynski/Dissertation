package dis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

	private int totalHoursAvailable;

	@NotNull
	@Size(min = 1, message = "Please input department name")
	private String departmentName;

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

	// @Override
	// public String toString() {
	// return "Department(Name: " + this.departmentName + ")";
	//
	// }

}
