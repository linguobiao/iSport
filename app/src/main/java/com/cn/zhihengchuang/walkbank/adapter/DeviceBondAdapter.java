package com.cn.zhihengchuang.walkbank.adapter;

import java.util.ArrayList;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.isport.trackernew.R;
import com.lingb.helper.StringHelper;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceBondAdapter extends BaseAdapter {
	private ArrayList<DeviceEntity> lists;
	private Context context;

	public DeviceBondAdapter(ArrayList<DeviceEntity> lists, Context context) {
		if (lists != null)
			this.lists = lists;
		else
			this.lists = new ArrayList<DeviceEntity>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	public void notifymDataSetChanged(ArrayList<DeviceEntity> lists) {
		if (lists != null) {
			this.lists = lists;
			notifyDataSetChanged();
		} else {
			this.lists = new ArrayList<DeviceEntity>();
			notifyDataSetChanged();
		}
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.view_settings_device_item, null);
		TextView content = (TextView) convertView.findViewById(R.id.deviceName);
		DeviceEntity device = lists.get(position);
		if (MyApp.getIntance().mService != null) {
			if (MyApp.getIntance().mService.mConnectionState == MyApp
					.getIntance().mService.STATE_CONNECTED
					&& MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC
							.getValue().toString().equals(device.getMac())) {
				content.setText(StringHelper.replaceDeviceNameToCC431(device.getName()) + " "+(position+1)
						+context.getResources().getString( R.string.Connected));
			} else {
				content.setText(StringHelper.replaceDeviceNameToCC431(device.getName()) + " "+(position+1));
			}
		}

		return convertView;
	}
}
