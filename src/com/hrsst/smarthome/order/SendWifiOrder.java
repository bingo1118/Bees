package com.hrsst.smarthome.order;

import com.hrsst.smarthome.util.CRC16;
import com.hrsst.smarthome.util.IntegerTo16;

public class SendWifiOrder {
	public static byte[] firstOrder(){
		byte[] mac = "EEEEEEEEEEEE".getBytes();
		byte[] datas = new byte[26];
		byte[] crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0a;
		datas[4] = 0x01;
		datas[5] = 0x01;
		datas[6] = 0x00;
		datas[7] = 0x0D;
		crc[0] = 0x0a;
		crc[1] = 0x01;
		crc[2] = 0x01;
		crc[3] = 0x00;
		crc[4] = 0x0D;
		for(int i=0;i<12;i++){
			datas[8+i] = mac[i];
			crc[5+i] = mac[i];
		}
		datas[20] = 0x02;
		crc[17] =0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	public static byte[] secondOrder(String ssid,byte bFlag,byte[] seq){
		byte[] mac = "EEEEEEEEEEEE".getBytes();
		byte[] datas = new byte[90];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		byte[] ssidData = ssid.getBytes();
		int ssidDataLen = ssidData.length;
		byte[] crc = new byte[82];
		int len = new IntegerTo16().algorismToHEXString(13+ssidDataLen);
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0a;
		datas[4] = bFlag;
		datas[5] = seq[1];
		datas[6] = seq[0];
		datas[7] = (byte) len;
		crc[0]=0x0a;
		crc[1]=bFlag;
		crc[2] = seq[1];
		crc[3] = seq[0];
		crc[4]=(byte) len;
		for(int i=0;i<12;i++){
			datas[8+i]=mac[i];
			crc[5+i] = mac[i];
		}
		datas[20] = 0x02;
		crc[17] = 0x02;
		for(int j=0;j<ssidDataLen;j++){
			datas[21+j] = ssidData[j];
			crc[18+j] = ssidData[j];
		}
		
		if(ssidData.length<64){
			for(int g=0;g<(64-ssidDataLen);g++){
				datas[21+ssidDataLen+g]=0x00;
				crc[18+ssidDataLen+g] = 0x00;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crc));
		datas[85] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[86] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[87] = tail;
		datas[88] = tail2;
		datas[89] = tail3;
		return datas;
	}
	
	public static byte[] ACKOrder(byte[] seq){
		byte[] mac = "EEEEEEEEEEEE".getBytes();
		byte[] datas = new byte[26];
		byte[] crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0a;
		datas[4] = 0x09;
		datas[5] = seq[0];
		datas[6] = seq[1];
		datas[7] = 0x0D;
		crc[0] = 0x0a;
		crc[1] = 0x09;
		crc[2] = seq[0];
		crc[3] = seq[1];
		crc[4] = 0x0D;
		for(int i=0;i<12;i++){
			datas[8+i] = mac[i];
			crc[5+i] = mac[i];
		}
		datas[20] = 0x02;
		crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	public static byte[] WorkMode(){
		byte[] mac = "EEEEEEEEEEEE".getBytes();
		byte[] datas = new byte[27];
		byte[] crc = new byte[19];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0A;
		datas[4] = 0x04;
		datas[5] = 0x04;
		datas[6] = 0x00;
		datas[7] = 0x0E;
		crc[0]= 0x0A;
		crc[1]= 0x04;
		crc[2]= 0x04;
		crc[3]= 0x00;	
		crc[4]= 0x0E;
		for(int i=0;i<12;i++){
			datas[8+i] = mac[i];
			crc[5+i] = mac[i];
		}
		datas[20] = 0x02;
		crc[17] = 0x02;
		datas[21] = 0x01;
		crc[18] = 0x01;
		String srcStr = String.format("%04x", CRC16.calcCrc16(crc));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[23] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[24] = tail;
		datas[25] = tail2;
		datas[26] = tail3;
		return datas;
	}
}
