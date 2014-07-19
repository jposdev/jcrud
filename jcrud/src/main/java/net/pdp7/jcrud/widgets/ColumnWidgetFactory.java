package net.pdp7.jcrud.widgets;

import java.sql.Date;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

public class ColumnWidgetFactory {

	protected long textAreaThreshold = 101;
	protected Map<Class<?>, Widget> widgetsByClass = new ImmutableMap.Builder<Class<?>, Widget>()
			.put(Date.class, new DateWidget())
			.build();

	public Widget widgetForColumn(Column column) {
		ColumnDataType columnDataType = column.getColumnDataType();
		Class<?> columnType = columnDataType.getTypeMappedClass();

		if(String.class.equals(columnType) && column.getSize() >= textAreaThreshold) {
			return new TextAreaWidget();
		}

		if(widgetsByClass.containsKey(columnType)) {
			return widgetsByClass.get(columnType);
		}

		return new DefaultWidget();
	}

	public ColumnWidgetFactory setTextAreaThreshold(long textAreaThreshold) {
		this.textAreaThreshold = textAreaThreshold;
		return this;
	}

}
