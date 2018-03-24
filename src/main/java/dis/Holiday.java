package dis;

import java.sql.Timestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Holiday {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Timestamp day;
	private boolean halfDay;
	private Employee employee;

}
