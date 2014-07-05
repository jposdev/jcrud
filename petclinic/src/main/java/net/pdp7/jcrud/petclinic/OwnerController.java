package net.pdp7.jcrud.petclinic;

import net.pdp7.jcrud.TableController;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import schemacrawler.schema.Table;

@Controller
@RequestMapping("/owners")
public class OwnerController extends TableController {

	public OwnerController(Table table, NamedParameterJdbcTemplate jdbcTemplate) {
		super(table, jdbcTemplate);
	}
	
	@Override
	@RequestMapping("/list")
	public ModelAndView list() {
		return super.list();
	}

	@Override
	@RequestMapping("/add_form")
	public ModelAndView addForm() {
		return super.addForm();
	}

	@Override
	@RequestMapping("/add")
	public View add(WebRequest request) {
		return super.add(request);
	}

	@Override
	@RequestMapping("/edit_form")
	public ModelAndView editForm(WebRequest request) {
		return super.editForm(request);
	}

	@Override
	@RequestMapping("/edit")
	public View edit(WebRequest request) {
		return super.edit(request);
	}
}
