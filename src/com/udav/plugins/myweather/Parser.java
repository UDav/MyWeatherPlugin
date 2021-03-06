package com.udav.plugins.myweather;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.udav.plugins.PluginConstants;
import com.udav.plugins.containers.DBHelper;
import com.udav.plugins.containers.ForecastWeather;
import com.udav.plugins.containers.Weather;
import com.udav.plugins.containers.WeatherNextHours;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Parser {
	
	private static Document weatherData = null;
	
	private static Document loadData(String address){
		Document doc = null;
		try{
			URL url = new URL(address);
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(PluginConstants.LOG_TAG, "can't load file! ", e);
			//Log.e(PluginConstants.LOG_TAG, "can't load file! ", e);
		}
		return doc;
	}

	/**
	 * parse City list and insert value in database
	 */
	public static void parseCity(Context context){
		NodeList nl = null;
		try {
			Document doc = loadData("http://weather.yandex.ru/static/cities.xml");	
			nl = doc.getElementsByTagName("cities").item(0).getChildNodes();
			
			for (int i=0; i<nl.getLength(); i++){
				Node child = nl.item(i);
				if (child instanceof Element) {
					if (((Element)child).getAttribute("name").equals("Россия")){
						Node childOfChild = null;
						for(int j=0; j<child.getChildNodes().getLength(); j++){
							childOfChild = child.getChildNodes().item(j);
							if (childOfChild instanceof Element){
								DBHelper.setDataToDB(context, ((Element)childOfChild).getAttribute("id"),
										childOfChild.getTextContent());
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
/*	private static Bitmap loadPict(String imgId){
		InputStream in = null;
		Bitmap b = null;
		try {
			in = new URL("http://img.yandex.net/i/wiz"+imgId+".png").openStream();
			b = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (in != null)
				try {
					in.close();
				} catch (Exception e){
					e.printStackTrace();
				}
		}
		return b;
	}*/
	
	public static Weather weatherParse(Context context, String cityID){
		Weather w = new Weather();
		NodeList nl = null;
		
		Document doc = loadData("http://export.yandex.ru/weather-ng/forecasts/"+cityID+".xml");
		weatherData = doc;
		
		if (doc != null) {	
			nl = doc.getElementsByTagName("forecast").item(0).getChildNodes();
			
			w.setCity(((Element)doc.getElementsByTagName("forecast").item(0)).getAttribute("city"));
			
			for (int i=0; i<nl.getLength(); i++){
				Node child = nl.item(i);
				
				if (child instanceof Element) {
					if (child.getNodeName().equals("fact")){
						Node childOfChild = null;
						for (int j=0; j<child.getChildNodes().getLength(); j++) {
							childOfChild = child.getChildNodes().item(j);
							
							/*if ("station".equals(childOfChild.getNodeName()) && 
									"ru".equals(((Element)childOfChild).getAttribute("lang"))) {
								w.setCity(childOfChild.getTextContent());
							} else*/
							if ("temperature".equals(childOfChild.getNodeName())) {
								w.setTemperature(Integer.parseInt(childOfChild.getTextContent()));
							} else
							if ("weather_type_short".equals(childOfChild.getNodeName())){
								w.setWeatherType(childOfChild.getTextContent());
							} else
							if ("image".equals(childOfChild.getNodeName())){
								String t = childOfChild.getTextContent();
								if (t.charAt(0) == 'n')
									w.setImgID(Integer.parseInt(t.substring(1))+20);
								else
									w.setImgID(Integer.parseInt(t));
							} else
							if ("humidity".equals(childOfChild.getNodeName())){
								w.setHumidity(childOfChild.getTextContent());
							}else
							if ("wind_direction".equals(childOfChild.getNodeName())){
								w.setWindDirection(childOfChild.getTextContent());
							}else
							if ("wind_speed".equals(childOfChild.getNodeName())) {
								w.setWindSpeed(childOfChild.getTextContent());
							}else
							if ("pressure".equals(childOfChild.getNodeName())){
								w.setPressure(childOfChild.getTextContent());
							}
						}
					}
				}
			}
			Date date = new Date();
			int hours = date.getHours();
			int min = date.getMinutes();
			w.setUpdateTime(((hours<10)?"0"+hours:hours)+":"+((min<10)?"0"+min:min));
			
			DBHelper.setCurrentWeatherToDB(context, w);
			return w;
		} else return null;
	}
	
	public static ArrayList<WeatherNextHours> arrWeatherNextHours;// = new ArrayList<WeatherNextHours>();
	
	public static ArrayList<ForecastWeather> parseForecast(Context context, String cityID){
		ArrayList<ForecastWeather> forecast = new ArrayList<ForecastWeather>();
		arrWeatherNextHours = new ArrayList<WeatherNextHours>();
		
		Document doc = null;
		if (weatherData != null) {
			doc = weatherData;
		} else {
			doc = loadData("http://export.yandex.ru/weather-ng/forecasts/"+cityID+".xml");
		}
		
		if (doc != null) { 
			NodeList nl = doc.getElementsByTagName("forecast").item(0).getChildNodes();
			for (int i=0; i<nl.getLength(); i++){
				Node child = nl.item(i);
			
				if (child instanceof Element) {
					if (child.getNodeName().equals("day")){
						ForecastWeather temp = new ForecastWeather();
						temp.setDate(((Element) child).getAttribute("date"));
						
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						String currentDate = format.format(calendar.getTime());
						calendar.add(Calendar.DATE, 1);
						String nextDate = format.format(calendar.getTime());
						
						if ((temp.getDate().equals(currentDate)) || (temp.getDate().equals(nextDate))){
							Node childOfChild = null;
							for (int j=0; j<child.getChildNodes().getLength(); j++) {
								childOfChild = child.getChildNodes().item(j);
								if (childOfChild instanceof Element) {
									//parse hours forecast
									if (childOfChild.getNodeName().equals("hour")){
										WeatherNextHours tmp = new WeatherNextHours();
										tmp.setTime(((Element)childOfChild).getAttribute("at"));
										Node childOfChildOfChild = null;
										for (int k=0; k<childOfChild.getChildNodes().getLength(); k++) {
											childOfChildOfChild = childOfChild.getChildNodes().item(k);
											if ("temperature".equals(childOfChildOfChild.getNodeName())) {
												tmp.setTemperature(childOfChildOfChild.getTextContent());
											} else
											if ("image".equals(childOfChildOfChild.getNodeName())){
												String t = childOfChildOfChild.getTextContent();
												if (t.charAt(0) == 'n')
													tmp.setPictID(Integer.parseInt(t.substring(1))+20);
												else
													tmp.setPictID(Integer.parseInt(t));
												
											}
										}
										arrWeatherNextHours.add(tmp);
									}
								}
							}
						}
						DBHelper.setWeatherNextHoursToDB(context, arrWeatherNextHours);
						
						Node childOfChild = null;
						for (int j=0; j<child.getChildNodes().getLength(); j++) {
							childOfChild = child.getChildNodes().item(j);
							if (childOfChild instanceof Element) {
								//parse day forecast
								if (childOfChild.getNodeName().equals("day_part") &&
										((Element)childOfChild).getAttribute("typeid").equals("5")){
									Node childOfChildOfChild = null;
									for (int k=0; k<childOfChild.getChildNodes().getLength(); k++) {
										childOfChildOfChild = childOfChild.getChildNodes().item(k);
										if ("temperature".equals(childOfChildOfChild.getNodeName())) {
											temp.setDayTemp(childOfChildOfChild.getTextContent());
										} else
										if ("weather_type_short".equals(childOfChildOfChild.getNodeName())){
											temp.setDayWeatherType(childOfChildOfChild.getTextContent());
										} else
										if ("image".equals(childOfChildOfChild.getNodeName())){
											String t = childOfChildOfChild.getTextContent();
											if (t.charAt(0) == 'n')
												temp.setDayImgID(Integer.parseInt(t.substring(1))+20);
											else
												temp.setDayImgID(Integer.parseInt(t));
										} else
										if ("humidity".equals(childOfChildOfChild.getNodeName())){
											temp.setDayHumidity(childOfChildOfChild.getTextContent());
										}else
										if ("wind_direction".equals(childOfChildOfChild.getNodeName())){
											temp.setDayWindDirection(childOfChildOfChild.getTextContent());
										}else
										if ("wind_speed".equals(childOfChildOfChild.getNodeName())) {
											temp.setDayWindSpeed(childOfChildOfChild.getTextContent());
										}else
										if ("pressure".equals(childOfChildOfChild.getNodeName())){
											temp.setDayPressure(childOfChildOfChild.getTextContent());
										}
									}
								}
								//parse night forecast
								if (childOfChild.getNodeName().equals("day_part") &&
										((Element)childOfChild).getAttribute("typeid").equals("6")){
									Node childOfChildOfChild = null;
									for (int k=0; k<childOfChild.getChildNodes().getLength(); k++) {
										childOfChildOfChild = childOfChild.getChildNodes().item(k);
										if ("temperature".equals(childOfChildOfChild.getNodeName())) {
											temp.setNightTemp(childOfChildOfChild.getTextContent());
										} else
										if ("weather_type_short".equals(childOfChildOfChild.getNodeName())){
											temp.setNightWeatherType(childOfChildOfChild.getTextContent());
										} else
										if ("image".equals(childOfChildOfChild.getNodeName())){
											String t = childOfChildOfChild.getTextContent();
											if (t.charAt(0) == 'n')
												temp.setNightImgID(Integer.parseInt(t.substring(1))+20);
											else
												temp.setNightImgID(Integer.parseInt(t));
										} else
										if ("humidity".equals(childOfChildOfChild.getNodeName())){
											temp.setNightHumidity(childOfChildOfChild.getTextContent());
										}else
										if ("wind_direction".equals(childOfChildOfChild.getNodeName())){
											temp.setNightWindDirection(childOfChildOfChild.getTextContent());
										}else
										if ("wind_speed".equals(childOfChildOfChild.getNodeName())) {
											temp.setNightWindSpeed(childOfChildOfChild.getTextContent());
										}else
										if ("pressure".equals(childOfChildOfChild.getNodeName())){
											temp.setNightPressure(childOfChildOfChild.getTextContent());
										}
									}
								}
							}
						}
					forecast.add(temp);
					}
				}
			}
		//	arrWeatherNextHours.remove(25); // delete duplicate hour 0
		DBHelper.setForecastWeatherToDB(context, forecast);
		return forecast;	
		} else return null;
	}
	
}
