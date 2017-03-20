package com.j256.ormlite.fast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.ObjectFactory;
import com.j256.ormlite.table.TableInfo;

import java.sql.SQLException;

public abstract class BaseFastTableInfo<T, ID> extends TableInfo<T, ID> {
    protected Dao<T, ID> baseDao;

    public BaseFastTableInfo(DatabaseType databaseType, Dao<T, ID> baseDaoImpl, DatabaseTableConfig<T> tableConfig) throws SQLException {
        super(databaseType, baseDaoImpl, tableConfig);
        this.baseDao = baseDaoImpl;
    }

    @Override
    public T createObject() throws SQLException {
        try {
            T instance;
            ObjectFactory<T> factory = null;
            if (baseDao != null) {
                factory = baseDao.getObjectFactory();
            }
            if (factory == null) {
                instance = createEntity();
            } else {
                instance = factory.createObject(null, getDataClass());
            }
            return instance;
        } catch (Exception e) {
            throw SqlExceptionUtil.create("Could not create object for " + getDataClass(), e);
        }
    }

    protected abstract T createEntity();
}
