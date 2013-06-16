package com.udav.plugins.containers;

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
		Cursor mCursor = db.rawQuery("SELECT * FROM CurrentWeather ORDER BY id DESC LIMIT 1", new String[]{});
		
		Weather weather = new Weather();
		if (mCursor.getCount() == 0) return weather;
		else {
			mCursor.moveToFirst();
		
			weather.setCity(mCursor.getString(mCursor.getColumnIndex("city")));
			weather.setTemperature(mCursor.getShort(mCursor.getColumnIndex("temperature")));
			weather.setWeatherType(mCursor.getString(mCursor.getColumnIndex("weatherType")));
			weather.setImgID(mCursor.getInt(mCursor.getColumnIndex("imgID")));
			weather.setHumidity(mCursor.getString(mCursor.getColumnIndex("humidity")));
			weather.setPressure(mCursor.getString(mCursor.getColumnIndex("pressure")));
			weather.setWindDirection(mCursor.getString(mCursor.getColumnIndex("windDirection")));
			weather.setWindSpeed(mCursor.getString(mCursor.getColumnIndex("windSpeed")));
			weather.setUpdateTime(mCursor.getString(mCursor.getColumnIndex("updateTime")));
		
			return weather;
		}
	}
	
	public static boolean setCurrentWeatherToDB(Context context, Weather weather){
		SQLiteDatabase db = getDB(context);
		db.delete("CurrentWeather", null, null);
		
		ContentValues mContentValues = new ContentValues();
		mContentValues.put("city", weather.getCity());
		mContentValues.put("weatherType", weather.getWeatherType());
		mContentValues.put("imgID", weather.getImgID());
		mContentValues.put("humidity", weather.getHumidity());
		mContentValues.put("pressure", weather.getPressure());
		mContentValues.put("temperature", weather.getTemperature());
		mContentValues.put("windDirection", weather.getWindDirection());
		mContentValues.put("windSpeed", weather.getWindSpeed());
		mContentValues.put("updateTime", weather.getUpdateTime());
		
		db.insert("CurrentWeather", null, mContentValues);
		return true;
	}
	
	public static ArrayList<ForecastWeather> getForecastWeatherFromDB(Context context){
		SQLiteDatabase db = getDB(context);
		
		ArrayList<ForecastWeather> forecastArray = new ArrayList<ForecastWeather>();
		Cursor mCursor = db.rawQuery("SELECT * FROM ForecastWeather", new String[]{});
		
		if (mCursor.getCount() == 0) return forecastArray;
		else {
			mCursor.moveToFirst();
			for (int i=0; i<mCursor.getCount(); i++) {
				ForecastWeather forecast = new ForecastWeather();
				forecast.setDate(mCursor.getString(mCursor.getColumnIndex("date")));
				forecast.setDayTemp(mCursor.getString(mCursor.getColumnIndex("dayTemp")));
				forecast.setDayImgID(mCursor.getInt(mCursor.getColumnIndex("dayImgID")));
				forecast.setDayHumidity(mCursor.getString(mCursor.getColumnIndex("dayHumidity")));
				forecast.setDayPressure(mCursor.getString(mCursor.getColumnIndex("dayPressure")));
				forecast.setDayWindDirection(mCursor.getString(mCursor.getColumnIndex("dayWindDirection")));
				forecast.setDayWindSpeed(mCursor.getString(mCursor.getColumnIndex("dayWindSpeed")));
			
				forecast.setNightTemp(mCursor.getString(mCursor.getColumnIndex("nightTemp")));
				forecast.setNightImgID(mCursor.getInt(mCursor.getColumnIndex("nightImgID")));
				forecast.setNightHumidity(mCursor.getString(mCursor.getColumnIndex("nightHumidity")));
				forecast.setNightPressure(mCursor.getString(mCursor.getColumnIndex("nightPressure")));
				forecast.setNightWindDirection(mCursor.getString(mCursor.getColumnIndex("nightWindDirection")));
				forecast.setNightWindSpeed(mCursor.getString(mCursor.getColumnIndex("nightWindSpeed")));
			
				forecastArray.add(forecast);
				mCursor.moveToNext();
			}
			return forecastArray;
		}
	}
	
	public static boolean setForecastWeatherToDB(Context context, ArrayList<ForecastWeather> forecastArray){
		SQLiteDatabase db = getDB(context);
		
		db.delete("ForecastWeather", null, null);
		
		
		for (int i=0; i<forecastArray.size(); i++){
			ForecastWeather forecast = forecastArray.get(i);
			ContentValues mContentValues = new ContentValues();
			
			mContentValues.put("date", forecast.getDate());
			mContentValues.put("dayTemp", forecast.getDayTemp());
			mContentValues.put("dayImgID", forecast.getDayImgID());
			mContentValues.put("dayHumidity", forecast.getDayHumidity());
			mContentValues.put("dayPressure", forecast.getDayPressure());
			mContentValues.put("dayWindDirection", forecast.getDayWindDirection());
			mContentValues.put("dayWindSpeed", forecast.getDayWindSpeed());
			
			mContentValues.put("nightTemp", forecast.getNightTemp());
			mContentValues.put("nightImgID", forecast.getNightImgID());
			mContentValues.put("nightHumidity", forecast.getNightHumidity());
			mContentValues.put("nightPressure", forecast.getNightPressure());
			mContentValues.put("nightWindDirection", forecast.getNightWindDirection());
			mContentValues.put("nightWindSpeed", forecast.getNightWindSpeed());
			
			db.insert("ForecastWeather", null, mContentValues);
		}
		
		return true;
	}
	
	public static ArrayList<WeatherNextHours> getWeatherNextHoursFromDB(Context context){
		SQLiteDatabase db = getDB(context);
		ArrayList<WeatherNextHours> arrayWeatherNextHours = new ArrayList<WeatherNextHours>();
		
		Cursor mCursor = db.rawQuery("SELECT * FROM WeatherNextHours", new String[]{});
		if (mCursor.getCount() == 0) return null;
		else {
			mCursor.moveToFirst();
			for (int i=0; i<mCursor.getCount(); i++) {
				WeatherNextHours weatherNextHours = new WeatherNextHours();
				weatherNextHours.setTime(mCursor.getString(mCursor.getColumnIndex("time")));
				weatherNextHours.setTemperature(mCursor.getString(mCursor.getColumnIndex("temperature")));
				weatherNextHours.setPictID(mCursor.getInt(mCursor.getColumnIndex("pictID")));
				arrayWeatherNextHours.add(weatherNextHours);
				mCursor.moveToNext();
			}
		}
		
		return arrayWeatherNextHours;
	}
	
	public static boolean setWeatherNextHoursToDB(Context context, 
			ArrayList<WeatherNextHours> arrayWeatherNextHours) {
		SQLiteDatabase db = getDB(context);
		
		db.delete("WeatherNextHours", null, null);
		for (int i=0; i<arrayWeatherNextHours.size(); i++) {
			WeatherNextHours weatherNextHours = arrayWeatherNextHours.get(i);
			ContentValues mContentValues = new ContentValues();
			mContentValues.put("time", weatherNextHours.getTime());
			mContentValues.put("temperature", weatherNextHours.getTemperature());
			mContentValues.put("pictID", weatherNextHours.getPictID());
			
			db.insert("WeatherNextHours", null, mContentValues);
		}
		
		return true;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table City(id integer primary key autoincrement, cityID text, cityName text);");
		
		db.execSQL("create table CurrentWeather(id integer primary key autoincrement, city text, weatherType text, " +
				"imgID integer, humidity text, pressure text, temperature text, windDirection text, " +
				"windSpeed text, updateTime text);");
		
		db.execSQL("create table ForecastWeather(id integer primary key autoincrement, date text, " +
				"dayTemp text, dayImgID integer, dayHumidity text, dayPressure text, dayWindDirection text," +
				"dayWindSpeed text, nightTemp text, nightImgID integer, " +
				"nightHumidity text, nightPressure text, " +
				"nightWindDirection text, nightWindSpeed text);");
		
		db.execSQL("CREATE TABLE WeatherNextHours(id integer primary key autoincrement, time text," +
				"temperature text, pictID integer);");
		
		System.out.println("Create table OK!");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
		

}

