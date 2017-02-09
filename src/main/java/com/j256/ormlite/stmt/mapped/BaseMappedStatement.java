package com.j256.ormlite.stmt.mapped;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DbField;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.table.TableInfo;

/**
 * Abstract mapped statement which has common statements used by the subclasses.
 * 
 * @author graywatson
 */
public abstract class BaseMappedStatement<T, ID> {

	protected static Logger logger = LoggerFactory.getLogger(BaseMappedStatement.class);

	protected final TableInfo<T, ID> tableInfo;
	protected final Class<T> clazz;
	protected final DbField idField;
	protected final String statement;
	protected final DbField[] argDbFields;

	protected BaseMappedStatement(TableInfo<T, ID> tableInfo, String statement, DbField[] argDbFields) {
		this.tableInfo = tableInfo;
		this.clazz = tableInfo.getDataClass();
		this.idField = tableInfo.getIdField();
		this.statement = statement;
		this.argDbFields = argDbFields;
	}

	/**
	 * Return the array of field objects pulled from the data object.
	 */
	protected Object[] getFieldObjects(Object data) throws SQLException {
		Object[] objects = new Object[argDbFields.length];
		for (int i = 0; i < argDbFields.length; i++) {
			DbField dbField = argDbFields[i];
			if (dbField.isAllowGeneratedIdInsert()) {
				objects[i] = dbField.getFieldValueIfNotDefault(data);
			} else {
				objects[i] = dbField.extractJavaFieldToSqlArgValue(data);
			}
			if (objects[i] == null) {
				// NOTE: the default value could be null as well
				objects[i] = dbField.getDefaultValue();
			}
		}
		return objects;
	}

	/**
	 * Return a field object converted from an id.
	 */
	protected Object convertIdToFieldObject(ID id) throws SQLException {
		return idField.convertJavaFieldToSqlArgValue(id);
	}

	static void appendWhereFieldEq(DatabaseType databaseType, DbField dbField, StringBuilder sb,
								   List<DbField> dbFieldList) {
		sb.append("WHERE ");
		appendFieldColumnName(databaseType, sb, dbField, dbFieldList);
		sb.append("= ?");
	}

	static void appendTableName(DatabaseType databaseType, StringBuilder sb, String prefix, String tableName) {
		if (prefix != null) {
			sb.append(prefix);
		}
		databaseType.appendEscapedEntityName(sb, tableName);
		sb.append(' ');
	}

	static void appendFieldColumnName(DatabaseType databaseType, StringBuilder sb, DbField dbField,
			List<DbField> dbFieldList) {
		databaseType.appendEscapedEntityName(sb, dbField.getColumnName());
		if (dbFieldList != null) {
			dbFieldList.add(dbField);
		}
		sb.append(' ');
	}

	@Override
	public String toString() {
		return "MappedStatement: " + statement;
	}
}
