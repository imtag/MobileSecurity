package com.tfx.mobilesafe.domain;

public class ServicePhoneType {
	String name;
	int out_id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOut_id() {
		return out_id;
	}
	public void setOut_id(int out_id) {
		this.out_id = out_id;
	}
	@Override
	public String toString() {
		return "ServicePhoneType [name=" + name + ", out_id=" + out_id + "]";
	}
	
}
