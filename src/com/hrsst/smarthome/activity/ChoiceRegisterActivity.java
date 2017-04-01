package com.hrsst.smarthome.activity;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.global.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class ChoiceRegisterActivity extends Activity implements OnClickListener {
	private Button nextBtn;
	private TextView title_text;
	private RadioButton phone_register, email_register;
	private int current_type;
	Context mContext;
	private String actionFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register1);
		actionFlag = getIntent().getExtras().getString("actionFlag");
		mContext = this;
		init();
	}

	public void init() {
		nextBtn = (Button) findViewById(R.id.next_step);
		phone_register = (RadioButton) findViewById(R.id.register_type_phone);
		email_register = (RadioButton) findViewById(R.id.register_type_email);
		title_text = (TextView) findViewById(R.id.title_text);
		if (actionFlag.equals("Reset")) {
			title_text.setText(R.string.find_psw);
			phone_register.setText(R.string.find_by_phone);
			email_register.setText(R.string.find_by_email);
		}
		nextBtn.setOnClickListener(this);
		phone_register.setOnClickListener(this);
		email_register.setOnClickListener(this);
		phone_register.setChecked(true);
		email_register.setChecked(false);
		current_type = Constants.RegisterType.PHONE;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.next_step:
			if (actionFlag.equals("Reset")) {
				if (current_type == Constants.RegisterType.PHONE) {
					Intent intent = new Intent(mContext,
							PhoneFindPwdActivity.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(mContext,
							EmailFindPwdActivity.class);
					startActivity(intent);
					finish();
				}
			} else {
				if (current_type == Constants.RegisterType.PHONE) {
					Intent register_phone = new Intent(mContext,
							PhoneRegisterActivity.class);
					startActivity(register_phone);
					finish();
				} else {
					Intent register_email = new Intent(mContext,
							EmailRegisterActivity.class);
					register_email.putExtra("isEmailRegister", true);
					startActivity(register_email);
					finish();
				}
			}
			finish();
			break;
		case R.id.register_type_phone:
			phone_register.setChecked(true);
			email_register.setChecked(false);
			current_type = Constants.RegisterType.PHONE;
			break;
		case R.id.register_type_email:
			email_register.setChecked(true);
			phone_register.setChecked(false);
			current_type = Constants.RegisterType.EMALL;
			break;
		default:
			break;
		}
	}
}
