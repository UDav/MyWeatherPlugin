package com.udav.extras.liveview.plugins.myweather;

import android.graphics.Bitmap;

public class ForecastWeather {
	private String date;
	// day data
	private String dayTemp;
	private String dayWeatherType;
	private String dayImgID;
	private String dayHumidity;
	private String dayPressure;
	private String dayWindDirection;
	private String dayWindSpeed;
	private Bitmap dayBitmap;
	//night data
	private String nightTemp;
	private String nightWeatherType;
	private String nightImgID;
	private String nightHumidity;
	private String nightPressure;
	private String nightWindDirection;
	private String mightWindSpeed;
	private Bitmap nightBitmap;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDayTemp() {
		return dayTemp;
	}
	public void setDayTemp(String dayTemp) {
		this.dayTemp = dayTemp;
	}
	public String getDayWeatherType() {
		return dayWeatherType;
	}
	public void setDayWeatherType(String dayWeatherType) {
		this.dayWeatherType = dayWeatherType;
	}
	public String getDayImgID() {
		return dayImgID;
	}
	public void setDayImgID(String dayImgID) {
		this.dayImgID = dayImgID;
	}
	public String getDayHumidity() {
		return dayHumidity;
	}
	public void setDayHumidity(String dayHumidity) {
		this.dayHumidity = dayHumidity;
	}
	public String getDayPressure() {
		return dayPressure;
	}
	public void setDayPressure(String dayPressure) {
		this.dayPressure = dayPressure;
	}
	public String getDayWindDirection() {
		return dayWindDirection;
	}
	public void setDayWindDirection(String dayWindDirection) {
		this.dayWindDirection = dayWindDirection;
	}
	public String getDayWindSpeed() {
		return dayWindSpeed;
	}
	public void setDayWindSpeed(String dayWindSpeed) {
		this.dayWindSpeed = dayWindSpeed;
	}
	public Bitmap getDayBitmap() {
		return dayBitmap;
	}
	public void setDayBitmap(Bitmap dayBitmap) {
		this.dayBitmap = dayBitmap;
	}
	public String getNightTemp() {
		return nightTemp;
	}
	public void setNightTemp(String nightTemp) {
		this.nightTemp = nightTemp;
	}
	public String getNightWeatherType() {
		return nightWeatherType;
	}
	public void setNightWeatherType(String nightWeatherType) {
		this.nightWeatherType = nightWeatherType;
	}
	public String getNightImgID() {
		return nightImgID;
	}
	public void setNightImgID(String nightImgID) {
		this.nightImgID = nightImgID;
	}
	public String getNightHumidity() {
		return nightHumidity;
	}
	public void setNightHumidity(String nightHumidity) {
		this.nightHumidity = nightHumidity;
	}
	public String getNightPressure() {
		return nightPressure;
	}
	public void setNightPressure(String nightPressure) {
		this.nightPressure = nightPressure;
	}
	public String getNightWindDirection() {
		return nightWindDirection;
	}
	public void setNightWindDirection(String nightWindDirection) {
		this.nightWindDirection = nightWindDirection;
	}
	public String getMightWindSpeed() {
		return mightWindSpeed;
	}
	public void setMightWindSpeed(String mightWindSpeed) {
		this.mightWindSpeed = mightWindSpeed;
	}
	public Bitmap getNightBitmap() {
		return nightBitmap;
	}
	public void setNightBitmap(Bitmap nightBitmap) {
		this.nightBitmap = nightBitmap;
	}
	
}
