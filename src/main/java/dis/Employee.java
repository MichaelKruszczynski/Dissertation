package dis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	@Size(min = 1, message = "Please input employee name")
	private String name;

	private EmployeeType type;

	@NotNull
	@Range(min = 1, message = "Please input employee number")
	private int employeeNo;

	@NotNull
	@Range(min = 1, message = "Please input total annual holiday")
	private int totalAnnualHolidayDays;
	// private int bookedFullDays;
	// private int bookedHalfDays;
	// private double daysAvailableToBook;

	@NotNull
	@ManyToOne
	private Department department;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmployeeNo(int employeeNo) {
		this.employeeNo = employeeNo;
	}

	public int getEmployeeNo() {
		return employeeNo;
	}

	public void setType(EmployeeType type) {
		this.type = type;
	}

	public EmployeeType getType() {
		return type;
	}

	public int getTotalAnnualHolidayDays() {
		return totalAnnualHolidayDays;
	}

	public void setTotalAnnualHolidayDays(int totalAnnualHolidayDays) {
		this.totalAnnualHolidayDays = totalAnnualHolidayDays;
	}

	// public int getBookedFullDays() {
	// return bookedFullDays;
	// }
	//
	// public void setBookedFullDays(int bookedFullDays) {
	// this.bookedFullDays = bookedFullDays;
	// }
	//
	// public int getBookedHalfDays() {
	// return bookedHalfDays;
	// }
	//
	// public void setBookedHalfDays(int bookedHalfDays) {
	// this.bookedHalfDays = bookedHalfDays;
	// }
	//
	// public double getDaysAvailableToBook() {
	// return daysAvailableToBook;
	// }
	//
	// public void setDaysAvailableToBook(int daysAvailableToBook) {
	// this.daysAvailableToBook = daysAvailableToBook;
	// }

	public long getId() {
		return id;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

}
