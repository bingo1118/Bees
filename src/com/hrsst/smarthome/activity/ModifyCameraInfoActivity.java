package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.net.SocketUDP;
import com.hrsst.smarthome.order.SendServerOrder;
import com.hrsst.smarthome.order.UnPackServer;
import com.hrsst.smarthome.pojo.Contact;
import com.hrsst.smarthome.pojo.UnPackageFromServer;
import com.hrsst.smarthome.pojo.UserDevice;
import com.hrsst.smarthome.util.SharedPreferencesManager;
import com.hrsst.smarthome.widget.NormalDialog;
import com.p2p.core.P2PHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyCameraInfoActivity extends Activity implements OnClickListener{
	private TextView mSave;
	Context mContext;
	EditText old_pwd, new_pwd, re_new_pwd,contactName;
	TextView contactId;
	Contact contact;
	NormalDialog dialog;
	private boolean isRegFilter = false;
	String password_old, password_new, password_re_new;
	private UserDevice mUserDevice;
	private String fromUserNum;
	private SocketUDP mSocketUDPClient;
	private String modifyName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_contact);
		contact = (Contact) getIntent().getSerializableExtra("contact");
		mContext = this;
		initCompent();
		regFilter();
		fromUserNum = SharedPreferencesManager.getInstance().getData(mContext,
				Constants.UserInfo.USER_NUMBER);
		mSocketUDPClient = SocketUDP.newInstance(Constants.SeverInfo.SERVER,
				Constants.SeverInfo.PORT);
		mSocketUDPClient.startAcceptMessage();
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_SET_DEVICE_PASSWORD);
		filter.addAction(Constants.P2P.RET_SET_DEVICE_PASSWORD);
		filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
		filter.addAction("Constants.Action.unActionCamera");
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals("Constants.Action.unActionCamera")) {
				byte[] datas = intent.getExtras().getByteArray("datasByte");
				UnPackageFromServer mUnPackageFromServer = UnPackServer.unActionCamera(datas);
				if(mUnPackageFromServer!=null){
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					String binderResult = mUnPackageFromServer.binderResult;
					switch (binderResult) {
					case "success":
						contact.contactName = modifyName;
//						contact.contactPassword = password_new;
						contact.contactPassword = mUserDevice.getCameraPwd();//@@5.4
						Toast.makeText(mContext, R.string.change_success, Toast.LENGTH_SHORT).show();
						finishThis();
						break;
					case "failed":
						Toast.makeText(mContext, R.string.change_fail, 1).show();
						break;
					default:
						break;
					}
				}
			}
			
			if (intent.getAction()
					.equals(Constants.P2P.RET_SET_DEVICE_PASSWORD)) {
				int result = intent.getIntExtra("result", -1);
				
				if (result == Constants.P2P_SET.DEVICE_PASSWORD_SET.SETTING_SUCCESS) {
					byte[] orderSend =SendServerOrder.ModifyDev(mUserDevice,(byte)0x02);
					mSocketUDPClient.sendMsg(orderSend);
				} else {
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					Toast.makeText(mContext, R.string.operation_fail, Toast.LENGTH_SHORT).show();
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_DEVICE_PASSWORD)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					Toast.makeText(mContext, R.string.old_psw_error, Toast.LENGTH_SHORT).show();
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Toast.makeText(mContext, R.string.network_fail, Toast.LENGTH_SHORT).show();
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_DEVICE_NOT_SUPPORT)) {
				finish();
			}
		}
	};
	
	public void initCompent() {
		contactId = (TextView) findViewById(R.id.contactId);
		contactName = (EditText) findViewById(R.id.contactName);
		old_pwd = (EditText) findViewById(R.id.contactPwdOld);
		new_pwd = (EditText) findViewById(R.id.contactPwdNewA);
		re_new_pwd = (EditText) findViewById(R.id.contactPwdNewB);
		contactId.setText(contact.contactName);
		contactName.setText(contact.contactName);
		contactName.setSelection(contact.contactName.length());//@@
		mSave=(TextView)findViewById(R.id.save);
		mSave.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.save:
			password_old = old_pwd.getText().toString().trim();
			password_new = new_pwd.getText().toString().trim();
			password_re_new = re_new_pwd.getText().toString().trim();
			modifyName = contactName.getText().toString().trim();
			
			if ("".equals(modifyName.trim())) {
				Toast.makeText(mContext, R.string.please_input_device_name, Toast.LENGTH_SHORT).show();
				return;
			}
			
			//名称不能超过8个字符@@
			if (modifyName.length()>8) {
				Toast.makeText(mContext, R.string.no_more_then_ten_words, Toast.LENGTH_SHORT).show();
				return;
			}
			
			if ("".equals(password_old.trim())) {
				Toast.makeText(mContext, R.string.please_input_old_massage_psw, Toast.LENGTH_SHORT).show();
				return;
			}

			if ("".equals(password_new.trim())) {
				Toast.makeText(mContext, R.string.please_input_new_massage_psw, Toast.LENGTH_SHORT).show();
				return;
			}
			
			if ("".equals(password_re_new.trim())) {
				Toast.makeText(mContext, R.string.please_input_psw_again, Toast.LENGTH_SHORT).show();
				return;
			}
			//限制密码为纯数字@@
//			if (password_new.charAt(0) == '0'||!isNumeric(password_new)) {
//				Toast.makeText(mContext, R.string.visitor_pwd_must_digit, Toast.LENGTH_SHORT).show();
//				return;
//			}
			//密码长度不能超过6-15个字符@@
			if (password_new.length()>15||password_new.length()<6) {
				Toast.makeText(mContext, R.string.psw_have_to_be_num, Toast.LENGTH_SHORT).show();
				return;
			}

			if (!password_re_new.equals(password_new)) {
				Toast.makeText(mContext,R.string.differentpassword, Toast.LENGTH_SHORT).show();
				return;
			}

			if (null == dialog) {
				dialog = new NormalDialog(this, this.getResources().getString(
						R.string.verification), "", "", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
			}
			dialog.showDialog();
			
			String password_o=P2PHandler.getInstance().EntryPassword(password_old);//如果包含英文，转换为数字格式。。
			String password_n=P2PHandler.getInstance().EntryPassword(password_new);//如果包含英文，转换为数字格式。。
			mUserDevice = new UserDevice();
			mUserDevice.setUserNum(fromUserNum);
//			mUserDevice.setCameraPwd(password_new);
			mUserDevice.setCameraPwd(password_n);//@@5.4
			mUserDevice.setDevName(modifyName);
			mUserDevice.setDevMac(contact.contactId);
			
			P2PHandler.getInstance().setDevicePassword(contact.contactId,
					password_o, password_n);
			break;
		}
	}

	@Override
	public void onDestroy() {
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
		super.onDestroy();
		
	}
	
	private void finishThis(){
		Intent i = new Intent(mContext,ApMonitorActivity.class);
		i.putExtra("contact",contact);
		startActivity(i);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){
			finishThis();
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 判断字符串是否全为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		  for (int i = 0; i < str.length(); i++){
		   System.out.println(str.charAt(i));
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
}
