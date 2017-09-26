package com.example.veikko.weathergetter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class WeatherEngine implements HTTPGetThread.ThreadReport /* implements HTTPGetThread.OnRequestDoneInterface */
{

    Context mContext;

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

    /*
    public void getWeatherData(String city)
    {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=65dbec3aae5e5bf9000c7a956c8b76f6";
        HTTPGetThread getter = new HTTPGetThread(url, this, this);
        getter.start();
    }
*/

    public void setContext(Context c)
    {
        mContext = c;
    }

    public void getWeatherData( )
    {
        String city = getSharedPreferences("Main", "city_name");
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=65dbec3aae5e5bf9000c7a956c8b76f6";
        HTTPGetThread getter = new HTTPGetThread(url, this, this);
        getter.start();
    }

    //Metodi, jolla voi hakea jaetun String -muuttujan
    protected String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = mContext.getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        return pref.getString(sharedVariableTag, null);
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
