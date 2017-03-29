package com.ismart.amdemo.entity;

public class FpListData {
	private String fpno;
	private String name;

	public FpListData(String fpno,String name) {
		super();
		this.fpno = fpno;
		this.name = name;
	}

	public String getFpno() {
		return fpno;
	}

	public String getName() {
		return name;
	}
	
}