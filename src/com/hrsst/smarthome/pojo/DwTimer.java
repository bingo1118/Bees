package com.hrsst.smarthome.pojo;

public class DwTimer {
	private String dwMac;
	private int sequence;
	private int enable;
	private int repeat;
	private int sun;
	private int mon;
	private int tue;
	private int wed;
	private int thu;
	private int fri;
	private int sat;
	private int socketOnEnable;
	private int socketOffEnable;
	private String socketOnTime;
	private String socketOffTime;
	private String recordeTime;
	public DwTimer() {
		super();
	}
	public DwTimer(String dwMac, int sequence, int enable, int repeat, int sun,
			int mon, int tue, int wed, int thu, int fri, int sat,
			int socketOnEnable, int socketOffEnable, String socketOnTime,
			String socketOffTime,String recordeTime) {
		super();
		this.dwMac = dwMac;
		this.sequence = sequence;
		this.enable = enable;
		this.repeat = repeat;
		this.sun = sun;
		this.mon = mon;
		this.tue = tue;
		this.wed = wed;
		this.thu = thu;
		this.fri = fri;
		this.sat = sat;
		this.socketOnEnable = socketOnEnable;
		this.socketOffEnable = socketOffEnable;
		this.socketOnTime = socketOnTime;
		this.socketOffTime = socketOffTime;
		this.recordeTime = recordeTime;
	}
	public String getDwMac() {
		return dwMac;
	}
	public void setDwMac(String dwMac) {
		this.dwMac = dwMac;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public int getSun() {
		return sun;
	}
	public void setSun(int sun) {
		this.sun = sun;
	}
	public int getMon() {
		return mon;
	}
	public void setMon(int mon) {
		this.mon = mon;
	}
	public int getTue() {
		return tue;
	}
	public void setTue(int tue) {
		this.tue = tue;
	}
	public int getWed() {
		return wed;
	}
	public void setWed(int wed) {
		this.wed = wed;
	}
	public int getThu() {
		return thu;
	}
	public void setThu(int thu) {
		this.thu = thu;
	}
	public int getFri() {
		return fri;
	}
	public void setFri(int fri) {
		this.fri = fri;
	}
	public int getSat() {
		return sat;
	}
	public void setSat(int sat) {
		this.sat = sat;
	}
	public int getSocketOnEnable() {
		return socketOnEnable;
	}
	public void setSocketOnEnable(int socketOnEnable) {
		this.socketOnEnable = socketOnEnable;
	}
	public int getSocketOffEnable() {
		return socketOffEnable;
	}
	public void setSocketOffEnable(int socketOffEnable) {
		this.socketOffEnable = socketOffEnable;
	}
	public String getSocketOnTime() {
		return socketOnTime;
	}
	public void setSocketOnTime(String socketOnTime) {
		this.socketOnTime = socketOnTime;
	}
	public String getSocketOffTime() {
		return socketOffTime;
	}
	public void setSocketOffTime(String socketOffTime) {
		this.socketOffTime = socketOffTime;
	}
	public String getRecordeTime() {
		return recordeTime;
	}
	public void setRecordeTime(String recordeTime) {
		this.recordeTime = recordeTime;
	}
	
}
