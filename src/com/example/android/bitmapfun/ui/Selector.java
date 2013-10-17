package com.example.android.bitmapfun.ui;

import java.util.Date;

public class Selector {
	private Date startDate;
	private Date endDate;
	private int maxNumberToReturn = Integer.MAX_VALUE;
	
	public Selector(Date s, Date e) {
		this.startDate = s;
		this.endDate = e;
	}
	
	public Selector() {
	}
	
	public Selector(Date s, Date e, int m) {
		this(s,e);
		this.maxNumberToReturn = m;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

}
