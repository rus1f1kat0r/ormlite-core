package com.j256.ormlite.fast;

import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public interface FieldTypeFactory {
    FieldType create(DatabaseFieldConfig config, ConnectionSource connectionSource) throws SQLException;
}
