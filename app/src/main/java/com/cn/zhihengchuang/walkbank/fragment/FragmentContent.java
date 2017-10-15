package com.cn.zhihengchuang.walkbank.fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.AreaData;
import org.xclcharts.renderer.XEnum;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.appformwork.preference.MicroRecruitSettings;
import com.cn.zhihengchuang.walkbank.activity.CalendarActivity;
import com.cn.zhihengchuang.walkbank.activity.ExerciseFragmentActivity;
import com.cn.zhihengchuang.walkbank.activity.HorizontalScreenActivity;
import com.cn.zhihengchuang.walkbank.activity.MainActivityGroup;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.event.DataRefreshEvent;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.TimeUtil1;
import com.cn.zhihengchuang.walkbank.util.TimeUtils;
import com.cn.zhihengchuang.walkbank.view.AreaChart02View;
import com.cn.zhihengchuang.walkbank.view.TasksCompletedView;
import com.cn.zhihengchuang.walkbank.view.XScrollView;
import com.cn.zhihengchuang.walkbank.view.vertical.VerticalViewPager;
import com.cn.zhihengchuang.walkbank.view.vertical.VerticalViewPager.OnPageChangeListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lingb.global.Global;
import com.squareup.otto.Subscribe;

public class FragmentContent extends BaseFragment implements XScrollView.IXScrollViewListener {
	private final static String TAG = BleService.class.getSimpleName();
	private static String tvContentValue;
	private TextView tvContent;
	private ImageView horizontalView;
	private ImageView imgLogo;
	private TasksCompletedView task_view;
	private ImageView image_date_icon, today;
	private ImageView share_iv;
	private static TextView text_date;
	private TextView text_week;
	private XScrollView mScrollView;
	VerticalViewPager verticalViewPager;
	public List<View> viewPageList;
	private int total_carles = 0;
	private int total_distance = 0;
	private int total_steps = 0;
	private RadioButton carles, distance, steps;
	private TextView per, time;
	private LinkedList<AreaData> mDataset;
	private AreaChart02View chart_area;
	private LinkedList<String> mLabels;
	PedometerModel pedometer;
	private static final String KEY_DATE = "date";
	private static ExerciseFragmentActivity mActivity;
	private ViewPager mViewPager;
	private RadioGroup chartRG;
	private RadioGroup hostory_foot_rg;
	private static int state = 0;
	private static int select = 0;
	private boolean isFrist = true;
	private boolean isSyncing = false;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message arg0) {
			try {
				if (pedometer != null) {
					steps.setText(pedometer.getTotalsteps() + "");

					Log.i(TAG, "steps." + pedometer.getTotalsteps());
					String targetDistance = share.getString(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "target_distance", "10000");
					if (Integer.parseInt(targetDistance) != 0) {
						int perInt = (int) (pedometer.getTotalsteps() * 100 / Integer.parseInt(targetDistance));
						if (perInt >= 999) {
							perInt = 999;
						}
						per.setText(perInt + "%");
						task_view.setProgress((int) (pedometer.getTotalsteps() * 100 / Integer.parseInt(targetDistance)));
					}
					time.setText(pedometer.getTotalsteps() + "");
					carles.setText(pedometer.getTotalcalories() + "");
					if (share.getInt("metric", 0) == 0) {
						distance.setText(pedometer.getTotaldistance() / 100f + "km");
					} else {
						DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
						distance.setText(decimalFormat.format(pedometer.getTotaldistance() * 0.621 / 100f) + "mi");
					}
					MyPagerAdapter mAdapter = new MyPagerAdapter(getChildFragmentManager());
					mViewPager.setAdapter(mAdapter);
					DbUtils db = DbUtils.create(mActivity);
					if (state == 1) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Calendar cal = Calendar.getInstance();
						try {
							cal.setTime(sdf.parse(tvContentValue));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						mViewPager.setCurrentItem((cal.get(Calendar.WEEK_OF_YEAR) - 1) / 8);
					} else if (state == 0) {
						PedometerModel pedometer1 = null;
						try {
							pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
						} catch (DbException e) {
							e.printStackTrace();
						}
						if (pedometer1 != null) {
							mViewPager.setCurrentItem(TimeUtil1.daysBetween(pedometer1.getDatestring(), tvContentValue) - 1);
						} else {
							mViewPager.setCurrentItem(0);
						}
					}

					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							setViewStep();
						}
					}, 300);

				}
			} catch (Exception e) {
				Log.e("FragmentContent", "error:");
			}
			return false;
		}

	});
	private Handler mHandler;

	/**
	 * 画距离图
	 */
	/*
	 * private void setViewDistances(){ mDataset.clear(); mLabels.clear();
	 * SharedPreferences share = getActivity().getSharedPreferences(
	 * Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); MicroRecruitSettings
	 * settings = MyApp.getIntance() .getAppSettings(); String sportHalfTime =
	 * share.getString( pedometer.getDatestring() + "distances_half_time", "");
	 * String sportHalf = share.getString(pedometer.getDatestring() +
	 * "distances_half", ""); // String sportHalf =
	 * settings.SPORT_HALF.getValue().toString(); if
	 * (!TextUtils.isEmpty(sportHalfTime) && !TextUtils.isEmpty(sportHalf)) {
	 * String[] sportTime = sportHalfTime.split(","); String[] sport =
	 * sportHalf.split(",");
	 * 
	 * for (int i = 0; i < sportTime.length; i++) { if (i == 0) {
	 * mLabels.add("0h"); } else if (i == 6) { mLabels.add("3h"); } else if (i
	 * == 12) { mLabels.add("6h"); } else if (i == 18) { mLabels.add("9h"); }
	 * else if (i == 24) { mLabels.add("12h"); } else if (i == 30) {
	 * mLabels.add("15h"); } else if (i == 36) { mLabels.add("18h"); } else if
	 * (i == 42) { mLabels.add("21h"); } else if (i == 48) { mLabels.add("24h");
	 * } else { mLabels.add(""); } } List<Double> dataSeries1 = new
	 * LinkedList<Double>(); Double[] sortArray = new Double[sport.length]; for
	 * (int i = 0; i < sport.length; i++) {
	 * dataSeries1.add(Double.parseDouble(sport[i]));
	 * sortArray[i]=Double.parseDouble(sport[i]); } AreaData line1 = new
	 * AreaData("小熊", dataSeries1, Color.parseColor("#7CDF2E"), Color.WHITE,
	 * Color.parseColor("#DDF9C7")); // 不显示点
	 * line1.setDotStyle(XEnum.DotStyle.HIDE);// 隐藏图形 //
	 * line1.setDotStyle(XEnum.DotStyle.RECT); // line1.setLabelVisible(true);
	 * line1.setApplayGradient(true); line1.setAreaBeginColor(Color.WHITE);
	 * line1.setAreaEndColor(Color.parseColor("#DDF9C7")); mDataset.add(line1);
	 * Arrays.sort(sortArray); chart_area.setmDataset(mDataset);
	 * chart_area.setmLabels(mLabels); chart_area.setAxisMax((int)
	 * (sortArray[48] + 5)); chart_area.initView(); } }
	 */

	/**
	 * 画计步图
	 */
	private void setViewStep() {
		mDataset = new LinkedList<AreaData>();
		mLabels = new LinkedList<String>();
		SharedPreferences share = mActivity.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
		if (pedometer != null) {
			MicroRecruitSettings settings = MyApp.getIntance().getAppSettings();
			String sportHalfTime = share.getString(pedometer.getUuid() + pedometer.getDatestring() + "sport_half_time", "");
			String sportHalf = share.getString(pedometer.getUuid() + pedometer.getDatestring() + "sport_half", "");
			// String sportHalf = settings.SPORT_HALF.getValue().toString();
			Log.i("chart", "sprotHalfTime = " + sportHalfTime + "   sportHalf = " + sportHalf);
			if (!TextUtils.isEmpty(sportHalfTime) && !TextUtils.isEmpty(sportHalf)) {
				String[] sportTime = sportHalfTime.split(",");
				String[] sport = sportHalf.split(",");

				for (int i = 0; i < sportTime.length; i++) {
					if (i == 0) {
						mLabels.add("0h");
					} else if (i == 6) {
						mLabels.add("3h");
					} else if (i == 12) {
						mLabels.add("6h");
					} else if (i == 18) {
						mLabels.add("9h");
					} else if (i == 24) {
						mLabels.add("12h");
					} else if (i == 30) {
						mLabels.add("15h");
					} else if (i == 36) {
						mLabels.add("18h");
					} else if (i == 42) {
						mLabels.add("21h");
					} else if (i == 48) {
						mLabels.add("24h");
					} else {
						mLabels.add("");
					}
				}
				List<Double> dataSeries1 = new LinkedList<Double>();
				Double[] sortArray = new Double[sport.length];
				for (int i = 0; i < sport.length; i++) {
					dataSeries1.add(Double.parseDouble(sport[i]));
					sortArray[i] = Double.parseDouble(sport[i]);
				}
				AreaData line1 = new AreaData("小熊", dataSeries1, Color.parseColor("#7CDF2E"), Color.WHITE, Color.parseColor("#DDF9C7"));
				// 不显示点
				line1.setDotStyle(XEnum.DotStyle.HIDE);// 隐藏图形
				// line1.setDotStyle(XEnum.DotStyle.RECT);
				// line1.setLabelVisible(true);
				line1.setApplayGradient(true);
				line1.setAreaBeginColor(Color.WHITE);
				line1.setAreaEndColor(Color.parseColor("#DDF9C7"));
				mDataset.add(line1);
				Arrays.sort(sortArray);
				chart_area.setmDataset(mDataset);
				chart_area.setmLabels(mLabels);
				chart_area.setAxisMax((int) (sortArray[sortArray.length - 1] + 5));
				chart_area.initView();
			}

		}
	}

	/* *//**
	 * 画卡路里图
	 */
	/*
	 * private void setViewCalorie(){
	 * 
	 * 
	 * mDataset.clear(); mLabels.clear(); SharedPreferences share =
	 * getActivity().getSharedPreferences( Constants.SHARE_FILE_NAME,
	 * Activity.MODE_PRIVATE); MicroRecruitSettings settings =
	 * MyApp.getIntance() .getAppSettings(); String sportHalfTime =
	 * share.getString( pedometer.getDatestring() + "calorie_half_time", "");
	 * String sportHalf = share.getString(pedometer.getDatestring() +
	 * "calorie_half", ""); // String sportHalf =
	 * settings.SPORT_HALF.getValue().toString(); if
	 * (!TextUtils.isEmpty(sportHalfTime) && !TextUtils.isEmpty(sportHalf)) {
	 * String[] sportTime = sportHalfTime.split(","); String[] sport =
	 * sportHalf.split(",");
	 * 
	 * for (int i = 0; i < sportTime.length; i++) { if (i == 0) {
	 * mLabels.add("0h"); } else if (i == 6) { mLabels.add("3h"); } else if (i
	 * == 12) { mLabels.add("6h"); } else if (i == 18) { mLabels.add("9h"); }
	 * else if (i == 24) { mLabels.add("12h"); } else if (i == 30) {
	 * mLabels.add("15h"); } else if (i == 36) { mLabels.add("18h"); } else if
	 * (i == 42) { mLabels.add("21h"); } else if (i == 48) { mLabels.add("24h");
	 * } else { mLabels.add(""); } } List<Double> dataSeries1 = new
	 * LinkedList<Double>(); Double[] sortArray = new Double[sport.length]; for
	 * (int i = 0; i < sport.length; i++) {
	 * dataSeries1.add(Double.parseDouble(sport[i]));
	 * sortArray[i]=Double.parseDouble(sport[i]); } AreaData line1 = new
	 * AreaData("小熊", dataSeries1, Color.parseColor("#7CDF2E"), Color.WHITE,
	 * Color.parseColor("#DDF9C7")); // 不显示点
	 * line1.setDotStyle(XEnum.DotStyle.HIDE);// 隐藏图形 //
	 * line1.setDotStyle(XEnum.DotStyle.RECT); // line1.setLabelVisible(true);
	 * line1.setApplayGradient(true); line1.setAreaBeginColor(Color.WHITE);
	 * line1.setAreaEndColor(Color.parseColor("#DDF9C7")); mDataset.add(line1);
	 * Arrays.sort(sortArray); chart_area.setmDataset(mDataset);
	 * chart_area.setmLabels(mLabels); chart_area.setAxisMax((int)
	 * (sortArray[48] + 5)); chart_area.initView(); } }
	 */
	public static FragmentContent newInstance(long date) {
		FragmentContent fragmentFirst = new FragmentContent();
		Bundle args = new Bundle();
		args.putLong(KEY_DATE, date);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("test", "onCreate  **********");
		super.onCreate(savedInstanceState);
		final long millis = getArguments().getLong(KEY_DATE);
		int i = 0;
		if (millis > 0 && isFrist) {
			final Context context = getActivity();
			if (context != null) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				tvContentValue = df.format(new Date(millis));

				return;
			}
			isFrist = false;
		}

		tvContentValue = "";

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// scroll_exercise_fragment
		Log.i("test", "onCreateView  **********");
		View view = inflater.inflate(R.layout.fragment_step, container, false);
		isFrist = true;
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (ExerciseFragmentActivity) activity;
	}

	View cell_bottom;
	RadioButton radio_day;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initBroadcastReceiver();
		verticalViewPager = (VerticalViewPager) view.findViewById(R.id.verticalViewPager);
		registerBoradcastReceiver();
		share = getActivity().getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		viewPageList = new ArrayList<View>();
		View cell_top = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scroll_exercise, null);
		viewPageList.add(cell_top);

		cell_bottom = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_step_history, null);
		hostory_foot_rg = (RadioGroup) cell_bottom.findViewById(R.id.hostory_foot_rg);
		viewPageList.add(cell_bottom);
		initView(cell_top);
		mHandler = new Handler();
		mViewPager = (ViewPager) cell_bottom.findViewById(R.id.msviewPager);
		chartRG = (RadioGroup) cell_bottom.findViewById(R.id.chart_rg);
		radio_day = (RadioButton) cell_bottom.findViewById(R.id.day_rb);
		horizontalView = (ImageView) cell_bottom.findViewById(R.id.horizontalView);
		imgLogo = (ImageView) cell_top.findViewById(R.id.img_logo);
		horizontalView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				radio_day.setChecked(true);
				Intent intent = new Intent(getActivity(), HorizontalScreenActivity.class);
				intent.putExtra("tvContentValue", tvContentValue);
				intent.putExtra("state", state);
				startActivity(intent);
			}
		});
		chartRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.day_rb:
					state = 0;
					break;
				case R.id.day_week:
					state = 1;
					break;
				case R.id.day_month:
					state = 2;
					break;
				case R.id.day_every_week:
					state = 3;
					break;
				default:
					break;
				}
				MyPagerAdapter mAdapter = new MyPagerAdapter(getChildFragmentManager());
				mViewPager.setAdapter(mAdapter);
				DbUtils db = DbUtils.create(mActivity);
				if (state == 1) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal = Calendar.getInstance();
					try {
						cal.setTime(sdf.parse(tvContentValue));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					mViewPager.setCurrentItem((cal.get(Calendar.WEEK_OF_YEAR) - 1) / 8);
				} else if (state == 0) {
					PedometerModel pedometer1 = null;
					try {
						pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
					} catch (DbException e) {
						e.printStackTrace();
					}
					if (pedometer1 != null) {
						mViewPager.setCurrentItem(TimeUtil1.daysBetween(pedometer1.getDatestring(), tvContentValue) - 1);
					} else {
						mViewPager.setCurrentItem(0);
					}
				}
			}
		});
		hostory_foot_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.exercise_hostory_steps:
					select = 0;
					break;
				case R.id.exercise_hostory_carles:
					select = 1;
					break;
				case R.id.exercise_hostory_distance:
					select = 2;
					break;
				}
				MyPagerAdapter mAdapter = new MyPagerAdapter(getChildFragmentManager());
				mViewPager.setAdapter(mAdapter);
				DbUtils db = DbUtils.create(mActivity);
				if (state == 1) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal = Calendar.getInstance();
					try {
						cal.setTime(sdf.parse(tvContentValue));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					mViewPager.setCurrentItem((cal.get(Calendar.WEEK_OF_YEAR) - 1) / 8);
				} else if (state == 0) {
					PedometerModel pedometer1 = null;
					try {
						pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
					} catch (DbException e) {
						e.printStackTrace();
					}
					if (pedometer1 != null) {
						mViewPager.setCurrentItem(TimeUtil1.daysBetween(pedometer1.getDatestring(), tvContentValue) - 1);
					} else {
						mViewPager.setCurrentItem(0);
					}
				}
			}
		});
		DbUtils db = DbUtils.create(mActivity);
		PedometerModel pedometer1 = null;
		try {
			pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
		} catch (DbException e) {
			e.printStackTrace();
		}
		MyPagerAdapter mAdapter = new MyPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mAdapter);
		if (pedometer1 != null) {
			mViewPager.setCurrentItem(TimeUtil1.daysBetween(pedometer1.getDatestring(), tvContentValue) - 1);
		} else {
			mViewPager.setCurrentItem(0);
		}
		final long millis = getArguments().getLong(KEY_DATE);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		tvContentValue = df.format(new Date(millis));
		text_date.setText(tvContentValue.replace("-", "/"));
		setData(tvContentValue);
		verticalViewPager.setAdapter(new com.cn.zhihengchuang.walkbank.view.vertical.PagerAdapter() {
			@Override
			public int getCount() {
				return viewPageList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				super.destroyItem(container, position, object);

				container.removeView(viewPageList.get(position % viewPageList.size()));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				container.addView(viewPageList.get(position % viewPageList.size()));
				return viewPageList.get(position);
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}
		});
		verticalViewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mScrollView.requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_MOVE:
					mScrollView.requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_UP:
					mScrollView.requestDisallowInterceptTouchEvent(false);
					break;

				}
				return false;
			}
		});
		verticalViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == 1) {
					MyApp.getIntance().setmPosition(1);
					mActivity.image_date_icon.setVisibility(View.GONE);
					mActivity.today.setVisibility(View.GONE);
					mActivity.mViewPager.setScanScroll(false);
				} else {
					if (!MyApp.getIntance().isToday()) {
						mActivity.today.setVisibility(View.VISIBLE);
					}
					mActivity.image_date_icon.setVisibility(View.VISIBLE);
					MyApp.getIntance().setmPosition(0);
					mActivity.mViewPager.setScanScroll(true);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		verticalViewPager.setCurrentItem(0);

	}

	private  class MyPagerAdapter extends FragmentStatePagerAdapter {
		DbUtils db = DbUtils.create(mActivity);
		PedometerModel pedometer1;
		private Calendar cal;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			try {
				pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
			} catch (DbException e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getCount() {
			// pedometer1.setDatestring("2015-03-01");
			if (state == 0) {
				if (pedometer1 != null) {
					return TimeUtil1.daysBetween(pedometer1.getDatestring(), DateUtil.getCurrentDate());
				} else {
					return 1;
				}
			} else if (state == 1) {
				return 7;
			} else if (state == 3) {
				return 1;
			} else {
				return 2;
			}

		}

		@Override
		public Fragment getItem(int position) {
			/*
			 * long timeForPosition = TimeUtils.getDetayForPosition(position)
			 * .getTimeInMillis();
			 */
			if (state == 0) {

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String date = null;
				if (pedometer1 != null) {
					try {
						date = df.format(DateUtil.dateAddDay(df.parse(pedometer1.getDatestring()), position * 8));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					date = DateUtil.getCurrentDate();
				}
				return HostoryChartFragment.newInstance(date, state, "", select);
			} else {
				return HostoryChartFragment.newInstance(text_date.getText().toString().replace("/", "-"), state, "" + position, select);
			}

		}

		@Override
		public CharSequence getPageTitle(int position) {
			// Calendar cal = TimeUtils.getDayForPosition(position);
			return "";
		}

	}

	// ////////////////////订阅事件//////////////////////////////////
	@Subscribe
	public void onDataRefreshEvent(DataRefreshEvent event) {
		System.out.println("haole" + event.date);
		if (event.date.equals(tvContentValue)) {
			// setData();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mReceiver);
	}

	protected void initView(View view) {

		mScrollView = (XScrollView) view.findViewById(R.id.scroll_view);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(true);
		mScrollView.setIXScrollViewListener(this);
		View content = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_step_daily, null);

		task_view = (TasksCompletedView) content.findViewById(R.id.tasks_view);

		text_date = (TextView) content.findViewById(R.id.main_fragment_text_date);
		text_week = (TextView) content.findViewById(R.id.main_fragment_text_Week);
		chart_area = (AreaChart02View) content.findViewById(R.id.chart_area);
		// no_values = (TextView) content.findViewById(R.id.exercise_no_values);
		steps = (RadioButton) content.findViewById(R.id.exercise_total_steps);
		distance = (RadioButton) content.findViewById(R.id.exercise_total_distance);
		carles = (RadioButton) content.findViewById(R.id.exercise_total_carles);
		per = (TextView) content.findViewById(R.id.exercise_per);
		time = (TextView) content.findViewById(R.id.exercise_time);
		// g = (BarGraph) content.findViewById(R.id.bargraph);
		carles.setOnClickListener(new OnClickListenerImpl());
		distance.setOnClickListener(new OnClickListenerImpl());
		steps.setOnClickListener(new OnClickListenerImpl());

		mScrollView.setView(content);
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.UPDATE_OK);
		myIntentFilter.addAction(Constants.CANCEL_REFRESH);
		myIntentFilter.addAction(Constants.SYNCHRONOUS_FAILURE);
		myIntentFilter.addAction(BleService.ACTION_GATT_ERROR);
		myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		myIntentFilter.addAction(Constants.OLD_UPDATE_OK);
		myIntentFilter.addAction(Constants.CLEAR_AlL);
		// 注册广播
		getActivity().registerReceiver(mReceiver, myIntentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.UPDATE_OK.equals(action)) {
				String date = intent.getStringExtra("date");
				boolean isOld = intent.getBooleanExtra("isOld", false);
				boolean isRealTime = intent.getBooleanExtra("isRealTime", false);
				/*
				 * tvContentValue = text_date.getText().toString() .replace("/",
				 * "-");
				 */
				final long millis = getArguments().getLong(KEY_DATE);

				if (millis > 0) {

					if (context != null) {
						tvContentValue = TimeUtils.getFormattedDate(mActivity, millis);

					}
				}
				if (date.equals(tvContentValue)) {
					setData(tvContentValue);
				}
				mScrollView.stopRefresh();
				if (!isRealTime) {
					isSyncing = false;
				}

			} else if (Constants.CANCEL_REFRESH.equals(action)) {
				mScrollView.stopRefresh();
				isSyncing = false;
			} else if (Constants.OLD_UPDATE_OK.equals(action)) {
				isSyncing = false;
			} else if (Constants.SYNCHRONOUS_FAILURE.equals(action)) {
				mScrollView.stopRefresh();
				boolean isRealTime = share.getBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "open_real_time", false);
				if (!isRealTime) {
					isSyncing = false;
					Toast.makeText(getActivity(), getResources().getString(R.string.synchronization_failure), 1000).show();
				}

			} else if (BleService.ACTION_GATT_ERROR.equals(action)) {
				mScrollView.stopRefresh();
				isSyncing = false;
				boolean isRealTime = share.getBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "open_real_time", false);
				if (!isRealTime) {
					Toast.makeText(getActivity(), getResources().getString(R.string.synchronization_failure), 1000).show();
				}

			} else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Log.i("test", "recevice ACTION_GATT_DISCONNECTED");
				mScrollView.stopRefresh();
				isSyncing = false;

			} else if (Constants.CLEAR_AlL.equals(intent.getAction())) {
				setData(tvContentValue);
			}

		}
	};
	private SharedPreferences share;

	private void setData(final String date) {
		String str = DateUtil.getWeek(tvContentValue, getActivity());
		if (tvContentValue == null)
			tvContentValue = DateUtil.getCurrentDate();
		text_week.setText(str);
		Log.i(TAG, "laile.");
		/*
		 * if(tvContentValue.endsWith(DateUtil.getCurrentDate())){
		 * mActivity.today.setVisibility(View.GONE); }else{
		 * mActivity.today.setVisibility(View.VISIBLE); }
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				DbUtils db = DbUtils.create(getActivity());
				try {
					if (TextUtils.isEmpty(date)) {
						pedometer = db.findFirst(Selector.from(PedometerModel.class).where("datestring", "=", text_date.getText().toString().replace("/", "-")).and(WhereBuilder.b("uuid", "=", MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString())));
					} else {
						pedometer = db.findFirst(Selector.from(PedometerModel.class).where("datestring", "=", date).and(WhereBuilder.b("uuid", "=", MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString())));
					}

					/*
					 * MicroRecruitSettings
					 * settings=MyApp.getIntance().getAppSettings();
					 * settings.SPORT_HALF_TIME.getValue()
					 * settings.SPORT_HALF_TIME.setValue(sportTime);
					 * settings.SPORT_HALF.setValue(sportStep);
					 */
					if (pedometer != null) {
						handler.sendEmptyMessage(0);
					} else {

					}

				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		}).start();

		/*
		 * points = new ArrayList<Bar>(); for (int i = 0; i <= 24; i++) { Bar d
		 * = new Bar(); d.setColor(Color.parseColor("#278CC5")); if (i % 6 == 0)
		 * d.setName(i + ":00"); else d.setName(""); d.setValue(0);
		 * d.setSetvaluetextenble(Boolean.FALSE); points.add(d); }
		 */
		// g.setBars(points);
		/*
		 * if (values == 0) { no_values.setVisibility(View.VISIBLE); } else {
		 * no_values.setVisibility(View.GONE); }
		 */
		total_carles = 0;
		total_distance = 0;
		total_steps = 0;
		/*
		 * DatabaseHelper db = new DatabaseHelper(getActivity(),
		 * Constants.DATABASE_NAME, null, 1); Cursor cursor = db
		 * .select(Constants.DATABASE_DETAIL_TABLE_NAME, null, " datetime = ? ",
		 * new String[] { date }, null, null, null, null); cursor.close();
		 * Cursor cursor2 = db .select(Constants.DATABASE_TOTAL_TABLE_NAME,
		 * null, " datetime = ? ", new String[] { date }, null, null, null,
		 * null); while (cursor2.moveToNext()) { total_carles =
		 * cursor2.getInt(cursor2 .getColumnIndex("total_carles")); total_steps
		 * = cursor2.getInt(cursor2.getColumnIndex("total_steps"));
		 * total_distance = cursor2.getInt(cursor2
		 * .getColumnIndex("total_distance")); } cursor2.close(); db.close();
		 * carles.setText(total_carles + ""); distance.setText(total_distance +
		 * ""); steps.setText(total_steps + ""); task_view.setProgress((int)
		 * (total_steps / 100)); per.setText((int) (total_steps / 100) + "%");
		 * time.setText(total_steps + "");
		 */

	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_fragment_date:
				Intent intent = new Intent(getActivity(), CalendarActivity.class);
				getActivity().startActivityForResult(intent, 10);
				break;
			case R.id.exercise_total_carles:
				if (pedometer != null) {
					String targetDistance = share.getString(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "target_distance", "10000");
					int perint = (int) (pedometer.getTotalsteps() * 100 / Integer.parseInt(targetDistance));
					if (perint >= 999) {
						perint = 999;
					}
					per.setText(perint + "%");
					time.setText(pedometer.getTotalcalories() + "");
					imgLogo.setImageResource(R.drawable.calorie_logo);
					chart_area.initView();
				}

				break;
			case R.id.exercise_total_distance:
				if (pedometer != null) {
					String targetDistance = share.getString(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "target_distance", "10000");
					int perint = (int) (pedometer.getTotalsteps() * 100 / Integer.parseInt(targetDistance));
					if (perint >= 999) {
						perint = 999;
					}
					per.setText(perint + "%");
					if (share.getInt("metric", 0) == 0) {
						time.setText(pedometer.getTotaldistance() / 100f + "");
					} else {
						DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
						time.setText(decimalFormat.format(pedometer.getTotaldistance() * 0.621 / 100f));
					}

					imgLogo.setImageResource(R.drawable.location_logo);
					chart_area.initView();
				}
				break;
			case R.id.exercise_total_steps:
				if (pedometer != null) {
					String targetDistance = share.getString(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + "target_distance", "10000");
					int perint = (int) (pedometer.getTotalsteps() * 100 / Integer.parseInt(targetDistance));
					if (perint >= 999) {
						perint = 999;
					}
					per.setText(perint + "%");
					time.setText(pedometer.getTotalsteps() + "");
					imgLogo.setImageResource(R.drawable.foot_logo);

					setViewStep();
				}
				break;
			/*
			 * case R.id.share_iv: // showShare(); break;
			 */
			}
		}
	}

	@Override
	public void onRefresh() {
		/*
		 * if(share.getBoolean(MyApp.getIntance()
		 * .getAppSettings().LAST_CONNECT_MAC.getValue()
		 * .toString()+"open_real_time", false)){ return;
		 * 
		 * };
		 */
		if (MyApp.getIntance().mService != null && !TextUtils.isEmpty(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString())) {
			share = getActivity().getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
			share.edit().putBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + DateUtil.getCurrentDate() + "istongbu", false).commit();

			if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
				if (!isSyncing) {
					Log.i("Test", "************** start sync = " + isSyncing);
					MyApp.getIntance().mService.mCommand = BleService.COMMAND_SYNC_SAVE;
					MyApp.getIntance().mService.SendTimeCommand();
					isSyncing = true;
				}
				/*
				 * String[] date2 = DateUtil.getCurrentDate().split("-"); byte[]
				 * time = new byte[] { (byte) 0xbe, 0x02, 0x01, (byte) 0xfe,
				 * (byte) (Integer.parseInt(date2[0]) >> 8), (byte)
				 * Integer.parseInt(date2[0]), (byte)
				 * Integer.parseInt(date2[1]), (byte)
				 * Integer.parseInt(date2[2]), 0x00 };
				 * MyApp.getIntance().mService.writeData(time,
				 * BleService.MAIN_SERVICE, BleService.SEND_DATA_CHAR);
				 */
			} else {
				// MyApp.getIntance().mService.mCommand =
				// MyApp.getIntance().mService.COMMAND_SYNC_SAVE;
				// MyApp.getIntance().mService.connect(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString());
				mScrollView.stopRefresh();
				Toast.makeText(getActivity(), getString(R.string.please_connect), Toast.LENGTH_SHORT).show();
			}
			;

		} else {
			Toast.makeText(getActivity(), getString(R.string.please_bind), Toast.LENGTH_SHORT).show();
			mScrollView.stopRefresh();
		}
	}

	@Override
	public void onLoadMore() {

	} 
	
	@Override
	public void onResume() {
		Log.i("test", "fragment content onresume");
		verticalViewPager.setCurrentItem(0);
		if (!MyApp.getIntance().isToday()) {
			mActivity.today.setVisibility(View.VISIBLE);
		}
		mActivity.image_date_icon.setVisibility(View.VISIBLE);
		MyApp.getIntance().setmPosition(0);
		mActivity.mViewPager.setScanScroll(true);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(myBLEBroadcastReceiver);
		super.onDestroy();

	}

	/**
	 * my BLE BroadcastReceiver
	 */
	private BroadcastReceiver myBLEBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Global.ACTION_VIEWPAGER_STOP)) {
				// if (viewPageList.size() < 2) {
				// viewPageList.add(cell_bottom);
				// }
			}
		}
	};

	/**
	 * 初始化 BroadcastReceiver
	 */
	private void initBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Global.ACTION_VIEWPAGER_STOP);

		getActivity().registerReceiver(myBLEBroadcastReceiver, intentFilter);

	}

}