package net.pdp7.jcrud.widgets;

import java.util.Objects;

import org.springframework.web.context.request.WebRequest;
import org.unbescape.html.HtmlEscape;

import com.hp.gagawa.java.elements.Input;

public class DefaultWidget implements Widget {
	@Override
	public String render(String name, Object value) {
		return new Input()
			// FIXME: see https://code.google.com/p/gagawa/issues/detail?id=4
			.setValue(HtmlEscape.escapeHtml5Xml(Objects.toString(value, "")))
			.setName(name)
			.write();
	}

	@Override
	public Object parseFromRequest(WebRequest request, String name) {
		return request.getParameter(name);
	}
}
