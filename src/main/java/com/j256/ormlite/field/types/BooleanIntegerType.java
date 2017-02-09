package com.j256.ormlite.field.types;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Booleans can be stored in the database as the integer column type and the value 1 (really non-0) for true and 0 for
 * false. You must choose this DataType specifically with the {@link DatabaseField#dataType()} specifier.
 * 
 * <pre>
 * &#64;DatabaseField(dataType = DataType.BOOLEAN_INTEGER)
 * </pre>
 * 
 * Thanks much to stew.
 * 
 * @author graywatson
 */
public class BooleanIntegerType extends BooleanType {

	private static final Integer TRUE_VALUE = Integer.valueOf(1);
	private static final Integer FALSE_VALUE = Integer.valueOf(0);

	private static final BooleanIntegerType singleTon = new BooleanIntegerType();

	public static BooleanIntegerType getSingleton() {
		return singleTon;
	}

	public BooleanIntegerType() {
		super(SqlType.INTEGER);
	}

	@Override
	public Object parseDefaultString(DbField dbField, String defaultStr) {
		return javaToSqlArg(dbField, Boolean.parseBoolean(defaultStr));
	}

	@Override
	public Object javaToSqlArg(DbField dbField, Object obj) {
		return ((Boolean) obj ? TRUE_VALUE : FALSE_VALUE);
	}

	@Override
	public Object resultToSqlArg(DbField dbField, DatabaseResults results, int columnPos) throws SQLException {
		return results.getInt(columnPos);
	}

	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) {
		return ((Integer) sqlArg == 0 ? Boolean.FALSE : Boolean.TRUE);
	}

	@Override
	public Object resultStringToJava(DbField dbField, String stringValue, int columnPos) {
		if (stringValue.length() == 0) {
			return Boolean.FALSE;
		} else {
			return sqlArgToJava(dbField, Integer.parseInt(stringValue), columnPos);
		}
	}
}
