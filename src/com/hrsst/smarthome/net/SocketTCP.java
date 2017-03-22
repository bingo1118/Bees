package com.hrsst.smarthome.net;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.hrsst.smarthome.util.ByteToString;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SocketTCP {
	static Socket client;

	static BufferedReader in;
	
	static DataInputStream pk;

	private static SocketTCP socketClient;

	private static final String TAG = "SocketClient";

	private String site;

	private int port;

	private boolean onGoinglistner = true;

	private Context mContext;

	public static synchronized SocketTCP newInstance(String site, int port,Context mContext) {

		if (socketClient == null) {
			socketClient = new SocketTCP(site, port,mContext);
		}
		Log.i(TAG, "socketClient =" + socketClient);
		return socketClient;
	}

	private SocketTCP(String site, int port,Context mContext) {
		this.site = site;
		this.port = port;
		this.mContext = mContext;
	}

	public void connectServer() {
		Log.i(TAG, "into connectServer()");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					client = new Socket(site, port);
					Log.i(TAG, "Client is created! site:" + site + " port:" + port);
					acceptMsg();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.d(TAG, "UnknownHostException");
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, "IOException");
				}
			}
		}).start();
		Log.i(TAG, "out connectServer()");
	}

	public String sendMsg(final byte[] msg) {
		Log.i(TAG, "into sendMsgsendMsg(final ChatMessage msg)  msg =" + msg);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (client != null && client.isConnected()) {
						if (!client.isOutputShutdown()) {
							DataOutputStream outData = new DataOutputStream(client.getOutputStream());
							outData.write(msg);
							Log.v("send" ,"msg.....success");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, "client snedMsg error!");
				}
			}
		}).start();
		return "";
	}

	private void closeConnection() {
		try {
			if (client != null && client.isConnected()) {
				client.close();
				client = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (onGoinglistner) {
					if (null!=client) {
						try {
							if (client.isConnected()) {
								pk = new DataInputStream(client.getInputStream());
								
								byte[] result = new byte[26];
								pk.readFully(result);
								String str = new ByteToString().encodeHexStr(result);	
								Log.i("bs", str);
								if (str != null && !str.equals("")) {
									int cmd2 = result[4]&0xff;
									System.out.println("cmd2cmd2="+cmd2);
									cmd2(cmd2,result,mContext);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
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
	
	private void cmd2(int cmd,byte[] result,Context mContext){
		switch (cmd) {
		case 129://81 配置设备状态回复包
			Intent unOpenOrCloseOrderPack = new Intent();
			unOpenOrCloseOrderPack.putExtra("datasByte", result);
			unOpenOrCloseOrderPack.setAction("Constants.Action.unWifiNamePack");
			mContext.sendBroadcast(unOpenOrCloseOrderPack);
			break;
		case 9://09 ACK
			Intent unServerACKPack = new Intent();
			unServerACKPack.putExtra("datasByte", result);
			unServerACKPack.setAction("Constants.Action.unACKPack");
			mContext.sendBroadcast(unServerACKPack);
			break;
		default:
			break;
		}
	}
}
