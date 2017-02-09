package com.j256.ormlite.stmt.mapped;

import java.sql.SQLException;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DbField;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

/**
 * Mapped statement for updating an object.
 * 
 * @author graywatson
 */
public class MappedUpdate<T, ID> extends BaseMappedStatement<T, ID> {

	private final DbField versionDbField;
	private final int versionFieldTypeIndex;

	private MappedUpdate(TableInfo<T, ID> tableInfo, String statement, DbField[] argDbFields,
						 DbField versionDbField, int versionFieldTypeIndex) {
		super(tableInfo, statement, argDbFields);
		this.versionDbField = versionDbField;
		this.versionFieldTypeIndex = versionFieldTypeIndex;
	}

	public static <T, ID> MappedUpdate<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo)
			throws SQLException {
		DbField idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot update " + tableInfo.getDataClass() + " because it doesn't have an id field");
		}
		StringBuilder sb = new StringBuilder(64);
		appendTableName(databaseType, sb, "UPDATE ", tableInfo.getTableName());
		boolean first = true;
		int argFieldC = 0;
		DbField versionDbField = null;
		int versionFieldTypeIndex = -1;
		// first we count up how many arguments we are going to have
		for (DbField dbField : tableInfo.getFieldTypes()) {
			if (isFieldUpdatable(dbField, idField)) {
				if (dbField.isVersion()) {
					versionDbField = dbField;
					versionFieldTypeIndex = argFieldC;
				}
				argFieldC++;
			}
		}
		// one more for where id = ?
		argFieldC++;
		if (versionDbField != null) {
			// one more for the AND version = ?
			argFieldC++;
		}
		DbField[] argDbFields = new DbField[argFieldC];
		argFieldC = 0;
		for (DbField dbField : tableInfo.getFieldTypes()) {
			if (!isFieldUpdatable(dbField, idField)) {
				continue;
			}
			if (first) {
				sb.append("SET ");
				first = false;
			} else {
				sb.append(", ");
			}
			appendFieldColumnName(databaseType, sb, dbField, null);
			argDbFields[argFieldC++] = dbField;
			sb.append("= ?");
		}
		sb.append(' ');
		appendWhereFieldEq(databaseType, idField, sb, null);
		argDbFields[argFieldC++] = idField;
		if (versionDbField != null) {
			sb.append(" AND ");
			appendFieldColumnName(databaseType, sb, versionDbField, null);
			sb.append("= ?");
			argDbFields[argFieldC++] = versionDbField;
		}
		return new MappedUpdate<T, ID>(tableInfo, sb.toString(), argDbFields, versionDbField, versionFieldTypeIndex);
	}

	/**
	 * Update the object in the database.
	 */
	public int update(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
		try {
			// there is always and id field as an argument so just return 0 lines updated
			if (argDbFields.length <= 1) {
				return 0;
			}
			Object[] args = getFieldObjects(data);
			Object newVersion = null;
			if (versionDbField != null) {
				newVersion = versionDbField.extractJavaFieldValue(data);
				newVersion = versionDbField.moveToNextValue(newVersion);
				args[versionFieldTypeIndex] = versionDbField.convertJavaFieldToSqlArgValue(newVersion);
			}
			int rowC = databaseConnection.update(statement, args, argDbFields);
			if (rowC > 0) {
				if (newVersion != null) {
					// if we have updated a row then update the version field in our object to the new value
					versionDbField.assignField(data, newVersion, false, null);
				}
				if (objectCache != null) {
					// if we've changed something then see if we need to update our cache
					Object id = idField.extractJavaFieldValue(data);
					T cachedData = objectCache.get(clazz, id);
					if (cachedData != null && cachedData != data) {
						// copy each field from the updated data into the cached object
						for (DbField dbField : tableInfo.getFieldTypes()) {
							if (dbField != idField) {
								dbField.assignField(cachedData, dbField.extractJavaFieldValue(data), false,
										objectCache);
							}
						}
					}
				}
			}
			logger.debug("update data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("update arguments: {}", (Object) args);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run update stmt on object " + data + ": " + statement, e);
		}
	}

	private static boolean isFieldUpdatable(DbField dbField, DbField idField) {
		if (dbField == idField || dbField.isForeignCollection() || dbField.isReadOnly()) {
			return false;
		} else {
			return true;
		}
	}
}
