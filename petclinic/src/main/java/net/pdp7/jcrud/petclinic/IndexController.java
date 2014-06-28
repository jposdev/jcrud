package net.pdp7.jcrud.petclinic;

import net.pdp7.jcrud.TableController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;

@Controller
public class IndexController {

	protected TableController[] crudControllers;

	public IndexController(TableController[] crudControllers) {
		this.crudControllers = crudControllers;
	}

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("index", new ImmutableMap.Builder<String,Object>()
				.put("crud_controllers", crudControllers)
				.build());
	}
}
