package com.j256.ormlite.field.types;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Persists an Enum Java class as its ordinal integer value. You can also specify the {@link EnumStringType} as the
 * type.
 * 
 * @author graywatson
 */
public class EnumIntegerType extends BaseEnumType {

	private static final EnumIntegerType singleTon = new EnumIntegerType();

	public static EnumIntegerType getSingleton() {
		return singleTon;
	}

	private EnumIntegerType() {
		super(SqlType.INTEGER);
	}

	/**
	 * Here for others to subclass.
	 */
	protected EnumIntegerType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(DbField dbField, String defaultStr) {
		return Integer.parseInt(defaultStr);
	}

	@Override
	public Object resultToSqlArg(DbField dbField, DatabaseResults results, int columnPos) throws SQLException {
		return results.getInt(columnPos);
	}

	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) throws SQLException {
		if (dbField == null) {
			return sqlArg;
		}
		// do this once
		Integer valInteger = (Integer) sqlArg;
		@SuppressWarnings("unchecked")
		Map<Integer, Enum<?>> enumIntMap = (Map<Integer, Enum<?>>) dbField.getDataTypeConfigObj();
		if (enumIntMap == null) {
			return enumVal(dbField, valInteger, null, dbField.getUnknownEnumVal());
		} else {
			return enumVal(dbField, valInteger, enumIntMap.get(valInteger), dbField.getUnknownEnumVal());
		}
	}

	@Override
	public Object javaToSqlArg(DbField dbField, Object obj) {
		Enum<?> enumVal = (Enum<?>) obj;
		return (Integer) enumVal.ordinal();
	}

	@Override
	public boolean isEscapedValue() {
		return false;
	}

	@Override
	public Object makeConfigObject(DbField dbField) throws SQLException {
		Map<Integer, Enum<?>> enumIntMap = new HashMap<Integer, Enum<?>>();
		Enum<?>[] constants = (Enum<?>[]) dbField.getType().getEnumConstants();
		if (constants == null) {
			throw new SQLException("Field " + dbField + " improperly configured as type " + this);
		}
		for (Enum<?> enumVal : constants) {
			enumIntMap.put(enumVal.ordinal(), enumVal);
		}
		return enumIntMap;
	}

	@Override
	public Class<?> getPrimaryClass() {
		return int.class;
	}
}
