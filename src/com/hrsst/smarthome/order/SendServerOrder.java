package com.hrsst.smarthome.order;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.CRC16;
import com.hrsst.smarthome.util.IntegerTo16;

public class SendServerOrder {
	
	/**
	 * 手机回复包
	 * @param mac
	 * @param seq
	 * @return
	 */
	public static byte[] ClientACKOrder(String mac,byte[] seq){
		byte[] datas = new byte[26];
		byte[] macByte = mac.getBytes();
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x09;
		datas[5] = seq[0];
		datas[6] = seq[1];
		datas[7] = 0x0D;
		Crc[0] = 0x0C;
		Crc[1] = 0x09;
		Crc[2] = seq[0];
		Crc[3] = seq[1];
		Crc[4] = 0x0D;
		for(int i=0;i<12;i++){
			datas[8+i] = macByte[i];
			Crc[5+i] = macByte[i];
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	//seq=12
	public static byte[] OpenOrCloseOrder(String mac,byte states[]){
		byte[] datas = new byte[34];
		byte[] macByte = mac.getBytes();
		byte[] crc = new byte[26];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x05;
		datas[5] = 0x00;
		datas[6] = 0x0C;
		datas[7] = 0x15;
		crc[0]=0x0C;
		crc[1]=0x05;
		crc[2]=0x00;
		crc[3]=0x0C;
		crc[4]=0x15;
		for(int i=0;i<12;i++){
			datas[8+i] = macByte[i];
			crc[5+i]=macByte[i];
		}
		datas[20] = 0x02;
		crc[17]=0x02;
		for(int j=0;j<8;j++){
			crc[18+j]=states[j];
			datas[21+j] = states[j];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crc));
		datas[29] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[30] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));	
		datas[31] = tail;
		datas[32] = tail2;
		datas[33] = tail3;
		return datas;
	}
	
	//seq=11
	public static byte[] StudyOrder(String mac,byte[] location,byte type){
		byte[] datas = new byte[77];
		byte[] macByte = mac.getBytes();
		byte[] Crc = new byte[69];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x0C;
		datas[5] = 0x00;
		datas[6] = 0x0B;
		datas[7] = 0x40;
		Crc[0] = 0x0C;
		Crc[1] = 0x0C;
		Crc[2] = 0x00;
		Crc[3] = 0x0B;
		Crc[4] = 0x40;
		for(int i=0;i<12;i++){
			datas[8+i] = macByte[i];
			Crc[5+i] = macByte[i];
		}
		Crc[17] = 0x02;
		Crc[18] = type;
		datas[20] = 0x02;
		datas[21] = type;
		for(int j=0;j<50;j++){
			datas[22+j] = location[j];
			Crc[19+j] = location[j];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[72] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[73] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));	
		datas[74] = tail;
		datas[75] = tail2;
		datas[76] = tail3;
		return datas;
	}
	
	//seq=10
	public static byte[] TimerOrder(String mac,byte[] timerStates){
		byte[] datas = new byte[44];
		byte[] macByte = mac.getBytes();
		byte[] Crc = new byte[36];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x08;
		datas[5] = 0x00;
		datas[6] = 0x0A;
		datas[7] = 0x1F;
		Crc[0] = 0x0C;
		Crc[1] = 0x08;
		Crc[2] = 0x00;
		Crc[3] = 0x0A;
		Crc[4] = 0x1F;
		for(int i=0;i<12;i++){
			datas[8+i] = macByte[i];
			Crc[5+i] = macByte[i];
		}
		Crc[17] = 0x02;
		datas[20] = 0x02;
		for(int j=0;j<18;j++){
			Crc[18+j] = timerStates[j];
			datas[21+j] = timerStates[j];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[39] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[40] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));	
		datas[41] = tail;
		datas[42] = tail2;
		datas[43] = tail3;
		return datas;
	}
	
	//设备状态获取包 seq=9
	public static byte[] GetDeviceStates(String mac){
		byte[] datas = new byte[26];
		byte[] macByte = mac.getBytes();
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x02;
		datas[5] = 0x00;
		datas[6] = 0x09;
		datas[7] = 0x0D;
		Crc[0] = 0x0C;
		Crc[1] = 0x02;//7B5B5D0C020D3138666533346436383939640290E35B5D7D
		Crc[2] = 0x00;
		Crc[3] = 0x09;
		Crc[4] = 0x0D;
		for(int i=0;i<12;i++){
			datas[8+i] = macByte[i];
			Crc[5+i] = macByte[i];
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;//90 E3
		return datas;
	}
	
	//设备状态获取包 seq=8
	public static byte[] GetDeviceStatesList(List<String> mac){
		int listTotal = mac.size();
		int macToal = listTotal*12;
		byte[] datas = new byte[15+macToal];
		byte[] macByte = new byte[macToal];
		for(int j=0;j<listTotal;j++){
			for(int g=0;g<12;g++){
				macByte[j*12+g] = mac.get(j).getBytes()[g];
			}
		}
		byte[] Crc = new byte[7+macToal];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x02;
		datas[5] = 0x00;
		datas[6] = 0x08;
		datas[7] = new IntegerTo16().algorismToHEXString(2+macToal);
		datas[8] = 0x02;
		datas[9] = new IntegerTo16().algorismToHEXString(listTotal);
		Crc[0] = 0x0C;
		Crc[1] = 0x02;
		Crc[2] = 0x00;
		Crc[3] = 0x08;
		Crc[4] = datas[7];
		Crc[5] = 0x02;
		Crc[6] = datas[9];
		for(int i=0;i<macToal;i++){
			datas[10+i] = macByte[i];
			Crc[7+i] = macByte[i];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[10+macToal] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[11+macToal] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[12+macToal] = tail;
		datas[13+macToal] = tail2;
		datas[14+macToal] = tail3;//90 E3
		return datas;
	}
	
	
	public static byte[] GetLocation(String location){
		byte[] datas = new byte[50];
		try {
			byte[] lo = location.getBytes("utf-8");
			int loInt = lo.length;
			for(int i=0;i<loInt;i++){
				datas[i] = lo[i];
			}
			if(loInt>=50){
				return datas;
			}else{
				for(int j=0;j<(50-loInt);j++){
					datas[loInt+j] = -0;
				}
				return datas;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//共享设备包 seq=7
	public static byte[] ShareDev(String mac,String formUserNum,String toUserNum){
		byte[] datas = new byte[68];
		byte[] macByte = mac.getBytes();
		byte[] Crc = new byte[60];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x12;
		datas[5] = 0x00;
		datas[6] = 0x07;
		datas[7] = 0x37;
		Crc[0] = 0x0C;
		Crc[1] = 0x12;
		Crc[2] = 0x00;
		Crc[3] = 0x07;
		Crc[4] = 0x37;
		int macByteLen = macByte.length;
		for(int i=0;i<macByteLen;i++){
			datas[8+i] = macByte[i];
			Crc[5+i] = macByte[i];
		}
		int macLen = 12-macByteLen;
		if(macLen>0){
			for(int i=0;i<macLen;i++){
				datas[8+macByteLen+i] = -0;
				Crc[5+macByteLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		byte[] fromUserNumByte = formUserNum.getBytes();
		int fromLen = fromUserNumByte.length;
		for(int j=0;j<fromLen;j++){
			datas[21+j] = fromUserNumByte[j];
			Crc[18+j] = fromUserNumByte[j];
		}
		if(fromLen<12){
			for(int g=0;g<(12-fromLen);g++){
				datas[21+fromLen+g] = -0;
				Crc[18+fromLen+g] = -0;
			}
		}
		byte[] userByte = toUserNum.getBytes();
		int len = userByte.length;
		for(int j=0;j<len;j++){
			datas[33+j] = userByte[j];
			Crc[30+j] = userByte[j];
		}
		if(len<30){
			for(int g=0;g<(30-len);g++){
				datas[33+len+g] = -0;
				Crc[30+len+g] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[63] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[64] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[65] = tail;
		datas[66] = tail2;
		datas[67] = tail3;
		return datas;
	}
	
	//同意共享
	public static byte[] AgreeShare(String num){
		byte[] datas = new byte[24];
		byte[] Crc = new byte[16];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x11;
		datas[5] = 0x0B;
		datas[6] = 0x00;
		datas[7] = 0x0B;
		Crc[0] = 0x0C;
		Crc[1] = 0x11;
		Crc[2] = 0x0B;
		Crc[3] = 0x00;
		Crc[4] = 0x0B;
		datas[8] = 0x02;
		Crc[5] = 0x02;
		byte[] numByte = num.getBytes();
		int len = numByte.length;
		int byLen = 10-len;
		if(byLen > 0){
			for(int i=0;i<byLen;i++){
				datas[9+i] = "0".getBytes()[0];
				Crc[6+i] = "0".getBytes()[0];
			}
		}
		for(int j=0;j<len;j++){
			datas[9+byLen+j] = numByte[j];
			Crc[6+byLen+j] = numByte[j];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[19] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[20] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[21] = tail;
		datas[22] = tail2;
		datas[23] = tail3;
		return datas;
	}
	
	//修改报警器位置 seq=0E
	public static byte[] ModifyAlarmName(String alarmMac,String location){
		byte[] datas = new byte[76];
		byte[] Crc = new byte[68];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x98;
		datas[5] = 0x0E;
		datas[6] = 0x00;
		datas[7] = 0x3F;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x98;
		Crc[2] = 0x0E;
		Crc[3] = 0x00;
		Crc[4] = 0x3F;
		byte[] alarmMacByte = alarmMac.getBytes();
		byte[] locationByte = location.getBytes();
		int alarmMacLen = alarmMacByte.length;
		int locationByteLen = locationByte.length;
		for(int i=0;i<alarmMacLen;i++){
			datas[8+i] = alarmMacByte[i];
			Crc[5+i] = alarmMacByte[i];
		}
		if(alarmMacLen<12){
			int zeroLen = 12-alarmMacLen;
			for(int j=0;j<zeroLen;j++){
				datas[8+alarmMacLen+j] = -0;
				Crc[5+alarmMacLen+j] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		for(int i=0;i<locationByteLen;i++){
			datas[21+i] = locationByte[i];
			Crc[18+i] = locationByte[i];
		}
		if(locationByteLen<50){
			int zeroLen = 50-locationByteLen;
			for(int j=0;j<zeroLen;j++){
				datas[21+locationByteLen+j] = -0;
				Crc[18+locationByteLen+j] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[71] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[72] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[73] = tail;
		datas[74] = tail2;
		datas[75] = tail3;
		return datas;
	}
	
	//清空报警消息表数据 seq=6
	public static byte[] ClearAlarmMessage(String userID){
		byte[] datas = new byte[26];
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x10;
		datas[5] = 0x00;
		datas[6] = 0x06;
		datas[7] = 0x0D;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x10;
		Crc[2] = 0x00;
		Crc[3] = 0x06;
		Crc[4] = 0x0D;
		byte[] userIDByte = userID.getBytes();
		int userIDLen = userIDByte.length;
		for(int i=0;i<userIDLen;i++){
			datas[8+i] = userIDByte[i];
			Crc[5+i] = userIDByte[i];
		}
		int len = 12-userIDLen;
		if(len>0){
			for(int j=0;j<len;j++){
				datas[8+userIDLen+j] = -0;
				Crc[5+userIDLen+j] = -0;
			}
		}
		Crc[17] = 02;
		datas[20] = 02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	//绑定用户  seq=5
	public static byte[] BinderUser(String userID,String devMac){
		byte[] datas = new byte[38];
		byte[] Crc = new byte[30];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x14;
		datas[5] = 0x00;
		datas[6] = 0x05;
		datas[7] = 0x19;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x14;
		Crc[2] = 0x00;
		Crc[3] = 0x05;
		Crc[4] = 0x19;
		byte[] devMacByte = devMac.getBytes();
		byte[] userIDByte = userID.getBytes();
		int userIDLen = userIDByte.length;
		int devMacLen = devMacByte.length;
		for(int i=0;i<devMacLen;i++){
			datas[8+i] = devMacByte[i];
			Crc[5+i] = devMacByte[i];
		}
		int devMacByteLen = 12-devMacLen;
		if(devMacByteLen>0){
			for(int i=0;i<devMacByteLen;i++){
				datas[8+devMacLen+i] = -0;
				Crc[5+devMacLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		for(int i=0;i<userIDLen;i++){
			datas[21+i] = userIDByte[i];
			Crc[18+i] = userIDByte[i];
		}
		int userIDByteLen = 12-userIDLen;
		if(userIDByteLen>0){
			for(int i=0;i<userIDByteLen;i++){
				datas[21+userIDLen+i] = -0;
				Crc[18+userIDLen+i] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[33] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[34] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[35] = tail;
		datas[36] = tail2;
		datas[37] = tail3;
		return datas;
	}
	
	//用户登录 seq=4
	public static byte[] LoginUser(String userID,String pwd,byte type){
		byte[] datas = new byte[77];
		byte[] Crc = new byte[69];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x16;
		datas[5] = 0x00;
		datas[6] = 0x04;
		datas[7] = 0x40;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x16;
		Crc[2] = 0x00;
		Crc[3] = 0x04;
		Crc[4] = 0x40;
		datas[8] = type;
		Crc[5] = type;
		datas[9] = 0x02;
		Crc[6] = 0x02;
		byte[] userIDByte = userID.getBytes();
		int userIDByteLen = userIDByte.length;
		for(int i=0;i<userIDByteLen;i++){
			datas[10+i] = userIDByte[i];
			Crc[7+i] = userIDByte[i];
		}
		int userIDLen = 30-userIDByteLen;
		if(userIDLen>0){
			for(int i=0;i<userIDLen;i++){
				datas[10+userIDByteLen+i] = -0;
				Crc[7+userIDByteLen+i] = -0;
			}
		}
		byte[] pwdByte = pwd.getBytes();
		int pwdByteLen = pwdByte.length;
		for(int i=0;i<pwdByteLen;i++){
			datas[40+i] = pwdByte[i];
			Crc[37+i] = pwdByte[i];
		}
		int pwdLen = 32-pwdByteLen;
		if(pwdLen>0){
			for(int i=0;i<pwdLen;i++){
				datas[40+pwdByteLen+i] = -0;
				Crc[37+pwdByteLen+i] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[72] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[73] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[74] = tail;
		datas[75] = tail2;
		datas[76] = tail3;
		return datas;
	}
	
	//心跳包 seq=3
	public static byte[] HearPackage(String userID,String sessionId){
		byte[] datas = new byte[48];
		byte[] Crc = new byte[40];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x18;
		datas[5] = 0x00;
		datas[6] = 0x03;
		datas[7] = 0x23;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x18;
		Crc[2] = 0x00;
		Crc[3] = 0x03;
		Crc[4] = 0x23;
		byte[] userIDByte = userID.getBytes();
		int userIDByteLen = userIDByte.length;
		for(int i=0;i<userIDByteLen;i++){
			datas[8+i] = userIDByte[i];
			Crc[5+i] = userIDByte[i];
		}
		int userIDLen = 12-userIDByteLen;
		if(userIDLen>0){
			for(int i=0;i<userIDLen;i++){
				datas[8+userIDByteLen+i] = -0;
				Crc[5+userIDByteLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		byte[] sessionIdByte = sessionId.getBytes();
		int sessionIdByteLen = sessionIdByte.length;
		for(int i=0;i<sessionIdByteLen;i++){
			datas[21+i] = sessionIdByte[i];
			Crc[18+i] = sessionIdByte[i];
		}
		int sessionIdLen = 22-sessionIdByteLen;
		if(sessionIdLen>0){
			for(int i=0;i<sessionIdLen;i++){
				datas[21+sessionIdByteLen+i] =-0;
				Crc[18+sessionIdByteLen+i] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[43] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[44] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[45] = tail;
		datas[46] = tail2;
		datas[47] = tail3;
		return datas;
	}
	
	//获取服务器时间 seq=1
	public static byte[] GetServerTimePackage(){
		byte[] datas = new byte[14];
		byte[] Crc = new byte[6];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x30;
		datas[5] = 0x00;
		datas[6] = 0x01;
		datas[7] = 0x01;
		datas[8] = 0x02;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x30;
		Crc[2] = 0x00;
		Crc[3] = 0x01;
		Crc[4] = 0x01;
		Crc[5] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[9] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[10] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[11] = tail;
		datas[12] = tail2;
		datas[13] = tail3;
		return datas;
	}
	
	//获取设备心跳时间 seq=2
	public static byte[] GetDevHeartTimePackage(String devMac){
		byte[] datas = new byte[26];
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x32;
		datas[5] = 0x00;
		datas[6] = 0x02;
		datas[7] = 0x0D;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x32;
		Crc[2] = 0x00;
		Crc[3] = 0x02;
		Crc[4] = 0x0D;
		byte[] devMacByte = devMac.getBytes();
		for(int i=0;i<12;i++){
			datas[8+i] = devMacByte[i];
			Crc[5+i] = devMacByte[i];
		}
		Crc[17] = 0x02;
		datas[20] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	//获取用户设备 seq=0C
	public static byte[] GetUserDev(String userId){
		byte[] datas = new byte[26];
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x26;
		datas[5] = 0x00;
		datas[6] = 0x0C;
		datas[7] = 0x0D;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x26;
		Crc[2] = 0x00;
		Crc[3] = 0x0C;
		Crc[4] = 0x0D;
		byte[] userIdByte = userId.getBytes();
		int len = userIdByte.length;
		for(int i=0;i<len;i++){
			datas[8+i] = userIdByte[i];
			Crc[5+i] = userIdByte[i];
		}
		int userIdLen = 12-len;
		if(userIdLen>0){
			for(int i=0;i<userIdLen;i++){
				datas[8+len+i] = -0;
				Crc[5+len+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	//设备布防/撤防 seq=0D
	public static byte[] Defence(String userId,String devMac,byte defence){
		byte[] datas = new byte[39];
		byte[] Crc = new byte[31];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x34;
		datas[5] = 0x00;
		datas[6] = 0x0D;
		datas[7] = 0x1A;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x34;
		Crc[2] = 0x00;
		Crc[3] = 0x0D;
		Crc[4] = 0x1A;
		byte[] userIdByte = userId.getBytes();
		int len = userIdByte.length;
		for(int i=0;i<len;i++){
			datas[8+i] = userIdByte[i];
			Crc[5+i] = userIdByte[i];
		}
		int userIdLen = 12-len;
		if(userIdLen>0){
			for(int i=0;i<userIdLen;i++){
				datas[8+len+i] = -0;
				Crc[5+len+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		datas[21] = defence;
		Crc[18] = defence;
		byte[] devMacByte = devMac.getBytes();
		int devMacByteLen = devMacByte.length;
		for(int i=0;i<devMacByteLen;i++){
			datas[22+i] = devMacByte[i];
			Crc[19+i] = devMacByte[i];
		}
		int devMacLen = 12-devMacByteLen;
		if(devMacLen>0){
			for(int i=0;i<devMacLen;i++){
				datas[22+devMacByteLen+i] = -0;
				Crc[19+devMacByteLen+i] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[34] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[35] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[36] = tail;
		datas[37] = tail2;
		datas[38] = tail3;
		return datas;
	}
	
	//绑定摄像头 seq=0E
	public static byte[] BinderCamera(UserDevice mUserDevice){
		byte[] datas = new byte[105];
		byte[] Crc = new byte[97];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x20;
		datas[5] = 0x00;
		datas[6] = 0x0E;
		datas[7] = 0x5C;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x20;
		Crc[2] = 0x00;
		Crc[3] = 0x0E;
		Crc[4] = 0x5C;
		datas[8] = (byte) mUserDevice.getDevType();
		Crc[5] = (byte) mUserDevice.getDevType();
		datas[9] = 0x02;
		Crc[6] = 0x02;
		byte[] userIdByte = mUserDevice.getUserNum().getBytes();
		int userIdByteLen = userIdByte.length;
		for(int i=0;i<userIdByteLen;i++){
			datas[10+i] = userIdByte[i];
			Crc[7+i] = userIdByte[i];
		}
		int userNumLen = 12-userIdByteLen;
		if(userNumLen>0){
			for(int i=0;i<userNumLen;i++){
				datas[10+userIdByteLen+i] = -0;
				Crc[7+userIdByteLen+i] = -0;
			}
		}
		byte[] devMacByte = mUserDevice.getDevMac().getBytes();
		int devMacByteLen = devMacByte.length;
		for(int i=0;i<devMacByteLen;i++){
			datas[22+i] = devMacByte[i];
			Crc[19+i] = devMacByte[i];
		}
		int devMacLen = 12-devMacByteLen;
		if(devMacLen>0){
			for(int i=0;i<devMacLen;i++){
				datas[22+devMacByteLen+i] = -0;
				Crc[19+devMacByteLen+i] = -0;
			}
		}
		String cameraPwd = mUserDevice.getCameraPwd();
		if(null!=cameraPwd&&cameraPwd.length()>0){
			byte[] cameraPwdByte = cameraPwd.getBytes();
			int cameraPwdByteLen = cameraPwdByte.length;
			for(int i=0;i<cameraPwdByteLen;i++){
				datas[34+i] = cameraPwdByte[i];
				Crc[31+i] = cameraPwdByte[i];
			}
			int cameraPwdLen = 16-cameraPwdByteLen;
			if(cameraPwdLen>0){
				for(int i=0;i<cameraPwdLen;i++){
					datas[34+cameraPwdByteLen+i] = -0;
					Crc[31+cameraPwdByteLen+i] = -0;
				}
			}
		}else{
			for(int i=0;i<16;i++){
				datas[34+i] = -0;
				Crc[31+i] = -0;
			}
		}
		String devName = mUserDevice.getDevName();
		if(null!=devName&&devName.length()>0){
			byte[] devNameByte = devName.getBytes();
			int devNameByteLen = devNameByte.length;
			for(int i=0;i<devNameByteLen;i++){
				datas[50+i] = devNameByte[i];
				Crc[47+i] = devNameByte[i];
			}
			int devNameLen = 50-devNameByteLen;
			if(devNameLen>0){
				for(int i=0;i<devNameLen;i++){
					datas[50+devNameByteLen+i] = -0;
					Crc[47+devNameByteLen+i] = -0;
				}
			}
		}else{
			for(int i=0;i<50;i++){
				datas[50+i] = -0;
				Crc[47+i] = -0;
			}
		}
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[100] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[101] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[102] = tail;
		datas[103] = tail2;
		datas[104] = tail3;
		return datas;
	}
	
	//删除或者修改设备 actionType表示操作类型：1表示删除设备，2表示修改设备名称，3表示修改设备密码
	public static byte[] ModifyDev(UserDevice mUserDevice,byte actionType){
		byte[] datas = new byte[105];
		byte[] Crc = new byte[97];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = (byte) 0x24;
		datas[5] = 0x00;
		datas[6] = 0x0F;
		datas[7] = 0x5C;
		Crc[0] = 0x0C;
		Crc[1] = (byte) 0x24;
		Crc[2] = 0x00;
		Crc[3] = 0x0F;
		Crc[4] = 0x5C;
		datas[8] = actionType;
		Crc[5] = actionType;
		datas[9] = 0x02;
		Crc[6] = 0x02;
		byte[] userIdByte = mUserDevice.getUserNum().getBytes();
		int userIdByteLen = userIdByte.length;
		for(int i=0;i<userIdByteLen;i++){
			datas[10+i] = userIdByte[i];
			Crc[7+i] = userIdByte[i];
		}
		int userNumLen = 12-userIdByteLen;
		if(userNumLen>0){
			for(int i=0;i<userNumLen;i++){
				datas[10+userIdByteLen+i] = -0;
				Crc[7+userIdByteLen+i] = -0;
			}
		}
		byte[] devMacByte = mUserDevice.getDevMac().getBytes();
		int devMacByteLen = devMacByte.length;
		for(int i=0;i<devMacByteLen;i++){
			datas[22+i] = devMacByte[i];
			Crc[19+i] = devMacByte[i];
		}
		int devMacLen = 12-devMacByteLen;
		if(devMacLen>0){
			for(int i=0;i<devMacLen;i++){
				datas[22+devMacByteLen+i] = -0;
				Crc[19+devMacByteLen+i] = -0;
			}
		}
		String cameraPwd = mUserDevice.getCameraPwd();
		if(null!=cameraPwd&&cameraPwd.length()>0){
			byte[] cameraPwdByte = cameraPwd.getBytes();
			int cameraPwdByteLen = cameraPwdByte.length;
			for(int i=0;i<cameraPwdByteLen;i++){
				datas[34+i] = cameraPwdByte[i];
				Crc[31+i] = cameraPwdByte[i];
			}
			int cameraPwdLen = 16-cameraPwdByteLen;
			if(cameraPwdLen>0){
				for(int i=0;i<cameraPwdLen;i++){
					datas[34+cameraPwdByteLen+i] = -0;
					Crc[31+cameraPwdByteLen+i] = -0;
				}
			}
		}else{
			for(int i=0;i<16;i++){
				datas[34+i] = -0;
				Crc[31+i] = -0;
			}
		}
		String devName = mUserDevice.getDevName();
		if(null!=devName&&devName.length()>0){
			byte[] devNameByte = devName.getBytes();
			int devNameByteLen = devNameByte.length;
			for(int i=0;i<devNameByteLen;i++){
				datas[50+i] = devNameByte[i];
				Crc[47+i] = devNameByte[i];
			}
			int devNameLen = 50-devNameByteLen;
			if(devNameLen>0){
				for(int i=0;i<devNameLen;i++){
					datas[50+devNameByteLen+i] = -0;
					Crc[47+devNameByteLen+i] = -0;
				}
			}
		}else{
			for(int i=0;i<50;i++){
				datas[50+i] = -0;
				Crc[47+i] = -0;
			}
		}
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[100] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[101] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[102] = tail;
		datas[103] = tail2;
		datas[104] = tail3;
		return datas;
	}
	
	//询问该用户是否在技威服务器上注册过账号
	public static byte[] ifRegisterInyoo(String userNum){
		byte[] datas = new byte[26];
		byte[] userIdByte = userNum.getBytes();
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x25;
		datas[5] = 0x0D;
		datas[6] = 0x0D;
		datas[7] = 0x0D;
		Crc[0]=0x0C;
		Crc[1]=0x25;
		Crc[2]=0x0D;
		Crc[3]=0x0D;
		Crc[4]=0x0D;
		int len = userIdByte.length;
		for(int i=0;i<len;i++){
			datas[8+i] = userIdByte[i];
			Crc[5+i] = userIdByte[i];
		}
		if(len<12){
			int le =12-len;
			for(int j=0;j<le;j++){
				datas[8+len+j] = -0;
				Crc[5+len+j] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
		
	//把用户名与技威的用户名绑定
	public static byte[] ifBinderInyoo(String userNum,String yooUserId,String yooEmail){
		byte[] datas = new byte[68];
		byte[] userIdByte = userNum.getBytes();
		byte[] yooUserIdByte = yooUserId.getBytes();
		byte[] yooEmailByte = yooEmail.getBytes();
		byte[] Crc = new byte[60];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x23;
		datas[5] = 0x00;
		datas[6] = 0x1F;
		datas[7] = 0x37;
		Crc[0]=0x0C;
		Crc[1]=0x23;
		Crc[2]=0x00;
		Crc[3]=0x1F;
		Crc[4]=0x37;
		int len = userIdByte.length;
		for(int i=0;i<len;i++){
			datas[8+i] = userIdByte[i];
			Crc[5+i] = userIdByte[i];
		}
		if(len<12){
			int le =12-len;
			for(int j=0;j<le;j++){
				datas[8+len+j] = -0;
				Crc[5+len+j] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		int lenYooId = yooUserIdByte.length;
		for(int i=0;i<lenYooId;i++){
			datas[21+i] = yooUserIdByte[i];
			Crc[18+i] = yooUserIdByte[i];
		}
		if(lenYooId<12){
			int leY =12-lenYooId;
			for(int j=0;j<leY;j++){
				datas[21+lenYooId+j] = -0;
				Crc[18+lenYooId+j] = -0;
			}
		}
		int lenEmail = yooEmailByte.length;
		for(int i=0;i<lenEmail;i++){
			datas[33+i] = yooEmailByte[i];
			Crc[30+i] = yooEmailByte[i];
		}
		if(lenEmail<30){
			int leE = 30-lenEmail;
			for(int j=0;j<leE;j++){
				datas[33+lenEmail+j] = -0;
				Crc[30+lenEmail+j] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[63] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[64] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[65] = tail;
		datas[66] = tail2;
		datas[67] = tail3;
		return datas;
	}
	
	//绑定预置位
	public static byte[] binderPreset(String presetId,int presetType){
		byte[] datas = new byte[31];
		byte[] Crc = new byte[23];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x36;
		datas[5] = 0x00;
		datas[6] = 0x2F;
		datas[7] = 0x12;
		Crc[0]=0x0C;
		Crc[1]=0x36;
		Crc[2]=0x00;
		Crc[3]=0x2F;
		Crc[4]=0x12;
		int len = presetId.length();
		byte[] presetIdByte = presetId.getBytes();
		for(int i=0;i<len;i++){
			datas[8+i] = presetIdByte[i];
			Crc[5+i]= presetIdByte[i];
		}
		int presetIdByteLen = 16-len;
		if(presetIdByteLen>0){
			for(int i=0;i<presetIdByteLen;i++){
				datas[8+len+i] = -0;
				Crc[5+len+i]= -0;
			}
		}
		datas[24] = 0x02;
		Crc[21] = 0x02;
		datas[25] = (byte) presetType;
		Crc[22] = (byte) presetType;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[26] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[27] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[28] = tail;
		datas[29] = tail2;
		datas[30] = tail3;
		return datas;
	}
	
	//获取绑定预置位
	public static byte[] getBinderPreset(List<String> list){
		int num = list.size();
		int len = 15+num*16;
		int CrcLen = 7+num*16;
		byte dataLen = new IntegerTo16().algorismToHEXString(2+num*16);
		byte[] datas = new byte[len];
		byte[] Crc = new byte[CrcLen];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x38;
		datas[5] = 0x00;
		datas[6] = 0x3F;
		datas[7] = dataLen;
		datas[8] = (byte) num;
		datas[9] = 0x02;
		Crc[0]=0x0C;
		Crc[1]=0x38;
		Crc[2]=0x00;
		Crc[3]=0x3F;
		Crc[4]=dataLen;
		Crc[5] =  (byte) num;
		Crc[6] = 0x02;
		for(int i=0;i<num;i++){
			String str = list.get(i);
			byte[] strByte = str.getBytes();
			int lenStr = strByte.length;
			int lenData = i*16;
			for(int j=0;j<lenStr;j++){
				datas[10+j+lenData] = strByte[j];
				Crc[7+j+lenData] = strByte[j];
			}
			int strByteLen = 16-lenStr;
			if(strByteLen>0){
				for(int j=0;j<strByteLen;j++){
					datas[10+lenStr+lenData+j] = -0;
					Crc[7+lenStr+lenData+j] = -0;
				}
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[len-5] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[len-4] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[len-3] = tail;
		datas[len-2] = tail2;
		datas[len-1] = tail3;
		return datas;
	}
	
	//解除摄像头与插座关联
	public static byte[] unBinderCameraAndSocket(String relateSocket){
		byte[] datas = new byte[26];
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x40;
		datas[5] = 0x00;
		datas[6] = 0x4F;
		datas[7] = 0x0D;
		Crc[0]=0x0C;
		Crc[1]=0x40;
		Crc[2]=0x00;
		Crc[3]=0x4F;
		Crc[4]=0X0D;
		byte[] relateSocketByte = relateSocket.getBytes();
		int relateSocketLen = relateSocketByte.length;
		for(int i=0;i<relateSocketLen;i++){
			datas[8+i] = relateSocketByte[i];
			Crc[5+i] = relateSocketByte[i];
		}
		int len = 12-relateSocketLen;
		if(len>0){
			for(int i=0;i<len;i++){
				datas[8+relateSocketLen+i] = -0;
				Crc[5+relateSocketLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	//查询摄像头是否与插座关联
	public static byte[] findBinderCameraAndSocket(String relateSocket){
		byte[] datas = new byte[26];
		byte[] Crc = new byte[18];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x42;
		datas[5] = 0x00;
		datas[6] = 0x5F;
		datas[7] = 0x0D;
		Crc[0]=0x0C;
		Crc[1]=0x42;
		Crc[2]=0x00;
		Crc[3]=0x5F;
		Crc[4]=0X0D;
		byte[] relateSocketByte = relateSocket.getBytes();
		int relateSocketLen = relateSocketByte.length;
		for(int i=0;i<relateSocketLen;i++){
			datas[8+i] = relateSocketByte[i];
			Crc[5+i] = relateSocketByte[i];
		}
		int len = 12-relateSocketLen;
		if(len>0){
			for(int i=0;i<len;i++){
				datas[8+relateSocketLen+i] = -0;
				Crc[5+relateSocketLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[23] = tail;
		datas[24] = tail2;
		datas[25] = tail3;
		return datas;
	}
	
	//关联摄像头与插座
	public static byte[] binderCameraAndSocket(String relateSocket,String devMac){
		byte[] datas = new byte[38];
		byte[] Crc = new byte[30];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x44;
		datas[5] = 0x00;
		datas[6] = 0x6F;
		datas[7] = 0x19;
		Crc[0]=0x0C;
		Crc[1]=0x44;
		Crc[2]=0x00;
		Crc[3]=0x6F;
		Crc[4]=0X19;
		byte[] relateSocketByte = relateSocket.getBytes();
		int relateSocketLen = relateSocketByte.length;
		for(int i=0;i<relateSocketLen;i++){
			datas[8+i] = relateSocketByte[i];
			Crc[5+i] = relateSocketByte[i];
		}
		int len = 12-relateSocketLen;
		if(len>0){
			for(int i=0;i<len;i++){
				datas[8+relateSocketLen+i] = -0;
				Crc[5+relateSocketLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		byte[] devMacByte = devMac.getBytes();
		int devMacLen = devMacByte.length;
		for(int i=0;i<devMacLen;i++){
			datas[21+i] = devMacByte[i];
			Crc[18+i] = devMacByte[i];
		}
		int devMacByteLen = 12-devMacLen;
		if(devMacByteLen>0){
			for(int i=0;i<devMacByteLen;i++){
				datas[21+devMacLen+i] = -0;
				Crc[18+devMacLen+i] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[33] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[34] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[35] = tail;
		datas[36] = tail2;
		datas[37] = tail3;
		return datas;
	}
	
	//查询用户未关联的插座和摄像头
	public static byte[] userUnBinderCameraAndSocket(String userNum,int devType){
		byte[] datas = new byte[27];
		byte[] Crc = new byte[19];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x47;
		datas[5] = 0x00;
		datas[6] = 0x7F;
		datas[7] = 0x0E;
		Crc[0]=0x0C;
		Crc[1]=0x47;
		Crc[2]=0x00;
		Crc[3]=0x7F;
		Crc[4]=0X0E;
		byte[] relateSocketByte = userNum.getBytes();
		int relateSocketLen = relateSocketByte.length;
		for(int i=0;i<relateSocketLen;i++){
			datas[8+i] = relateSocketByte[i];
			Crc[5+i] = relateSocketByte[i];
		}
		int len = 12-relateSocketLen;
		if(len>0){
			for(int i=0;i<len;i++){
				datas[8+relateSocketLen+i] = -0;
				Crc[5+relateSocketLen+i] = -0;
			}
		}
		datas[20] = 0x02;
		Crc[17] = 0x02;
		datas[21] = (byte) devType;
		Crc[18] = (byte) devType;
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[22] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[23] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[24] = tail;
		datas[25] = tail2;
		datas[26] = tail3;
		return datas;
	}
	
	//查询用户是否持有该摄像头
	public static byte[] ifUserOwnCamera(String userNum,String devMac){
		byte[] datas = new byte[38];
		byte[] Crc = new byte[30];
		byte head = "{".getBytes()[0];
		byte head2 = "[".getBytes()[0];
		byte head3 = "]".getBytes()[0];
		byte tail = "[".getBytes()[0];
		byte tail2 = "]".getBytes()[0];
		byte tail3 = "}".getBytes()[0];
		datas[0] = head;
		datas[1] = head2;
		datas[2] = head3;
		datas[3] = 0x0C;
		datas[4] = 0x49;
		datas[5] = 0x00;
		datas[6] = 0x7F;
		datas[7] = 0x19;
		Crc[0]=0x0C;
		Crc[1]=0x49;
		Crc[2]=0x00;
		Crc[3]=0x7F;
		Crc[4]=0x19;
		byte[] userNumByte = userNum.getBytes();
		int userNumLen = userNumByte.length;
		for(int i=0;i<userNumLen;i++){
			datas[8+i] = userNumByte[i];
			Crc[5+i]= userNumByte[i];
		}
		int userNumByteLen = 12-userNumLen;
		if(userNumByteLen>0){
			for(int i=0;i<userNumByteLen;i++){
				datas[8+userNumLen+i] = -0;
				Crc[5+userNumLen+i]= -0;
			}
		}
		datas[20] = 0x02;
		Crc[17]= 0x02;
		byte[] devMacByte = devMac.getBytes();
		int devMacLen = devMacByte.length;
		for(int i=0;i<devMacLen;i++){
			datas[21+i] = devMacByte[i];
			Crc[18+i]= devMacByte[i];
		}
		int devMacByteLen = 12-devMacLen;
		if(devMacByteLen>0){
			for(int i=0;i<devMacByteLen;i++){
				datas[21+devMacLen+i] = -0;
				Crc[18+devMacLen+i]= -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(Crc));
		datas[33] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		datas[34] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		datas[35] = tail;
		datas[36] = tail2;
		datas[37] = tail3;
		return datas;
	}
}
