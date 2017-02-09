package com.j256.ormlite.stmt;

import java.sql.SQLException;

import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;

/**
 * Base class for other select argument classes.
 * 
 * @author graywatson
 */
public abstract class BaseArgumentHolder implements ArgumentHolder {

	private String columnName = null;
	private DbField dbField = null;
	private SqlType sqlType = null;

	public BaseArgumentHolder() {
		// no args
	}

	public BaseArgumentHolder(String columName) {
		this.columnName = columName;
	}

	public BaseArgumentHolder(SqlType sqlType) {
		this.sqlType = sqlType;
	}

	/**
	 * Return the stored value.
	 */
	protected abstract Object getValue();

	@Override
	public abstract void setValue(Object value);

	/**
	 * Return true if the value is set.
	 */
	protected abstract boolean isValueSet();

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public void setMetaInfo(String columnName) {
		if (this.columnName == null) {
			// not set yet
		} else if (this.columnName.equals(columnName)) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("Column name cannot be set twice from " + this.columnName + " to "
					+ columnName + ".  Using a SelectArg twice in query with different columns?");
		}
		this.columnName = columnName;
	}

	@Override
	public void setMetaInfo(DbField dbField) {
		if (this.dbField == null) {
			// not set yet
		} else if (this.dbField == dbField) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("FieldType name cannot be set twice from " + this.dbField + " to "
					+ dbField + ".  Using a SelectArg twice in query with different columns?");
		}
		this.dbField = dbField;
	}

	@Override
	public void setMetaInfo(String columnName, DbField dbField) {
		setMetaInfo(columnName);
		setMetaInfo(dbField);
	}

	@Override
	public Object getSqlArgValue() throws SQLException {
		if (!isValueSet()) {
			throw new SQLException("Column value has not been set for " + columnName);
		}
		Object value = getValue();
		if (value == null) {
			return null;
		} else if (dbField == null) {
			return value;
		} else if (dbField.isForeign() && dbField.getType() == value.getClass()) {
			DbField refDbField = dbField.getForeignRefField();
			return refDbField.extractJavaFieldValue(value);
		} else {
			return dbField.convertJavaFieldToSqlArgValue(value);
		}
	}

	@Override
	public DbField getFieldType() {
		return dbField;
	}

	@Override
	public SqlType getSqlType() {
		return sqlType;
	}

	@Override
	public String toString() {
		if (!isValueSet()) {
			return "[unset]";
		}
		Object val;
		try {
			val = getSqlArgValue();
			if (val == null) {
				return "[null]";
			} else {
				return val.toString();
			}
		} catch (SQLException e) {
			return "[could not get value: " + e + "]";
		}
	}
}
