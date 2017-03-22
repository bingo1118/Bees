package com.hrsst.smarthome.mygridview.lib.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hrsst.smarthome.R;
import com.hrsst.smarthome.mygridview.lib.PullToRefreshBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingLayout extends BaseLoadingLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView headerImage, header_pic;
	private final ProgressBar headerProgress;
	private final TextView headerText;
	private final TextView headerText1;
	private TextView loadingText;

	private String pullLabel, pullLabel1;
	private String refreshingLabel;
	private String releaseLabel, releaseLabel1;
	private String currentTime;// 当前的更新时间
	private String oldTime;// 上一次更新时间
	private Context mContext;
	private boolean flagShow;// 是否出现几亿人那个图片：

	private final Animation rotateAnimation, resetRotateAnimation;

	public LoadingLayout(Context context, final int mode, String releaseLabel, String releaseLabel1, String pullLabel, String pullLabel1, String refreshingLabel, boolean flagShow1) {
		super(context, mode, releaseLabel, releaseLabel1, pullLabel, pullLabel1, refreshingLabel);
		mContext = context;
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
		headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);// 下拉可以刷新
		headerText1 = (TextView) header.findViewById(R.id.pull_to_refresh_text1);// 下拉可以刷新
		loadingText = (TextView) header.findViewById(R.id.pull_to_refresh_loading_text);// 刷新加载
		headerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
		headerProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);
		header_pic = (ImageView) header.findViewById(R.id.pull_head_pic);
		flagShow = flagShow1;

		if (flagShow)
			viewVisible();

		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		this.releaseLabel = releaseLabel;// "松开即可刷新"
		this.pullLabel = pullLabel;// 拖拉可以刷新
		this.releaseLabel1 = releaseLabel1;
		this.refreshingLabel = refreshingLabel;

		if (pullLabel1.equals("")) {
			this.pullLabel1 = mContext.getResources().getString(R.string.pull_loading_old_geng_ganggang);
		} else {
			this.pullLabel1 = mContext.getResources().getString(R.string.pull_loading_old_geng) + oldTime;
		}

		currentTime = getMyCurrentTime();
		this.releaseLabel1 = mContext.getResources().getString(R.string.pull_loading_new_geng) + currentTime;

		switch (mode) {
		case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
			headerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
			break;
		case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
		default:
			headerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
			break;
		}
	}

	/**
	 * 得到当前时间格式：
	 *
	 * @return
	 */
	public static String getMyCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);

		/*
		 * 获取年月日当前时间 SimpleDateFormat sDateFormat = new
		 * SimpleDateFormat("yyyy-MM-dd   hh:mm:ss"); String date =
		 * sDateFormat.format(new java.util.Date());
		 */

		/*
		 * 如果想获取当前的年月,则可以这样写(只获取时间或秒种一样): SimpleDateFormat sdf=new
		 * SimpleDateFormat("yyyy-MM"); String date=sdf.format(new
		 * java.util.Date());
		 */

		/*
		 * 当然还有就是可以指定时区的时间(待):
		 * df=DateFormat.getDateTimeInstance(DateFormat.FULL,
		 * DateFormat.FULL,Locale.CHINA); System.out.println(df.format(new
		 * Date()));
		 */
	}

	// 几亿图片提示：
	private void viewVisible() {
		header_pic.setVisibility(View.VISIBLE);
		headerImage.setVisibility(View.GONE);
		headerProgress.setVisibility(View.GONE);
		headerText.setVisibility(View.GONE);
		headerText1.setVisibility(View.GONE);
		loadingText.setVisibility(View.GONE);
	}

	public void reset() {// 避免最后又出现一下
		// headerText.setText(pullLabel);
		// headerImage.setVisibility(View.VISIBLE);
		if (pullLabel1.equals("")) {
			this.pullLabel1 = mContext.getResources().getString(R.string.pull_loading_old_geng_ganggang);
		} else {
			this.pullLabel1 = mContext.getResources().getString(R.string.pull_loading_old_geng) + oldTime;
		}

		headerProgress.setVisibility(View.GONE);
		loadingText.setVisibility(View.GONE);

		headerText.setVisibility(View.VISIBLE);
		headerText1.setVisibility(View.VISIBLE);
		headerText.setText(pullLabel);
		headerText1.setText(pullLabel1);
		headerImage.setVisibility(View.VISIBLE);

		if (flagShow)
			viewVisible();
	}

	public void releaseToRefresh() {
		currentTime = getMyCurrentTime();
		this.releaseLabel1 = mContext.getResources().getString(R.string.pull_loading_new_geng) + currentTime;
		oldTime = currentTime;// 记住当前更新时间，下次下拉显示
		headerText.setVisibility(View.VISIBLE);
		headerText1.setVisibility(View.VISIBLE);
		headerText.setText(releaseLabel);
		headerText1.setText(releaseLabel1);
		headerImage.clearAnimation();
		headerImage.startAnimation(rotateAnimation);

		if (flagShow)
			viewVisible();
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
	}

	public void refreshing() {

		loadingText.setText(refreshingLabel);
		loadingText.setVisibility(View.VISIBLE);

		headerImage.clearAnimation();
		headerText.setVisibility(View.GONE);
		headerText1.setVisibility(View.GONE);
		headerImage.setVisibility(View.GONE);
		headerProgress.setVisibility(View.VISIBLE);

		if (flagShow)
			viewVisible();
	}

	public void setRefreshingLabel(String refreshingLabel) {
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		headerText.setText(pullLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(resetRotateAnimation);
	}

	public void setTextColor(int color) {
		headerText.setTextColor(color);
	}

}
