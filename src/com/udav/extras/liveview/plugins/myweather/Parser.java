package com.udav.extras.liveview.plugins.myweather;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parser {
	
	public static ArrayList<String> resultCity = new ArrayList<String>();
	public static ArrayList<String> resultCityID = new ArrayList<String>();

	public static void parseCity(){
		NodeList nl = null;
		try {
			Document doc = null;
			URL url = new URL("http://weather.yandex.ru/static/cities.xml");
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			doc.getDocumentElement().normalize();
			
			nl = doc.getElementsByTagName("cities").item(0).getChildNodes();
			
			for (int i=0; i<nl.getLength(); i++){
				Node child = nl.item(i);
				if (child instanceof Element) {
					if (((Element)child).getAttribute("name").equals("Россия")){
						Node childOfChild = null;
						for(int j=0; j<child.getChildNodes().getLength(); j++){
							childOfChild = child.getChildNodes().item(j);
							if (childOfChild instanceof Element){
								resultCity.add(childOfChild.getTextContent());
								resultCityID.add(((Element)childOfChild).getAttribute("id"));
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
}
