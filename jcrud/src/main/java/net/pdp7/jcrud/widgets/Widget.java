package net.pdp7.jcrud.widgets;

import org.springframework.web.context.request.WebRequest;

public interface Widget {

	/** make sure you always escape value as an HTML attribute! */
	public abstract String render(String name, Object value);

	public abstract Object parseFromRequest(WebRequest request, String name);

}