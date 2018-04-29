package dis;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Holiday {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Timestamp day;
	private boolean halfDay;
	@ManyToOne
	private Employee employee;

}
