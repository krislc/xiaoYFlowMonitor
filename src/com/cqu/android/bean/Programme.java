package com.cqu.android.bean;

import java.util.Calendar;

import android.graphics.drawable.Drawable;

public class Programme {
	
	//图标  
	private Drawable icon;
	
	//程序名  
	private String name;
	
	//程序Uid
	private int uid;
	
	//程序上行值
	private long send;
	
	//程序下行值
	private long receive;
	
	//程序的类型
	private String netType;
	
	//程序连网时间
	private Calendar linkDate;
	
	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getSend() {
		return send;
	}

	public void setSend(long send) {
		this.send = send;
	}

	public long getReceive() {
		return receive;
	}

	public void setReceive(long receive) {
		this.receive = receive;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public Calendar getLinkDate() {
		return linkDate;
	}

	public void setLinkDate(Calendar linkDate) {
		this.linkDate = linkDate;
	}

}