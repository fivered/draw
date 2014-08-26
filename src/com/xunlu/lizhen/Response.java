package com.xunlu.lizhen;

import java.io.Serializable;

public class Response implements Serializable{
	String flag;
	public String getFlag() {
		return flag;
	}
	public boolean isOk() {
		return "success".equals(flag);
	}
}
