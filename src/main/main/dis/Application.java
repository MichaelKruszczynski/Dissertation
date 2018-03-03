package dis;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring app
 */

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		// SpringApplication application = new
		// SpringApplication(Application.class);
		// Map<String, Object> map = new HashMap<>();
		// map.put("SERVER_PORT", "8585");
		// application.setDefaultProperties(map);
		// application.run(args);
		SpringApplication.run(Application.class, args);
	}
}
