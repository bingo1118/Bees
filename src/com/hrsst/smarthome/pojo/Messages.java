package com.hrsst.smarthome.pojo;

public class Messages {
	private int devType;
	private String location;
	private String alarmDev;
	private String devName;
	private String alarmTime;
	private String devMac;
	private String userNum;
	private int id;
	
	public Messages(int devType, String location, String alarmDev,
			String devName, String alarmTime, String devMac, String userNum,
			int id) {
		super();
		this.devType = devType;
		this.location = location;
		this.alarmDev = alarmDev;
		this.devName = devName;
		this.alarmTime = alarmTime;
		this.devMac = devMac;
		this.userNum = userNum;
		this.id = id;
	}
	public Messages() {
		super();
	}
	public int getDevType() {
		return devType;
	}
	public void setDevType(int devType) {
		this.devType = devType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAlarmDev() {
		return alarmDev;
	}
	public void setAlarmDev(String alarmDev) {
		this.alarmDev = alarmDev;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getUserNum() {
		return userNum;
	}
	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	};
	
}
