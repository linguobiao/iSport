package com.cn.zhihengchuang.walkbank.dialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.util.Constants;

public class DialogSetSex extends Activity {
	private RelativeLayout re_man, re_woman;
	private ImageView image_man, image_woman;
	private TextView tv_man, tv_woman;
	private TextView dialog_title;
	private SharedPreferences share = null;
	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_select);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		edit = share.edit();
		init();
		setDefaultData();
	}

	private void setDefaultData() {
		if(getIntent().getBooleanExtra("is_from_left", true)){
			String mac=getIntent().getStringExtra("mac");
			if(share.getBoolean(mac+"left_hand", true)){
				image_man.setVisibility(View.VISIBLE);
				image_woman.setVisibility(View.INVISIBLE);
				tv_man.setTextColor(Color.parseColor("#3c3c3c"));
				tv_woman.setTextColor(Color.parseColor("#b8b8b8"));
			}else{
				image_man.setVisibility(View.INVISIBLE);
				image_woman.setVisibility(View.VISIBLE);
				tv_man.setTextColor(Color.parseColor("#b8b8b8"));
				tv_woman.setTextColor(Color.parseColor("#3c3c3c"));
			}
			tv_man.setText(getResources().getString(R.string.left_hand));
			tv_woman.setText(getResources().getString(R.string.right_hand));
			dialog_title.setText(R.string.setting_wear_way);
		}else{
			if(!getIntent().getBooleanExtra("is_from_metric", false)){
				if(getIntent().getBooleanExtra("is_man", true)){
					image_man.setVisibility(View.VISIBLE);
					image_woman.setVisibility(View.INVISIBLE);
					tv_man.setTextColor(Color.parseColor("#3c3c3c"));
					tv_woman.setTextColor(Color.parseColor("#b8b8b8"));
				}else{
					image_man.setVisibility(View.INVISIBLE);
					image_woman.setVisibility(View.VISIBLE);
					tv_man.setTextColor(Color.parseColor("#b8b8b8"));
					tv_woman.setTextColor(Color.parseColor("#3c3c3c"));
				}
			}else{
				Log.i("test", "is man = " + getIntent().getBooleanExtra("is_man", true));
				if(getIntent().getBooleanExtra("is_man", true)){
					image_man.setVisibility(View.VISIBLE);
					image_woman.setVisibility(View.INVISIBLE);
					tv_man.setTextColor(Color.parseColor("#3c3c3c"));
					tv_woman.setTextColor(Color.parseColor("#b8b8b8"));
				}else{
					image_man.setVisibility(View.INVISIBLE);
					image_woman.setVisibility(View.VISIBLE);
					tv_man.setTextColor(Color.parseColor("#b8b8b8"));
					tv_woman.setTextColor(Color.parseColor("#3c3c3c"));
				}
				tv_man.setText(getResources().getString(R.string.metric));
				tv_woman.setText(getResources().getString(R.string.Inch));
				dialog_title.setText(R.string.set_metric);
			
			}
		}
		
		
	}

	private void init() {
		tv_man = (TextView) findViewById(R.id.set_sex_man);
		tv_woman = (TextView) findViewById(R.id.set_sex_woman);
		re_man = (RelativeLayout) findViewById(R.id.set_sex_re_man);
		re_woman = (RelativeLayout) findViewById(R.id.set_sex_re_woman);
		image_man = (ImageView) findViewById(R.id.set_sex_man_image);
		image_woman = (ImageView) findViewById(R.id.set_sex_woman_image);
		dialog_title=(TextView) findViewById(R.id.dialog_title);
		re_man.setOnClickListener(new OnClickListenerImpl());
		re_woman.setOnClickListener(new OnClickListenerImpl());
	}
	
	public TextView getTv_man() {
		return tv_man;
	}

	public void setTv_man(TextView tv_man) {
		this.tv_man = tv_man;
	}

	public TextView getTv_woman() {
		return tv_woman;
	}

	public void setTv_woman(TextView tv_woman) {
		this.tv_woman = tv_woman;
	}

	private class OnClickListenerImpl implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.set_sex_re_man:
				image_man.setVisibility(View.VISIBLE);
				image_woman.setVisibility(View.INVISIBLE);
				edit.putBoolean("is_man", true).commit();
				intent.putExtra("is_man", true);
				break;
			case R.id.set_sex_re_woman:
				image_man.setVisibility(View.INVISIBLE);
				image_woman.setVisibility(View.VISIBLE);
				edit.putBoolean("is_man", false).commit();
				intent.putExtra("is_man", false);
				break;
			}
			if(!getIntent().getBooleanExtra("is_from_metric", false)){
				setResult(204, intent);
			}else{
				setResult(205, intent);
			}
			DialogSetSex.this.finish();
		}
	}
}