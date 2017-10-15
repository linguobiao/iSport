package com.cn.zhihengchuang.walkbank.fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.AreaData;
import org.xclcharts.renderer.XEnum;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.view.AreaChart03View;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class HostoryChartFragment extends BaseFragment {
	private TextView date;
	private AreaChart03View chart;
	private LinkedList<AreaData> mDataset;
	private LinkedList<String> mLabels;
	private SharedPreferences share;
	private final static String TAG = BleService.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_hostory_chart,
				container, false);
		date = (TextView) view.findViewById(R.id.date);
		share = getActivity().getSharedPreferences(
				Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		chart = (AreaChart03View) view.findViewById(R.id.chart_area);
		String hostory = getArguments().getString("hostory");
		String position = getArguments().getString("position");
		int state = getArguments().getInt("state");
		int Select = getArguments().getInt("Select");
		AreaData line1 = null;
		// date.setText(hostory);
		if (state == 0) {
			line1 = new AreaData("小熊", initDayData(hostory, chart, Select),
					Color.parseColor("#DDF9C7"), Color.WHITE,
					Color.parseColor("#DDF9C7"));
		} else if (state == 1) {
			line1 = new AreaData("小熊", initWeekData(position, hostory, chart,
					Select), Color.parseColor("#DDF9C7"), Color.WHITE,
					Color.parseColor("#DDF9C7"));
		} else if (state == 2) {
			line1 = new AreaData("小熊", initMonthData(position, hostory, chart,
					Select), Color.parseColor("#DDF9C7"), Color.WHITE,
					Color.parseColor("#DDF9C7"));
		} else if (state == 3) {
			line1 = new AreaData("小熊",
					initEveryWeekData(hostory, chart, Select),
					Color.parseColor("#DDF9C7"), Color.WHITE,
					Color.parseColor("#DDF9C7"));
		}

		// String sportHalf = settings.SPORT_HALF.getValue().toString();

		/*
		 * List<Double> dataSeries1 = new LinkedList<Double>(); for (int i = 0;
		 * i < sport.length; i++) {
		 * dataSeries1.add(Double.parseDouble(sport[i])); }
		 */

		// 不显示点
		line1.setDotStyle(XEnum.DotStyle.HIDE);// 隐藏图形
		// line1.setDotStyle(XEnum.DotStyle.RECT);
		// line1.setLabelVisible(true);
		line1.setApplayGradient(true);
		line1.setAreaBeginColor(Color.WHITE);
		line1.setAreaEndColor(Color.parseColor("#DDF9C7"));
		mDataset.add(line1);
		chart.setmDataset(mDataset);
		chart.setmLabels(mLabels);
		chart.initView();
		return view;
	}

	/**
	 * 日表
	 * 
	 * @param hostory
	 */
	private List<Double> initDayData(String hostory,
			AreaChart03View chart_area, int Select) {
		mDataset = new LinkedList<AreaData>();
		mLabels = new LinkedList<String>();
		mLabels.add(DateUtil.getMouthDay(hostory));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 1)));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 2)));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 3)));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 4)));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 5)));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 6)));
		mLabels.add(DateUtil.getMouthDay(DateUtil.getDateStr(hostory, 7)));
		DbUtils db = DbUtils.create(getActivity());
		PedometerModel pedometer1 = null;
		List<Double> dataSeries1 = new LinkedList<Double>();
		try {
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", hostory)
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
					
				}

			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 1))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 2))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 3))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 4))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 5))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 6))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 7))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			Double[] sortArray = new Double[dataSeries1.size()];
			for (int i = 0; i < dataSeries1.size(); i++) {
				sortArray[i] = dataSeries1.get(i);
			}
			Arrays.sort(sortArray);
			Intent intent = new Intent(Constants.SELECT_OK);
			intent.putExtra("title", sortArray[7] + "");
			getActivity().sendBroadcast(intent);
			if (Select == 0) {
				chart_area.setTwoMi(false);
			} else if (Select == 1) {
				chart_area.setTwoMi(false);
			} else if (Select == 2) {
				chart_area.setTwoMi(true);
			}
			chart_area.setAxisMax((int) (sortArray[7] + 5));

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSeries1;
	}

	/**
	 * 周表
	 * 
	 * @param hostory
	 */
	private List<Double> initWeekData(String position, String date,
			AreaChart03View chart_area, int Select) {
		mDataset = new LinkedList<AreaData>();
		mLabels = new LinkedList<String>();
		List<Double> dataSeries1 = new LinkedList<Double>();
		String[] dates = date.split("-");
		DbUtils db = DbUtils.create(getActivity());
		if (position.equals("0")) {
			mLabels.add(getActivity().getString(R.string.one_week));
			mLabels.add(getActivity().getString(R.string.two_week));
			mLabels.add(getActivity().getString(R.string.three_week));
			mLabels.add(getActivity().getString(R.string.four_week));
			mLabels.add(getActivity().getString(R.string.five_week));
			mLabels.add(getActivity().getString(R.string.six_week));
			mLabels.add(getActivity().getString(R.string.seven_week));
			mLabels.add(getActivity().getString(R.string.eight_week));
			String date1 = dates[0] + "/" + "1";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "2";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "3";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "4";
			setWeekPoint(dataSeries1, db, date4, Select);
			String date5 = dates[0] + "/" + "5";
			setWeekPoint(dataSeries1, db, date5, Select);
			String date6 = dates[0] + "/" + "6";
			setWeekPoint(dataSeries1, db, date6, Select);
			String date7 = dates[0] + "/" + "7";
			setWeekPoint(dataSeries1, db, date7, Select);
			String date8 = dates[0] + "/" + "8";
			setWeekPoint(dataSeries1, db, date8, Select);
		} else if (position.equals("1")) {
			mLabels.add(getActivity().getString(R.string.nine_week));
			mLabels.add(getActivity().getString(R.string.ten_week));
			mLabels.add(getActivity().getString(R.string.eleven_week));
			mLabels.add(getActivity().getString(R.string.twelve_week));
			mLabels.add(getActivity().getString(R.string.thirteen_week));
			mLabels.add(getActivity().getString(R.string.fourteen_week));
			mLabels.add(getActivity().getString(R.string.fifteen_week));
			mLabels.add(getActivity().getString(R.string.sixteen_week));
			String date1 = dates[0] + "/" + "9";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "10";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "11";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "12";
			setWeekPoint(dataSeries1, db, date4, Select);
			String date5 = dates[0] + "/" + "13";
			setWeekPoint(dataSeries1, db, date5, Select);
			String date6 = dates[0] + "/" + "14";
			setWeekPoint(dataSeries1, db, date6, Select);
			String date7 = dates[0] + "/" + "15";
			setWeekPoint(dataSeries1, db, date7, Select);
			String date8 = dates[0] + "/" + "16";
			setWeekPoint(dataSeries1, db, date8, Select);
		} else if (position.equals("2")) {
			mLabels.add(getActivity().getString(R.string.seventeen_week));
			mLabels.add(getActivity().getString(R.string.eighteen_week));
			mLabels.add(getActivity().getString(R.string.nineteen_week));
			mLabels.add(getActivity().getString(R.string.twenty_week));
			mLabels.add(getActivity().getString(R.string.twenty_one_week));
			mLabels.add(getActivity().getString(R.string.twenty_two_week));
			mLabels.add(getActivity().getString(R.string.twenty_three_week));
			mLabels.add(getActivity().getString(R.string.twenty_four_week));
			String date1 = dates[0] + "/" + "17";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "18";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "19";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "20";
			setWeekPoint(dataSeries1, db, date4, Select);
			String date5 = dates[0] + "/" + "21";
			setWeekPoint(dataSeries1, db, date5, Select);
			String date6 = dates[0] + "/" + "22";
			setWeekPoint(dataSeries1, db, date6, Select);
			String date7 = dates[0] + "/" + "23";
			setWeekPoint(dataSeries1, db, date7, Select);
			String date8 = dates[0] + "/" + "24";
			setWeekPoint(dataSeries1, db, date8, Select);

		} else if (position.equals("3")) {
			mLabels.add(getActivity().getString(R.string.twenty_five_week));
			mLabels.add(getActivity().getString(R.string.twenty_six_week));
			mLabels.add(getActivity().getString(R.string.twenty_seven_week));
			mLabels.add(getActivity().getString(
					R.string.twenty_seven_eight_week));
			mLabels.add(getActivity()
					.getString(R.string.twenty_seven_nine_week));
			mLabels.add(getActivity().getString(R.string.thirty_week));
			mLabels.add(getActivity().getString(R.string.thirty_one_week));
			mLabels.add(getActivity().getString(R.string.thirty_two_week));
			String date1 = dates[0] + "/" + "25";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "26";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "27";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "28";
			setWeekPoint(dataSeries1, db, date4, Select);
			String date5 = dates[0] + "/" + "29";
			setWeekPoint(dataSeries1, db, date5, Select);
			String date6 = dates[0] + "/" + "30";
			setWeekPoint(dataSeries1, db, date6, Select);
			String date7 = dates[0] + "/" + "31";
			setWeekPoint(dataSeries1, db, date7, Select);
			String date8 = dates[0] + "/" + "32";
			setWeekPoint(dataSeries1, db, date8, Select);

		} else if (position.equals("4")) {
			mLabels.add(getActivity().getString(R.string.thirty_three_week));
			mLabels.add(getActivity().getString(R.string.thirty_four_week));
			mLabels.add(getActivity().getString(R.string.thirty_five_week));
			mLabels.add(getActivity().getString(R.string.thirty_six_week));
			mLabels.add(getActivity().getString(R.string.thirty_seven_week));
			mLabels.add(getActivity().getString(R.string.thirty_eight_week));
			mLabels.add(getActivity().getString(R.string.thirty_nine_week));
			mLabels.add(getActivity().getString(R.string.forty_week));
			String date1 = dates[0] + "/" + "33";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "34";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "35";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "36";
			setWeekPoint(dataSeries1, db, date4, Select);
			String date5 = dates[0] + "/" + "37";
			setWeekPoint(dataSeries1, db, date5, Select);
			String date6 = dates[0] + "/" + "38";
			setWeekPoint(dataSeries1, db, date6, Select);
			String date7 = dates[0] + "/" + "39";
			setWeekPoint(dataSeries1, db, date7, Select);
			String date8 = dates[0] + "/" + "40";
			setWeekPoint(dataSeries1, db, date8, Select);

		} else if (position.equals("5")) {
			mLabels.add(getActivity().getString(R.string.forty_one_week));
			mLabels.add(getActivity().getString(R.string.forty_two_week));
			mLabels.add(getActivity().getString(R.string.forty_three_week));
			mLabels.add(getActivity().getString(R.string.forty_four_week));
			mLabels.add(getActivity().getString(R.string.forty_five_week));
			mLabels.add(getActivity().getString(R.string.forty_six_week));
			mLabels.add(getActivity().getString(R.string.forty_seven_week));
			mLabels.add(getActivity().getString(R.string.forty_eight_wek));
			String date1 = dates[0] + "/" + "41";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "42";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "43";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "44";
			setWeekPoint(dataSeries1, db, date4, Select);
			String date5 = dates[0] + "/" + "45";
			setWeekPoint(dataSeries1, db, date5, Select);
			String date6 = dates[0] + "/" + "46";
			setWeekPoint(dataSeries1, db, date6, Select);
			String date7 = dates[0] + "/" + "47";
			setWeekPoint(dataSeries1, db, date7, Select);
			String date8 = dates[0] + "/" + "48";
			setWeekPoint(dataSeries1, db, date8, Select);

		} else if (position.equals("6")) {
			mLabels.add(getActivity().getString(R.string.forty_nine_week));
			mLabels.add(getActivity().getString(R.string.fifty_week));
			mLabels.add(getActivity().getString(R.string.fifty_one_week));
			mLabels.add(getActivity().getString(R.string.fifty_two_week));
			mLabels.add("");
			mLabels.add("");
			mLabels.add("");
			mLabels.add("");
			String date1 = dates[0] + "/" + "49";
			setWeekPoint(dataSeries1, db, date1, Select);
			String date2 = dates[0] + "/" + "50";
			setWeekPoint(dataSeries1, db, date2, Select);
			String date3 = dates[0] + "/" + "51";
			setWeekPoint(dataSeries1, db, date3, Select);
			String date4 = dates[0] + "/" + "52";
			setWeekPoint(dataSeries1, db, date4, Select);
			dataSeries1.add(Double.parseDouble(0 + ""));
			dataSeries1.add(Double.parseDouble(0 + ""));
			dataSeries1.add(Double.parseDouble(0 + ""));
			dataSeries1.add(Double.parseDouble(0 + ""));
		}
		Double[] sortArray = new Double[dataSeries1.size()];
		for (int i = 0; i < dataSeries1.size(); i++) {
			sortArray[i] = dataSeries1.get(i);
		}
		Arrays.sort(sortArray);
		if (Select == 0) {
			chart_area.setTwoMi(false);
		} else if (Select == 1) {
			chart_area.setTwoMi(false);
		} else if (Select == 2) {
			chart_area.setTwoMi(true);
		}
		Intent intent = new Intent(Constants.SELECT_OK);

		intent.putExtra("title", sortArray[7] + "");
		getActivity().sendBroadcast(intent);
		chart_area.setAxisMax((int) (sortArray[7] + 5));
		return dataSeries1;

	}

	/**
	 * 月表
	 * 
	 * @param hostory
	 */
	private List<Double> initMonthData(String position, String date,
			AreaChart03View chart_area, int select) {
		List<Double> dataSeries1 = new LinkedList<Double>();
		String[] dates = date.split("-");
		DbUtils db = DbUtils.create(getActivity());
		if (position.equals("0")) {
			mDataset = new LinkedList<AreaData>();
			mLabels = new LinkedList<String>();
			mLabels.add(getActivity().getString(R.string.january));
			mLabels.add(getActivity().getString(R.string.february));
			mLabels.add(getActivity().getString(R.string.march));
			mLabels.add(getActivity().getString(R.string.april));
			mLabels.add(getActivity().getString(R.string.may));
			mLabels.add(getActivity().getString(R.string.june));
			mLabels.add(getActivity().getString(R.string.july));
			mLabels.add(getActivity().getString(R.string.august));
			String date1 = dates[0] + "/" + "01";
			setMonthPoint(dataSeries1, db, date1, select);
			String date2 = dates[0] + "/" + "02";
			setMonthPoint(dataSeries1, db, date2, select);
			String date3 = dates[0] + "/" + "03";
			setMonthPoint(dataSeries1, db, date3, select);
			String date4 = dates[0] + "/" + "04";
			setMonthPoint(dataSeries1, db, date4, select);
			String date5 = dates[0] + "/" + "05";
			setMonthPoint(dataSeries1, db, date5, select);
			String date6 = dates[0] + "/" + "06";
			setMonthPoint(dataSeries1, db, date6, select);
			String date7 = dates[0] + "/" + "07";
			setMonthPoint(dataSeries1, db, date7, select);
			String date8 = dates[0] + "/" + "08";
			setMonthPoint(dataSeries1, db, date8, select);
		} else {
			mDataset = new LinkedList<AreaData>();
			mLabels = new LinkedList<String>();
			mLabels.add(getActivity().getString(R.string.september));
			mLabels.add(getActivity().getString(R.string.october));
			mLabels.add(getActivity().getString(R.string.november));
			mLabels.add(getActivity().getString(R.string.december));
			mLabels.add("");
			mLabels.add("");
			mLabels.add("");
			mLabels.add("");
			String date9 = dates[0] + "/" + "09";
			setMonthPoint(dataSeries1, db, date9, select);
			String date10 = dates[0] + "/" + "10";
			setMonthPoint(dataSeries1, db, date10, select);
			String date11 = dates[0] + "/" + "11";
			setMonthPoint(dataSeries1, db, date11, select);
			String date12 = dates[0] + "/" + "12";
			setMonthPoint(dataSeries1, db, date12, select);
			dataSeries1.add(Double.parseDouble(0 + ""));
			dataSeries1.add(Double.parseDouble(0 + ""));
			dataSeries1.add(Double.parseDouble(0 + ""));
			dataSeries1.add(Double.parseDouble(0 + ""));
		}

		Double[] sortArray = new Double[dataSeries1.size()];
		for (int i = 0; i < dataSeries1.size(); i++) {
			sortArray[i] = dataSeries1.get(i);
		}
		Arrays.sort(sortArray);
		if (select == 0) {
			chart_area.setTwoMi(false);
		} else if (select == 1) {
			chart_area.setTwoMi(false);
		} else if (select == 2) {
			chart_area.setTwoMi(true);
		}
		Intent intent = new Intent(Constants.SELECT_OK);
		intent.putExtra("title", sortArray[7] + "");
		getActivity().sendBroadcast(intent);
		chart_area.setAxisMax((int) (sortArray[7] + 5));
		return dataSeries1;

	}

	/**
	 * 每周表
	 * 
	 * @param hostory
	 */
	private List<Double> initEveryWeekData(String date,
			AreaChart03View chart_area, int Select) {
		List<Double> dataSeries1 = new LinkedList<Double>();
		String[] dates = date.split("-");
		DbUtils db = DbUtils.create(getActivity());
		mDataset = new LinkedList<AreaData>();
		mLabels = new LinkedList<String>();
		mLabels.add(getActivity().getString(R.string.sun));
		mLabels.add(getActivity().getString(R.string.mon));
		mLabels.add(getActivity().getString(R.string.tue));
		mLabels.add(getActivity().getString(R.string.wed));
		mLabels.add(getActivity().getString(R.string.thu));
		mLabels.add(getActivity().getString(R.string.fri));
		mLabels.add(getActivity().getString(R.string.sat));
		String hostory = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = sdf.parse(date);
			Calendar cal = Calendar.getInstance();

			cal.setTime(date1);
			System.out.println("今天的日期: " + cal.getTime());
			int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
			cal.add(Calendar.DATE, -day_of_week);
			hostory=sdf.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// cal.setTime(new Date(string));

		PedometerModel pedometer1 = null;
		try {
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", hostory)
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
					
				}

			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 1))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 2))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 3))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 4))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 5))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			pedometer1 = db.findFirst(Selector
					.from(PedometerModel.class)
					.where("datestring", "=", DateUtil.getDateStr(hostory, 6))
					.and(WhereBuilder.b("uuid", "=", MyApp.getIntance()
							.getAppSettings().LAST_CONNECT_MAC.getValue()
							.toString())));
			if (pedometer1 != null) {
				if (Select == 0) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalsteps() + ""));
				} else if (Select == 1) {
					dataSeries1.add(Double.parseDouble(pedometer1
							.getTotalcalories() + ""));
				} else if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance() + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(pedometer1
								.getTotaldistance()*0.621 + "") / 100d);
					}
				}
			} else {
				dataSeries1.add(Double.parseDouble(0 + ""));
			}
			
			Double[] sortArray = new Double[dataSeries1.size()];
			for (int i = 0; i < dataSeries1.size(); i++) {
				sortArray[i] = dataSeries1.get(i);
			}
			Arrays.sort(sortArray);
			Intent intent = new Intent(Constants.SELECT_OK);
			intent.putExtra("title", sortArray[6] + "");
			getActivity().sendBroadcast(intent);
			if (Select == 0) {
				chart_area.setTwoMi(false);
			} else if (Select == 1) {
				chart_area.setTwoMi(false);
			} else if (Select == 2) {
				chart_area.setTwoMi(true);
			}
			chart_area.setAxisMax((int) (sortArray[6] + 5));

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSeries1;

	}

	/**
	 * 从数据库中读取月表数据
	 * 
	 * @param dataSeries1
	 * @param db
	 * @param date1
	 */
	private void setMonthPoint(List<Double> dataSeries1, DbUtils db,
			String date1, int Select) {
		WhereBuilder builder = WhereBuilder.b("yearmonth", "==", date1).and(
				"uuid",
				"==",
				MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue()
						.toString());
		try {
			List<PedometerModel> pedometers = db.findAll(Selector.from(
					PedometerModel.class).where(builder));
			if (pedometers == null) {
				dataSeries1.add(Double.parseDouble(0 + ""));
			} else {
				double monthStep = 0;
				for (PedometerModel ped : pedometers) {
					if (Select == 0) {
						monthStep = monthStep + ped.getTotalsteps();
					} else if (Select == 1) {
						monthStep = monthStep + ped.getTotalcalories();
					} else {
						monthStep = monthStep + ped.getTotaldistance();
					}
				}
				if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(monthStep + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(monthStep*0.621 + "") / 100d);
						
					}
				} else {
					dataSeries1.add(Double.parseDouble(monthStep + ""));
				}

			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库中读取周表数据
	 * 
	 * @param dataSeries1
	 * @param db
	 * @param date1
	 */
	private void setWeekPoint(List<Double> dataSeries1, DbUtils db,
			String date1, int Select) {
		WhereBuilder builder = WhereBuilder.b("yearweek", "==", date1).and(
				"uuid",
				"==",
				MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue()
						.toString());
		try {
			List<PedometerModel> pedometers = db.findAll(Selector.from(
					PedometerModel.class).where(builder));
			if (pedometers == null) {
				dataSeries1.add(Double.parseDouble(0 + ""));
			} else {
				double monthStep = 0;
				for (PedometerModel ped : pedometers) {
					if (Select == 0) {
						monthStep = monthStep + ped.getTotalsteps();
					} else if (Select == 1) {
						monthStep = monthStep + ped.getTotalcalories();
					} else {
						monthStep = monthStep + ped.getTotaldistance();
					}

				}
				if (Select == 2) {
					if(share.getInt("metric", 0)==0){
						dataSeries1.add(Double.parseDouble(monthStep + "") / 100d);
					}else{
						DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						dataSeries1.add(Double.parseDouble(monthStep*0.621 + "") / 100d);
						
					}
					
				} else {
					dataSeries1.add(Double.parseDouble(monthStep + ""));
				}

			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public static HostoryChartFragment newInstance(String date, int state,
			String position, int Select) {
		HostoryChartFragment fragmentFirst = new HostoryChartFragment();
		Bundle args = new Bundle();
		args.putString("position", position);
		args.putString("hostory", date);
		args.putInt("state", state);
		args.putInt("Select", Select);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

}
