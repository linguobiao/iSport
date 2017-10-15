package com.cn.zhihengchuang.walkbank.ble;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.appformwork.preference.MicroRecruitSettings;
import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.entity.SleepModel;
import com.cn.zhihengchuang.walkbank.entity.SportsModel;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.FormatTransfer;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * 
 * @author longke 数据解析类
 * 
 */
public class ParserLoader {
	private final static String TAG = BleService.class.getSimpleName();
	static int tmpSteps = 0;

	@SuppressLint("NewApi")
	public static PedometerModel processData(ArrayList<Byte> datas, Context context, BluetoothGatt gatt) {
		SharedPreferences share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
		PedometerModel ped = new PedometerModel();
		ped.setUuid(gatt.getDevice().getAddress());
		parserDate(datas, context, gatt, true, ped);
		parserStep(datas, ped);
		ped.setTotalcalories((formatToInt(datas.get(12)) << 24) | (formatToInt(datas.get(13)) << 16) | (formatToInt(datas.get(14)) << 8)
				| (formatToInt(datas.get(15))));
		ped.setTotaldistance((formatToInt(datas.get(16)) << 8) | formatToInt(datas.get(17)));
		ped.setTotalsporttime((formatToInt(datas.get(18)) << 8) | formatToInt(datas.get(19)));

		// ==========修复固件35导致的数据错乱问题
		// 判断是否是今天的数据
		boolean isToday = false;
		String today = DateUtil.getCurrentDate();
		try {
			if (today.equals(ped.getDatestring())) {
				isToday = true;
			}
		} catch (Exception e) {

		}
		// Log.e("TAG1", ped.getDatestring()+"===============================");
		// Log.e("TAG1", printHexString(datas));
		// 如果不是今天的数据，则重新排列
		if (!isToday && datas.size() > 110) {
			ArrayList<Byte> d1 = new ArrayList<Byte>();
			// 先把从46-59这14个字节复制出来放在d1里面
			for (int i = 0; i < 14; i++) {
				d1.add(datas.get(46 + i));
			}
			// 60+ 50
			int m = 110;
			while (m < datas.size()) {
				// 一个一个替换
				for (int i = 0; i < 14 && (i + m) < datas.size(); i++) {
					byte value = datas.get(m + i);
					datas.set(m + i, d1.get(i));
					d1.set(i, value);
				}
				m += 64;
			}
			// 打印
			// Log.e("TAG1", ped.getDatestring()+"===============================");
			// Log.e("TAG1", printHexString(datas));
		}
		// ===================================

		// 第二个包
		ped.setTotalsleeptime((formatToInt(datas.get(20)) << 8) | formatToInt(datas.get(21)));
		ped.setTotalstilltime((formatToInt(datas.get(22)) << 8) | formatToInt(datas.get(23)));
		ped.setWalktime((formatToInt(datas.get(24)) << 8) | formatToInt(datas.get(25)));
		ped.setSlowwalktime((formatToInt(datas.get(26)) << 8) | formatToInt(datas.get(27)));
		ped.setMidwalktime((formatToInt(datas.get(28)) << 8) | formatToInt(datas.get(29)));
		ped.setFastwalktime((formatToInt(datas.get(30)) << 8) | formatToInt(datas.get(31)));
		ped.setSlowruntime((formatToInt(datas.get(32)) << 8) | formatToInt(datas.get(33)));
		ped.setMidruntime((formatToInt(datas.get(34)) << 8) | formatToInt(datas.get(35)));
		ped.setFastruntime((formatToInt(datas.get(36)) << 8) | formatToInt(datas.get(37)));
		// [BLTManager sharedInstance].elecQuantity = val[38];

		// 第三个包
		ped.setStepSize((formatToInt(datas.get(40)) << 8) | (formatToInt(datas.get(41))));
		ped.setWeight(((formatToInt(datas.get(42)) << 8) + (formatToInt(datas.get(43)))) / 100);
		ped.setTargetsleep((formatToInt(datas.get(44)) << 16) | (formatToInt(datas.get(45)) << 8) | (formatToInt(datas.get(46))));
		ped.setTotalbytes((formatToInt(datas.get(49)) << 8) | (formatToInt(datas.get(50))));
		int[] calories = new int[2];
		calories[0] = 1;
		calories[1] = 2;
		ped.setDetailcalories(calories);
		DbUtils db = DbUtils.create(context);
		db.configAllowTransaction(true);
		db.configDebug(true);
		try {
			// PedometerModel pedometer =
			// db.findFirst(Selector.from(PedometerModel.class).where("datestring","=",pedometer.getDatestring()));
			WhereBuilder builder = WhereBuilder.b("datestring", "==", ped.getDatestring()).and("uuid", "==", ped.getUuid());
			db.delete(PedometerModel.class, builder);
			db.saveBindingId(ped);
			PedometerModel pedometer = db.findFirst(Selector.from(PedometerModel.class).where(builder));
			if (pedometer != null) {
				Log.i(TAG, "pedometer." + pedometer.getTotalsteps());
				Log.i(TAG, "pedometer." + formatToInt(datas.get(7)));
				Log.i(TAG, "pedometer." + pedometer.getDetailcalories());
				Intent intent = new Intent(Constants.UPDATE_OK);
				Log.i("test", "----------- processData");
				intent.putExtra("date", ped.getDatestring());
				context.sendBroadcast(intent);
				MyApp.getIntance().mService.setIsSync(false);				/*
				 * BusProvider.getInstance().post(new DataRefreshEvent(pedometer.getDatestring())); Log.i(TAG, "fale.");
				 */

			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		try {
			// ped.getDetailsteps()
			ArrayList<SportsModel> sports = new ArrayList<SportsModel>();
			ArrayList<SportsModel> tempSports = new ArrayList<SportsModel>();
			ArrayList<SleepModel> sleeps = new ArrayList<SleepModel>();
			int[] detailSteps = new int[] {};
			int i = 60;
			int lastOrder = 0;
			int signOrder = 0;
			for (; i < datas.size() && i < (3 * 288 + 64);) {
				if (datas.size() > i + 1) {
					int time = formatToInt(datas.get(i));
					if (time == 0 && i > 63) {
						i = 3 * 288 + 64;
						break;
					}
					if (time == 126) {
						System.out.println("hao");
					}
					// String.format("%02X ", datas.get(i+1));
					int state = (formatToInt(datas.get(i + 1)) >> 4);
					if (state == 0) {
						SportsModel model = new SportsModel();
						// model.wareUUID = [BLTManager
						// sharedInstance].model.bltID;
						// model.dateDay = totalModel.dateString;
						model.last_order = lastOrder;
						byte[] order = new byte[1];
						order[0] = datas.get(i);
						model.current_order = byteArrayToInt(order) + signOrder;
						ArrayList<Byte> by = new ArrayList<Byte>();
						by.add(datas.get(i + 1));
						by.add(datas.get(i + 2));
						model.steps = byteArrayListToInt(by);
						model.calorie = (int) (model.steps * ((60 - 13.63636) * 0.000693 + 0.00495));
						model.distance = 75 * model.steps / 100f;
						if (model.current_order <= 288) {

							for (int o = lastOrder + 1; o < model.current_order; o++) {
								SportsModel modelSport = new SportsModel();
								modelSport.current_order = o;
								modelSport.steps = model.steps;
								modelSport.calorie = (int) (model.steps * ((60 - 13.63636) * 0.000693 + 0.00495));
								modelSport.distance = 75 * model.steps / 100f;
								sports.add(modelSport);
								SleepModel sleep = new SleepModel();
								sleep.current_order = o;
								sleep.sleep_state = 0;
								sleeps.add(sleep);
							}
							sports.add(model);
						}
						if (model.current_order <= 288) {
							SleepModel modelSport = new SleepModel();
							modelSport.current_order = formatToInt(datas.get(i)) + signOrder;
							modelSport.sleep_state = 0;
							sleeps.add(modelSport);
						}
						/*
						 * CGFloat tmpCalories = [model stepsConvertCalories:model.steps withWeight:totalModel.weight withModel:YES] + caloriesPrecision; model.calories = (NSInteger)tmpCalories; caloriesPrecision = tmpCalories - (NSInteger)tmpCalories;
						 * 
						 * CGFloat tmpDistance = [model StepsConvertDistance:model.steps withPace:totalModel.stepSize] + distancePrecision; model.distance = (NSInteger)tmpDistance; distancePrecision = tmpDistance - (NSInteger)tmpDistance;
						 */

						// totalModel.totalSteps += model.steps;
						// totalModel.totalCalories += model.calories;

						lastOrder = model.current_order;
						i += 3;
					} else if (state == 8) {
						SleepModel model = new SleepModel();
						// model.wareUUID = [BLTManager
						// sharedInstance].model.bltID;
						// model.dateDay = totalModel.dateString;
						model.last_order = lastOrder;
						model.current_order = formatToInt(datas.get(i)) + signOrder;
						byte[] order = new byte[1];
						order[0] = datas.get(i);
						model.current_order = byteArrayToInt(order) + signOrder;
						ArrayList<Byte> by = new ArrayList<Byte>();
						by.add(datas.get(i + 1));
						model.sleep_state = byteArrayListToInt(by);
						;

						// if( model.current_order<=288){
						// Log.i(TAG , "hao"+model.sleep_state);
						for (int o = lastOrder + 1; o < model.current_order; o++) {
							SleepModel modelSport = new SleepModel();
							modelSport.current_order = o;
							modelSport.sleep_state = model.sleep_state;
							sleeps.add(modelSport);
							SportsModel sport = new SportsModel();
							sport.current_order = formatToInt(datas.get(i)) + signOrder;
							sport.steps = 0;
							sport.calorie = 0;
							sport.distance = 0;
							sports.add(sport);
						}
						sleeps.add(model);
						if (model.current_order <= 288) {
							SportsModel modelSport = new SportsModel();
							modelSport.current_order = formatToInt(datas.get(i)) + signOrder;
							modelSport.steps = 0;
							modelSport.calorie = 0;
							modelSport.distance = 0;
							sports.add(modelSport);
						}
						lastOrder = model.current_order;
						// }
						if (ped.getDatestring().equals(DateUtil.getCurrentDate())) {
							i += 3;
						} else {
							i += 2;
						}

					} else {
						i += 6;
					}
					if (time == 255) {
						signOrder = 255;
					}
				} else {
					break;
				}
			}
			Log.i(TAG, "sports.size()" + sports.size());
			/*
			 * for (SportsModel model : sports) { Log.i(TAG, "hao" + model.steps + "model.current_order" + model.current_order); }
			 */
			Log.i(TAG, "sleeps.size()" + sleeps.size());
			for (int j = sports.size(); j < 288; j++) {
				SportsModel modelSport = new SportsModel();
				modelSport.current_order = j;
				modelSport.steps = 0;
				modelSport.calorie = 0;
				modelSport.distance = 0;
				sports.add(modelSport);
			}
			String sleepsTime = "";
			String sleepsState = "";
			String sportTime = "0,";
			String sportStep = "0,";
			String distancesTime = "0,";
			String sportDistances = "0,";
			String calorieTime = "0,";
			String sportCalorie = "0,";
			for (int j = sleeps.size(); j < 288; j++) {
				SleepModel model = new SleepModel();
				model.current_order = j;
				model.sleep_state = 0;
				sleeps.add(model);
			}
			for (int z = 0; z < sleeps.size(); z++) {
				if (z != sleeps.size() - 1) {
					sleepsTime = sleepsTime + sleeps.get(z).current_order + ",";
					sleepsState = sleepsState + sleeps.get(z).sleep_state + ",";
				} else {
					sleepsTime = sleepsTime + sleeps.get(z).current_order + "";
					sleepsState = sleepsState + sleeps.get(z).sleep_state + "";
				}

			}

			int steps = 0;
			for (int s = 0; s < sports.size(); s++) {
				if (s % 6 == 0) {
					steps = sports.get(s).steps;
				} else if (s % 6 == 5) {
					steps = steps + sports.get(s).steps;
					if (s == 287) {
						sportTime = sportTime + s + "";
						sportStep = sportStep + steps + "";
					} else {
						sportTime = sportTime + s + ",";
						sportStep = sportStep + steps + ",";
					}
				} else {
					steps = steps + sports.get(s).steps;
				}

			}
			float distances = 0;
			for (int s = 0; s < sports.size(); s++) {
				if (s % 6 == 0) {
					distances = sports.get(s).distance;

				} else if (s % 6 == 5) {
					distances = distances + sports.get(s).distance;
					if (s == 287) {
						distancesTime = distancesTime + s + "";
						sportDistances = sportDistances + distances + "";
					} else {
						distancesTime = distancesTime + s + ",";
						sportDistances = sportDistances + distances + ",";
					}
				} else {
					distances = distances + sports.get(s).distance;
				}

			}
			int calorie = 0;
			for (int s = 0; s < sports.size(); s++) {
				if (s % 6 == 0) {
					calorie = sports.get(s).calorie;
				} else if (s % 6 == 5) {
					calorie = calorie + sports.get(s).calorie;
					if (s == 287) {
						calorieTime = calorieTime + s + "";
						sportCalorie = sportCalorie + calorie + "";
					} else {
						calorieTime = calorieTime + s + ",";
						sportCalorie = sportCalorie + calorie + ",";
					}
				} else {
					calorie = calorie + sports.get(s).calorie;
				}

			}
			Log.i(TAG, "sleepsTime" + sleepsTime);
			Log.i(TAG, "sleepsState" + sleepsState);
			Log.i(TAG, "distancesTime" + distancesTime);
			Log.i(TAG, "sportDistances" + sportDistances);
			Log.i(TAG, "calorieTime" + calorieTime);
			Log.i(TAG, "sportCalorie" + sportCalorie);

			MicroRecruitSettings settings = MyApp.getIntance().getAppSettings();
			settings.setDate(ped.getDatestring());
			settings.SLEEP_HALF_TIME.setValue(sleepsTime);
			settings.SLEEP_HALF.setValue(sleepsState);
			Log.i("chart", "sprotStep = " + sportStep);
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "sport_half_time", sportTime).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "sport_half", sportStep).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "sleep_half_time", sleepsTime).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "sleep_half", sleepsState).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "distances_half_time", distancesTime).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "distances_half", sportStep).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "calorie_half_time", calorieTime).commit();
			share.edit().putString(ped.getUuid() + ped.getDatestring() + "calorie_half", sportCalorie).commit();
		} catch (Exception e) {
		}

		return ped;

	}

	@SuppressLint("NewApi")
	public static void processRealTimeData(ArrayList<Byte> datas, Context context, BluetoothGatt gatt) {
		PedometerModel ped = new PedometerModel();
		ped.setUuid(gatt.getDevice().getAddress());
		parserDate(datas, context, gatt, false, ped);
		parserStep(datas, ped);
		ped.setTotalcalories((formatToInt(datas.get(12)) << 24) | (formatToInt(datas.get(13)) << 16) | (formatToInt(datas.get(14)) << 8)
				| (formatToInt(datas.get(15))));
		ped.setTotaldistance((formatToInt(datas.get(16)) << 8) | formatToInt(datas.get(17)));
		ped.setTotalsporttime((formatToInt(datas.get(18)) << 8) | formatToInt(datas.get(19)));
		DbUtils db = DbUtils.create(context);
		db.configAllowTransaction(true);
		db.configDebug(true);
		try {
			// PedometerModel pedometer =
			// db.findFirst(Selector.from(PedometerModel.class).where("datestring","=",pedometer.getDatestring()));
			WhereBuilder builder = WhereBuilder.b("datestring", "==", ped.getDatestring()).and("uuid", "==", ped.getUuid());
			db.delete(PedometerModel.class, builder);
			db.saveBindingId(ped);
			PedometerModel pedometer = db.findFirst(Selector.from(PedometerModel.class).where(builder));
			if (pedometer != null) {
				Log.i(TAG, "pedometer." + pedometer.getTotalsteps());
				Log.i(TAG, "pedometer." + formatToInt(datas.get(7)));
				Log.i(TAG, "pedometer." + pedometer.getDetailcalories());
				Intent intent = new Intent(Constants.UPDATE_OK);
				Log.i("test", "----------- processRealTimeData");
				intent.putExtra("date", ped.getDatestring());
				intent.putExtra("isRealTime", true);
				context.sendBroadcast(intent);
				
				String[] time = DateUtil.getHm().split(":");
				int order = (Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1])) / 5;
				SharedPreferences share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
				String sport = share.getString(ped.getUuid() + ped.getDatestring() + "sport_half", "");

				if (!TextUtils.isEmpty(sport)) {
					String[] stepsArray = sport.split(",");
					int steps = Integer.parseInt(stepsArray[order / 6]);
					if (tmpSteps == 0) {
						tmpSteps = ped.getTotalsteps();
					}
					steps += ped.getTotalsteps() - tmpSteps;
					Log.i(TAG, "steps222222222" + steps);
					tmpSteps = ped.getTotalsteps();
					stepsArray[order / 6] = steps + "";
					String stepsArrays = "";
					for (int i = 0; i < stepsArray.length; i++) {
						if (i != stepsArray.length - 1) {
							stepsArrays = stepsArrays + stepsArray[i] + ",";
						} else {
							stepsArrays = stepsArrays + stepsArray[i] + "";
						}
					}
					Log.i("chart", "stepsArrays = " + stepsArrays);
					share.edit().putString(ped.getUuid() + ped.getDatestring() + "sport_half", stepsArrays).commit();
				} else {

				}

			}
		} catch (DbException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取总步数
	 * 
	 * @param datas
	 * @param ped
	 */
	private static void parserStep(ArrayList<Byte> datas, PedometerModel ped) {
		byte[] bytes = new byte[4];
		bytes[0] = datas.get(8);
		bytes[1] = datas.get(9);
		bytes[2] = datas.get(10);
		bytes[3] = datas.get(11);
		ped.setTotalsteps(byteArrayToInt(bytes));
	}

	/**
	 * 解析日期
	 * 
	 * @param datas
	 * @param ped
	 */
	@SuppressLint("NewApi")
	public static PedometerModel parserDate(ArrayList<Byte> datas, Context context, BluetoothGatt gatt, Boolean isHostory, PedometerModel ped) {
		byte[] bytes = new byte[2];
		bytes[0] = datas.get(4);
		bytes[1] = datas.get(5);
		Calendar c = Calendar.getInstance();
		c.set(byteArrayToInt(bytes), formatToInt(datas.get(6)) - 1, formatToInt(datas.get(7)));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		String defaultStartDate = sdf.format(c.getTime()); // 格式化前一天
		ped.setDatestring(defaultStartDate);
		SharedPreferences share = context.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
		if (isHostory) {
			int state = DateUtil.compare_date(defaultStartDate, DateUtil.getCurrentDate());
			if (state == -1) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				try {
					share.edit().putString(gatt.getDevice().getAddress() + "hostory", df.format(DateUtil.dateAddDay(df.parse(defaultStartDate), 1))).commit();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (state == 0) {
				share.edit().putBoolean(gatt.getDevice().getAddress() + DateUtil.getCurrentDate() + "istongbu", true).commit();
			}

		}

		String[] year = defaultStartDate.split("-");
		// ped.setYearweek(year[0] + "/"
		// + DateUtil.getYearWeek(DateUtil.getWeek(defaultStartDate) + "")
		// + "");
		ped.setYearweek(year[0] + "/" + DateUtil.getYearWeek(defaultStartDate) + "");
		ped.setYearmonth(DateUtil.getYearMouth(defaultStartDate));
		return ped;
	}

	private static int formatToInt(Byte b) {
		String sHH = FormatTransfer.toHexString(b);
		int iHH = Integer.parseInt(sHH, 16);
		return iHH;
	}

	/**
	 * int到byte[]
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		// 由高位到低位
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	/**
	 * byte[]转int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < bytes.length; i++) {
			int shift = (bytes.length - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	/**
	 * ArrayListbyte转int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int byteArrayListToInt(ArrayList<Byte> bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < bytes.size(); i++) {
			int shift = (bytes.size() - 1 - i) * 8;
			Log.i("test", "shift = " + shift);
			value += (bytes.get(i) & 0x000000FF) << shift;// 往高位游
			
			Log.i("test", "value = " + value + ",  by = " + bytes.get(i) + ",  shift = " + shift);
		}
		return value;
	}

	public static String printHexString(ArrayList<Byte> datas) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < datas.size(); i++) {
			if (i != 0 && i % 4 == 0) {
				buffer.append(" ");
			}
			if (i != 0 && i % 20 == 0) {
				buffer.append("\n");
			}
			String hex = Integer.toHexString(datas.get(i) & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			buffer.append(hex);
		}
		return buffer.toString();
	}
	/*
	 * // 取出当前5分钟的具体数据。 - (NSInteger)getDataWithIndex:(int)index withType:(SportsModelType)type { if (type != SportsModelSleep) { if (self.sportsArray && self.sportsArray.count > 0) { for (int i = 0; i < self.sportsArray.count; i++) { SportsModel *model = self.sportsArray[i]; if (index <= model.currentOrder) { switch
	 * (type) { case SportsModelSteps: { return model.steps; } break; case SportsModelCalories: { return model.calories; } break; case SportsModelDistance: { return model.distance; } break;
	 * 
	 * default: break; } } }
	 * 
	 * return 0; } else { return 0; } } else { if (index < 144) { if (self.lastSleepArray && self.lastSleepArray.count) { for (int i = 0; i < self.lastSleepArray.count; i++) { SleepModel *model = self.lastSleepArray[i]; if (index + 144 <= model.currentOrder) { return model.sleepState; } }
	 * 
	 * return 3; } else { return 3; } } else { if (self.sleepArray && self.sleepArray.count > 0) { for (int i = 0; i < self.sleepArray.count; i++) { SleepModel *model = self.sleepArray[i]; if (index - 144 <= model.currentOrder) { return model.sleepState; } }
	 * 
	 * return 3; } else { return 3; } } }
	 */
}// end of processBleProfile}
