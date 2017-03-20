package com.j256.ormlite.field;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.EagerForeignCollection;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.LazyForeignCollection;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.types.VoidType;
import com.j256.ormlite.logger.Log.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.mapped.MappedQueryForFieldEq;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableInfo;

/**
 * Per field information configured from the {@link DatabaseField} annotation and the associated {@link Field} in the
 * class. Use the {@link #createFieldType} static method to instantiate the class.
 * 
 * @author graywatson
 */
public class ReflectiveFieldType extends BaseFieldType {

	private final Field field;
	private final Method fieldGetMethod;
	private final Method fieldSetMethod;

	/**
	 * You should use {@link ReflectiveFieldType#createFieldType} to instantiate one of these field if you have a {@link Field}.
	 */
	public ReflectiveFieldType(ConnectionSource connectionSource, String tableName, Field field, DatabaseFieldConfig fieldConfig,
							   Class<?> parentClass) throws SQLException {
		super(tableName, fieldConfig, connectionSource, parentClass, field.getType());
		this.field = field;
		Class<?> clazz = field.getType();
		if (fieldConfig.isForeignCollection()) {
			if (clazz != Collection.class && !ForeignCollection.class.isAssignableFrom(clazz)) {
				throw new SQLException("Field class for '" + fieldConfig.getFieldName() + "' must be of class "
						+ ForeignCollection.class.getSimpleName() + " or Collection.");
			}
			Type type = field.getGenericType();
			if (!(type instanceof ParameterizedType)) {
				throw new SQLException("Field class for '" + field.getName() + "' must be a parameterized Collection.");
			}
			Type[] genericArguments = ((ParameterizedType) type).getActualTypeArguments();
			if (genericArguments.length == 0) {
				// i doubt this will ever be reached
				throw new SQLException("Field class for '" + field.getName()
						+ "' must be a parameterized Collection with at least 1 type.");
			}
		}
		if (fieldConfig.isUseGetSet()) {
			this.fieldGetMethod = DatabaseFieldConfig.findGetMethod(field, true);
			this.fieldSetMethod = DatabaseFieldConfig.findSetMethod(field, true);
		} else {
			if (!field.isAccessible()) {
				try {
					this.field.setAccessible(true);
				} catch (SecurityException e) {
					throw new IllegalArgumentException("Could not open access to field " + field.getName()
							+ ".  You may have to set useGetSet=true to fix.");
				}
			}
			this.fieldGetMethod = null;
			this.fieldSetMethod = null;
		}
	}

	@Override
	protected DataPersister getDataPersister(Class<? extends DataPersister> persisterClass) throws SQLException  {
		DataPersister dataPersister;Method method;
		try {
			method = persisterClass.getDeclaredMethod("getSingleton");
		} catch (Exception e) {
			throw SqlExceptionUtil
					.create("Could not find getSingleton static method on class " + persisterClass, e);
		}
		Object result;
		try {
			result = method.invoke(null);
		} catch (InvocationTargetException e) {
			throw SqlExceptionUtil.create("Could not run getSingleton method on class " + persisterClass,
					e.getTargetException());
		} catch (Exception e) {
			throw SqlExceptionUtil.create("Could not run getSingleton method on class " + persisterClass, e);
		}
		if (result == null) {
			throw new SQLException(
					"Static getSingleton method should not return null on class " + persisterClass);
		}
		try {
			dataPersister = (DataPersister) result;
		} catch (Exception e) {
			throw SqlExceptionUtil
					.create("Could not cast result of static getSingleton method to DataPersister from class "
							+ persisterClass, e);
		}
		return dataPersister;
	}

	public Field getField() {
		return field;
	}

	/**
	 * Return the generic type of the field associated with this field type.
	 */
	@Override
	public Type getGenericType() {
		return field.getGenericType();
	}

	/**
	 * Assign to the data object the val corresponding to the fieldType.
	 */
	@Override
	public void assignField(Object data, Object val, boolean parentObject, ObjectCache objectCache)
			throws SQLException {
		if (logger.isLevelEnabled(Level.TRACE)) {
			logger.trace("assiging from data {}, val {}: {}", (data == null ? "null" : data.getClass()),
					(val == null ? "null" : val.getClass()), val);
		}
		// if this is a foreign object then val is the foreign object's id val
		if (getForeignRefField() != null && val != null) {
			// get the current field value which is the foreign-id
			Object foreignRef = extractJavaFieldValue(data);
			/*
			 * See if we don't need to create a new foreign object. If we are refreshing and the id field has not
			 * changed then there is no need to create a new foreign object and maybe lose previously refreshed field
			 * information.
			 */
			if (foreignRef != null && foreignRef.equals(val)) {
				return;
			}
			// awhitlock: raised as OrmLite issue: bug #122
			Object cachedVal;
			ObjectCache foreignCache = foreignDao.getObjectCache();
			if (foreignCache == null) {
				cachedVal = null;
			} else {
				cachedVal = foreignCache.get(getType(), val);
			}
			if (cachedVal != null) {
				val = cachedVal;
			} else if (!parentObject) {
				// the value we are to assign to our field is now the foreign object itself
				val = createForeignObject(val, objectCache);
			}
		}

		if (fieldSetMethod == null) {
			try {
				field.set(data, val);
			} catch (IllegalArgumentException e) {
				throw SqlExceptionUtil.create(
						"Could not assign object '" + val + "' of type " + val.getClass() + " to field " + this, e);
			} catch (IllegalAccessException e) {
				throw SqlExceptionUtil.create(
						"Could not assign object '" + val + "' of type " + val.getClass() + "' to field " + this, e);
			}
		} else {
			try {
				fieldSetMethod.invoke(data, val);
			} catch (Exception e) {
				throw SqlExceptionUtil
						.create("Could not call " + fieldSetMethod + " on object with '" + val + "' for " + this, e);
			}
		}
	}

