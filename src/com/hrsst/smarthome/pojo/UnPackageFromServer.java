package com.hrsst.smarthome.pojo;

import java.util.List;
import java.util.Map;

public class UnPackageFromServer {
	public byte[] seq;
	public String serverTime;
	public String devHeartTime;
	public String devMac;
	public String devStates;
	public String order;
	public String timerOrder;
	public List<Map<String,DeviceStates>> deviceStatesList;
	public String alarmPos;
	public String clearAlarmMsg;
	public String binderUser;
	public User mUser;
	public String heartPackage;
	public List<UserDevice> userDeviceList;
	public int defence;
	public String binderResult;
	public List<String> macList;
	public List<String> cameraList;
	public Map<String,Integer> map;
	public String devPwd;
	public String result;
	public String devName;
}
