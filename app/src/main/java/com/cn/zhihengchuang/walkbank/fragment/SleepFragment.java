package com.cn.zhihengchuang.walkbank.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.BarData;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.Bar;
import com.cn.zhihengchuang.walkbank.interfaces.FragmentCallBackListener;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.TimeUtils;
import com.cn.zhihengchuang.walkbank.view.BarChart03View;
import com.cn.zhihengchuang.walkbank.view.TasksCompletedView;
import com.cn.zhihengchuang.walkbank.view.XScrollView;

public class SleepFragment extends Fragment implements XScrollView.IXScrollViewListener {
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
	private TextView exercise_time;
	private BarChart03View chart;
	private ArrayList<Bar> points;
	private ImageView share_iv;
	private final static String TAG = BleService.class.getSimpleName();
	private int i = 0;
	private String tvContentValue;
	private XScrollView mScrollView;
	private SharedPreferences share;
	private static final String KEY_DATE = "date";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final long millis = getArguments().getLong(KEY_DATE);
		if (millis > 0) {
			final Context context = getActivity();
			if (context != null) {
				tvContentValue = TimeUtils.getFormattedDate(context, millis);
				return;
			}
		}

		tvContentValue = "";

	}

	public static SleepFragment newInstance(long date) {
		SleepFragment fragmentFirst = new SleepFragment();
		Bundle args = new Bundle();
		args.putLong(KEY_DATE, date);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

	/**
	 * 画计步图
	 */
	private void setViewStep(boolean isFirst) {
		SharedPreferences share = getActivity().getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
		String timeJia = "";
		for (int i = 0; i < 288; i++) {
			if (i == 287) {
				timeJia = timeJia + i + "";
			} else {
				timeJia = timeJia + i + ",";
			}
		}
		String sleepJia = "";
		for (int i = 0; i < 288; i++) {
			if (i < 97) {
				sleepJia = sleepJia + 128 + ",";
			} else if (i > 270) {
				if (i == 287) {
					sleepJia = sleepJia + 128 + "";
				} else {
					sleepJia = sleepJia + 128 + ",";
				}

			} else if (i > 152 && i < 168) {
				sleepJia = sleepJia + 129 + ",";
			} else {
				sleepJia = sleepJia + 130 + ",";
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		/**
		 * 假数据
		 */
		/*
		 * share.edit().putString(MyApp.getIntance() .getAppSettings().LAST_CONNECT_MAC.getValue().toString() + DateUtil.getCurrentDate() + "sleep_half_time", timeJia).commit(); try { share.edit().putString(MyApp.getIntance() .getAppSettings().LAST_CONNECT_MAC.getValue().toString() +
		 * df.format(DateUtil.dateAddDay(df.parse(DateUtil.getCurrentDate()), -1)) + "sleep_half_time", timeJia).commit(); } catch (ParseException e1) { // TODO Auto-generated catch block e1.printStackTrace(); } share.edit().putString(MyApp.getIntance() .getAppSettings().LAST_CONNECT_MAC.getValue().toString() +
		 * DateUtil.getCurrentDate() + "sleep_half", sleepJia).commit(); try { share.edit().putString(MyApp.getIntance() .getAppSettings().LAST_CONNECT_MAC.getValue().toString() + df.format(DateUtil.dateAddDay(df.parse(DateUtil.getCurrentDate()), -1)) + "sleep_half", sleepJia).commit(); } catch (ParseException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */
		String sleepHalfTime = share.getString(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + tvContentValue + "sleep_half_time",
				"");
		String sleepHalf = share.getString(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + tvContentValue + "sleep_half", "");
		// 昨天睡眠时间
		String sleepLastHalf = "";
		String sleepLastHalfTime = "";
		List<Integer> dataColorA = new LinkedList<Integer>();

		try {
			sleepLastHalfTime = share.getString(
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + df.format(DateUtil.dateAddDay(df.parse(tvContentValue), -1))
							+ "sleep_half_time", "");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 昨天睡眠时间
		try {
			sleepLastHalf = share.getString(
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + df.format(DateUtil.dateAddDay(df.parse(tvContentValue), -1))
							+ "sleep_half", "");
			Log.i(TAG, "sleepLastHalf" + sleepLastHalf);
			Log.i(TAG, "DateUtil.dateAddDay(df.parse(tvContentValue), -1)" + df.format(DateUtil.dateAddDay(df.parse(tvContentValue), -1)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = "";
		String time = "";
		int start = 0;
		int end = 0;
		if (!TextUtils.isEmpty(sleepHalf)) {
			String[] sleepHalfs = sleepHalf.split(",");
			if (sleepHalfs.length < 288) {
				for (int h = sleepHalfs.length; h < 288; h++) {
					sleepHalf = sleepHalf + ",0";
				}
			}
			// sleepHalfs = sleepHalf.split(",");
			/*
			 * for (int s = 0; s < 288 ; s++) { if (s == sleepHalfs.length-1) { content = content + sleepHalfs[s].replace("0", "131") + ""; } else { if(s==0){ content = content + sleepHalfs[s].replace("0", "131") + ","; }else{ content = content + sleepHalfs[s].replace("0", "131") + ","; } } }
			 */

		}
		for (int s = 0; s < 288; s++) {
			if (s == 287) {
				time = time + s + "";
			} else {
				time = time + s + ",";
			}
		}
		List<BarData> chartData = new LinkedList<BarData>();
		List<String> chartLabels = new LinkedList<String>();
		List<Double> dataSeriesA = new LinkedList<Double>();
		int totalMinite = 0;
		String[] sleepData = sleepHalf.split(",");
		for (int i = 0; i < sleepData.length; i++) {
			if (!TextUtils.isEmpty(sleepData[i])) {
				if (sleepData[i].equals("0")) {
					dataSeriesA.add(Double.parseDouble("0"));
					dataColorA.add(Color.parseColor("#D9EDC9"));
				} else if (sleepData[i].equals("130")) {
					totalMinite = totalMinite + 5;
					dataSeriesA.add(Double.parseDouble("90"));
					dataColorA.add(Color.parseColor("#87CD51"));
				} else if (sleepData[i].equals("129")) {
					totalMinite = totalMinite + 5;
					dataSeriesA.add(Double.parseDouble("100"));
					dataColorA.add(Color.parseColor("#9ED673"));
				} else if (sleepData[i].equals("128")) {
					totalMinite = totalMinite + 5;
					dataSeriesA.add(Double.parseDouble("130"));
					dataColorA.add(Color.parseColor("#ACDC88"));
				} else {
					totalMinite = totalMinite + 5;
					dataSeriesA.add(Double.parseDouble("60"));
					dataColorA.add(Color.parseColor("#87CD51"));
				}
				if (i == 0) {
					chartLabels.add("0");
				} else if (i == 23) {
					chartLabels.add("2");

				} else if (i == 47) {
					chartLabels.add("4");

				} else if (i == 71) {
					chartLabels.add("6");
				} else if (i == 95) {
					chartLabels.add("8");
				} else if (i == 119) {
					chartLabels.add("10");
				} else if (i == 143) {
					chartLabels.add("12");
				} else if (i == 167) {
					chartLabels.add("14");
				} else if (i == 191) {
					chartLabels.add("16");
				} else if (i == 215) {
					chartLabels.add("18");
				} else if (i == 239) {
					chartLabels.add("20");
				} else if (i == 263) {
					chartLabels.add("22");
				} else if (i == 287) {
					chartLabels.add("24");
				} else {
					chartLabels.add("");
				}

			}

		}
		task_view.setProgress(totalMinite * 100 / 480);
		int sleepTotalHour = totalMinite / 60;
		int sleepTotalMinite = totalMinite % 60;
		exercise_time.setText(sleepTotalHour + ":" + String.format("%02d", sleepTotalMinite));
		if (!TextUtils.isEmpty(time)) {
			String[] timeLabel = time.split(",");
			for (int i = 0; i < timeLabel.length; i++) {
				if (!TextUtils.isEmpty(timeLabel[i])) {

				}

			}
		}

		// 依数据值确定对应的柱形颜色.

		/*
		 * dataColorA.add(Color.parseColor("#D9EDC9")); dataColorA.add(Color.parseColor("#D9EDC9")); dataColorA.add(Color.parseColor("#D9EDC9")); dataColorA.add(Color.parseColor("#007CC2")); dataColorA.add(Color.parseColor("#007CC2")); dataColorA.add(Color.parseColor("#007CC2"));
		 * dataColorA.add(Color.parseColor("#007CC2")); dataColorA.add(Color.parseColor("#007CC2")); dataColorA.add(Color.parseColor("#007CC2")); dataColorA.add(Color.parseColor("#76C3ED")); dataColorA.add(Color.parseColor("#76C3ED")); dataColorA.add(Color.parseColor("#B3DECB"));
		 * dataColorA.add(Color.parseColor("#B3DECB")); dataColorA.add(Color.parseColor("#B3DECB")); dataColorA.add(Color.parseColor("#CD8136")); dataColorA.add(Color.parseColor("#74C5F0")); dataColorA.add(Color.parseColor("#74C5F0")); dataColorA.add(Color.parseColor("#74C5F0"));
		 * dataColorA.add(Color.parseColor("#007CC4")); dataColorA.add(Color.parseColor("#007CC4")); dataColorA.add(Color.parseColor("#007CC4")); dataColorA.add(Color.parseColor("#007CC4")); dataColorA.add(Color.parseColor("#E67817")); dataColorA.add(Color.parseColor("#E67817"));
		 * dataColorA.add(Color.parseColor("#E67817")); dataColorA.add(Color.parseColor("#B7DDC8")); dataColorA.add(Color.parseColor("#79C4EC")); dataColorA.add(Color.parseColor("#79C4EC"));
		 */

		// BarData BarDataA = new
		// BarData("",dataSeriesA,dataColorA,(int)Color.rgb(53, 169, 239));
		chartData.clear();
		chartData.add(new BarData("", dataSeriesA, dataColorA, Color.rgb(53, 169, 239)));
		chart.setChartData(chartData);
		chart.setChartLabels(chartLabels);
		chart.initView();
		if (!isFirst) {
			chart.invalidate();
		}
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.UPDATE_OK);
		myIntentFilter.addAction(Constants.CANCEL_REFRESH);
		myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		myIntentFilter.addAction(Constants.SYNCHRONOUS_FAILURE);
		// 注册广播
		getActivity().registerReceiver(mReceiver, myIntentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.UPDATE_OK.equals(action)) {
				String date = intent.getStringExtra("date");
				boolean isRealTime = intent.getBooleanExtra("isRealTime", false);
				/*
				 * tvContentValue = text_date.getText().toString() .replace("/", "-");
				 */
				final long millis = getArguments().getLong(KEY_DATE);

				if (millis > 0) {

					if (context != null) {
						tvContentValue = TimeUtils.getFormattedDate(getActivity(), millis);

					}
				}
				if (date.equals(tvContentValue) && !isRealTime) {
					if (mScrollView != null) {
						mScrollView.postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									setData();
									setViewStep(false);
								} catch (Exception e) {
									Log.e("SleepFragment", "onReceive error:", e);
								}
							}
						}, 250);
					}
				}
				mScrollView.stopRefresh();
//				if (!isRealTime) {
//					Toast.makeText(context, getResources().getString(R.string.successful_synchronization), 1000).show();
//				}
			} else if (Constants.CANCEL_REFRESH.equals(action)) {
				mScrollView.stopRefresh();
			} else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mScrollView.stopRefresh();
			} else if (Constants.SYNCHRONOUS_FAILURE.equals(action)) {
				mScrollView.stopRefresh();
				Toast.makeText(getActivity(), getResources().getString(R.string.synchronization_failure), 1000).show();
			}

		}
	};

	private void setData() {
		String str = DateUtil.getWeek(tvContentValue, getActivity());
		if (tvContentValue == null)
			tvContentValue = DateUtil.getCurrentDate();
		text_Week.setText(str);
		text_date.setText(tvContentValue.replace("-", "/"));
		// for(int i=0;i<50;i++){

		// }

		points = new ArrayList<Bar>();
		for (int i = 0; i < 10; i++) {
			Bar d = new Bar();
			if (i == 0) {
				d.setName("22:00");
			} else if ((i + 1) == 10) {
				d.setName("7:00");
			} else {
				d.setName("");
			}
			if (i == 2 || i == 3 || i == 6) {
				d.setValue(3000);
				d.setColor(Color.parseColor("#fde575"));
				values++;
			} else if (i == 5 || i == 7 || i == 4) {// 深度
				d.setValue(2100);
				d.setColor(Color.parseColor("#128bf1"));
			} else if (i == 0 || i == 1 || i == 8 || i == 9) {
				d.setValue(2700);
				d.setColor(Color.parseColor("#79e4ff"));
				values++;
			}
			d.setSetvaluetextenble(Boolean.FALSE);
			points.add(d);
		}
		// chart.setBars(points);
		if (values == 0) {
			no_values.setVisibility(View.VISIBLE);
		} else {
			no_values.setVisibility(View.GONE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_scroll_exercise, container, false);
		initView(view);
		/*
		 * today.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { callback.callBack(); } }); share_iv.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated method stub showShare(); } });
		 */

		return view;
	}

	protected void initView(View view) {

		mScrollView = (XScrollView) view.findViewById(R.id.scroll_view);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(true);
		mScrollView.setIXScrollViewListener(this);
		View content = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sleep, null);
		task_view = (TasksCompletedView) content.findViewById(R.id.tasks_view);
		image_date_icon = (ImageView) content.findViewById(R.id.main_fragment_date);
		text_date = (TextView) content.findViewById(R.id.main_fragment_text_date);
		text_Week = (TextView) content.findViewById(R.id.main_fragment_text_Week);
		no_values = (TextView) content.findViewById(R.id.exercise_no_values);
		chart = (BarChart03View) content.findViewById(R.id.bargraph);
		exercise_time = (TextView) content.findViewById(R.id.exercise_time);

		mScrollView.setView(content);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		registerBoradcastReceiver();
		setData();
		setViewStep(true);

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getActivity().unregisterReceiver(mReceiver);
	}

	/*
	 * private void showShare() { ShareSDK.initSDK(getActivity()); OnekeyShare oks = new OnekeyShare(); // 关闭sso授权 oks.disableSSOWhenAuthorize();
	 * 
	 * // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法 // oks.setNotification(R.drawable.ic_launcher, // getString(R.string.app_name)); // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用 oks.setTitle(getString(R.string.share)); // titleUrl是标题的网络链接，仅在人人网和QQ空间使用 oks.setTitleUrl("http://sharesdk.cn"); // text是分享文本，所有平台都需要这个字段
	 * oks.setText("我是分享文本"); // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数 // oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");//确保SDcard下面存在此张图片 // url仅在微信（包括好友和朋友圈）中使用 oks.setUrl("http://sharesdk.cn"); // comment是我对这条分享的评论，仅在人人网和QQ空间使用 oks.setComment("我是测试评论文本"); // site是分享此内容的网站名称，仅在QQ空间使用
	 * oks.setSite(getString(R.string.app_name)); // siteUrl是分享此内容的网站地址，仅在QQ空间使用 oks.setSiteUrl("http://sharesdk.cn");
	 * 
	 * // 启动分享GUI oks.show(getActivity()); }
	 */

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (MyApp.getIntance().mService != null && !TextUtils.isEmpty(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString())) {
			share = getActivity().getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
			share.edit().putBoolean(MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.getValue().toString() + DateUtil.getCurrentDate() + "istongbu", false)
					.commit();
			if (MyApp.getIntance().mService.mConnectionState == MyApp.getIntance().mService.STATE_CONNECTED) {
				MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_SYNC_SAVE;
				MyApp.getIntance().mService.SendTimeCommand();
				/*
				 * String[] date2 = DateUtil.getCurrentDate().split("-"); byte[] time = new byte[] { (byte) 0xbe, 0x02, 0x01, (byte) 0xfe, (byte) (Integer.parseInt(date2[0]) >> 8), (byte) Integer.parseInt(date2[0]), (byte) Integer.parseInt(date2[1]), (byte) Integer.parseInt(date2[2]), 0x00 };
				 * MyApp.getIntance().mService.writeData(time, BleService.MAIN_SERVICE, BleService.SEND_DATA_CHAR);
				 */
			} else {
				// MyApp.getIntance().mService.mCommand = MyApp.getIntance().mService.COMMAND_SYNC_SAVE;
				// MyApp.getIntance().mService.connect(MyApp.getIntance()
				// .getAppSettings().LAST_CONNECT_MAC.getValue()
				// .toString());
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
		// TODO Auto-generated method stub

	}

}