package com.cn.zhihengchuang.walkbank.db;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.cn.zhihengchuang.walkbank.util.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {
	public final static String pedometermodel_sql = "create table "+Constants.DATABASE_PEDOMETER_MODEL_NAME
			+"( id Integer PRIMARY KEY AUTOINCREMENT , "
			+ " uuid  text ,  " 
			+ " username  text ,  " //用户名
			+ " dateString  text ,  " // 日期
			+ " totalbytes int, "  //数据包
			+ " totalcalories  int ,  " // 当天的总卡路里
			+ " totalsportTime   int ,  " //  当天的总运动时间
			+ " totalsleepTime int  , " // 当天的总睡眠时间
			+ " totalstillTime int  , " // 当天的总静止时间
			+ " walktime int , " // 当天的散步时间
			+ " slowwalktime int , " // 当天的慢步时间
			+ " fastwalktime int ,"//当天中等散步时间
			+ " midWalkTime int ,"//当天中等散步时间
			+ " midWalkTime int ,"//当天中等散步时间
			+ " midWalkTime int ,"//当天中等散步时间
			+ " midWalkTime int ,"//当天中等散步时间
			+ " add_date text default (time(datetime('now', 'localtime')))"
			+ " );";
	public final static String tatal_sql = "create table "+Constants.DATABASE_TOTAL_TABLE_NAME
			+"( id Integer PRIMARY KEY AUTOINCREMENT , "
			+ " e_mail  text ,  " //用户名
			+ " submit  int ,  " // 1表示已经上传 0表示未上传
			+ " states  int ,  " // 1表示运动 0表示睡眠
			+ " sportid int, "
			+ " total_steps  int ,  " // 总步数
			+ " total_carles  int ,  " // 总卡路里
			+ " datetime text  , " // 日期
			+ " total_distance  , " // 总距离
			+ " userid text , " // 用户ID
			+ " add_date text default (time(datetime('now', 'localtime')))"
			+ " );";

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, Constants.DATABASE_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) { 
		db.execSQL(pedometermodel_sql);
	    db.execSQL(tatal_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF not EXISTS " + Constants.DATABASE_PEDOMETER_MODEL_NAME);
		db.execSQL("DROP TABLE IF not EXISTS " + Constants.DATABASE_TOTAL_TABLE_NAME);
		onCreate(db);
	}
	
	public Cursor select(String table_name) {
		return select(table_name, null, null, null, null, null, null, null);
	}
	
	public Cursor select(String table_name, String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy,String limit) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(table_name, columns, selection, selectionArgs, groupBy, having, orderBy,limit);
		return cursor;
	}

	public long insert(String table_name, Map<String, String> map) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		Object[] key = map.keySet().toArray();
		for(int i = 0; i < map.size(); i++){
			cv.put((String) key[i], map.get(key[i]));
		}
		long result = db.insert(table_name, null, cv);
		return result;
	}
	
	public int update(String table_name, String name, String value, Map<String, String> map){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();  
        Object[] key = map.keySet().toArray();
		for(int i = 0; i < map.size(); i++){
			cv.put((String) key[i], map.get(key[i]));
		}
        int result = db.update(table_name, cv, name +" = ? ", new String[]{ value } );
		return result;
	}
	
	public int delete(String table_name, String name, String value) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "";
		String[] wherevalues = new String[1];
		if(name != null){
			where = name + " = ? ";
			wherevalues[0] = value;
		}
		return db.delete(table_name, where, wherevalues);
	}

}