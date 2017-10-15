package com.cn.zhihengchuang.walkbank.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cn.zhihengchuang.walkbank.activity.CalendarActivity;
import com.cn.zhihengchuang.walkbank.activity.ExerciseFragmentActivity;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.entity.Bar;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.interfaces.FragmentCallBackListener;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.TimeUtils;
import com.cn.zhihengchuang.walkbank.view.TasksCompletedView;
import com.cn.zhihengchuang.walkbank.view.XScrollView;
import com.cn.zhihengchuang.walkbank.view.vertical.VerticalViewPager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class ExerciseFragment extends Fragment implements
		 XScrollView.IXScrollViewListener {
	private TasksCompletedView task_view;
	private ImageView image_date_icon, today;
	private ImageView share_iv;
	private TextView text_date;
	private TextView text_week;
	private String date;
	private String str;
	private ExerciseFragmentActivity context;
	private FragmentCallBackListener callback;
	private int values;
	// private TextView no_values;
	// private BarGraph g;
	private ArrayList<Bar> points;
	private int total_carles = 0;
	private int total_distance = 0;
	private int total_steps = 0;
	private RadioButton carles, distance, steps;
	private TextView per, time;
	private Handler mHandler;
	private XScrollView mScrollView;
	VerticalViewPager verticalViewPager;
	public static List<View> viewPageList;
	public static String tvContentValue;
	private TextView tvContent;
	private static final String KEY_DATE = "date";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = (ExerciseFragmentActivity) getActivity();
		final long millis = getArguments().getLong(KEY_DATE);
		
		if (millis > 0) {
			final Context context = getActivity();
			if (context != null) {
				tvContentValue = TimeUtils.getFormattedDate(context, millis);
				return;
			}
		}

		tvContentValue = "";
		registerBoradcastReceiver();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	// ////////////////////订阅事件//////////////////////////////////
	/*
	 * @Subscribe public void onDataRefreshEvent(DataRefreshEvent event) { //
	 * this.myResume=event.myresume; setData(); mScrollView.stopRefresh();
	 * //requestResumeList(); }
	 */

	public static ExerciseFragment newInstance(long date) {
		ExerciseFragment fragmentFirst = new ExerciseFragment();
		Bundle args = new Bundle();
		args.putLong(KEY_DATE, date);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	}
	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.UPDATE_OK);
		myIntentFilter.addAction(BleService.ACTION_GATT_ERROR);
		// 注册广播
		getActivity().registerReceiver(mReceiver, myIntentFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.UPDATE_OK.equals(action)) {
				String date=intent.getStringExtra("date");
				if(date.equals(tvContentValue)){
					setData();
				}
			}else if(BleService.ACTION_GATT_ERROR.equals(action)){
				
			}
			
		}
	};
	private void setData() {
		str = DateUtil.getWeek(tvContentValue, context);
		if (tvContentValue == null)
			tvContentValue = DateUtil.getCurrentDate();
		text_week.setText(str);
		DbUtils db = DbUtils.create(getActivity());
		try {
			PedometerModel pedometer = db.findFirst(Selector.from(PedometerModel.class).where("datestring", "=", tvContentValue));
			steps.setText(pedometer.getTotalsteps()+ "");
			task_view.setProgress((int)(pedometer.getTotalsteps()/ 100)); 
			per.setText((int) (pedometer.getTotalsteps() / 100) + "%");
			time.setText(pedometer.getTotalsteps()+ "");
			carles.setText(pedometer.getTotalcalories()+ "");
			distance.setText(pedometer.getTotaldistance() +"");
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		text_date.setText(tvContentValue.replace("-", "/"));
		
		/*points = new ArrayList<Bar>();
		for (int i = 0; i <= 24; i++) {
			Bar d = new Bar();
			d.setColor(Color.parseColor("#278CC5"));
			if (i % 6 == 0)
				d.setName(i + ":00");
			else
				d.setName("");
			d.setValue(0);
			d.setSetvaluetextenble(Boolean.FALSE);
			points.add(d);
		}*/
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {// scroll_exercise_fragment
		View view = inflater.inflate(R.layout.fragment_scroll_exercise,
				container, false);

		initView(view);
		setData();
		return view;
	}

	private class OnClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_fragment_date:
				Intent intent = new Intent(context, CalendarActivity.class);
				getActivity().startActivityForResult(intent, 10);
				break;
			case R.id.main_fragment_today_date:
				callback.callBack();
				break;
			case R.id.exercise_total_carles:
				per.setText((int) (total_steps / 100) + "%");
				time.setText(total_carles + "");
				break;
			case R.id.exercise_total_distance:
				per.setText((int) (total_steps / 100) + "%");
				time.setText(total_distance + "");
				break;
			case R.id.exercise_total_steps:
				per.setText((int) (total_steps / 100) + "%");
				time.setText(total_steps + "");
				break;
			/*case R.id.share_iv:
				//showShare();
				break;*/
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = ((FragmentCallBackListener) activity);
	}

	protected void initView(View view) {
		mHandler = new Handler();

		mScrollView = (XScrollView) view.findViewById(R.id.scroll_view);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(true);
		mScrollView.setIXScrollViewListener(this);

		View content = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_step_daily, null);
		task_view = (TasksCompletedView) content.findViewById(R.id.tasks_view);
		image_date_icon = (ImageView) content
				.findViewById(R.id.main_fragment_date);
		today = (ImageView) content.findViewById(R.id.main_fragment_today_date);
		//share_iv = (ImageView) content.findViewById(R.id.share_iv);
		text_date = (TextView) content
				.findViewById(R.id.main_fragment_text_date);
		text_week = (TextView) content
				.findViewById(R.id.main_fragment_text_Week);
		// no_values = (TextView) content.findViewById(R.id.exercise_no_values);
		steps = (RadioButton) content.findViewById(R.id.exercise_total_steps);
		distance = (RadioButton) content
				.findViewById(R.id.exercise_total_distance);
		carles = (RadioButton) content.findViewById(R.id.exercise_total_carles);
		per = (TextView) content.findViewById(R.id.exercise_per);
		time = (TextView) content.findViewById(R.id.exercise_time);
		// g = (BarGraph) content.findViewById(R.id.bargraph);
		today.setOnClickListener(new OnClickListenerImpl());
		image_date_icon.setOnClickListener(new OnClickListenerImpl());
		carles.setOnClickListener(new OnClickListenerImpl());
		distance.setOnClickListener(new OnClickListenerImpl());
		steps.setOnClickListener(new OnClickListenerImpl());
		share_iv.setOnClickListener(new OnClickListenerImpl());
		mScrollView.setView(content);
	}

	private String getTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
				.format(new Date());
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

}