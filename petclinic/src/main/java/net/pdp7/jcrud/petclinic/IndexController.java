package net.pdp7.jcrud.petclinic;

import net.pdp7.jcrud.MasterDetailController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Controller
public class IndexController {

	protected MasterDetailController[] crudControllers;

	public IndexController(MasterDetailController[] crudControllers) {
		this.crudControllers = crudControllers;
	}

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("index", new ImmutableMap.Builder<String,Object>()
				.put("crud_controllers", crudControllers)
				.build());
	}
}
