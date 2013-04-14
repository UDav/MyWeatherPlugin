package com.udav.extras.liveview.plugins.myweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper{
	private static DBHelper mDBHelper;
	private Context context;
	
	private DBHelper(Context context) {
		super(context, "MyWeatherDB", null, 1);
		this.context = context;
	}
	
	public static SQLiteDatabase getDB(Context context) {
		if (mDBHelper == null) mDBHelper = new DBHelper(context);
		return mDBHelper.getWritableDatabase();
	}
	
	public static boolean setDataToDB(Context context, String cityID, String cityName){
		SQLiteDatabase db = getDB(context);
		ContentValues mContentValues = new ContentValues();
		mContentValues.put("cityID", cityID);
		mContentValues.put("cityName", cityName);
		db.insert("City", null, mContentValues);
		return true;
	}
	
	public static Cursor getDataFromDB(Context context) {
		Cursor mCursor = getDB(context).rawQuery("SELECT * FROM City ORDER BY cityName", new String[]{});
		return mCursor;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table City(id integer primary key autoincrement, cityID text, cityName text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
		

}

