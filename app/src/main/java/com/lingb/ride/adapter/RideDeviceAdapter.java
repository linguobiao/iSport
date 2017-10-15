package com.lingb.ride.adapter;

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
import com.lingb.helper.StringHelper;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;

public class RideDeviceAdapter extends BaseAdapter {
	private ArrayList<DeviceEntity> lists;
	private Context context;
	
	public RideDeviceAdapter (ArrayList<DeviceEntity> lists , Context context){
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
		convertView = inflater.inflate(R.layout.view_ride_device_item, null);
		TextView content = (TextView) convertView.findViewById(R.id.manage_device_name);
		ImageView bind_icon = (ImageView) convertView.findViewById(R.id.bind_device_item_icon);
		DeviceEntity entity = lists.get(position);
		content.setText("CBGT 4 AT");
		if (entity.isIs_device()) {
			if(entity.isIs_bind()){
				bind_icon.setVisibility(View.VISIBLE);
			}else{
				bind_icon.setVisibility(View.GONE);
			}
		} else {
			bind_icon.setVisibility(View.GONE);
		}
		return convertView;
	}
}