package com.j256.ormlite.field;

import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseResults;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;

public interface DbField {
	/** default suffix added to fields that are id fields of foreign objects */
	String FOREIGN_ID_FIELD_SUFFIX = "_id";

	void configDaoInformation(ConnectionSource connectionSource, Class<?> parentClass) throws SQLException;

	String getTableName();

	String getFieldName();

	Class<?> getType();

	Type getGenericType();

	String getColumnName();

	DataPersister getDataPersister();

	Object getDataTypeConfigObj();

	SqlType getSqlType();

	Object getDefaultValue();

	int getWidth();

	boolean isCanBeNull();

	boolean isId();

	boolean isGeneratedId();

	boolean isGeneratedIdSequence();

	String getGeneratedIdSequence();

	boolean isForeign();

	void assignField(Object data, Object val, boolean parentObject, ObjectCache objectCache)
			throws SQLException;

	Object assignIdValue(Object data, Number val, ObjectCache objectCache) throws SQLException;

	<FV> FV extractRawJavaFieldValue(Object object) throws SQLException;

	Object extractJavaFieldValue(Object object) throws SQLException;

	Object extractJavaFieldToSqlArgValue(Object object) throws SQLException;

	Object convertJavaFieldToSqlArgValue(Object fieldVal) throws SQLException;

	Object convertStringToJavaField(String value, int columnPos) throws SQLException;

	Object moveToNextValue(Object val) throws SQLException;

	DbField getForeignIdField();

	DbField getForeignRefField();

	boolean isEscapedValue();

	Enum<?> getUnknownEnumVal();

	String getFormat();

	boolean isUnique();

	boolean isUniqueCombo();

	String getIndexName();

	String getUniqueIndexName();

	boolean isEscapedDefaultValue();

	boolean isComparable() throws SQLException;

	boolean isArgumentHolderRequired();

	boolean isForeignCollection();

	<FT, FID> BaseForeignCollection<FT, FID> buildForeignCollection(Object parent, FID id) throws SQLException;

	<T> T resultToJava(DatabaseResults results, Map<String, Integer> columnPositions) throws SQLException;

	boolean isSelfGeneratedId();

	boolean isAllowGeneratedIdInsert();

	String getColumnDefinition();

	boolean isForeignAutoCreate();

	boolean isVersion();

	Object generateId();

	boolean isReadOnly();

	<FV> FV getFieldValueIfNotDefault(Object object) throws SQLException;

	boolean isObjectsFieldValueDefault(Object object) throws SQLException;

	Object getJavaDefaultValueDefault();

	<T> int createWithForeignDao(T foreignData) throws SQLException;

	boolean isForeignAutoRefresh();

	public static class LevelCounters {

		LevelCounters() {
		}

		// current auto-refresh recursion level
		int autoRefreshLevel;
		// maximum auto-refresh recursion level
		int autoRefreshLevelMax;

		// current foreign-collection recursion level
		int foreignCollectionLevel;
		// maximum foreign-collection recursion level
		int foreignCollectionLevelMax;
	}
}
