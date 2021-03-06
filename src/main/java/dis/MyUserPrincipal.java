package dis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUserPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L;
	private Employee employee;

	public MyUserPrincipal(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String getPassword() {
		return employee.getPassword();
	}

	@Override
	public String getUsername() {
		return employee.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return isCredentialsNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !employee.isTokenExpired();
	}

	@Override
	public boolean isEnabled() {
		return employee.isEnabled();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getAuthorities(employee.getRoles());
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(Collection<Role> roles) {
		List<String> privileges = new ArrayList<>();
		// List<Privilege> collection = new ArrayList<>();
		// for (Role role : roles) {
		// collection.addAll(role.getPrivileges());
		// }
		// for (Privilege item : collection) {
		// privileges.add(item.getName());
		// }
		for (Role item : roles) {
			privileges.add(item.getName());
		}
		return privileges;
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String privilege : privileges) {
			authorities.add(new SimpleGrantedAuthority(privilege));
		}
		return authorities;

	}

	public Employee getEmployee() {

		return employee;

	}

}