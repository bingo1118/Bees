package com.hrsst.smarthome.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DevOnLineOrOutLine {
	
	//true表示在线，false表示离线
	public boolean onLineOrOutLine(String timeStr){
		SimpleDateFormat sf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d2 = sf.parse(timeStr);
			long devTime = d2.getTime();
			long nowTime = System.currentTimeMillis();
			long i = (nowTime-devTime)/1000;
			if(i<=180){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
}
