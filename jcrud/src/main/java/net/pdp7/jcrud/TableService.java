package net.pdp7.jcrud;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;

import com.google.common.base.Joiner;

public class TableService {

	protected final Table table;
	protected final NamedParameterJdbcTemplate jdbcTemplate;
	protected final DatabaseType databaseType;

	public TableService(Table table, NamedParameterJdbcTemplate jdbcTemplate, DatabaseType databaseType) {
		this.table = table;
		this.jdbcTemplate = jdbcTemplate;
		this.databaseType = databaseType;
	}

	public List<Map<String, Object>> listItems() {
		return jdbcTemplate.queryForList("select * from " + table.getName(), Collections.emptyMap());
	}

	public Map<String, Object> getItem(Map<String, Object> primaryKeys) {
		String itemQuery = "select * from " + table.getName() + " where " + conditionWherePrimaryKeys();
		return jdbcTemplate.queryForMap(itemQuery, primaryKeys);
	}

	public void insertItem(Map<String, Object> insertKeys) {
		String insertColumnsString = Joiner.on(',').join(insertKeys.keySet());
		String valueColumns = Joiner.on(',').join(insertKeys.keySet().stream().map(c -> ":" + c).toArray());
		String query = "insert into " + table.getName() + "(" + insertColumnsString + ") values (" + valueColumns + ")";
		jdbcTemplate.update(query, insertKeys);
	}

	public void updateItem(Map<String, Object> primaryKeys, Map<String, Object> updateKeys) {
		String sets = editableColumnNames().stream()
				.map(cn -> cn + "=:" + cn)
				.collect(Collectors.joining(","));

		Map<String, Object> params = new HashMap<>(primaryKeys);
		params.putAll(updateKeys);

		jdbcTemplate.update("update " + table.getName() + " set " + sets + " where " + conditionWherePrimaryKeys(), params);
	}

	protected String conditionWherePrimaryKeys() {
		return primaryKeyColumnNames().stream()
				.map(cn -> cn + " = :" + cn)
				.collect(Collectors.joining(" and "));
	}

	protected Map<String, String> getPrimaryKeysFromItem(Map<String, Object> item) {
		return primaryKeyColumnNames().stream().collect(Collectors.toMap(cn -> cn, cn -> item.get(cn).toString()));
	}

	public Table getTable() {
		return table;
	}

	protected List<Column> listColumns() {
		return nonAutoincrementedColumns();
	}

	protected List<Column> nonAutoincrementedColumns() {
		return table.getColumns().stream().filter(c -> !databaseType.isAutoincrementColumn(c))
				.collect(Collectors.toList());
	}

	protected List<IndexColumn> primaryKeyColumns() {
		return table.getPrimaryKey().getColumns();
	}

	protected List<String> primaryKeyColumnNames() {
		return primaryKeyColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
	}

	protected List<Column> editableColumns() {
		return nonAutoincrementedColumns();
	}

	protected List<String> editableColumnNames() {
		return editableColumns().stream().map(Column::getName).collect(Collectors.toList());
	}
}
