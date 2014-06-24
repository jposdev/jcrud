package net.pdp7.jcrud.petclinic;

import net.pdp7.jcrud.DatabaseType;
import net.pdp7.jcrud.MasterDetailController;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import schemacrawler.schema.Table;

@Controller
@RequestMapping("/visits")
public class VisitController extends MasterDetailController {

	public VisitController(Table table, JdbcTemplate jdbcTemplate, DatabaseType databaseType) {
		super(table, jdbcTemplate, databaseType);
	}
	
	@Override
	@RequestMapping("/list")
	public ModelAndView list() {
		return super.list();
	}

}
