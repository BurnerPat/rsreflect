package com.burnerpat.rsreflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface AccessStrategy {	
	public boolean accessField(Field field);
	public boolean accessSetter(Method setter);
}
