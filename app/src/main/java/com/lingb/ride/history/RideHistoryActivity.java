package com.lingb.ride.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cn.zhihengchuang.walkbank.util.DialogHelper;
import com.isport.trackernew.R;
import com.lingb.ride.adapter.HistoryItemAdapter;
import com.lingb.ride.bean.Ride;
import com.lingb.ride.database.DatabaseProvider;
import com.lingb.swipelistview.SwipeMenu;
import com.lingb.swipelistview.SwipeMenuCreator;
import com.lingb.swipelistview.SwipeMenuItem;
import com.lingb.swipelistview.SwipeMenuListView;
import com.lingb.swipelistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lingb.swipelistview.SwipeMenuListView.OnSwipeListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class RideHistoryActivity extends Activity {

	public static final int REQUEST_CODE_HISTORY_DETAIL = 111;

	private List<Ride> rideList = new ArrayList<Ride>();
	private HistoryItemAdapter mAdapter;
	private Dialog dialog_confirm_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_history);

		initUI();
		queryHistory();
		initList();
	};
	
	@Override
	protected void onDestroy() {
		DialogHelper.dismissDialog(dialog_confirm_cancel);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_HISTORY_DETAIL) {
				int position = data.getExtras().getInt("position");
				DatabaseProvider.deleteHistoryOne(RideHistoryActivity.this, rideList.get(position).getDateTime());
				rideList.remove(position);
				mAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_back:
				finish();
				break;
			case R.id.text_delete:
				deleteAllData();
				break;
			case R.id.text_confirm:
				DatabaseProvider.deleteHistoryAll(RideHistoryActivity.this);
				rideList.clear();
				mAdapter.notifyDataSetChanged();
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

	private void deleteAllData() {
		if (rideList != null && rideList.size() > 0) {
			
			if (dialog_confirm_cancel == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.dialog_ride_confirm_cancel, null);
				TextView text_msg = (TextView) view.findViewById(R.id.text_msg);
				text_msg.setText(getString(R.string.ride_delete_all_record));
				TextView text_confirm = (TextView) view.findViewById(R.id.text_confirm);
				TextView text_cancel = (TextView) view.findViewById(R.id.text_cancel);
				text_confirm.setOnClickListener(myOnClickListener);
				text_cancel.setOnClickListener(myOnClickListener);
				dialog_confirm_cancel = new Dialog(RideHistoryActivity.this, R.style.dialog_transparent);
				dialog_confirm_cancel.setContentView(view);
				dialog_confirm_cancel.show();

				dialog_confirm_cancel.setCanceledOnTouchOutside(true);
			} else {
				dialog_confirm_cancel.show();
			}
			
//			new AlertDialog.Builder(RideHistoryActivity.this, style.dialog_style_light).setMessage("      " + getString(R.string.ride_delete_all_record) + "      ").setPositiveButton(getString(R.string.ride_Confirm), new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					DatabaseProvider.deleteHistoryAll(RideHistoryActivity.this);
//					rideList.clear();
//					mAdapter.notifyDataSetChanged();
//				}
//			}).setNegativeButton(getString(R.string.ride_Cancel), new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//
//				}
//			}).show();
		}

	}

	private void queryHistory() {
		rideList = DatabaseProvider.queryHistoryAll(RideHistoryActivity.this);
		Log.i("data", "list = " + rideList + ",  size = " + rideList.size());
		mAdapter = new HistoryItemAdapter(rideList, RideHistoryActivity.this);
		list_history.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

	}

	private void initList() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.button_ride_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		list_history.setMenuCreator(creator);

		// step 2. listener item click event
		list_history.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				// ApplicationInfo item = mAppList.get(position);
				switch (index) {
				case 0:
					// delete
					// delete(item);
					DatabaseProvider.deleteHistoryOne(RideHistoryActivity.this, rideList.get(position).getDateTime());
					rideList.remove(position);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		});

		// set SwipeListener
		list_history.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());

		// test item long click
		list_history.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// Toast.makeText(getApplicationContext(), position +
				// " long click", 0).show();

				return false;
			}
		});
		list_history.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Ride ride = rideList.get(position);
				Intent intent = new Intent(RideHistoryActivity.this, RideHistoryDetailActivity.class);
				intent.putExtra("ride", (Serializable) ride);
				intent.putExtra("position", position);
				RideHistoryActivity.this.startActivityForResult(intent, REQUEST_CODE_HISTORY_DETAIL);

			}
		});
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	private void initUI() {
		TextView text_back = (TextView) findViewById(R.id.text_back);
		text_back.setOnClickListener(myOnClickListener);
		TextView text_delete = (TextView) findViewById(R.id.text_delete);
		text_delete.setOnClickListener(myOnClickListener);
		list_history = (SwipeMenuListView) (findViewById(R.id.list_history));

	}

	private SwipeMenuListView list_history;
}
