package com.cn.zhihengchuang.walkbank.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.entity.Bar;
import com.cn.zhihengchuang.walkbank.interfaces.FragmentCallBackListener;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.view.SleepBarGraph;
import com.cn.zhihengchuang.walkbank.view.TasksCompletedView;

public class SleepFragment extends Fragment implements FragmentCallBackListener {
	private TasksCompletedView task_view;
	private ImageView image_date_icon, today;
	private TextView text_date;
	private TextView text_Week;
	private String date;
	private String str;
	private Context context;
	private FragmentCallBackListener callback;
	private int values;
	private TextView no_values;
	private SleepBarGraph g;
	private ArrayList<Bar> points;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
	}
	
	private void setData() {
		str = DateUtil.getWeek(date, context);
		if(date == null)
			date = DateUtil.getCurrentDate();
		text_Week.setText(str );
		text_date.setText( date.replace("-", "/"));
		task_view.setProgress(50);
		points = new ArrayList<Bar>();
		for(int i=0;i<10;i++){
			Bar d = new Bar();
			if(i==0){
				d.setName("22:00");
			}else if((i+1)==10){
				d.setName("7:00");
			}else{
				d.setName("");
			}
			if(i==2 || i==3 || i == 6){
				d.setValue(3000);
				d.setColor(Color.parseColor("#fde575"));
				values ++;
			}else if(i==5 || i==7 || i == 4){//深度
				d.setValue(2100);
				d.setColor(Color.parseColor("#128bf1"));
			}else if(i==0 || i==1 || i==8 || i == 9){
				d.setValue(2700);
				d.setColor(Color.parseColor("#79e4ff"));
				values ++;
			}
			d.setSetvaluetextenble(Boolean.FALSE);
			points.add(d);
		}
		g.setBars(points);
		if(values == 0){
			no_values.setVisibility(View.VISIBLE);
		}else{
			no_values.setVisibility(View.GONE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sleep, container, false);
		task_view = (TasksCompletedView) view.findViewById(R.id.tasks_view);
		image_date_icon = (ImageView) view.findViewById(R.id.main_fragment_date);
		today = (ImageView) view.findViewById(R.id.main_fragment_today_date);
		text_date = (TextView) view.findViewById(R.id.main_fragment_text_date);
		text_Week = (TextView) view.findViewById(R.id.main_fragment_text_Week);
		no_values = (TextView) view.findViewById(R.id.exercise_no_values);
		g = (SleepBarGraph) view.findViewById(R.id.bargraph);
		today.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.callBack();
			}
		});
		setData();
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = ((FragmentCallBackListener) activity);
	}

	@Override
	public void callBack(String str) {
		this.date = str;
		setData();
	}

	@Override
	public void callBack() {
		
	}
	
}