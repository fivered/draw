package com.xunlu.lizhen;

import java.io.Serializable;

public class SorcererResponse implements Serializable{
	String flag;
	String msg;
	String base_score;
	public String getBase_score() {
		return base_score;
	}
	public String getMsg() {
		return msg;
	}
	public String getFlag() {
		return flag;
	}
	public boolean isOk() {
		return "success".equals(flag);
	}
}
