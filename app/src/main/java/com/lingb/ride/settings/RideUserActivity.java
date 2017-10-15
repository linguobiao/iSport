package com.lingb.ride.settings;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.isport.trackernew.R;
import com.lingb.global.Global;
import com.lingb.helper.CalculateHelper;
import com.lingb.helper.KeyBoardHelper;
import com.lingb.helper.ProfileHelper;
import com.lingb.helper.SpHelper;
import com.lingb.helper.WheelHelper;
import com.lingb.ride.RideMainActivity;
import com.lingb.ride.adapter.MyDatePickerDialog;
import com.lingb.ride.bean.Profile;
import com.lingb.ride.database.DatabaseProvider;
import com.lingb.splash.FirstDeviceActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.HierarchyTraceType;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RideUserActivity extends Activity {
	
	public static final String DEF_NAME = "NAME";
	private final int DEF_UNIT = Global.TYPE_UNIT_METRIC;
	private final int DEF_GENDER = Global.TYPE_GENDER_MALE;
	private final int DEF_HEIGHT = 175;
	private final int DEF_WEIGHT = 70;
	public static final int DEF_SIZE = 0;
	private final String DEF_BIRTHDAY = "1990-01-01";

	private final int TYPE_NULL = 0;
	private final int TYPE_UNIT = 1;
	private final int TYPE_BIRTHDAY = 2;
	private final int TYPE_HEIGHT = 3;
	private final int TYPE_WEIGHT = 4;
	private final int TYPE_GENDER = 5;
	private final int TYPE_SIZE = 6;

	private int type = TYPE_NULL;
	
	private final int TEXT_SIZE_TIRE = 20;
	
	private int currentSize = DEF_SIZE;
	

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_settings_user);
		initUI();
		initValue();
		initWheel();
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (wheel_goal.isShown()) {
				WheelHelper.hideWheel(view_goal);
				return false;
			}
			
					boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
					if (isNewUp) {
						
						MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(null);
						SpHelper.putString(Global.KEY_BOUNDED_DEVICE, null);
						SpHelper.putString(Global.KEY_BOUNDED_NAME, null);
						MyApp.getIntance().mRideService.scan(false);
						MyApp.getIntance().mRideService.disconnect();
						startActivity(new Intent(RideUserActivity.this, FirstDeviceActivity.class));
						finish();
						return false;
					}
				}
		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.text_back:
				boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
				if (isNewUp) {
					MyApp.getIntance().getAppSettings().LAST_CONNECT_MAC.setValue(null);
					SpHelper.putString(Global.KEY_BOUNDED_DEVICE, null);
					SpHelper.putString(Global.KEY_BOUNDED_NAME, null);
					MyApp.getIntance().mRideService.scan(false);
					MyApp.getIntance().mRideService.disconnect();
					startActivity(new Intent(RideUserActivity.this, FirstDeviceActivity.class));
				}
				finish();
				break;
			case R.id.button_save:
				clickSave();
				break;
			case R.id.text_unit:
				type = TYPE_UNIT;
				clickSelect();
				break;
			case R.id.text_birthday:
