package dis;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// This means that this class is a Controller
@RequestMapping(path = "/user")

public class UserController {
	@Autowired
	// This means to get the bean called userRepository
	// which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;

	@RequestMapping
	public String main() {
		return "user";
	}

	@GetMapping(path = "/add")
	public String createForm(Model model) {
		model.addAttribute("user", "userAdd");
		model.addAttribute(new User());
		return "userAdd";
	}

	@PostMapping(path = "/add")
	public String createSubmit(@ModelAttribute @Valid User user, Errors errors, Model model) {

		if (errors.hasErrors()) {

			return "userAdd";
		}

		userRepository.save(user);
		return "userAddResult";
	}

	@GetMapping(path = "/all")
	public String readAll(Model model) {
		Iterable<User> findAll = userRepository.findAll();
		model.addAttribute("users", findAll);
		// this returns JSON or XML with the users
		// return departmentRepository.findAll();
		return "userAll";
	}

	@GetMapping(path = "/{id}")
	public String readById(@PathVariable("id") long id, Model model) {
		model.addAttribute("user", userRepository.findOne(id));
		// this returns JSON or XML with the department
		return "userById";

	}

	// @ TODO
	@GetMapping(path = "/{id}/edit")
	public String createEdit(@PathVariable("id") long id, Model model) {
		// with input provided
		model.addAttribute("user", userRepository.findOne(id));
		// this returns JSON or XML with the department
		return "userEdit";

	}

	@PostMapping(path = "/{id}/edit", params = "edit=Delete")
	public String createEditPostDelete(@PathVariable("id") long id, Model model) {
		User findOne = userRepository.findOne(id);
		userRepository.delete(findOne);
		return readAll(model);
	}

	@PostMapping(path = "/{id}/edit", params = "edit=Save")
	public String editForm(@PathVariable("id") long id, @ModelAttribute @Valid User user, Errors errors, Model model) {
		if (errors.hasErrors()) {

			return "userEdit";
		}
		// doin't create new object here, read old one by id and update its properties
		User dbUser = userRepository.findOne(id);
		// update the properties with values comming from model
		dbUser.setFirstName(user.getFirstName());
		dbUser.setLastName(user.getLastName());
		dbUser.setEmail(user.getEmail());
		dbUser.setPassword(user.getPassword());
		// then save(update) to database
		userRepository.save(dbUser);
		return readAll(model); // and choose template to kick in afterwards
		// and then add another method that also catches the action of deleting and
		// removes instead
		// keep in mind that you may be forced to change this one to catch the action
		// type "submit" or "save" or something like this

	}

	@RequestMapping(path = "/delete/{id}")
	public String createEdit(@ModelAttribute User user, Model model) {
		User findOne = userRepository.findOne(user.getId());
		userRepository.delete(findOne);

		return readAll(model);
	}
}