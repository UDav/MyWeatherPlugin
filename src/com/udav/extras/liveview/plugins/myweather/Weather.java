package com.udav.extras.liveview.plugins.myweather;

import android.graphics.Bitmap;

public class Weather {
	private String city;
	private String weatherType;
	private int imgID;
	private String humidity;
	private String pressure;
	private String tommorow = "0", tommorowNight = "0";
	private int temperature;
	private String windDerection;
	private String windSpeed;
	private String updateTime;
	private Bitmap pict;
	private long time = 0;
	
	public Weather(){
		city = "";
		weatherType = "";
		imgID = 0;
		humidity = "";
		pressure = "";
		temperature = -999;
		windDerection = "";
		updateTime = "00:00";
		pict = null;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getWeatherType() {
		return weatherType;
	}

	public void setWeatherType(String weatherType) {
		this.weatherType = weatherType;
	}

	public int getImgID() {
		return imgID;
	}

	public void setImgID(int imgID) {
		this.imgID = imgID;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getTommorow() {
		return tommorow;
	}

	public void setTommorow(String tommorow) {
		this.tommorow = tommorow;
	}

	public String getTommorowNight() {
		return tommorowNight;
	}

	public void setTommorowNight(String tommorowNight) {
		this.tommorowNight = tommorowNight;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public String getPressure() {
		return pressure;
	}

	public void setPressure(String pressure) {
		this.pressure = pressure;
	}

	public String getWindDerection() {
		return windDerection;
	}

	public void setWindDerection(String windDerection) {
		this.windDerection = windDerection;
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Bitmap getPict() {
		return pict;
	}

	public void setPict(Bitmap pict) {
		this.pict = pict;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String toString(){
		return "Weather[city="+city+", weatherType="+weatherType+", temperature="+temperature+
				", humidity="+humidity+"]";
	}
	
	
}
