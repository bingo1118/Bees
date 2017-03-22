package com.hrsst.smarthome.pojo;

import java.io.Serializable;

public class EnvironmentInfo implements Serializable{
	
	public EnvironmentInfo() {
		super();
		this.environmentQuality = 1;
		this.methanal = "--";
		this.temperature = "--";
		this.pm25 = "--";
		this.humidity = "--";
		this.co2 = "--";
	}
	private int environmentQuality;//环境质量 1优 2良 3中 4差
	private String methanal;// 甲醛 mg/m3
	private String temperature;//温度
	private String pm25;// pm2.5  ug/m3
	private String humidity;// 湿度  %
	private String co2;// 二氧化碳  暂时没用到
	
	
	public int getEnvironmentQuality() {
		return environmentQuality;
	}
	public void setEnvironmentQuality(int environmentQuality) {
		this.environmentQuality = environmentQuality;
	}
	public String getMethanal() {
		return methanal;
	}
	public void setMethanal(String methanal) {
		this.methanal = methanal;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getCo2() {
		return co2;
	}
	public void setCo2(String co2) {
		this.co2 = co2;
	}
}
