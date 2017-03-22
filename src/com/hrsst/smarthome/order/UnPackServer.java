package com.hrsst.smarthome.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hrsst.smarthome.pojo.DeviceStates;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.User;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.ASCIIToString;
import com.hrsst.smarthome.util.ByteToString;

public class UnPackServer {
	
	//获取服务器 ack
	public UnPackageFromServer unServerACKPack(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		if(pk.length==26){
			mUnPackageFromServer = new UnPackageFromServer();
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.seq = seq;
		}
		return mUnPackageFromServer;
	}
	
	/*配置设备状态回复包
	 *0:close
	 *1:open*/
	public UnPackageFromServer unOpenOrCloseOrderPack(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int state = pk[21];
		if(state==1){
			mUnPackageFromServer.devStates= "close";
		}else{
			mUnPackageFromServer.devStates= "open";
		}
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		return mUnPackageFromServer;
		
	}
	
	/*433MAC地址学习操作命令回复包
	 *0:fail
	 *1:success*/
	public UnPackageFromServer unStudyOrderPack(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int state = pk[21];
		if(state==0){
			mUnPackageFromServer.order= "fail";
		}else if(state==1){
			mUnPackageFromServer.order= "success";
		}else{
			mUnPackageFromServer.order= "repetition";
		}
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		return mUnPackageFromServer;
	}
	
	/*配置设备定时回复包
	 *0:fail
	 *1:success*/
	public UnPackageFromServer unTimerOrderPack(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int state = pk[21];
		if(state==1){
			mUnPackageFromServer.timerOrder= "true";
		}else{
			mUnPackageFromServer.timerOrder= "fail";
		}
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		return mUnPackageFromServer;
	}
	
	/*设备状态获取回复包
	 *0:fail
	 *1:success*/
	public UnPackageFromServer unGetDeviceStatesListPack(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		
		int dataLen = pk[7];
		byte[] crc = new byte[5+dataLen];
		for(int i=0;i<crc.length;i++){
			crc[i] = pk[3+i];
		}
		int macTotal = crc[6];
		List<Map<String,DeviceStates>> list = new ArrayList<Map<String,DeviceStates>>();
		for(int i=0;i<macTotal;i++){
			Map<String,DeviceStates> map = new HashMap<String,DeviceStates>();
			DeviceStates mDeviceStates = new DeviceStates();
			byte[] bmac = new byte[12];
			for(int j=0;j<12;j++){
				bmac[j] = crc[(i*15)+7+j];
			}
			String mac = new ByteToString().encodeHexStr(bmac);
			String wifiMac = ASCIIToString.hexStr2Str(mac);
			int socketStates = crc[(i*15)+19];
			int lightStates = crc[(i*15)+20];
			int lightOnOrOutLine = crc[(i*15)+21];
			mDeviceStates.setMac(wifiMac);
			mDeviceStates.setSocketStates(socketStates);
			mDeviceStates.setLightStates(lightStates);
			mDeviceStates.setLightOnOrOutLine(lightOnOrOutLine);
			map.put(wifiMac, mDeviceStates);
			list.add(map);
		}
		mUnPackageFromServer.deviceStatesList = list;
		return mUnPackageFromServer;
		
	}

	
	//修改报警器位置
	public UnPackageFromServer unModifyAlarmName(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int dataLen = pk[7];
		byte[] crc = new byte[5+dataLen];
		for(int i=0;i<crc.length;i++){
			crc[i] = pk[3+i];
		}

		int macTotal = crc[6];
		if(macTotal==0){
			mUnPackageFromServer.alarmPos= "failed";
		}else{
			mUnPackageFromServer.alarmPos= "success";
		}
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		return mUnPackageFromServer;
	}
	
	//清空报警消息表数据回复包
	public UnPackageFromServer unClearAlarmMessage(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int dataLen = pk[7];
		byte[] crc = new byte[5+dataLen];
		for(int i=0;i<crc.length;i++){
			crc[i] = pk[3+i];
		}

		int macTotal = crc[6];
		if(macTotal==0){
			mUnPackageFromServer.clearAlarmMsg= "failed";
		}else{
			mUnPackageFromServer.clearAlarmMsg= "success";
		}
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		return mUnPackageFromServer;
	}
	
