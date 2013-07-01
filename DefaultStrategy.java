package com.burnerpat.rsreflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultStrategy implements AccessStrategy, NamingStrategy {
	
	@Override
	public boolean accessField(Field field) {
		return true;
	}

	@Override
	public boolean accessSetter(Method setter) {
		return true;
	}

	@Override
	public String getFieldName(String column) throws ReflectionException {
		char[] in = column.toCharArray();
		StringBuilder out = new StringBuilder();
		if (Character.isDigit(in[0])) {
			throw new ReflectionException("the first letter of a name must not be a digit");
		}
		for (int i = 0; i < in.length; i++) {
			if (in[i] == '_') {
				if (++i < in.length) {
					out.append(Character.toUpperCase(in[i]));
				}
			}
			else if (Character.isLetter(in[i]) || Character.isDigit(in[i])) {
				out.append(Character.toLowerCase(in[i]));
			}
			else {
				throw new ReflectionException("invalid character " + in[i] + " in name");
			}
		}
		return out.toString();
	}

	@Override
	public String getSetterName(String field) {
		StringBuilder out = new StringBuilder("set");
		out.append(Character.toUpperCase(field.charAt(0)));
		out.append(field, 1, field.length());
		return out.toString();
	}
}
