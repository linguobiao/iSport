package com.cn.zhihengchuang.walkbank.activity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.adapter.CachingFragmentStatePagerAdapter;
import com.cn.zhihengchuang.walkbank.ble.BleService;
import com.cn.zhihengchuang.walkbank.event.DataRefreshEvent;
import com.cn.zhihengchuang.walkbank.fragment.FragmentContent;
import com.cn.zhihengchuang.walkbank.interfaces.FragmentCallBackListener;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.TimeUtils;
import com.cn.zhihengchuang.walkbank.view.CustomViewPager;
import com.cn.zhihengchuang.walkbank.view.ViewPagerScroller;
import com.lingb.global.Global;
import com.squareup.otto.Subscribe;

@SuppressLint("NewApi")
public class ExerciseFragmentActivity extends BaseActivity implements FragmentCallBackListener {
	private final static String TAG = BleService.class.getSimpleName();
	public CustomViewPager mViewPager;// fragment
	// private TabFragmentPagerAdapter mAdapter;// fragment的adapter
	public static FragmentManager fm;
	private SharedPreferences share = null;
	private Editor edit;
	private static List<String> lists;
	private String selectDate;
	private boolean isClick = false;
	private BleService mBleService;
	// private static List<FragmentCallBackListener> fcblistenerlist;//
	// 与fragment通信的通信接口
	private static Context mContext;
	private CachingFragmentStatePagerAdapter adapterViewPager;
	public ImageView image_date_icon;
	public ImageView today;
	private ImageView share_iv;
	private boolean isFromActivityResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名称
		edit = share.edit();
		mContext = this;
		// init();
		mRegisterReceiver();
		// initViewPagerScroll(mViewPager);
		image_date_icon = (ImageView) findViewById(R.id.main_fragment_date);
		today = (ImageView) findViewById(R.id.main_fragment_today_date);
		today.setVisibility(View.GONE);
		// share_iv = (ImageView) findViewById(R.id.share_iv);

