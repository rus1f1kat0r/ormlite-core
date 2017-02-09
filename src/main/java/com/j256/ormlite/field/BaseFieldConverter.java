package com.j256.ormlite.field;

import java.sql.SQLException;

import com.j256.ormlite.support.DatabaseResults;

/**
 * Base class for field-converters.
 * 
 * @author graywatson
 */
public abstract class BaseFieldConverter implements FieldConverter {

	/**
	 * @throws SQLException
	 *             If there are problems with the conversion.
	 */
	@Override
	public Object javaToSqlArg(DbField dbField, Object javaObject) throws SQLException {
		// noop pass-thru
		return javaObject;
	}

	@Override
	public Object resultToJava(DbField dbField, DatabaseResults results, int columnPos) throws SQLException {
		Object value = resultToSqlArg(dbField, results, columnPos);
		if (value == null) {
			return null;
		} else {
			return sqlArgToJava(dbField, value, columnPos);
		}
	}

	/**
	 * @throws SQLException
	 *             If there are problems with the conversion.
	 */
	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) throws SQLException {
		// noop pass-thru
		return sqlArg;
	}

	@Override
	public boolean isStreamType() {
		return false;
	}
}
