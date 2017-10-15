package com.lingb.ride.database;

import java.util.Calendar;

import com.lingb.global.Global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseAdapter {
	private static String TAG = "DatabaseAdapter";
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "ride.db";
	////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////

	public static final String TABLE_RIDE_PROFLIE = "TABLE_RIDE_PROFLIE";
	public static final String TABLE_RIDE = "TABLE_RIDE";
	
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	private static final String KEY_ROWID = "_id";
	
	public static final String KEY_NAME = "KEY_NAME";
	public static final String KEY_UNIT = "KEY_UNIT";
	public static final String KEY_GENDER = "KEY_GENDER";
	public static final String KEY_HEIGHT = "KEY_HEIGHT";
	public static final String KEY_WEIGHT = "KEY_WEIGHT";
	public static final String KEY_BIRTHDAY = "KEY_BIRTHDAY";
	public static final String KEY_SIZE = "KEY_SIZE";
	public static final String KEY_PROFILE_ID = "KEY_PROFILE_ID";
	
	
	public static final String KEY_DATE = "KEY_DATE";
	public static final String KEY_DATE_LONG = "KEY_DATE_LONG";
	public static final String KEY_DATETIME = "KEY_DATETIME";
	public static final String KEY_DATETIME_LONG = "KEY_DATETIME_LONG";
	public static final String KEY_TOTAL_TIME = "KEY_TOTAL_TIME";
	public static final String KEY_TOTAL_DISTANCE = "KEY_TOTAL_DISTANCE";
	public static final String KEY_SPEED = "KEY_SPEED";
	public static final String KEY_SPEED_MAX = "KEY_SPEED_MAX";
	public static final String KEY_SPEED_MIN = "KEY_SPEED_MIN";
	public static final String KEY_CADENCE = "KEY_CADENCE";
	public static final String KEY_CADENCE_MAX = "KEY_CADENCE_MAX";
	public static final String KEY_CADENCE_MIN = "KEY_CADENCE_MIN";
	
	private static final int PROFILE_ID = 1;
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	
	public static final String CREATE_TABLE_PROFILE = "CREATE TABLE IF NOT EXISTS " + TABLE_RIDE_PROFLIE +
			"(" + KEY_ROWID + " integer primary key autoincrement, " +
			KEY_NAME + " text not null, " +
			KEY_UNIT + " int not null, " +
			KEY_GENDER + " int not null, " + 
			KEY_HEIGHT + " double not null, " +
			KEY_WEIGHT + " double not null, " + 
			KEY_BIRTHDAY + " text not null, " + 
			KEY_SIZE + " int not null" +
			"); ";
	public static final String CREATE_TABLE_RIDE = "CREATE TABLE IF NOT EXISTS " + TABLE_RIDE +
			"(" + KEY_ROWID + " integer primary key autoincrement, " +
			KEY_DATE + " text not null, " + 
			KEY_DATETIME + " text not null, " + 
			KEY_DATETIME_LONG + " long not null, " +
			KEY_TOTAL_TIME + " int not null, " + 
			KEY_TOTAL_DISTANCE + " double not null, " + 
			KEY_SPEED + " double not null, " +
			KEY_SPEED_MAX + " double not null, " +
			KEY_SPEED_MIN + " double not null, " +
			KEY_CADENCE + " double not null, " +
			KEY_CADENCE_MAX + " double not null, " +
			KEY_CADENCE_MIN + " double not null, " +
			KEY_PROFILE_ID + " int not null" +
			"); ";
			
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
		
	private final Context context;
	private DatabaseOpenHelper databaseOpenHelper;
	private SQLiteDatabase db;
	
	public DatabaseAdapter(Context ctx){
		this.context = ctx;
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * open database
	 * @return
	 */
	public DatabaseAdapter openDatabase(){
		if (databaseOpenHelper != null) {
			db = databaseOpenHelper.getWritableDatabase();
		}
		
		return this;
	}
	
	public SQLiteDatabase getSQLiteDatabase() {
		return db;
	}
	/**
	 * close database
	 */
	public void closeDatabase(){
		databaseOpenHelper.close();
	}
	
	
	public long insert_profile(String name, int unit, int gendar, double height, double weight, String birthday, int size){
		
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_UNIT, unit);
		initialValues.put(KEY_GENDER, gendar);
		initialValues.put(KEY_HEIGHT, height);
		initialValues.put(KEY_WEIGHT, weight);
		initialValues.put(KEY_BIRTHDAY, birthday);
		initialValues.put(KEY_SIZE, size);

		return db.insert(TABLE_RIDE_PROFLIE, null, initialValues);
	}
	
	public int update_profile(String oldName, String name, int unit, int gendar, double height, double weight, String birthday, int size){

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_UNIT, unit);
		initialValues.put(KEY_GENDER, gendar);
		initialValues.put(KEY_HEIGHT, height);
		initialValues.put(KEY_WEIGHT, weight);
		initialValues.put(KEY_BIRTHDAY, birthday);
		initialValues.put(KEY_SIZE, size);
		
		return db.update(TABLE_RIDE_PROFLIE, initialValues, KEY_NAME + "=" + "\'" + oldName + "\'" , null);
	}
	
	/**
	 * query profile
	 * @return
	 */
	public Cursor query_profile(){

		Cursor mCursor = db.query(true, TABLE_RIDE_PROFLIE, 
				new String[]{KEY_NAME, KEY_UNIT, KEY_GENDER, KEY_HEIGHT, KEY_WEIGHT, KEY_BIRTHDAY, KEY_SIZE, KEY_ROWID}, 
				null, null, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	public Cursor query_profile(String name) {
		if (name != null) {
			Cursor mCursor = db.query(true, TABLE_RIDE_PROFLIE, 
					new String[]{KEY_NAME, KEY_UNIT, KEY_GENDER, 
						KEY_HEIGHT, KEY_WEIGHT, 
						KEY_BIRTHDAY, KEY_SIZE, 
						KEY_ROWID}, 
					KEY_NAME + "=" + "\'" + name + "\'", 
					null, null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			
			return mCursor;
		}
		
		return null;
	}
	
	/**
	 * insert history hour
	 * @param datetime
	 * @param datetime
	 * @param totalTime
	 * @param totalDistance
	 * @param sleepQuality
	 * @return
	 */
	public long insert_history_hour(Calendar datetime, int totalTime, double totalDistance, double speed, double speedMax, double speedMin, double cadence, double cadenceMax, double cadenceMin){
		Log.i(TAG, "insert  datetime:" + datetime.getTime() + ", totalTime:" + totalTime + ", totalDistance:" + totalDistance + ", " + speed);
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_DATE, Global.sdf_yyyy_MM_dd.format(datetime.getTime()));
		initialValues.put(KEY_DATETIME, Global.sdf_yyyy_MM_dd_HH_mm_ss.format(datetime.getTime()));
		initialValues.put(KEY_DATETIME_LONG, datetime.getTimeInMillis());
		initialValues.put(KEY_TOTAL_TIME, totalTime);
		initialValues.put(KEY_TOTAL_DISTANCE, totalDistance);
		initialValues.put(KEY_SPEED, speed);
		initialValues.put(KEY_SPEED_MAX, speedMax);
		initialValues.put(KEY_SPEED_MIN, speedMin);
		initialValues.put(KEY_CADENCE, cadence);
		initialValues.put(KEY_CADENCE_MAX, cadenceMax);
		initialValues.put(KEY_CADENCE_MIN, cadenceMin);
		initialValues.put(KEY_PROFILE_ID, PROFILE_ID);

		return db.insert(TABLE_RIDE, null, initialValues);
	}
	
	
	/**
	 * update history hour
	 * @param date
	 * @param datetime
	 * @param step
	 * @param burn
	 * @param sleep
	 * @return
	 */
	public int update_history_hour(Calendar datetime, int totalTime, double totalDistance, double speed, double speedMax, double speedMin, double cadence, double cadenceMax, double cadenceMin){

		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_DATE, Global.sdf_yyyy_MM_dd.format(datetime.getTime()));
		initialValues.put(KEY_DATETIME, Global.sdf_yyyy_MM_dd_HH_mm_ss.format(datetime.getTime()));
		initialValues.put(KEY_DATETIME_LONG, datetime.getTimeInMillis());
		initialValues.put(KEY_TOTAL_TIME, totalTime);
		initialValues.put(KEY_TOTAL_DISTANCE, totalDistance);
		initialValues.put(KEY_SPEED, speed);
		initialValues.put(KEY_SPEED_MAX, speedMax);
		initialValues.put(KEY_SPEED_MIN, speedMin);
		initialValues.put(KEY_CADENCE, cadence);
		initialValues.put(KEY_CADENCE_MAX, cadenceMax);
		initialValues.put(KEY_CADENCE_MIN, cadenceMin);
		initialValues.put(KEY_PROFILE_ID, PROFILE_ID);
		
		return db.update(
				TABLE_RIDE, 
				initialValues, 
				KEY_DATETIME_LONG + "=" + datetime.getTimeInMillis() + " and " + KEY_PROFILE_ID + "=" + PROFILE_ID,
				null);
	}
	
	/**
	 * query history hour
	 * @return
	 */
	public Cursor query_history_hour(Calendar datetime){
		Log.i(TAG, "query history hour_ date:" + datetime.getTime());
		
		Cursor mCursor = db.query(true, TABLE_RIDE, 
				new String[]{KEY_DATETIME, KEY_TOTAL_TIME, KEY_TOTAL_DISTANCE, KEY_SPEED, KEY_SPEED_MAX, KEY_SPEED_MIN, KEY_CADENCE, KEY_CADENCE_MAX, KEY_CADENCE_MIN}, 
				KEY_DATETIME_LONG + "=" + datetime.getTimeInMillis() + " and " + KEY_PROFILE_ID + "=" + PROFILE_ID, 
				null, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	
	
	/**
	 * query history hour
	 * @param dateBegin
	 * @param dateEnd
	 * @return
	 */
	public Cursor query_history_hour(Calendar dateBegin, Calendar dateEnd) {
		Log.i(TAG, "query history hour_ begin:" + dateBegin.getTime() + ", end:" + dateEnd.getTime());
		
		Cursor mCursor = db.query(true, TABLE_RIDE, 
				new String[]{KEY_DATETIME, KEY_TOTAL_TIME, KEY_TOTAL_DISTANCE, KEY_SPEED, KEY_SPEED_MAX, KEY_SPEED_MIN, KEY_CADENCE, KEY_CADENCE_MAX, KEY_CADENCE_MIN}, 
				KEY_DATETIME_LONG + ">=" + dateBegin.getTimeInMillis() + " and " + KEY_DATETIME_LONG + "<" + dateEnd.getTimeInMillis()  + " and " + KEY_PROFILE_ID + "=" + PROFILE_ID, 
				null, null, null, KEY_DATETIME_LONG + " asc", null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	/**
	 * query history hour
	 * @param dateBegin
	 * @param dateEnd
	 * @return
	 */
	public Cursor query_history_all() {
		Log.i(TAG, "query history all");
		
		Cursor mCursor = db.query(true, TABLE_RIDE, 
				new String[]{KEY_DATETIME, KEY_TOTAL_TIME, KEY_TOTAL_DISTANCE, KEY_SPEED, KEY_SPEED_MAX, KEY_SPEED_MIN, KEY_CADENCE, KEY_CADENCE_MAX, KEY_CADENCE_MIN}, 
				KEY_PROFILE_ID + "=" + PROFILE_ID, 
				null, null, null, KEY_DATETIME_LONG + " desc", null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	/**
	 * delete a day's history hour data
	 * @param date
	 * @return
	 */
	public void delete_history_one(Calendar date) {
		if (date != null) {
			
			Log.i(TAG, "delete history hour " );
			db.delete(TABLE_RIDE, KEY_DATETIME_LONG + "=" + date.getTimeInMillis() + "" + " and " + KEY_PROFILE_ID + "=" + PROFILE_ID, null);
			
		}
		
	}
	
	public void delete_history_all() {
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_RIDE);
		
		db.execSQL(DatabaseAdapter.CREATE_TABLE_RIDE);
	}
	
	
	public void delete_history_hour_after_now(Calendar now) {
		if (now != null) {
			
			db.delete(
					TABLE_RIDE, 
					KEY_DATETIME_LONG + ">" + now.getTimeInMillis() , 
					null);
			
		}
	}
	
	public void deleteAllData() {
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_RIDE_PROFLIE);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_RIDE);
		
		db.execSQL(DatabaseAdapter.CREATE_TABLE_PROFILE);
		db.execSQL(DatabaseAdapter.CREATE_TABLE_RIDE);
	}
}
