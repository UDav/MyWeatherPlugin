/*
 * Copyright (c) 2010 Sony Ericsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.udav.plugins;

import android.content.Context;
import android.content.res.Resources;
import android.drm.DrmErrorEvent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import com.udav.mymeatherplugin.R;
import com.udav.plugins.containers.ForecastWeather;
import com.udav.plugins.containers.Weather;
import com.udav.plugins.myweather.Parser;

/**
 * Utils.
 */
public final class PluginUtils {
    
    private PluginUtils() {
        
    }
    
    /**
     * Stores icon to phone file system
     * 
     * @param resources Reference to project resources
     * @param resource Reference to specific resource
     * @param fileName The icon file name
     */
    public static String storeIconToFile(Context ctx, Resources resources, int resource, String fileName) {
        Log.d(PluginConstants.LOG_TAG, "Store icon to file.");
        
        if(resources == null) {
            return "";
        }
        
        Bitmap bitmap = BitmapFactory.decodeStream(resources.openRawResource(resource));
        
        try {
            FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close(); 
        } 
        catch (IOException e) { 
            Log.e(PluginConstants.LOG_TAG, "Failed to store to device", e);
        }
        
        File iconFile = ctx.getFileStreamPath(fileName);
        Log.d(PluginConstants.LOG_TAG, "Icon stored. " + iconFile.getAbsolutePath());
        
        return iconFile.getAbsolutePath();
    }
    
    /**
     * Rotates and stores image to device
     *  
     * @param bitmap
     * @param degrees
     * @return
     */
    public static void rotateAndSend(LiveViewAdapter liveView, int pluginId, Bitmap bitmap, int degrees) {
        Bitmap newBitmap = null;
        try
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch(Exception e) {
            Log.e(PluginConstants.LOG_TAG, "Failed to rotate bitmap.", e);
            return;
        }
        
        sendScaledImage(liveView, pluginId, newBitmap);
    }
    
    public static void sendTextBitmap(LiveViewAdapter liveView, int pluginId, String text) {
        sendTextBitmap(liveView, pluginId, text, 64, 15);
    }
    
    /**
     * Stores text to an image on file.
     * 
     * @param liveView Reference to LiveView connection
     * @param pluginId Id of the plugin
     * @param text The text string
     * @param bitmapSizeX Bitmap size X
     * @param fontSize Font size
     * @return Absolute path to file
     */
    public static void sendTextBitmap(LiveViewAdapter liveView, int pluginId, String text, int bitmapSizeX, int fontSize) {
        // Empty bitmap and link the canvas to it
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(bitmapSizeX, fontSize+5, Bitmap.Config.RGB_565);
        }
        catch(IllegalArgumentException  e) {
            return;
        }
        
        Canvas canvas = new Canvas(bitmap);

        // Set the text properties in the canvas
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(fontSize);
        textPaint.setColor(Color.WHITE);

