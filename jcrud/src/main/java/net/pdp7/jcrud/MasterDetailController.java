package net.pdp7.jcrud;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

public class MasterDetailController {

	protected final Table table;
	protected final NamedParameterJdbcTemplate jdbcTemplate;
	protected final DatabaseType databaseType;

	public MasterDetailController(Table table, NamedParameterJdbcTemplate jdbcTemplate, DatabaseType databaseType) {
		this.table = table;
		this.jdbcTemplate = jdbcTemplate;
		this.databaseType = databaseType;
	}
	
	public Table getTable() {
		return table;
	}
	
	protected List<Column> listColumns() {
		return nonAutoincrementedColumns();
	}

	protected List<Column> nonAutoincrementedColumns() {
		return table.getColumns().stream().filter(c -> !databaseType.isAutoincrementColumn(c)).collect(Collectors.toList());
	}
	
	public ModelAndView list() {
		List<Map<String, Object>> items = jdbcTemplate.queryForList("select * from " + table.getName(), Collections.emptyMap());
		
		return new ModelAndView("list", new ImmutableMap.Builder<String, Object>()
				.put("items", items)
				.put("columns", listColumns())
				.put("add_form", addFormUri())
				.build());
	}

	public UriComponents listUri() {
		return MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).list()).build();
	}

	protected List<Column> editableColumns() {
		return nonAutoincrementedColumns();
	}
	
	public ModelAndView addForm() {
		return new ModelAndView("change", new ImmutableMap.Builder<String, Object>()
				.put("item", new HashMap<Object, Object>())
				.put("columns", editableColumns())
				.put("change_save", addUri())
				.put("list", listUri())
				.build());
	}

	public UriComponents addFormUri() {
		return MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).addForm()).build();
	}

	public View add(WebRequest request) {
		Map<String, Object> insertColumns = new HashMap<>();
		for(Column column : editableColumns()) {
			String columnName = column.getName();
			String columnValue = request.getParameter(columnName);
			if(columnValue != null) {
				insertColumns.put(columnName, columnValue);
			}
		}
		String insertColumnsString = Joiner.on(',').join(insertColumns.keySet());
		String valueColumns = Joiner.on(',').join(insertColumns.keySet().stream().map(c -> ":" + c).toArray());
		String query = "insert into " + table.getName() + "(" + insertColumnsString + ") values (" + valueColumns + ")";
		jdbcTemplate.update(query, insertColumns);
		return new RedirectView(listUri().toUriString());
	}
	
	public UriComponents addUri() {
		return MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).add(null)).build();
	}

}
