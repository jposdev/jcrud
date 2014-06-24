package net.pdp7.jcrud;

import schemacrawler.schema.Column;

public interface DatabaseType {

	public boolean isAutoincrementColumn(Column column);
	
}
