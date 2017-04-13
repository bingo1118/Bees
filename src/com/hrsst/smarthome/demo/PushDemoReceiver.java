package com.hrsst.smarthome.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.activity.AlarmActivity;
import com.hrsst.smarthome.activity.SystemMessageActivity;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.global.MyApp;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.Contact;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.igexin.sdk.PushConsts;

public class PushDemoReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    public static final int NOTIFICATION_ID = 0x53256561;
    private Contact mContact;
    private SocketUDP mSocketUDPClient;
    private byte[] payload;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
//                String appid = bundle.getString("appid");
                payload = bundle.getByteArray("payload");
                if(payload!=null&&payload.length>8){
                	int type = payload[0];
                    if(type==1){//报警@@
                    	regFilter();
                    	if(payload.length>12){
                    		byte[] mac = new byte[12];
                    		for (int i = 0; i < 12; i++) {
                    			mac[i] = payload[i + 2];
                    		}
                    		String macStr = new String(mac).trim();
                    		String userNum = SharedPreferencesManager.getInstance().getData(context,  Constants.UserInfo.USER_NUMBER);
                    		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER
                    				, Constants.SeverInfo.PORT);
                    		mSocketUDPClient.startAcceptMessage();
                    		byte[] orderSend = SendServerOrder.ifUserOwnCamera(userNum,macStr);
                    		mSocketUDPClient.sendMsg(orderSend);
                    	}
                    }
                    if(type==2){//共享设备。。
                    	byte[] receive = new byte[payload.length-1];
                    	for(int i = 0;i<receive.length;i++){
                    		receive[i] = payload[i+1];
                    	}
                    	String ss = new String(receive);
                    	String toUserNum = SharedPreferencesManager.getInstance().getData(context, Constants.UserInfo.USER_NUMBER);
                        showDownNotification(context,toUserNum,ss);
                    }
                }             
                break;
            case PushConsts.GET_CLIENTID:
            	String cid = bundle.getString("clientid");
                SharedPreferencesManager.getInstance().putData(MyApp.app,
                        SharedPreferencesManager.SP_FILE_GWELL,
                        SharedPreferencesManager.CID,
                        cid);
            	Intent i = new Intent();
            	i.setAction("STOP_PUSH_INIT_SERVICE");
            	context.sendBroadcast(i);
            	break;
            default:
                break;
        }
    }
    
    @SuppressWarnings("deprecation")
	private void showDownNotification(Context context,String toUserNum,String ss){
    	long when = System.currentTimeMillis();
        //从系统服务中获得通知管理器
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //定义notification

        Notification mNotification = new Notification(
                R.drawable.notification,
                context.getResources().getString(R.string.app_name),
                when);
        mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;

        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.share_notify);

        contentView.setImageViewResource(R.id.share_icon,
                R.drawable.notification);
//        contentView.setTextViewText(R.id.share_text, toUserNum+context.getString(R.string.get_device_share_massage));
//        contentView.setTextViewText(R.id.share_text, getfromusername(ss)+"发来设备共享消息");
        contentView.setTextViewText(R.id.share_text, context.getString(R.string.get_device_share_massage));
        //通知消息与Intent关联
        Intent it=new Intent(context,SystemMessageActivity.class);
        it.putExtra("toUserNum", toUserNum);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent=PendingIntent.getActivity(context, 100, it, PendingIntent.FLAG_CANCEL_CURRENT);
        //具体的通知内容
        mNotification.contentView = contentView;
        mNotification.contentIntent = contentIntent;

        mNotification.defaults = Notification.DEFAULT_SOUND;
        //执行通知
        nm.notify(NOTIFICATION_ID, mNotification);
    } 
    
    private String getfromusername(String str) {
    	String str2="";
    	if(str != null && !"".equals(str)){
    	for(int i=0;i<str.length();i++){
    	if(str.charAt(i)>=48 && str.charAt(i)<=57){
    	str2+=str.charAt(i);
    			}
    		}
    	}
		return str2;
	}

	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("Constants.Action.unIfUserOwnCamera");
		MyApp.app.registerReceiver(mReceiver, filter);
	}
	
	private BroadcastReceiver mReceiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			//收到广播，是否用户拥有摄像机。。
			if(arg1.getAction().equals("Constants.Action.unIfUserOwnCamera")){
				byte[] datas = arg1.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unIfUserOwnCamera(datas);
				if(null!=mUnPackageFromServer){
					String result = mUnPackageFromServer.result;
					if("yes".equals(result)){
						mContact = new Contact();
						mContact.contactId = mUnPackageFromServer.devMac;
						mContact.contactPassword = mUnPackageFromServer.devPwd;
						mContact.contactName = mUnPackageFromServer.devName;
						
						String ifWatching = SharedPreferencesManager.getInstance().getData(arg0, Constants.WatchAction.IF_WATCH);
				    	String cameraId = SharedPreferencesManager.getInstance().getData(arg0, Constants.WatchAction.CAMERA_ID);
				    	if(null!=ifWatching&&ifWatching.equals("yes")&&null!=cameraId&&cameraId.equals(mContact.contactId)){
				    		
				    	}else{
				    		Intent i =new Intent(arg0,AlarmActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("payload", payload);
                            i.putExtra("mContact", mContact);
                            arg0.startActivity(i);
				    	}
					}else{
						Intent i =new Intent(arg0,AlarmActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("payload", payload);
                        i.putExtra("mContact", mContact);
                        arg0.startActivity(i);
					}
				}else{
					Intent i =new Intent(arg0,AlarmActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("payload", payload);
                    i.putExtra("mContact", mContact);
                    arg0.startActivity(i);
				}
				arg0.unregisterReceiver(mReceiver);
			}
		}
	};

}
