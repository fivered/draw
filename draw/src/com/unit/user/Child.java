package com.unit.user;

import java.util.List;

import android.graphics.Bitmap;

public class Child {
	
	private String name = null;
	private Bitmap bit = null;
	private int bianhao = -1;
	private float  fenzhi = -1;
	private String date = null;
	
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getBianhao() {
		return bianhao;
	}
	public void setBianhao(int bianhao) {
		this.bianhao = bianhao;
	}
	public float getFenzhi() {
		return fenzhi;
	}
	public void setFenzhi(float fenzhi) {
		this.fenzhi = fenzhi;
	}
	public Bitmap getBit() {
		return bit;
	}
	public void setBit(Bitmap bit) {
		this.bit = bit;
	}
	
	
}
