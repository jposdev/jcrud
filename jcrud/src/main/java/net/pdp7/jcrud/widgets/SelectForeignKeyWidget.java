package net.pdp7.jcrud.widgets;

import java.util.Map;

import net.pdp7.jcrud.TableService;

import com.hp.gagawa.java.elements.Option;
import com.hp.gagawa.java.elements.Select;

public class SelectForeignKeyWidget extends DefaultWidget {

	protected final TableService tableService;

	public SelectForeignKeyWidget(TableService tableService) {
		this.tableService = tableService;
	}

	@Override
	public String render(String name, Object value) {
		Select select = new Select()
			.setName(name);

		for(Map<String, Object> item : tableService.listItems()) {
			Map<String, String> primaryKeys = tableService.getPrimaryKeysFromItem(item);
			if(primaryKeys.size() != 1) {
				throw new UnsupportedOperationException("Composite foreign keys are not supported");
			}
			String primaryKey = primaryKeys.values().iterator().next();
			Option option = new Option()
				.setValue(primaryKey)
				.appendText(tableService.getItemAsString(item));
			if(primaryKey == value) {
				option.setSelected("selected");
			}
			select.appendChild(option);
		}
		
		return select.write();
	}
	
}
