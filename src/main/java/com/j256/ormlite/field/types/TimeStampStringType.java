package com.j256.ormlite.field.types;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.SqlType;

/**
 * Type that persists a {@link java.sql.Timestamp} object as a String.
 * 
 * @author graywatson
 */
public class TimeStampStringType extends DateStringType {

	private static final TimeStampStringType singleTon = new TimeStampStringType();

	public static TimeStampStringType getSingleton() {
		return singleTon;
	}

	private TimeStampStringType() {
		super(SqlType.STRING);
	}

	/**
	 * Here for others to subclass.
	 */
	protected TimeStampStringType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object sqlArgToJava(DbField dbField, Object sqlArg, int columnPos) throws SQLException {
		Date date = (Date) super.sqlArgToJava(dbField, sqlArg, columnPos);
		return new Timestamp(date.getTime());
	}

	@Override
	public Object javaToSqlArg(DbField dbField, Object javaObject) {
		Timestamp timeStamp = (Timestamp) javaObject;
		return super.javaToSqlArg(dbField, new Date(timeStamp.getTime()));
	}

	@Override
	public boolean isValidForField(Field field) {
		return (field.getType() == java.sql.Timestamp.class);
	}

	@Override
	public Object moveToNextValue(Object currentValue) {
		long newVal = System.currentTimeMillis();
		if (currentValue == null) {
			return new java.sql.Timestamp(newVal);
		} else if (newVal == ((java.sql.Timestamp) currentValue).getTime()) {
			return new java.sql.Timestamp(newVal + 1L);
		} else {
			return new java.sql.Timestamp(newVal);
		}
	}
}
