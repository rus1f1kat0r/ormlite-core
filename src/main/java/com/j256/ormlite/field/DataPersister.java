package com.j256.ormlite.field;

import java.lang.reflect.Field;
import java.sql.SQLException;

import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.stmt.ArgumentHolder;

/**
 * Data type that provide Java class to/from database mapping.
 * 
 * <p>
 * If you are defining your own custom persister, then chances are you should extend {@link BaseDataType}. See
 * {@link DatabaseField#persisterClass()}.
 * </p>
 * 
 * @author graywatson
 */
public interface DataPersister extends FieldConverter {

	/**
	 * Return the classes that should be associated with this.
	 */
	public Class<?>[] getAssociatedClasses();

	/**
	 * Return the class names that should be associated with this or null. This is used by reflection classes so we can
	 * discover if a Field matches _without_ needed the class dependency in -core.
	 */
	public String[] getAssociatedClassNames();

	/**
	 * This makes a configuration object for the data-type or returns null if none. The object can be accessed later via
	 * {@link ReflectiveFieldType#getDataTypeConfigObj()}.
	 */
	public Object makeConfigObject(FieldType fieldType) throws SQLException;

	/**
	 * Convert a {@link Number} object to its primitive object suitable for assigning to a java ID field.
	 */
	public Object convertIdNumber(Number number);

	/**
	 * Return true if this type can be auto-generated by the database. Probably only numbers will return true.
	 */
	public boolean isValidGeneratedType();

	/**
	 * Return true if the field is appropriate for this persister otherwise false.
	 * @param fieldType
	 */
	public boolean isValidForField(Class<?> fieldType);

	/**
	 * Return the class most associated with this persister or null if none.
	 */
	public Class<?> getPrimaryClass();

	/**
	 * Return whether this field's default value should be escaped in SQL.
	 */
	public boolean isEscapedDefaultValue();

	/**
	 * Return whether we need to escape this value in SQL expressions. Numbers _must_ not be escaped but most other
	 * values should be.
	 */
	public boolean isEscapedValue();

	/**
	 * Return whether this field is a primitive type or not. This is used to know if we should throw if the field value
	 * is null.
	 */
	public boolean isPrimitive();

	/**
	 * Return true if this data type be compared in SQL statements.
	 */
	public boolean isComparable();

	/**
	 * Return true if this data type can be an id column in a class.
	 */
	public boolean isAppropriateId();

	/**
	 * Must use {@link ArgumentHolder} when querying for values of this type.
	 */
	public boolean isArgumentHolderRequired();

	/**
	 * Return true if this type creates its own generated ids else false to have the database do it.
	 */
	public boolean isSelfGeneratedId();

	/**
	 * Return a generated id if appropriate or null if none.
	 */
	public Object generateId();

	/**
	 * Return the default width associated with this type or 0 if none.
	 */
	public int getDefaultWidth();

	/**
	 * Compare two fields of this type returning true if equals else false.
	 */
	public boolean dataIsEqual(Object obj1, Object obj2);

	/**
	 * Return true if this is a valid field for the {@link DatabaseField#version()}.
	 */
	public boolean isValidForVersion();

	/**
	 * Move the current-value to the next value. Used for the version field.
	 */
	public Object moveToNextValue(Object currentValue) throws SQLException;

	/**
	 * Get the type that should be used when defining this 
	 */
	public String getSqlOtherType();
}
