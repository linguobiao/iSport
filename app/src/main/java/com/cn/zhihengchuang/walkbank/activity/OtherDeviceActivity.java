package com.cn.zhihengchuang.walkbank.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.isport.trackernew.R;

public class OtherDeviceActivity extends Activity{
	private RelativeLayout re_back;
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_device_other);
		init();
		setDefaultData();
	}

	private void setDefaultData() {
		title.setText(getIntent().getStringExtra("title"));
	}

	private void init() {
		re_back = (RelativeLayout) findViewById(R.id.return_back);
		title = (TextView) findViewById(R.id.title_name);
		re_back.setOnClickListener(new OnClickListenerImpl());
	}

	private class OnClickListenerImpl implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				OtherDeviceActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}
}