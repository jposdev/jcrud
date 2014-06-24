package net.pdp7.jcrud;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ModelAndView;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import com.google.common.collect.ImmutableMap;

public class MasterDetailController {

	protected final Table table;
	protected final JdbcTemplate jdbcTemplate;
	protected final DatabaseType databaseType;

	public MasterDetailController(Table table, JdbcTemplate jdbcTemplate, DatabaseType databaseType) {
		this.table = table;
		this.jdbcTemplate = jdbcTemplate;
		this.databaseType = databaseType;
	}

	public List<Column> listColumns() {
		return table.getColumns().stream().filter(c -> !databaseType.isAutoincrementColumn(c)).collect(Collectors.toList());
	}
	
	public ModelAndView list() {
		List<Map<String, Object>> items = jdbcTemplate.queryForList("select * from " + table.getName());
		return new ModelAndView("list", new ImmutableMap.Builder<String, Object>()
				.put("items", items)
				.put("columns", listColumns())
				.build());
	}
	
}
