package com.udav.extras.liveview.plugins.myweather;

public class WeatherNextHours {
	private String time;
	private String temperature;
	private String pictID;
	
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime(){
		return time;
	}
	
	public void setTemperature(String temperature){
		this.temperature = temperature;
	}
	public String getTemperature(){
		return temperature;
	}
	
	public void setPictID(String pictID) {
		this.pictID = pictID;
	}
	public String getPictID(){
		return pictID;
	}
}
