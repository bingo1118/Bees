package com.hrsst.smarthome.pojo;

public class DeviceStates {
	private String mac;//macµØÖ·
	private int lightStates;//¿ª/¹Ø×´Ì¬socket
	private int socketStates;
	private String newTime;
	private String deviceName;
	private int lightOnOrOutLine;
	
	public DeviceStates() {
		super();
	}
	
	public DeviceStates(String mac, int lightStates, int socketStates,
			String newTime, String deviceName, int lightOnOrOutLine) {
		super();
		this.mac = mac;
		this.lightStates = lightStates;
		this.socketStates = socketStates;
		this.newTime = newTime;
		this.deviceName = deviceName;
		this.lightOnOrOutLine = lightOnOrOutLine;
	}

	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getLightStates() {
		return lightStates;
	}
	public void setLightStates(int lightStates) {
		this.lightStates = lightStates;
	}
	public int getSocketStates() {
		return socketStates;
	}
	public void setSocketStates(int socketStates) {
		this.socketStates = socketStates;
	}
	public String getNewTime() {
		return newTime;
	}
	public void setNewTime(String newTime) {
		this.newTime = newTime;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getLightOnOrOutLine() {
		return lightOnOrOutLine;
	}

	public void setLightOnOrOutLine(int lightOnOrOutLine) {
		this.lightOnOrOutLine = lightOnOrOutLine;
	}
	
	
}
