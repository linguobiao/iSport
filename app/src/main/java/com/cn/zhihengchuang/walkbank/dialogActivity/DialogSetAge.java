package com.cn.zhihengchuang.walkbank.dialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.numberpi.NumberPicker;
import com.cn.zhihengchuang.walkbank.util.Constants;

public class DialogSetAge extends Activity {
	private Button mPickerok = null;
	private Button mPickeresc = null;
	private Boolean is_from_foot;
	int src = 0;
	NumberPicker np = null;
	private SharedPreferences share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME,
				Activity.MODE_PRIVATE); // 指定操作的文件名
		int divierId = this.getResources().getIdentifier("android:id/titleDivider", null, null);  
	    View divider = findViewById(divierId);  
	    divider.setBackgroundColor(Color.parseColor("#754c50")); 
		mPickerok = (Button) findViewById(R.id.numberPickerok);
		mPickeresc = (Button) findViewById(R.id.numberPickercanle);
		np = (NumberPicker) findViewById(R.id.numberPicker);
		int age = getIntent().getIntExtra("age", 30);
		is_from_foot=getIntent().getBooleanExtra("is_from_foot", false);
		if(is_from_foot){
			if(share.getInt("metric", 0)==0){
				np.setMaxValue(150);
				np.setMinValue(30);
				np.setLable("  " + "cm");
				setTitle(R.string.set_the_pace);
			}else{
				np.setMaxValue(59);
				np.setMinValue(12);
				np.setLable("  " + "inch");
				setTitle(R.string.set_the_pace);
			}
			
		}else{
			np.setMaxValue(80);
			np.setMinValue(20);
			setTitle(getResources().getString(R.string.user_info_set_age_title));
			np.setLable("  " + getResources().getString(R.string.complete_user_info_default_age_unit));
		}
		np.setValue(age);
		np.setFocusable(true);
		np.setFocusableInTouchMode(true);
		np.setMinimumWidth(100);
		mPickerok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				src = np.getValue();
				Intent intent = new Intent();
				intent.putExtra("age", "" + src);
				setResult(201, intent);
				// 关闭掉这个Activity
				finish();

			}
		});
		mPickeresc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(50, intent); // 50 为空
				// 关闭掉这个Activity
				finish();
			}
		});
	}
}