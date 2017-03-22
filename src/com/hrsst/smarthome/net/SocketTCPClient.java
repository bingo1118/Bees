package com.hrsst.smarthome.net;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.hrsst.smarthome.util.ByteToString;

import android.util.Log;

public class SocketTCPClient {
	static Socket client;

	static BufferedReader in;
	
	static DataInputStream pk;

	private static SocketTCPClient socketClient;

	private static final String TAG = "SocketClient";

	private String site;

	private int port;

	private boolean onGoinglistner = true;

	private ClientMsgListener clientListener;

	public static interface ClientMsgListener {

		public void handlerErorMsg(String errorMsg);

		public void handlerHotMsg(byte[] hotMsg);

	}

	public static synchronized SocketTCPClient newInstance(String site, int port,
			ClientMsgListener clientListener) {

		if (socketClient == null) {
			socketClient = new SocketTCPClient(site, port, clientListener);
		}
		Log.i(TAG, "socketClient =" + socketClient);
		return socketClient;
	}

	// ÇÐ»»ÏûÏ¢¼àÌýÆ÷
	public void setMsgListener(ClientMsgListener listener) {
		this.clientListener = listener;
	}

	private SocketTCPClient(String site, int port, ClientMsgListener clientListener) {

		this.site = site;
		this.port = port;
		this.clientListener = clientListener;
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
					//clientListener.handlerHotMsg("INT_CLIENT_SUCCESS");
				} catch (UnknownHostException e) {
					e.printStackTrace();
					//clientListener.handlerErorMsg("INT_CLIENT_FAIL");
					Log.d(TAG, "UnknownHostException");
				} catch (IOException e) {
					e.printStackTrace();
					//clientListener.handlerErorMsg("INT_CLIENT_FAIL");
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
							System.out.println("send msg.....success");
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
								byte[] bs = new byte[pk.available()];
								for(int i = 0;i<bs.length;i++){
									bs[i]=pk.readByte();
								}
								//String str = new String(bs, "utf-8");
								String str = new ByteToString().encodeHexStr(bs);
								if (str != null && !str.equals("")) {
									clientListener.handlerHotMsg(bs);
								}
							}
						} catch (Exception e) {
							System.out.println("client="+client);
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
	
	
}
