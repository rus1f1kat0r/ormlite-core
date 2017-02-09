package com.j256.ormlite.stmt;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Before;

import com.j256.ormlite.BaseCoreTest;
import com.j256.ormlite.field.DbField;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.table.TableInfo;

public abstract class BaseCoreStmtTest extends BaseCoreTest {

	protected TableInfo<Foo, Integer> baseFooTableInfo;
	protected DbField numberDbField;
	protected DbField stringDbField;
	protected DbField foreignDbField;

	@Override
	@Before
	public void before() throws Exception {
		super.before();

		Field field = Foo.class.getDeclaredField("stringField");
		assertEquals(String.class, field.getType());
		stringDbField = FieldType.createFieldType(connectionSource, "BaseFoo", field, Foo.class);
		stringDbField.configDaoInformation(connectionSource, Foo.class);
		field = Foo.class.getDeclaredField("val");
		assertEquals(int.class, field.getType());
		numberDbField = FieldType.createFieldType(connectionSource, "BaseFoo", field, Foo.class);
		numberDbField.configDaoInformation(connectionSource, Foo.class);
		field = Foreign.class.getDeclaredField("foo");
		assertEquals(Foo.class, field.getType());
		foreignDbField = FieldType.createFieldType(connectionSource, "BaseFoo", field, Foreign.class);
		foreignDbField.configDaoInformation(connectionSource, Foreign.class);

		baseFooTableInfo = new TableInfo<Foo, Integer>(connectionSource, null, Foo.class);
	}
}
