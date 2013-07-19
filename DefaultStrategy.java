/** Copyright 2013 BurnerPat (https://github.com/BurnerPat)

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
