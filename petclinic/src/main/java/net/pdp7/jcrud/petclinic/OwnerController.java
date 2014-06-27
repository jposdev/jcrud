package net.pdp7.jcrud.petclinic;

import net.pdp7.jcrud.DatabaseType;
import net.pdp7.jcrud.MasterDetailController;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import schemacrawler.schema.Table;

@Controller
@RequestMapping("/owners")
public class OwnerController extends MasterDetailController {

	public OwnerController(Table table, JdbcTemplate jdbcTemplate, DatabaseType databaseType) {
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
	public ModelAndView add() {
		return super.add();
	}
}