	//绑定用户回复包
	public UnPackageFromServer unBinderUser(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int macTotal = pk[9];
		if(macTotal==0){
			mUnPackageFromServer.binderUser= "failed";
		}else{
			mUnPackageFromServer.binderUser= "success";
		}
		byte[] seq = new byte[2];
		seq[0] = pk[5];
		seq[1] = pk[6];
		mUnPackageFromServer.seq = seq;
		return mUnPackageFromServer;
	}
	
	//用户登录回复包
	public UnPackageFromServer unLoginUser(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = new UnPackageFromServer();
		int pkLen = pk.length;
		User mUser = new User();
		if(pkLen==81){
			int result = pk[8];
			if(result==1){
				byte[] sessionByte = new byte[22];
				for(int i=0;i<22;i++){
					sessionByte[i]=pk[10+i];
				}
				byte[] userNumByte = new byte[12];
				for(int i=0;i<12;i++){
					userNumByte[i]=pk[32+i];
				}
				byte[] cameraUserByte = new byte[12];
				for(int i=0;i<12;i++){
					cameraUserByte[i]=pk[44+i];
				}
				byte[] cameraEmailByte = new byte[20];
				for(int i=0;i<20;i++){
					cameraEmailByte[i]=pk[56+i];
				}
				mUser.setIfLogin(1);
				mUser.setCameraEmail(new String(cameraEmailByte).trim());
				mUser.setCameraUser(new String(cameraUserByte).trim());
				mUser.setSessionId(new String(sessionByte).trim());
				mUser.setUserNum(new String(userNumByte).trim());
				mUnPackageFromServer.mUser= mUser;
			}else{
				mUser.setIfLogin(2);
				mUnPackageFromServer.mUser= mUser;
			}
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.seq = seq;
			return mUnPackageFromServer;
		}else{
			mUser.setIfLogin(3);
			mUnPackageFromServer.mUser= mUser;
			return mUnPackageFromServer;
		}
	}
	
	public UnPackageFromServer unHeartPackage(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		if(pk.length==15){
			mUnPackageFromServer = new UnPackageFromServer();
			int result = pk[9];
			if(result==0){
				mUnPackageFromServer.heartPackage= "onLine";
			}else{
				mUnPackageFromServer.heartPackage= "offLine";
			}
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.seq=seq;
		}
		return mUnPackageFromServer;
	}
	
	//获取服务器时间 seq=1
	public static UnPackageFromServer unGetServerTimePackage(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		if(pk.length==36){
			mUnPackageFromServer = new UnPackageFromServer();
			byte[] time = new byte[22];
			for(int i=0;i<22;i++){
				time[i] = pk[9+i];
			}
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.serverTime=new String(time).trim();
			mUnPackageFromServer.seq=seq;
		}
		return mUnPackageFromServer;
	}
	
	//获取设备心跳时间 seq=2
	public static UnPackageFromServer unGetDevHeartTimePackage(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		if(pk.length==48){
			mUnPackageFromServer = new UnPackageFromServer();
			byte[] time = new byte[22];
			byte[] devMac = new byte[12];
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			for(int i=0;i<22;i++){
				time[i] = pk[21+i];
			}
			for(int i=0;i<12;i++){
				devMac[i] = pk[8+i];
			}
			mUnPackageFromServer.seq = seq;
			mUnPackageFromServer.devMac = new String(devMac);
			mUnPackageFromServer.devHeartTime=new String(time).trim();
		}
		return mUnPackageFromServer;
	}
	
