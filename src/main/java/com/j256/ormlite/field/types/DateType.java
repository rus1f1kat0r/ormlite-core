package com.j256.ormlite.field.types;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;

import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Type that persists a {@link java.util.Date} object.
 * 
 * <p>
 * NOTE: This is <i>not</i> the same as the {@link java.sql.Date} class that is handled by {@link SqlDateType}.
 * </p>
 * 
 * @author graywatson
 */
public class DateType extends BaseDateType {

	private static final DateType singleTon = new DateType();

	public static DateType getSingleton() {
		return singleTon;
	}

	private DateType() {
		super(SqlType.DATE, new Class<?>[] { java.util.Date.class });
	}

	protected DateType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(DbField dbField, String defaultStr) throws SQLException {
		DateStringFormatConfig dateFormatConfig = convertDateStringConfig(dbField, getDefaultDateFormatConfig());
		try {
			return new Timestamp(parseDateString(dateFormatConfig, defaultStr).getTime());
		} catch (ParseException e) {
			throw SqlExceptionUtil.create("Problems parsing default date string '" + defaultStr + "' using '"
					+ dateFormatConfig + '\'', e);
		}
	}

	@Override
	public Object resultToSqlArg(DbField dbField, DatabaseResults results, int columnPos) throws SQLException {
		return results.getTimestamp(columnPos);
	}

	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) {
		Timestamp value = (Timestamp) sqlArg;
		return new java.util.Date(value.getTime());
	}

	@Override
	public Object javaToSqlArg(DbField dbField, Object javaObject) {
		java.util.Date date = (java.util.Date) javaObject;
		return new Timestamp(date.getTime());
	}

	@Override
	public boolean isArgumentHolderRequired() {
		return true;
	}

	/**
	 * Return the default date format configuration.
	 */
	protected DateStringFormatConfig getDefaultDateFormatConfig() {
		return defaultDateFormatConfig;
	}
}
