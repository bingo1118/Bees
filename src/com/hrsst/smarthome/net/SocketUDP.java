package com.hrsst.smarthome.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.hrsst.smarthome.global.MyApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SocketUDP {
	static DatagramPacket client;

	static BufferedReader in;

	private static SocketUDP socketClient;

	private static final String TAG = "SocketClient";

	private InetAddress site;

	private int port;

	private boolean onGoinglistner = true;
	
	private  DatagramSocket socket;

	private Context context;

	public static synchronized SocketUDP newInstance(InetAddress site, int port) {

		if (socketClient == null) {
			socketClient = new SocketUDP(site, port);
			Log.i("newInstance", "newInstance");
		}
		return socketClient;
	}

	private SocketUDP(InetAddress site, int port) {
		this.site = site;
		this.port = port;
	}

	public String sendMsg(final byte[] msg) {
		Log.i(TAG, "into sendMsgsendMsg(final ChatMessage msg)  msg =" + msg);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (socket == null) {
						socket = new DatagramSocket(port);
					}
					client = new DatagramPacket(msg, msg.length, site, port);
					socket.send(client);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return "";
	}

	private void closeConnection() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void acceptMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (onGoinglistner) {
					try {
						if (socket == null) {
							socket = new DatagramSocket(port);
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						byte[] data = new byte[64*1024];
						 //参数一:要接受的data 参数二：data的长度  
				        DatagramPacket packet = new DatagramPacket(data, data.length);  
				        data=null;
				        socket.receive(packet);
				        Log.i("cmd1", "..........");
				        //把接收到的data转换为String字符串 
				        byte[] pk = packet.getData();
				        packet=null;
				        int num;
				        if((pk[4]&0xff)==39){
				        	num = (pk[7]&0xff)+(pk[8]&0xff)*256+13;
//						}
//				        else if((pk[4]&0xff)==72){
//							num = (pk[7]&0xff)+(pk[8]&0xff)*256+13;
						}else{
							num = (pk[7]&0xff)+13;
						}
				        
				        byte[] result = new byte[num];
				        for(int i=0;i<num;i++){
				        	result[i] = pk[i];
				        }
						if (result != null && !result.equals("")) {
							int cmd = result[4]&0xff;
							cmd2(cmd,result);
							Log.i("cmd1", cmd+"");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void clearClient() {
		closeConnection();
	}
	
	public void startAcceptMessage() {
//		onGoinglistner = true;
//		this.context = mContext;
//		if(null!=context){
			acceptMsg();
//		}
		
	}
	
	private void cmd2(int cmd,byte[] result){
		switch (cmd) {
		case 133://85 配置设备状态回复包
			Intent unOpenOrCloseOrderPack = new Intent();
			unOpenOrCloseOrderPack.putExtra("datasByte", result);
			unOpenOrCloseOrderPack.setAction("Constants.Action.unOpenOrCloseOrderPack");
			MyApp.app.sendBroadcast(unOpenOrCloseOrderPack);
			break;
		case 136://136 配置设备定时回复包
			Intent unTimerOrderPack = new Intent();
			unTimerOrderPack.putExtra("datasByte", result);
			unTimerOrderPack.setAction("Constants.Action.unTimerOrderPack");
			MyApp.app.sendBroadcast(unTimerOrderPack);
			break;
		case 140://8C 433MAC地址学习操作命令回复包
			Intent unStudyOrderPack = new Intent();
			unStudyOrderPack.putExtra("datasByte", result);
			unStudyOrderPack.setAction("Constants.Action.unStudyOrderPack");
			MyApp.app.sendBroadcast(unStudyOrderPack);
			break;
		case 34://22 设备状态获取回复包
			Intent unGetDeviceStatesListPack = new Intent();
			unGetDeviceStatesListPack.putExtra("datasByte", result);
			unGetDeviceStatesListPack.setAction("Constants.Action.unGetDeviceStatesListPack");
			MyApp.app.sendBroadcast(unGetDeviceStatesListPack);
			break;
		case 9://09 ACK
			Intent unServerACKPack = new Intent();
			unServerACKPack.putExtra("datasByte", result);
			unServerACKPack.setAction("Constants.Action.unServerACKPack");
			MyApp.app.sendBroadcast(unServerACKPack);
			break;
		case 151://97 ACK
			Intent unModifyAlarmName = new Intent();
			unModifyAlarmName.putExtra("datasByte", result);
			unModifyAlarmName.setAction("Constants.Action.unModifyAlarmName");
			MyApp.app.sendBroadcast(unModifyAlarmName);
			break;
		case 19://13 ACK
			Intent unClearAlarmMessage = new Intent();
			unClearAlarmMessage.putExtra("datasByte", result);
			unClearAlarmMessage.setAction("Constants.Action.unClearAlarmMessage");
			MyApp.app.sendBroadcast(unClearAlarmMessage);
			break;
		case 21://15
			Intent unBinderUser = new Intent();
			unBinderUser.putExtra("datasByte", result);
			unBinderUser.setAction("Constants.Action.unBinderUser");
			MyApp.app.sendBroadcast(unBinderUser);
			break;
		case 25://19 心跳包回复
			Intent unHearPackage = new Intent();
			unHearPackage.putExtra("datasByte", result);
			unHearPackage.setAction("Constants.Action.unHearPackage");
			MyApp.app.sendBroadcast(unHearPackage);
			break;
		case 23://17
			Intent unLoginUser = new Intent();
			unLoginUser.putExtra("datasByte", result);
			unLoginUser.setAction("Constants.Action.unLoginUser");
			MyApp.app.sendBroadcast(unLoginUser);
			break;
		case 49://31
			Intent unGetServerTimePackage = new Intent();
			unGetServerTimePackage.putExtra("datasByte", result);
			unGetServerTimePackage.setAction("Constants.Action.unGetServerTimePackage");
			MyApp.app.sendBroadcast(unGetServerTimePackage);
			break;
		case 51://33
			Intent unGetDevHeartTimePackage = new Intent();
			unGetDevHeartTimePackage.putExtra("datasByte", result);
			unGetDevHeartTimePackage.setAction("Constants.Action.unGetDevHeartTimePackage");
			MyApp.app.sendBroadcast(unGetDevHeartTimePackage);
			break;
		case 53://35 布防/撤防
			Intent unDefence = new Intent();
			unDefence.putExtra("datasByte", result);
			unDefence.setAction("Constants.Action.unDefence");
			MyApp.app.sendBroadcast(unDefence);
			break;
		case 33://21 绑定摄像头回复包 
			Intent unBinderCamera = new Intent();
			unBinderCamera.putExtra("datasByte", result);
			unBinderCamera.setAction("Constants.Action.unBinderCamera");
			MyApp.app.sendBroadcast(unBinderCamera);
			break;
		case 40://28删除或者修改设备回复包
			Intent unActionCamera = new Intent();
			unActionCamera.putExtra("datasByte", result);
			unActionCamera.setAction("Constants.Action.unActionCamera");
			MyApp.app.sendBroadcast(unActionCamera);
			break;
		case 39://27 获取用户设备回复包
			Intent unGetUserDev = new Intent();
			unGetUserDev.putExtra("datasByte", result);
			unGetUserDev.setAction("Constants.Action.unGetUserDev");
			MyApp.app.sendBroadcast(unGetUserDev);
			break;
		case 103://0x67 把用户名与技威的用户名绑定
			Intent ifBinderInyoo = new Intent();
			ifBinderInyoo.putExtra("datasByte", result);
			ifBinderInyoo.setAction("Constants.Action.ifBinderInyoo");
			MyApp.app.sendBroadcast(ifBinderInyoo);
			break;
		case 69://0x45 询问该用户是否在技威服务器上注册过账号
			Intent unIfRegisterInyoo = new Intent();
			unIfRegisterInyoo.putExtra("datasByte", result);
			unIfRegisterInyoo.setAction("Constants.Action.unIfRegisterInyoo");
			MyApp.app.sendBroadcast(unIfRegisterInyoo);
			break;
		case 55://0x37 绑定预置位
			Intent binderPreset = new Intent();
			binderPreset.putExtra("datasByte", result);
			binderPreset.setAction("Constants.Action.BinderPreset");
			MyApp.app.sendBroadcast(binderPreset);
			break;
		case 57://0x39 获取绑定预置位
			Intent getBinderPreset = new Intent();
			getBinderPreset.putExtra("datasByte", result);
			getBinderPreset.setAction("Constants.Action.GetBinderPreset");
			MyApp.app.sendBroadcast(getBinderPreset);
			break;
		case 65://0x41 解除摄像头与插座关联回复包
			Intent unBinderCameraAndSocketPk = new Intent();
			unBinderCameraAndSocketPk.putExtra("datasByte", result);
			unBinderCameraAndSocketPk.setAction("Constants.Action.unBinderCameraAndSocketPk");
			MyApp.app.sendBroadcast(unBinderCameraAndSocketPk);
			break;
		case 67://0x43 查询摄像头是否与插座关联回复包
			Intent unFindBinderCameraAndSocket = new Intent();
			unFindBinderCameraAndSocket.putExtra("datasByte", result);
			unFindBinderCameraAndSocket.setAction("Constants.Action.unFindBinderCameraAndSocket");
			MyApp.app.sendBroadcast(unFindBinderCameraAndSocket);
			break;
		case 70://0x46关联摄像头与插座回复包
			Intent unBinderCameraAndSocket = new Intent();
			unBinderCameraAndSocket.putExtra("datasByte", result);
			unBinderCameraAndSocket.setAction("Constants.Action.unBinderCameraAndSocket");
			MyApp.app.sendBroadcast(unBinderCameraAndSocket);
			break;
		case 72://0x48查询未关联摄像头与插座回复包
			Intent unUserUnBinderCameraAndSocket = new Intent();
			unUserUnBinderCameraAndSocket.putExtra("datasByte", result);
			unUserUnBinderCameraAndSocket.setAction("Constants.Action.unUserUnBinderCameraAndSocket");
			MyApp.app.sendBroadcast(unUserUnBinderCameraAndSocket);
			break;
		case 80://0x50查询用户是否持有该摄像头回复包
			Intent unIfUserOwnCamera = new Intent();
			unIfUserOwnCamera.putExtra("datasByte", result);
			unIfUserOwnCamera.setAction("Constants.Action.unIfUserOwnCamera");
			MyApp.app.sendBroadcast(unIfUserOwnCamera);
			break;
		default:
			break;
		}
	}
}