package com.cn.zhihengchuang.walkbank.activity;

import com.isport.trackernew.R;
import com.lingb.helper.StringHelper;
import com.cn.zhihengchuang.walkbank.util.ToastUtil;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UnbindDeviceActivity extends Activity{
	private RelativeLayout re_back;
	private TextView title;
	private Button unbind_device;
	private Button connect_device;
	private TextView device_name, device_mac;
	private ToastUtil toast;
	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_device_unbind);
		init();
		setDefaultData();
	}

	private void setDefaultData() {
		position=getIntent().getIntExtra("position", 0);
		title.setText(StringHelper.replaceDeviceNameToCC431(getIntent().getStringExtra("title"))+" "+(position+1));
		device_name.setText(StringHelper.replaceDeviceNameToCC431(getIntent().getStringExtra("title")));
		device_mac.setText(getIntent().getStringExtra("mac"));
	}

	private void init() {
		toast = new ToastUtil(this);
		device_mac = (TextView) findViewById(R.id.unbind_device_mac);
		device_name = (TextView) findViewById(R.id.unbind_device_name);
		unbind_device = (Button) findViewById(R.id.unbind_device_unbind);
		connect_device=(Button) findViewById(R.id.connect_device);
		if(MyApp.getIntance().mService.mConnectionState==MyApp.getIntance().mService.STATE_CONNECTED){
		   connect_device.setText(R.string.disconnect);
		}else{
		   connect_device.setText(R.string.connect);
		}
		re_back = (RelativeLayout) findViewById(R.id.return_back);
		title = (TextView) findViewById(R.id.title_name);
		re_back.setOnClickListener(new OnClickListenerImpl());
		unbind_device.setOnClickListener(new OnClickListenerImpl());
		connect_device.setOnClickListener(new OnClickListenerImpl());
	}

	private class OnClickListenerImpl implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_back:
				UnbindDeviceActivity.this.finish();
				break;
			case R.id.unbind_device_unbind:
				setResult(102);
				toast.getToast(getResources().getString(R.string.unbind_success));
				UnbindDeviceActivity.this.finish();
				break;
			case R.id.connect_device:
				setResult(103);
				if (MyApp.getIntance().mService != null) {
					if(MyApp.getIntance().mService.mConnectionState==MyApp.getIntance().mService.STATE_CONNECTED){
						MyApp.getIntance().getAppSettings().IS_NEED_CONNECT.setValue(false);
						MyApp.getIntance().mService.disconnect();
					}else{
						MyApp.getIntance().getAppSettings().IS_NEED_CONNECT.setValue(true);
					}
				}
				
				
				UnbindDeviceActivity.this.finish();
				break;
			}
		}
	}
}