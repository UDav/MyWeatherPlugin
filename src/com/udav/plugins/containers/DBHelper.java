package com.udav.plugins.containers;

import java.sql.NClob;
import java.util.ArrayList;

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
	
	public static Weather getCurrentWeatherFromDB(Context context){
		SQLiteDatabase db = getDB(context);
		Cursor mCursor = db.rawQuery("SELECT * FROM CurrenWeather ORDER BY id DESC LIMIT 1", new String[]{});
		mCursor.moveToFirst();
		
		Weather weather = new Weather();
		weather.setCity(mCursor.getString(mCursor.getColumnIndex("city")));
		weather.setTemperature(mCursor.getShort(mCursor.getColumnIndex("temperature")));
		weather.setWeatherType(mCursor.getString(mCursor.getColumnIndex("weatherType")));
		weather.setImgID(mCursor.getInt(mCursor.getColumnIndex("ingID")));
		weather.setHumidity(mCursor.getString(mCursor.getColumnIndex("humidity")));
		weather.setPressure(mCursor.getString(mCursor.getColumnIndex("pressure")));
		weather.setWindDerection(mCursor.getString(mCursor.getColumnIndex("windDirection")));
		weather.setWindSpeed(mCursor.getString(mCursor.getColumnIndex("windSpeed")));
		weather.setUpdateTime(mCursor.getString(mCursor.getColumnIndex("updateTime")));
		
		return weather;
	}
	
	public static boolean setCurrentWeatherToDB(Context context, Weather weather){
		SQLiteDatabase db = getDB(context);
		db.delete("CurrentWeather", null, null);
		
		ContentValues mContentValues = new ContentValues();
		mContentValues.put("city", weather.getCity());
		mContentValues.put("weatherType", weather.getWeatherType());
		mContentValues.put("imgID", weather.getImgID());
		mContentValues.put("humidity", weather.getHumidity());
		mContentValues.put("presure", weather.getPressure());
		mContentValues.put("temperature", weather.getTemperature());
		mContentValues.put("windDirection", weather.getWindDerection());
		mContentValues.put("windSpeed", weather.getWindSpeed());
		mContentValues.put("updateTime", weather.getUpdateTime());
		
		db.insert("CurrentWeather", null, mContentValues);
		return true;
	}
	
	public static ArrayList<ForecastWeather> getForecastWeatherFromDB(Context context){
		SQLiteDatabase db = getDB(context);
		
		ArrayList<ForecastWeather> forecastArray = new ArrayList<ForecastWeather>();
		
		Cursor mCursor = db.rawQuery("SELECT * FROM ForecastWeather", new String[]{});
		for (int i=0; i<mCursor.getCount(); i++) {
			ForecastWeather forecast = new ForecastWeather();
			forecast.setDate(mCursor.getString(mCursor.getColumnIndex("date")));
			forecast.setDayTemp(mCursor.getString(mCursor.getColumnIndex("dayTemp")));
			forecast.setDayImgID(mCursor.getInt(mCursor.getColumnIndex("dayImgID")));
			forecast.setDayHumidity(mCursor.getString(mCursor.getColumnIndex("dayHumidity")));
			forecast.setDayPressure(mCursor.getString(mCursor.getColumnIndex("dayPressure")));
			forecast.setDayWindDirection(mCursor.getString(mCursor.getColumnIndex("dayWindDirection")));
			forecast.setDayWindSpeed(mCursor.getString(mCursor.getColumnIndex("dayWindSpeed")));
			
			forecast.setNightTemp(mCursor.getString(mCursor.getColumnIndex("nigthTemp")));
			forecast.setNightImgID(mCursor.getInt(mCursor.getColumnIndex("nigthImgID")));
			forecast.setNightHumidity(mCursor.getString(mCursor.getColumnIndex("nigthHumidity")));
			forecast.setNightPressure(mCursor.getString(mCursor.getColumnIndex("nigthPressure")));
			forecast.setNightWindDirection(mCursor.getString(mCursor.getColumnIndex("nigthWindDirection")));
			forecast.setNightWindSpeed(mCursor.getString(mCursor.getColumnIndex("nigthWindSpeed")));
			
			forecastArray.add(forecast);
		}
		
		return forecastArray;
	}
	
	public static boolean setForecastWeatherToDB(Context context, ArrayList<ForecastWeather> forecastArray){
		SQLiteDatabase db = getDB(context);
		
		for (int i=0; i<forecastArray.size(); i++){
			ContentValues mContentValues = new ContentValues();
			ForecastWeather forecast = forecastArray.get(i);
			
			mContentValues.put("date", forecast.getDate());
			mContentValues.put("dayTemp", forecast.getDayTemp());
			mContentValues.put("dayImgID", forecast.getDayImgID());
			mContentValues.put("dayHumidity", forecast.getDayHumidity());
			mContentValues.put("dayPressure", forecast.getDayPressure());
			mContentValues.put("dayWindDirection", forecast.getDayWindDirection());
			mContentValues.put("dayWindSpeed", forecast.getDayWindSpeed());
			
			mContentValues.put("nigthTemp", forecast.getNightTemp());
			mContentValues.put("nigthImgID", forecast.getNightImgID());
			mContentValues.put("nigthHumidity", forecast.getNightHumidity());
			mContentValues.put("nigthPressure", forecast.getNightPressure());
			mContentValues.put("nigthWindDirection", forecast.getNightWindDirection());
			mContentValues.put("nigthWindSpeed", forecast.getNightWindSpeed());
			
			db.insert("ForecastWeather", null, mContentValues);
		}
		
		return true;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table City(id integer primary key autoincrement, cityID text, cityName text);");
		db.execSQL("creare table CurrentWeather(id integer primary key autoincrement, city text, weatherType text, imgID integer" +
				"humidity text, pressure text, temperature text, windDirection text, windSpeed text, updateTime text)");
		db.execSQL("create table ForecastWeather(id integer primary key autoincrement, date text, dayTemp text, dayImgID integer," +
				"dayHumidity text, dayPressure text, dayWindDirection text, dayWindSpeed text, nightTemp text, " +
				"nightWeatherType text, nightImgID integer, nightHumidity text, nightPressure text, nightWindDirection text" +
				"nightWindSpeed text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
		

}

