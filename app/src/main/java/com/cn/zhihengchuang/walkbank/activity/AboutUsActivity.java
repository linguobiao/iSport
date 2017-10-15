package com.cn.zhihengchuang.walkbank.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.lingb.global.Global;

public class AboutUsActivity extends Activity {
	private TextView re_back, text_version;

	@TargetApi(19)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_about_us);
		init();
	}

	private void init() {
		re_back = (TextView) findViewById(R.id.return_back);
		re_back.setOnClickListener(new OnClickListenerImpl());
		TextView text_link = (TextView) findViewById(R.id.text_link);

		if (Global.PACKAGE_NAME.startsWith(Global.PACKAGE_NAME_READY)) {
			text_link.setVisibility(View.VISIBLE);
		} else {
			text_link.setVisibility(View.GONE);
		}
		
		text_version = (TextView) findViewById(R.id.text_version);

		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			text_version.setText(pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				AboutUsActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}

}