		/*
		 * share_iv.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub //showShare(); } });
		 */
		today.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar.getInstance()));
			}
		});
		image_date_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ExerciseFragmentActivity.this, CalendarActivity.class);
				startActivityForResult(intent, 10);
			}
		});
		// fm = getSupportFragmentManager();
		// 只當容器，主要內容已Fragment呈現
		// initFragment(new TwoFragment());

	}

	/**
	 * 设置ViewPager的滑动速度
	 * 
	 * */
	private void initViewPagerScroll(ViewPager mViewPager) {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			ViewPagerScroller scroller = new ViewPagerScroller(mViewPager.getContext());
			mScroller.set(mViewPager, scroller);
		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}

	private void mRegisterReceiver() {
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constants.CLEAR_AlL);
		filter.setPriority(1000);
		registerReceiver(myReceiver, filter);
	}

	/*
	 * private void showShare() {
	 * ShareSDK.initSDK(ExerciseFragmentActivity.this); OnekeyShare oks = new
	 * OnekeyShare(); // 关闭sso授权 oks.disableSSOWhenAuthorize();
	 * 
	 * // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法 //
	 * oks.setNotification(R.drawable.ic_launcher, //
	 * getString(R.string.app_name)); // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	 * oks.setTitle(getString(R.string.share)); // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	 * oks.setTitleUrl("http://sharesdk.cn"); // text是分享文本，所有平台都需要这个字段
	 * oks.setText("我是分享文本"); // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数 //
	 * oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片 //
	 * url仅在微信（包括好友和朋友圈）中使用 oks.setUrl("http://sharesdk.cn"); //
	 * comment是我对这条分享的评论，仅在人人网和QQ空间使用 oks.setComment("我是测试评论文本"); //
	 * site是分享此内容的网站名称，仅在QQ空间使用 oks.setSite(getString(R.string.app_name)); //
	 * siteUrl是分享此内容的网站地址，仅在QQ空间使用 oks.setSiteUrl("http://sharesdk.cn");
	 * 
	 * // 启动分享GUI oks.show(ExerciseFragmentActivity.this); }
	 */

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void init() {
		// fcblistenerlist = new ArrayList<FragmentCallBackListener>();//
		// 与fragment通信的通信接口
		/*
		 * lists = new ArrayList<String>(); date = DateUtil.getCurrentDate();
		 * lists.add(date); lists.add(DateUtil.getDateStr(date, -1));
		 */
		// lists.add(DateUtil.getDateStr(date, -2));

	}

	/*
	 * private void setListener() { mViewPager.setOnPageChangeListener(new
	 * OnPageChangeListener() {
	 * 
	 * @Override public void onPageSelected(int position) { selectDate =
	 * lists.get(position);
	 * 
	 * if (position == 0) { lists.add(DateUtil.getDateStr(selectDate, -1));
	 * mAdapter = new TabFragmentPagerAdapter( getSupportFragmentManager());
	 * mViewPager.setAdapter(mAdapter); mViewPager.setCurrentItem(1); //
	 * ViewPager 跟随一起 切换 position = 1; } else
	 * if(position==lists.size()-1&&!tempStr.equals(DateUtil.
	 * getCurrentDate())){ lists.add(DateUtil.getDateStr(tempStr, 1)); mAdapter
	 * = new TabFragmentPagerAdapter(getSupportFragmentManager());
	 * mViewPager.setAdapter(mAdapter); }
	 * 
	 * setIntentDate(position); }
	 * 
	 * @Override public void onPageScrolled(int arg0, float arg1, int arg2) { }
	 * 
	 * @Override public void onPageScrollStateChanged(int arg0) { } }); }
	 */

	/*
	 * private void setIntentDate(int i) { System.out.println(lists.size() +
	 * "chong"); System.out.println(i); for (final FragmentCallBackListener
	 * callBackListener : fcblistenerlist) {
	 * 
	 * callBackListener.callBack(DateUtil.getDateStr(date, i - lists.size() +
	 * 1)); } }
	 */
	/*
	 * @Override public void onAttachFragment(Fragment fragment) {
	 * super.onAttachFragment(fragment); try { FragmentCallBackListener
	 * callBackListener = (FragmentCallBackListener) fragment;
	 * //fcblistenerlist.add(callBackListener);
	 * //callBackListener.callBack(date); } catch (Exception e) { } }
	 */

	/*
	 * public static class TabFragmentPagerAdapter extends FragmentPagerAdapter
	 * {
	 * 
	 * public TabFragmentPagerAdapter(FragmentManager fm) { super(fm); }
	 * 
	 * @Override public Fragment getItem(int arg0) { System.out.println(arg0 +
	 * "===================="); Fragment ft = new ExerciseFragment();//
	 * DateUtil.getDateStr(date, // arg0 - lists.size() + 1) return ft; }
	 * 
	 * @Override public int getCount() { return lists.size(); } }
	 */
	public BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.CLEAR_AlL.equals(intent.getAction())) {
				mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar.getInstance()));

			}
		}
	};
	private String tempStr;

	/*
	 * @Override public void callBack(String str) { SimpleDateFormat sdf = new
	 * SimpleDateFormat("yyyy-MM-dd"); //得到指定模范的时间 Date d1 = null; Date d2 =
	 * null; try { d1 = sdf.parse(str); d2 = sdf.parse(selectDate); } catch
	 * (ParseException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * //比较 if(d2.getTime() > d1.getTime()) { for(int i = 0; i >
	 * DateUtil.getGapCount(d2, d1);i++){
	 * lists.add(DateUtil.getDateStr(selectDate, -(i+1))); } }
	 * lists.add(0,DateUtil.getDateStr(str, -1)); lists.add(1,str);
	 * //lists.add(1,DateUtil.getDateStr(str, -1)); //date=str; //tempStr=str;
	 * //lists.add(DateUtil.getDateStr(str, -lists.size())); mAdapter = new
	 * TabFragmentPagerAdapter(getSupportFragmentManager());
	 * mViewPager.setAdapter(mAdapter); mViewPager.setCurrentItem(1); //
	 * ViewPager 跟随一起 切换 setIntentDate(1); }
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("onActivityResult");
		Log.i(TAG, "onActivityResult");
		isFromActivityResult = true;
		if (data != null && resultCode == 201) {
			System.out.println(data.getStringExtra("date") + "tianxia");
			String[] dates = data.getStringExtra("date").split("-");
			Log.i("date", "date[] = " + Arrays.toString(dates));
			Calendar temp = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[2]));
			cal.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
			cal.set(Calendar.YEAR, Integer.parseInt(dates[0]));
			if (cal.getTimeInMillis() > temp.getTimeInMillis()) {
				Toast.makeText(ExerciseFragmentActivity.this, getResources().getString(R.string.your_date_is_not_yet), 1000).show();
				return;
			}
			mViewPager.setCurrentItem(TimeUtils.getPositionForDay(cal));
		}
	}

	/*
	 * @Override public void callBack() { System.out.println(mViewPager+"");
	 * mViewPager.setCurrentItem(lists.size() - 1); }
	 */
	/*
	 * // 切換Fragment public static void changeFragment(Fragment f) {
	 * changeFragment(f, false); }
	 * 
	 * // 初始化Fragment(FragmentActivity中呼叫) public static void
	 * initFragment(Fragment f) { changeFragment(f, true); }
	 * 
	 * private static void changeFragment(Fragment f, boolean init) {
	 * FragmentTransaction ft = fm.beginTransaction(); ft.replace(R.id.fragment,
	 * f); if (!init) ft.addToBackStack(null); ft.commit(); }
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("touch");
		return super.onTouchEvent(event);
	}

	@Override
	public void callBack(String str) {
		// TODO Auto-generated method stub

	}

	@Subscribe
	public void onDataRefreshEvent(DataRefreshEvent event) {
		// if(event.date.equals(tvContentValue)){
		System.out.println("haole" + event.date);
		// }
	}

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBleService = ((BleService.LocalBinder) service).getService();
			MyApp.getIntance().mService = mBleService;
			if (!mBleService.initialize()) {
				// Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBleService = null;
		}
	};

	private void initData() {
		Intent i = new Intent(this, BleService.class);
		bindService(i, mServiceConnection, BIND_AUTO_CREATE);
		BluetoothAdapter.getDefaultAdapter().enable();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		/*
		 * adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
		 * mViewPager.setAdapter(adapterViewPager);
		 * MyApp.getIntance().setmPosition(0);
		 * mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar
		 * .getInstance()));
		 */
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
		Log.i(TAG, "onResume");
		if (!isFromActivityResult) {
			mViewPager = (CustomViewPager) findViewById(R.id.mViewPager);
			adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
			mViewPager.setAdapter(adapterViewPager);
			MyApp.getIntance().setmPosition(0);
			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					MyApp.getIntance().setmPosition(0);
					if (arg0 == adapterViewPager.getCount() - 1) {
						today.setVisibility(View.GONE);
						MyApp.getIntance().setToday(true);
					} else {
						today.setVisibility(View.VISIBLE);
						MyApp.getIntance().setToday(false);
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

					if (arg1 == 0 && arg2 == 0) {
						sendBroadcast(new Intent(Global.ACTION_VIEWPAGER_STOP));
					}
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}
			});
			mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar.getInstance()));
			// initData();
		}

	}

	public static class MyPagerAdapter extends CachingFragmentStatePagerAdapter {

		private Calendar cal;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			TimeUtils.LAST_DAY_OF_TIME = Calendar.getInstance();
			if ((int) ((Calendar.getInstance().getTimeInMillis() - TimeUtils.FIRST_DAY_OF_TIME.getTimeInMillis() + 1000000) / (24 * 60 * 60 * 1000)) + 1 != TimeUtils.DAYS_OF_TIME) {
				TimeUtils.DAYS_OF_TIME = (int) ((Calendar.getInstance().getTimeInMillis() - TimeUtils.FIRST_DAY_OF_TIME.getTimeInMillis() + 1000000) / (24 * 60 * 60 * 1000)) + 1;
				notifyDataSetChanged();
			}

			return TimeUtils.DAYS_OF_TIME;

		}

		@Override
		public Fragment getItem(int position) {
			Log.i("test", "************ get item ");
			long timeForPosition = TimeUtils.getDayForPosition(position).getTimeInMillis();
			return FragmentContent.newInstance(timeForPosition);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Calendar cal = TimeUtils.getDayForPosition(position);
			return TimeUtils.getFormattedDate(mContext, cal.getTimeInMillis());
		}

	}

	@Override
	public void callBack() {
		// TODO Auto-generated method stub
		// mViewPager.setCurrentItem(lists.size() - 1);
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		isFromActivityResult = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}

}