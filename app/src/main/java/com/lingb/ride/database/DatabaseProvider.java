package com.lingb.ride.database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lingb.global.Global;
import com.lingb.helper.CalendarHelper;
import com.lingb.ride.bean.Profile;
import com.lingb.ride.bean.Ride;

import android.content.Context;
import android.database.Cursor;

public class DatabaseProvider {
	
//	private static final String TAG = "DatabaseProvider";
	
	
	/**
	 * query profile
	 * @param context
	 * @return
	 */
	public static List<Profile> queryProfile(Context context) {
		List<Profile> profileList = new ArrayList<Profile>();
		
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
		databaseAdapter.openDatabase();
		Cursor cursor = databaseAdapter.query_profile();
		if (cursor.moveToFirst()){
			do {
				String name = cursor.getString(0);
				int unit = cursor.getInt(1);
				int gender = cursor.getInt(2);
				double height = cursor.getDouble(3);
				double weight = cursor.getDouble(4);
				String birthday = cursor.getString(5);
				int size = cursor.getInt(6);
				int id = cursor.getInt(7);
				
				Profile profile = new Profile();
				profile.setName(name);
				profile.setUnit(unit);
				profile.setGender(gender);
				profile.setHeight(height);
				profile.setWeight(weight);
				profile.setBirthday(birthday);
				profile.setSize(size);
				profile.setID(id);
				
				profileList.add(profile);
			} while (cursor.moveToNext());
		}
		
		databaseAdapter.closeDatabase();
		
		return profileList;
	}
	
	
	/**
	 * query profile
	 * @param context
	 * @param nameQuery
	 * @return
	 */
	public static Profile queryProfile(Context context, String nameQuery) {
		Profile profile = null;
		
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
		databaseAdapter.openDatabase();
		Cursor cursor = databaseAdapter.query_profile(nameQuery);
		try {
			if (cursor != null && cursor.getCount() != 0){
				String name = cursor.getString(0);
				int unit = cursor.getInt(1);
				int gender = cursor.getInt(2);
				double height = cursor.getDouble(3);
				double weight = cursor.getDouble(4);
				String birthday = cursor.getString(5);
				int size = cursor.getInt(6);
				int id = cursor.getInt(7);
				
				profile = new Profile();
				profile.setName(name);
				profile.setUnit(unit);
				profile.setGender(gender);
				profile.setHeight(height);
				profile.setWeight(weight);
				profile.setBirthday(birthday);
				profile.setSize(size);
				profile.setID(id);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		databaseAdapter.closeDatabase();
		
		return profile;
	}
	
	
	/**
	 * insert profile
	 * @param context
	 * @param profile
	 */
	public static void insertProfile(Context context, Profile profile) {
		if (profile != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();

			databaseAdapter.insert_profile(
					profile.getName(), profile.getUnit(), 
					profile.getGender(), 
					profile.getHeight(), profile.getWeight(),
					profile.getBirthday(), profile.getSize());

			databaseAdapter.closeDatabase();
		}
	}
	
	/**
	 * update profile
	 * @param context
	 * @param profile
	 */
	public static void updateProfile(Context context, String oldName, Profile profile) {
		if (oldName != null && profile != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();

			databaseAdapter.update_profile(oldName, profile.getName(), profile.getUnit(), profile.getGender(), 
					profile.getHeight(), profile.getWeight(),
					profile.getBirthday(), profile.getSize());

			databaseAdapter.closeDatabase();
		}
	}
	
	
	
	/**
	 * 插入小时历史数据
	 * @param context
	 * @param ride
	 */
	public static void insertHistoryHour(Context context, Ride ride) {
		if (ride != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();

			Calendar date = CalendarHelper.setSecondFormat(ride.getDateTime());
			databaseAdapter.insert_history_hour(date, ride.getTotalTime(), ride.getTotalDistance(), ride.getSpeed(), ride.getSpeedMax(), ride.getSpeedMin(), ride.getCadence(), ride.getCadenceMax(), ride.getCadenceMin());
			
			databaseAdapter.closeDatabase();
		}
	}
	
	/**
	 * 更新小时历史数据
	 * @param context
	 * @param history
	 */
	public static void updateHistoryHour(Context context,int profileID,  Ride ride) {
		if (ride != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();

			Calendar date = CalendarHelper.setSecondFormat(ride.getDateTime());
			
			databaseAdapter.update_history_hour(date, ride.getTotalTime(), ride.getTotalDistance(), ride.getSpeed(), ride.getSpeedMax(), ride.getSpeedMin(), ride.getCadence(), ride.getCadenceMax(), ride.getCadenceMin());

			databaseAdapter.closeDatabase();
		}
	}
	
	
//	/**
//	 * 查询一个日期的历史小时数据
//	 * @param context
//	 * @param date
//	 * @return
//	 */
//	public static HistoryHour queryHistoryHour(Context context,int profileID,  Calendar date) {
//		
//		if (context != null && date != null) {
//			date = CalendarHelper.setHourFormat(date);
//			
//			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
//			// 打开数据库
//			databaseAdapter.openDatabase();
//			
//			Cursor cursor = databaseAdapter.query_history_hour(profileID, date);
//			try {
//				if (cursor.moveToFirst()){
//					
//					HistoryHour history = new HistoryHour();
//					Date _date = new Date();
//					try {
//						_date = Global.sdf_3.parse(cursor.getString(0));
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					Calendar _cal = Calendar.getInstance();
//					_cal.setTime(_date);
//					
//					history.setDate(_cal);
//					history.setStep(cursor.getInt(1));
//					history.setBurn(cursor.getDouble(2));
//					history.setSleepGrade(cursor.getInt(3));		
//						
//					databaseAdapter.closeDatabase();
//					return history;		
//				} 
//			} finally {
//				if (cursor != null) {
//					cursor.close();
//					cursor = null;
//				}
//			}
//			
//			databaseAdapter.closeDatabase();
//		}
//		
//		
//		return null;
//	}
	
	/**
	 * 查询一个时间段的历史日数据
	 * @param context
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List<Ride> queryHistoryHour( Context context, Calendar begin, Calendar end) {
		
		List<Ride> rideList = new ArrayList<Ride>();
		
		if (context != null && begin != null && end != null) {
			
			begin = CalendarHelper.setSecondFormat(begin);
			end = CalendarHelper.setSecondFormat(end);
			
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			// 打开数据库
			databaseAdapter.openDatabase();
			Cursor cursor = databaseAdapter.query_history_hour(begin, end);
			if (cursor.moveToFirst()){
				do {
					Ride ride = new Ride();
					Date _date = new Date();
					try {
						_date = Global.sdf_yyyy_MM_dd_HH_mm_ss.parse(cursor.getString(0));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Calendar _cal = Calendar.getInstance();
					_cal.setTime(_date);
					
					ride.setDateTime(_cal);
					ride.setTotalTime(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_TOTAL_TIME)));
					ride.setTotalDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_TOTAL_DISTANCE)));
					ride.setSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_SPEED)));
					ride.setSpeedMax(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_SPEED_MAX)));
					ride.setSpeedMin(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_SPEED_MIN)));
					ride.setCadence(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_CADENCE)));
					ride.setCadenceMax(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_CADENCE_MAX)));
					ride.setCadenceMin(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_CADENCE_MIN)));
					
					rideList.add(ride);
				} while (cursor.moveToNext());
			}
			
			databaseAdapter.closeDatabase();
		}
		
		return rideList;
	}
	/**
	 * 查询所有的历史日数据
	 * @param context
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List<Ride> queryHistoryAll( Context context) {
		
		List<Ride> rideList = new ArrayList<Ride>();
		
		if (context != null) {
			
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			// 打开数据库
			databaseAdapter.openDatabase();
			Cursor cursor = databaseAdapter.query_history_all();
			if (cursor.moveToFirst()){
				do {
					Ride ride = new Ride();
					Date _date = new Date();
					try {
						_date = Global.sdf_yyyy_MM_dd_HH_mm_ss.parse(cursor.getString(0));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Calendar _cal = Calendar.getInstance();
					_cal.setTime(_date);
					
					ride.setDateTime(_cal);
					ride.setTotalTime(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_TOTAL_TIME)));
					ride.setTotalDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_TOTAL_DISTANCE)));
					ride.setSpeed(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_SPEED)));
					ride.setSpeedMax(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_SPEED_MAX)));
					ride.setSpeedMin(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_SPEED_MIN)));
					ride.setCadence(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_CADENCE)));
					ride.setCadenceMax(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_CADENCE_MAX)));
					ride.setCadenceMin(cursor.getDouble(cursor.getColumnIndex(DatabaseAdapter.KEY_CADENCE_MIN)));
					
					rideList.add(ride);
				} while (cursor.moveToNext());
			}
			
			databaseAdapter.closeDatabase();
		}
		
		return rideList;
	}
	
	
	/**
	 * 删除一天的history hour数据
	 * @param context
	 * @param date
	 */
	public static void deleteHistoryOne(Context context, Calendar date) {
		if (date != null) {
			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
			databaseAdapter.openDatabase();
			
			databaseAdapter.delete_history_one(date);
			
			databaseAdapter.closeDatabase();
		}
	}
	
	
	public static void deleteHistoryAll(Context context) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
		databaseAdapter.openDatabase();
		
		databaseAdapter.delete_history_all();
		
		databaseAdapter.closeDatabase();
	}
//	public static void deleteAllTable(Context context) {
//		DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
//		databaseAdapter.openDatabase();
//
//		databaseAdapter.deleteAllData();
//
//		databaseAdapter.closeDatabase();
//	}
//	
//	
//	public static void deleteHistoryAfterNow(Context context, Calendar now) {
//		System.out.println("*******************************************delect");
//		DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
//		databaseAdapter.openDatabase();
//
//		databaseAdapter.delete_history_day_after_now(now);
//		databaseAdapter.delete_history_hour_after_now(now);
//
//		databaseAdapter.closeDatabase();
//	}
	
//	/**
//	 * 使用事务保存和更新小时数据
//	 * @param context
//	 * @param historyHourList
//	 * @param profileID
//	 */
//	public static void saveHistoryHour(Context context, List<HistoryHour> historyHourList, int profileID) {
//		if (profileID != -1 && historyHourList != null) {
//
//			DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
//			databaseAdapter.openDatabase();
//			SQLiteDatabase db = databaseAdapter.getSQLiteDatabase();
//			// 开始事务
//			db.beginTransaction();
//			try {
//				for (HistoryHour historyHour : historyHourList) {
//					if (historyHour != null) {
//						Cursor cursor = databaseAdapter.query_history_hour(profileID, historyHour.getDate());
//						try {
//							if (cursor.moveToFirst()) {
//								// 已经有这个小时的数据，则更新
//								Calendar date = CalendarHelper.setHourFormat(historyHour.getDate());
//								databaseAdapter.update_history_hour(profileID, date, historyHour.getStep(), historyHour.getBurn(), historyHour.getSleepGrade());
//
//							} else {
//								// 还没有这个小时的数据，则插入
//								Calendar date = CalendarHelper.setHourFormat(historyHour.getDate());
//								databaseAdapter.insert_history_hour(profileID, date, historyHour.getStep(), historyHour.getBurn(), historyHour.getSleepGrade());
//							}
//						} finally {
//							if (cursor != null) {
//								cursor.close();
//								cursor = null;
//							}
//						}
//					}
//				}
//				// 事务成功，提交事务
//				db.setTransactionSuccessful();
//			} finally {
//				// 结束事务
//				db.endTransaction();
//				databaseAdapter.closeDatabase();
//			}
//		}
//	}
}
