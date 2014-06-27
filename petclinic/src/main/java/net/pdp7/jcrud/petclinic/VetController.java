package net.pdp7.jcrud.petclinic;

import net.pdp7.jcrud.DatabaseType;
import net.pdp7.jcrud.MasterDetailController;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import schemacrawler.schema.Table;

@Controller
@RequestMapping("/vets")
public class VetController extends MasterDetailController {

	public VetController(Table table, NamedParameterJdbcTemplate jdbcTemplate, DatabaseType databaseType) {
		super(table, jdbcTemplate, databaseType);
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
}
