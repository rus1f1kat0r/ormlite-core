package com.j256.ormlite.field.types;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Type that persists a {@link java.util.Date} object as a String.
 * 
 * @author graywatson
 */
public class DateStringType extends BaseDateType {

	public static int DEFAULT_WIDTH = 50;

	private static final DateStringType singleTon = new DateStringType();

	public static DateStringType getSingleton() {
		return singleTon;
	}

	private DateStringType() {
		super(SqlType.STRING);
	}

	protected DateStringType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	protected DateStringType(SqlType sqlType) {
		super(sqlType);
	}

	@Override
	public Object parseDefaultString(DbField dbField, String defaultStr) throws SQLException {
		DateStringFormatConfig formatConfig = convertDateStringConfig(dbField, defaultDateFormatConfig);
		try {
			// we parse to make sure it works and then format it again
			return normalizeDateString(formatConfig, defaultStr);
		} catch (ParseException e) {
			throw SqlExceptionUtil.create("Problems with field " + dbField + " parsing default date-string '"
					+ defaultStr + "' using '" + formatConfig + "'", e);
		}
	}

	@Override
	public Object resultToSqlArg(DbField dbField, DatabaseResults results, int columnPos) throws SQLException {
		return results.getString(columnPos);
	}

	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) throws SQLException {
		String value = (String) sqlArg;
		DateStringFormatConfig formatConfig = convertDateStringConfig(dbField, defaultDateFormatConfig);
		try {
			return parseDateString(formatConfig, value);
		} catch (ParseException e) {
			throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing date-string '" + value
					+ "' using '" + formatConfig + "'", e);
		}
	}

	@Override
	public Object javaToSqlArg(DbField dbField, Object obj) {
		DateFormat dateFormat = convertDateStringConfig(dbField, defaultDateFormatConfig).getDateFormat();
		return dateFormat.format((Date) obj);
	}

	@Override
	public Object makeConfigObject(DbField dbField) {
		String format = dbField.getFormat();
		if (format == null) {
			return defaultDateFormatConfig;
		} else {
			return new DateStringFormatConfig(format);
		}
	}

	@Override
	public int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	@Override
	public Object resultStringToJava(DbField dbField, String stringValue, int columnPos) throws SQLException {
		return sqlArgToJava(dbField, stringValue, columnPos);
	}

	@Override
	public Class<?> getPrimaryClass() {
		return byte[].class;
	}
}
