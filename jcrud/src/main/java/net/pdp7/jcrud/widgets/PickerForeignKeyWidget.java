package net.pdp7.jcrud.widgets;

import java.util.Objects;

import net.pdp7.jcrud.TableController;

import org.unbescape.html.HtmlEscape;

import com.hp.gagawa.java.elements.Input;

public class PickerForeignKeyWidget extends DefaultWidget {

	protected final TableController tableController;

	public PickerForeignKeyWidget(TableController tableController) {
		this.tableController = tableController;
	}

	@Override
	public String render(String name, Object value) {
		Input input = new Input()
			// FIXME: see https://code.google.com/p/gagawa/issues/detail?id=4
			.setValue(HtmlEscape.escapeHtml5Xml(Objects.toString(value, "")))
			.setName(name)
			.setCSSClass("picker");
		String output = input.write();
		Input listUri = new Input()
			.setType("hidden")
			.setName(name + "_list_uri")
			.setValue(tableController.listUri(true).toUriString());
		output += listUri.write();
		return output;
	}
}
