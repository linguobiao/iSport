package com.cn.zhihengchuang.walkbank.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.adapter.DeviceBondAdapter;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.BltModel;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lingb.global.Global;
import com.lingb.helper.StringHelper;

public class MenuSetActivity extends Activity {
	private LinearLayout bind_device ,
	ly_user_info, ly_about;
	private TextView tvBack;
	private ListView listView_layout;
	private DeviceBondAdapter mAdapter;
	ArrayList<DeviceEntity> lists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		sendBroadcast(new Intent(Global.ACTION_UPDATE_CONNECT));
		registerBoradcastReceiver();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		init();
		if (mAdapter != null) {
			mAdapter.notifymDataSetChanged(lists);
		}
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	private void init() {
		LinearLayout layout_device_change = (LinearLayout) findViewById(R.id.layout_device_change);
		layout_device_change.setOnClickListener(new OnClickListenerImpl());
		
		bind_device = (LinearLayout) findViewById(R.id.menu_bind_device);
		//clear_cash_layout = (LinearLayout) findViewById(R.id.clear_cash_layout);
	    
		ly_user_info = (LinearLayout) findViewById(R.id.ly_user_info);
		ly_about = (LinearLayout) findViewById(R.id.ly_about);
		ly_about.setOnClickListener(new OnClickListenerImpl());
		listView_layout = (ListView) findViewById(R.id.listView_layout);
		tvBack = (TextView) findViewById(R.id.back_tv);
		lists = new ArrayList<DeviceEntity>();
		mAdapter = new DeviceBondAdapter(lists, this);
		listView_layout.setAdapter(mAdapter);
		DbUtils db = DbUtils.create(getApplicationContext());
		List<BltModel> blts;
		lists.clear();
		if(!TextUtils.isEmpty(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString())){
			DeviceEntity entity = new DeviceEntity();
			String deviceName = MyApp.getIntance().getAppSettings().LAST_CONNECT_NAME.getValue().toString();
			if (deviceName!= null) {
				if (deviceName.contains("_")) {
					deviceName.split("_");
					
				}
				deviceName = StringHelper.replaceDeviceNameToCC431(deviceName);
			}
			entity.setName(deviceName);
			entity.setMac(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
			lists.add(entity);
			mAdapter.notifymDataSetChanged(lists);
		};
		
		
		/*try {
			blts = db.findAll(Selector.from(BltModel.class));
			if (blts != null) {
				for (int i = blts.size()-1 ; i>= 0; i--) {
					DeviceEntity entity = new DeviceEntity();
					BltModel blt = blts.get(i);
					entity.setName(blt.getBltname());
					entity.setMac(blt.getBltid());
					lists.add(entity);
				}
				mAdapter.notifymDataSetChanged(lists);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}*/
		
		
		listView_layout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				DeviceEntity entity=lists.get(arg2);
//				if("W240N".equals(entity.getName())  || "CC431".equals(entity.getName())){
					if (MyApp.getIntance().list_device_new.contains(entity.getName())) {
						
						Intent intent = new Intent(MenuSetActivity.this,
								LoveSportsSettingActivity.class);
						intent.putExtra("device", entity);
						intent.putExtra("position", arg2);
						startActivity(intent);
//					}
				}else{
					Intent intent = new Intent(MenuSetActivity.this,
							W240SettingActivity.class);
					intent.putExtra("device", entity);
					startActivity(intent);
				}
			}
		});
		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		bind_device.setOnClickListener(new OnClickListenerImpl());
		ly_user_info.setOnClickListener(new OnClickListenerImpl());
		//clear_cash_layout.setOnClickListener(new OnClickListenerImpl());
		// love_sport_layout.setOnClickListener(new OnClickListenerImpl());
	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.menu_bind_device:
				intent = new Intent(MenuSetActivity.this,
						ManageDeviceActivity.class);
				break;
			
			case R.id.ly_user_info:
				intent = new Intent(MenuSetActivity.this,
						UserInfoActivity.class);
				break;
			case R.id.ly_about:
				intent = new Intent(MenuSetActivity.this,
						AboutUsActivity.class);
				break;
			/*case R.id.clear_cash_layout:
				new AlertDialog.Builder(MenuSetActivity.this)
				 .setMessage(getResources().getString(R.string.setting_the_default_setting))
				  	.setPositiveButton(getResources().getString(R.string.user_info_done), new AlertDialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							clear();
						}
					}).setNegativeButton(getResources().getString(R.string.user_info_cancle), new AlertDialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							
						}
					})
				  	.show();
				break;*/
			
				
			}
			if (intent != null) {
				startActivity(intent);
			}
		}
	}
	private void clear() {
		/*share.edit().putString(entity.getMac()
				+ "target_distance", "7000").commit();
		if (share.getInt("metric", 0) == 0) {
			share.edit().putString(entity.getMac()
					+ "foot_distance", "60").commit();
			
		} else {
			share.edit().putString(entity.getMac()
					+ "foot_distance", "24").commit();
			
		}
		share.edit().putBoolean(entity.getMac() + "left_hand",true).commit();
		showDialog(getString(R.string.please_wait));
		DbUtils db = DbUtils.create(LoveSportsSettingActivity.this);
		db.configAllowTransaction(true);
		db.configDebug(true);
		WhereBuilder builder = WhereBuilder.b("uuid","==",
				entity.getMac());
		try {
			db.delete(PedometerModel.class, builder);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMyDialog.dismiss();*/
		Toast.makeText(MenuSetActivity.this, getResources().getString(R.string.clear_data_seccess), 1000).show();
		//sendBroadcast(new Intent(Constants.CLEAR_AlL));

	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.CONNECTING_DEVICE);
		myIntentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		
		myIntentFilter.addAction(Global.ACTION_FINISH_BAND);
		
		// 注册广播
		registerReceiver(mReceiver, myIntentFilter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.CONNECTING_DEVICE.equals(action)) {
				DbUtils db = DbUtils.create(getApplicationContext());
				List<BltModel> blts;
				lists.clear();
				try {
					blts = db.findAll(Selector.from(BltModel.class));
					if (blts != null) {
						for (int i = blts.size()-1 ; i>= 0; i--) {
							DeviceEntity entity = new DeviceEntity();
							BltModel blt = blts.get(i);
							String deviceName = blt.getBltname();
							if (deviceName != null) {
								if (deviceName.contains("_")) {
									deviceName = deviceName.split("_")[0];
								}
								deviceName = StringHelper.replaceDeviceNameToCC431(deviceName);
							}
							entity.setName(deviceName);
							entity.setMac(blt.getBltid());
							lists.add(entity);
						}

					}
					mAdapter.notifymDataSetChanged(lists);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(BleService.ACTION_GATT_CONNECTED.equals(action)){
				if(lists!=null){
					mAdapter.notifymDataSetChanged(lists);
				}
				
			}// 连接断开 ***************************************************************
			else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
				if(lists!=null){
					mAdapter.notifymDataSetChanged(lists);
				}
			} else if (action.equals(Global.ACTION_FINISH_BAND)) {
				MenuSetActivity.this.finish();
			}

		}
	};

}