package net.pdp7.jcrud;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;

public class TableService {

	protected final Table table;
	protected final NamedParameterJdbcTemplate jdbcTemplate;

	public TableService(Table table, NamedParameterJdbcTemplate jdbcTemplate) {
		this.table = table;
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Map<String, Object>> listItems() {
		return jdbcTemplate.queryForList("select * from " + table.getName(), Collections.emptyMap());
	}

	public List<Map<String, Object>> listItems(Map<String, Object> conditions) {
		String itemQuery = "select * from " + table.getName() + " where " + conditionWhereKeys(conditions);
		return jdbcTemplate.queryForList(itemQuery, conditions);
	}

	public Map<String, Object> getItem(Map<String, Object> primaryKeys) {
		String itemQuery = "select * from " + table.getName() + " where " + conditionWherePrimaryKeys();
		return jdbcTemplate.queryForMap(itemQuery, primaryKeys);
	}

	public void insertItem(Map<String, Object> insertKeys) {
		String insertColumnsString = editableColumnNames().collect(Collectors.joining(","));
		String valueColumns = editableColumnNames().map(c -> ":" + c).collect(Collectors.joining(","));
		String query = "insert into " + table.getName() + "(" + insertColumnsString + ") values (" + valueColumns + ")";
		jdbcTemplate.update(query, insertKeys);
	}

	// FIXME: V of primaryKeys being ? extends Object is a hack
	public void updateItem(Map<String, ? extends Object> primaryKeys, Map<String, Object> updateKeys) {
		String sets = editableColumnNames()
				.map(cn -> cn + "=:" + cn)
				.collect(Collectors.joining(","));

		Map<String, Object> params = new HashMap<>(primaryKeys);
		params.putAll(updateKeys);

		jdbcTemplate.update("update " + table.getName() + " set " + sets + " where " + conditionWherePrimaryKeys(), params);
	}

	protected String conditionWherePrimaryKeys() {
		return primaryKeyColumnNames()
				.map(cn -> cn + " = :" + cn)
				.collect(Collectors.joining(" and "));
	}

	protected String conditionWhereKeys(Map<String, Object> conditions) {
		return conditions.keySet().stream()
				.map(cn -> cn + " = :" + cn)
				.collect(Collectors.joining(" and "));
	}
	
	public Map<String, String> getPrimaryKeysFromItem(Map<String, Object> item) {
		return primaryKeyColumnNames().collect(Collectors.toMap(cn -> cn, cn -> item.get(cn).toString()));
	}

	/**
	 * @return a string representation of an item, override to change it
	 */
	public String getItemAsString(Map<String, Object> item) {
		return primaryKeyColumnNames().map(cn -> item.get(cn).toString()).collect(Collectors.joining(" "));
	}
	
	public Table getTable() {
		return table;
	}

	protected Stream<Column> listColumns() {
		return nonAutoincrementedColumns();
	}

	protected Stream<Column> nonAutoincrementedColumns() {
		return table.getColumns().stream().filter(c -> !c.isAutoIncremented());
	}

	protected List<IndexColumn> primaryKeyColumns() {
		return table.getPrimaryKey().getColumns();
	}

	protected Stream<String> primaryKeyColumnNames() {
		return primaryKeyColumns().stream().map(IndexColumn::getName);
	}

	protected Stream<Column> editableColumns() {
		return nonAutoincrementedColumns();
	}

	protected Stream<String> editableColumnNames() {
		return editableColumns().map(Column::getName);
	}
}
