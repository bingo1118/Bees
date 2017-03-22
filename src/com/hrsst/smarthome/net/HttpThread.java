package com.hrsst.smarthome.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

public class HttpThread extends Thread {
	
	private String url;
	private Handler handler;
	String str;
	private String data;
	
	
	
	public HttpThread(String url, Handler handler,String data) {
		this.url = url;
		this.handler = handler;
		this.data=data;
	}



	@Override
	public void run() {
		try {
			URL httpUrl=new URL(url);
//			byte[] entitydata = ("mac=559b5b14&userNum=04045919&devName=呵呵&devType=3").getBytes(); 
			byte[] entitydata = data.getBytes(); 
			HttpURLConnection connection=(HttpURLConnection)httpUrl.openConnection();
			connection.setReadTimeout(5000);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);//@
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
			connection.setRequestProperty("Content-Length", String.valueOf(entitydata.length)); //传递数据的长据  
	        OutputStream outStream = connection.getOutputStream();  
	        outStream.write(entitydata);  
	        //把内存中的数据刷新输送给对方  
	        outStream.flush();  
	        outStream.close();  
			
			
			final StringBuffer sb=new StringBuffer();
			BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"gbk"));
			while((str=reader.readLine())!=null){
				sb.append(str);
			}
			String a=sb.toString();
			JSONObject jsonObject=new JSONObject(a);
			int errorCode=jsonObject.getInt("errorCode");
			Message msg=new Message();
			msg.what=errorCode;
			handler.sendMessage(msg);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Message msg=new Message();
			msg.what=2;
			handler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
			Message msg=new Message();
			msg.what=3;
			handler.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
			Message msg=new Message();
			msg.what=2;
			handler.sendMessage(msg);
		}
		
	}

}
