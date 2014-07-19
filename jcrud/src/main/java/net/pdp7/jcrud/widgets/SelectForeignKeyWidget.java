package net.pdp7.jcrud.widgets;

import java.util.Map;

import net.pdp7.jcrud.TableController;
import schemacrawler.schema.Column;

import com.hp.gagawa.java.elements.Option;
import com.hp.gagawa.java.elements.Select;

public class SelectForeignKeyWidget extends DefaultWidget {

	protected final Column column;
	protected final TableController tableController;

	public SelectForeignKeyWidget(Column column, TableController tableController) {
		this.column = column;
		this.tableController = tableController;
	}

	@Override
	public String render(String name, Object value) {
		Select select = new Select()
			.setName(name);

		for(Map<String, Object> item : tableController.listItems()) {
			Map<String, String> primaryKeys = tableController.getPrimaryKeysFromItem(item);
			if(primaryKeys.size() != 1) {
				throw new UnsupportedOperationException("Composite foreign keys are not supported");
			}
			String primaryKey = primaryKeys.values().iterator().next();
			Option option = new Option()
				.setValue(primaryKey)
				.appendText(tableController.getItemAsString(item));
			if(primaryKey == value) {
				option.setSelected("selected");
			}
			select.appendChild(option);
		}
		
		return select.write();
	}
	
}
