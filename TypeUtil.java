package com.burnerpat.rsreflect;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class TypeUtil {
	private static final Map<Class<?>, Class<?>> WRAPPER_MAP;
	static {
		WRAPPER_MAP = new HashMap<Class<?>, Class<?>>();
		WRAPPER_MAP.put(boolean.class, Boolean.class);
		WRAPPER_MAP.put(byte.class, Byte.class);
		WRAPPER_MAP.put(char.class, Character.class);
		WRAPPER_MAP.put(double.class, Double.class);
		WRAPPER_MAP.put(float.class, Float.class);
		WRAPPER_MAP.put(int.class, Integer.class);
		WRAPPER_MAP.put(long.class, Long.class);
		WRAPPER_MAP.put(short.class, Short.class);
	}
	
	public static Class<?> getFieldType(int columnType) throws ReflectionException {
		switch (columnType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR: {
				return String.class;
			}
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY: {
				return Byte[].class;
			}
			case Types.BIT: {
				return Boolean.class;
			}
			case Types.TINYINT:
			case Types.SMALLINT: {
				return Short.class;
			}
			case Types.INTEGER: {
				return Integer.class;
			}
			case Types.BIGINT: {
				return Long.class;
			}
			case Types.REAL: {
				return Float.class;
			}
			case Types.DOUBLE: {
				return Double.class;
			}
			case Types.DECIMAL:
			case Types.NUMERIC: {
				return BigDecimal.class;
			}
			case Types.DATE: {
				return Date.class;
			}
			case Types.TIME: {
				return Time.class;
			}
			case Types.TIMESTAMP: {
				return Timestamp.class;
			}
			case Types.BLOB: {
				return Blob.class;
			}
			case Types.CLOB: {
				return Clob.class;
			}
			case Types.ARRAY: {
				return Array.class;
			}
			case Types.OTHER: {
				return Object.class;
			}
			default: {
				throw new ReflectionException("unsupported or unknown column type: " + columnType);
			}
		}
	}
	
	public static Object getValue(ResultSet resultSet, int columnIndex) throws ReflectionException, SQLException {
		int columnType = resultSet.getMetaData().getColumnType(columnIndex);
		switch (columnType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR: {
				return resultSet.getString(columnIndex);
			}
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY: {
				return resultSet.getBytes(columnIndex);
			}
			case Types.BIT: {
				return resultSet.getBoolean(columnIndex);
			}
			case Types.TINYINT:
			case Types.SMALLINT: {
				return resultSet.getShort(columnIndex);
			}
			case Types.INTEGER: {
				return resultSet.getInt(columnIndex);
			}
			case Types.BIGINT: {
				return resultSet.getLong(columnIndex);
			}
			case Types.REAL: {
				return resultSet.getFloat(columnIndex);
			}
			case Types.DOUBLE: {
				return resultSet.getDouble(columnIndex);
			}
			case Types.DECIMAL:
			case Types.NUMERIC: {
				return resultSet.getBigDecimal(columnIndex);
			}
			case Types.DATE: {
				return resultSet.getDate(columnIndex);
			}
			case Types.TIME: {
				return resultSet.getTime(columnIndex);
			}
			case Types.TIMESTAMP: {
				return resultSet.getTimestamp(columnIndex);
			}
			case Types.BLOB: {
				return resultSet.getBlob(columnIndex);
			}
			case Types.CLOB: {
				return resultSet.getClob(columnIndex);
			}
			case Types.ARRAY: {
				return resultSet.getArray(columnIndex);
			}
			case Types.OTHER: {
				return resultSet.getObject(columnIndex);
			}
			default: {
				throw new ReflectionException("unsupported or unknown column type: " + columnType);
			}
		}
	}
	
	public static Class<?> getWrapper(Class<?> primitive) throws ReflectionException {
		Class<?> wrapper = WRAPPER_MAP.get(primitive);
		if (wrapper != null) {
			return wrapper;
		}
		else {
			throw new ReflectionException(primitive.getName() + " is not a primitive type");
		}
	}
}