        // Create the text layout and draw it to the canvas
        Layout textLayout = new StaticLayout(text, textPaint, bitmapSizeX, Layout.Alignment.ALIGN_CENTER, 1, 1, false);
        textLayout.draw(canvas);
        
        
        try
        { 
            liveView.sendImageAsBitmap(pluginId, centerX(bitmap), centerY(bitmap), bitmap);
        } catch(Exception e) {
            Log.d(PluginConstants.LOG_TAG, "Failed to send bitmap", e);
        }
    }
    
    private final static char DEGREES = (char)176; 
    /**
     * Draw current weather on display
     * @param context
     * @param liveView
     * @param pluginId
     * @param w
     * @param fontSize
     */
    public static void displayWeather(Context context, LiveViewAdapter liveView, int pluginId, Weather w, int fontSize) {
        // Empty bitmap and link the canvas to it
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(PluginConstants.LIVEVIEW_SCREEN_X, 
            		PluginConstants.LIVEVIEW_SCREEN_Y, Bitmap.Config.RGB_565);
        }
        catch(IllegalArgumentException  e) {
            return;
        }
        
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(fontSize);

        Rect bounds = new Rect();
       
        //draw city
        String city = w.getCity();
        paint.getTextBounds(city, 0, city.length(), bounds);
        canvas.drawText(w.getCity(), (PluginConstants.LIVEVIEW_SCREEN_X-bounds.right)/2, 
        		0-bounds.top+5, paint);
        
        //draw time update
        paint.setTextSize(10);
        String updateTime = context.getString(R.string.update) + " " + w.getUpdateTime();
        paint.getTextBounds(updateTime, 0, updateTime.length(), bounds);
        canvas.drawText(updateTime, (PluginConstants.LIVEVIEW_SCREEN_X-bounds.right)/2, 
        		(0-bounds.top+5)*2, paint);
        paint.setTextSize(fontSize);
       
        //draw temperature
        paint.setTextSize(20);
        String temperature = String.valueOf(w.getTemperature())+DEGREES;
        paint.getTextBounds(temperature, 0, temperature.length(), bounds);
        canvas.drawText(temperature, (PluginConstants.LIVEVIEW_SCREEN_X-bounds.right)/2, 
        		(PluginConstants.LIVEVIEW_SCREEN_Y+bounds.top)/2, paint);
        paint.setTextSize(fontSize);
        
        //draw weather picture
        //draw weather type
        String weatherType = w.getWeatherType();
        paint.getTextBounds(weatherType, 0, weatherType.length(), bounds);
        
        int y = 85;
        Bitmap pict = BitmapFactory.decodeResource(context.getResources(), selectImage(w.getImgID()));
        pict = changeSize(pict, 24);
        
        if (pict != null) {
        	if (bounds.right+pict.getWidth() > PluginConstants.LIVEVIEW_SCREEN_X){
        		
        		int x = (PluginConstants.LIVEVIEW_SCREEN_X-pict.getWidth())/2;
        		canvas.drawBitmap(pict, x, y-pict.getHeight(), paint);
        		x = (PluginConstants.LIVEVIEW_SCREEN_X-bounds.right)/2;
        		y = y - bounds.top + 5;
        		canvas.drawText(w.getWeatherType(), x, y, paint);
        	} else {
        		int x = (PluginConstants.LIVEVIEW_SCREEN_X-(pict.getWidth()+bounds.right))/2;
        		canvas.drawBitmap(pict, x, y-pict.getHeight(), paint);
        		canvas.drawText(w.getWeatherType(), x+pict.getWidth(), y, paint);
        	}
        }
        
        //draw wind
        paint.setTextSize(10);
        String wind = w.getWindDirection() + " " + w.getWindSpeed() + context.getString(R.string.windSpeed);
        Bitmap windPict = BitmapFactory.decodeResource(context.getResources(), R.drawable.wind);
        windPict = changeSize(windPict, 10);
        paint.getTextBounds(wind, 0, wind.length(), bounds);
        int xWind = (PluginConstants.LIVEVIEW_SCREEN_X-(windPict.getWidth()+bounds.right))/2;
        y = y + 5;
        canvas.drawBitmap(windPict, xWind, y, paint);
        y = y + windPict.getHeight();
        canvas.drawText(wind, xWind+windPict.getWidth()+5, y, paint);
        
        
        //draw humidity
        String humidity = w.getHumidity()+"%";
        Bitmap humidityPict = BitmapFactory.decodeResource(context.getResources(), R.drawable.humidity);
        humidityPict = changeSize(humidityPict, 10);
        paint.getTextBounds(humidity, 0, humidity.length(), bounds);
        canvas.drawText(humidity, humidityPict.getWidth()+5, 128, paint);
        canvas.drawBitmap(humidityPict, 0, PluginConstants.LIVEVIEW_SCREEN_Y-humidityPict.getHeight(), paint);
        
        //draw pressure
        String pressure = w.getPressure()+context.getString(R.string.pressure);
        Bitmap pressurePict = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure);
        pressurePict = changeSize(pressurePict, 10);
        paint.getTextBounds(pressure, 0, pressure.length(), bounds);
        canvas.drawText(pressure, PluginConstants.LIVEVIEW_SCREEN_X-bounds.right, 
        		PluginConstants.LIVEVIEW_SCREEN_Y, paint);
        canvas.drawBitmap(pressurePict, PluginConstants.LIVEVIEW_SCREEN_X-pressurePict.getWidth()-bounds.right-5, 
        		PluginConstants.LIVEVIEW_SCREEN_Y - pressurePict.getHeight(), paint);
        
        try{ 
            liveView.sendImageAsBitmap(pluginId, centerX(bitmap), centerY(bitmap), bitmap);
        } catch(Exception e) {
            Log.d(PluginConstants.LOG_TAG, "Failed to send bitmap", e);
        }
    }
    
    /**
     * Draw forecast weather on display
     * @param context
     * @param liveView
     * @param pluginId
     * @param fw
     * @param fontSize
     */
    public static void displayForecastWeather(Context context, LiveViewAdapter liveView, int pluginId, ForecastWeather fw, int fontSize) {
        // Empty bitmap and link the canvas to it
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(PluginConstants.LIVEVIEW_SCREEN_X, 
            		PluginConstants.LIVEVIEW_SCREEN_Y, Bitmap.Config.RGB_565);
        }
        catch(IllegalArgumentException  e) {
            return;
        }
        
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(fontSize);

        Rect bounds = new Rect();
        
        //draw date
        String date = fw.getDate();
        paint.getTextBounds(date, 0, date.length(), bounds);
        int yDate = 0-bounds.top+5;
        canvas.drawText(date, (PluginConstants.LIVEVIEW_SCREEN_X-bounds.right)/2, 
        		yDate, paint);
        //draw separetor
        canvas.drawLine(PluginConstants.LIVEVIEW_SCREEN_X/2, 0-bounds.top+5, 
        		PluginConstants.LIVEVIEW_SCREEN_X/2, 
        		PluginConstants.LIVEVIEW_SCREEN_Y, paint);
        //draw day
        String day = context.getString(R.string.day);
        paint.getTextBounds(day, 0, day.length(), bounds);
        int yDay = yDate * 2;
        canvas.drawText(day, 0, yDay, paint);
        //draw day temperature
        String temp = fw.getDayTemp()+DEGREES;
        paint.getTextBounds(temp, 0, temp.length(), bounds);
        int yTemp = yDate * 3;
        canvas.drawText(temp, 0, yTemp, paint);
        //draw pict
        Bitmap dayPict = BitmapFactory.decodeResource(context.getResources(), selectImage(fw.getDayImgID()));
        dayPict = changeSize(dayPict, 24);
        canvas.drawBitmap(dayPict, 0, yTemp, paint);
        
        paint.setTextSize(10);
        //draw wind
        String wind = fw.getDayWindDirection() + fw.getDayWindSpeed()+context.getString(R.string.windSpeed);
        Bitmap windPict = BitmapFactory.decodeResource(context.getResources(), R.drawable.wind);
        windPict = changeSize(windPict, 10);
        paint.getTextBounds(wind, 0, wind.length(), bounds);
        int yWind = yTemp+dayPict.getHeight()-bounds.top+10;
        canvas.drawText(wind, windPict.getWidth()+5, yWind, paint);
        canvas.drawBitmap(windPict, 0, yWind-windPict.getHeight(), paint);
        //draw humidity
        String humidity = fw.getDayHumidity()+"%";
        Bitmap humidityPict = BitmapFactory.decodeResource(context.getResources(), R.drawable.humidity);
        humidityPict = changeSize(humidityPict, 10);
        paint.getTextBounds(humidity, 0, humidity.length(), bounds);
        int yHumidity = yWind-bounds.top+5;
        canvas.drawText(humidity, humidityPict.getWidth()+5, yHumidity, paint);
        canvas.drawBitmap(humidityPict, 0, yHumidity-humidityPict.getHeight(), paint);
        //draw pressure
        String pressure = fw.getDayPressure()+context.getString(R.string.pressure);
        Bitmap pressurePict = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure);
        pressurePict = changeSize(pressurePict, 10);
        paint.getTextBounds(pressure, 0, pressure.length(), bounds);
        int yPressure = yHumidity-bounds.top+5;
        canvas.drawText(pressure, pressurePict.getWidth()+5, yPressure, paint);
        canvas.drawBitmap(pressurePict, 0, yPressure-pressurePict.getHeight(), paint);
        paint.setTextSize(fontSize);
        
        //draw Night
        String night = context.getString(R.string.night);
        paint.getTextBounds(night, 0, night.length(), bounds);
        int yNight = yDate * 2;
        canvas.drawText(night, PluginConstants.LIVEVIEW_SCREEN_X-bounds.right, yNight, paint);
        //draw day temperature
        String nightTemp = fw.getNightTemp()+DEGREES;
        paint.getTextBounds(nightTemp, 0, nightTemp.length(), bounds);
        int yNightTemp = yDate * 3;
        canvas.drawText(nightTemp, PluginConstants.LIVEVIEW_SCREEN_X-bounds.right, yNightTemp, paint);
        
        int middle = PluginConstants.LIVEVIEW_SCREEN_X/2;
        //draw pict
        Bitmap nightPict = BitmapFactory.decodeResource(context.getResources(), selectImage(fw.getNightImgID()));
        nightPict = changeSize(nightPict, 24);
        canvas.drawBitmap(nightPict, PluginConstants.LIVEVIEW_SCREEN_X-nightPict.getWidth(), 
        		yNightTemp, paint);
        //draw wind
        paint.setTextSize(10);
        String nightWind = fw.getNightWindDirection() + fw.getNightWindSpeed()+context.getString(R.string.windSpeed);
        paint.getTextBounds(nightWind, 0, nightWind.length(), bounds);
        int yNightWind = yNightTemp+nightPict.getHeight()-bounds.top+10;
        canvas.drawText(nightWind, middle+5+windPict.getWidth()+5, yNightWind, paint);
        canvas.drawBitmap(windPict, middle+5, 
        		yNightWind-windPict.getHeight(), paint);
        //draw humidity
        String nightHumidity = fw.getNightHumidity()+"%";
        paint.getTextBounds(nightHumidity, 0, nightHumidity.length(), bounds);
        int yNightHumidity = yNightWind-bounds.top+5;
        canvas.drawText(nightHumidity, middle+5+humidityPict.getWidth()+5, yNightHumidity, paint);
        canvas.drawBitmap(humidityPict, 
        		middle+5, 
        		yNightHumidity-humidityPict.getHeight(), paint);
        //draw pressure
        String nightPressure = fw.getNightPressure()+context.getString(R.string.pressure);
        paint.getTextBounds(nightPressure, 0, pressure.length(), bounds);
        int yNightPressure = yHumidity-bounds.top+5;
        canvas.drawText(nightPressure, middle+5+pressurePict.getWidth()+5, yNightPressure, paint);
        canvas.drawBitmap(pressurePict, middle+5, 
        		yNightPressure-pressurePict.getHeight(), paint);
        paint.setTextSize(fontSize);
        
        try{ 
            liveView.sendImageAsBitmap(pluginId, centerX(bitmap), centerY(bitmap), bitmap);
        } catch(Exception e) {
            Log.d(PluginConstants.LOG_TAG, "Failed to send bitmap", e);
        }
    }
    
    public static void displayWeatherNextHours(Context context, LiveViewAdapter liveView, int pluginId){
    	Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(PluginConstants.LIVEVIEW_SCREEN_X, 
            		PluginConstants.LIVEVIEW_SCREEN_Y, Bitmap.Config.RGB_565);
        }
        catch(IllegalArgumentException  e) {
            return;
        }
        Canvas canvas = new Canvas(bitmap);
        
        int size=0;
        int index=0;
        
        Date date = new Date();
        Log.d(PluginConstants.LOG_TAG, ""+date.getHours());
        Log.d(PluginConstants.LOG_TAG, Parser.arrWeatherNextHours.get(index).getTime());
        
        // set index equal current time
        while(Integer.parseInt(Parser.arrWeatherNextHours.get(index).getTime()) < date.getHours()) 
        	index++;
        
        Paint paint = new Paint();
    	paint.setColor(Color.WHITE);
    	paint.setTextSize(10);
    	Rect bounds = new Rect();
    	int bottom = 0;
        
    	//draw first line
        while (index < Parser.arrWeatherNextHours.size()) {
        	Bitmap pict = BitmapFactory.decodeResource(context.getResources(), 
        			selectImage(Parser.arrWeatherNextHours.get(index).getPictID()));	
        	pict = changeSize(pict, 20);
        	if ((size + pict.getWidth()) > PluginConstants.LIVEVIEW_SCREEN_X) break;
        	// draw hour
        	String hour = Parser.arrWeatherNextHours.get(index).getTime();
        	paint.getTextBounds(hour, 0, hour.length(), bounds);
        	int yHour = 0-bounds.top + 5;
        	int xCenter = size+((pict.getWidth()-bounds.right)/2);
        	canvas.drawText(hour, xCenter, yHour, paint);
        	//draw weather pict
        	canvas.drawBitmap(pict, size, yHour, new Paint());
        	//draw temperature
        	String temperature = Parser.arrWeatherNextHours.get(index).getTemperature()+DEGREES;
        	paint.getTextBounds(temperature, 0, temperature.length(), bounds);
        	bottom = yHour + pict.getHeight() - bounds.top;
        	xCenter = size+((pict.getWidth()-bounds.right)/2);
        	canvas.drawText(temperature, xCenter, bottom, paint);
        	
        	size += pict.getWidth() + 5;
        	index++;
        }
        canvas.drawLine(0, bottom+=5, PluginConstants.LIVEVIEW_SCREEN_X, bottom, paint);
        
        //draw second line
        size = 0;
        while (index < Parser.arrWeatherNextHours.size()) {
        	Bitmap pict = BitmapFactory.decodeResource(context.getResources(), 
        			selectImage(Parser.arrWeatherNextHours.get(index).getPictID()));
        	pict = changeSize(pict, 20);
        	if ((size + pict.getWidth()) > PluginConstants.LIVEVIEW_SCREEN_X) break;
        	// draw hour
        	String hour = Parser.arrWeatherNextHours.get(index).getTime();
        	paint.getTextBounds(hour, 0, hour.length(), bounds);
        	int yHour = bottom - bounds.top + 5;
        	int xCenter = size + ((pict.getWidth()-bounds.right)/2);
        	canvas.drawText(hour, xCenter, yHour, paint);
        	//draw weather pict
        	canvas.drawBitmap(pict, size, yHour, new Paint());
        	//draw temperature
        	String temperature = Parser.arrWeatherNextHours.get(index).getTemperature()+DEGREES;
        	paint.getTextBounds(temperature, 0, temperature.length(), bounds);
        	xCenter = size + ((pict.getWidth()-bounds.right)/2);
        	canvas.drawText(temperature, xCenter, yHour +pict.getHeight()-bounds.top, paint);
        	
        	size += pict.getWidth() + 5;
        	index++;
        }
        
        try{ 
            liveView.sendImageAsBitmap(pluginId, centerX(bitmap), centerY(bitmap), bitmap);
        } catch(Exception e) {
            Log.d(PluginConstants.LOG_TAG, "Failed to send bitmap", e);
        }
    }
    
    public static void displayNotConnection(LiveViewAdapter liveView, int pluginId) {
    	Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(PluginConstants.LIVEVIEW_SCREEN_X, 
            		PluginConstants.LIVEVIEW_SCREEN_Y, Bitmap.Config.RGB_565);
        }
        catch(IllegalArgumentException  e) {
            return;
        }
        
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Rect bounds = new Rect();
        paint.setColor(Color.WHITE);
        String text = "No data! Wait!";
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (128-bounds.right)/2, (128+bounds.top)/2, paint);
        
        try{ 
            liveView.sendImageAsBitmap(pluginId, centerX(bitmap), centerY(bitmap), bitmap);
        } catch(Exception e) {
            Log.d(PluginConstants.LOG_TAG, "Failed to send bitmap", e);
        }
    }
    
    private static Bitmap changeSize(Bitmap src, int size) {
    	Bitmap out;
    	if (src.getWidth() > src.getHeight())
    		out = Bitmap.createScaledBitmap(src, size*2, size, false);
    	else
    		out = Bitmap.createScaledBitmap(src, size, size, false);
    	return out;
    }
    
    /**
     * Gets resource id dynamically
     * 
     * @param context
     * @param resourceName
     * @param resourceType
     * @return
     */
    public static int getDynamicResourceId(Context context, String resourceName, String resourceType) {
        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
    }
    
    /**
     * Gets resource string dynamically
     * 
     * @param context
     * @param resourceName
     * @return
     */
    public static String getDynamicResourceString(Context context, String resourceName) {
        int resourceId = getDynamicResourceId(context, resourceName, "string");
        return context.getString(resourceId);
    }
    
    /**
     * Sends an image to LiveView and puts it in the middle of the screen
     * 
     * @param liveView
     * @param pluginId
     * @param bitmap
     * @param path
     */
    public static void sendScaledImage(LiveViewAdapter liveView, int pluginId, Bitmap bitmap) {
        try {
            if(liveView != null) {
                liveView.sendImageAsBitmap(pluginId, centerX(bitmap), centerY(bitmap), bitmap);
            }
        } catch(Exception e) {
            Log.e(PluginConstants.LOG_TAG, "Failed to send image.", e);
        }
    }
    
    /**
     * Get centered X axle
     * 
     * @param bitmap
     * @return
     */
    private static int centerX(Bitmap bitmap) {
        return (PluginConstants.LIVEVIEW_SCREEN_X/2) - (bitmap.getWidth()/2);
    }
    
    /**
     * Get centered Y axle
     * 
     * @param bitmap
     * @return
     */
    private static int centerY(Bitmap bitmap) {
        return (PluginConstants.LIVEVIEW_SCREEN_Y/2) - (bitmap.getHeight()/2);
    }
    
    /**
     * day ID 1-18
     * night ID 21-38
     * @param imgID
     * @return
     */
    private static int selectImage(int imgID){
    	int pictID = 0;
    	switch (imgID){
    		case 1:
    			pictID = R.drawable.wiz1;
    			break;
    		case 2:
    			pictID = R.drawable.wiz2;
    			break;
    		case 3:
    			pictID = R.drawable.wiz3;
    			break;
    		case 4:
    			pictID = R.drawable.wiz4;
    			break;
    		case 5:
    			pictID = R.drawable.wiz5;
    			break;
    		case 6:
    			pictID = R.drawable.wiz6;
    			break;
    		case 7:
    			pictID = R.drawable.wiz7;
    			break;
    		case 8:
    			pictID = R.drawable.wiz8;
    			break;
    		case 9:
    			pictID = R.drawable.wiz9;
    			break;
    		case 10:
    			pictID = R.drawable.wiz10;
    			break;
    		case 11:
    			pictID = R.drawable.wiz11;
    			break;
    		case 12:
    			pictID = R.drawable.wiz12;
    			break;
    		case 13:
    			pictID = R.drawable.wiz13;
    			break;
    		case 14:
    			pictID = R.drawable.wiz14;
    			break;
    		case 15:
    			pictID = R.drawable.wiz15;
    			break;
    		case 16:
    			pictID = R.drawable.wiz16;
    			break;
    		case 17:
    			pictID = R.drawable.wiz17;
    			break;
    		case 18:
    			pictID = R.drawable.wiz18;
    			break;
    		case 21:
    			pictID = R.drawable.wiz21;
    			break;
    		case 22:
    			pictID = R.drawable.wiz22;
    			break;
    		case 23:
    			pictID = R.drawable.wiz23;
    			break;
    		case 24:
    			pictID = R.drawable.wiz24;
    			break;
    		case 25:
    			pictID = R.drawable.wiz25;
    			break;
    		case 26:
    			pictID = R.drawable.wiz26;
    			break;
    		case 27:
    			pictID = R.drawable.wiz27;
    			break;
    		case 28:
    			pictID = R.drawable.wiz28;
    			break;
    		case 29:
    			pictID = R.drawable.wiz29;
    			break;
    		case 30:
    			pictID = R.drawable.wiz30;
    			break;
    		case 31:
    			pictID = R.drawable.wiz31;
    			break;
    		case 32:
    			pictID = R.drawable.wiz32;
    			break;
    		case 33:
    			pictID = R.drawable.wiz33;
    			break;
    		case 34:
    			pictID = R.drawable.wiz34;
    			break;
    		case 35:
    			pictID = R.drawable.wiz35;
    			break;
    		case 36:
    			pictID = R.drawable.wiz36;
    			break;
    		case 37:
    			pictID = R.drawable.wiz37;
    			break;
    		case 38:
    			pictID = R.drawable.wiz38;
    			break;
    		default:
    			pictID = R.drawable.plug; 
    			break;
    	}
    	return pictID;
    }
    
}