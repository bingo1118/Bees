package com.hrsst.smarthome.util;

public class To16AndAddByte {
	
	public byte[] addByte(byte[] c,byte[] c1){
    	byte[] c2=new byte[c.length+c1.length];
    	for (int i = 0; i < c2.length; i++) {
    	if(i<c.length){
    		c2[i]=c[i];
    		}else{
    		c2[i]=c1[i-c.length];
    		}
    	}
		return c2;
	}
	
	public String to16(String str){
		return String.format("0x%04x", str);
	}
	
	 public byte uniteBytes(byte src0, byte src1) {  
	        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();  
	        _b0 = (byte)(_b0 << 4);  
	        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();  
	        byte ret = (byte)(_b0 ^ _b1);  
	        return ret;  
	 }   
	      
	  /** 
	   * 将指定字符串src，以每两个字符分割转换为16进制形式 
	   * 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF, 0xD9} 
	   * @param src String 
	   * @return byte[] 
	   */  
	  public byte[] HexString2Bytes(String src){  
	    byte[] ret = new byte[src.length()/2];  
	    byte[] tmp = src.getBytes();  
	    for(int i=0; i< tmp.length/2; i++){  
	      ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);  
	    }  
	    return ret;  
	  }  
}

