package com.hrsst.smarthome.pojo;

public class ShareMessages {
	private int id;
	private String toUserNum;
	private String fromUserNum;
	private String dwMac;
	private String time;
	private int isRead;
	
	public ShareMessages(int id, String toUserNum, String fromUserNum,
			String dwMac, String time, int isRead) {
		super();
		this.id = id;
		this.toUserNum = toUserNum;
		this.fromUserNum = fromUserNum;
		this.dwMac = dwMac;
		this.time = time;
		this.isRead = isRead;
	}
	public ShareMessages() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToUserNum() {
		return toUserNum;
	}
	public void setToUserNum(String toUserNum) {
		this.toUserNum = toUserNum;
	}
	public String getFromUserNum() {
		return fromUserNum;
	}
	public void setFromUserNum(String fromUserNum) {
		this.fromUserNum = fromUserNum;
	}
	public String getDwMac() {
		return dwMac;
	}
	public void setDwMac(String dwMac) {
		this.dwMac = dwMac;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
}
