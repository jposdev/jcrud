package net.pdp7.jcrud.widgets;

import java.util.Objects;

import com.hp.gagawa.java.elements.Textarea;

public class TextAreaWidget extends DefaultWidget {

	protected String defaultCols = "20";
	protected String defaultRows = "20";

	@Override
	public String render(String name, Object value) {
		return new Textarea(defaultCols, defaultRows)
			.setName(name)
			.appendText(Objects.toString(value, ""))
			.write();
	}

	public TextAreaWidget setDefaultCols(String defaultCols) {
		this.defaultCols = defaultCols;
		return this;
	}

	public TextAreaWidget setDefaultRows(String defaultRows) {
		this.defaultRows = defaultRows;
		return this;
	}

}
