package com.example.veikko.weathergetter;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class WeatherEngine implements HTTPGetThread.ThreadReport /* implements HTTPGetThread.OnRequestDoneInterface */
{
    // This interface is used to report data back to UI
    public interface WeatherDataAvailableInterface
    {
        // This method is called back in background thread.
        public void weatherDataAvailable();
    }

    double KELVIN_CONVERT = 273.15;

    protected String temperature;
    protected String iconId;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    WeatherDataAvailableInterface uiCallback;

    public WeatherEngine(WeatherDataAvailableInterface callbackInterface)
    {
        this.uiCallback = callbackInterface;
    }

    public void getWeatherData(String city)
    {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=65dbec3aae5e5bf9000c7a956c8b76f6";
        HTTPGetThread getter = new HTTPGetThread(url, this, this);
        getter.start();
    }


    @Override
    public void onRequestDone(String data)
    {
        Log.d("LABRA dataa tulee: ", data);
        try
        {
            // No proper error handling here:
            Map<String, Object> parsed = JsonUtils.jsonToMap(new JSONObject(data));
            Map<String, Object> mainElement = (Map) parsed.get("main");
            double temp = (double)mainElement.get("temp");
            double tempInC = temp - KELVIN_CONVERT;
            this.temperature = String.format("%.1f", tempInC);

            ArrayList<Map<String, Object>> array = (ArrayList<Map<String, Object>>)parsed.get("weather");
            Map<String, Object> weatherElement = array.get(0);
            iconId = (String)weatherElement.get("icon");

            uiCallback.weatherDataAvailable();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
