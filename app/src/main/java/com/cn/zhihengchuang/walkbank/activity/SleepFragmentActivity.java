package com.cn.zhihengchuang.walkbank.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

//import cn.sharesdk.onekeyshare.OnekeyShare;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.activity.ExerciseFragmentActivity.MyPagerAdapter;
import com.cn.zhihengchuang.walkbank.adapter.CachingFragmentStatePagerAdapter;
import com.cn.zhihengchuang.walkbank.fragment.FragmentContent;
import com.cn.zhihengchuang.walkbank.fragment.SleepFragment;
import com.cn.zhihengchuang.walkbank.interfaces.FragmentCallBackListener;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.TimeUtils;

@SuppressLint("NewApi")
public class SleepFragmentActivity extends FragmentActivity  {
	private ViewPager mViewPager;// fragment
	//private TabFragmentPagerAdapter mAdapter;// fragment的adapter
	public static FragmentManager fm;  
	private SharedPreferences share = null;
	private Editor edit;
	private ImageView image_date_icon;
	private ImageView today;
	private ImageView share_iv;
	private MyPagerAdapter adapterViewPager;
	private static String date;
	private static Context mContext;
	private static List<String> lists;
	private static List<FragmentCallBackListener> fcblistenerlist;//与fragment通信的通信接口

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名称
		edit = share.edit();
		mContext=this;
		init();
		//mRegisterReceiver();
		
		//mViewPager.setCurrentItem(2); // ViewPager 跟随一起 切换
		//fm = getSupportFragmentManager();  
		// 只當容器，主要內容已Fragment呈現  
		//initFragment(new TwoFragment());  

	}

	/*private void mRegisterReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BROADCAST_ACTION_CONNECTION_DEVICE);
		filter.addAction(Constants.BROADCAST_ACTION_DISCONNECTION_DEVICE);
		filter.addAction(Constants.BROADCAST_ACTION_DATA_STEPS);
		filter.setPriority(1000);
		registerReceiver(myReceiver, filter);
	}*/

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void init() {
		/*fcblistenerlist = new ArrayList<FragmentCallBackListener>();//与fragment通信的通信接口
		lists = new ArrayList<String>();
		date = DateUtil.getCurrentDate();
		lists.add(date);
		lists.add(DateUtil.getDateStr(date, -1));
		//lists.add(DateUtil.getDateStr(date, -2));
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		setListener();*/
		image_date_icon = (ImageView) findViewById(R.id.main_fragment_date);
		today = (ImageView) findViewById(R.id.main_fragment_today_date);
		//share_iv = (ImageView) findViewById(R.id.share_iv);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapterViewPager);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0==adapterViewPager.getCount()-1){
					today.setVisibility(View.GONE);
				}else{
					today.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			}
        );
		// set pager to current date

		mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar
				.getInstance()));
		/*share_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//showShare();
			}
		});*/
		today.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar
						.getInstance()));
			}
		});
		image_date_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SleepFragmentActivity.this,
						CalendarActivity.class);
				startActivityForResult(intent, 10);
			}
		});
	}

	/*private void setListener() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if(position == 0){
					lists.add(DateUtil.getDateStr(date, -lists.size()));
					mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
					mViewPager.setAdapter(mAdapter);
					mViewPager.setCurrentItem(1); // ViewPager 跟随一起 切换
					position = 1;
				}
				setIntentDate(position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}*/
	
	/*private void setIntentDate(int i) {
		System.out.println(lists.size()+"chong");
		System.out.println(i);
		for(final FragmentCallBackListener callBackListener: fcblistenerlist){
			callBackListener.callBack(DateUtil.getDateStr(date, i - lists.size() + 1));
		}
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		try {
			FragmentCallBackListener callBackListener=(FragmentCallBackListener) fragment;
			fcblistenerlist.add(callBackListener);
		    callBackListener.callBack(date);
		} catch (Exception e) {}
	}

	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			System.out.println(arg0+"====================");
			Fragment ft = new SleepFragment();//DateUtil.getDateStr(date, arg0 - lists.size() + 1)
			return ft;
		}

		@Override
		public int getCount() {
			return lists.size();
		}
	}
	
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.BROADCAST_ACTION_DISCONNECTION_DEVICE.equals(intent
					.getAction())
					|| Constants.BROADCAST_ACTION_CONNECTION_DEVICE
							.equals(intent.getAction())) {

			} else if (Constants.BROADCAST_ACTION_DATA_STEPS.equals(intent
					.getAction())) {

			}
		}
	};*/

	/*@Override
	public void callBack(String str) {

	}

	@Override
	public void callBack() {
		System.out.println(mViewPager+"");
		mViewPager.setCurrentItem(lists.size() - 1);
	}*/
	
	/*// 切換Fragment
	public static void changeFragment(Fragment f) {
		changeFragment(f, false);
	}

	// 初始化Fragment(FragmentActivity中呼叫)
	public static void initFragment(Fragment f) {
		changeFragment(f, true);
	}*/

	/*private static void changeFragment(Fragment f, boolean init) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment, f);
		if (!init)
			ft.addToBackStack(null);
		ft.commit();
	}*/
	/*private void showShare(){
		ShareSDK.initSDK(SleepFragmentActivity.this);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		//oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		 
		// 启动分享GUI
		oks.show(SleepFragmentActivity.this);
	}*/
	public static class MyPagerAdapter extends CachingFragmentStatePagerAdapter {

		private Calendar cal;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return TimeUtils.DAYS_OF_TIME;
		}

		@Override
		public Fragment getItem(int position) {
			long timeForPosition = TimeUtils.getDayForPosition(position)
					.getTimeInMillis();
			return SleepFragment.newInstance(timeForPosition);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Calendar cal = TimeUtils.getDayForPosition(position);
			
			return TimeUtils.getFormattedDate(mContext, cal.getTimeInMillis());
		}

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && resultCode == 201) {
			System.out.println(data.getStringExtra("date") + "tianxia");
			String[] dates=data.getStringExtra("date").split("-");
			Calendar temp = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[2]));
			cal.set(Calendar.MONTH, Integer.parseInt(dates[1])-1);
			cal.set(Calendar.YEAR, Integer.parseInt(dates[0])); 
			if(cal.getTimeInMillis()>temp.getTimeInMillis()){
				Toast.makeText(SleepFragmentActivity.this, getResources().getString(R.string.your_date_is_not_yet), 1000).show();
				return;
			}
			mViewPager.setCurrentItem(TimeUtils.getPositionForDay(cal));
			 }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("touch");
		return super.onTouchEvent(event);
	}

}