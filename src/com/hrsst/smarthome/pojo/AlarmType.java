package com.hrsst.smarthome.pojo;

public class AlarmType {
	private String recordTime;
	private String location;
	private String devMac;
	private String mac;

	public AlarmType(String recordTime, String location, String devMac,
			String mac) {
		super();
		this.recordTime = recordTime;
		this.location = location;
		this.devMac = devMac;
		this.mac = mac;
	}
	public AlarmType() {
		super();
	}
	public String getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	
}
