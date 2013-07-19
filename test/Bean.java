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

package com.burnerpat.rsreflect.test;

public class Bean {
	public String myString;
	public int myInt;
	private double myDouble;
	private boolean myBoolean;
	
	public Bean() {
		System.out.println("Constructor");
	}
	
	public void setMyString(String myString) {
		System.out.println("setMyString(\"" + myString + "\")");
		this.myString = myString;
	}
	
	public void setMyDouble(double myDouble) {
		System.out.println("setMyDouble(" + myDouble + ")");
		this.myDouble = myDouble;
	}
	
	public String toString() {
		return "{myString = \"" + myString + "\"; myInt = " + myInt + "; myDouble = " + myDouble + "; myBoolean = " + myBoolean + "}";
	}
}
