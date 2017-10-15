package com.cn.zhihengchuang.walkbank.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.fragment.HostoryChartFragment;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.TimeUtil1;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class HorizontalScreenActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private RadioGroup chartRG;
	private static int state = 0;
	private static String tvContentValue = "";
	private ImageView ivBack;
	private ImageView horizontalView;
	private RadioGroup hostory_foot_rg;
	private static int select;
	static DbUtils db;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_step_history);
		db = DbUtils.create(HorizontalScreenActivity.this);
		mViewPager = (ViewPager) findViewById(R.id.msviewPager);
		chartRG = (RadioGroup) findViewById(R.id.chart_rg);
		tvContentValue = getIntent().getStringExtra("tvContentValue");
		state = getIntent().getExtras().getInt("state");
		
		Log.i("test", "state = " + state + ",   content = " + getIntent().getStringExtra("tvContentValue"));
		ivBack = (ImageView) findViewById(R.id.ivBack);
		horizontalView = (ImageView) findViewById(R.id.horizontalView);
		hostory_foot_rg = (RadioGroup) findViewById(R.id.hostory_foot_rg);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		horizontalView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		chartRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
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
				Log.i("test", "onclick state = " + state);
				MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
				mViewPager.setAdapter(mAdapter);
				DbUtils db = DbUtils.create(HorizontalScreenActivity.this);
				if (state == 1) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal = Calendar.getInstance();
					try {
						cal.setTime(sdf.parse(tvContentValue));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mViewPager.setCurrentItem(cal.get(Calendar.WEEK_OF_YEAR) / 8);
				} else if (state == 0) {
					PedometerModel pedometer1 = null;
					try {
						pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
					} catch (DbException e) {
						// TODO Auto-generated catch block
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
				// TODO Auto-generated method stub
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
				MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
				mViewPager.setAdapter(mAdapter);
				DbUtils db = DbUtils.create(HorizontalScreenActivity.this);
				if (state == 1) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal = Calendar.getInstance();
					try {
						cal.setTime(sdf.parse(tvContentValue));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mViewPager.setCurrentItem(cal.get(Calendar.WEEK_OF_YEAR) / 8);
				} else if (state == 0) {
					PedometerModel pedometer1 = null;
					try {
						pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
					} catch (DbException e) {
						// TODO Auto-generated catch block
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
		DbUtils db = DbUtils.create(HorizontalScreenActivity.this);
		PedometerModel pedometer1 = null;
		try {
			pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		if (pedometer1 != null) {
			mViewPager.setCurrentItem(TimeUtil1.daysBetween(pedometer1.getDatestring(), tvContentValue) - 1);
		} else {
			mViewPager.setCurrentItem(0);
		}
		mAdapter.notifyDataSetChanged();
	}

	public static class MyPagerAdapter extends FragmentStatePagerAdapter {
		PedometerModel pedometer1;
		private Calendar cal;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			try {
				pedometer1 = db.findFirst(Selector.from(PedometerModel.class));
			} catch (DbException e) {
				// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					date = DateUtil.getCurrentDate();
				}
				
				
				
				return HostoryChartFragment.newInstance(date, state, "", select);
			} else {
				
				HostoryChartFragment fragmentFirst = new HostoryChartFragment();
				Bundle args = new Bundle();
				args.putString("position", "" + position);
				args.putString("hostory", tvContentValue);
				args.putInt("state", state);
				args.putInt("Select", select);
				fragmentFirst.setArguments(args);
				
				return fragmentFirst;
			}

		}

		@Override
		public CharSequence getPageTitle(int position) {
			// Calendar cal = TimeUtils.getDayForPosition(position);
			return "";
		}

	}

}
