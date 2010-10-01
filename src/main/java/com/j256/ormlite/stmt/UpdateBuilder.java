package com.j256.ormlite.stmt;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.table.TableInfo;

/**
 * Assists in building sql UPDATE statements for a particular table in a particular database. Uses the
 * {@link DatabaseType} to get per-database SQL statements.
 * 
 * @param T
 *            The class that the code will be operating on.
 * @param ID
 *            The class of the ID column associated with the class. The T class does not require an ID field. The class
 *            needs an ID parameter however so you can use Void or Object to satisfy the compiler.
 * @author graywatson
 */
public class UpdateBuilder<T, ID> extends StatementBuilder<T, ID> {

	public UpdateBuilder(DatabaseType databaseType, TableInfo<T> tableInfo) {
		super(databaseType, tableInfo, StatementType.UPDATE);
	}

	/**
	 * Build and return a prepared update that can be used by {@link Dao#update(PreparedUpdate)} method. If you change
	 * the where or make other calls you will need to re-call this method to re-prepare the statement for execution.
	 */
	@SuppressWarnings("deprecation")
	public PreparedUpdate<T> prepare() throws SQLException {
		return super.prepareStatement();
	}
}