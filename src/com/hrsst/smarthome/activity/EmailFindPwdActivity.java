package com.hrsst.smarthome.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.util.CheakEmail;
import com.hrsst.smarthome.util.MD5;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailFindPwdActivity extends Activity implements OnClickListener {
	private Context mContext;
	private EditText emailNum, pwdNum, pwdNum1;
	private String emailNumStr, pwdNumStr, pwdNum1Str;
	private Button findBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_find);
		mContext = this;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		emailNum = (EditText) findViewById(R.id.email_find_et);
		pwdNum = (EditText) findViewById(R.id.email_find_pwd_et);
		pwdNum1 = (EditText) findViewById(R.id.email_find_rewrite_pwd_et);
		findBtn = (Button) findViewById(R.id.email_find_btn);
		findBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.email_find_btn:
			emailNumStr = emailNum.getText().toString().trim();
			pwdNumStr = pwdNum.getText().toString().trim();
			pwdNum1Str = pwdNum1.getText().toString().trim();
			boolean isEmailed = CheakEmail.getInstance()
					.cheakEmail(emailNumStr);
			if (isEmailed == true) {
				if (null != pwdNumStr && pwdNumStr.length() > 0
						&& pwdNumStr.equals(pwdNum1Str)) {
					find(emailNumStr, pwdNumStr);
				} else {
					Toast.makeText(mContext, "两次输入的密码不一致，请重新输入", 1).show();
				}
			} else {
				Toast.makeText(mContext, "邮箱格式不正确，请重新输入", 1).show();
			}
			break;

		default:
			break;
		}
	}

	private void find(final String emailNumStr2, final String pwdNumStr2) {
		// TODO Auto-generated method stub
		String pwd = new MD5().getMD5ofStr(pwdNumStr2);
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userID", emailNumStr2);
		map.put("userPwd", pwd);
		map.put("flag", "2");
		JsonArrayPostRequest mJsonRequest = new JsonArrayPostRequest(
				Constants.UPDATE_USER_PWD_URL, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						if (response.length() > 0) {
							try {
								String obj = response.get(0).toString();
								System.out.println("obj=" + obj);
								if (null != obj && obj.length() > 0
										&& obj.equals("success")) {
									Intent i = new Intent(
											EmailFindPwdActivity.this,
											EmailActiviteActivity.class);
									i.putExtra("userID", emailNumStr2);
									i.putExtra("userPwd", pwdNumStr2);
									startActivity(i);
									finish();

								}
								if (null != obj && obj.length() > 0
										&& obj.equals("false")) {
									Toast.makeText(mContext, "找回失败，请重新找回密码", 1)
											.show();
								}
								if (null != obj && obj.length() > 0
										&& obj.equals("userenotxit")) {
									Toast.makeText(mContext, "用户不存在", 1).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						findBtn.setEnabled(true);
					}
				}, map);
		mQueue.add(mJsonRequest);
	}

}
