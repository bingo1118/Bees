package com.hrsst.smarthome.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetServerOrder {
	
	public static List<byte[]> getServerOrder(Set<byte[]> set){
		List<byte[]> li = new ArrayList<byte[]>();
		for(byte[] b : set){
			li.add(b);
		}
		return li;
	}
}
