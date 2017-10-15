package com.lingb.ride.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.StringHelper;
import com.lingb.ride.bean.Ride;
import com.cn.zhihengchuang.walkbank.entity.DeviceEntity;

public class HistoryItemAdapter extends BaseAdapter {
	private List<Ride> lists;
	private Context context;
	
	public HistoryItemAdapter (List<Ride> lists , Context context){
		if(lists!=null)
			this.lists = lists;
		else
			this.lists = new ArrayList<Ride>();
		this.context = context;
	}

	@Override
	public int getCount() {
		Log.i("data", "**** size = " + lists.size());
		return lists.size();
	}
	
	public void notifymDataSetChanged(List<Ride> lists){
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.view_history_item, null);
			new ViewHolder(convertView);			
		}
		Log.i("data", "**** " + Global.sdf_yyyy_MM_dd_HH_mm_ss.format(lists.get(position).getDateTime().getTimeInMillis()));
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.text_datetime.setText(Global.sdf_yyyy_MM_dd_HH_mm_ss.format(lists.get(position).getDateTime().getTimeInMillis()));
		return convertView;
	}
	
	class ViewHolder {
		TextView text_datetime;
		
		public ViewHolder(View view) {
			text_datetime = (TextView) view.findViewById(R.id.text_datetime);
			view.setTag(this);
		}
	}
}