package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.util.BitmapCache;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ActionComnfigerActivity extends Activity {
	private Context mContext;
	private ImageView next_action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_configer);
		mContext = this;
		ImageView liuchengtu1 = (ImageView) findViewById(R.id.liuchengtu1_image);
		Bitmap mBitmap = BitmapCache.getInstance().getBitmap(R.drawable.liuchengtu1,mContext);
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), mBitmap);
		liuchengtu1.setImageDrawable(bd);
		next_action = (ImageView) findViewById(R.id.next_action);
		next_action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkWifi()){
					Intent intent = new Intent(ActionComnfigerActivity.this,
							IntroducedNextOneActivity.class);
					startActivity(intent);
					finish();
				}else{
					Toast.makeText(mContext, "Çë¿ªÆôÊÖ»úWIFI", 1).show();
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
				
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BitmapCache.getInstance().clearCache();
	}
	
	private boolean checkWifi() {  
	    boolean isWifiConnect = false;  
	    ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo[] networkInfos = cm.getAllNetworkInfo();  
		for (int i = 0; i<networkInfos.length; i++) {  
		    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {  
		       if(networkInfos[i].getType() == cm.TYPE_MOBILE) {  
		           isWifiConnect = false;  
		       }  
		       if(networkInfos[i].getType() == cm.TYPE_WIFI) {  
		           isWifiConnect = true;  
		       }  
		    }  
		}  
		return isWifiConnect;  
	}
}
