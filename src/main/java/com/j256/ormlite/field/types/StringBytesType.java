package com.j256.ormlite.field.types;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Type that persists a String as a byte array.
 * 
 * @author graywatson
 */
public class StringBytesType extends BaseDataType {

	private static final String DEFAULT_STRING_BYTES_CHARSET_NAME = "Unicode";

	private static final StringBytesType singleTon = new StringBytesType();

	public static StringBytesType getSingleton() {
		return singleTon;
	}

	private StringBytesType() {
		super(SqlType.BYTE_ARRAY);
	}

	/**
	 * Here for others to subclass.
	 */
	protected StringBytesType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(DbField dbField, String defaultStr) throws SQLException {
		if (defaultStr == null) {
			return null;
		} else {
			try {
				return defaultStr.getBytes(getCharsetName(dbField));
			} catch (UnsupportedEncodingException e) {
				throw SqlExceptionUtil.create("Could not convert default string: " + defaultStr, e);
			}
		}
	}

	@Override
	public Object resultToSqlArg(DbField dbField, DatabaseResults results, int columnPos) throws SQLException {
		return results.getBytes(columnPos);
	}

	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) throws SQLException {
		byte[] bytes = (byte[]) sqlArg;
		String charsetName = getCharsetName(dbField);
		try {
			// NOTE: I can't use new String(bytes, Charset) because it was introduced in 1.6.
			return new String(bytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw SqlExceptionUtil.create("Could not convert string with charset name: " + charsetName, e);
		}
	}

	@Override
	public Object javaToSqlArg(DbField dbField, Object javaObject) throws SQLException {
		String string = (String) javaObject;
		String charsetName = getCharsetName(dbField);
		try {
			// NOTE: I can't use string.getBytes(Charset) because it was introduced in 1.6.
			return string.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw SqlExceptionUtil.create("Could not convert string with charset name: " + charsetName, e);
		}
	}

	@Override
	public boolean isArgumentHolderRequired() {
		return true;
	}

	@Override
	public Object resultStringToJava(DbField dbField, String stringValue, int columnPos) throws SQLException {
		throw new SQLException("String-bytes type cannot be converted from string to Java");
	}

	@Override
	public Class<?> getPrimaryClass() {
		return String.class;
	}

	private String getCharsetName(DbField dbField) {
		if (dbField == null || dbField.getFormat() == null) {
			return DEFAULT_STRING_BYTES_CHARSET_NAME;
		} else {
			return dbField.getFormat();
		}
	}
}
