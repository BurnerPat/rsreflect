package com.burnerpat.rsreflect.test;

import java.sql.SQLException;
import java.sql.Types;

import com.burnerpat.rsreflect.RSReflect;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.mockrunner.mock.jdbc.MockResultSetMetaData;

public class MainTest  {	
	public static void main(String[] args) {
		MockResultSet resultSet = new MockResultSet("demo_data");
		resultSet.addColumn("my_string", new String[]{"hello world!", "second value", "third value"});
		resultSet.addColumn("my_double", new Double[]{1.0, 2.0, 3.0});
		resultSet.addColumn("my_boolean", new Boolean[]{true, true, true});
		resultSet.addColumn("unmapped", new String[]{"nothing", "to see", "here"});
		
		try {
			MockResultSetMetaData meta = (MockResultSetMetaData)resultSet.getMetaData();
			meta.setColumnType(1, Types.VARCHAR);
			meta.setColumnType(2, Types.DOUBLE);
			meta.setColumnType(3, Types.BIT);
			meta.setColumnType(4, Types.VARCHAR);
			resultSet.setResultSetMetaData(meta);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			Bean obj = null;
			RSReflect<Bean> reflect = new RSReflect<Bean>(Bean.class);
			while ((obj = reflect.mapNext(resultSet)) != null) {
				System.out.println(obj.toString());
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
