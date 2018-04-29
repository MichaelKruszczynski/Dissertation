package dis;

public class ManagersSummaryEntry {

	private Department department;
	private double standardHolidayHoursAvailablePerWeek;
	private double AvailableHolidayHoursThisWeek;

	public Department getDepartmentName() {
		return department;
	}

	public void setDepartmentName(Department department) {
		this.department = department;
	}

	public double getStandardHolidayHoursAvailablePerWeek() {
		return standardHolidayHoursAvailablePerWeek;
	}

	public void setStandardHolidayHoursAvailablePerWeek(double standardHolidayHoursAvailablePerWeek) {
		this.standardHolidayHoursAvailablePerWeek = standardHolidayHoursAvailablePerWeek;
	}

	public double getAvailableHolidayHoursThisWeek() {
		return AvailableHolidayHoursThisWeek;
	}

	public void setAvailableHolidayHoursThisWeek(double availableHolidayHoursThisWeek) {
		AvailableHolidayHoursThisWeek = availableHolidayHoursThisWeek;
	}

}
