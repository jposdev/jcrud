package net.pdp7.jcrud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.context.request.WebRequest;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;

import com.google.common.collect.ImmutableMap;

public class Inline {

	protected final ForeignKey foreignKey;
	protected final TableController tableController;
	protected final String inlineId;
	protected int extraInlines = 1;

	public Inline(ForeignKey foreignKey, TableController tableController) {
		this(foreignKey, tableController, foreignKey.getName());
	}

	/** use this form if a TableController will have two inlines with the
	 * same foreign key
	 *
	 * @param inlineId distinct identifier for the inlines
	 */
	public Inline(ForeignKey foreignKey, TableController tableController, String inlineId) {
		this.tableController = tableController;
		this.foreignKey = foreignKey;
		this.inlineId = inlineId;
	}

	public List<Map<String, Object>> list(Map<String, Object> referencedItem) {
		List<Map<String, Object>> items = new ArrayList<>();
		if(!referencedItem.isEmpty()) {
			Map<String, Object> conditions = foreignKey.getColumnReferences().stream().collect(Collectors.toMap(cr -> cr.getPrimaryKeyColumn().getName(), cr -> referencedItem.get(cr.getForeignKeyColumn().getName())));
			items.addAll(tableController.listItems(conditions));
			items.forEach(i -> i.put("_primary_key", primaryKeysToString(tableController.getPrimaryKeysFromItem(i))));
			items.forEach(i -> i.put("_keep", true));
		}
		for(int i=0; i<extraInlines; i++) {
			items.add(new ImmutableMap.Builder<String,Object>()
					.put("_primary_key", "extra_" + i)
					.put("_keep", false)
					.build());
		}
		return items;
	}

	protected String primaryKeysToString(Map<String, String> primaryKeys) {
		return primaryKeys.entrySet().stream()
				.map(e -> e.getKey() + "=" + e.getValue())
				.collect(Collectors.joining("/"));
	}

	public List<Column> getEditableColumns() {
		return tableController
				.editableColumns()
				.filter(c -> !getTargetForeignKeyColumns().contains(c))
				.collect(Collectors.toList());
	}

	protected Set<Column> getTargetForeignKeyColumns() {
		return foreignKey.getColumnReferences().stream()
				.map(fkcr -> fkcr.getForeignKeyColumn())
				.collect(Collectors.toSet());
	}
	
	public TableController getTableController() {
		return tableController;
	}
	
	public String getInlineId() {
		return inlineId;
	}

	public void add(WebRequest request) {
	}

	public void update(Map<String, Object> primaryKeys, WebRequest request) {
		processInlineUpdates(primaryKeys, request);
		processInlineAdds(primaryKeys, request);
	}

	protected void processInlineAdds(Map<String, Object> primaryKeys, WebRequest request) {
		for(int i=0; request.getParameterMap().keySet().contains(inlineId + "/extra_" + i + "/_keep"); i++) {
			if(!request.getParameter(inlineId + "/extra_" + i + "/_keep").equals("keep")) {
				continue;
			}
			final int inlineIndex = i;
			Map<String, Object> insertKeys = getEditableColumns().stream()
					.collect(Collectors.toMap(
							c -> c.getName(),
							c -> request.getParameter(inlineId + "/extra_" + inlineIndex + "/" + c.getName())
					));
			insertKeys.putAll(primaryKeys);
			tableController.insertItem(insertKeys);
		}
	}

	protected void processInlineUpdates(Map<String, Object> primaryKeys, WebRequest request) {
		// TODO: process deletions!
		List<Map<String, Object>> itemsToUpdate = tableController.listItems(foreignKey.getColumnReferences().stream().collect(Collectors.toMap(cr -> cr.getPrimaryKeyColumn().getName(), cr -> primaryKeys.get(cr.getForeignKeyColumn().getName()))));
		for(Map<String, Object> itemToUpdate : itemsToUpdate) {
			Map<String, String> itemToUpdatePrimaryKeys = tableController.getPrimaryKeysFromItem(itemToUpdate);
			Map<String, Object> updateKeys = getEditableColumns().stream()
					.collect(Collectors.toMap(
							c -> c.getName(),
							c -> request.getParameter(inlineId + "/" + primaryKeysToString(itemToUpdatePrimaryKeys) + "/" + c.getName())
					));
			updateKeys.putAll(primaryKeys);
			tableController.updateItem(itemToUpdatePrimaryKeys, updateKeys);
		}
	}
}
