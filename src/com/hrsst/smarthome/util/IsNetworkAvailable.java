package com.hrsst.smarthome.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;

public class IsNetworkAvailable {
	
	public static final boolean ping() { 
        String result = null; 
        try { 
                String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网 
                Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次 
                // 读取ping的内容，可以不加 
                InputStream input = p.getInputStream(); 
                BufferedReader in = new BufferedReader(new InputStreamReader(input)); 
                StringBuffer stringBuffer = new StringBuffer(); 
                String content = ""; 
                while ((content = in.readLine()) != null) { 
                        stringBuffer.append(content); 
                }  
                // ping的状态 
                int status = p.waitFor(); 
                if (status == 0) { 
                        result = "success"; 
                        return true; 
                } else { 
                        result = "failed";
                        return false;
                } 
        } catch (Exception e) { 
                result = "IOException"; 
                return false;
        } 
        
	}
	
	public static final boolean isNetworkAvailable(Context mContext) {
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			return manager.getActiveNetworkInfo().isAvailable();
		}else{
			return false;
		}
		
	}
}
