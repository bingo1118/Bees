package com.hrsst.smarthome.widget;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.hrsst.smarthome.R;
import com.hrsst.smarthome.adapter.AlarmTypeAdapter;
import com.hrsst.smarthome.adapter.YzwAdapter;
import com.hrsst.smarthome.global.Constants;
import com.hrsst.smarthome.volley.JsonArrayPostRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;

public class NormalDialog {
	Context context;
	String title_str, content_str, btn1_str, btn2_str;
	AlertDialog dialog;
	private int style = 999;
	String[] list_data = new String[] {};
	private OnButtonOkListener onButtonOkListener;
	private OnButtonCancelListener onButtonCancelListener;
	private OnItemClickListener onItemClickListener;
	private OnCancelListener onCancelListener;
	
	public NormalDialog(Context context, String title, String content,
			String btn1, String btn2) {
		this.context = context;
		this.title_str = title;
		this.content_str = content;
		this.btn1_str = btn1;
		this.btn2_str = btn2;
	}
	
	public NormalDialog(Context context, String title,
			String btn1, String btn2) {
		this.context = context;
		this.title_str = title;
		this.btn1_str = btn1;
		this.btn2_str = btn2;
	}

	public NormalDialog(Context context) {
		this.context = context;
		this.title_str = "";
		this.content_str = "";
		this.btn1_str = "";
		this.btn2_str = "";
	}
	
	public static final int DIALOG_STYLE_NORMAL = 1;
	public static final int DIALOG_STYLE_LOADING = 2;
	public static final int DIALOG_STYLE_UPDATE = 3;
	public static final int DIALOG_STYLE_DOWNLOAD = 4;
	public static final int DIALOG_STYLE_PROMPT = 5;
	
	public void showDialog() {
		switch (style) {
		case DIALOG_STYLE_NORMAL:
			showNormalDialog();
			break;
		case DIALOG_STYLE_PROMPT:
			showPromptDialog();
			break;
		case DIALOG_STYLE_LOADING:
			showLoadingDialog();
			break;
		default:
			showNormalDialog();
			break;
		}
	}
	
	public void showPromptDialog() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_prompt, null);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		content.setText(content_str);
		title.setText(title_str);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.dismiss();
					}
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);

		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}
	
	public void showNormalDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_normal, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView button1 = (TextView) view.findViewById(R.id.button1_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		title.setText(title_str);
		content.setText(content_str);
		button1.setText(btn1_str);
		button2.setText(btn2_str);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != dialog) {
					dialog.dismiss();
				}

				onButtonOkListener.onClick();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.cancel();
					}
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);

		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	
	public void showLoadingDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		title.setText(title_str);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.Loading_dialog_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}
	
	public void setTitle(String title) {
		this.title_str = title;
	}

	public void setTitle(int id) {
		this.title_str = context.getResources().getString(id);
	}

	public void setListData(String[] data) {
		this.list_data = data;
	}

	public void setCanceledOnTouchOutside(boolean bool) {
		dialog.setCanceledOnTouchOutside(bool);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setCancelable(boolean bool) {
		dialog.setCancelable(bool);
	}

	public void cancel() {
		dialog.cancel();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public boolean isShowing() {
		return dialog.isShowing();
	}

	public void setBtnListener(TextView btn1, TextView btn2) {

	}

	public void setStyle(int style) {
		this.style = style;
	}

	public interface OnButtonOkListener {
		public void onClick();
	}

	public interface OnButtonCancelListener {
		public void onClick();
	}

	public void setOnButtonOkListener(OnButtonOkListener onButtonOkListener) {
		this.onButtonOkListener = onButtonOkListener;
	}

	public void setOnButtonCancelListener(
			OnButtonCancelListener onButtonCancelListener) {
		this.onButtonCancelListener = onButtonCancelListener;
	}

	public void setOnCancelListener(OnCancelListener onCancelListener) {
		this.onCancelListener = onCancelListener;
		Log.i("dxsSMTP", "setlistener");
	}
	
	private ListView listrView,alarm_statusList;
	public void showSelectDialog(int item) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_list, null);
		TextView button1 = (TextView) view.findViewById(R.id.calarm_button1_text);
		TextView button2 = (TextView) view.findViewById(R.id.calarm_button2_text);
		listrView = (ListView) view.findViewById(R.id.calarm_type_list);
		alarm_statusList = (ListView) view.findViewById(R.id.alarm_status);
		TextView yzw_tv = (TextView) view.findViewById(R.id.ywz_tv);
		if(item>1){
			alarm_statusList.setVisibility(View.GONE);
			yzw_tv.setVisibility(View.GONE);
			DisplayMetrics dMetrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
			listrView.getLayoutParams().width=dMetrics.widthPixels;
		}
		findAllAlarmType(view);
		button1.setText(btn1_str);
		button2.setText(btn2_str);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonOkListener.onClick();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.cancel();
					}
				} else {
					onButtonCancelListener.onClick();
					if (null != dialog) {
						dialog.cancel();
					}
				}
			}
		});
		
	}
	
	OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
            {
             return true;
            }
            else
            {
             return false;
            }
        }
    } ;
	
	private void findAllAlarmType(final View view){
		List<String> lt = new ArrayList<String>();
		lt.add("ÑÌ¸ÐÌ½²âÆ÷");
		lt.add("ÃÅ´ÅÌ½²âÆ÷");
		lt.add("È¼ÆøÌ½²âÆ÷");
		lt.add("ºìÍâÌ½²âÆ÷");
		lt.add("Ë®½û");
		lt.add("ÍòÄÜÒ£¿ØÆ÷");
		AlarmTypeAdapter mAlarmTypeAdapter = new AlarmTypeAdapter(context,lt);
		listrView.setAdapter(mAlarmTypeAdapter);
		YzwAdapter mYzwAdapter = new YzwAdapter(context,Constants.STATUS);
		alarm_statusList.setAdapter(mYzwAdapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.setOnKeyListener(keylistener);
		dialog.setCancelable(false);
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}
}
