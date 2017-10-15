package com.lingb.ride.history;

import com.cn.zhihengchuang.walkbank.util.DialogHelper;
import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.ViewHelper;
import com.lingb.ride.RideMainActivity;
import com.lingb.ride.bean.Profile;
import com.lingb.ride.bean.Ride;
import com.lingb.ride.database.DatabaseProvider;
import com.lingb.ride.settings.RideUserActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class RideHistoryDetailActivity extends Activity {
	
	private int position = 0;
	private int unitType = Global.TYPE_UNIT_METRIC;
	private Dialog dialog_confirm_cancel;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_history_detail);
		Intent intent = getIntent();
		Ride ride = (Ride) intent.getExtras().get("ride");
		position = intent.getExtras().getInt("position");
		Profile profile = DatabaseProvider.queryProfile(RideHistoryDetailActivity.this, RideUserActivity.DEF_NAME);
		if (profile != null) {
			unitType = profile.getUnit();
		}
		initUI();
		initValue(ride);
	};

	@Override
	protected void onDestroy() {
		DialogHelper.dismissDialog(dialog_confirm_cancel);
		super.onDestroy();
	}

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_back:
				finish();
				break;
			case R.id.text_delete:
				clickDelete();
				break;
			case R.id.text_confirm:
				Intent intent = new Intent();
				intent.putExtra("position", position);
				setResult(RESULT_OK, intent);
				RideHistoryDetailActivity.this.finish();
				DialogHelper.dismissDialog(dialog_confirm_cancel);
				break;
			case R.id.text_cancel:
				DialogHelper.dismissDialog(dialog_confirm_cancel);
				break;
			default:
				break;
			}
		}
	};
	
	private void clickDelete() {
		
		if (dialog_confirm_cancel == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_ride_confirm_cancel, null);
			TextView text_msg = (TextView) view.findViewById(R.id.text_msg);
			text_msg.setText(getString(R.string.ride_delete_record));
			TextView text_confirm = (TextView) view.findViewById(R.id.text_confirm);
			TextView text_cancel = (TextView) view.findViewById(R.id.text_cancel);
			text_confirm.setOnClickListener(myOnClickListener);
			text_cancel.setOnClickListener(myOnClickListener);
			dialog_confirm_cancel = new Dialog(RideHistoryDetailActivity.this, R.style.dialog_transparent);
			dialog_confirm_cancel.setContentView(view);
			dialog_confirm_cancel.show();

			dialog_confirm_cancel.setCanceledOnTouchOutside(true);
		} else {
			dialog_confirm_cancel.show();
		}
		
//		new AlertDialog.Builder(RideHistoryDetailActivity.this, style.dialog_style_light).setMessage("         " + getString(R.string.ride_delete_record) + "         ").setPositiveButton(getString(R.string.ride_Confirm), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Intent intent = new Intent();
//				intent.putExtra("position", position);
//				setResult(RESULT_OK, intent);
//				RideHistoryDetailActivity.this.finish();
//			}
//		}).setNegativeButton(getString(R.string.ride_Cancel), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//			}
//		}).show();
	}

	
	private void initValue(Ride ride) {
		if (ride != null) {
			text_datetime.setText(Global.sdf_yyyy_MM_dd_HH_mm_ss.format(ride.getDateTime().getTime()));
			int hour = ride.getTotalTime()/ 3600;
			int minute = (ride.getTotalTime() - hour * 3600) / 60;
			int second = ride.getTotalTime()% 60;
			text_time.setText(Global.df_00.format(hour) + ":" + Global.df_00.format(minute) + ":" + Global.df_00.format(second));
			ViewHelper.setText(text_distance, ride.getTotalDistance(), Global.df_0_00, unitType, getString(R.string.ride_km), getString(R.string.ride_mi));
			ViewHelper.setText(text_speed, ride.getSpeed(), Global.df_0_0, unitType, getString(R.string.ride_kmh), getString(R.string.ride_mph));
			ViewHelper.setText(text_speed_max, ride.getSpeedMax(), Global.df_0_0, unitType, getString(R.string.ride_kmh), getString(R.string.ride_mph));
			ViewHelper.setText(text_speed_min, ride.getSpeedMin(), Global.df_0_0, unitType, getString(R.string.ride_kmh), getString(R.string.ride_mph));
//		text_distance.setText(Global.df_0_0.format(ride.getTotalDistance()) + " m");
//		text_speed.setText(Global.df_0_0.format(ride.getSpeed()) + " km/h");
//		text_speed_max.setText(Global.df_0_0.format(ride.getSpeedMax()) + " km/h");
//		text_speed_min.setText(Global.df_0_0.format(ride.getSpeedMin()) + " km/h");
			text_cadence.setText(Global.df_0.format(ride.getCadence()) + " rpm");
			text_cadence_max.setText(Global.df_0.format(ride.getCadenceMax()) + " rpm");
			text_cadence_min.setText(Global.df_0.format(ride.getCadenceMin()) + " rpm");
			
		}
		
	}

	private void initUI() {
		TextView text_back = (TextView) findViewById(R.id.text_back);
		text_back.setOnClickListener(myOnClickListener);
		TextView text_delete = (TextView) findViewById(R.id.text_delete);
		text_delete.setOnClickListener(myOnClickListener);
		
		text_datetime = (TextView) findViewById(R.id.text_datetime);
		text_speed = (TextView) findViewById(R.id.text_speed);
		text_speed_max = (TextView) findViewById(R.id.text_speed_max);
		text_speed_min = (TextView) findViewById(R.id.text_speed_min);
		text_cadence = (TextView) findViewById(R.id.text_cadence);
		text_cadence_max = (TextView) findViewById(R.id.text_cadence_max);
		text_cadence_min = (TextView) findViewById(R.id.text_cadence_min);
		text_time = (TextView) findViewById(R.id.text_time);
		text_distance = (TextView) findViewById(R.id.text_distance);

	}

	private TextView text_datetime;
	private TextView text_speed, text_speed_max, text_speed_min;
	private TextView text_cadence, text_cadence_max, text_cadence_min;
	private TextView text_time, text_distance;

}
