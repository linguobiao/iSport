package com.lingb.ride.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper{
	private static String TAG = "DatabaseOpenHelper";
	
	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DatabaseAdapter.CREATE_TABLE_PROFILE);
		db.execSQL(DatabaseAdapter.CREATE_TABLE_RIDE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		if (oldVersion == 1) {
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_RIDE_PROFLIE);
			db.execSQL(DatabaseAdapter.CREATE_TABLE_PROFILE);
			oldVersion = 2;
		}
	}

}
