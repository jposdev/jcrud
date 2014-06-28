package net.pdp7.jcrud;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumn;
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

		items.forEach(item -> item.put("edit_uri", editFormUri(item)));
		
		return new ModelAndView("list", new ImmutableMap.Builder<String, Object>()
				.put("items", items)
				.put("columns", listColumns())
				.put("add_form", addFormUri())
				.build());
	}

	public UriComponents listUri() {
		return MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).list()).build();
	}

	protected List<IndexColumn> primaryKeyColumns() {
		return table.getPrimaryKey().getColumns();
	}

	protected List<Column> editableColumns() {
		return nonAutoincrementedColumns();
	}
	
	public ModelAndView addForm() {
		return changeForm(new HashMap<String, Object>(), addUri());
	}

	public ModelAndView editForm(WebRequest request) {
		List<String> primaryKeyColumnNames = primaryKeyColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
		Map<String, Object> primaryKeys = primaryKeyColumnNames.stream().collect(Collectors.toMap(cn -> cn, cn -> request.getParameter(cn)));
		String whereCondition = primaryKeyColumnNames.stream().map(cn -> cn + " = :" + cn).collect(Collectors.joining(" AND "));
		String itemQuery = "select * from " + table.getName() + " where " + whereCondition;
		Map<String, Object> item = jdbcTemplate.queryForMap(itemQuery, primaryKeys);
		return changeForm(item, editUri(item));
	}

	protected UriComponents editFormUri(Map<String, Object> item) {
		List<String> primaryKeyColumnNames = primaryKeyColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
		Map<String, String> primaryKeys = primaryKeyColumnNames.stream().collect(Collectors.toMap(cn -> cn, cn -> item.get(cn).toString()));
		UriComponentsBuilder editForm = MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).editForm(null));
		editForm.queryParams(convertMapToMultiMap(primaryKeys));
		return editForm.build();
	}

	public View edit(WebRequest request) {
		List<String> primaryKeyColumnNames = primaryKeyColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
		Map<String, Object> primaryKeys = primaryKeyColumnNames.stream().collect(Collectors.toMap(cn -> cn, cn -> request.getParameter(cn)));
		String whereCondition = primaryKeyColumnNames.stream().map(cn -> cn + " = :" + cn).collect(Collectors.joining(" AND "));

		String set = editableColumns().stream().map(c -> c.getName() + "=:" + c.getName()).collect(Collectors.joining(","));
		Map<String, Object> updateKeys = editableColumns().stream().collect(Collectors.toMap(c -> c.getName(), c -> request.getParameter(c.getName())));

		Map<String, Object> params = new HashMap<>(primaryKeys);
		params.putAll(updateKeys);

		jdbcTemplate.update("update " + table.getName() + " set " + set + " where " + whereCondition, params);
		return new RedirectView(listUri().toUriString());
	}

	protected UriComponents editUri(Map<String, Object> item) {
		List<String> primaryKeyColumnNames = primaryKeyColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
		Map<String, String> primaryKeys = primaryKeyColumnNames.stream().collect(Collectors.toMap(cn -> cn, cn -> item.get(cn).toString()));
		UriComponentsBuilder editForm = MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).edit(null));
		editForm.queryParams(convertMapToMultiMap(primaryKeys));
		return editForm.build();
	}

	protected <K,V> MultiValueMap<K, V> convertMapToMultiMap(Map<K, V> primaryKeys) {
		return new LinkedMultiValueMap<K,V>(primaryKeys.entrySet().stream().collect(Collectors.<Map.Entry<K, V>, K,List<V>>toMap(e -> e.getKey(), e -> Collections.singletonList(e.getValue()))));
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

	protected ModelAndView changeForm(Map<String, Object> item, UriComponents changeSaveUri) {
		return new ModelAndView("change", new ImmutableMap.Builder<String, Object>()
				.put("item", item)
				.put("columns", editableColumns())
				.put("change_save", changeSaveUri)
				.put("list", listUri())
				.build());
	}
}
