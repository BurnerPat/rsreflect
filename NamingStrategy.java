package com.burnerpat.rsreflect;

public interface NamingStrategy {
	public String getFieldName(String column) throws ReflectionException;
	public String getSetterName(String field) throws ReflectionException;
}
