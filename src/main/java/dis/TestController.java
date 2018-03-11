package dis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// This means that this class is a Controller
@RequestMapping(path = "/test")
public class TestController {

	@RequestMapping
	public String main() {
		return "test";
	}

}