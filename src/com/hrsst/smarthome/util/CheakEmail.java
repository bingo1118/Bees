package com.hrsst.smarthome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheakEmail {
	private static CheakEmail manager = null;
	private CheakEmail(){}
	
	public synchronized static CheakEmail getInstance(){
		if(null==manager){
			synchronized(SharedPreferencesManager.class){
				if(null==manager){
					manager = new CheakEmail();
				}
			}
		}
		return manager;
	}
	
	public boolean cheakEmail(String email){
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";    
	    Pattern regex = Pattern.compile(check);    
	    Matcher matcher = regex.matcher(email);    
	    boolean isMatched = matcher.matches();
	    return isMatched;
	}
}
