package com.example.veikko.weathergetter;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Luokan on luonut tuomo päivämäärällä 26.9.2017.
 */


class HTTPGetThread extends Thread
{
    private boolean running = true;

    private String sentUrl;
    private WeatherEngine e;

    private ThreadReport observer = null;

    HTTPGetThread(String url, WeatherEngine weatherEngine, ThreadReport newObserver) {
        sentUrl = url;
        e = weatherEngine;
        this.observer = newObserver;
    }

    public void run() {
        try {
            while (running)
            {
                loadStuff();
                running = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadStuff() {
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(sentUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String allData = fromStream(in);
            //System.out.println(allData);'
            observer.onRequestDone(allData);

            Log.d("AllData", allData);
            //TODO: Observer printtaa data lämpötilan kohdalle
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }

    private String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }

    interface ThreadReport
    {
        //void setTemperature(String allData);
        void onRequestDone(String data);
    }
}
