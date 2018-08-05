package dis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	private EmployeeType type;
	private int employeeNo;
	private int totalAnnualHolidayDays;
	private int bookedFullDays;
	private int bookedHalfDays;
	private double daysAvailableToBook;
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

	public int getBookedFullDays() {
		return bookedFullDays;
	}

	public void setBookedFullDays(int bookedFullDays) {
		this.bookedFullDays = bookedFullDays;
	}

	public int getBookedHalfDays() {
		return bookedHalfDays;
	}

	public void setBookedHalfDays(int bookedHalfDays) {
		this.bookedHalfDays = bookedHalfDays;
	}

	public double getDaysAvailableToBook() {
		return daysAvailableToBook;
	}

	public void setDaysAvailableToBook(int daysAvailableToBook) {
		this.daysAvailableToBook = daysAvailableToBook;
	}

	public long getId() {
		return id;
	}

}
