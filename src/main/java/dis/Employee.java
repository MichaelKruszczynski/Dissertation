package dis;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
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
	private AccessLevel accessLevel;

	@Email
	@Size(min = 1, message = "Please input email address")
	private String email;
	@NotNull
	@Size(min = 6, message = "Please input password")
	private String password;

	private boolean enabled;
	private boolean tokenExpired;

	// @ManyToMany(fetch = FetchType.EAGER, mappedBy = "topic", cascade =
	// CascadeType.ALL)
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	// @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Collection<Role> roles;

	@NotNull
	@Range(min = 1, message = "Please input employee number")
	private int employeeNo;

	@NotNull
	@Range(min = 1, message = "Please input total annual holiday")
	private int totalAnnualHolidayDays;
	// private int bookedFullDays;
	// private int bookedHalfDays;
	// private double daysAvailableToBook;

	// @NotNull
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTokenExpired() {
		return tokenExpired;
	}

	public void setTokenExpired(boolean tokenExpired) {
		this.tokenExpired = tokenExpired;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public boolean hasRole(Role role) {
		return true;
	}

	public boolean hasRole(Role[] role) {
		return true;
	}

	public boolean hasRole(List<Role> role) {
		return true;
	}

	public boolean hasRole(Collection<Role> role) {
		return true;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

}
