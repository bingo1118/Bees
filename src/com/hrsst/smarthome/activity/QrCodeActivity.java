package com.hrsst.smarthome.activity;

import com.google.zxing.Result;

import cn.itguy.zxingportrait.CaptureActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;
/**
 * ∂˛Œ¨¬Î…®√Ë¿‡£¨ºÃ≥–CaptureActivityø‚
 * @author bin
 *
 */
public class QrCodeActivity extends CaptureActivity {
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		super.handleDecode(rawResult, barcode, scaleFactor);
		beepManager.playBeepSoundAndVibrate();
		Toast.makeText(QrCodeActivity.this, "Success", Toast.LENGTH_SHORT).show();
		Intent intent=new Intent();
		intent.putExtra("msg", rawResult.toString());
		setResult(1, intent);
		finish();
	}
}
