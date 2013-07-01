package com.burnerpat.rsreflect.test;

public class Bean {
	public String myString;
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
		return "{myString = \"" + myString + "\"; myDouble = " + myDouble + "; myBoolean = " + myBoolean + "}";
	}
}
