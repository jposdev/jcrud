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

import schemacrawler.schema.Table;

import com.google.common.collect.ImmutableMap;

public class TableController extends TableService {

	public TableController(Table table, NamedParameterJdbcTemplate jdbcTemplate, DatabaseType databaseType) {
		super(table, jdbcTemplate, databaseType);
	}

	public ModelAndView list() {
		List<Map<String, Object>> items = listItems();

		items.forEach(item -> item.put("edit_uri", editFormUri(item)));

		return new ModelAndView("list", new ImmutableMap.Builder<String, Object>()
				.put("items", items)
				.put("columns", listColumns())
				.put("add_form", addFormUri())
				.build()
		);
	}

	public UriComponents listUri() {
		return MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).list()).build();
	}

	public ModelAndView addForm() {
		return changeForm(new HashMap<String, Object>(), addUri());
	}

	public UriComponents addFormUri() {
		return MvcUriComponentsBuilder
				.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).addForm())
				.build();
	}

	public View add(WebRequest request) {
		Map<String, Object> insertColumns = getEditableColumnsFromRequest(request);
		insertItem(insertColumns);
		return new RedirectView(listUri().toUriString());
	}

	public UriComponents addUri() {
		return MvcUriComponentsBuilder
				.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).add(null))
				.build();
	}

	public ModelAndView editForm(WebRequest request) {
		Map<String, Object> primaryKeys = getPrimaryKeysFromRequest(request);
		Map<String, Object> item = getItem(primaryKeys);
		return changeForm(item, editUri(item));
	}

	protected UriComponents editFormUri(Map<String, Object> item) {
		return MvcUriComponentsBuilder
					.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).editForm(null))
					.queryParams(convertMapToMultiMap(getPrimaryKeysFromItem(item)))
					.build();
	}

	public View edit(WebRequest request) {
		Map<String, Object> primaryKeys = getPrimaryKeysFromRequest(request);
		Map<String, Object> updateKeys = getEditableColumnsFromRequest(request);

		updateItem(primaryKeys, updateKeys);
		return new RedirectView(listUri().toUriString());
	}

	protected UriComponents editUri(Map<String, Object> item) {
		return MvcUriComponentsBuilder
				.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).edit(null))
				.queryParams(convertMapToMultiMap(getPrimaryKeysFromItem(item)))
				.build();
	}

	protected ModelAndView changeForm(Map<String, Object> item, UriComponents changeSaveUri) {
		return new ModelAndView("change", new ImmutableMap.Builder<String, Object>()
				.put("item", item)
				.put("columns", editableColumns())
				.put("change_save", changeSaveUri)
				.put("list", listUri())
				.build()
		);
	}

	protected Map<String, Object> getEditableColumnsFromRequest(WebRequest request) {
		return editableColumnNames().stream()
				.collect(Collectors.toMap(cn -> cn, cn -> request.getParameter(cn)));
	}

	protected Map<String, Object> getPrimaryKeysFromRequest(WebRequest request) {
		return primaryKeyColumnNames().stream()
				.collect(Collectors.toMap(cn -> cn, cn -> request.getParameter(cn)));
	}

	protected <K, V> MultiValueMap<K, V> convertMapToMultiMap(Map<K, V> primaryKeys) {
		return new LinkedMultiValueMap<K, V>(primaryKeys
				.entrySet()
				.stream()
				.collect(Collectors.<Map.Entry<K, V>, K, List<V>> toMap(
						e -> e.getKey(),
						e -> Collections.singletonList(e.getValue())
				)
		));
	}
}
