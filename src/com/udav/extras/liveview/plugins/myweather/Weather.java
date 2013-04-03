package com.udav.extras.liveview.plugins.myweather;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Weather {
	private String city;
	private String weatherType;
	private String imgId;
	private String humidity;
	private String pressure;
	private String tommorow = "0", tommorowNight = "0";
	private int temperature;
	private String windDerection;
	private String windSpeed;
	
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

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
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

	public String toString(){
		return "Weather[city="+city+", weatherType="+weatherType+", temperature="+temperature+
				", humidity="+humidity+"]";
	}
	
	public void weatherParse(String cityID){
		NodeList nl = null;
		try {
			Document doc = null;
			URL url = new URL("http://export.yandex.ru/weather-ng/forecasts/"+cityID+".xml");
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
			
			nl = doc.getElementsByTagName("forecast").item(0).getChildNodes();
			
			for (int i=0; i<nl.getLength(); i++){
				Node child = nl.item(i);
				
				if (child instanceof Element) {
					if (child.getNodeName().equals("fact")){
						Node childOfChild = null;
						for (int j=0; j<child.getChildNodes().getLength(); j++) {
							childOfChild = child.getChildNodes().item(j);
							
							if ("station".equals(childOfChild.getNodeName()) && 
									"ru".equals(((Element)childOfChild).getAttribute("lang"))) {
								city = childOfChild.getTextContent();
							} else
							if ("temperature".equals(childOfChild.getNodeName())) {
								temperature = Integer.parseInt(childOfChild.getTextContent());
							} else
							if ("weather_type_short".equals(childOfChild.getNodeName())){
								weatherType = childOfChild.getTextContent();
							} else
							if ("image".equals(childOfChild.getNodeName())){
								imgId = childOfChild.getTextContent();
							} else
							if ("humidity".equals(childOfChild.getNodeName())){
								humidity = childOfChild.getTextContent();
							}else
							if ("wind_direction".equals(childOfChild.getNodeName())){
								windDerection = childOfChild.getTextContent();
							}else
							if ("wind_speed".equals(childOfChild.getNodeName())) {
								windSpeed = childOfChild.getTextContent();
							}else
							if ("pressure".equals(childOfChild.getNodeName())){
								pressure = childOfChild.getTextContent();
							}
						}
					}
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