	//获取用户设备回复包 seq=0C
	public static UnPackageFromServer unGetUserDev(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		int len =(pk[7]&0xff)+(pk[8]&0xff)*256+13;
		if(pk.length==len){
			int devLen = pk[9];
			if(devLen>0){
				byte[] seq = new byte[2];
				seq[0] = pk[5];
				seq[1] = pk[6];
				mUnPackageFromServer = new UnPackageFromServer();
				List<UserDevice> userDeviceList = new ArrayList<UserDevice>();
				List<String> macList = new ArrayList<String>();
				List<String> camseraList = new ArrayList<String>();
				for(int i=0;i<devLen;i++){
					UserDevice mUserDevice = new UserDevice();
					int devType = pk[11+i*93];
					mUserDevice.setDevType(devType);
					byte[] userNum = new byte[12];
					for(int j=0;j<12;j++){
						userNum[j]=pk[(12+i*93)+j];
					}
					mUserDevice.setUserNum(new String(userNum).trim());
					byte[] devMac = new byte[12];
					for(int j=0;j<12;j++){
						devMac[j]=pk[(24+i*93)+j];
					}
					mUserDevice.setDevMac(new String(devMac).trim());
					switch (devType) {
					case 1:
						//排插没有密码，不做处理
						macList.add(new String(devMac).trim());
						break;
					case 2:
						camseraList.add(new String(devMac).trim());
						byte[] cameraPwd = new byte[16];
						for(int j=0;j<16;j++){
							cameraPwd[j]=pk[(36+i*93)+j];
						}
						mUserDevice.setCameraPwd(new String(cameraPwd).trim());
						break;
					default:
						break;
					}
					mUserDevice.setIsShare(pk[52+i*93]);
					byte[] devName = new byte[50];
					for(int j=0;j<50;j++){
						devName[j]=pk[(53+i*93)+j];
					}
					mUserDevice.setDevName(new String(devName).trim());
					mUserDevice.setDefence(pk[103+i*93]);
					userDeviceList.add(mUserDevice);
				}
				mUnPackageFromServer.userDeviceList = userDeviceList;
				mUnPackageFromServer.macList = macList;
				mUnPackageFromServer.cameraList = camseraList;
				mUnPackageFromServer.seq = seq;
			}
		}
		return mUnPackageFromServer;
	}
	
	//设备布防/撤防 回复包
	public static UnPackageFromServer unDefence(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		if(pk.length==27){
			mUnPackageFromServer = new UnPackageFromServer();
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.seq = seq;
			mUnPackageFromServer.defence = pk[21];
		}
		return mUnPackageFromServer;
	}
	
	//绑定摄像头回复包 、删除或者修改设备回复包
	public static UnPackageFromServer unActionCamera(byte[] pk){
		UnPackageFromServer mUnPackageFromServer = null;
		if(pk.length==15){
			mUnPackageFromServer = new UnPackageFromServer();
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.seq = seq;
			int result = pk[9];
			if(result==1){
				mUnPackageFromServer.binderResult="success";
			}else{
				mUnPackageFromServer.binderResult="failed";
			}
		}
		return mUnPackageFromServer;
	}
	
	//把用户名与技威的用户名绑定
	public static String ifBinderInyoo(byte[] pk){
		int result = pk[9];
		if(result==1){
			return "success";
		}else{
			return "failed";
		}
	}
		
	//询问该用户是否在技威服务器上注册过账号unIfRegisterInyoo
	public static String unIfRegisterInyoo(byte[] pk){
		int result = pk[9];
		if(result==1){
			byte[] emailByte = new byte[30];
			for(int i=0;i<30;i++){
				emailByte[i]=pk[10+i];
			}
			String emailStr = new String(emailByte).trim();
			return emailStr;
		}else{
			return "no";
		}
	}
	
	//绑定预置位回复包
	public static String unBinderPreset(byte[] pk){
		int result = pk[9];
		if(result==1){
			return "success";
		}else{
			return "failed";
		}
	}
	
