package com.cqu.android.bean;

import android.graphics.drawable.Drawable;

public class Application {
	private String name;
	private Drawable icon;
	private String GTraffic;
	private String WTraffic;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getGTraffic() {
		return GTraffic;
	}
	public void setGTraffic(String traffic) {
		GTraffic = traffic;
	}
	public String getWTraffic() {
		return WTraffic;
	}
	public void setWTraffic(String traffic) {
		WTraffic = traffic;
	}
	
}
