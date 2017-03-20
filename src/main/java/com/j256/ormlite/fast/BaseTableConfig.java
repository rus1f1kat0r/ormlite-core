package com.j256.ormlite.fast;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class BaseTableConfig<E> extends DatabaseTableConfig<E> {
    private FieldType[] fieldTypes;

    public BaseTableConfig(Class<E> dataClass, String tableName, List<DatabaseFieldConfig> fieldConfigs) {
        super(dataClass, tableName, fieldConfigs);
    }

    public static FieldType[] getFieldTypes(ConnectionSource connectionSource, List<DatabaseFieldConfig> all, Map<String, FieldTypeFactoryHolder> fields) throws SQLException {
        FieldType[] result = new FieldType[all.size()];
        for (int i = 0; i < all.size(); i ++ ) {
            DatabaseFieldConfig each = all.get(i);
            result[i] = fields.get(each.getFieldName())
                    .getFactory().create(each, connectionSource);
        }
        return result;
    }

    @Override
    public void extractFieldTypes(ConnectionSource connectionSource) throws SQLException {
        if (fieldTypes == null) {
            List<DatabaseFieldConfig> fieldConfigs = getFieldConfigs();
            fieldTypes = convertFieldConfigs(connectionSource, fieldConfigs);
        }
    }

    protected abstract FieldType[] convertFieldConfigs(ConnectionSource connectionSource, List<DatabaseFieldConfig> all) throws SQLException;

    @Override
    public List<DatabaseFieldConfig> getFieldConfigs() {
        return super.getFieldConfigs();
    }

    @Override
    public FieldType[] getFieldTypes(DatabaseType databaseType) throws SQLException {
        if (fieldTypes == null) {
            throw new SQLException("Field types have not been extracted in table config");
        }
        return fieldTypes;
    }
}
