package com.j256.ormlite.fast;

import com.j256.ormlite.field.DatabaseFieldConfig;

public class FieldTypeFactoryHolder {
    private final DatabaseFieldConfig config;
    private final FieldTypeFactory factory;

    public FieldTypeFactoryHolder(DatabaseFieldConfig config, FieldTypeFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    public DatabaseFieldConfig getConfig() {
        return config;
    }

    public FieldTypeFactory getFactory() {
        return factory;
    }
}
