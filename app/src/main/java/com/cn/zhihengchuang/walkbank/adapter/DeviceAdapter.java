package com.cn.zhihengchuang.walkbank.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.StringHelper;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;

public class DeviceAdapter extends BaseAdapter {
	private ArrayList<DeviceEntity> lists;
	private Context context;
	
	public DeviceAdapter (ArrayList<DeviceEntity> lists , Context context){
		if(lists!=null)
			this.lists = lists;
		else
			this.lists = new ArrayList<DeviceEntity>();
		this.context = context;
	}

	@Override
	public int getCount() {
		return lists.size();
	}
	
	public void notifymDataSetChanged(ArrayList<DeviceEntity> lists){
		if(lists!=null){
			this.lists = lists;
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
		convertView = inflater.inflate(R.layout.view_scan_device_item, null);
		TextView content = (TextView) convertView.findViewById(R.id.manage_device_name);
		ImageView bind_icon = (ImageView) convertView.findViewById(R.id.bind_device_item_icon);
		ImageView rssi_icon = (ImageView) convertView.findViewById(R.id.bind_device_item_rssi);
		ImageView detail_icon = (ImageView) convertView.findViewById(R.id.bind_device_item_detail);
		DeviceEntity entity = lists.get(position);
		String name = entity.getName();
		if (name != null) {
			name = StringHelper.formatDeviceName(name);
		}
		if (MyApp.getIntance().list_device_ride.contains(name)) {
			content.setText(StringHelper.getRideDeviceName(name));
		} else {
			content.setText(StringHelper.replaceDeviceNameToCC431(name));
		}
		if (entity.isIs_device()) {
			if(entity.isIs_bind()){
				bind_icon.setVisibility(View.VISIBLE);
			}else{
				bind_icon.setVisibility(View.GONE);
			}
			if(entity.getRssi() > -70){
				rssi_icon.setImageResource(R.drawable.ic_rssi_3_bars);
			}else if(entity.getRssi() > - 85){
				rssi_icon.setImageResource(R.drawable.ic_rssi_2_bars);
			}else{
				rssi_icon.setImageResource(R.drawable.ic_rssi_1_bars);
			}
		} else {
			bind_icon.setVisibility(View.GONE);
			rssi_icon.setVisibility(View.GONE);
			detail_icon.setVisibility(View.GONE);
		}
		return convertView;
	}
}