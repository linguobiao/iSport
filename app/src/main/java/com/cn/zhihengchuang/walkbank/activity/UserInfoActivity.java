package com.cn.zhihengchuang.walkbank.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetAge;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetHeight;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetSex;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogSetWeight;
import com.cn.zhihengchuang.walkbank.dialogActivity.DialogTakePhoto;
import com.cn.zhihengchuang.walkbank.util.BitmapUtil;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.Tools;

public class UserInfoActivity extends Activity{
	private RelativeLayout re_back;
	private ImageView user_image;
	private LinearLayout ly_age, ly_height, ly_weight, ly_sex;
	private SharedPreferences share = null;
	private Editor edit;
	private TextView age, height, weight, sex;
	private TextView tv_name;
	private TextView weight_user_info_unit;
	private TextView height_user_info_unit;
	private EditText ed_name;
	
	@TargetApi(19)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_profile);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		edit = share.edit();
		init();
		setDefaultData();
	}

	private void setDefaultData() {
		Bitmap bitmap = BitmapUtil.getBitmap(getApplicationContext().getFilesDir().getAbsolutePath() + Constants.SAVEUSERIMAGE);
		if(bitmap != null)
			user_image.setImageBitmap(bitmap);
		String name = share.getString("name", "");
		if (name.equals("")) {
			tv_name.setText(getString(R.string.complete_user_info_hint_nick_name));
		} else {
			tv_name.setText(name);
		}
		
		age.setText(share.getString("age", "30"));
		if(share.getInt("metric", 0)==0){
			weight.setText(share.getString("weight", "75.0"));
			height.setText(share.getString("height", "170"));
			height_user_info_unit.setText("cm");
			weight_user_info_unit.setText("kg");
		}else{
			weight.setText(share.getString("weight", "150.0"));
			height.setText(share.getString("height", "72"));
			weight_user_info_unit.setText("lb");
			height_user_info_unit.setText("inch");
		}
		if(share.getBoolean("is_man", true))
			sex.setText(getResources().getString(R.string.user_info_man));
		else
			sex.setText(getResources().getString(R.string.user_info_woman));
	}

	private void init() {
		re_back = (RelativeLayout) findViewById(R.id.return_back);
		ly_sex = (LinearLayout) findViewById(R.id.user_info_ly4);
		ly_age = (LinearLayout) findViewById(R.id.user_info_ly1);
		ly_height = (LinearLayout) findViewById(R.id.user_info_ly2);
		ly_weight =(LinearLayout) findViewById(R.id.user_info_ly3);
		age = (TextView) findViewById(R.id.user_info_age);
		sex = (TextView) findViewById(R.id.user_info_sex);
		height = (TextView) findViewById(R.id.user_info_height);
		weight = (TextView) findViewById(R.id.user_info_weight);
		weight_user_info_unit=(TextView) findViewById(R.id.weight_user_info_unit);
		height_user_info_unit=(TextView) findViewById(R.id.height_user_info_unit);
		tv_name = (TextView) findViewById(R.id.complete_user_info_tv_name);
		ed_name = (EditText) findViewById(R.id.complete_user_info_ed_name);
		user_image = (ImageView) findViewById(R.id.complete_user_info_image);
		ly_age.setOnClickListener(new OnClickListenerImpl());
		ly_height.setOnClickListener(new OnClickListenerImpl());
		ly_weight.setOnClickListener(new OnClickListenerImpl());
		ly_sex.setOnClickListener(new OnClickListenerImpl());
		tv_name.setOnClickListener(new OnClickListenerImpl());
		user_image.setOnClickListener(new OnClickListenerImpl());
		re_back.setOnClickListener(new OnClickListenerImpl());
	}
	
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {  
	    if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
	        View v = getCurrentFocus();  
	        if (isShouldHideInput(v, ev)) {
	            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	            if (imm != null) {  
	                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
	                ed_name.setVisibility(View.GONE);
					tv_name.setVisibility(View.VISIBLE);
					String name_temp = ed_name.getText().toString();
					if (name_temp.equals("")) {
						name_temp = getString(R.string.complete_user_info_hint_nick_name);
					}
					tv_name.setText(name_temp);
					edit.putString("name", ed_name.getText().toString()).commit();
	            }  
	        }  
	        return super.dispatchTouchEvent(ev);  
	    }   
	    if (getWindow().superDispatchTouchEvent(ev)) {  
	        return true;  
	    }  
	    return onTouchEvent(ev);  
	}  
	
	public boolean isShouldHideInput(View v, MotionEvent event) {  
	    if (v != null && (v instanceof EditText)) {  
	        int[] leftTop = { 0, 0 };  
	        v.getLocationInWindow(leftTop);  
	        int left = leftTop[0];  
	        int top = leftTop[1];  
	        int bottom = top + v.getHeight();  
	        int right = left + v.getWidth();  
	        if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {  
	            return false;  
	        } else {  
	            return true;  
	        }  
	    }  
	    return false;  
	} 

	private class OnClickListenerImpl implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent dialog_intent = null;
			switch (v.getId()) {
			case R.id.return_back:
				UserInfoActivity.this.finish();
				break;
			case R.id.complete_user_info_tv_name:
				tv_name.setVisibility(View.GONE);
				ed_name.setVisibility(View.VISIBLE);
				String name_temp = tv_name.getText().toString();
				if (name_temp.equals(getString(R.string.complete_user_info_hint_nick_name))) {
					name_temp = "";
				}
				ed_name.setText(name_temp);
				break;
			case R.id.complete_user_info_image:
				dialog_intent = new Intent(UserInfoActivity.this, DialogTakePhoto.class);
				break;
			case R.id.user_info_ly1:
				dialog_intent = new Intent(UserInfoActivity.this, DialogSetAge.class);
				dialog_intent.putExtra("is_from_foot", false);
				dialog_intent.putExtra("age", Integer.parseInt(age.getText().toString().trim()));
				break;
			case R.id.user_info_ly2:
				dialog_intent = new Intent(UserInfoActivity.this, DialogSetHeight.class);
				if(share.getInt("metric", 0)==0){
					dialog_intent.putExtra("height", height.getText().toString().replace("cm", "").trim());
				}else{
					dialog_intent.putExtra("height", height.getText().toString().replace("inch", "").trim()) ;
				}
				//dialog_intent.putExtra("height", Integer.parseInt(height.getText().toString().trim()));
				break;
			case R.id.user_info_ly3:
				dialog_intent = new Intent(UserInfoActivity.this, DialogSetWeight.class);
				if(share.getInt("metric", 0)==0){
					dialog_intent.putExtra("weight", weight.getText().toString().replace(" kg", "").trim());
				}else{
					dialog_intent.putExtra("weight", weight.getText().toString().replace(" lb", "").trim() );
				}
				break;
			case R.id.user_info_ly4:
				dialog_intent = new Intent(UserInfoActivity.this, DialogSetSex.class);
				dialog_intent.putExtra("is_man", share.getBoolean("is_man", true));
				dialog_intent.putExtra("is_from_left",
						false);
				break;
			}
			if(dialog_intent != null){
				startActivityForResult(dialog_intent, 101);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null)
			return;
		if(resultCode == 200){//照片
			user_image.setImageBitmap((Bitmap) data.getExtras().getParcelable("data"));
		}else if(resultCode == 201){//年龄
			age.setText(data.getStringExtra("age"));
			edit.putString("age", age.getText().toString().trim()).commit();
		}else if(resultCode == 202){//身高
			
			if(share.getInt("metric", 0)==0){
				height_user_info_unit.setText("cm");
			}else{
				height_user_info_unit.setText("inch");
			}
			height.setText(data.getStringExtra("height"));
			edit.putString("height", height.getText().toString().trim()).commit();
		}else if(resultCode == 203){//体重
			weight.setText(data.getStringExtra("weight"));
			edit.putString("weight", weight.getText().toString().trim()).commit();
			if(share.getInt("metric", 0)==0){
				weight_user_info_unit.setText("kg");
			}else{
				weight_user_info_unit.setText("lb");
			}
			
		}else if(resultCode == 204){//性别
			if(data.getBooleanExtra("is_man", true))
				sex.setText(getResources().getString(R.string.user_info_man));
			else
				sex.setText(getResources().getString(R.string.user_info_woman));
		}
	}

}