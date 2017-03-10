package com.tfx.mobilesafe.domain;

public class BlackBean {
	String phone;
	int mode;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int i) {
		this.mode = i;
	}

	@Override
	public String toString() {
		return "BlackBean [phone=" + phone + ", mode=" + mode + "]";
	}

}