	/**
	 * Return the value from the field in the object that is defined by this ReflectiveFieldType.
	 */
	@Override
	public <FV> FV extractRawJavaFieldValue(Object object) throws SQLException {
		Object val;
		if (fieldGetMethod == null) {
			try {
				// field object may not be a T yet
				val = field.get(object);
			} catch (Exception e) {
				throw SqlExceptionUtil.create("Could not get field value for " + this, e);
			}
		} else {
			try {
				val = fieldGetMethod.invoke(object);
			} catch (Exception e) {
				throw SqlExceptionUtil.create("Could not call " + fieldGetMethod + " for " + this, e);
			}
		}

		@SuppressWarnings("unchecked")
		FV converted = (FV) val;
		return converted;
	}

	/**
	 * Return An instantiated {@link ReflectiveFieldType} or null if the field does not have a {@link DatabaseField} annotation.
	 */
	public static ReflectiveFieldType createFieldType(ConnectionSource connectionSource, String tableName, Field field,
													  Class<?> parentClass) throws SQLException {
		DatabaseType databaseType = connectionSource.getDatabaseType();
		DatabaseFieldConfig fieldConfig = DatabaseFieldConfig.fromField(databaseType, tableName, field);
		if (fieldConfig == null) {
			return null;
		} else {
			return new ReflectiveFieldType(connectionSource, tableName, field, fieldConfig, parentClass);
		}
	}

	@Override
	public boolean equals(Object arg) {
		if (arg == null || arg.getClass() != this.getClass()) {
			return false;
		}
		ReflectiveFieldType other = (ReflectiveFieldType) arg;
		return field.equals(other.field)
				&& (parentClass == null ? other.parentClass == null : parentClass.equals(other.parentClass));
	}

	@Override
	public int hashCode() {
		return field.hashCode();
	}

	/**
	 * If we have a class Foo with a collection of Bar's then we go through Bar's DAO looking for a Foo field. We need
	 * this field to build the query that is able to find all Bar's that have foo_id that matches our id.
	 */
	@Override
	protected FieldType findForeignFieldType(Class<?> clazz, Class<?> foreignClass, BaseDaoImpl<?, ?> foreignDao)
			throws SQLException {
		String foreignColumnName = fieldConfig.getForeignCollectionForeignFieldName();
		for (FieldType fieldType : foreignDao.getTableInfo().getFieldTypes()) {
			if (fieldType.getType() == foreignClass
					&& (foreignColumnName == null || fieldType.getFieldName().equals(foreignColumnName))) {
				if (!fieldType.isForeign() && !fieldType.isForeignAutoRefresh()) {
					// this may never be reached
					throw new SQLException("Foreign collection object " + clazz + " for field '" + field.getName()
							+ "' contains a field of class " + foreignClass + " but it's not foreign");
				}
				return fieldType;
			}
		}
		// build our complex error message
		StringBuilder sb = new StringBuilder();
		sb.append("Foreign collection class ").append(clazz.getName());
		sb.append(" for field '").append(field.getName()).append("' column-name does not contain a foreign field");
		if (foreignColumnName != null) {
			sb.append(" named '").append(foreignColumnName).append('\'');
		}
		sb.append(" of class ").append(foreignClass.getName());
		throw new SQLException(sb.toString());
	}

	@Override
	protected Class<?> getForeignCollectionClass(Class<?> parentClass, Class<?> fieldClass) throws SQLException {
		if (fieldClass != Collection.class && !ForeignCollection.class.isAssignableFrom(fieldClass)) {
			throw new SQLException("Field class for '" + fieldConfig.getFieldName() + "' must be of class "
					+ ForeignCollection.class.getSimpleName() + " or Collection.");
		}
		Type type = field.getGenericType();
		if (!(type instanceof ParameterizedType)) {
			throw new SQLException("Field class for '" + fieldConfig.getFieldName() + "' must be a parameterized Collection.");
		}
		Type[] genericArguments = ((ParameterizedType) type).getActualTypeArguments();
		if (genericArguments.length == 0) {
			// i doubt this will ever be reached
			throw new SQLException("Field class for '" + fieldConfig.getFieldName()
					+ "' must be a parameterized Collection with at least 1 type.");
		}

		// If argument is a type variable we need to get arguments from superclass
		if (genericArguments[0] instanceof TypeVariable) {
			genericArguments = ((ParameterizedType) parentClass.getGenericSuperclass()).getActualTypeArguments();
		}

		if (!(genericArguments[0] instanceof Class)) {
			throw new SQLException("Field class for '" + field.getName()
					+ "' must be a parameterized Collection whose generic argument is an entity class not: "
					+ genericArguments[0]);
		}
		return (Class<?>) genericArguments[0];
	}
}
