package dis;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
// This means that this class is a Controller
// This means URL's start with /demo (after Application path)
	@RequestMapping(path = "/")
public class MainController {
}
