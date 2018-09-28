package dis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// @Order(ManagementServerProperties.BASIC_AUTH_ORDER + 1)
// @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
// @Order(SecurityProperties.IGNORED_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private MyUserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
		// auth.build();
	}

	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// // TODO Auto-generated method stub
	//// super.configure(http);
	//// http.anyMatchers("/").hasAnyAuthority("ROLE_USER").anyRequest().authenticated();
	// // .accessDecisionManager(accessDecisionManager());
	// }

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(getEncoder());
		return authProvider;

	}

	@Bean
	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder(11);
	}

}