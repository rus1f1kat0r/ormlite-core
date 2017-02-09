package com.j256.ormlite.stmt.query;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DbField;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.Where;

/**
 * Internal class handling the SQL 'IS NULL' comparison query part. Used by {@link Where#isNull}.
 * 
 * @author graywatson
 */
public class IsNull extends BaseComparison {

	public IsNull(String columnName, DbField dbField) throws SQLException {
		super(columnName, dbField, null, false);
	}

	@Override
	public void appendOperation(StringBuilder sb) {
		sb.append("IS NULL ");
	}

	@Override
	public void appendValue(DatabaseType databaseType, StringBuilder sb, List<ArgumentHolder> argList) {
		// there is no value
	}
}
