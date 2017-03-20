package com.j256.ormlite.fast;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public abstract class BaseFieldTypeFactory implements FieldTypeFactory {

    @Override
    public FieldType create(DatabaseFieldConfig config, ConnectionSource connectionSource) throws SQLException {
        DatabaseType databaseType = connectionSource.getDatabaseType();
        if (databaseType.isEntityNamesMustBeUpCase()) {
            config.setFieldName(databaseType.upCaseEntityName(config.getFieldName()));
        }
        return null;
    }
}
