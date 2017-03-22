package com.hrsst.smarthome.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.MyApp;
import com.hrsst.smarthome.pojo.UpdateInfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;


public class UpdateManager {
	private static final String UPDATE_URL = "http://upg1.cloudlinks.cn/upg/android/";
	public static final int HANDLE_MSG_DOWNING = 0X11;
	public static final int HANDLE_MSG_DOWN_SUCCESS = 0X12;
	public static final int HANDLE_MSG_DOWN_FAULT = 0X13;
	private boolean isDowning = false;
	private String version_server;
	private static UpdateManager manager = null;
	private int download_state;
	private Context mContext;

	private UpdateManager(){};
	
	public synchronized static UpdateManager getInstance(){
		if(null==manager){
			synchronized(UpdateManager.class){
				manager = new UpdateManager();
			}
		}
		return manager;
	}
	
	public boolean getIsDowning(){
		return isDowning;
	}
	
	public void cancelDown(){
		isDowning = false;
	}
	
	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
	
	public void downloadApk(Handler handler,String filePath,String fileName,String downloadPath){
		boolean isSuccess = true;
		int progress = 0;
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				String savePath = Environment.getExternalStorageDirectory()+"/"+filePath;
				File dirfile = new File(savePath);
				if(!dirfile.exists()){
					dirfile.mkdirs();
				}
				
				File apkfile = new File(savePath+"/"+fileName);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkfile));
				URL down_url = new URL(downloadPath);
				HttpURLConnection connection = (HttpURLConnection) down_url.openConnection();
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				int fileLength = connection.getContentLength();
				int downLength = 0;
				int n;
				byte[] buffer = new byte[1024];
				isDowning = true;
				while((n=bis.read(buffer, 0, buffer.length))!=-1){
					if(!isDowning){
						isSuccess = false;
						break;
					}
					bos.write(buffer, 0, n);
					downLength +=n;
					progress = (int) (((float) downLength / fileLength) * 100);
					Message msg = new Message();
					msg.what = HANDLE_MSG_DOWNING;
					msg.arg1 = progress;
					handler.sendMessage(msg);
				}
				bis.close();
				bos.close();
				isDowning = false;
				connection.disconnect();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			isDowning = false;
			isSuccess = false;
			e.printStackTrace();
		} 
		System.out.println("isSuccess=========="+isSuccess);
		Message msg = new Message();
		msg.arg1 = progress;
		if(isSuccess){
			msg.what = HANDLE_MSG_DOWN_SUCCESS;
		}else{
			msg.what = HANDLE_MSG_DOWN_FAULT;
		}
		handler.sendMessage(msg);
	}
	
	
}