	//获取预置位回复包
	public static UnPackageFromServer unGetBinderPreset(byte[] pk){
		int result = pk[8]&0xff;
		UnPackageFromServer mUnPackageFromServer = null;
		if(result>0){
			mUnPackageFromServer = new UnPackageFromServer();
			Map<String,Integer> map = new HashMap<String,Integer>();
			byte[] seq = new byte[2];
			seq[0] = pk[5];
			seq[1] = pk[6];
			mUnPackageFromServer.seq = seq;
			for(int i=0;i<result;i++){
				byte[] presetId = new byte[16];
				int len = i*17;
				for(int j=0;j<16;j++){
					presetId[j] = pk[10+len+j];
				}
				int type = pk[26+len];
				String str = new String(presetId).trim();
				map.put(str, type);
			}
			mUnPackageFromServer.map = map;
			return mUnPackageFromServer;
		}else{
			return null;
		}
	}
	//解除摄像头与插座关联回复包
	public static String unBinderCameraAndSocketPk(byte[] pk){
		int result = pk[9];
		if(result==1){
			return "success";
		}else{
			return "failed";
		}
	}
	//查询摄像头是否与插座关联回复包
	public static UnPackageFromServer unFindBinderCameraAndSocket(byte[] pk){
		int len = pk.length;
		UnPackageFromServer mUnPackageFromServer =null;
		if(len==93){
			int result = pk[8]&0xff;
			mUnPackageFromServer = new UnPackageFromServer();
			if(result==1){
				byte[] devMacByte = new byte[12];
				for(int i=0;i<12;i++){
					devMacByte[i] = pk[10+i];
				}
				byte[] devNameByte = new byte[50];
				for(int i=0;i<50;i++){
					devNameByte[i] = pk[22+i];			
				}
				byte[] devPwdByte = new byte[16];
				for(int i=0;i<16;i++){
					devPwdByte[i] = pk[72+i];
				}
				String devMac = new String(devMacByte).trim();
				String devName = new String(devNameByte).trim();
				String devPwd = new String(devPwdByte).trim();
				mUnPackageFromServer.devMac = devMac;
				mUnPackageFromServer.devName = devName;
				mUnPackageFromServer.devPwd = devPwd;
				mUnPackageFromServer.result="yes";
			}else{
				mUnPackageFromServer.result="no";
			}
		}
		return mUnPackageFromServer;
	}
	
	//关联摄像头与插座回复包
	public static String unBinderCameraAndSocket(byte[] pk){
		int result = pk[9];
		if(result==1){
			return "success";
		}else{
			return "failed";
		}
	}
	
	//查询未关联插座与摄像头回复包
	public static List<UserDevice> unUserUnBinderCameraAndSocket(byte[] pk){
		int num = pk[8]&0xff;
		List<UserDevice> mUserDeviceList =null;
		if(num>0){
			mUserDeviceList =new ArrayList<>();
			for(int i=0;i<num;i++){
				int len = i*62;
				byte[] devMac = new byte[12];
				for(int j=0;j<12;j++){
					devMac[j] = pk[10+j+len];
				}
				byte[] devName = new byte[50];
				for(int j=0;j<50;j++){
					devName[j] = pk[22+j+len];
				}
				String devMacStr = new String(devMac).trim();
				String devNameStr = new String(devName).trim();
				UserDevice mUserDevice = new UserDevice();
				mUserDevice.setDevMac(devMacStr);
				mUserDevice.setDevName(devNameStr);
				mUserDeviceList.add(mUserDevice);
			}
		}
		return mUserDeviceList;
	}
	//查询用户是否持有该摄像头
	public static UnPackageFromServer unIfUserOwnCamera(byte[] pk){
		int len = pk.length;
		UnPackageFromServer mUnPackageFromServer =null;
		if(len==93){
			int result = pk[8]&0xff;
			mUnPackageFromServer = new UnPackageFromServer();
			if(result==1){
				byte[] devMacByte = new byte[12];
				for(int i=0;i<12;i++){
					devMacByte[i] = pk[10+i];
				}
				byte[] devNameByte = new byte[50];
				for(int i=0;i<50;i++){
					devNameByte[i] = pk[22+i];			
				}
				byte[] devPwdByte = new byte[16];
				for(int i=0;i<16;i++){
					devPwdByte[i] = pk[72+i];
				}
				String devMac = new String(devMacByte).trim();
				String devName = new String(devNameByte).trim();
				String devPwd = new String(devPwdByte).trim();
				mUnPackageFromServer.devMac = devMac;
				mUnPackageFromServer.devName = devName;
				mUnPackageFromServer.devPwd = devPwd;
				mUnPackageFromServer.result="yes";
			}else{
				mUnPackageFromServer.result="no";
			}
		}
		return mUnPackageFromServer;
	}
}
