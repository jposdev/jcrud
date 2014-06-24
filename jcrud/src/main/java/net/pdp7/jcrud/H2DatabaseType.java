package net.pdp7.jcrud;

import schemacrawler.schema.Column;

public class H2DatabaseType implements DatabaseType {

	public boolean isAutoincrementColumn(Column column) {
		return column.getAttributes().get("IS_AUTOINCREMENT").equals("YES");
	}

}
