package com.j256.ormlite.stmt.query;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DbField;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.StatementBuilder;

/**
 * Internal class handling the SQL SET part used by UPDATE statements. Used by
 * {@link StatementBuilder#updateColumnExpression(String, String)}.
 * 
 * <p>
 * It's not a comparison per se but does have a columnName = value form so it works.
 * </p>
 * 
 * @author graywatson
 */
public class SetExpression extends BaseComparison {

	public SetExpression(String columnName, DbField dbField, String string) throws SQLException {
		super(columnName, dbField, string, true);
	}

	@Override
	public void appendOperation(StringBuilder sb) {
		sb.append("= ");
	}

	@Override
	protected void appendArgOrValue(DatabaseType databaseType, DbField dbField, StringBuilder sb,
									List<ArgumentHolder> selectArgList, Object argOrValue) {
		// we know it is a string so just append it
		sb.append(argOrValue).append(' ');
	}
}
