package com.hrsst.smarthome.global;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.activity.ForwardDownActivity;
import com.hrsst.smarthome.util.CrashHandler;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.util.UpdateManager;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyApp extends Application{
	public static MyApp app;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private RemoteViews cur_down_view;
	public static final int NOTIFICATION_DOWN_ID = 0x53256562;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app = this;
		//启动集错程序
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
	}
	
	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}
	
	/**
	 * 创建下载图标
	 */
	@SuppressWarnings("deprecation")
	public  void showDownNotification(int state,int value) {
		boolean isShowNotify = SharedPreferencesManager.getInstance().getIsShowNotify(this);
		if(isShowNotify){
			
			mNotificationManager = getNotificationManager();
			mNotification = new Notification();
			long when = System.currentTimeMillis();
			mNotification = new Notification(
					R.drawable.notification,
					this.getResources().getString(R.string.app_name),
					when);
			// 放置在"正在运行"栏目中
			mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
	
			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.notify_down_bar);
			cur_down_view = contentView;
			contentView.setImageViewResource(R.id.icon,
					R.drawable.notification);
			
			Intent intent = new Intent(this,ForwardDownActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			
			switch(state){
			case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
				cur_down_view.setTextViewText(R.id.down_complete_text, this.getResources().getString(R.string.down_complete_click));
				cur_down_view.setTextViewText(R.id.progress_value,"100%");
				contentView.setProgressBar(R.id.progress_bar, 100, 100, false);
				intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_SUCCESS);
				break;
			case UpdateManager.HANDLE_MSG_DOWNING:
				cur_down_view.setTextViewText(R.id.down_complete_text, this.getResources().getString(R.string.down_londing_click));
				cur_down_view.setTextViewText(R.id.progress_value,value+"%");
				contentView.setProgressBar(R.id.progress_bar, 100, value, false);
				intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWNING);
				break;
			case UpdateManager.HANDLE_MSG_DOWN_FAULT:
				cur_down_view.setTextViewText(R.id.down_complete_text, this.getResources().getString(R.string.down_fault_click));
				cur_down_view.setTextViewText(R.id.progress_value,value+"%");
				contentView.setProgressBar(R.id.progress_bar, 100, value, false);
				intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_FAULT);
				break;
			}
			mNotification.contentView = contentView;
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mNotification.contentIntent = contentIntent;
			
			mNotificationManager.notify(NOTIFICATION_DOWN_ID,
					mNotification);
		}
	}
	
	public void hideDownNotification(){
		mNotificationManager = getNotificationManager();
		mNotificationManager.cancel(NOTIFICATION_DOWN_ID);
		
	}
	
	
}
