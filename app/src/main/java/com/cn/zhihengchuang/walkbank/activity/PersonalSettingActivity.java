package com.cn.zhihengchuang.walkbank.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetAge;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetHeight;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetSex;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetWeight;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogTakePhoto;
import com.cn.zhihengchuang.walkbank.util.BitmapUtil;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.TimeZoneWrapper;
import com.cn.zhihengchuang.walkbank.util.Tools;

public class PersonalSettingActivity extends Activity implements AdapterView.OnItemSelectedListener {
	private SharedPreferences share;
	private Editor edit;
	private ImageView user_image;
	private TextView age;
	private TextView height;
	private TextView weight;
	private TextView sex;
	private TextView confirm_text;
	private TextView metric_text;
	private TextView weight_user_info_unit;
	private TextView height_user_info_unit;
	private RelativeLayout re_back;
	private LinearLayout ly_sex;
	private LinearLayout ly_age;
	private LinearLayout ly_height;
	private LinearLayout ly_weight;
	private LinearLayout ly_metric;
	private Spinner mTimeZoneSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_first_start);
		share = getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		edit = share.edit();
		init();
		setDefaultData();
	}

	private void setDefaultData() {
		Bitmap bitmap = BitmapUtil.getBitmap(getApplicationContext().getFilesDir().getAbsolutePath() + Constants.SAVEUSERIMAGE);
		if (bitmap != null)
			user_image.setImageBitmap(bitmap);
		age.setText(share.getString("age", "35"));
		
		if(share.getInt("metric", 0) == 1) {
			height.setText(share.getString("height", "72.0"));
			weight.setText(share.getString("weight", "150.0"));
			weight_user_info_unit.setText("lb");
			height_user_info_unit.setText("inch");
		} else {
			height.setText(share.getString("height", "170.0"));
			weight.setText(share.getString("weight", "75.0"));
			weight_user_info_unit.setText("kg");
			height_user_info_unit.setText("cm");
		}
		
		if (share.getBoolean("is_man", true)) {
			sex.setText(getResources().getString(R.string.user_info_man));
		} else {
			sex.setText(getResources().getString(R.string.user_info_woman));
		}
		if (share.getInt("metric", 0) == 1) {
			metric_text.setText(getResources().getString(R.string.Inch));
		} else {
			metric_text.setText(getResources().getString(R.string.metric));
		}
	}

	private void init() {
		re_back = (RelativeLayout) findViewById(R.id.return_back);
		ly_sex = (LinearLayout) findViewById(R.id.user_info_ly4);
		ly_age = (LinearLayout) findViewById(R.id.user_info_ly1);
		ly_height = (LinearLayout) findViewById(R.id.user_info_ly2);
		ly_weight = (LinearLayout) findViewById(R.id.user_info_ly3);
		ly_metric = (LinearLayout) findViewById(R.id.user_info_ly5);
		age = (TextView) findViewById(R.id.user_info_age);
		sex = (TextView) findViewById(R.id.user_info_sex);
		weight_user_info_unit = (TextView) findViewById(R.id.weight_user_info_unit);
		height_user_info_unit = (TextView) findViewById(R.id.height_user_info_unit);
		metric_text = (TextView) findViewById(R.id.metric);
		height = (TextView) findViewById(R.id.user_info_height);
		confirm_text = (TextView) findViewById(R.id.confirm_text);
		weight = (TextView) findViewById(R.id.user_info_weight);
		user_image = (ImageView) findViewById(R.id.complete_user_info_image);
		ly_age.setOnClickListener(new OnClickListenerImpl());
		ly_height.setOnClickListener(new OnClickListenerImpl());
		ly_weight.setOnClickListener(new OnClickListenerImpl());
		ly_sex.setOnClickListener(new OnClickListenerImpl());
		user_image.setOnClickListener(new OnClickListenerImpl());
		re_back.setOnClickListener(new OnClickListenerImpl());
		confirm_text.setOnClickListener(new OnClickListenerImpl());
		ly_metric.setOnClickListener(new OnClickListenerImpl());
		mTimeZoneSpinner = (Spinner) findViewById(R.id.timeZoneSpinner);

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timezone_values, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mTimeZoneSpinner.setAdapter(adapter);
		mTimeZoneSpinner.setOnItemSelectedListener(this);
	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent dialog_intent = null;
			switch (v.getId()) {
			case R.id.return_back:
				finish();
				break;

			case R.id.complete_user_info_image:
				dialog_intent = new Intent(PersonalSettingActivity.this, DialogTakePhoto.class);
				break;
			case R.id.user_info_ly1:
				dialog_intent = new Intent(PersonalSettingActivity.this, DialogSetAge.class);
				dialog_intent.putExtra("age", Integer.parseInt(age.getText().toString().trim()));
				break;
			case R.id.user_info_ly2:
				dialog_intent = new Intent(PersonalSettingActivity.this, DialogSetHeight.class);
				dialog_intent.putExtra("height", height.getText().toString().replace("inch", "").trim());
				/*
				 * if(share.getInt("metric", 0)==1){ dialog_intent.putExtra("height", Integer.parseInt(height.getText().toString().replace("cm", "").trim())); }else{ dialog_intent.putExtra("height", Integer.parseInt(height.getText().toString().replace("inch", "").trim())); }
				 */

				break;
			case R.id.user_info_ly3:
				dialog_intent = new Intent(PersonalSettingActivity.this, DialogSetWeight.class);

				dialog_intent.putExtra("weight", weight.getText().toString().replace(" kg", "").trim());
				break;
			case R.id.user_info_ly4:
				dialog_intent = new Intent(PersonalSettingActivity.this, DialogSetSex.class);
				dialog_intent.putExtra("is_man", share.getBoolean("is_man", true));
				dialog_intent.putExtra("is_from_left", false);
				break;
			case R.id.user_info_ly5:
				dialog_intent = new Intent(PersonalSettingActivity.this, DialogSetSex.class);
				int unit = share.getInt("metric", 0);
				if (unit == 1) {
					dialog_intent.putExtra("is_man", false);
				} else {
					dialog_intent.putExtra("is_man", true);
				}
				
				dialog_intent.putExtra("is_from_left", false);
				dialog_intent.putExtra("is_from_metric", true);
				break;
			case R.id.confirm_text:
				setResult(RESULT_OK);
				finish();
				break;
			}
			if (dialog_intent != null) {
				startActivityForResult(dialog_intent, 101);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (resultCode == 200) {// 照片
			user_image.setImageBitmap((Bitmap) data.getExtras().getParcelable("data"));
		} else if (resultCode == 201) {// 年龄
			age.setText(data.getStringExtra("age"));
			edit.putString("age", age.getText().toString().trim()).commit();
		} else if (resultCode == 202) {// 身高
			height.setText(data.getStringExtra("height"));
			edit.putString("height", height.getText().toString().trim()).commit();
		} else if (resultCode == 203) {// 体重
			weight.setText(data.getStringExtra("weight"));
			edit.putString("weight", weight.getText().toString().trim()).commit();
		} else if (resultCode == 204) {// 性别
			if (data.getBooleanExtra("is_man", true))
				sex.setText(getResources().getString(R.string.user_info_man));
			else
				sex.setText(getResources().getString(R.string.user_info_woman));
		} else if (resultCode == 205) {// 性别
			if (data.getBooleanExtra("is_man", true)) {
				if (share.getInt("metric", 0) != 0) {
					weight.setText(Tools.roundHalfUp(Float.parseFloat(weight.getText().toString()) * 0.45359237d) + "");
					edit.putString("weight", weight.getText().toString().trim()).commit();
					height.setText(Tools.roundHalfUp(Float.parseFloat(height.getText().toString()) * 2.54d) + "");
					edit.putString("height", height.getText().toString().trim()).commit();
				}
				height_user_info_unit.setText("cm");
				weight_user_info_unit.setText("kg");
				edit.putInt("metric", 0).commit();
				metric_text.setText(getResources().getString(R.string.metric));
			} else {
				if (share.getInt("metric", 0) != 1) {
					weight.setText(Tools.roundHalfUp(Float.parseFloat(weight.getText().toString()) * 2.2046d) + "");
					edit.putString("weight", weight.getText().toString().trim()).commit();
					height.setText(Tools.roundHalfUp(Float.parseFloat(height.getText().toString()) * 0.393700787401d) + "");
					edit.putString("height", height.getText().toString().trim()).commit();
				}
				edit.putInt("metric", 1).commit();
				metric_text.setText(getResources().getString(R.string.Inch));
				weight_user_info_unit.setText("lb");
				height_user_info_unit.setText("inch");

			}

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		TimeZoneWrapper tzWrapper = new TimeZoneWrapper(parent.getItemAtPosition(position).toString());
		// mTimeZoneInformationTextView.setText(tzWrapper.printInformation());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
