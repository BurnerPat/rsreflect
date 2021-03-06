/* Copyright 2013 BurnerPat (https://github.com/BurnerPat)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.burnerpat.rsreflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RSReflect<T> {
	
	public static final DefaultStrategy DEFAULT_STRATEGY = new DefaultStrategy();
	
	private final Class<T> clazz;
	private final NamingStrategy namingStrategy;
	private final AccessStrategy accessStrategy;
	
	private Map<String, Object> map;
	
	public RSReflect(Class<T> clazz) {
		this(clazz, DEFAULT_STRATEGY, DEFAULT_STRATEGY);
	}
	
	public RSReflect(Class<T> clazz, NamingStrategy namingStrategy) {
		this(clazz, namingStrategy, DEFAULT_STRATEGY);
	}
	
	public RSReflect(Class<T> clazz, AccessStrategy accessStrategy) {
		this(clazz, DEFAULT_STRATEGY, accessStrategy);
	}
	
	public RSReflect(Class<T> clazz, NamingStrategy namingStrategy, AccessStrategy accessStrategy) {
		this.clazz = clazz;
		this.namingStrategy = namingStrategy;
		this.accessStrategy = accessStrategy;
	}
	
	public void invalidate() {
		map = null;
	}
	
	public void prepareMap(ResultSet resultSet) throws SQLException, ReflectionException {
		map = prepare(resultSet);
	}
	
	private Map<String, Object> prepare(ResultSet resultSet) throws SQLException, ReflectionException {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultSetMetaData meta = resultSet.getMetaData();
		
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String column = meta.getColumnName(i);
			try {
				String fieldName = namingStrategy.getFieldName(column);
				Field field = clazz.getDeclaredField(fieldName);
				if (accessStrategy.accessField(field)) {
					try {
						assertFieldType(meta.getColumnType(i), field.getType());
					}
					catch (ReflectionException ex) {
						throw new ReflectionException("exception for field " + fieldName, ex);
					}
					if (!Modifier.isPublic(field.getModifiers())) {
						Method setter = clazz.getDeclaredMethod(namingStrategy.getSetterName(fieldName), field.getType());
						if (Modifier.isPublic(setter.getModifiers()) && accessStrategy.accessSetter(setter)) {
							result.put(column, setter);
						}
						else {
							result.put(column, null);
						}
					}
					else {
						result.put(column, field);
					}
				}
				else {
					result.put(column, null);
				}
			}
			catch (NoSuchFieldException ex) {
				result.put(column, null);
			}
			catch (NoSuchMethodException ex) {
				result.put(column, null);
			}
			catch (SecurityException ex) {
				result.put(column, null);
			}
		}
		
		return result;
	}
	
	private void assertFieldType(int columnType, Class<?> fieldType) throws ReflectionException {
		Class<?> targetType = TypeUtil.getFieldType(columnType);
		if (!fieldType.isPrimitive()) {
			if (!fieldType.isAssignableFrom(targetType)) {
				throw new ReflectionException("cannot cast from " + targetType.getName() + " to " + fieldType.getName());
			}
		}
		else {
			Class<?> wrapper = TypeUtil.getWrapper(fieldType);
			if (!wrapper.isAssignableFrom(targetType)) {
				throw new ReflectionException("cannot cast from " + targetType.getName() + " to " + wrapper.getName());
			}
		}
	}
	
	public List<T> mapAll(ResultSet resultSet) throws SQLException, ReflectionException {
		LinkedList<T> list = new LinkedList<T>();
		T obj = null;
		
		while ((obj = mapNext(resultSet)) != null) {
			list.add(obj);
		}
		return list;
	}
	
	public T mapNext(ResultSet resultSet) throws SQLException, ReflectionException {
		if (resultSet.next()) {
			return mapRow(resultSet);
		}
		else {
			return null;
		}
	}
	
	public T mapRow(ResultSet resultSet) throws SQLException, ReflectionException {
		if (map == null) {
			map = prepare(resultSet);
		}
		T obj = null;
		
		try {
			obj = clazz.newInstance();
		}
		catch (IllegalAccessException ex) {
			throw new ReflectionException("unable to instantiate object", ex);
		}
		catch (InstantiationException ex) {
			throw new ReflectionException("unable to instantiate object", ex);
		}
		
		Iterator<String> iterator = map.keySet().iterator();
		for (int i = 0; i < map.size(); i++) {
			String column = iterator.next();
			Object accessor = map.get(column);
			try {
				if (accessor != null) {
					if (accessor instanceof Field) {
						Field field = (Field)accessor;
						try {
							Class<?> type = field.getType();
							Object value = TypeUtil.getValue(resultSet, i + 1);
							if (!type.isPrimitive()) {
								field.set(obj, type.cast(value));
							}
							else {
								if (type.equals(boolean.class)) {
									field.set(obj, ((Boolean)value).booleanValue());
								} 
								else if (type.equals(byte.class)) {
									field.set(obj, ((Byte)value).byteValue());
								} 
								else if (type.equals(char.class)) {
									field.set(obj, ((Character)value).charValue());
								} 
								else if (type.equals(double.class)) {
									field.set(obj, ((Double)value).doubleValue());
								}
								else if (type.equals(float.class)) {
									field.set(obj, ((Float)value).floatValue());
								} 
								else if (type.equals(int.class)) {
									field.set(obj, ((Integer)value).intValue());
								} 
								else if (type.equals(long.class)) {
									field.set(obj, ((Long)value).longValue());
								} 
								else if (type.equals(short.class)) {
									field.set(obj, ((Short)value).shortValue());
								} 
							}
						}
						catch (IllegalAccessException ex) {
							throw new ReflectionException("illegal access for field " + field.getName() + " of " + clazz.getName(), ex);
						}
						catch (ClassCastException ex) {
							throw new ReflectionException("cast error for field " + field.getName() + " of " + clazz.getName(), ex);
						}
					}
					else if (accessor instanceof Method) {
						Method setter = (Method)accessor;
						try {
							Class<?> parameter = setter.getParameterTypes()[0];
							Object value = TypeUtil.getValue(resultSet, i + 1);
							if (!parameter.isPrimitive()) {
								setter.invoke(obj, parameter.cast(value));
							}
							else {
								if (parameter.equals(boolean.class)) {
									setter.invoke(obj, ((Boolean)value).booleanValue());
								} 
								else if (parameter.equals(byte.class)) {
									setter.invoke(obj, ((Byte)value).byteValue());
								} 
								else if (parameter.equals(char.class)) {
									setter.invoke(obj, ((Character)value).charValue());
								} 
								else if (parameter.equals(double.class)) {
									setter.invoke(obj, ((Double)value).doubleValue());
								}
								else if (parameter.equals(float.class)) {
									setter.invoke(obj, ((Float)value).floatValue());
								} 
								else if (parameter.equals(int.class)) {
									setter.invoke(obj, ((Integer)value).intValue());
								} 
								else if (parameter.equals(long.class)) {
									setter.invoke(obj, ((Long)value).longValue());
								} 
								else if (parameter.equals(short.class)) {
									setter.invoke(obj, ((Short)value).shortValue());
								} 
							}
						}
						catch (IllegalAccessException ex) {
							throw new ReflectionException("illegal access for method " + setter.getName() + " on " + clazz.getName(), ex);
						}
						catch (IllegalArgumentException ex) {
							throw new ReflectionException("illegal argument for method " + setter.getName() + " on " + clazz.getName(), ex);
						}
						catch (InvocationTargetException ex) {
							throw new ReflectionException("invocation target error for method " + setter.getName() + " on " + clazz.getName(), ex);
						}
						catch (ClassCastException ex) {
							throw new ReflectionException("cast error for method " + setter.getName() + " on " + clazz.getName(), ex);
						}
					}
				}
			}
			catch (ReflectionException ex) {
				throw new ReflectionException("error with column " + column, ex);
			}
		}
		
		return obj;
	}
}