//				type = TYPE_BIRTHDAY;
//				clickSelect();
				actionClickTextDate();
				break;
			case R.id.text_height:
				type = TYPE_HEIGHT;
				clickSelect();
				break;
			case R.id.text_weight:
				type = TYPE_WEIGHT;
				clickSelect();
				break;
			case R.id.text_gender:
				type = TYPE_GENDER;
				clickSelect();
				break;
			case R.id.layout_size:
				type = TYPE_SIZE;
				clickSelect();
				break;
			case R.id.button_one_wheel_commit:
				actionCommit();
				break;
			case R.id.button_one_wheel_cancel:
				WheelHelper.hideWheel(view_goal);
				break;
			default:
				break;
			}
		}
	};

	private boolean isMe = true;
	
	private void clickSelect() {
		WheelHelper.hideWheel(view_goal);

		if (type == TYPE_UNIT) {
			wheel_goal.setViewAdapter(adapter_unit);

			String unit = text_unit.getText().toString();
			WheelHelper.setWheelCurrentItem(unit, ARRAY_UNIT, wheel_goal);
			int currentItem = wheel_goal.getCurrentItem();
			if (currentItem == 1) {
				isMe = false;
			} else {
				isMe = true;
			}
		} else if (type == TYPE_HEIGHT) {
			if (text_unit.getText().toString().equals(getString(R.string.ride_Imperial))) {
				wheel_goal.setViewAdapter(adapter_height_in);
				String height = text_height.getText().toString();
				WheelHelper.setWheelCurrentItem(height, ARRAY_HEIGHT_IN, wheel_goal);
				
			} else {
				wheel_goal.setViewAdapter(adapter_height);
				String height = text_height.getText().toString();
				WheelHelper.setWheelCurrentItem(height, ARRAY_HEIGHT, wheel_goal);
				
			}

		} else if (type == TYPE_WEIGHT) {
			if (text_unit.getText().toString().equals(getString(R.string.ride_Imperial))) {
				wheel_goal.setViewAdapter(adapter_weight_lbs);
				String weight = text_weight.getText().toString();
				WheelHelper.setWheelCurrentItem(weight, ARRAY_WEIGHT_LBS, wheel_goal);
				
			} else {
				wheel_goal.setViewAdapter(adapter_weight);
				String weight = text_weight.getText().toString();
				WheelHelper.setWheelCurrentItem(weight, ARRAY_WEIGHT, wheel_goal);
			}
		} else if (type == TYPE_GENDER) {
			wheel_goal.setViewAdapter(adapter_gender);

			String gender = text_gender.getText().toString();
			WheelHelper.setWheelCurrentItem(gender, ARRAY_GENDER, wheel_goal);
		} else if (type == TYPE_SIZE) {
			wheel_goal.setViewAdapter(adapter_size);

			if (wheel_goal != null) {
				wheel_goal.setCurrentItem(currentSize);
			}
		}

		WheelHelper.showWheel(view_goal);
	}

	/**
	 * 确认事件
	 */
	private void actionCommit() {
		WheelHelper.hideWheel(view_goal);

		if (type == TYPE_UNIT) {
			String unit = ARRAY_UNIT[wheel_goal.getCurrentItem()];
			text_unit.setText(unit);
			if (wheel_goal.getCurrentItem() == 0) {
				changeUnit(true);
				text_height_unit.setText(getString(R.string.ride_cm));
				text_weight_unit.setText(getString(R.string.ride_kg));
			} else if (wheel_goal.getCurrentItem() == 1) {
				changeUnit(false);
				text_height_unit.setText(getString(R.string.ride_in));
				text_weight_unit.setText(getString(R.string.ride_lbs));
			}
		} else if (type == TYPE_HEIGHT) {
			if (text_unit.getText().toString().equals(getString(R.string.ride_Imperial))) {
				String height = ARRAY_HEIGHT_IN[wheel_goal.getCurrentItem()];
				text_height.setText(height);
				
			} else {
				String height = ARRAY_HEIGHT[wheel_goal.getCurrentItem()];
				text_height.setText(height);
				
			}
		} else if (type == TYPE_WEIGHT) {
			if (text_unit.getText().toString().equals(getString(R.string.ride_Imperial))) {
				String weight = ARRAY_WEIGHT_LBS[wheel_goal.getCurrentItem()];
				text_weight.setText(weight);
				
			} else {
				String weight = ARRAY_WEIGHT[wheel_goal.getCurrentItem()];
				text_weight.setText(weight);
				
			}
		} else if (type == TYPE_GENDER) {
			String gender = ARRAY_GENDER[wheel_goal.getCurrentItem()];
			text_gender.setText(gender);
		} else if (type == TYPE_SIZE) {
			currentSize = wheel_goal.getCurrentItem();
			if (currentSize == Global.TIRE_L.length) {
				text_size.setVisibility(View.GONE);
				clickSize();
			} else {
				String size = String.valueOf(Global.TIRE_L[wheel_goal.getCurrentItem()]);
				text_size.setText(size + "");
				
			}
		}
	}
	
	/**
	 * 点击日期按钮
	 */
	private void actionClickTextDate() {
		WheelHelper.hideWheel(view_goal);
		Date date = new Date();
		try {
			date = Global.sdf_yyyy_MM_dd_birthday.parse(text_birthday.getText().toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal_birthday = Calendar.getInstance();
		cal_birthday.setTime(date);
		Dialog dlg = new MyDatePickerDialog(RideUserActivity.this, R.style.dialog_transparent, new MyDatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

				// 把改变之前currentDate保存在currentTemp，用于对比是所设置的日期是在当前日期的前还是后，决定viewpager滑动方向
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, dayOfMonth);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.YEAR, year);
				text_birthday.setText(Global.sdf_yyyy_MM_dd_birthday.format(cal.getTimeInMillis()));

			}
		}, cal_birthday.get(Calendar.YEAR), cal_birthday.get(Calendar.MONTH), cal_birthday.get(Calendar.DAY_OF_MONTH));
		// 设置日期对话框的日期不超过今天的日期
		DatePicker datePicker = ((MyDatePickerDialog) dlg).getDatePicker();
		datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		dlg.show();
	}
	
	private void clickSize() {
		text_size.setVisibility(View.INVISIBLE);
		edit_size.setVisibility(View.VISIBLE);
		edit_size.setFocusable(true);
		edit_size.setFocusableInTouchMode(true);
		edit_size.requestFocus();
//		edit_size.setFadingEdgeLength(Edi)

		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				InputMethodManager inputManager =
				(InputMethodManager) edit_size.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(edit_size, 0);
			}
		}, 200);

	}
	
	private void clickSave() {
		
		if (edit_size.getVisibility() == View.VISIBLE) {
			
		} else {
			
			
			String size = text_size.getText().toString();
			if (size == null || size.equals("")) {
				Toast.makeText(RideUserActivity.this, getString(R.string.ride_tire_null), Toast.LENGTH_SHORT).show();
				return;
			}
			
			Profile profile = new Profile();
			profile.setName(DEF_NAME);
			String unit = text_unit.getText().toString();
			if (unit.equals(getString(R.string.ride_Imperial))) {
				profile.setUnit(Global.TYPE_UNIT_IMPERIAL);
			} else {
				profile.setUnit(Global.TYPE_UNIT_METRIC);
			}
			
			if (text_gender.getText().toString().equals(getString(R.string.ride_Female))) {
				profile.setGender(Global.TYPE_GENDER_FEMALE);
			} else {
				profile.setGender(Global.TYPE_GENDER_MALE);
			}
			
			String heightStr1 = text_height.getText().toString();
			String weightStr1 = text_weight.getText().toString();
			double height = DEF_HEIGHT;
			double weight = DEF_WEIGHT;
			
			// 如果身高没有改变，就设置成原来的身高
			Log.i("test", "heightstr1 = " + heightStr1 + ",   show = " + height_show);
			if (heightStr1.equalsIgnoreCase(height_show)) {
				if (unit.equals(getString(R.string.ride_Imperial))) {
					height = CalculateHelper.inchToCm(height_database);
				} else {
					height = height_database;
				}
			} else {
				if (heightStr1 != null && !heightStr1.equals("")) {
					height = Double.parseDouble(heightStr1);
					if (unit.equals(getString(R.string.ride_Imperial))) {
						height = CalculateHelper.inchToCm(height);
					}
				}
			}
			// 如果体重没有改变，就设置成原来的体重
			if (weightStr1.equalsIgnoreCase(weight_show)) {
				if (unit.equals(getString(R.string.ride_Imperial))) {
					weight = CalculateHelper.LbsToKg(weight_database);
				} else {
					weight = weight_database;
				}
			} else {
				if (weightStr1 != null && !weightStr1.equals("")) {
					weight = Double.parseDouble(weightStr1);
					if (unit.equals(getString(R.string.ride_Imperial))) {
						weight = CalculateHelper.LbsToKg(weight);
					}
				}
			}
			
			////////////
			profile.setHeight(height);
			profile.setWeight(weight);
			profile.setBirthday(text_birthday.getText().toString().trim());
			profile.setSize(currentSize);
			
			if (currentSize == Global.TIRE_L.length) {
				ProfileHelper.setTireSize(Integer.parseInt(text_size.getText().toString().trim()));
			}
			
			Profile temp = DatabaseProvider.queryProfile(RideUserActivity.this, DEF_NAME);
			if (temp == null) {
				DatabaseProvider.insertProfile(RideUserActivity.this, profile);
			} else {
				DatabaseProvider.updateProfile(RideUserActivity.this, DEF_NAME, profile);
			}
			boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
			if (isNewUp) {
				startActivity(new Intent(RideUserActivity.this, RideMainActivity.class));
			}
			finish();
		}
	}
	
	private int height_current_metric, weight_current_metric, height_save_imperial, weight_current_imperial;
	private double height_database, weight_database;
	private String height_show, weight_show;
	
	private void changeUnit(boolean isMetric) {
		if (isMetric == isMe) {
			return;
		}
		double height = Double.parseDouble(text_height.getText().toString());
		double weight = Double.parseDouble(text_weight.getText().toString());
		if (isMetric) {
			// 如果当前的值和之前保存的值相等，即没有变化，则不重新计算公制值，直接设置成之前保存的公制值。
			if ((int)height == height_save_imperial) {
				text_height.setText(String.valueOf(height_current_metric));
			}
			// 如果不相等，则说明有改变，就要把当前的英制值保存起来，并转换成公制之后显示出来并保存这个公制值。
			else {
				height_save_imperial = (int) height;
				height = CalculateHelper.parserDouble(CalculateHelper.inchToCm(height), 0, BigDecimal.ROUND_HALF_UP);
				text_height.setText(String.valueOf((int) height));
				height_current_metric = (int) height;
			}
			
			if ((int)weight == weight_current_imperial) {
				text_weight.setText(String.valueOf(weight_current_metric));

			} else {
				weight_current_imperial = (int) weight;
				weight = CalculateHelper.parserDouble(CalculateHelper.LbsToKg(weight), 0, BigDecimal.ROUND_HALF_UP);
				text_weight.setText(String.valueOf((int) weight));
				weight_current_metric = (int) weight;

			}
		} else {
			Log.i("tests", "height = " + height + ",  current_metric = " + height_current_metric);
			if ((int)height == height_current_metric) {
				Log.i("tests", "height_save_imperial = " + height_save_imperial);
				text_height.setText(String.valueOf(height_save_imperial));
			} else {
				height_current_metric = (int) height;
				height = CalculateHelper.parserDouble(CalculateHelper.cmToInch(height), 0, BigDecimal.ROUND_HALF_UP);
				text_height.setText(String.valueOf((int) height));
				height_save_imperial = (int) height;
			}
			if ((int)weight == weight_current_metric) {
				text_weight.setText(String.valueOf(weight_current_imperial));
			} else {
				weight_current_metric = (int) weight;
				weight = CalculateHelper.parserDouble(CalculateHelper.kgToLbs(weight), 0, BigDecimal.ROUND_HALF_UP);
				text_weight.setText(String.valueOf((int) weight));
				weight_current_imperial = (int) weight;
			}
		}
	}
	
	private OnTouchListener myOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			edit_size.setFocusable(false);
			return false;
		}
	};
	
	private OnFocusChangeListener myOnFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.edit_size:
				if(!hasFocus) {
					edit_size.setVisibility(View.INVISIBLE);
					text_size.setVisibility(View.VISIBLE);
					if (!edit_size.getText().toString().trim().equals("")) {
						int size = Integer.parseInt(edit_size.getText().toString().trim());
						if (size < 10000 && size > 100) {
							text_size.setText(size + "");
						}
					}
					KeyBoardHelper.hideKeyboard3(RideUserActivity.this, edit_size);

				}
				break;

			default:
				break;
			}
			
		}
	};

	/**
	 * 初始化wheel
	 */
	private void initWheel() {
		ARRAY_UNIT = new String[2];
		ARRAY_UNIT[0] = getString(R.string.ride_Metric);
		ARRAY_UNIT[1] = getString(R.string.ride_Imperial);

		ARRAY_HEIGHT = new String[151];
		int height = 100;
		for (int i = 0; i < 151; i++) {
			ARRAY_HEIGHT[i] = String.valueOf(height);
			height += 1;
		}

		ARRAY_WEIGHT = new String[191];
		int weight = 10;
		for (int i = 0; i < 191; i++) {
			ARRAY_WEIGHT[i] = String.valueOf(weight);
			weight += 1;
		}
		
		ARRAY_HEIGHT_IN = new String[61];
		int height_in = 39;
		for (int i = 0; i < 61; i++) {
			ARRAY_HEIGHT_IN[i] = String.valueOf(height_in);
			height_in += 1;
		}
 
		ARRAY_WEIGHT_LBS = new String[420];
		int weight_lbs = 22;
		for (int i = 0; i < 420; i++) {
			ARRAY_WEIGHT_LBS[i] = String.valueOf(weight_lbs);
			weight_lbs += 1;
		}
		
		
		ARRAY_GENDER = new String[2];
		ARRAY_GENDER[0] = getString(R.string.ride_Male);
		ARRAY_GENDER[1] = getString(R.string.ride_Female);
		
		Log.i("size", "l = " + Global.TIRE_L.length + ",  xi = " + Global.TIRE_SIZE.length);
		ARRAY_SIZE = new String[Global.TIRE_L.length + 1];
		for (int i = 0; i < Global.TIRE_L.length; i ++) {
			ARRAY_SIZE[i] = Global.TIRE_SIZE[i] +"    " + Global.TIRE_L[i] + "mm";
		}
		ARRAY_SIZE[Global.TIRE_L.length] = getString(R.string.ride_Custom_Tire_size);
		
		adapter_unit = new ArrayWheelAdapter<String>(this, ARRAY_UNIT);
		adapter_height = new ArrayWheelAdapter<String>(this, ARRAY_HEIGHT);
		adapter_weight = new ArrayWheelAdapter<String>(this, ARRAY_WEIGHT);
		adapter_height_in = new ArrayWheelAdapter<String>(this, ARRAY_HEIGHT_IN);
		adapter_weight_lbs = new ArrayWheelAdapter<String>(this, ARRAY_WEIGHT_LBS);
		adapter_gender = new ArrayWheelAdapter<String>(this, ARRAY_GENDER);
		adapter_size = new ArrayWheelAdapter<String>(this, ARRAY_SIZE);
		wheel_goal.setViewAdapter(adapter_unit);
		adapter_unit.setTextSize(TEXT_SIZE_TIRE);
		adapter_height.setTextSize(TEXT_SIZE_TIRE);
		adapter_weight.setTextSize(TEXT_SIZE_TIRE);
		adapter_height_in.setTextSize(TEXT_SIZE_TIRE);
		adapter_weight_lbs.setTextSize(TEXT_SIZE_TIRE);
		adapter_gender.setTextSize(TEXT_SIZE_TIRE);
		adapter_size.setTextSize(TEXT_SIZE_TIRE);
	}

	private void initValue() {

		int unit = DEF_UNIT;
		int gender = DEF_GENDER;
		double height = DEF_HEIGHT;
		double weight = DEF_WEIGHT;
		int size = DEF_SIZE;
		String birthday = DEF_BIRTHDAY;

		Profile profile = DatabaseProvider.queryProfile(RideUserActivity.this, DEF_NAME);
		if (profile != null) {
			unit = profile.getUnit();
			gender = profile.getGender();
			height = profile.getHeight();
			weight = profile.getWeight();
			size = profile.getSize();
			birthday = profile.getBirthday();

		}

		if (unit == Global.TYPE_UNIT_IMPERIAL) {
			text_unit.setText(getString(R.string.ride_Imperial));
			text_height_unit.setText(getString(R.string.ride_in));
			text_weight_unit.setText(getString(R.string.ride_lbs));
			
			height = CalculateHelper.parserDouble(CalculateHelper.cmToInch(height), 0, BigDecimal.ROUND_HALF_UP);
			weight = CalculateHelper.parserDouble(CalculateHelper.kgToLbs(weight), 0, BigDecimal.ROUND_HALF_UP);
			height_save_imperial = (int) height;
			weight_current_imperial = (int) weight;
			height_current_metric = (int) CalculateHelper.parserDouble(CalculateHelper.inchToCm(height), 0, BigDecimal.ROUND_HALF_UP);
			weight_current_metric = (int)CalculateHelper.parserDouble( CalculateHelper.LbsToKg(weight), 0, BigDecimal.ROUND_HALF_UP);
			
		} else {
			text_unit.setText(getString(R.string.ride_Metric));
			text_height_unit.setText(getString(R.string.ride_cm));
			text_weight_unit.setText(getString(R.string.ride_kg));
			
			height_current_metric = (int) height;
			weight_current_metric = (int) weight;
			height_save_imperial = (int) CalculateHelper.parserDouble(CalculateHelper.cmToInch(height), 0, BigDecimal.ROUND_HALF_UP);
			weight_current_imperial = (int) CalculateHelper.parserDouble(CalculateHelper.kgToLbs(weight), 0, BigDecimal.ROUND_HALF_UP);

		} 
		
		height_database = height;
		weight_database = weight;
		height_show = Global.df_0.format(height);
		Log.i("test", "*********** height show " + height_show + ",   height = " + height);
		weight_show = Global.df_0.format(weight);
		if (height != 0) {
			text_height.setText(height_show);
		}
		if (weight != 0) {
			text_weight.setText(weight_show);
		}
		
		
		//////////////////

		if (gender == Global.TYPE_GENDER_FEMALE) {
			text_gender.setText(getString(R.string.ride_Female));
		} else {
			text_gender.setText(getString(R.string.ride_Male));
		}

//		text_height.setText((int)height + "");
//		text_weight.setText((int)weight + "");
		text_birthday.setText(birthday);
		
		boolean isNewUp = SpHelper.getBoolean(Global.KEY_IS_NEW_UP_RIDE, true);
		if (isNewUp) {
			text_size.setText("");
			currentSize = DEF_SIZE;
		} else {
			
			if (size == Global.TIRE_L.length) {
				text_size.setText(String.valueOf(ProfileHelper.getTireSize()));
			} else {
				text_size.setText(Global.TIRE_L[size] + "");
			}
			currentSize = size;
			
		}

	}

	private void initUI() {
		TextView text_back = (TextView) findViewById(R.id.text_back);
		text_back.setOnClickListener(myOnClickListener);
		TextView button_save = (TextView) findViewById(R.id.button_save);
		button_save.setOnClickListener(myOnClickListener);

		text_unit = (TextView) findViewById(R.id.text_unit);
		text_birthday = (TextView) findViewById(R.id.text_birthday);
		text_height = (TextView) findViewById(R.id.text_height);
		text_weight = (TextView) findViewById(R.id.text_weight);
		text_gender = (TextView) findViewById(R.id.text_gender);
		text_size = (TextView) findViewById(R.id.text_size);
		edit_size = (EditText) findViewById(R.id.edit_size);
		edit_size.setOnFocusChangeListener(myOnFocusChangeListener);
		text_height_unit = (TextView) findViewById(R.id.text_height_unit);
		text_weight_unit = (TextView) findViewById(R.id.text_weight_unit);
		text_unit.setOnClickListener(myOnClickListener);
		text_birthday.setOnClickListener(myOnClickListener);
		text_height.setOnClickListener(myOnClickListener);
		text_weight.setOnClickListener(myOnClickListener);
		text_gender.setOnClickListener(myOnClickListener);
		findViewById(R.id.layout_size).setOnClickListener(myOnClickListener);

		view_goal = findViewById(R.id.layout_wheel_goal);
		wheel_goal = (WheelView) findViewById(R.id.wheel_wheel);

		Button button_ok = (Button) findViewById(R.id.button_one_wheel_commit);
		Button button_cancel = (Button) findViewById(R.id.button_one_wheel_cancel);
		button_ok.setOnClickListener(myOnClickListener);
		button_cancel.setOnClickListener(myOnClickListener);
		
		edit_size.setFocusable(false);
		
		final View layout_content = findViewById(R.id.layout_content);
		layout_content.setOnTouchListener(myOnTouchListener);
		text_unit.setOnTouchListener(myOnTouchListener);
		text_birthday.setOnTouchListener(myOnTouchListener);
		text_height.setOnTouchListener(myOnTouchListener);
		text_weight.setOnTouchListener(myOnTouchListener);
		text_size.setOnTouchListener(myOnTouchListener);
		text_gender.setOnTouchListener(myOnTouchListener);
		button_save.setOnTouchListener(myOnTouchListener);
		

	}

	private TextView text_unit, text_birthday, text_height;
	private TextView text_weight, text_gender, text_size;
	private TextView text_height_unit, text_weight_unit;
	private EditText edit_size;

	private View view_goal;
	private WheelView wheel_goal;
	private ArrayWheelAdapter<String> adapter_unit, adapter_height, adapter_weight, adapter_gender, adapter_size, adapter_height_in, adapter_weight_lbs;
	private String[] ARRAY_UNIT = null;
	private String[] ARRAY_HEIGHT = null;
	private String[] ARRAY_WEIGHT = null;
	private String[] ARRAY_HEIGHT_IN = null;
	private String[] ARRAY_WEIGHT_LBS = null;
	private String[] ARRAY_GENDER = null;
	private String[] ARRAY_SIZE = null;

}
