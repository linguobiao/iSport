package com.cn.zhihengchuang.walkbank.ble;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.cn.zhihengchuang.walkbank.activity.MyApp;
import com.cn.zhihengchuang.walkbank.entity.PedometerModel;
import com.cn.zhihengchuang.walkbank.entity.SleepModel;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.DateUtil;
import com.cn.zhihengchuang.walkbank.util.DbUtils;
import com.cn.zhihengchuang.walkbank.util.FormatTransfer;
import com.cn.zhihengchuang.walkbank.util.SystemConfig;
import com.cn.zhihengchuang.walkbank.util.Tools;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class OldParserLoader {
	private final static String TAG = BleService.class.getSimpleName();
	public static final int DEEPSLEEP = 800;
	public static final int LIGHTSLEEP = 1600;
	public static final int VERYLIGHTSLEEP = 3500;

	public void processData(final byte data[]) {
		if (data == null)
			return;

		int length = data.length;
		Log.i(TAG, "length:" + length);
		if (length == 6) {
			// checksum
			// this.processDataChecksum(data);
		} else if (length == 4) {
			/*
			 * this.processDataWirte(data); this.processNoData(data);
			 * this.processDataDisconnect(data);
			 * this.processAlarmDataWrite(data);
			 * this.processReminderDataWrite(data);
			 * this.processWearInfoDataWrite(data);
			 * this.processClearDataWrite(data); this.processTestWrite(data);
			 */
		} else if (length == 20) {
			// Log.d("processData------->mIsAutoConnect:" + mIsAutoConnect);
			// if (this.mIsAutoConnect) {
			// 获取到配置文件信息，开始处理
			// Log.d("mHandlerSetting 1");
			// CheckProfile.processBleProfile(this.mBleService, this, data);
			// } else if (this.mHandlerSetting != null) {
			// Log.d("mHandlerSetting 2");
			// Message msg = new Message();
			// msg.what = BleController.WHAT_PROFILE_READ;
			// msg.obj = data;
			// this.mHandlerSetting.sendMessage(msg);
			// }
		} else {
			// Log.d("processData---------->" +
			// FormatTransfer.byte2bits(data[0]));
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static synchronized boolean processDataUpload(Context context,
			BluetoothGatt gatt, ArrayList<Byte> mCache, int mChecksum,
			String currentType) {
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE);
		if (mCache == null)
			return true;

		int clength = mCache.size();
		/*if (clength < mChecksum) {
			
			 * Intent intent = new Intent(Constants.UPDATE_OK);
			 * intent.putExtra("date",
			 * Tools.defaultDateFormat(Calendar.getInstance().getTime()));
			 * context.sendBroadcast(intent);
			 
			Log.i(TAG, "processDataUpload    invaild data length:" + clength);
			return false;
		}*/

		byte[] checkDatas = new byte[2];
		for (int i = mChecksum - 2; i < mChecksum; i++) {
			checkDatas[i - mChecksum + 2] = mCache.get(i);
		}

		short checkData = FormatTransfer.lBytesToShort(checkDatas);
		short sum = 0;
		for (int i = 0; i < mChecksum - 2; i++) {
			sum += FormatTransfer.byteToShort(mCache.get(i));
		}

		// BleService.dataList.add("历史数据:" + System.currentTimeMillis());

		if (SystemConfig.TYPEW240.equals(currentType)) {
			if (checkData != sum) {
				/*
				 * Intent intent = new Intent(Constants.UPDATE_OK);
				 * intent.putExtra("date",
				 * Tools.defaultDateFormat(Calendar.getInstance().getTime()));
				 * context.sendBroadcast(intent);
				 */
				return false;
			}
		} else if (SystemConfig.TYPEP118.equals(currentType)) {
		}

		mChecksum -= 2;
		byte[] realData = new byte[mChecksum];
		for (int i = 0; i < mChecksum; i++)
			realData[i] = mCache.get(i);

		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < realData.length; i++) {
		// sb.append(FormatTransfer.toHexString(realData[i]) + ",");
		// }
		// BleService.dataList.add(sb.toString());

		int length = realData.length;
		Log.i(TAG, "processDataUpload----------->length:" + length);
		Log.i(TAG, "processDataUpload----------->Checksum:" + mChecksum);

		int realLength = mChecksum;
		Log.i(TAG, "processDataUpload----------->realLength:" + realLength);
		// byte[] realData = new byte[realLength];
		// System.arraycopy(data, 0, realData, 0, realLength);

		// 锟斤拷取�?2锟斤拷锟街斤�?实时锟斤拷锟�?
		// Total Steps (4bytes)
		// Total Calories (4bytes)
		// Total Distance (4bytes)
		byte[] totalSteps = new byte[4];
		System.arraycopy(realData, 0, totalSteps, 0, 4);
		int tSteps = FormatTransfer.lBytesToInt(totalSteps);
		// Log.d(bytesToHexString(totalSteps) + "<---tSteps--------->" +
		// tSteps);

		// BleService.mLogList.add(Tools.defaultLongDateFormat(new Date())
		// + ": TotalSteps:" + bytesToHexString(totalSteps));
		// BleService.dataList.add(Tools.defaultLongDateFormat(new Date())
		// + ": TotalSteps:" + tSteps);

		byte[] totalCalories = new byte[4];
		System.arraycopy(realData, 4, totalCalories, 0, 4);
		int tCalories = FormatTransfer.lBytesToInt(totalCalories);
		/*
		 * Log.d(bytesToHexString(totalCalories) + "<---tCalories--------->" +
		 * tCalories);
		 */

		// BleService.mLogList.add(Tools.defaultLongDateFormat(new Date())
		// + ": TotalCalories:" + bytesToHexString(totalCalories));
		// BleService.dataList.add(Tools.defaultLongDateFormat(new Date())
		// + ": totalCalories:" + tCalories);

		byte[] totalDistance = new byte[4];
		System.arraycopy(realData, 8, totalDistance, 0, 4);
		// 锟斤拷锟斤拷4锟斤�?0锟斤拷为锟斤拷位锟斤拷锟斤拷锟斤拷锟斤拷锟斤�?
		// int tDistance = FormatTransfer.lBytesToInt(totalDistance) * 10;
		int tDistance = FormatTransfer.lBytesToInt(totalDistance);
		PedometerModel ped = new PedometerModel();

		/*
		 * Log.d(bytesToHexString(totalDistance) + "<---tDistance--------->" +
		 * tDistance);
		 */
		/*
		 * BleService.mLogList.add(Tools.defaultLongDateFormat(new Date()) +
		 * ": steps="+tSteps+", calories="+tCalories+",distance="+tDistance);
		 */

		// BleService.mLogList.add(Tools.defaultLongDateFormat(new Date())
		// + ": TotalDistances:" + bytesToHexString(totalDistance));
		// BleService.dataList.add(Tools.defaultLongDateFormat(new Date())
		// + ": TotalDistances:" + tDistance);

		java.util.Calendar cale = java.util.Calendar.getInstance();
		String nowDate = Tools.defaultDateFormat(cale.getTime());
		ped.setDatestring(nowDate);
		ped.setTotalsteps(tSteps);
		ped.setTotalcalories(tCalories);
		ped.setTotaldistance(tDistance);
		String[] year = nowDate.split("-");
		ped.setYearweek(year[0] + "/"
				+ DateUtil.getYearWeek(DateUtil.getWeek(nowDate) + "") + "");
		ped.setYearmonth(DateUtil.getYearMouth(nowDate));
		ped.setUuid(gatt.getDevice().getAddress());
		Log.i(TAG, "nowDate--------->" + nowDate);
		DbUtils db = DbUtils.create(context);
		db.configAllowTransaction(true);
		db.configDebug(true);
		try {
			// PedometerModel pedometer =
			// db.findFirst(Selector.from(PedometerModel.class).where("datestring","=",pedometer.getDatestring()));
			WhereBuilder builder = WhereBuilder.b("datestring", "==",
					ped.getDatestring()).and("uuid", "==", ped.getUuid());
			db.delete(PedometerModel.class, builder);
			db.saveBindingId(ped);
			PedometerModel pedometer = db.findFirst(Selector.from(
					PedometerModel.class).where(builder));
			if (pedometer != null) {
				Log.i(TAG, "pedometer." + pedometer.getTotalsteps());
				// Log.i(TAG, "pedometer." + formatToInt(datas.get(7)));
				// Log.i(TAG, "pedometer." + pedometer.getDetailcalories());
				/*
				 * Intent intent = new Intent(Constants.UPDATE_OK);
				 * intent.putExtra("date", ped.getDatestring());
				 * context.sendBroadcast(intent);
				 */
				/*
				 * BusProvider.getInstance().post(new
				 * DataRefreshEvent(pedometer.getDatestring())); Log.i(TAG,
				 * "fale.");
				 */

			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * HalfHourDAO hDao = new HalfHourDAO(mBleService); SleepDataDAO sDao =
		 * new SleepDataDAO(mBleService);
		 */
		// hDao.deleteAll();

		// int minute = cale.get(Calendar.MINUTE);
		// if(minute<30)
		// cale.add(Calendar.MINUTE, -(minute+30));
		// else
		// cale.add(Calendar.MINUTE, -minute);

		// if (SystemConfig.TYPEP118.equals(currentType)) {
		// int minute = cale.get(Calendar.MINUTE);
		// if (minute < 30)
		// cale.add(Calendar.MINUTE, -(minute + 30));
		// else
		// cale.add(Calendar.MINUTE, -minute);
		// } else if (SystemConfig.TYPEW240.equals(currentType)) {
		int m = cale.get(Calendar.MINUTE);
		cale.add(Calendar.MINUTE, -((m % 5) + 5));
		// }
		cale.set(Calendar.SECOND, 0);

		// int copyIndex = 12;
		String date = null;
		String minite = null;
		int oStep = 0;
		int oCalories = 0;
		int oDistance = 0;

		int x = 0, y = 0, z = 0;
		int flag = -1; // 判断是运动数据还是睡眠数据
		if (realLength >= 18) {
			// this.sendMessage(OPERATE_DATA_COUNT, realLength);
			String stepTimes = "";
			String steps = "";
			String sleepTimes = "";
			String sleepState = "";
			// 先进先出
			if (SystemConfig.TYPEW240.equals(currentType)) {

				for (int i = 12; i <= realLength - 6; i += 6) {
					// this.sendMessage(OPERATE_DATA_CURRENT, 1);
					flag = -1;
					Log.i(TAG, "I---------->" + i);
					// Log.d("I---------->" + i);
					date = null;
					SimpleDateFormat df = new SimpleDateFormat("HH:mm");
					minite = df.format(cale.getTime());
					SimpleDateFormat dfy = new SimpleDateFormat("yyyy-MM-dd");
					date = dfy.format(cale.getTime());

					byte[] oneSteps = new byte[2];
					// System.arraycopy(realData, i-1, oneSteps, 0, 2);
					System.arraycopy(realData, i, oneSteps, 0, 2);
					//
					// hDao.remove(nowDate, currentType);
					// sDao.remove(nowDate);

					if (oneSteps[1] >= 0) {
						// copyIndex += 2;
						flag = 0;
						oStep = FormatTransfer.lBytesToShort(oneSteps);
						Log.i(TAG, date + minite + "<--oStep--->"
								+ bytesToHexString(oneSteps) + ">------->"
								+ oStep);
						// hDao.insert(date,
						// String.valueOf(oStep < 0 ? 0 : oStep),
						// HalfHourField.TYPE_STEPS, currentType);
						// BleService.dataList.add(date + ":步数:" + oStep);
					} else {
						flag = 1;
						oneSteps[1] = (byte) (0x7f & oneSteps[1]);
						x = FormatTransfer.lBytesToShort(oneSteps);
						// hDao.insert(date, "0", HalfHourField.TYPE_STEPS,
						// currentType);
						// BleService.dataList.add(date + ":x差值:" + x);
					}

					// cale.add(java.util.Calendar.MINUTE, -30);

					// date = null;
					// date = Tools.defaultLongDateFormat(cale.getTime());
					byte[] oneCalories = new byte[2];
					System.arraycopy(realData, i + 2, oneCalories, 0, 2);
					// copyIndex += 2;
					if (oneCalories[1] >= 0) {
						oCalories = FormatTransfer.lBytesToShort(oneCalories);
						Log.i(TAG, date + "<--oCalories---->"
								+ bytesToHexString(oneCalories) + ">------>"
								+ oCalories);
						// hDao.insert(date,
						// String.valueOf(oCalories < 0 ? 0 : oCalories),
						// HalfHourField.TYPE_CALORIES, currentType);
						// BleService.dataList.add(date + ":卡路里:" + oCalories);
					} else {
						oneCalories[1] = (byte) (0x7f & oneCalories[1]);
						y = FormatTransfer.lBytesToShort(oneCalories);
						// hDao.insert(date, "0", HalfHourField.TYPE_CALORIES,
						// currentType);
						// BleService.dataList.add(date + ":Y差值:" + y);
					}
					// cale.add(java.util.Calendar.MINUTE, -30);

					// date = null;
					// date = Tools.defaultLongDateFormat(cale.getTime());
					byte[] oneDistance = new byte[2];
					System.arraycopy(realData, i + 4, oneDistance, 0, 2);
					// System.arraycopy(realData, i-5, oneDistance, 0, 2);
					// copyIndex += 2;
					if (oneDistance[1] >= 0) {

						oDistance = FormatTransfer.lBytesToShort(oneDistance) * 10;
						Log.i(TAG, date + "<--oDistance----->"
								+ bytesToHexString(oneDistance) + ">----->"
								+ oDistance);
						// hDao.insert(date,
						// String.valueOf(oDistance < 0 ? 0 : oDistance),
						// HalfHourField.TYPE_DISTANCE, currentType);
						// BleService.dataList.add(date + ":距离:" + oDistance);
					} else {
						oneDistance[1] = (byte) (0x7f & oneDistance[1]);
						z = FormatTransfer.lBytesToShort(oneDistance);
						// hDao.insert(date, "0", HalfHourField.TYPE_DISTANCE,
						// currentType);
						// BleService.dataList.add(date + ":Z差值:" + z);
					}
					if (flag == 1) {
						int num = x + y + z;
						Log.i(TAG, "num>>>>>>" + num);
						if (num >= 0 && num < DEEPSLEEP) {
							num = 128;
							Log.i(TAG, date + minite + "<--深度睡眠---->");
						} else if (num >= DEEPSLEEP && num < LIGHTSLEEP) {
							num = 129;
							Log.i(TAG, date + minite + "<--浅度睡眠---->");
						} else if (num >= LIGHTSLEEP && num < VERYLIGHTSLEEP) {
							num = 130;
							Log.i(TAG, date + minite + "<--及浅度睡眠---->");
						} else if (num >= VERYLIGHTSLEEP) {
							num = 131;
							Log.i(TAG, date + minite + "<--醒着的---->");
						}
						String sleepsTime = "";
						String sleepstate = "";
						for (int s = 0; s < 288; s++) {
							if (s == 287) {
								sleepsTime = sleepsTime + s + "";
							} else {
								sleepsTime = sleepsTime + s + ",";
							}

						}
						String time[] = minite.split(":");
						int order = (Integer.parseInt(time[0]) * 60 + Integer
								.parseInt(time[1])) / 5;
						String sleepHalf = share.getString(ped.getUuid() + date
								+ "sleep_half", "");
						share.edit()
								.putString(
										ped.getUuid() + date
												+ "sleep_half_time", sleepsTime)
								.commit();
						if (TextUtils.isEmpty(sleepHalf)) {

							for (int s = 0; s < 288; s++) {
								if (s == 287) {
									sleepstate = sleepstate + "0";
								} else {
									sleepstate = sleepstate + "0,";
								}

							}
						} else {
							String[] sleep = sleepHalf.split(",");
							for (int p = 0; p < sleep.length; p++) {
								if (p == order) {
									sleep[p] = num + "";
								}
							}
							for (int s = 0; s < sleep.length; s++) {
								if (s == 287) {
									sleepstate = sleepstate + sleep[s] + "";
								} else {
									sleepstate = sleepstate + sleep[s] + ",";
								}

							}
						}
						Log.i(TAG, "date>>>" + date + "sleepsState>>>"
								+ sleepstate);
						String[] sleep = sleepHalf.split(",");
						/*
						 * if
						 * (!TextUtils.isEmpty(sleepHalf)&&!"0".equals(sleep[order
						 * ])) { share.edit() .putString(ped.getUuid() + date +
						 * "sleep_half", sleepstate).commit(); }else
						 * if(TextUtils.isEmpty(sleepHalf)){ share.edit()
						 * .putString(ped.getUuid() + date + "sleep_half",
						 * sleepstate).commit(); }
						 */
						share.edit()
								.putString(ped.getUuid() + date + "sleep_half",
										sleepstate).commit();

						// sDao.insert(date, num + "",currentType);
						// hDao.insert(date, "0", "0", "0", currentType);
					} else {
						if (date.equals(nowDate)) {
							String time[] = minite.split(":");
							int order = (Integer.parseInt(time[0]) * 60 + Integer
									.parseInt(time[1])) / 5;
							stepTimes = stepTimes + order + ",";
							steps = steps + oStep + ",";
						}

						/*
						 * hDao.insert(date, String.valueOf(oStep < 0 ? 0 :
						 * oStep), String.valueOf(oCalories < 0 ? 0 :
						 * oCalories), String.valueOf(oDistance < 0 ? 0 :
						 * oDistance), currentType);
						 */
						// if (SystemConfig.TYPEW240.equals(currentType)) {
						// sDao.insert(date, String.valueOf(-1));
						// }
					}
					// if (SystemConfig.TYPEP118.equals(currentType)) {
					// cale.add(java.util.Calendar.MINUTE, -30);
					// } else if (SystemConfig.TYPEW240.equals(currentType)) {
					cale.add(java.util.Calendar.MINUTE, -5);
					// }
					// byte[] allBytes = new byte[6];
					// allBytes[0] = oneSteps[0];
					// allBytes[1] = oneSteps[1];
					// allBytes[2] = oneCalories[0];
					// allBytes[3] = oneCalories[1];
					// allBytes[4] = oneDistance[0];
					// allBytes[5] = oneDistance[1];
					// BleService.mLogList.add(Tools.defaultLongDateFormat(new
					// Date())
					// + ": Group:" + bytesToHexString(allBytes));
				}
				String[] steptimes = stepTimes.split(",");
				String[] mSteps = steps.split(",");
				Log.i(TAG, "mSteps>>>" + steps);
				int fiveSteps[] = new int[288];
				for (int t = 0; t < steptimes.length; t++) {
					if (!TextUtils.isEmpty(steptimes[t])) {
						fiveSteps[Integer.parseInt(steptimes[t])] = Integer
								.parseInt(mSteps[t]);
					}

				}
				int halfStep = 0;
				String halfSteps = "0,";
				String halfTime = "0,";
				String sleepsTime = "";
				String sleepsState = "";
				for (int s = 0; s < fiveSteps.length; s++) {
					if (s % 6 == 0) {
						halfStep = fiveSteps[s];
					} else if (s % 6 == 5) {
						halfStep = halfStep + fiveSteps[s];
						if (s == 287) {
							halfTime = halfTime + s + "";
							halfSteps = halfSteps + halfStep + "";
						} else {
							halfTime = halfTime + s + ",";
							halfSteps = halfSteps + halfStep + ",";
						}
					} else {
						halfStep = halfStep + fiveSteps[s];
					}

				}

				Log.i(TAG, "ped.getDatestring()>>>" + ped.getDatestring());
				share.edit()
						.putString(
								ped.getUuid() + ped.getDatestring()
										+ "sport_half_time", halfTime).commit();
				share.edit()
						.putString(
								ped.getUuid() + ped.getDatestring()
										+ "sport_half", halfSteps).commit();
			} else if (SystemConfig.TYPEP118.equals(currentType)) {
				// 后进先出
				for (int i = realLength - 6; i >= 12; i -= 6) {
					// this.sendMessage(OPERATE_DATA_CURRENT, 1);
					flag = -1;
					Log.i(TAG, "I---------->" + i);
					date = null;
					SimpleDateFormat df = new SimpleDateFormat("HH:mm");
					minite = df.format(cale.getTime());
					SimpleDateFormat dfy = new SimpleDateFormat("yyyy-MM-dd");
					date = dfy.format(cale.getTime());
					byte[] oneSteps = new byte[2];
					// System.arraycopy(realData, i-1, oneSteps, 0, 2);
					System.arraycopy(realData, i, oneSteps, 0, 2);
					//
					// hDao.remove(nowDate, currentType);
					// sDao.remove(nowDate);

					if (oneSteps[1] >= 0) {
						// copyIndex += 2;
						flag = 0;
						oStep = FormatTransfer.lBytesToShort(oneSteps);
						Log.i(TAG, date + "<--oStep--->" + oneSteps
								+ ">------->" + oStep);
						// hDao.insert(date,
						// String.valueOf(oStep < 0 ? 0 : oStep),
						// HalfHourField.TYPE_STEPS, currentType);
						// BleService.dataList.add(date + ":步数:" + oStep);
					} else {
						flag = 1;
						oneSteps[1] = (byte) (0x7f & oneSteps[1]);
						x = FormatTransfer.lBytesToShort(oneSteps);
						// hDao.insert(date, "0", HalfHourField.TYPE_STEPS,
						// currentType);
						// BleService.dataList.add(date + ":x差值:" + x);
					}

					// cale.add(java.util.Calendar.MINUTE, -30);

					// date = null;
					// date = Tools.defaultLongDateFormat(cale.getTime());
					byte[] oneCalories = new byte[2];
					System.arraycopy(realData, i + 2, oneCalories, 0, 2);
					// copyIndex += 2;
					if (oneCalories[1] >= 0) {
						oCalories = FormatTransfer.lBytesToShort(oneCalories);
						Log.i(TAG, date + "<--oCalories---->" + oneCalories
								+ ">------>" + oCalories);
						// hDao.insert(date,
						// String.valueOf(oCalories < 0 ? 0 : oCalories),
						// HalfHourField.TYPE_CALORIES, currentType);
						// BleService.dataList.add(date + ":卡路里:" + oCalories);
					} else {
						oneCalories[1] = (byte) (0x7f & oneCalories[1]);
						y = FormatTransfer.lBytesToShort(oneCalories);
						// hDao.insert(date, "0", HalfHourField.TYPE_CALORIES,
						// currentType);
						// BleService.dataList.add(date + ":Y差值:" + y);
					}

					// cale.add(java.util.Calendar.MINUTE, -30);

					// date = null;
					// date = Tools.defaultLongDateFormat(cale.getTime());
					byte[] oneDistance = new byte[2];
					System.arraycopy(realData, i + 4, oneDistance, 0, 2);
					// System.arraycopy(realData, i-5, oneDistance, 0, 2);
					// copyIndex += 2;
					if (oneDistance[1] >= 0) {

						oDistance = FormatTransfer.lBytesToShort(oneDistance) * 10;
						Log.i(TAG, date + "<--oDistance----->" + oneDistance
								+ ">----->" + oDistance);
						// hDao.insert(date,
						// String.valueOf(oDistance < 0 ? 0 : oDistance),
						// HalfHourField.TYPE_DISTANCE, currentType);
						// BleService.dataList.add(date + ":距离:" + oDistance);
					} else {
						oneDistance[1] = (byte) (0x7f & oneDistance[1]);
						z = FormatTransfer.lBytesToShort(oneDistance);
						// hDao.insert(date, "0", HalfHourField.TYPE_DISTANCE,
						// currentType);
						// BleService.dataList.add(date + ":Z差值:" + z);
					}
					// if (flag == 1) {
					// int num = x + y + z;
					// sDao.insert(date, num + "");
					// } else {
					// if (SystemConfig.TYPEW240.equals(currentType)) {
					// sDao.insert(date, String.valueOf(-1));
					// }
					// }
					if (flag == 1) {
						int num = x + y + z;
						Log.i(TAG, "num>>>>>>" + num);
						if (num >= 0 && num < DEEPSLEEP) {
							num = 128;
							Log.i(TAG, date + minite + "<--深度睡眠---->");
						} else if (num >= DEEPSLEEP && num < LIGHTSLEEP) {
							num = 129;
							Log.i(TAG, date + minite + "<--浅度睡眠---->");
						} else if (num >= LIGHTSLEEP && num < VERYLIGHTSLEEP) {
							num = 130;
							Log.i(TAG, date + minite + "<--及浅度睡眠---->");
						} else if (num >= VERYLIGHTSLEEP) {
							num = 131;
							Log.i(TAG, date + minite + "<--醒着的---->");
						}
						String sleepsTime = "";
						String sleepstate = "";
						for (int s = 0; s < 288; s++) {
							if (s == 287) {
								sleepsTime = sleepsTime + s + "";
							} else {
								sleepsTime = sleepsTime + s + ",";
							}

						}
						String time[] = minite.split(":");
						int order = (Integer.parseInt(time[0]) * 60 + Integer
								.parseInt(time[1])) / 5;
						String sleepHalf = share.getString(ped.getUuid() + date
								+ "sleep_half", "");
						share.edit()
								.putString(
										ped.getUuid() + date
												+ "sleep_half_time", sleepsTime)
								.commit();
						if (TextUtils.isEmpty(sleepHalf)) {

							for (int s = 0; s < 288; s++) {
								if (s == 287) {
									sleepstate = sleepstate + "0";
								} else {
									sleepstate = sleepstate + "0,";
								}

							}
						} else {
							String[] sleep = sleepHalf.split(",");
							for (int p = 0; p < sleep.length; p++) {
								if (p == order) {
									sleep[p] = num + "";
								}
							}
							for (int s = 0; s < sleep.length; s++) {
								if (s == 287) {
									sleepstate = sleepstate + sleep[s] + "";
								} else {
									sleepstate = sleepstate + sleep[s] + ",";
								}

							}
						}
						share.edit()
						.putString(ped.getUuid() + date + "sleep_half",
								sleepstate).commit();
						// sDao.insert(date, num + "",currentType);
						// hDao.insert(date, "0", "0", "0", currentType);
					} else {
						if (date.equals(nowDate)) {
							String time[] = minite.split(":");
							int order = (Integer.parseInt(time[0]) * 60 + Integer
									.parseInt(time[1])) / 5;
							stepTimes = stepTimes + order + ",";
							steps = steps + oStep + ",";
						}
						/*
						 * hDao.insert(date, String.valueOf(oStep < 0 ? 0 :
						 * oStep), String.valueOf(oCalories < 0 ? 0 :
						 * oCalories), String.valueOf(oDistance < 0 ? 0 :
						 * oDistance), currentType);
						 */
						// if (SystemConfig.TYPEW240.equals(currentType)) {
						// sDao.insert(date, String.valueOf(-1));
						// }
					}

					// if (SystemConfig.TYPEP118.equals(currentType)) {
					// cale.add(java.util.Calendar.MINUTE, -30);
					// } else if (SystemConfig.TYPEW240.equals(currentType)) {
					cale.add(java.util.Calendar.MINUTE, -5);
					// }
					// byte[] allBytes = new byte[6];
					// allBytes[0] = oneSteps[0];
					// allBytes[1] = oneSteps[1];
					// allBytes[2] = oneCalories[0];
					// allBytes[3] = oneCalories[1];
					// allBytes[4] = oneDistance[0];
					// allBytes[5] = oneDistance[1];
					// BleService.mLogList.add(Tools.defaultLongDateFormat(new
					// Date())
					// + ": Group:" + bytesToHexString(allBytes));
				}
				String[] steptimes = stepTimes.split(",");
				String[] mSteps = steps.split(",");
				Log.i(TAG, "mSteps>>>" + steps);
				int fiveSteps[] = new int[288];
				for (int t = 0; t < steptimes.length; t++) {
					if (!TextUtils.isEmpty(steptimes[t])) {
						fiveSteps[Integer.parseInt(steptimes[t])] = Integer
								.parseInt(mSteps[t]);
					}

				}
				int halfStep = 0;
				String halfSteps = "0,";
				String halfTime = "0,";
				for (int s = 0; s < fiveSteps.length; s++) {
					if (s % 6 == 0) {
						halfStep = fiveSteps[s];
					} else if (s % 6 == 5) {
						halfStep = halfStep + fiveSteps[s];
						if (s == 287) {
							halfTime = halfTime + s + "";
							halfSteps = halfSteps + halfStep + "";
						} else {
							halfTime = halfTime + s + ",";
							halfSteps = halfSteps + halfStep + ",";
						}
					} else {
						halfStep = halfStep + fiveSteps[s];
					}

				}
				Log.i(TAG, "halfStep>>>" + halfSteps);
				Log.i(TAG, "ped.getDatestring()>>>" + ped.getDatestring());
				share.edit()
						.putString(
								ped.getUuid() + ped.getDatestring()
										+ "sport_half_time", halfTime).commit();
				share.edit()
						.putString(
								ped.getUuid() + ped.getDatestring()
										+ "sport_half", halfSteps).commit();
			}
		}
		// hDao.close();
		// this.sendMessage(OPERATE_DATA_SUCCESS, 0);
		// BleService.dataList.add("历史数据:" + System.currentTimeMillis());
		// 通知刷锟斤拷
		Intent intent = new Intent(Constants.UPDATE_OK);
		intent.putExtra("date", ped.getDatestring());
		intent.putExtra("isOld", true);
		context.sendBroadcast(intent);
		return true;
	}

	protected static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			stringBuilder.append(" ");
		}
		return stringBuilder.toString();
	}

}
