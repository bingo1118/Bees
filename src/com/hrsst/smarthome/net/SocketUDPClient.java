package com.hrsst.smarthome.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SocketUDPClient {
	static DatagramPacket client;

	static BufferedReader in;

	private static SocketUDPClient socketClient;

	private static final String TAG = "SocketClient";

	private InetAddress site;

	private int port;

	private boolean onGoinglistner = true;

	private ClientUDPMsgListener clientListener;
	
	private  DatagramSocket socket;

	public static interface ClientUDPMsgListener {

		public void handlerErorMsg(String errorMsg);

		public void handlerHotMsg(byte[] hotMsg);

	}

	public static synchronized SocketUDPClient newInstance(InetAddress site, int port,
			ClientUDPMsgListener clientListener) {

		if (socketClient == null) {
			socketClient = new SocketUDPClient(site, port, clientListener);
		}
		Log.i(TAG, "socketClient =" + socketClient);
		return socketClient;
	}

	// 切换消息监听器
	public void setMsgListener(ClientUDPMsgListener listener) {
		this.clientListener = listener;
	}

	private SocketUDPClient(InetAddress site, int port, ClientUDPMsgListener clientListener) {
		this.site = site;
		this.port = port;
		this.clientListener = clientListener;
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

	public void acceptMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (onGoinglistner) {
					try {
						if (socket == null) {
							socket = new DatagramSocket(port);
						}
						byte data[] = new byte[1024];
						 //参数一:要接受的data 参数二：data的长度  
				        DatagramPacket packet = new DatagramPacket(data, data.length);  
				        socket.receive(packet);
				        //把接收到的data转换为String字符串 
				        byte[] pk = packet.getData();
				        int num = pk[5]+11;
				        byte[] result = new byte[num];
				        for(int i=0;i<num;i++){
				        	result[i] = pk[i];
				        }//123, 91, 93, 12, 9, 13, 2, 1, 53, 99, 99, 102, 55, 102, 56, 56, 56, 53, 3, -106, 14, 91, 93, 125
				         //123, 91, 93, 12, 34, 17, 3, 1, 53, 99, 99, 102, 55, 102, 56, 56, 56, 53, 102, 102, 0, 0, 0, -91, 47, 91, 93, 125
						Log.i(TAG, "into acceptMsg()  result =" + result);
						if (result != null && !result.equals("")) {
							clientListener.handlerHotMsg(result);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public void acceptMsg(final Context mContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (onGoinglistner) {
					try {
						if (socket == null) {
							socket = new DatagramSocket(port);
						}
						byte data[] = new byte[1024];
						 //参数一:要接受的data 参数二：data的长度  
				        DatagramPacket packet = new DatagramPacket(data, data.length);  
				        socket.receive(packet);
				        //把接收到的data转换为String字符串 
				        byte[] pk = packet.getData();
				        int num = pk[5]+11;
				        byte[] result = new byte[num];
				        for(int i=0;i<num;i++){
				        	result[i] = pk[i];
				        }
						Log.i(TAG, "into acceptMsg()  result =" + result);
						if (result != null && !result.equals("")) {
							Intent i = new Intent();
							i.putExtra("datasByte", result);
							i.setAction("Constants.Action.GET_SERVER_DATA");
							mContext.sendBroadcast(i);
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

	public void stopAcceptMessage() {
		onGoinglistner = false;
	}
}